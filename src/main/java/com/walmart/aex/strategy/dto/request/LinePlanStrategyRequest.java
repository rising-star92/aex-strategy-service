package com.walmart.aex.strategy.dto.request;

import lombok.Data;

@Data
public class LinePlanStrategyRequest {

    private Long planId;

    private String planDesc;

    private String channel;

    private String attribute;

    private StrategyType strategyType;

}
