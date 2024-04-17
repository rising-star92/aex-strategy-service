package com.walmart.aex.strategy.service;

import com.walmart.aex.strategy.dto.request.*;
import com.walmart.aex.strategy.entity.*;
import com.walmart.aex.strategy.enums.IncludeOffshoreMkt;
import com.walmart.aex.strategy.exception.CustomException;
import com.walmart.aex.strategy.properties.AppProperties;
import com.walmart.aex.strategy.repository.StrategyCcClusEligRankingRepository;
import com.walmart.aex.strategy.util.CommonUtil;
import io.strati.ccm.utils.client.annotation.ManagedConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManager;
import java.util.*;
import java.util.stream.Collectors;

import static com.walmart.aex.strategy.service.ClusterEligibilityFlService.IN_STORE_DATE;
import static com.walmart.aex.strategy.service.ClusterEligibilityFlService.MARK_DOWN_DATE;

@Service
@Slf4j
public class ClusterEligibilityCcService {

    @Autowired
    private EntityManager entityManager;

    @ManagedConfiguration
    private AppProperties appProperties;

    public static final String IS_ELIGIBLE = "isEligible";
    private final StrategyCcClusEligRankingRepository strategyCcClusEligRankingRepository;

    public ClusterEligibilityCcService(StrategyCcClusEligRankingRepository strategyCcClusEligRankingRepository) {
        this.strategyCcClusEligRankingRepository = strategyCcClusEligRankingRepository;
    }

    public void updateClusterEligibilityMetricsAtStyleCc(Set<StrategyFlClusEligRanking> strategyFlClusEligRankings, List<Style> styles, PlanStrategyId planStrategyId, Integer finelineNbr, Set<String> updatedField) {
        for (Style style : styles) {
            if (!CollectionUtils.isEmpty(style.getCustomerChoices())) {
                for (CustomerChoice customerChoice : style.getCustomerChoices()) {
                    Map<String, String> commonCcUpdatedFields = Optional.ofNullable(customerChoice.getUpdatedFields())
                            .map(UpdatedFields::getWeatherCluster)
                            .map(CommonUtil::getUpdatedFieldsMap)
                            .orElse(null);
                    if (commonCcUpdatedFields != null) {
                        log.info("Updating the cc :{} level field for: {}", customerChoice.getCcId(), StringUtils.join(commonCcUpdatedFields));
                        if (commonCcUpdatedFields.containsKey(IN_STORE_DATE)) updatedField.add(IN_STORE_DATE);
                        if (commonCcUpdatedFields.containsKey(MARK_DOWN_DATE)) updatedField.add(MARK_DOWN_DATE);
                        List<StrategyCcClusEligRanking> strategyCcClusEligRankings =
                                strategyCcClusEligRankingRepository.findStrategyCcClusEligRankingByStrategyCcClusEligRankingId_StrategyStyleClusId_StrategyFlClusEligRankingId_PlanClusterStrategyId_PlanStrategyIdAndStrategyCcClusEligRankingId_StrategyStyleClusId_StrategyFlClusEligRankingId_finelineNbrAndStrategyCcClusEligRankingId_StrategyStyleClusId_styleNbrAndStrategyCcClusEligRankingId_ccId
                                        (planStrategyId, finelineNbr, style.getStyleNbr(), customerChoice.getCcId())
                                        .orElseThrow(() -> new CustomException("Invalid Style Cc - " + customerChoice.getCcId()));
                        updateCcMetrics(strategyFlClusEligRankings, commonCcUpdatedFields, strategyCcClusEligRankings, customerChoice,
                                planStrategyId);
                    }
                }
            }
        }
    }
    public void updateCcMetrics(Set<StrategyFlClusEligRanking> strategyFlClusEligRankings, Map<String, String> commonCcUpdatedFields,
                                List<StrategyCcClusEligRanking> strategyCcClusEligRankings, CustomerChoice customerChoice, PlanStrategyId planStrategyId) {
        WeatherCluster weatherClusterCc = CommonUtil.getWeatherClusterCc(customerChoice);
        Integer analyticsClusterId = CommonUtil.getAnalyticsClusterId(weatherClusterCc);
        //Set Attributes at cluster All level
        if (analyticsClusterId.equals(0)) {
            setMetricsForClusterAllCc(commonCcUpdatedFields, strategyCcClusEligRankings, weatherClusterCc, strategyFlClusEligRankings);
        } else {
            setMetricsForClusterNCc(commonCcUpdatedFields, strategyCcClusEligRankings, weatherClusterCc,
                    analyticsClusterId, planStrategyId, strategyFlClusEligRankings);
        }

    }

