package com.walmart.aex.strategy.service;

import com.walmart.aex.strategy.dto.request.Fixture;
import com.walmart.aex.strategy.dto.request.Lvl3;
import com.walmart.aex.strategy.dto.request.UpdatedFields;
import com.walmart.aex.strategy.entity.*;
import com.walmart.aex.strategy.exception.CustomException;
import com.walmart.aex.strategy.repository.FixtureAllocationStrategyRepository;
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
public class FixtureAllocationMercCatgService {

    private final FixtureAllocationStrategyRepository fixtureAllocationStrategyRepository;
    private final FixtureAllocationSubCatgService fixtureAllocationSubCatgService;

    public FixtureAllocationMercCatgService(FixtureAllocationStrategyRepository fixtureAllocationStrategyRepository,
                                            FixtureAllocationSubCatgService fixtureAllocationSubCatgService) {
        this.fixtureAllocationStrategyRepository = fixtureAllocationStrategyRepository;
        this.fixtureAllocationSubCatgService = fixtureAllocationSubCatgService;
    }

    public List<StrategyMerchCatgFixture> updateFixtureAllocationRulesMetrics(Lvl3 lvl3, PlanStrategyId planStrategyId) {
        log.info("Calling the StrategyMerchCatgFixture repository planId {} & strategyId: {} & lvl3Nbr: {}",
                planStrategyId.getPlanId(), planStrategyId.getStrategyId(), lvl3.getLvl3Nbr());
        List<StrategyMerchCatgFixture> strategyMerchCatgFixtures = fixtureAllocationStrategyRepository.
                findStrategyMerchCatgFixtureByStrategyMerchCatgFixtureId_StrategyMerchCatgId_PlanStrategyIdAndStrategyMerchCatgFixtureId_StrategyMerchCatgId_lvl3Nbr(
                        planStrategyId, lvl3.getLvl3Nbr())
                .orElseThrow(() -> new CustomException(String.format("Fixture Allocation doesn't exists for the PlanId :%s, StrategyId: %s  & lvl3Nbr : %s provided",
                        planStrategyId.getPlanId(), planStrategyId.getStrategyId(), lvl3.getLvl3Nbr())));
        Optional.ofNullable(lvl3.getUpdatedFields())
                .map(UpdatedFields::getFixture)
                .map(CommonUtil::getUpdatedFieldsMap)
                .ifPresent(fixtureCatgUpdatedFields -> updateCatgFixtureMetrics(lvl3, fixtureCatgUpdatedFields, strategyMerchCatgFixtures));
        //Check if we have subCatg level UpdateFields
        if (!CollectionUtils.isEmpty(lvl3.getLvl4List())) {
            fixtureAllocationSubCatgService.updateSubCatgFixtureMetrics(lvl3.getLvl4List(), strategyMerchCatgFixtures);
        }
        return strategyMerchCatgFixtures;
    }

    private void updateCatgFixtureMetrics(Lvl3 lvl3, Map<String, String> fixtureCatgUpdatedFields, List<StrategyMerchCatgFixture> strategyMerchCatgFixtures) {
        log.info("Updating the fixtureAllocation for Catg:{} for field & value: {}", lvl3.getLvl3Nbr(), StringUtils.join(fixtureCatgUpdatedFields));
        String fixtureType = Optional.ofNullable(CommonUtil.fetchRequestedFixtureType(lvl3.getStrategy()))
                .map(Fixture::getType).orElse(null);
        if (fixtureCatgUpdatedFields.containsKey("defaultMinCap")) {
            BigDecimal defaultMinCap = Optional.ofNullable(CommonUtil.fetchRequestedFixtureType(lvl3.getStrategy()))
                    .map(Fixture::getDefaultMinCap).orElse(null);
            Optional.ofNullable(fetchMerchCatgFixture(strategyMerchCatgFixtures, fixtureType))
                    .ifPresent(strategyMerchCatgFixture -> setMerchCatgFixtureMinCap(strategyMerchCatgFixture, defaultMinCap));
        }
        if (fixtureCatgUpdatedFields.containsKey("defaultMaxCap")) {
            BigDecimal defaultMaxCap = Optional.ofNullable(CommonUtil.fetchRequestedFixtureType(lvl3.getStrategy()))
                    .map(Fixture::getDefaultMaxCap).orElse(null);
            Optional.ofNullable(fetchMerchCatgFixture(strategyMerchCatgFixtures, fixtureType))
                    .ifPresent(strategyMerchCatgFixture -> setMerchCatgFixtureMaxCap(strategyMerchCatgFixture, defaultMaxCap));
        }
        if (fixtureCatgUpdatedFields.containsKey("maxCcs")) {
            Integer maxCcs = Optional.ofNullable(CommonUtil.fetchRequestedFixtureType(lvl3.getStrategy()))
                    .map(Fixture::getMaxCcs).orElse(null);
            Optional.ofNullable(fetchMerchCatgFixture(strategyMerchCatgFixtures, fixtureType))
                    .ifPresent(strategyMerchCatgFixture -> setMaxCcMetric(strategyMerchCatgFixture, maxCcs));
        }
    }

    private void setMaxCcMetric(StrategyMerchCatgFixture strategyMerchCatgFixture, Integer maxCcs) {
        strategyMerchCatgFixture.setMaxCcsPerFixture(maxCcs);
        //RollDown to SubCatg, FL & CCs
        Optional.ofNullable(strategyMerchCatgFixture.getStrategySubCatgFixtures())
                .stream()
                .flatMap(Collection::stream)
                .forEach(strategySubCatgFixture -> CommonUtil.rollDownMaxCcsToSubCatgFlAndCcs(strategySubCatgFixture, maxCcs));
    }

    private void setMerchCatgFixtureMinCap(StrategyMerchCatgFixture strategyMerchCatgFixture, BigDecimal defaultMinCap) {
        strategyMerchCatgFixture.setMinFixturesPerFineline(defaultMinCap);
        //Roll down to subCatgs
        Optional.ofNullable(strategyMerchCatgFixture.getStrategySubCatgFixtures())
                .stream()
                .flatMap(Collection::stream)
                .forEach(strategySubCatgFixture -> strategySubCatgFixture.setMinFixturesPerFineline(defaultMinCap));
    }

    private void setMerchCatgFixtureMaxCap(StrategyMerchCatgFixture strategyMerchCatgFixture, BigDecimal defaultMaxCap) {
        strategyMerchCatgFixture.setMaxFixturesPerFineline(defaultMaxCap);
        //Roll down to subCatgs
        Optional.ofNullable(strategyMerchCatgFixture.getStrategySubCatgFixtures())
                .stream()
                .flatMap(Collection::stream)
                .forEach(strategySubCatgFixture -> strategySubCatgFixture.setMaxFixturesPerFineline(defaultMaxCap));
    }


    private StrategyMerchCatgFixture fetchMerchCatgFixture(List<StrategyMerchCatgFixture> strategyMerchCatgFixtures, String fixtureType) {
        return Optional.ofNullable(strategyMerchCatgFixtures)
                .stream()
                .flatMap(Collection::stream)
                .filter(strategyMerchCatgFixture -> strategyMerchCatgFixture.getFixtureType().getFixtureTypeName().equals(fixtureType))
                .findFirst()
                .orElse(null);
    }
}
