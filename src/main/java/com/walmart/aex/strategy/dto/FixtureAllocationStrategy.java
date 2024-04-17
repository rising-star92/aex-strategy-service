package com.walmart.aex.strategy.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class FixtureAllocationStrategy {

    private Long PlanId;

    private Long strategyId;

    private Long weatherClusterStrategyId;

    private String seasonCode;

    private Integer fiscalYear;

    private Integer lvl0Nbr;

    private Integer lvl1Nbr;

    private Integer lvl2Nbr;

    private Integer lvl3Nbr;

    private String lvl3Name;

    private Integer lvl4Nbr;

    private String lvl4Name;

    private Integer finelineNbr;

    private String finelineName;
    private String outFitting;

    private String altFinelineName;

    private String fixtureRoleName;

    private BigDecimal categoryMin;

    private BigDecimal categoryMax;

    private BigDecimal subCategoryMin;

    private BigDecimal subcategoryMax;

    private Integer categoryMaxCC;

    private Integer subCategoryMaxCC;

    private BigDecimal adjBelowMinFixturesPerFineline;

    private BigDecimal adjBelowMaxFixturesPerFineline;

    private BigDecimal adjMinFixturesPerFineline;

    private BigDecimal adjMaxFixturesPerFineline;

    private Integer minFixtureGroup;

    private Integer maxFixtureGroup;

    private Integer minPresentationUnits;

    private Integer maxPresentationUnits;

    private Integer finelineMaxCcs;

    private BigDecimal adjMaxFixturesPerCc;

    private String styleNbr;

    private String ccId;

    private String altCcDesc;

    private String colorName;

    private String traitChoice;

    private Integer allocRunTypeCode;

    private String allocRunTypeDesc;

    private Integer runStatusCode;

    private String runStatusDesc;

    private Integer rfaStatusCode;

    private String rfaStatusDesc;

    private Integer finelineRank;

    private String brands;

    private Integer prodDimensionId;

    private boolean hasProdDimChanged;

    public FixtureAllocationStrategy(Long planId, Long strategyId, Integer lvl0Nbr, Integer lvl1Nbr, Integer lvl2Nbr, Integer lvl3Nbr, String lvl3Name, Integer lvl4Nbr,
                                     String lvl4Name, String fixtureRoleName, BigDecimal categoryMin, BigDecimal categoryMax,
                                     BigDecimal subCategoryMin, BigDecimal subcategoryMax, Integer categoryMaxCC, Integer subCategoryMaxCC){
        this.PlanId = planId;
        this.strategyId = strategyId;
        this.lvl0Nbr = lvl0Nbr;
        this.lvl1Nbr = lvl1Nbr;
        this.lvl2Nbr = lvl2Nbr;
        this.lvl3Nbr = lvl3Nbr;
        this.lvl3Name = lvl3Name;
        this.lvl4Nbr = lvl4Nbr;
        this.lvl4Name = lvl4Name;
        this.fixtureRoleName = fixtureRoleName;
        this.categoryMin= categoryMin;
        this.categoryMax = categoryMax;
        this.subCategoryMin = subCategoryMin;
        this.subcategoryMax = subcategoryMax;
        this.categoryMaxCC = categoryMaxCC;
        this.subCategoryMaxCC = subCategoryMaxCC;
    }

    public FixtureAllocationStrategy(Long planId, Long strategyId, Integer lvl0Nbr, Integer lvl1Nbr, Integer lvl2Nbr, Integer lvl3Nbr, String lvl3Name, Integer lvl4Nbr,
                                     String lvl4Name, Integer finelineNbr, String finelineName, String outFitting, String brands, String altFinelineName, String fixtureRoleName, BigDecimal adjBelowMinFixturesPerFineline,
                                     BigDecimal adjBelowMaxFixturesPerFineline, Integer minFixtureGroup, Integer maxFixtureGroup,
                                     BigDecimal adjMinFixturesPerFineline, BigDecimal adjMaxFixturesPerFineline,
                                     Integer finelineMaxCcs){
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
        this.outFitting = outFitting;
        this.brands = brands;
        this.altFinelineName = altFinelineName;
        this.fixtureRoleName = fixtureRoleName;
        this.adjBelowMinFixturesPerFineline= adjBelowMinFixturesPerFineline;
        this.adjBelowMaxFixturesPerFineline = adjBelowMaxFixturesPerFineline;
        this.minFixtureGroup = minFixtureGroup;
        this.maxFixtureGroup = maxFixtureGroup;
        this.adjMinFixturesPerFineline = adjMinFixturesPerFineline;
        this.adjMaxFixturesPerFineline = adjMaxFixturesPerFineline;
        this.finelineMaxCcs = finelineMaxCcs;
    }

    public FixtureAllocationStrategy(Long planId, Long strategyId, Integer lvl0Nbr, Integer lvl1Nbr, Integer lvl2Nbr, Integer lvl3Nbr, String lvl3Name, Integer lvl4Nbr,
                                     String lvl4Name, Integer finelineNbr, String finelineName, String outFitting, String altFinelineName, String styleNbr, String ccId, String altCcDesc, String colorName,
                                     String fixtureRoleName, BigDecimal adjMaxFixturesPerCc){
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
        this.outFitting = outFitting;
        this.altFinelineName = altFinelineName;
        this.styleNbr = styleNbr;
        this.ccId = ccId;
        this.altCcDesc = altCcDesc;
        this.colorName = colorName;
        this.fixtureRoleName = fixtureRoleName;
        this.adjMaxFixturesPerCc = adjMaxFixturesPerCc;
    }

    public FixtureAllocationStrategy(Long planId, Long strategyId, Integer lvl0Nbr, Integer lvl1Nbr, Integer lvl2Nbr, Integer lvl3Nbr, String lvl3Name, Integer lvl4Nbr,
                                     String lvl4Name, Integer finelineNbr, String finelineName, String altFinelineName, String styleNbr, String ccId, String altCcDesc, String colorName, String fixtureRoleName,
                                     BigDecimal categoryMin, BigDecimal categoryMax, BigDecimal subCategoryMin, BigDecimal subcategoryMax, Integer categoryMaxCC, Integer subCategoryMaxCC,
                                     BigDecimal adjBelowMinFixturesPerFineline,
                                     BigDecimal adjBelowMaxFixturesPerFineline, Integer minFixtureGroup, Integer maxFixtureGroup,
                                     BigDecimal adjMinFixturesPerFineline, BigDecimal adjMaxFixturesPerFineline,
                                     Integer finelineMaxCcs, BigDecimal adjMaxFixturesPerCc){
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
        this.styleNbr = styleNbr;
        this.ccId = ccId;
        this.altCcDesc = altCcDesc;
        this.colorName = colorName;
        this.fixtureRoleName = fixtureRoleName;
        this.categoryMin= categoryMin;
        this.categoryMax = categoryMax;
        this.subCategoryMin = subCategoryMin;
        this.subcategoryMax = subcategoryMax;
        this.categoryMaxCC = categoryMaxCC;
        this.subCategoryMaxCC = subCategoryMaxCC;
        this.adjBelowMinFixturesPerFineline= adjBelowMinFixturesPerFineline;
        this.adjBelowMaxFixturesPerFineline = adjBelowMaxFixturesPerFineline;
        this.minFixtureGroup = minFixtureGroup;
        this.maxFixtureGroup = maxFixtureGroup;
        this.adjMinFixturesPerFineline = adjMinFixturesPerFineline;
        this.adjMaxFixturesPerFineline = adjMaxFixturesPerFineline;
        this.finelineMaxCcs = finelineMaxCcs;
        this.adjMaxFixturesPerCc = adjMaxFixturesPerCc;
    }

    public FixtureAllocationStrategy(Long planId, Long strategyId, Long weatherClusterStrategyId, String seasonCode, Integer fiscalYear, Integer lvl0Nbr, Integer lvl1Nbr, Integer lvl2Nbr, Integer lvl3Nbr, String lvl3Name, Integer lvl4Nbr,
                                     String lvl4Name, Integer finelineNbr, String finelineName, String altFinelineName, String outFitting, String brands, String traitChoice, Integer allocRunTypeCode,
                                     String allocRunTypeDesc, Integer runStatusCode, String runStatusDesc, Integer rfaStatusCode, String rfaStatusDesc, Integer finelineRank){
        this.PlanId = planId;
        this.strategyId = strategyId;
        this.weatherClusterStrategyId = weatherClusterStrategyId;
        this.seasonCode = seasonCode;
        this.fiscalYear = fiscalYear;
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
        this.outFitting = outFitting;
        this.brands = brands;
        this.traitChoice = traitChoice;
        this.allocRunTypeCode = allocRunTypeCode;
        this.allocRunTypeDesc = allocRunTypeDesc;
        this.runStatusCode = runStatusCode;
        this.runStatusDesc = runStatusDesc;
        this.rfaStatusCode = rfaStatusCode;
        this.rfaStatusDesc = rfaStatusDesc;
        this.finelineRank = finelineRank;
    }

    public FixtureAllocationStrategy(){}

}
