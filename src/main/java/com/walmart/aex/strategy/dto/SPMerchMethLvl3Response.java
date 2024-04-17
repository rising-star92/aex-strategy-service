package com.walmart.aex.strategy.dto;

import com.walmart.aex.strategy.dto.sizepack.SPMerchMethFixture;
import com.walmart.aex.strategy.dto.sizepack.SPMerchMethLvl4;
import lombok.Data;

import java.util.List;
@Data
public class SPMerchMethLvl3Response {
    private Integer lvl3Nbr;
    private String lvl3Desc;
    private List<SPMerchMethFixtureResponse> fixtureTypes;
    private List<SPMerchMethLvl4Response> lvl4List;
}
