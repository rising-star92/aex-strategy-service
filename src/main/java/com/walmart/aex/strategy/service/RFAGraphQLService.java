package com.walmart.aex.strategy.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.walmart.aex.strategy.dto.assortproduct.gql.RFAGraphQLRequest;
import com.walmart.aex.strategy.dto.assortproduct.gql.RFAGraphQLResponse;
import com.walmart.aex.strategy.exception.StrategyServiceException;

import lombok.extern.slf4j.Slf4j;
import java.util.*;

@Service
@Slf4j
public class RFAGraphQLService {

	private static final String RESPONSE_URL = "Received response URL {}  Status {} Time taken {} ms";
	
	private final RestTemplate restTemplate;
	
	public RFAGraphQLService(@Qualifier("APRestTemplate") RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}    
    
    public RFAGraphQLResponse post(String url, String query, Map<String, String> headers, Map<String, Object> data) throws StrategyServiceException {
        log.info("Calling GET URL {} data {}", url, data);
        long startTime = System.currentTimeMillis();
        HttpHeaders httpHeaders = getHttpHeaders(headers);
        ResponseEntity<RFAGraphQLResponse> response = null;
        try {
            RFAGraphQLRequest rfaGraphQLReq = new RFAGraphQLRequest(query, data);
            HttpEntity<RFAGraphQLRequest> request = new HttpEntity<>(rfaGraphQLReq, httpHeaders);
            response = restTemplate.exchange(url, HttpMethod.POST, request, RFAGraphQLResponse.class);
            log.info(RESPONSE_URL, url, response.getStatusCode(), (System.currentTimeMillis() - startTime));
            
            log.info("Response: {}", response.getBody());
        } catch (Exception e) {
            log.error("Exception occurred while sending request to {} and exception {}", url, e.getMessage());
            throw new StrategyServiceException("Unable to call api " + url);
        }
        return response.getBody();
    }

    private HttpHeaders getHttpHeaders(Map<String, String> headers) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setAccept(List.of(MediaType.APPLICATION_JSON));
        httpHeaders.setCacheControl(CacheControl.noCache());
        if (headers != null && !headers.isEmpty()) {
            headers.forEach(httpHeaders::add);
        }
        return httpHeaders;
    }
}
