package com.walmart.aex.strategy.dto;

import com.walmart.aex.strategy.dto.request.Lvl3;
import lombok.Data;

import java.util.List;

@Data
public class PlanStrategyResponse {
    private Long planId;
    private Long fixtureStrategyId;
    private Long weatherClusterStrategyId;
    private String seasonCode;
    private Integer fiscalYear;
    private Integer lvl0Nbr;
    private Integer lvl1Nbr;
    private Integer lvl2Nbr;
    private String status;
    private List<Lvl3> lvl3List;
}
