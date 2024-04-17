package com.walmart.aex.strategy.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.strategy.dto.PlanStrategyListenerResponse;
import com.walmart.aex.strategy.dto.PlanStrategyRequest;
import com.walmart.aex.strategy.dto.PlanStrategyResponse;
import com.walmart.aex.strategy.dto.RFAStatusDataResponse;
import com.walmart.aex.strategy.exception.CustomException;
import com.walmart.aex.strategy.service.RunRFAService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@Slf4j
public class RunRFAController {

        private static final String SUCCESS_STATUS = "Success";
        private static final String FAILURE_STATUS = "Failure";

        @Autowired
        ObjectMapper objectMapper;

        @Autowired
        RunRFAService runRFAService;

    @QueryMapping
    public RFAStatusDataResponse getRFAAllocationTypes(){
        log.info("Fetching the RFA Lock Types");
        RFAStatusDataResponse response =  new RFAStatusDataResponse();
        long startTime = System.currentTimeMillis();
        try {
            log.info("Fetch RFA lock types, received at: {}", startTime);
            response.setRfaStatusList(runRFAService.fetchAllocRunTypes());
            response.setStatus(SUCCESS_STATUS);
            long endTime = System.currentTimeMillis();
            log.info("Response for RFA lock types: {}, Total time taken to process the request: {}", objectMapper.writeValueAsString(response), startTime-endTime);
        } catch (Exception exp) {
            log.error("Exception occurred when fetching RFA Lock Types: {}", exp.getMessage());
            response.setStatus(FAILURE_STATUS);
            throw new CustomException("Exception occurred when fetching RFA lock types: "+exp);
        }
        return  response;
    }

    @QueryMapping
    public PlanStrategyResponse getRunRFAStatusByPlan(@Argument Long planId) {
        log.info("Fetching Run RFA for planId: {}", planId);
        PlanStrategyResponse response = new PlanStrategyResponse();
        long startTime = System.currentTimeMillis();
        try {
            log.info("Fetch Run RFA for planId: {}, received at: {}", objectMapper.writeValueAsString(planId), startTime);
            response = runRFAService.fetchRunRFAStatusByPlan(planId, response);
            response.setStatus(SUCCESS_STATUS);
            long endTime = System.currentTimeMillis();
            log.info("Response for Run RFA for planId: {}, Total time taken to process the request: {}", objectMapper.writeValueAsString(response), startTime-endTime);
        } catch (Exception exp) {
            log.error("Exception occurred when fetching Run RFA for Finelines: {}", exp.getMessage());
            response.setStatus(FAILURE_STATUS);
            throw new CustomException("Exception occurred when fetching Run RFA for finelines: "+exp);
        }
        return response;

    }

    @MutationMapping
    public PlanStrategyResponse updateAndFetchFineLineRFAStatus(@Argument PlanStrategyRequest request) {
        log.info("Updating RFA Lock Status for Finelines for planId {}", request.getPlanId());
        PlanStrategyResponse response = new PlanStrategyResponse();
        long startTime = System.currentTimeMillis();
        try {
            runRFAService.updateFinelineRFAStatus(request);
            response = runRFAService.fetchRunRFAStatusByPlan(request.getPlanId(), response);
            response.setStatus(SUCCESS_STATUS);
            long endTime = System.currentTimeMillis();
            log.info("Response for Update for planId: {}, Total time taken to process the request: {}", objectMapper.writeValueAsString(response), startTime-endTime);

        } catch (Exception exp) {
            log.error("Exception occurred when updating RFA statuses: {}", exp.getMessage());
            response.setStatus(FAILURE_STATUS);
        }
        return response;
    }

    @PostMapping(path = "/updateRFAStatus", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public PlanStrategyListenerResponse updateRFAStatusFromRfa(@RequestBody PlanStrategyRequest request) {
        log.info("Updating RFA Lock Status for Finelines for planId after RFA execution {}", request.getPlanId());
        PlanStrategyListenerResponse response = new PlanStrategyListenerResponse();
        long startTime = System.currentTimeMillis();
        try {
            runRFAService.updateFinelineRFAStatus(request);
            response.setStatus(SUCCESS_STATUS);
            long endTime = System.currentTimeMillis();
            log.info("Response for Update for planId: {}, Total time taken to process the request: {}", objectMapper.writeValueAsString(response), startTime-endTime);

        } catch (Exception exp) {
            log.error("Exception occurred when updating RFA statuses: {}", exp.getMessage());
            response.setStatus(FAILURE_STATUS);
        }
        return response;
    }
}
