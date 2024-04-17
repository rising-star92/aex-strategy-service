package com.walmart.aex.strategy.dto.assortproduct.gql;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RFAGraphQLRequest {
	private String query;
    private Map<String, Object> variables;
}
