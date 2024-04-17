package com.walmart.aex.strategy.service;

import com.walmart.aex.strategy.dto.request.SizeProfileDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class SPSizeProfileAdjustmentServiceTest {
    @InjectMocks
    private SPSizeProfileAdjustmentService spSizeProfileAdjustmentService;

    @Test
    void getAdjustedSizeProfileObjectWhenMerchantsAssociatesOneLessSize() {
        List<SizeProfileDTO> sizeProfileStrategiesFromRequest = Arrays.asList(getSizeProfileObj(229, "S/M", 7.50, 7.50, 1)
                , getSizeProfileObj(230, "L/XL", 12.50, 12.50, 1)
                , getSizeProfileObj(231, "XL", 28.50, 28.50, 1)
                , getSizeProfileObj(234, "0X", 27.00, 27.00, 1)
                , getSizeProfileObj(235, "1X", 18.50, 18.50, 1)
                , getSizeProfileObj(236, "2X", 0.0, 0.0, 1));
        List<SizeProfileDTO> sizeProfileStrategiesFromResponse = spSizeProfileAdjustmentService.getAdjustedSizeProfileObject(sizeProfileStrategiesFromRequest);
        double sum = sizeProfileStrategiesFromResponse.stream().filter(s -> s.getIsEligible() > 0).mapToDouble(SizeProfileDTO::getAdjustedSizeProfile).sum();
        assertEquals(100, sum);
    }

    @Test
    void getAdjustedSizeProfileObjectWhenMerchantsAssociatesMoreThanOneLessSize() {
        List<SizeProfileDTO> sizeProfileStrategiesFromRequest = Arrays.asList(getSizeProfileObj(229, "S/M", 0.00, 0.00, 1)
                , getSizeProfileObj(230, "L/XL", 12.50, 12.50, 1)
                , getSizeProfileObj(231, "XL", 28.50, 28.50, 1)
                , getSizeProfileObj(234, "0X", 27.00, 27.00, 1)
                , getSizeProfileObj(235, "1X", 18.50, 18.50, 1)
                , getSizeProfileObj(236, "2X", 0.0, 0.0, 1));

        List<SizeProfileDTO> sizeProfileStrategiesFromResponse = spSizeProfileAdjustmentService.getAdjustedSizeProfileObject(sizeProfileStrategiesFromRequest);
        double sum = sizeProfileStrategiesFromResponse.stream().filter(s -> s.getIsEligible() > 0).mapToDouble(SizeProfileDTO::getAdjustedSizeProfile).sum();
        assertEquals(100, sum);
    }

    @Test
    void getAdjustedSizeProfileObjectWhenMerchantsAssociatesOneAdditionalSize() {
        List<SizeProfileDTO> sizeProfileStrategiesFromRequest = Arrays.asList(getSizeProfileObj(229, "S/M", 7.50, 7.50, 1)
                , getSizeProfileObj(230, "L/XL", 12.50, 12.50, 1)
                , getSizeProfileObj(231, "XL", 28.50, 28.50, 1)
                , getSizeProfileObj(234, "0X", 27.00, 27.00, 1)
                , getSizeProfileObj(235, "1X", 18.50, 18.50, 1)
                , getSizeProfileObj(236, "2X", 6.0, 6.0, 1)
                , getSizeProfileObj(237, "3X", null, 5.0, 1));

        List<SizeProfileDTO> sizeProfileStrategiesFromResponse = spSizeProfileAdjustmentService.getAdjustedSizeProfileObject(sizeProfileStrategiesFromRequest);
        double sum = sizeProfileStrategiesFromResponse.stream().filter(s -> s.getIsEligible() > 0).mapToDouble(SizeProfileDTO::getAdjustedSizeProfile).sum();
        assertEquals(100, Math.round(sum));
    }

    @Test
    void getAdjustedSizeProfileObjectWhenMerchantsAssociatesOnePlusAdditionalSize() {
        List<SizeProfileDTO> sizeProfileStrategiesFromRequest = Arrays.asList(getSizeProfileObj(229, "S/M", 7.50, 7.50, 1)
                , getSizeProfileObj(230, "L/XL", 12.50, 12.50, 1)
                , getSizeProfileObj(231, "XL", 28.50, 28.50, 1)
                , getSizeProfileObj(234, "0X", 27.00, 27.00, 1)
                , getSizeProfileObj(235, "1X", 18.50, 18.50, 1)
                , getSizeProfileObj(236, "2X", 6.0, 6.0, 1)
                , getSizeProfileObj(237, "3X", null, 2.0, 1)
                , getSizeProfileObj(238, "4X", null, 2.0, 1));
        List<SizeProfileDTO> sizeProfileStrategiesFromResponse = spSizeProfileAdjustmentService.getAdjustedSizeProfileObject(sizeProfileStrategiesFromRequest);
        double sum = sizeProfileStrategiesFromResponse.stream().filter(s -> s.getIsEligible() > 0).mapToDouble(SizeProfileDTO::getAdjustedSizeProfile).sum();
        assertEquals(100, sum);
    }

    @Test
    void getAdjustedSizeProfileObjectWhenMerchantsAssociatesOneNewSizeAndNotAssociateSize() {
        List<SizeProfileDTO> sizeProfileStrategiesFromRequest = Arrays.asList(getSizeProfileObj(229, "S/M", 7.50, 7.50, 1)
                , getSizeProfileObj(230, "L/XL", 12.50, 12.50, 1)
                , getSizeProfileObj(231, "XL", 28.50, 28.50, 1)
                , getSizeProfileObj(234, "0X", 27.00, 27.00, 1)
                , getSizeProfileObj(235, "1X", 18.50, 18.50, 1)
                , getSizeProfileObj(236, "2X", 0.0, 0.0, 1));
        List<SizeProfileDTO> sizeProfileStrategiesFromResponse = spSizeProfileAdjustmentService.getAdjustedSizeProfileObject(sizeProfileStrategiesFromRequest);
        double sum1 = sizeProfileStrategiesFromResponse.stream().filter(s -> s.getIsEligible() > 0).mapToDouble(SizeProfileDTO::getAdjustedSizeProfile).sum();
        assertEquals(100, sum1);
        List<SizeProfileDTO> sizeProfileStrategiesFromRequest2 = Arrays.asList(getSizeProfileObj(229, "S/M", sizeProfileStrategiesFromResponse.get(0).getSizeProfilePrcnt(), sizeProfileStrategiesFromResponse.get(0).getAdjustedSizeProfile(), 1)
                , getSizeProfileObj(230, "L/XL", sizeProfileStrategiesFromResponse.get(1).getSizeProfilePrcnt(), sizeProfileStrategiesFromResponse.get(1).getAdjustedSizeProfile(), 1)
                , getSizeProfileObj(231, "XL", sizeProfileStrategiesFromResponse.get(2).getSizeProfilePrcnt(), sizeProfileStrategiesFromResponse.get(2).getAdjustedSizeProfile(), 1)
                , getSizeProfileObj(234, "0X", sizeProfileStrategiesFromResponse.get(3).getSizeProfilePrcnt(), sizeProfileStrategiesFromResponse.get(3).getAdjustedSizeProfile(), 1)
                , getSizeProfileObj(235, "1X", sizeProfileStrategiesFromResponse.get(4).getSizeProfilePrcnt(), sizeProfileStrategiesFromResponse.get(4).getAdjustedSizeProfile(), 1)
                , getSizeProfileObj(236, "2X", sizeProfileStrategiesFromResponse.get(5).getSizeProfilePrcnt(), 5.0, 1));

        List<SizeProfileDTO> sizeProfileStrategiesFromResponse1 = spSizeProfileAdjustmentService.getAdjustedSizeProfileObject(sizeProfileStrategiesFromRequest2);
        double sum2 = sizeProfileStrategiesFromResponse1.stream().filter(s -> s.getIsEligible() > 0).mapToDouble(SizeProfileDTO::getAdjustedSizeProfile).sum();
        assertEquals(100, Math.round(sum2));
    }

    @Test
    void getAdjustedSizeProfileObjectWhenMerchantsAssociatesOneLessSizeAndHasIneigibleSizeProfileObject() {
        List<SizeProfileDTO> sizeProfileStrategiesFromRequest = Arrays.asList(getSizeProfileObj(229, "S/M", 7.50, 7.50, 1)
                , getSizeProfileObj(230, "L/XL", 12.50, 12.50, 1)
                , getSizeProfileObj(231, "XL", 28.50, 28.50, 1)
                , getSizeProfileObj(234, "0X", 27.00, 27.00, 1)
                , getSizeProfileObj(235, "1X", 18.50, 18.50, 1)
                , getSizeProfileObj(236, "2X", 0.0, 0.0, 1)
                , getSizeProfileObj(237, "3X", 0.0, 10.0, 0));
        List<SizeProfileDTO> sizeProfileStrategiesFromResponse = spSizeProfileAdjustmentService.getAdjustedSizeProfileObject(sizeProfileStrategiesFromRequest);
        double sum = sizeProfileStrategiesFromResponse.stream().filter(s -> s.getIsEligible() > 0).mapToDouble(SizeProfileDTO::getAdjustedSizeProfile).sum();
        assertEquals(100, sum);
    }

    @Test
    void processsSizeProfileAdjustmentTest(){
        List<SizeProfileDTO> sizeProfileStrategiesFromRequest = Arrays.asList(getSizeProfileObj(229, "S/M", 7.50, 7.50, 1)
                , getSizeProfileObj(230, "L/XL", 12.50, 12.50, 0)
                , getSizeProfileObj(231, "XL", 28.50, 28.50, 0)
                , getSizeProfileObj(234, "0X", 27.00, 27.00, 0)
                , getSizeProfileObj(235, "1X", 18.50, 18.50, 0)
                , getSizeProfileObj(236, "2X", null, null, 0)
                , getSizeProfileObj(237, "3X", null, null, 0));
        Set<Integer> ahsIdSet = new HashSet<>(Arrays.asList(229, 230, 231,232));

        List<SizeProfileDTO> sizeLists = spSizeProfileAdjustmentService.processSizeProfileAdjustment(sizeProfileStrategiesFromRequest,ahsIdSet);
        assertEquals(0, sizeLists.get(0).getIsEligible());
        double sum = sizeLists.stream().filter(s -> ahsIdSet.contains(s.getAhsSizeId()) && s.getAdjustedSizeProfile()!=null).mapToDouble(SizeProfileDTO::getAdjustedSizeProfile).sum();
        assertEquals(100, Math.round(sum));
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

    @Test
    void testaddAdjSizeProfile() {
        List<SizeProfileDTO> sizeLists = new ArrayList<>();
        SizeProfileDTO sizeProfileDTO = new SizeProfileDTO();
        sizeProfileDTO.setAdjustedSizeProfile(null);
        sizeProfileDTO.setSizeProfilePrcnt(17.0);
        sizeProfileDTO.setIsEligible(1);
        sizeProfileDTO.setAhsSizeId(246);
        sizeProfileDTO.setSizeDesc("S/M");
        sizeLists.add(sizeProfileDTO);

        sizeLists = spSizeProfileAdjustmentService.addDefaultAdjSizeProfilePct(sizeLists);
        assertEquals(1, sizeLists.get(0).getIsEligible());
        assertEquals(2, sizeLists.get(0).getAdjustedSizeProfile());
    }
}
