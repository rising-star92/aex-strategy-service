package com.walmart.aex.strategy.dto.PlanDTO;

import lombok.Data;

import java.util.List;

@Data
public class PlanHierarchyDTO {
    private Integer lvl0Nbr;
    private String lvl0GenDesc1;
    private List<Lvl1DTO> lvl1;
}
