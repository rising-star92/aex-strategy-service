package com.walmart.aex.strategy.service;

import com.walmart.aex.strategy.dto.PlanStrategyResponse;
import com.walmart.aex.strategy.dto.PresentationUnitsStrategy;
import com.walmart.aex.strategy.dto.request.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PresentationUnitsStrategyMapper {

    public void mapPresentationUnitStrategyLvl2(PresentationUnitsStrategy presentationUnitsStrategy, PlanStrategyResponse strategyDTO) {
        if (strategyDTO.getPlanId() == null) {
            strategyDTO.setPlanId(presentationUnitsStrategy.getPlanId());
        }
        if (strategyDTO.getLvl0Nbr() == null)
            strategyDTO.setLvl0Nbr(presentationUnitsStrategy.getLvl0Nbr());
        if (strategyDTO.getLvl1Nbr() == null)
            strategyDTO.setLvl1Nbr(presentationUnitsStrategy.getLvl1Nbr());
        if (strategyDTO.getLvl2Nbr() == null)
            strategyDTO.setLvl2Nbr(presentationUnitsStrategy.getLvl2Nbr());
        strategyDTO.setLvl3List(mapPresentationUnitStrategyLvl3(presentationUnitsStrategy, strategyDTO));
    }

    private List<Lvl3> mapPresentationUnitStrategyLvl3(PresentationUnitsStrategy presentationUnitsStrategy, PlanStrategyResponse strategyDTO) {
        List<Lvl3> lvl3List = Optional.ofNullable(strategyDTO.getLvl3List())
                .orElse(new ArrayList<>());
        lvl3List.stream()
                .filter(lvl3 -> presentationUnitsStrategy.getLvl3Nbr().equals(lvl3.getLvl3Nbr()))
                .findFirst()
                .ifPresentOrElse(lvl3 -> setLvl3Strategy(presentationUnitsStrategy, lvl3),
                        () -> setLvl3(presentationUnitsStrategy, lvl3List));
        return lvl3List;
    }

    private void setLvl3(PresentationUnitsStrategy presentationUnitsStrategy, List<Lvl3> lvl3List) {
        Lvl3 lvl3 = new Lvl3();
        lvl3.setLvl3Nbr(presentationUnitsStrategy.getLvl3Nbr());
        lvl3.setLvl3Name(presentationUnitsStrategy.getLvl3Name());
        lvl3List.add(lvl3);
        setLvl3Strategy(presentationUnitsStrategy, lvl3);
    }

    private void setLvl3Strategy(PresentationUnitsStrategy presentationUnitsStrategy, Lvl3 lvl3) {
        Strategy strategy = Optional.ofNullable(lvl3.getStrategy())
                .orElse(new Strategy());
        List<PresentationUnit> presentationUnits = Optional.ofNullable(lvl3.getStrategy())
                .map(Strategy::getPresentationUnits)
                .orElse(new ArrayList<>());
        lvl3.setStrategy(mapLvl3Strategy(presentationUnitsStrategy, presentationUnits, strategy));
        lvl3.setLvl4List(mapPresentationUnitStrategyLvl4(presentationUnitsStrategy, lvl3));
    }

    private Strategy mapLvl3Strategy(PresentationUnitsStrategy presentationUnitsStrategy, List<PresentationUnit> presentationUnits, Strategy strategy) {
        List<String> presentationUnitTypeList = Optional.ofNullable(presentationUnits)
                .stream()
                .flatMap(Collection::stream)
                .map(PresentationUnit::getType)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(presentationUnits) || !presentationUnitTypeList.contains(presentationUnitsStrategy.getFixtureRoleName())) {
            presentationUnits.add(mapLvl3StrategyPresentationUnit(presentationUnitsStrategy, new PresentationUnit()));
        }
        strategy.setPresentationUnits(presentationUnits);
        return strategy;
    }

    private PresentationUnit mapLvl3StrategyPresentationUnit(PresentationUnitsStrategy presentationUnitsStrategy, PresentationUnit presentationUnit) {
        presentationUnit.setType(presentationUnitsStrategy.getFixtureRoleName());
        presentationUnit.setMin(presentationUnitsStrategy.getMerchCatgMinPresentationUnit());
        presentationUnit.setMax(presentationUnitsStrategy.getMerchCatgMaxPresentationUnit());
        return presentationUnit;
    }

    private List<Lvl4> mapPresentationUnitStrategyLvl4(PresentationUnitsStrategy presentationUnitsStrategy, Lvl3 lvl3) {
        List<Lvl4> lvl4List = Optional.ofNullable(lvl3.getLvl4List())
                .orElse(new ArrayList<>());
        lvl4List.stream()
                .filter(lvl4 -> presentationUnitsStrategy.getLvl4Nbr().equals(lvl4.getLvl4Nbr()))
                .findFirst()
                .ifPresentOrElse(lvl4 -> setLvl4Strategy(presentationUnitsStrategy, lvl4),
                        () -> setLvl4(presentationUnitsStrategy, lvl4List));
        return lvl4List;
    }

    private void setLvl4Strategy(PresentationUnitsStrategy presentationUnitsStrategy, Lvl4 lvl4) {
        Strategy strategy = Optional.ofNullable(lvl4.getStrategy())
                .orElse(new Strategy());
        List<PresentationUnit> presentationUnits = Optional.ofNullable(lvl4.getStrategy())
                .map(Strategy::getPresentationUnits)
                .orElse(new ArrayList<>());
        lvl4.setStrategy(mapLvl4Strategy(presentationUnitsStrategy, presentationUnits, strategy));
        //Check if we have a finelineNbr, this defines we are just responding for catg & subCatg for the planId
        if (presentationUnitsStrategy.getFinelineNbr() != null) {
            lvl4.setFinelines(mapPresentationUnitStrategyFineline(presentationUnitsStrategy, lvl4));
        }
    }

    private Strategy mapLvl4Strategy(PresentationUnitsStrategy presentationUnitsStrategy, List<PresentationUnit> presentationUnits, Strategy strategy) {
        List<String> presentationUnitTypeList = Optional.ofNullable(presentationUnits)
                .stream()
                .flatMap(Collection::stream)
                .map(PresentationUnit::getType)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(presentationUnits) || !presentationUnitTypeList.contains(presentationUnitsStrategy.getFixtureRoleName())) {
            presentationUnits.add(mapLvl4StrategyPresentationUnit(presentationUnitsStrategy, new PresentationUnit()));
        }
        strategy.setPresentationUnits(presentationUnits);
        return strategy;
    }

    private PresentationUnit mapLvl4StrategyPresentationUnit(PresentationUnitsStrategy presentationUnitsStrategy, PresentationUnit presentationUnit) {
        presentationUnit.setType(presentationUnitsStrategy.getFixtureRoleName());
        presentationUnit.setMin(presentationUnitsStrategy.getSubCatgMinPresentationUnit());
        presentationUnit.setMax(presentationUnitsStrategy.getSubCatgMaxPresentationUnit());
        return presentationUnit;
    }

    private void setLvl4(PresentationUnitsStrategy presentationUnitsStrategy, List<Lvl4> lvl4List) {
        Lvl4 lvl4 = new Lvl4();
        lvl4.setLvl4Nbr(presentationUnitsStrategy.getLvl4Nbr());
        lvl4.setLvl4Name(presentationUnitsStrategy.getLvl4Name());
        lvl4List.add(lvl4);
        setLvl4Strategy(presentationUnitsStrategy, lvl4);
    }

    private List<Fineline> mapPresentationUnitStrategyFineline(PresentationUnitsStrategy presentationUnitsStrategy, Lvl4 lvl4) {
        List<Fineline> finelines = Optional.ofNullable(lvl4.getFinelines())
                .orElse(new ArrayList<>());
        finelines.stream()
                .filter(fineline -> presentationUnitsStrategy.getFinelineNbr().equals(fineline.getFinelineNbr()))
                .findFirst()
                .ifPresentOrElse(fineline -> setFinelineStrategy(presentationUnitsStrategy, fineline),
                        () -> setFineline(presentationUnitsStrategy, finelines));
        return finelines;
    }

    private void setFineline(PresentationUnitsStrategy presentationUnitsStrategy, List<Fineline> finelines) {
        Fineline fineline = new Fineline();
        fineline.setFinelineNbr(presentationUnitsStrategy.getFinelineNbr());
        fineline.setFinelineName(presentationUnitsStrategy.getFinelineName());
        fineline.setAltFinelineName(presentationUnitsStrategy.getAltFinelineName());
        finelines.add(fineline);
        setFinelineStrategy(presentationUnitsStrategy, fineline);
    }

    private void setFinelineStrategy(PresentationUnitsStrategy presentationUnitsStrategy, Fineline fineline) {
        Strategy strategy = Optional.ofNullable(fineline.getStrategy())
                .orElse(new Strategy());
        List<PresentationUnit> presentationUnits = Optional.ofNullable(fineline.getStrategy())
                .map(Strategy::getPresentationUnits)
                .orElse(new ArrayList<>());
        fineline.setStrategy(mapFinelineStrategy(presentationUnitsStrategy, presentationUnits, strategy));
    }

    private Strategy mapFinelineStrategy(PresentationUnitsStrategy presentationUnitsStrategy, List<PresentationUnit> presentationUnits, Strategy strategy) {
        List<String> presentationUnitTypeList = Optional.ofNullable(presentationUnits)
                .stream()
                .flatMap(Collection::stream)
                .map(PresentationUnit::getType)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(presentationUnits) || !presentationUnitTypeList.contains(presentationUnitsStrategy.getFixtureRoleName())) {
            presentationUnits.add(mapfinelineStrategyPresentationUnit(presentationUnitsStrategy, new PresentationUnit()));
        }
        strategy.setPresentationUnits(presentationUnits);
        return strategy;
    }

    private PresentationUnit mapfinelineStrategyPresentationUnit(PresentationUnitsStrategy presentationUnitsStrategy, PresentationUnit presentationUnit) {
        presentationUnit.setType(presentationUnitsStrategy.getFixtureRoleName());
        presentationUnit.setMin(presentationUnitsStrategy.getFlMinPresentationUnit());
        presentationUnit.setMax(presentationUnitsStrategy.getFlMaxPresentationUnit());
        return presentationUnit;
    }
}
