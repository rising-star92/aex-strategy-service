package com.walmart.aex.strategy.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.walmart.aex.strategy.dto.*;
import com.walmart.aex.strategy.dto.PlanDTO.PlanDefMessageDTO;
import com.walmart.aex.strategy.dto.request.LinePlanStrategyRequest;
import com.walmart.aex.strategy.dto.request.update.LinePlanStrategyUpdateAttributeRequest;
import com.walmart.aex.strategy.dto.request.update.LinePlanStrategyUpdateRequest;
import com.walmart.aex.strategy.exception.CustomException;
import com.walmart.aex.strategy.service.LinePlanStrategyService;
import com.walmart.aex.strategy.service.LinePlanUpdateService;
import com.walmart.aex.strategy.service.PlanDefinitionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@Slf4j
public class LinePlanStrategyController {

    private final LinePlanStrategyService linePlanStrategyService;

    private final PlanDefinitionService planDefinitionService;

    private final LinePlanUpdateService linePlanUpdateService;

    public static final String SUCCESS_STATUS = "Success";

    public LinePlanStrategyController(LinePlanStrategyService linePlanStrategyService,PlanDefinitionService planDefinitionService,LinePlanUpdateService linePlanUpdateService) {
        this.linePlanStrategyService = linePlanStrategyService;
        this.planDefinitionService = planDefinitionService;
        this.linePlanUpdateService =linePlanUpdateService;
    }

    @PostMapping(path = "/planDefinition")
    public @ResponseBody
    PlanStrategyListenerResponse createPlanStrategy(@RequestBody PlanDefMessageDTO request) {
        try {
            return planDefinitionService.addPlanDefinition(request);
        } catch (Exception exp) {
            log.error("Exception occurred when creating a plan Definition:", exp);
            throw new CustomException("Exception occurred when creating a plan Definition: " + exp);
        }
    }

    @PostMapping(path = "/updatePlanStrategy")
    public @ResponseBody
    PlanStrategyListenerResponse updatePlanStrategy(@RequestBody PlanDefMessageDTO request) {
        try {
            return planDefinitionService.updatePlanStrategy(request);
        } catch (Exception exp) {
            log.error("Exception occurred when updating a plan Definition:", exp);
            throw new CustomException("Exception occurred when updating a plan Definition: " + exp);
        }
    }


    @GetMapping(path = "/fetchCurrentLinePlanStrategyTargetCount")
    public @ResponseBody StrategyCountResponse fetchCurrentLinePlanStrategyTargetCount(Long planId) {
        return linePlanStrategyService.fetchCurrentLinePlanStrategyTargetCount(planId);
    }

    @QueryMapping
    public @ResponseBody
    LinePlanStrategyResponse fetchAttributeStrategy(@Argument LinePlanStrategyRequest request) throws JsonProcessingException {
        return linePlanStrategyService.fetchTargetsByAttribute(request);
    }
    @GetMapping(path = "/fetchCurrentLinePlanStrategy")
    public @ResponseBody List<LinePlanCount> fetchCurrentLinePlanStrategy(LinePlanStrategyRequest request) {
        return linePlanStrategyService.fetchCurrentLinePlanStrategy(request);
    }

    @MutationMapping
    public StrategyUpdateResponse updateLinePlanStrategyTargetCount(@Argument LinePlanStrategyUpdateRequest request) {
        log.info("Updating updateLinePlanStrategyTargetCount metrics for planId {}", request.getPlanId());
        return linePlanUpdateService.updateLinePLanTargetCounts(request);
    }

    @MutationMapping
    public @ResponseBody
    LinePlanStrategyAttributeGrpResponse updateLinePlanStrategyAttributeGroup(@Argument LinePlanStrategyUpdateAttributeRequest request)throws JsonProcessingException{
        try {
            log.info("Updating updateLinePlanStrategyAttributeGroup metrics for planId {}", request.getPlanId());
            return linePlanUpdateService.updateAttributeGroupCounts(request);
        }
        catch (Exception e) {
            log.error("Exception occurred when updating a attribiuteGrp Target:", e);
            throw new CustomException("Exception occurred when updating a attribiuteGrp Target: " + e);
        }

    }

}
