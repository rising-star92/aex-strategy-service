package com.walmart.aex.strategy.service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.strategy.dto.SizeResponseDTO;
import com.walmart.aex.strategy.dto.sizeprofile.PlanSizeProfile;
import com.walmart.aex.strategy.dto.sizeprofile.SizeProfileMapperDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class SizeProfileMapperTest {

    @InjectMocks
    SizeProfileMapper sizeProfileMapper;
    @Mock
    SizeResponseDTO sizeResponseDTO;
    private static final Long planId=471L;

    @BeforeEach
    void setUp() {
        sizeProfileMapper = new SizeProfileMapper(new ObjectMapper(), new SizeAndPackValidationMapper());
    }
    @Test
    void testMapSizeProfileLvl2Sp()
    {
        PlanSizeProfile planSizeProfile= new PlanSizeProfile();
        sizeResponseDTO =new SizeResponseDTO();
        sizeResponseDTO.setPlanId(planId);
        sizeResponseDTO.setCcSizeObj(null);
        sizeResponseDTO.setFineLineSizeObj(null);
        sizeResponseDTO.setStyleSizeObj(null);
        sizeResponseDTO.setClusterId(0);
        Set<Integer> eligibleSizes = new HashSet<>();
        sizeProfileMapper.mapSizeProfileLvl2Sp(SizeProfileMapperDTO.builder()
                        .sizeResponseDTO(sizeResponseDTO)
                        .response(planSizeProfile)
                        .styleNbr("1")
                        .ccId("black")
                        .eligibleAhsSizeIds(eligibleSizes)
                .build());
        assertNotNull(planSizeProfile);
        assertEquals(471l, planSizeProfile.getPlanId());
    }
    
    @Test
    void testMapSizeProfileLvlSizeRounding1()
    {
        PlanSizeProfile planSizeProfile= new PlanSizeProfile();
        sizeResponseDTO =new SizeResponseDTO();
        sizeResponseDTO.setPlanId(planId);
        sizeResponseDTO.setCcSizeObj(null);
        sizeResponseDTO.setFineLineSizeObj(null);
        sizeResponseDTO.setStyleSizeObj(null);
        sizeResponseDTO.setClusterId(0);
        Set<Integer> eligibleSizes = new HashSet<>();
        eligibleSizes.add(new Integer(33));
        eligibleSizes.add(new Integer(45));
        eligibleSizes.add(new Integer(39));
        sizeProfileMapper.mapSizeProfileLvl2Sp(SizeProfileMapperDTO.builder()
                        .sizeResponseDTO(getSizeResponseDTO1())
                        .response(planSizeProfile)
                        .styleNbr("1")
                        .ccId("black")
                        .eligibleAhsSizeIds(eligibleSizes)
                        .validationResponseList((List.of(getSizeResponseDTO2())))
                        .channel("store")
                .build());
        assertNotNull(planSizeProfile);
        assertEquals("BLACK", planSizeProfile.getLvl3List().get(0).getLvl4List().get(0).getFinelines().get(0).getStyles().get(0).getCustomerChoices().get(0).getColorFamily());
        assertEquals(101, planSizeProfile.getLvl3List().get(0).getLvl4List().get(0).getFinelines().get(0).getStyles().get(0).getCustomerChoices().get(0).getClusters().get(0).getTotalSizeProfilePct());
    }
    
    @Test
    void testMapSizeProfileLvlSizeRounding2()
    {
        PlanSizeProfile planSizeProfile= new PlanSizeProfile();
        sizeResponseDTO =new SizeResponseDTO();
        sizeResponseDTO.setPlanId(planId);
        sizeResponseDTO.setCcSizeObj(null);
        sizeResponseDTO.setFineLineSizeObj(null);
        sizeResponseDTO.setStyleSizeObj(null);
        sizeResponseDTO.setClusterId(0);
        Set<Integer> eligibleSizes = new HashSet<>();
        eligibleSizes.add(new Integer(33));
        eligibleSizes.add(new Integer(45));
        eligibleSizes.add(new Integer(39));
        sizeProfileMapper.mapSizeProfileLvl2Sp(SizeProfileMapperDTO.builder()
                        .sizeResponseDTO(getSizeResponseDTO2())
                        .response(planSizeProfile)
                        .styleNbr("1")
                        .ccId("black")
                        .eligibleAhsSizeIds(eligibleSizes)
                .build());
        assertNotNull(planSizeProfile);
        assertEquals(101, planSizeProfile.getLvl3List().get(0).getLvl4List().get(0).getFinelines().get(0).getStyles().get(0).getCustomerChoices().get(0).getClusters().get(0).getTotalSizeProfilePct());
    }
    
    @Test
    void testMapSizeProfileLvlSizeRounding3()
    {
        PlanSizeProfile planSizeProfile= new PlanSizeProfile();
        sizeResponseDTO =new SizeResponseDTO();
        sizeResponseDTO.setPlanId(planId);
        sizeResponseDTO.setCcSizeObj(null);
        sizeResponseDTO.setFineLineSizeObj(null);
        sizeResponseDTO.setStyleSizeObj(null);
        sizeResponseDTO.setClusterId(0);
        Set<Integer> eligibleSizes = new HashSet<>();
        eligibleSizes.add(new Integer(33));
        eligibleSizes.add(new Integer(45));
        eligibleSizes.add(new Integer(39));
        sizeProfileMapper.mapSizeProfileLvl2Sp(SizeProfileMapperDTO.builder()
                        .sizeResponseDTO(getSizeResponseDTO3())
                        .response(planSizeProfile)
                        .styleNbr("1")
                        .ccId("black")
                        .eligibleAhsSizeIds(eligibleSizes)
                .build());
        assertNotNull(planSizeProfile);
        assertEquals(100, planSizeProfile.getLvl3List().get(0).getLvl4List().get(0).getFinelines().get(0).getStyles().get(0).getCustomerChoices().get(0).getClusters().get(0).getTotalSizeProfilePct());
    }
    
    @Test
    void testMapSizeProfileLvlSizeRounding4()
    {
        PlanSizeProfile planSizeProfile= new PlanSizeProfile();
        sizeResponseDTO =new SizeResponseDTO();
        sizeResponseDTO.setPlanId(planId);
        sizeResponseDTO.setCcSizeObj(null);
        sizeResponseDTO.setFineLineSizeObj(null);
        sizeResponseDTO.setStyleSizeObj(null);
        sizeResponseDTO.setClusterId(0);
        Set<Integer> eligibleSizes = new HashSet<>();
        eligibleSizes.add(new Integer(33));
        eligibleSizes.add(new Integer(45));
        eligibleSizes.add(new Integer(39));
        sizeProfileMapper.mapSizeProfileLvl2Sp(SizeProfileMapperDTO.builder()
                        .sizeResponseDTO(getSizeResponseDTO4())
                        .response(planSizeProfile)
                        .styleNbr("1")
                        .ccId("black")
                        .eligibleAhsSizeIds(eligibleSizes)
                .build());
        assertNotNull(planSizeProfile);
        assertEquals(97, planSizeProfile.getLvl3List().get(0).getLvl4List().get(0).getFinelines().get(0).getStyles().get(0).getCustomerChoices().get(0).getClusters().get(0).getTotalSizeProfilePct());
    }
    
    private static SizeResponseDTO getSizeResponseDTO1() {
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
        sizeResponse.setColorFamily("BLACK");
        sizeResponse.setCcId(ccId);
        sizeResponse.setCcSizeObj("[{\"ahsSizeId\":45,\"sizeDesc\":\"S\",\"sizeProfilePrcnt\":24,\"adjustedSizeProfile\":20.55,\"isEligible\":1,\"metrics\":null}, "
        		+ "{\"ahsSizeId\":33,\"sizeDesc\":\"S\",\"sizeProfilePrcnt\":45.67,\"adjustedSizeProfile\":80,\"isEligible\":1,\"metrics\":null}]");

        return sizeResponse;

    }
    
    private static SizeResponseDTO getSizeResponseDTO2() {
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
        sizeResponse.setCcSizeObj("[{\"ahsSizeId\":45,\"sizeDesc\":\"S\",\"sizeProfilePrcnt\":24,\"adjustedSizeProfile\":30.92,\"isEligible\":1,\"metrics\":null}, "
        		+ "{\"ahsSizeId\":33,\"sizeDesc\":\"S\",\"sizeProfilePrcnt\":45.67,\"adjustedSizeProfile\":70,\"isEligible\":1,\"metrics\":null}]");

        return sizeResponse;

    }
    
    private static SizeResponseDTO getSizeResponseDTO3() {
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
        sizeResponse.setCcSizeObj("[{\"ahsSizeId\":45,\"sizeDesc\":\"S\",\"sizeProfilePrcnt\":24,\"adjustedSizeProfile\":69.92,\"isEligible\":1,\"metrics\":null}, "
        		+ "{\"ahsSizeId\":33,\"sizeDesc\":\"S\",\"sizeProfilePrcnt\":45.67,\"adjustedSizeProfile\":30,\"isEligible\":1,\"metrics\":null}]");

        return sizeResponse;

    }
    
    private static SizeResponseDTO getSizeResponseDTO4() {
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
        sizeResponse.setCcSizeObj("[{\"ahsSizeId\":45,\"sizeDesc\":\"S\",\"sizeProfilePrcnt\":24,\"adjustedSizeProfile\":57.39,\"isEligible\":1,\"metrics\":null}, "
        		+ "{\"ahsSizeId\":33,\"sizeDesc\":\"S\",\"sizeProfilePrcnt\":45.67,\"adjustedSizeProfile\":40,\"isEligible\":1,\"metrics\":null}]");

        return sizeResponse;

    }
}