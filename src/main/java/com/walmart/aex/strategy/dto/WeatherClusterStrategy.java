package com.walmart.aex.strategy.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class WeatherClusterStrategy {
    private Long planId;
    private Long strategyId;
    private Long programId;
    private Integer lvl0Nbr;
    private Integer lvl1Nbr;
    private Integer lvl2Nbr;
    private Integer lvl3Nbr;
    private String lvl3Name;
    private Integer lvl4Nbr;
    private String lvl4Name;
    private Integer finelineNbr;
    private String finelineDesc;
    private String outFitting;
    private String altFinelineName;
    private String styleNbr;
    private String ccId;
    private String altCcDesc;
    private String colorName;
    private Integer analyticsClusterId;
    private String analyticsClusterDesc;
    private Integer isEligible;
    private Integer isEligibleFlag;
    private String inStoreDate;
    private String markDownDate;
    private Integer allocationWeeks;
    private String includeOffshore;
    private List<String> includeOffshoreList;
    private BigDecimal lySales;
    private Integer lyUnits;
    private Integer onHandQty;
    private BigDecimal salesToStockRatio;
    private BigDecimal forecastedSales;
    private Integer forecastedUnits;
    private Integer ranking;
    private Integer algoClusterRanking;
    private Integer storeCount;

    private String brands;

    public WeatherClusterStrategy(
            Long planId, Long strategyId, Integer lvl3Nbr, String lvl3Name,
            Integer lvl4Nbr, String lvl4Name, Integer finelineNbr, String finelineDesc, String outFitting, String altFinelineName, String styleNbr, String ccId, String altCcDesc, String colorName, Integer analyticsClusterId,
            String analyticsClusterDesc, Integer isEligible, Integer isEligibleFlag, String inStoreDate, String markDownDate,
            Integer allocationWeeks, String includeOffshore, Integer ranking) {
        this.planId = planId;
        this.strategyId = strategyId;
        this.lvl3Nbr = lvl3Nbr;
        this.lvl3Name = lvl3Name;
        this.lvl4Nbr = lvl4Nbr;
        this.lvl4Name = lvl4Name;
        this.finelineNbr = finelineNbr;
        this.finelineDesc = finelineDesc;
        this.outFitting = outFitting;
        this.altFinelineName = altFinelineName;
        this.styleNbr = styleNbr;
        this.ccId = ccId;
        this.altCcDesc = altCcDesc;
        this.colorName = colorName;
        this.analyticsClusterId = analyticsClusterId;
        this.analyticsClusterDesc = analyticsClusterDesc;
        this.isEligible = isEligible;
        this.isEligibleFlag = isEligibleFlag;
        this.inStoreDate = inStoreDate;
        this.markDownDate = markDownDate;
        this.allocationWeeks = allocationWeeks;
        this.includeOffshore = includeOffshore;
        this.ranking = ranking;
    }

    public WeatherClusterStrategy(
            Long planId, Long strategyId, Integer lvl0Nbr, Integer lvl1Nbr, Integer lvl2Nbr, Integer lvl3Nbr, String lvl3Name,
            Integer lvl4Nbr, String lvl4Name, Integer finelineNbr, String finelineDesc, String outFitting, String altFinelineName, Integer analyticsClusterId,
            String analyticsClusterDesc, Integer isEligible, Integer isEligibleFlag,String inStoreDate, String markDownDate,
            Integer allocationWeeks, String includeOffshore, BigDecimal lySales, Integer lyUnits, Integer onHandQty,
            BigDecimal salesToStockRatio, BigDecimal forecastedSales, Integer forecastedUnits, Integer ranking,
            Integer algoClusterRanking) {
        this.planId = planId;
        this.strategyId = strategyId;
        this.lvl0Nbr = lvl0Nbr;
        this.lvl1Nbr = lvl1Nbr;
        this.lvl2Nbr = lvl2Nbr;
        this.lvl3Nbr = lvl3Nbr;
        this.lvl3Name = lvl3Name;
        this.lvl4Nbr = lvl4Nbr;
        this.lvl4Name = lvl4Name;
        this.finelineNbr = finelineNbr;
        this.finelineDesc = finelineDesc;
        this.outFitting = outFitting;
        this.altFinelineName = altFinelineName;
        this.analyticsClusterId = analyticsClusterId;
        this.analyticsClusterDesc = analyticsClusterDesc;
        this.isEligible = isEligible;
        this.isEligibleFlag = isEligibleFlag;
        this.inStoreDate = inStoreDate;
        this.markDownDate = markDownDate;
        this.allocationWeeks = allocationWeeks;
        this.includeOffshore = includeOffshore;
        this.lySales = lySales;
        this.lyUnits = lyUnits;
        this.onHandQty = onHandQty;
        this.salesToStockRatio = salesToStockRatio;
        this.forecastedSales = forecastedSales;
        this.forecastedUnits = forecastedUnits;
        this.ranking = ranking;
        this.algoClusterRanking = algoClusterRanking;
    }

    public WeatherClusterStrategy(
            Long planId, Long strategyId, Integer lvl0Nbr, Integer lvl1Nbr, Integer lvl2Nbr, Integer lvl3Nbr, String lvl3Name,
            Integer lvl4Nbr, String lvl4Name, Integer finelineNbr, String finelineDesc, String outFitting, String altFinelineName, Integer analyticsClusterId,
            String analyticsClusterDesc, Integer isEligible, Integer isEligibleFlag, String inStoreDate, String markDownDate,
            Integer allocationWeeks, String includeOffshore, BigDecimal lySales, Integer lyUnits, Integer onHandQty,
            BigDecimal salesToStockRatio, BigDecimal forecastedSales, Integer forecastedUnits, Integer ranking,
            Integer algoClusterRanking, Integer storeCount, String brands) {
        this.planId = planId;
        this.strategyId = strategyId;
        this.lvl0Nbr = lvl0Nbr;
        this.lvl1Nbr = lvl1Nbr;
        this.lvl2Nbr = lvl2Nbr;
        this.lvl3Nbr = lvl3Nbr;
        this.lvl3Name = lvl3Name;
        this.lvl4Nbr = lvl4Nbr;
        this.lvl4Name = lvl4Name;
        this.finelineNbr = finelineNbr;
        this.finelineDesc = finelineDesc;
        this.outFitting = outFitting;
        this.altFinelineName = altFinelineName;
        this.analyticsClusterId = analyticsClusterId;
        this.analyticsClusterDesc = analyticsClusterDesc;
        this.isEligible = isEligible;
        this.isEligibleFlag = isEligibleFlag;
        this.inStoreDate = inStoreDate;
        this.markDownDate = markDownDate;
        this.allocationWeeks = allocationWeeks;
        this.includeOffshore = includeOffshore;
        this.lySales = lySales;
        this.lyUnits = lyUnits;
        this.onHandQty = onHandQty;
        this.salesToStockRatio = salesToStockRatio;
        this.forecastedSales = forecastedSales;
        this.forecastedUnits = forecastedUnits;
        this.ranking = ranking;
        this.algoClusterRanking = algoClusterRanking;
        this.storeCount = storeCount;
        this.brands = brands;
    }

    public WeatherClusterStrategy(
            Long planId, Long strategyId, Long programId, Integer lvl0Nbr, Integer lvl1Nbr, Integer lvl2Nbr, Integer lvl3Nbr, String lvl3Name,
            Integer lvl4Nbr, String lvl4Name, Integer finelineNbr, String finelineDesc, String outFitting, String altFinelineName, String styleNbr, String ccId, String altCcDesc, String colorName,
            Integer analyticsClusterId,
            String analyticsClusterDesc, Integer isEligible, Integer isEligibleFlag,String inStoreDate, String markDownDate,
            Integer allocationWeeks, String includeOffshore, Integer ranking) {
        this.planId = planId;
        this.strategyId = strategyId;
        this.programId = programId;
        this.lvl0Nbr = lvl0Nbr;
        this.lvl1Nbr = lvl1Nbr;
        this.lvl2Nbr = lvl2Nbr;
        this.lvl3Nbr = lvl3Nbr;
        this.lvl3Name = lvl3Name;
        this.lvl4Nbr = lvl4Nbr;
        this.lvl4Name = lvl4Name;
        this.finelineNbr = finelineNbr;
        this.finelineDesc = finelineDesc;
        this.outFitting=outFitting;
        this.altFinelineName = altFinelineName;
        this.styleNbr = styleNbr;
        this.ccId = ccId;
        this.altCcDesc = altCcDesc;
        this.colorName = colorName;
        this.analyticsClusterId = analyticsClusterId;
        this.analyticsClusterDesc = analyticsClusterDesc;
        this.isEligible = isEligible;
        this.isEligibleFlag = isEligibleFlag;
        this.inStoreDate = inStoreDate;
        this.markDownDate = markDownDate;
        this.allocationWeeks = allocationWeeks;
        this.includeOffshore = includeOffshore;
        this.ranking = ranking;
    }
}
