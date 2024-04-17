package com.walmart.aex.strategy.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.strategy.dto.programfixture.ProgramFixtureRequest;
import com.walmart.aex.strategy.dto.programfixture.ProgramFixtureResponse;
import com.walmart.aex.strategy.properties.ProgramFixtureProperties;
import org.junit.jupiter.api.Assertions;
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
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProgramFixtureServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ProgramFixtureProperties programFixtureProperties;

    @InjectMocks
    private ProgramFixtureService programFixtureService;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        programFixtureProperties = PowerMockito.mock(ProgramFixtureProperties.class);
    }

    @Test
    void testFetchProgramFixtures() throws IOException {
        // Arrange
        File programFixtureRequestFile =
                new File(Objects.requireNonNull(this.getClass().getResource("/data/programFixtureRequest.json"))
                        .getFile());
        ProgramFixtureRequest request = objectMapper.readValue(programFixtureRequestFile, ProgramFixtureRequest.class);

        File programFixtureResponseFile =
                new File(Objects.requireNonNull(this.getClass().getResource("/data/programFixtureResponse.json"))
                        .getFile());
        ProgramFixtureResponse response = objectMapper.readValue(programFixtureResponseFile, ProgramFixtureResponse.class);

        ReflectionTestUtils.setField(programFixtureService, "programFixtureProperties", programFixtureProperties);

        when(programFixtureProperties.getCsaBaseUrl()).thenReturn("http://localhost:8080");
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class),
                any(ParameterizedTypeReference.class))).thenReturn(new ResponseEntity<>(response, HttpStatus.OK));

        // Act
        ProgramFixtureResponse programFixtureResponse = programFixtureService.fetchProgramFixtures(request);

        // Assert
        assertNotNull(programFixtureResponse);
    }

}
