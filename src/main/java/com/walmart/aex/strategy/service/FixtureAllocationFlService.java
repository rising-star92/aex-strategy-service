package com.walmart.aex.strategy.service;

import com.walmart.aex.strategy.dto.request.*;
import com.walmart.aex.strategy.entity.StrategyFinelineFixture;
import com.walmart.aex.strategy.entity.StrategyMerchCatgFixture;
import com.walmart.aex.strategy.entity.StrategySubCatgFixture;
import com.walmart.aex.strategy.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class FixtureAllocationFlService {
    private final FixtureAllocationCcService fixtureAllocationCcService;

    public FixtureAllocationFlService(FixtureAllocationCcService fixtureAllocationCcService) {
        this.fixtureAllocationCcService = fixtureAllocationCcService;
    }

    public void updateFlFixtureMetrics(List<Fineline> finelines, List<StrategyMerchCatgFixture> strategyMerchCatgFixtures,
                                       Integer lvl4Nbr) {
        for (Fineline fineline : finelines) {
            Optional.ofNullable(fineline.getUpdatedFields())
                    .map(UpdatedFields::getFixture)
                    .map(CommonUtil::getUpdatedFieldsMap)
                    .ifPresent(fixtureFlUpdatedFields -> updateFlMetrics(fineline, fixtureFlUpdatedFields, strategyMerchCatgFixtures, lvl4Nbr));
            if (!CollectionUtils.isEmpty(fineline.getStyles())) {
                for (Style style : fineline.getStyles()) {
                    for (CustomerChoice customerChoice : style.getCustomerChoices()) {
                        fixtureAllocationCcService.updateCCFixtureMetrics(customerChoice, strategyMerchCatgFixtures, lvl4Nbr, fineline.getFinelineNbr(), style.getStyleNbr());
                    }
                }
            }
        }
    }

    private void updateFlMetrics(Fineline fineline, Map<String, String> fixtureFlUpdatedFields, List<StrategyMerchCatgFixture> strategyMerchCatgFixtures,
                                 Integer lvl4Nbr) {
        log.info("Updating the fixtureAllocation for Catg:{} for field & value: {}", fineline.getFinelineNbr(), StringUtils.join(fixtureFlUpdatedFields));
        String fixtureType = Optional.ofNullable(CommonUtil.fetchRequestedFixtureType(fineline.getStrategy()))
                .map(Fixture::getType).orElse(null);
        if (fixtureFlUpdatedFields.containsKey("belowMin")) {
            BigDecimal belowMin = Optional.ofNullable(CommonUtil.fetchRequestedFixtureType(fineline.getStrategy()))
                    .map(Fixture::getBelowMin).orElse(null);
            Optional.ofNullable(fetchStrategyFlFixture(strategyMerchCatgFixtures, fixtureType, lvl4Nbr, fineline.getFinelineNbr()))
                    .ifPresent(strategyFinelineFixture -> strategyFinelineFixture.setAdjBelowMinFixturesPerFineline(belowMin));
        }
        if (fixtureFlUpdatedFields.containsKey("belowMax")) {
            BigDecimal belowMax = Optional.ofNullable(CommonUtil.fetchRequestedFixtureType(fineline.getStrategy()))
                    .map(Fixture::getBelowMax).orElse(null);
            Optional.ofNullable(fetchStrategyFlFixture(strategyMerchCatgFixtures, fixtureType, lvl4Nbr, fineline.getFinelineNbr()))
                    .ifPresent(strategyFinelineFixture -> strategyFinelineFixture.setAdjBelowMaxFixturesPerFineline(belowMax));
        }
        if (fixtureFlUpdatedFields.containsKey("fgMin")) {
            BigDecimal fgMin = Optional.ofNullable(CommonUtil.fetchRequestedFixtureType(fineline.getStrategy()))
                    .map(Fixture::getFgMin)
                    .orElse(null);
            Optional.ofNullable(fetchStrategyFlFixture(strategyMerchCatgFixtures, fixtureType, lvl4Nbr, fineline.getFinelineNbr()))
                    .ifPresent(strategyFinelineFixture -> strategyFinelineFixture.setAdjMinFixturesPerFineline(fgMin));
        }
        if (fixtureFlUpdatedFields.containsKey("fgMax")) {
            BigDecimal fgMax = Optional.ofNullable(CommonUtil.fetchRequestedFixtureType(fineline.getStrategy()))
                    .map(Fixture::getFgMax).orElse(null);
            Optional.ofNullable(fetchStrategyFlFixture(strategyMerchCatgFixtures, fixtureType, lvl4Nbr, fineline.getFinelineNbr()))
                    .ifPresent(strategyFinelineFixture -> strategyFinelineFixture.setAdjMaxFixturesPerFineline(fgMax));
        }
        if (fixtureFlUpdatedFields.containsKey("fgStart")) {
            Integer fgStart = Optional.ofNullable(CommonUtil.fetchRequestedFixtureType(fineline.getStrategy()))
                    .map(Fixture::getFgStart).orElse(null);
            Optional.ofNullable(fetchStrategyFlFixture(strategyMerchCatgFixtures, fixtureType, lvl4Nbr, fineline.getFinelineNbr()))
                    .ifPresent(strategyFinelineFixture -> strategyFinelineFixture.setMinFixtureGroup(fgStart));
        }
        if (fixtureFlUpdatedFields.containsKey("fgEnd")) {
            Integer fgEnd = Optional.ofNullable(CommonUtil.fetchRequestedFixtureType(fineline.getStrategy()))
                    .map(Fixture::getFgEnd).orElse(null);
            Optional.ofNullable(fetchStrategyFlFixture(strategyMerchCatgFixtures, fixtureType, lvl4Nbr, fineline.getFinelineNbr()))
                    .ifPresent(strategyFinelineFixture -> strategyFinelineFixture.setMaxFixtureGroup(fgEnd));
        }
        if (fixtureFlUpdatedFields.containsKey("maxCcs")) {
            Integer maxCcs = Optional.ofNullable(CommonUtil.fetchRequestedFixtureType(fineline.getStrategy()))
                    .map(Fixture::getMaxCcs).orElse(null);
            Optional.ofNullable(fetchStrategyFlFixture(strategyMerchCatgFixtures, fixtureType, lvl4Nbr, fineline.getFinelineNbr()))
                    .ifPresent(strategyFinelineFixture -> CommonUtil.rollDownMaxCcsToFlAndCcs(strategyFinelineFixture, maxCcs));
        }
    }

    private StrategyFinelineFixture fetchStrategyFlFixture(List<StrategyMerchCatgFixture> strategyMerchCatgFixtures, String fixtureType, Integer lvl4Nbr, Integer finelineNbr) {
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
