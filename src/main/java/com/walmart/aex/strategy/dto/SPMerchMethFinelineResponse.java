package com.walmart.aex.strategy.dto;

import lombok.Data;

import java.util.List;
@Data
public class SPMerchMethFinelineResponse {
    private Integer finelineNbr;
    private String finelineDesc;
    private String altFinelineName;
    private List<SPMerchMethFixtureResponse> fixtureTypes;
}
