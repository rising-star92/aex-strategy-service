package com.walmart.aex.strategy.dto.sizepack;

import lombok.Data;

import java.util.List;
@Data
public class SPMerchMethLvl3 {
    private Integer lvl3Nbr;
    private String lvl3Desc;
    private List<SPMerchMethFixture> fixtureTypes;
    private List<SPMerchMethLvl4> lvl4List;
}
