package com.walmart.aex.strategy.dto.request.update;

import lombok.Data;

import java.util.List;

@Data
public class Lvl12AttributeGrpInput {

    private Integer lvl2Nbr;
    private List<Lvl13AttributeGrpInput> lvl3List;
}
