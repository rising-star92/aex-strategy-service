package com.walmart.aex.strategy.dto;

import org.springframework.beans.factory.annotation.Value;

public interface WeatherClusterStrategyDTO {

    @Value("#{@weatherClusterStrategyDTOToWeatherClusterStrategyMapperUtil.buildWeatherClusterStrategy(target.planId, target.strategyId," +
            "target.lvl0Nbr, target.lvl1Nbr, target.lvl2Nbr, target.lvl3Nbr, target.lvl3Name, target.lvl4Nbr," +
            "target.lvl4Name, target.finelineNbr, target.finelineDesc, target.outFitting, target.altFinelineName, target.analyticsClusterId, " +
            "target.analyticsClusterDesc, target.isEligible, target.isEligibleFlag, target.inStoreDate, target.markDownDate, " +
            "target.sellingWeeks, target.excludeOffshore, target.lySales, target.lyUnits, target.onHandQty, " +
            "target.salesToStockRatio, target.forecastedSales, target.forecastedUnits, target.ranking, " +
            "target.algoClusterRanking, target.storeCount, target.brands)}")
    WeatherClusterStrategy getWeatherClusterStrategy();
}
