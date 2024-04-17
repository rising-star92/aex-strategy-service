package com.walmart.aex.strategy.service;

import com.walmart.aex.strategy.dto.FixtureAllocationStrategy;
import com.walmart.aex.strategy.dto.request.*;
import com.walmart.aex.strategy.entity.*;
import com.walmart.aex.strategy.enums.ChannelType;
import com.walmart.aex.strategy.enums.TraitChoiceType;
import com.walmart.aex.strategy.properties.AppProperties;
import com.walmart.aex.strategy.util.CommonMethods;
import com.walmart.aex.strategy.util.CommonUtil;
import io.strati.ccm.utils.client.annotation.ManagedConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Service
@Slf4j
public class PlanStrategyUpdateMapper {

    /**
     * This method check Merch Catg Metrics exists that is in the request payload.
     *
     * @param strategyMerchCatgs
     * @param lvl3
     * @param planStrategyId
     * @param lvl2Nbr
     * @param lvl1Nbr
     * @param lvl0Nbr
     * @param planClusterStrategies
     */

    @ManagedConfiguration
    private AppProperties appProperties;

    @Autowired
    private CommonMethods commonMethods;

    public void updateMerchCatgMetrics(Set<StrategyMerchCatg> strategyMerchCatgs, Lvl3 lvl3, PlanStrategyId planStrategyId,
                                       Integer lvl2Nbr, Integer lvl1Nbr, Integer lvl0Nbr, Set<PlanClusterStrategy> planClusterStrategies, FixtureAllocationStrategy allocationStrategy) {
        //request hierarchy identifier
        StrategyMerchCatgId strategyMerchCatgId = new StrategyMerchCatgId(planStrategyId, lvl0Nbr, lvl1Nbr, lvl2Nbr, lvl3.getLvl3Nbr());
        Optional.ofNullable(strategyMerchCatgs)
                .stream()
                .flatMap(Collection::stream)
                .filter(strategyMerchCatg -> strategyMerchCatg.getStrategyMerchCatgId().equals(strategyMerchCatgId))
                .findFirst()
                .ifPresent(strategyMerchCatg -> setMerchCatgMetrics(strategyMerchCatg, strategyMerchCatgId, lvl3,
                        lvl0Nbr, lvl1Nbr, lvl2Nbr, planClusterStrategies, allocationStrategy));
    }

    /**
     * This method sets Merch Catg Metrics & loops through subCatg that is in the request payload.
     *
     * @param strategyMerchCatg
     * @param lvl3
     * @param strategyMerchCatgId
     * @param lvl2Nbr
     * @param lvl1Nbr
     * @param lvl0Nbr
     * @param planClusterStrategies
     */

    private void setMerchCatgMetrics(StrategyMerchCatg strategyMerchCatg, StrategyMerchCatgId strategyMerchCatgId, Lvl3 lvl3,
                                     Integer lvl0Nbr, Integer lvl1Nbr, Integer lvl2Nbr, Set<PlanClusterStrategy> planClusterStrategies, FixtureAllocationStrategy allocationStrategy) {
        log.info("Updating plan Merch Catg Metrics for : {}", strategyMerchCatgId.toString());
        strategyMerchCatg.setChannelId(ChannelType.getChannelIdFromName(lvl3.getChannel()));
        if (!CollectionUtils.isEmpty(lvl3.getLvl4List())) {
            for (Lvl4 lvl4 : lvl3.getLvl4List()) {
                updateSubCatgMetrics(strategyMerchCatg.getStrategySubCatgs(), lvl4, strategyMerchCatgId,
                        lvl0Nbr, lvl1Nbr, lvl2Nbr, lvl3.getLvl3Nbr(), planClusterStrategies, allocationStrategy);
                if (allocationStrategy.isHasProdDimChanged()) {
                    allocationStrategy.setLvl3Name(lvl3.getLvl3Name());
                }
            }
        }
    }

    private void updateSubCatgMetrics(Set<StrategySubCatg> strategySubCatgs, Lvl4 lvl4, StrategyMerchCatgId strategyMerchCatgId
            , Integer lvl0Nbr, Integer lvl1Nbr, Integer lvl2Nbr, Integer lvl3Nbr, Set<PlanClusterStrategy> planClusterStrategies, FixtureAllocationStrategy allocationStrategy) {
        //request hierarchy identifier
        StrategySubCatgId strategySubCatgId = new StrategySubCatgId(strategyMerchCatgId, lvl4.getLvl4Nbr());
        Optional.ofNullable(strategySubCatgs)
                .stream()
                .flatMap(Collection::stream)
                .filter(strategySubCatg -> strategySubCatg.getStrategySubCatgId().equals(strategySubCatgId))
                .findFirst()
                .ifPresent(strategySubCatg -> setSubCatgMetrics(strategySubCatgId, strategySubCatg, lvl4, lvl0Nbr, lvl1Nbr,
                        lvl2Nbr, lvl3Nbr, planClusterStrategies, allocationStrategy));
    }

