package com.walmart.aex.strategy.service;

import com.walmart.aex.strategy.dto.AnalyticsClusterStoreDTO;
import com.walmart.aex.strategy.dto.request.*;
import com.walmart.aex.strategy.entity.*;
import com.walmart.aex.strategy.enums.IncludeOffshoreMkt;
import com.walmart.aex.strategy.exception.CustomException;
import com.walmart.aex.strategy.properties.AppProperties;
import com.walmart.aex.strategy.repository.StrategyFlClusEligRankingRepository;
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

@Service
@Slf4j
public class ClusterEligibilityFlService {

    public static final String IS_ELIGIBLE = "isEligible";
    public static final String IN_STORE_DATE = "inStoreDate";
    public static final String MARK_DOWN_DATE = "markDownDate";
    private final StrategyFlClusEligRankingRepository strategyFlClusEligRankingRepository;
    private final ClusterEligibilityCcService clusterEligibilityCcService;
    private final AnalyticsClusterStoreCountService analyticsClusterStoreCountService;

    @ManagedConfiguration
    private AppProperties appProperties;

    @Autowired
    private EntityManager entityManager;

    public ClusterEligibilityFlService(StrategyFlClusEligRankingRepository strategyFlClusEligRankingRepository,
                                       ClusterEligibilityCcService clusterEligibilityCcService,
                                       AnalyticsClusterStoreCountService analyticsClusterStoreCountService) {
        this.strategyFlClusEligRankingRepository = strategyFlClusEligRankingRepository;
        this.clusterEligibilityCcService = clusterEligibilityCcService;
        this.analyticsClusterStoreCountService = analyticsClusterStoreCountService;
    }

    public Set<StrategyFlClusEligRanking> updateClusterEligibilityMetrics(Fineline fineline, PlanStrategyId planStrategyId,
                                                                          Integer lvl3Nbr, Integer lvl4Nbr, Set<String> updatedField) {
        Set<StrategyFlClusEligRanking> strategyFlClusEligRankings =
                strategyFlClusEligRankingRepository.findStrategyFlClusEligRankingByStrategyFlClusEligRankingId_PlanClusterStrategyId_PlanStrategyIdAndStrategyFlClusEligRankingId_lvl3NbrAndStrategyFlClusEligRankingId_lvl4NbrAndStrategyFlClusEligRankingId_finelineNbr(
                        planStrategyId, lvl3Nbr, lvl4Nbr, fineline.getFinelineNbr())
                        .orElseThrow(() -> new CustomException(String.format("Unable to find a fineline :%s in planId :%s & StategyId:%s"
                                , fineline.getFinelineNbr(), planStrategyId.getPlanId(), planStrategyId.getStrategyId())));

        Optional.ofNullable(fineline.getUpdatedFields())
                .map(UpdatedFields::getWeatherCluster)
                .map(CommonUtil::getUpdatedFieldsMap)
                .ifPresent(weatherClusterFlUpdatedFields -> updateFlMetrics(fineline, planStrategyId, weatherClusterFlUpdatedFields, strategyFlClusEligRankings));
        //Check if we have CC level UpdateFields
        if (fineline.getStyles() != null) {
            clusterEligibilityCcService.updateClusterEligibilityMetricsAtStyleCc(strategyFlClusEligRankings, fineline.getStyles(),
                    planStrategyId, fineline.getFinelineNbr(), updatedField);
        }
        return strategyFlClusEligRankings;
    }

    public void updateFlMetrics(Fineline fineline, PlanStrategyId planStrategyId,
                                Map<String, String> commonUpdatedFields, Set<StrategyFlClusEligRanking> strategyFlClusEligRankings) {
        log.info("Updating the fineline :{} for field & value: {}", fineline.getFinelineNbr(), StringUtils.join(commonUpdatedFields));
        WeatherCluster weatherClusterFineline = CommonUtil.getWeatherClusterFineline(fineline);
        Integer analyticsClusterId = CommonUtil.getAnalyticsClusterId(weatherClusterFineline);
        //Set Attributes at cluster All level
        if (analyticsClusterId.equals(0)) {
            setMetricsForClusterAll(commonUpdatedFields, strategyFlClusEligRankings, weatherClusterFineline);
        } else {
            setMetricsForClusterN(commonUpdatedFields, strategyFlClusEligRankings, weatherClusterFineline, analyticsClusterId, planStrategyId);
        }
    }

