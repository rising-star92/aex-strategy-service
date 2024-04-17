package com.walmart.aex.strategy.service;

import com.walmart.aex.strategy.dto.programfixture.MerchantAllocationsDataRequest;
import com.walmart.aex.strategy.dto.programfixture.ProgramFixtureRequest;
import com.walmart.aex.strategy.dto.programfixture.ProgramFixtureResponse;
import com.walmart.aex.strategy.exception.CustomException;
import com.walmart.aex.strategy.properties.ProgramFixtureProperties;
import io.strati.ccm.utils.client.annotation.ManagedConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
@Slf4j
public class ProgramFixtureService {

    private final RestTemplate restTemplate;

    @ManagedConfiguration
    private ProgramFixtureProperties programFixtureProperties;

    public ProgramFixtureService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ProgramFixtureResponse fetchProgramFixtures(ProgramFixtureRequest request) {

        if (Optional.ofNullable(request).map(ProgramFixtureRequest::getMerchantAllocationsDataRequest)
                .map(MerchantAllocationsDataRequest::getMerchantName)
                .filter(StringUtils::isNotEmpty).isPresent()) {

            log.info("Fetching Program Fixture info for dept: {}, weeks: {} to {} & merchant: {}", request.getDeptNumber(),
                    request.getStartYrWeek(), request.getEndYrWeek(),
                    request.getMerchantAllocationsDataRequest().getMerchantName());

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            httpHeaders.set("WM_SVC.NAME", "AEX_STRATEGY_SERVICE");

            ResponseEntity<ProgramFixtureResponse> response = restTemplate
                    .exchange(programFixtureProperties.getCsaBaseUrl() + "/v1/csa-service/ap-data/programs-fixture",
                    HttpMethod.POST, new HttpEntity<>(request, httpHeaders), new ParameterizedTypeReference<>() {});

            return Optional.of(response).map(ResponseEntity::getBody).orElse(null);
        } else {
            log.error("Merchant Name is not available in the request...");
            throw new CustomException("Merchant Name is not available...");
        }

    }

}
