package com.walmart.aex.strategy.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.strategy.dto.*;
import com.walmart.aex.strategy.dto.request.CustomerChoiceSP;
import com.walmart.aex.strategy.dto.request.StrategySP;
import com.walmart.aex.strategy.dto.request.StyleSP;
import com.walmart.aex.strategy.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class PlanStrategySizeEligMapper {
    private final ObjectMapper objectMapper;

    private final SizeAndPackValidationMapper sizeAndPackValidationMapper;

    public PlanStrategySizeEligMapper(ObjectMapper objectMapper, SizeAndPackValidationMapper sizeAndPackValidationMapper) {
        this.objectMapper = objectMapper;
        this.sizeAndPackValidationMapper = sizeAndPackValidationMapper;
    }

    public void mapPlanStrategyLvl2Sp(SizeEligMapperDTO sizeEligMapperDTO) {
        PlanStrategySPResponse response = sizeEligMapperDTO.getResponse();
        SizeResponseDTO sizeResponseDTO = sizeEligMapperDTO.getSizeResponseDTO();
        if (response.getPlanId() == null) {
            response.setPlanId(sizeResponseDTO.getPlanId());
        }
        if (response.getLvl0Nbr() == null)
            response.setLvl0Nbr(sizeResponseDTO.getLvl0Nbr());
        if (response.getLvl1Nbr() == null)
            response.setLvl1Nbr(sizeResponseDTO.getLvl1Nbr());
        if (response.getLvl2Nbr() == null)
            response.setLvl2Nbr(sizeResponseDTO.getLvl2Nbr());
        response.setLvl3List(mapPlanStrategySpLvl3(sizeEligMapperDTO));
    }

    private List<Lvl3ListSPResponse> mapPlanStrategySpLvl3(SizeEligMapperDTO sizeEligMapperDTO) {
        List<Lvl3ListSPResponse> lvl3List = Optional.ofNullable(sizeEligMapperDTO.getResponse().getLvl3List()).orElse(new ArrayList<>());

        lvl3List.stream()
                .filter(lvl3 -> sizeEligMapperDTO.getSizeResponseDTO().getLvl3Nbr().equals(lvl3.getLvl3Nbr())).findFirst()
                .ifPresentOrElse(lvl3 -> lvl3.setLvl4List(mapSubCategorySpLvl4(sizeEligMapperDTO, lvl3)),
                        () -> setLvl3SP(sizeEligMapperDTO, lvl3List));
        return lvl3List;
    }

    private void setLvl3SP(SizeEligMapperDTO sizeEligMapperDTO, List<Lvl3ListSPResponse> lvl3List) {
        SizeResponseDTO dto = sizeEligMapperDTO.getSizeResponseDTO();
        Lvl3ListSPResponse lvl3 = new Lvl3ListSPResponse();
        lvl3.setLvl3Nbr(dto.getLvl3Nbr());
        if (Objects.isNull(sizeEligMapperDTO.getFinelineNbr()) || Objects.nonNull(sizeEligMapperDTO.getCatgFlag())) {
            lvl3.setLvl3Name(dto.getLvl3GenDesc1());
            StrategySPResponse s = new StrategySPResponse();
            try {
                if (dto.getCatSizeObj() != null)
                    s.setSizeProfile(objectMapper.readValue(dto.getCatSizeObj(), new TypeReference<>() {
                    }));
            } catch (JsonProcessingException jsonProcessingException) {
                log.error("Error parsing catg size: ", jsonProcessingException);
                throw new CustomException("Error Parsing category size profile");
            }
            lvl3.setStrategy(s);
        }
        lvl3List.add(lvl3);
        lvl3.setLvl4List(mapSubCategorySpLvl4(sizeEligMapperDTO, lvl3));
    }

    private List<Lvl4ListSPResponse> mapSubCategorySpLvl4(SizeEligMapperDTO sizeEligMapperDTO, Lvl3ListSPResponse lvl3) {
        List<Lvl4ListSPResponse> lvl4List = Optional.ofNullable(lvl3.getLvl4List()).orElse(new ArrayList<>());

        lvl4List.stream()
                .filter(lvl4 -> sizeEligMapperDTO.getSizeResponseDTO().getLvl4Nbr().equals(lvl4.getLvl4Nbr())).findFirst()
                .ifPresentOrElse(lvl4 -> lvl4.setFinelines(mapPlanStrategyFinelineSp(sizeEligMapperDTO, lvl4)),
                        () -> setLvl4(sizeEligMapperDTO, lvl4List));
        return lvl4List;
    }

    private void setLvl4(SizeEligMapperDTO sizeEligMapperDTO, List<Lvl4ListSPResponse> lvl4List) {
        SizeResponseDTO dto = sizeEligMapperDTO.getSizeResponseDTO();
        Lvl4ListSPResponse lvl4 = new Lvl4ListSPResponse();
        lvl4.setLvl4Nbr(dto.getLvl4Nbr());
        if (Objects.isNull(sizeEligMapperDTO.getFinelineNbr()) || Objects.nonNull(sizeEligMapperDTO.getCatgFlag())) {
            lvl4.setLvl4Name(dto.getLvl4GenDesc1());
            StrategySPResponse strategySPResponse = new StrategySPResponse();
            try {
                if (dto.getSubCategorySizeObj() != null)
                    strategySPResponse.setSizeProfile(objectMapper.readValue(dto.getSubCategorySizeObj(), new TypeReference<>() {
                    }));
            } catch (JsonProcessingException jsonProcessingException) {
                log.error("Error parsing subcatg size: ", jsonProcessingException);
                throw new CustomException("Error Parsing Sub Category size profile");
            }
            lvl4.setStrategy(strategySPResponse);
        }
        lvl4List.add(lvl4);
        lvl4.setFinelines(mapPlanStrategyFinelineSp(sizeEligMapperDTO, lvl4));
    }

    private List<FineLineSPResponse> mapPlanStrategyFinelineSp(SizeEligMapperDTO sizeEligMapperDTO, Lvl4ListSPResponse lvl4) {
        List<FineLineSPResponse> finelineList = Optional.ofNullable(lvl4.getFinelines()).orElse(new ArrayList<>());
        if (Objects.nonNull(sizeEligMapperDTO.getFinelineNbr())) {
            finelineList.stream()
                    .filter(fineLine -> sizeEligMapperDTO.getSizeResponseDTO().getFinelineNbr().equals(fineLine.getFinelineNbr())).findFirst()
                    .ifPresentOrElse(fineline -> fineline.setStyles(mapPlanStrategyStyleSp(sizeEligMapperDTO, fineline)),
                            () -> setFinelineSP(sizeEligMapperDTO, finelineList));
        } else {
            setFinelineSP(sizeEligMapperDTO, finelineList);
        }
        return finelineList;
    }

    private void setFinelineSP(SizeEligMapperDTO sizeEligMapperDTO, List<FineLineSPResponse> finelineList) {
        SizeResponseDTO dto = sizeEligMapperDTO.getSizeResponseDTO();
        FineLineSPResponse fineline = new FineLineSPResponse();
        fineline.setFinelineNbr(dto.getFinelineNbr());
        if (Objects.isNull(sizeEligMapperDTO.getFinelineNbr()) || Objects.nonNull(sizeEligMapperDTO.getCatgFlag())) {
            fineline.setFinelineName(dto.getFinelineDesc());
            fineline.setAltFinelineName(dto.getAltFinelineName());
            fineline.setMetadata(
                    MetadataSPResponse.builder()
                            .validationData(sizeAndPackValidationMapper.getFinelineValidation(sizeEligMapperDTO.getSizeResponseDTO(), sizeEligMapperDTO.getValidationSizeResponseList()))
                            .build());
            StrategySPResponse strategySPResponse = new StrategySPResponse();
            try {
                if (dto.getFineLineSizeObj() != null)
                    strategySPResponse.setSizeProfile(objectMapper.readValue(dto.getFineLineSizeObj(), new TypeReference<>() {
                    }));
            } catch (JsonProcessingException jsonProcessingException) {
                log.error("Error parsing fineline size: ", jsonProcessingException);
                throw new CustomException("Error Parsing fineline size profile");
            }
            fineline.setStrategy(strategySPResponse);
            if (Objects.nonNull(sizeEligMapperDTO.getCatgFlag()))
                fineline.setStyles(mapPlanStrategyStyleSp(sizeEligMapperDTO, fineline));
        } else {
            fineline.setStyles(mapPlanStrategyStyleSp(sizeEligMapperDTO, fineline));
        }
        finelineList.add(fineline);
    }

    private List<StyleSP> mapPlanStrategyStyleSp(SizeEligMapperDTO sizeEligMapperDTO, FineLineSPResponse fineline) {
        List<StyleSP> styleSPS = Optional.ofNullable(fineline.getStyles()).orElse(new ArrayList<>());
        styleSPS.stream()
                .filter(styleSP -> sizeEligMapperDTO.getSizeResponseDTO().getStyleNbr().equals(styleSP.getStyleNbr())).findFirst()
                .ifPresentOrElse(styleSP -> styleSP.setCustomerChoices(mapPlanStrategyCcSp(sizeEligMapperDTO, styleSP)),
                        () -> setStyleSP(sizeEligMapperDTO, styleSPS));
        return styleSPS;
    }

    private void setStyleSP(SizeEligMapperDTO sizeEligMapperDTO, List<StyleSP> styleSPS) {
        SizeResponseDTO dto = sizeEligMapperDTO.getSizeResponseDTO();
        StyleSP styleSP = new StyleSP();
        styleSP.setStyleNbr(dto.getStyleNbr());
        styleSP.setAltStyleDesc(dto.getAltStyleDesc());
        styleSP.setMetadata(MetadataSPResponse.builder()
                .validationData(ValidationSPResponse.builder().sizeProfilePctList(new HashSet<>()).build())
                .build());
        styleSP.setCustomerChoices(mapPlanStrategyCcSp(sizeEligMapperDTO, styleSP));
        styleSPS.add(styleSP);
    }

    private List<CustomerChoiceSP> mapPlanStrategyCcSp(SizeEligMapperDTO sizeEligMapperDTO, StyleSP styleSP) {
        styleSP.getMetadata().getValidationData().getSizeProfilePctList().addAll(sizeAndPackValidationMapper.getStyleValidation(
                sizeEligMapperDTO.getSizeResponseDTO(), sizeEligMapperDTO.getValidationSizeResponseList()
        ));
        List<CustomerChoiceSP> customerChoiceSPS = Optional.ofNullable(styleSP.getCustomerChoices()).orElse(new ArrayList<>());
        setCcSP(sizeEligMapperDTO, customerChoiceSPS);
        return customerChoiceSPS;
    }

    private void setCcSP(SizeEligMapperDTO sizeEligMapperDTO, List<CustomerChoiceSP> customerChoiceSPS) {
        SizeResponseDTO dto = sizeEligMapperDTO.getSizeResponseDTO();
        CustomerChoiceSP customerChoiceSP = new CustomerChoiceSP();
        customerChoiceSP.setCcId(dto.getCcId());
        customerChoiceSP.setAltCcDesc(dto.getAltCcDesc());
        customerChoiceSP.setColorFamily(dto.getColorFamily());
        customerChoiceSP.setColorName(dto.getColorName());
        customerChoiceSP.setMetadata(MetadataSPResponse.builder()
                .validationData(sizeAndPackValidationMapper.getCcValidation(sizeEligMapperDTO.getSizeResponseDTO(), sizeEligMapperDTO.getValidationSizeResponseList(), false))
                .build());
        StrategySP strategySP = new StrategySP();
        try {
            if (dto.getCcSizeObj() != null)
                strategySP.setSizeProfile(objectMapper.readValue(dto.getCcSizeObj(), new TypeReference<>() {
                }));
        } catch (JsonProcessingException jsonProcessingException) {
            log.error("Error parsing fineline size: ", jsonProcessingException);
            throw new CustomException("Error Parsing fineline size profile");
        }
        customerChoiceSP.setStrategy(strategySP);
        customerChoiceSPS.add(customerChoiceSP);
    }
}
