package com.walmart.aex.strategy.dto;

import lombok.Data;

@Data
public class SPMerchMethFixtureResponse {
    private Integer merchMethodCode;
    private String merchMethodDesc;
    private Integer fixtureTypeRollupId;
    private String fixtureType;
}
