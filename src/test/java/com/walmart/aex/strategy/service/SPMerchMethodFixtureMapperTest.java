package com.walmart.aex.strategy.service;

import com.walmart.aex.strategy.dto.sizepack.SPMerchMethLvl3;
import com.walmart.aex.strategy.entity.PlanStrategy;
import com.walmart.aex.strategy.entity.StrategyMerchCatgFixture;
import com.walmart.aex.strategy.repository.FixtureAllocationStrategyRepository;
import com.walmart.aex.strategy.util.GetSPMerchMethodFixtureUtil;
import com.walmart.aex.strategy.util.GetSizeSPClusterObjUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@ExtendWith(MockitoExtension.class)
class SPMerchMethodFixtureMapperTest {
    @InjectMocks
    private SPMerchMethFixtureMapper spMerchMethFixtureMapper;

    @Mock
    private FixtureAllocationStrategyRepository fixtureAllocationStrategyRepository;

    @Test
    void setSPMerchMethodFixtureInitialRequest() {
        //Arrange
        SPMerchMethLvl3 lvl3 = GetSPMerchMethodFixtureUtil.getLvl3List();
        Integer lvl0Nbr = 387;
        Integer lvl1Nbr = 34;
        Integer lvl2Nbr = 1056308 ;

        PlanStrategy planStrategy = GetSizeSPClusterObjUtil.getPlanStrategyForSize();
        Set<StrategyMerchCatgFixture> strategyMerchCatgFixtures1 = planStrategy.getStrategyMerchCatgs().stream().findFirst()
                .map(strategyMerchCatg -> strategyMerchCatg.getStrategyMerchCatgFixtures()).orElse(null);
        List<StrategyMerchCatgFixture> strategyMerchCatgFixtures2 = new ArrayList<>(strategyMerchCatgFixtures1);
        Mockito.when(fixtureAllocationStrategyRepository.findStrategyMerchCatgFixtureByStrategyMerchCatgFixtureId_StrategyMerchCatgId_PlanStrategyIdAndStrategyMerchCatgFixtureId_StrategyMerchCatgId_lvl3Nbr(planStrategy.getPlanStrategyId(),lvl3.getLvl3Nbr())).thenReturn(java.util.Optional.of(strategyMerchCatgFixtures2));
        //Act
        
        List<StrategyMerchCatgFixture> strategyMerchCatgFixtures = spMerchMethFixtureMapper.updateFixtureMerchMethod(lvl3, planStrategy.getPlanStrategyId());
        // Assert
        assertNotNull(strategyMerchCatgFixtures);
        assertEquals(1, strategyMerchCatgFixtures.size());

        assertEquals(Integer.valueOf(1), strategyMerchCatgFixtures.stream().findFirst()
                .map(strategyMerchCatgFixture -> strategyMerchCatgFixture.getStrategySubCatgFixtures().size()).orElse(null));
    }

}
