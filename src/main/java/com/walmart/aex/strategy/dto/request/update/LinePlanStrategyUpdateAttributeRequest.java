package com.walmart.aex.strategy.dto.request.update;

import com.walmart.aex.strategy.dto.request.StrategyType;
import lombok.Data;

import java.util.List;

@Data
public class LinePlanStrategyUpdateAttributeRequest {

    private Long planId;

    private StrategyType strategyType;

    private String planDesc;

    private Integer lvl0Nbr;

    private String channel;

    private List<Lvl1AttributeGrpInput> lvl1List;
}
