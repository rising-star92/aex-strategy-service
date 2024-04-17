package com.walmart.aex.strategy.dto.request.update;

import com.walmart.aex.strategy.dto.request.StrategyType;
import lombok.Data;

import java.util.List;

@Data
public class LinePlanStrategyUpdateRequest {

    private Long planId;

    private String planDesc;

    private StrategyType strategyType;

    private Integer lvl0Nbr;

    private String channel;

    private AllInput all;

    private List<Lvl1Input> lvl1List;
}