    private void setMetricsForClusterN(Map<String, String> commonUpdatedFields, Set<StrategyFlClusEligRanking> strategyFlClusEligRankings,
                                       WeatherCluster weatherClusterFineline, Integer analyticsClusterId, PlanStrategyId planStrategyId) {
        //Set isEligibility for N clusterType
        if (commonUpdatedFields.containsKey(IS_ELIGIBLE)) {
            Integer isEligibleFlag = Optional.ofNullable(weatherClusterFineline)
                    .map(WeatherCluster::getIsEligible)
                    .map(flag -> Boolean.TRUE.equals(flag) ? 1 : 0)
                    .orElse(null);
            setIsEligibleForClusterN(strategyFlClusEligRankings, planStrategyId, isEligibleFlag, analyticsClusterId);
        }
        //inStore Date at Cluster N
        if (commonUpdatedFields.containsKey(IN_STORE_DATE)) {
            setInStoreDateForClusterN(strategyFlClusEligRankings, analyticsClusterId, planStrategyId, weatherClusterFineline);
        }
        //MarkDown Date at Cluster N
        if (commonUpdatedFields.containsKey(MARK_DOWN_DATE)) {
            setMarkDownDateForClusterN(strategyFlClusEligRankings, analyticsClusterId, planStrategyId, weatherClusterFineline);
        }
        //exclude offshore Selection at Cluster N
        if (commonUpdatedFields.containsKey("includeOffshore")) {
            setExcludeOffshoreForClusterN(strategyFlClusEligRankings, analyticsClusterId, planStrategyId, weatherClusterFineline);
        }
        //Rank at Cluster N
        if (commonUpdatedFields.containsKey("ranking")) {
            setRankingForClusterN(strategyFlClusEligRankings, analyticsClusterId, planStrategyId, weatherClusterFineline);
        }

    }

    private void setMetricsForClusterAll(Map<String, String> commonUpdatedFields, Set<StrategyFlClusEligRanking> strategyFlClusEligRankings,
                                         WeatherCluster weatherClusterFineline) {
        //Set isEligibility for all clusterType
        if (commonUpdatedFields.containsKey(IS_ELIGIBLE)) {
            Integer isEligibleFlag = Optional.ofNullable(weatherClusterFineline)
                    .map(WeatherCluster::getIsEligible)
                    .map(flag -> Boolean.TRUE.equals(flag) ? 1 : 0)
                    .orElse(null);
            setIsEligibileForAll(strategyFlClusEligRankings, isEligibleFlag);
        }
        //inStore Date at all clusterType
        if (commonUpdatedFields.containsKey(IN_STORE_DATE)) {
            setInStoreDateForAll(strategyFlClusEligRankings, weatherClusterFineline);
        }
        //MarkDown Date all clusterType
        if (commonUpdatedFields.containsKey(MARK_DOWN_DATE)) {
            setMarkDownDateForAll(strategyFlClusEligRankings, weatherClusterFineline);
        }
        //Rank at all Cluster type
        if (commonUpdatedFields.containsKey("ranking")) {
            setRankingForAll(strategyFlClusEligRankings, weatherClusterFineline);
        }
    }

    private void setRankingForAll(Set<StrategyFlClusEligRanking> strategyFlClusEligRankings, WeatherCluster weatherClusterFineline) {
        Optional.ofNullable(strategyFlClusEligRankings)
                .stream()
                .flatMap(Collection::stream)
                .forEach(strategyFlClusEligRanking -> strategyFlClusEligRanking.setMerchantOverrideRank(weatherClusterFineline.getRanking()));
    }

    private void setInStoreDateForAll(Set<StrategyFlClusEligRanking> strategyFlClusEligRankings, WeatherCluster weatherClusterFineline) {
        Integer inStoreYrWk = CommonUtil.getInStoreYrWk(weatherClusterFineline);
        String inStoreDesc = CommonUtil.getInStoreYrWkDesc(weatherClusterFineline);
        Optional.ofNullable(strategyFlClusEligRankings)
                .stream()
                .flatMap(Collection::stream)
                .forEach(strategyFlClusEligRanking -> {
                    strategyFlClusEligRanking.setInStoreYrWkDesc(inStoreDesc);
                    strategyFlClusEligRanking.setInStoreYrWk(inStoreYrWk);
                    //Rolldown to style & cc level
                    if (!CollectionUtils.isEmpty(strategyFlClusEligRanking.getStrategyStyleCluses())) {
                        Set<StrategyStyleClus> strategyStyleCluses = strategyFlClusEligRanking.getStrategyStyleCluses();
                        setClusterEligStyleCcForInStoreDate(strategyStyleCluses, inStoreDesc, inStoreYrWk);
                    }
                });
    }

