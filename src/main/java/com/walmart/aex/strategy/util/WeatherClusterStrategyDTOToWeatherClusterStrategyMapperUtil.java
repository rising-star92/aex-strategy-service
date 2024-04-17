package com.walmart.aex.strategy.util;

import com.walmart.aex.strategy.dto.WeatherClusterStrategy;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class WeatherClusterStrategyDTOToWeatherClusterStrategyMapperUtil {

    public WeatherClusterStrategy buildWeatherClusterStrategy(Long planId, Long strategyId, Integer lvl0Nbr, Integer lvl1Nbr,
                                                              Integer lvl2Nbr, Integer lvl3Nbr, String lvl3Name,
                                                              Integer lvl4Nbr, String lvl4Name, Integer finelineNbr,
                                                              String finelineDesc, String outFitting, String altFinelineName, Integer analyticsClusterId,
                                                              String analyticsClusterDesc, Integer isEligible, Integer isEligibleFlag,
                                                              String inStoreDate, String markDownDate,
                                                              Integer sellingWeeks, String excludeOffshore,
                                                              BigDecimal lySales, Integer lyUnits, Integer onHandQty,
                                                              BigDecimal salesToStockRatio, BigDecimal forecastedSales,
                                                              Integer forecastedUnits, Integer ranking,
                                                              Integer algoClusterRanking, Integer storeCount, String brands) {
        return new WeatherClusterStrategy(planId, strategyId, lvl0Nbr, lvl1Nbr, lvl2Nbr,
                lvl3Nbr, lvl3Name, lvl4Nbr, lvl4Name, finelineNbr, finelineDesc,outFitting, altFinelineName, analyticsClusterId,
                analyticsClusterDesc, isEligible, isEligibleFlag,inStoreDate, markDownDate, sellingWeeks, excludeOffshore, lySales,
                lyUnits, onHandQty, salesToStockRatio, forecastedSales, forecastedUnits,
                ranking, algoClusterRanking, storeCount, brands);
    }

    public WeatherClusterStrategy buildWeatherClusterCcStrategy(Long planId, Long strategyId, Long programId, Integer lvl0Nbr, Integer lvl1Nbr, Integer lvl2Nbr, Integer lvl3Nbr, String lvl3Name,
                                                                Integer lvl4Nbr, String lvl4Name, Integer finelineNbr, String finelineDesc,String outFitting, String altFinelineName, String styleNbr,
                                                                String ccId, String altCcDesc, String colorName,
                                                                Integer analyticsClusterId,
                                                                String analyticsClusterDesc, Integer isEligible, Integer isEligibleFlag,String inStoreDate, String markDownDate,
                                                                Integer sellingWeeks, String excludeOffshore, Integer ranking) {
        return new WeatherClusterStrategy(planId, strategyId, programId, lvl0Nbr, lvl1Nbr, lvl2Nbr,
                lvl3Nbr, lvl3Name, lvl4Nbr, lvl4Name, finelineNbr, finelineDesc,outFitting, altFinelineName, styleNbr, ccId, altCcDesc, colorName, analyticsClusterId,
                analyticsClusterDesc, isEligible, isEligibleFlag,inStoreDate, markDownDate, sellingWeeks, excludeOffshore,
                ranking);
    }
}
