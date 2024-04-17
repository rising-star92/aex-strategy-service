package com.walmart.aex.strategy.dto.request.update;

import lombok.Data;

import java.util.List;

@Data
public class Lvl2Input {
    private Integer lvl2Nbr;
    private List<Lvl3InputLP> lvl3List;
}
