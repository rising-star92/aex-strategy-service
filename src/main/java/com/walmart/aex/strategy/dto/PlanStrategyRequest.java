package com.walmart.aex.strategy.dto;

import com.walmart.aex.strategy.dto.request.Lvl3;
import com.walmart.aex.strategy.dto.request.UpdateAll;
import lombok.Data;

import java.util.List;

@Data
public class PlanStrategyRequest {
    private Long planId;
    private UpdateAll all;
    private List<Lvl3> lvl3List;
}
