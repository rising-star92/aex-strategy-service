package com.walmart.aex.strategy.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.walmart.aex.strategy.dto.*;
import com.walmart.aex.strategy.dto.request.PlanStrategyDTO;
import com.walmart.aex.strategy.dto.request.PlanStrategyDeleteMessage;
import com.walmart.aex.strategy.entity.PlanStrategyId;
import com.walmart.aex.strategy.enums.EventType;
import com.walmart.aex.strategy.enums.StratGroupType;
import com.walmart.aex.strategy.exception.CustomException;
import com.walmart.aex.strategy.producer.StrategyProducer;
import com.walmart.aex.strategy.repository.StrategyGroupRepository;
import com.walmart.aex.strategy.service.*;
import com.walmart.aex.strategy.util.CommonUtil;
import com.walmart.aex.strategy.util.KafkaUtil;
import lombok.extern.slf4j.Slf4j;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Controller
@Slf4j
public class PlanStrategyController {

    public static final String SUCCESS_STATUS = "Success";
    private static final String FAILURE_STATUS = "Failure";
    private final PlanStrategyService planStrategyService;
    private final ClusterEligibilityService clusterEligibilityService;
    private final ProgClusterEligibilityService progClusterEligibilityService;
    private final StrategyGroupRepository strategyGroupRepository;
    private final PlanStrategyDeleteFlService planStrategyDeleteFlService;

    @Autowired
    private StrategyProducer strategyProducer;

    @Autowired
    ObjectMapper objectMapper;
    private final SizeAndPackDeleteService sizeAndPackDeleteService;


    public PlanStrategyController(PlanStrategyService planStrategyService, ClusterEligibilityService clusterEligibilityService,
                                  ProgClusterEligibilityService progClusterEligibilityService,
                                  StrategyGroupRepository strategyGroupRepository,
                                  PlanStrategyDeleteFlService planStrategyDeleteFlService,
                                  SizeAndPackDeleteService sizeAndPackDeleteService) {
        this.planStrategyService = planStrategyService;
        this.clusterEligibilityService = clusterEligibilityService;
        this.progClusterEligibilityService = progClusterEligibilityService;
        this.strategyGroupRepository = strategyGroupRepository;
        this.planStrategyDeleteFlService = planStrategyDeleteFlService;
        this.sizeAndPackDeleteService = sizeAndPackDeleteService;
    }

    @PostMapping(path = "/strategyService")
    public @ResponseBody
    PlanStrategyListenerResponse createPlanStrategy(@RequestBody PlanStrategyDTO request) {
        try {
            return planStrategyService.addPlanStrategy(request);
        } catch (Exception exp) {
            log.error("Exception occurred when creating a plan Strategy: {}", exp);
            throw new CustomException("Exception occurred when creating a plan Strategy: " + exp);
        }
    }

    @QueryMapping
    public PlanStrategyResponse getClusterEligRankingByPlan(@Argument Long planId) {
        log.info("Fetching Cluster Elig Ranking By PlanId {}", planId);
        return clusterEligibilityService.fetchClusterEligRankingStrategy(planId);
    }

    @QueryMapping
    public PlanStrategyResponse getProgramEligRankingByPlan(@Argument Long planId, @Argument Long programId) {
        log.info("Fetching Program Elig Ranking By PlanId {}, programID {}", planId, programId);
        return clusterEligibilityService.fetchTraitEligRankingStrategy(planId, programId);
    }

    @PutMapping(path = "/strategyService")
    public @ResponseBody
    PlanStrategyListenerResponse updatePlanStrategyForClpMetrics(@RequestBody PlanStrategyDTO request) {
        log.info("Updating CLP metrics in plan strategy {}", request.toString());
        try {
            return planStrategyService.updatePlanStrategy(request, EventType.UPDATE);
        } catch (Exception exp) {
            log.error("Exception occurred when updating a plan Strategy: {}", exp.getMessage());
            throw new CustomException("Exception occurred when updating a plan Strategy: " + exp);
        }
    }