    private void setClusterEligStyleCcForInStoreDate(Set<StrategyStyleClus> strategyStyleCluses, String inStoreDesc, Integer inStoreYrWk) {
        for (StrategyStyleClus strategyStyleClus : strategyStyleCluses) {
            if (!CollectionUtils.isEmpty(strategyStyleClus.getStrategyCcClusEligRankings())) {
                Set<StrategyCcClusEligRanking> strategyCcClusEligRankings = strategyStyleClus.getStrategyCcClusEligRankings();
                for (StrategyCcClusEligRanking strategyCcClusEligRanking : strategyCcClusEligRankings) {
                    strategyCcClusEligRanking.setInStoreYrWkDesc(inStoreDesc);
                    strategyCcClusEligRanking.setInStoreYrWk(inStoreYrWk);
                }
            }
        }
    }


    private void setMarkDownDateForAll(Set<StrategyFlClusEligRanking> strategyFlClusEligRankings, WeatherCluster weatherClusterFineline) {
        Integer markDownYrWk = CommonUtil.getMarkdownYrWk(weatherClusterFineline);
        String markDownDesc = CommonUtil.getMarkdownYrWkDesc(weatherClusterFineline);
        Optional.ofNullable(strategyFlClusEligRankings)
                .stream()
                .flatMap(Collection::stream)
                .forEach(strategyFlClusEligRanking -> {
                    strategyFlClusEligRanking.setMarkDownYrWkDesc(markDownDesc);
                    strategyFlClusEligRanking.setMarkDownYrWk(markDownYrWk);
                    //Rolldown to style & cc level
                    if (!CollectionUtils.isEmpty(strategyFlClusEligRanking.getStrategyStyleCluses())) {
                        Set<StrategyStyleClus> strategyStyleCluses = strategyFlClusEligRanking.getStrategyStyleCluses();
                        setClusterEligStyleCcForMarkDownDate(strategyStyleCluses, markDownDesc, markDownYrWk);
                    }
                });
    }

    private void setClusterEligStyleCcForMarkDownDate(Set<StrategyStyleClus> strategyStyleCluses, String markDownDesc, Integer markDownYrWk) {
        for (StrategyStyleClus strategyStyleClus : strategyStyleCluses) {
            if (!CollectionUtils.isEmpty(strategyStyleClus.getStrategyCcClusEligRankings())) {
                Set<StrategyCcClusEligRanking> strategyCcClusEligRankings = strategyStyleClus.getStrategyCcClusEligRankings();
                for (StrategyCcClusEligRanking strategyCcClusEligRanking : strategyCcClusEligRankings) {
                    strategyCcClusEligRanking.setMarkDownYrWkDesc(markDownDesc);
                    strategyCcClusEligRanking.setMarkDownYrWk(markDownYrWk);
                }
            }
        }
    }


    private void setRankingForClusterN(Set<StrategyFlClusEligRanking> strategyFlClusEligRankings, Integer analyticsClusterId,
                                       PlanStrategyId planStrategyId, WeatherCluster weatherClusterFineline) {
        PlanClusterStrategyId planClusterStrategyId = new PlanClusterStrategyId(planStrategyId, analyticsClusterId);
        Optional.ofNullable(strategyFlClusEligRankings)
                .stream()
                .flatMap(Collection::stream)
                .filter(strategyFlClusEligRanking -> strategyFlClusEligRanking.getStrategyFlClusEligRankingId().getPlanClusterStrategyId()
                        .equals(planClusterStrategyId))
                .findFirst()
                .ifPresent(strategyFlClusEligRanking -> strategyFlClusEligRanking.setMerchantOverrideRank(weatherClusterFineline.getRanking()));
    }

