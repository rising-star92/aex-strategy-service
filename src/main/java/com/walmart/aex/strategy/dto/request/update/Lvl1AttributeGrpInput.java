package com.walmart.aex.strategy.dto.request.update;

import lombok.Data;

import java.util.List;

@Data
public class Lvl1AttributeGrpInput {

    private Integer lvl1Nbr;
    private List<Lvl12AttributeGrpInput> lvl2List;
}
