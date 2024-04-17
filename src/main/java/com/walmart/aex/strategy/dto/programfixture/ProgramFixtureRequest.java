package com.walmart.aex.strategy.dto.programfixture;

import lombok.Data;

@Data
public class ProgramFixtureRequest {

    private Integer deptNumber;

    private Integer startYrWeek;

    private Integer endYrWeek;

    private MerchantAllocationsDataRequest merchantAllocationsDataRequest;

}
