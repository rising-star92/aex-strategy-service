package com.walmart.aex.strategy.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.walmart.aex.strategy.properties.RFAGraphQLProperties;
import com.walmart.aex.strategy.dto.assortproduct.RFARequest;
import com.walmart.aex.strategy.dto.assortproduct.RFASpaceResponse;
import com.walmart.aex.strategy.dto.assortproduct.gql.RFAGraphQLResponse;
import com.walmart.aex.strategy.dto.assortproduct.gql.RFAPayload;
import com.walmart.aex.strategy.exception.StrategyServiceException;

import io.strati.ccm.utils.client.annotation.ManagedConfiguration;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AssortProductService {
	
	private final RFAGraphQLService rfaGraphQLService;
	
	@ManagedConfiguration
    RFAGraphQLProperties rfaGraphQLProperties;
	
	AssortProductService(RFAGraphQLService rfaGraphQLService){
		this.rfaGraphQLService = rfaGraphQLService;
	}

	public RFASpaceResponse getRFASpaceDataOutput(RFARequest request) throws StrategyServiceException
	{		
		RFASpaceResponse rfaSpaceResponse = new RFASpaceResponse();
		String query = null;
		
		Map<String, String> headers = new HashMap<>();
        headers.put("WM_CONSUMER.ID", rfaGraphQLProperties.getAssortProductConsumerId());
        headers.put("WM_SVC.NAME", rfaGraphQLProperties.getAssortProductConsumerName());
        headers.put("WM_SVC.ENV", rfaGraphQLProperties.getAssortProductConsumerEnv());

        Map<String, Object> data = new HashMap<>();
        data.put("request", request);
        
        if(request.getFinelineNbr() == null)
        {
        	query = rfaGraphQLProperties.getAssortProductRFAFinelineQuery();
            rfaSpaceResponse = (RFASpaceResponse) postService(rfaGraphQLProperties.getAssortProductUrl(), query,
    				headers, data, RFAPayload::getGetRFAFinelineSpaceAllocation);
        }        	
        else
        {
        	query = rfaGraphQLProperties.getAssortProductRFAStyleCcQuery();            
            rfaSpaceResponse = (RFASpaceResponse) postService(rfaGraphQLProperties.getAssortProductUrl(), query,
    												headers, data, RFAPayload::getGetRFAStyleCcSpaceAllocation);
        }
        	        
		return rfaSpaceResponse;
	}
	
	private Object postService(String url, String query, Map<String, String> headers, Map<String, Object> data, Function<RFAPayload, ?> responseFunc) throws StrategyServiceException
	{
		RFAGraphQLResponse rfaGraphQLResponse = rfaGraphQLService.post(url, query, headers,data);

        if (CollectionUtils.isEmpty(rfaGraphQLResponse.getErrors()))
            return Optional.ofNullable(rfaGraphQLResponse)
                    .stream()
                    .map(RFAGraphQLResponse::getData)
                    .map(responseFunc)
                    .findFirst()
                    .orElse(null);

        log.error("Error returned in GraphQL call: {}", rfaGraphQLResponse.getErrors());
        return null;
	}
}