    private void setMarkDownDateForClusterN(Set<StrategyFlClusEligRanking> strategyFlClusEligRankings, Integer analyticsClusterId,
                                            PlanStrategyId planStrategyId, WeatherCluster weatherCluster) {
        PlanClusterStrategyId planClusterStrategyId = new PlanClusterStrategyId(planStrategyId, analyticsClusterId);
        Integer markDownWk = CommonUtil.getMarkdownYrWk(weatherCluster);
        String markDownDesc = CommonUtil.getMarkdownYrWkDesc(weatherCluster);

        Optional.ofNullable(strategyFlClusEligRankings)
                .stream()
                .flatMap(Collection::stream)
                .filter(strategyFlClusEligRanking -> strategyFlClusEligRanking.getStrategyFlClusEligRankingId().getPlanClusterStrategyId()
                        .equals(planClusterStrategyId))
                .findFirst()
                .ifPresent(strategyFlClusEligRanking -> {
                    strategyFlClusEligRanking.setMarkDownYrWk(markDownWk);
                    strategyFlClusEligRanking.setMarkDownYrWkDesc(markDownDesc);
                    //Rolldown to style & cc level
                    if (!CollectionUtils.isEmpty(strategyFlClusEligRanking.getStrategyStyleCluses())) {
                        Set<StrategyStyleClus> strategyStyleCluses = strategyFlClusEligRanking.getStrategyStyleCluses();
                        setStyleCcMarkDownMetrics(strategyStyleCluses, markDownWk, markDownDesc);
                    }
                });
    }

    private void setInStoreDateForClusterN(Set<StrategyFlClusEligRanking> strategyFlClusEligRankings, Integer analyticsClusterId,
                                           PlanStrategyId planStrategyId, WeatherCluster weatherCluster) {
        PlanClusterStrategyId planClusterStrategyId = new PlanClusterStrategyId(planStrategyId, analyticsClusterId);
        Integer inStoreYrWk = CommonUtil.getInStoreYrWk(weatherCluster);
        String inStoreDesc = CommonUtil.getInStoreYrWkDesc(weatherCluster);

        Optional.ofNullable(strategyFlClusEligRankings)
                .stream()
                .flatMap(Collection::stream)
                .filter(strategyFlClusEligRanking -> strategyFlClusEligRanking.getStrategyFlClusEligRankingId().getPlanClusterStrategyId()
                        .equals(planClusterStrategyId))
                .findFirst()
                .ifPresent(strategyFlClusEligRanking -> {
                    strategyFlClusEligRanking.setInStoreYrWk(inStoreYrWk);
                    strategyFlClusEligRanking.setInStoreYrWkDesc(inStoreDesc);
                    //Rolldown to style & cc level
                    if (!CollectionUtils.isEmpty(strategyFlClusEligRanking.getStrategyStyleCluses())) {
                        Set<StrategyStyleClus> strategyStyleCluses = strategyFlClusEligRanking.getStrategyStyleCluses();
                        setStyleCcInStoreMetrics(strategyStyleCluses, inStoreYrWk, inStoreDesc);
                    }
                });
    }

    private void setIsEligibileForAll(Set<StrategyFlClusEligRanking> strategyFlClusEligRankings, Integer isEligibleFlag) {
        Optional.ofNullable(strategyFlClusEligRankings)
                .stream()
                .flatMap(Collection::stream)
                .forEach(strategyFlClusEligRanking -> {
                    strategyFlClusEligRanking.setIsEligibleFlag(isEligibleFlag);
                    //Rolldown to style & cc level
                    if (!CollectionUtils.isEmpty(strategyFlClusEligRanking.getStrategyStyleCluses())) {
                        Set<StrategyStyleClus> strategyStyleCluses = strategyFlClusEligRanking.getStrategyStyleCluses();
                        setClusterEligStyleCcForAll(strategyStyleCluses, isEligibleFlag);
                    }
                });
    }

