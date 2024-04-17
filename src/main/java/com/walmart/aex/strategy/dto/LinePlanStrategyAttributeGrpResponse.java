package com.walmart.aex.strategy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LinePlanStrategyAttributeGrpResponse {
    private Long planId;

    private String planDesc;
}
