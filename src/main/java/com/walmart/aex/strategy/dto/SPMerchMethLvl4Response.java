package com.walmart.aex.strategy.dto;

import lombok.Data;

import java.util.List;
@Data
public class SPMerchMethLvl4Response {
    private Integer lvl4Nbr;
    private String lvl4Desc;
    private List<SPMerchMethFixtureResponse> fixtureTypes;
    private List<SPMerchMethFinelineResponse> finelines;
}
