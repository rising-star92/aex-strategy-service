package com.walmart.aex.strategy.service;

import com.walmart.aex.strategy.dto.request.CustomerChoice;
import com.walmart.aex.strategy.entity.PlanStrategyId;
import com.walmart.aex.strategy.repository.EligCcClusProgRepository;
import com.walmart.aex.strategy.repository.FixtureAllocationCcStrategyRepository;
import com.walmart.aex.strategy.repository.StrategyCcClusEligRankingRepository;
import com.walmart.aex.strategy.repository.StrategyCcRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class PlanStrategyDeleteCcService {

    private final FixtureAllocationCcStrategyRepository fixtureAllocationCcStrategyRepository;
    private final StrategyCcClusEligRankingRepository strategyCcClusEligRankingRepository;

    private final EligCcClusProgRepository eligCcClusProgRepository;
    private final StrategyCcRepository strategyCcRepository;

    public PlanStrategyDeleteCcService(FixtureAllocationCcStrategyRepository fixtureAllocationCcStrategyRepository,
                                       StrategyCcClusEligRankingRepository strategyCcClusEligRankingRepository,
                                       EligCcClusProgRepository eligCcClusProgRepository, StrategyCcRepository strategyCcRepository){
        this.fixtureAllocationCcStrategyRepository = fixtureAllocationCcStrategyRepository;
        this.strategyCcClusEligRankingRepository = strategyCcClusEligRankingRepository;
        this.eligCcClusProgRepository = eligCcClusProgRepository;
        this.strategyCcRepository = strategyCcRepository;
    }

    void deleteStrategiesAtCc(List<CustomerChoice> customerChoices,
                              PlanStrategyId weatherPlanStrategyId, Long planId, Integer lvl3Nbr,
                              Integer lvl4Nbr, Integer finelineNbr, String styleNbr) {
        for (CustomerChoice cc : customerChoices) {
            if (cc.getCcId() != null) {
                log.info("Deleting ccId weather cluster strategy info for ccId: {}, and planStrategyId: {}", cc.getCcId(), weatherPlanStrategyId);
                strategyCcClusEligRankingRepository.deleteStrategyCcClusEligRankingByStrategyCcClusEligRankingId_StrategyStyleClusId_StrategyFlClusEligRankingId_PlanClusterStrategyId_PlanStrategyId_planIdAndStrategyCcClusEligRankingId_StrategyStyleClusId_StrategyFlClusEligRankingId_lvl3NbrAndStrategyCcClusEligRankingId_StrategyStyleClusId_StrategyFlClusEligRankingId_lvl4NbrAndStrategyCcClusEligRankingId_StrategyStyleClusId_StrategyFlClusEligRankingId_finelineNbrAndStrategyCcClusEligRankingId_StrategyStyleClusId_styleNbrAndStrategyCcClusEligRankingId_ccId(
                        planId, lvl3Nbr, lvl4Nbr, finelineNbr, styleNbr, cc.getCcId());
                strategyCcClusEligRankingRepository.flush();

                log.info("Deleting program ccId weather cluster strategy info for ccId: {}, and planStrategyId: {}", cc.getCcId(), weatherPlanStrategyId);
                eligCcClusProgRepository.deleteEligCcClusProgByEligCcClusProgId_EligStyleClusProgId_StrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_PlanClusterStrategyId_PlanStrategyId_planIdAndEligCcClusProgId_EligStyleClusProgId_StrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_lvl3NbrAndEligCcClusProgId_EligStyleClusProgId_StrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_lvl4NbrAndEligCcClusProgId_EligStyleClusProgId_StrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_finelineNbrAndEligCcClusProgId_EligStyleClusProgId_styleNbrAndEligCcClusProgId_ccId(
                        planId, lvl3Nbr, lvl4Nbr, finelineNbr, styleNbr, cc.getCcId());
                eligCcClusProgRepository.flush();

                log.info("Deleting ccId fixture & presentation units strategy info for ccId: {}, and planId: {}", cc.getCcId(), planId);
                fixtureAllocationCcStrategyRepository.deleteStrategyCcFixtureByStrategyCcFixtureId_StrategyStyleFixtureId_StrategyFinelineFixtureId_StrategySubCatgFixtureId_StrategyMerchCatgFixtureId_StrategyMerchCatgId_PlanStrategyId_planIdAndStrategyCcFixtureId_StrategyStyleFixtureId_StrategyFinelineFixtureId_StrategySubCatgFixtureId_StrategyMerchCatgFixtureId_StrategyMerchCatgId_lvl3NbrAndStrategyCcFixtureId_StrategyStyleFixtureId_StrategyFinelineFixtureId_StrategySubCatgFixtureId_lvl4NbrAndStrategyCcFixtureId_StrategyStyleFixtureId_StrategyFinelineFixtureId_finelineNbrAndStrategyCcFixtureId_StrategyStyleFixtureId_styleNbrAndStrategyCcFixtureId_ccId(
                        planId, lvl3Nbr, lvl4Nbr, finelineNbr, styleNbr, cc.getCcId());
                fixtureAllocationCcStrategyRepository.flush();
                //TODO for Size & Volume grouping strategy delete
                log.info("Deleting ccId in strat_cc for ccId: {}, and planStrategyId: {}", cc.getCcId(), weatherPlanStrategyId);
                strategyCcRepository.deleteStrategyCcByStrategyCcId_StrategyStyleId_StrategyFinelineId_StrategySubCatgId_StrategyMerchCatgId_PlanStrategyId_planIdAndStrategyCcId_StrategyStyleId_StrategyFinelineId_StrategySubCatgId_StrategyMerchCatgId_lvl3NbrAndStrategyCcId_StrategyStyleId_StrategyFinelineId_StrategySubCatgId_lvl4NbrAndStrategyCcId_StrategyStyleId_StrategyFinelineId_finelineNbrAndStrategyCcId_StrategyStyleId_styleNbrAndStrategyCcId_ccId(
                        planId, lvl3Nbr, lvl4Nbr, finelineNbr, styleNbr, cc.getCcId());
                strategyCcRepository.flush();
            }
        }
    }

}