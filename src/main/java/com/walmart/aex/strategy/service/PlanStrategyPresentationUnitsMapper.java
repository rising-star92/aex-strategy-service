package com.walmart.aex.strategy.service;

import com.walmart.aex.strategy.dto.PlanFinelinesThickness;
import com.walmart.aex.strategy.dto.request.*;
import com.walmart.aex.strategy.entity.*;
import com.walmart.aex.strategy.enums.ChannelType;
import com.walmart.aex.strategy.enums.FixtureTypeRollup;
import com.walmart.aex.strategy.repository.StrategyFinelineRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManager;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PlanStrategyPresentationUnitsMapper {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private StrategyFinelineRepository strategyFinelineRepository;

    @Autowired
    private PresentationUnitsMinMaxMappingService presentationUnitsMinMaxMappingService;

    public PlanStrategy updatePUBasedOnRFA(PlanStrategy planStrategy, int lvl0Nbr, Lvl1 lvl1, Lvl2 lvl2) {
        boolean isRemoveNeeded = true;
        Set<StrategyMerchCatg> strategyMerchCatgs = Optional.ofNullable(planStrategy.getStrategyMerchCatgs())
                .orElse(new HashSet<>());
        if (CollectionUtils.isEmpty(strategyMerchCatgs)) {
            isRemoveNeeded = false;
        }
        boolean finalIsRemoveNeeded = isRemoveNeeded;
        if (!CollectionUtils.isEmpty(lvl2.getLvl3List())) {
            lvl2.getLvl3List()
                    .forEach(lvl3 -> setPresentationUnitMerchCatg(planStrategy, lvl0Nbr, lvl1.getLvl1Nbr(), lvl2.getLvl2Nbr(), lvl3, strategyMerchCatgs, finalIsRemoveNeeded));

            //Removing the categories which does not have an allocation by RFA
            if(isRemoveNeeded){
                List<Integer> lvl3Nbrs = Optional.ofNullable(lvl2.getLvl3List()).stream().flatMap(Collection::stream)
                        .map(Lvl3::getLvl3Nbr).collect(Collectors.toList());
                if(!CollectionUtils.isEmpty(lvl3Nbrs)) {
                    strategyMerchCatgs.removeIf(strategyMerchCatg -> (!lvl3Nbrs.contains(strategyMerchCatg.getStrategyMerchCatgId().getLvl3Nbr()) && planStrategy.getPlanStrategyId().equals(strategyMerchCatg.getPlanStrategy().getPlanStrategyId())));
                }
            }
        }
        planStrategy.setStrategyMerchCatgs(strategyMerchCatgs);
        return planStrategy;
    }

    private void setPresentationUnitMerchCatg(PlanStrategy planStrategy, Integer lvl0Nbr, Integer lvl1Nbr, Integer lvl2Nbr,
                                              Lvl3 lvl3, Set<StrategyMerchCatg> strategyMerchCatgs, boolean finalIsRemoveNeeded) {
        log.info("Set Lvl3 Id for presentation units");
        StrategyMerchCatgId strategyMerchCatgId = new StrategyMerchCatgId(planStrategy.getPlanStrategyId(),
                lvl0Nbr, lvl1Nbr, lvl2Nbr, lvl3.getLvl3Nbr());
        log.info("Check if a strategyMerchCatg Id : {} already exists or not", strategyMerchCatgId.getLvl3Nbr().toString());
        StrategyMerchCatg strategyMerchCatg = strategyMerchCatgs
                .stream()
                .filter(strategyMerchCatg1 -> strategyMerchCatg1.getStrategyMerchCatgId().getLvl3Nbr().equals(strategyMerchCatgId.getLvl3Nbr()))
                .findFirst()
                .orElse(new StrategyMerchCatg());
        if (strategyMerchCatg.getStrategyMerchCatgId() == null) {
            strategyMerchCatg.setStrategyMerchCatgId(strategyMerchCatgId);
        }
        strategyMerchCatg.setChannelId(ChannelType.getChannelIdFromName(lvl3.getChannel()));
        modifyFixtureTypes(planStrategy.getPlanStrategyId().getPlanId(), strategyMerchCatgs, strategyMerchCatg, lvl3, finalIsRemoveNeeded, planStrategy.getPlanStrategyId().getStrategyId());
    }

    private void modifyFixtureTypes(Long planId, Set<StrategyMerchCatg> strategyMerchCatgs, StrategyMerchCatg strategyMerchCatg, Lvl3 lvl3,
                                    boolean isFinalRemoveNeeded, Long strategyId) {
        if (!CollectionUtils.isEmpty(lvl3.getLvl4List())) {
            lvl3.getLvl4List().forEach(lvl4 -> setPresentationUnitForFineline(planId, lvl4, strategyMerchCatgs, strategyMerchCatg, lvl3, isFinalRemoveNeeded));
        }
        if (isFinalRemoveNeeded) {
            removeCatgFixtureTypesNotPartOfUpdate(strategyMerchCatg, lvl3);
            removeMerchCatg(strategyMerchCatgs, lvl3, strategyId);

            //Removing the sub categories which does not have an allocation by RFA
            removeSubCategoryIfNotAllocated(strategyMerchCatg, lvl3, strategyId);
        }

    }

    private void setPresentationUnitForFineline(Long planId, Lvl4 lvl4, Set<StrategyMerchCatg> strategyMerchCatgs, StrategyMerchCatg strategyMerchCatg,
                                                Lvl3 lvl3, boolean isFinalRemoveNeeded) {
        List<Integer> finelineNbr = Optional.ofNullable(lvl4.getFinelines())
                .stream()
                .flatMap(Collection::stream)
                .map(Fineline::getFinelineNbr)
                .collect(Collectors.toList());
        List<PlanFinelinesThickness> planFinelinesThicknesses = strategyFinelineRepository.getFinelinesThickness(planId, finelineNbr);

        if (!CollectionUtils.isEmpty(lvl4.getFinelines())) {
            lvl4.getFinelines().forEach(fineline -> processPresentationUnitForFineline(planFinelinesThicknesses,
                    fineline, strategyMerchCatgs, strategyMerchCatg, lvl3, lvl4, isFinalRemoveNeeded));
            if (isFinalRemoveNeeded)
                removeSubCatgFixtureTypesNotPartOfUpdate(strategyMerchCatg, lvl3, lvl4);
        }
    }

    private void processPresentationUnitForFineline(List<PlanFinelinesThickness> planFinelinesThicknesses,
                                                    Fineline fineline, Set<StrategyMerchCatg> strategyMerchCatgs, StrategyMerchCatg strategyMerchCatg, Lvl3 lvl3, Lvl4 lvl4, boolean isFinalRemoveNeeded) {
        PlanFinelinesThickness planFinelinesThickness = new PlanFinelinesThickness();
        if (!CollectionUtils.isEmpty(planFinelinesThicknesses)) {
            planFinelinesThickness = planFinelinesThicknesses.stream().filter(planFinelinesThickness1 -> planFinelinesThickness1.getFinelineNbr().equals(fineline.getFinelineNbr())).findFirst().orElse(null);
        }
        PlanFinelinesThickness finalPlanFinelinesThickness = planFinelinesThickness;
        Optional.ofNullable(fineline.getStrategy())
                .map(Strategy::getPresentationUnits)
                .stream()
                .findAny()
                .ifPresentOrElse(presentationUnits -> setPresentationUnits(strategyMerchCatgs, strategyMerchCatg, lvl3, lvl4, fineline, presentationUnits, isFinalRemoveNeeded, finalPlanFinelinesThickness),
                        () -> removeFLFixtureTypesNotPartOfUpdate(strategyMerchCatgs, lvl3, lvl4, fineline));
    }

    private void setPresentationUnits(Set<StrategyMerchCatg> strategyMerchCatgs, StrategyMerchCatg strategyMerchCatg, Lvl3 lvl3,
                                      Lvl4 lvl4, Fineline fineline, List<PresentationUnit> presentationUnits, boolean isFinalRemoveNeeded,
                                      PlanFinelinesThickness planFinelinesThickness) {
        if (!CollectionUtils.isEmpty(presentationUnits))
            presentationUnits.forEach(presentationUnit -> {
                addFixtureTypeForAllLevel(strategyMerchCatgs, strategyMerchCatg, lvl3, lvl4, fineline, presentationUnit, planFinelinesThickness);
                if (isFinalRemoveNeeded)
                    removeFLFixtureTypesNotPartOfUpdate(strategyMerchCatgs, lvl3, lvl4, fineline);
            });
    }

    private void addFixtureTypeForAllLevel(Set<StrategyMerchCatg> strategyMerchCatgs, StrategyMerchCatg strategyMerchCatg,
                                           Lvl3 lvl3, Lvl4 lvl4, Fineline fineline, PresentationUnit presentationUnit,
                                           PlanFinelinesThickness planFinelinesThickness) {
        boolean isNewCatg = false;
        boolean isNewSubCatg = false;
        Integer fixtureTypeId = FixtureTypeRollup.getFixtureIdFromName(presentationUnit.getType());

        log.info("Check if a fixtureType Id : {} already exists or not for Category : {}", fixtureTypeId.toString(), lvl3.getLvl3Nbr().toString());
        StrategyMerchCatgFixture category = Optional.ofNullable(strategyMerchCatg.getStrategyMerchCatgFixtures())
                .stream()
                .flatMap(Collection::stream)
                .filter(categoryList -> categoryList.getStrategyMerchCatgFixtureId().getStrategyMerchCatgId().getLvl3Nbr().equals(lvl3.getLvl3Nbr())
                        && categoryList.getStrategyMerchCatgFixtureId().getFixtureTypeId().equals(fixtureTypeId))
                .findFirst()
                .orElse(new StrategyMerchCatgFixture());

        if (category.getStrategyMerchCatgFixtureId() == null) {
            log.info("Adding fixtureType Id: {} for Category: {}", fixtureTypeId, lvl3.getLvl3Nbr().toString());
            category.setStrategyMerchCatgFixtureId(new StrategyMerchCatgFixtureId(strategyMerchCatg.getStrategyMerchCatgId(), fixtureTypeId));
            category.setStrategySubCatgFixtures(new HashSet<>());
            isNewCatg = true;
        }

        log.info("Check if a fixtureType Id : {} already exists or not for SubCategory : {}", fixtureTypeId, lvl4.getLvl4Nbr().toString());
        StrategySubCatgFixture subCategory = Optional.ofNullable(category.getStrategySubCatgFixtures())
                .stream()
                .flatMap(Collection::stream)
                .filter(subCatList -> subCatList.getStrategySubCatgFixtureId().getLvl4Nbr().equals(lvl4.getLvl4Nbr())
                        && subCatList.getStrategySubCatgFixtureId().getStrategyMerchCatgFixtureId().getFixtureTypeId().equals(fixtureTypeId))
                .findFirst()
                .orElse(new StrategySubCatgFixture());
        if (subCategory.getStrategySubCatgFixtureId() == null) {
            log.info("Adding fixtureType Id : {} for Sub Category : {}", fixtureTypeId, lvl4.getLvl4Nbr());
            subCategory.setStrategySubCatgFixtureId(new StrategySubCatgFixtureId(category.getStrategyMerchCatgFixtureId(), lvl4.getLvl4Nbr()));
            subCategory.setStrategyFinelineFixtures(new HashSet<>());
            isNewSubCatg = true;
        }

        log.info("Check if a fixtureType Id : {} already exists or not for FineLine : {}", fixtureTypeId, fineline.getFinelineNbr().toString());
        StrategyFinelineFixture fineLineFixture = Optional.ofNullable(subCategory.getStrategyFinelineFixtures())
                .stream()
                .flatMap(Collection::stream)
                .filter(lineItemList -> lineItemList.getStrategyFinelineFixtureId().getStrategySubCatgFixtureId().getStrategyMerchCatgFixtureId().getFixtureTypeId().equals(fixtureTypeId)
                        && lineItemList.getStrategyFinelineFixtureId().getFinelineNbr().equals(fineline.getFinelineNbr()))
                .findFirst()
                .orElse(new StrategyFinelineFixture());
        if (fineLineFixture.getStrategyFinelineFixtureId() == null) {
            List<StrategyPUMinMax> strategyPUMinMaxes = presentationUnitsMinMaxMappingService.getPresentationUnitsMinMax();
            log.info("Adding fixtureType Id: {} for fineline : {}", fixtureTypeId, fineline.getFinelineNbr());
            fineLineFixture.setStrategyFinelineFixtureId(new StrategyFinelineFixtureId(subCategory.getStrategySubCatgFixtureId(), fineline.getFinelineNbr()));
            if (!CollectionUtils.isEmpty(strategyPUMinMaxes) && planFinelinesThickness.getThicknessId() != null) {
                fineLineFixture.setMinPresentationUnits(getMinPresentationUnits(strategyPUMinMaxes, planFinelinesThickness,
                        subCategory.getStrategySubCatgFixtureId().getStrategyMerchCatgFixtureId().getFixtureTypeId()));
                log.info("The Min value set for the fineline:{}, minQty:{}", fineline.getFinelineNbr(), fineLineFixture.getMinPresentationUnits());
                fineLineFixture.setMaxPresentationUnits(getMaxPresentationUnits(strategyPUMinMaxes, planFinelinesThickness,
                         subCategory.getStrategySubCatgFixtureId().getStrategyMerchCatgFixtureId().getFixtureTypeId()));
                log.info("The Max value set for the fineline:{}, maxQty:{}", fineline.getFinelineNbr(), fineLineFixture.getMaxPresentationUnits());
            }
            subCategory.getStrategyFinelineFixtures().add(fineLineFixture);
        }

        if (isNewSubCatg)
            category.getStrategySubCatgFixtures().add(subCategory);

        if (isNewCatg) {
            if (CollectionUtils.isEmpty(strategyMerchCatg.getStrategyMerchCatgFixtures()))
                strategyMerchCatg.setStrategyMerchCatgFixtures(new HashSet<>());
            strategyMerchCatg.getStrategyMerchCatgFixtures().add(category);
            strategyMerchCatgs.add(strategyMerchCatg);
        }
    }

    private Integer getMinPresentationUnits(List<StrategyPUMinMax> strategyPUMinMaxes, PlanFinelinesThickness planFinelinesThickness, Integer fixtureId) {
        log.info("getting the min PU for:{}", planFinelinesThickness.toString());
        return strategyPUMinMaxes
                .stream()
                .filter(strategyPUMinMax -> strategyPUMinMax.getStrategyPUMinMaxId().getAhsVId().equals(planFinelinesThickness.getThicknessId()) &&
                        strategyPUMinMax.getStrategyPUMinMaxId().getLvl0Nbr().equals(planFinelinesThickness.getLvl0Nbr()) &&
                        strategyPUMinMax.getStrategyPUMinMaxId().getLvl1Nbr().equals(planFinelinesThickness.getLvl1Nbr()) &&
                        strategyPUMinMax.getStrategyPUMinMaxId().getLvl2Nbr().equals(planFinelinesThickness.getLvl2Nbr()) &&
                        strategyPUMinMax.getStrategyPUMinMaxId().getLvl3Nbr().equals(planFinelinesThickness.getLvl3Nbr()) &&
                        strategyPUMinMax.getStrategyPUMinMaxId().getLvl4Nbr().equals(planFinelinesThickness.getLvl4Nbr()) &&
                        strategyPUMinMax.getStrategyPUMinMaxId().getFixtureTypeId().equals(fixtureId))
                .findFirst().map(StrategyPUMinMax::getMinQty).orElse(null);

    }

    private Integer getMaxPresentationUnits(List<StrategyPUMinMax> strategyPUMinMaxes, PlanFinelinesThickness planFinelinesThickness, Integer fixtureId) {
        return strategyPUMinMaxes
                .stream()
                .filter(strategyPUMinMax -> strategyPUMinMax.getStrategyPUMinMaxId().getAhsVId().equals(planFinelinesThickness.getThicknessId()) &&
                        strategyPUMinMax.getStrategyPUMinMaxId().getLvl0Nbr().equals(planFinelinesThickness.getLvl0Nbr()) &&
                        strategyPUMinMax.getStrategyPUMinMaxId().getLvl1Nbr().equals(planFinelinesThickness.getLvl1Nbr()) &&
                        strategyPUMinMax.getStrategyPUMinMaxId().getLvl2Nbr().equals(planFinelinesThickness.getLvl2Nbr()) &&
                        strategyPUMinMax.getStrategyPUMinMaxId().getLvl3Nbr().equals(planFinelinesThickness.getLvl3Nbr()) &&
                        strategyPUMinMax.getStrategyPUMinMaxId().getLvl4Nbr().equals(planFinelinesThickness.getLvl4Nbr()) &&
                        strategyPUMinMax.getStrategyPUMinMaxId().getFixtureTypeId().equals(fixtureId))
                .findFirst().map(StrategyPUMinMax::getMaxQty).orElse(null);
    }

    private void removeFLFixtureTypesNotPartOfUpdate(Set<StrategyMerchCatg> strategyMerchCatgs, Lvl3 lvl3, Lvl4 lvl4, Fineline fineline) {
        log.info("Deleting either FineLine {} or fixture types not part of update", fineline.getFinelineNbr());
        if (!CollectionUtils.isEmpty(strategyMerchCatgs)) {

            List<Integer> fineLineNbrs = Optional.ofNullable(lvl4.getFinelines())
                    .stream()
                    .flatMap(Collection::stream)
                    .map(Fineline::getFinelineNbr)
                    .collect(Collectors.toList());

            List<Integer> fixtureIds = Optional.ofNullable(fineline.getStrategy().getPresentationUnits())
                    .stream()
                    .flatMap(Collection::stream)
                    .map(fixture -> FixtureTypeRollup.getFixtureIdFromName(fixture.getType()))
                    .collect(Collectors.toList());

            if (CollectionUtils.isEmpty(fixtureIds))
                fineLineNbrs.remove(fineline.getFinelineNbr());
            strategyMerchCatgs.forEach(strategyMerchCatg -> strategyMerchCatg.getStrategyMerchCatgFixtures().forEach(strategyMerchCatgFixture -> {
                if (strategyMerchCatgFixture.getStrategyMerchCatgFixtureId().getStrategyMerchCatgId().getLvl3Nbr().equals(lvl3.getLvl3Nbr()))
                    strategyMerchCatgFixture.getStrategySubCatgFixtures().forEach(subCatgFixture -> {
                        if (subCatgFixture.getStrategySubCatgFixtureId().getLvl4Nbr().equals(lvl4.getLvl4Nbr()))
                            subCatgFixture.getStrategyFinelineFixtures()
                                    .removeIf(strategyFinelineFixture -> (strategyFinelineFixture.getStrategyFinelineFixtureId().getFinelineNbr().equals(fineline.getFinelineNbr())
                                            && !fixtureIds.contains(strategyFinelineFixture.getStrategyFinelineFixtureId().getStrategySubCatgFixtureId().getStrategyMerchCatgFixtureId().getFixtureTypeId()))
                                            || (!fineLineNbrs.contains(strategyFinelineFixture.getStrategyFinelineFixtureId().getFinelineNbr())));
                        entityManager.flush();
                    });
            }));
        }
    }

    private void removeSubCatgFixtureTypesNotPartOfUpdate(StrategyMerchCatg strategyMerchCatg, Lvl3 lvl3, Lvl4 lvl4) {
        log.info("Removing fixtureTypes for SubCategory: {}", lvl4.getLvl4Nbr().toString());
        List<Integer> fixtureIds = Optional.ofNullable(lvl4.getStrategy().getPresentationUnits())
                .stream()
                .flatMap(Collection::stream)
                .map(fixture -> FixtureTypeRollup.getFixtureIdFromName(fixture.getType()))
                .collect(Collectors.toList());

        strategyMerchCatg.getStrategyMerchCatgFixtures().stream()
                .filter(categoryList -> categoryList.getStrategyMerchCatgFixtureId().getStrategyMerchCatgId().getLvl3Nbr().equals(lvl3.getLvl3Nbr()))
                .findFirst().ifPresent(category -> category.getStrategySubCatgFixtures().removeIf(stratSubCatgFixture -> stratSubCatgFixture.getStrategySubCatgFixtureId().getLvl4Nbr().equals(lvl4.getLvl4Nbr())
                && !fixtureIds.contains(stratSubCatgFixture.getStrategySubCatgFixtureId().getStrategyMerchCatgFixtureId().getFixtureTypeId())));
        entityManager.flush();

    }

    private void removeCatgFixtureTypesNotPartOfUpdate(StrategyMerchCatg strategyMerchCatg, Lvl3 lvl3) {
        log.info("Removing fixtureTypes for Category: {}", lvl3.getLvl3Nbr().toString());
        List<Integer> fixtureIds = Optional.ofNullable(lvl3.getStrategy().getPresentationUnits())
                .stream()
                .flatMap(Collection::stream)
                .map(fixture -> FixtureTypeRollup.getFixtureIdFromName(fixture.getType()))
                .collect(Collectors.toList());

        strategyMerchCatg.getStrategyMerchCatgFixtures().removeIf(stratCatgFixture -> stratCatgFixture.getStrategyMerchCatgFixtureId().getStrategyMerchCatgId().getLvl3Nbr().equals(lvl3.getLvl3Nbr())
                && !fixtureIds.contains(stratCatgFixture.getStrategyMerchCatgFixtureId().getFixtureTypeId()));
        entityManager.flush();
    }

    private void removeMerchCatg(Set<StrategyMerchCatg> strategyMerchCatgs, Lvl3 lvl3, Long strategyId) {
        log.info("Delete Category if no presentation unit catg fixture exists, Category: {}", lvl3.getLvl3Nbr().toString());
        if (checkIfCatgFixtureExists(strategyMerchCatgs, lvl3)) {
            strategyMerchCatgs
                    .removeIf(strategyMerchCatg ->
                            strategyMerchCatg.getStrategyMerchCatgId().getLvl3Nbr().equals(lvl3.getLvl3Nbr())
                                    && strategyMerchCatg.getStrategyMerchCatgId().getPlanStrategyId().getStrategyId().equals(strategyId));
            entityManager.flush();
        }

    }

    private boolean checkIfCatgFixtureExists(Set<StrategyMerchCatg> strategyMerchCatgs, Lvl3 lvl3) {
        return Optional.ofNullable(strategyMerchCatgs)
                .stream()
                .flatMap(Collection::stream)
                .filter(strategyMerchCatg -> strategyMerchCatg.getStrategyMerchCatgId().getLvl3Nbr().equals(lvl3.getLvl3Nbr()))
                .anyMatch(strategyMerchCatg -> CollectionUtils.isEmpty(strategyMerchCatg.getStrategyMerchCatgFixtures()));

    }

    private void removeSubCategoryIfNotAllocated(StrategyMerchCatg strategyMerchCatg, Lvl3 lvl3, Long strategyId){
        List<Integer> lvl4Nbrs = Optional.ofNullable(lvl3.getLvl4List())
                .stream().flatMap(Collection::stream)
                .map(Lvl4::getLvl4Nbr).collect(Collectors.toList());
        if(!CollectionUtils.isEmpty(lvl4Nbrs)) {
            strategyMerchCatg.getStrategyMerchCatgFixtures().stream()
                    .filter(categoryList -> categoryList.getStrategyMerchCatgFixtureId().getStrategyMerchCatgId().getLvl3Nbr().equals(lvl3.getLvl3Nbr()) && categoryList.getStrategyMerchCatgFixtureId().getStrategyMerchCatgId().getPlanStrategyId().getStrategyId().equals(strategyId))
                    .collect(Collectors.toSet()).forEach(category -> category.getStrategySubCatgFixtures().removeIf(stratSubCatgFixture -> (!lvl4Nbrs.contains(stratSubCatgFixture.getStrategySubCatgFixtureId().getLvl4Nbr()))));
            entityManager.flush();
        }
    }

}