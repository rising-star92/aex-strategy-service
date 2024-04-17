package com.walmart.aex.strategy.service;

import com.walmart.aex.strategy.dto.request.Style;
import com.walmart.aex.strategy.entity.PlanStrategyId;
import com.walmart.aex.strategy.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Slf4j
@Service
public class PlanStrategyDeleteStyleService {

    private final StrategyStyleClusRepository strategyStyleClusRepository;

    private final EligStyleClusProgRepository eligStyleClusProgRepository;
    private final FixtureAllocationStyleStrategyRepository fixtureAllocationStyleStrategyRepository;
    private final StrategyStyleRepository strategyStyleRepository;
    private final PlanStrategyDeleteCcService planStrategyDeleteCcService;
    private final StrategyCcRepository strategyCcRepository;

    public PlanStrategyDeleteStyleService(StrategyStyleClusRepository strategyStyleClusRepository,
                                          EligStyleClusProgRepository eligStyleClusProgRepository, FixtureAllocationStyleStrategyRepository fixtureAllocationStyleStrategyRepository,
                                          StrategyStyleRepository strategyStyleRepository,
                                          StrategyCcRepository strategyCcRepository,
                                          PlanStrategyDeleteCcService planStrategyDeleteCcService){
        this.strategyStyleClusRepository = strategyStyleClusRepository;
        this.eligStyleClusProgRepository = eligStyleClusProgRepository;
        this.fixtureAllocationStyleStrategyRepository = fixtureAllocationStyleStrategyRepository;
        this.strategyStyleRepository = strategyStyleRepository;
        this.planStrategyDeleteCcService = planStrategyDeleteCcService;
        this.strategyCcRepository = strategyCcRepository;

    }

    void deleteStrategiesAtStyleOrCC(List<Style> styles,
                                     PlanStrategyId weatherPlanStrategyId, Long planId,
                                     Integer lvl3Nbr, Integer lvl4Nbr, Integer finelineNbr) {
        for (Style style : styles) {
            if (style.getStyleNbr() != null && CollectionUtils.isEmpty(style.getCustomerChoices())) {
                log.info("Deleting Style weather cluster strategy info for styleNbr: {}, and planStrategyId: {}", style.getStyleNbr(), weatherPlanStrategyId);
                strategyStyleClusRepository.deleteStrategyStyleClusByStrategyStyleClusId_StrategyFlClusEligRankingId_PlanClusterStrategyId_planStrategyIdAndStrategyStyleClusId_StrategyFlClusEligRankingId_lvl3NbrAndStrategyStyleClusId_StrategyFlClusEligRankingId_lvl4NbrAndStrategyStyleClusId_StrategyFlClusEligRankingId_finelineNbrAndStrategyStyleClusId_styleNbr(
                        weatherPlanStrategyId, lvl3Nbr, lvl4Nbr, finelineNbr, style.getStyleNbr());
                strategyStyleClusRepository.flush();

                log.info("Deleting Program Style weather cluster strategy info for styleNbr: {}, and planStrategyId: {}", style.getStyleNbr(), weatherPlanStrategyId);
                eligStyleClusProgRepository.deleteEligStyleClusProgByEligStyleClusProgId_StrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_PlanClusterStrategyId_planStrategyIdAndEligStyleClusProgId_StrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_lvl3NbrAndEligStyleClusProgId_StrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_lvl4NbrAndEligStyleClusProgId_StrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_finelineNbrAndEligStyleClusProgId_styleNbr(
                        weatherPlanStrategyId, lvl3Nbr, lvl4Nbr, finelineNbr, style.getStyleNbr());
                eligStyleClusProgRepository.flush();
                log.info("Deleting Style fixture & Presentation Units strategy info for styleNbr: {}, and planId: {}", style.getStyleNbr(), planId);
                fixtureAllocationStyleStrategyRepository.deleteStrategyStyleFixtureByStrategyStyleFixtureId_StrategyFinelineFixtureId_StrategySubCatgFixtureId_StrategyMerchCatgFixtureId_StrategyMerchCatgId_PlanStrategyId_planIdAndStrategyStyleFixtureId_StrategyFinelineFixtureId_StrategySubCatgFixtureId_StrategyMerchCatgFixtureId_StrategyMerchCatgId_lvl3NbrAndStrategyStyleFixtureId_StrategyFinelineFixtureId_StrategySubCatgFixtureId_lvl4NbrAndStrategyStyleFixtureId_StrategyFinelineFixtureId_finelineNbrAndStrategyStyleFixtureId_styleNbr(
                        planId, lvl3Nbr, lvl4Nbr, finelineNbr, style.getStyleNbr());
                fixtureAllocationStyleStrategyRepository.flush();
                //TODO for Size & Volume grouping strategy delete
                log.info("Deleting Style in strat_style strategy info for styleNbr: {}, and planStrategyId: {}", style.getStyleNbr(), weatherPlanStrategyId);
                strategyStyleRepository.deleteStrategyStyleByStrategyStyleId_StrategyFinelineId_StrategySubCatgId_StrategyMerchCatgId_planStrategyIdAndStrategyStyleId_StrategyFinelineId_StrategySubCatgId_StrategyMerchCatgId_lvl3NbrAndStrategyStyleId_StrategyFinelineId_StrategySubCatgId_lvl4NbrAndStrategyStyleId_StrategyFinelineId_finelineNbrAndStrategyStyleId_styleNbr(
                        weatherPlanStrategyId, lvl3Nbr, lvl4Nbr, finelineNbr, style.getStyleNbr());
                strategyStyleRepository.flush();
            } else if (!CollectionUtils.isEmpty(style.getCustomerChoices())) {
                planStrategyDeleteCcService.deleteStrategiesAtCc(style.getCustomerChoices(), weatherPlanStrategyId, planId,
                        lvl3Nbr, lvl4Nbr, finelineNbr, style.getStyleNbr());
            }
        }

    }
}