    @DeleteMapping(path = "/strategyService")
    public @ResponseBody
    PlanStrategyListenerResponse deletePlanStrategy(@RequestBody PlanStrategyDeleteMessage request) {
        log.info("Deleting Plan Strategy {}", request.toString());

        PlanStrategyListenerResponse response = new PlanStrategyListenerResponse();
        try {
            if (request.getStrongKey() != null) {
                planStrategyDeleteFlService.deletePlanStrategy(request);
                sizeAndPackDeleteService.deleteSizeStrategy(request);
                //Update the strategy, due to delete we could have some metrics update in LP
                if (request.getPlanStrategyDTO() != null) {
                    planStrategyService.updatePlanStrategy(request.getPlanStrategyDTO(), EventType.DELETE);
                }
                response.setStatus(SUCCESS_STATUS);
            }
        } catch (Exception exp) {
            log.error("Exception occurred when creating a plan Strategy: {}", exp.getMessage());
            response.setStatus(FAILURE_STATUS);
            throw new CustomException("Exception occurred when creating a plan Strategy: " + exp);
        }
        log.info("Response for Deleting Plan Strategy {}", response);

        return response;
    }

    @QueryMapping
    public PlanStrategyResponse getCcClusterEligRankingByPlanFl(@Argument PlanStrategyRequest planStrategyRequest) {
        log.info("Fetching Cc Cluster Elig Ranking By Plan Fl {}", planStrategyRequest.toString());
        return clusterEligibilityService.fetchCcClusterEligRankingStrategy(planStrategyRequest);

    }
    @QueryMapping
    public RFAMinMaxValidationResponse isRfaDefaultMinMaxCapValid(@Argument Long planId) {
        log.info("Validating default Min and Max cap for planId: {}", planId);
        RFAMinMaxValidationResponse response = new RFAMinMaxValidationResponse();
        PlanStrategyRequest request = new PlanStrategyRequest();
        request.setPlanId(planId);
        try {
            Long strategyId = strategyGroupRepository.getStrategyIdByStrategyGroupTypeAndPlanId(StratGroupType.FIXTURE.getStrategyGroupTypeCode(), planId);
            Map<String, Boolean> queryResult =  clusterEligibilityService.isRfaDefaultMinMaxCapValid(request, strategyId);
            response.setIsCCRulesValid(queryResult.get("isCCRulesValid"));
            response.setIsFinelineRulesValid(queryResult.get("isFinelineRulesValid"));
            response.setStatus(SUCCESS_STATUS);
        } catch (Exception exp) {
            log.error("Exception occurred when updating Updating ClusterEligibility metrics: {}", exp.getMessage());
            response.setStatus(FAILURE_STATUS);
            response.setErrorMessage(exp.getMessage());
        }
        return response;
    }
    @QueryMapping
    public RFAMinMaxValidationResponse isRfaDefaultMinMaxCapRulesValid(@Argument PlanStrategyRequest planStrategyRequest) throws JsonProcessingException {
        log.info("Validating default Min and Max cap for planId: {}", planStrategyRequest.getPlanId());
        RFAMinMaxValidationResponse response = new RFAMinMaxValidationResponse();
        long startTime = System.currentTimeMillis();
        try {
            Long strategyId = strategyGroupRepository.getStrategyIdByStrategyGroupTypeAndPlanId(StratGroupType.FIXTURE.getStrategyGroupTypeCode(), planStrategyRequest.getPlanId());
            Map<String, Boolean> queryResult =  clusterEligibilityService.isRfaDefaultMinMaxCapValid(planStrategyRequest, strategyId);
            response.setIsCCRulesValid(queryResult.get("isCCRulesValid"));
            response.setIsFinelineRulesValid(queryResult.get("isFinelineRulesValid"));
            response.setStatus(SUCCESS_STATUS);
            long endTime = System.currentTimeMillis();
            log.info("Response for isRfaDefaultMinMaxCapValid: {}, Total time taken to process the request: {}", objectMapper.writeValueAsString(response), startTime-endTime);
        } catch (Exception exp) {
            log.error("Exception occurred when updating Updating ClusterEligibility metrics: {}", exp.getMessage());
            response.setStatus(FAILURE_STATUS);
            response.setErrorMessage(exp.getMessage());
        }
        return response;
    }