    private void setSubCatgMetrics(StrategySubCatgId strategySubCatgId, StrategySubCatg strategySubCatg, Lvl4 lvl4, Integer lvl0Nbr,
                                   Integer lvl1Nbr, Integer lvl2Nbr, Integer lvl3Nbr, Set<PlanClusterStrategy> planClusterStrategies, FixtureAllocationStrategy allocationStrategy) {
        log.info("Updating plan Sub Catg Metrics for : {}", strategySubCatgId.toString());
        strategySubCatg.setChannelId(ChannelType.getChannelIdFromName(lvl4.getChannel()));
        if (!CollectionUtils.isEmpty(lvl4.getFinelines())) {
            for (Fineline fineline : lvl4.getFinelines()) {
                updatePlanStrategyFineline(strategySubCatg.getStrategyFinelines(), fineline, strategySubCatgId,
                        lvl0Nbr, lvl1Nbr, lvl2Nbr, lvl3Nbr, lvl4.getLvl4Nbr(), planClusterStrategies, allocationStrategy);
                if (allocationStrategy.isHasProdDimChanged()) {
                    allocationStrategy.setLvl4Name(lvl4.getLvl4Name());
                }
            }
        }
    }

    private void updatePlanStrategyFineline(Set<StrategyFineline> strategyFinelines, Fineline fineline,
                                            StrategySubCatgId strategySubCatgId, Integer lvl0Nbr, Integer lvl1Nbr, Integer lvl2Nbr,
                                            Integer lvl3Nbr, Integer lvl4Nbr, Set<PlanClusterStrategy> planClusterStrategies, FixtureAllocationStrategy allocationStrategy) {
        //request hierarchy identifier
        StrategyFinelineId strategyFinelineId = new StrategyFinelineId(strategySubCatgId, fineline.getFinelineNbr());
        Optional.ofNullable(strategyFinelines)
                .stream()
                .flatMap(Collection::stream)
                .filter(strategyFineline -> strategyFineline.getStrategyFinelineId().equals(strategyFinelineId))
                .findFirst()
                .ifPresent(strategyFineline -> setPlanStrategyFineline(strategyFineline, strategyFinelineId, fineline, lvl0Nbr,
                        lvl1Nbr, lvl2Nbr, lvl3Nbr, lvl4Nbr, planClusterStrategies, allocationStrategy));
    }

    private void setPlanStrategyFineline(StrategyFineline strategyFineline, StrategyFinelineId strategyFinelineId,
                                         Fineline fineline, Integer lvl0Nbr, Integer lvl1Nbr, Integer lvl2Nbr, Integer lvl3Nbr,
                                         Integer lvl4Nbr, Set<PlanClusterStrategy> planClusterStrategies, FixtureAllocationStrategy allocationStrategy) {

        log.info("Updating plan strategyFineline Metrics for : {}", strategyFinelineId.toString());
        //Fineline level changes
        if (fineline.getStrategy() != null) {
            updateWeatherCluster(fineline, lvl0Nbr, lvl1Nbr, lvl2Nbr,
                    lvl3Nbr, lvl4Nbr, planClusterStrategies);
            strategyFineline.setChannelId(ChannelType.getChannelIdFromName(fineline.getChannel()));
            strategyFineline.setTraitChoiceCode(TraitChoiceType.getTraitChoiceCodeFromName(fineline.getTraitChoice()));
            strategyFineline.setOutFitting(Optional.ofNullable(fineline.getOutFitting()).orElse(null));
            strategyFineline.setFinelineDesc(fineline.getFinelineName());
            strategyFineline.setAltFinelineName(Optional.ofNullable(fineline.getAltFinelineName()).orElse(null));
            strategyFineline.setBrands(!CollectionUtils.isEmpty(fineline.getBrands()) ? commonMethods.getBrandAttributeString(fineline.getBrands()) : null);

            ProductDimensions productDimensions = Optional.ofNullable(fineline.getProductDimensions()).orElse(null);

            //Set the prod Dimensions if it has been changed
            if (((productDimensions == null || productDimensions.getProductDimId() == null) && strategyFineline.getProductDimId() != null) ||
                    ((productDimensions != null && productDimensions.getProductDimId() != null) && !productDimensions.getProductDimId().equals(strategyFineline.getProductDimId()))) {
                strategyFineline.setProductDimId(productDimensions == null ? null : productDimensions.getProductDimId());
                allocationStrategy.setHasProdDimChanged(true);
                allocationStrategy.setLvl0Nbr(lvl0Nbr);
                allocationStrategy.setLvl1Nbr(lvl1Nbr);
                allocationStrategy.setLvl2Nbr(lvl2Nbr);
                allocationStrategy.setLvl3Nbr(lvl3Nbr);
                allocationStrategy.setLvl4Nbr(lvl4Nbr);
                allocationStrategy.setFinelineNbr(fineline.getFinelineNbr());
                allocationStrategy.setFinelineName(fineline.getFinelineName());
                allocationStrategy.setProdDimensionId(strategyFineline.getProductDimId());
            }
        }
        if (!CollectionUtils.isEmpty(fineline.getStyles())) {
            for (Style style : fineline.getStyles()) {
                updatePlanStrategyStyle(strategyFineline.getStrategyStyles(), style, strategyFinelineId);
            }
        }
    }

