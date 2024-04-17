package com.walmart.aex.strategy.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class Lvl2 {
    private Integer lvl2Nbr;

    private String lvl2Name;

    private Strategy strategy;

    private List<Lvl3> lvl3List;
}
