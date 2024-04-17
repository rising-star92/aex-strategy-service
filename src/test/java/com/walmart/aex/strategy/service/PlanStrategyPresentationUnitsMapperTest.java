package com.walmart.aex.strategy.service;

import com.walmart.aex.strategy.dto.PlanFinelinesThickness;
import com.walmart.aex.strategy.dto.request.Lvl1;
import com.walmart.aex.strategy.dto.request.Lvl2;
import com.walmart.aex.strategy.dto.request.PlanStrategyDTO;
import com.walmart.aex.strategy.entity.*;
import com.walmart.aex.strategy.repository.StrategyFinelineRepository;
import com.walmart.aex.strategy.util.GetPresentationUnitsUtil;
import org.hibernate.collection.internal.PersistentList;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;


@ExtendWith(MockitoExtension.class)
class PlanStrategyPresentationUnitsMapperTest {

    @InjectMocks
    PlanStrategyPresentationUnitsMapper planStrategyPresentationUnitsMapper;

    @Mock
    private EntityManager entityManager;

    @Mock
    private StrategyFinelineRepository strategyFinelineRepository;

    @Mock
    private PresentationUnitsMinMaxMappingService presentationUnitsMinMaxMappingService;


    @Test
    void testUpdateExistingPUBasedOnRfa() {
        //Arrange
        PlanStrategyDTO strategyDTORequest = GetPresentationUnitsUtil.getStrategyRequestDto();
        PlanStrategy strategy = GetPresentationUnitsUtil.getPlanStrategy();
        List<PlanFinelinesThickness> planFinelinesThicknessList = getFinelineThickness();

        List<StrategyPUMinMax> strategyPUMinMaxes = getStrategyPuMinMax();

        when(strategyFinelineRepository.getFinelinesThickness(anyLong(), anyList())).thenReturn(planFinelinesThicknessList);
        when(presentationUnitsMinMaxMappingService.getPresentationUnitsMinMax()).thenReturn(strategyPUMinMaxes);
        //Act
        for (Lvl1 lvl1 : strategyDTORequest.getLvl1List()) {
            for (Lvl2 lvl2 : lvl1.getLvl2List()) {
                strategy = planStrategyPresentationUnitsMapper.updatePUBasedOnRFA(strategy, strategyDTORequest.getLvl0Nbr(), lvl1, lvl2);
            }
        }

        //Checking new fixture is being added for category 6033
        StrategyMerchCatgFixture strategyMerchCatgFixture6033_4 = strategy.getStrategyMerchCatgs().stream().filter(
                strategyMerchCatg -> strategyMerchCatg.getStrategyMerchCatgId().getLvl3Nbr().equals(6033))
                .findFirst()
                .map(StrategyMerchCatg::getStrategyMerchCatgFixtures)
                .stream()
                .flatMap(Collection::stream)
                .filter(strategyMerchCatgFixture1 -> strategyMerchCatgFixture1.getStrategyMerchCatgFixtureId().getFixtureTypeId().equals(4))
                .findFirst()
                .orElse(null);

        //Checking new fixture is being added for subcategory 7110
        StrategySubCatgFixture strategySubCatgFixture6033_7110_4 = strategyMerchCatgFixture6033_4.getStrategySubCatgFixtures().stream().filter(
                        strategyMerchSubCatg -> strategyMerchSubCatg.getStrategySubCatgFixtureId().getLvl4Nbr().equals(7110) &&
                                strategyMerchSubCatg.getStrategySubCatgFixtureId().getStrategyMerchCatgFixtureId().getFixtureTypeId().equals(4))
                .findFirst()
                .orElse(null);

        //Checking new fixture is being added for fineLine 1135
        StrategyFinelineFixture strategyFineLineFixture6033_7110_1135_4 = strategySubCatgFixture6033_7110_4.getStrategyFinelineFixtures().stream().filter(
                        strategyFineLine -> strategyFineLine.getStrategyFinelineFixtureId().getFinelineNbr().equals(1135) &&
                                strategyFineLine.getStrategyFinelineFixtureId().getStrategySubCatgFixtureId().getStrategyMerchCatgFixtureId().getFixtureTypeId().equals(4))
                .findFirst()
                .orElse(null);

        //Checking new fixture is being added for category 6034
        StrategyMerchCatgFixture strategyMerchCatgFixture6034_3 = strategy.getStrategyMerchCatgs().stream().filter(
                        strategyMerchCatg -> strategyMerchCatg.getStrategyMerchCatgId().getLvl3Nbr().equals(6034))
                .findFirst()
                .map(StrategyMerchCatg::getStrategyMerchCatgFixtures)
                .stream()
                .flatMap(Collection::stream)
                .filter(strategyMerchCatgFixture1 -> strategyMerchCatgFixture1.getStrategyMerchCatgFixtureId().getFixtureTypeId().equals(3))
                .findFirst()
                .orElse(null);

        //Checking new fixture is being added for category 6034
        StrategyMerchCatgFixture strategyMerchCatgFixture6034_4 = strategy.getStrategyMerchCatgs().stream().filter(
                strategyMerchCatg -> strategyMerchCatg.getStrategyMerchCatgId().getLvl3Nbr().equals(6034))
                .findFirst()
                .map(StrategyMerchCatg::getStrategyMerchCatgFixtures)
                .stream()
                .flatMap(Collection::stream)
                .filter(strategyMerchCatgFixture1 -> strategyMerchCatgFixture1.getStrategyMerchCatgFixtureId().getFixtureTypeId().equals(4))
                .findFirst()
                .orElse(null);

        //Checking new fixture is being added for subcategory 7112
        StrategySubCatgFixture strategySubCatgFixture6034_7112_3 = strategyMerchCatgFixture6034_3.getStrategySubCatgFixtures().stream().filter(
                        strategyMerchSubCatg -> strategyMerchSubCatg.getStrategySubCatgFixtureId().getLvl4Nbr().equals(7112) &&
                                strategyMerchSubCatg.getStrategySubCatgFixtureId().getStrategyMerchCatgFixtureId().getFixtureTypeId().equals(3))
                .findFirst()
                .orElse(null);

        //Checking new fixture is being added for subcategory 7112
        StrategySubCatgFixture strategySubCatgFixture6034_7112_4 = strategyMerchCatgFixture6034_4.getStrategySubCatgFixtures().stream().filter(
                strategyMerchSubCatg -> strategyMerchSubCatg.getStrategySubCatgFixtureId().getLvl4Nbr().equals(7112) &&
                        strategyMerchSubCatg.getStrategySubCatgFixtureId().getStrategyMerchCatgFixtureId().getFixtureTypeId().equals(4))
                .findFirst()
                .orElse(null);

        //Checking new fixture is being added for fineLine 1144
        StrategyFinelineFixture strategyFineLineFixture6033_7112_1144_3 = strategySubCatgFixture6034_7112_3.getStrategyFinelineFixtures().stream().filter(
                        strategyFineLine -> strategyFineLine.getStrategyFinelineFixtureId().getFinelineNbr().equals(1144) &&
                                strategyFineLine.getStrategyFinelineFixtureId().getStrategySubCatgFixtureId().getStrategyMerchCatgFixtureId().getFixtureTypeId().equals(3))
                .findFirst()
                .orElse(null);

        //Checking new fixture is being added for fineLine 1145
        StrategyFinelineFixture strategyFineLineFixture6033_7112_1145_4 = strategySubCatgFixture6034_7112_4.getStrategyFinelineFixtures().stream().filter(
                strategyFineLine -> strategyFineLine.getStrategyFinelineFixtureId().getFinelineNbr().equals(1145) &&
                        strategyFineLine.getStrategyFinelineFixtureId().getStrategySubCatgFixtureId().getStrategyMerchCatgFixtureId().getFixtureTypeId().equals(4))
                .findFirst()
                .orElse(null);


        //Checking fixture not part of the request is being removed from category 6033
        StrategyMerchCatgFixture strategyMerchCatgFixture6033_R1 = strategy.getStrategyMerchCatgs().stream().filter(
                        strategyMerchCatg -> strategyMerchCatg.getStrategyMerchCatgId().getLvl3Nbr().equals(6033))
                .findFirst()
                .map(StrategyMerchCatg::getStrategyMerchCatgFixtures)
                .stream()
                .flatMap(Collection::stream)
                .filter(strategyMerchCatgFixture -> strategyMerchCatgFixture.getStrategyMerchCatgFixtureId().getFixtureTypeId().equals(1))
                .findFirst()
                .orElse(null);

        //Checking fixture not part of the request is being removed from category 6034
        StrategyMerchCatgFixture strategyMerchCatgFixture6034_R1 = strategy.getStrategyMerchCatgs().stream().filter(
                        strategyMerchCatg -> strategyMerchCatg.getStrategyMerchCatgId().getLvl3Nbr().equals(6034))
                .findFirst()
                .map(StrategyMerchCatg::getStrategyMerchCatgFixtures)
                .stream()
                .flatMap(Collection::stream)
                .filter(strategyMerchCatgFixture -> strategyMerchCatgFixture.getStrategyMerchCatgFixtureId().getFixtureTypeId().equals(1))
                .findFirst()
                .orElse(null);

        //Assert
        assertNotNull(strategyMerchCatgFixture6033_4);
        assertNotNull(strategySubCatgFixture6033_7110_4);
        assertNotNull(strategyFineLineFixture6033_7110_1135_4);

        assertNotNull(strategyMerchCatgFixture6034_3);
        assertNotNull(strategySubCatgFixture6034_7112_3);
        assertNotNull(strategyFineLineFixture6033_7112_1144_3);

        assertNull(strategyMerchCatgFixture6033_R1);
        assertNull(strategyMerchCatgFixture6034_R1);

        assertEquals(50, strategyFineLineFixture6033_7110_1135_4.getMinPresentationUnits().intValue());
        assertEquals(100, strategyFineLineFixture6033_7110_1135_4.getMaxPresentationUnits().intValue());
        assertNull(strategyFineLineFixture6033_7112_1144_3.getMinPresentationUnits());
        assertNull(strategyFineLineFixture6033_7112_1144_3.getMaxPresentationUnits());

        assertEquals(25, strategyFineLineFixture6033_7112_1145_4.getMinPresentationUnits().intValue());
        assertEquals(50, strategyFineLineFixture6033_7112_1145_4.getMaxPresentationUnits().intValue());
       verify(entityManager, times(16)).flush();
    }