    private void updateWeatherCluster(Fineline fineline,
                                      Integer lvl0Nbr, Integer lvl1Nbr, Integer lvl2Nbr,
                                      Integer lvl3Nbr, Integer lvl4Nbr, Set<PlanClusterStrategy> planClusterStrategies) {
        Optional.ofNullable(fineline.getStrategy())
                .map(Strategy::getWeatherClusters)
                .stream()
                .flatMap(Collection::stream)
                .filter(weatherCluster -> weatherCluster.getType().getAnalyticsClusterId().equals(0))
                .findFirst()
                .ifPresent(weatherCluster -> updatePlanClusterStrategy(weatherCluster, fineline, lvl0Nbr,
                        lvl1Nbr, lvl2Nbr, lvl3Nbr, lvl4Nbr, planClusterStrategies));
    }

    private void updatePlanClusterStrategy(WeatherCluster weatherCluster, Fineline fineline, Integer lvl0Nbr,
                                           Integer lvl1Nbr, Integer lvl2Nbr, Integer lvl3Nbr, Integer lvl4Nbr,
                                           Set<PlanClusterStrategy> planClusterStrategies) {
        if (weatherCluster.getType().getAnalyticsClusterId().equals(0) && CommonUtil.getInStoreYrWkDesc(weatherCluster) != null) {
            Optional.ofNullable(planClusterStrategies)
                    .stream()
                    .flatMap(Collection::stream)
                    .forEach(planClusterStrategy -> updatePlanClusterEligRankingMetrics(planClusterStrategy, weatherCluster, fineline, lvl0Nbr, lvl1Nbr,
                            lvl2Nbr, lvl3Nbr, lvl4Nbr));
        }

    }

    private void updatePlanClusterEligRankingMetrics(PlanClusterStrategy planClusterStrategy, WeatherCluster weatherCluster,
                                                     Fineline fineline, Integer lvl0Nbr, Integer lvl1Nbr, Integer lvl2Nbr, Integer lvl3Nbr, Integer lvl4Nbr) {
        StrategyFlClusEligRankingId strategyFlClusEligRankingId = new StrategyFlClusEligRankingId(planClusterStrategy.getPlanClusterStrategyId(), lvl0Nbr,
                lvl1Nbr, lvl2Nbr, lvl3Nbr, lvl4Nbr, fineline.getFinelineNbr());

        Optional.of(planClusterStrategy)
                .map(PlanClusterStrategy::getStrategyFlClusEligRankings)
                .stream()
                .flatMap(Collection::stream)
                .filter(strategyFlClusEligRanking -> strategyFlClusEligRanking.getStrategyFlClusEligRankingId().equals(strategyFlClusEligRankingId))
                .findFirst()
                .ifPresent(strategyFlClusEligRanking -> updateStrategyFlClusEligRanking(strategyFlClusEligRanking, weatherCluster, fineline.getStyles()));
    }

