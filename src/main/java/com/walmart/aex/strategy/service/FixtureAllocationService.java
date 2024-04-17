package com.walmart.aex.strategy.service;

import com.walmart.aex.strategy.dto.PlanStrategyRequest;
import com.walmart.aex.strategy.dto.PlanStrategyResponse;
import com.walmart.aex.strategy.dto.request.*;
import com.walmart.aex.strategy.entity.PlanStrategyId;
import com.walmart.aex.strategy.entity.StrategyMerchCatgFixture;
import com.walmart.aex.strategy.enums.StratGroupType;
import com.walmart.aex.strategy.repository.FixtureAllocationCcStrategyRepository;
import com.walmart.aex.strategy.repository.FixtureAllocationStrategyRepository;
import com.walmart.aex.strategy.repository.StrategyGroupRepository;
import com.walmart.aex.strategy.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Slf4j
public class FixtureAllocationService {
    private final FixtureAllocationStrategyRepository fixtureAllocationStrategyRepository;
    private final FixtureAllocationStrategyMapper fixtureAllocationStrategyMapper;
    private final StrategyGroupRepository strategyGroupRepository;
    private final FixtureAllocationCcStrategyRepository fixtureAllocationCcStrategyRepository;
    private final FixtureAllocationMercCatgService fixtureAllocationMercCatgService;

    public FixtureAllocationService(FixtureAllocationStrategyRepository fixtureAllocationStrategyRepository,
                                    FixtureAllocationStrategyMapper fixtureAllocationStrategyMapper,
                                    StrategyGroupRepository strategyGroupRepository,
                                    FixtureAllocationCcStrategyRepository fixtureAllocationCcStrategyRepository,
                                    FixtureAllocationMercCatgService fixtureAllocationMercCatgService) {
        this.fixtureAllocationStrategyRepository = fixtureAllocationStrategyRepository;
        this.fixtureAllocationStrategyMapper = fixtureAllocationStrategyMapper;
        this.strategyGroupRepository = strategyGroupRepository;
        this.fixtureAllocationCcStrategyRepository = fixtureAllocationCcStrategyRepository;
        this.fixtureAllocationMercCatgService = fixtureAllocationMercCatgService;
    }

    public PlanStrategyResponse fetchCatSubcatFinelineFixtureStrategy(Long planId, PlanStrategyResponse response) {
        log.info("Get Min and Max rules for FixtureAllocation Fineline PlanStrategy Request: {}", planId);
        Long strategyId = strategyGroupRepository.getStrategyIdByStrategyGroupTypeAndPlanId(StratGroupType.FIXTURE.getStrategyGroupTypeCode(), planId);
        Optional.ofNullable(fixtureAllocationStrategyRepository.getFineLineRuleMinMax(planId, strategyId))
                .stream()
                .flatMap(Collection::stream)
                .forEach(fixtureAllocationStrategy -> fixtureAllocationStrategyMapper.mapFixtureStrategyLvl2(fixtureAllocationStrategy, response));
        return response;
    }

    public PlanStrategyResponse fetchFinelineFixtureByCatgAndSubCatg(PlanStrategyRequest request, PlanStrategyResponse response) {
        log.info("Get FixtureAllocation for all finelines based on lvl3 & lvl4 for planId: {}", request.getPlanId());
        List<Integer> lvl3List = CommonUtil.getRequestedLvl3List(request);
        List<Integer> lvl4List = CommonUtil.getRequestedLvl4List(request);
        Long strategyId = strategyGroupRepository.getStrategyIdByStrategyGroupTypeAndPlanId(StratGroupType.FIXTURE.getStrategyGroupTypeCode(), request.getPlanId());
        Optional.ofNullable(fixtureAllocationStrategyRepository.getFineLineRuleMinMaxByCatgAndSubCatg(request.getPlanId(), strategyId, lvl3List, lvl4List))
                .stream()
                .flatMap(Collection::stream)
                .forEach(fixtureAllocationStrategy -> fixtureAllocationStrategyMapper.mapFixtureStrategyLvl2(fixtureAllocationStrategy, response));
        return response;

    }

    public PlanStrategyResponse fetchCcFixtureByCatgSubCatgAndFl(PlanStrategyRequest request, PlanStrategyResponse response) {
        log.info("Get  FixtureAllocation for all Ccs based on lvl3, lvl4 & Fl for planId: {}", request.getPlanId());
        List<Integer> lvl3List = CommonUtil.getRequestedLvl3List(request);
        List<Integer> lvl4List = CommonUtil.getRequestedLvl4List(request);
        List<Integer> finelines = CommonUtil.getRequestedFlList(request);
        Long strategyId = strategyGroupRepository.getStrategyIdByStrategyGroupTypeAndPlanId(StratGroupType.FIXTURE.getStrategyGroupTypeCode(), request.getPlanId());
        Optional.ofNullable(fixtureAllocationCcStrategyRepository.getFixtureCCBasedOnCatgSubCatgAndFl(request.getPlanId(), strategyId, lvl3List, lvl4List, finelines))
                .stream()
                .flatMap(Collection::stream)
                .forEach(fixtureAllocationStrategy -> fixtureAllocationStrategyMapper.mapFixtureStrategyLvl2(fixtureAllocationStrategy, response));
        return response;
    }

    @Transactional
    public void updateFixtureAllocationRules(PlanStrategyRequest request, PlanStrategyId planStrategyId) {
        log.info("Updating Fixture Allocation metrics for planId {} & strategyId: {}", planStrategyId.getPlanId(), planStrategyId.getStrategyId());
        List<StrategyMerchCatgFixture> strategyMerchCatgFixtures = new ArrayList<>();
        for (Lvl3 lvl3 : request.getLvl3List()) {
            strategyMerchCatgFixtures.addAll(fixtureAllocationMercCatgService.updateFixtureAllocationRulesMetrics(lvl3, planStrategyId));
        }
        fixtureAllocationStrategyRepository.saveAll(strategyMerchCatgFixtures);
    }


    public PlanStrategyResponse fetchUpdateFixtureChanges(PlanStrategyId planStrategyId, Integer lvl3Nbr, Integer lvl4Nbr,
                                                          Integer finelineNbr, String ccId, PlanStrategyResponse response) {
        log.info("fetch the updated metrics for Fixture allocation for planId {} & strategyId: {}", planStrategyId.getPlanId(), planStrategyId.getStrategyId());
        Optional.ofNullable(fixtureAllocationStrategyRepository.getFixtureAllocationRulesMetrics(planStrategyId.getPlanId(),
                planStrategyId.getStrategyId(), lvl3Nbr, lvl4Nbr, finelineNbr, ccId))
                .stream()
                .flatMap(Collection::stream)
                .forEach(fixtureAllocationStrategy -> fixtureAllocationStrategyMapper.mapFixtureStrategyLvl2(fixtureAllocationStrategy, response));
        return response;
    }
}