    private void setMetricsForClusterNCc(Map<String, String> commonCcUpdatedFields, List<StrategyCcClusEligRanking> strategyCcClusEligRankings,
                                         WeatherCluster weatherClusterCc, Integer analyticsClusterId, PlanStrategyId planStrategyId,
                                         Set<StrategyFlClusEligRanking> strategyFlClusEligRankings) {
        //Set isEligibility for N clusterType
        if (commonCcUpdatedFields.containsKey(IS_ELIGIBLE)) {
            Integer isEligibleFlag = Optional.ofNullable(weatherClusterCc)
                    .map(WeatherCluster::getIsEligible)
                    .map(flag -> Boolean.TRUE.equals(flag) ? 1 : 0)
                    .orElse(null);
            setIsEligibleForClusterNCc(strategyCcClusEligRankings, planStrategyId, isEligibleFlag, analyticsClusterId, strategyFlClusEligRankings);
        }
        //inStore Date at Cluster N
        if (commonCcUpdatedFields.containsKey("inStoreDate")) {
            setInStoreDateForClusterNCc(strategyCcClusEligRankings, analyticsClusterId, planStrategyId, weatherClusterCc);
        }
        //MarkDown Date at Cluster N
        if (commonCcUpdatedFields.containsKey("markDownDate")) {
            setMarkDownDateForClusterNCc(strategyCcClusEligRankings, analyticsClusterId, planStrategyId, weatherClusterCc);
        }
        //exclude offshore Selection at Cluster N
        if (commonCcUpdatedFields.containsKey("includeOffshore")) {
            setExcludeOffshoreForClusterNCc(strategyCcClusEligRankings, analyticsClusterId, planStrategyId, weatherClusterCc);
        }
        //Rank at Cluster N
        if (commonCcUpdatedFields.containsKey("ranking")) {
            setRankingForClusterNCc(strategyCcClusEligRankings, analyticsClusterId, planStrategyId, weatherClusterCc);
        }
    }

    public void setMetricsForClusterAllCc(Map<String, String> commonCcUpdatedFields, List<StrategyCcClusEligRanking> strategyCcClusEligRankings,
                                           WeatherCluster weatherClusterCc, Set<StrategyFlClusEligRanking> strategyFlClusEligRankings) {

        //Set isEligibility for all clusterType
        if (commonCcUpdatedFields.containsKey(IS_ELIGIBLE)) {
            Integer isEligibleFlag = Optional.ofNullable(weatherClusterCc)
                    .map(WeatherCluster::getIsEligible)
                    .map(flag -> Boolean.TRUE.equals(flag) ? 1 : 0)
                    .orElse(null);
            setIsEligibileForAllCc(strategyCcClusEligRankings, isEligibleFlag, strategyFlClusEligRankings);
        }

        //inStore Date at all clusterType
        if (commonCcUpdatedFields.containsKey("inStoreDate")) {
            setInStoreDateForAllCc(strategyCcClusEligRankings, weatherClusterCc);
        }
        //MarkDown Date all clusterType
        if (commonCcUpdatedFields.containsKey("markDownDate")) {
            setMarkDownDateForAllCc(strategyCcClusEligRankings, weatherClusterCc);
        }
        //Rank at all Cluster type
        if (commonCcUpdatedFields.containsKey("ranking")) {
            setRankingForAllCc(strategyCcClusEligRankings, weatherClusterCc);
        }
    }

