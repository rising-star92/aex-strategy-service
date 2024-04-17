package com.walmart.aex.strategy.service;

import com.walmart.aex.strategy.dto.*;
import com.walmart.aex.strategy.dto.request.Fineline;
import com.walmart.aex.strategy.dto.request.Lvl3;
import com.walmart.aex.strategy.dto.request.Lvl4;
import com.walmart.aex.strategy.repository.*;
import com.walmart.aex.strategy.util.SetAllClusterOffshoreUtil;
import com.walmart.aex.strategy.util.WeatherClusterStrategyDTOToWeatherClusterStrategyMapperUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.Tuple;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class ClusterEligibilityServiceTest {

    @InjectMocks
    ClusterEligibilityService clusterEligibilityService;

    @Mock
    StrategyFinelineRepository strategyFinelineRepository;

    @Mock
    StrategyCcRepository strategyCcRepository;

    @Mock
    private StrategySubCatgFixtureRepository strategySubCatgFixtureRepository;
    @Mock
    PlanStrategyClusterEligRankingMapper planStrategyClusterEligRankingMapper;

    @Mock
    StrategyClusterStoreRepository strategyClusterStoreRepository;

    @Mock
    AnalyticsClusterStoreCountService analyticsClusterStoreCountService;

    @Mock
    SetAllClusterOffshoreUtil setAllClusterOffshoreUtil;

    @Mock
    StrategyGroupRepository strategyGroupRepository;

    @Mock(name = "weatherClusterStrategyDTOToWeatherClusterStrategyMapperUtil")
    WeatherClusterStrategyDTOToWeatherClusterStrategyMapperUtil weatherClusterStrategyDTOToWeatherClusterStrategyMapperUtil;


    @Test
    void testFetchProgramEligRankingByPlanWhenNoDataAvailable() {

        WeatherClusterStrategyDTO weatherClusterStrategyDTOMock = new WeatherClusterStrategyDTO() {
            @Override
            public WeatherClusterStrategy getWeatherClusterStrategy() {
                return null;
            }
        };

        List<WeatherClusterStrategyDTO> weatherClusterStrategyDTOList = new ArrayList<>();
        weatherClusterStrategyDTOList.add(weatherClusterStrategyDTOMock);
        Mockito.doReturn(weatherClusterStrategyDTOList)
                .when(strategyFinelineRepository)
                .getWeatherClusterTraitStrategy(any(Long.class),any(Long.class),any(Long.class));

        PlanStrategyResponse resp = clusterEligibilityService.fetchTraitEligRankingStrategy(1L, 1L);
        Mockito.verify(strategyFinelineRepository, times(1)).getWeatherClusterTraitStrategy(any(Long.class),any(Long.class),any(Long.class));
        assertNotNull(resp);
        assertNull(resp.getPlanId());
    }

    @Test
    void testFetchCcProgramEligRankingByPlan() {
        WeatherClusterStrategy mockWeatherClusterStrategy = new WeatherClusterStrategy(304L,1L,1L,50000,34,1056308,1056309,"TopsEcommWomens",1056381,
                "FleeceTopsEcWomens",51,"WomenPullover",null,"WomenPulloverX","style","cc","altCc","colorname",0,"all",0,0,"FYE2023WK01",
                "FYE2023WK16",null,null,null);

        WeatherClusterCcStrategyDTO weatherClusterStrategyDTOMock = new WeatherClusterCcStrategyDTO() {
            @Override
            public WeatherClusterStrategy getWeatherClusterCcStrategy() {
                return mockWeatherClusterStrategy;
            }
        };

        PlanStrategyRequest planStrategyRequest = new PlanStrategyRequest();
        planStrategyRequest.setPlanId(304L);


        List<Fineline> finelineList = new ArrayList<>();
        Fineline fineline = new Fineline();
        fineline.setFinelineNbr(51);
        finelineList.add(fineline);

        List<Lvl4> lvl4List = new ArrayList<>();
        Lvl4 lvl4 = new Lvl4();
        lvl4.setLvl4Nbr(1056381);
        lvl4.setFinelines(finelineList);
        lvl4List.add(lvl4);

        List<Lvl3> lvl3List = new ArrayList<>();
        Lvl3 lvl3 = new Lvl3();
        lvl3.setLvl3Nbr(1056309);
        lvl3.setLvl4List(lvl4List);
        lvl3List.add(lvl3);
        planStrategyRequest.setLvl3List(lvl3List);

        List<WeatherClusterCcStrategyDTO> weatherClusterStrategyDTOList = new ArrayList<>();
        weatherClusterStrategyDTOList.add(weatherClusterStrategyDTOMock);
        Mockito.doReturn(weatherClusterStrategyDTOList)
                .when(strategyCcRepository)
                .getWeatherClusterTraitCcStrategy(304L,1L,1L,51);
        when(strategyGroupRepository.getStrategyIdByStrategyGroupTypeAndPlanId(anyInt(), anyLong())).thenReturn(1l);

        Mockito.doCallRealMethod().when(planStrategyClusterEligRankingMapper).mapPlanStrategyLvl2(mockWeatherClusterStrategy, new PlanStrategyResponse(), 51 );
        PlanStrategyResponse resp = clusterEligibilityService.fetchCcTraitEligRankingStrategy(1L, planStrategyRequest);
        Mockito.verify(strategyCcRepository, times(1)).getWeatherClusterTraitCcStrategy(304L,1L,1L,51);
        assertNotNull(resp);
        assertEquals(304, resp.getPlanId());
    }
    @Test
    void testIsRfaDefaultMinMaxCapValid() {
        PlanStrategyRequest planStrategyRequest = new PlanStrategyRequest();
        planStrategyRequest.setPlanId(236l);
        List<Fineline> finelineList = new ArrayList<>();
        Fineline fineline = new Fineline();
        fineline.setFinelineNbr(51);
        finelineList.add(fineline);

        List<Lvl4> lvl4List = new ArrayList<>();
        Lvl4 lvl4 = new Lvl4();
        lvl4.setLvl4Nbr(31526);
        lvl4.setFinelines(finelineList);
        lvl4List.add(lvl4);

        List<Lvl3> lvl3List = new ArrayList<>();
        Lvl3 lvl3 = new Lvl3();
        lvl3.setLvl3Nbr(12238);
        lvl3.setLvl4List(lvl4List);
        lvl3List.add(lvl3);
        planStrategyRequest.setLvl3List(lvl3List);

        Integer[] lvl3ListMock = new Integer[]{12238};
        Integer[] lvl4ListMock = new Integer[]{31526};
        Tuple mockTuple = mock(Tuple.class);
        when(mockTuple.get(Mockito.eq(0))).thenReturn(1);
        when(mockTuple.get(Mockito.eq(1))).thenReturn(1);
        Mockito.doReturn(mockTuple)
                .when(strategySubCatgFixtureRepository)
                .isSubCategoryFixtureMinMaxInvalid(236l,lvl3ListMock,lvl4ListMock,5l);

        Map<String, Boolean> resp = clusterEligibilityService.isRfaDefaultMinMaxCapValid(planStrategyRequest, 5l);

        Mockito.verify(strategySubCatgFixtureRepository, times(1)).isSubCategoryFixtureMinMaxInvalid(236l,lvl3ListMock,lvl4ListMock,5l);
        assertNotNull(resp);
        assertEquals(resp.get("isCCRulesValid"), true);
        assertEquals(resp.get("isFinelineRulesValid"), true);
    }
}