    private void setIsEligibleForClusterN(Set<StrategyFlClusEligRanking> strategyFlClusEligRankings, PlanStrategyId planStrategyId,
                                          Integer isEligibleFlag, Integer analyticsClusterId) {
        PlanClusterStrategyId planClusterStrategyId = new PlanClusterStrategyId(planStrategyId, analyticsClusterId);
        Optional.ofNullable(strategyFlClusEligRankings)
                .stream()
                .flatMap(Collection::stream)
                .filter(strategyFlClusEligRanking -> strategyFlClusEligRanking.getStrategyFlClusEligRankingId().getPlanClusterStrategyId()
                        .equals(planClusterStrategyId))
                .findFirst()
                .ifPresent(strategyFlClusEligRanking -> {
                    strategyFlClusEligRanking.setIsEligibleFlag(isEligibleFlag);
                    //Rolldown to style & cc level
                    if (!CollectionUtils.isEmpty(strategyFlClusEligRanking.getStrategyStyleCluses())) {
                        Set<StrategyStyleClus> strategyStyleCluses = strategyFlClusEligRanking.getStrategyStyleCluses();
                        setClusterEligStyleCcForAll(strategyStyleCluses, isEligibleFlag);
                    }
                });
        //will check the status of all clusters eligible flag and based on all is updated
        validateAndUpdateAllFlag(strategyFlClusEligRankings, planStrategyId);
    }


    private void validateAndUpdateAllFlag(Set<StrategyFlClusEligRanking> strategyFlClusEligRankings, PlanStrategyId planStrategyId) {
        PlanClusterStrategyId allPlanClusterStrategyId = new PlanClusterStrategyId(planStrategyId, 0);
        Integer selectStatusId;
        Set<Integer> flagsPar = getIsEligibleFlagsPartialAtFinelineClusters(strategyFlClusEligRankings, allPlanClusterStrategyId);
        if (flagsPar.size() == 1 && flagsPar.contains(1)) {
            selectStatusId=1;
        } else if (flagsPar.size() > 1){
            selectStatusId=2;
        } else {
            selectStatusId=0;
        }
        Optional.ofNullable(strategyFlClusEligRankings)
                .stream()
                .flatMap(Collection::stream)
                .filter(strategyFlClusEligRanking -> strategyFlClusEligRanking.getStrategyFlClusEligRankingId().getPlanClusterStrategyId()
                        .equals(allPlanClusterStrategyId))
                .findFirst()
                .ifPresent(strategyFlClusEligRanking -> {
                    strategyFlClusEligRanking.setIsEligibleFlag(selectStatusId);
                    //Rolldown to style & cc level
                    if (!CollectionUtils.isEmpty(strategyFlClusEligRanking.getStrategyStyleCluses())) {
                        Set<StrategyStyleClus> strategyStyleCluses = strategyFlClusEligRanking.getStrategyStyleCluses();
                        setClusterEligStyleCcForAll(strategyStyleCluses, selectStatusId);
                    }
                });
    }

    private Set<Integer> getIsEligibleFlagsPartialAtFinelineClusters(Set<StrategyFlClusEligRanking> strategyFlClusEligRankings, PlanClusterStrategyId allPlanClusterStrategyId) {
        return Optional.ofNullable(strategyFlClusEligRankings)
                .stream()
                .flatMap(Collection::stream)
                .filter(strategyFlClusEligRanking -> !strategyFlClusEligRanking.getStrategyFlClusEligRankingId().getPlanClusterStrategyId()
                        .equals(allPlanClusterStrategyId))
                .map(StrategyFlClusEligRanking::getIsEligibleFlag).collect(Collectors.toSet());
    }

