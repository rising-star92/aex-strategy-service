package com.walmart.aex.strategy.service;

import com.walmart.aex.strategy.dto.request.*;
import com.walmart.aex.strategy.entity.*;
import com.walmart.aex.strategy.enums.ChannelType;
import com.walmart.aex.strategy.enums.FixtureTypeRollup;
import com.walmart.aex.strategy.repository.StrategyFinelineFixtureRepository;
import com.walmart.aex.strategy.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManager;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PlanStrategyFixtureMapper {

    @Autowired
    private StrategyFinelineFixtureRepository strategyFinelineFixtureRepository;

    @Autowired
    private EntityManager entityManager;

    /**
     * This method creates Merch Catg (lvl3) into Strategy Merch Catg Table based on CLP data events.
     *
     * @param lvl3s
     * @param planStrategyId
     * @param planStrategy
     * @param lvl1
     * @param lvl2
     * @return StrategyMerchCatg
     */
    public Set<StrategyMerchCatg> setStrategyMerchCatg(PlanStrategy planStrategy, List<Lvl3> lvl3s, PlanStrategyId planStrategyId,
                                                       PlanStrategyDTO request, Lvl1 lvl1, Lvl2 lvl2) {
        Set<StrategyMerchCatg> strategyMerchCatgs = Optional.ofNullable(planStrategy.getStrategyMerchCatgs())
                .orElse(new HashSet<>());
        for (Lvl3 lvl3 : lvl3s) {
            StrategyMerchCatgId strategyMerchCatgId = new StrategyMerchCatgId(planStrategyId,
                    request.getLvl0Nbr(), lvl1.getLvl1Nbr(), lvl2.getLvl2Nbr(), lvl3.getLvl3Nbr());
            log.info("Check if a strategyMerchCatg Id : {} already exists or not for Fixture Strategy", lvl3.getLvl3Nbr());
            StrategyMerchCatg strategyMerchCatg = Optional.of(strategyMerchCatgs)
                    .stream()
                    .flatMap(Collection::stream).filter(strategyMerchCatg1 -> strategyMerchCatg1.getStrategyMerchCatgId().equals(strategyMerchCatgId))
                    .findFirst()
                    .orElse(new StrategyMerchCatg());
            if (strategyMerchCatg.getStrategyMerchCatgId() == null) {
                strategyMerchCatg.setStrategyMerchCatgId(strategyMerchCatgId);
            }
            strategyMerchCatg.setChannelId(ChannelType.getChannelIdFromName(lvl3.getChannel()));
            List<Fixture> fixtures = getFixture(lvl3);
            //delete fixture that are not part of the updated request
            if (!CollectionUtils.isEmpty(fixtures)) {
                List<Integer> fixtureIds = Optional.of(fixtures)
                        .stream()
                        .flatMap(Collection::stream)
                        .map(fixture -> FixtureTypeRollup.getFixtureIdFromName(fixture.getType()))
                        .collect(Collectors.toList());
                Optional.ofNullable(strategyMerchCatg.getStrategyMerchCatgFixtures())
                        .stream()
                        .flatMap(Collection::stream)
                        .forEach(strategyMerchCatgFixture -> filterMercCatgFixtureForFLDelete(lvl3, strategyMerchCatgFixture, fixtureIds));
            } else {
                List<Integer> fixtureIds = new ArrayList<>();
                Optional.ofNullable(strategyMerchCatg.getStrategyMerchCatgFixtures())
                        .stream()
                        .flatMap(Collection::stream)
                        .forEach(strategyMerchCatgFixture -> filterMercCatgFixtureForFLDelete(lvl3, strategyMerchCatgFixture, fixtureIds));
            }
            //Add & Persist fixture at fineline/style/cc that part of the update
            if (!CollectionUtils.isEmpty(fixtures)) {
                for (Fixture fixture : fixtures) {
                    setMerchCatgFixture(strategyMerchCatg, lvl3, fixture);
                }
            }
            strategyMerchCatgs.add(strategyMerchCatg);
        }
        return strategyMerchCatgs;
    }

    private void filterMercCatgFixtureForFLDelete(Lvl3 lvl3, StrategyMerchCatgFixture strategyMerchCatgFixture, List<Integer> fixtures) {
        for (Lvl4 lvl4 : lvl3.getLvl4List()) {
            Optional.ofNullable(strategyMerchCatgFixture.getStrategySubCatgFixtures())
                    .stream()
                    .flatMap(Collection::stream)
                    .filter(strategySubCatgFixture -> strategySubCatgFixture.getStrategySubCatgFixtureId().getLvl4Nbr().equals(lvl4.getLvl4Nbr()))
                    .forEach(strategySubCatgFixture -> filterSubCatgFixtureForFLDelete(lvl4, strategySubCatgFixture, fixtures));
        }
    }

    private void filterSubCatgFixtureForFLDelete(Lvl4 lvl4, StrategySubCatgFixture strategySubCatgFixture, List<Integer> fixtures) {
        for (Fineline fineline : lvl4.getFinelines()) {
            if (!CollectionUtils.isEmpty(strategySubCatgFixture.getStrategyFinelineFixtures())) {
                log.info("Clear the Fixture fineline to set new order pref for lvl4Nbr :{},  fineline: {}, fixturePref:{}", lvl4.getLvl4Nbr(),
                        fineline.getFinelineNbr(), strategySubCatgFixture.getStrategySubCatgFixtureId().getStrategyMerchCatgFixtureId().getFixtureTypeId());
                Optional.ofNullable(strategySubCatgFixture.getStrategyFinelineFixtures())
                        .stream()
                        .flatMap(Collection::stream)
                        .filter(strategyFinelineFixture -> strategyFinelineFixture.getStrategyFinelineFixtureId().getFinelineNbr().equals(fineline.getFinelineNbr()))
                        .forEach(strategyFinelineFixture -> strategyFinelineFixture.getStrategyFinelineFixtureRanks().clear());
                strategySubCatgFixture.getStrategyFinelineFixtures()
                        .removeIf(strategyFinelineFixture -> deleteFlFixturePref(strategyFinelineFixture, fineline, fixtures));

            }
        }
    }

    private boolean deleteFlFixturePref(StrategyFinelineFixture strategyFinelineFixture, Fineline fineline, List<Integer> fixtures) {
        boolean filterCriteria = strategyFinelineFixture.getStrategyFinelineFixtureId().getFinelineNbr().equals(fineline.getFinelineNbr())
                && !fixtures.contains(strategyFinelineFixture.getStrategyFinelineFixtureId().getStrategySubCatgFixtureId().getStrategyMerchCatgFixtureId().getFixtureTypeId());
        if (filterCriteria)
            log.info("deleted fineline fixture pref for fineline:{} & finelineFixture: {} since doesn't exists in updated fixtures: {}",
                    fineline.getFinelineNbr(),
                    strategyFinelineFixture.getStrategyFinelineFixtureId().getStrategySubCatgFixtureId().getStrategyMerchCatgFixtureId().getFixtureTypeId(),
                    fixtures.toString());
        return filterCriteria;
    }


    private void setMerchCatgFixture(StrategyMerchCatg strategyMerchCatg, Lvl3 lvl3, Fixture fixture) {
        Set<StrategyMerchCatgFixture> strategyMerchCatgFixtures = Optional.ofNullable(strategyMerchCatg.getStrategyMerchCatgFixtures())
                .orElse(new HashSet<>());
        StrategyMerchCatgFixtureId strategyMerchCatgFixtureId = new StrategyMerchCatgFixtureId(strategyMerchCatg.getStrategyMerchCatgId(),
                FixtureTypeRollup.getFixtureIdFromName(fixture.getType()));
        log.info("Check if a strategyMerchCatgFixture Id : {} already exists or not for Fixture Strategy", lvl3.getLvl3Nbr());
        StrategyMerchCatgFixture strategyMerchCatgFixture = Optional.of(strategyMerchCatgFixtures)
                .stream()
                .flatMap(Collection::stream).filter(strategyMerchCatgFixture1 -> strategyMerchCatgFixture1.getStrategyMerchCatgFixtureId().equals(strategyMerchCatgFixtureId))
                .findFirst()
                .orElse(new StrategyMerchCatgFixture());
        if (strategyMerchCatgFixture.getStrategyMerchCatgFixtureId() == null) {
            strategyMerchCatgFixture.setStrategyMerchCatgFixtureId(strategyMerchCatgFixtureId);
        }
        if (!CollectionUtils.isEmpty(lvl3.getLvl4List())) {
            setSubCatgFixture(strategyMerchCatgFixture, lvl3.getLvl4List(), fixture);
        }
        strategyMerchCatgFixtures.add(strategyMerchCatgFixture);
        strategyMerchCatg.setStrategyMerchCatgFixtures(strategyMerchCatgFixtures);
    }

    private List<Fixture> getFixture(Lvl3 lvl3) {
        log.info("Getting the Fixtures Pref set in the request");
        return Optional.ofNullable(lvl3.getLvl4List())
                .stream()
                .flatMap(Collection::stream)
                .findFirst()
                .map(Lvl4::getFinelines)
                .stream()
                .flatMap(Collection::stream)
                .findFirst()
                .map(Fineline::getStrategy)
                .map(Strategy::getFixture)
                .orElse(null);
    }

    private void setSubCatgFixture(StrategyMerchCatgFixture strategyMerchCatgFixture, List<Lvl4> lvl4s, Fixture fixture) {
        for (Lvl4 lvl4 : lvl4s) {
            Set<StrategySubCatgFixture> strategySubCatgFixtures = Optional.ofNullable(strategyMerchCatgFixture.getStrategySubCatgFixtures())
                    .orElse(new HashSet<>());
            StrategySubCatgFixtureId strategySubCatgFixtureId = new StrategySubCatgFixtureId(strategyMerchCatgFixture.getStrategyMerchCatgFixtureId(),
                    lvl4.getLvl4Nbr());
            log.info("Check if a strategySubCatgFixture Id : {} already exists or not for Fixture Strategy", lvl4.getLvl4Nbr());
            StrategySubCatgFixture strategySubCatgFixture = Optional.of(strategySubCatgFixtures)
                    .stream()
                    .flatMap(Collection::stream).filter(strategySubCatgFixture1 -> strategySubCatgFixture1.getStrategySubCatgFixtureId().equals(strategySubCatgFixtureId))
                    .findFirst()
                    .orElse(new StrategySubCatgFixture());
            Integer maxCCsValue = Optional.ofNullable(strategySubCatgFixture.getMaxCcsPerFixture()).orElse(null);
            if (strategySubCatgFixture.getStrategySubCatgFixtureId() == null) {
                strategySubCatgFixture.setStrategySubCatgFixtureId(strategySubCatgFixtureId);
            }
            if (!CollectionUtils.isEmpty(lvl4.getFinelines())) {
                setFinelineFixture(strategySubCatgFixture, lvl4.getFinelines(), fixture, maxCCsValue);
            }
            strategySubCatgFixtures.add(strategySubCatgFixture);
            strategyMerchCatgFixture.setStrategySubCatgFixtures(strategySubCatgFixtures);
        }
    }

    private void setFinelineFixture(StrategySubCatgFixture strategySubCatgFixture, List<Fineline> finelines, Fixture fixture, Integer maxCCs) {
        for (Fineline fineline : finelines) {
            Set<StrategyFinelineFixture> strategyFinelineFixtures = Optional.ofNullable(strategySubCatgFixture.getStrategyFinelineFixtures())
                    .orElse(new HashSet<>());
            StrategyFinelineFixtureId strategyFinelineFixtureId = new StrategyFinelineFixtureId(strategySubCatgFixture.getStrategySubCatgFixtureId(),
                    fineline.getFinelineNbr());
            log.info("Check if a strategyFinelineFixture Id : {} already exists or not for Fixture Strategy", fineline.getFinelineNbr());
            StrategyFinelineFixture strategyFinelineFixture = Optional.of(strategyFinelineFixtures)
                    .stream()
                    .flatMap(Collection::stream).filter(strategyFinelineFixture1 -> strategyFinelineFixture1.getStrategyFinelineFixtureId().equals(strategyFinelineFixtureId))
                    .findFirst()
                    .orElse(new StrategyFinelineFixture());
            if (strategyFinelineFixture.getStrategyFinelineFixtureId() == null) {
                strategyFinelineFixture.setStrategyFinelineFixtureId(strategyFinelineFixtureId);
                strategyFinelineFixture.setMaxCcsPerFixture(maxCCs);
            }
            Integer maxCCAtFl = Optional.ofNullable(strategyFinelineFixture.getMaxCcsPerFixture()).orElse(null);
            setFinelineFixtureRank(strategyFinelineFixture, strategyFinelineFixtureId, fixture);
            if (!CollectionUtils.isEmpty(fineline.getStyles())) {
                setStyleFixture(strategyFinelineFixture, fineline.getStyles(), Optional.ofNullable(maxCCAtFl).orElse(maxCCs));
            }
            strategyFinelineFixtures.add(strategyFinelineFixture);
            strategySubCatgFixture.setStrategyFinelineFixtures(strategyFinelineFixtures);
        }
    }

    private void setStyleFixture(StrategyFinelineFixture strategyFinelineFixture, List<Style> styles, Integer maxCCs) {
        for (Style style : styles) {
            Set<StrategyStyleFixture> strategyStyleFixtures = Optional.ofNullable(strategyFinelineFixture.getStrategyStyleFixtures())
                    .orElse(new HashSet<>());
            StrategyStyleFixtureId strategyStyleFixtureId = new StrategyStyleFixtureId(strategyFinelineFixture.getStrategyFinelineFixtureId()
                    , style.getStyleNbr());
            log.info("Check if a strategyStyleFixture Id : {} already exists or not for Fixture Strategy", style.getStyleNbr());
            StrategyStyleFixture strategyStyleFixture = Optional.of(strategyStyleFixtures)
                    .stream()
                    .flatMap(Collection::stream).filter(strategyStyleFixture1 -> strategyStyleFixture1.getStrategyStyleFixtureId().equals(strategyStyleFixtureId))
                    .findFirst()
                    .orElse(new StrategyStyleFixture());
            if (strategyStyleFixture.getStrategyStyleFixtureId() == null) {
                strategyStyleFixture.setStrategyStyleFixtureId(strategyStyleFixtureId);
            }
            if (!CollectionUtils.isEmpty(style.getCustomerChoices())) {
                setCcFixture(strategyStyleFixture, style.getCustomerChoices(), maxCCs);
            }
            strategyStyleFixtures.add(strategyStyleFixture);
            strategyFinelineFixture.setStrategyStyleFixtures(strategyStyleFixtures);
        }
    }

    private void setCcFixture(StrategyStyleFixture strategyStyleFixture, List<CustomerChoice> customerChoices, Integer maxCCs) {
        for (CustomerChoice customerChoice : customerChoices) {
            Set<StrategyCcFixture> strategyCcFixtures = Optional.ofNullable(strategyStyleFixture.getStrategyCcFixtures())
                    .orElse(new HashSet<>());
            StrategyCcFixtureId strategyCcFixtureId = new StrategyCcFixtureId(strategyStyleFixture.getStrategyStyleFixtureId(), customerChoice.getCcId());
            log.info("Check if a strategyCcFixture Id : {} already exists or not for Fixture Strategy", customerChoice.getCcId());
            StrategyCcFixture strategyCcFixture = Optional.of(strategyCcFixtures)
                    .stream()
                    .flatMap(Collection::stream).filter(strategyCcFixture1 -> strategyCcFixture1.getStrategyCcFixtureId().equals(strategyCcFixtureId))
                    .findFirst()
                    .orElse(new StrategyCcFixture());
            if (strategyCcFixture.getStrategyCcFixtureId() == null) {
                strategyCcFixture.setStrategyCcFixtureId(strategyCcFixtureId);
                CommonUtil.rollDownMaxCcToCCs(strategyCcFixture, maxCCs);
            }
            strategyCcFixtures.add(strategyCcFixture);
            strategyStyleFixture.setStrategyCcFixtures(strategyCcFixtures);
        }
    }

    private void setFinelineFixtureRank(StrategyFinelineFixture strategyFinelineFixture, StrategyFinelineFixtureId strategyFinelineFixtureId,
                                        Fixture fixture) {
        Set<StrategyFinelineFixtureRank> strategyFinelineFixtureRanks = Optional.ofNullable(strategyFinelineFixture.getStrategyFinelineFixtureRanks())
                .orElse(new HashSet<>());
        StrategyFinelineFixtureRankId strategyFinelineFixtureRankId = new StrategyFinelineFixtureRankId(strategyFinelineFixtureId, fixture.getOrderPref());
        StrategyFinelineFixtureRank strategyFinelineFixtureRank = Optional.of(strategyFinelineFixtureRanks)
                .stream()
                .flatMap(Collection::stream)
                .filter(strategyFinelineFixtureRank1 -> strategyFinelineFixtureRank1.getStrategyFinelineFixtureRankId().equals(strategyFinelineFixtureRankId))
                .findFirst()
                .orElse(new StrategyFinelineFixtureRank());
        if (strategyFinelineFixtureRank.getStrategyFinelineFixtureRankId() == null) {
            strategyFinelineFixtureRank.setStrategyFinelineFixtureRankId(strategyFinelineFixtureRankId);
        }
        strategyFinelineFixtureRanks.add(strategyFinelineFixtureRank);
        strategyFinelineFixture.setStrategyFinelineFixtureRanks(strategyFinelineFixtureRanks);
    }
}
