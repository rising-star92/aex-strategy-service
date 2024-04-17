package com.walmart.aex.strategy.service;

import com.walmart.aex.strategy.dto.request.CustomerChoice;
import com.walmart.aex.strategy.dto.request.Fineline;
import com.walmart.aex.strategy.dto.request.PlanStrategyDeleteMessage;
import com.walmart.aex.strategy.dto.request.StrongKey;
import com.walmart.aex.strategy.dto.request.Style;
import com.walmart.aex.strategy.entity.PlanClusterStrategy;
import com.walmart.aex.strategy.entity.PlanStrategy;
import com.walmart.aex.strategy.entity.PlanStrategyId;
import com.walmart.aex.strategy.entity.StrategyCcSPCluster;
import com.walmart.aex.strategy.entity.StrategyFineLineSPCluster;
import com.walmart.aex.strategy.entity.StrategyMerchCategorySPCluster;
import com.walmart.aex.strategy.entity.StrategyStyleSPCluster;
import com.walmart.aex.strategy.entity.StrategySubCategorySPCluster;
import com.walmart.aex.strategy.enums.StratGroupType;
import com.walmart.aex.strategy.repository.PlanStrategyRepository;
import com.walmart.aex.strategy.repository.StrategyGroupRepository;
import com.walmart.aex.strategy.util.GetSizeSPClusterObjUtil;
import com.walmart.aex.strategy.util.GetStrategyFlClusEligRankingUtil;
import org.apache.commons.collections.CollectionUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class SizeAndPackDeleteServiceTest {

    @Mock
    private StrategyGroupRepository strategyGroupRepository;

    @Mock
    private PlanStrategyRepository planStrategyRepository;

    @InjectMocks
    private SizeAndPackDeleteService sizeAndPackDeleteService;

    @BeforeEach
    public void setUp(){
        PlanStrategy planStrategy = new PlanStrategy();
        PlanStrategyId planStrategyId = new PlanStrategyId();
        planStrategyId.setPlanId(1l);
        planStrategyId.setStrategyId(4l);
        planStrategy.setPlanStrategyId(GetStrategyFlClusEligRankingUtil.getPlanStrategyId());
        planStrategy.setPlanClusterStrategies(GetSizeSPClusterObjUtil.getPlanClusterStrategies());
        lenient().when(strategyGroupRepository.getStrategyIdBySeasonCd(StratGroupType.SIZE_PROFILE.getStrategyGroupTypeCode(),null, null)).thenReturn(4l);
        lenient().when(planStrategyRepository.findById(any(PlanStrategyId.class))).thenReturn(java.util.Optional.of(planStrategy));
    }

    @Test
    void testDeletePlanDeleteFineline() {
        PlanStrategyDeleteMessage planStrategyDeleteMessage = getStrategyDeleteRequestPayload();
        StrongKey strongKey = planStrategyDeleteMessage.getStrongKey();
        Fineline fineline = strongKey.getFineline();
        //delete fineline
        fineline.setStyles(null);
        strongKey.setFineline(fineline);

        Set<StrategyMerchCategorySPCluster> strategyMerchCategorySPClusters = getCategoryClusters();

        //assert before deletion
        Set<StrategyFineLineSPCluster> strategyFineLineSPClusters = strategyMerchCategorySPClusters
                .stream()
                .map(StrategyMerchCategorySPCluster::getStrategySubCatgSPClusters)
                .flatMap(Collection::stream).map(StrategySubCategorySPCluster::getStrategyFinelinesSPCluster)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
        assertEquals(1, strategyFineLineSPClusters.size(), "fineline clusters size should be one");

        //Run
        sizeAndPackDeleteService.deleteFinelineSp(strategyMerchCategorySPClusters, strongKey);

        //assert
        strategyFineLineSPClusters = strategyMerchCategorySPClusters
                .stream()
                .map(StrategyMerchCategorySPCluster::getStrategySubCatgSPClusters)
                .flatMap(Collection::stream).map(StrategySubCategorySPCluster::getStrategyFinelinesSPCluster)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
        assertEquals(0, strategyFineLineSPClusters.size(), "fineline cluster should be zero");
    }

    @Test
    void testDeletePlanDeleteStyle() {
        PlanStrategyDeleteMessage planStrategyDeleteMessage = getStrategyDeleteRequestPayload();
        Fineline fineline = planStrategyDeleteMessage.getStrongKey().getFineline();
        Style style = fineline.getStyles().get(0);
        style.setCustomerChoices(null);
        List<Style> styles = new ArrayList<>();
        styles.add(style);
        fineline.setStyles(styles);

        Set<StrategySubCategorySPCluster> strategySubCategorySPClusters = getSubCategoryClusters(getCategoryClusters());

        //assert before deletion
        Set<StrategyStyleSPCluster> strategyStylesSPClusters = strategySubCategorySPClusters
                .stream()
                .map(StrategySubCategorySPCluster::getStrategyFinelinesSPCluster)
                .flatMap(Collection::stream).map(StrategyFineLineSPCluster::getStrategyStylesSPClusters)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
        assertEquals(1, strategyStylesSPClusters.size(), "style clusters size should be one");

        //Run
        sizeAndPackDeleteService.deleteStyleSp(strategySubCategorySPClusters, fineline);

        //assert
        strategyStylesSPClusters = strategySubCategorySPClusters
                .stream()
                .map(StrategySubCategorySPCluster::getStrategyFinelinesSPCluster)
                .flatMap(Collection::stream).map(StrategyFineLineSPCluster::getStrategyStylesSPClusters)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
        assertEquals(0, strategyStylesSPClusters.size(), "style cluster should be zero");
    }

    @Test
    void testDeletePlanDeleteCC() {
        PlanStrategyDeleteMessage planStrategyDeleteMessage = getStrategyDeleteRequestPayload();
        Style style = planStrategyDeleteMessage.getStrongKey().getFineline().getStyles().get(0);

        Set<StrategyFineLineSPCluster> strategyFinelinesSPClusters = getFinelineClusters(getSubCategoryClusters(getCategoryClusters()));

        //Run
        sizeAndPackDeleteService.deleteCcSp(strategyFinelinesSPClusters, style);

        //assert
        Set<StrategyCcSPCluster> strategyCcSPClusters1 = strategyFinelinesSPClusters.stream().
                map(StrategyFineLineSPCluster::getStrategyStylesSPClusters)
                .flatMap(Collection::stream)
                .map(StrategyStyleSPCluster::getStrategyCcSPClusters)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
        assertEquals(0, strategyCcSPClusters1.size(), "cc cluster should be zero");
    }

    @Test
    void testDeletePlanForAllLevels() {
        PlanStrategyDeleteMessage planStrategyDeleteMessage = getStrategyDeleteRequestPayload();
        Set<StrategyMerchCategorySPCluster> strategyMerchCategorySPClusters= getCategoryClusters();

        //Run : For better code ceverage
        sizeAndPackDeleteService.deleteSizeStrategy(planStrategyDeleteMessage);

        //Run
        sizeAndPackDeleteService.deleteSPStrategyRelatedEntries(strategyMerchCategorySPClusters, planStrategyDeleteMessage.getStrongKey());

        //deleting cc should delete style, fineline, sub category and category as all levels had one entries
        assertTrue(CollectionUtils.isEmpty(strategyMerchCategorySPClusters));

    }

    private Set<StrategyFineLineSPCluster> getFinelineClusters(Set<StrategySubCategorySPCluster> strategySubCategorySPClusters) {
        for (StrategySubCategorySPCluster strategySubCategorySPCluster : strategySubCategorySPClusters) {
            return strategySubCategorySPCluster.getStrategyFinelinesSPCluster();
        }

        return null;
    }

    private Set<StrategyMerchCategorySPCluster> getCategoryClusters() {
        Set<PlanClusterStrategy> planClusterStrategies = GetSizeSPClusterObjUtil.getPlanClusterStrategies();

        for (PlanClusterStrategy planClusterStrategy : planClusterStrategies) {
            return planClusterStrategy.getStrategyMerchCategorySPCluster();
        }
        return null;
    }

    private Set<StrategySubCategorySPCluster> getSubCategoryClusters(Set<StrategyMerchCategorySPCluster> strategyMerchCategorySPClusters) {
        for(StrategyMerchCategorySPCluster strategyMerchCategorySPCluster : strategyMerchCategorySPClusters) {
            return strategyMerchCategorySPCluster.getStrategySubCatgSPClusters();
        }

        return null;
    }

    private PlanStrategyDeleteMessage getStrategyDeleteRequestPayload() {
        PlanStrategyDeleteMessage planStrategyDeleteMessage = new PlanStrategyDeleteMessage();
        StrongKey strongKey = new StrongKey();
        strongKey.setPlanId(346l);
        strongKey.setPlanDesc("S2 - FYE 2023");
        strongKey.setLvl0Nbr(39107153);
        strongKey.setLvl1Nbr(50400);
        strongKey.setLvl2Nbr(105400);
        strongKey.setLvl3Nbr(34556);
        strongKey.setLvl4Nbr(4567);
        Fineline fineline = new Fineline();
        fineline.setFinelineNbr(151);
        List<Style> styles = new ArrayList<>();
        Style style = new Style();
        style.setStyleNbr("151_23_01_001");
        List<CustomerChoice> customerChoices = new ArrayList<>();
        CustomerChoice customerChoice = new CustomerChoice();
        customerChoice.setCcId("151_23_01_001_001");
        customerChoices.add(customerChoice);
        style.setCustomerChoices(customerChoices);
        styles.add(style);
        fineline.setStyles(styles);
        strongKey.setFineline(fineline);
        planStrategyDeleteMessage.setStrongKey(strongKey);
        return planStrategyDeleteMessage;
    }






}