    public void setRankingForAllCc(List<StrategyCcClusEligRanking> strategyCcClusEligRankings, WeatherCluster weatherClusterCc) {
        Optional.ofNullable(strategyCcClusEligRankings)
                .stream()
                .flatMap(Collection::stream)
                .forEach(strategyCcClusEligRanking -> strategyCcClusEligRanking.setMerchantOverrideRank(weatherClusterCc.getRanking()));
    }

    public void setMarkDownDateForAllCc(List<StrategyCcClusEligRanking> strategyCcClusEligRankings, WeatherCluster weatherClusterCc) {
        Integer markDownWk = CommonUtil.getMarkdownYrWk(weatherClusterCc);
        String markDownDesc = CommonUtil.getMarkdownYrWkDesc(weatherClusterCc);
        Optional.ofNullable(strategyCcClusEligRankings)
                .stream()
                .flatMap(Collection::stream)
                .forEach(strategyCcClusEligRanking -> {
                    strategyCcClusEligRanking.setMarkDownYrWkDesc(markDownDesc);
                    strategyCcClusEligRanking.setMarkDownYrWk(markDownWk);
                });
    }

    public void setInStoreDateForAllCc(List<StrategyCcClusEligRanking> strategyCcClusEligRankings, WeatherCluster weatherClusterCc) {
        Integer inStoreWk = CommonUtil.getInStoreYrWk(weatherClusterCc);
        String inStoreDesc = CommonUtil.getInStoreYrWkDesc(weatherClusterCc);
        Optional.ofNullable(strategyCcClusEligRankings)
                .stream()
                .flatMap(Collection::stream)
                .forEach(strategyCcClusEligRanking -> {
                    strategyCcClusEligRanking.setInStoreYrWkDesc(inStoreDesc);
                    strategyCcClusEligRanking.setInStoreYrWk(inStoreWk);
                });
    }

    private void setExcludeOffshoreForClusterNCc(List<StrategyCcClusEligRanking> strategyCcClusEligRankings, Integer analyticsClusterId,
                                                 PlanStrategyId planStrategyId, WeatherCluster weatherClusterCc) {
        PlanClusterStrategyId planClusterStrategyId = new PlanClusterStrategyId(planStrategyId, analyticsClusterId);
        List<Integer> offshoreExclude = Optional.ofNullable(weatherClusterCc)
                .map(WeatherCluster::getIncludeOffshore)
                .stream()
                .flatMap(Collection::stream)
                .map(IncludeOffshoreMkt::getMarketSelectCode)
                .collect(Collectors.toList());
        Optional.ofNullable(strategyCcClusEligRankings)
                .stream()
                .flatMap(Collection::stream)
                .filter(strategyCcClusEligRanking -> strategyCcClusEligRanking.getStrategyCcClusEligRankingId().getStrategyStyleClusId()
                        .getStrategyFlClusEligRankingId().getPlanClusterStrategyId()
                        .equals(planClusterStrategyId))
                .findFirst()
                .ifPresent(strategyCcClusEligRanking -> setExcludeOffshoreMktCc(strategyCcClusEligRanking, offshoreExclude));
    }

    private void setExcludeOffshoreMktCc(StrategyCcClusEligRanking strategyCcClusEligRanking, List<Integer> offshoreExclude) {
        Optional.ofNullable(strategyCcClusEligRanking)
                .map(StrategyCcClusEligRanking::getStrategyCcMktClusEligs)
                .ifPresent(strategyCcMktClusEligs -> setExcludeOffshoreMktIfExistsCc(strategyCcClusEligRanking, strategyCcMktClusEligs, offshoreExclude));
    }