    private List<StrategyPUMinMax> getStrategyPuMinMax() {
        List<StrategyPUMinMax> strategyPUMinMaxes = new ArrayList<>();
        StrategyPUMinMax strategyPUMinMax = new StrategyPUMinMax();
        StrategyPUMinMaxId strategyPUMinMaxId = new StrategyPUMinMaxId();
        strategyPUMinMaxId.setLvl0Nbr(5000);
        strategyPUMinMaxId.setLvl1Nbr(34);
        strategyPUMinMaxId.setLvl2Nbr(2999);
        strategyPUMinMaxId.setLvl3Nbr(6033);
        strategyPUMinMaxId.setLvl4Nbr(7110);
        strategyPUMinMaxId.setFixtureTypeId(1);
        strategyPUMinMaxId.setAhsAsiId(2607);
        strategyPUMinMaxId.setAhsVId(123);
        strategyPUMinMax.setStrategyPUMinMaxId(strategyPUMinMaxId);
        strategyPUMinMax.setAhsAttributeName("Thin");
        strategyPUMinMax.setAhsValue("Thin");
        strategyPUMinMax.setMinQty(100);
        strategyPUMinMax.setMaxQty(200);

        StrategyPUMinMax strategyPUMinMax1 = new StrategyPUMinMax();
        StrategyPUMinMaxId strategyPUMinMaxId1 = new StrategyPUMinMaxId();
        strategyPUMinMaxId1.setLvl0Nbr(5000);
        strategyPUMinMaxId1.setLvl1Nbr(34);
        strategyPUMinMaxId1.setLvl2Nbr(2999);
        strategyPUMinMaxId1.setLvl3Nbr(6033);
        strategyPUMinMaxId1.setLvl4Nbr(7110);
        strategyPUMinMaxId1.setFixtureTypeId(4);
        strategyPUMinMaxId1.setAhsAsiId(2607);
        strategyPUMinMaxId1.setAhsVId(321);
        strategyPUMinMax1.setStrategyPUMinMaxId(strategyPUMinMaxId1);
        strategyPUMinMax1.setAhsAttributeName("Medium");
        strategyPUMinMax1.setAhsValue("Medium");
        strategyPUMinMax1.setMinQty(50);
        strategyPUMinMax1.setMaxQty(100);

        StrategyPUMinMax strategyPUMinMax2 = new StrategyPUMinMax();
        StrategyPUMinMaxId strategyPUMinMaxId2 = new StrategyPUMinMaxId();
        strategyPUMinMaxId2.setLvl0Nbr(5000);
        strategyPUMinMaxId2.setLvl1Nbr(34);
        strategyPUMinMaxId2.setLvl2Nbr(2999);
        strategyPUMinMaxId2.setLvl3Nbr(6034);
        strategyPUMinMaxId2.setLvl4Nbr(7112);
        strategyPUMinMaxId2.setFixtureTypeId(4);
        strategyPUMinMaxId2.setAhsAsiId(2607);
        strategyPUMinMaxId2.setAhsVId(111);
        strategyPUMinMax2.setStrategyPUMinMaxId(strategyPUMinMaxId2);
        strategyPUMinMax2.setAhsAttributeName("Thick");
        strategyPUMinMax2.setAhsValue("Thick");
        strategyPUMinMax2.setMinQty(25);
        strategyPUMinMax2.setMaxQty(50);

        StrategyPUMinMax strategyPUMinMax3 = new StrategyPUMinMax();
        StrategyPUMinMaxId strategyPUMinMaxId3 = new StrategyPUMinMaxId();
        strategyPUMinMaxId3.setLvl0Nbr(5000);
        strategyPUMinMaxId3.setLvl1Nbr(34);
        strategyPUMinMaxId3.setLvl2Nbr(2999);
        strategyPUMinMaxId3.setLvl3Nbr(6034);
        strategyPUMinMaxId3.setLvl4Nbr(7112);
        strategyPUMinMaxId3.setFixtureTypeId(1);
        strategyPUMinMaxId3.setAhsAsiId(2607);
        strategyPUMinMaxId3.setAhsVId(112);
        strategyPUMinMax3.setStrategyPUMinMaxId(strategyPUMinMaxId3);
        strategyPUMinMax3.setAhsAttributeName("Medium");
        strategyPUMinMax3.setAhsValue("Medium");
        strategyPUMinMax3.setMinQty(100);
        strategyPUMinMax3.setMaxQty(200);

        strategyPUMinMaxes.add(strategyPUMinMax);
        strategyPUMinMaxes.add(strategyPUMinMax1);
        strategyPUMinMaxes.add(strategyPUMinMax2);
        strategyPUMinMaxes.add(strategyPUMinMax3);
        return strategyPUMinMaxes;
    }

