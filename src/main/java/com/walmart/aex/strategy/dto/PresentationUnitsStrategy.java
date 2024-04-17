package com.walmart.aex.strategy.dto;

import lombok.Data;

@Data
public class PresentationUnitsStrategy {

    private Long PlanId;

    private Long strategyId;

    private Integer lvl0Nbr;

    private Integer lvl1Nbr;

    private Integer lvl2Nbr;

    private Integer lvl3Nbr;

    private String lvl3Name;

    private Integer lvl4Nbr;

    private String lvl4Name;

    private Integer finelineNbr;

    private String finelineName;

    private String altFinelineName;

    private String fixtureRoleName;

    private Integer merchCatgMinPresentationUnit;

    private Integer merchCatgMaxPresentationUnit;

    private Integer subCatgMinPresentationUnit;

    private Integer subCatgMaxPresentationUnit;

    private Integer flMinPresentationUnit;

    private Integer flMaxPresentationUnit;

    public PresentationUnitsStrategy(Long planId, Long strategyId, Integer lvl0Nbr, Integer lvl1Nbr, Integer lvl2Nbr, Integer lvl3Nbr, String lvl3Name, Integer lvl4Nbr,
                                     String lvl4Name, Integer finelineNbr, String finelineName, String altFinelineName, String fixtureRoleName , Integer merchCatgMinPresentationUnit,
                                     Integer merchCatgMaxPresentationUnit, Integer subCatgMinPresentationUnit, Integer subCatgMaxPresentationUnit,
                                     Integer flMinPresentationUnit, Integer flMaxPresentationUnit){

        this.PlanId = planId;
        this.strategyId = strategyId;
        this.lvl0Nbr = lvl0Nbr;
        this.lvl1Nbr = lvl1Nbr;
        this.lvl2Nbr = lvl2Nbr;
        this.lvl3Nbr = lvl3Nbr;
        this.lvl3Name = lvl3Name;
        this.lvl4Nbr = lvl4Nbr;
        this.lvl4Name = lvl4Name;
        this.finelineNbr = finelineNbr;
        this.finelineName = finelineName;
        this.altFinelineName = altFinelineName;
        this.fixtureRoleName = fixtureRoleName;
        this.merchCatgMinPresentationUnit = merchCatgMinPresentationUnit;
        this.merchCatgMaxPresentationUnit = merchCatgMaxPresentationUnit;
        this.subCatgMinPresentationUnit = subCatgMinPresentationUnit;
        this.subCatgMaxPresentationUnit = subCatgMaxPresentationUnit;
        this.flMinPresentationUnit = flMinPresentationUnit;
        this.flMaxPresentationUnit = flMaxPresentationUnit;
    }


}
