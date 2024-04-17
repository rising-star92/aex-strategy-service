package com.walmart.aex.strategy.service;

import com.walmart.aex.strategy.dto.request.Lvl3;
import com.walmart.aex.strategy.dto.request.PresentationUnit;
import com.walmart.aex.strategy.dto.request.UpdatedFields;
import com.walmart.aex.strategy.entity.PlanStrategyId;
import com.walmart.aex.strategy.entity.StrategyMerchCatgFixture;
import com.walmart.aex.strategy.entity.StrategySubCatgFixture;
import com.walmart.aex.strategy.exception.CustomException;
import com.walmart.aex.strategy.repository.FixtureAllocationStrategyRepository;
import com.walmart.aex.strategy.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Service
@Slf4j
public class PresentationUnitMercCatgService {

    private final PresentationUnitSubCatgService presentationUnitSubCatgService;
    private final FixtureAllocationStrategyRepository fixtureAllocationStrategyRepository;

    public PresentationUnitMercCatgService(PresentationUnitSubCatgService presentationUnitSubCatgService,
                                           FixtureAllocationStrategyRepository fixtureAllocationStrategyRepository) {
        this.presentationUnitSubCatgService = presentationUnitSubCatgService;
        this.fixtureAllocationStrategyRepository = fixtureAllocationStrategyRepository;
    }

    public List<StrategyMerchCatgFixture> updatePresentationUnitMetrics(Lvl3 lvl3, PlanStrategyId planStrategyId, Set<String> updatedFieldRequest) {
        log.info("Calling the StrategyMerchCatgFixture repository planId {} & strategyId: {} & lvl3Nbr: {}",
                planStrategyId.getPlanId(), planStrategyId.getStrategyId(), lvl3.getLvl3Nbr());
        List<StrategyMerchCatgFixture> strategyMerchCatgPresentationUnit = fixtureAllocationStrategyRepository.
                findStrategyMerchCatgFixtureByStrategyMerchCatgFixtureId_StrategyMerchCatgId_PlanStrategyIdAndStrategyMerchCatgFixtureId_StrategyMerchCatgId_lvl3Nbr(
                        planStrategyId, lvl3.getLvl3Nbr())
                .orElseThrow(() -> new CustomException(String.format("Presentation unit strategy doesn't exists for the PlanId :%s, StrategyId: %s  & lvl3Nbr : %s provided",
                        planStrategyId.getPlanId(), planStrategyId.getStrategyId(), lvl3.getLvl3Nbr())));
        Optional.ofNullable(lvl3.getUpdatedFields())
                .map(UpdatedFields::getPresentationUnits)
                .map(CommonUtil::getUpdatedFieldsMap)
                .ifPresent(presentationUnitCatgUpdatedFields -> updateCatgPresentationUnitMetrics(lvl3, presentationUnitCatgUpdatedFields,
                        strategyMerchCatgPresentationUnit, updatedFieldRequest));
        //Check if we have subCatg level UpdateFields
        if (!CollectionUtils.isEmpty(lvl3.getLvl4List())) {
            presentationUnitSubCatgService.updateSubCatgPresentationUnitMetrics(lvl3.getLvl4List(), strategyMerchCatgPresentationUnit,
                    updatedFieldRequest);
        }
        return strategyMerchCatgPresentationUnit;
    }

    private void updateCatgPresentationUnitMetrics(Lvl3 lvl3, Map<String, String> presentationUnitCatgUpdatedFields,
                                                   List<StrategyMerchCatgFixture> strategyMerchCatgFixtures,
                                                   Set<String> updatedFieldRequest) {
        log.info("Updating the presentation unit for Catg:{} for field & value: {}", lvl3.getLvl3Nbr(), StringUtils.join(presentationUnitCatgUpdatedFields));
        String puFixtureType = Optional.ofNullable(CommonUtil.fetchReqPresentationUnitFixtureType(lvl3.getStrategy()))
                .map(PresentationUnit::getType).orElse(null);
        if (presentationUnitCatgUpdatedFields.containsKey("min")) {
            updatedFieldRequest.add("min");
            Integer min = Optional.ofNullable(CommonUtil.fetchReqPresentationUnitFixtureType(lvl3.getStrategy()))
                    .map(PresentationUnit::getMin).orElse(null);
            Optional.ofNullable(fetchMerchCatgPUs(strategyMerchCatgFixtures, puFixtureType))
                    .ifPresent(strategyMerchCatgFixture -> setMerchCatgPUMinCap(strategyMerchCatgFixture, min));
        }
        if (presentationUnitCatgUpdatedFields.containsKey("max")) {
            updatedFieldRequest.add("max");
            Integer max = Optional.ofNullable(CommonUtil.fetchReqPresentationUnitFixtureType(lvl3.getStrategy()))
                    .map(PresentationUnit::getMax).orElse(null);
            Optional.ofNullable(fetchMerchCatgPUs(strategyMerchCatgFixtures, puFixtureType))
                    .ifPresent(strategyMerchCatgFixture -> setMerchCatgPUMaxCap(strategyMerchCatgFixture, max));
        }

    }

    private void setMerchCatgPUMinCap(StrategyMerchCatgFixture strategyMerchCatgFixture, Integer min) {
        strategyMerchCatgFixture.setMinPresentationUnits(min);
        //Roll down to subCatgs
        Optional.ofNullable(strategyMerchCatgFixture.getStrategySubCatgFixtures())
                .stream()
                .flatMap(Collection::stream)
                .forEach(strategySubCatgFixture -> setSubCatgPUMinCap(strategySubCatgFixture, min));
    }

    private void setMerchCatgPUMaxCap(StrategyMerchCatgFixture strategyMerchCatgFixture, Integer max) {
        strategyMerchCatgFixture.setMaxPresentationUnits(max);
        //Roll down to subCatgs
        Optional.ofNullable(strategyMerchCatgFixture.getStrategySubCatgFixtures())
                .stream()
                .flatMap(Collection::stream)
                .forEach(strategySubCatgFixture -> setSubCatgPUMaxCap(strategySubCatgFixture, max));
    }

    private void setSubCatgPUMinCap(StrategySubCatgFixture strategySubCatgFixture, Integer min) {
        strategySubCatgFixture.setMinPresentationUnits(min);
        //Roll down to Finelines
        Optional.ofNullable(strategySubCatgFixture.getStrategyFinelineFixtures())
                .stream()
                .flatMap(Collection::stream)
                .forEach(strategyFinelineFixture -> strategyFinelineFixture.setMinPresentationUnits(min));
    }

    private void setSubCatgPUMaxCap(StrategySubCatgFixture strategySubCatgFixture, Integer max) {
        strategySubCatgFixture.setMaxPresentationUnits(max);
        //Roll down to Fl
        Optional.ofNullable(strategySubCatgFixture.getStrategyFinelineFixtures())
                .stream()
                .flatMap(Collection::stream)
                .forEach(strategyFinelineFixture -> strategyFinelineFixture.setMaxPresentationUnits(max));
    }


    private StrategyMerchCatgFixture fetchMerchCatgPUs(List<StrategyMerchCatgFixture> strategyMerchCatgFixtures, String puFixtureType) {
        return Optional.ofNullable(strategyMerchCatgFixtures)
                .stream()
                .flatMap(Collection::stream)
                .filter(strategyMerchCatgFixture -> strategyMerchCatgFixture.getFixtureType().getFixtureTypeName().equals(puFixtureType))
                .findFirst()
                .orElse(null);
    }
}
