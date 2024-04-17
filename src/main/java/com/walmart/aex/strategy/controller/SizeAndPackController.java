package com.walmart.aex.strategy.controller;

import com.walmart.aex.strategy.dto.*;
import com.walmart.aex.strategy.dto.request.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.walmart.aex.strategy.dto.sizeprofile.PlanSizeProfile;
import com.walmart.aex.strategy.exception.CustomException;
import com.walmart.aex.strategy.service.SizeAndPackService;
import com.walmart.aex.strategy.service.SizeEligibilityService;
import com.walmart.aex.strategy.service.StrategySPClusAdjSizeProfileService;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class SizeAndPackController {


    public static final String SUCCESS_STATUS = "Success";
    private static final String FAILURE_STATUS = "Failure";
    private final SizeAndPackService sizeAndPackService;
    private final SizeEligibilityService sizeEligibilityService;
    
    @Autowired
    StrategySPClusAdjSizeProfileService strategySPClusAdjSizeProfileService;
    
    public SizeAndPackController(SizeAndPackService sizeAndPackService, SizeEligibilityService sizeEligibilityService) {
        this.sizeAndPackService = sizeAndPackService;
        this.sizeEligibilityService = sizeEligibilityService;

    }

    @MutationMapping
    public SPMerchMethodFixtureResponse createMerchMethodFixture(@Argument SPMerchMethodFixtureRequest request) {
        log.info("Create Merch Method fixture details for planId {}", request.getPlanId());
        try {
            return sizeAndPackService.updateMerchMethod(request);
        } catch (Exception exp) {
            log.error("Exception occurred when saving merch method data: ", exp);
            throw new CustomException("Exception occurred when saving merch method data: " + exp);

        }
    }

    @MutationMapping
    public UpdateAdjSizeProfileResponse  updateSizeAssociation(@Argument PlanStrategySP request, @Argument String channel) {
        UpdateAdjSizeProfileResponse response = new UpdateAdjSizeProfileResponse();
        try {
             sizeEligibilityService.updatePlanStrategyForSizeCluster(request, channel);
             response.setStatus(SUCCESS_STATUS);
        } catch (Exception e) {
            log.error("Exception occurred while updating Size data: {}", e.getMessage());
            response.setStatus(FAILURE_STATUS);
        }
        return response;
    }

    @QueryMapping
    public SPMerchMethodFixtureResponse getMerchMethodFixtureByPlanId(@Argument Long planId ,@Argument String planDesc){
        return sizeAndPackService.fetchMerchMethodFixture(planId ,planDesc, null);
    }

    @QueryMapping
    public PlanStrategySPResponse getCcByFineline(@Argument Long planId, @Argument String channel, @Argument Integer finelineNbr) {
        log.info("get CC size for plan {} and fineline {}", planId, finelineNbr);
        try {
            return sizeEligibilityService.fetchCcSize(planId, finelineNbr, channel);
        } catch (Exception exp) {
            log.error("Exception occurred when fetching CC: ", exp);
            throw new CustomException("Exception occurred when fetching cc: " + exp);

        }
    }

    @QueryMapping
    public PlanStrategySPResponse getCcByFinelineWithoutSizeAssociation(@Argument Long planId, @Argument String channel, @Argument Integer finelineNbr) {
        log.info("get CC without size association for plan {} and fineline {}", planId, finelineNbr);
        try {
            return sizeEligibilityService.getCcByFinelineWithoutSizeAssociation(planId, finelineNbr, channel);
        } catch (Exception exp) {
            log.error("Exception occurred when fetching CC without association: ", exp);
            throw new CustomException("Exception occurred when fetching cc without association: " + exp);

        }
    }
    
    @QueryMapping
    public PlanStrategySPResponse getFinelineWithoutSizeAssociation(@Argument Long planId, @Argument String channel) {
        try {
            return sizeEligibilityService.getFinelineWithoutSizeAssociation(planId, channel);
        } catch (Exception exp) {
            log.error("Exception occurred when fetching Fineline without association: ", exp);
            throw new CustomException("Exception occurred when fetching fineline without association: " + exp);

        }
    }

    @QueryMapping
    public PlanStrategySPResponse getCategoryHierarchyByPlanId(@Argument Long planId, @Argument String channel) throws JsonProcessingException {
        return sizeEligibilityService.fetchSubCategories(planId, channel);
    }
    
	@MutationMapping
	public PlanSizeProfile updateFinelineAdjSizeProfilePct(@Argument UpdateFinelineAdjSizeProfileRequest request) throws JsonMappingException, JsonProcessingException {

      SizeProfileRequest spRequest =
            new SizeProfileRequest(request.getPlanId(),
                  request.getChannel(), null, null, null,
                  request.getFineline(), null, null);

      strategySPClusAdjSizeProfileService.updateStratFinelineSPClusAdjustedSizeProfile(request);
      return sizeAndPackService.fetchFinelineSizeProfile(spRequest) ;

	}
    
	@MutationMapping
	public PlanSizeProfile updateStyleSPClusAdjSizeProfilePct(@Argument UpdateStyleAdjSizeProfileRequest request) throws JsonMappingException, JsonProcessingException {

      SizeProfileRequest spRequest =
            new SizeProfileRequest(request.getPlanId(),
                  request.getChannel(), null, null, null,
                  request.getFineline(), request.getStyle(), null);

      strategySPClusAdjSizeProfileService.updateStratStyleSPClusAdjustedSizeProfile(request);
      return sizeAndPackService.fetchStyleSizeProfile(spRequest, request.getStyle());
	}
    
	@MutationMapping
	public PlanSizeProfile updateCustomerChoiceAdjSizeProfilePct(@Argument UpdateCustomerChoicesAdjSizeProfileRequest request) {

      SizeProfileRequest spRequest =
            new SizeProfileRequest(request.getPlanId(),
                  request.getChannel(), null, null, null,
                  request.getFinelineNbr(), request.getStyle(), request.getCustomerChoice());

      strategySPClusAdjSizeProfileService.updateCustomerChoicesAdjSizeProfile(request);
      return sizeAndPackService.fetchCcSizeProfile(spRequest, request.getCustomerChoice());
	}

    /**
     * This Mutation endpoint give Merchant choice to override the analytics recommendations at CC level when creating a plan,
     * - if Merchant request of spreading CC performed successfully - return success
     * - else //Todo: ?
     */
    @MutationMapping
    public SpreadSizeProfileResponse updateSpreadSizeProfilePercentage(@Argument SpreadSizeProfileRequest request) {
        return strategySPClusAdjSizeProfileService.updateCustomerChoicesAdjSizeProfileActiveSpreadIndicator(request);
    }

    @QueryMapping
    public PlanSizeProfile getFineLinesSizeClus(@Argument SizeProfileRequest sizeProfileRequest) {
        return sizeAndPackService.fetchFinelineSizeProfile(sizeProfileRequest) ;
    }

    @QueryMapping
    public PlanSizeProfile getStylesSizeClus(@Argument SizeProfileRequest sizeProfileRequest) {
        return sizeAndPackService.fetchStyleSizeProfile(sizeProfileRequest,sizeProfileRequest.getStyleNbr()) ;
    }

    @QueryMapping
    public PlanSizeProfile getCcSizeClus(@Argument SizeProfileRequest sizeProfileRequest) {
        return sizeAndPackService.fetchCcSizeProfile(sizeProfileRequest,sizeProfileRequest.getCcId()) ;
    }

    @QueryMapping
    public PlanSizeProfile getAllCcSizeClus(@Argument SizeProfileRequest sizeProfileRequest) {
        return sizeAndPackService.fetchAllCcSizeProfile(sizeProfileRequest);
    }

    @QueryMapping
    public PlanStrategySPResponse getFinelinesWithSizeAssociation(@Argument Long planId, @Argument String channel) {
        return sizeEligibilityService.fetchSubCategoriesWithSizeAssociation(planId, channel);
    }
    @QueryMapping
    public PlanStrategySPResponse getStylesCCsWithSizeAssociation(@Argument Long planId, @Argument String channel, @Argument Integer finelineNbr){
        return sizeEligibilityService.fetchStylesCCsWithAssociation(planId, channel,finelineNbr);
    }
}
