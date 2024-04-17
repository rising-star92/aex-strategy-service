package com.walmart.aex.strategy.dto;

import org.springframework.beans.factory.annotation.Value;

public interface RunRFAFetchDTO {
    @Value("#{@runRFAFetchDTOToFixtureAllocationStrategyMapperUtil.buildFixtureAllocationStrategyForRunRFA(target.planId, target.strategyId, target.weatherClusterStrategyId," +
            "target.seasonCode, target.fiscalYear, target.lvl0Nbr, target.lvl1Nbr, target.lvl2Nbr, target.lvl3Nbr, target.lvl3Name, target.lvl4Nbr," +
            "target.lvl4Name, target.finelineNbr, target.finelineName, target.altFinelineName, target.outFitting, target.brands, target.traitChoice, target.allocRunTypeCode, target.allocRunTypeDesc," +
            "target.runStatusCode, target.runStatusDesc, target.rfaStatusCode, target.rfaStatusDesc, target.finelineRank)}")
     FixtureAllocationStrategy getRunRFAFetchData();
}
