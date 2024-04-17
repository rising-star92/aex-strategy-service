package com.walmart.aex.strategy.dto.PlanDTO;

import lombok.Data;

import java.util.List;

@Data
public class Lvl1DTO {
    private Integer lvl1Nbr;
    private String lvl1GenDesc1;
    private List<Lvl2DTO> lvl2;
}
