package com.walmart.aex.strategy.dto.sizepack;

import lombok.Data;

import java.util.List;

@Data
public class SPMerchMethFineline {
    private Integer finelineNbr;
    private String finelineDesc;
    private List<SPMerchMethFixture> fixtureTypes;
}