    private List<PlanFinelinesThickness> getFinelineThickness() {

        List<PlanFinelinesThickness> planFinelinesThicknessList =  new ArrayList<>();
        PlanFinelinesThickness planFinelinesThickness = new PlanFinelinesThickness();
        planFinelinesThickness.setPlanId(465L);
        planFinelinesThickness.setLvl0Nbr(5000);
        planFinelinesThickness.setLvl1Nbr(34);
        planFinelinesThickness.setLvl2Nbr(2999);
        planFinelinesThickness.setLvl3Nbr(6033);
        planFinelinesThickness.setLvl4Nbr(7110);
        planFinelinesThickness.setFinelineNbr(1134);
        planFinelinesThickness.setThicknessId(123);

        PlanFinelinesThickness planFinelinesThickness1 = new PlanFinelinesThickness();
        planFinelinesThickness1.setPlanId(465L);
        planFinelinesThickness1.setLvl0Nbr(5000);
        planFinelinesThickness1.setLvl1Nbr(34);
        planFinelinesThickness1.setLvl2Nbr(2999);
        planFinelinesThickness1.setLvl3Nbr(6033);
        planFinelinesThickness1.setLvl4Nbr(7110);
        planFinelinesThickness1.setFinelineNbr(1135);
        planFinelinesThickness1.setThicknessId(321);

        PlanFinelinesThickness planFinelinesThickness2 = new PlanFinelinesThickness();
        planFinelinesThickness2.setPlanId(465L);
        planFinelinesThickness2.setLvl0Nbr(5000);
        planFinelinesThickness2.setLvl1Nbr(34);
        planFinelinesThickness2.setLvl2Nbr(2999);
        planFinelinesThickness2.setLvl3Nbr(6034);
        planFinelinesThickness2.setLvl4Nbr(7112);
        planFinelinesThickness2.setFinelineNbr(1145);
        planFinelinesThickness2.setThicknessId(111);

        PlanFinelinesThickness planFinelinesThickness3 = new PlanFinelinesThickness();
        planFinelinesThickness3.setPlanId(465L);
        planFinelinesThickness3.setLvl0Nbr(5000);
        planFinelinesThickness3.setLvl1Nbr(34);
        planFinelinesThickness3.setLvl2Nbr(2999);
        planFinelinesThickness3.setLvl3Nbr(6034);
        planFinelinesThickness3.setLvl4Nbr(7112);
        planFinelinesThickness3.setFinelineNbr(1144);
        planFinelinesThickness3.setThicknessId(null);

        planFinelinesThicknessList.add(planFinelinesThickness);
        planFinelinesThicknessList.add(planFinelinesThickness1);
        planFinelinesThicknessList.add(planFinelinesThickness2);
        planFinelinesThicknessList.add(planFinelinesThickness3);
        return planFinelinesThicknessList;
    }