    private void updateStrategyFlClusEligRanking(StrategyFlClusEligRanking strategyFlClusEligRanking, WeatherCluster weatherCluster, List<Style> styles) {
        log.info("Updating StrategyFlClusEligRanking Metrics for : {}", strategyFlClusEligRanking.getStrategyFlClusEligRankingId().toString());
        if (weatherCluster.getInStoreDisabledInLP() != null && !weatherCluster.getInStoreDisabledInLP()) {
            strategyFlClusEligRanking.setInStoreYrWkDesc(CommonUtil.getInStoreYrWkDesc(weatherCluster));
            strategyFlClusEligRanking.setInStoreYrWk(CommonUtil.getInStoreYrWk(weatherCluster));
        }
        if (weatherCluster.getMarkDownDisabledInLP() != null && !weatherCluster.getMarkDownDisabledInLP()) {
            strategyFlClusEligRanking.setMarkDownYrWk(CommonUtil.getMarkdownYrWk(weatherCluster));
            strategyFlClusEligRanking.setMarkDownYrWkDesc(CommonUtil.getMarkdownYrWkDesc(weatherCluster));
        }
        //Update if we have entries for fineline tied to Programs
        Set<StrategyFlClusPrgmEligRanking> programsFls = Optional.ofNullable(strategyFlClusEligRanking.getStrategyFlClusPrgmEligRankings())
                .orElse(null);
        updateStrategyFLProgClus(programsFls, weatherCluster);
        //Roll Down to Style & Program associated Styles
        if (!CollectionUtils.isEmpty(styles)) {
            for (Style style : styles) {
                if (!CollectionUtils.isEmpty(style.getCustomerChoices())) {
                    for (CustomerChoice cc : style.getCustomerChoices()) {
                        Optional.ofNullable(cc.getStrategy())
                                .map(Strategy::getWeatherClusters)
                                .stream().flatMap(Collection::stream)
                                .filter(weatherClusterCc -> weatherClusterCc.getType().getAnalyticsClusterId().equals(0))
                                .findFirst().ifPresent(weatherClusterCc -> setStrategyAndProgramCcs(weatherClusterCc,
                                strategyFlClusEligRanking, programsFls, style, cc));
                    }
                }
            }
        }

    }

    private void setStrategyAndProgramCcs(WeatherCluster weatherClusterCc, StrategyFlClusEligRanking strategyFlClusEligRanking,
                                          Set<StrategyFlClusPrgmEligRanking> programsFls, Style style, CustomerChoice cc) {
        if (weatherClusterCc.getType().getAnalyticsClusterId().equals(0) && CommonUtil.getInStoreYrWkDesc(weatherClusterCc) != null) {
            Optional.ofNullable(strategyFlClusEligRanking.getStrategyStyleCluses())
                    .stream()
                    .flatMap(Collection::stream)
                    .filter(strategyStyleClus -> strategyStyleClus.getStrategyStyleClusId().getStyleNbr().equals(style.getStyleNbr()))
                    .findFirst().flatMap(strategyStyleClus -> Optional.ofNullable(strategyStyleClus.getStrategyCcClusEligRankings())
                    .stream()
                    .flatMap(Collection::stream)
                    .filter(strategyCcClus -> strategyCcClus.getStrategyCcClusEligRankingId().getCcId().equals(cc.getCcId()))
                    .findFirst()).ifPresent(strategyCcClusEligRanking -> {
                if (weatherClusterCc.getInStoreDisabledInLP() != null && !weatherClusterCc.getInStoreDisabledInLP()) {
                    strategyCcClusEligRanking.setInStoreYrWkDesc(CommonUtil.getInStoreYrWkDesc(weatherClusterCc));
                    strategyCcClusEligRanking.setInStoreYrWk(CommonUtil.getInStoreYrWk(weatherClusterCc));
                }
                if (weatherClusterCc.getMarkDownDisabledInLP() != null && !weatherClusterCc.getMarkDownDisabledInLP()) {
                    strategyCcClusEligRanking.setMarkDownYrWk(CommonUtil.getMarkdownYrWk(weatherClusterCc));
                    strategyCcClusEligRanking.setMarkDownYrWkDesc(CommonUtil.getMarkdownYrWkDesc(weatherClusterCc));
                }
            });
            updateStyleProgClus(programsFls, style, weatherClusterCc, cc);
        }
    }

