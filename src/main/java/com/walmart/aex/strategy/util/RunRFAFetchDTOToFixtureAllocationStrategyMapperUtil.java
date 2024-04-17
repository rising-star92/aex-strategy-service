package com.walmart.aex.strategy.util;

import com.walmart.aex.strategy.dto.FixtureAllocationStrategy;
import org.springframework.stereotype.Component;

@Component
public class RunRFAFetchDTOToFixtureAllocationStrategyMapperUtil {
    public FixtureAllocationStrategy buildFixtureAllocationStrategyForRunRFA(Long planId, Long strategyId, Long weatherClusterStrategyId, String seasonCode, Integer fiscalYear, Integer lvl0Nbr, Integer lvl1Nbr,
                                                                 Integer lvl2Nbr, Integer lvl3Nbr, String lvl3Name,
                                                                 Integer lvl4Nbr, String lvl4Name, Integer finelineNbr,
                                                                 String finelineName, String altFinelineName,String outFitting, String brands, String traitChoice, Integer allocRunTypeCode, String allocRunTypeDesc,
                                                                              Integer runStatusCode, String runStatusDesc, Integer rfaStatusCode,
                                                                 String rfaStatusDesc, Integer finelineRank) {
        return new FixtureAllocationStrategy(planId, strategyId, weatherClusterStrategyId, seasonCode, fiscalYear, lvl0Nbr, lvl1Nbr, lvl2Nbr,
                lvl3Nbr, lvl3Name, lvl4Nbr, lvl4Name, finelineNbr, finelineName, altFinelineName, outFitting, brands, traitChoice, allocRunTypeCode, allocRunTypeDesc,
                runStatusCode, runStatusDesc, rfaStatusCode, rfaStatusDesc, finelineRank
        );
    }
}
