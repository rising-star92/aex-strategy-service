package com.walmart.aex.strategy.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class Lvl3 {

    private Integer lvl3Nbr;

    private String lvl3Name;

    private String channel;

    private UpdatedFields updatedFields;

    private Strategy strategy;

    private List<Lvl4> lvl4List;

}
