package com.walmart.aex.strategy.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.strategy.dto.request.SizeProfileDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SizeAndPackServiceTest {

    @Mock
    ObjectMapper objectMapper;

    @InjectMocks
    SizeAndPackService sizeAndPackService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void getTotalSizeProfilePctWithSizesTest() throws JsonProcessingException {
        Set<Integer> eligibleSizeIds = new HashSet<>();
        eligibleSizeIds.add(246);
        when(objectMapper.readValue(Arrays.toString(getSizeProfileDTO()), SizeProfileDTO[].class)).thenReturn(getSizeProfileDTO());
        double result = sizeAndPackService.getTotalSizeProfilePct(Arrays.toString(getSizeProfileDTO()), eligibleSizeIds);
        assertEquals(50.00, result);
        verify(objectMapper, times(1)).readValue(Arrays.toString(getSizeProfileDTO()), SizeProfileDTO[].class);
    }

    @Test
    void getTotalSizeProfilePctWithNoSizesTest() throws JsonProcessingException {
        Set<Integer> eligibleSizeIds = new HashSet<>();
        double result = sizeAndPackService.getTotalSizeProfilePct(Arrays.toString(getSizeProfileDTO()), eligibleSizeIds);
        assertEquals(-1.00, result);
        verify(objectMapper, times(0)).readValue(Arrays.toString(getSizeProfileDTO()), SizeProfileDTO[].class);
    }

    @Test
    void safeReadSizeObjectNullTest() {
        assertEquals(Collections.emptyList(), sizeAndPackService.safeReadSizeObject(null));
    }

    public static SizeProfileDTO[] getSizeProfileDTO() {
        SizeProfileDTO[] spoDtos = new SizeProfileDTO[1];
        SizeProfileDTO sizeProfile = new SizeProfileDTO();
        sizeProfile.setAdjustedSizeProfile(50.0);
        sizeProfile.setSizeProfilePrcnt(17.0);
        sizeProfile.setIsEligible(0);
        sizeProfile.setAhsSizeId(246);
        sizeProfile.setSizeDesc("S/M");
        spoDtos[0] = sizeProfile;
        return spoDtos;
    }
}