package com.walmart.aex.strategy.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.walmart.aex.strategy.dto.LinePlanCount;
import com.walmart.aex.strategy.dto.LinePlanStrategyResponse;
import com.walmart.aex.strategy.dto.StrategyCountResponse;
import com.walmart.aex.strategy.dto.request.*;
import com.walmart.aex.strategy.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class LinePlanStrategyServiceTest {
    @InjectMocks
    private LinePlanStrategyService linePlanStrategyService;

    @Mock
    private CategoryLinePlanRepository categoryLinePlanRepository;

    @Mock
    private SubcategoryLinePlanRepository subcategoryLinePlanRepository;

    @Mock
    private LinePlanStrategyRepository linePlanStrategyRepository;


    private LinePlanCount fetchLinePlanCount(Integer type, Integer fiscalYear, Integer finelineCount,
                                             Integer customerChoiceCount) {
        LinePlanCount linePlanCount = new LinePlanCount();
        linePlanCount.setType(type);
        linePlanCount.setPlanId(123l);
        linePlanCount.setLvl0Nbr(50000);
        linePlanCount.setLvl1Nbr(34);
        linePlanCount.setLvl2Nbr(1489);
        linePlanCount.setLvl2Name("Lvl2 Name");
        linePlanCount.setLvl3Nbr(8901);
        linePlanCount.setLvl3Name("Lvl3 Name");
        linePlanCount.setLvl4Nbr(1936);
        linePlanCount.setLvl4Name("Lvl4 Name");
        linePlanCount.setFiscalYear(fiscalYear);
        linePlanCount.setFinelineCount(finelineCount);
        linePlanCount.setCustomerChoiceCount(customerChoiceCount);
        return linePlanCount;
    }

    @Test
    void testFetchAttributeStrategy() throws JsonProcessingException {
        long planId = 384L;
        int channelId = 1;
        int strategyId = 2;

        List<LinePlanCount> categoryAttributesList = new ArrayList<>();
        LinePlanCount newPlan = new LinePlanCount();
        newPlan.setType(2);
        newPlan.setLvl0Nbr(5000);
        newPlan.setLvl1Nbr(34);
        newPlan.setLvl2Nbr(1056197);
        newPlan.setLvl3Nbr(1056198);
        newPlan.setFinelineCount(1);
        newPlan.setCustomerChoiceCount(22);
        String json = "[{\n" +
                "  \"attributeGroupName\": \"newness\",\n" +
                "  \"goal\": {\n" +
                "    \"finelineAttribute\": [\n" +
                "      {\n" +
                "        \"name\": \"new\",\n" +
                "        \"count\": 0,\n" +
                "        \"percentage\": 0\n" +
                "      }\n" +
                "    ],\n" +
                "    \"customerChoiceAttribute\": [\n" +
                "      {\n" +
                "        \"name\": \"new\",\n" +
                "        \"count\": 0,\n" +
                "        \"percentage\": 0\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}]";
        newPlan.setAttributeJson(json);
        categoryAttributesList.add(newPlan);

        List<LinePlanCount> subcategoryAttributesList = new ArrayList<>();
        LinePlanCount subcategory = new LinePlanCount();
        subcategory.setType(2);
        subcategory.setLvl0Nbr(5000);
        subcategory.setLvl1Nbr(34);
        subcategory.setLvl2Nbr(1056197);
        subcategory.setLvl3Nbr(1056198);
        subcategory.setLvl4Nbr(9999);
        subcategory.setFinelineCount(10);
        subcategory.setCustomerChoiceCount(20);
        subcategory.setAttributeJson(json);
        subcategoryAttributesList.add(subcategory);

        when(categoryLinePlanRepository.getCategoryAttributeStrategy(planId, channelId, strategyId)).thenReturn(categoryAttributesList);
        when(subcategoryLinePlanRepository.getSubcategoryAttributeStrategy(planId, channelId, strategyId)).thenReturn(subcategoryAttributesList);


        LinePlanStrategyRequest request = new LinePlanStrategyRequest();
        request.setChannel("Store");
        request.setPlanDesc("FYE - 2024");
        request.setPlanId(planId);
        request.setAttribute("newness");
        StrategyType strategyType = new StrategyType();
        strategyType.setStrategyDesc("Plan description");
        strategyType.setStrategyId(2L);
        request.setStrategyType(strategyType);

        LinePlanStrategyResponse response = linePlanStrategyService.fetchTargetsByAttribute(request);
        assertEquals(384L, response.getPlanId());
        assertEquals(5000, response.getLvl0Nbr());
        assertNotNull(response.getLvl1List());
        assertEquals(1, response.getLvl1List().size());
        assertEquals(34, response.getLvl1List().get(0).getLvl1Nbr());
        assertEquals(1056197, response.getLvl1List().get(0).getLvl2List().get(0).getLvl2Nbr());
        assertEquals(1056198, response.getLvl1List().get(0).getLvl2List().get(0).getLvl3List().get(0).getLvl3Nbr());
        assertEquals(1, response.getLvl1List().get(0).getLvl2List().get(0).getLvl3List().get(0).getStrategy().getLinePlan().getCurrent().getFinelineCount());
        assertEquals(22, response.getLvl1List().get(0).getLvl2List().get(0).getLvl3List().get(0).getStrategy().getLinePlan().getCurrent().getCustomerChoiceCount());
        assertEquals(9999, response.getLvl1List().get(0).getLvl2List().get(0).getLvl3List().get(0).getLvl4List().get(0).getLvl4Nbr());
        assertEquals(10, response.getLvl1List().get(0).getLvl2List().get(0).getLvl3List().get(0).getLvl4List().get(0).getStrategy().getLinePlan().getCurrent().getFinelineCount());
        assertEquals(20, response.getLvl1List().get(0).getLvl2List().get(0).getLvl3List().get(0).getLvl4List().get(0).getStrategy().getLinePlan().getCurrent().getCustomerChoiceCount());

    }

}
