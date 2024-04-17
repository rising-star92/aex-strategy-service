package com.walmart.aex.strategy.dto.programfixture;

import lombok.Data;

import java.util.List;

@Data
public class ProgramFixtureResponse {

    private MerchantAllocationsDataRequest merchantAllocationsDataRequest;

    private List<ProgramDetail> programDetails;

}
