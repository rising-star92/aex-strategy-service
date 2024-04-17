package com.walmart.aex.strategy.dto;

import com.walmart.aex.strategy.dto.request.AllLvls;
import com.walmart.aex.strategy.dto.request.Lvl1;
import lombok.Data;

import java.util.List;

@Data
public class LinePlanStrategyResponse {

    private Long planId;

    private String planDesc;

    private Integer lvl0Nbr;

    private String lvl0Name;

    private AllLvls all;

    private List<Lvl1> lvl1List;


}
