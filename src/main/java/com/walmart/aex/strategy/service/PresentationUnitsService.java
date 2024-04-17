package com.walmart.aex.strategy.service;

import com.walmart.aex.strategy.dto.FixtureAllocationStrategy;
import com.walmart.aex.strategy.dto.PlanStrategyRequest;
import com.walmart.aex.strategy.dto.PlanStrategyResponse;
import com.walmart.aex.strategy.dto.request.Lvl1;
import com.walmart.aex.strategy.dto.request.Lvl2;
import com.walmart.aex.strategy.dto.request.Lvl3;
import com.walmart.aex.strategy.dto.request.PlanStrategyDTO;
import com.walmart.aex.strategy.entity.*;
import com.walmart.aex.strategy.enums.StratGroupType;
import com.walmart.aex.strategy.producer.StrategyProducer;
import com.walmart.aex.strategy.repository.*;
import com.walmart.aex.strategy.util.CommonUtil;
import com.walmart.aex.strategy.util.KafkaUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PresentationUnitsService {

    private final StrategyGroupRepository strategyGroupRepository;
    private final PresentationUnitStrategyRepository presentationUnitStrategyRepository;
    private final PresentationUnitsStrategyMapper presentationUnitsStrategyMapper;
    private final PresentationUnitMercCatgService presentationUnitMercCatgService;
    private final FixtureAllocationStrategyRepository fixtureAllocationStrategyRepository;
    private final PlanStrategyRepository planStrategyRepository;
    private final PlanStrategyPresentationUnitsMapper planStrategyPresentationUnitsMapper;
    private final ExecutorService executorService;

    private final StrategyFinelineFixtureRepository strategyFinelineFixtureRepository;

    private final PresentationUnitsMinMaxMappingService presentationUnitsMinMaxMappingService;

    @Autowired
    private StrategyProducer strategyProducer;


    public PresentationUnitsService(StrategyGroupRepository strategyGroupRepository, PresentationUnitStrategyRepository presentationUnitStrategyRepository,
                                    PresentationUnitsStrategyMapper presentationUnitsStrategyMapper,
                                    PresentationUnitMercCatgService presentationUnitMercCatgService,
                                    FixtureAllocationStrategyRepository fixtureAllocationStrategyRepository,
                                    PlanStrategyRepository planStrategyRepository,
                                    PlanStrategyPresentationUnitsMapper planStrategyPresentationUnitsMapper,
                                    StrategyFinelineFixtureRepository strategyFinelineFixtureRepository,
                                    PresentationUnitsMinMaxMappingService presentationUnitsMinMaxMappingService,
                                    ExecutorService executorService) {
        this.strategyGroupRepository = strategyGroupRepository;
        this.presentationUnitStrategyRepository = presentationUnitStrategyRepository;
        this.presentationUnitsStrategyMapper = presentationUnitsStrategyMapper;
        this.presentationUnitMercCatgService = presentationUnitMercCatgService;
        this.fixtureAllocationStrategyRepository = fixtureAllocationStrategyRepository;
        this.planStrategyRepository = planStrategyRepository;
        this.planStrategyPresentationUnitsMapper = planStrategyPresentationUnitsMapper;
        this.strategyFinelineFixtureRepository = strategyFinelineFixtureRepository;
        this.presentationUnitsMinMaxMappingService = presentationUnitsMinMaxMappingService;
        this.executorService = executorService;
    }

    public PlanStrategyResponse fetchPUsCatSubCats(Long planId, PlanStrategyResponse response) {
        log.info("Get Min and Max rules for FixtureAllocation Fineline PlanStrategy Request: {}", planId);
        Long strategyId = strategyGroupRepository.getStrategyIdByStrategyGroupTypeAndPlanId(StratGroupType.PRESENTATION_UNITS.getStrategyGroupTypeCode(), planId);
        Optional.ofNullable(presentationUnitStrategyRepository.getPresentationUnitsStrategy(planId, strategyId, null, null, null)
        )
                .stream()
                .flatMap(Collection::stream)
                .forEach(fixtureAllocationStrategy -> presentationUnitsStrategyMapper.mapPresentationUnitStrategyLvl2(fixtureAllocationStrategy, response));
        return response;
    }

    public PlanStrategyResponse fetchPUsFlByCatgAndSubCatg(PlanStrategyRequest request, PlanStrategyResponse response) {
        log.info("Get FixtureAllocation for all finelines based on lvl3 & lvl4 for planId: {}", request.getPlanId());
        Integer lvl3List = CommonUtil.getLvl3Details(request);
        Integer lvl4List = CommonUtil.getLvl4Details(request);
        Long strategyId = strategyGroupRepository.getStrategyIdByStrategyGroupTypeAndPlanId(StratGroupType.PRESENTATION_UNITS.getStrategyGroupTypeCode(), request.getPlanId());
        Optional.ofNullable(presentationUnitStrategyRepository.getPresentationUnitsStrategy(request.getPlanId(), strategyId, lvl3List, lvl4List, null))
                .stream()
                .flatMap(Collection::stream)
                .forEach(fixtureAllocationStrategy -> presentationUnitsStrategyMapper.mapPresentationUnitStrategyLvl2(fixtureAllocationStrategy, response));
        return response;

    }

    @Transactional
    public void updatePresentationUnitStrategy(PlanStrategyRequest request, PlanStrategyId planStrategyId, Set<String> updatedFieldRequest) {
        log.info("Updating Presentation unit metrics for planId {} & strategyId: {}", planStrategyId.getPlanId(), planStrategyId.getStrategyId());
        List<StrategyMerchCatgFixture> strategyMerchCatgFixtures = new ArrayList<>();
        for (Lvl3 lvl3 : request.getLvl3List()) {
            strategyMerchCatgFixtures.addAll(presentationUnitMercCatgService.updatePresentationUnitMetrics(lvl3, planStrategyId, updatedFieldRequest));
        }
        fixtureAllocationStrategyRepository.saveAll(strategyMerchCatgFixtures);
    }

    public PlanStrategyResponse fetchUpdatePUChanges(PlanStrategyId planStrategyId, Integer lvl3List, Integer lvl4List, PlanStrategyResponse response,
                                                     boolean forKafkaMessage, Integer fineline) {
        log.info("fetch the updated metrics for Presentation Units for planId {} & strategyId: {}", planStrategyId.getPlanId(), planStrategyId.getStrategyId());
        if (forKafkaMessage) {
            Optional.ofNullable(presentationUnitStrategyRepository.getPresentationUnitsStrategy(planStrategyId.getPlanId(), planStrategyId.getStrategyId(), lvl3List, lvl4List, fineline))
                    .stream()
                    .flatMap(Collection::stream)
                    .forEach(fixtureAllocationStrategy -> presentationUnitsStrategyMapper.mapPresentationUnitStrategyLvl2(fixtureAllocationStrategy, response));
        } else {
            Optional.ofNullable(presentationUnitStrategyRepository.getPresentationUnitsStrategy(planStrategyId.getPlanId(), planStrategyId.getStrategyId(), lvl3List, lvl4List, null))
                    .stream()
                    .flatMap(Collection::stream)
                    .forEach(fixtureAllocationStrategy -> presentationUnitsStrategyMapper.mapPresentationUnitStrategyLvl2(fixtureAllocationStrategy, response));
        }
        return response;
    }

    @Transactional
    public void updatePresentationUnitsFromRfa(PlanStrategyDTO request, PlanStrategyId planStrategyId) {
        log.info("Updating Presentation Units for planId:{} after RFA execution", request.getPlanId());
        PlanStrategy planStrategy = planStrategyRepository.findById(planStrategyId).orElse(new PlanStrategy());
        if (planStrategy.getPlanStrategyId() == null) {
            planStrategy.setPlanStrategyId(planStrategyId);
        }
        for (Lvl1 lvl1 : request.getLvl1List()) {
            log.info("Set Lvl1 for presentation units");
            for (Lvl2 lvl2 : lvl1.getLvl2List()) {
                log.info("Set Lvl2 for presentation units");
                planStrategyPresentationUnitsMapper.updatePUBasedOnRFA(planStrategy, request.getLvl0Nbr(), lvl1, lvl2);
            }
        }
        log.info("Saving the Plan Strategy : {}", planStrategy);
        planStrategyRepository.save(planStrategy);
        planStrategyRepository.flush();
    }

    public void generateKafkaMessagesForTheRequestedFinelines(List<Integer> finelineNbr, PlanStrategyId planStrategyId) {
        if (!CollectionUtils.isEmpty(finelineNbr)) {
            List<StrategyFinelineFixture> strategyFinelineFixtures = strategyFinelineFixtureRepository.findByPlan_idAndFineline_nbr(planStrategyId.getPlanId(), finelineNbr);
            finelineNbr.forEach(fl -> CompletableFuture.runAsync(() -> getFinelineFixturesForFl(fl, planStrategyId, strategyFinelineFixtures), executorService)
            );
        }
    }

    private void getFinelineFixturesForFl(Integer fl, PlanStrategyId planStrategyId, List<StrategyFinelineFixture> strategyFinelineFixtures) {
        List<StrategyFinelineFixture> strategyFinelineFixtureList = Optional.ofNullable(strategyFinelineFixtures)
                .stream()
                .flatMap(Collection::stream)
                .filter(strategyFinelineFixtures1 -> strategyFinelineFixtures1.getStrategyFinelineFixtureId().getFinelineNbr().equals(fl) &&
                        strategyFinelineFixtures1.getStrategyFinelineFixtureId().getStrategySubCatgFixtureId().getStrategyMerchCatgFixtureId().getStrategyMerchCatgId().getPlanStrategyId().equals(planStrategyId))
                .collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(strategyFinelineFixtureList)) {
            sendKafkaMessageForEachFineline(fl, strategyFinelineFixtureList, planStrategyId);
        }

    }

    void sendKafkaMessageForEachFineline(Integer fl, List<StrategyFinelineFixture> strategyFinelineFixtures, PlanStrategyId planStrategyId) {
        PlanStrategyResponse response = new PlanStrategyResponse();
        Integer lvl3Nbr = strategyFinelineFixtures
                .stream()
                .map(strategyFinelineFixture -> strategyFinelineFixture.getStrategyFinelineFixtureId().getStrategySubCatgFixtureId().getStrategyMerchCatgFixtureId().getStrategyMerchCatgId().getLvl3Nbr())
                .findFirst()
                .orElse(null);
        Integer lvl4Nbr = strategyFinelineFixtures
                .stream()
                .map(strategyFinelineFixture -> strategyFinelineFixture.getStrategyFinelineFixtureId().getStrategySubCatgFixtureId().getLvl4Nbr())
                .findFirst()
                .orElse(null);
        fetchUpdatePUChanges(planStrategyId, lvl3Nbr, lvl4Nbr, response, true, fl);
        KafkaUtil.generateKafkaMessage(response, strategyFinelineFixtures, strategyProducer, fl);
    }


    @Transactional
    public List<StrategyFinelineFixture> updatePresentationMinMax(FixtureAllocationStrategy allocationStrategy) {
        Long strategyId = strategyGroupRepository.getStrategyIdBySeasonCd(StratGroupType.PRESENTATION_UNITS.getStrategyGroupTypeCode(), null, null);
        allocationStrategy.setStrategyId(strategyId);
        log.info("Fetching the Presentation unit min max for PlanId: {}", allocationStrategy.getPlanId());
        List<StrategyFinelineFixture> finelineFixtures = strategyFinelineFixtureRepository.findFineLines_ByPlan_Id_And_Strategy_IdAnd_Cat_IdAndSub_Cat_IdAndFineline_nbr(
                allocationStrategy.getPlanId(), strategyId, allocationStrategy.getLvl0Nbr(), allocationStrategy.getLvl1Nbr(),
                allocationStrategy.getLvl2Nbr(), allocationStrategy.getLvl3Nbr(), allocationStrategy.getLvl4Nbr(), allocationStrategy.getFinelineNbr());

        if (!CollectionUtils.isEmpty(finelineFixtures)) {
            if (allocationStrategy.getProdDimensionId() != null) {
                log.info("Fetching the Presentation unit min max matrix for Category: {} and Sub Category: {}", allocationStrategy.getLvl3Nbr(), allocationStrategy.getLvl4Nbr());
                List<StrategyPUMinMax> puMinMax = Optional.ofNullable(presentationUnitsMinMaxMappingService.getPresentationUnitsMinMax())
                        .stream()
                        .flatMap(Collection::stream)
                        .filter(strategyPUMinMax -> strategyPUMinMax.getStrategyPUMinMaxId().getLvl0Nbr().equals(allocationStrategy.getLvl0Nbr()) &&
                                strategyPUMinMax.getStrategyPUMinMaxId().getLvl1Nbr().equals(allocationStrategy.getLvl1Nbr()) &&
                                strategyPUMinMax.getStrategyPUMinMaxId().getLvl2Nbr().equals(allocationStrategy.getLvl2Nbr()) &&
                                strategyPUMinMax.getStrategyPUMinMaxId().getLvl3Nbr().equals(allocationStrategy.getLvl3Nbr()) &&
                                strategyPUMinMax.getStrategyPUMinMaxId().getLvl4Nbr().equals(allocationStrategy.getLvl4Nbr())
                        )
                        .collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(puMinMax)) {
                    finelineFixtures.forEach(finelineFixture -> setPresentationUnitMinMax(finelineFixture, puMinMax, allocationStrategy));
                }
            } else {
                finelineFixtures.forEach(finelineFixture -> {
                    finelineFixture.setMinPresentationUnits(null);
                    finelineFixture.setMaxPresentationUnits(null);
                });
            }
            log.info("Saving the Presentation unit values");
            strategyFinelineFixtureRepository.saveAll(finelineFixtures);
        }
        return finelineFixtures;
    }

    private void setPresentationUnitMinMax(StrategyFinelineFixture flFixture, List<StrategyPUMinMax> puMinMaxList, FixtureAllocationStrategy allocationStrategy) {
        StrategyPUMinMax puMinMax = puMinMaxList.stream().filter(minMax -> minMax.getStrategyPUMinMaxId().getAhsVId().
                equals(allocationStrategy.getProdDimensionId()) && minMax.getStrategyPUMinMaxId().getFixtureTypeId().equals(flFixture.getStrategyFinelineFixtureId().getStrategySubCatgFixtureId().getStrategyMerchCatgFixtureId().getFixtureTypeId())).
                findFirst().orElse(null);
        if (puMinMax != null) {
            flFixture.setMaxPresentationUnits(puMinMax.getMaxQty());
            flFixture.setMinPresentationUnits(puMinMax.getMinQty());
        } else {
            flFixture.setMaxPresentationUnits(null);
            flFixture.setMinPresentationUnits(null);
        }
    }

}
