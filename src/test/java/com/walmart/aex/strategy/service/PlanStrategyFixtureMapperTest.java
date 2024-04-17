package com.walmart.aex.strategy.service;

import com.walmart.aex.strategy.dto.request.Lvl1;
import com.walmart.aex.strategy.dto.request.Lvl2;
import com.walmart.aex.strategy.dto.request.Lvl3;
import com.walmart.aex.strategy.dto.request.PlanStrategyDTO;
import com.walmart.aex.strategy.entity.PlanStrategy;
import com.walmart.aex.strategy.entity.PlanStrategyId;
import com.walmart.aex.strategy.entity.StrategyMerchCatg;
import com.walmart.aex.strategy.util.GetStrategyFlClusEligRankingUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@ExtendWith(MockitoExtension.class)
class PlanStrategyFixtureMapperTest {

    @InjectMocks
    PlanStrategyFixtureMapper planStrategyFixtureMapper;

    @Mock
    EntityManager entityManager;

    @Test
    void setStrategyMerchCatgInitialRequest() {
        //Arrange
        PlanStrategy planStrategy = new PlanStrategy();
        List<Lvl3> lvl3Set = GetStrategyFlClusEligRankingUtil.getLvl3List();
        PlanStrategyDTO request = GetStrategyFlClusEligRankingUtil.getPlanStrategyRequest();
        Lvl1 lvl1 = GetStrategyFlClusEligRankingUtil.getLvl1List().stream().findFirst().orElse(null);
        Lvl2 lvl2 = GetStrategyFlClusEligRankingUtil.getLvl2List().stream().findFirst().orElse(null);
        PlanStrategyId planStrategyId = GetStrategyFlClusEligRankingUtil.getPlanStrategyIdForCreate();

        //Act
        Set<StrategyMerchCatg> strategyMerchCatgs = planStrategyFixtureMapper.setStrategyMerchCatg(planStrategy, lvl3Set, planStrategyId, request, lvl1, lvl2);

        // Assert
        assertNotNull(strategyMerchCatgs);
        assertEquals(1, strategyMerchCatgs.size());
    }
}
