package com.walmart.aex.strategy.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.walmart.aex.strategy.dto.Finelines;
import com.walmart.aex.strategy.dto.request.FPVolumeDeviationUserSelectionRequest;
import com.walmart.aex.strategy.entity.PlanStrategy;
import com.walmart.aex.strategy.entity.VDLevelText;
import com.walmart.aex.strategy.properties.AppProperties;
import com.walmart.aex.strategy.repository.PlanStrategyRepository;
import com.walmart.aex.strategy.repository.StrategyGroupRepository;
import com.walmart.aex.strategy.repository.StratergyFLVGRepository;
import com.walmart.aex.strategy.repository.VDLevelTextRepository;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class FlowPlanServiceTest{

    @InjectMocks
    private FlowPlanService flowPlanService;
    @Mock
    private StratergyFLVGRepository stratergyFLVGRepository;
    @Mock
    private StrategyGroupRepository strategyGroupRepository;
    @Mock
    private VDLevelTextRepository vdLevelTextRepository;
    @Mock
    private PlanStrategyRepository planStrategyRepository;
    @Mock
    private AppProperties appProperties;


    @Test
    void testSaveVolumeDeviationUserSelection() throws JsonProcessingException {
        Mockito.when(appProperties.getVolumeDeviationAnalyticsClusterGroupDesc()).thenReturn("Volume Deviation");
        Mockito.when(strategyGroupRepository.findStratergyId("Volume Deviation")).thenReturn(8l);
        Mockito.when(vdLevelTextRepository.findVdLevelCodeByVdLevelDesc(Mockito.anyString())).thenReturn(java.util.Optional.of(new VDLevelText()));
        String messgae = flowPlanService.saveVolumeDeviationUserSelection(getRequest());
        Assert.assertNotNull(messgae);
    }

    @Test
    void testSaveVolumeDeviationUserSelection_whenStartergy_null() throws JsonProcessingException {
        Mockito.when(appProperties.getVolumeDeviationAnalyticsClusterGroupDesc()).thenReturn("Volume Deviation");
        Mockito.when(strategyGroupRepository.findStratergyId(Mockito.anyString())).thenReturn(null);
        String messgae = flowPlanService.saveVolumeDeviationUserSelection(getRequest());
        Assert.assertEquals(null, messgae);
    }

    @Test
    void testSaveVolumeDeviationUserSelection_whenStartergy_zero() throws JsonProcessingException {
        Mockito.when(appProperties.getVolumeDeviationAnalyticsClusterGroupDesc()).thenReturn("Volume Deviation");
        Mockito.when(strategyGroupRepository.findStratergyId(Mockito.anyString())).thenReturn(0l);
        String messgae = flowPlanService.saveVolumeDeviationUserSelection(getRequest());
        Assert.assertEquals(null, messgae);
    }

    private FPVolumeDeviationUserSelectionRequest getRequest() {
        FPVolumeDeviationUserSelectionRequest fpVolumeDeviationUserSelectionRequest = new FPVolumeDeviationUserSelectionRequest();
        fpVolumeDeviationUserSelectionRequest.setFinelineNbr(1102);
        fpVolumeDeviationUserSelectionRequest.setLvl0Nbr(50000);
        fpVolumeDeviationUserSelectionRequest.setLvl1Nbr(23);
        fpVolumeDeviationUserSelectionRequest.setLvl2Nbr(3670);
        fpVolumeDeviationUserSelectionRequest.setLvl3Nbr(2668);
        fpVolumeDeviationUserSelectionRequest.setLvl4Nbr(5514);
        fpVolumeDeviationUserSelectionRequest.setPlanId(27l);
        fpVolumeDeviationUserSelectionRequest.setVolumeDeviationLevel("fineline");
        return fpVolumeDeviationUserSelectionRequest;
    }

    @Test
    void testSaveDefaultVolumeDeviation() throws JsonProcessingException{
        Mockito.when(strategyGroupRepository.getStrategyIdByStrategyGroupTypeAndPlanId(Mockito.anyInt(),Mockito.anyLong())).thenReturn(11L);
        String message = flowPlanService.saveDefaultVolumeDeviation(createRequest());
        Assert.assertNotNull(message);
    }

    @Test
    void testSaveDefaultVolumeDeviationWhenStrategyIsNotSavedForPlanId() throws JsonProcessingException{
        Mockito.when(strategyGroupRepository.getStrategyIdByStrategyGroupTypeAndPlanId(Mockito.anyInt(),Mockito.anyLong())).thenReturn(null);
        Mockito.when(strategyGroupRepository.getStrategyIdByStrategyGroupType(Mockito.anyLong())).thenReturn(11l);
        String message = flowPlanService.saveDefaultVolumeDeviation(createRequest());
        Assert.assertNotNull(message);
    }

    private List<Finelines> createRequest(){
        List<Finelines> finelinesList = new ArrayList<>();
        finelinesList.add(new Finelines(1102,null,50000,23,3670,2668,5514,27,"Fineline"));
    return finelinesList;
    }


}
