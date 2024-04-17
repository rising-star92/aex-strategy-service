package com.walmart.aex.strategy.service;

import com.walmart.aex.strategy.repository.FiscalWeekRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

import static org.junit.Assert.assertEquals;

@ExtendWith(MockitoExtension.class)
class PlanStrategyClusterEligRankingMapperTest {

    @InjectMocks
    private PlanStrategyClusterEligRankingMapper planStrategyClusterEligRankingMapper;

    @Mock
    private FiscalWeekRepository fiscalWeekRepository;

    @Test
    void testGetSellingWeeks(){
        //Arrange
        String inStoreWeekDesc = "FYE2021WK01";
        String markDownWeekDesc = "FYE2021WK13";
        //Act
        Integer sellingWeeks = planStrategyClusterEligRankingMapper.getAllocationWeeks(inStoreWeekDesc, markDownWeekDesc);
        //Assert
        assertEquals(13, (int) sellingWeeks);

    }

    @Test
    void testGetSellingWeeksFallsDifferentYears(){
        //Arrange
        String inStoreWeekDesc = "FYE2021WK50";
        String markDownWeekDesc = "FYE2022WK01";
        when(fiscalWeekRepository.getMaxWeeksForWmYear(anyInt())).thenReturn(52);

        //Act
        Integer sellingWeeks = planStrategyClusterEligRankingMapper.getAllocationWeeks(inStoreWeekDesc, markDownWeekDesc);

        //Assert
        assertEquals(4, (int) sellingWeeks);

    }
}
