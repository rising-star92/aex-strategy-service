package com.walmart.aex.strategy.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.walmart.aex.strategy.dto.PlanStrategyListenerResponse;
import com.walmart.aex.strategy.dto.mapper.PlanStrategyMapperDTO;
import com.walmart.aex.strategy.dto.request.*;
import com.walmart.aex.strategy.entity.*;
import com.walmart.aex.strategy.enums.EventType;
import com.walmart.aex.strategy.enums.StratGroupType;
import com.walmart.aex.strategy.properties.AppProperties;
import com.walmart.aex.strategy.repository.*;
import com.walmart.aex.strategy.util.GetSizeSPClusterObjUtil;
import com.walmart.aex.strategy.util.GetStrategyFlClusEligRankingUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.powermock.api.mockito.PowerMockito;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlanStrategyServiceTest {

    @InjectMocks
    private PlanStrategyService planStrategyService;

    @InjectMocks
    private ObjectMapper objectMapper;

    @Mock
    private ObjectMapper mockObjectMapper;

    @Mock
    private PlanStrategyRepository planStrategyRepository;

    @Mock
    private PlanStrategyClusterRepository planStrategyClusterRepository;

    @Mock
    private StrategyFlClusPrgmEligRankingRepository strategyFlClusPrgmEligRankingRepository;

    @Mock
    private PlanStrategyClusterEligMapper planStrategyClusterEligMapper;

    @Mock
    private PlanStrategyMapper planStrategyMapper;

    @Mock
    private PlanStrategyUpdateMapper planStrategyUpdateMapper;

    @Mock
    StrategySPClusterMapper strategySPClusterMapper;

    @Mock
    private StrategyFlClustMetricRepository strategyFlClustMetricRepository;

    @Mock
    private StrategyGroupRepository strategyGroupRepository;

    @Mock
    private PlanStrategyFixtureMapper planStrategyFixtureMapper;

    @Mock
    PlanClusterStrategyRepository planClusterStrategyRepository;

    @Mock
    StrategyMerchCatgFixtureRepository strategyMerchCatgFixtureRepository;

    @Mock
    StrategySubCatgFixtureRepository    strategySubCatgFixtureRepository;

    @Mock
    PresentationUnitsService presentationUnitsService;


    @Mock
    private AppProperties appProperties;

    @BeforeEach
    public void setup() {
        objectMapper = new ObjectMapper();
        JavaTimeModule module = new JavaTimeModule();
        LocalDateTimeDeserializer localDateTimeDeserializer = new LocalDateTimeDeserializer(
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"));
        module.addDeserializer(LocalDateTime.class, localDateTimeDeserializer);
        objectMapper.registerModule(module);
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        appProperties = PowerMockito.mock(AppProperties.class);

        planStrategyService = new PlanStrategyService(planStrategyRepository, planStrategyMapper,
                planStrategyClusterEligMapper, objectMapper, planStrategyUpdateMapper, strategySPClusterMapper, strategyFlClustMetricRepository,
                strategyGroupRepository, planStrategyClusterRepository,planStrategyFixtureMapper,
                strategySubCatgFixtureRepository, strategyMerchCatgFixtureRepository, presentationUnitsService);
        ReflectionTestUtils.setField(planStrategyService, "appProperties", appProperties);

    }

    @Test
    void testAddPlanStrategyForCLPAndAnlaytics() throws JsonProcessingException {
        //Arrange

        PlanStrategyDTO request = GetStrategyFlClusEligRankingUtil.getPlanStrategyRequest();

        PlanStrategy planStrategy = new PlanStrategy();

        PlanStrategyId planStrategyId = new PlanStrategyId();
        planStrategyId.setPlanId(request.getPlanId());
        planStrategyId.setStrategyId(1l);

        PlanStrategyListenerResponse response = new PlanStrategyListenerResponse();

        String requestString = objectMapper.writeValueAsString(request);

        lenient().when(planStrategyRepository.findById(any(PlanStrategyId.class))).thenReturn(java.util.Optional.of(planStrategy));
        lenient().when(planStrategyMapper.setStrategyMerchCatg(any(PlanStrategy.class), any(List.class), any(PlanStrategyId.class), any(PlanStrategyDTO.class), any(Lvl1.class), any(Lvl2.class)))
                .thenReturn(planStrategy.getStrategyMerchCatgs());
        lenient().when(planStrategyClusterEligMapper.setPlanClusterStrategy(any(PlanStrategy.class), any(List.class), any(PlanStrategyId.class), any(PlanStrategyDTO.class), any(Lvl1.class), any(Lvl2.class)))
                .thenReturn(planStrategy.getPlanClusterStrategies());
        lenient().when(planStrategyRepository.save(any(PlanStrategy.class))).thenReturn(GetStrategyFlClusEligRankingUtil.getPlanStrategy());
        lenient().doNothing().when(strategyFlClustMetricRepository).updateAlgoClusterRanking(anyLong(), anyLong());
        //when(appProperties.getSPReleaseFlag()).thenReturn("false");

        //Act
        response = planStrategyService.addPlanStrategy(request);

        // Assert
        assertNotNull(response);
        assertEquals("Success", response.getStatus());
    }

    @Test
    void testUpdatePlanStrategyForCLPAndAnlaytics() throws JsonProcessingException {
        //Arrange

        PlanStrategyDTO request = GetStrategyFlClusEligRankingUtil.getPlanStrategyRequest();

        PlanStrategy planStrategy = new PlanStrategy();

        PlanStrategyId planStrategyId = new PlanStrategyId();
        planStrategyId.setPlanId(request.getPlanId());
        planStrategyId.setStrategyId(1l);

        request.getLvl1List().forEach(lvl1 -> {
            lvl1.getLvl2List().forEach(lvl2 -> {
                lvl2.getLvl3List().forEach(lvl3 -> {
                    lvl3.getLvl4List().forEach(lvl4 -> {
                        lvl4.getFinelines().forEach(fineline -> {
                            ProductDimensions productDimensions = new ProductDimensions();
                            productDimensions.setProductDimId(123);
                            productDimensions.setProductDimDesc("Thin");
                            fineline.setProductDimensions(productDimensions);
                        });
                    });
                });
            });
        });

        PlanStrategyListenerResponse response = new PlanStrategyListenerResponse();

        lenient().when(planStrategyRepository.findById(any(PlanStrategyId.class))).thenReturn(java.util.Optional.of(planStrategy));
        lenient().when(planStrategyMapper.setStrategyMerchCatg(any(PlanStrategy.class), any(List.class), any(PlanStrategyId.class), any(PlanStrategyDTO.class), any(Lvl1.class), any(Lvl2.class)))
                .thenReturn(planStrategy.getStrategyMerchCatgs());
        lenient().when(planStrategyClusterEligMapper.setPlanClusterStrategy(any(PlanStrategy.class), any(List.class), any(PlanStrategyId.class), any(PlanStrategyDTO.class), any(Lvl1.class), any(Lvl2.class)))
                .thenReturn(planStrategy.getPlanClusterStrategies());
        lenient().when(planStrategyRepository.save(any(PlanStrategy.class))).thenReturn(GetStrategyFlClusEligRankingUtil.getPlanStrategy());
        lenient().doNothing().when(strategyFlClustMetricRepository).updateAlgoClusterRanking(anyLong(), anyLong());

        //Act
        response = planStrategyService.updatePlanStrategy(request, EventType.UPDATE);

        // Assert
        assertNotNull(response);
        assertEquals("Success", response.getStatus());
    }

    @Test
    void testAddPlanStrategyForaddSizeStrategy() throws JsonProcessingException {
        //Arrange
        PlanStrategyDTO request = GetStrategyFlClusEligRankingUtil.getPlanStrategyRequest();
        PlanStrategy planStrategy = new PlanStrategy();
        PlanStrategyId planStrategyId = new PlanStrategyId();
        planStrategyId.setPlanId(request.getPlanId());
        planStrategyId.setStrategyId(4l);
        planStrategy.setPlanStrategyId(GetStrategyFlClusEligRankingUtil.getPlanStrategyId());
        planStrategy.setPlanClusterStrategies(GetSizeSPClusterObjUtil.getPlanClusterStrategies());
        PlanStrategyListenerResponse response = new PlanStrategyListenerResponse();
        when(strategyGroupRepository.getStrategyIdBySeasonCd(StratGroupType.SIZE_PROFILE.getStrategyGroupTypeCode(), null, null)).thenReturn(4l);
        when(planStrategyRepository.findById(any(PlanStrategyId.class))).thenReturn(java.util.Optional.of(planStrategy));
        when(strategySPClusterMapper.setPlanStrategyCluster(any(PlanStrategyMapperDTO.class)))
                .thenReturn(planStrategy.getPlanClusterStrategies());
        //Act
        planStrategyService.addSizeStrategy(request);

        // Assert
        assertNotNull(response);
    }

    @Test
    void testUpdatePlanStrategyForUpdateSizeStrategy() throws JsonProcessingException {
        //Arrange
        PlanStrategyDTO request = GetStrategyFlClusEligRankingUtil.getPlanStrategyRequest();
        PlanStrategy planStrategy = new PlanStrategy();
        PlanStrategyId planStrategyId = new PlanStrategyId();
        planStrategyId.setPlanId(request.getPlanId());
        planStrategyId.setStrategyId(4l);
        planStrategy.setPlanStrategyId(GetStrategyFlClusEligRankingUtil.getPlanStrategyId());
        planStrategy.setPlanClusterStrategies(GetSizeSPClusterObjUtil.getPlanClusterStrategies());
        PlanStrategyListenerResponse response = new PlanStrategyListenerResponse();
        when(strategyGroupRepository.getStrategyIdBySeasonCd(StratGroupType.SIZE_PROFILE.getStrategyGroupTypeCode(), null, null)).thenReturn(4l);
        when(planStrategyRepository.findById(any(PlanStrategyId.class))).thenReturn(java.util.Optional.of(planStrategy));
        //Act
        planStrategyService.updateSizeStrategy(request);

        // Assert
        assertNotNull(response);
    }
}
