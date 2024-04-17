package com.walmart.aex.strategy.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.walmart.aex.strategy.dto.LinePlanCount;
import com.walmart.aex.strategy.dto.LinePlanStrategyAttributeGrpResponse;
import com.walmart.aex.strategy.dto.request.StrategyType;
import com.walmart.aex.strategy.dto.request.update.*;
import com.walmart.aex.strategy.repository.CategoryLinePlanRepository;
import com.walmart.aex.strategy.repository.SubcategoryLinePlanRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LinePlanUpdateServiceTest {

    @Mock
    private CategoryLinePlanRepository categoryLinePlanRepository;

    @Mock
    private SubcategoryLinePlanRepository subcategoryLinePlanRepository;

    @InjectMocks
    private LinePlanUpdateService linePlanUpdateService;

    @Test
    void testUpdateAttributeGrpWithLvl4()throws JsonProcessingException {
            long planId = 384L;
            int channelId = 1;
            int strategyId = 2;
            int lvl1 = 34;
            int lvl0 = 50000;
            int lvl2 = 1056197;
            int lvl3 = 1056198;
            int lvl4 = 1056283;

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

            when(subcategoryLinePlanRepository.getSubcategoryAttributeGrpStrategy(planId, channelId, strategyId,lvl3,lvl4,lvl2,lvl1,lvl0)).thenReturn(subcategory);


            LinePlanStrategyAttributeGrpResponse response = linePlanUpdateService.updateAttributeGroupCounts(attributeGrpRequestWithlvl4());

            assertEquals(384, response.getPlanId());

    }

    @Test
    void testAttributeGrpwithLvl3()throws JsonProcessingException{
        long planId = 384L;
        int channelId = 1;
        int strategyId = 2;
        int lvl1 = 34;
        int lvl0 = 50000;
        int lvl2 = 1056197;
        int lvl3 = 1056198;
        int lvl4 = 1056283;

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

         when(categoryLinePlanRepository.getCategoryAttributeGrpStrategy(planId, channelId, strategyId,lvl3,lvl2, lvl1,lvl0)).thenReturn(newPlan);

        LinePlanStrategyAttributeGrpResponse response = linePlanUpdateService.updateAttributeGroupCounts(attributeRequestWithlvl3());

        assertEquals(384, response.getPlanId());

    }

    private LinePlanStrategyUpdateAttributeRequest  attributeGrpRequestWithlvl4(){

        LinePlanStrategyUpdateAttributeRequest request = new LinePlanStrategyUpdateAttributeRequest();
        StrategyType strategyType = new StrategyType();

        strategyType.setStrategyId(2l);
        strategyType.setStrategyDesc("Plan Definition");


        request.setPlanId(384L);
        request.setPlanDesc("S1 - FYE 2024");
        request.setLvl0Nbr(50000);
        request.setChannel("Store");
        request.setStrategyType(strategyType);

        AttributeFields attributefields = new AttributeFields();
        attributefields.setAttribute("finelineAttribute");
        attributefields.setAttributeGroupName("priceTier");
        attributefields.setName("better");
        attributefields.setCount(20);


        Lvl14AttributeGrpInput lvl4AttributeGrpInput = new Lvl14AttributeGrpInput();
        lvl4AttributeGrpInput.setLvl4Nbr(1056283);
        lvl4AttributeGrpInput.setUpdatedFields(Arrays.asList(attributefields));

        Lvl13AttributeGrpInput lvl3AttributeGrpInput = new Lvl13AttributeGrpInput();
        lvl3AttributeGrpInput.setLvl3Nbr(1056198);
        lvl3AttributeGrpInput.setLvl4List(Arrays.asList(lvl4AttributeGrpInput));

        Lvl12AttributeGrpInput lvl2AttributeGrpInput = new Lvl12AttributeGrpInput();
        lvl2AttributeGrpInput.setLvl2Nbr(1056197);
        lvl2AttributeGrpInput.setLvl3List(Arrays.asList(lvl3AttributeGrpInput));

        Lvl1AttributeGrpInput lvl1AttributeGrpInput = new Lvl1AttributeGrpInput();
        lvl1AttributeGrpInput.setLvl1Nbr(34);
        lvl1AttributeGrpInput.setLvl2List(Arrays.asList(lvl2AttributeGrpInput));

        request.setLvl1List(Arrays.asList(lvl1AttributeGrpInput));

        return request;
    }

    private LinePlanStrategyUpdateAttributeRequest attributeRequestWithlvl3(){
        {

            LinePlanStrategyUpdateAttributeRequest request = new LinePlanStrategyUpdateAttributeRequest();
            StrategyType strategyType = new StrategyType();

            strategyType.setStrategyId(2l);
            strategyType.setStrategyDesc("Plan Definition");


            request.setPlanId(384L);
            request.setPlanDesc("S1 - FYE 2024");
            request.setLvl0Nbr(50000);
            request.setChannel("Store");
            request.setStrategyType(strategyType);

            AttributeFields attributefields = new AttributeFields();
            attributefields.setAttribute("finelineAttribute");
            attributefields.setAttributeGroupName("priceTier");
            attributefields.setName("better");
            attributefields.setCount(20);


            Lvl13AttributeGrpInput lvl3AttributeGrpInput = new Lvl13AttributeGrpInput();
            lvl3AttributeGrpInput.setLvl3Nbr(1056198);
            lvl3AttributeGrpInput.setUpdatedFields(Arrays.asList(attributefields));

            Lvl12AttributeGrpInput lvl2AttributeGrpInput = new Lvl12AttributeGrpInput();
            lvl2AttributeGrpInput.setLvl2Nbr(1056197);
            lvl2AttributeGrpInput.setLvl3List(Arrays.asList(lvl3AttributeGrpInput));

            Lvl1AttributeGrpInput lvl1AttributeGrpInput = new Lvl1AttributeGrpInput();
            lvl1AttributeGrpInput.setLvl1Nbr(34);
            lvl1AttributeGrpInput.setLvl2List(Arrays.asList(lvl2AttributeGrpInput));

            request.setLvl1List(Arrays.asList(lvl1AttributeGrpInput));

            return request;
            
        }
    }
}
