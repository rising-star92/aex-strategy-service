package com.walmart.aex.strategy.dto;

import org.springframework.beans.factory.annotation.Value;

public interface WeatherClusterCcStrategyDTO {
    @Value("#{@weatherClusterStrategyDTOToWeatherClusterStrategyMapperUtil.buildWeatherClusterCcStrategy(target.planId,target.strategyId,target.programId," +
            "target.lvl0Nbr, target.lvl1Nbr, target.lvl2Nbr, target.lvl3Nbr, target.lvl3Name, target.lvl4Nbr," +
            "target.lvl4Name, target.finelineNbr, target.finelineDesc, target.outFitting, target.altFinelineName,target.styleNbr,target.customerChoice, target.altCcDesc, target.colorName," +
            "target.analyticsClusterId, " +
            "target.analyticsClusterDesc, target.isEligible, target.isEligibleFlag, target.inStoreDate, target.markDownDate, " +
            "target.sellingWeeks, target.excludeOffshore, target.ranking)}")
    WeatherClusterStrategy getWeatherClusterCcStrategy();
}