    @QueryMapping
    public PlanStrategyResponse getProgramCcClusterEligRankingByPlanFl(@Argument PlanStrategyRequest planStrategyRequest, @Argument Long programId) {
        log.info("Fetching ProgramCcClusterEligRankingByPlanFl {}, ProgramID {}", planStrategyRequest.toString(), programId);
        return clusterEligibilityService.fetchCcTraitEligRankingStrategy(programId, planStrategyRequest);

    }

    @MutationMapping
    public PlanStrategyResponse updateClusterEligibilityMetrics(@Argument PlanStrategyRequest request) {
        log.info("Updating ClusterEligibility metrics {}", request.toString());
        PlanStrategyResponse response = new PlanStrategyResponse();
        try {
            Long strategyId = strategyGroupRepository.getStrategyIdByStrategyGroupTypeAndPlanId(StratGroupType.WEATHER_CLUSTER.getStrategyGroupTypeCode()
                    , request.getPlanId());
            PlanStrategyId planStrategyId = PlanStrategyId.builder()
                    .planId(request.getPlanId())
                    .strategyId(strategyId)
                    .build();
            Integer requestedFlNbr = CommonUtil.getFinelineDetails(request);
            Set<String> updatedField= new HashSet<>();
            clusterEligibilityService.updateClusterEligibility(request, planStrategyId, requestedFlNbr, updatedField);
            response = clusterEligibilityService.fetchFinelineAndCcChanges(planStrategyId, requestedFlNbr, response);
            response.setStatus(SUCCESS_STATUS);
            if (!updatedField.isEmpty()){
                PlanStrategyResponse finalResponse = response;
                CompletableFuture.runAsync(() ->
                        KafkaUtil.postMessage(request, finalResponse, strategyProducer, EventType.UPDATE, updatedField));
            }
            log.info("Response for updating ClusterEligibility metrics {}", response);
        } catch (Exception exp) {
            log.error("Exception occurred when updating Updating ClusterEligibility metrics: {}", exp.getMessage());
            response.setStatus(FAILURE_STATUS);
        }
        return response;
    }

    @MutationMapping
    public PlanStrategyResponse updatePrgClusterEligibilityMetrics(@Argument PlanStrategyRequest request, @Argument Long programId) {
        log.info("Updating Program ClusterEligibility metrics for request {}", request.toString());
        PlanStrategyResponse response = new PlanStrategyResponse();
        try {
            Long strategyId = strategyGroupRepository.getStrategyIdByStrategyGroupTypeAndPlanId(StratGroupType.WEATHER_CLUSTER.getStrategyGroupTypeCode(), request.getPlanId());
            PlanStrategyId planStrategyId = PlanStrategyId.builder()
                    .planId(request.getPlanId())
                    .strategyId(strategyId)
                    .build();
            Integer finelineNbr = CommonUtil.getFinelineDetails(request);
            Set<String> updatedField= new HashSet<>();
            progClusterEligibilityService.updatePrgClusterEligibility(request, planStrategyId, programId, finelineNbr,updatedField);
            response = progClusterEligibilityService.fetchProgFlAndCcChanges(planStrategyId, response, programId, finelineNbr);
            response.setStatus(SUCCESS_STATUS);
            if (!updatedField.isEmpty()){
                PlanStrategyResponse finalResponse = response;
                CompletableFuture.runAsync(() ->
                        KafkaUtil.postMessage(request, finalResponse, strategyProducer, EventType.UPDATE, updatedField));
            }
            log.info("Response for updating Program ClusterEligibility metrics {}", response);
        } catch (Exception exp) {
            log.error("Exception occurred when updating Updating ClusterEligibility metrics: {} {}", request.getPlanId(), exp.toString());
            response.setStatus(FAILURE_STATUS);
        }
        return response;
    }
}
