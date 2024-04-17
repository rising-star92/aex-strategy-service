package com.walmart.aex.strategy.controller;

import com.walmart.aex.strategy.dto.ProgramResponseDTO;
import com.walmart.aex.strategy.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import com.walmart.aex.strategy.dto.ProgramTraitsResponse;
import com.walmart.aex.strategy.service.PlanProgramTraitsService;

import lombok.extern.slf4j.Slf4j;
import java.util.ArrayList;

@Controller
@Slf4j
public class SpaceStrategyController {

    @Autowired
    private PlanProgramTraitsService planProgramTraitsService;

    private static final String SUCCESS_STATUS = "Success";
    private static final String FAILURE_STATUS = "Failure";

    @QueryMapping(name = "getTraitProgramsByPlanId")
    public ProgramTraitsResponse getPlanProgramTraits(@Argument Long planId) {
        log.info("Fetching Program Traits for planId: {}", planId);
        ProgramTraitsResponse response = new ProgramTraitsResponse();
        try {
            response = planProgramTraitsService.getPlanProgramTraits(planId);
            response.setStatus(SUCCESS_STATUS);

        } catch (CustomException e) {
            response.setStatus(FAILURE_STATUS);
            response.setMessage(e.getMessage());
            response.setTrait(new ArrayList<>());
        } catch (Exception exp) {
            log.error("Exception occurred when fetching Program Traits: {}", exp.getMessage());
            response.setStatus(FAILURE_STATUS);
        }
        return response;

    }

    @MutationMapping(name = "deleteProgramByProgramId")
    public ProgramResponseDTO deleteProgramByProgramId(@Argument Long programId) {
        log.info("Deleting Program that are made eligible for a plan, for programId: {}", programId);
        ProgramResponseDTO response = new ProgramResponseDTO();
        try{
            response = planProgramTraitsService.deleteProgramEligibilityByProgramId(programId);
        } catch (Exception exp) {
            log.error("Exception occurred when deleting program Eligibility by programId: {}, Exception:{}", programId, exp.getMessage());
            response.setStatus(FAILURE_STATUS);
            response.setError(exp.getMessage());
        }
        return response;
    }
}
