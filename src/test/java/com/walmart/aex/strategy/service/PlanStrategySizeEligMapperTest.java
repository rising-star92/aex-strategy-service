package com.walmart.aex.strategy.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.strategy.dto.*;
import com.walmart.aex.strategy.dto.request.CustomerChoiceSP;
import com.walmart.aex.strategy.dto.request.StyleSP;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PlanStrategySizeEligMapperTest {

    private SizeResponseDTO sizeResponseDTO;
    private List<SizeResponseDTO> validationSizeResponseList;

    private ObjectMapper objectMapper;

    @InjectMocks
    private PlanStrategySizeEligMapper planStrategySizeEligMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        planStrategySizeEligMapper = new PlanStrategySizeEligMapper(objectMapper, new SizeAndPackValidationMapper());
    }

    @Test
    void mapCcPlanStrategyLvl2SpTest() throws IOException {
        sizeResponseDTO = objectMapper.readValue(readJsonFileAsString("CcSizeResponseDTO"), SizeResponseDTO.class);
        validationSizeResponseList = List.of(new SizeResponseDTO(2760, "34_2760_3_22_4", "34_2760_3_22_4_001", 0, 1, 1, -45.00));
        PlanStrategySPResponse response = new PlanStrategySPResponse();
        planStrategySizeEligMapper.mapPlanStrategyLvl2Sp(SizeEligMapperDTO.builder().sizeResponseDTO(sizeResponseDTO).response(response).validationSizeResponseList(validationSizeResponseList).finelineNbr(2760).catgFlag(null).channel("online").build());
        StyleSP styleSP = response.getLvl3List()
                        .stream().filter(lvl3 -> lvl3.getLvl3Nbr() == 12228).findFirst().orElse(new Lvl3ListSPResponse()).getLvl4List()
                        .stream().filter(lvl4 -> lvl4.getLvl4Nbr() == 31507).findFirst().orElse(new Lvl4ListSPResponse()).getFinelines()
                        .stream().filter(fineline -> fineline.getFinelineNbr() == 2760).findFirst().orElse(new FineLineSPResponse()).getStyles()
                        .stream().filter(style -> style.getStyleNbr().equals("34_2760_3_22_4")).findFirst().orElse(new StyleSP());
        assertEquals(sizeResponseDTO.getAltStyleDesc(), styleSP.getAltStyleDesc());
        assertEquals(sizeResponseDTO.getAltCcDesc(), styleSP.getCustomerChoices().stream().filter(cc -> cc.getCcId().equals("34_2760_3_22_4_001")).findFirst().orElse(new CustomerChoiceSP()).getAltCcDesc());
        ValidationSPResponse expectedValidationResponse = ValidationSPResponse.builder().sizeProfilePctList(Set.of(-45L)).build();

        assertEquals(expectedValidationResponse, styleSP.getMetadata().getValidationData());
        assertEquals(expectedValidationResponse, styleSP.getCustomerChoices().stream().filter(cc -> cc.getCcId().equals("34_2760_3_22_4_001")).findFirst().orElse(new CustomerChoiceSP()).getMetadata().getValidationData());
    }

    @Test
    void mapFinelinePlanStrategyLvl2SpTest() throws IOException {
        sizeResponseDTO = objectMapper.readValue(readJsonFileAsString("FinelineSizeResponseDTO"), SizeResponseDTO.class);
        validationSizeResponseList = List.of(new SizeResponseDTO(2760, "34_2760_3_22_4", "34_2760_3_22_4_001", 2, 1, null, -1.00));
        PlanStrategySPResponse response = new PlanStrategySPResponse();
        planStrategySizeEligMapper.mapPlanStrategyLvl2Sp(SizeEligMapperDTO.builder().sizeResponseDTO(sizeResponseDTO).response(response).validationSizeResponseList(validationSizeResponseList).finelineNbr(null).catgFlag(null).channel("store").build());
        FineLineSPResponse fineLineSPResponse = response.getLvl3List()
                .stream().filter(lvl3 -> lvl3.getLvl3Nbr() == 12228).findFirst().orElse(new Lvl3ListSPResponse()).getLvl4List()
                .stream().filter(lvl4 -> lvl4.getLvl4Nbr() == 31507).findFirst().orElse(new Lvl4ListSPResponse()).getFinelines()
                .stream().filter(fineline -> fineline.getFinelineNbr() == 2760).findFirst().orElse(new FineLineSPResponse());
        Set<Integer> merchMethodCodes = new HashSet<>();
        merchMethodCodes.add(null);
        ValidationSPResponse expectedValidationResponse = ValidationSPResponse.builder().merchMethodCodeList(merchMethodCodes).sizeProfilePctList(Set.of(-1L)).build();
        assertEquals(sizeResponseDTO.getAltFinelineName(), fineLineSPResponse.getAltFinelineName());
        assertEquals(expectedValidationResponse, fineLineSPResponse.getMetadata().getValidationData());
    }

    String readJsonFileAsString(String fileName) throws IOException {
        return new String(Files.readAllBytes(Paths.get("src/test/resources/data/" + fileName + ".json")));
    }
}