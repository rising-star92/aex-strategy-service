package com.walmart.aex.strategy.service;

import com.walmart.aex.strategy.dto.midas.FinelineRankMetricsDTO;
import com.walmart.aex.strategy.dto.midas.StrongKeyFlat;
import com.walmart.aex.strategy.exception.ClpApException;
import com.walmart.aex.strategy.properties.MidasApiProperties;
import com.walmart.aex.strategy.properties.CredProperties;
import io.strati.ccm.utils.client.annotation.ManagedConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;

@Service
@Slf4j
public class MidasServiceCall {

    @ManagedConfiguration
    private MidasApiProperties midasProperties;

    @Autowired
    @Qualifier("APRestTemplate")
    RestTemplate restTemplate;

    @Autowired
    CredProperties credProperties;


    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 10000))
    public FinelineRankMetricsDTO invokeMidasApi(StrongKeyFlat strongKeyFlat) {
        try {
            Integer lvl1Nbr = strongKeyFlat.getLvl1Nbr();
            Integer lvl2Nbr = strongKeyFlat.getLvl2Nbr();
            Long finelineNbr = strongKeyFlat.getFinelineNbr().longValue();
            String season = strongKeyFlat.getSeasonCode();
            Integer fiscalYear = strongKeyFlat.getFiscalYear();

            String query = String.format(midasProperties.getAPRankingMetricsQuery(), lvl1Nbr, lvl2Nbr, finelineNbr, season, fiscalYear);
            String url = midasProperties.getMidasApiBaseURL();
            log.info("Invoking Midas API for Create event with URL : {} and query : {}", url, query);

            ResponseEntity<FinelineRankMetricsDTO> result =
                    restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(query, getHeadersForMidas()), FinelineRankMetricsDTO.class);
            log.info("Successfully invoked Midas Api with result = {}", result);
            return result.getBody();
        } catch (Exception e) {
            log.error("Exception in Getting Midas Data for the given Input", e);
            throw new ClpApException("Exception calling midas for analytics metrics: " + strongKeyFlat.toString()+"/n"+e.getMessage(), e);
        }
    }

    private HttpHeaders getHeadersForMidas() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("WM_SVC.NAME", "aex-MIDAS-Service-Call");
        headers.set("consumer", midasProperties.getMidasHeaderConsumer());
        headers.set("signature_key_version", midasProperties.getMidasHeaderSignatureKeyVersion());
        headers.set("signature_ts", midasProperties.getMidasHeaderSignatureTS());
        headers.set("signature_auth_flag", midasProperties.getMidasHeaderSignatureAuthFlag());
        headers.set("request_ts", String.valueOf(Instant.now().getEpochSecond()));
        headers.set("tenant", midasProperties.getMidasHeaderTenant());
        headers.set("Authorization", credProperties.fetchMidasApiAuthorization());
        return headers;
    }

    @Recover
    public String recover(Exception e, StrongKeyFlat header) {
        throw new ClpApException("Timeout calling midas for analytics metrics: " + header.toString(), e);
    }

}
