package com.walmart.aex.strategy.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PlanStrategySPResponse {
    private Long planId;
    private Integer lvl0Nbr;
    private Integer lvl1Nbr;
    private Integer lvl2Nbr;
    private String status;
    private List<Lvl3ListSPResponse> lvl3List = new ArrayList<Lvl3ListSPResponse>();
}
