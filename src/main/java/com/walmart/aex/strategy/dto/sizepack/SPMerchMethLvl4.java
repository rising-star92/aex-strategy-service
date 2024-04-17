package com.walmart.aex.strategy.dto.sizepack;

import lombok.Data;

import java.util.List;

@Data
public class SPMerchMethLvl4 {
    private Integer lvl4Nbr;
    private String lvl4Desc;
    private List<SPMerchMethFixture> fixtureTypes;
    private List<SPMerchMethFineline> finelines;
}