    private void setExcludeOffshoreForClusterN(Set<StrategyFlClusEligRanking> strategyFlClusEligRankings,
                                               Integer analyticsClusterId, PlanStrategyId planStrategyId,
                                               WeatherCluster weatherCluster) {
        //Request planClusterStrategyId (i.e. cluster 1 or cluster 7)
        PlanClusterStrategyId planClusterStrategyId = new PlanClusterStrategyId(planStrategyId, analyticsClusterId);
        //Need this planClusterStrategyId for calculating update storeCount for cluster 0
        PlanClusterStrategyId planClusterStrategyIdFor0 = new PlanClusterStrategyId(planStrategyId, 0);
        //Get analytics StoreCount to set the count based on offshore changes
        List<AnalyticsClusterStoreDTO> analyticsClusterStoreDTOS = Optional.ofNullable(analyticsClusterStoreCountService.getAnalyticsClusterStoreCount(planStrategyId.getStrategyId()))
                .stream()
                .flatMap(Collection::stream)
                .filter(analyticsClusterStoreDTO -> analyticsClusterStoreDTO.getAnalyticsClusterId().equals(analyticsClusterId))
                .collect(Collectors.toList());
        //Requested offshore list
        List<String> offshoreExcludeStateCode = Optional.ofNullable(weatherCluster)
                .map(WeatherCluster::getIncludeOffshore)
                .stream()
                .flatMap(Collection::stream)
                .map(IncludeOffshoreMkt::getMarketValue)
                .collect(Collectors.toList());
        log.info("updating includeOffshore for analyticsClusterId:{}, with list of :{}", analyticsClusterId,
                String.join(",", offshoreExcludeStateCode));
        List<String> difference = new ArrayList<>();

        if (analyticsClusterId == 1) {
            log.info("Exclude the difference from the request list with the actual cluster offshore list for cluster 1");
            difference = appProperties.getCluster1OffshoreList()
                    .stream()
                    .filter(element -> !offshoreExcludeStateCode.contains(element))
                    .collect(Collectors.toList());

        } else if (analyticsClusterId == 2) {
            log.info("Exclude the difference from the request list with the actual cluster offshore list for cluster 2");
            difference = appProperties.getCluster2OffshoreList()
                    .stream()
                    .filter(element -> !offshoreExcludeStateCode.contains(element))
                    .collect(Collectors.toList());

        }
        else if (analyticsClusterId == 7) {
            log.info("Exclude the difference from the request list with the actual cluster offshore list for cluster 7");
            difference = appProperties.getCluster7OffshoreList()
                    .stream()
                    .filter(element -> !offshoreExcludeStateCode.contains(element))
                    .collect(Collectors.toList());
        }
        //get the count based on the list of selection
        List<String> finalDifference = difference;
        Integer storeCount = Optional.of(analyticsClusterStoreDTOS)
                .stream()
                .flatMap(Collection::stream)
                .filter(analyticsClusterStoreDTO -> !finalDifference.contains(analyticsClusterStoreDTO.getStateProvinceCode()))
                .map(AnalyticsClusterStoreDTO::getStoreCount)
                .mapToInt(Long::intValue)
                .sum();
        //Set the store count for
        Optional.ofNullable(strategyFlClusEligRankings)
                .stream()
                .flatMap(Collection::stream)
                .filter(strategyFlClusEligRanking -> strategyFlClusEligRanking.getStrategyFlClusEligRankingId().getPlanClusterStrategyId()
                        .equals(planClusterStrategyId))
                .findFirst()
                .ifPresent(strategyFlClusEligRanking -> strategyFlClusEligRanking.setStoreCount(storeCount));
        Integer overAllCluster0Count = getOverAllStoreCount(strategyFlClusEligRankings);
        //Update the cluster 0 count
        Optional.ofNullable(strategyFlClusEligRankings)
                .stream()
                .flatMap(Collection::stream)
                .filter(strategyFlClusEligRanking -> strategyFlClusEligRanking.getStrategyFlClusEligRankingId().getPlanClusterStrategyId()
                        .equals(planClusterStrategyIdFor0))
                .findFirst()
                .ifPresent(strategyFlClusEligRanking -> strategyFlClusEligRanking.setStoreCount(overAllCluster0Count));
        //Set strategyFlMktClusEligs object
        Optional.ofNullable(strategyFlClusEligRankings)
                .stream()
                .flatMap(Collection::stream)
                .filter(strategyFlClusEligRanking -> strategyFlClusEligRanking.getStrategyFlClusEligRankingId().getPlanClusterStrategyId()
                        .equals(planClusterStrategyId))
                .findFirst()
                .ifPresent(strategyFlClusEligRanking -> setExcludeOffshoreMkt(strategyFlClusEligRanking, offshoreExcludeStateCode));
    }

    private Integer getOverAllStoreCount(Set<StrategyFlClusEligRanking> strategyFlClusEligRankings) {
        log.info("Calculate the update storecount for cluster 0");
        return Optional.ofNullable(strategyFlClusEligRankings)
                .stream()
                .flatMap(Collection::stream)
                .filter(strategyFlClusEligRanking -> !strategyFlClusEligRanking.getStrategyFlClusEligRankingId()
                        .getPlanClusterStrategyId().getAnalyticsClusterId().equals(0))
                .mapToInt(StrategyFlClusEligRanking::getStoreCount)
                .sum();
    }

