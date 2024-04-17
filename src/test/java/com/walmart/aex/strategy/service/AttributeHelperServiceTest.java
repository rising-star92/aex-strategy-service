package com.walmart.aex.strategy.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.strategy.properties.AppProperties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.powermock.api.mockito.PowerMockito;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;


@ExtendWith(MockitoExtension.class)
class AttributeHelperServiceTest {
    @InjectMocks
    private AttributeHelperService attributeHelperService;

    @Mock
    private RestTemplate restTemplate;

    @Spy
    private ObjectMapper objectMapper;

    @Test
    void testRetrieveValuesByHierarchy() throws IOException {
        AppProperties appProperties = PowerMockito.mock(AppProperties.class);
        PowerMockito.when(appProperties.getAHSUrl()).thenReturn("https://localhost:8080");
        ReflectionTestUtils.setField(attributeHelperService, "appProperties", appProperties);

        String json = new String(Files.readAllBytes(Paths.get("src/test/resources/ahsResponse.json")));
        Mockito.when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(String.class))).thenReturn(ResponseEntity.ok(json));
        Set<String> result = attributeHelperService.retrieveValuesByHierarchy("colorFamily", 1056198, 34, "S1-FYE24");
        Assertions.assertEquals(18, result.size());
    }


}
