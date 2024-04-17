package com.walmart.aex.strategy.dto.customstore;

import lombok.Data;

@Data
public class CustomStoreRequest {

    private String programName;

    private Long planId;

    private Integer deptNbr;

    private Integer startWeek;

    private Integer endWeek;

    private String primaryMerchantName;

}
