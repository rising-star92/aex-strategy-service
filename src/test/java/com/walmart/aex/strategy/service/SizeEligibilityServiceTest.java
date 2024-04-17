package com.walmart.aex.strategy.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.lenient;

import java.util.*;

import com.walmart.aex.strategy.dto.SizeEligMapperDTO;
import com.walmart.aex.strategy.dto.assortproduct.RFAFinelineData;
import com.walmart.aex.strategy.entity.StrategyGroup;
import com.walmart.aex.strategy.repository.StratCcSPClusRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.strategy.dto.PlanStrategySPResponse;
import com.walmart.aex.strategy.dto.SizeResponseDTO;
import com.walmart.aex.strategy.dto.assortproduct.RFARequest;
import com.walmart.aex.strategy.dto.assortproduct.RFASpaceResponse;
import com.walmart.aex.strategy.dto.assortproduct.RFAStylesCcData;
import com.walmart.aex.strategy.dto.request.CustomerChoiceSP;
import com.walmart.aex.strategy.dto.request.SizeProfileDTO;
import com.walmart.aex.strategy.dto.request.StrategySP;
import com.walmart.aex.strategy.enums.StratGroupType;
import com.walmart.aex.strategy.exception.StrategyServiceException;
import com.walmart.aex.strategy.repository.StrategyCcRepository;
import com.walmart.aex.strategy.repository.StrategyFinelineRepository;
import com.walmart.aex.strategy.repository.StrategyGroupRepository;
import com.walmart.aex.strategy.util.GetSizeSPClusterObjUtil;

import graphql.Assert;

@ExtendWith(MockitoExtension.class)
class SizeEligibilityServiceTest {
    @InjectMocks
    private SizeEligibilityService sizeEligibilityService;
    @Mock
    private SizeAndPackService sizeAndPackService;

    @Mock
    private StrategyFinelineRepository strategyFinelineRepository;
    @Mock
    private PlanStrategySizeEligMapper planStrategySizeEligMapper;
    @Mock
    private StrategyCcRepository strategyCcRepository;
    @Mock
    private StrategyGroupRepository strategyGroupRepository;
    @Mock
    private AssortProductService assortProductService;
    @Mock
    private StratCcSPClusRepository stratCcSPClusRepository;
    @Spy
    private ObjectMapper objectMapper;

    @Test
    void fetchStylesCCsWithAssociation() {
        long planId = 123L;
        int channelId = 1;
        int finelineNbr = 151;
        long strategyId = 4L;

        Set<Integer> ahsIdSet = new HashSet<Integer>();
        ahsIdSet.add(246);
        PlanStrategySPResponse response = new PlanStrategySPResponse();
        //Arrange
        List<SizeResponseDTO> sizeResponseDTOList = new ArrayList<>();
        sizeResponseDTOList.add(getSizeResponseDTO());

        List<SizeProfileDTO> sizeProfileDTOList = new ArrayList<>();
        sizeProfileDTOList.add(GetSizeSPClusterObjUtil.getSizeProfileDTO());
        Optional<List<StrategyGroup>> strategyGroups = Optional.of(
                List.of(
                        StrategyGroup.builder().strategyId(4L).strategyGroupTypeId(Long.valueOf(StratGroupType.SIZE_PROFILE.getStrategyGroupTypeCode())).build(),
                        StrategyGroup.builder().strategyId(5L).strategyGroupTypeId(Long.valueOf(StratGroupType.PRESENTATION_UNITS.getStrategyGroupTypeCode())).build()
                )
        );
        Mockito.when(strategyGroupRepository.findAllByStrategyGroupTypeIdIn(anyList())).thenReturn(strategyGroups);
        Mockito.when(stratCcSPClusRepository.getMerchMethodAndTotalSizePct(planId, 4L, 5L, channelId)).thenReturn(Collections.emptyList());
        Mockito.when(strategyCcRepository.getCcSizeByFineline(planId,strategyId,finelineNbr,channelId)).thenReturn(sizeResponseDTOList);
        lenient().when(sizeAndPackService.getEligibleSizeIds(getSizeResponseDTO().getCcSizeObj())).thenReturn(ahsIdSet);
        lenient().doNothing().when(planStrategySizeEligMapper).mapPlanStrategyLvl2Sp(SizeEligMapperDTO.builder().sizeResponseDTO(getSizeResponseDTO()).response(response).validationSizeResponseList(Collections.emptyList()).finelineNbr(finelineNbr).catgFlag(null).build());
        try {
            Mockito.when(assortProductService.getRFASpaceDataOutput(getRFARequest(planId, finelineNbr))).thenReturn(getRfaSpaceResponse());
        } catch (StrategyServiceException e) {
            throw new RuntimeException(e);
        }

        //Act
        PlanStrategySPResponse planStrategySPResponse = sizeEligibilityService.fetchStylesCCsWithAssociation(planId,"store",finelineNbr);
        // Assert
        assertEquals(0,planStrategySPResponse.getLvl3List().size());
    }

