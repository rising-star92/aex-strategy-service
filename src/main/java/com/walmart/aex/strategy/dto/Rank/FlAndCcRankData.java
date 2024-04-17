package com.walmart.aex.strategy.dto.Rank;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FlAndCcRankData {
    private Long planId;
    private Integer lvl0Nbr;
    private Integer lvl1Nbr;
    private Integer lvl2Nbr;
    private Integer lvl3Nbr;
    private Integer lvl4Nbr;
    private Integer finelineNbr;
    private Integer flRank;
    private String styleNbr;
    private String customerChoice;
    private Integer ccRank;

    public FlAndCcRankData(Long planId, Integer lvl0Nbr, Integer lvl1Nbr, Integer lvl2Nbr,
                           Integer lvl3Nbr, Integer lvl4Nbr, Integer finelineNbr, String styleNbr,
                           String customerChoice, Integer ccRank) {
        this.planId = planId;
        this.lvl0Nbr = lvl0Nbr;
        this.lvl1Nbr = lvl1Nbr;
        this.lvl2Nbr = lvl2Nbr;
        this.lvl3Nbr = lvl3Nbr;
        this.lvl4Nbr = lvl4Nbr;
        this.finelineNbr = finelineNbr;
        this.styleNbr = styleNbr;
        this.customerChoice = customerChoice;
        this.ccRank = ccRank;
    }

    public FlAndCcRankData(Long planId, Integer lvl0Nbr, Integer lvl1Nbr, Integer lvl2Nbr, Integer lvl3Nbr, Integer lvl4Nbr, Integer finelineNbr, Integer flRank) {
        this.planId = planId;
        this.lvl0Nbr = lvl0Nbr;
        this.lvl1Nbr = lvl1Nbr;
        this.lvl2Nbr = lvl2Nbr;
        this.lvl3Nbr = lvl3Nbr;
        this.lvl4Nbr = lvl4Nbr;
        this.finelineNbr = finelineNbr;
        this.flRank = flRank;
    }
}