    private void setExcludeOffshoreMktIfExistsCc(StrategyCcClusEligRanking strategyCcClusEligRanking, Set<StrategyCcMktClusElig> strategyCcMktClusEligs,
                                                 List<Integer> offshoreExclude) {
        strategyCcMktClusEligs.clear();
        entityManager.flush();
        if (!CollectionUtils.isEmpty(offshoreExclude)) {
            for (Integer offshoreMkt : offshoreExclude) {
                StrategyCcMktClusElig strategyCcMktClusElig = new StrategyCcMktClusElig();
                StrategyCcMktClusEligId strategyCcMktClusEligId = new StrategyCcMktClusEligId(strategyCcClusEligRanking.getStrategyCcClusEligRankingId(), offshoreMkt);
                strategyCcMktClusElig.setStrategyCcMktClusEligId(strategyCcMktClusEligId);
                strategyCcMktClusElig.setStrategyCcClusEligRanking(strategyCcClusEligRanking);
                strategyCcClusEligRanking.getStrategyCcMktClusEligs().add(strategyCcMktClusElig);
            }

        }
    }

    private void setRankingForClusterNCc(List<StrategyCcClusEligRanking> strategyCcClusEligRankings, Integer analyticsClusterId,
                                         PlanStrategyId planStrategyId, WeatherCluster weatherClusterCc) {
        PlanClusterStrategyId planClusterStrategyId = new PlanClusterStrategyId(planStrategyId, analyticsClusterId);

        Optional.ofNullable(strategyCcClusEligRankings)
                .stream()
                .flatMap(Collection::stream)
                .filter(strategyCcClusEligRanking -> strategyCcClusEligRanking.getStrategyCcClusEligRankingId().getStrategyStyleClusId()
                        .getStrategyFlClusEligRankingId().getPlanClusterStrategyId()
                        .equals(planClusterStrategyId))
                .findFirst()
                .ifPresent(strategyCcClusEligRanking -> strategyCcClusEligRanking.setMerchantOverrideRank(weatherClusterCc.getRanking()));
    }

    private void setMarkDownDateForClusterNCc(List<StrategyCcClusEligRanking> strategyCcClusEligRankings, Integer analyticsClusterId,
                                              PlanStrategyId planStrategyId, WeatherCluster weatherClusterCc) {
        PlanClusterStrategyId planClusterStrategyId = new PlanClusterStrategyId(planStrategyId, analyticsClusterId);
        Integer markDownWk = CommonUtil.getMarkdownYrWk(weatherClusterCc);
        String markDownDesc = CommonUtil.getMarkdownYrWkDesc(weatherClusterCc);
        Optional.ofNullable(strategyCcClusEligRankings)
                .stream()
                .flatMap(Collection::stream)
                .filter(strategyCcClusEligRanking -> strategyCcClusEligRanking.getStrategyCcClusEligRankingId().getStrategyStyleClusId()
                        .getStrategyFlClusEligRankingId().getPlanClusterStrategyId()
                        .equals(planClusterStrategyId))
                .findFirst()
                .ifPresent(strategyCcClusEligRanking -> {
                    strategyCcClusEligRanking.setMarkDownYrWk(markDownWk);
                    strategyCcClusEligRanking.setMarkDownYrWkDesc(markDownDesc);
                });
    }

    public void setInStoreDateForClusterNCc(List<StrategyCcClusEligRanking> strategyCcClusEligRankings, Integer analyticsClusterId,
                                            PlanStrategyId planStrategyId, WeatherCluster weatherClusterCc) {
        PlanClusterStrategyId planClusterStrategyId = new PlanClusterStrategyId(planStrategyId, analyticsClusterId);
        Integer inStoreYrWk = CommonUtil.getInStoreYrWk(weatherClusterCc);
        String inStoreDesc = CommonUtil.getInStoreYrWkDesc(weatherClusterCc);

        Optional.ofNullable(strategyCcClusEligRankings)
                .stream()
                .flatMap(Collection::stream)
                .filter(strategyCcClusEligRanking -> strategyCcClusEligRanking.getStrategyCcClusEligRankingId().getStrategyStyleClusId()
                        .getStrategyFlClusEligRankingId().getPlanClusterStrategyId()
                        .equals(planClusterStrategyId))
                .findFirst()
                .ifPresent(strategyCcClusEligRanking -> {
                    strategyCcClusEligRanking.setInStoreYrWk(inStoreYrWk);
                    strategyCcClusEligRanking.setInStoreYrWkDesc(inStoreDesc);
                });
    }

