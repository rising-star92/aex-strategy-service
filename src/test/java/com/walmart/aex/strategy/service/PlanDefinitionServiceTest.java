package com.walmart.aex.strategy.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.strategy.dto.PlanDTO.*;
import com.walmart.aex.strategy.dto.PlanStrategyListenerResponse;
import com.walmart.aex.strategy.entity.PlanStrategy;
import com.walmart.aex.strategy.entity.PlanStrategyId;
import com.walmart.aex.strategy.properties.AppProperties;
import com.walmart.aex.strategy.repository.CategoryLinePlanRepository;
import com.walmart.aex.strategy.repository.PlanStrategyRepository;
import com.walmart.aex.strategy.repository.SubcategoryLinePlanRepository;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.powermock.api.mockito.PowerMockito;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static com.walmart.aex.strategy.util.CommonUtil.STRATEGY_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class PlanDefinitionServiceTest {
    @InjectMocks
    private PlanDefinitionService planDefinitionService;

    @Mock AttributeHelperService attributeHelperService;
    @Mock
    private RestTemplate restTemplate;

    @Mock
    private PlanStrategyRepository planStrategyRepository;

    @Mock
    private CategoryLinePlanRepository categoryLinePlanRepository;

    @Mock
    private SubcategoryLinePlanRepository subcategoryLinePlanRepository;

    @Spy
    private ObjectMapper objectMapper;

    @Test
    void testRetrieveValuesByHierarchy() throws IOException {
        AppProperties appProperties = PowerMockito.mock(AppProperties.class);
        PowerMockito.when(appProperties.getAHSAttributeTypes()).thenReturn(Arrays.asList("colorFamily","fabricConstruction"));
        ReflectionTestUtils.setField(planDefinitionService, "appProperties", appProperties);

        Set<String> colorFamily = new HashSet<>();
        colorFamily.add("white");
        colorFamily.add("black");
        colorFamily.add("blue");

        Set<String> fabricConstruction = new HashSet<>();
        fabricConstruction.add("Knit");
        fabricConstruction.add("Woven");

        Mockito.when(attributeHelperService.retrieveValuesByHierarchy(eq("colorFamily"),anyInt(),anyInt(),anyString())).thenReturn(colorFamily);
        Mockito.when(attributeHelperService.retrieveValuesByHierarchy(eq("fabricConstruction"),anyInt(),anyInt(),anyString())).thenReturn(fabricConstruction);

        String result = planDefinitionService.fetchAttributeValues(34, 1056198, "S1-FYE24");
        Assertions.assertTrue(result.contains("Woven"));
        Assertions.assertTrue(result.contains("Knit"));
        Assertions.assertTrue(result.contains("white"));
        Assertions.assertTrue(result.contains("black"));
        Assertions.assertTrue(result.contains("blue"));

    }

    @Test
    void testUpdatePlanStrategy(){

        PlanStrategyId planStrategyId = PlanStrategyId.builder()
                .planId(376L)
                .strategyId(STRATEGY_ID)
                .build();

        when(planStrategyRepository.findById(any(PlanStrategyId.class)))
                .thenReturn(Optional.ofNullable(getPlanStrategy()));
        when(categoryLinePlanRepository.getCategories(anyLong(),anyLong(),anyInt(),anyInt(),anyInt()))
                .thenReturn(Arrays.asList(new Integer[]{22}));
        when(subcategoryLinePlanRepository.getSubCategoryNbr(anyLong(),anyLong(),anyInt(),anyInt(),anyInt(),anyInt()))
                .thenReturn(Arrays.asList(new Integer[]{345,1234}));

        doNothing().when(subcategoryLinePlanRepository).deleteById(any());
        PlanDefMessageDTO requestParent = new PlanDefMessageDTO();
        requestParent.setPayload(updateCategoryRequest());
        PlanStrategyListenerResponse response =  planDefinitionService.updatePlanStrategy(requestParent);

        assertEquals("Success", response.getStatus());

    }

    private PlanDTO updateCategoryRequest(){
        PlanDTO planDTO = new PlanDTO();

        MerchantDTO merchantDetails = new MerchantDTO();

        merchantDetails.setMerchantAlias("a0d06uh");
        merchantDetails.setFirstName("Naga");
        merchantDetails.setLastName("Devisetty");
        merchantDetails.setMerchantEmail("a0d06uh@homeoffice.wal-mart.com");
        merchantDetails.setDomain("homeoffice");

        List<MerchantDTO> merchantList = new ArrayList<>();
        merchantList.add(merchantDetails);

        Lvl4DTO lvl4DTO = new Lvl4DTO();
        lvl4DTO.setLvl4Nbr(345);
        lvl4DTO.setLvl4GenDesc1("Test");

        List<Lvl4DTO> lvl4List = new ArrayList<>();
        lvl4List.add(lvl4DTO);

        Lvl3DTO lvl3DTO = new Lvl3DTO();
        lvl3DTO.setLvl3Nbr(22);
        lvl3DTO.setLvl3GenDesc1("test");
        lvl3DTO.setLvl4(lvl4List);

        List<Lvl3DTO> lvl3List = new ArrayList<>();
        lvl3List.add(lvl3DTO);

        Lvl2DTO lvl2DTO = new Lvl2DTO();
        lvl2DTO.setLvl2Nbr(6419);
        lvl2DTO.setLvl3(lvl3List);

        List<Lvl2DTO> lvl2DTOList = new ArrayList<>();
        lvl2DTOList.add(lvl2DTO);

        Lvl1DTO lvl1DTO = new Lvl1DTO();
        lvl1DTO.setLvl1Nbr(34);
        lvl1DTO.setLvl1GenDesc1("");
        lvl1DTO.setLvl2(lvl2DTOList);

        List<Lvl1DTO> lvl1List = new ArrayList<>();
        lvl1List.add(lvl1DTO);

        PlanHierarchyDTO planHierarchy = new PlanHierarchyDTO();
        planHierarchy.setLvl0Nbr(50000);
        planHierarchy.setLvl0GenDesc1("Apparel");
        planHierarchy.setLvl1(lvl1List);

        planDTO.setPlanId(376L);
        planDTO.setPlanDesc("S2 - FYE 2024");
        planDTO.setFiscalYear(2024);
        planDTO.setFiscalYearDesc("FYE 2024");
        planDTO.setMerchant(merchantList);
        planDTO.setPlanHierarchy(planHierarchy);


        return planDTO;
    }

    private PlanStrategyId getPlanStrategyId(){
        PlanStrategyId planStrategyId = new PlanStrategyId();
        planStrategyId.setPlanId(376L);
        planStrategyId.setStrategyId(2L);

        return planStrategyId;
    }

    private PlanStrategy getPlanStrategy(){
        PlanStrategy planStrategy = new PlanStrategy();
        planStrategy.setPlanStrategyId(getPlanStrategyId());
       
        return planStrategy;
    }

}
