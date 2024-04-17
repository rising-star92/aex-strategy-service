package com.walmart.aex.strategy.dto.PlanDTO;

import lombok.Data;

import java.util.List;

@Data
public class Lvl2DTO {
    private Integer lvl2Nbr;
    private String lvl2GenDesc1;
    private List<Lvl3DTO> lvl3;
}