    private void setExcludeOffshoreMkt(StrategyFlClusEligRanking strategyFlClusEligRanking, List<String> offshoreExclude) {
        Optional.ofNullable(strategyFlClusEligRanking)
                .map(StrategyFlClusEligRanking::getStrategyFlMktClusEligs)
                .ifPresentOrElse(strategyFlMktClusEligs -> setExcludeOffshoreMktIfExists(strategyFlClusEligRanking, strategyFlMktClusEligs, offshoreExclude),
                        () -> setExcludeOffshoreMktIfNotExists(strategyFlClusEligRanking, offshoreExclude));

        if (!CollectionUtils.isEmpty(strategyFlClusEligRanking.getStrategyStyleCluses())) {
            for (StrategyStyleClus styleClus : strategyFlClusEligRanking.getStrategyStyleCluses()) {
                if (!CollectionUtils.isEmpty(styleClus.getStrategyCcClusEligRankings())) {
                    for (StrategyCcClusEligRanking ccClus : styleClus.getStrategyCcClusEligRankings()) {
                        Optional.ofNullable(ccClus)
                                .map(StrategyCcClusEligRanking::getStrategyCcMktClusEligs)
                                .ifPresentOrElse(strategyCcMktClusEligs -> setExcludeOffshoreMktIfExistsCc(ccClus, strategyCcMktClusEligs, offshoreExclude),
                                        () -> setExcludeOffshoreMktIfNotExistsCc(ccClus, offshoreExclude));
                        }
                    }
                }
            }
    }

    private void setExcludeOffshoreMktIfExists(StrategyFlClusEligRanking strategyFlClusEligRanking,
                                               Set<StrategyFlMktClusElig> strategyFlMktClusEligs, List<String> offshoreExclude) {
        strategyFlMktClusEligs.clear();
        entityManager.flush();
        if (!CollectionUtils.isEmpty(offshoreExclude)) {
            for (String offshoreMkt : offshoreExclude) {
                StrategyFlMktClusElig strategyFlMktClusElig = new StrategyFlMktClusElig();
                StrategyFlMktClusEligId strategyFlMktClusEligId = new StrategyFlMktClusEligId(strategyFlClusEligRanking.getStrategyFlClusEligRankingId(),
                        IncludeOffshoreMkt.getChannelIdFromName(offshoreMkt));
                strategyFlMktClusElig.setStrategyFlMktClusEligId(strategyFlMktClusEligId);
                strategyFlMktClusElig.setStrategyFlClusEligRanking(strategyFlClusEligRanking);
                strategyFlClusEligRanking.getStrategyFlMktClusEligs().add(strategyFlMktClusElig);
            }

        }

    }

    private void setExcludeOffshoreMktIfNotExists(StrategyFlClusEligRanking strategyFlClusEligRanking, List<String> offshoreExclude) {
        Set<StrategyFlMktClusElig> strategyFlMktClusEligs = new HashSet<>();
        if (!CollectionUtils.isEmpty(offshoreExclude)) {
            for (String offshoreMkt : offshoreExclude) {
                StrategyFlMktClusElig strategyFlMktClusElig = new StrategyFlMktClusElig();
                StrategyFlMktClusEligId strategyFlMktClusEligId = new StrategyFlMktClusEligId(strategyFlClusEligRanking.getStrategyFlClusEligRankingId(),
                        IncludeOffshoreMkt.getChannelIdFromName(offshoreMkt));
                strategyFlMktClusElig.setStrategyFlMktClusEligId(strategyFlMktClusEligId);
                strategyFlMktClusElig.setStrategyFlClusEligRanking(strategyFlClusEligRanking);
                strategyFlMktClusEligs.add(strategyFlMktClusElig);
            }
            strategyFlClusEligRanking.setStrategyFlMktClusEligs(strategyFlMktClusEligs);

        }
    }

