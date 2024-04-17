package com.walmart.aex.strategy.service;

import com.walmart.aex.strategy.dto.request.Lvl4;
import com.walmart.aex.strategy.dto.request.PresentationUnit;
import com.walmart.aex.strategy.dto.request.UpdatedFields;
import com.walmart.aex.strategy.entity.StrategyMerchCatgFixture;
import com.walmart.aex.strategy.entity.StrategySubCatgFixture;
import com.walmart.aex.strategy.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Service
@Slf4j
public class PresentationUnitSubCatgService {

    private final PresentationUnitFlService presentationUnitFlService;

    public PresentationUnitSubCatgService(PresentationUnitFlService presentationUnitFlService) {
        this.presentationUnitFlService = presentationUnitFlService;
    }

    public void updateSubCatgPresentationUnitMetrics(List<Lvl4> lvl4List, List<StrategyMerchCatgFixture> strategyMerchCatgFixtures,
                                                     Set<String> updatedFieldRequest) {
        for (Lvl4 lvl4 : lvl4List) {
            Optional.ofNullable(lvl4.getUpdatedFields())
                    .map(UpdatedFields::getPresentationUnits)
                    .map(CommonUtil::getUpdatedFieldsMap)
                    .ifPresent(puSubCatgUpdatedFields -> updatePUSubCatgMetrics(lvl4, puSubCatgUpdatedFields, strategyMerchCatgFixtures,
                            updatedFieldRequest));
            if (!CollectionUtils.isEmpty(lvl4.getFinelines())) {
                presentationUnitFlService.updateFlPresentationUnits(lvl4.getFinelines(), strategyMerchCatgFixtures, lvl4.getLvl4Nbr(),
                        updatedFieldRequest);
            }

        }
    }

    private void updatePUSubCatgMetrics(Lvl4 lvl4, Map<String, String> fixtureSubCatgUpdatedFields, List<StrategyMerchCatgFixture> strategyMerchCatgFixtures,
                                        Set<String> updatedFieldRequest) {
        log.info("Updating the fixtureAllocation for Catg:{} for field & value: {}", lvl4.getLvl4Nbr(), StringUtils.join(fixtureSubCatgUpdatedFields));
        String puFixtureType = Optional.ofNullable(CommonUtil.fetchReqPresentationUnitFixtureType(lvl4.getStrategy()))
                .map(PresentationUnit::getType).orElse(null);
        if (fixtureSubCatgUpdatedFields.containsKey("min")) {
            updatedFieldRequest.add("min");
            Integer min = Optional.ofNullable(CommonUtil.fetchReqPresentationUnitFixtureType(lvl4.getStrategy()))
                    .map(PresentationUnit::getMin).orElse(null);
            Optional.ofNullable(fetchStrategySubCatgPUs(strategyMerchCatgFixtures, puFixtureType, lvl4.getLvl4Nbr()))
                    .ifPresent(strategySubCatgFixture -> setSubCatgPUMinCap(strategySubCatgFixture, min));
        }
        if (fixtureSubCatgUpdatedFields.containsKey("max")) {
            updatedFieldRequest.add("max");
            Integer max = Optional.ofNullable(CommonUtil.fetchReqPresentationUnitFixtureType(lvl4.getStrategy()))
                    .map(PresentationUnit::getMax).orElse(null);
            Optional.ofNullable(fetchStrategySubCatgPUs(strategyMerchCatgFixtures, puFixtureType, lvl4.getLvl4Nbr()))
                    .ifPresent(strategySubCatgFixture -> setSubCatgPUMaxCap(strategySubCatgFixture, max));
        }
    }

    private void setSubCatgPUMinCap(StrategySubCatgFixture strategySubCatgFixture, Integer min) {
        strategySubCatgFixture.setMinPresentationUnits(min);
        //Roll down to finelines
        Optional.ofNullable(strategySubCatgFixture.getStrategyFinelineFixtures())
                .stream()
                .flatMap(Collection::stream)
                .forEach(strategyFinelineFixture -> strategyFinelineFixture.setMinPresentationUnits(min));
    }

    private void setSubCatgPUMaxCap(StrategySubCatgFixture strategySubCatgFixture, Integer max) {
        strategySubCatgFixture.setMaxPresentationUnits(max);
        //Roll down to subCatgs
        Optional.ofNullable(strategySubCatgFixture.getStrategyFinelineFixtures())
                .stream()
                .flatMap(Collection::stream)
                .forEach(strategyFinelineFixture -> strategyFinelineFixture.setMaxPresentationUnits(max));
    }

    private StrategySubCatgFixture fetchStrategySubCatgPUs(List<StrategyMerchCatgFixture> strategyMerchCatgFixtures,
                                                           String puFixtureType, Integer lvl4Nbr) {
        return Optional.ofNullable(strategyMerchCatgFixtures)
                .stream()
                .flatMap(Collection::stream)
                .filter(strategyMerchCatgFixture -> strategyMerchCatgFixture.getFixtureType().getFixtureTypeName().equals(puFixtureType))
                .findFirst()
                .map(StrategyMerchCatgFixture::getStrategySubCatgFixtures)
                .stream()
                .flatMap(Collection::stream)
                .filter(strategySubCatgFixture -> strategySubCatgFixture.getStrategySubCatgFixtureId().getLvl4Nbr().equals(lvl4Nbr))
                .findFirst().orElse(null);
    }
}
