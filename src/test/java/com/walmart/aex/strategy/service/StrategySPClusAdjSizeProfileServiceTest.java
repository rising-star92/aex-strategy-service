package com.walmart.aex.strategy.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.strategy.dto.SpreadSizeProfileResponse;
import com.walmart.aex.strategy.dto.request.SizeProfileDTO;
import com.walmart.aex.strategy.dto.request.SpreadSizeProfileRequest;
import com.walmart.aex.strategy.entity.StrategyCcSPCluster;
import com.walmart.aex.strategy.properties.AppProperties;
import com.walmart.aex.strategy.properties.FeatureConfigProperties;
import com.walmart.aex.strategy.repository.StratCcSPClusRepository;
import com.walmart.aex.strategy.repository.StratFineLineSPClusRepository;
import com.walmart.aex.strategy.repository.StratStyleSPClusRepository;
import com.walmart.aex.strategy.repository.StrategyGroupRepository;
import com.walmart.aex.strategy.util.GetSizeSPClusterObjUtil;
import graphql.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class StrategySPClusAdjSizeProfileServiceTest {

    @InjectMocks
    private StrategySPClusAdjSizeProfileService strategySPClusAdjSizeProfileService;


    @Mock
    AdjustedSizeProfileUpdateMapper adjustedSizeProfileUpdateMapper;

    @Mock
    StratFineLineSPClusRepository stratFineLineSpRep;

    @Mock
    StratStyleSPClusRepository stratStyleSPClusRepo;

    @Mock
    StratCcSPClusRepository stratCcSPClusRepository;

    @Mock
    StrategyGroupRepository strategyGroupRepository;

    @Mock
    private SPSizeProfileAdjustmentService spSizeProfileAdjustmentService;

    @Mock
    StratCcSPClusRepository stratCcRepo;

    @Mock
    private SizeAndPackService sizeAndPackService ;

    @Mock
    private StrategySPClusterMapper strategySPClusterMapper;

    @Captor
    ArgumentCaptor<List<StrategyCcSPCluster>> argumentCaptor;
    @Mock
    private AppProperties appProperties;

    @Mock
    private FeatureConfigProperties featureConfigProperties;

    @BeforeEach
    public void init(){
        MockitoAnnotations.initMocks(this);
    }
    private static final ObjectMapper objectMapper = new ObjectMapper();
    @Test
    void updateCustomerChoicesAdjSizeProfileActiveSpreadIndicatorClusterIdNonZeroTest(){
        SpreadSizeProfileRequest request = new SpreadSizeProfileRequest();
        request.setChannel("Store");
        request.setPlanId(Long.valueOf(1));
        final int FINAL_SPREAD_INDICATOR = 0;

        List<StrategyCcSPCluster> ccSPClusterList = new ArrayList<>();
        StrategyCcSPCluster strategyCcSPCluster = GetSizeSPClusterObjUtil.getStrategyCustomerChoiceSPCluster();
        strategyCcSPCluster.setSizeProfileObj("[{\"ahsSizeId\":2627, \"sizeDesc\":\"0X\", \"sizeProfilePrcnt\":null, \"adjustedSizeProfile\":null, \"isEligible\":0},\n" +
                " {\"ahsSizeId\":2628, \"sizeDesc\":\"1X\", \"sizeProfilePrcnt\":null, \"adjustedSizeProfile\":null, \"isEligible\":0}]");
        strategyCcSPCluster.setAnalyticsSPPercent(20.0f);
        strategyCcSPCluster.setMerchantOverrideSPPercent(30.0f);
        strategyCcSPCluster.setCalcSpSpreadInd(0);
        ccSPClusterList.add(strategyCcSPCluster);
        Mockito.when(appProperties.getSPSpreadFeatureFlag()).thenReturn("true");
        Mockito.when(stratCcSPClusRepository.getStrategyCcAllSPClusterDataWithActiveSpreadInd(request.getPlanId(),
                 1,
                 1)).thenReturn(ccSPClusterList);
        Mockito.when(stratCcRepo.saveAll(ccSPClusterList)).thenReturn(ccSPClusterList);
        strategySPClusAdjSizeProfileService.updateCustomerChoicesAdjSizeProfileActiveSpreadIndicator(request);
        Mockito.verify(stratCcRepo, Mockito.times(1)).saveAll(Mockito.any());
        Mockito.verify(stratCcRepo).saveAll(argumentCaptor.capture());
        List<StrategyCcSPCluster> captorVals = argumentCaptor.getValue();
        Assert.assertNotNull(captorVals);
        Assert.assertTrue(FINAL_SPREAD_INDICATOR == captorVals.get(0).getCalcSpSpreadInd());
    }

    @Test
    void updateCustomerChoicesAdjSizeProfileTest() throws JsonProcessingException {
        SpreadSizeProfileRequest request = new SpreadSizeProfileRequest();
        request.setChannel("Store");
        request.setPlanId(346L);
        final int FINAL_SPREAD_INDICATOR = 0;

        List<StrategyCcSPCluster> ccSPClusterList = new ArrayList<>();
        StrategyCcSPCluster strategyCcSPCluster1 = GetSizeSPClusterObjUtil.getStrategyCustomerChoiceSPCluster();
        strategyCcSPCluster1.setCalcSpSpreadInd(1);
        strategyCcSPCluster1.setSizeProfileObj("[{\"ahsSizeId\":229, \"sizeDesc\":\"0X\", \"sizeProfilePrcnt\":null, \"adjustedSizeProfile\":null, \"isEligible\":1},\n" +
                " {\"ahsSizeId\":230, \"sizeDesc\":\"1X\", \"sizeProfilePrcnt\":null, \"adjustedSizeProfile\":null, \"isEligible\":1}" +
                "{\"ahsSizeId\":231, \"sizeDesc\":\"2X\", \"sizeProfilePrcnt\":null, \"adjustedSizeProfile\":null, \"isEligible\":1}" +
                "{\"ahsSizeId\":232, \"sizeDesc\":\"3X\", \"sizeProfilePrcnt\":null, \"adjustedSizeProfile\":null, \"isEligible\":1}" +
                "{\"ahsSizeId\":233, \"sizeDesc\":\"4X\", \"sizeProfilePrcnt\":null, \"adjustedSizeProfile\":null, \"isEligible\":0}]");

        StrategyCcSPCluster strategyCcSPCluster2 = GetSizeSPClusterObjUtil.getStrategyCustomerChoiceSPCluster();
        strategyCcSPCluster2.getStrategyCcSPClusId().getStrategyStyleSPClusId().getStrategyFinelineSPClusId().getStrategySubCatgSPClusId().getStrategyMerchCatgSPClusId().getPlanClusterStrategyId().setAnalyticsClusterId(1);
        strategyCcSPCluster2.setSizeProfileObj("[{\"ahsSizeId\":229, \"sizeDesc\":\"0X\", \"sizeProfilePrcnt\":null, \"adjustedSizeProfile\":null, \"isEligible\":0},\n" +
                " {\"ahsSizeId\":230, \"sizeDesc\":\"1X\", \"sizeProfilePrcnt\":10, \"adjustedSizeProfile\":10, \"isEligible\":0}" +
                "{\"ahsSizeId\":231, \"sizeDesc\":\"2X\", \"sizeProfilePrcnt\":30, \"adjustedSizeProfile\":30, \"isEligible\":0}" +
                "{\"ahsSizeId\":232, \"sizeDesc\":\"3X\", \"sizeProfilePrcnt\":20, \"adjustedSizeProfile\":20, \"isEligible\":0}" +
                "{\"ahsSizeId\":233, \"sizeDesc\":\"4X\", \"sizeProfilePrcnt\":10, \"adjustedSizeProfile\":10, \"isEligible\":0}]");
        ccSPClusterList.add(strategyCcSPCluster1);
        ccSPClusterList.add(strategyCcSPCluster2);
        Set<Integer> ahsIdSet = new HashSet<>(Arrays.asList(229, 230, 231,232));
        Mockito.when(appProperties.getSPSpreadFeatureFlag()).thenReturn("true");
        Mockito.when(featureConfigProperties.getTotalSizePercentFeature()).thenReturn(true);
        Mockito.when(sizeAndPackService.getEligibleSizeIds(strategyCcSPCluster1.getSizeProfileObj())).thenReturn(ahsIdSet);
        List<SizeProfileDTO> sizeProfileDTOList = Arrays.asList(getSizeProfileObj(229, "0X", null, null, 0)
                , getSizeProfileObj(230, "1X", 10.00, 10.00, 0)
                , getSizeProfileObj(231, "2X", 30.00, 30.00, 0)
                , getSizeProfileObj(232, "3X", 20.00, 20.00, 0)
                , getSizeProfileObj(233, "4X", 10.00, 10.00, 0));
        List<SizeProfileDTO> sizeProfileDTOListUpdated = Arrays.asList(getSizeProfileObj(229, "0X", null, 5.00, 0)
                , getSizeProfileObj(230, "1X", 10.00, 15.83, 0)
                , getSizeProfileObj(231, "2X", 30.00, 47.50, 0)
                , getSizeProfileObj(232, "3X", 20.00, 31.67, 0)
                , getSizeProfileObj(233, "4X", 10.00, 10.00, 0));
        Mockito.when(sizeAndPackService.safeReadSizeObject(strategyCcSPCluster2.getSizeProfileObj())).thenReturn(sizeProfileDTOList);
        Mockito.when(spSizeProfileAdjustmentService.processSizeProfileAdjustment(sizeProfileDTOList,ahsIdSet)).thenReturn(sizeProfileDTOListUpdated);
        Mockito.when(stratCcSPClusRepository.getStrategyCcAllSPClusterDataWithActiveSpreadInd(request.getPlanId(),
                1,
                1)).thenReturn(ccSPClusterList);
        Mockito.when(stratCcRepo.saveAll(ccSPClusterList)).thenReturn(ccSPClusterList);
        Mockito.when(sizeAndPackService.getTotalSizeProfilePct(Arrays.toString(sizeProfileDTOListUpdated.toArray()), null)).thenReturn(-1.0);
        SpreadSizeProfileResponse response = strategySPClusAdjSizeProfileService.updateCustomerChoicesAdjSizeProfileActiveSpreadIndicator(request);
        //verify
        Mockito.verify(stratCcRepo).saveAll(argumentCaptor.capture());
        List<StrategyCcSPCluster> captorVals = argumentCaptor.getValue();
        for(StrategyCcSPCluster o:captorVals){
            if(o.getStrategyCcSPClusId().getStrategyStyleSPClusId().getStrategyFinelineSPClusId().getStrategySubCatgSPClusId().getStrategyMerchCatgSPClusId().getPlanClusterStrategyId().getAnalyticsClusterId()!=0){
                List<SizeProfileDTO> sizeObj = Arrays.asList(objectMapper.readValue(o.getSizeProfileObj(), SizeProfileDTO[].class));
                double sum = sizeObj.stream().filter(s -> s.getAdjustedSizeProfile()!=null && ahsIdSet.contains(s.getAhsSizeId())).mapToDouble(SizeProfileDTO::getAdjustedSizeProfile).sum();
                assertEquals(100, sum);
                assertEquals(-1.0, o.getTotalSizeProfilePct());
            }
        }
        Assert.assertNotNull(captorVals);
        Assert.assertTrue(FINAL_SPREAD_INDICATOR == captorVals.get(0).getCalcSpSpreadInd());
        Assert.assertTrue(response.getStatus().equalsIgnoreCase("Success"));
    }

    @Test
    void updateEmptySizeAssociationTest() throws JsonProcessingException {
        SpreadSizeProfileRequest request = new SpreadSizeProfileRequest();
        request.setChannel("Store");
        request.setPlanId(346L);
        final int FINAL_SPREAD_INDICATOR = 0;

        List<StrategyCcSPCluster> ccSPClusterList = new ArrayList<>();
        StrategyCcSPCluster strategyCcSPCluster1 = GetSizeSPClusterObjUtil.getStrategyCustomerChoiceSPCluster();
        strategyCcSPCluster1.setCalcSpSpreadInd(1);
        strategyCcSPCluster1.setSizeProfileObj("[{\"ahsSizeId\":229, \"sizeDesc\":\"0X\", \"sizeProfilePrcnt\":null, \"adjustedSizeProfile\":null, \"isEligible\":0},\n" +
                " {\"ahsSizeId\":230, \"sizeDesc\":\"1X\", \"sizeProfilePrcnt\":null, \"adjustedSizeProfile\":null, \"isEligible\":0}" +
                "{\"ahsSizeId\":231, \"sizeDesc\":\"2X\", \"sizeProfilePrcnt\":null, \"adjustedSizeProfile\":null, \"isEligible\":0}" +
                "{\"ahsSizeId\":232, \"sizeDesc\":\"3X\", \"sizeProfilePrcnt\":null, \"adjustedSizeProfile\":null, \"isEligible\":0}" +
                "{\"ahsSizeId\":233, \"sizeDesc\":\"4X\", \"sizeProfilePrcnt\":null, \"adjustedSizeProfile\":null, \"isEligible\":0}]");

        StrategyCcSPCluster strategyCcSPCluster2 = GetSizeSPClusterObjUtil.getStrategyCustomerChoiceSPCluster();
        strategyCcSPCluster2.getStrategyCcSPClusId().getStrategyStyleSPClusId().getStrategyFinelineSPClusId().getStrategySubCatgSPClusId().getStrategyMerchCatgSPClusId().getPlanClusterStrategyId().setAnalyticsClusterId(1);
        strategyCcSPCluster2.setSizeProfileObj("[{\"ahsSizeId\":229, \"sizeDesc\":\"0X\", \"sizeProfilePrcnt\":null, \"adjustedSizeProfile\":null, \"isEligible\":0},\n" +
                " {\"ahsSizeId\":230, \"sizeDesc\":\"1X\", \"sizeProfilePrcnt\":10, \"adjustedSizeProfile\":10, \"isEligible\":0}" +
                "{\"ahsSizeId\":231, \"sizeDesc\":\"2X\", \"sizeProfilePrcnt\":30, \"adjustedSizeProfile\":30, \"isEligible\":0}" +
                "{\"ahsSizeId\":232, \"sizeDesc\":\"3X\", \"sizeProfilePrcnt\":20, \"adjustedSizeProfile\":20, \"isEligible\":0}" +
                "{\"ahsSizeId\":233, \"sizeDesc\":\"4X\", \"sizeProfilePrcnt\":10, \"adjustedSizeProfile\":10, \"isEligible\":0}]");
        ccSPClusterList.add(strategyCcSPCluster1);
        ccSPClusterList.add(strategyCcSPCluster2);
        Mockito.when(appProperties.getSPSpreadFeatureFlag()).thenReturn("true");
        Mockito.when(sizeAndPackService.getEligibleSizeIds(strategyCcSPCluster1.getSizeProfileObj())).thenReturn(Collections.emptySet());
        Mockito.when(stratCcSPClusRepository.getStrategyCcAllSPClusterDataWithActiveSpreadInd(request.getPlanId(),
                1,
                1)).thenReturn(ccSPClusterList);
        Mockito.when(stratCcRepo.saveAll(ccSPClusterList)).thenReturn(ccSPClusterList);
        SpreadSizeProfileResponse response = strategySPClusAdjSizeProfileService.updateCustomerChoicesAdjSizeProfileActiveSpreadIndicator(request);
        //verify
        Mockito.verify(stratCcRepo).saveAll(argumentCaptor.capture());
        List<StrategyCcSPCluster> captorVals = argumentCaptor.getValue();
        for(StrategyCcSPCluster o:captorVals){
            if(o.getStrategyCcSPClusId().getStrategyStyleSPClusId().getStrategyFinelineSPClusId().getStrategySubCatgSPClusId().getStrategyMerchCatgSPClusId().getPlanClusterStrategyId().getAnalyticsClusterId()!=0){
                assertEquals(-1.0, o.getTotalSizeProfilePct());
            }
        }
        Assert.assertNotNull(captorVals);
        Assert.assertTrue(FINAL_SPREAD_INDICATOR == captorVals.get(0).getCalcSpSpreadInd());
        Assert.assertTrue(response.getStatus().equalsIgnoreCase("Success"));
    }

    @Test
    void findIfAllEligibleSizeIdsHaveNoRecommendationsTest_WhenBothIsEligibleAndRecommendationIsPresent() throws JsonProcessingException {
        List<SizeProfileDTO> sizeProfileDTOs = new ArrayList<>();
        sizeProfileDTOs.add(getSizeProfileObj(2627,"0X",10.00,null,1));
        sizeProfileDTOs.add(getSizeProfileObj(2628,"1X",20.00,null,1));
        Set<Integer> eligibleAHSIds = new HashSet<>();
        Integer ahsId1 = 2627;
        Integer ahsId2 = 2628;
        eligibleAHSIds.add(ahsId1);
        eligibleAHSIds.add(ahsId2);
        boolean isAllEligibleSizeMissingRecommendation =  strategySPClusAdjSizeProfileService.findIfAllEligibleSizeIdsHaveNoRecommendations(sizeProfileDTOs,eligibleAHSIds);
        Assertions.assertFalse(isAllEligibleSizeMissingRecommendation);
    }

    @Test
    void findIfAllEligibleSizeIdsHaveNoRecommendationsTest_WhenOneEligibleSizeHasRecommendation() {
        List<SizeProfileDTO> sizeProfileDTOs = new ArrayList<>();
        sizeProfileDTOs.add(getSizeProfileObj(2627,"0X",10.00,null,1));
        sizeProfileDTOs.add(getSizeProfileObj(2628,"1X",null,null,1));
        Set<Integer> eligibleAHSIds = new HashSet<>();
        Integer ahsId1 = 2627;
        Integer ahsId2 = 2628;
        eligibleAHSIds.add(ahsId1);
        eligibleAHSIds.add(ahsId2);
        boolean isAllEligibleSizeMissingRecommendation =  strategySPClusAdjSizeProfileService.findIfAllEligibleSizeIdsHaveNoRecommendations(sizeProfileDTOs,eligibleAHSIds);
        Assertions.assertFalse(isAllEligibleSizeMissingRecommendation);
    }

    @Test
    void findIfAllEligibleSizeIdsHaveNoRecommendationsTest_WhenSizeAssociationIsDoneButNoRecommendation(){
        List<SizeProfileDTO> sizeProfileDTOs = new ArrayList<>();
        sizeProfileDTOs.add(getSizeProfileObj(2627,"0X",null,null,1));
        sizeProfileDTOs.add(getSizeProfileObj(2628,"1X",null,null,1));
        Set<Integer> eligibleAHSIds = new HashSet<>();
        Integer ahsId1 = 2627;
        Integer ahsId2 = 2628;
        eligibleAHSIds.add(ahsId1);
        eligibleAHSIds.add(ahsId2);
        boolean isAllEligibleSizeMissingRecommendation =  strategySPClusAdjSizeProfileService.findIfAllEligibleSizeIdsHaveNoRecommendations(sizeProfileDTOs,eligibleAHSIds);
        Assertions.assertTrue(isAllEligibleSizeMissingRecommendation);
    }

    @Test
    void findIfAllEligibleSizeIdsHaveNoRecommendationsTest_WhenSizeAssociationIsDoneButMissAllRecommendationButUserOverrideIsPresent() throws JsonProcessingException {
        List<SizeProfileDTO> sizeProfileDTOs = new ArrayList<>();
        sizeProfileDTOs.add(getSizeProfileObj(2627,"0X",null,10.00,1));
        sizeProfileDTOs.add(getSizeProfileObj(2628,"1X",null,2.00,1));
        Set<Integer> eligibleAHSIds = new HashSet<>();
        Integer ahsId1 = 2627;
        Integer ahsId2 = 2628;
        eligibleAHSIds.add(ahsId1);
        eligibleAHSIds.add(ahsId2);
        boolean isAllEligibleSizeMissingRecommendation =  strategySPClusAdjSizeProfileService.findIfAllEligibleSizeIdsHaveNoRecommendations(sizeProfileDTOs,eligibleAHSIds);
        Assertions.assertTrue(isAllEligibleSizeMissingRecommendation);
    }

    private SizeProfileDTO getSizeProfileObj(Integer ahsSizeId, String sizeDesc, Double sizeProfilePrcnt, Double sizeAdjProfilePrcnt, Integer isEligible) {
        SizeProfileDTO sizeProfile = new SizeProfileDTO();
        sizeProfile.setAhsSizeId(ahsSizeId);
        sizeProfile.setSizeDesc(sizeDesc);
        sizeProfile.setSizeProfilePrcnt(sizeProfilePrcnt);
        sizeProfile.setAdjustedSizeProfile(sizeAdjProfilePrcnt);
        sizeProfile.setIsEligible(isEligible);
        return sizeProfile;
    }



}
