package com.walmart.aex.strategy.dto.PlanDTO;

import lombok.Data;

import java.util.List;

@Data
public class PlanDTO {
    private Long planId;
    private String planDesc;
    private Integer fiscalYear;
    private String fiscalYearDesc;
    private List<MerchantDTO> merchant;
    private PlanHierarchyDTO planHierarchy;
}
