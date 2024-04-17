package com.walmart.aex.strategy.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.strategy.dto.customstore.CustomStoreRequest;
import com.walmart.aex.strategy.dto.programfixture.ProgramFixtureRequest;
import com.walmart.aex.strategy.dto.programfixture.ProgramFixtureResponse;
import com.walmart.aex.strategy.dto.storecluster.StoreCluster;
import com.walmart.aex.strategy.dto.storecluster.StoreClusterCreateResponse;
import com.walmart.aex.strategy.dto.storecluster.StoreClusterSearchRequest;
import com.walmart.aex.strategy.properties.StoreClusterProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.powermock.api.mockito.PowerMockito;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomStoreServiceTest {

    @Spy
    private ExecutorService executorService = Executors.newFixedThreadPool(2);

    @Mock
    private ProgramFixtureService programFixtureService;

    @Mock
    private StoreClusterService storeClusterService;

    @InjectMocks
    private CustomStoreService customStoreService;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Test
    void testFetchStoreClusters() throws IOException {
        // Arrange
        File customStoreRequestFile =
                new File(Objects.requireNonNull(this.getClass().getResource("/data/customStoreRequest.json"))
                        .getFile());
        CustomStoreRequest request  = objectMapper.readValue(customStoreRequestFile, CustomStoreRequest.class);

        File programFixtureResponseFile =
                new File(Objects.requireNonNull(this.getClass().getResource("/data/programFixtureResponse.json"))
                        .getFile());
        ProgramFixtureResponse programFixtureResponse = objectMapper.readValue(programFixtureResponseFile,
                ProgramFixtureResponse.class);

        File storeClustersFile =
                new File(Objects.requireNonNull(this.getClass().getResource("/data/storeClusters.json"))
                        .getFile());
        List<StoreCluster> storeClusters = List.of(objectMapper.readValue(storeClustersFile, StoreCluster[].class));

        when(programFixtureService.fetchProgramFixtures(any(ProgramFixtureRequest.class)))
                .thenReturn(programFixtureResponse);
        when(storeClusterService.fetchStoreClusters(any(StoreClusterSearchRequest.class))).thenReturn(storeClusters);

        // Act
        ByteArrayInputStream stream = customStoreService.fetchCustomStoreExcel(request);

        // Assert
        assertNotNull(stream);
    }

}
