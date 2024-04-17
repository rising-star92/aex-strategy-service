package com.walmart.aex.strategy.service;

import com.walmart.aex.strategy.dto.FixtureAllocationStrategy;
import com.walmart.aex.strategy.dto.PlanStrategyResponse;
import com.walmart.aex.strategy.dto.request.*;
import com.walmart.aex.strategy.util.CommonMethods;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FixtureAllocationStrategyMapper {

    @Autowired
    private CommonMethods commonMethods;

    public void mapFixtureStrategyLvl2(FixtureAllocationStrategy fixtureAllocationStrategy, PlanStrategyResponse strategyDTO) {
        if (strategyDTO.getPlanId() == null) {
            strategyDTO.setPlanId(fixtureAllocationStrategy.getPlanId());
        }
        if (strategyDTO.getLvl0Nbr() == null)
            strategyDTO.setLvl0Nbr(fixtureAllocationStrategy.getLvl0Nbr());
        if (strategyDTO.getLvl1Nbr() == null)
            strategyDTO.setLvl1Nbr(fixtureAllocationStrategy.getLvl1Nbr());
        if (strategyDTO.getLvl2Nbr() == null)
            strategyDTO.setLvl2Nbr(fixtureAllocationStrategy.getLvl2Nbr());
        strategyDTO.setLvl3List(mapFixtureStrategyLvl3(fixtureAllocationStrategy, strategyDTO));
    }

    private List<Lvl3> mapFixtureStrategyLvl3(FixtureAllocationStrategy fixtureAllocationStrategy, PlanStrategyResponse strategyDTO) {
        List<Lvl3> lvl3List = Optional.ofNullable(strategyDTO.getLvl3List())
                .orElse(new ArrayList<>());
        lvl3List.stream()
                .filter(lvl3 -> fixtureAllocationStrategy.getLvl3Nbr().equals(lvl3.getLvl3Nbr()))
                .findFirst()
                .ifPresentOrElse(lvl3 -> setLvl3Strategy(fixtureAllocationStrategy, lvl3),
                        () -> setLvl3(fixtureAllocationStrategy, lvl3List));
        return lvl3List;
    }

    private void setLvl3(FixtureAllocationStrategy fixtureAllocationStrategy, List<Lvl3> lvl3List) {
        Lvl3 lvl3 = new Lvl3();
        lvl3.setLvl3Nbr(fixtureAllocationStrategy.getLvl3Nbr());
        lvl3.setLvl3Name(fixtureAllocationStrategy.getLvl3Name());
        lvl3List.add(lvl3);
        setLvl3Strategy(fixtureAllocationStrategy, lvl3);
    }

    private void setLvl3Strategy(FixtureAllocationStrategy fixtureAllocationStrategy, Lvl3 lvl3) {
        Strategy strategy = Optional.ofNullable(lvl3.getStrategy())
                .orElse(new Strategy());
        List<Fixture> fixtureAllocation = Optional.ofNullable(lvl3.getStrategy())
                .map(Strategy::getFixture)
                .orElse(new ArrayList<>());
        lvl3.setStrategy(mapLvl3Strategy(fixtureAllocationStrategy, fixtureAllocation, strategy));
        lvl3.setLvl4List(mapFixtureStrategyLvl4(fixtureAllocationStrategy, lvl3));
    }

    private Strategy mapLvl3Strategy(FixtureAllocationStrategy fixtureAllocationStrategy, List<Fixture> fixtureAllocation, Strategy strategy) {
        List<String> fixtureTypeList = Optional.ofNullable(fixtureAllocation)
                .stream()
                .flatMap(Collection::stream)
                .map(Fixture::getType)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(fixtureAllocation) || !fixtureTypeList.contains(fixtureAllocationStrategy.getFixtureRoleName())) {
            fixtureAllocation.add(mapLvl3StrategyFixture(fixtureAllocationStrategy, new Fixture()));
        }
        strategy.setFixture(fixtureAllocation);
        return strategy;
    }

    private Fixture mapLvl3StrategyFixture(FixtureAllocationStrategy fixtureAllocationStrategy, Fixture fixture) {
        fixture.setType(fixtureAllocationStrategy.getFixtureRoleName());
        fixture.setDefaultMinCap(fixtureAllocationStrategy.getCategoryMin());
        fixture.setDefaultMaxCap(fixtureAllocationStrategy.getCategoryMax());
        fixture.setMaxCcs(fixtureAllocationStrategy.getCategoryMaxCC());
        return fixture;
    }

    private List<Lvl4> mapFixtureStrategyLvl4(FixtureAllocationStrategy fixtureAllocationStrategy, Lvl3 lvl3) {
        List<Lvl4> lvl4List = Optional.ofNullable(lvl3.getLvl4List())
                .orElse(new ArrayList<>());
        lvl4List.stream()
                .filter(lvl4 -> fixtureAllocationStrategy.getLvl4Nbr().equals(lvl4.getLvl4Nbr()))
                .findFirst()
                .ifPresentOrElse(lvl4 -> setLvl4Strategy(fixtureAllocationStrategy, lvl4),
                        () -> setLvl4(fixtureAllocationStrategy, lvl4List));
        return lvl4List;
    }

    private void setLvl4Strategy(FixtureAllocationStrategy fixtureAllocationStrategy, Lvl4 lvl4) {
        Strategy strategy = Optional.ofNullable(lvl4.getStrategy())
                .orElse(new Strategy());
        List<Fixture> fixtureAllocation = Optional.ofNullable(lvl4.getStrategy())
                .map(Strategy::getFixture)
                .orElse(new ArrayList<>());
        lvl4.setStrategy(mapLvl4Strategy(fixtureAllocationStrategy, fixtureAllocation, strategy));
        //Check if we have a finelineNbr, this defines we are just responding for catg & subCatg for the planId
        if (fixtureAllocationStrategy.getFinelineNbr() != null) {
            lvl4.setFinelines(mapFixtureStrategyFineline(fixtureAllocationStrategy, lvl4));
        }
    }

    private Strategy mapLvl4Strategy(FixtureAllocationStrategy fixtureAllocationStrategy, List<Fixture> fixtureAllocation, Strategy strategy) {
        List<String> fixtureTypeList = Optional.ofNullable(fixtureAllocation)
                .stream()
                .flatMap(Collection::stream)
                .map(Fixture::getType)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(fixtureAllocation) || !fixtureTypeList.contains(fixtureAllocationStrategy.getFixtureRoleName())) {
            fixtureAllocation.add(mapLvl4StrategyFixture(fixtureAllocationStrategy, new Fixture()));
        }
        strategy.setFixture(fixtureAllocation);
        return strategy;
    }

    private Fixture mapLvl4StrategyFixture(FixtureAllocationStrategy fixtureAllocationStrategy, Fixture fixture) {
        fixture.setType(fixtureAllocationStrategy.getFixtureRoleName());
        fixture.setDefaultMinCap(fixtureAllocationStrategy.getSubCategoryMin());
        fixture.setDefaultMaxCap(fixtureAllocationStrategy.getSubcategoryMax());
        fixture.setMaxCcs(fixtureAllocationStrategy.getSubCategoryMaxCC());
        return fixture;
    }

    private void setLvl4(FixtureAllocationStrategy fixtureAllocationStrategy, List<Lvl4> lvl4List) {
        Lvl4 lvl4 = new Lvl4();
        lvl4.setLvl4Nbr(fixtureAllocationStrategy.getLvl4Nbr());
        lvl4.setLvl4Name(fixtureAllocationStrategy.getLvl4Name());
        lvl4List.add(lvl4);
        setLvl4Strategy(fixtureAllocationStrategy, lvl4);
    }

    private List<Fineline> mapFixtureStrategyFineline(FixtureAllocationStrategy fixtureAllocationStrategy, Lvl4 lvl4) {
        List<Fineline> finelines = Optional.ofNullable(lvl4.getFinelines())
                .orElse(new ArrayList<>());
        finelines.stream()
                .filter(fineline -> fixtureAllocationStrategy.getFinelineNbr().equals(fineline.getFinelineNbr()))
                .findFirst()
                .ifPresentOrElse(fineline -> setFinelineStrategy(fixtureAllocationStrategy, fineline),
                        () -> setFineline(fixtureAllocationStrategy, finelines));
        return finelines;
    }

    private void setFineline(FixtureAllocationStrategy fixtureAllocationStrategy, List<Fineline> finelines) {
        Fineline fineline = new Fineline();
        fineline.setFinelineNbr(fixtureAllocationStrategy.getFinelineNbr());
        fineline.setFinelineName(fixtureAllocationStrategy.getFinelineName());
        fineline.setAltFinelineName(fixtureAllocationStrategy.getAltFinelineName());
        finelines.add(fineline);
        setFinelineStrategy(fixtureAllocationStrategy, fineline);
    }

    private void setFinelineStrategy(FixtureAllocationStrategy fixtureAllocationStrategy, Fineline fineline) {
        fineline.setOutFitting(fixtureAllocationStrategy.getOutFitting());

        //Setting the brands
        List<Brands> brands = fixtureAllocationStrategy.getBrands()!=null? commonMethods.getBrandAttributes(fixtureAllocationStrategy.getBrands()):
                new ArrayList<>();
        fineline.setBrands(brands);

        Strategy strategy = Optional.ofNullable(fineline.getStrategy())
                .orElse(new Strategy());
        List<Fixture> fixtureAllocation = Optional.ofNullable(fineline.getStrategy())
                .map(Strategy::getFixture)
                .orElse(new ArrayList<>());
        fineline.setStrategy(mapFinelineStrategy(fixtureAllocationStrategy, fixtureAllocation, strategy));
        if (fixtureAllocationStrategy.getStyleNbr() != null) {
            fineline.setStyles(mapFixtureStrategyStyle(fixtureAllocationStrategy, fineline));
        }
    }

    private Strategy mapFinelineStrategy(FixtureAllocationStrategy fixtureAllocationStrategy, List<Fixture> fixtureAllocation, Strategy strategy) {
        List<String> fixtureTypeList = Optional.ofNullable(fixtureAllocation)
                .stream()
                .flatMap(Collection::stream)
                .map(Fixture::getType)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(fixtureAllocation) || !fixtureTypeList.contains(fixtureAllocationStrategy.getFixtureRoleName())) {
            fixtureAllocation.add(mapfinelineStrategyFixture(fixtureAllocationStrategy, new Fixture()));
        }
        strategy.setFixture(fixtureAllocation);
        return strategy;
    }

    private Fixture mapfinelineStrategyFixture(FixtureAllocationStrategy fixtureAllocationStrategy, Fixture fixture) {
        fixture.setType(fixtureAllocationStrategy.getFixtureRoleName());
        fixture.setBelowMin(fixtureAllocationStrategy.getAdjBelowMinFixturesPerFineline());
        fixture.setBelowMax(fixtureAllocationStrategy.getAdjBelowMaxFixturesPerFineline());
        fixture.setFgMin(fixtureAllocationStrategy.getAdjMinFixturesPerFineline());
        fixture.setFgMax(fixtureAllocationStrategy.getAdjMaxFixturesPerFineline());
        fixture.setFgStart(fixtureAllocationStrategy.getMinFixtureGroup());
        fixture.setFgEnd(fixtureAllocationStrategy.getMaxFixtureGroup());
        fixture.setMaxCcs(fixtureAllocationStrategy.getFinelineMaxCcs());
        return fixture;
    }

    private List<Style> mapFixtureStrategyStyle(FixtureAllocationStrategy fixtureAllocationStrategy, Fineline fineline) {
        List<Style> styles = Optional.ofNullable(fineline.getStyles())
                .orElse(new ArrayList<>());
        styles.stream()
                .filter(style -> fixtureAllocationStrategy.getStyleNbr().equals(style.getStyleNbr()))
                .findFirst()
                .ifPresentOrElse(style -> setStyleCcs(fixtureAllocationStrategy, style),
                        () -> setStyle(fixtureAllocationStrategy, styles));
        return styles;
    }

    private void setStyle(FixtureAllocationStrategy fixtureAllocationStrategy, List<Style> styles) {
        Style style = new Style();
        style.setStyleNbr(fixtureAllocationStrategy.getStyleNbr());
        styles.add(style);
        setStyleCcs(fixtureAllocationStrategy, style);
    }

    private void setStyleCcs(FixtureAllocationStrategy fixtureAllocationStrategy, Style style) {
        if (fixtureAllocationStrategy.getCcId() != null) {
            style.setCustomerChoices(mapFixtureStrategyCc(fixtureAllocationStrategy, style));
        }
    }

    private List<CustomerChoice> mapFixtureStrategyCc(FixtureAllocationStrategy fixtureAllocationStrategy, Style style) {
        List<CustomerChoice> customerChoices = Optional.ofNullable(style.getCustomerChoices())
                .orElse(new ArrayList<>());
        customerChoices.stream()
                .filter(customerChoice -> fixtureAllocationStrategy.getCcId().equals(customerChoice.getCcId()))
                .findFirst()
                .ifPresentOrElse(customerChoice -> setCcStrategy(fixtureAllocationStrategy, customerChoice),
                        () -> setCc(fixtureAllocationStrategy, customerChoices));
        return customerChoices;
    }

    private void setCc(FixtureAllocationStrategy fixtureAllocationStrategy, List<CustomerChoice> customerChoices) {
        CustomerChoice customerChoice = new CustomerChoice();
        customerChoice.setCcId(fixtureAllocationStrategy.getCcId());
        customerChoice.setAltCcDesc(fixtureAllocationStrategy.getAltCcDesc());
        customerChoice.setColorName(fixtureAllocationStrategy.getColorName());
        customerChoices.add(customerChoice);
        setCcStrategy(fixtureAllocationStrategy, customerChoice);
    }

    private void setCcStrategy(FixtureAllocationStrategy fixtureAllocationStrategy, CustomerChoice customerChoice) {
        Strategy strategy = Optional.ofNullable(customerChoice.getStrategy())
                .orElse(new Strategy());
        List<Fixture> fixtureAllocation = Optional.ofNullable(customerChoice.getStrategy())
                .map(Strategy::getFixture)
                .orElse(new ArrayList<>());
        customerChoice.setStrategy(mapCcStrategy(fixtureAllocationStrategy, fixtureAllocation, strategy));
    }

    private Strategy mapCcStrategy(FixtureAllocationStrategy fixtureAllocationStrategy, List<Fixture> fixtureAllocation, Strategy strategy) {
        List<String> fixtureTypeList = Optional.ofNullable(fixtureAllocation)
                .stream()
                .flatMap(Collection::stream)
                .map(Fixture::getType)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(fixtureAllocation) || !fixtureTypeList.contains(fixtureAllocationStrategy.getFixtureRoleName())) {
            fixtureAllocation.add(mapCcStrategyFixture(fixtureAllocationStrategy, new Fixture()));
        }
        strategy.setFixture(fixtureAllocation);
        return strategy;
    }

    private Fixture mapCcStrategyFixture(FixtureAllocationStrategy fixtureAllocationStrategy, Fixture fixture) {
        fixture.setType(fixtureAllocationStrategy.getFixtureRoleName());
        fixture.setAdjMaxCc(fixtureAllocationStrategy.getAdjMaxFixturesPerCc());
        return fixture;
    }


}
