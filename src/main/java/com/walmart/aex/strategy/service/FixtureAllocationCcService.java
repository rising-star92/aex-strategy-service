package com.walmart.aex.strategy.service;

import com.walmart.aex.strategy.dto.request.CustomerChoice;
import com.walmart.aex.strategy.dto.request.Fixture;
import com.walmart.aex.strategy.dto.request.UpdatedFields;
import com.walmart.aex.strategy.entity.*;
import com.walmart.aex.strategy.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class FixtureAllocationCcService {
    public void updateCCFixtureMetrics(CustomerChoice customerChoice, List<StrategyMerchCatgFixture> strategyMerchCatgFixtures,
                                       Integer lvl4Nbr, Integer finelineNbr, String styleNbr) {
        Optional.ofNullable(customerChoice.getUpdatedFields())
                .map(UpdatedFields::getFixture)
                .map(CommonUtil::getUpdatedFieldsMap)
                .ifPresent(fixtureCcUpdatedFields -> updateCCMetrics(customerChoice, strategyMerchCatgFixtures,
                        lvl4Nbr, fixtureCcUpdatedFields, finelineNbr, styleNbr));
    }

    private void updateCCMetrics(CustomerChoice customerChoice, List<StrategyMerchCatgFixture> strategyMerchCatgFixtures, Integer lvl4Nbr,
                                 Map<String, String> fixtureCcUpdatedFields, Integer finelineNbr, String styleNbr) {
        log.info("Updating the fixtureAllocation for ccId:{} for field & value: {}", customerChoice.getCcId(), StringUtils.join(fixtureCcUpdatedFields));
        String fixtureType = Optional.ofNullable(CommonUtil.fetchRequestedFixtureType(customerChoice.getStrategy()))
                .map(Fixture::getType).orElse(null);
        if (fixtureCcUpdatedFields.containsKey("adjMaxCc")) {
            BigDecimal adjMaxCc = Optional.ofNullable(CommonUtil.fetchRequestedFixtureType(customerChoice.getStrategy()))
                    .map(Fixture::getAdjMaxCc).orElse(null);
            Optional.ofNullable(fetchStrategyCcFixture(strategyMerchCatgFixtures, fixtureType, lvl4Nbr, finelineNbr, styleNbr, customerChoice.getCcId()))
                    .ifPresent(strategyCcFixture -> strategyCcFixture.setAdjMaxFixturesPerCc(adjMaxCc));
        }
    }

    private StrategyCcFixture fetchStrategyCcFixture(List<StrategyMerchCatgFixture> strategyMerchCatgFixtures, String fixtureType, Integer lvl4Nbr,
                                                     Integer finelineNbr, String styleNbr, String ccId) {
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
                .findFirst()
                .map(StrategyFinelineFixture::getStrategyStyleFixtures)
                .stream()
                .flatMap(Collection::stream)
                .filter(strategyStyleFixture -> strategyStyleFixture.getStrategyStyleFixtureId().getStyleNbr().equals(styleNbr))
                .findFirst()
                .map(StrategyStyleFixture::getStrategyCcFixtures)
                .stream()
                .flatMap(Collection::stream)
                .filter(strategyCcFixture -> strategyCcFixture.getStrategyCcFixtureId().getCcId().equals(ccId))
                .findFirst()
                .orElse(null);
    }
}
