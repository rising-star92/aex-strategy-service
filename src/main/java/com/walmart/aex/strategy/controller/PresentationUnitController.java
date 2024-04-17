package com.walmart.aex.strategy.controller;

import com.walmart.aex.strategy.dto.PlanStrategyListenerResponse;
import com.walmart.aex.strategy.dto.PlanStrategyRequest;
import com.walmart.aex.strategy.dto.PlanStrategyResponse;
import com.walmart.aex.strategy.dto.request.PlanPresentationUnitsDTO;
import com.walmart.aex.strategy.entity.PlanStrategyId;
import com.walmart.aex.strategy.enums.EventType;
import com.walmart.aex.strategy.enums.StratGroupType;
import com.walmart.aex.strategy.producer.StrategyProducer;
import com.walmart.aex.strategy.repository.StrategyGroupRepository;
import com.walmart.aex.strategy.service.PresentationUnitsService;
import com.walmart.aex.strategy.util.CommonUtil;
import com.walmart.aex.strategy.util.KafkaUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Controller
@Slf4j
public class PresentationUnitController {

    private static final String SUCCESS_STATUS = "Success";
    private static final String FAILURE_STATUS = "Failure";
    private final PresentationUnitsService presentationUnitsService;
    private final StrategyGroupRepository strategyGroupRepository;
    private final ExecutorService executorService;
    @Autowired
    private StrategyProducer strategyProducer;

    public PresentationUnitController(PresentationUnitsService presentationUnitsService,
                                      StrategyGroupRepository strategyGroupRepository, ExecutorService executorService) {
        this.presentationUnitsService = presentationUnitsService;
        this.strategyGroupRepository = strategyGroupRepository;
        this.executorService = executorService;
    }

    @QueryMapping
    public PlanStrategyResponse getPresentationUnitByPlan(@Argument Long planId) {
        log.info("Fetching PresentationUnit by planId{}", planId);
        PlanStrategyResponse response = new PlanStrategyResponse();
        try {
            response = presentationUnitsService.fetchPUsCatSubCats(planId, response);
            response.setStatus(SUCCESS_STATUS);
        } catch (Exception exp) {
            log.error("Exception occurred when fetching Presentation Units by PlanId: {}", exp.getMessage());
            response.setStatus(FAILURE_STATUS);
        }
        return response;

    }

    @QueryMapping
    public PlanStrategyResponse getPresentationUnitByCatgAndSubCatg(@Argument PlanStrategyRequest planStrategyRequest) {
        log.info("Fetching Fl Presentation Units for planId: {}", planStrategyRequest.getPlanId());
        PlanStrategyResponse response = new PlanStrategyResponse();
        try {
            response = presentationUnitsService.fetchPUsFlByCatgAndSubCatg(planStrategyRequest, response);
            response.setStatus(SUCCESS_STATUS);
        } catch (Exception exp) {
            log.error("Exception occurred when fetching Fl Presentation Units by Catg & SubCatg: {}", exp.getMessage());
            response.setStatus(FAILURE_STATUS);
        }
        return response;
    }


    @MutationMapping
    public PlanStrategyResponse updatePresentationUnitMetrics(@Argument PlanStrategyRequest request) {
        log.info("Updating Presentation units, min & max values for planId {}", request.getPlanId());
        PlanStrategyResponse response = new PlanStrategyResponse();
        Set<String> updatedFieldRequest = new HashSet<>();
        try {
            Long strategyId = strategyGroupRepository.getStrategyIdByStrategyGroupTypeAndPlanId(StratGroupType.PRESENTATION_UNITS.getStrategyGroupTypeCode(), request.getPlanId());
            PlanStrategyId planStrategyId = PlanStrategyId.builder()
                    .planId(request.getPlanId())
                    .strategyId(strategyId)
                    .build();
            presentationUnitsService.updatePresentationUnitStrategy(request, planStrategyId, updatedFieldRequest);
            Integer lvl3List = CommonUtil.getLvl3Details(request);
            Integer lvl4List = CommonUtil.getLvl4Details(request);
            response = presentationUnitsService.fetchUpdatePUChanges(planStrategyId, lvl3List, lvl4List, response, false, null);
            response.setStatus(SUCCESS_STATUS);

            PlanStrategyResponse finalResponse = response;
            CompletableFuture.runAsync(() ->
                    KafkaUtil.postMessage(request, finalResponse, strategyProducer, EventType.UPDATE, updatedFieldRequest), executorService);

        } catch (Exception exp) {
            log.error("Exception occurred when updating Presentation units metrics: {}", exp.toString());
            response.setStatus(FAILURE_STATUS);
        }
        return response;
    }

    @PostMapping(path = "/presentationUnits", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public PlanStrategyListenerResponse updatePresentationUnitsFromRfa(@RequestBody PlanPresentationUnitsDTO request){
        log.info("Updating Presentation unit for request: {}", request.toString());
        PlanStrategyListenerResponse response = new PlanStrategyListenerResponse();
        try{
            Long strategyId = strategyGroupRepository.getStrategyIdBySeasonCd(StratGroupType.PRESENTATION_UNITS.getStrategyGroupTypeCode(), null, null);
            PlanStrategyId planStrategyId = PlanStrategyId.builder()
                    .planId(request.getPlanStrategyDTO().getPlanId())
                    .strategyId(strategyId)
                    .build();
            presentationUnitsService.updatePresentationUnitsFromRfa(request.getPlanStrategyDTO(), planStrategyId);
            presentationUnitsService.generateKafkaMessagesForTheRequestedFinelines(request.getFinelineNbrs(), planStrategyId);
            response.setStatus(SUCCESS_STATUS);
        } catch (Exception exp) {
            log.error("Exception occurred when updating Presentation units from RFA: {}", exp.getMessage());
            response.setStatus(FAILURE_STATUS);
        }
        return response;
    }
}
