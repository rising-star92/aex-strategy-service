package com.walmart.aex.strategy.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.walmart.aex.strategy.properties.AppProperties;
import com.walmart.aex.strategy.util.CommonUtil;
import io.strati.ccm.utils.client.annotation.ManagedConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Service
@Slf4j
public class AttributeHelperService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    @ManagedConfiguration
    private AppProperties appProperties;

    public AttributeHelperService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public Set<String> retrieveValuesByHierarchy(String attributeType, Integer lvl1Nbr, Integer lvl3Nbr, String season) {
        HashSet<String> values = new HashSet<>();
        String requestPayload = fetchRequestPayload(attributeType, lvl1Nbr, lvl3Nbr, CommonUtil.getAHSSeason(season));
        HttpHeaders headers = fetchHeaders();

        try {
            ResponseEntity<String> response = restTemplate.exchange(appProperties.getAHSUrl(), HttpMethod.POST, new HttpEntity<>(requestPayload, headers), String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                ArrayNode jsonNode = (ArrayNode) objectMapper.readTree(Objects.requireNonNull(response.getBody()).toLowerCase());
                return new HashSet<>(jsonNode.findValuesAsText("value"));
            }
        } catch (Exception e) {
            log.error("Error occurred while fetching value from AHS", e);
        }
        return values;
    }

    private HttpHeaders fetchHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("wm_svc.name", "ATTRIBUTE-HELPER-SERVICE");
        headers.set("wm_svc.env", appProperties.getAHSEnv());
        headers.set("wm_consumer.id", appProperties.getAHSConsumerId());
        return headers;
    }

    private String fetchRequestPayload(String attributeType, Integer lvl1Nbr, Integer lvl3Nbr, String season) {
        return "{\"attributeType\":\"" + attributeType + "\",\"categoryId\":" + lvl3Nbr + ",\"departmentId\":" + lvl1Nbr + ",\"season\":\"" + season + "\"}";
    }


}

