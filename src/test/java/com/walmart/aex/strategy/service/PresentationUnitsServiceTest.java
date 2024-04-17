package com.walmart.aex.strategy.service;

import com.walmart.aex.strategy.dto.FixtureAllocationStrategy;
import com.walmart.aex.strategy.dto.request.Lvl1;
import com.walmart.aex.strategy.dto.request.Lvl2;
import com.walmart.aex.strategy.dto.request.PlanStrategyDTO;
import com.walmart.aex.strategy.entity.PlanStrategy;
import com.walmart.aex.strategy.entity.PlanStrategyId;
import com.walmart.aex.strategy.entity.StrategyFinelineFixture;
import com.walmart.aex.strategy.entity.StrategyPUMinMax;
import com.walmart.aex.strategy.enums.StratGroupType;
import com.walmart.aex.strategy.producer.StrategyProducer;
import com.walmart.aex.strategy.repository.*;
import com.walmart.aex.strategy.util.GetPresentationUnitsUtil;
import com.walmart.aex.strategy.util.KafkaUtil;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.powermock.core.classloader.annotations.PrepareForTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
@PrepareForTest({KafkaUtil.class})
class PresentationUnitsServiceTest {

    @Mock
    private StrategyGroupRepository strategyGroupRepository;

    @Mock
    private PresentationUnitStrategyRepository presentationUnitStrategyRepository;

    @Mock
    private PresentationUnitsStrategyMapper presentationUnitsStrategyMapper;

    @Mock
    private PresentationUnitMercCatgService presentationUnitMercCatgService;

    @Mock
    private FixtureAllocationStrategyRepository fixtureAllocationStrategyRepository;

    @Mock
    private PlanStrategyRepository planStrategyRepository;

    @Mock
    private PlanStrategyPresentationUnitsMapper planStrategyPresentationUnitsMapper;

    @Mock
    private StrategyFinelineFixtureRepository strategyFinelineFixtureRepository;

    @Mock
    private PresentationUnitsMinMaxMappingService presentationUnitsMinMaxMappingService;

    @Mock
    private StrategyProducer producer;

    @Mock
    private ExecutorService executorService;

    @InjectMocks
    PresentationUnitsService presentationUnitsService;

    @BeforeEach
    public void SetUp(){
        presentationUnitsService = new PresentationUnitsService(strategyGroupRepository, presentationUnitStrategyRepository,
                presentationUnitsStrategyMapper, presentationUnitMercCatgService, fixtureAllocationStrategyRepository,
                planStrategyRepository, planStrategyPresentationUnitsMapper, strategyFinelineFixtureRepository,
                presentationUnitsMinMaxMappingService,
                executorService);
    }

    @Test
    void testupdatePresentationUnitsFromRfaCalledOnce(){
        //Arrange

        PlanStrategyDTO strategyDTORequest = GetPresentationUnitsUtil.getStrategyRequestDto();

        PlanStrategy strategy = GetPresentationUnitsUtil.getPlanStrategy();

        when(planStrategyRepository.findById(any(PlanStrategyId.class))).thenReturn(java.util.Optional.of(strategy));
        when(planStrategyPresentationUnitsMapper.updatePUBasedOnRFA(any(PlanStrategy.class), anyInt(), any(Lvl1.class), any(Lvl2.class))).thenReturn(strategy);
        //doNothing().when(producer).postStrategyChangesToKafka(any(PlanStrategyResponse.class), any(Headers.class), any(EventType.class), anyLong());
        when(planStrategyRepository.save(any(PlanStrategy.class))).thenReturn(strategy);

        //Act
        presentationUnitsService.updatePresentationUnitsFromRfa(strategyDTORequest, strategy.getPlanStrategyId());

        //Assert
        verify(planStrategyPresentationUnitsMapper, times(1)).updatePUBasedOnRFA(any(PlanStrategy.class), anyInt(), any(Lvl1.class), any(Lvl2.class));
        //verify(producer, times(1)).postStrategyChangesToKafka(any(PlanStrategyResponse.class), any(Headers.class), any(EventType.class), anyLong());

    }

    @Nested
    @DisplayName("Testing Presentation Unit Update with min max based on Prod Dimension")
    class PresenatationUnitMinMax {

        FixtureAllocationStrategy allocationStrategy = new FixtureAllocationStrategy();
        List<StrategyFinelineFixture> stratFlFixtures = new ArrayList<>();

