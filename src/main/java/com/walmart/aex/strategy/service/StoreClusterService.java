package com.walmart.aex.strategy.service;

import com.walmart.aex.strategy.dto.storecluster.StoreCluster;
import com.walmart.aex.strategy.dto.storecluster.StoreClusterCreateResponse;
import com.walmart.aex.strategy.dto.storecluster.StoreClusterSearchRequest;
import com.walmart.aex.strategy.properties.StoreClusterProperties;
import io.strati.ccm.utils.client.annotation.ManagedConfiguration;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class StoreClusterService {

    private final RestTemplate restTemplate;

    @ManagedConfiguration
    private StoreClusterProperties storeClusterProperties;

    public StoreClusterService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<StoreCluster> fetchStoreClusters(StoreClusterSearchRequest request) {

        ResponseEntity<List<StoreCluster>> responseEntity = restTemplate
                .exchange(storeClusterProperties.getStoreClusterBaseUrl() + "/graphql",
                        HttpMethod.POST, new HttpEntity<>(request, getHttpHeaders()),
                        new ParameterizedTypeReference<>() {});

        return Optional.of(responseEntity).map(ResponseEntity::getBody).orElse(null);

    }

    public StoreClusterCreateResponse createStoreClusters(StoreCluster storeCluster) {

        ResponseEntity<StoreClusterCreateResponse> responseEntity = restTemplate
                .exchange(storeClusterProperties.getStoreClusterBaseUrl() + "/graphql",
                        HttpMethod.POST, new HttpEntity<>(storeCluster, getHttpHeaders()),
                        new ParameterizedTypeReference<>() {});

        return Optional.of(responseEntity).map(ResponseEntity::getBody).orElse(null);
    }

    @NotNull
    private static HttpHeaders getHttpHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return httpHeaders;
    }

}
