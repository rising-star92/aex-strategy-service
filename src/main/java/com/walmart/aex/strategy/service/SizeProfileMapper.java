package com.walmart.aex.strategy.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.strategy.dto.MetadataSPResponse;
import com.walmart.aex.strategy.dto.SizeResponseDTO;
import com.walmart.aex.strategy.dto.ValidationSPResponse;
import com.walmart.aex.strategy.dto.request.SizeProfileDTO;
import com.walmart.aex.strategy.dto.sizeprofile.*;
import com.walmart.aex.strategy.enums.MerchMethod;
import com.walmart.aex.strategy.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class SizeProfileMapper {
    private final ObjectMapper objectMapper;

    private final SizeAndPackValidationMapper sizeAndPackValidationMapper;

    public SizeProfileMapper(ObjectMapper objectMapper, SizeAndPackValidationMapper sizeAndPackValidationMapper) {
        this.objectMapper = objectMapper;
        this.sizeAndPackValidationMapper = sizeAndPackValidationMapper;
    }

    public void mapSizeProfileLvl2Sp(SizeProfileMapperDTO sizeProfileMapperDTO) {
        PlanSizeProfile response = sizeProfileMapperDTO.getResponse();
        SizeResponseDTO sizeResponseDTO = sizeProfileMapperDTO.getSizeResponseDTO();
        if (response.getPlanId() == null) {
            response.setPlanId(sizeResponseDTO.getPlanId());
        }
        if (response.getLvl0Nbr() == null)
            response.setLvl0Nbr(sizeResponseDTO.getLvl0Nbr());
        if (response.getLvl1Nbr() == null)
            response.setLvl1Nbr(sizeResponseDTO.getLvl1Nbr());
        if (response.getLvl2Nbr() == null)
            response.setLvl2Nbr(sizeResponseDTO.getLvl2Nbr());
        response.setLvl3List(mapSizeProfileLvl3Sp(sizeProfileMapperDTO));
    }

    private List<Lvl3List> mapSizeProfileLvl3Sp(SizeProfileMapperDTO sizeProfileMapperDTO) {
        List<Lvl3List> lvl3List = Optional.ofNullable(sizeProfileMapperDTO.getResponse().getLvl3List()).orElse(new ArrayList<>());

        lvl3List.stream()
                .filter(lvl3 -> sizeProfileMapperDTO.getSizeResponseDTO().getLvl3Nbr().equals(lvl3.getLvl3Nbr())).findFirst()
                .ifPresentOrElse(lvl3 -> lvl3.setLvl4List(mapSizeProfileLvl4Sp(sizeProfileMapperDTO, lvl3)),
                        () -> setLvl3SP(sizeProfileMapperDTO, lvl3List));
        return lvl3List;
    }

    private void setLvl3SP(SizeProfileMapperDTO sizeProfileMapperDTO, List<Lvl3List> lvl3List) {
        Lvl3List lvl3 = new Lvl3List();
        lvl3.setLvl3Nbr(sizeProfileMapperDTO.getSizeResponseDTO().getLvl3Nbr());
        lvl3.setLvl4List(mapSizeProfileLvl4Sp(sizeProfileMapperDTO, lvl3));
        lvl3List.add(lvl3);
    }

    private List<Lvl4List> mapSizeProfileLvl4Sp(SizeProfileMapperDTO sizeProfileMapperDTO, Lvl3List lvl3) {
        List<Lvl4List> lvl4DtoList = Optional.ofNullable(lvl3.getLvl4List()).orElse(new ArrayList<>());

        lvl4DtoList.stream()
                .filter(lvl4 -> sizeProfileMapperDTO.getSizeResponseDTO().getLvl4Nbr().equals(lvl4.getLvl4Nbr())).findFirst()
                .ifPresentOrElse(lvl4 -> lvl4.setFinelines(mapSizeProfileFlSp(sizeProfileMapperDTO, lvl4)),
                        () -> setLvl4SP(sizeProfileMapperDTO, lvl4DtoList));
        return lvl4DtoList;
    }

    private void setLvl4SP(SizeProfileMapperDTO sizeProfileMapperDTO, List<Lvl4List> lvl4DtoList) {
        Lvl4List lvl4 = new Lvl4List();
        lvl4.setLvl4Nbr(sizeProfileMapperDTO.getSizeResponseDTO().getLvl4Nbr());
        lvl4.setFinelines(mapSizeProfileFlSp(sizeProfileMapperDTO, lvl4));
        lvl4DtoList.add(lvl4);
    }

    private List<FineLine> mapSizeProfileFlSp(SizeProfileMapperDTO sizeProfileMapperDTO, Lvl4List lvl4) {
        List<FineLine> finelineDtoList = Optional.ofNullable(lvl4.getFinelines()).orElse(new ArrayList<>());
        SizeResponseDTO sizeResponseDTO = sizeProfileMapperDTO.getSizeResponseDTO();

        finelineDtoList.stream()
                .filter(finelineDto -> sizeProfileMapperDTO.getSizeResponseDTO().getFinelineNbr().equals(finelineDto.getFinelineNbr())).findFirst()
                .ifPresentOrElse(finelineDto -> {
                            if (Objects.isNull(sizeProfileMapperDTO.getStyleNbr()) && Objects.isNull(sizeProfileMapperDTO.getCcId())) {
                                List<ClusterDto> clusters = Optional.ofNullable(finelineDto.getClusters()).orElse(new ArrayList<>());
                                finelineDto.setClusters(mapSizeProfileClusterSp(sizeResponseDTO.getFineLineSizeObj(), sizeResponseDTO.getClusterId(), clusters, sizeProfileMapperDTO.getEligibleAhsSizeIds()));
                            } else {
                                finelineDto.setMerchMethods(mapSizeProfileMerchMethodSp(sizeResponseDTO, finelineDto));
                                finelineDto.setStyles(mapSizeProfileStyleSp(sizeProfileMapperDTO, finelineDto));
                            }
                        },
                        () -> setFinelineSP(sizeProfileMapperDTO, finelineDtoList));
        return finelineDtoList;
    }

    private void setFinelineSP(SizeProfileMapperDTO sizeProfileMapperDTO, List<FineLine> finelineDtoList) {
        SizeResponseDTO sizeResponseDTO = sizeProfileMapperDTO.getSizeResponseDTO();
        FineLine fineLine = new FineLine();
        Metrics metricsObj = new Metrics();
        fineLine.setFinelineNbr(sizeResponseDTO.getFinelineNbr());
        fineLine.setMetrics(metricsObj);
        if (Objects.nonNull(sizeProfileMapperDTO.getValidationResponseList())) {
            fineLine.setMetadata(
                    MetadataSPResponse.builder()
                            .validationData(sizeAndPackValidationMapper.getFinelineValidation(sizeResponseDTO, sizeProfileMapperDTO.getValidationResponseList()))
                            .build());
        }
        if (Objects.isNull(sizeProfileMapperDTO.getStyleNbr()) && Objects.isNull(sizeProfileMapperDTO.getCcId())) {
            List<ClusterDto> clusters = Optional.ofNullable(fineLine.getClusters()).orElse(new ArrayList<>());
            fineLine.setClusters(mapSizeProfileClusterSp(sizeResponseDTO.getFineLineSizeObj(), sizeResponseDTO.getClusterId(), clusters, sizeProfileMapperDTO.getEligibleAhsSizeIds()));
        } else {
            if (Objects.isNull(sizeProfileMapperDTO.getStyleNbr())) {
            fineLine.setMerchMethods(mapSizeProfileMerchMethodSp(sizeResponseDTO, fineLine)); }
            fineLine.setStyles(mapSizeProfileStyleSp(sizeProfileMapperDTO, fineLine));
        }
        finelineDtoList.add(fineLine);
    }

    private List<MerchMethodsDto> mapSizeProfileMerchMethodSp(SizeResponseDTO finelineResponse, FineLine fineline) {
        List<MerchMethodsDto> merchMethodsDtos = Optional.ofNullable(fineline.getMerchMethods()).orElse(new ArrayList<>());
        if (finelineResponse.getFixtureTypeId() != null) {
        merchMethodsDtos.stream()
                .filter(merchMethodsDto -> merchMethodsDto.getFixtureTypeRollupId().equals(finelineResponse.getFixtureTypeId())).findFirst()
                .ifPresentOrElse(merchMethodsDto -> log.info("Merch Method is already added for fineline: {}", fineline.getFinelineNbr()),
                        () -> setFinelineMerchMethod(finelineResponse, merchMethodsDtos)); }
        return merchMethodsDtos;
    }

    private void setFinelineMerchMethod(SizeResponseDTO finelineResponse, List<MerchMethodsDto> merchMethodsDtos) {
        MerchMethodsDto merchMethodsDto = new MerchMethodsDto();
        merchMethodsDto.setFixtureTypeRollupId(finelineResponse.getFixtureTypeId());
        merchMethodsDto.setMerchMethod(MerchMethod.getMerchMethodFromId(finelineResponse.getMerchMethodCode()));
        merchMethodsDto.setMerchMethodCode(finelineResponse.getMerchMethodCode());
        merchMethodsDtos.add(merchMethodsDto);
    }

    private List<StyleDto> mapSizeProfileStyleSp(SizeProfileMapperDTO sizeProfileMapperDTO, FineLine fineline) {
        List<StyleDto> styleDtoList = Optional.ofNullable(fineline.getStyles()).orElse(new ArrayList<>());
        SizeResponseDTO sizeResponseDTO = sizeProfileMapperDTO.getSizeResponseDTO();

        styleDtoList.stream()
                .filter(styleDto -> sizeResponseDTO.getStyleNbr().equals(styleDto.getStyleNbr())).findFirst()
                .ifPresentOrElse(styleDto -> {
                            if (Objects.isNull(sizeProfileMapperDTO.getCcId())) {
                                List<ClusterDto> clusters = Optional.ofNullable(styleDto.getClusters()).orElse(new ArrayList<>());
                                styleDto.setClusters(mapSizeProfileClusterSp(sizeResponseDTO.getStyleSizeObj(), sizeResponseDTO.getClusterId(), clusters, sizeProfileMapperDTO.getEligibleAhsSizeIds()));
                            } else
                                styleDto.setCustomerChoices(mapSizeProfileCcSp(sizeProfileMapperDTO, styleDto));
                        },
                        () -> setStyleSP(sizeProfileMapperDTO, styleDtoList));
        return styleDtoList;
    }

    private void setStyleSP(SizeProfileMapperDTO sizeProfileMapperDTO, List<StyleDto> styleDtoList) {
        SizeResponseDTO sizeResponseDTO = sizeProfileMapperDTO.getSizeResponseDTO();
        StyleDto styleDto = new StyleDto();
        Metrics metricsObj = new Metrics();
        styleDto.setStyleNbr(sizeResponseDTO.getStyleNbr());
        styleDto.setMetadata(MetadataSPResponse.builder().validationData(ValidationSPResponse.builder().sizeProfilePctList(new HashSet<>()).build()).build());
        styleDto.setMetrics(metricsObj);
        if (Objects.isNull(sizeProfileMapperDTO.getCcId())) {
            List<ClusterDto> clusters = Optional.ofNullable(styleDto.getClusters()).orElse(new ArrayList<>());
            styleDto.setClusters(mapSizeProfileClusterSp(sizeResponseDTO.getStyleSizeObj(), sizeResponseDTO.getClusterId(), clusters, sizeProfileMapperDTO.getEligibleAhsSizeIds()));
        } else {
            styleDto.setCustomerChoices(mapSizeProfileCcSp(sizeProfileMapperDTO, styleDto));
        }
        styleDtoList.add(styleDto);
    }

    private List<CustomerChoiceList> mapSizeProfileCcSp(SizeProfileMapperDTO sizeProfileMapperDTO, StyleDto styleDto) {
        List<CustomerChoiceList> customerChoiceDtoList = Optional.ofNullable(styleDto.getCustomerChoices()).orElse(new ArrayList<>());
        SizeResponseDTO sizeResponseDTO = sizeProfileMapperDTO.getSizeResponseDTO();
        styleDto.getMetadata().getValidationData().getSizeProfilePctList().addAll(sizeAndPackValidationMapper.getStyleValidation(sizeResponseDTO, sizeProfileMapperDTO.getValidationResponseList()));

        customerChoiceDtoList.stream()
                .filter(customerChoiceDto -> sizeResponseDTO.getCcId().equals(customerChoiceDto.getCcId())).findFirst()
                .ifPresentOrElse(customerChoiceDto -> {
                            List<ClusterDto> clusters = Optional.ofNullable(customerChoiceDto.getClusters()).orElse(new ArrayList<>());
                            clusters.stream()
                                    .filter(clusterDto -> sizeResponseDTO.getClusterId().equals(clusterDto.getClusterID())).findFirst()
                                    .ifPresentOrElse(clusterDto -> log.info("Cluster already existing for cc: {}", customerChoiceDto.getCcId()),
                                            () -> customerChoiceDto.setClusters(mapSizeProfileClusterSp(sizeResponseDTO.getCcSizeObj(), sizeResponseDTO.getClusterId(), clusters, sizeProfileMapperDTO.getEligibleAhsSizeIds())));
                        },
                        () -> setCcSP(sizeProfileMapperDTO, customerChoiceDtoList));
        return customerChoiceDtoList;
    }

    private void setCcSP(SizeProfileMapperDTO sizeProfileMapperDTO, List<CustomerChoiceList> customerChoiceDtoList) {
        SizeResponseDTO sizeResponseDTO = sizeProfileMapperDTO.getSizeResponseDTO();
        CustomerChoiceList customerChoiceList = new CustomerChoiceList();
        Metrics metricsObj = new Metrics();
        customerChoiceList.setColorFamily(sizeResponseDTO.getColorFamily());
        customerChoiceList.setCcId(sizeResponseDTO.getCcId());
        customerChoiceList.setMetrics(metricsObj);
        customerChoiceList.setMetadata(MetadataSPResponse.builder()
                        .validationData(sizeAndPackValidationMapper.getCcValidation(sizeResponseDTO, sizeProfileMapperDTO.getValidationResponseList(), true))
                .build());
        List<ClusterDto> clusters = Optional.ofNullable(customerChoiceList.getClusters()).orElse(new ArrayList<>());
        customerChoiceList.setClusters(mapSizeProfileClusterSp(sizeResponseDTO.getCcSizeObj(), sizeResponseDTO.getClusterId(), clusters, sizeProfileMapperDTO.getEligibleAhsSizeIds()));
        customerChoiceDtoList.add(customerChoiceList);
    }

    private List<ClusterDto> mapSizeProfileClusterSp(String sizeObj, Integer clusterId, List<ClusterDto> clusters, Set<Integer> eligibleAhsSizeIds) {
        ClusterDto clusterDto = new ClusterDto();
        clusterDto.setClusterID(clusterId);
        clusterDto.setSizes(setSizeProfiles(sizeObj, eligibleAhsSizeIds));
        clusterDto.setTotalSizeProfilePct((double) (Math.round(calculateClusterTotalSizeProfilePct(clusterDto.getSizes()))));
        clusters.add(clusterDto);
        return clusters;
    }

    private static Double calculateClusterTotalSizeProfilePct(List<SizeProfileDTO> sizes) {
        final Double ZERO = 0.0;
        try {
            return Optional.ofNullable(sizes)
                    .stream()
                    .flatMap(Collection::stream)
                    .filter(Objects::nonNull)
                    .mapToDouble(sizeProfile -> {
                        //Favor adjusted size profile if present, otherwise use size profile.  0.0 if both are null
                        return sizeProfile.getMetrics() != null
                                ? Optional.ofNullable(sizeProfile.getMetrics().getAdjSizeProfilePct())
                                .orElse(Optional.ofNullable(sizeProfile.getMetrics().getSizeProfilePct()).orElse(ZERO))
                                : ZERO;
                    })
                    .sum();
        } catch (NullPointerException npe) {
            return ZERO;
        }
    }

    private List<SizeProfileDTO> setSizeProfiles(String sizeObj, Set<Integer> eligibleAhsSizeIds) {
        List<SizeProfileDTO> sizeDto = new ArrayList<>();
        List<SizeProfileDTO> sizeLists = new ArrayList<>();
        try {
            if (sizeObj != null) {
                sizeDto = Arrays.asList(objectMapper.readValue(sizeObj, SizeProfileDTO[].class));
            }
        } catch (JsonProcessingException jsonProcessingException) {
            log.error("Error parsing fineline size: ", jsonProcessingException);
            throw new CustomException("Error Parsing fineline size profile");
        }
        sizeDto.stream().filter(sizeProfileDTO -> eligibleAhsSizeIds.contains(sizeProfileDTO.getAhsSizeId())).forEach(dto1 -> {
            SizeProfileDTO sizeProfileDTO = new SizeProfileDTO();
            Metrics metrics = new Metrics();
            if (dto1.getSizeProfilePrcnt() != null) {
                metrics.setSizeProfilePct(dto1.getSizeProfilePrcnt());
            } else metrics.setSizeProfilePct((double) 0);

            if (dto1.getAdjustedSizeProfile() != null) {
                metrics.setAdjSizeProfilePct(dto1.getAdjustedSizeProfile());
            } else metrics.setAdjSizeProfilePct((double) 0);
            sizeProfileDTO.setAhsSizeId(dto1.getAhsSizeId());
            sizeProfileDTO.setSizeDesc(dto1.getSizeDesc());
            sizeProfileDTO.setMetrics(metrics);
            sizeLists.add(sizeProfileDTO);
        });
        return sizeLists;
    }
}