    @Test
    void testCreatingNewPUBasedOnRfa() {
        //Arrange
        PlanStrategyDTO strategyDTORequest = GetPresentationUnitsUtil.getStrategyRequestDto();
        PlanStrategy strategy = GetPresentationUnitsUtil.getNewPlanStrategy();

        //Act
        for (Lvl1 lvl1 : strategyDTORequest.getLvl1List()) {
            for (Lvl2 lvl2 : lvl1.getLvl2List()) {
                strategy = planStrategyPresentationUnitsMapper.updatePUBasedOnRFA(strategy, strategyDTORequest.getLvl0Nbr(), lvl1, lvl2);
            }
        }

        //Checking new fixture is being added for category 6033
        StrategyMerchCatgFixture strategyMerchCatgFixture6033_4 = strategy.getStrategyMerchCatgs().stream().filter(
                        strategyMerchCatg -> strategyMerchCatg.getStrategyMerchCatgId().getLvl3Nbr().equals(6033))
                .findFirst()
                .get()
                .getStrategyMerchCatgFixtures()
                .stream()
                .filter(strategyMerchCatgFixture1 -> strategyMerchCatgFixture1.getStrategyMerchCatgFixtureId().getFixtureTypeId().equals(4))
                .findFirst()
                .orElse(null);

        //Checking new fixture is being added for subcategory 7110
        StrategySubCatgFixture strategySubCatgFixture6033_7110_4 = strategyMerchCatgFixture6033_4.getStrategySubCatgFixtures().stream().filter(
                        strategyMerchSubCatg -> strategyMerchSubCatg.getStrategySubCatgFixtureId().getLvl4Nbr().equals(7110) &&
                                strategyMerchSubCatg.getStrategySubCatgFixtureId().getStrategyMerchCatgFixtureId().getFixtureTypeId().equals(4))
                .findFirst()
                .orElse(null);

        //Checking new fixture is being added for fineLine 1135
        StrategyFinelineFixture strategyFineLineFixture6033_7110_1135_4 = strategySubCatgFixture6033_7110_4.getStrategyFinelineFixtures().stream().filter(
                        strategyFineLine -> strategyFineLine.getStrategyFinelineFixtureId().getFinelineNbr().equals(1135) &&
                                strategyFineLine.getStrategyFinelineFixtureId().getStrategySubCatgFixtureId().getStrategyMerchCatgFixtureId().getFixtureTypeId().equals(4))
                .findFirst()
                .orElse(null);

        //Checking new fixture is being added for category 6034
        StrategyMerchCatgFixture strategyMerchCatgFixture6034_3 = strategy.getStrategyMerchCatgs().stream().filter(
                        strategyMerchCatg -> strategyMerchCatg.getStrategyMerchCatgId().getLvl3Nbr().equals(6034))
                .findFirst()
                .get()
                .getStrategyMerchCatgFixtures()
                .stream()
                .filter(strategyMerchCatgFixture1 -> strategyMerchCatgFixture1.getStrategyMerchCatgFixtureId().getFixtureTypeId().equals(3))
                .findFirst()
                .orElse(null);

        //Checking new fixture is being added for subcategory 7112
        StrategySubCatgFixture strategySubCatgFixture6034_7112_3 = strategyMerchCatgFixture6034_3.getStrategySubCatgFixtures().stream().filter(
                        strategyMerchSubCatg -> strategyMerchSubCatg.getStrategySubCatgFixtureId().getLvl4Nbr().equals(7112) &&
                                strategyMerchSubCatg.getStrategySubCatgFixtureId().getStrategyMerchCatgFixtureId().getFixtureTypeId().equals(3))
                .findFirst()
                .orElse(null);

        //Checking new fixture is being added for fineLine 1144
        StrategyFinelineFixture strategyFineLineFixture6033_7112_1144_3 = strategySubCatgFixture6034_7112_3.getStrategyFinelineFixtures().stream().filter(
                        strategyFineLine -> strategyFineLine.getStrategyFinelineFixtureId().getFinelineNbr().equals(1144) &&
                                strategyFineLine.getStrategyFinelineFixtureId().getStrategySubCatgFixtureId().getStrategyMerchCatgFixtureId().getFixtureTypeId().equals(3))
                .findFirst()
                .orElse(null);

        //Assert
        assertNotNull(strategyMerchCatgFixture6033_4);
        assertNotNull(strategySubCatgFixture6033_7110_4);
        assertNotNull(strategyFineLineFixture6033_7110_1135_4);

        assertNotNull(strategyMerchCatgFixture6034_3);
        assertNotNull(strategySubCatgFixture6034_7112_3);
        assertNotNull(strategyFineLineFixture6033_7112_1144_3);
    }

