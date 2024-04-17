package com.walmart.aex.strategy.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.strategy.dto.programfixture.ProgramFixtureRequest;
import com.walmart.aex.strategy.dto.programfixture.ProgramFixtureResponse;
import com.walmart.aex.strategy.dto.storecluster.StoreCluster;
import com.walmart.aex.strategy.dto.storecluster.StoreClusterCreateResponse;
import com.walmart.aex.strategy.dto.storecluster.StoreClusterSearchRequest;
import com.walmart.aex.strategy.properties.ProgramFixtureProperties;
import com.walmart.aex.strategy.properties.StoreClusterProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.powermock.api.mockito.PowerMockito;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StoreClusterServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private StoreClusterProperties storeClusterProperties;

    @InjectMocks
    private StoreClusterService storeClusterService;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        storeClusterProperties = PowerMockito.mock(StoreClusterProperties.class);
    }

    @Test
    void testFetchStoreClusters() throws IOException {
        // Arrange
        File storeClusterSearchRequestFile =
                new File(Objects.requireNonNull(this.getClass().getResource("/data/storeClusterSearchRequest.json"))
                        .getFile());
        StoreClusterSearchRequest request = objectMapper.readValue(storeClusterSearchRequestFile, StoreClusterSearchRequest.class);

        File storeClusterFile =
                new File(Objects.requireNonNull(this.getClass().getResource("/data/storeClusters.json"))
                        .getFile());
        List<StoreCluster> response = List.of(objectMapper.readValue(storeClusterFile, StoreCluster[].class));

        ReflectionTestUtils.setField(storeClusterService, "storeClusterProperties", storeClusterProperties);

        when(storeClusterProperties.getStoreClusterBaseUrl()).thenReturn("http://localhost:8080");
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class),
                any(ParameterizedTypeReference.class))).thenReturn(new ResponseEntity<>(response, HttpStatus.OK));

        // Act
        List<StoreCluster> storeClusters = storeClusterService.fetchStoreClusters(request);

        // Assert
        assertNotNull(storeClusters);
    }

    @Test
    void testCreateStoreClusters() throws IOException {
        // Arrange
        File storeClusterFile =
                new File(Objects.requireNonNull(this.getClass().getResource("/data/storeCluster.json"))
                        .getFile());
        StoreCluster request = objectMapper.readValue(storeClusterFile, StoreCluster.class);

        File storeClusterCreateResponseFile =
                new File(Objects.requireNonNull(this.getClass().getResource("/data/storeClusterCreateResponse.json"))
                        .getFile());
        StoreClusterCreateResponse response = objectMapper.readValue(storeClusterCreateResponseFile,
                StoreClusterCreateResponse.class);

        ReflectionTestUtils.setField(storeClusterService, "storeClusterProperties", storeClusterProperties);

        when(storeClusterProperties.getStoreClusterBaseUrl()).thenReturn("http://localhost:8080");
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class),
                any(ParameterizedTypeReference.class))).thenReturn(new ResponseEntity<>(response, HttpStatus.OK));

        // Act
        StoreClusterCreateResponse storeClusterCreateResponse = storeClusterService.createStoreClusters(request);

        // Assert
        assertNotNull(storeClusterCreateResponse);
        assertTrue(storeClusterCreateResponse.isSuccess());
    }

}
