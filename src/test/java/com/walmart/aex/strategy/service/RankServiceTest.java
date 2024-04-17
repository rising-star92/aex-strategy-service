package com.walmart.aex.strategy.service;

import com.walmart.aex.strategy.dto.Rank.FlAndCcRankData;
import com.walmart.aex.strategy.dto.Rank.RankRequest;
import com.walmart.aex.strategy.dto.Rank.RankResponse;
import com.walmart.aex.strategy.repository.StrategyCcRepository;
import com.walmart.aex.strategy.repository.StrategyFlClusEligRankingRepository;
import com.walmart.aex.strategy.repository.StrategyFlClusPrgmEligRankingRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class RankServiceTest {

    @InjectMocks
    private RankService rankService;

    @Mock
    private StrategyCcRepository strategyCcRepository;

    @Mock
    private StrategyFlClusEligRankingRepository strategyFlClusEligRankingRepository;

    @Mock
    private StrategyFlClusPrgmEligRankingRepository strategyFlClusPrgmEligRankingRepository;

    @Test
    public void fetchFinelineRankTest()
    {
        RankRequest request = new RankRequest();
        List<FlAndCcRankData> flAndCcRankDataList = new ArrayList<>();
        getFlAndCcData(flAndCcRankDataList);
        Mockito.when(strategyFlClusEligRankingRepository.getFinelineRankData(any(),any(),any(),any(),any(),any(),any(),any(),any(),any(),any(),any(),any(),any(),any())).thenReturn(flAndCcRankDataList);
        Mockito.when(strategyFlClusPrgmEligRankingRepository.getFinelinePrgRank(any(),any(),any(),any(),any(),any(),any(),any(),any(),any(),any(),any(),any(),any(),any())).thenReturn(flAndCcRankDataList);
        RankResponse response = rankService.fetchFinelineRank(request);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(2,response.getFlAndCcRankData().get(0).getFlRank());
    }

    @Test
    public void fetchFinelineRankTraitRankTest()
    {
        RankRequest request = new RankRequest();
        List<FlAndCcRankData> flAndCcRankDataList = new ArrayList<>();
        getFlAndCcData(flAndCcRankDataList);
        List<FlAndCcRankData> flAndCcRankTraitDataList = new ArrayList<>();
        getFlAndCcData(flAndCcRankTraitDataList);
        flAndCcRankTraitDataList.get(0).setFlRank(12);
        Mockito.when(strategyFlClusEligRankingRepository.getFinelineRankData(any(),any(),any(),any(),any(),any(),any(),any(),any(),any(),any(),any(),any(),any(),any())).thenReturn(flAndCcRankDataList);
        Mockito.when(strategyFlClusPrgmEligRankingRepository.getFinelinePrgRank(any(),any(),any(),any(),any(),any(),any(),any(),any(),any(),any(),any(),any(),any(),any())).thenReturn(flAndCcRankTraitDataList);
        RankResponse response = rankService.fetchFinelineRank(request);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(12,response.getFlAndCcRankData().get(0).getFlRank());
    }

    @Test
    public void fetchFinelineRankTraitRankIsNullTest()
    {
        RankRequest request = new RankRequest();
        List<FlAndCcRankData> flAndCcRankDataList = new ArrayList<>();
        getFlAndCcData(flAndCcRankDataList);
        Mockito.when(strategyFlClusEligRankingRepository.getFinelineRankData(any(),any(),any(),any(),any(),any(),any(),any(),any(),any(),any(),any(),any(),any(),any())).thenReturn(flAndCcRankDataList);
        Mockito.when(strategyFlClusPrgmEligRankingRepository.getFinelinePrgRank(any(),any(),any(),any(),any(),any(),any(),any(),any(),any(),any(),any(),any(),any(),any())).thenReturn(null);
        RankResponse response = rankService.fetchFinelineRank(request);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(2,response.getFlAndCcRankData().get(0).getFlRank());
    }

    @Test
    public void fetchFinelineRankNullDataTest()
    {
        RankRequest request = new RankRequest();
        RankResponse response = rankService.fetchFinelineRank(request);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(0,response.getFlAndCcRankData().size());
    }



    @Test
    public void fetchCcRankTest()
    {
        RankRequest request = new RankRequest();
        List<FlAndCcRankData> flAndCcRankDataList = new ArrayList<>();
        getFlAndCcData(flAndCcRankDataList);
        Mockito.when(strategyCcRepository.getCcRankData(any(),any(),any(),any(),any(),any(),any(),any(),any(),any(),any(),any(),any(),any(),any(),any(),any())).thenReturn(flAndCcRankDataList);
        Mockito.when(strategyCcRepository.getCcRankPrgData(any(),any(),any(),any(),any(),any(),any(),any(),any(),any(),any(),any(),any(),any(),any(),any(),any())).thenReturn(flAndCcRankDataList);
        RankResponse response = rankService.fetchCcRank(request);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(2,response.getFlAndCcRankData().get(0).getCcRank());
    }


    @Test
    public void fetchCcTraitRankTest()
    {
        RankRequest request = new RankRequest();
        List<FlAndCcRankData> flAndCcRankDataList = new ArrayList<>();
        getFlAndCcData(flAndCcRankDataList);
        List<FlAndCcRankData> flAndCcRankTraitDataList = new ArrayList<>();
        getFlAndCcData(flAndCcRankTraitDataList);
        flAndCcRankTraitDataList.get(0).setCcRank(12);
        Mockito.when(strategyCcRepository.getCcRankData(any(),any(),any(),any(),any(),any(),any(),any(),any(),any(),any(),any(),any(),any(),any(),any(),any())).thenReturn(flAndCcRankDataList);
        Mockito.when(strategyCcRepository.getCcRankPrgData(any(),any(),any(),any(),any(),any(),any(),any(),any(),any(),any(),any(),any(),any(),any(),any(),any())).thenReturn(flAndCcRankTraitDataList);
        RankResponse response = rankService.fetchCcRank(request);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(12,response.getFlAndCcRankData().get(0).getCcRank());
    }

    @Test
    public void fetchCcTraitRankIsNullTest()
    {
        RankRequest request = new RankRequest();
        List<FlAndCcRankData> flAndCcRankDataList = new ArrayList<>();
        getFlAndCcData(flAndCcRankDataList);
        Mockito.when(strategyCcRepository.getCcRankData(any(),any(),any(),any(),any(),any(),any(),any(),any(),any(),any(),any(),any(),any(),any(),any(),any())).thenReturn(flAndCcRankDataList);
        Mockito.when(strategyCcRepository.getCcRankPrgData(any(),any(),any(),any(),any(),any(),any(),any(),any(),any(),any(),any(),any(),any(),any(),any(),any())).thenReturn(null);
        RankResponse response = rankService.fetchCcRank(request);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(2,response.getFlAndCcRankData().get(0).getCcRank());
    }

    @Test
    public void fetchCcRankNullDataTest()
    {
        RankRequest request = new RankRequest();
        RankResponse response = rankService.fetchCcRank(request);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(0,response.getFlAndCcRankData().size());
    }

    private void getFlAndCcData(List<FlAndCcRankData> flAndCcRankDataList)
    {
        FlAndCcRankData flAndCcRankData = new FlAndCcRankData();
        flAndCcRankData.setPlanId(12l);
        flAndCcRankData.setLvl0Nbr(10);
        flAndCcRankData.setLvl1Nbr(11);
        flAndCcRankData.setLvl2Nbr(22);
        flAndCcRankData.setLvl3Nbr(33);
        flAndCcRankData.setLvl4Nbr(44);
        flAndCcRankData.setFinelineNbr(1234);
        flAndCcRankData.setStyleNbr("1-styleNbr");
        flAndCcRankData.setCustomerChoice("1234-Black");
        flAndCcRankData.setCcRank(2);
        flAndCcRankData.setFlRank(2);
        flAndCcRankDataList.add(flAndCcRankData);

    }
}