    @Test
    void fetchStylesCCsWithAssociationWithEmptyStrategyId() {
        long planId = 123L;
        int finelineNbr = 151;

        Mockito.when(strategyGroupRepository.findAllByStrategyGroupTypeIdIn(anyList())).thenReturn(Optional.of(Collections.emptyList()));

        //Act
        PlanStrategySPResponse planStrategySPResponse = sizeEligibilityService.fetchStylesCCsWithAssociation(planId,"store",finelineNbr);
        // Assert
        assertEquals("Failed", planStrategySPResponse.getStatus());
    }

    private static SizeResponseDTO getSizeResponseDTO() {
        Long planId = 123l;
        Integer lvl0Nbr = 39107153;
        Integer lvl1Nbr = 50400;
        Integer lvl2Nbr = 105400;
        Integer lvl3Nbr = 34556;
        Integer lvl4Nbr = 4567;
        Integer fineline1Nbr = 151;

        String style1Nbr = "151_23_01_001";
        String ccId = "151_23_01_001_001";

        SizeResponseDTO sizeResponse = new SizeResponseDTO();
        sizeResponse.setPlanId(planId);
        sizeResponse.setLvl0Nbr(lvl0Nbr);
        sizeResponse.setLvl1Nbr(lvl1Nbr);
        sizeResponse.setLvl2Nbr(lvl2Nbr);
        sizeResponse.setLvl3Nbr(lvl3Nbr);
        sizeResponse.setLvl4Nbr(lvl4Nbr);
        sizeResponse.setFinelineNbr(fineline1Nbr);
        sizeResponse.setStyleNbr(style1Nbr);
        sizeResponse.setCcId(ccId);
        sizeResponse.setCcSizeObj("[{\\\"ahsSizeId\\\":246,\\\"sizeDesc\\\":\\\"S/M\\\",\\\"sizeProfilePrcnt\\\":null,\\\"adjustedSizeProfile\\\":35.0,\\\"isEligible\\\":1,\\\"metrics\\\":null}]");

        return sizeResponse;

    }
    @Test
    void fetchSubCategoriesWithSizeAssociationTest(){
        SizeResponseDTO finelineSizeResponseDTO = new SizeResponseDTO(
                177L,
                11L,
                50000,
                34,
                6419,
                12228,
                "catSizeObj",
                31515,
                "subCatSizeObj",
                5131,
                "[{\"ahsSizeId\":229,\"sizeDesc\":\"S/M\",\"sizeProfilePrcnt\":null,\"adjustedSizeProfile\":null,\"isEligible\":2,\"metrics\":null},\n" +
                        "{\"ahsSizeId\":230,\"sizeDesc\":\"L/XL\",\"sizeProfilePrcnt\":null,\"adjustedSizeProfile\":null,\"isEligible\":1,\"metrics\":null},{\"ahsSizeId\":231,\"sizeDesc\":\"XL\",\"sizeProfilePrcnt\":null,\"adjustedSizeProfile\":null,\"isEligible\":1,\"metrics\":null},{\"ahsSizeId\":234,\"sizeDesc\":\"0X\",\"sizeProfilePrcnt\":null,\"adjustedSizeProfile\":null,\"isEligible\":1,\"metrics\":null},{\"ahsSizeId\":235,\"sizeDesc\":\"1X\",\"sizeProfilePrcnt\":null,\"adjustedSizeProfile\":null,\"isEligible\":0,\"metrics\":null},{\"ahsSizeId\":236,\"sizeDesc\":\"2X\",\"sizeProfilePrcnt\":null,\"adjustedSizeProfile\":null,\"isEligible\":0,\"metrics\":null},{\"ahsSizeId\":237,\"sizeDesc\":\"3X\",\"sizeProfilePrcnt\":null,\"adjustedSizeProfile\":null,\"isEligible\":0,\"metrics\":null},{\"ahsSizeId\":238,\"sizeDesc\":\"4X\",\"sizeProfilePrcnt\":null,\"adjustedSizeProfile\":null,\"isEligible\":0,\"metrics\":null},{\"ahsSizeId\":239,\"sizeDesc\":\"5X\",\"sizeProfilePrcnt\":null,\"adjustedSizeProfile\":null,\"isEligible\":0,\"metrics\":null},{\"ahsSizeId\":240,\"sizeDesc\":\"0XP\",\"sizeProfilePrcnt\":null,\"adjustedSizeProfile\":null,\"isEligible\":0,\"metrics\":null},{\"ahsSizeId\":241,\"sizeDesc\":\"1XP\",\"sizeProfilePrcnt\":null,\"adjustedSizeProfile\":null,\"isEligible\":0,\"metrics\":null},{\"ahsSizeId\":242,\"sizeDesc\":\"2XP\",\"sizeProfilePrcnt\":null,\"adjustedSizeProfile\":null,\"isEligible\":0,\"metrics\":null},{\"ahsSizeId\":243,\"sizeDesc\":\"3XP\",\"sizeProfilePrcnt\":null,\"adjustedSizeProfile\":null,\"isEligible\":0,\"metrics\":null},{\"ahsSizeId\":244,\"sizeDesc\":\"4XP\",\"sizeProfilePrcnt\":null,\"adjustedSizeProfile\":null,\"isEligible\":0,\"metrics\":null},{\"ahsSizeId\":245,\"sizeDesc\":\"5XP\",\"sizeProfilePrcnt\":null,\"adjustedSizeProfile\":null,\"isEligible\":0,\"metrics\":null}]",
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        SizeResponseDTO finelineCcValidationResponseDTO = new SizeResponseDTO(
                5131,
                "style1",
                "cc1",
                1,
                100.00
        );
        Mockito.when(strategyFinelineRepository
                .getCategoriesWithSize(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyInt())).thenReturn(List.of(finelineSizeResponseDTO));
        Optional<List<StrategyGroup>> strategyGroups = Optional.of(
                List.of(
                        StrategyGroup.builder().strategyId(4L).strategyGroupTypeId(Long.valueOf(StratGroupType.SIZE_PROFILE.getStrategyGroupTypeCode())).build(),
                        StrategyGroup.builder().strategyId(5L).strategyGroupTypeId(Long.valueOf(StratGroupType.PRESENTATION_UNITS.getStrategyGroupTypeCode())).build()
                )
        );
        Mockito.when(strategyGroupRepository.findAllByStrategyGroupTypeIdIn(anyList())).thenReturn(strategyGroups);
        Mockito.when(stratCcSPClusRepository.getMerchMethodAndTotalSizePct(177L, 4L, 5L, 1)).thenReturn(List.of(finelineCcValidationResponseDTO));
        try {
            Mockito.when(assortProductService.getRFASpaceDataOutput(getRFARequest(177L, null))).thenReturn(getRfaSpaceFinelineResponse());
        } catch (StrategyServiceException e) {
            throw new RuntimeException(e);
        }

        PlanStrategySPResponse planStrategySPResponse = sizeEligibilityService.fetchSubCategoriesWithSizeAssociation(177L, "store");
        Assert.assertNotNull(planStrategySPResponse);
        assertEquals("Success", planStrategySPResponse.getStatus());
    }

    @Test
    void fetchSubCategoriesWithSizeAssociationOnlineTest(){
        List<SizeResponseDTO> sizeResponseDTOList =  new ArrayList<>();
        SizeResponseDTO sizeResponseDTO = new SizeResponseDTO();
        sizeResponseDTO.setCcSizeObj("12");
        sizeResponseDTO.setCatSizeObj("12");
        sizeResponseDTO.setLvl3Nbr(1);
        sizeResponseDTO.setLvl0Nbr(2);
        sizeResponseDTO.setLvl1Nbr(12);
        sizeResponseDTO.setSubCategorySizeObj("subcategory");
        sizeResponseDTO.setStyleSizeObj("subcategory");
        sizeResponseDTO.setClusterId(13);
        sizeResponseDTO.setCcId("12");
        sizeResponseDTO.setPlanId(11L);
        sizeResponseDTO.setLvl2Nbr(12);
        sizeResponseDTO.setFineLineSizeObj("[{\"ahsSizeId\":229,\"sizeDesc\":\"S/M\",\"sizeProfilePrcnt\":null,\"adjustedSizeProfile\":null,\"isEligible\":2,\"metrics\":null},\n" +
                "{\"ahsSizeId\":230,\"sizeDesc\":\"L/XL\",\"sizeProfilePrcnt\":null,\"adjustedSizeProfile\":null,\"isEligible\":1,\"metrics\":null},{\"ahsSizeId\":231,\"sizeDesc\":\"XL\",\"sizeProfilePrcnt\":null,\"adjustedSizeProfile\":null,\"isEligible\":1,\"metrics\":null},{\"ahsSizeId\":234,\"sizeDesc\":\"0X\",\"sizeProfilePrcnt\":null,\"adjustedSizeProfile\":null,\"isEligible\":1,\"metrics\":null},{\"ahsSizeId\":235,\"sizeDesc\":\"1X\",\"sizeProfilePrcnt\":null,\"adjustedSizeProfile\":null,\"isEligible\":0,\"metrics\":null},{\"ahsSizeId\":236,\"sizeDesc\":\"2X\",\"sizeProfilePrcnt\":null,\"adjustedSizeProfile\":null,\"isEligible\":0,\"metrics\":null},{\"ahsSizeId\":237,\"sizeDesc\":\"3X\",\"sizeProfilePrcnt\":null,\"adjustedSizeProfile\":null,\"isEligible\":0,\"metrics\":null},{\"ahsSizeId\":238,\"sizeDesc\":\"4X\",\"sizeProfilePrcnt\":null,\"adjustedSizeProfile\":null,\"isEligible\":0,\"metrics\":null},{\"ahsSizeId\":239,\"sizeDesc\":\"5X\",\"sizeProfilePrcnt\":null,\"adjustedSizeProfile\":null,\"isEligible\":0,\"metrics\":null},{\"ahsSizeId\":240,\"sizeDesc\":\"0XP\",\"sizeProfilePrcnt\":null,\"adjustedSizeProfile\":null,\"isEligible\":0,\"metrics\":null},{\"ahsSizeId\":241,\"sizeDesc\":\"1XP\",\"sizeProfilePrcnt\":null,\"adjustedSizeProfile\":null,\"isEligible\":0,\"metrics\":null},{\"ahsSizeId\":242,\"sizeDesc\":\"2XP\",\"sizeProfilePrcnt\":null,\"adjustedSizeProfile\":null,\"isEligible\":0,\"metrics\":null},{\"ahsSizeId\":243,\"sizeDesc\":\"3XP\",\"sizeProfilePrcnt\":null,\"adjustedSizeProfile\":null,\"isEligible\":0,\"metrics\":null},{\"ahsSizeId\":244,\"sizeDesc\":\"4XP\",\"sizeProfilePrcnt\":null,\"adjustedSizeProfile\":null,\"isEligible\":0,\"metrics\":null},{\"ahsSizeId\":245,\"sizeDesc\":\"5XP\",\"sizeProfilePrcnt\":null,\"adjustedSizeProfile\":null,\"isEligible\":0,\"metrics\":null}]");

        sizeResponseDTOList.add(sizeResponseDTO);
        Mockito.when(strategyFinelineRepository
                .getCategoriesWithSize(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyInt())).thenReturn(sizeResponseDTOList);
        Optional<List<StrategyGroup>> strategyGroups = Optional.of(
                List.of(
                        StrategyGroup.builder().strategyId(4L).strategyGroupTypeId(Long.valueOf(StratGroupType.SIZE_PROFILE.getStrategyGroupTypeCode())).build(),
                        StrategyGroup.builder().strategyId(5L).strategyGroupTypeId(Long.valueOf(StratGroupType.PRESENTATION_UNITS.getStrategyGroupTypeCode())).build()
                )
        );
        Mockito.when(strategyGroupRepository.findAllByStrategyGroupTypeIdIn(anyList())).thenReturn(strategyGroups);
        Mockito.when(stratCcSPClusRepository.getTotalSizePct(12L, 4L, 2)).thenReturn(Collections.emptyList());

        PlanStrategySPResponse planStrategySPResponse = sizeEligibilityService.fetchSubCategoriesWithSizeAssociation(12L, "online");
        Assert.assertNotNull(planStrategySPResponse);
        assertEquals("Success", planStrategySPResponse.getStatus());
    }

    @Test
    void fetchSubCategoriesWithSizeAssociationTestWithEmptyStrategyId() {
        long planId = 123L;
        int finelineNbr = 151;

        Mockito.when(strategyGroupRepository.findAllByStrategyGroupTypeIdIn(anyList())).thenReturn(Optional.of(Collections.emptyList()));

        //Act
        PlanStrategySPResponse planStrategySPResponse = sizeEligibilityService.fetchSubCategoriesWithSizeAssociation(planId,"store");
        // Assert
        assertEquals("Failed", planStrategySPResponse.getStatus());
    }

    @Test
    void fetchSubCategoriesWithSizeAssociationWithNullHierarchyTest(){
        Mockito.when(strategyFinelineRepository
                .getCategoriesWithSize(177L, 4L, 1)).thenReturn(getFinelineSizeResponse());
        Optional<List<StrategyGroup>> strategyGroups = Optional.of(
                List.of(
                        StrategyGroup.builder().strategyId(4L).strategyGroupTypeId(Long.valueOf(StratGroupType.SIZE_PROFILE.getStrategyGroupTypeCode())).build(),
                        StrategyGroup.builder().strategyId(5L).strategyGroupTypeId(Long.valueOf(StratGroupType.PRESENTATION_UNITS.getStrategyGroupTypeCode())).build()
                )
        );
        Mockito.when(strategyGroupRepository.findAllByStrategyGroupTypeIdIn(anyList())).thenReturn(strategyGroups);
        Mockito.when(stratCcSPClusRepository.getMerchMethodAndTotalSizePct(177L, 4L, 5L, 1)).thenReturn(Collections.emptyList());
        try {
            Mockito.when(assortProductService.getRFASpaceDataOutput(getRFARequest(177L, null))).thenReturn(getRfaSpaceFinelineResponse());
        } catch (StrategyServiceException e) {
            throw new RuntimeException(e);
        }

        PlanStrategySPResponse planStrategySPResponse = sizeEligibilityService.fetchSubCategoriesWithSizeAssociation(177L, "store");
        Assert.assertNotNull(planStrategySPResponse);
        assertEquals("Success", planStrategySPResponse.getStatus());
    }

    //This test is for code coverage for method sizeEligibilityService.getCcByFinelineWithoutSizeAssociation.
    @Test
    void getCcByFinelineWithNoSizesAssociatedTest() {
        long planId = 123l;
        int channelId = 1;
        int finelineNbr = 151;
        long strategyId = 4l;

        Boolean isViewExceptionFlow = true;
        PlanStrategySPResponse response = new PlanStrategySPResponse();

        List<SizeProfileDTO> sizeProfileDTOList = new ArrayList<>();
        sizeProfileDTOList.add(GetSizeSPClusterObjUtil.getSizeProfileDTO());

        Mockito.when(strategyGroupRepository.getStrategyIdBySeasonCd(StratGroupType.SIZE_PROFILE.getStrategyGroupTypeCode(),null, null)).thenReturn(4l);
        Mockito.when(strategyCcRepository.getCcSizeByFineline(planId,strategyId,finelineNbr,channelId)).thenReturn(getSizeResponse());
        try {
            Mockito.when(assortProductService.getRFASpaceDataOutput(getRFARequest(planId, finelineNbr))).thenReturn(getRfaSpaceResponse());
        } catch (StrategyServiceException e) {
            throw new RuntimeException(e);
        }

        lenient().doNothing().when(planStrategySizeEligMapper).mapPlanStrategyLvl2Sp(SizeEligMapperDTO.builder().sizeResponseDTO(getSizeResponseDTO()).response(response).validationSizeResponseList(Collections.emptyList()).finelineNbr(finelineNbr).catgFlag(null).build());

        //Act
        PlanStrategySPResponse planStrategySPResponse = sizeEligibilityService.getCcByFinelineWithoutSizeAssociation(planId,finelineNbr, "store");
        // Assert
        assertEquals(0,planStrategySPResponse.getLvl3List().size());
    }

    //This test is to get unassociated sizes for cc. Since size is not associated to cc, customerChoiceSPS must have cc value.
    @Test
    void getCcWithUnAssociatedSizeDataTest() {
        List<CustomerChoiceSP> customerChoiceSPS = new ArrayList<>();
        customerChoiceSPS.add(getCCUnAssociatedSizeResponseDTO());
        //Act
        customerChoiceSPS = sizeEligibilityService.getCcWithUnAssociatedSizeData(customerChoiceSPS);
        // Assert
        assertEquals(1,customerChoiceSPS.size());
        String ccId = customerChoiceSPS.get(0).getCcId();
        assertEquals("ccId1", ccId, "ccId should match");
    }

    //This test is to get associated sizes for cc. Since size is associated cc, customerChoiceSPS list must be empty.
    @Test
    void getCcWithAssociatedSizeDataTest() {
        List<CustomerChoiceSP> customerChoiceSPS = new ArrayList<>();
        customerChoiceSPS.add(getCCAssociatedSizeResponseDTO());
        //Act
        customerChoiceSPS = sizeEligibilityService.getCcWithUnAssociatedSizeData(customerChoiceSPS);
        // Assert
        assertEquals(0,customerChoiceSPS.size());
    }


    private static List<SizeResponseDTO> getSizeResponse() {
        Long planId = 123L;
        Integer lvl0Nbr = 39107153;
        Integer lvl1Nbr = 50400;
        Integer lvl2Nbr = 105400;
        Integer lvl3Nbr = 34556;
        Integer lvl4Nbr = 4567;
        Integer fineline1Nbr = 151;

        String style1Nbr = "151_23_01_001";
        String ccId = "ccId1";

        SizeResponseDTO sizeResponse = new SizeResponseDTO();
        sizeResponse.setPlanId(planId);
        sizeResponse.setLvl0Nbr(lvl0Nbr);
        sizeResponse.setLvl1Nbr(lvl1Nbr);
        sizeResponse.setLvl2Nbr(lvl2Nbr);
        sizeResponse.setLvl3Nbr(lvl3Nbr);
        sizeResponse.setLvl4Nbr(lvl4Nbr);
        sizeResponse.setFinelineNbr(fineline1Nbr);
        sizeResponse.setStyleNbr(style1Nbr);
        sizeResponse.setCcId(ccId);
        sizeResponse.setCcSizeObj("[{\"ahsSizeId\":246,\"sizeDesc\":\"S/M\",\"sizeProfilePrcnt\":null,\"adjustedSizeProfile\":35.0,\"isEligible\":0,\"metrics\":null}]");

        SizeResponseDTO sizeResponse2 = new SizeResponseDTO();
        sizeResponse2.setPlanId(planId);
        sizeResponse2.setLvl0Nbr(lvl0Nbr);
        sizeResponse2.setLvl1Nbr(lvl1Nbr);
        sizeResponse2.setLvl2Nbr(lvl2Nbr);
        sizeResponse2.setLvl3Nbr(lvl3Nbr);
        sizeResponse2.setLvl4Nbr(lvl4Nbr);
        sizeResponse2.setFinelineNbr(fineline1Nbr);
        sizeResponse2.setStyleNbr("151_23_01_002");
        sizeResponse2.setCcId(ccId);
        sizeResponse2.setCcSizeObj("[{\"ahsSizeId\":246,\"sizeDesc\":\"S/M\",\"sizeProfilePrcnt\":null,\"adjustedSizeProfile\":35.0,\"isEligible\":0,\"metrics\":null}]");

        return List.of(sizeResponse, sizeResponse2);
    }

    private static CustomerChoiceSP getCCUnAssociatedSizeResponseDTO() {
        CustomerChoiceSP customerChoiceSP = new CustomerChoiceSP();
        customerChoiceSP.setCcId("ccId1");

        List<SizeProfileDTO> sizeProfileDTOS = new ArrayList<>();
        SizeProfileDTO sizeProfileDTO1 = new SizeProfileDTO();
        sizeProfileDTO1.setIsEligible(0);
        sizeProfileDTO1.setAhsSizeId(102);
        SizeProfileDTO sizeProfileDTO2 = new SizeProfileDTO();
        sizeProfileDTO2.setIsEligible(0);
        sizeProfileDTO2.setAhsSizeId(103);
        sizeProfileDTOS.add(sizeProfileDTO1);
        sizeProfileDTOS.add(sizeProfileDTO2);
        StrategySP strategySP = new StrategySP();
        strategySP.setSizeProfile(sizeProfileDTOS);
        customerChoiceSP.setStrategy(strategySP);

        return customerChoiceSP;
    }

    private static CustomerChoiceSP getCCAssociatedSizeResponseDTO() {
        CustomerChoiceSP customerChoiceSP = new CustomerChoiceSP();
        customerChoiceSP.setCcId("ccId1");

        List<SizeProfileDTO> sizeProfileDTOS = new ArrayList<>();
        SizeProfileDTO sizeProfileDTO1 = new SizeProfileDTO();
        sizeProfileDTO1.setIsEligible(1);
        sizeProfileDTO1.setAhsSizeId(102);
        SizeProfileDTO sizeProfileDTO2 = new SizeProfileDTO();
        sizeProfileDTO2.setIsEligible(1);
        sizeProfileDTO2.setAhsSizeId(103);
        sizeProfileDTOS.add(sizeProfileDTO1);
        sizeProfileDTOS.add(sizeProfileDTO2);
        StrategySP strategySP = new StrategySP();
        strategySP.setSizeProfile(sizeProfileDTOS);
        customerChoiceSP.setStrategy(strategySP);

        return customerChoiceSP;
    }

    private RFASpaceResponse getRfaSpaceResponse() {
        RFASpaceResponse rfaSpaceResponse = new RFASpaceResponse();
        List<RFAStylesCcData> rfaStylesCcDataList = new ArrayList<>();
        RFAStylesCcData rfaStylesCcData = new RFAStylesCcData();
        rfaStylesCcDataList.add(rfaStylesCcData);
        List<String> ccs = new ArrayList<>();
        ccs.add("ccId1");
        rfaStylesCcData.setFineline_nbr(151);
        rfaStylesCcData.setStyle_nbr("151_23_01_001");
        rfaStylesCcData.setCustomer_choice(ccs);
        rfaSpaceResponse.setRfaStylesCcData(rfaStylesCcDataList);
        return rfaSpaceResponse;
    }

    private RFASpaceResponse getRfaSpaceFinelineResponse() {
        RFASpaceResponse rfaSpaceResponse = new RFASpaceResponse();
        List<RFAFinelineData> rfaFinelineDataList = new ArrayList<>();
        RFAFinelineData rfaFinelineData1 = new RFAFinelineData();
        rfaFinelineData1.setPlan_id_partition(177L);
        rfaFinelineData1.setFineline_nbr(List.of(5131, 5132));
        rfaFinelineData1.setCustomer_choice(List.of("cc1", "cc2", "cc3", "cc4"));
        rfaFinelineData1.setRpt_lvl_0_nbr(50000);
        rfaFinelineData1.setRpt_lvl_1_nbr(34);
        rfaFinelineData1.setRpt_lvl_2_nbr(6419);
        rfaFinelineData1.setRpt_lvl_3_nbr(12228);
        rfaFinelineData1.setRpt_lvl_4_nbr(31515);
        rfaFinelineDataList.add(rfaFinelineData1);

        RFAFinelineData rfaFinelineData2 = new RFAFinelineData();
        rfaFinelineData2.setPlan_id_partition(177L);
        rfaFinelineData2.setFineline_nbr(List.of(3624));
        rfaFinelineData2.setCustomer_choice(null);
        rfaFinelineData2.setRpt_lvl_0_nbr(null);
        rfaFinelineData2.setRpt_lvl_1_nbr(null);
        rfaFinelineData2.setRpt_lvl_2_nbr(null);
        rfaFinelineData2.setRpt_lvl_3_nbr(null);
        rfaFinelineData2.setRpt_lvl_4_nbr(null);
        rfaFinelineDataList.add(rfaFinelineData2);

        rfaSpaceResponse.setRfaFinelineData(rfaFinelineDataList);
        return rfaSpaceResponse;
    }

    private List<SizeResponseDTO> getFinelineSizeResponse() {
        SizeResponseDTO sizeResponseDTO = new SizeResponseDTO();
        sizeResponseDTO.setPlanId(177L);
        sizeResponseDTO.setLvl0Nbr(50000);
        sizeResponseDTO.setLvl1Nbr(34);
        sizeResponseDTO.setLvl2Nbr(6419);
        sizeResponseDTO.setLvl3Nbr(12228);
        sizeResponseDTO.setLvl4Nbr(31515);
        sizeResponseDTO.setFinelineNbr(5131);
        sizeResponseDTO.setCatSizeObj("CategorySizeObj");
        sizeResponseDTO.setSubCategorySizeObj("SubCategorySizeObj");
        sizeResponseDTO.setClusterId(1);
        sizeResponseDTO.setFineLineSizeObj("[{\"ahsSizeId\":229,\"sizeDesc\":\"S/M\",\"sizeProfilePrcnt\":null,\"adjustedSizeProfile\":null,\"isEligible\":2,\"metrics\":null},\n" +
                "{\"ahsSizeId\":230,\"sizeDesc\":\"L/XL\",\"sizeProfilePrcnt\":null,\"adjustedSizeProfile\":null,\"isEligible\":1,\"metrics\":null},\n" +
                "{\"ahsSizeId\":231,\"sizeDesc\":\"XL\",\"sizeProfilePrcnt\":null,\"adjustedSizeProfile\":null,\"isEligible\":1,\"metrics\":null},\n" +
                "{\"ahsSizeId\":234,\"sizeDesc\":\"0X\",\"sizeProfilePrcnt\":null,\"adjustedSizeProfile\":null,\"isEligible\":1,\"metrics\":null},\n" +
                "{\"ahsSizeId\":235,\"sizeDesc\":\"1X\",\"sizeProfilePrcnt\":null,\"adjustedSizeProfile\":null,\"isEligible\":0,\"metrics\":null},\n" +
                "{\"ahsSizeId\":236,\"sizeDesc\":\"2X\",\"sizeProfilePrcnt\":null,\"adjustedSizeProfile\":null,\"isEligible\":0,\"metrics\":null},\n" +
                "{\"ahsSizeId\":237,\"sizeDesc\":\"3X\",\"sizeProfilePrcnt\":null,\"adjustedSizeProfile\":null,\"isEligible\":0,\"metrics\":null},\n" +
                "{\"ahsSizeId\":238,\"sizeDesc\":\"4X\",\"sizeProfilePrcnt\":null,\"adjustedSizeProfile\":null,\"isEligible\":0,\"metrics\":null},\n" +
                "{\"ahsSizeId\":239,\"sizeDesc\":\"5X\",\"sizeProfilePrcnt\":null,\"adjustedSizeProfile\":null,\"isEligible\":0,\"metrics\":null},\n" +
                "{\"ahsSizeId\":240,\"sizeDesc\":\"0XP\",\"sizeProfilePrcnt\":null,\"adjustedSizeProfile\":null,\"isEligible\":0,\"metrics\":null},\n" +
                "{\"ahsSizeId\":241,\"sizeDesc\":\"1XP\",\"sizeProfilePrcnt\":null,\"adjustedSizeProfile\":null,\"isEligible\":0,\"metrics\":null},\n" +
                "{\"ahsSizeId\":242,\"sizeDesc\":\"2XP\",\"sizeProfilePrcnt\":null,\"adjustedSizeProfile\":null,\"isEligible\":0,\"metrics\":null},\n" +
                "{\"ahsSizeId\":243,\"sizeDesc\":\"3XP\",\"sizeProfilePrcnt\":null,\"adjustedSizeProfile\":null,\"isEligible\":0,\"metrics\":null},\n" +
                "{\"ahsSizeId\":244,\"sizeDesc\":\"4XP\",\"sizeProfilePrcnt\":null,\"adjustedSizeProfile\":null,\"isEligible\":0,\"metrics\":null},\n" +
                "{\"ahsSizeId\":245,\"sizeDesc\":\"5XP\",\"sizeProfilePrcnt\":null,\"adjustedSizeProfile\":null,\"isEligible\":0,\"metrics\":null}]");

        SizeResponseDTO sizeResponseDTO2 = new SizeResponseDTO();
        sizeResponseDTO2.setPlanId(177L);
        sizeResponseDTO2.setLvl0Nbr(50000);
        sizeResponseDTO2.setLvl1Nbr(34);
        sizeResponseDTO2.setLvl2Nbr(6419);
        sizeResponseDTO2.setLvl3Nbr(12228);
        sizeResponseDTO2.setLvl4Nbr(31515);
        sizeResponseDTO2.setFinelineNbr(5134);
        sizeResponseDTO2.setCatSizeObj("CategorySizeObj");
        sizeResponseDTO2.setSubCategorySizeObj("SubCategorySizeObj");
        sizeResponseDTO2.setClusterId(1);
        sizeResponseDTO2.setFineLineSizeObj("[{\"ahsSizeId\":229,\"sizeDesc\":\"S/M\",\"sizeProfilePrcnt\":null,\"adjustedSizeProfile\":null,\"isEligible\":2,\"metrics\":null},\n" +
                "{\"ahsSizeId\":230,\"sizeDesc\":\"L/XL\",\"sizeProfilePrcnt\":null,\"adjustedSizeProfile\":null,\"isEligible\":1,\"metrics\":null},\n" +
                "{\"ahsSizeId\":231,\"sizeDesc\":\"XL\",\"sizeProfilePrcnt\":null,\"adjustedSizeProfile\":null,\"isEligible\":1,\"metrics\":null},\n" +
                "{\"ahsSizeId\":234,\"sizeDesc\":\"0X\",\"sizeProfilePrcnt\":null,\"adjustedSizeProfile\":null,\"isEligible\":1,\"metrics\":null},\n" +
                "{\"ahsSizeId\":235,\"sizeDesc\":\"1X\",\"sizeProfilePrcnt\":null,\"adjustedSizeProfile\":null,\"isEligible\":0,\"metrics\":null},\n" +
                "{\"ahsSizeId\":236,\"sizeDesc\":\"2X\",\"sizeProfilePrcnt\":null,\"adjustedSizeProfile\":null,\"isEligible\":0,\"metrics\":null},\n" +
                "{\"ahsSizeId\":237,\"sizeDesc\":\"3X\",\"sizeProfilePrcnt\":null,\"adjustedSizeProfile\":null,\"isEligible\":0,\"metrics\":null},\n" +
                "{\"ahsSizeId\":238,\"sizeDesc\":\"4X\",\"sizeProfilePrcnt\":null,\"adjustedSizeProfile\":null,\"isEligible\":0,\"metrics\":null},\n" +
                "{\"ahsSizeId\":239,\"sizeDesc\":\"5X\",\"sizeProfilePrcnt\":null,\"adjustedSizeProfile\":null,\"isEligible\":0,\"metrics\":null},\n" +
                "{\"ahsSizeId\":240,\"sizeDesc\":\"0XP\",\"sizeProfilePrcnt\":null,\"adjustedSizeProfile\":null,\"isEligible\":0,\"metrics\":null},\n" +
                "{\"ahsSizeId\":241,\"sizeDesc\":\"1XP\",\"sizeProfilePrcnt\":null,\"adjustedSizeProfile\":null,\"isEligible\":0,\"metrics\":null},\n" +
                "{\"ahsSizeId\":242,\"sizeDesc\":\"2XP\",\"sizeProfilePrcnt\":null,\"adjustedSizeProfile\":null,\"isEligible\":0,\"metrics\":null},\n" +
                "{\"ahsSizeId\":243,\"sizeDesc\":\"3XP\",\"sizeProfilePrcnt\":null,\"adjustedSizeProfile\":null,\"isEligible\":0,\"metrics\":null},\n" +
                "{\"ahsSizeId\":244,\"sizeDesc\":\"4XP\",\"sizeProfilePrcnt\":null,\"adjustedSizeProfile\":null,\"isEligible\":0,\"metrics\":null},\n" +
                "{\"ahsSizeId\":245,\"sizeDesc\":\"5XP\",\"sizeProfilePrcnt\":null,\"adjustedSizeProfile\":null,\"isEligible\":0,\"metrics\":null}]");

        return List.of(sizeResponseDTO, sizeResponseDTO2);
    }

    private RFARequest getRFARequest(Long planId, Integer finelineNbr) {
        RFARequest request = new RFARequest();
        request.setFinelineNbr(finelineNbr);
        request.setPlanId(planId);
        return request;
    }
    
    @Test
    void testGetFinelineWithoutSizeAssociation() {
    	 PlanStrategySPResponse response = new PlanStrategySPResponse();
    	 
     	List<SizeResponseDTO> sizeResponseDTOs = new ArrayList<>();
     	sizeResponseDTOs.add(getUnAssociatedSizeResponseDTO());
     	
     	  List<SizeProfileDTO> sizeProfileDTOList = new ArrayList<>();
           sizeProfileDTOList.add(GetSizeSPClusterObjUtil.getSizeProfileDTO());

         Mockito.when(strategyGroupRepository.getStrategyIdBySeasonCd(StratGroupType.SIZE_PROFILE.getStrategyGroupTypeCode(),null, null)).thenReturn(4l);
     	Mockito.when(strategyFinelineRepository.getCategoriesWithSize(12l, 4l, 1)).thenReturn(sizeResponseDTOs);
     	 try {
              Mockito.when(assortProductService.getRFASpaceDataOutput(getRFARequest(12l, null))).thenReturn(getRfaSpaceResponse());
          } catch (StrategyServiceException e) {
              throw new RuntimeException(e);
          }
        lenient().doNothing().when(planStrategySizeEligMapper).mapPlanStrategyLvl2Sp(SizeEligMapperDTO.builder().sizeResponseDTO(getSizeResponseDTO()).response(response).validationSizeResponseList(Collections.emptyList()).finelineNbr(15).catgFlag(null).build());

    	
     	PlanStrategySPResponse planStrategySPResponse=sizeEligibilityService.getFinelineWithoutSizeAssociation(12l, "store");
     	Assert.assertNotNull(planStrategySPResponse);
    	assertEquals(0,planStrategySPResponse.getLvl3List().size());
    	
    }
    
    private static SizeResponseDTO getUnAssociatedSizeResponseDTO() {
        Long planId = 123l;
        Integer lvl0Nbr = 39107153;
        Integer lvl1Nbr = 50400;
        Integer lvl2Nbr = 105400;
        Integer lvl3Nbr = 34556;
        Integer lvl4Nbr = 4567;
        Integer fineline1Nbr = 151;

        String style1Nbr = "151_23_01_001";
        String ccId = "151_23_01_001_001";

        SizeResponseDTO sizeResponse = new SizeResponseDTO();
        sizeResponse.setPlanId(planId);
        sizeResponse.setLvl0Nbr(lvl0Nbr);
        sizeResponse.setLvl1Nbr(lvl1Nbr);
        sizeResponse.setLvl2Nbr(lvl2Nbr);
        sizeResponse.setLvl3Nbr(lvl3Nbr);
        sizeResponse.setLvl4Nbr(lvl4Nbr);
        sizeResponse.setFinelineNbr(fineline1Nbr);
        sizeResponse.setStyleNbr(style1Nbr);
        sizeResponse.setCcId(ccId);
        sizeResponse.setCcSizeObj("[{\\\"ahsSizeId\\\":246,\\\"sizeDesc\\\":\\\"S/M\\\",\\\"sizeProfilePrcnt\\\":null,\\\"adjustedSizeProfile\\\":35.0,\\\"isEligible\\\":0,\\\"metrics\\\":null}]");

        return sizeResponse;
    }

    
    
    
    
    
}
