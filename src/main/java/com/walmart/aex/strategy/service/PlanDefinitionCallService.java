package com.walmart.aex.strategy.service;

import com.walmart.aex.strategy.exception.CustomException;
import com.walmart.aex.strategy.properties.AppProperties;
import com.walmart.aex.strategy.properties.RFAGraphQLProperties;
import io.strati.ccm.utils.client.annotation.ManagedConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PlanDefinitionCallService {


    private final RestTemplate restTemplate;

    @ManagedConfiguration
    private RFAGraphQLProperties connProperties;

    @ManagedConfiguration
    private AppProperties appProperties;

    public PlanDefinitionCallService(@Qualifier("APRestTemplate") RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }

    @Retryable(value = {CustomException.class}, maxAttempts = 3, backoff = @Backoff(delay = 500))
    public List<Integer> getAexPlansGettingImpacted(Integer deptNbr, Integer startWeek, Integer endWeek) {
        long startTime = System.currentTimeMillis();
        try {
            HttpHeaders headers = setPlanDefinitionHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(headers);
            String url = appProperties.getPlanDefinitionUrl() + "/getAllPlanIdsByPlanWeeks/deptNbr/" + deptNbr + "/startWeek/" + startWeek + "/endWeek/" + endWeek;
            ResponseEntity<List<Integer>> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, new ParameterizedTypeReference<>() {
            });
            long endTime = System.currentTimeMillis();
            log.info("Total time taken for fetching planIds based on weeks: {}", endTime - startTime);
            return Optional.ofNullable(responseEntity).map(ResponseEntity::getBody)
                    .stream().flatMap(Collection::stream).collect(Collectors.toList());
        } catch (Exception ex) {
            log.error(String.format("Error While fetching planIds from cbam plan definition : %s ", ex.getMessage()));
            throw new CustomException(String.format("Error While fetching planIds from cbam plan definition : %s ", ex.getMessage()));
        }

    }

    public HttpHeaders setPlanDefinitionHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.set("wm_consumer.id", connProperties.getAssortProductConsumerId());
        headers.set("wm_svc.name", "CBAM_PLAN_DEFINITION");
        headers.set("wm_svc.env", connProperties.getAssortProductConsumerEnv());
        return headers;
    }
}
