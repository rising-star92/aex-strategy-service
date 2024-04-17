package com.walmart.aex.strategy.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
public class PlanFinelinesThickness {
    private Long planId;
    private Integer lvl0Nbr;
    private Integer lvl1Nbr;
    private Integer lvl2Nbr;
    private Integer lvl3Nbr;
    private Integer lvl4Nbr;
    private Integer finelineNbr;
    private Integer thicknessId;

    public PlanFinelinesThickness( Long planId, Integer lvl0Nbr, Integer lvl1Nbr, Integer lvl2Nbr, Integer lvl3Nbr,
                                   Integer lvl4Nbr, Integer finelineNbr, Integer thicknessId){
        this.planId = planId;
        this.lvl0Nbr = lvl0Nbr;
        this.lvl1Nbr = lvl1Nbr;
        this.lvl2Nbr = lvl2Nbr;
        this.lvl3Nbr = lvl3Nbr;
        this.lvl4Nbr = lvl4Nbr;
        this.finelineNbr = finelineNbr;
        this.thicknessId = thicknessId;
    }
}