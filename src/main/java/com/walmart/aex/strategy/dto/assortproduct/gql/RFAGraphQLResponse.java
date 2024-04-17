package com.walmart.aex.strategy.dto.assortproduct.gql;

import java.util.List;
import lombok.Data;

@Data
public class RFAGraphQLResponse {
	private RFAPayload data;
    private List<RFAError> errors;
}