        @BeforeEach
        void setDataForRun() {

            allocationStrategy.setPlanId(102L);
            allocationStrategy.setStrategyId(1L);
            allocationStrategy.setLvl3Nbr(6033);
            allocationStrategy.setLvl4Nbr(7110);
            allocationStrategy.setHasProdDimChanged(true);
            allocationStrategy.setLvl0Nbr(5000);
            allocationStrategy.setLvl1Nbr(34);
            allocationStrategy.setLvl2Nbr(2999);
            allocationStrategy.setFinelineNbr(1134);
            allocationStrategy.setLvl3Name("1134- FL");

            stratFlFixtures = GetPresentationUnitsUtil.getFineLineFixture(allocationStrategy);

            when(strategyGroupRepository.getStrategyIdBySeasonCd(StratGroupType.PRESENTATION_UNITS.getStrategyGroupTypeCode(), null, null)).thenReturn(7L);
            when(strategyFinelineFixtureRepository.findFineLines_ByPlan_Id_And_Strategy_IdAnd_Cat_IdAndSub_Cat_IdAndFineline_nbr(
                    allocationStrategy.getPlanId(), 7L, allocationStrategy.getLvl0Nbr(), allocationStrategy.getLvl1Nbr(), allocationStrategy.getLvl2Nbr(), allocationStrategy.getLvl3Nbr(), allocationStrategy.getLvl4Nbr(), allocationStrategy.getFinelineNbr())).thenReturn(stratFlFixtures);
            when(strategyFinelineFixtureRepository.saveAll(stratFlFixtures)).thenReturn(stratFlFixtures);
        }


        @Test
        void testSetMinMaxBasedonProdDimensions(){

           //Arrange
            allocationStrategy.setProdDimensionId(2967);
            List<StrategyPUMinMax> puMinMaxes =  GetPresentationUnitsUtil.getPUMinMaxMetrics(allocationStrategy);
            when(presentationUnitsMinMaxMappingService.getPresentationUnitsMinMax()).thenReturn(puMinMaxes);

            //Act
            List<StrategyFinelineFixture> stratFlFixturesFinal = presentationUnitsService.updatePresentationMinMax(allocationStrategy);

            StrategyFinelineFixture flFixture = stratFlFixturesFinal.stream().findFirst().get();
            StrategyPUMinMax puMinMax = puMinMaxes.stream().filter(minMax->minMax.getStrategyPUMinMaxId().getLvl0Nbr().equals(allocationStrategy.getLvl0Nbr())
                    && minMax.getStrategyPUMinMaxId().getLvl1Nbr().equals(allocationStrategy.getLvl1Nbr())
                    && minMax.getStrategyPUMinMaxId().getLvl2Nbr().equals(allocationStrategy.getLvl2Nbr())
                    && minMax.getStrategyPUMinMaxId().getLvl3Nbr().equals(allocationStrategy.getLvl3Nbr())
                    && minMax.getStrategyPUMinMaxId().getLvl4Nbr().equals(allocationStrategy.getLvl4Nbr())
                    && minMax.getStrategyPUMinMaxId().getAhsVId().equals(allocationStrategy.getProdDimensionId())
                    && minMax.getStrategyPUMinMaxId().getFixtureTypeId().equals(flFixture.getStrategyFinelineFixtureId().getStrategySubCatgFixtureId().getStrategyMerchCatgFixtureId().getFixtureTypeId())
            ).findFirst().get();

            //Assert
            Assert.assertEquals(flFixture.getMinPresentationUnits(), puMinMax.getMinQty());
            Assert.assertEquals(flFixture.getMaxPresentationUnits(), puMinMax.getMaxQty());
        }

        @Test
        void testSetMinMaxBased_When_ProdDimensions_Set_Null(){

            //Arrange
            allocationStrategy.setProdDimensionId(null);

            //Act
            List<StrategyFinelineFixture> stratFlFixturesFinal = presentationUnitsService.updatePresentationMinMax(allocationStrategy);

            StrategyFinelineFixture flFixture = stratFlFixturesFinal.stream().findFirst().get();

            //Assert
            Assert.assertEquals(null, flFixture.getMinPresentationUnits());
            Assert.assertEquals(null, flFixture.getMaxPresentationUnits());
        }

        /*@Test
        void testPUMinMax_Changes_Notified(){
            //Arrange
            allocationStrategy.setProdDimensionId(2967);
            List<StrategyPUMinMax> puMinMaxes =  GetPresentationUnitsUtil.getPUMinMaxMetrics(allocationStrategy);
            when(presentationUnitsMinMaxMappingService.getPresentationUnitsMinMax()).thenReturn(puMinMaxes);

            //Act
            List<StrategyFinelineFixture> stratFlFixturesFinal = presentationUnitsService.updatePresentationMinMax(allocationStrategy);
            PlanStrategyId planStrategyId = PlanStrategyId.builder().strategyId(allocationStrategy.getStrategyId()).planId(allocationStrategy.getPlanId()).build();
            presentationUnitsService.sendKafkaMessageForEachFineline(allocationStrategy.getFinelineNbr(), stratFlFixturesFinal, planStrategyId);
            //presentationUnitsService.notifyPresentationUnitMinMaxChange(allocationStrategy, stratFlFixturesFinal);

            //Assert
            verify(planStrategyPresentationUnitsMapper, times(1)).createStrategyRequest(any(FixtureAllocationStrategy.class), any());
        }*/
    }
}
