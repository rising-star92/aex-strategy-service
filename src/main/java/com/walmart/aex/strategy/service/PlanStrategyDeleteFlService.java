package com.walmart.aex.strategy.service;

import com.walmart.aex.strategy.dto.request.PlanStrategyDeleteMessage;
import com.walmart.aex.strategy.dto.request.StrongKey;
import com.walmart.aex.strategy.entity.PlanStrategyId;
import com.walmart.aex.strategy.enums.StratGroupType;
import com.walmart.aex.strategy.exception.CustomException;
import com.walmart.aex.strategy.repository.*;
import com.walmart.aex.strategy.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Optional;


@Service
@Slf4j
public class PlanStrategyDeleteFlService {

    private final StrategyFlClusEligRankingRepository strategyFlClusEligRankingRepository;
    private final StrategyGroupRepository strategyGroupRepository;
    private final FixtureAllocationFlStrategyRepository fixtureAllocationFlStrategyRepository;
    private final StrategyFinelineRepository strategyFinelineRepository;
    private final PlanStrategyDeleteStyleService planStrategyDeleteStyleService;

    public PlanStrategyDeleteFlService(StrategyFlClusEligRankingRepository strategyFlClusEligRankingRepository,
                                       StrategyGroupRepository strategyGroupRepository,
                                       FixtureAllocationFlStrategyRepository fixtureAllocationFlStrategyRepository,
                                       StrategyFinelineRepository strategyFinelineRepository,
                                       PlanStrategyDeleteStyleService planStrategyDeleteStyleService) {
        this.strategyFlClusEligRankingRepository = strategyFlClusEligRankingRepository;
        this.strategyGroupRepository = strategyGroupRepository;
        this.fixtureAllocationFlStrategyRepository = fixtureAllocationFlStrategyRepository;
        this.strategyFinelineRepository = strategyFinelineRepository;
        this.planStrategyDeleteStyleService = planStrategyDeleteStyleService;
    }

    @Transactional
    public void deletePlanStrategy(PlanStrategyDeleteMessage request) {
        //Get WeatherCluster strategy for the season
        StrongKey strongKey = Optional.ofNullable(request.getStrongKey()).orElse(null);
        if (strongKey != null) {
            Long weatherStrategyId = strategyGroupRepository.getStrategyIdByStrategyGroupTypeAndPlanId(StratGroupType.WEATHER_CLUSTER.getStrategyGroupTypeCode(), strongKey.getPlanId());
            PlanStrategyId weatherPlanStrategyId = new PlanStrategyId(strongKey.getPlanId(), weatherStrategyId);
            if (strongKey.getFineline().getFinelineNbr() != null && CollectionUtils.isEmpty(strongKey.getFineline().getStyles())) {
                deleteStratagiesAtFl(weatherPlanStrategyId, strongKey.getPlanId(), strongKey.getLvl3Nbr(), strongKey.getLvl4Nbr(),
                        strongKey.getFineline().getFinelineNbr());
            } else if (!CollectionUtils.isEmpty(strongKey.getFineline().getStyles())) {
                planStrategyDeleteStyleService.deleteStrategiesAtStyleOrCC(strongKey.getFineline().getStyles(), weatherPlanStrategyId, strongKey.getPlanId(),
                        strongKey.getLvl3Nbr(), strongKey.getLvl4Nbr(), strongKey.getFineline().getFinelineNbr());
            }
        } else {
            log.error("StrongKey not provided, please validate");
            throw new CustomException("StrongKey not provided, please validate");
        }
    }

    private void deleteStratagiesAtFl(PlanStrategyId weatherPlanStrategyId, Long planId, Integer lvl3Nbr,
                                      Integer lvl4Nbr, Integer finelineNbr) {
        log.info("Deleting fineline weather cluster strategy info for finelineNbr: {}, and planStrategyId: {}", finelineNbr, weatherPlanStrategyId);
        strategyFlClusEligRankingRepository.deleteStrategyFlClusEligRankingByStrategyFlClusEligRankingId_PlanClusterStrategyId_PlanStrategyIdAndStrategyFlClusEligRankingId_lvl3NbrAndStrategyFlClusEligRankingId_lvl4NbrAndStrategyFlClusEligRankingId_finelineNbr(
                weatherPlanStrategyId, lvl3Nbr, lvl4Nbr, finelineNbr);
        strategyFlClusEligRankingRepository.flush();
        log.info("Deleting fineline fixture & Presentation Units strategy info for finelineNbr: {}, and planId: {}", finelineNbr, planId);
        fixtureAllocationFlStrategyRepository.deleteStrategyFinelineFixtureByStrategyFinelineFixtureId_StrategySubCatgFixtureId_StrategyMerchCatgFixtureId_StrategyMerchCatgId_PlanStrategyId_planIdAndStrategyFinelineFixtureId_StrategySubCatgFixtureId_StrategyMerchCatgFixtureId_StrategyMerchCatgId_lvl3NbrAndStrategyFinelineFixtureId_StrategySubCatgFixtureId_lvl4NbrAndStrategyFinelineFixtureId_finelineNbr(
                planId, lvl3Nbr, lvl4Nbr, finelineNbr);
        fixtureAllocationFlStrategyRepository.flush();
        //TODO for Size & Volume grouping strategy delete
        log.info("Deleting fineline at strat_fl for finelineNbr: {}, and planStrategyId: {}", finelineNbr, weatherPlanStrategyId);
        strategyFinelineRepository.deleteStrategyFinelineByStrategyFinelineId_StrategySubCatgId_StrategyMerchCatgId_planStrategyIdAndStrategyFinelineId_StrategySubCatgId_StrategyMerchCatgId_lvl3NbrAndStrategyFinelineId_StrategySubCatgId_lvl4NbrAndStrategyFinelineId_finelineNbr(
                weatherPlanStrategyId, lvl3Nbr, lvl4Nbr, finelineNbr);
        strategyFinelineRepository.flush();
    }


}