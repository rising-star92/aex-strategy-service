package com.walmart.aex.strategy.service;

import com.walmart.aex.strategy.dto.request.*;
import com.walmart.aex.strategy.entity.*;
import com.walmart.aex.strategy.enums.IncludeOffshoreMkt;
import com.walmart.aex.strategy.properties.AppProperties;
import com.walmart.aex.strategy.repository.StrategyFlClusEligRankingRepository;
import com.walmart.aex.strategy.repository.StrategyFlClusPrgmEligRankingRepository;
import com.walmart.aex.strategy.util.GetEligFlClusProgUtil;
import com.walmart.aex.strategy.util.GetStrategyFlClusEligRankingUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.powermock.api.mockito.PowerMockito;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProgClusterEligibilityFlServiceTest {

    @InjectMocks
    private ProgClusterEligibilityFlService progClusterEligibilityFlService;

    @Mock
    private StrategyFlClusPrgmEligRankingRepository strategyFlClusPrgmEligRankingRepository;

    @Mock
    private StrategyFlClusEligRankingRepository strategyFlClusEligRankingRepository;

    @Mock
    private TraitClusterStoreCountService traitClusterStoreCountService;

    @Mock
    private ProgClusterEligibilityCcService progClusterEligibilityCcService;

    @Mock
    private PlanStrategyClusterEligMapper planStrategyClusterEligMapper;

    @Mock
    private AppProperties appProperties;

    @BeforeEach
    public void setup() {
        appProperties = PowerMockito.mock(AppProperties.class);
        progClusterEligibilityFlService = new ProgClusterEligibilityFlService(strategyFlClusPrgmEligRankingRepository, progClusterEligibilityCcService,
                strategyFlClusEligRankingRepository, traitClusterStoreCountService, planStrategyClusterEligMapper);
    }
    /**
     * Scenario: Turning the isEligible flag at the Fineline to True.
     * Expected: All the Cluster from 1 to 7 and all should be Turned True, as well as all the CC under the FL should also be True
     * Assert: isEligible flag at all cluster size should be 1 and that should be True.
     */
    @Test
    void testUpdateProgFlMetricsAllSetTrue() {
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
        doReturn(GetEligFlClusProgUtil.getStrategyFlClusPrgmEligRankings())
                .when(strategyFlClusPrgmEligRankingRepository).findStrategyFlClusPrgmEligRankingByStrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_PlanClusterStrategyId_PlanStrategyIdAndStrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_lvl3NbrAndStrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_lvl4NbrAndStrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_finelineNbrAndStrategyFlClusPrgmEligRankingId_programId(any(PlanStrategyId.class), anyInt(), anyInt(), any(), any());
        doReturn(GetStrategyFlClusEligRankingUtil.getStrategyFlClusEligRankings(false))
                .when(strategyFlClusEligRankingRepository).findStrategyFlClusEligRankingByStrategyFlClusEligRankingId_PlanClusterStrategyId_PlanStrategyIdAndStrategyFlClusEligRankingId_lvl3NbrAndStrategyFlClusEligRankingId_lvl4NbrAndStrategyFlClusEligRankingId_finelineNbr(any(PlanStrategyId.class), anyInt(), anyInt(), any());
        Set<String> updatedField = new HashSet<>();
        //Act
        Set<StrategyFlClusPrgmEligRanking> strategyFlClusPrgmEligRankings = progClusterEligibilityFlService.updateProgClusterEligibilityMetrics(fineline,
                planStrategyId, 34556, 456, 123L,updatedField);
        //Assert
        Set<Integer> resultFlag =
                Optional.ofNullable(strategyFlClusPrgmEligRankings)
                        .stream()
                        .flatMap(Collection::stream)
                        .map(StrategyFlClusPrgmEligRanking::getIsEligibleFlag)
                        .collect(Collectors.toSet());

        Set<Integer> ccResultsFlag = getAllCcFlagStatus(strategyFlClusPrgmEligRankings);

        assertEquals(1, resultFlag.size());
        assertTrue(resultFlag.contains(1));
        //cc also set to True
        assertEquals(1, ccResultsFlag.size());
        assertTrue(ccResultsFlag.contains(1));
    }

    /**
     * Scenario: Turning the isEligible flag at the Fineline to True.
     * Expected: All the Cluster from 1 to 7 and all should be Turned True, as well as all the CC under the FL should also be True
     * Assert: isEligible flag at all cluster size should be 1 and that should be True.
     */
    @Test
    void testUpdateProgFlMetricsAllSetTruePartial() {
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
        doReturn(GetEligFlClusProgUtil.getStrategyFlClusPrgmEligRankingsPartial())
                .when(strategyFlClusPrgmEligRankingRepository).findStrategyFlClusPrgmEligRankingByStrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_PlanClusterStrategyId_PlanStrategyIdAndStrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_lvl3NbrAndStrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_lvl4NbrAndStrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_finelineNbrAndStrategyFlClusPrgmEligRankingId_programId(any(PlanStrategyId.class), anyInt(), anyInt(), any(), any());
        doReturn(GetStrategyFlClusEligRankingUtil.getStrategyFlClusEligRankingsPartial(false))
                .when(strategyFlClusEligRankingRepository).findStrategyFlClusEligRankingByStrategyFlClusEligRankingId_PlanClusterStrategyId_PlanStrategyIdAndStrategyFlClusEligRankingId_lvl3NbrAndStrategyFlClusEligRankingId_lvl4NbrAndStrategyFlClusEligRankingId_finelineNbr(any(PlanStrategyId.class), anyInt(), anyInt(), any());
        Set<String> updatedField = new HashSet<>();
        //Act
        Set<StrategyFlClusPrgmEligRanking> strategyFlClusPrgmEligRankings = progClusterEligibilityFlService.updateProgClusterEligibilityMetrics(fineline,
                planStrategyId, 34556, 456, 123L,updatedField);
        //Assert
        Set<Integer> resultFlag =
                Optional.ofNullable(strategyFlClusPrgmEligRankings)
                        .stream()
                        .flatMap(Collection::stream)
                        .map(StrategyFlClusPrgmEligRanking::getIsEligibleFlag)
                        .collect(Collectors.toSet());

        Set<Integer> ccResultsFlag = getAllCcFlagStatusPartial(strategyFlClusPrgmEligRankings);

        assertEquals(1, resultFlag.size());
        assertTrue(resultFlag.contains(1));
        //cc also set to True
        assertEquals(1, ccResultsFlag.size());
        assertTrue(ccResultsFlag.contains(1));
    }
    /**
     * Scenario: Turning the isEligible flag at the Fineline to false.
     * Expected: All the Cluster from 1 to 7 and all should be Turned false, as well as all the CC under the FL should also be false
     * Assert: isEligible flag at all cluster size should be 1 and that should be false.
     */
    @Test
    void testUpdateProgFlMetricsAllSetFalse() {
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
        doReturn(GetEligFlClusProgUtil.getStrategyFlClusPrgmEligRankings())
                .when(strategyFlClusPrgmEligRankingRepository).findStrategyFlClusPrgmEligRankingByStrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_PlanClusterStrategyId_PlanStrategyIdAndStrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_lvl3NbrAndStrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_lvl4NbrAndStrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_finelineNbrAndStrategyFlClusPrgmEligRankingId_programId(any(PlanStrategyId.class), anyInt(), anyInt(), any(), any());
        doReturn(GetStrategyFlClusEligRankingUtil.getStrategyFlClusEligRankings(false))
                .when(strategyFlClusEligRankingRepository).findStrategyFlClusEligRankingByStrategyFlClusEligRankingId_PlanClusterStrategyId_PlanStrategyIdAndStrategyFlClusEligRankingId_lvl3NbrAndStrategyFlClusEligRankingId_lvl4NbrAndStrategyFlClusEligRankingId_finelineNbr(any(PlanStrategyId.class), anyInt(), anyInt(), any());
        Set<String> updatedField = new HashSet<>();
        //Act
        Set<StrategyFlClusPrgmEligRanking> strategyFlClusPrgmEligRankings = progClusterEligibilityFlService.updateProgClusterEligibilityMetrics(fineline,
                planStrategyId, 34556, 456, 123L,updatedField);
        //Assert
        Set<Integer> resultFlag =
                Optional.ofNullable(strategyFlClusPrgmEligRankings)
                        .stream()
                        .flatMap(Collection::stream)
                        .map(StrategyFlClusPrgmEligRanking::getIsEligibleFlag)
                        .collect(Collectors.toSet());

        Set<Integer> ccResultsFlag = getAllCcFlagStatus(strategyFlClusPrgmEligRankings);

        assertEquals(1, resultFlag.size());
        assertTrue(resultFlag.contains(0));
        //cc also set to False
        assertEquals(1, ccResultsFlag.size());
        assertTrue(ccResultsFlag.contains(0));

    }

    @Test
    void testUpdateProgFlMetricsAllSetFalsePartial() {
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
        doReturn(GetEligFlClusProgUtil.getStrategyFlClusPrgmEligRankingsPartial())
                .when(strategyFlClusPrgmEligRankingRepository).findStrategyFlClusPrgmEligRankingByStrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_PlanClusterStrategyId_PlanStrategyIdAndStrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_lvl3NbrAndStrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_lvl4NbrAndStrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_finelineNbrAndStrategyFlClusPrgmEligRankingId_programId(any(PlanStrategyId.class), anyInt(), anyInt(), any(), any());
        doReturn(GetStrategyFlClusEligRankingUtil.getStrategyFlClusEligRankingsPartial(false))
                .when(strategyFlClusEligRankingRepository).findStrategyFlClusEligRankingByStrategyFlClusEligRankingId_PlanClusterStrategyId_PlanStrategyIdAndStrategyFlClusEligRankingId_lvl3NbrAndStrategyFlClusEligRankingId_lvl4NbrAndStrategyFlClusEligRankingId_finelineNbr(any(PlanStrategyId.class), anyInt(), anyInt(), any());
        Set<String> updatedField = new HashSet<>();
        //Act
        Set<StrategyFlClusPrgmEligRanking> strategyFlClusPrgmEligRankings = progClusterEligibilityFlService.updateProgClusterEligibilityMetrics(fineline,
                planStrategyId, 34556, 456, 123L,updatedField);
        //Assert
        Set<Integer> resultFlag =
                Optional.ofNullable(strategyFlClusPrgmEligRankings)
                        .stream()
                        .flatMap(Collection::stream)
                        .map(StrategyFlClusPrgmEligRanking::getIsEligibleFlag)
                        .collect(Collectors.toSet());

        Set<Integer> ccResultsFlag = getAllCcFlagStatusPartial(strategyFlClusPrgmEligRankings);

        assertEquals(1, resultFlag.size());
        assertTrue(resultFlag.contains(0));
        //cc also set to False
        assertEquals(1, ccResultsFlag.size());
        assertTrue(ccResultsFlag.contains(0));

    }
    /**
     * Scenario: Turning the isEligible flag at one of the Cluster (i.e. cluster 1) to False and the rest of them are True.
     * Expected: Just cluster 1 & All should be false and rest of the clusters should be True. As well as the cc under taht cluster 1 along all should be false
     * Assert: isEligible flag at all cluster size should be 2 and all isEligible should be false.
     */

    @Test
    void testUpdateProgFlMetricsClusterNFalsePartial() {
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
        doReturn(GetEligFlClusProgUtil.getStrategyFlClusPrgmEligRankingsPartial())
                .when(strategyFlClusPrgmEligRankingRepository).findStrategyFlClusPrgmEligRankingByStrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_PlanClusterStrategyId_PlanStrategyIdAndStrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_lvl3NbrAndStrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_lvl4NbrAndStrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_finelineNbrAndStrategyFlClusPrgmEligRankingId_programId(any(PlanStrategyId.class), anyInt(), anyInt(), any(), any());
        doReturn(GetStrategyFlClusEligRankingUtil.getStrategyFlClusEligRankingsPartial(false))
                .when(strategyFlClusEligRankingRepository).findStrategyFlClusEligRankingByStrategyFlClusEligRankingId_PlanClusterStrategyId_PlanStrategyIdAndStrategyFlClusEligRankingId_lvl3NbrAndStrategyFlClusEligRankingId_lvl4NbrAndStrategyFlClusEligRankingId_finelineNbr(any(PlanStrategyId.class), anyInt(), anyInt(), any());
        Set<String> updatedField = new HashSet<>();
        //Act
        Set<StrategyFlClusPrgmEligRanking> strategyFlClusPrgmEligRankings = progClusterEligibilityFlService.updateProgClusterEligibilityMetrics(fineline,
                planStrategyId, 34556, 456, 123L,updatedField);
        //Assert
        Set<Integer> resultFlFlag =
                Optional.ofNullable(strategyFlClusPrgmEligRankings)
                        .stream()
                        .flatMap(Collection::stream)
                        .map(StrategyFlClusPrgmEligRanking::getIsEligibleFlag)
                        .collect(Collectors.toSet());

        Set<Integer> resultCcFlag = getAllCcFlagStatusPartial(strategyFlClusPrgmEligRankings);

        Integer allFlag = getAllFlagAtFinelinePartial(strategyFlClusPrgmEligRankings);

        Integer allCcFlag = getClusterNCcFlagStatusPartial(strategyFlClusPrgmEligRankings);
        //cluster 1 is false and others are true
        assertEquals(3, resultFlFlag.size());
        //all flag should be false
        assertEquals(2, allFlag);
        //ccFlag
        assertEquals(3, resultCcFlag.size());
        assertEquals(2, allCcFlag);
    }
    /**
     * Scenario: Update the inStoreDate at one of the Cluster (i.e. cluster 1) to FYE2023WK21.
     * Expected: Just cluster 1 Finleine and all Ccs should be ne now updated to FYE2023WK21.
     * Assert: inStoreDesc should cluster 1 should be FYE2023WK21.
     */

    @Test
    void testUpdateProgFlMetricsInStore() {
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
        doReturn(GetEligFlClusProgUtil.getStrategyFlClusPrgmEligRankings())
                .when(strategyFlClusPrgmEligRankingRepository).findStrategyFlClusPrgmEligRankingByStrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_PlanClusterStrategyId_PlanStrategyIdAndStrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_lvl3NbrAndStrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_lvl4NbrAndStrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_finelineNbrAndStrategyFlClusPrgmEligRankingId_programId(any(PlanStrategyId.class), anyInt(), anyInt(), any(), any());
        doReturn(GetStrategyFlClusEligRankingUtil.getStrategyFlClusEligRankings(false))
                .when(strategyFlClusEligRankingRepository).findStrategyFlClusEligRankingByStrategyFlClusEligRankingId_PlanClusterStrategyId_PlanStrategyIdAndStrategyFlClusEligRankingId_lvl3NbrAndStrategyFlClusEligRankingId_lvl4NbrAndStrategyFlClusEligRankingId_finelineNbr(any(PlanStrategyId.class), anyInt(), anyInt(), any());
        Set<String> updatedField = new HashSet<>();
        //Act
        Set<StrategyFlClusPrgmEligRanking> strategyFlClusPrgmEligRankings = progClusterEligibilityFlService.updateProgClusterEligibilityMetrics(fineline,
                planStrategyId, 34556, 456, 123L,updatedField);
        //Assert
        String instoreDate = Optional.ofNullable(strategyFlClusPrgmEligRankings)
                .stream()
                .flatMap(Collection::stream)
                .filter(strategyFlClusPrgmEligRanking -> strategyFlClusPrgmEligRanking.getStrategyFlClusPrgmEligRankingId()
                        .getStrategyFlClusEligRankingId().getPlanClusterStrategyId().getAnalyticsClusterId().equals(1))
                .findFirst()
                .map(StrategyFlClusPrgmEligRanking::getInStoreYrWkDesc)
                .orElse(null);

        assertEquals("FYE2023WK21", instoreDate);
    }
    /**
     * Scenario: Update the MarkDownDate at one of the Cluster (i.e. cluster 1) to FYE2023WK31.
     * Expected: Just cluster 1 Finleine and all Ccs should be ne now updated to FYE2023WK31.
     * Assert: markDownDesc should cluster 1 should be FYE2023WK31.
     */

    @Test
    void testUpdateProgFlMetricsMarkDown() {
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
        doReturn(GetEligFlClusProgUtil.getStrategyFlClusPrgmEligRankings())
                .when(strategyFlClusPrgmEligRankingRepository).findStrategyFlClusPrgmEligRankingByStrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_PlanClusterStrategyId_PlanStrategyIdAndStrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_lvl3NbrAndStrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_lvl4NbrAndStrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_finelineNbrAndStrategyFlClusPrgmEligRankingId_programId(any(PlanStrategyId.class), anyInt(), anyInt(), any(), any());
        doReturn(GetStrategyFlClusEligRankingUtil.getStrategyFlClusEligRankings(false))
                .when(strategyFlClusEligRankingRepository).findStrategyFlClusEligRankingByStrategyFlClusEligRankingId_PlanClusterStrategyId_PlanStrategyIdAndStrategyFlClusEligRankingId_lvl3NbrAndStrategyFlClusEligRankingId_lvl4NbrAndStrategyFlClusEligRankingId_finelineNbr(any(PlanStrategyId.class), anyInt(), anyInt(), any());
        Set<String> updatedField = new HashSet<>();
        //Act
        Set<StrategyFlClusPrgmEligRanking> strategyFlClusPrgmEligRankings = progClusterEligibilityFlService.updateProgClusterEligibilityMetrics(fineline,
                planStrategyId, 34556, 456, 123L,updatedField);
        //Assert
        String markDownDate = Optional.ofNullable(strategyFlClusPrgmEligRankings)
                .stream()
                .flatMap(Collection::stream)
                .filter(strategyFlClusPrgmEligRanking -> strategyFlClusPrgmEligRanking.getStrategyFlClusPrgmEligRankingId()
                        .getStrategyFlClusEligRankingId().getPlanClusterStrategyId().getAnalyticsClusterId().equals(1))
                .findFirst()
                .map(StrategyFlClusPrgmEligRanking::getMarkDownYrWkDesc)
                .orElse(null);

        assertEquals("FYE2023WK31", markDownDate);
    }
    /**
     * Scenario: Update the Ranking at one of the Cluster (i.e. cluster 1) to 1.
     * Expected: Just cluster 1 Finleine and all Ccs should be ne now updated to 1.
     * Assert: ranking should cluster 1 should be 1.
     */
    @Test
    void testUpdateProgFlMetricsRanking() {
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
        doReturn(GetEligFlClusProgUtil.getStrategyFlClusPrgmEligRankings())
                .when(strategyFlClusPrgmEligRankingRepository).findStrategyFlClusPrgmEligRankingByStrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_PlanClusterStrategyId_PlanStrategyIdAndStrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_lvl3NbrAndStrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_lvl4NbrAndStrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_finelineNbrAndStrategyFlClusPrgmEligRankingId_programId(any(PlanStrategyId.class), anyInt(), anyInt(), any(), any());
        doReturn(GetStrategyFlClusEligRankingUtil.getStrategyFlClusEligRankings(false))
                .when(strategyFlClusEligRankingRepository).findStrategyFlClusEligRankingByStrategyFlClusEligRankingId_PlanClusterStrategyId_PlanStrategyIdAndStrategyFlClusEligRankingId_lvl3NbrAndStrategyFlClusEligRankingId_lvl4NbrAndStrategyFlClusEligRankingId_finelineNbr(any(PlanStrategyId.class), anyInt(), anyInt(), any());
        Set<String> updatedField = new HashSet<>();
        //Act
        Set<StrategyFlClusPrgmEligRanking> strategyFlClusPrgmEligRankings = progClusterEligibilityFlService.updateProgClusterEligibilityMetrics(fineline,
                planStrategyId, 34556, 456, 123L,updatedField);
        //Assert
        Integer rank = Optional.ofNullable(strategyFlClusPrgmEligRankings)
                .stream()
                .flatMap(Collection::stream)
                .filter(strategyFlClusPrgmEligRanking -> strategyFlClusPrgmEligRanking.getStrategyFlClusPrgmEligRankingId()
                        .getStrategyFlClusEligRankingId().getPlanClusterStrategyId().getAnalyticsClusterId().equals(1))
                .findFirst()
                .map(StrategyFlClusPrgmEligRanking::getMerchantOverrideRank)
                .orElse(null);

        assertEquals(1, rank);
    }
    /**
     * Scenario: Add just PR to Include Offshore list for one of the Cluster (i.e. cluster 1).
     * Expected: If PR don't exists and other values exists, need to clear those and add just PR to the list .
     * Assert: include offshore for cluster 1 count should be 1.
     */

    @Test
    void testUpdateProgFlMetricsIncludeOffshore() {
        //Arrange
        UpdatedFields updatedFields = new UpdatedFields();
        Field weatherClusterField = new Field();
        weatherClusterField.setKey("includeOffshore");
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
        List<String> cluster1 = new ArrayList<>();
        cluster1.add("PR");
        List<String> cluster7 = new ArrayList<>();
        cluster7.add("AK");



        doReturn(GetStrategyFlClusEligRankingUtil.getAnalyticsTraitClusterStore())
                .when(traitClusterStoreCountService).getAnalyticsClusterStoreCount(anyLong());
        doReturn(GetEligFlClusProgUtil.getStrategyFlClusPrgmEligRankings())
                .when(strategyFlClusPrgmEligRankingRepository).findStrategyFlClusPrgmEligRankingByStrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_PlanClusterStrategyId_PlanStrategyIdAndStrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_lvl3NbrAndStrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_lvl4NbrAndStrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_finelineNbrAndStrategyFlClusPrgmEligRankingId_programId(any(PlanStrategyId.class), anyInt(), anyInt(), any(), any());
        doReturn(GetStrategyFlClusEligRankingUtil.getStrategyFlClusEligRankings(false))
                .when(strategyFlClusEligRankingRepository).findStrategyFlClusEligRankingByStrategyFlClusEligRankingId_PlanClusterStrategyId_PlanStrategyIdAndStrategyFlClusEligRankingId_lvl3NbrAndStrategyFlClusEligRankingId_lvl4NbrAndStrategyFlClusEligRankingId_finelineNbr(any(PlanStrategyId.class), anyInt(), anyInt(), any());
        when(appProperties.getCluster1OffshoreList()).thenReturn(cluster1);
        ReflectionTestUtils.setField(progClusterEligibilityFlService, "appProperties", appProperties);
        Set<String> updatedField = new HashSet<>();
        //Act
        Set<StrategyFlClusPrgmEligRanking> strategyFlClusPrgmEligRankings = progClusterEligibilityFlService.updateProgClusterEligibilityMetrics(fineline,
                planStrategyId, 34556, 456, 123L,updatedField);
        //Assert
        Long count = Optional.ofNullable(strategyFlClusPrgmEligRankings)
                .stream()
                .flatMap(Collection::stream)
                .filter(strategyFlClusPrgmEligRanking -> strategyFlClusPrgmEligRanking.getStrategyFlClusPrgmEligRankingId()
                        .getStrategyFlClusEligRankingId().getPlanClusterStrategyId().getAnalyticsClusterId().equals(1))
                .findFirst()
                .map(StrategyFlClusPrgmEligRanking::getFinelineMarketClusterPrgElgs)
                .stream().count();


        assertEquals(1l, count);
    }
    /**
     * Scenario: Add just HI to Include Offshore list for one of the Cluster (i.e. cluster 2).
     * Expected: If HI don't exists and other values exists, need to clear those and add just HI to the list .
     * Assert: include offshore for cluster 2 count should be 1.
     */

    @Test
    void testUpdateProgFlMetricsIncludeOffshoreCluster2() {
        //Arrange
        UpdatedFields updatedFields = new UpdatedFields();
        Field weatherClusterField = new Field();
        weatherClusterField.setKey("includeOffshore");
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
        List<String> cluster2 = new ArrayList<>();
        cluster2.add("HI");
        List<String> cluster7 = new ArrayList<>();
        cluster7.add("AK");



        doReturn(GetStrategyFlClusEligRankingUtil.getAnalyticsTraitClusterStore())
                .when(traitClusterStoreCountService).getAnalyticsClusterStoreCount(anyLong());
        doReturn(GetEligFlClusProgUtil.getStrategyFlClusPrgmEligRankings())
                .when(strategyFlClusPrgmEligRankingRepository).findStrategyFlClusPrgmEligRankingByStrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_PlanClusterStrategyId_PlanStrategyIdAndStrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_lvl3NbrAndStrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_lvl4NbrAndStrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_finelineNbrAndStrategyFlClusPrgmEligRankingId_programId(any(PlanStrategyId.class), anyInt(), anyInt(), any(), any());
        doReturn(GetStrategyFlClusEligRankingUtil.getStrategyFlClusEligRankings(false))
                .when(strategyFlClusEligRankingRepository).findStrategyFlClusEligRankingByStrategyFlClusEligRankingId_PlanClusterStrategyId_PlanStrategyIdAndStrategyFlClusEligRankingId_lvl3NbrAndStrategyFlClusEligRankingId_lvl4NbrAndStrategyFlClusEligRankingId_finelineNbr(any(PlanStrategyId.class), anyInt(), anyInt(), any());
        Mockito.lenient().when(appProperties.getCluster2OffshoreList()).thenReturn(cluster2);
        ReflectionTestUtils.setField(progClusterEligibilityFlService, "appProperties", appProperties);
        Set<String> updatedField = new HashSet<>();
        //Act
        Set<StrategyFlClusPrgmEligRanking> strategyFlClusPrgmEligRankings = progClusterEligibilityFlService.updateProgClusterEligibilityMetrics(fineline,
                planStrategyId, 34556, 456, 123L,updatedField);
        //Assert
        Long count = Optional.ofNullable(strategyFlClusPrgmEligRankings)
                .stream()
                .flatMap(Collection::stream)
                .filter(strategyFlClusPrgmEligRanking -> strategyFlClusPrgmEligRanking.getStrategyFlClusPrgmEligRankingId()
                        .getStrategyFlClusEligRankingId().getPlanClusterStrategyId().getAnalyticsClusterId().equals(2))
                .findFirst()
                .map(StrategyFlClusPrgmEligRanking::getFinelineMarketClusterPrgElgs)
                .stream().count();


        assertEquals(1l, count);
    }
    /**
     * Scenario: Update the inStoreDate at one of all cluster at fineline level to FYE2023WK21.
     * Expected: All the clusters from 1 to 7 and all should now be reflecting to FYE2023WK21.
     * Assert: inStoreDesc at all levels at fl and all the cc's under it should be FYE2023WK21.
     */

    @Test
    void testUpdateProgFlMetricsInStoreAll() {
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
        doReturn(GetEligFlClusProgUtil.getStrategyFlClusPrgmEligRankings())
                .when(strategyFlClusPrgmEligRankingRepository).findStrategyFlClusPrgmEligRankingByStrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_PlanClusterStrategyId_PlanStrategyIdAndStrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_lvl3NbrAndStrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_lvl4NbrAndStrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_finelineNbrAndStrategyFlClusPrgmEligRankingId_programId(any(PlanStrategyId.class), anyInt(), anyInt(), any(), any());
        doReturn(GetStrategyFlClusEligRankingUtil.getStrategyFlClusEligRankings(false))
                .when(strategyFlClusEligRankingRepository).findStrategyFlClusEligRankingByStrategyFlClusEligRankingId_PlanClusterStrategyId_PlanStrategyIdAndStrategyFlClusEligRankingId_lvl3NbrAndStrategyFlClusEligRankingId_lvl4NbrAndStrategyFlClusEligRankingId_finelineNbr(any(PlanStrategyId.class), anyInt(), anyInt(), any());
        Set<String> updatedField = new HashSet<>();
        //Act
        Set<StrategyFlClusPrgmEligRanking> strategyFlClusPrgmEligRankings = progClusterEligibilityFlService.updateProgClusterEligibilityMetrics(fineline,
                planStrategyId, 34556, 456, 123L,updatedField);
        //Assert
        Set<String> instoreDate = Optional.ofNullable(strategyFlClusPrgmEligRankings)
                .stream()
                .flatMap(Collection::stream)
                .map(StrategyFlClusPrgmEligRanking::getInStoreYrWkDesc)
                .collect(Collectors.toSet());
        String resultInstoreDate = String.join("", instoreDate);
        Set<String> ccResultsInStoreDate = getAllCcInstoreDate(strategyFlClusPrgmEligRankings);
        String resultCcInstoreDate = String.join("", ccResultsInStoreDate);
        assertEquals(1, instoreDate.size());
        assertEquals("FYE2023WK21", resultInstoreDate);
        //cc also set to inStoreDate
        assertEquals(1, ccResultsInStoreDate.size());
        assertEquals("FYE2023WK21", resultCcInstoreDate);
    }
    /**
     * Scenario: Update the markDownDate at one of all cluster at fineline level to FYE2023WK31.
     * Expected: All the clusters from 1 to 7 and all should now be reflecting to FYE2023WK31.
     * Assert: markDownDate at all levels at fl and all the cc's under it should be FYE2023WK31.
     */

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
        doReturn(GetEligFlClusProgUtil.getStrategyFlClusPrgmEligRankings())
                .when(strategyFlClusPrgmEligRankingRepository).findStrategyFlClusPrgmEligRankingByStrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_PlanClusterStrategyId_PlanStrategyIdAndStrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_lvl3NbrAndStrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_lvl4NbrAndStrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_finelineNbrAndStrategyFlClusPrgmEligRankingId_programId(any(PlanStrategyId.class), anyInt(), anyInt(), any(), any());
        doReturn(GetStrategyFlClusEligRankingUtil.getStrategyFlClusEligRankings(false))
                .when(strategyFlClusEligRankingRepository).findStrategyFlClusEligRankingByStrategyFlClusEligRankingId_PlanClusterStrategyId_PlanStrategyIdAndStrategyFlClusEligRankingId_lvl3NbrAndStrategyFlClusEligRankingId_lvl4NbrAndStrategyFlClusEligRankingId_finelineNbr(any(PlanStrategyId.class), anyInt(), anyInt(), any());
        Set<String> updatedField = new HashSet<>();
        //Act
        Set<StrategyFlClusPrgmEligRanking> strategyFlClusPrgmEligRankings = progClusterEligibilityFlService.updateProgClusterEligibilityMetrics(fineline,
                planStrategyId, 34556, 456, 123L,updatedField);
        //Assert
        Set<String> markDownDate = Optional.ofNullable(strategyFlClusPrgmEligRankings)
                .stream()
                .flatMap(Collection::stream)
                .map(StrategyFlClusPrgmEligRanking::getMarkDownYrWkDesc)
                .collect(Collectors.toSet());
        String resultMarkDownDate = String.join("", markDownDate);
        Set<String> ccResultsMarkDownDate = getAllCcMarkDownDate(strategyFlClusPrgmEligRankings);
        String resultCcMarkDownDate = String.join("", ccResultsMarkDownDate);
        assertEquals(1, markDownDate.size());
        assertEquals("FYE2023WK31", resultMarkDownDate);
        //cc also set to inStoreDate
        assertEquals(1, ccResultsMarkDownDate.size());
        assertEquals("FYE2023WK31", resultCcMarkDownDate);
    }
    /**
     * Scenario: Update the ranking at one of all cluster at fineline level to 1.
     * Expected: All the clusters from 1 to 7 and all should now be reflecting to 1.
     * Assert: ranking at all levels should be 1.
     */

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
        doReturn(GetEligFlClusProgUtil.getStrategyFlClusPrgmEligRankings())
                .when(strategyFlClusPrgmEligRankingRepository).findStrategyFlClusPrgmEligRankingByStrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_PlanClusterStrategyId_PlanStrategyIdAndStrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_lvl3NbrAndStrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_lvl4NbrAndStrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_finelineNbrAndStrategyFlClusPrgmEligRankingId_programId(any(PlanStrategyId.class), anyInt(), anyInt(), any(), any());
        doReturn(GetStrategyFlClusEligRankingUtil.getStrategyFlClusEligRankings(false))
                .when(strategyFlClusEligRankingRepository).findStrategyFlClusEligRankingByStrategyFlClusEligRankingId_PlanClusterStrategyId_PlanStrategyIdAndStrategyFlClusEligRankingId_lvl3NbrAndStrategyFlClusEligRankingId_lvl4NbrAndStrategyFlClusEligRankingId_finelineNbr(any(PlanStrategyId.class), anyInt(), anyInt(), any());
        Set<String> updatedField = new HashSet<>();
        //Act
        Set<StrategyFlClusPrgmEligRanking> strategyFlClusPrgmEligRankings = progClusterEligibilityFlService.updateProgClusterEligibilityMetrics(fineline,
                planStrategyId, 34556, 456, 123L,updatedField);
        //Assert
        Set<Integer> rank = Optional.ofNullable(strategyFlClusPrgmEligRankings)
                .stream()
                .flatMap(Collection::stream)
                .map(StrategyFlClusPrgmEligRanking::getMerchantOverrideRank)
                .collect(Collectors.toSet());

        assertEquals(1, rank.size());
        assertTrue(rank.contains(1));
    }

    /**
     * Scenario: Turning the isEligible flag at the Fineline to True when we dont have any entries in Program table.
     * Expected: Need to create entries for Fl and CC for all clusters and isEligible should be Turned True, as well as all the CC under the FL should also be True
     * Assert: isEligible flag at all cluster size should be 1 and that should be True.
     */
    @Test
    void testInitialProgFlMetricsAllSetTruePartial() {
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

        Optional<Set<StrategyFlClusPrgmEligRanking>> strategyFlClusPrgmEligRankingsInput = Optional.of(new HashSet<>());

        doReturn(strategyFlClusPrgmEligRankingsInput)
                .when(strategyFlClusPrgmEligRankingRepository).findStrategyFlClusPrgmEligRankingByStrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_PlanClusterStrategyId_PlanStrategyIdAndStrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_lvl3NbrAndStrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_lvl4NbrAndStrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_finelineNbrAndStrategyFlClusPrgmEligRankingId_programId(any(PlanStrategyId.class), anyInt(), anyInt(), any(), any());
        doReturn(GetStrategyFlClusEligRankingUtil.getStrategyFlClusEligRankingsPartial(false))
                .when(strategyFlClusEligRankingRepository).findStrategyFlClusEligRankingByStrategyFlClusEligRankingId_PlanClusterStrategyId_PlanStrategyIdAndStrategyFlClusEligRankingId_lvl3NbrAndStrategyFlClusEligRankingId_lvl4NbrAndStrategyFlClusEligRankingId_finelineNbr(any(PlanStrategyId.class), anyInt(), anyInt(), any());
        Set<String> updatedField = new HashSet<>();
        //Act
        Set<StrategyFlClusPrgmEligRanking> strategyFlClusPrgmEligRankings = progClusterEligibilityFlService.updateProgClusterEligibilityMetrics(fineline,
                planStrategyId, 34556, 456, 123L,updatedField);
        //Assert
        Set<Integer> resultFlag =
                Optional.ofNullable(strategyFlClusPrgmEligRankings)
                        .stream()
                        .flatMap(Collection::stream)
                        .map(StrategyFlClusPrgmEligRanking::getIsEligibleFlag)
                        .collect(Collectors.toSet());

        Set<Integer> ccResultsFlag = getAllCcFlagStatusPartial(strategyFlClusPrgmEligRankings);

        assertEquals(1, resultFlag.size());
        assertTrue(resultFlag.contains(1));
        //cc also set to True
        assertEquals(1, ccResultsFlag.size());
        assertTrue(ccResultsFlag.contains(1));
    }

    private Set<String> getAllCcMarkDownDate(Set<StrategyFlClusPrgmEligRanking> strategyFlClusPrgmEligRankings){
        Set<String> ccMarkDOwnDate = new HashSet<>();
        Optional.ofNullable(strategyFlClusPrgmEligRankings)
                .stream()
                .flatMap(Collection::stream)
                .map(StrategyFlClusPrgmEligRanking::getEligStyleClusProgs)
                .forEach(eligStyleClusProgs -> {
                    Optional.ofNullable(eligStyleClusProgs)
                            .stream()
                            .flatMap(Collection::stream)
                            .forEach(eligStyleClusProg -> {
                                ccMarkDOwnDate.addAll(Optional.ofNullable(eligStyleClusProg.getEligCcClusProgs())
                                        .stream()
                                        .flatMap(Collection::stream)
                                        .map(EligCcClusProg::getMarkDownYrWkDesc)
                                        .collect(Collectors.toSet()));
                            });
                });
        return ccMarkDOwnDate;
    }
    private Set<String> getAllCcInstoreDate(Set<StrategyFlClusPrgmEligRanking> strategyFlClusPrgmEligRankings){
        Set<String> ccInstoreDate = new HashSet<>();
        Optional.ofNullable(strategyFlClusPrgmEligRankings)
                .stream()
                .flatMap(Collection::stream)
                .map(StrategyFlClusPrgmEligRanking::getEligStyleClusProgs)
                .forEach(eligStyleClusProgs -> {
                    Optional.ofNullable(eligStyleClusProgs)
                            .stream()
                            .flatMap(Collection::stream)
                            .forEach(eligStyleClusProg -> {
                                ccInstoreDate.addAll(Optional.ofNullable(eligStyleClusProg.getEligCcClusProgs())
                                        .stream()
                                        .flatMap(Collection::stream)
                                        .map(EligCcClusProg::getInStoreYrWkDesc)
                                        .collect(Collectors.toSet()));
                            });
                });
        return ccInstoreDate;
    }
    private Integer getClusterNCcFlagStatus(Set<StrategyFlClusPrgmEligRanking> strategyFlClusPrgmEligRankings) {
        return Optional.ofNullable(strategyFlClusPrgmEligRankings)
                .stream()
                .flatMap(Collection::stream)
                .filter(strategyFlClusPrgmEligRanking -> strategyFlClusPrgmEligRanking.getStrategyFlClusPrgmEligRankingId()
                        .getStrategyFlClusEligRankingId().getPlanClusterStrategyId().getAnalyticsClusterId().equals(0))
                .findFirst()
                .map(StrategyFlClusPrgmEligRanking::getEligStyleClusProgs)
                .stream()
                .flatMap(Collection::stream)
                .findFirst()
                .map(EligStyleClusProg::getEligCcClusProgs)
                .stream()
                .flatMap(Collection::stream)
                .findFirst()
                .map(EligCcClusProg::getIsEligibleFlag)
                .orElse(null);
    }

    private Integer getClusterNCcFlagStatusPartial(Set<StrategyFlClusPrgmEligRanking> strategyFlClusPrgmEligRankings) {
        return Optional.ofNullable(strategyFlClusPrgmEligRankings)
                .stream()
                .flatMap(Collection::stream)
                .filter(strategyFlClusPrgmEligRanking -> strategyFlClusPrgmEligRanking.getStrategyFlClusPrgmEligRankingId()
                        .getStrategyFlClusEligRankingId().getPlanClusterStrategyId().getAnalyticsClusterId().equals(0))
                .findFirst()
                .map(StrategyFlClusPrgmEligRanking::getEligStyleClusProgs)
                .stream()
                .flatMap(Collection::stream)
                .findFirst()
                .map(EligStyleClusProg::getEligCcClusProgs)
                .stream()
                .flatMap(Collection::stream)
                .findFirst()
                .map(EligCcClusProg::getIsEligibleFlag)
                .orElse(null);
    }

    private Integer getAllFlagAtFineline(Set<StrategyFlClusPrgmEligRanking> strategyFlClusPrgmEligRankings) {
        return Optional.ofNullable(strategyFlClusPrgmEligRankings)
                .stream()
                .flatMap(Collection::stream)
                .filter(strategyFlClusPrgmEligRanking -> strategyFlClusPrgmEligRanking.getStrategyFlClusPrgmEligRankingId()
                        .getStrategyFlClusEligRankingId().getPlanClusterStrategyId().getAnalyticsClusterId().equals(0))
                .findFirst()
                .map(StrategyFlClusPrgmEligRanking::getIsEligibleFlag)
                .orElse(null);
    }

    private Integer getAllFlagAtFinelinePartial(Set<StrategyFlClusPrgmEligRanking> strategyFlClusPrgmEligRankings) {
        return Optional.ofNullable(strategyFlClusPrgmEligRankings)
                .stream()
                .flatMap(Collection::stream)
                .filter(strategyFlClusPrgmEligRanking -> strategyFlClusPrgmEligRanking.getStrategyFlClusPrgmEligRankingId()
                        .getStrategyFlClusEligRankingId().getPlanClusterStrategyId().getAnalyticsClusterId().equals(0))
                .findFirst()
                .map(StrategyFlClusPrgmEligRanking::getIsEligibleFlag)
                .orElse(null);
    }

    private Set<Integer> getAllCcFlagStatus(Set<StrategyFlClusPrgmEligRanking> strategyFlClusPrgmEligRankings){
        Set<Integer> ccFlags = new HashSet<>();
        Optional.ofNullable(strategyFlClusPrgmEligRankings)
                .stream()
                .flatMap(Collection::stream)
                .map(StrategyFlClusPrgmEligRanking::getEligStyleClusProgs)
                .forEach(eligStyleClusProgs -> {
                    Optional.ofNullable(eligStyleClusProgs)
                            .stream()
                            .flatMap(Collection::stream)
                            .forEach(eligStyleClusProg -> {
                                ccFlags.addAll(Optional.ofNullable(eligStyleClusProg.getEligCcClusProgs())
                                        .stream()
                                        .flatMap(Collection::stream)
                                        .map(EligCcClusProg::getIsEligibleFlag)
                                        .collect(Collectors.toSet()));
                            });
                });
        return ccFlags;
    }

    private Set<Integer> getAllCcFlagStatusPartial(Set<StrategyFlClusPrgmEligRanking> strategyFlClusPrgmEligRankings){
        Set<Integer> ccFlags = new HashSet<>();
        Optional.ofNullable(strategyFlClusPrgmEligRankings)
                .stream()
                .flatMap(Collection::stream)
                .map(StrategyFlClusPrgmEligRanking::getEligStyleClusProgs)
                .forEach(eligStyleClusProgs -> {
                    Optional.ofNullable(eligStyleClusProgs)
                            .stream()
                            .flatMap(Collection::stream)
                            .forEach(eligStyleClusProg -> {
                                ccFlags.addAll(Optional.ofNullable(eligStyleClusProg.getEligCcClusProgs())
                                        .stream()
                                        .flatMap(Collection::stream)
                                        .map(EligCcClusProg::getIsEligibleFlag)
                                        .collect(Collectors.toSet()));
                            });
                });
        return ccFlags;
    }
}