    private void updateStyleProgClus(Set<StrategyFlClusPrgmEligRanking> programsFls, Style style, WeatherCluster weatherClusterCc, CustomerChoice cc) {
        if (programsFls != null && !CollectionUtils.isEmpty(programsFls)) {
            programsFls.forEach(fineline -> Optional.ofNullable(fineline.getEligStyleClusProgs())
                    .stream()
                    .flatMap(Collection::stream)
                    .filter(strategyStyleClus -> strategyStyleClus.getEligStyleClusProgId().getStyleNbr().equals(style.getStyleNbr()))
                    .findFirst().flatMap(strategyStyleClus -> Optional.ofNullable(strategyStyleClus.getEligCcClusProgs())
                            .stream()
                            .flatMap(Collection::stream)
                            .filter(strategyCcClus -> strategyCcClus.getEligCcClusProgId().getCcId().equals(cc.getCcId()))
                            .findFirst()).ifPresent(strategyCcClusEligRanking -> {
                        if (weatherClusterCc.getInStoreDisabledInLP() != null && !weatherClusterCc.getInStoreDisabledInLP()) {
                            strategyCcClusEligRanking.setInStoreYrWkDesc(CommonUtil.getInStoreYrWkDesc(weatherClusterCc));
                            strategyCcClusEligRanking.setInStoreYrWk(CommonUtil.getInStoreYrWk(weatherClusterCc));
                        }
                        if (weatherClusterCc.getMarkDownDisabledInLP() != null && !weatherClusterCc.getMarkDownDisabledInLP()) {
                            strategyCcClusEligRanking.setMarkDownYrWk(CommonUtil.getMarkdownYrWk(weatherClusterCc));
                            strategyCcClusEligRanking.setMarkDownYrWkDesc(CommonUtil.getMarkdownYrWkDesc(weatherClusterCc));
                        }
                    }));
        }

    }

    private void updateStrategyFLProgClus(Set<StrategyFlClusPrgmEligRanking> programsFls, WeatherCluster weatherCluster) {
        if (programsFls != null && !CollectionUtils.isEmpty(programsFls)) {
            for (StrategyFlClusPrgmEligRanking programFl : programsFls) {
                if (weatherCluster.getInStoreDisabledInLP() != null && !weatherCluster.getInStoreDisabledInLP()) {
                    programFl.setInStoreYrWkDesc(CommonUtil.getInStoreYrWkDesc(weatherCluster));
                    programFl.setInStoreYrWk(CommonUtil.getInStoreYrWk(weatherCluster));
                }
                if (weatherCluster.getMarkDownDisabledInLP() != null && !weatherCluster.getMarkDownDisabledInLP()) {
                    programFl.setMarkDownYrWk(CommonUtil.getMarkdownYrWk(weatherCluster));
                    programFl.setMarkDownYrWkDesc(CommonUtil.getMarkdownYrWkDesc(weatherCluster));
                }
            }
        }

    }

    private void updatePlanStrategyStyle(Set<StrategyStyle> strategyStyles, Style style, StrategyFinelineId strategyFinelineId) {
        StrategyStyleId strategyStyleId = new StrategyStyleId(strategyFinelineId, style.getStyleNbr());
        Optional.ofNullable(strategyStyles)
                .stream()
                .flatMap(Collection::stream)
                .filter(strategyStyle -> strategyStyle.getStrategyStyleId().equals(strategyStyleId))
                .findFirst()
                .ifPresent(strategyStyle -> setPlanStrategyStyle(strategyStyle, strategyStyleId, style));
    }

    private void setPlanStrategyStyle(StrategyStyle strategyStyle, StrategyStyleId strategyStyleId, Style style) {
        log.info("Updating plan strategyStyle Metrics for : {}", strategyStyleId.toString());
        strategyStyle.setChannelId(ChannelType.getChannelIdFromName(style.getChannel()));
        strategyStyle.setAltStyleDesc(Optional.ofNullable(style.getAltStyleDesc()).orElse(null));
        if (!CollectionUtils.isEmpty(style.getCustomerChoices())) {
            for (CustomerChoice cc : style.getCustomerChoices()) {
                updatePlanStrategyCc(strategyStyle.getStrategyCcs(), cc, strategyStyleId);
            }
        }
    }

    private void updatePlanStrategyCc(Set<StrategyCc> strategyCcs, CustomerChoice cc, StrategyStyleId strategyStyleId) {
        StrategyCcId strategyCcId = new StrategyCcId(strategyStyleId, cc.getCcId());
        Optional.ofNullable(strategyCcs)
                .stream()
                .flatMap(Collection::stream)
                .filter(strategyCc -> strategyCc.getStrategyCcId().equals(strategyCcId))
                .findFirst()
                .ifPresent(strategyCc -> {
                    log.info("Updating plan strategyCc Metrics for : {}", strategyCcId.toString());
                    strategyCc.setChannelId(ChannelType.getChannelIdFromName(cc.getChannel()));
                    strategyCc.setColorName(Optional.ofNullable(cc.getColorName()).orElse(null));
                    strategyCc.setAltCcDesc(Optional.ofNullable(cc.getAltCcDesc()).orElse(null));
                    strategyCc.setColorFamily(cc.getColorFamily());
                });
    }

}
