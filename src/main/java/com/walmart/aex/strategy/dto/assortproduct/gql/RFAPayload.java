package com.walmart.aex.strategy.dto.assortproduct.gql;

import com.walmart.aex.strategy.dto.assortproduct.RFASpaceResponse;

import lombok.Data;

@Data
public class RFAPayload {
	private RFASpaceResponse getRFAFinelineSpaceAllocation;
    private RFASpaceResponse getRFAStyleCcSpaceAllocation;
}