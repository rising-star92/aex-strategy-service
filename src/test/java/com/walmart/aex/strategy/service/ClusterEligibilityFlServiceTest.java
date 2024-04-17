package com.walmart.aex.strategy.service;

import com.walmart.aex.strategy.dto.request.*;
import com.walmart.aex.strategy.entity.PlanStrategyId;
import com.walmart.aex.strategy.entity.StrategyCcClusEligRanking;
import com.walmart.aex.strategy.entity.StrategyFlClusEligRanking;
import com.walmart.aex.strategy.entity.StrategyStyleClus;
import com.walmart.aex.strategy.enums.IncludeOffshoreMkt;
import com.walmart.aex.strategy.properties.AppProperties;
import com.walmart.aex.strategy.repository.StrategyCcClusEligRankingRepository;
import com.walmart.aex.strategy.repository.StrategyFlClusEligRankingRepository;
import com.walmart.aex.strategy.util.GetStrategyFlClusEligRankingUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.powermock.api.mockito.PowerMockito;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class ClusterEligibilityFlServiceTest {
    @InjectMocks
    private ClusterEligibilityFlService clusterEligibilityFlService;

    @Mock
    private StrategyFlClusEligRankingRepository strategyFlClusEligRankingRepository;

    @Mock
    private StrategyCcClusEligRankingRepository strategyCcClusEligRankingRepository;

    @Test
    void testUpdateFlMetricsAllSetFalsePartial() {
        //Arrange
        UpdatedFields updatedFields = new UpdatedFields();
        Field weatherClusterField = new Field();
        weatherClusterField.setKey("isEligible");
        weatherClusterField.setValue("false");
        updatedFields.setWeatherCluster(Arrays.asList(weatherClusterField));

        Fineline fineline = new Fineline();
        fineline.setFinelineNbr(51);
        fineline.setFinelineName("Women Hoodie");
        fineline.setUpdatedFields(updatedFields);

        Strategy strategy = new Strategy();
        List<WeatherCluster> weatherClusterList = new ArrayList<>();
        WeatherCluster weatherCluster = new WeatherCluster();

        ClusterType type = new ClusterType();
        type.setAnalyticsClusterId(0);
        type.setAnalyticsClusterDesc("all");
        weatherCluster.setType(type);
        weatherCluster.setIsEligible(false);
        weatherClusterList.add(weatherCluster);
        strategy.setWeatherClusters(weatherClusterList);
        fineline.setStrategy(strategy);
        PlanStrategyId planStrategyId = GetStrategyFlClusEligRankingUtil.getPlanStrategyId();
        doReturn(GetStrategyFlClusEligRankingUtil.getStrategyFlClusEligRankingsPartial(false))
                .when(strategyFlClusEligRankingRepository).findStrategyFlClusEligRankingByStrategyFlClusEligRankingId_PlanClusterStrategyId_PlanStrategyIdAndStrategyFlClusEligRankingId_lvl3NbrAndStrategyFlClusEligRankingId_lvl4NbrAndStrategyFlClusEligRankingId_finelineNbr(any(PlanStrategyId.class), anyInt(), anyInt(), any());
        Set<String> updatedField = new HashSet<>();

        //Act
        Set<StrategyFlClusEligRanking> strategyFlClusEligRankings = clusterEligibilityFlService.updateClusterEligibilityMetrics(fineline,
                planStrategyId, 34556, 456,updatedField);
        //Assert
        Set<Integer> resultFlag =
                Optional.ofNullable(strategyFlClusEligRankings)
                        .stream()
                        .flatMap(Collection::stream)
                        .map(StrategyFlClusEligRanking::getIsEligibleFlag)
                        .collect(Collectors.toSet());

        Set<Integer> ccResultsFlag = getAllCcFlagStatusPartial(strategyFlClusEligRankings);

        assertEquals(1, resultFlag.size());
        assertTrue(resultFlag.contains(0));
        //cc also set to False
        assertEquals(1, ccResultsFlag.size());
        assertTrue(ccResultsFlag.contains(0));

    }

    private Set<Integer> getAllCcFlagStatus(Set<StrategyFlClusEligRanking> strategyFlClusEligRankings){
        Set<Integer> ccFlags = new HashSet<>();
        Optional.ofNullable(strategyFlClusEligRankings)
                .stream()
                .flatMap(Collection::stream)
                .map(StrategyFlClusEligRanking::getStrategyStyleCluses)
                .forEach(strategyStyleCluses -> {
                    Optional.ofNullable(strategyStyleCluses)
                            .stream()
                            .flatMap(Collection::stream)
                            .forEach(strategyStyleClus -> {
                                ccFlags.addAll(Optional.ofNullable(strategyStyleClus.getStrategyCcClusEligRankings())
                                        .stream()
                                        .flatMap(Collection::stream)
                                        .map(StrategyCcClusEligRanking::getIsEligibleFlag)
                                        .collect(Collectors.toSet()));
                            });
                });
        return ccFlags;
    }

    private Set<Integer> getAllCcFlagStatusPartial(Set<StrategyFlClusEligRanking> strategyFlClusEligRankings){
        Set<Integer> ccFlags = new HashSet<>();
        Optional.ofNullable(strategyFlClusEligRankings)
                .stream()
                .flatMap(Collection::stream)
                .map(StrategyFlClusEligRanking::getStrategyStyleCluses)
                .forEach(strategyStyleCluses -> {
                    Optional.ofNullable(strategyStyleCluses)
                            .stream()
                            .flatMap(Collection::stream)
                            .forEach(strategyStyleClus -> {
                                ccFlags.addAll(Optional.ofNullable(strategyStyleClus.getStrategyCcClusEligRankings())
                                        .stream()
                                        .flatMap(Collection::stream)
                                        .map(StrategyCcClusEligRanking::getIsEligibleFlag)
                                        .collect(Collectors.toSet()));
                            });
                });
        return ccFlags;
    }

    private Integer getClusterNCcFlagStatus(Set<StrategyFlClusEligRanking> strategyFlClusEligRankings){

        return Optional.ofNullable(strategyFlClusEligRankings)
                .stream()
                .flatMap(Collection::stream)
                .filter(strategyFlClusEligRanking -> strategyFlClusEligRanking.getStrategyFlClusEligRankingId().getPlanClusterStrategyId().getAnalyticsClusterId().equals(0))
                .findFirst()
                .map(StrategyFlClusEligRanking::getStrategyStyleCluses)
                .stream()
                .flatMap(Collection::stream)
                .findFirst()
                .map(StrategyStyleClus::getStrategyCcClusEligRankings)
                .stream()
                .flatMap(Collection::stream)
                .findFirst()
                .map(StrategyCcClusEligRanking::getIsEligibleFlag)
                .orElse(null);
    }

    private Integer getClusterNCcFlagStatusPartial(Set<StrategyFlClusEligRanking> strategyFlClusEligRankings){

        return Optional.ofNullable(strategyFlClusEligRankings)
                .stream()
                .flatMap(Collection::stream)
                .filter(strategyFlClusEligRanking -> strategyFlClusEligRanking.getStrategyFlClusEligRankingId().getPlanClusterStrategyId().getAnalyticsClusterId().equals(0))
                .findFirst()
                .map(StrategyFlClusEligRanking::getStrategyStyleCluses)
                .stream()
                .flatMap(Collection::stream)
                .findFirst()
                .map(StrategyStyleClus::getStrategyCcClusEligRankings)
                .stream()
                .flatMap(Collection::stream)
                .findFirst()
                .map(StrategyCcClusEligRanking::getIsEligibleFlag)
                .orElse(null);
    }

    @Test
    void testUpdateFlMetricsAllSetTruePartial() {
        //Arrange
        UpdatedFields updatedFields = new UpdatedFields();
        Field weatherClusterField = new Field();
        weatherClusterField.setKey("isEligible");
        weatherClusterField.setValue("true");
        updatedFields.setWeatherCluster(Arrays.asList(weatherClusterField));

        Fineline fineline = new Fineline();
        fineline.setFinelineNbr(51);
        fineline.setFinelineName("Women Hoodie");
        fineline.setUpdatedFields(updatedFields);

        Strategy strategy = new Strategy();
        List<WeatherCluster> weatherClusterList = new ArrayList<>();
        WeatherCluster weatherCluster = new WeatherCluster();

        ClusterType type = new ClusterType();
        type.setAnalyticsClusterId(0);
        type.setAnalyticsClusterDesc("all");
        weatherCluster.setType(type);
        weatherCluster.setIsEligible(true);
        weatherClusterList.add(weatherCluster);
        strategy.setWeatherClusters(weatherClusterList);
        fineline.setStrategy(strategy);
        PlanStrategyId planStrategyId = GetStrategyFlClusEligRankingUtil.getPlanStrategyId();
        doReturn(GetStrategyFlClusEligRankingUtil.getStrategyFlClusEligRankingsPartial(false))
                .when(strategyFlClusEligRankingRepository).findStrategyFlClusEligRankingByStrategyFlClusEligRankingId_PlanClusterStrategyId_PlanStrategyIdAndStrategyFlClusEligRankingId_lvl3NbrAndStrategyFlClusEligRankingId_lvl4NbrAndStrategyFlClusEligRankingId_finelineNbr(any(PlanStrategyId.class), anyInt(), anyInt(), any());
        Set<String> updatedField = new HashSet<>();

        //Act
        Set<StrategyFlClusEligRanking> strategyFlClusEligRankings = clusterEligibilityFlService.updateClusterEligibilityMetrics(fineline,
                planStrategyId, 34556, 456,updatedField);
        //Assert
        Set<Integer> resultFlag =
                Optional.ofNullable(strategyFlClusEligRankings)
                        .stream()
                        .flatMap(Collection::stream)
                        .map(StrategyFlClusEligRanking::getIsEligibleFlag)
                        .collect(Collectors.toSet());

        Set<Integer> ccResultsFlag = getAllCcFlagStatus(strategyFlClusEligRankings);

        assertEquals(1, resultFlag.size());
        assertTrue(resultFlag.contains(1));
        //cc also set to True
        assertEquals(1, ccResultsFlag.size());
        assertTrue(ccResultsFlag.contains(1));
    }

    @Test
    void testUpdateFlMetricsClusterNFalseWithCluster2() {
        //Arrange
        UpdatedFields updatedFields = new UpdatedFields();
        Field weatherClusterField = new Field();
        weatherClusterField.setKey("isEligible");
        weatherClusterField.setValue("false");
        updatedFields.setWeatherCluster(Arrays.asList(weatherClusterField));

        Fineline fineline = new Fineline();
        fineline.setFinelineNbr(51);
        fineline.setFinelineName("Women Hoodie");
        fineline.setUpdatedFields(updatedFields);

        Strategy strategy = new Strategy();
        List<WeatherCluster> weatherClusterList = new ArrayList<>();
        WeatherCluster weatherCluster = new WeatherCluster();

        ClusterType type = new ClusterType();
        type.setAnalyticsClusterId(2);
        type.setAnalyticsClusterDesc("cluster 2");
        weatherCluster.setType(type);
        weatherCluster.setIsEligible(false);
        weatherClusterList.add(weatherCluster);
        strategy.setWeatherClusters(weatherClusterList);
        fineline.setStrategy(strategy);
        PlanStrategyId planStrategyId = GetStrategyFlClusEligRankingUtil.getPlanStrategyId();
        doReturn(GetStrategyFlClusEligRankingUtil.getStrategyFlClusEligRankings(false))
                .when(strategyFlClusEligRankingRepository).findStrategyFlClusEligRankingByStrategyFlClusEligRankingId_PlanClusterStrategyId_PlanStrategyIdAndStrategyFlClusEligRankingId_lvl3NbrAndStrategyFlClusEligRankingId_lvl4NbrAndStrategyFlClusEligRankingId_finelineNbr(any(PlanStrategyId.class), anyInt(), anyInt(), any());
        Set<String> updatedField = new HashSet<>();

        //Act
        Set<StrategyFlClusEligRanking> strategyFlClusEligRankings = clusterEligibilityFlService.updateClusterEligibilityMetrics(fineline,
                planStrategyId, 34556, 456,updatedField);
        //Assert
        Set<Integer> resultFlFlag =
                Optional.ofNullable(strategyFlClusEligRankings)
                        .stream()
                        .flatMap(Collection::stream)
                        .map(StrategyFlClusEligRanking::getIsEligibleFlag)
                        .collect(Collectors.toSet());

        Set<Integer> resultCcFlag = getAllCcFlagStatus(strategyFlClusEligRankings);

        Integer allFlag = getAllFlagAtFineline(strategyFlClusEligRankings);

        Integer allCcFlag = getClusterNCcFlagStatus(strategyFlClusEligRankings);
        //cluster 1 is false and others are true
        assertEquals(3, resultFlFlag.size());
        //all flag should be false
        assertEquals(2, allFlag);
        //ccFlag
        assertEquals(3, resultCcFlag.size());
        assertEquals(2, allCcFlag);
    }
    @Test
    void testUpdateFlMetricsClusterNFalsePartial() {
        //Arrange
        UpdatedFields updatedFields = new UpdatedFields();
        Field weatherClusterField = new Field();
        weatherClusterField.setKey("isEligible");
        weatherClusterField.setValue("false");
        updatedFields.setWeatherCluster(Arrays.asList(weatherClusterField));

        Fineline fineline = new Fineline();
        fineline.setFinelineNbr(51);
        fineline.setFinelineName("Women Hoodie");
        fineline.setUpdatedFields(updatedFields);

        Strategy strategy = new Strategy();
        List<WeatherCluster> weatherClusterList = new ArrayList<>();
        WeatherCluster weatherCluster = new WeatherCluster();

        ClusterType type = new ClusterType();
        type.setAnalyticsClusterId(1);
        type.setAnalyticsClusterDesc("cluster 1");
        weatherCluster.setType(type);
        weatherCluster.setIsEligible(false);
        weatherClusterList.add(weatherCluster);
        strategy.setWeatherClusters(weatherClusterList);
        fineline.setStrategy(strategy);
        PlanStrategyId planStrategyId = GetStrategyFlClusEligRankingUtil.getPlanStrategyId();
        doReturn(GetStrategyFlClusEligRankingUtil.getStrategyFlClusEligRankingsPartial(false))
                .when(strategyFlClusEligRankingRepository).findStrategyFlClusEligRankingByStrategyFlClusEligRankingId_PlanClusterStrategyId_PlanStrategyIdAndStrategyFlClusEligRankingId_lvl3NbrAndStrategyFlClusEligRankingId_lvl4NbrAndStrategyFlClusEligRankingId_finelineNbr(any(PlanStrategyId.class), anyInt(), anyInt(), any());
        Set<String> updatedField = new HashSet<>();

        //Act
        Set<StrategyFlClusEligRanking> strategyFlClusEligRankings = clusterEligibilityFlService.updateClusterEligibilityMetrics(fineline,
                planStrategyId, 34556, 456,updatedField);
        //Assert
        Set<Integer> resultFlFlag =
                Optional.ofNullable(strategyFlClusEligRankings)
                        .stream()
                        .flatMap(Collection::stream)
                        .map(StrategyFlClusEligRanking::getIsEligibleFlag)
                        .collect(Collectors.toSet());

        Set<Integer> resultCcFlag = getAllCcFlagStatusPartial(strategyFlClusEligRankings);

        Integer allFlag = getAllFlagAtFinelinePartial(strategyFlClusEligRankings);

        Integer allCcFlag = getClusterNCcFlagStatusPartial(strategyFlClusEligRankings);
        //cluster 1 is false and others are true
        assertEquals(3, resultFlFlag.size());
        //all flag should be false
        assertEquals(2, allFlag);
        //ccFlag
        assertEquals(3, resultCcFlag.size());
        assertEquals(2, allCcFlag);
    }

    private Integer getAllFlagAtFineline(Set<StrategyFlClusEligRanking> strategyFlClusEligRankings){
        return Optional.ofNullable(strategyFlClusEligRankings)
                .stream()
                .flatMap(Collection::stream)
                .filter(strategyFlClusEligRanking -> strategyFlClusEligRanking.getStrategyFlClusEligRankingId().getPlanClusterStrategyId().getAnalyticsClusterId().equals(0))
                .findFirst()
                .map(StrategyFlClusEligRanking::getIsEligibleFlag)
                .orElse(null);
    }

    private Integer getAllFlagAtFinelinePartial(Set<StrategyFlClusEligRanking> strategyFlClusEligRankings){
        return Optional.ofNullable(strategyFlClusEligRankings)
                .stream()
                .flatMap(Collection::stream)
                .filter(strategyFlClusEligRanking -> strategyFlClusEligRanking.getStrategyFlClusEligRankingId().getPlanClusterStrategyId().getAnalyticsClusterId().equals(0))
                .findFirst()
                .map(StrategyFlClusEligRanking::getIsEligibleFlag)
                .orElse(null);
    }

    @Test
    void testUpdateFlMetricsInStore() {
        //Arrange
        UpdatedFields updatedFields = new UpdatedFields();
        Field weatherClusterField = new Field();
        weatherClusterField.setKey("inStoreDate");
        weatherClusterField.setValue("FYE2023WK21");
        updatedFields.setWeatherCluster(Arrays.asList(weatherClusterField));

        Fineline fineline = new Fineline();
        fineline.setFinelineNbr(51);
        fineline.setFinelineName("Women Hoodie");
        fineline.setUpdatedFields(updatedFields);
        FiscalWeek fiscalWeek = new FiscalWeek();
        fiscalWeek.setFiscalWeekDesc("FYE2023WK21");

        Strategy strategy = new Strategy();
        List<WeatherCluster> weatherClusterList = new ArrayList<>();
        WeatherCluster weatherCluster = new WeatherCluster();

        ClusterType type = new ClusterType();
        type.setAnalyticsClusterId(1);
        type.setAnalyticsClusterDesc("cluster 1");
        weatherCluster.setType(type);
        weatherCluster.setIsEligible(true);
        weatherCluster.setInStoreDate(fiscalWeek);
        weatherClusterList.add(weatherCluster);
        strategy.setWeatherClusters(weatherClusterList);
        fineline.setStrategy(strategy);
        PlanStrategyId planStrategyId = GetStrategyFlClusEligRankingUtil.getPlanStrategyId();
        doReturn(GetStrategyFlClusEligRankingUtil.getStrategyFlClusEligRankings(false))
                .when(strategyFlClusEligRankingRepository).findStrategyFlClusEligRankingByStrategyFlClusEligRankingId_PlanClusterStrategyId_PlanStrategyIdAndStrategyFlClusEligRankingId_lvl3NbrAndStrategyFlClusEligRankingId_lvl4NbrAndStrategyFlClusEligRankingId_finelineNbr(any(PlanStrategyId.class), anyInt(), anyInt(), any());
        Set<String> updatedField = new HashSet<>();

        //Act
        Set<StrategyFlClusEligRanking> strategyFlClusEligRankings = clusterEligibilityFlService.updateClusterEligibilityMetrics(fineline,
                planStrategyId, 34556, 456,updatedField);
        //Assert
        String instoreDate = Optional.ofNullable(strategyFlClusEligRankings)
                .stream()
                .flatMap(Collection::stream)
                .filter(strategyFlClusEligRanking -> strategyFlClusEligRanking.getStrategyFlClusEligRankingId().getPlanClusterStrategyId().getAnalyticsClusterId().equals(1))
                .findFirst()
                .map(StrategyFlClusEligRanking::getInStoreYrWkDesc)
                .orElse(null);

        assertEquals("FYE2023WK21", instoreDate);
    }

    @Test
    void testUpdateFlMetricsMarkDown() {
        //Arrange
        UpdatedFields updatedFields = new UpdatedFields();
        Field weatherClusterField = new Field();
        weatherClusterField.setKey("markDownDate");
        weatherClusterField.setValue("FYE2023WK31");
        updatedFields.setWeatherCluster(Arrays.asList(weatherClusterField));

        Fineline fineline = new Fineline();
        fineline.setFinelineNbr(51);
        fineline.setFinelineName("Women Hoodie");
        fineline.setUpdatedFields(updatedFields);
        FiscalWeek fiscalWeek = new FiscalWeek();
        fiscalWeek.setFiscalWeekDesc("FYE2023WK31");

        Strategy strategy = new Strategy();
        List<WeatherCluster> weatherClusterList = new ArrayList<>();
        WeatherCluster weatherCluster = new WeatherCluster();

        ClusterType type = new ClusterType();
        type.setAnalyticsClusterId(1);
        type.setAnalyticsClusterDesc("cluster 1");
        weatherCluster.setType(type);
        weatherCluster.setIsEligible(true);
        weatherCluster.setMarkDownDate(fiscalWeek);
        weatherClusterList.add(weatherCluster);
        strategy.setWeatherClusters(weatherClusterList);
        fineline.setStrategy(strategy);
        PlanStrategyId planStrategyId = GetStrategyFlClusEligRankingUtil.getPlanStrategyId();
        doReturn(GetStrategyFlClusEligRankingUtil.getStrategyFlClusEligRankings(false))
                .when(strategyFlClusEligRankingRepository).findStrategyFlClusEligRankingByStrategyFlClusEligRankingId_PlanClusterStrategyId_PlanStrategyIdAndStrategyFlClusEligRankingId_lvl3NbrAndStrategyFlClusEligRankingId_lvl4NbrAndStrategyFlClusEligRankingId_finelineNbr(any(PlanStrategyId.class), anyInt(), anyInt(), any());
        Set<String> updatedField = new HashSet<>();

        //Act
        Set<StrategyFlClusEligRanking> strategyFlClusEligRankings = clusterEligibilityFlService.updateClusterEligibilityMetrics(fineline,
                planStrategyId, 34556, 456,updatedField);
        //Assert
        String markDownDate = Optional.ofNullable(strategyFlClusEligRankings)
                .stream()
                .flatMap(Collection::stream)
                .filter(strategyFlClusEligRanking -> strategyFlClusEligRanking.getStrategyFlClusEligRankingId().getPlanClusterStrategyId().getAnalyticsClusterId().equals(1))
                .findFirst()
                .map(StrategyFlClusEligRanking::getMarkDownYrWkDesc)
                .orElse(null);

        assertEquals("FYE2023WK31", markDownDate);
    }

    @Test
    void testUpdateFlMetricsRanking() {
        //Arrange
        UpdatedFields updatedFields = new UpdatedFields();
        Field weatherClusterField = new Field();
        weatherClusterField.setKey("ranking");
        weatherClusterField.setValue("1");
        updatedFields.setWeatherCluster(Arrays.asList(weatherClusterField));

        Fineline fineline = new Fineline();
        fineline.setFinelineNbr(51);
        fineline.setFinelineName("Women Hoodie");
        fineline.setUpdatedFields(updatedFields);
        FiscalWeek fiscalWeek = new FiscalWeek();
        fiscalWeek.setFiscalWeekDesc("FYE2023WK31");

        Strategy strategy = new Strategy();
        List<WeatherCluster> weatherClusterList = new ArrayList<>();
        WeatherCluster weatherCluster = new WeatherCluster();

        ClusterType type = new ClusterType();
        type.setAnalyticsClusterId(1);
        type.setAnalyticsClusterDesc("cluster 1");
        weatherCluster.setType(type);
        weatherCluster.setIsEligible(true);
        weatherCluster.setMarkDownDate(fiscalWeek);
        weatherCluster.setRanking(1);
        weatherClusterList.add(weatherCluster);
        strategy.setWeatherClusters(weatherClusterList);
        fineline.setStrategy(strategy);
        PlanStrategyId planStrategyId = GetStrategyFlClusEligRankingUtil.getPlanStrategyId();
        doReturn(GetStrategyFlClusEligRankingUtil.getStrategyFlClusEligRankings(false))
                .when(strategyFlClusEligRankingRepository).findStrategyFlClusEligRankingByStrategyFlClusEligRankingId_PlanClusterStrategyId_PlanStrategyIdAndStrategyFlClusEligRankingId_lvl3NbrAndStrategyFlClusEligRankingId_lvl4NbrAndStrategyFlClusEligRankingId_finelineNbr(any(PlanStrategyId.class), anyInt(), anyInt(), any());
        Set<String> updatedField = new HashSet<>();

        //Act
        Set<StrategyFlClusEligRanking> strategyFlClusEligRankings = clusterEligibilityFlService.updateClusterEligibilityMetrics(fineline,
                planStrategyId, 34556, 456,updatedField);
        //Assert
        Integer rank = Optional.ofNullable(strategyFlClusEligRankings)
                .stream()
                .flatMap(Collection::stream)
                .filter(strategyFlClusEligRanking -> strategyFlClusEligRanking.getStrategyFlClusEligRankingId().getPlanClusterStrategyId().getAnalyticsClusterId().equals(1))
                .findFirst()
                .map(StrategyFlClusEligRanking::getMerchantOverrideRank)
                .orElse(null);

        assertEquals(1, rank);
    }

    @Test
    void testUpdateFlMetricsExcludeOffshore() {
        //Arrange
        UpdatedFields updatedFields = new UpdatedFields();
        Field weatherClusterField = new Field();
        weatherClusterField.setKey("excludeOffshore");
        weatherClusterField.setValue("PR");
        updatedFields.setWeatherCluster(Arrays.asList(weatherClusterField));

        Fineline fineline = new Fineline();
        fineline.setFinelineNbr(51);
        fineline.setFinelineName("Women Hoodie");
        fineline.setUpdatedFields(updatedFields);
        FiscalWeek fiscalWeek = new FiscalWeek();
        fiscalWeek.setFiscalWeekDesc("FYE2023WK31");

        Strategy strategy = new Strategy();
        List<WeatherCluster> weatherClusterList = new ArrayList<>();
        WeatherCluster weatherCluster = new WeatherCluster();
        List<IncludeOffshoreMkt> includeOffshoreMkts = Arrays.asList(IncludeOffshoreMkt.PR);
        ClusterType type = new ClusterType();
        type.setAnalyticsClusterId(1);
        type.setAnalyticsClusterDesc("cluster 1");
        weatherCluster.setType(type);
        weatherCluster.setIsEligible(true);
        weatherCluster.setMarkDownDate(fiscalWeek);
        weatherCluster.setRanking(1);
        weatherCluster.setIncludeOffshore(includeOffshoreMkts);
        weatherClusterList.add(weatherCluster);
        strategy.setWeatherClusters(weatherClusterList);
        fineline.setStrategy(strategy);
        PlanStrategyId planStrategyId = GetStrategyFlClusEligRankingUtil.getPlanStrategyId();
        doReturn(GetStrategyFlClusEligRankingUtil.getStrategyFlClusEligRankings(false))
                .when(strategyFlClusEligRankingRepository).findStrategyFlClusEligRankingByStrategyFlClusEligRankingId_PlanClusterStrategyId_PlanStrategyIdAndStrategyFlClusEligRankingId_lvl3NbrAndStrategyFlClusEligRankingId_lvl4NbrAndStrategyFlClusEligRankingId_finelineNbr(any(PlanStrategyId.class), anyInt(), anyInt(), any());
        Set<String> updatedField = new HashSet<>();

        //Act
        Set<StrategyFlClusEligRanking> strategyFlClusEligRankings = clusterEligibilityFlService.updateClusterEligibilityMetrics(fineline,
                planStrategyId, 34556, 456,updatedField);
        //Assert
        Long count = Optional.ofNullable(strategyFlClusEligRankings)
                .stream()
                .flatMap(Collection::stream)
                .filter(strategyFlClusEligRanking -> strategyFlClusEligRanking.getStrategyFlClusEligRankingId().getPlanClusterStrategyId().getAnalyticsClusterId().equals(1))
                .findFirst()
                .map(StrategyFlClusEligRanking::getStrategyFlMktClusEligs)
                .stream().count();


        assertEquals(1l, count);
    }
    @Test
    void testUpdateFlMetricsExcludeOffshoreWithCluster2() {
        //Arrange
        UpdatedFields updatedFields = new UpdatedFields();
        Field weatherClusterField = new Field();
        weatherClusterField.setKey("excludeOffshore");
        weatherClusterField.setValue("HI");
        updatedFields.setWeatherCluster(Arrays.asList(weatherClusterField));

        Fineline fineline = new Fineline();
        fineline.setFinelineNbr(51);
        fineline.setFinelineName("Women Hoodie");
        fineline.setUpdatedFields(updatedFields);
        FiscalWeek fiscalWeek = new FiscalWeek();
        fiscalWeek.setFiscalWeekDesc("FYE2023WK31");

        Strategy strategy = new Strategy();
        List<WeatherCluster> weatherClusterList = new ArrayList<>();
        WeatherCluster weatherCluster = new WeatherCluster();
        List<IncludeOffshoreMkt> includeOffshoreMkts = Arrays.asList(IncludeOffshoreMkt.HI);
        ClusterType type = new ClusterType();
        type.setAnalyticsClusterId(2);
        type.setAnalyticsClusterDesc("cluster 2");
        weatherCluster.setType(type);
        weatherCluster.setIsEligible(true);
        weatherCluster.setMarkDownDate(fiscalWeek);
        weatherCluster.setRanking(1);
        weatherCluster.setIncludeOffshore(includeOffshoreMkts);
        weatherClusterList.add(weatherCluster);
        strategy.setWeatherClusters(weatherClusterList);
        fineline.setStrategy(strategy);
        PlanStrategyId planStrategyId = GetStrategyFlClusEligRankingUtil.getPlanStrategyId();
        doReturn(GetStrategyFlClusEligRankingUtil.getStrategyFlClusEligRankings(false))
                .when(strategyFlClusEligRankingRepository).findStrategyFlClusEligRankingByStrategyFlClusEligRankingId_PlanClusterStrategyId_PlanStrategyIdAndStrategyFlClusEligRankingId_lvl3NbrAndStrategyFlClusEligRankingId_lvl4NbrAndStrategyFlClusEligRankingId_finelineNbr(any(PlanStrategyId.class), anyInt(), anyInt(), any());
        Set<String> updatedField = new HashSet<>();

        //Act
        Set<StrategyFlClusEligRanking> strategyFlClusEligRankings = clusterEligibilityFlService.updateClusterEligibilityMetrics(fineline,
                planStrategyId, 34556, 456,updatedField);
        //Assert
        Long count = Optional.ofNullable(strategyFlClusEligRankings)
                .stream()
                .flatMap(Collection::stream)
                .filter(strategyFlClusEligRanking -> strategyFlClusEligRanking.getStrategyFlClusEligRankingId().getPlanClusterStrategyId().getAnalyticsClusterId().equals(1))
                .findFirst()
                .map(StrategyFlClusEligRanking::getStrategyFlMktClusEligs)
                .stream().count();


        assertEquals(1l, count);
    }
    @Test
    void testCase10ClusterNTruePartial() {
        //Arrange
        UpdatedFields updatedFields = new UpdatedFields();
        Field weatherClusterField = new Field();
        weatherClusterField.setKey("isEligible");
        weatherClusterField.setValue("true");
        updatedFields.setWeatherCluster(Arrays.asList(weatherClusterField));

        Fineline fineline = new Fineline();
        fineline.setFinelineNbr(51);
        fineline.setFinelineName("Women Hoodie");
        fineline.setUpdatedFields(updatedFields);

        Strategy strategy = new Strategy();
        List<WeatherCluster> weatherClusterList = new ArrayList<>();
        WeatherCluster weatherCluster = new WeatherCluster();

        ClusterType type = new ClusterType();
        type.setAnalyticsClusterId(1);
        type.setAnalyticsClusterDesc("cluster 1");
        weatherCluster.setType(type);
        weatherCluster.setIsEligible(true);
        weatherCluster.setIsEligibleFlag(1);
        weatherClusterList.add(weatherCluster);
        strategy.setWeatherClusters(weatherClusterList);
        fineline.setStrategy(strategy);
        PlanStrategyId planStrategyId = GetStrategyFlClusEligRankingUtil.getPlanStrategyId();
        doReturn(GetStrategyFlClusEligRankingUtil.getStrategyFlClusEligRankingsPartial(true))
                .when(strategyFlClusEligRankingRepository).findStrategyFlClusEligRankingByStrategyFlClusEligRankingId_PlanClusterStrategyId_PlanStrategyIdAndStrategyFlClusEligRankingId_lvl3NbrAndStrategyFlClusEligRankingId_lvl4NbrAndStrategyFlClusEligRankingId_finelineNbr(any(PlanStrategyId.class), anyInt(), anyInt(), any());
        Set<String> updatedField = new HashSet<>();

        //Act
        Set<StrategyFlClusEligRanking> strategyFlClusEligRankings = clusterEligibilityFlService.updateClusterEligibilityMetrics(fineline,
                planStrategyId, 34556, 456,updatedField);
        //Assert
        Set<Integer> resultFlFlag =
                Optional.ofNullable(strategyFlClusEligRankings)
                        .stream()
                        .flatMap(Collection::stream)
                        .map(StrategyFlClusEligRanking::getIsEligibleFlag)
                        .collect(Collectors.toSet());

        Set<Integer> resultCcFlag = getAllCcFlagStatusPartial(strategyFlClusEligRankings);

        Integer allFlag = getAllFlagAtFinelinePartial(strategyFlClusEligRankings);

        Integer allCcFlag = getClusterNCcFlagStatusPartial(strategyFlClusEligRankings);
        //cluster 1 is false and others are true
        assertEquals(1, resultFlFlag.size());
        //all flag should be false
        assertEquals(1, allFlag);
        //ccFlag
        assertEquals(1, resultCcFlag.size());
        assertEquals(1, allCcFlag);
    }

    @Test
    void testUpdateFlMetricsInStoreAll() {
        //Arrange
        UpdatedFields updatedFields = new UpdatedFields();
        Field weatherClusterField = new Field();
        weatherClusterField.setKey("inStoreDate");
        weatherClusterField.setValue("FYE2023WK21");
        updatedFields.setWeatherCluster(Arrays.asList(weatherClusterField));

        Fineline fineline = new Fineline();
        fineline.setFinelineNbr(51);
        fineline.setFinelineName("Women Hoodie");
        fineline.setUpdatedFields(updatedFields);
        FiscalWeek fiscalWeek = new FiscalWeek();
        fiscalWeek.setFiscalWeekDesc("FYE2023WK21");

        Strategy strategy = new Strategy();
        List<WeatherCluster> weatherClusterList = new ArrayList<>();
        WeatherCluster weatherCluster = new WeatherCluster();

        ClusterType type = new ClusterType();
        type.setAnalyticsClusterId(0);
        type.setAnalyticsClusterDesc("all");
        weatherCluster.setType(type);
        weatherCluster.setIsEligible(true);
        weatherCluster.setInStoreDate(fiscalWeek);
        weatherClusterList.add(weatherCluster);
        strategy.setWeatherClusters(weatherClusterList);
        fineline.setStrategy(strategy);
        PlanStrategyId planStrategyId = GetStrategyFlClusEligRankingUtil.getPlanStrategyId();
        doReturn(GetStrategyFlClusEligRankingUtil.getStrategyFlClusEligRankings(false))
                .when(strategyFlClusEligRankingRepository).findStrategyFlClusEligRankingByStrategyFlClusEligRankingId_PlanClusterStrategyId_PlanStrategyIdAndStrategyFlClusEligRankingId_lvl3NbrAndStrategyFlClusEligRankingId_lvl4NbrAndStrategyFlClusEligRankingId_finelineNbr(any(PlanStrategyId.class), anyInt(), anyInt(), any());
        Set<String> updatedField = new HashSet<>();

        //Act
        Set<StrategyFlClusEligRanking> strategyFlClusEligRankings = clusterEligibilityFlService.updateClusterEligibilityMetrics(fineline,
                planStrategyId, 34556, 456,updatedField);
        //Assert
        Set<String> instoreDate = Optional.ofNullable(strategyFlClusEligRankings)
                .stream()
                .flatMap(Collection::stream)
                .map(StrategyFlClusEligRanking::getInStoreYrWkDesc)
                .collect(Collectors.toSet());
        String resultInstoreDate = String.join("", instoreDate);
        Set<String> ccResultsInStoreDate = getAllCcInstoreDate(strategyFlClusEligRankings);
        String resultCcInstoreDate = String.join("", ccResultsInStoreDate);
        assertEquals(1, instoreDate.size());
        assertEquals("FYE2023WK21", resultInstoreDate);
        //cc also set to inStoreDate
        assertEquals(1, ccResultsInStoreDate.size());
        assertEquals("FYE2023WK21", resultCcInstoreDate);


    }

    private Set<String> getAllCcInstoreDate(Set<StrategyFlClusEligRanking> strategyFlClusEligRankings){
        Set<String> ccInstoreDate = new HashSet<>();
        Optional.ofNullable(strategyFlClusEligRankings)
                .stream()
                .flatMap(Collection::stream)
                .map(StrategyFlClusEligRanking::getStrategyStyleCluses)
                .forEach(strategyStyleCluses -> {
                    Optional.ofNullable(strategyStyleCluses)
                            .stream()
                            .flatMap(Collection::stream)
                            .forEach(strategyStyleClus -> {
                                ccInstoreDate.addAll(Optional.ofNullable(strategyStyleClus.getStrategyCcClusEligRankings())
                                        .stream()
                                        .flatMap(Collection::stream)
                                        .map(StrategyCcClusEligRanking::getInStoreYrWkDesc)
                                        .collect(Collectors.toSet()));
                            });
                });
        return ccInstoreDate;
    }


    @Test
    void testUpdateFlMetricsMarkDownAll() {
        //Arrange
        UpdatedFields updatedFields = new UpdatedFields();
        Field weatherClusterField = new Field();
        weatherClusterField.setKey("markDownDate");
        weatherClusterField.setValue("FYE2023WK31");
        updatedFields.setWeatherCluster(Arrays.asList(weatherClusterField));

        Fineline fineline = new Fineline();
        fineline.setFinelineNbr(51);
        fineline.setFinelineName("Women Hoodie");
        fineline.setUpdatedFields(updatedFields);
        FiscalWeek fiscalWeek = new FiscalWeek();
        fiscalWeek.setFiscalWeekDesc("FYE2023WK31");

        Strategy strategy = new Strategy();
        List<WeatherCluster> weatherClusterList = new ArrayList<>();
        WeatherCluster weatherCluster = new WeatherCluster();

        ClusterType type = new ClusterType();
        type.setAnalyticsClusterId(0);
        type.setAnalyticsClusterDesc("all");
        weatherCluster.setType(type);
        weatherCluster.setIsEligible(true);
        weatherCluster.setMarkDownDate(fiscalWeek);
        weatherClusterList.add(weatherCluster);
        strategy.setWeatherClusters(weatherClusterList);
        fineline.setStrategy(strategy);
        PlanStrategyId planStrategyId = GetStrategyFlClusEligRankingUtil.getPlanStrategyId();
        doReturn(GetStrategyFlClusEligRankingUtil.getStrategyFlClusEligRankings(false))
                .when(strategyFlClusEligRankingRepository).findStrategyFlClusEligRankingByStrategyFlClusEligRankingId_PlanClusterStrategyId_PlanStrategyIdAndStrategyFlClusEligRankingId_lvl3NbrAndStrategyFlClusEligRankingId_lvl4NbrAndStrategyFlClusEligRankingId_finelineNbr(any(PlanStrategyId.class), anyInt(), anyInt(), any());
        Set<String> updatedField = new HashSet<>();
        //Act
        Set<StrategyFlClusEligRanking> strategyFlClusEligRankings = clusterEligibilityFlService.updateClusterEligibilityMetrics(fineline,
                planStrategyId, 34556, 456,updatedField);
        //Assert
        Set<String> markDownDate = Optional.ofNullable(strategyFlClusEligRankings)
                .stream()
                .flatMap(Collection::stream)
                .map(StrategyFlClusEligRanking::getMarkDownYrWkDesc)
                .collect(Collectors.toSet());
        String resultMarkDownDate = String.join("", markDownDate);
        Set<String> ccResultsMarkDownDate = getAllCcMarkDownDate(strategyFlClusEligRankings);
        String resultCcMarkDownDate = String.join("", ccResultsMarkDownDate);
        assertEquals(1, markDownDate.size());
        assertEquals("FYE2023WK31", resultMarkDownDate);
        //cc also set to inStoreDate
        assertEquals(1, ccResultsMarkDownDate.size());
        assertEquals("FYE2023WK31", resultCcMarkDownDate);


    }

    private Set<String> getAllCcMarkDownDate(Set<StrategyFlClusEligRanking> strategyFlClusEligRankings){
        Set<String> ccMarkDOwnDate = new HashSet<>();
        Optional.ofNullable(strategyFlClusEligRankings)
                .stream()
                .flatMap(Collection::stream)
                .map(StrategyFlClusEligRanking::getStrategyStyleCluses)
                .forEach(strategyStyleCluses -> {
                    Optional.ofNullable(strategyStyleCluses)
                            .stream()
                            .flatMap(Collection::stream)
                            .forEach(strategyStyleClus -> {
                                ccMarkDOwnDate.addAll(Optional.ofNullable(strategyStyleClus.getStrategyCcClusEligRankings())
                                        .stream()
                                        .flatMap(Collection::stream)
                                        .map(StrategyCcClusEligRanking::getMarkDownYrWkDesc)
                                        .collect(Collectors.toSet()));
                            });
                });
        return ccMarkDOwnDate;
    }

    @Test
    void testUpdateFlMetricsRankingAll() {
        //Arrange
        UpdatedFields updatedFields = new UpdatedFields();
        Field weatherClusterField = new Field();
        weatherClusterField.setKey("ranking");
        weatherClusterField.setValue("1");
        updatedFields.setWeatherCluster(Arrays.asList(weatherClusterField));

        Fineline fineline = new Fineline();
        fineline.setFinelineNbr(51);
        fineline.setFinelineName("Women Hoodie");
        fineline.setUpdatedFields(updatedFields);
        FiscalWeek fiscalWeek = new FiscalWeek();
        fiscalWeek.setFiscalWeekDesc("FYE2023WK31");

        Strategy strategy = new Strategy();
        List<WeatherCluster> weatherClusterList = new ArrayList<>();
        WeatherCluster weatherCluster = new WeatherCluster();

        ClusterType type = new ClusterType();
        type.setAnalyticsClusterId(0);
        type.setAnalyticsClusterDesc("all");
        weatherCluster.setType(type);
        weatherCluster.setIsEligible(true);
        weatherCluster.setMarkDownDate(fiscalWeek);
        weatherCluster.setRanking(1);
        weatherClusterList.add(weatherCluster);
        strategy.setWeatherClusters(weatherClusterList);
        fineline.setStrategy(strategy);
        PlanStrategyId planStrategyId = GetStrategyFlClusEligRankingUtil.getPlanStrategyId();
        doReturn(GetStrategyFlClusEligRankingUtil.getStrategyFlClusEligRankings(false))
                .when(strategyFlClusEligRankingRepository).findStrategyFlClusEligRankingByStrategyFlClusEligRankingId_PlanClusterStrategyId_PlanStrategyIdAndStrategyFlClusEligRankingId_lvl3NbrAndStrategyFlClusEligRankingId_lvl4NbrAndStrategyFlClusEligRankingId_finelineNbr(any(PlanStrategyId.class), anyInt(), anyInt(), any());
        Set<String> updatedField = new HashSet<>();
        //Act
        Set<StrategyFlClusEligRanking> strategyFlClusEligRankings = clusterEligibilityFlService.updateClusterEligibilityMetrics(fineline,
                planStrategyId, 34556, 456,updatedField);
        //Assert
        Set<Integer> rank = Optional.ofNullable(strategyFlClusEligRankings)
                .stream()
                .flatMap(Collection::stream)
                .map(StrategyFlClusEligRanking::getMerchantOverrideRank)
                .collect(Collectors.toSet());

        assertEquals(1, rank.size());
        assertTrue(rank.contains(1));
    }

}
