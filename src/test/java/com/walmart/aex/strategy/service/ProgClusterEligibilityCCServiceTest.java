package com.walmart.aex.strategy.service;

import com.walmart.aex.strategy.dto.request.*;
import com.walmart.aex.strategy.entity.EligCcClusProg;
import com.walmart.aex.strategy.entity.PlanStrategyId;
import com.walmart.aex.strategy.entity.StrategyFlClusEligRanking;
import com.walmart.aex.strategy.entity.StrategyFlClusPrgmEligRanking;
import com.walmart.aex.strategy.properties.AppProperties;
import com.walmart.aex.strategy.repository.EligCcClusProgRepository;
import com.walmart.aex.strategy.util.GetEligFlClusProgUtil;
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
class ProgClusterEligibilityCCServiceTest {

    @Spy
    @InjectMocks
    private ProgClusterEligibilityCcService progClusterEligibilityCcService;

    @Mock
    private EligCcClusProgRepository eligCcClusProgRepository;
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

        PlanStrategyId planStrategyId = GetStrategyFlClusEligRankingUtil.getPlanStrategyId();

        doReturn(GetEligFlClusProgUtil.getEligCcClusProgPartial())
                .when(eligCcClusProgRepository).findEligCcClusProgByEligCcClusProgId_EligStyleClusProgId_StrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_PlanClusterStrategyId_PlanStrategyIdAndEligCcClusProgId_EligStyleClusProgId_StrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_finelineNbrAndEligCcClusProgId_EligStyleClusProgId_styleNbrAndEligCcClusProgId_ccIdAndEligCcClusProgId_EligStyleClusProgId_StrategyFlClusPrgmEligRankingId_ProgramId(any(PlanStrategyId.class), anyInt(), anyString(), anyString(), anyLong());
        Set<String> updatedField = new HashSet<>();
        //Act
        Set<StrategyFlClusEligRanking> strategyFlClusEligRankings = GetStrategyFlClusEligRankingUtil.getStrategyFlClusEligRankingsPartial(false)
                .orElse(null);
        Set<StrategyFlClusPrgmEligRanking> strategyFlClusPrgmEligRankings = GetEligFlClusProgUtil.getStrategyFlClusPrgmEligRankingsPartial()
                .orElse(null);
        progClusterEligibilityCcService.updateProgClusterEligibilityMetricsAtStyleCc(strategyFlClusPrgmEligRankings, strategyFlClusEligRankings, fineline.getStyles(), planStrategyId,fineline.getFinelineNbr(), 123L,updatedField);
        //Assert
        verify(eligCcClusProgRepository, times(1)).findEligCcClusProgByEligCcClusProgId_EligStyleClusProgId_StrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_PlanClusterStrategyId_PlanStrategyIdAndEligCcClusProgId_EligStyleClusProgId_StrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_finelineNbrAndEligCcClusProgId_EligStyleClusProgId_styleNbrAndEligCcClusProgId_ccIdAndEligCcClusProgId_EligStyleClusProgId_StrategyFlClusPrgmEligRankingId_ProgramId(any(PlanStrategyId.class), anyInt(), anyString(), anyString(), anyLong());
    }

    @Test
    void testUpdateCcMetricsForFLAndStyleAndCCEntriesInProgPartial() {
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
        weatherCluster.setIsEligible(true);
        weatherClusterList.add(weatherCluster);
        strategy.setWeatherClusters(weatherClusterList);

        CustomerChoice customerChoice = new CustomerChoice();
        customerChoice.setCcId("51_2_23_001_001");
        customerChoice.setStrategy(strategy);
        customerChoice.setUpdatedFields(updatedFields);
        style.setCustomerChoices(Collections.singletonList(customerChoice));
        fineline.setStyles(Collections.singletonList(style));

        Set<EligCcClusProg> eligCcClusProgs = new HashSet<>();

        PlanStrategyId planStrategyId = GetStrategyFlClusEligRankingUtil.getPlanStrategyId();
        doReturn(Optional.of(eligCcClusProgs))
                .when(eligCcClusProgRepository).findEligCcClusProgByEligCcClusProgId_EligStyleClusProgId_StrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_PlanClusterStrategyId_PlanStrategyIdAndEligCcClusProgId_EligStyleClusProgId_StrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_finelineNbrAndEligCcClusProgId_EligStyleClusProgId_styleNbrAndEligCcClusProgId_ccIdAndEligCcClusProgId_EligStyleClusProgId_StrategyFlClusPrgmEligRankingId_ProgramId(any(PlanStrategyId.class), anyInt(), anyString(), anyString(), anyLong());

        Set<String> updatedField = new HashSet<>();
        //Act
        Set<StrategyFlClusEligRanking> strategyFlClusEligRankings = GetStrategyFlClusEligRankingUtil.getStrategyFlClusEligRankingsPartial(false)
                .orElse(null);
        Set<StrategyFlClusPrgmEligRanking> strategyFlClusPrgmEligRankings = new HashSet<>();
        progClusterEligibilityCcService.updateProgClusterEligibilityMetricsAtStyleCc(strategyFlClusPrgmEligRankings, strategyFlClusEligRankings, fineline.getStyles(), planStrategyId,fineline.getFinelineNbr(), 123L,updatedField);
        //Assert
        verify(eligCcClusProgRepository, times(1)).findEligCcClusProgByEligCcClusProgId_EligStyleClusProgId_StrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_PlanClusterStrategyId_PlanStrategyIdAndEligCcClusProgId_EligStyleClusProgId_StrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_finelineNbrAndEligCcClusProgId_EligStyleClusProgId_styleNbrAndEligCcClusProgId_ccIdAndEligCcClusProgId_EligStyleClusProgId_StrategyFlClusPrgmEligRankingId_ProgramId(any(PlanStrategyId.class), anyInt(), anyString(), anyString(), anyLong());
        verify(progClusterEligibilityCcService, times(1)).checkIfFlStyleCcProgExists(anySet(),anySet(), anyLong(), anyString(), anyString());
        verify(progClusterEligibilityCcService, times(8)).createFlStyleProgForEachClus(anySet(),any(StrategyFlClusEligRanking.class), anyLong(), anyString(), anyString());
        verify(progClusterEligibilityCcService, times(8)).fetchProgStyleCcs(anySet(), anyString(), anyString());
        verify(progClusterEligibilityCcService, times(8)).fetchProgCcs(anySet(), anyString());
    }

    @Test
    void testUpdateCcMetricsForFLAndStyleAndCCEntriesForClusNPartial() {
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
        type.setAnalyticsClusterId(1);
        type.setAnalyticsClusterDesc("cluster 1");
        weatherCluster.setType(type);
        weatherCluster.setIsEligible(true);
        weatherClusterList.add(weatherCluster);
        strategy.setWeatherClusters(weatherClusterList);

        CustomerChoice customerChoice = new CustomerChoice();
        customerChoice.setCcId("51_2_23_001_001");
        customerChoice.setStrategy(strategy);
        customerChoice.setUpdatedFields(updatedFields);
        style.setCustomerChoices(Collections.singletonList(customerChoice));
        fineline.setStyles(Collections.singletonList(style));

        Set<EligCcClusProg> eligCcClusProgs = new HashSet<>();

        PlanStrategyId planStrategyId = GetStrategyFlClusEligRankingUtil.getPlanStrategyId();
        doReturn(Optional.of(eligCcClusProgs))
                .when(eligCcClusProgRepository).findEligCcClusProgByEligCcClusProgId_EligStyleClusProgId_StrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_PlanClusterStrategyId_PlanStrategyIdAndEligCcClusProgId_EligStyleClusProgId_StrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_finelineNbrAndEligCcClusProgId_EligStyleClusProgId_styleNbrAndEligCcClusProgId_ccIdAndEligCcClusProgId_EligStyleClusProgId_StrategyFlClusPrgmEligRankingId_ProgramId(any(PlanStrategyId.class), anyInt(), anyString(), anyString(), anyLong());

        Set<StrategyFlClusEligRanking> strategyFlClusEligRankings = GetStrategyFlClusEligRankingUtil.getStrategyFlClusEligRankingsPartial(false)
                .orElse(null);
        Set<StrategyFlClusPrgmEligRanking> strategyFlClusPrgmEligRankings = new HashSet<>();

        Set<String> updatedField = new HashSet<>();
        //Act
        progClusterEligibilityCcService.updateProgClusterEligibilityMetricsAtStyleCc(strategyFlClusPrgmEligRankings, strategyFlClusEligRankings, fineline.getStyles(), planStrategyId,fineline.getFinelineNbr(), 123L,updatedField);
        //Assert
        verify(eligCcClusProgRepository, times(1)).findEligCcClusProgByEligCcClusProgId_EligStyleClusProgId_StrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_PlanClusterStrategyId_PlanStrategyIdAndEligCcClusProgId_EligStyleClusProgId_StrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_finelineNbrAndEligCcClusProgId_EligStyleClusProgId_styleNbrAndEligCcClusProgId_ccIdAndEligCcClusProgId_EligStyleClusProgId_StrategyFlClusPrgmEligRankingId_ProgramId(any(PlanStrategyId.class), anyInt(), anyString(), anyString(), anyLong());
        verify(progClusterEligibilityCcService, times(1)).checkIfFlStyleCcProgExists(anySet(),anySet(), anyLong(), anyString(), anyString());
        verify(progClusterEligibilityCcService, times(8)).createFlStyleProgForEachClus(anySet(),any(StrategyFlClusEligRanking.class), anyLong(), anyString(), anyString());
        verify(progClusterEligibilityCcService, times(8)).fetchProgStyleCcs(anySet(), anyString(), anyString());
        verify(progClusterEligibilityCcService, times(8)).fetchProgCcs(anySet(), anyString());
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

        PlanStrategyId planStrategyId = GetStrategyFlClusEligRankingUtil.getPlanStrategyId();

        doReturn(GetEligFlClusProgUtil.getEligCcClusProg())
                .when(eligCcClusProgRepository).findEligCcClusProgByEligCcClusProgId_EligStyleClusProgId_StrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_PlanClusterStrategyId_PlanStrategyIdAndEligCcClusProgId_EligStyleClusProgId_StrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_finelineNbrAndEligCcClusProgId_EligStyleClusProgId_styleNbrAndEligCcClusProgId_ccIdAndEligCcClusProgId_EligStyleClusProgId_StrategyFlClusPrgmEligRankingId_ProgramId(any(PlanStrategyId.class), anyInt(), anyString(), anyString(), anyLong());
        Set<String> updatedField = new HashSet<>();

        //Act
        Set<StrategyFlClusEligRanking> strategyFlClusEligRankings = GetStrategyFlClusEligRankingUtil.getStrategyFlClusEligRankings(false)
                .orElse(null);
        Set<StrategyFlClusPrgmEligRanking> strategyFlClusPrgmEligRankings = GetEligFlClusProgUtil.getStrategyFlClusPrgmEligRankings()
                .orElse(null);
        progClusterEligibilityCcService.updateProgClusterEligibilityMetricsAtStyleCc(strategyFlClusPrgmEligRankings, strategyFlClusEligRankings, fineline.getStyles(), planStrategyId,fineline.getFinelineNbr(), 123L,updatedField);
        //Assert
        verify(eligCcClusProgRepository, times(1)).findEligCcClusProgByEligCcClusProgId_EligStyleClusProgId_StrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_PlanClusterStrategyId_PlanStrategyIdAndEligCcClusProgId_EligStyleClusProgId_StrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_finelineNbrAndEligCcClusProgId_EligStyleClusProgId_styleNbrAndEligCcClusProgId_ccIdAndEligCcClusProgId_EligStyleClusProgId_StrategyFlClusPrgmEligRankingId_ProgramId(any(PlanStrategyId.class), anyInt(), anyString(), anyString(), anyLong());
        verify(progClusterEligibilityCcService, times(1)).updateProgCcMetrics(anySet(), anyMap(), anySet(), any(CustomerChoice.class), any(PlanStrategyId.class));
        verify(progClusterEligibilityCcService, times(1)).setStyleCcMetricsForAll(anyMap(),anySet(),any(WeatherCluster.class));
        verify(progClusterEligibilityCcService, times(1)).setCcInStoreDateForAll(anySet(),any(WeatherCluster.class));
        assertEquals(true, updatedField.contains("inStoreDate"));

    }

    @Test
    void testUpdateCcMetricsMarkDownSetCcClusterN() {
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
        type.setAnalyticsClusterId(1);
        type.setAnalyticsClusterDesc("cluster 1");
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

        PlanStrategyId planStrategyId = GetStrategyFlClusEligRankingUtil.getPlanStrategyId();

        doReturn(GetEligFlClusProgUtil.getEligCcClusProg())
                .when(eligCcClusProgRepository).findEligCcClusProgByEligCcClusProgId_EligStyleClusProgId_StrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_PlanClusterStrategyId_PlanStrategyIdAndEligCcClusProgId_EligStyleClusProgId_StrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_finelineNbrAndEligCcClusProgId_EligStyleClusProgId_styleNbrAndEligCcClusProgId_ccIdAndEligCcClusProgId_EligStyleClusProgId_StrategyFlClusPrgmEligRankingId_ProgramId(any(PlanStrategyId.class), anyInt(), anyString(), anyString(), anyLong());

        Set<String> updatedField = new HashSet<>();
        //Act
        Set<StrategyFlClusEligRanking> strategyFlClusEligRankings = GetStrategyFlClusEligRankingUtil.getStrategyFlClusEligRankings(false)
                .orElse(null);
        Set<StrategyFlClusPrgmEligRanking> strategyFlClusPrgmEligRankings = GetEligFlClusProgUtil.getStrategyFlClusPrgmEligRankings()
                .orElse(null);
        progClusterEligibilityCcService.updateProgClusterEligibilityMetricsAtStyleCc(strategyFlClusPrgmEligRankings, strategyFlClusEligRankings, fineline.getStyles(), planStrategyId,fineline.getFinelineNbr(), 123L,updatedField);
        //Assert
        verify(eligCcClusProgRepository, times(1)).findEligCcClusProgByEligCcClusProgId_EligStyleClusProgId_StrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_PlanClusterStrategyId_PlanStrategyIdAndEligCcClusProgId_EligStyleClusProgId_StrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_finelineNbrAndEligCcClusProgId_EligStyleClusProgId_styleNbrAndEligCcClusProgId_ccIdAndEligCcClusProgId_EligStyleClusProgId_StrategyFlClusPrgmEligRankingId_ProgramId(any(PlanStrategyId.class), anyInt(), anyString(), anyString(), anyLong());
        verify(progClusterEligibilityCcService, times(1)).updateProgCcMetrics(anySet(), anyMap(), anySet(), any(CustomerChoice.class), any(PlanStrategyId.class));
        verify(progClusterEligibilityCcService, times(1)).setStyleCcMetricsForClusterN(anyMap(),anySet(),any(WeatherCluster.class), anyInt(), any(PlanStrategyId.class));
        verify(progClusterEligibilityCcService, times(1)).setCcMarkDownDateForClusterN(anySet(),anyInt(), any(PlanStrategyId.class), any(WeatherCluster.class));
        assertEquals(true, updatedField.contains("markDownDate"));
    }



    @Test
    void testUpdateCcMetricsInStoreSetCcClusterN() {
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
        type.setAnalyticsClusterId(1);
        type.setAnalyticsClusterDesc("cluster 1");
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

        PlanStrategyId planStrategyId = GetStrategyFlClusEligRankingUtil.getPlanStrategyId();

        doReturn(GetEligFlClusProgUtil.getEligCcClusProg())
                .when(eligCcClusProgRepository).findEligCcClusProgByEligCcClusProgId_EligStyleClusProgId_StrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_PlanClusterStrategyId_PlanStrategyIdAndEligCcClusProgId_EligStyleClusProgId_StrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_finelineNbrAndEligCcClusProgId_EligStyleClusProgId_styleNbrAndEligCcClusProgId_ccIdAndEligCcClusProgId_EligStyleClusProgId_StrategyFlClusPrgmEligRankingId_ProgramId(any(PlanStrategyId.class), anyInt(), anyString(), anyString(), anyLong());
        Set<String> updatedField = new HashSet<>();

        //Act
        Set<StrategyFlClusEligRanking> strategyFlClusEligRankings = GetStrategyFlClusEligRankingUtil.getStrategyFlClusEligRankings(false)
                .orElse(null);
        Set<StrategyFlClusPrgmEligRanking> strategyFlClusPrgmEligRankings = GetEligFlClusProgUtil.getStrategyFlClusPrgmEligRankings()
                .orElse(null);
        progClusterEligibilityCcService.updateProgClusterEligibilityMetricsAtStyleCc(strategyFlClusPrgmEligRankings, strategyFlClusEligRankings, fineline.getStyles(), planStrategyId,fineline.getFinelineNbr(), 123L,updatedField);
        //Assert
        verify(eligCcClusProgRepository, times(1)).findEligCcClusProgByEligCcClusProgId_EligStyleClusProgId_StrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_PlanClusterStrategyId_PlanStrategyIdAndEligCcClusProgId_EligStyleClusProgId_StrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_finelineNbrAndEligCcClusProgId_EligStyleClusProgId_styleNbrAndEligCcClusProgId_ccIdAndEligCcClusProgId_EligStyleClusProgId_StrategyFlClusPrgmEligRankingId_ProgramId(any(PlanStrategyId.class), anyInt(), anyString(), anyString(), anyLong());
        verify(progClusterEligibilityCcService, times(1)).updateProgCcMetrics(anySet(), anyMap(), anySet(), any(CustomerChoice.class), any(PlanStrategyId.class));
        verify(progClusterEligibilityCcService, times(1)).setStyleCcMetricsForClusterN(anyMap(),anySet(),any(WeatherCluster.class), anyInt(), any(PlanStrategyId.class));
        verify(progClusterEligibilityCcService, times(1)).setCcInStoreDateForClusterN(anySet(),anyInt(), any(PlanStrategyId.class), any(WeatherCluster.class));
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

        PlanStrategyId planStrategyId = GetStrategyFlClusEligRankingUtil.getPlanStrategyId();

        doReturn(GetEligFlClusProgUtil.getEligCcClusProg())
                .when(eligCcClusProgRepository).findEligCcClusProgByEligCcClusProgId_EligStyleClusProgId_StrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_PlanClusterStrategyId_PlanStrategyIdAndEligCcClusProgId_EligStyleClusProgId_StrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_finelineNbrAndEligCcClusProgId_EligStyleClusProgId_styleNbrAndEligCcClusProgId_ccIdAndEligCcClusProgId_EligStyleClusProgId_StrategyFlClusPrgmEligRankingId_ProgramId(any(PlanStrategyId.class), anyInt(), anyString(), anyString(), anyLong());
        Set<String> updatedField = new HashSet<>();
        //Act
        Set<StrategyFlClusEligRanking> strategyFlClusEligRankings = GetStrategyFlClusEligRankingUtil.getStrategyFlClusEligRankings(false)
                .orElse(null);
        Set<StrategyFlClusPrgmEligRanking> strategyFlClusPrgmEligRankings = GetEligFlClusProgUtil.getStrategyFlClusPrgmEligRankings()
                .orElse(null);
        progClusterEligibilityCcService.updateProgClusterEligibilityMetricsAtStyleCc(strategyFlClusPrgmEligRankings, strategyFlClusEligRankings, fineline.getStyles(), planStrategyId,fineline.getFinelineNbr(), 123L,updatedField);
        //Assert
        verify(eligCcClusProgRepository, times(1)).findEligCcClusProgByEligCcClusProgId_EligStyleClusProgId_StrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_PlanClusterStrategyId_PlanStrategyIdAndEligCcClusProgId_EligStyleClusProgId_StrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_finelineNbrAndEligCcClusProgId_EligStyleClusProgId_styleNbrAndEligCcClusProgId_ccIdAndEligCcClusProgId_EligStyleClusProgId_StrategyFlClusPrgmEligRankingId_ProgramId(any(PlanStrategyId.class), anyInt(), anyString(), anyString(), anyLong());
        verify(progClusterEligibilityCcService, times(1)).updateProgCcMetrics(anySet(), anyMap(), anySet(), any(CustomerChoice.class), any(PlanStrategyId.class));
        verify(progClusterEligibilityCcService, times(1)).setStyleCcMetricsForAll(anyMap(),anySet(),any(WeatherCluster.class));
        verify(progClusterEligibilityCcService, times(1)).setCcMarkDownDateForAll(anySet(),any(WeatherCluster.class));
        assertEquals(true, updatedField.contains("markDownDate"));
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

        PlanStrategyId planStrategyId = GetStrategyFlClusEligRankingUtil.getPlanStrategyId();

        doReturn(GetEligFlClusProgUtil.getEligCcClusProg())
                .when(eligCcClusProgRepository).findEligCcClusProgByEligCcClusProgId_EligStyleClusProgId_StrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_PlanClusterStrategyId_PlanStrategyIdAndEligCcClusProgId_EligStyleClusProgId_StrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_finelineNbrAndEligCcClusProgId_EligStyleClusProgId_styleNbrAndEligCcClusProgId_ccIdAndEligCcClusProgId_EligStyleClusProgId_StrategyFlClusPrgmEligRankingId_ProgramId(any(PlanStrategyId.class), anyInt(), anyString(), anyString(), anyLong());
        Set<String> updatedField = new HashSet<>();

        //Act
        Set<StrategyFlClusEligRanking> strategyFlClusEligRankings = GetStrategyFlClusEligRankingUtil.getStrategyFlClusEligRankings(false)
                .orElse(null);
        Set<StrategyFlClusPrgmEligRanking> strategyFlClusPrgmEligRankings = GetEligFlClusProgUtil.getStrategyFlClusPrgmEligRankings()
                .orElse(null);
        progClusterEligibilityCcService.updateProgClusterEligibilityMetricsAtStyleCc(strategyFlClusPrgmEligRankings, strategyFlClusEligRankings, fineline.getStyles(), planStrategyId,fineline.getFinelineNbr(), 123L,updatedField);
        //Assert
        verify(eligCcClusProgRepository, times(1)).findEligCcClusProgByEligCcClusProgId_EligStyleClusProgId_StrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_PlanClusterStrategyId_PlanStrategyIdAndEligCcClusProgId_EligStyleClusProgId_StrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_finelineNbrAndEligCcClusProgId_EligStyleClusProgId_styleNbrAndEligCcClusProgId_ccIdAndEligCcClusProgId_EligStyleClusProgId_StrategyFlClusPrgmEligRankingId_ProgramId(any(PlanStrategyId.class), anyInt(), anyString(), anyString(), anyLong());
        verify(progClusterEligibilityCcService, times(1)).updateProgCcMetrics(anySet(), anyMap(), anySet(), any(CustomerChoice.class), any(PlanStrategyId.class));
        verify(progClusterEligibilityCcService, times(1)).setStyleCcMetricsForAll(anyMap(),anySet(),any(WeatherCluster.class));
        verify(progClusterEligibilityCcService, times(1)).setCcRankingForAll(anySet(),any(WeatherCluster.class));
    }


    @Test
    void testUpdateCcMetricsRankingSetCcClusterN() {
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

        doReturn(GetEligFlClusProgUtil.getEligCcClusProg())
                .when(eligCcClusProgRepository).findEligCcClusProgByEligCcClusProgId_EligStyleClusProgId_StrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_PlanClusterStrategyId_PlanStrategyIdAndEligCcClusProgId_EligStyleClusProgId_StrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_finelineNbrAndEligCcClusProgId_EligStyleClusProgId_styleNbrAndEligCcClusProgId_ccIdAndEligCcClusProgId_EligStyleClusProgId_StrategyFlClusPrgmEligRankingId_ProgramId(any(PlanStrategyId.class), anyInt(), anyString(), anyString(), anyLong());
        Set<String> updatedField = new HashSet<>();

        //Act
        Set<StrategyFlClusEligRanking> strategyFlClusEligRankings = GetStrategyFlClusEligRankingUtil.getStrategyFlClusEligRankings(false)
                .orElse(null);
        Set<StrategyFlClusPrgmEligRanking> strategyFlClusPrgmEligRankings = GetEligFlClusProgUtil.getStrategyFlClusPrgmEligRankings()
                .orElse(null);
        progClusterEligibilityCcService.updateProgClusterEligibilityMetricsAtStyleCc(strategyFlClusPrgmEligRankings, strategyFlClusEligRankings, fineline.getStyles(), planStrategyId,fineline.getFinelineNbr(), 123L,updatedField);
        //Assert
        verify(eligCcClusProgRepository, times(1)).findEligCcClusProgByEligCcClusProgId_EligStyleClusProgId_StrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_PlanClusterStrategyId_PlanStrategyIdAndEligCcClusProgId_EligStyleClusProgId_StrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_finelineNbrAndEligCcClusProgId_EligStyleClusProgId_styleNbrAndEligCcClusProgId_ccIdAndEligCcClusProgId_EligStyleClusProgId_StrategyFlClusPrgmEligRankingId_ProgramId(any(PlanStrategyId.class), anyInt(), anyString(), anyString(), anyLong());
        verify(progClusterEligibilityCcService, times(1)).updateProgCcMetrics(anySet(), anyMap(), anySet(), any(CustomerChoice.class), any(PlanStrategyId.class));
        verify(progClusterEligibilityCcService, times(1)).setStyleCcMetricsForClusterN(anyMap(),anySet(),any(WeatherCluster.class), anyInt(), any(PlanStrategyId.class));
        verify(progClusterEligibilityCcService, times(1)).setCcRankingForClusterN(anySet(), anyInt(), any(PlanStrategyId.class), any(WeatherCluster.class));
    }
}
