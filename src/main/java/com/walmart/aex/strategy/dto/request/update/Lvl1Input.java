package com.walmart.aex.strategy.dto.request.update;

import lombok.Data;

import java.util.List;

@Data
public class Lvl1Input {
    private Integer lvl1Nbr;
    private List<Lvl2Input> lvl2List;
}
