package com.walmart.aex.strategy.dto.sizeprofile;

import lombok.Data;

import java.util.List;

@Data
public class Lvl3List {
    private Integer lvl3Nbr;
    private String lvl3Desc;
    private List<Lvl4List> lvl4List;
}
