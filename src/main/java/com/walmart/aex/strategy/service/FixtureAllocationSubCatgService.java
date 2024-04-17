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
public class FixtureAllocationSubCatgService {

    private final FixtureAllocationFlService fixtureAllocationFlService;

    public FixtureAllocationSubCatgService(FixtureAllocationFlService fixtureAllocationFlService) {
        this.fixtureAllocationFlService = fixtureAllocationFlService;
    }

    public void updateSubCatgFixtureMetrics(List<Lvl4> lvl4List, List<StrategyMerchCatgFixture> strategyMerchCatgFixtures) {
        for (Lvl4 lvl4 : lvl4List) {
            Optional.ofNullable(lvl4.getUpdatedFields())
                    .map(UpdatedFields::getFixture)
                    .map(CommonUtil::getUpdatedFieldsMap)
                    .ifPresent(fixtureSubCatgUpdatedFields -> updateSubCatgMetrics(lvl4, fixtureSubCatgUpdatedFields, strategyMerchCatgFixtures));
            if (!CollectionUtils.isEmpty(lvl4.getFinelines())) {
                fixtureAllocationFlService.updateFlFixtureMetrics(lvl4.getFinelines(), strategyMerchCatgFixtures, lvl4.getLvl4Nbr());
            }

        }
    }

    private void updateSubCatgMetrics(Lvl4 lvl4, Map<String, String> fixtureSubCatgUpdatedFields, List<StrategyMerchCatgFixture> strategyMerchCatgFixtures) {
        log.info("Updating the fixtureAllocation for Catg:{} for field & value: {}", lvl4.getLvl4Nbr(), StringUtils.join(fixtureSubCatgUpdatedFields));
        String fixtureType = Optional.ofNullable(CommonUtil.fetchRequestedFixtureType(lvl4.getStrategy()))
                .map(Fixture::getType).orElse(null);
        if (fixtureSubCatgUpdatedFields.containsKey("defaultMinCap")) {
            BigDecimal defaultMinCap = Optional.ofNullable(CommonUtil.fetchRequestedFixtureType(lvl4.getStrategy()))
                    .map(Fixture::getDefaultMinCap).orElse(null);
            Optional.ofNullable(fetchStrategySubCatgFixture(strategyMerchCatgFixtures, fixtureType, lvl4.getLvl4Nbr()))
                    .ifPresent(strategySubCatgFixture -> strategySubCatgFixture.setMinFixturesPerFineline(defaultMinCap));
        }
        if (fixtureSubCatgUpdatedFields.containsKey("defaultMaxCap")) {
            BigDecimal defaultMaxCap = Optional.ofNullable(CommonUtil.fetchRequestedFixtureType(lvl4.getStrategy()))
                    .map(Fixture::getDefaultMaxCap).orElse(null);
            Optional.ofNullable(fetchStrategySubCatgFixture(strategyMerchCatgFixtures, fixtureType, lvl4.getLvl4Nbr()))
                    .ifPresent(strategySubCatgFixture -> strategySubCatgFixture.setMaxFixturesPerFineline(defaultMaxCap));
        }
        if (fixtureSubCatgUpdatedFields.containsKey("maxCcs")) {
            Integer maxCcs = Optional.ofNullable(CommonUtil.fetchRequestedFixtureType(lvl4.getStrategy()))
                    .map(Fixture::getMaxCcs).orElse(null);
            Optional.ofNullable(fetchStrategySubCatgFixture(strategyMerchCatgFixtures, fixtureType, lvl4.getLvl4Nbr()))
                    .ifPresent(strategySubCatgFixture -> CommonUtil.rollDownMaxCcsToSubCatgFlAndCcs(strategySubCatgFixture, maxCcs));
        }
    }

    private StrategySubCatgFixture fetchStrategySubCatgFixture(List<StrategyMerchCatgFixture> strategyMerchCatgFixtures, String fixtureType, Integer lvl4Nbr) {
        return Optional.ofNullable(strategyMerchCatgFixtures)
                .stream()
                .flatMap(Collection::stream)
                .filter(strategyMerchCatgFixture -> strategyMerchCatgFixture.getFixtureType().getFixtureTypeName().equals(fixtureType))
                .findFirst()
                .map(StrategyMerchCatgFixture::getStrategySubCatgFixtures)
                .stream()
                .flatMap(Collection::stream)
                .filter(strategySubCatgFixture -> strategySubCatgFixture.getStrategySubCatgFixtureId().getLvl4Nbr().equals(lvl4Nbr))
                .findFirst().orElse(null);
    }

}