    private void setExcludeOffshoreMktIfExistsCc(StrategyCcClusEligRanking strategyCcClusEligRanking,
                                               Set<StrategyCcMktClusElig> strategyCcMktClusEligs, List<String> offshoreExclude) {
        strategyCcMktClusEligs.clear();
        entityManager.flush();
        if (!CollectionUtils.isEmpty(offshoreExclude)) {
            for (String offshoreMkt : offshoreExclude) {
                StrategyCcMktClusElig strategyCcMktClusElig = new StrategyCcMktClusElig();
                StrategyCcMktClusEligId strategyCcMktClusEligId = new StrategyCcMktClusEligId(strategyCcClusEligRanking.getStrategyCcClusEligRankingId(),
                        IncludeOffshoreMkt.getChannelIdFromName(offshoreMkt));
                strategyCcMktClusElig.setStrategyCcMktClusEligId(strategyCcMktClusEligId);
                strategyCcMktClusElig.setStrategyCcClusEligRanking(strategyCcClusEligRanking);
                strategyCcClusEligRanking.getStrategyCcMktClusEligs().add(strategyCcMktClusElig);
            }

        }

    }

    private void setExcludeOffshoreMktIfNotExistsCc(StrategyCcClusEligRanking strategyCcClusEligRanking, List<String> offshoreExclude) {
        Set<StrategyCcMktClusElig> strategyCcMktClusEligs = new HashSet<>();
        if (!CollectionUtils.isEmpty(offshoreExclude)) {
            for (String offshoreMkt : offshoreExclude) {
                StrategyCcMktClusElig strategyCcMktClusElig = new StrategyCcMktClusElig();
                StrategyCcMktClusEligId strategyCcMktClusEligId = new StrategyCcMktClusEligId(strategyCcClusEligRanking.getStrategyCcClusEligRankingId(),
                        IncludeOffshoreMkt.getChannelIdFromName(offshoreMkt));
                strategyCcMktClusElig.setStrategyCcMktClusEligId(strategyCcMktClusEligId);
                strategyCcMktClusElig.setStrategyCcClusEligRanking(strategyCcClusEligRanking);
                strategyCcMktClusEligs.add(strategyCcMktClusElig);
            }
            strategyCcClusEligRanking.setStrategyCcMktClusEligs(strategyCcMktClusEligs);

        }
    }

    public void setClusterEligStyleCcForAll(Set<StrategyStyleClus> strategyStyleCluses, Integer isEligibleFlag) {
        for (StrategyStyleClus strategyStyleClus : strategyStyleCluses) {
            if (!CollectionUtils.isEmpty(strategyStyleClus.getStrategyCcClusEligRankings())) {
                Set<StrategyCcClusEligRanking> strategyCcClusEligRankings = strategyStyleClus.getStrategyCcClusEligRankings();
                for (StrategyCcClusEligRanking strategyCcClusEligRanking : strategyCcClusEligRankings) {
                    strategyCcClusEligRanking.setIsEligibleFlag(isEligibleFlag);
                }
            }
        }
    }

    public void setStyleCcInStoreMetrics(Set<StrategyStyleClus> strategyStyleCluses, Integer inStoreYrWk, String inStoreDesc) {
        for (StrategyStyleClus strategyStyleClus : strategyStyleCluses) {
            if (!CollectionUtils.isEmpty(strategyStyleClus.getStrategyCcClusEligRankings())) {
                Set<StrategyCcClusEligRanking> strategyCcClusEligRankings = strategyStyleClus.getStrategyCcClusEligRankings();
                for (StrategyCcClusEligRanking strategyCcClusEligRanking : strategyCcClusEligRankings) {
                    strategyCcClusEligRanking.setInStoreYrWk(inStoreYrWk);
                    strategyCcClusEligRanking.setInStoreYrWkDesc(inStoreDesc);
                }
            }
        }
    }

    public void setStyleCcMarkDownMetrics(Set<StrategyStyleClus> strategyStyleCluses, Integer markDownWk, String markDownDesc) {
        for (StrategyStyleClus strategyStyleClus : strategyStyleCluses) {
            if (!CollectionUtils.isEmpty(strategyStyleClus.getStrategyCcClusEligRankings())) {
                Set<StrategyCcClusEligRanking> strategyCcClusEligRankings = strategyStyleClus.getStrategyCcClusEligRankings();
                for (StrategyCcClusEligRanking strategyCcClusEligRanking : strategyCcClusEligRankings) {
                    strategyCcClusEligRanking.setMarkDownYrWk(markDownWk);
                    strategyCcClusEligRanking.setMarkDownYrWkDesc(markDownDesc);
                }
            }
        }
    }

}
