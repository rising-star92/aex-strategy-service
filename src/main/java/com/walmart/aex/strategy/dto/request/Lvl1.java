package com.walmart.aex.strategy.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class Lvl1 {
    private Integer lvl1Nbr;
    private String lvl1Name;
    private List<Lvl2> lvl2List;
}
