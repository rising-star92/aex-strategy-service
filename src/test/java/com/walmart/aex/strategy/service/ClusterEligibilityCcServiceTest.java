package com.walmart.aex.strategy.service;

import com.walmart.aex.strategy.dto.request.*;
import com.walmart.aex.strategy.entity.PlanStrategyId;
import com.walmart.aex.strategy.entity.StrategyFlClusEligRanking;
import com.walmart.aex.strategy.properties.AppProperties;
import com.walmart.aex.strategy.repository.StrategyCcClusEligRankingRepository;
import com.walmart.aex.strategy.util.GetStrategyFlClusEligRankingUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.powermock.api.mockito.PowerMockito;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClusterEligibilityCcServiceTest {
    @Spy
    @InjectMocks
    private ClusterEligibilityCcService clusterEligibilityCcService;

    @Mock
    private StrategyCcClusEligRankingRepository strategyCcClusEligRankingRepository;

    @Test
    void testUpdateCcMetricsAllSetFalseCcPartial() {
        //Arrange
        UpdatedFields updatedFields = new UpdatedFields();
        Field weatherClusterField = new Field();
        weatherClusterField.setKey("isEligible");
        weatherClusterField.setValue("false");
        updatedFields.setWeatherCluster(Arrays.asList(weatherClusterField));

        Fineline fineline = new Fineline();
        fineline.setFinelineNbr(51);
        fineline.setFinelineName("Women Hoodie");
        Style style = new Style();
        style.setStyleNbr("51_2_23_001");


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

        CustomerChoice customerChoice = new CustomerChoice();
        customerChoice.setCcId("51_2_23_001_001");
        customerChoice.setStrategy(strategy);
        customerChoice.setUpdatedFields(updatedFields);
        style.setCustomerChoices(Collections.singletonList(customerChoice));
        fineline.setStyles(Collections.singletonList(style));
        Set<String> updatedField = new HashSet<>();

        PlanStrategyId planStrategyId = GetStrategyFlClusEligRankingUtil.getPlanStrategyId();


        doReturn(GetStrategyFlClusEligRankingUtil.getStrategyCcClusEligRankingPartial())
                .when(strategyCcClusEligRankingRepository).findStrategyCcClusEligRankingByStrategyCcClusEligRankingId_StrategyStyleClusId_StrategyFlClusEligRankingId_PlanClusterStrategyId_PlanStrategyIdAndStrategyCcClusEligRankingId_StrategyStyleClusId_StrategyFlClusEligRankingId_finelineNbrAndStrategyCcClusEligRankingId_StrategyStyleClusId_styleNbrAndStrategyCcClusEligRankingId_ccId(any(PlanStrategyId.class), anyInt(), anyString(), anyString());
        //Act
        Set<StrategyFlClusEligRanking> strategyFlClusEligRankings = GetStrategyFlClusEligRankingUtil.getStrategyFlClusEligRankings(false)
                .orElse(null);
        clusterEligibilityCcService.updateClusterEligibilityMetricsAtStyleCc(strategyFlClusEligRankings, fineline.getStyles(), planStrategyId, fineline.getFinelineNbr(),updatedField);
        //Assert
        verify(strategyCcClusEligRankingRepository, times(1)).findStrategyCcClusEligRankingByStrategyCcClusEligRankingId_StrategyStyleClusId_StrategyFlClusEligRankingId_PlanClusterStrategyId_PlanStrategyIdAndStrategyCcClusEligRankingId_StrategyStyleClusId_StrategyFlClusEligRankingId_finelineNbrAndStrategyCcClusEligRankingId_StrategyStyleClusId_styleNbrAndStrategyCcClusEligRankingId_ccId(any(PlanStrategyId.class), anyInt(), anyString(), anyString());
    }
    @Test
    void testUpdateCcMetricsInStoreSetCc() {
        //Arrange
        UpdatedFields updatedFields = new UpdatedFields();
        Field weatherClusterField = new Field();
        weatherClusterField.setKey("inStoreDate");
        weatherClusterField.setValue("FYE2023WK21");
        updatedFields.setWeatherCluster(Arrays.asList(weatherClusterField));

        FiscalWeek fiscalWeek = new FiscalWeek();
        fiscalWeek.setFiscalWeekDesc("FYE2023WK21");

        Fineline fineline = new Fineline();
        fineline.setFinelineNbr(51);
        fineline.setFinelineName("Women Hoodie");
        Style style = new Style();
        style.setStyleNbr("51_2_23_001");


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

        CustomerChoice customerChoice = new CustomerChoice();
        customerChoice.setCcId("51_2_23_001_001");
        customerChoice.setStrategy(strategy);
        customerChoice.setUpdatedFields(updatedFields);
        style.setCustomerChoices(Collections.singletonList(customerChoice));
        fineline.setStyles(Collections.singletonList(style));
        Set<String> updatedField = new HashSet<>();

        PlanStrategyId planStrategyId = GetStrategyFlClusEligRankingUtil.getPlanStrategyId();

        doReturn(GetStrategyFlClusEligRankingUtil.getStrategyCcClusEligRanking())
                .when(strategyCcClusEligRankingRepository).findStrategyCcClusEligRankingByStrategyCcClusEligRankingId_StrategyStyleClusId_StrategyFlClusEligRankingId_PlanClusterStrategyId_PlanStrategyIdAndStrategyCcClusEligRankingId_StrategyStyleClusId_StrategyFlClusEligRankingId_finelineNbrAndStrategyCcClusEligRankingId_StrategyStyleClusId_styleNbrAndStrategyCcClusEligRankingId_ccId(any(PlanStrategyId.class), anyInt(), anyString(), anyString());
        //Act
        Set<StrategyFlClusEligRanking> strategyFlClusEligRankings = GetStrategyFlClusEligRankingUtil.getStrategyFlClusEligRankings(false)
                .orElse(null);
        clusterEligibilityCcService.updateClusterEligibilityMetricsAtStyleCc(strategyFlClusEligRankings, fineline.getStyles(), planStrategyId, fineline.getFinelineNbr(),updatedField);
        //Assert
        verify(strategyCcClusEligRankingRepository, times(1)).findStrategyCcClusEligRankingByStrategyCcClusEligRankingId_StrategyStyleClusId_StrategyFlClusEligRankingId_PlanClusterStrategyId_PlanStrategyIdAndStrategyCcClusEligRankingId_StrategyStyleClusId_StrategyFlClusEligRankingId_finelineNbrAndStrategyCcClusEligRankingId_StrategyStyleClusId_styleNbrAndStrategyCcClusEligRankingId_ccId(any(PlanStrategyId.class), anyInt(), anyString(), anyString());
        assertEquals(true, updatedField.contains("inStoreDate"));
    }

    @Test
    void testUpdateCcMetricsMarkDownSetCc() {
        //Arrange
        UpdatedFields updatedFields = new UpdatedFields();
        Field weatherClusterField = new Field();
        weatherClusterField.setKey("markDownDate");
        weatherClusterField.setValue("FYE2023WK31");
        updatedFields.setWeatherCluster(Arrays.asList(weatherClusterField));

        FiscalWeek fiscalWeek = new FiscalWeek();
        fiscalWeek.setFiscalWeekDesc("FYE2023WK31");

        Fineline fineline = new Fineline();
        fineline.setFinelineNbr(51);
        fineline.setFinelineName("Women Hoodie");
        Style style = new Style();
        style.setStyleNbr("51_2_23_001");


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

        CustomerChoice customerChoice = new CustomerChoice();
        customerChoice.setCcId("51_2_23_001_001");
        customerChoice.setStrategy(strategy);
        customerChoice.setUpdatedFields(updatedFields);
        style.setCustomerChoices(Collections.singletonList(customerChoice));
        fineline.setStyles(Collections.singletonList(style));
        Set<String> updatedField = new HashSet<>();

        PlanStrategyId planStrategyId = GetStrategyFlClusEligRankingUtil.getPlanStrategyId();

        doReturn(GetStrategyFlClusEligRankingUtil.getStrategyCcClusEligRanking())
                .when(strategyCcClusEligRankingRepository).findStrategyCcClusEligRankingByStrategyCcClusEligRankingId_StrategyStyleClusId_StrategyFlClusEligRankingId_PlanClusterStrategyId_PlanStrategyIdAndStrategyCcClusEligRankingId_StrategyStyleClusId_StrategyFlClusEligRankingId_finelineNbrAndStrategyCcClusEligRankingId_StrategyStyleClusId_styleNbrAndStrategyCcClusEligRankingId_ccId(any(PlanStrategyId.class), anyInt(), anyString(), anyString());
        //Act
        Set<StrategyFlClusEligRanking> strategyFlClusEligRankings = GetStrategyFlClusEligRankingUtil.getStrategyFlClusEligRankings(false)
                .orElse(null);
        clusterEligibilityCcService.updateClusterEligibilityMetricsAtStyleCc(strategyFlClusEligRankings, fineline.getStyles(), planStrategyId, fineline.getFinelineNbr(),updatedField);
        //Assert
        assertEquals(true, updatedField.contains("markDownDate"));
        verify(strategyCcClusEligRankingRepository, times(1)).findStrategyCcClusEligRankingByStrategyCcClusEligRankingId_StrategyStyleClusId_StrategyFlClusEligRankingId_PlanClusterStrategyId_PlanStrategyIdAndStrategyCcClusEligRankingId_StrategyStyleClusId_StrategyFlClusEligRankingId_finelineNbrAndStrategyCcClusEligRankingId_StrategyStyleClusId_styleNbrAndStrategyCcClusEligRankingId_ccId(any(PlanStrategyId.class), anyInt(), anyString(), anyString());
    }

    @Test
    void testUpdateCcMetricsRankingSetCc() {
        //Arrange
        UpdatedFields updatedFields = new UpdatedFields();
        Field weatherClusterField = new Field();
        weatherClusterField.setKey("ranking");
        weatherClusterField.setValue("1");
        updatedFields.setWeatherCluster(Arrays.asList(weatherClusterField));

        FiscalWeek fiscalWeek = new FiscalWeek();
        fiscalWeek.setFiscalWeekDesc("FYE2023WK31");

        Fineline fineline = new Fineline();
        fineline.setFinelineNbr(51);
        fineline.setFinelineName("Women Hoodie");
        Style style = new Style();
        style.setStyleNbr("51_2_23_001");


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

        CustomerChoice customerChoice = new CustomerChoice();
        customerChoice.setCcId("51_2_23_001_001");
        customerChoice.setStrategy(strategy);
        customerChoice.setUpdatedFields(updatedFields);
        style.setCustomerChoices(Collections.singletonList(customerChoice));
        fineline.setStyles(Collections.singletonList(style));

        PlanStrategyId planStrategyId = GetStrategyFlClusEligRankingUtil.getPlanStrategyId();

        Set<String> updatedField = new HashSet<>();

        doReturn(GetStrategyFlClusEligRankingUtil.getStrategyCcClusEligRanking())
                .when(strategyCcClusEligRankingRepository).findStrategyCcClusEligRankingByStrategyCcClusEligRankingId_StrategyStyleClusId_StrategyFlClusEligRankingId_PlanClusterStrategyId_PlanStrategyIdAndStrategyCcClusEligRankingId_StrategyStyleClusId_StrategyFlClusEligRankingId_finelineNbrAndStrategyCcClusEligRankingId_StrategyStyleClusId_styleNbrAndStrategyCcClusEligRankingId_ccId(any(PlanStrategyId.class), anyInt(), anyString(), anyString());
        //Act
        Set<StrategyFlClusEligRanking> strategyFlClusEligRankings = GetStrategyFlClusEligRankingUtil.getStrategyFlClusEligRankings(false)
                .orElse(null);
        clusterEligibilityCcService.updateClusterEligibilityMetricsAtStyleCc(strategyFlClusEligRankings, fineline.getStyles(), planStrategyId, fineline.getFinelineNbr(),updatedField);
        //Assert
        verify(strategyCcClusEligRankingRepository, times(1)).findStrategyCcClusEligRankingByStrategyCcClusEligRankingId_StrategyStyleClusId_StrategyFlClusEligRankingId_PlanClusterStrategyId_PlanStrategyIdAndStrategyCcClusEligRankingId_StrategyStyleClusId_StrategyFlClusEligRankingId_finelineNbrAndStrategyCcClusEligRankingId_StrategyStyleClusId_styleNbrAndStrategyCcClusEligRankingId_ccId(any(PlanStrategyId.class), anyInt(), anyString(), anyString());
    }

    @Test
    void testUpdateCcMetricsRankingSetCcAll() {
        //Arrange
        UpdatedFields updatedFields = new UpdatedFields();
        Field weatherClusterField = new Field();
        weatherClusterField.setKey("ranking");
        weatherClusterField.setValue("1");
        updatedFields.setWeatherCluster(Arrays.asList(weatherClusterField));

        FiscalWeek fiscalWeek = new FiscalWeek();
        fiscalWeek.setFiscalWeekDesc("FYE2023WK31");

        Fineline fineline = new Fineline();
        fineline.setFinelineNbr(51);
        fineline.setFinelineName("Women Hoodie");
        Style style = new Style();
        style.setStyleNbr("51_2_23_001");


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

        CustomerChoice customerChoice = new CustomerChoice();
        customerChoice.setCcId("51_2_23_001_001");
        customerChoice.setStrategy(strategy);
        customerChoice.setUpdatedFields(updatedFields);
        style.setCustomerChoices(Collections.singletonList(customerChoice));
        fineline.setStyles(Collections.singletonList(style));
        Set<String> updatedField = new HashSet<>();

        PlanStrategyId planStrategyId = GetStrategyFlClusEligRankingUtil.getPlanStrategyId();

        doReturn(GetStrategyFlClusEligRankingUtil.getStrategyCcClusEligRanking())
                .when(strategyCcClusEligRankingRepository).findStrategyCcClusEligRankingByStrategyCcClusEligRankingId_StrategyStyleClusId_StrategyFlClusEligRankingId_PlanClusterStrategyId_PlanStrategyIdAndStrategyCcClusEligRankingId_StrategyStyleClusId_StrategyFlClusEligRankingId_finelineNbrAndStrategyCcClusEligRankingId_StrategyStyleClusId_styleNbrAndStrategyCcClusEligRankingId_ccId(any(PlanStrategyId.class), anyInt(), anyString(), anyString());
        //Act
        Set<StrategyFlClusEligRanking> strategyFlClusEligRankings = GetStrategyFlClusEligRankingUtil.getStrategyFlClusEligRankings(false)
                .orElse(null);
        clusterEligibilityCcService.updateClusterEligibilityMetricsAtStyleCc(strategyFlClusEligRankings, fineline.getStyles(), planStrategyId, fineline.getFinelineNbr(),updatedField);
        //Assert
        verify(strategyCcClusEligRankingRepository, times(1)).findStrategyCcClusEligRankingByStrategyCcClusEligRankingId_StrategyStyleClusId_StrategyFlClusEligRankingId_PlanClusterStrategyId_PlanStrategyIdAndStrategyCcClusEligRankingId_StrategyStyleClusId_StrategyFlClusEligRankingId_finelineNbrAndStrategyCcClusEligRankingId_StrategyStyleClusId_styleNbrAndStrategyCcClusEligRankingId_ccId(any(PlanStrategyId.class), anyInt(), anyString(), anyString());
        verify(clusterEligibilityCcService, times(1)).updateCcMetrics(anySet(), anyMap(), anyList(), any(CustomerChoice.class), any(PlanStrategyId.class));
        verify(clusterEligibilityCcService, times(1)).setMetricsForClusterAllCc(anyMap(),anyList(),any(WeatherCluster.class),anySet());
        verify(clusterEligibilityCcService, times(1)).setRankingForAllCc(anyList(),any(WeatherCluster.class));


    }

    @Test
    void testUpdateCcMetricsInStoreSetCcAll() {
        //Arrange
        UpdatedFields updatedFields = new UpdatedFields();
        Field weatherClusterField = new Field();
        weatherClusterField.setKey("inStoreDate");
        weatherClusterField.setValue("FYE2023WK21");
        updatedFields.setWeatherCluster(Arrays.asList(weatherClusterField));
        FiscalWeek inStoreFiscalWeek = new FiscalWeek();
        inStoreFiscalWeek.setFiscalWeekDesc("FYE2023WK21");

        FiscalWeek fiscalWeek = new FiscalWeek();
        fiscalWeek.setFiscalWeekDesc("FYE2023WK31");

        Fineline fineline = new Fineline();
        fineline.setFinelineNbr(51);
        fineline.setFinelineName("Women Hoodie");
        Style style = new Style();
        style.setStyleNbr("51_2_23_001");


        Strategy strategy = new Strategy();
        List<WeatherCluster> weatherClusterList = new ArrayList<>();
        WeatherCluster weatherCluster = new WeatherCluster();

        ClusterType type = new ClusterType();
        type.setAnalyticsClusterId(0);
        type.setAnalyticsClusterDesc("all");
        weatherCluster.setType(type);
        weatherCluster.setIsEligible(true);
        weatherCluster.setMarkDownDate(fiscalWeek);
        weatherCluster.setInStoreDate(inStoreFiscalWeek);
        weatherCluster.setRanking(1);
        weatherClusterList.add(weatherCluster);
        strategy.setWeatherClusters(weatherClusterList);

        CustomerChoice customerChoice = new CustomerChoice();
        customerChoice.setCcId("51_2_23_001_001");
        customerChoice.setStrategy(strategy);
        customerChoice.setUpdatedFields(updatedFields);
        style.setCustomerChoices(Collections.singletonList(customerChoice));
        fineline.setStyles(Collections.singletonList(style));

        Set<String> updatedField = new HashSet<>();

        PlanStrategyId planStrategyId = GetStrategyFlClusEligRankingUtil.getPlanStrategyId();

        doReturn(GetStrategyFlClusEligRankingUtil.getStrategyCcClusEligRanking())
                .when(strategyCcClusEligRankingRepository).findStrategyCcClusEligRankingByStrategyCcClusEligRankingId_StrategyStyleClusId_StrategyFlClusEligRankingId_PlanClusterStrategyId_PlanStrategyIdAndStrategyCcClusEligRankingId_StrategyStyleClusId_StrategyFlClusEligRankingId_finelineNbrAndStrategyCcClusEligRankingId_StrategyStyleClusId_styleNbrAndStrategyCcClusEligRankingId_ccId(any(PlanStrategyId.class), anyInt(), anyString(), anyString());
        //Act
        Set<StrategyFlClusEligRanking> strategyFlClusEligRankings = GetStrategyFlClusEligRankingUtil.getStrategyFlClusEligRankings(false)
                .orElse(null);
        clusterEligibilityCcService.updateClusterEligibilityMetricsAtStyleCc(strategyFlClusEligRankings, fineline.getStyles(), planStrategyId, fineline.getFinelineNbr(),updatedField);
        //Assert
        verify(strategyCcClusEligRankingRepository, times(1)).findStrategyCcClusEligRankingByStrategyCcClusEligRankingId_StrategyStyleClusId_StrategyFlClusEligRankingId_PlanClusterStrategyId_PlanStrategyIdAndStrategyCcClusEligRankingId_StrategyStyleClusId_StrategyFlClusEligRankingId_finelineNbrAndStrategyCcClusEligRankingId_StrategyStyleClusId_styleNbrAndStrategyCcClusEligRankingId_ccId(any(PlanStrategyId.class), anyInt(), anyString(), anyString());
        verify(clusterEligibilityCcService, times(1)).updateCcMetrics(anySet(), anyMap(), anyList(), any(CustomerChoice.class), any(PlanStrategyId.class));
        verify(clusterEligibilityCcService, times(1)).setMetricsForClusterAllCc(anyMap(),anyList(),any(WeatherCluster.class),anySet());
        verify(clusterEligibilityCcService, times(1)).setInStoreDateForAllCc(anyList(),any(WeatherCluster.class));
        assertEquals(true, updatedField.contains("inStoreDate"));


    }

    @Test
    void testUpdateCcMetricsMarkDownSetCcAll() {
        //Arrange
        UpdatedFields updatedFields = new UpdatedFields();
        Field weatherClusterField = new Field();
        weatherClusterField.setKey("markDownDate");
        weatherClusterField.setValue("FYE2023WK32");
        updatedFields.setWeatherCluster(Arrays.asList(weatherClusterField));
        FiscalWeek inStoreFiscalWeek = new FiscalWeek();
        inStoreFiscalWeek.setFiscalWeekDesc("FYE2023WK21");

        FiscalWeek fiscalWeek = new FiscalWeek();
        fiscalWeek.setFiscalWeekDesc("FYE2023WK31");

        Fineline fineline = new Fineline();
        fineline.setFinelineNbr(51);
        fineline.setFinelineName("Women Hoodie");
        Style style = new Style();
        style.setStyleNbr("51_2_23_001");


        Strategy strategy = new Strategy();
        List<WeatherCluster> weatherClusterList = new ArrayList<>();
        WeatherCluster weatherCluster = new WeatherCluster();

        ClusterType type = new ClusterType();
        type.setAnalyticsClusterId(0);
        type.setAnalyticsClusterDesc("all");
        weatherCluster.setType(type);
        weatherCluster.setIsEligible(true);
        weatherCluster.setMarkDownDate(fiscalWeek);
        weatherCluster.setInStoreDate(inStoreFiscalWeek);
        weatherCluster.setRanking(1);
        weatherClusterList.add(weatherCluster);
        strategy.setWeatherClusters(weatherClusterList);

        CustomerChoice customerChoice = new CustomerChoice();
        customerChoice.setCcId("51_2_23_001_001");
        customerChoice.setStrategy(strategy);
        customerChoice.setUpdatedFields(updatedFields);
        style.setCustomerChoices(Collections.singletonList(customerChoice));
        fineline.setStyles(Collections.singletonList(style));

        Set<String> updatedField = new HashSet<>();

        PlanStrategyId planStrategyId = GetStrategyFlClusEligRankingUtil.getPlanStrategyId();

        doReturn(GetStrategyFlClusEligRankingUtil.getStrategyCcClusEligRanking())
                .when(strategyCcClusEligRankingRepository).findStrategyCcClusEligRankingByStrategyCcClusEligRankingId_StrategyStyleClusId_StrategyFlClusEligRankingId_PlanClusterStrategyId_PlanStrategyIdAndStrategyCcClusEligRankingId_StrategyStyleClusId_StrategyFlClusEligRankingId_finelineNbrAndStrategyCcClusEligRankingId_StrategyStyleClusId_styleNbrAndStrategyCcClusEligRankingId_ccId(any(PlanStrategyId.class), anyInt(), anyString(), anyString());
        //Act
        Set<StrategyFlClusEligRanking> strategyFlClusEligRankings = GetStrategyFlClusEligRankingUtil.getStrategyFlClusEligRankings(false)
                .orElse(null);
        clusterEligibilityCcService.updateClusterEligibilityMetricsAtStyleCc(strategyFlClusEligRankings, fineline.getStyles(), planStrategyId, fineline.getFinelineNbr(),updatedField);
        //Assert
        verify(strategyCcClusEligRankingRepository, times(1)).findStrategyCcClusEligRankingByStrategyCcClusEligRankingId_StrategyStyleClusId_StrategyFlClusEligRankingId_PlanClusterStrategyId_PlanStrategyIdAndStrategyCcClusEligRankingId_StrategyStyleClusId_StrategyFlClusEligRankingId_finelineNbrAndStrategyCcClusEligRankingId_StrategyStyleClusId_styleNbrAndStrategyCcClusEligRankingId_ccId(any(PlanStrategyId.class), anyInt(), anyString(), anyString());
        verify(clusterEligibilityCcService, times(1)).updateCcMetrics(anySet(), anyMap(), anyList(), any(CustomerChoice.class), any(PlanStrategyId.class));
        verify(clusterEligibilityCcService, times(1)).setMetricsForClusterAllCc(anyMap(),anyList(),any(WeatherCluster.class),anySet());
        verify(clusterEligibilityCcService, times(1)).setMarkDownDateForAllCc(anyList(),any(WeatherCluster.class));
        assertEquals(true, updatedField.contains("markDownDate"));


    }
}