    private void setIsEligibleForClusterNCc(List<StrategyCcClusEligRanking> strategyCcClusEligRankings, PlanStrategyId planStrategyId,
                                            Integer isEligibleFlag, Integer analyticsClusterId,
                                            Set<StrategyFlClusEligRanking> strategyFlClusEligRankings) {
        PlanClusterStrategyId planClusterStrategyId = new PlanClusterStrategyId(planStrategyId, analyticsClusterId);
        Optional.ofNullable(strategyCcClusEligRankings)
                .stream()
                .flatMap(Collection::stream)
                .filter(strategyCcClusEligRanking -> strategyCcClusEligRanking.getStrategyCcClusEligRankingId()
                        .getStrategyStyleClusId().getStrategyFlClusEligRankingId()
                        .getPlanClusterStrategyId().equals(planClusterStrategyId))
                .findFirst()
                .ifPresent(strategyCcClusEligRanking -> strategyCcClusEligRanking.setIsEligibleFlag(isEligibleFlag));
        //will check the status of all clusters eligible flag and based on all is updated
        validateAndUpdateAllFlagAtCc(strategyCcClusEligRankings, planStrategyId);
    }

    private void validateAndUpdateAllFlagAtCc(List<StrategyCcClusEligRanking> strategyCcClusEligRankings, PlanStrategyId planStrategyId) {
        PlanClusterStrategyId allPlanClusterStrategyId = new PlanClusterStrategyId(planStrategyId, 0);
        Integer selectStatusId;
        Set<Integer> flagsPar = getIsEligibleFlagsPartialAtCcClusters(strategyCcClusEligRankings, allPlanClusterStrategyId);
        if (flagsPar.size() == 1 && flagsPar.contains(1)) {
            selectStatusId=1;
        } else if (flagsPar.size() > 1) {
            selectStatusId=2;
        } else {
            selectStatusId=0;
        }
        Optional.ofNullable(strategyCcClusEligRankings)
                .stream()
                .flatMap(Collection::stream)
                .filter(strategyCcClusEligRanking -> strategyCcClusEligRanking.getStrategyCcClusEligRankingId().getStrategyStyleClusId()
                        .getStrategyFlClusEligRankingId().getPlanClusterStrategyId()
                        .equals(allPlanClusterStrategyId))
                .findFirst()
                .ifPresent(strategyCcClusEligRanking -> strategyCcClusEligRanking.setIsEligibleFlag(selectStatusId));
    }

    private Set<Integer> getIsEligibleFlagsPartialAtCcClusters(List<StrategyCcClusEligRanking> strategyCcClusEligRankings, PlanClusterStrategyId allPlanClusterStrategyId) {
        return Optional.ofNullable(strategyCcClusEligRankings)
                .stream()
                .flatMap(Collection::stream)
                .filter(strategyCcClusEligRanking -> !strategyCcClusEligRanking.getStrategyCcClusEligRankingId().getStrategyStyleClusId()
                        .getStrategyFlClusEligRankingId().getPlanClusterStrategyId().equals(allPlanClusterStrategyId))
                .map(StrategyCcClusEligRanking::getIsEligibleFlag).collect(Collectors.toSet());
    }

    private void setIsEligibileForAllCc(List<StrategyCcClusEligRanking> strategyCcClusEligRankings, Integer isEligibleFlag,
                                        Set<StrategyFlClusEligRanking> strategyFlClusEligRankings) {
        Optional.ofNullable(strategyCcClusEligRankings)
                .stream()
                .flatMap(Collection::stream)
                .forEach(strategyCcClusEligRanking -> strategyCcClusEligRanking.setIsEligibleFlag(isEligibleFlag));

    }


}
