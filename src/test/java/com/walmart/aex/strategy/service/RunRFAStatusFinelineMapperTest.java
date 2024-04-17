package com.walmart.aex.strategy.service;

import com.walmart.aex.strategy.dto.RFAStatusDataDTO;
import com.walmart.aex.strategy.entity.AllocationRunTypeText;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class RunRFAStatusFinelineMapperTest {
    @InjectMocks
    RunRFAStatusFinelineMapper mapper;

    @Test
    void testMapAllocationRuntype(){
        AllocationRunTypeText runTypeText = new AllocationRunTypeText();
        Integer id = 1;
        runTypeText.setAllocRunTypeCode(id);
        runTypeText.setAllocRunTypeDesc("Test");

        List<RFAStatusDataDTO> runTypeList = new ArrayList<>();
        mapper.mapAllocationRuntype(runTypeText, runTypeList);

        Assertions.assertEquals(1, runTypeList.size());
    }
}