    @Test
    void testRemoveFLNotPartOfUpdate(){
        //Arrange
        PlanStrategyDTO strategyDTORequest = GetPresentationUnitsUtil.getSingleCategoryStrategyDto();
        PlanStrategy strategy = GetPresentationUnitsUtil.getNewPlanStrategy();

        //Act
        for (Lvl1 lvl1 : strategyDTORequest.getLvl1List()) {
            for (Lvl2 lvl2 : lvl1.getLvl2List()) {
                strategy = planStrategyPresentationUnitsMapper.updatePUBasedOnRFA(strategy, strategyDTORequest.getLvl0Nbr(), lvl1, lvl2);
            }
        }

        //Getting the fixture for category 6033
        StrategyMerchCatgFixture strategyMerchCatgFixture6033_1 = strategy.getStrategyMerchCatgs().stream().filter(
                        strategyMerchCatg -> strategyMerchCatg.getStrategyMerchCatgId().getLvl3Nbr().equals(6033))
                .findFirst()
                .get()
                .getStrategyMerchCatgFixtures()
                .stream()
                .filter(strategyMerchCatgFixture1 -> strategyMerchCatgFixture1.getStrategyMerchCatgFixtureId().getFixtureTypeId().equals(1))
                .findFirst()
                .orElse(null);

        //Getting the fixture for subcategory 7110
        StrategySubCatgFixture strategySubCatgFixture6033_7110_1 = strategyMerchCatgFixture6033_1.getStrategySubCatgFixtures().stream().filter(
                        strategyMerchSubCatg -> strategyMerchSubCatg.getStrategySubCatgFixtureId().getLvl4Nbr().equals(7110) &&
                                strategyMerchSubCatg.getStrategySubCatgFixtureId().getStrategyMerchCatgFixtureId().getFixtureTypeId().equals(1))
                .findFirst()
                .orElse(null);

        //Checking new fixture is being added for fineLine 1135
        StrategyFinelineFixture strategyFineLineFixture6033_7110_1135_1 = strategySubCatgFixture6033_7110_1.getStrategyFinelineFixtures().stream().filter(
                        strategyFineLine -> strategyFineLine.getStrategyFinelineFixtureId().getFinelineNbr().equals(1135))
                .findFirst()
                .orElse(null);

        //Asset
        assertEquals(null, strategyFineLineFixture6033_7110_1135_1);
    }

    @Test
    void testRemoveCategoryAndSubcategoryNotPartOfUpdate(){
        //Arrange
        PlanStrategyDTO strategyDTORequest = GetPresentationUnitsUtil.getSingleCategoryStrategyDto();
        PlanStrategy strategy = GetPresentationUnitsUtil.getNewPlanStrategy();

        //Act
        for (Lvl1 lvl1 : strategyDTORequest.getLvl1List()) {
            for (Lvl2 lvl2 : lvl1.getLvl2List()) {
                strategy = planStrategyPresentationUnitsMapper.updatePUBasedOnRFA(strategy, strategyDTORequest.getLvl0Nbr(), lvl1, lvl2);
            }
        }

        //Getting the fixture for category 6034
        StrategyMerchCatg strategyMerchCatg6034 = strategy.getStrategyMerchCatgs().stream().filter(
                        strategyMerchCatg -> strategyMerchCatg.getStrategyMerchCatgId().getLvl3Nbr().equals(6034))
                .findFirst()
                .orElse(null);

        //Assert
        assertEquals(null, strategyMerchCatg6034);
    }

}
