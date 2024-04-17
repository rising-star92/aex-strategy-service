package com.walmart.aex.strategy.controller;

import com.walmart.aex.strategy.dto.PlanStrategyRequest;
import com.walmart.aex.strategy.dto.PlanStrategyResponse;
import com.walmart.aex.strategy.entity.PlanStrategyId;
import com.walmart.aex.strategy.enums.StratGroupType;
import com.walmart.aex.strategy.repository.StrategyGroupRepository;
import com.walmart.aex.strategy.service.FixtureAllocationService;
import com.walmart.aex.strategy.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
public class FixtureAllocationController {

    private static final String SUCCESS_STATUS = "Success";
    private static final String FAILURE_STATUS = "Failure";
    private final FixtureAllocationService fixtureAllocationService;
    private final StrategyGroupRepository strategyGroupRepository;

    public FixtureAllocationController(FixtureAllocationService fixtureAllocationService, StrategyGroupRepository strategyGroupRepository){
        this.fixtureAllocationService = fixtureAllocationService;
        this.strategyGroupRepository = strategyGroupRepository;
    }

    @QueryMapping
    public PlanStrategyResponse getAllocationRulesByPlan(@Argument Long planId) {
        log.info("Fetching Fixture Allocation by planId{}", planId);
        PlanStrategyResponse response = new PlanStrategyResponse();
        try {
            response = fixtureAllocationService.fetchCatSubcatFinelineFixtureStrategy(planId, response);
            response.setStatus(SUCCESS_STATUS);
        } catch (Exception exp) {
            log.error("Exception occurred when fetching Allocation Rules by PlanId: {}", exp.getMessage());
            response.setStatus(FAILURE_STATUS);
        }
        return response;

    }

    @QueryMapping
    public PlanStrategyResponse getAllocationRulesByCatgAndSubCatg(@Argument PlanStrategyRequest planStrategyRequest) {
        log.info("Fetching Fl Fixture Allocation for planId: {}", planStrategyRequest.getPlanId());
        PlanStrategyResponse response = new PlanStrategyResponse();
        try {
            response = fixtureAllocationService.fetchFinelineFixtureByCatgAndSubCatg(planStrategyRequest, response);
            response.setStatus(SUCCESS_STATUS);
        } catch (Exception exp) {
            log.error("Exception occurred when fetching Fl Allocation Rules: {}", exp.getMessage());
            response.setStatus(FAILURE_STATUS);
        }
        return response;

    }

    @QueryMapping
    public PlanStrategyResponse getCcAllocationRulesByCatgSubCatgAndFl(@Argument PlanStrategyRequest planStrategyRequest) {
        log.info("Fetching Cc Fixture Allocation for planId{}", planStrategyRequest.getPlanId());
        PlanStrategyResponse response = new PlanStrategyResponse();
        try {
            response = fixtureAllocationService.fetchCcFixtureByCatgSubCatgAndFl(planStrategyRequest, response);
            response.setStatus(SUCCESS_STATUS);
        } catch (Exception exp) {
            log.error("Exception occurred when fetching Cc Allocation Rules: {}", exp.getMessage());
            response.setStatus(FAILURE_STATUS);
        }
        return response;

    }

    @MutationMapping
    public PlanStrategyResponse updateFixtureAllocationMetrics(@Argument PlanStrategyRequest request) {
        log.info("Updating Fixture Allocation metrics for planId {}", request.getPlanId());
        PlanStrategyResponse response = new PlanStrategyResponse();
        try {
            Long strategyId = strategyGroupRepository.getStrategyIdByStrategyGroupTypeAndPlanId(StratGroupType.FIXTURE.getStrategyGroupTypeCode(), request.getPlanId());
            PlanStrategyId planStrategyId = PlanStrategyId.builder()
                    .planId(request.getPlanId())
                    .strategyId(strategyId)
                    .build();
            fixtureAllocationService.updateFixtureAllocationRules(request, planStrategyId);
            Integer lvl3List = CommonUtil.getLvl3Details(request);
            Integer lvl4List = CommonUtil.getLvl4Details(request);
            Integer requestedFlNbr = CommonUtil.getFinelineDetails(request);
            String requestedCcId = CommonUtil.getCcId(request);
            response = fixtureAllocationService.fetchUpdateFixtureChanges(planStrategyId, lvl3List, lvl4List, requestedFlNbr, requestedCcId, response);
            response.setStatus(SUCCESS_STATUS);
        } catch (Exception exp) {
            log.error("Exception occurred when updating Updating FixtureAllocation metrics: ", exp);
            response.setStatus(FAILURE_STATUS);
        }
        return response;
    }


}
