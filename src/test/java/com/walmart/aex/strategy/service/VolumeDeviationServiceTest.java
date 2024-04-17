package com.walmart.aex.strategy.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.strategy.dto.Finelines;
import com.walmart.aex.strategy.dto.VDRequest;
import com.walmart.aex.strategy.dto.VDResponseDTO;
import com.walmart.aex.strategy.dto.VolumeDeviationRequests;
import com.walmart.aex.strategy.entity.*;
import com.walmart.aex.strategy.properties.FeatureConfigProperties;
import com.walmart.aex.strategy.repository.FpFinelineVDLevelRepository;
import com.walmart.aex.strategy.repository.StrategyFinelineRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class VolumeDeviationServiceTest {

    @InjectMocks
    @Spy
    VolumeDeviationService volumeDeviationService;

    @Mock
    FpFinelineVDLevelRepository finelineVDLevelRepository;

    @Mock
    StrategyFinelineRepository strategyFinelineRepository;

    @Mock
    FeatureConfigProperties featureConfigProperties;

    @Test
    void getVolumeDeviationCategoryTest() throws JsonProcessingException {
        Mockito.when(finelineVDLevelRepository.findByPlan_idAndFineline_nbr(Mockito.anyLong(), Mockito.anyList())).thenReturn(getFpFinelineVDCategory());
        VDResponseDTO vdResponseDTO = volumeDeviationService.getVolumeDeviationCategory(getVDRequest(236, Arrays.asList(5408)), "");
        List<Finelines> finelines = vdResponseDTO.getFinelines();
        assertEquals(1, finelines.size());
        assertEquals("Category", finelines.get(0).getVolumeDeviationLevel());
    }

    @Test
    void getDefaultVolumeDeviationCategoryTest() {
       Mockito.when(featureConfigProperties.getSaveDefaultVolumeDeviation()).thenReturn(false);
        Mockito.when(strategyFinelineRepository.findByPlan_idAndFineline_nbr(Mockito.anyLong(), Mockito.anyList())).thenReturn(getStrategyFineline());
        VDResponseDTO vdResponseDTO = volumeDeviationService.getVolumeDeviationCategory(getVDRequest(236, Arrays.asList(5405)), "");
        List<Finelines> finelines = vdResponseDTO.getFinelines();
        assertEquals(1, finelines.size());
        assertEquals("Fineline", finelines.get(0).getVolumeDeviationLevel());
    }

    private List<FpFinelineVDCategory> getFpFinelineVDCategory() throws JsonProcessingException {
        List<FpFinelineVDCategory> fpFinelineVDCategories = new ArrayList<>();
        String json = "{\"fpFinelineVDCategoryId\":{\"vdLevelCodeId\":{\"vdLevelCode\":3},\"planId\":236,\"finelineNbr\":5408,\"strategyId\":7,\"rptLvl0Nbr\":50000,\"rptLvl1Nbr\":34,\"rptLvl2Nbr\":6420,\"rptLvl3Nbr\":12238,\"rptLvl4Nbr\":31526}}";
        ObjectMapper mapper = new ObjectMapper();
        FpFinelineVDCategory fpFinelineVDCategory = mapper.readValue(json, FpFinelineVDCategory.class);
        fpFinelineVDCategories.add(fpFinelineVDCategory);
        return fpFinelineVDCategories;
    }

    private List<StrategyFineline> getStrategyFineline() {
        StrategyFineline strategyFineline = new StrategyFineline();
        PlanStrategyId planStrategyId = new PlanStrategyId();
        planStrategyId.setPlanId(236L);
        StrategyMerchCatgId strategyMerchCatgId = new StrategyMerchCatgId();
        strategyMerchCatgId.setPlanStrategyId(planStrategyId);
        strategyMerchCatgId.setLvl0Nbr(50000);
        strategyMerchCatgId.setLvl1Nbr(34);
        strategyMerchCatgId.setLvl2Nbr(6420);
        strategyMerchCatgId.setLvl3Nbr(12238);
        StrategySubCatgId strategySubCatgId = new StrategySubCatgId();
        strategySubCatgId.setStrategyMerchCatgId(strategyMerchCatgId);
        strategySubCatgId.setLvl4Nbr(31526);
        StrategyFinelineId strategyFinelineId = new StrategyFinelineId();
        strategyFinelineId.setFinelineNbr(5405);
        strategyFinelineId.setStrategySubCatgId(strategySubCatgId);
        strategyFineline.setStrategyFinelineId(strategyFinelineId);
        return Arrays.asList(strategyFineline);
    }

    private VDRequest getVDRequest(Integer planId, List<Integer> finelines) {
        VDRequest vdRequest = new VDRequest();
        VolumeDeviationRequests volumeDeviationRequests = new VolumeDeviationRequests();
        volumeDeviationRequests.setPlanId(planId);
        volumeDeviationRequests.setFinelineNbr(finelines);
        vdRequest.setVolumeDeviationRequestsList(Arrays.asList(volumeDeviationRequests));
        return vdRequest;
    }
}
