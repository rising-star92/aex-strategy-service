package com.walmart.aex.strategy.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PlanStrategySP {

	private Long planId;
    private Integer lvl0Nbr;
    private Integer lvl1Nbr;
    private Integer lvl2Nbr;
    private List<Lvl3ListSP> lvl3List = new ArrayList<Lvl3ListSP>();
}
