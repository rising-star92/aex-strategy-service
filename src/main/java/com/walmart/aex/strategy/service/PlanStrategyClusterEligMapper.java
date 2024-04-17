package com.walmart.aex.strategy.service;

import com.walmart.aex.strategy.dto.AnalyticsClusterStoreDTO;
import com.walmart.aex.strategy.dto.request.*;
import com.walmart.aex.strategy.entity.*;
import com.walmart.aex.strategy.enums.IncludeOffshoreMkt;
import com.walmart.aex.strategy.exception.CustomException;
import com.walmart.aex.strategy.properties.AppProperties;
import com.walmart.aex.strategy.repository.StrategyClusterRepository;
import com.walmart.aex.strategy.util.CommonUtil;
import io.strati.ccm.utils.client.annotation.ManagedConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PlanStrategyClusterEligMapper {

    private final StrategyClusterRepository strategyClusterRepository;
    private final AnalyticsClusterStoreCountService analyticsClusterStoreCountService;
    @ManagedConfiguration
    private AppProperties appProperties;

    public PlanStrategyClusterEligMapper(StrategyClusterRepository strategyClusterRepository,
                                         AnalyticsClusterStoreCountService analyticsClusterStoreCountService) {
        this.strategyClusterRepository = strategyClusterRepository;
        this.analyticsClusterStoreCountService = analyticsClusterStoreCountService;
    }

    /**
     * This method check if there is a weather cluster provided in the request if so calls the fetchPlanClusterStrategy
     *
     * @param lvl3s
     * @param planStrategyId
     * @param request
     * @param lvl1
     * @param lvl2
     * @return PlanClusterStrategy
     */

    public Set<PlanClusterStrategy> setPlanClusterStrategy(PlanStrategy planStrategy, List<Lvl3> lvl3s, PlanStrategyId planStrategyId, PlanStrategyDTO request,
                                                           Lvl1 lvl1, Lvl2 lvl2) {
        Set<PlanClusterStrategy> planClusterStrategies = Optional.ofNullable(planStrategy.getPlanClusterStrategies())
                .orElse(new HashSet<>());
        for (Lvl3 lvl3 : lvl3s) {
            for (Lvl4 lvl4 : lvl3.getLvl4List()) {
                for (Fineline fineline : lvl4.getFinelines()) {
                    fetchPlanClusterStrategy(planStrategyId, request.getLvl0Nbr(), lvl1.getLvl1Nbr(), lvl2.getLvl2Nbr(),
                            lvl3.getLvl3Nbr(), lvl4.getLvl4Nbr(), fineline, planClusterStrategies);
                }
            }
        }

        return planClusterStrategies;
    }

    /**
     * This method check & sets PlanClusterStrategy and calls StrategyFLClustEligRanking entity .
     *
     * @param planStrategyId
     * @param lvl0Nbr
     * @param lvl1Nbr
     * @param lvl2Nbr
     * @param lvl3Nbr
     * @param lvl4Nbr
     * @param fineline
     * @param planClusterStrategies
     */

    private void fetchPlanClusterStrategy(PlanStrategyId planStrategyId,
                                          Integer lvl0Nbr, Integer lvl1Nbr, Integer lvl2Nbr, Integer lvl3Nbr, Integer lvl4Nbr, Fineline fineline,
                                          Set<PlanClusterStrategy> planClusterStrategies) {
        Set<WeatherCluster> weatherClusters = fetchFinelineWeatherClusters(fineline, planStrategyId.getStrategyId());
        if (!CollectionUtils.isEmpty(weatherClusters)) {
            for (WeatherCluster weatherCluster : weatherClusters) {
                PlanClusterStrategyId planClusterStrategyId = new PlanClusterStrategyId(planStrategyId,
                        fetchAnalyticsClusterId(weatherCluster.getType()));
                log.debug("Check if a planClusterStrategy Id : {} already exists or not", planClusterStrategyId);
                PlanClusterStrategy planClusterStrategy = Optional.of(planClusterStrategies)
                        .stream()
                        .flatMap(Collection::stream).filter(planClusterStrategy1 -> planClusterStrategy1.getPlanClusterStrategyId().equals(planClusterStrategyId))
                        .findFirst()
                        .orElse(new PlanClusterStrategy());
                //Set the PlanClusterStrategy entity
                if (planClusterStrategy.getPlanClusterStrategyId() == null) {
                    planClusterStrategy.setPlanClusterStrategyId(planClusterStrategyId);
                    planClusterStrategy.setAnalyticsClusterLabel(fetchAnalyticsClusterTypeDesc(weatherCluster.getType()));
                }
                //fetch & set the StrategyFLClusterEligRanking entity
                StrategyFlClusEligRankingId strategyFlClusEligRankingId = new StrategyFlClusEligRankingId(
                        planClusterStrategy.getPlanClusterStrategyId(), lvl0Nbr, lvl1Nbr, lvl2Nbr,
                        lvl3Nbr, lvl4Nbr, fineline.getFinelineNbr());
                planClusterStrategy.setStrategyFlClusEligRankings(fetchPlanStrageyFlClustEligRanking(planClusterStrategy,
                        strategyFlClusEligRankingId, fineline, weatherCluster));
                planClusterStrategies.add(planClusterStrategy);
            }
        }

    }

    /**
     * This method check & sets StrategyFlClusEligRanking and calls StrategyFlClusterMetric & cluster Style entity .
     *
     * @param strategyFlClusEligRankingId
     * @param weatherCluster
     * @return StrategyFlClusEligRanking
     */

    private Set<StrategyFlClusEligRanking> fetchPlanStrageyFlClustEligRanking(PlanClusterStrategy planClusterStrategy,
                                                                              StrategyFlClusEligRankingId strategyFlClusEligRankingId,
                                                                              Fineline fineline,
                                                                              WeatherCluster weatherCluster) {
        Set<StrategyFlClusEligRanking> strategyFlClusEligRankings = Optional.ofNullable(planClusterStrategy.getStrategyFlClusEligRankings())
                .orElse(new HashSet<>());
        log.debug("Check if a strategyFlClusEligRanking Id : {} already exists or not", strategyFlClusEligRankingId.toString());
        StrategyFlClusEligRanking strategyFlClusEligRanking = Optional.of(strategyFlClusEligRankings)
                .stream()
                .flatMap(Collection::stream).filter(strategyFlClusEligRanking1 -> strategyFlClusEligRanking1.getStrategyFlClusEligRankingId().equals(strategyFlClusEligRankingId))
                .findFirst()
                .orElse(new StrategyFlClusEligRanking());
        //Set the strategyFlClusEligRanking entity
        if (strategyFlClusEligRanking.getStrategyFlClusEligRankingId() == null) {
            strategyFlClusEligRanking.setStrategyFlClusEligRankingId(strategyFlClusEligRankingId);
            //fetch & set the StrategyFlMktClusEligs entity
            strategyFlClusEligRanking.setStrategyFlMktClusEligs(fetchStrategyFlMktCustElig(weatherCluster,
                    strategyFlClusEligRanking));
        }
        strategyFlClusEligRanking.setFinelineDesc(fineline.getFinelineName());
        strategyFlClusEligRanking.setInStoreYrWk(CommonUtil.getInStoreYrWk(weatherCluster));
        strategyFlClusEligRanking.setMarkDownYrWk(CommonUtil.getMarkdownYrWk(weatherCluster));
        strategyFlClusEligRanking.setInStoreYrWkDesc(CommonUtil.getInStoreYrWkDesc(weatherCluster));
        strategyFlClusEligRanking.setMarkDownYrWkDesc(CommonUtil.getMarkdownYrWkDesc(weatherCluster));
        //default isELig to False, with no Fashion this helps traits to show the right status.
        strategyFlClusEligRanking.setIsEligibleFlag(0);
        strategyFlClusEligRanking.setStoreCount(getClusterStoreCount(strategyFlClusEligRankingId.getPlanClusterStrategyId().getAnalyticsClusterId(),
                strategyFlClusEligRankingId.getPlanClusterStrategyId().getPlanStrategyId().getStrategyId()).intValue());
        //fetch & set the StrategyFlClusMetrics entity
        strategyFlClusEligRanking.setStrategyFlClusMetrics(setStrategyFlClusterMetric(weatherCluster, strategyFlClusEligRanking));
        //fetch & set the StrategyStyleCluster entity
        if (!CollectionUtils.isEmpty(fineline.getStyles())) {
            strategyFlClusEligRanking.setStrategyStyleCluses(setStrategyStyleCluster(fineline.getStyles(), strategyFlClusEligRanking));
        }
        strategyFlClusEligRankings.add(strategyFlClusEligRanking);
        return strategyFlClusEligRankings;
    }

    private Long getClusterStoreCount(Integer analyticsClusterId, Long strategyId) {
        log.info("Get the StoreCount for analyticsClusterId:{}, strategyId:{}", analyticsClusterId, strategyId);
        List<AnalyticsClusterStoreDTO> analyticsClusterStoreCount = analyticsClusterStoreCountService.getAnalyticsClusterStoreCount(strategyId);
        Long storeCount = null;
        if (analyticsClusterId == 0) {
            storeCount = Optional.ofNullable(analyticsClusterStoreCount)
                    .stream()
                    .flatMap(Collection::stream)
                    .map(AnalyticsClusterStoreDTO::getStoreCount)
                    .mapToLong(Long::intValue)
                    .sum();
        } else {
            storeCount = Optional.ofNullable(analyticsClusterStoreCount)
                    .stream()
                    .flatMap(Collection::stream)
                    .filter(analyticsClusterStoreDTO -> analyticsClusterStoreDTO.getAnalyticsClusterId().equals(analyticsClusterId))
                    .map(AnalyticsClusterStoreDTO::getStoreCount)
                    .mapToLong(Long::intValue)
                    .sum();
        }
        return storeCount;


    }

    /**
     * This method check & sets StrategyFlMktClusElig, we set HI & PR to cluster1 and AK to cluster7.
     *
     * @param strategyFlClusEligRanking
     * @param weatherCluster
     * @return StrategyFlMktClusElig
     */
    private Set<StrategyFlMktClusElig> fetchStrategyFlMktCustElig(WeatherCluster weatherCluster,
                                                                  StrategyFlClusEligRanking strategyFlClusEligRanking) {
        Set<StrategyFlMktClusElig> strategyFlMktClusEligs = Optional.ofNullable(strategyFlClusEligRanking.getStrategyFlMktClusEligs())
                .orElse(new HashSet<>());
        //If Weather cluster is 1 set PR as includeOffshore Mkt
        if (fetchAnalyticsClusterId(weatherCluster.getType()) == 1 && CollectionUtils.isEmpty(strategyFlMktClusEligs)) {
            strategyFlMktClusEligs =
                    Optional.ofNullable(appProperties.getCluster1OffshoreList())
                            .stream()
                            .flatMap(Collection::stream)
                            .map(offshore -> setStrategyFlMarketClusElig(strategyFlClusEligRanking.getStrategyFlClusEligRankingId(),
                                    IncludeOffshoreMkt.getChannelIdFromName(offshore)))
                            .collect(Collectors.toSet());
        }//If Weather cluster is 2 set HI as includeOffshore Mkt
        else if (fetchAnalyticsClusterId(weatherCluster.getType()) == 2 && CollectionUtils.isEmpty(strategyFlMktClusEligs)) {
            strategyFlMktClusEligs =
                    Optional.ofNullable(appProperties.getCluster2OffshoreList())
                            .stream()
                            .flatMap(Collection::stream)
                            .map(offshore -> setStrategyFlMarketClusElig(strategyFlClusEligRanking.getStrategyFlClusEligRankingId(),
                                    IncludeOffshoreMkt.getChannelIdFromName(offshore)))
                            .collect(Collectors.toSet());
        } //If Weather cluster is 7 set AK as includeOffshore Mkt
        else if (fetchAnalyticsClusterId(weatherCluster.getType()) == 7 && CollectionUtils.isEmpty(strategyFlMktClusEligs)) {
            strategyFlMktClusEligs =
                    Optional.ofNullable(appProperties.getCluster7OffshoreList())
                            .stream()
                            .flatMap(Collection::stream)
                            .map(offshore -> setStrategyFlMarketClusElig(strategyFlClusEligRanking.getStrategyFlClusEligRankingId(),
                                    IncludeOffshoreMkt.getChannelIdFromName(offshore)))
                            .collect(Collectors.toSet());
        }
        return strategyFlMktClusEligs;
    }

    /**
     * This method check & sets StrategyFlMktClusProgram we set PR to cluster1, HI to cluster2 and AK to cluster7.
     *
     * @param strategyFlClusPrgmEligRanking
     * @param weatherClusterFineline
     * @return FinelineMarketClusterPrgElg
     */
    public Set<FinelineMarketClusterPrgElg> fetchStrategyFlMktCustEligProgram(WeatherCluster weatherClusterFineline,
                                                                              StrategyFlClusPrgmEligRanking strategyFlClusPrgmEligRanking, StrategyFlClusPrgmEligRankingId strategyFlClusPrgmEligRankingId) {
        Set<FinelineMarketClusterPrgElg> finelineMarketClusterPrgElgs = Optional.ofNullable(strategyFlClusPrgmEligRanking.getFinelineMarketClusterPrgElgs())
                .orElse(new HashSet<>());
        //If Weather cluster is 0 add offshore states
        Integer weatherClusterFinelineClusterId = fetchAnalyticsClusterId(weatherClusterFineline.getType());

        if ((weatherClusterFinelineClusterId == 0 || weatherClusterFinelineClusterId == 1 || weatherClusterFinelineClusterId == 2 || weatherClusterFinelineClusterId == 7) && CollectionUtils.isEmpty(finelineMarketClusterPrgElgs)) {
            Integer analyticsClusterId = strategyFlClusPrgmEligRankingId.getStrategyFlClusEligRankingId().getPlanClusterStrategyId().getAnalyticsClusterId();
            switch (analyticsClusterId) {
                case 1:
                    finelineMarketClusterPrgElgs.addAll(Optional.ofNullable(appProperties.getCluster1OffshoreList())
                            .stream()
                            .flatMap(Collection::stream)
                            .map(offshore -> setFinelineMarketClusterPrgElg(strategyFlClusPrgmEligRankingId,
                                    IncludeOffshoreMkt.getChannelIdFromName(offshore))).collect(Collectors.toSet()));
                    break;
                case 2:
                    finelineMarketClusterPrgElgs.addAll(
                            Optional.ofNullable(appProperties.getCluster2OffshoreList())
                                    .stream()
                                    .flatMap(Collection::stream)
                                    .map(offshore -> setFinelineMarketClusterPrgElg(strategyFlClusPrgmEligRankingId,
                                            IncludeOffshoreMkt.getChannelIdFromName(offshore)))
                                    .collect(Collectors.toSet()));
                    break;
                case 7:
                    finelineMarketClusterPrgElgs.addAll(
                            Optional.ofNullable(appProperties.getCluster7OffshoreList())
                                    .stream()
                                    .flatMap(Collection::stream)
                                    .map(offshore -> setFinelineMarketClusterPrgElg(strategyFlClusPrgmEligRankingId,
                                            IncludeOffshoreMkt.getChannelIdFromName(offshore)))
                                    .collect(Collectors.toSet()));
                    break;
                default: //Do Noting
                    break;
            }
        }
        return finelineMarketClusterPrgElgs;
    }

    /**
     * This method check & sets StrategyFlMktClusProgram we set PR to cluster1, HI to cluster2 and AK to cluster7.
     *
     * @param eligCcClusProg
     * @param weatherClusterCc
     * @return FinelineMarketClusterPrgElg
     */
    public Set<EligCcMktClusProg> fetchStrategyCcMktCustEligProgram(WeatherCluster weatherClusterCc,
                                                                    EligCcClusProg eligCcClusProg, EligCcClusProgId eligCcClusProgId) {
        Set<EligCcMktClusProg> ccMarketClusterPrgElgs = Optional.ofNullable(eligCcClusProg.getEligCcMktClusProgs())
                .orElse(new HashSet<>());
        //If Weather cluster is 0 add offshore states
        Integer weatherClusterCcClusterId = fetchAnalyticsClusterId(weatherClusterCc.getType());

        if ((weatherClusterCcClusterId == 0 || weatherClusterCcClusterId == 1 || weatherClusterCcClusterId == 2 || weatherClusterCcClusterId == 7) && CollectionUtils.isEmpty(ccMarketClusterPrgElgs)) {
            Integer analyticsClusterId = eligCcClusProgId.getEligStyleClusProgId().getStrategyFlClusPrgmEligRankingId()
                    .getStrategyFlClusEligRankingId().getPlanClusterStrategyId().getAnalyticsClusterId();
            switch (analyticsClusterId) {
                case 1:
                    ccMarketClusterPrgElgs.addAll(Optional.ofNullable(appProperties.getCluster1OffshoreList())
                            .stream()
                            .flatMap(Collection::stream)
                            .map(offshore -> setCcMarketClusterPrgElg(eligCcClusProgId,
                                    IncludeOffshoreMkt.getChannelIdFromName(offshore))).collect(Collectors.toSet()));
                    break;
                case 2:
                    ccMarketClusterPrgElgs.addAll(Optional.ofNullable(appProperties.getCluster2OffshoreList())
                            .stream()
                            .flatMap(Collection::stream)
                            .map(offshore -> setCcMarketClusterPrgElg(eligCcClusProgId,
                                    IncludeOffshoreMkt.getChannelIdFromName(offshore))).collect(Collectors.toSet()));
                    break;
                case 7:
                    ccMarketClusterPrgElgs.addAll(Optional.ofNullable(appProperties.getCluster7OffshoreList())
                            .stream()
                            .flatMap(Collection::stream)
                            .map(offshore -> setCcMarketClusterPrgElg(eligCcClusProgId,
                                    IncludeOffshoreMkt.getChannelIdFromName(offshore))).collect(Collectors.toSet()));
                    break;
                default: //do Noting
                    break;
            }
        }
        return ccMarketClusterPrgElgs;
    }

    /**
     * This method returns the list of weather clusters from the request. if that doesn't match with cluster size defined add that cluster
     *
     * @param fineline
     * @return WeatherCluster
     */
    public Set<WeatherCluster> fetchFinelineWeatherClusters(Fineline fineline, Long strategyId) {
        Set<WeatherCluster> receivedWeatherClusters = Optional.ofNullable(fineline)
                .map(Fineline::getStrategy)
                .map(Strategy::getWeatherClusters)
                .stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());

        List<Integer> receivedClusterIds = Optional.of(receivedWeatherClusters)
                .stream()
                .flatMap(Collection::stream)
                .map(weatherCluster -> weatherCluster.getType().getAnalyticsClusterId())
                .collect(Collectors.toList());

        List<Integer> clusterIds = Optional.ofNullable(strategyClusterRepository.findAllWeatherClustersForStrategy(strategyId))
                .filter(ids -> !ids.isEmpty())
                .orElseThrow(() -> new CustomException(String.format(
                        "Strat Cluster table don't have entries for weather cluster for the strategyId: %s", strategyId)));
        //If the listener or analytics dont have all the cluster info defined for a strategy build those placeholder clusters
        if (clusterIds.size() > receivedClusterIds.size()) {
            for (Integer id : clusterIds) {
                if (!receivedClusterIds.contains(id)) {
                    receivedWeatherClusters.add(createNewWeatherCluster(id, receivedWeatherClusters));
                }

            }
        }
        return receivedWeatherClusters;
    }

    private WeatherCluster createNewWeatherCluster(Integer clusterId, Set<WeatherCluster> receivedWeatherClusters) {
        log.debug("creating the placeholder weather clusters");
        FiscalWeek inStoreWeek = Optional.ofNullable(receivedWeatherClusters)
                .stream()
                .flatMap(Collection::stream)
                .findFirst()
                .map(WeatherCluster::getInStoreDate)
                .orElse(null);
        FiscalWeek markdownWeek = Optional.ofNullable(receivedWeatherClusters)
                .stream()
                .flatMap(Collection::stream)
                .findFirst()
                .map(WeatherCluster::getMarkDownDate)
                .orElse(null);
        WeatherCluster weatherCluster = new WeatherCluster();
        ClusterType clusterType = new ClusterType();
        clusterType.setAnalyticsClusterId(clusterId);
        if (clusterId == 0) {
            clusterType.setAnalyticsClusterDesc("all");
        } else {
            clusterType.setAnalyticsClusterDesc("cluster " + clusterId.toString());
        }
        weatherCluster.setType(clusterType);
        weatherCluster.setInStoreDate(inStoreWeek);
        weatherCluster.setMarkDownDate(markdownWeek);
        return weatherCluster;
    }


    /**
     * This method check & sets StrategyStyleClus, and calls Customer Choice if exits.
     *
     * @param strategyFlClusEligRanking
     * @param styles
     * @return StrategyStyleClus
     */

    private Set<StrategyStyleClus> setStrategyStyleCluster(List<Style> styles, StrategyFlClusEligRanking strategyFlClusEligRanking) {
        Set<StrategyStyleClus> strategyStyleCluses = Optional.ofNullable(strategyFlClusEligRanking.getStrategyStyleCluses())
                .orElse(new HashSet<>());
        for (Style style : styles) {
            StrategyStyleClusId strategyStyleClusId = new StrategyStyleClusId(strategyFlClusEligRanking.getStrategyFlClusEligRankingId(), style.getStyleNbr());
            //Validate if a strategyStyle is already present (or) else create a new
            StrategyStyleClus strategyStyleClus = Optional.of(strategyStyleCluses)
                    .stream()
                    .flatMap(Collection::stream).filter(strategyStyleClus1 -> strategyStyleClus1.getStrategyStyleClusId().equals(strategyStyleClusId))
                    .findFirst()
                    .orElse(new StrategyStyleClus());
            //Set
            if (strategyStyleClus.getStrategyStyleClusId() == null) {
                strategyStyleClus.setStrategyStyleClusId(strategyStyleClusId);
            }
            if (!CollectionUtils.isEmpty(style.getCustomerChoices())) {
                //fetch & set the StrategyCCClusterEligRanking entity
                strategyStyleClus.setStrategyCcClusEligRankings(setStrategyCCClusterEligRanking(style.getCustomerChoices(), strategyStyleClus));
            }
            strategyStyleCluses.add(strategyStyleClus);
        }
        return strategyStyleCluses;
    }

    /**
     * This method loops through the cc list, validated if exits & also weather cluster with in Ccs, if so calls the checkAndSetStrategyClusterCcMetrics.
     *
     * @param customerChoices
     * @param strategyStyleClus
     * @return StrategyCcClusEligRanking
     */

    private Set<StrategyCcClusEligRanking> setStrategyCCClusterEligRanking(List<CustomerChoice> customerChoices,
                                                                           StrategyStyleClus strategyStyleClus) {
        Set<StrategyCcClusEligRanking> strategyCcClusEligRankings = Optional.ofNullable(
                strategyStyleClus.getStrategyCcClusEligRankings()).orElse(new HashSet<>());
        for (CustomerChoice cc : customerChoices) {
            WeatherCluster weatherClusterCc = CommonUtil.getWeatherClusterCc(cc);
            StrategyCcClusEligRankingId strategyCcClusEligRankingId = new StrategyCcClusEligRankingId(strategyStyleClus.getStrategyStyleClusId(), cc.getCcId());
            //Validate if a strategyCc is already present (or) else create a new
            StrategyCcClusEligRanking strategyCcClusEligRanking = Optional.of(strategyCcClusEligRankings)
                    .stream()
                    .flatMap(Collection::stream).filter(strategyCcClusEligRanking1 -> strategyCcClusEligRanking1.getStrategyCcClusEligRankingId().equals(strategyCcClusEligRankingId))
                    .findFirst()
                    .orElse(new StrategyCcClusEligRanking());
            //Get Customer choice weather clusters object
            if (strategyCcClusEligRanking.getStrategyCcClusEligRankingId() == null) {
                strategyCcClusEligRanking.setStrategyCcClusEligRankingId(strategyCcClusEligRankingId);
                //fetch & set the StrategyFlMktClusEligs entity
                strategyCcClusEligRanking.setStrategyCcMktClusEligs(fetchStrategyCcMktCustElig(strategyCcClusEligRanking, strategyCcClusEligRankingId));
            }
            //default isELig to True
            strategyCcClusEligRanking.setIsEligibleFlag(1);
            strategyCcClusEligRanking.setInStoreYrWk(CommonUtil.getInStoreYrWk(weatherClusterCc));
            strategyCcClusEligRanking.setInStoreYrWkDesc(CommonUtil.getInStoreYrWkDesc(weatherClusterCc));
            strategyCcClusEligRanking.setMarkDownYrWk(CommonUtil.getMarkdownYrWk(weatherClusterCc));
            strategyCcClusEligRanking.setMarkDownYrWkDesc(CommonUtil.getMarkdownYrWkDesc(weatherClusterCc));
            strategyCcClusEligRankings.add(strategyCcClusEligRanking);
        }
        return strategyCcClusEligRankings;
    }

    private Set<StrategyCcMktClusElig> fetchStrategyCcMktCustElig(StrategyCcClusEligRanking strategyCcClusEligRanking,
                                                                  StrategyCcClusEligRankingId strategyCcClusEligRankingId) {

        Set<StrategyCcMktClusElig> strategyCcMktClusElig = Optional.ofNullable(strategyCcClusEligRanking.getStrategyCcMktClusEligs())
                .orElse(new HashSet<>());
        Integer analyticsClusterId = Optional.ofNullable(strategyCcClusEligRankingId.getStrategyStyleClusId())
                .map(StrategyStyleClusId::getStrategyFlClusEligRankingId)
                .map(StrategyFlClusEligRankingId::getPlanClusterStrategyId)
                .map(PlanClusterStrategyId::getAnalyticsClusterId)
                .orElse(null);

        //If Weather cluster is 1 set HI & PR as includeOffshore Mkt
        if (analyticsClusterId != null && analyticsClusterId == 1 && CollectionUtils.isEmpty(strategyCcMktClusElig)) {
            strategyCcMktClusElig =
                    Optional.ofNullable(appProperties.getCluster1OffshoreList())
                            .stream()
                            .flatMap(Collection::stream)
                            .map(offshore -> setStrategyCcMarketClusElig(strategyCcClusEligRankingId,
                                    IncludeOffshoreMkt.getChannelIdFromName(offshore)))
                            .collect(Collectors.toSet());
        }//If Weather cluster is 2 set HI as includeOffshore Mkt
        else if (analyticsClusterId != null
                && analyticsClusterId == 2 && CollectionUtils.isEmpty(strategyCcMktClusElig)) {
            strategyCcMktClusElig =
                    Optional.ofNullable(appProperties.getCluster2OffshoreList())
                            .stream()
                            .flatMap(Collection::stream)
                            .map(offshore -> setStrategyCcMarketClusElig(strategyCcClusEligRankingId,
                                    IncludeOffshoreMkt.getChannelIdFromName(offshore)))
                            .collect(Collectors.toSet());
        }

        //If Weather cluster is 7 set AK as includeOffshore Mkt
        else if (analyticsClusterId != null && analyticsClusterId == 7 && CollectionUtils.isEmpty(strategyCcMktClusElig)) {
            strategyCcMktClusElig =
                    Optional.ofNullable(appProperties.getCluster7OffshoreList())
                            .stream()
                            .flatMap(Collection::stream)
                            .map(offshore -> setStrategyCcMarketClusElig(strategyCcClusEligRankingId,
                                    IncludeOffshoreMkt.getChannelIdFromName(offshore)))
                            .collect(Collectors.toSet());
        }
        return strategyCcMktClusElig;
    }

    /**
     * This method check & sets StrategyFlMktClusElig.
     *
     * @param strategyFlClusEligRankingId
     * @param offshoreId
     * @return StrategyFlMktClusElig
     */
    private StrategyFlMktClusElig setStrategyFlMarketClusElig(StrategyFlClusEligRankingId strategyFlClusEligRankingId,
                                                              Integer offshoreId) {
        StrategyFlMktClusEligId strategyFlMktClusEligId = new StrategyFlMktClusEligId(strategyFlClusEligRankingId, offshoreId);
        log.debug("Check if a strategyFlMktClusElig Id : {} already exists or not", strategyFlMktClusEligId.toString());
        StrategyFlMktClusElig strategyFlMktClusElig = new StrategyFlMktClusElig();
        strategyFlMktClusElig.setStrategyFlMktClusEligId(strategyFlMktClusEligId);
        return strategyFlMktClusElig;

    }

    /**
     * This method check & sets StrategyFlMktClusElig.
     *
     * @param strategyFlClusPrgmEligRankingId
     * @param offshoreId
     * @return FinelineMarketClusterPrgElg
     */
    private FinelineMarketClusterPrgElg setFinelineMarketClusterPrgElg(StrategyFlClusPrgmEligRankingId strategyFlClusPrgmEligRankingId,
                                                                       Integer offshoreId) {
        FinelineMarketClusterPrgElgId finelineMarketClusterPrgElgId = new FinelineMarketClusterPrgElgId(strategyFlClusPrgmEligRankingId, offshoreId);
        log.debug("Check if a finelineMarketClusterPrgElg Id : {} already exists or not", finelineMarketClusterPrgElgId.toString());
        FinelineMarketClusterPrgElg finelineMarketClusterPrgElg = new FinelineMarketClusterPrgElg();
        finelineMarketClusterPrgElg.setFinelineMarketClusterPrgElgId(finelineMarketClusterPrgElgId);
        return finelineMarketClusterPrgElg;

    }

    /**
     * This method check & sets EligCcMktClusProg.
     *
     * @param eligCcClusProgId
     * @param offshoreId
     * @return EligCcMktClusProg
     */
    private EligCcMktClusProg setCcMarketClusterPrgElg(EligCcClusProgId eligCcClusProgId,
                                                       Integer offshoreId) {
        EligCcMktClusProgId eligCcMktClusProgId = new EligCcMktClusProgId(eligCcClusProgId, offshoreId);
        log.debug("Check if a finelineMarketClusterPrgElg Id : {} already exists or not", eligCcMktClusProgId);
        EligCcMktClusProg eligCcMktClusProg = new EligCcMktClusProg();
        eligCcMktClusProg.setEligCcMktClusProgId(eligCcMktClusProgId);
        return eligCcMktClusProg;

    }

    /**
     * x
     * This method check & sets StrategyFlClusMetrics.
     *
     * @param strategyFlClusEligRanking
     * @param weatherCluster
     * @return StrategyFlClusMetrics
     */
    private StrategyFlClusMetrics setStrategyFlClusterMetric(WeatherCluster weatherCluster,
                                                             StrategyFlClusEligRanking strategyFlClusEligRanking) {
        StrategyFlClusEligRankingId strategyFlClusEligRankingId = strategyFlClusEligRanking.getStrategyFlClusEligRankingId();
        log.debug("Check if a strategyFlClusMetrics Id : {} already exists or not", strategyFlClusEligRankingId.toString());
        StrategyFlClusMetrics strategyFlClusMetrics = Optional.ofNullable(strategyFlClusEligRanking.getStrategyFlClusMetrics())
                .orElse(new StrategyFlClusMetrics());
        if (strategyFlClusMetrics.getStrategyFlClusEligRankingId() == null) {
            strategyFlClusMetrics.setStrategyFlClusEligRankingId(strategyFlClusEligRankingId);
        }
        strategyFlClusMetrics.setForecastedDollars(weatherCluster.getForecastedSales());
        strategyFlClusMetrics.setForecastedUnits(weatherCluster.getForecastedUnits());
        strategyFlClusMetrics.setOnHandQty(weatherCluster.getOnHandQty());
        strategyFlClusMetrics.setSalesDollars(weatherCluster.getLySales());
        strategyFlClusMetrics.setSalesUnits(weatherCluster.getLyUnits());
        strategyFlClusMetrics.setSalesToStockRatio(weatherCluster.getSalesToStockRatio());
        return strategyFlClusMetrics;
    }

    /**
     * gets the ClusterType Id.
     *
     * @param type
     * @return Integer
     */
    private Integer fetchAnalyticsClusterId(ClusterType type) {
        return Optional.ofNullable(type)
                .map(ClusterType::getAnalyticsClusterId)
                .orElseThrow(() -> new CustomException("Please provide a analyticsClusterId"));
    }

    /**
     * gets the ClusterType desc.
     *
     * @param type
     * @return String
     */

    private String fetchAnalyticsClusterTypeDesc(ClusterType type) {
        return Optional.ofNullable(type)
                .map(ClusterType::getAnalyticsClusterDesc)
                .orElseThrow(() -> new CustomException("Please provide a analyticsClusterDesc"));
    }

    /**
     * This method check & sets StrategyFlMktClusElig.
     *
     * @param strategyCcClusEligRankingId
     * @param offshoreId
     * @return StrategyFlMktClusElig
     */
    private StrategyCcMktClusElig setStrategyCcMarketClusElig(StrategyCcClusEligRankingId strategyCcClusEligRankingId,
                                                              Integer offshoreId) {
        StrategyCcMktClusEligId strategyCcMktClusEligId = new StrategyCcMktClusEligId(strategyCcClusEligRankingId, offshoreId);
        log.debug("Check if a strategyFlMktClusElig Id : {} already exists or not", strategyCcMktClusEligId);
        StrategyCcMktClusElig strategyCcMktClusElig = new StrategyCcMktClusElig();
        strategyCcMktClusElig.setStrategyCcMktClusEligId(strategyCcMktClusEligId);
        return strategyCcMktClusElig;

    }


}
