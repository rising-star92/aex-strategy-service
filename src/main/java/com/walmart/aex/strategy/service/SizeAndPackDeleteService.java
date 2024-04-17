package com.walmart.aex.strategy.service;

import com.walmart.aex.strategy.dto.request.Fineline;
import com.walmart.aex.strategy.dto.request.PlanStrategyDeleteMessage;
import com.walmart.aex.strategy.dto.request.StrongKey;
import com.walmart.aex.strategy.dto.request.Style;
import com.walmart.aex.strategy.entity.PlanClusterStrategy;
import com.walmart.aex.strategy.entity.PlanStrategy;
import com.walmart.aex.strategy.entity.PlanStrategyId;
import com.walmart.aex.strategy.entity.StrategyCcSPCluster;
import com.walmart.aex.strategy.entity.StrategyFineLineSPCluster;
import com.walmart.aex.strategy.entity.StrategyMerchCategorySPCluster;
import com.walmart.aex.strategy.entity.StrategyStyleSPCluster;
import com.walmart.aex.strategy.entity.StrategySubCategorySPCluster;
import com.walmart.aex.strategy.enums.StratGroupType;
import com.walmart.aex.strategy.exception.CustomException;
import com.walmart.aex.strategy.repository.PlanStrategyRepository;
import com.walmart.aex.strategy.repository.StrategyGroupRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SizeAndPackDeleteService {

    private final StrategyGroupRepository strategyGroupRepository;
    private final PlanStrategyRepository planStrategyRepository;


    public SizeAndPackDeleteService(StrategyGroupRepository strategyGroupRepository, PlanStrategyRepository planStrategyRepository) {
        this.strategyGroupRepository = strategyGroupRepository;
        this.planStrategyRepository = planStrategyRepository;
    }

    @Transactional
    public void deleteSizeStrategy(PlanStrategyDeleteMessage request) {
        if (request != null) {
            StrongKey strongKey = Optional.ofNullable(request.getStrongKey()).orElse(null);
            log.info("Deleting Size Strategy request: {}", strongKey);
            if (strongKey != null) {
                Long strategyId = strategyGroupRepository.getStrategyIdBySeasonCd(StratGroupType.SIZE_PROFILE.getStrategyGroupTypeCode(), null, null);
                PlanStrategyId planStrategyId = PlanStrategyId.builder()
                        .planId(strongKey.getPlanId())
                        .strategyId(strategyId)
                        .build();
                log.info("Check if a planStrategy Id : {} exists or not", planStrategyId.toString());
                PlanStrategy planStrategy = planStrategyRepository.findById(planStrategyId)
                        .orElseThrow(() -> new CustomException("Invalid Plan Strategy Id"));
                Set<PlanClusterStrategy> planClusterStrategies = planStrategy.getPlanClusterStrategies();
                Set<StrategyMerchCategorySPCluster> strategyMerchCategorySPClusters =
                        planClusterStrategies.stream().map(PlanClusterStrategy::getStrategyMerchCategorySPCluster)
                                .flatMap(Collection::stream)
                                .filter(strategyMerchCategorySPCluster -> strategyMerchCategorySPCluster.getStrategyMerchCatgSPClusId().getLvl3Nbr().equals(strongKey.getLvl3Nbr()))
                                .collect(Collectors.toSet());

                if (!CollectionUtils.isEmpty(strategyMerchCategorySPClusters)) {
                    deleteSPStrategyRelatedEntries(strategyMerchCategorySPClusters, strongKey);
                }
                deleteCatSp(planClusterStrategies, strongKey);
            }
        }

    }

    public Set<StrategyMerchCategorySPCluster> deleteSPStrategyRelatedEntries(Set<StrategyMerchCategorySPCluster> strategyMerchCategorySPClusters,StrongKey strongKey) {
        deleteSubCatgSp(strategyMerchCategorySPClusters, strongKey);
        strategyMerchCategorySPClusters.removeIf(merchCatPlan1 -> CollectionUtils.isEmpty(merchCatPlan1.getStrategySubCatgSPClusters()) && merchCatPlan1.getStrategyMerchCatgSPClusId().getLvl3Nbr().equals(strongKey.getLvl3Nbr()));
        return strategyMerchCategorySPClusters;
    }

    private void deleteCatSp(Set<PlanClusterStrategy> planClusterStrategies, StrongKey strongKey) {
        //Delete category entry if there are no subcategories under it
        if (!CollectionUtils.isEmpty(planClusterStrategies)) {
            planClusterStrategies.forEach(planClusterStrategy -> planClusterStrategy.getStrategyMerchCategorySPCluster().removeIf(strategyMerchCategorySPCluster ->
                    CollectionUtils.isEmpty(strategyMerchCategorySPCluster.getStrategySubCatgSPClusters()) && strategyMerchCategorySPCluster.getStrategyMerchCatgSPClusId().getLvl3Nbr().equals(strongKey.getLvl3Nbr())));
        }
    }

    private void deleteSubCatgSp(Set<StrategyMerchCategorySPCluster> strategyMerchCategorySPClusters, StrongKey strongKey) {
        deleteFinelineSp(strategyMerchCategorySPClusters, strongKey);
        //Delete sub category entry if there are no finelines under it
        if (!CollectionUtils.isEmpty(strategyMerchCategorySPClusters)) {
            strategyMerchCategorySPClusters.forEach(strategyMerchCategorySPCluster -> strategyMerchCategorySPCluster.
                    getStrategySubCatgSPClusters().removeIf(strategySubCategorySPCluster -> CollectionUtils.isEmpty(strategySubCategorySPCluster.
                            getStrategyFinelinesSPCluster()) && strategySubCategorySPCluster.getStrategySubCatgSPClusId().getLvl4Nbr().
                            equals(strongKey.getLvl4Nbr())));
        }
    }

    public void deleteFinelineSp(Set<StrategyMerchCategorySPCluster> strategyMerchCategorySPClusters, StrongKey strongKey) {
        Set<StrategySubCategorySPCluster> strategySubCategorySPClusters = fetchStrategySubCatgSpClus(strategyMerchCategorySPClusters, strongKey.getLvl4Nbr());
        Fineline fineline = strongKey.getFineline();
        //Delete style or cc flow
        if (fineline != null && !CollectionUtils.isEmpty(fineline.getStyles())) {
            log.info("Deleting style or cc for fineline {}", fineline.getFinelineNbr());
            deleteStyleSp(strategySubCategorySPClusters, fineline);
        }

        if (!CollectionUtils.isEmpty(strategySubCategorySPClusters)) {
            strategySubCategorySPClusters.forEach(strategySubCategorySPCluster -> {
                //remove fineline if fineline does not have any styles
                strategySubCategorySPCluster.getStrategyFinelinesSPCluster().removeIf(strategyFineLineSPCluster ->
                                CollectionUtils.isEmpty(strategyFineLineSPCluster.getStrategyStylesSPClusters()) &&
                                        strategyFineLineSPCluster.getStrategyIFineLineId().getFinelineNbr().equals(fineline.getFinelineNbr()));

                //Delete fineline flow
                if (fineline != null && CollectionUtils.isEmpty(fineline.getStyles())) {
                    log.info("Deleting fineline {} and related style and CCs", fineline.getFinelineNbr());
                    //First delete the styles and cc's before deleting the fineline
                    deleteStyleCCRelatedToFinelineSp(strategySubCategorySPClusters, fineline);
                    //Delete fineline now
                    strategySubCategorySPCluster.getStrategyFinelinesSPCluster().removeIf(strategyFineLineSPCluster ->
                                    strategyFineLineSPCluster.getStrategyIFineLineId().getFinelineNbr().equals(fineline.getFinelineNbr()));
                }
            });
        }
    }

    //Delete style entry from the DB
    public void deleteStyleSp(Set<StrategySubCategorySPCluster> strategySubCategorySPClusters, Fineline strongKeyFineline) {
        Set<StrategyFineLineSPCluster> strategyFineLineSPClusters = fetchStrategyFinelineSpClus(strategySubCategorySPClusters, strongKeyFineline.getFinelineNbr());
        strongKeyFineline.getStyles().forEach(style -> {
            //delete cc scenario
            if (style.getStyleNbr() != null && !CollectionUtils.isEmpty(style.getCustomerChoices())) {
                log.info("Deleting cc for style {}", style.getStyleNbr());
                deleteCcSp(strategyFineLineSPClusters, style);
            }

            //after deleting the cc's, if style does not have any cc, then delete style as well.
            if (!CollectionUtils.isEmpty(strategyFineLineSPClusters)) {
                strategyFineLineSPClusters.forEach(strategyFineLineSPCluster -> strategyFineLineSPCluster.getStrategyStylesSPClusters().removeIf(strategyStyleSPCluster -> CollectionUtils.isEmpty(strategyStyleSPCluster.getStrategyCcSPClusters()) && strategyStyleSPCluster.getStrategyStyleSPClusId().getStyleNbr().equalsIgnoreCase(style.getStyleNbr())
                        ));

                //Delete style scenario
                if (style.getStyleNbr() != null && CollectionUtils.isEmpty(style.getCustomerChoices())) {
                    log.info("Deleting style {} and related CCs", style.getStyleNbr());
                    //Before deleting style, delete cc's
                    deleteCcRelatedToStyleSp(strategyFineLineSPClusters, style);
                    //Delete Style
                    strategyFineLineSPClusters.forEach(strategyFineLineSPCluster -> strategyFineLineSPCluster.getStrategyStylesSPClusters().removeIf(strategyStyleSPCluster -> strategyStyleSPCluster.getStrategyStyleSPClusId().getStyleNbr().equalsIgnoreCase(style.getStyleNbr())
                    ));
                }
            }

        });
    }

    //Delete the selected CC entry from the DB
    public void deleteCcSp(Set<StrategyFineLineSPCluster> strategyFineLineSPClusters, Style style) {
        Set<StrategyStyleSPCluster> strategyStyleSPClusters1 = fetchStrategyStyleSpClus(strategyFineLineSPClusters, style.getStyleNbr());
        log.info("Delete CC flow : Deleting the CC related size and pack entries in Strategy DB for style {}", style);
        style.getCustomerChoices().forEach(customerChoice -> {
            if (!CollectionUtils.isEmpty(strategyStyleSPClusters1)) {
                //Delete CC flow, delete only the cc entry to be deleted.
                strategyStyleSPClusters1.forEach(strategyStyleSPCluster -> strategyStyleSPCluster.getStrategyCcSPClusters().
                        removeIf(strategyCcSPCluster -> strategyCcSPCluster.getStrategyCcSPClusId().getCcId().
                                equalsIgnoreCase(customerChoice.getCcId())));
            }
        });
    }

    //Before deleting fineline, delete style and CCs
    private void deleteStyleCCRelatedToFinelineSp(Set<StrategySubCategorySPCluster> strategySubCategorySPClusters, Fineline strongKeyFineline) {
        Set<StrategyFineLineSPCluster> strategyFineLineSPClusters = fetchStrategyFinelineSpClus(strategySubCategorySPClusters, strongKeyFineline.getFinelineNbr());
        //Delete style scenario
        strategyFineLineSPClusters.forEach(strategyFineLineSPCluster -> {
            Set<StrategyStyleSPCluster> strategyStyleSPClusters = strategyFineLineSPCluster.getStrategyStylesSPClusters();
            strategyStyleSPClusters.forEach(strategyStyleSPCluster -> {
                Set<StrategyCcSPCluster> strategyCcSPClusters = strategyStyleSPCluster.getStrategyCcSPClusters();
                if (!CollectionUtils.isEmpty(strategyCcSPClusters)) {
                    //remove all the cc under style
                    strategyCcSPClusters.removeIf(strategyCcSPCluster -> strategyCcSPCluster.getStrategyCcSPClusId().getCcId() != null);
                }
            });
            //remove all the styles under fineline
            strategyStyleSPClusters.removeIf(strategyStyleSPCluster -> CollectionUtils.isEmpty(strategyStyleSPCluster.getStrategyCcSPClusters()) && strategyStyleSPCluster.getStrategyStyleSPClusId().getStyleNbr() !=null);
        });
    }

    //Before deleting Style, delete all related CCs
    private void deleteCcRelatedToStyleSp(Set<StrategyFineLineSPCluster> strategyFineLineSPClusters, Style style) {
        Set<StrategyStyleSPCluster> strategyStyleSPClusters1 = fetchStrategyStyleSpClus(strategyFineLineSPClusters, style.getStyleNbr());
        //Delete Style flow, delete all the CCs related to that Style before deleting the style entry
        log.info("Delete Style flow : Deleting the CC related size and pack entries in Strategy DB for style {} before deleting the style", style);
        strategyStyleSPClusters1.forEach(strategyStyleSPCluster -> strategyStyleSPCluster.getStrategyCcSPClusters().
                removeIf(strategyCcSPCluster -> strategyCcSPCluster.getStrategyCcSPClusId().getCcId() != null));
    }

    private Set<StrategySubCategorySPCluster> fetchStrategySubCatgSpClus(Set<StrategyMerchCategorySPCluster> strategyMerchCategorySPClusters, Integer lvl4Nbr) {
        Set<StrategySubCategorySPCluster> strategySubCategorySPClusters = new HashSet<>();

        if (!CollectionUtils.isEmpty(strategyMerchCategorySPClusters)) {
            strategySubCategorySPClusters = strategyMerchCategorySPClusters
                    .stream()
                    .map(StrategyMerchCategorySPCluster::getStrategySubCatgSPClusters)
                    .flatMap(Collection::stream)
                    .filter(strategySubCategorySPCluster -> strategySubCategorySPCluster.getStrategySubCatgSPClusId().getLvl4Nbr().equals(lvl4Nbr))
                    .collect(Collectors.toSet());
        }
        return strategySubCategorySPClusters;
    }

    private Set<StrategyFineLineSPCluster> fetchStrategyFinelineSpClus(Set<StrategySubCategorySPCluster> strategySubCategorySPClusters, Integer finelineNbr) {
        Set<StrategyFineLineSPCluster> strategyFineLineSPClusters = new HashSet<>();
        if (!CollectionUtils.isEmpty(strategySubCategorySPClusters)) {
            strategyFineLineSPClusters = strategySubCategorySPClusters
                    .stream()
                    .map(StrategySubCategorySPCluster::getStrategyFinelinesSPCluster)
                    .flatMap(Collection::stream)
                    .filter(strategyFineLineSPCluster -> strategyFineLineSPCluster.getStrategyIFineLineId().getFinelineNbr().equals(finelineNbr))
                    .collect(Collectors.toSet());
        }
        return strategyFineLineSPClusters;
    }

    private Set<StrategyStyleSPCluster> fetchStrategyStyleSpClus(Set<StrategyFineLineSPCluster> strategyFineLineSPClusters, String styleNum) {
        Set<StrategyStyleSPCluster> strategyStyleSPClusters = new HashSet<>();
        if (!CollectionUtils.isEmpty(strategyFineLineSPClusters)) {
            strategyStyleSPClusters = strategyFineLineSPClusters
                    .stream()
                    .map(StrategyFineLineSPCluster::getStrategyStylesSPClusters)
                    .flatMap(Collection::stream)
                    .filter(strategyStyleSPCluster -> strategyStyleSPCluster.getStrategyStyleSPClusId().getStyleNbr().equals(styleNum))
                    .collect(Collectors.toSet());
        }
        return strategyStyleSPClusters;
    }
}
