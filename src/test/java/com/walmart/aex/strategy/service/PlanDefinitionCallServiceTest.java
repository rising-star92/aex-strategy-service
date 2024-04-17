package com.walmart.aex.strategy.service;

import com.walmart.aex.strategy.exception.CustomException;
import com.walmart.aex.strategy.properties.AppProperties;
import com.walmart.aex.strategy.properties.RFAGraphQLProperties;
import org.junit.Assert;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlanDefinitionCallServiceTest {

    PlanDefinitionCallService planDefinitionCallService;
    RFAGraphQLProperties connProperties;
    AppProperties appProperties;

    RestTemplate restTemplate;

    @BeforeEach
    public void SetUp() {
        connProperties = Mockito.mock(RFAGraphQLProperties.class);
        appProperties = Mockito.mock(AppProperties.class);
        restTemplate = Mockito.mock(RestTemplate.class);
        planDefinitionCallService = new PlanDefinitionCallService(restTemplate);
    }
    @Test
    void getAexPlansGettingImpactedTest(){
        Integer deptNbr = 34;
        Integer startWeek = 202530;
        Integer endWeek = 202552;
        List<Integer> planIds = Arrays.asList(1,2);

        Mockito.when(connProperties.getAssortProductConsumerId()).thenReturn("1233333333333");
        Mockito.when(connProperties.getAssortProductConsumerEnv()).thenReturn("dev");
        Mockito.when(appProperties.getPlanDefinitionUrl()).thenReturn("http://dev.walmart.com/");
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(ParameterizedTypeReference.class)))
                .thenReturn(new ResponseEntity<>(planIds, HttpStatus.OK));
        ReflectionTestUtils.setField(planDefinitionCallService, "connProperties", connProperties);
        ReflectionTestUtils.setField(planDefinitionCallService, "appProperties", appProperties);

        List<Integer> responseIds = planDefinitionCallService.getAexPlansGettingImpacted(deptNbr, startWeek,endWeek);

        Assert.assertEquals(planIds.size(), responseIds.size());

    }

    @Test
    void getAexPlansGettingImpactedFailureTest(){
        Integer deptNbr = 34;
        Integer startWeek = 202530;
        Integer endWeek = 202552;
        List<Integer> planIds = Arrays.asList(1,2);

        Mockito.when(connProperties.getAssortProductConsumerId()).thenReturn("1233333333333");
        Mockito.when(connProperties.getAssortProductConsumerEnv()).thenReturn("dev");
        Mockito.when(appProperties.getPlanDefinitionUrl()).thenReturn("http://dev.walmart.com/");
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(ParameterizedTypeReference.class)))
                .thenThrow(CustomException.class);
        ReflectionTestUtils.setField(planDefinitionCallService, "connProperties", connProperties);
        ReflectionTestUtils.setField(planDefinitionCallService, "appProperties", appProperties);

        try{
            List<Integer> responseIds = planDefinitionCallService.getAexPlansGettingImpacted(deptNbr, startWeek, endWeek);
        } catch(Exception ex){
            assertTrue(ex.getMessage().contains("Error While fetching planIds from cbam plan definition"));
        }
    }
}
