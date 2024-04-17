package com.walmart.aex.strategy.service;

import com.walmart.aex.strategy.dto.FixtureAllocationStrategy;
import com.walmart.aex.strategy.dto.request.*;
import com.walmart.aex.strategy.entity.PlanStrategy;
import com.walmart.aex.strategy.entity.PlanStrategyId;
import com.walmart.aex.strategy.entity.ProductDimensions;
import com.walmart.aex.strategy.entity.StrategyMerchCatg;
import com.walmart.aex.strategy.repository.StrategyFlClusPrgmEligRankingRepository;
import com.walmart.aex.strategy.util.CommonMethods;
import com.walmart.aex.strategy.util.UpdateInStoreMarkDownUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlanStrategyUpdateMapperTest {
    @InjectMocks
    private PlanStrategyUpdateMapper planStrategyUpdateMapper;

    @Mock
    private CommonMethods methods;

    @Mock
    private StrategyFlClusPrgmEligRankingRepository strategyFlClusPrgmEligRankingRepository;

    private static Integer lvl0Nbr = 39107153;

    PlanStrategy strategy;
    Lvl1 lvl1;
    Lvl2 lvl2;
    Lvl3 lvl3;
    FixtureAllocationStrategy allocationStrategy;
    PlanStrategyId planStrategyId;

    @BeforeEach
    public void SetUp(){
        strategy = UpdateInStoreMarkDownUtil.getPlanStrategy();
        lvl1 = UpdateInStoreMarkDownUtil.getLvl1List().stream().findFirst().orElse(null);
        lvl2 = UpdateInStoreMarkDownUtil.getLvl2List().stream().findFirst().orElse(null);
        lvl3 = UpdateInStoreMarkDownUtil.getLvl3List().stream().findFirst().orElse(null);
        allocationStrategy = new FixtureAllocationStrategy();
        planStrategyId = UpdateInStoreMarkDownUtil.getPlanStrategyIdForCreate();
    }

    @Test
    void setStrategyMerchCatgInitialRequest() {

        //Act
        planStrategyUpdateMapper.updateMerchCatgMetrics(strategy.getStrategyMerchCatgs(), lvl3,
                planStrategyId, lvl2.getLvl2Nbr(), lvl1.getLvl1Nbr(), lvl0Nbr,strategy.getPlanClusterStrategies(), allocationStrategy);

        //Assert
        Set<StrategyMerchCatg> strategyMerchCatgs = strategy.getStrategyMerchCatgs();
        assertNotNull(strategyMerchCatgs);
        assertEquals(Integer.valueOf(202313), strategy.getPlanClusterStrategies().stream().findFirst().flatMap(planClusterStrategy -> planClusterStrategy.getStrategyFlClusEligRankings()
                .stream().findFirst().map(strategyFlClusEligRanking -> strategyFlClusEligRanking.getMarkDownYrWk())).orElse(1));
        assertEquals(Integer.valueOf(202314), strategy.getPlanClusterStrategies().stream().findFirst().flatMap(planClusterStrategy -> planClusterStrategy.getStrategyFlClusEligRankings()
                        .stream().findFirst().flatMap(strategyFlClusEligRanking -> strategyFlClusEligRanking.getStrategyStyleCluses().stream().findFirst()
                                .flatMap(strategyStyleClus -> strategyStyleClus.getStrategyCcClusEligRankings().stream().findFirst()
                                        .map(o ->o.getMarkDownYrWk())))).orElse(1));
    }

    @Test
    void testBrandIsGettingAddingToFinelineData(){
        //Arrange

        lvl3.getLvl4List().forEach(lvl4 -> lvl4.getFinelines()
                .forEach(fineline -> UpdateInStoreMarkDownUtil.setFLBrand(fineline)));

        when(methods.getBrandAttributeString(any())).thenReturn("[" +
                "    {" +
                "        \"brandName\": \"General\"," +
                "        \"brandType\": null," +
                "        \"brandId\": null," +
                "        \"brandLabelCode\": null" +
                "    }" +
                "]");

        //Act
        planStrategyUpdateMapper.updateMerchCatgMetrics(strategy.getStrategyMerchCatgs(), lvl3,
                planStrategyId, lvl2.getLvl2Nbr(), lvl1.getLvl1Nbr(), lvl0Nbr,strategy.getPlanClusterStrategies(), allocationStrategy);

        //Assert
        assertNotNull(strategy.getStrategyMerchCatgs().stream().findFirst().flatMap(
                strategyMerchCatg -> strategyMerchCatg.getStrategySubCatgs().stream().findFirst().flatMap(
                        strategySubCatg -> strategySubCatg.getStrategyFinelines().stream().findFirst()
                        )
                ).get().getBrands());
    }

    @Test
    void testProdDimensionGettingAddedToFinelineData(){
        //Arrange
        ProductDimensions productDimensions = new ProductDimensions();
        productDimensions.setProductDimId(123);
        productDimensions.setProductDimDesc("Thin");

        lvl3.getLvl4List().forEach(lvl4 -> lvl4.getFinelines()
                .forEach(fineline -> fineline.setProductDimensions(productDimensions)));

        //Act
        planStrategyUpdateMapper.updateMerchCatgMetrics(strategy.getStrategyMerchCatgs(), lvl3,
                planStrategyId, lvl2.getLvl2Nbr(), lvl1.getLvl1Nbr(), lvl0Nbr,strategy.getPlanClusterStrategies(), allocationStrategy);

        //Assert
        assertNotNull(strategy.getStrategyMerchCatgs().stream().findFirst().flatMap(
                strategyMerchCatg -> strategyMerchCatg.getStrategySubCatgs().stream().findFirst().flatMap(
                        strategySubCatg -> strategySubCatg.getStrategyFinelines().stream().findFirst()
                )
        ).get().getProductDimId());
    }

    @Test
    void testProdDimensionNotAddedIfProdDimensionsNotChanged(){
        //Arrange
        ProductDimensions productDimensions = null;

        lvl3.getLvl4List().forEach(lvl4 -> lvl4.getFinelines()
                .forEach(fineline -> fineline.setProductDimensions(productDimensions)));

        //Act
        planStrategyUpdateMapper.updateMerchCatgMetrics(strategy.getStrategyMerchCatgs(), lvl3,
                planStrategyId, lvl2.getLvl2Nbr(), lvl1.getLvl1Nbr(), lvl0Nbr,strategy.getPlanClusterStrategies(), allocationStrategy);

        //Assert
        assertEquals(null,strategy.getStrategyMerchCatgs().stream().findFirst().flatMap(
                strategyMerchCatg -> strategyMerchCatg.getStrategySubCatgs().stream().findFirst().flatMap(
                        strategySubCatg -> strategySubCatg.getStrategyFinelines().stream().findFirst()
                )
        ).get().getProductDimId());
    }
}
