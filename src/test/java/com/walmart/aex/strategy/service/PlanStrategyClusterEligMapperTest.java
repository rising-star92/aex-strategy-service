package com.walmart.aex.strategy.service;

import com.walmart.aex.strategy.dto.request.*;
import com.walmart.aex.strategy.entity.*;
import com.walmart.aex.strategy.properties.AppProperties;
import com.walmart.aex.strategy.repository.StrategyClusterRepository;
import com.walmart.aex.strategy.util.GetStrategyFlClusEligRankingUtil;
import org.junit.jupiter.api.Assertions;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlanStrategyClusterEligMapperTest {

    @InjectMocks
    private PlanStrategyClusterEligMapper planStrategyClusterEligMapper;

    @Mock
    private StrategyClusterRepository strategyClusterRepository;

    @Mock
    private AnalyticsClusterStoreCountService analyticsClusterStoreCountService;

    @Mock
    private AppProperties appProperties;

    @BeforeEach
    public void setup() {
        appProperties = PowerMockito.mock(AppProperties.class);
        planStrategyClusterEligMapper = new PlanStrategyClusterEligMapper(strategyClusterRepository, analyticsClusterStoreCountService);
        ReflectionTestUtils.setField(planStrategyClusterEligMapper, "appProperties", appProperties);
    }

    @Test
    void testSetPlanClusterStrategy() {
        //Arrange
        PlanStrategy planStrategy = new PlanStrategy();
        List<Lvl3> lvl3Set = GetStrategyFlClusEligRankingUtil.getLvl3List();
        PlanStrategyDTO request = GetStrategyFlClusEligRankingUtil.getPlanStrategyRequest();
        Lvl1 lvl1 = GetStrategyFlClusEligRankingUtil.getLvl1List().stream().findFirst().orElse(null);
        Lvl2 lvl2 = GetStrategyFlClusEligRankingUtil.getLvl2List().stream().findFirst().orElse(null);
        PlanStrategyId planStrategyId = GetStrategyFlClusEligRankingUtil.getPlanStrategyIdForCreate();
        List<String> cluster1Offshore = new ArrayList<>();
        cluster1Offshore.add("PR");
        List<String> cluster7Offshore = new ArrayList<>();
        cluster7Offshore.add("AK");
        List<Integer> clusterIds = new ArrayList<>();
        clusterIds.add(0);
        clusterIds.add(1);
        clusterIds.add(2);
        clusterIds.add(3);
        clusterIds.add(4);
        clusterIds.add(5);
        clusterIds.add(6);
        clusterIds.add(7);

        when(appProperties.getCluster1OffshoreList()).thenReturn(cluster1Offshore);
        when(appProperties.getCluster7OffshoreList()).thenReturn(cluster7Offshore);
        when(strategyClusterRepository.findAllWeatherClustersForStrategy(anyLong())).thenReturn(clusterIds);

        //Act
        Set<PlanClusterStrategy> planClusterStrategies = planStrategyClusterEligMapper.setPlanClusterStrategy(planStrategy, lvl3Set, planStrategyId, request, lvl1, lvl2);

        // Assert
        Assertions.assertNotNull(planClusterStrategies);
        Assertions.assertEquals(8, planClusterStrategies.size());
    }
    @Test
    void testSetPlanClusterStrategy2() {
        //Arrange
        PlanStrategy planStrategy = new PlanStrategy();
        List<Lvl3> lvl3Set = GetStrategyFlClusEligRankingUtil.getLvl3List();
        PlanStrategyDTO request = GetStrategyFlClusEligRankingUtil.getPlanStrategyRequest();
        Lvl1 lvl1 = GetStrategyFlClusEligRankingUtil.getLvl1List().stream().findFirst().orElse(null);
        Lvl2 lvl2 = GetStrategyFlClusEligRankingUtil.getLvl2List().stream().findFirst().orElse(null);
        PlanStrategyId planStrategyId = GetStrategyFlClusEligRankingUtil.getPlanStrategyIdForCreate();
        List<String> cluster2Offshore = new ArrayList<>();
        cluster2Offshore.add("HI");
        List<String> cluster7Offshore = new ArrayList<>();
        cluster7Offshore.add("AK");
        List<Integer> clusterIds = new ArrayList<>();
        clusterIds.add(0);
        clusterIds.add(1);
        clusterIds.add(2);
        clusterIds.add(3);
        clusterIds.add(4);
        clusterIds.add(5);
        clusterIds.add(6);
        clusterIds.add(7);

        Mockito.lenient().when(appProperties.getCluster2OffshoreList()).thenReturn(cluster2Offshore);
        when(appProperties.getCluster7OffshoreList()).thenReturn(cluster7Offshore);
        when(strategyClusterRepository.findAllWeatherClustersForStrategy(anyLong())).thenReturn(clusterIds);

        //Act
        Set<PlanClusterStrategy> planClusterStrategies = planStrategyClusterEligMapper.setPlanClusterStrategy(planStrategy, lvl3Set, planStrategyId, request, lvl1, lvl2);

        // Assert
        Assertions.assertNotNull(planClusterStrategies);
        Assertions.assertEquals(8, planClusterStrategies.size());
    }
    @Test
    void testFetchFinelineWeatherClustersWithOnlyAllCluster() {

        Long strategyId = 1L;
        Fineline fineline = new Fineline();
        Strategy strategy = new Strategy();
        List<WeatherCluster> weatherClusters = new ArrayList<>();

        WeatherCluster weatherCluster1 = new WeatherCluster();
        ClusterType type0 = new ClusterType();
        type0.setAnalyticsClusterId(0);
        type0.setAnalyticsClusterDesc("all");
        weatherCluster1.setType(type0);
        FiscalWeek instoreFiscalWeek = new FiscalWeek();
        instoreFiscalWeek.setFiscalWeekDesc("FYE2021WK13");

        FiscalWeek markdownFiscalWeek = new FiscalWeek();
        markdownFiscalWeek.setFiscalWeekDesc("FYE2021WK19");
        weatherCluster1.setInStoreDate(instoreFiscalWeek);
        weatherCluster1.setMarkDownDate(markdownFiscalWeek);

        WeatherCluster weatherCluster2 = new WeatherCluster();
        weatherCluster2.setType(type0);
        weatherCluster2.setInStoreDate(instoreFiscalWeek);
        weatherCluster2.setMarkDownDate(markdownFiscalWeek);

        weatherClusters.add(weatherCluster1);
        weatherClusters.add(weatherCluster2);
        strategy.setWeatherClusters(weatherClusters);
        fineline.setFinelineNbr(456);
        fineline.setFinelineName("Mens Apparel");
        fineline.setStrategy(strategy);

        List<Integer> clusterIds = new ArrayList<>();
        clusterIds.add(0);
        clusterIds.add(1);
        clusterIds.add(2);
        clusterIds.add(3);
        clusterIds.add(4);
        clusterIds.add(5);
        clusterIds.add(6);
        clusterIds.add(7);
        when(strategyClusterRepository.findAllWeatherClustersForStrategy(anyLong())).thenReturn(clusterIds);
        //Act
        Set<WeatherCluster> responseWeatherClusters = planStrategyClusterEligMapper.fetchFinelineWeatherClusters(fineline, strategyId);
        //Assert
        Assertions.assertEquals(8, responseWeatherClusters.size());

    }

    @Test
    void testFetchFinelineWeatherClusters() {

        Long strategyId = 1L;
        Fineline fineline = new Fineline();
        Strategy strategy = new Strategy();
        List<WeatherCluster> weatherClusters = new ArrayList<>();

        WeatherCluster weatherCluster1 = new WeatherCluster();
        ClusterType type0 = new ClusterType();
        type0.setAnalyticsClusterId(0);
        type0.setAnalyticsClusterDesc("all");
        weatherCluster1.setType(type0);
        FiscalWeek instoreFiscalWeek = new FiscalWeek();
        instoreFiscalWeek.setFiscalWeekDesc("FYE2021WK13");

        FiscalWeek markdownFiscalWeek = new FiscalWeek();
        markdownFiscalWeek.setFiscalWeekDesc("FYE2021WK19");
        weatherCluster1.setInStoreDate(instoreFiscalWeek);
        weatherCluster1.setMarkDownDate(markdownFiscalWeek);

        WeatherCluster weatherCluster2 = new WeatherCluster();
        ClusterType type1 = new ClusterType();
        type1.setAnalyticsClusterId(2);
        type1.setAnalyticsClusterDesc("cluster 2");
        weatherCluster2.setType(type1);
        weatherCluster2.setInStoreDate(instoreFiscalWeek);
        weatherCluster2.setMarkDownDate(markdownFiscalWeek);

        WeatherCluster weatherCluster3 = new WeatherCluster();
        ClusterType type3 = new ClusterType();
        type3.setAnalyticsClusterId(3);
        type3.setAnalyticsClusterDesc("cluster 3");
        weatherCluster3.setType(type3);
        weatherCluster3.setInStoreDate(instoreFiscalWeek);
        weatherCluster3.setMarkDownDate(markdownFiscalWeek);


        weatherClusters.add(weatherCluster1);
        weatherClusters.add(weatherCluster2);
        weatherClusters.add(weatherCluster3);
        strategy.setWeatherClusters(weatherClusters);
        fineline.setFinelineNbr(456);
        fineline.setFinelineName("Mens Apparel");
        fineline.setStrategy(strategy);

        List<Integer> clusterIds = new ArrayList<>();
        clusterIds.add(0);
        clusterIds.add(1);
        clusterIds.add(2);
        clusterIds.add(3);
        clusterIds.add(4);
        clusterIds.add(5);
        clusterIds.add(6);
        clusterIds.add(7);
        when(strategyClusterRepository.findAllWeatherClustersForStrategy(anyLong())).thenReturn(clusterIds);
        //Act
        Set<WeatherCluster> responseWeatherClusters = planStrategyClusterEligMapper.fetchFinelineWeatherClusters(fineline, strategyId);
        //Assert
        Assertions.assertEquals(8, responseWeatherClusters.size());

    }

    @Test
    void testFetchStrategyFlMktCustEligProgramAddingCluster1Offshore() {

        WeatherCluster weatherCluster = new WeatherCluster();
        ClusterType type0 = new ClusterType();
        type0.setAnalyticsClusterId(0);
        type0.setAnalyticsClusterDesc("all");
        weatherCluster.setType(type0);
        weatherCluster.setIsEligible(true);

        StrategyFlClusPrgmEligRanking strategyFlClusPrgmEligRanking = new StrategyFlClusPrgmEligRanking();
        StrategyFlClusPrgmEligRankingId strategyFlClusPrgmEligRankingIdCluster1 = new StrategyFlClusPrgmEligRankingId();
        StrategyFlClusEligRankingId strategyFlClusEligRankingId = new StrategyFlClusEligRankingId();
        strategyFlClusEligRankingId.setLvl0Nbr(50000);
        strategyFlClusEligRankingId.setLvl1Nbr(34);
        strategyFlClusEligRankingId.setLvl2Nbr(1488);
        strategyFlClusEligRankingId.setLvl3Nbr(12775);
        strategyFlClusEligRankingId.setLvl4Nbr(34007);
        strategyFlClusEligRankingId.setFinelineNbr(457);

        PlanClusterStrategyId planClusterStrategyIdCluster1 = new PlanClusterStrategyId();
        planClusterStrategyIdCluster1.setAnalyticsClusterId(1);

        PlanStrategyId planStrategyId12 = new PlanStrategyId();
        planStrategyId12.setPlanId(12L);
        planStrategyId12.setStrategyId(6L);

        planClusterStrategyIdCluster1.setPlanStrategyId(planStrategyId12);

        strategyFlClusEligRankingId.setPlanClusterStrategyId(planClusterStrategyIdCluster1);

        strategyFlClusPrgmEligRankingIdCluster1.setProgramId(390L);
        strategyFlClusPrgmEligRankingIdCluster1.setStrategyFlClusEligRankingId(strategyFlClusEligRankingId);

        strategyFlClusPrgmEligRanking.setStrategyFlClusPrgmEligRankingId(strategyFlClusPrgmEligRankingIdCluster1);

        List<String> cluster1Offshore = new ArrayList<>();
        cluster1Offshore.add("PR");

        Mockito.lenient().when(appProperties.getCluster1OffshoreList()).thenReturn(cluster1Offshore);

        StrategyFlClusPrgmEligRankingId strategyFlClusPrgmEligRankingId =  new StrategyFlClusPrgmEligRankingId();
        strategyFlClusPrgmEligRankingId.setProgramId(123L);
        strategyFlClusPrgmEligRankingId.setStrategyFlClusEligRankingId(strategyFlClusEligRankingId);

        //Act
        Set<FinelineMarketClusterPrgElg> responseFinelineMarketClusterPrgElg = planStrategyClusterEligMapper.fetchStrategyFlMktCustEligProgram(weatherCluster, strategyFlClusPrgmEligRanking, strategyFlClusPrgmEligRankingId);

        Set<Integer> marketSelectCode =
                Optional.ofNullable(responseFinelineMarketClusterPrgElg)
                        .stream()
                        .flatMap(Collection::stream)
                        .map(FinelineMarketClusterPrgElg::getFinelineMarketClusterPrgElgId )
                        .map(FinelineMarketClusterPrgElgId::getMarketSelectCode)
                        .collect(Collectors.toSet());
        //Assert
        assert responseFinelineMarketClusterPrgElg != null;
        Assertions.assertEquals(1, responseFinelineMarketClusterPrgElg.size());
        assertTrue(marketSelectCode.contains(1));
    }

    @Test
    void testFetchStrategyFlMktCustEligProgramAddingCluster2Offshore() {

        WeatherCluster weatherCluster = new WeatherCluster();
        ClusterType type0 = new ClusterType();
        type0.setAnalyticsClusterId(0);
        type0.setAnalyticsClusterDesc("all");
        weatherCluster.setType(type0);
        weatherCluster.setIsEligible(true);

        StrategyFlClusPrgmEligRanking strategyFlClusPrgmEligRanking = new StrategyFlClusPrgmEligRanking();
        StrategyFlClusPrgmEligRankingId strategyFlClusPrgmEligRankingIdCluster1 = new StrategyFlClusPrgmEligRankingId();
        StrategyFlClusEligRankingId strategyFlClusEligRankingId = new StrategyFlClusEligRankingId();
        strategyFlClusEligRankingId.setLvl0Nbr(50000);
        strategyFlClusEligRankingId.setLvl1Nbr(34);
        strategyFlClusEligRankingId.setLvl2Nbr(1488);
        strategyFlClusEligRankingId.setLvl3Nbr(12775);
        strategyFlClusEligRankingId.setLvl4Nbr(34007);
        strategyFlClusEligRankingId.setFinelineNbr(457);

        PlanClusterStrategyId planClusterStrategyIdCluster1 = new PlanClusterStrategyId();
        planClusterStrategyIdCluster1.setAnalyticsClusterId(2);

        PlanStrategyId planStrategyId12 = new PlanStrategyId();
        planStrategyId12.setPlanId(12L);
        planStrategyId12.setStrategyId(6L);

        planClusterStrategyIdCluster1.setPlanStrategyId(planStrategyId12);

        strategyFlClusEligRankingId.setPlanClusterStrategyId(planClusterStrategyIdCluster1);

        strategyFlClusPrgmEligRankingIdCluster1.setProgramId(390L);
        strategyFlClusPrgmEligRankingIdCluster1.setStrategyFlClusEligRankingId(strategyFlClusEligRankingId);

        strategyFlClusPrgmEligRanking.setStrategyFlClusPrgmEligRankingId(strategyFlClusPrgmEligRankingIdCluster1);

        List<String> cluster2Offshore = new ArrayList<>();
        cluster2Offshore.add("HI");

        Mockito.lenient().when(appProperties.getCluster2OffshoreList()).thenReturn(cluster2Offshore);

        StrategyFlClusPrgmEligRankingId strategyFlClusPrgmEligRankingId =  new StrategyFlClusPrgmEligRankingId();
        strategyFlClusPrgmEligRankingId.setProgramId(123L);
        strategyFlClusPrgmEligRankingId.setStrategyFlClusEligRankingId(strategyFlClusEligRankingId);

        //Act
        Set<FinelineMarketClusterPrgElg> responseFinelineMarketClusterPrgElg = planStrategyClusterEligMapper.fetchStrategyFlMktCustEligProgram(weatherCluster, strategyFlClusPrgmEligRanking, strategyFlClusPrgmEligRankingId);

        Set<Integer> marketSelectCode =
                Optional.ofNullable(responseFinelineMarketClusterPrgElg)
                        .stream()
                        .flatMap(Collection::stream)
                        .map(FinelineMarketClusterPrgElg::getFinelineMarketClusterPrgElgId )
                        .map(FinelineMarketClusterPrgElgId::getMarketSelectCode)
                        .collect(Collectors.toSet());
        //Assert
        assert responseFinelineMarketClusterPrgElg != null;
        Assertions.assertEquals(1, responseFinelineMarketClusterPrgElg.size());
        assertTrue(marketSelectCode.contains(3));

    }

    @Test
    void testFetchStrategyFlMktCustEligProgramAddingCluster7Offshore() {

        WeatherCluster weatherCluster = new WeatherCluster();
        ClusterType type0 = new ClusterType();
        type0.setAnalyticsClusterId(0);
        type0.setAnalyticsClusterDesc("all");
        weatherCluster.setType(type0);
        weatherCluster.setIsEligible(true);

        StrategyFlClusPrgmEligRanking strategyFlClusPrgmEligRanking = new StrategyFlClusPrgmEligRanking();
        StrategyFlClusPrgmEligRankingId strategyFlClusPrgmEligRankingIdCluster1 = new StrategyFlClusPrgmEligRankingId();
        StrategyFlClusEligRankingId strategyFlClusEligRankingId = new StrategyFlClusEligRankingId();
        strategyFlClusEligRankingId.setLvl0Nbr(50000);
        strategyFlClusEligRankingId.setLvl1Nbr(34);
        strategyFlClusEligRankingId.setLvl2Nbr(1488);
        strategyFlClusEligRankingId.setLvl3Nbr(12775);
        strategyFlClusEligRankingId.setLvl4Nbr(34007);
        strategyFlClusEligRankingId.setFinelineNbr(457);

        PlanClusterStrategyId planClusterStrategyIdCluster1 = new PlanClusterStrategyId();
        planClusterStrategyIdCluster1.setAnalyticsClusterId(7);

        PlanStrategyId planStrategyId12 = new PlanStrategyId();
        planStrategyId12.setPlanId(12L);
        planStrategyId12.setStrategyId(6L);

        planClusterStrategyIdCluster1.setPlanStrategyId(planStrategyId12);

        strategyFlClusEligRankingId.setPlanClusterStrategyId(planClusterStrategyIdCluster1);

        strategyFlClusPrgmEligRankingIdCluster1.setProgramId(390L);
        strategyFlClusPrgmEligRankingIdCluster1.setStrategyFlClusEligRankingId(strategyFlClusEligRankingId);

        strategyFlClusPrgmEligRanking.setStrategyFlClusPrgmEligRankingId(strategyFlClusPrgmEligRankingIdCluster1);

        List<String> cluster7Offshore = new ArrayList<>();
        cluster7Offshore.add("AK");

        Mockito.lenient().when(appProperties.getCluster7OffshoreList()).thenReturn(cluster7Offshore);

        StrategyFlClusPrgmEligRankingId strategyFlClusPrgmEligRankingId =  new StrategyFlClusPrgmEligRankingId();
        strategyFlClusPrgmEligRankingId.setProgramId(123L);
        strategyFlClusPrgmEligRankingId.setStrategyFlClusEligRankingId(strategyFlClusEligRankingId);

        //Act
        Set<FinelineMarketClusterPrgElg> responseFinelineMarketClusterPrgElg = planStrategyClusterEligMapper.fetchStrategyFlMktCustEligProgram(weatherCluster, strategyFlClusPrgmEligRanking, strategyFlClusPrgmEligRankingId);

        Set<Integer> marketSelectCode =
                Optional.ofNullable(responseFinelineMarketClusterPrgElg)
                        .stream()
                        .flatMap(Collection::stream)
                        .map(FinelineMarketClusterPrgElg::getFinelineMarketClusterPrgElgId )
                        .map(FinelineMarketClusterPrgElgId::getMarketSelectCode)
                        .collect(Collectors.toSet());
        //Assert
        assert responseFinelineMarketClusterPrgElg != null;
        Assertions.assertEquals(1, responseFinelineMarketClusterPrgElg.size());
        assertTrue(marketSelectCode.contains(2));

    }
}
