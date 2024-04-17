package com.walmart.aex.strategy.service;

import com.walmart.aex.strategy.dto.request.*;
import com.walmart.aex.strategy.entity.StrategyFinelineFixture;
import com.walmart.aex.strategy.entity.StrategyMerchCatgFixture;
import com.walmart.aex.strategy.entity.StrategySubCatgFixture;
import com.walmart.aex.strategy.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class PresentationUnitFlService {
    public void updateFlPresentationUnits(List<Fineline> finelines, List<StrategyMerchCatgFixture> strategyMerchCatgFixtures,
                                          Integer lvl4Nbr, Set<String> updatedFieldRequest) {
        for (Fineline fineline : finelines) {
            Optional.ofNullable(fineline.getUpdatedFields())
                    .map(UpdatedFields::getPresentationUnits)
                    .map(CommonUtil::getUpdatedFieldsMap)
                    .ifPresent(puFlUpdatedFields -> updatePUFlMetrics(fineline, puFlUpdatedFields, strategyMerchCatgFixtures, lvl4Nbr,
                            updatedFieldRequest));
        }
    }

    private void updatePUFlMetrics(Fineline fineline, Map<String, String> puFlUpdatedFields,
                                   List<StrategyMerchCatgFixture> strategyMerchCatgFixtures, Integer lvl4Nbr,
                                   Set<String> updatedFieldRequest) {
        log.info("Updating the presentation units for Catg:{} for field & value: {}", fineline.getFinelineNbr(), StringUtils.join(puFlUpdatedFields));
        String fixtureType = Optional.ofNullable(CommonUtil.fetchReqPresentationUnitFixtureType(fineline.getStrategy()))
                .map(PresentationUnit::getType).orElse(null);
        if (puFlUpdatedFields.containsKey("min")) {
            updatedFieldRequest.add("min");
            Integer min = Optional.ofNullable(CommonUtil.fetchReqPresentationUnitFixtureType(fineline.getStrategy()))
                    .map(PresentationUnit::getMin).orElse(null);
            Optional.ofNullable(fetchStrategyFlPresentationUnit(strategyMerchCatgFixtures, fixtureType, lvl4Nbr, fineline.getFinelineNbr()))
                    .ifPresent(strategyFinelineFixture -> strategyFinelineFixture.setMinPresentationUnits(min));
        }
        if (puFlUpdatedFields.containsKey("max")) {
            updatedFieldRequest.add("max");
            Integer max = Optional.ofNullable(CommonUtil.fetchReqPresentationUnitFixtureType(fineline.getStrategy()))
                    .map(PresentationUnit::getMax).orElse(null);
            Optional.ofNullable(fetchStrategyFlPresentationUnit(strategyMerchCatgFixtures, fixtureType, lvl4Nbr, fineline.getFinelineNbr()))
                    .ifPresent(strategyFinelineFixture -> strategyFinelineFixture.setMaxPresentationUnits(max));
        }
    }

    private StrategyFinelineFixture fetchStrategyFlPresentationUnit(List<StrategyMerchCatgFixture> strategyMerchCatgFixtures,
                                                                    String fixtureType, Integer lvl4Nbr, Integer finelineNbr) {
        return Optional.ofNullable(strategyMerchCatgFixtures)
                .stream()
                .flatMap(Collection::stream)
                .filter(strategyMerchCatgFixture -> strategyMerchCatgFixture.getFixtureType().getFixtureTypeName().equals(fixtureType))
                .findFirst()
                .map(StrategyMerchCatgFixture::getStrategySubCatgFixtures)
                .stream()
                .flatMap(Collection::stream)
                .filter(strategySubCatgFixture -> strategySubCatgFixture.getStrategySubCatgFixtureId().getLvl4Nbr().equals(lvl4Nbr))
                .findFirst()
                .map(StrategySubCatgFixture::getStrategyFinelineFixtures)
                .stream()
                .flatMap(Collection::stream)
                .filter(strategyFinelineFixture -> strategyFinelineFixture.getStrategyFinelineFixtureId().getFinelineNbr().equals(finelineNbr))
                .findFirst().orElse(null);
    }
}
