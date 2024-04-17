package com.walmart.aex.strategy.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.strategy.dto.*;
import com.walmart.aex.strategy.dto.assortproduct.RFAFinelineData;
import com.walmart.aex.strategy.dto.assortproduct.RFARequest;
import com.walmart.aex.strategy.dto.assortproduct.RFASpaceResponse;
import com.walmart.aex.strategy.dto.assortproduct.RFAStylesCcData;
import com.walmart.aex.strategy.dto.request.CustomerChoiceSP;
import com.walmart.aex.strategy.dto.request.SizeProfileDTO;
import com.walmart.aex.strategy.dto.request.StyleSP;
import com.walmart.aex.strategy.entity.*;
import com.walmart.aex.strategy.enums.ChannelType;
import com.walmart.aex.strategy.enums.StratGroupType;
import com.walmart.aex.strategy.exception.CustomException;
import com.walmart.aex.strategy.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SizeEligibilityService {
    public static final String SUCCESS_STATUS = "Success";
    public static final String FAILED_STATUS = "Failed";

    private final StrategyFinelineRepository strategyFinelineRepository;
    private final PlanStrategySizeEligMapper planStrategySizeEligMapper;
    private final StrategyCcRepository strategyCcRepository;
    private final StrategyGroupRepository strategyGroupRepository;
    private final ObjectMapper objectMapper;
    private final SizeEligibilityRepository sizeEligibilityRepository;
    private final SizeEligibilityMapper sizeEligibilityMapper;
    private final AssortProductService assortProductService;
    private final SizeAndPackService sizeAndPackService;
    private final StratCcSPClusRepository stratCcSPClusRepository;

    public SizeEligibilityService(StrategyFinelineRepository strategyFinelineRepository,
                                  PlanStrategySizeEligMapper planStrategySizeEligMapper, StrategyCcRepository strategyCcRepository,
                                  StrategyGroupRepository strategyGroupRepository, ObjectMapper objectMapper,
                                  SizeEligibilityRepository sizeEligibilityRepository, SizeEligibilityMapper sizeEligibilityMapper,
                                  AssortProductService assortProductService,SizeAndPackService sizeAndPackService,
                                  StratCcSPClusRepository stratCcSPClusRepository) {
        this.strategyFinelineRepository = strategyFinelineRepository;
        this.planStrategySizeEligMapper = planStrategySizeEligMapper;
        this.strategyCcRepository = strategyCcRepository;
        this.strategyGroupRepository = strategyGroupRepository;
        this.objectMapper = objectMapper;
        this.sizeEligibilityRepository = sizeEligibilityRepository;
        this.sizeEligibilityMapper = sizeEligibilityMapper;
        this.assortProductService = assortProductService;
        this.sizeAndPackService = sizeAndPackService;
        this.stratCcSPClusRepository = stratCcSPClusRepository;
    }

    public PlanStrategySPResponse fetchSubCategories(Long planId, String channel) {
        PlanStrategySPResponse response = new PlanStrategySPResponse();
        try {
            List<SizeResponseDTO> sizeResponseDTOList = strategyFinelineRepository.getCategoriesWithSize(planId,
                    getStrategyIdBySeasonCd(), ChannelType.getChannelIdFromName(channel));
            if (channel.equalsIgnoreCase(ChannelType.STORE.getDescription())) {
                RFASpaceResponse rfaSpaceResponse = getRFAData(planId, null);
                if (rfaSpaceResponse.getRfaFinelineData() != null) {
                    sizeResponseDTOList = getRFAFinelineSpaceAllocatedData(sizeResponseDTOList, rfaSpaceResponse.getRfaFinelineData());
                }
            }
            SizeEligMapperDTO sizeEligMapperDTO = getSizeEligMapperDTO(response, Collections.emptyList(), null, null, channel);
            Optional.of(sizeResponseDTOList).stream().flatMap(Collection::stream)
                    .forEach(sizeResponseDTO -> {
                        sizeEligMapperDTO.setSizeResponseDTO(sizeResponseDTO);
                        planStrategySizeEligMapper.mapPlanStrategyLvl2Sp(sizeEligMapperDTO);
                    });
            response.setStatus(SUCCESS_STATUS);
        } catch (Exception e) {
            log.error("Exception While fetching Category size profiles :", e);
            throw new CustomException("Failed to fetch size profile Fineline data, due to" + e);
        }
        log.info("Fetch Size Profile Fineline response: {}", response);
        return response;
    }
    public PlanStrategySPResponse fetchSubCategoriesWithSizeAssociation(Long planId, String channel) {
        PlanStrategySPResponse response = new PlanStrategySPResponse();
        try {
            List<StrategyGroup> strategyGroups = getStrategyIds(List.of(StratGroupType.SIZE_PROFILE.getStrategyGroupTypeCode(), StratGroupType.PRESENTATION_UNITS.getStrategyGroupTypeCode()));
            Long sizeStrategyId = getStrategyId(strategyGroups, StratGroupType.SIZE_PROFILE.getStrategyGroupTypeCode());
            Long presentationStrategyId = getStrategyId(strategyGroups, StratGroupType.PRESENTATION_UNITS.getStrategyGroupTypeCode());
            if (Objects.isNull(sizeStrategyId) || Objects.isNull(presentationStrategyId)) {
                log.error("Size Strategy ID / Presentation Strategy ID is missing: {} | {}", sizeStrategyId, presentationStrategyId);
                response.setStatus(FAILED_STATUS);
                return response;
            }

            List<SizeResponseDTO> sizeResponseDTOList = strategyFinelineRepository.getCategoriesWithSize(planId,
                    sizeStrategyId, ChannelType.getChannelIdFromName(channel));
            List<SizeResponseDTO> validationSizeResponseList = getValidationSizeResponseList(planId, channel, sizeStrategyId, presentationStrategyId);

            if (channel.equalsIgnoreCase(ChannelType.STORE.getDescription())) {
                RFASpaceResponse rfaSpaceResponse = getRFAData(planId, null);
                if (rfaSpaceResponse.getRfaFinelineData() != null) {
                    sizeResponseDTOList = getRFAFinelineSpaceAllocatedData(sizeResponseDTOList, rfaSpaceResponse.getRfaFinelineData());
                    validationSizeResponseList = getRFAFinelineCcSpaceAllocatedData(validationSizeResponseList, rfaSpaceResponse.getRfaFinelineData());
                }
            }
            SizeEligMapperDTO sizeEligMapperDTO = getSizeEligMapperDTO(response, validationSizeResponseList, null, null, channel);
            Optional.of(sizeResponseDTOList)
                    .stream()
                    .flatMap(Collection::stream)
                    .filter(sizeResponseDTO -> !sizeAndPackService.getEligibleSizeIds(sizeResponseDTO.getFineLineSizeObj()).isEmpty())
                    .forEach(sizeResponseDTO -> {
                        sizeEligMapperDTO.setSizeResponseDTO(sizeResponseDTO);
                        planStrategySizeEligMapper.mapPlanStrategyLvl2Sp(sizeEligMapperDTO);
                    });
            response.setStatus(SUCCESS_STATUS);
        } catch (Exception e) {
            log.error("Exception While fetching Category size profiles :", e);
            throw new CustomException("Failed to fetch size profile Fineline data, due to" + e);
        }
        log.info("Fetch Size Profile Fineline response: {}", response);
        return response;
    }

    public PlanStrategySPResponse fetchCcSize(Long planId, Integer finelineNbr, String channel) {
        PlanStrategySPResponse response = new PlanStrategySPResponse();
        try {
            List<SizeResponseDTO> sizeResponseDTOList = getRFAStyleCcSpaceAllocatedData(planId, finelineNbr, channel, getStrategyIdBySeasonCd());
            SizeEligMapperDTO sizeEligMapperDTO = getSizeEligMapperDTO(response, Collections.emptyList(), finelineNbr, null, channel);
            Optional.of(sizeResponseDTOList)
                    .stream()
                    .flatMap(Collection::stream)
                    .forEach(sizeResponseDTO -> {
                        sizeEligMapperDTO.setSizeResponseDTO(sizeResponseDTO);
                        planStrategySizeEligMapper.mapPlanStrategyLvl2Sp(sizeEligMapperDTO);
                    });
            response.setStatus(SUCCESS_STATUS);
        } catch (Exception e) {
            log.error("Exception while fetching CC size profiles :", e);
            throw new CustomException("Failed to fetch CC size profile data, due to" + e);
        }
        log.info("Fetch Size Profile CC response: {}", response);
        return response;
    }


	public PlanStrategySPResponse getFinelineWithoutSizeAssociation(Long planId, String channel) {
		PlanStrategySPResponse response = new PlanStrategySPResponse();
		try {
			response = fetchSubCategories(planId, channel);
		} catch (Exception e) {
			log.error("Exception while fetching Fineline size profiles :", e);
			throw new CustomException("Failed to fetch Fineline size profile data, due to" + e);
		}

		log.info("Fetch Fineline's with without any size association for response: {}", response);
        if (Objects.nonNull(response)) {
            List<Lvl3ListSPResponse> lvl3ListSPResponses = response.getLvl3List();
            List<Lvl3ListSPResponse> lvl3WithUnassociatedSizes = new ArrayList<>();
            for (Lvl3ListSPResponse lvl3ListSPResponse : lvl3ListSPResponses) {
                List<Lvl4ListSPResponse> lvl4WithUnassociatedSizes = new ArrayList<>();
                List<Lvl4ListSPResponse> lvl4ListSPResponses = lvl3ListSPResponse.getLvl4List();
                for (Lvl4ListSPResponse lvl4ListSPResponse : lvl4ListSPResponses) {
                    List<FineLineSPResponse> finelines = lvl4ListSPResponse.getFinelines();
                    getFinelineWithUnAssociatedSizeData(finelines);
                    if (CollectionUtils.isEmpty(finelines)) {
                        lvl4WithUnassociatedSizes.add(lvl4ListSPResponse);
                    }
                }
                lvl4ListSPResponses.removeAll(lvl4WithUnassociatedSizes);
                if (CollectionUtils.isEmpty(lvl4ListSPResponses)) {
                    lvl3WithUnassociatedSizes.add(lvl3ListSPResponse);
                }
            }
            lvl3ListSPResponses.removeAll(lvl3WithUnassociatedSizes);
        }
		return response;
	}


    public PlanStrategySPResponse getCcByFinelineWithoutSizeAssociation(Long planId, Integer finelineNbr, String channel) {
        PlanStrategySPResponse response = new PlanStrategySPResponse();
        try {
            response = fetchCcSize(planId, finelineNbr, channel);
        } catch (Exception e) {
            log.error("Exception while fetching CC size profiles :", e);
            throw new CustomException("Failed to fetch CC size profile data, due to" + e);
        }

        log.info("Fetch CCs with without any size association for response: {}", response);
        if (Objects.nonNull(response)) {
            for(Lvl3ListSPResponse lvl3ListSPResponse : response.getLvl3List()) {
                for(Lvl4ListSPResponse lvl4ListSPResponse : lvl3ListSPResponse.getLvl4List()) {
                    for(FineLineSPResponse fineLineSPResponse : lvl4ListSPResponse.getFinelines()) {
                        for (StyleSP styleSP : fineLineSPResponse.getStyles()) {
                            List<CustomerChoiceSP> customerChoiceSPS = styleSP.getCustomerChoices();
                            getCcWithUnAssociatedSizeData(customerChoiceSPS);
                        }
                    }
                }
            }
        }
        return response;
    }

    public PlanStrategySPResponse fetchStylesCCsWithAssociation(Long planId, String channel, Integer finelineNbr) {
        PlanStrategySPResponse response = new PlanStrategySPResponse();
        try {
            List<StrategyGroup> strategyGroups = getStrategyIds(List.of(StratGroupType.SIZE_PROFILE.getStrategyGroupTypeCode(), StratGroupType.PRESENTATION_UNITS.getStrategyGroupTypeCode()));
            Long sizeStrategyId = getStrategyId(strategyGroups, StratGroupType.SIZE_PROFILE.getStrategyGroupTypeCode());
            Long presentationStrategyId = getStrategyId(strategyGroups, StratGroupType.PRESENTATION_UNITS.getStrategyGroupTypeCode());
            if (Objects.isNull(sizeStrategyId) || Objects.isNull(presentationStrategyId)) {
                log.error("Size Strategy ID / Presentation Strategy ID is missing: {} | {}", sizeStrategyId, presentationStrategyId);
                response.setStatus(FAILED_STATUS);
                return response;
            }

            List<SizeResponseDTO> sizeResponseDTOList = getRFAStyleCcSpaceAllocatedData(planId, finelineNbr, channel, sizeStrategyId);
            List<SizeResponseDTO> validationSizeResponseList = getValidationSizeResponseList(planId, channel, sizeStrategyId, presentationStrategyId);
            SizeEligMapperDTO sizeEligMapperDTO = getSizeEligMapperDTO(response, validationSizeResponseList, finelineNbr, null, channel);
            Optional.of(sizeResponseDTOList)
                    .stream()
                    .flatMap(Collection::stream)
                    .filter(sizeResponseDTO -> !sizeAndPackService.getEligibleSizeIds(sizeResponseDTO.getCcSizeObj()).isEmpty())
                    .forEach(sizeResponseDTO -> {
                        sizeEligMapperDTO.setSizeResponseDTO(sizeResponseDTO);
                        planStrategySizeEligMapper.mapPlanStrategyLvl2Sp(sizeEligMapperDTO);
                    });
            response.setStatus(SUCCESS_STATUS);
        } catch (Exception e) {
            log.error("Exception while fetching Style and CC data with size association :", e);
            throw new CustomException("Failed to fetch Style and CC data with size association, due to" + e);
        }
        log.info("Fetch Style and CC with size association response: {}", response);
        return response;
    }

    public PlanStrategySPResponse fetchSizeByCatg(Long planId, Integer lvl3Nbr, String channel) {
        PlanStrategySPResponse response = new PlanStrategySPResponse();
        Integer catgFlag = 1;
        try {
            List<SizeResponseDTO> sizeResponseDTOList = strategyCcRepository
                    .getSizeByCatg(planId, getStrategyIdBySeasonCd(), lvl3Nbr, ChannelType.getChannelIdFromName(channel));
            SizeEligMapperDTO sizeEligMapperDTO = getSizeEligMapperDTO(response, Collections.emptyList(), null, catgFlag, channel);
            Optional.of(sizeResponseDTOList)
                    .stream()
                    .flatMap(Collection::stream)
                    .forEach(sizeResponseDTO -> {
                        sizeEligMapperDTO.setSizeResponseDTO(sizeResponseDTO);
                        sizeEligMapperDTO.setFinelineNbr(sizeResponseDTO.getFinelineNbr());
                        planStrategySizeEligMapper.mapPlanStrategyLvl2Sp(sizeEligMapperDTO);
                    });
        } catch (Exception e) {
            log.error("Exception while fetching category level size profiles :", e);
            throw new CustomException("Failed to fetch CC size profile data, due to" + e);
        }
        log.info("Fetch Size Profile CC response: {}", response);

        return response;
    }

    @Transactional
    public void updatePlanStrategyForSizeCluster(PlanStrategySP request, String channel) {
        PlanStrategySPResponse responseDTO = new PlanStrategySPResponse();
        Integer channelId = ChannelType.getChannelIdFromName(channel);
        try {
            log.info("Received the payload from strategy listener for CLP & Analytics: {}",
                    objectMapper.writeValueAsString(request));
        } catch (JsonProcessingException exp) {
            responseDTO.setStatus(FAILED_STATUS);
            log.error("Couldn't parse the payload sent to Strategy Listener. Error: {}", exp.toString());
        }
        PlanStrategyId planStrategyId = new PlanStrategyId();
        planStrategyId.setPlanId(request.getPlanId());
        planStrategyId.setStrategyId(getStrategyIdBySeasonCd());
        PlanClusterStrategyId planClusterStrategyId = new PlanClusterStrategyId();
        planClusterStrategyId.setPlanStrategyId(planStrategyId);
        planClusterStrategyId.setAnalyticsClusterId(0);
        log.info("Check if a planStrategy Id : {} already exists or not", planStrategyId.toString());
        for (Lvl3ListSP lvl3 : request.getLvl3List()) {
            sizeEligibilityRepository
                    .saveAll(sizeEligibilityMapper.updateCategorySizes(channelId, planClusterStrategyId, lvl3));
        }
    }

    private List<SizeResponseDTO> getRFAFinelineSpaceAllocatedData(List<SizeResponseDTO> sizeResponseDTOList, List<RFAFinelineData> rfaFinelineDataList) {
        return Optional.of(sizeResponseDTOList)
                .stream()
                .flatMap(Collection::stream)
                .filter(sizeResponseDTO -> ObjectUtils.allNotNull(sizeResponseDTO.getLvl3Nbr(), sizeResponseDTO.getLvl4Nbr(), sizeResponseDTO.getFinelineNbr())
                        && getRfaFineline(sizeResponseDTO, rfaFinelineDataList) != null)
                .collect(Collectors.toList());
    }

    private List<SizeResponseDTO> getRFAFinelineCcSpaceAllocatedData(List<SizeResponseDTO> sizeResponseDTOList, List<RFAFinelineData> rfaFinelineDataList) {
        return Optional.of(sizeResponseDTOList)
                .stream()
                .flatMap(Collection::stream)
                .filter(sizeResponseDTO -> ObjectUtils.allNotNull(sizeResponseDTO.getFinelineNbr(), sizeResponseDTO.getCcId())
                        && rfaFinelineCcExist(sizeResponseDTO, rfaFinelineDataList))
                .collect(Collectors.toList());
    }

    private List<SizeResponseDTO> getRFAStyleCcSpaceAllocatedData(Long planId, Integer finelineNbr, String channel, Long strategyId) {
        List<SizeResponseDTO> sizeResponseDTOList = getSizeResponseDTOS(planId, channel, finelineNbr, strategyId);
        if (planId != null && channel.equalsIgnoreCase(ChannelType.STORE.getDescription())) {
            RFASpaceResponse rfaSpaceResponse = getRFAData(planId, finelineNbr);
            if (rfaSpaceResponse.getRfaStylesCcData() != null) {
                List<RFAStylesCcData> rfaStyleCCData = rfaSpaceResponse.getRfaStylesCcData();
                return Optional.of(sizeResponseDTOList)
                        .stream()
                        .flatMap(Collection::stream)
                        .filter(sizeResponseDTO -> ObjectUtils.allNotNull(sizeResponseDTO.getStyleNbr(), sizeResponseDTO.getCcId())
                                && getRfaStyleCc(sizeResponseDTO, rfaStyleCCData) != null)
                        .collect(Collectors.toList());
            }
        }
        return sizeResponseDTOList;
    }

    private RFAFinelineData getRfaFineline(SizeResponseDTO sizeResponseDTO, List<RFAFinelineData> rfaFinelineDataList) {
        return Optional.ofNullable(rfaFinelineDataList)
                .stream()
                .flatMap(Collection::stream)
                .filter(rfaFinelineData -> ObjectUtils.allNotNull(rfaFinelineData.getRpt_lvl_3_nbr(), rfaFinelineData.getRpt_lvl_4_nbr(), rfaFinelineData.getFineline_nbr())
                        && rfaFinelineData.getRpt_lvl_3_nbr().equals(sizeResponseDTO.getLvl3Nbr())
                        && rfaFinelineData.getRpt_lvl_4_nbr().equals(sizeResponseDTO.getLvl4Nbr())
                        && rfaFinelineData.getFineline_nbr().contains(sizeResponseDTO.getFinelineNbr())
                )
                .findFirst()
                .orElse(null);
    }

    private boolean rfaFinelineCcExist(SizeResponseDTO sizeResponseDTO, List<RFAFinelineData> rfaFinelineDataList) {
        return Optional.ofNullable(rfaFinelineDataList)
                .stream()
                .flatMap(Collection::stream)
                .anyMatch(rfaFinelineData -> ObjectUtils.allNotNull(rfaFinelineData.getFineline_nbr(), rfaFinelineData.getCustomer_choice())
                        && rfaFinelineData.getFineline_nbr().contains(sizeResponseDTO.getFinelineNbr())
                        && rfaFinelineData.getCustomer_choice().contains(sizeResponseDTO.getCcId())
                );
    }

    private RFAStylesCcData getRfaStyleCc(SizeResponseDTO sizeResponseDTO, List<RFAStylesCcData> rfaStyleCCDataList) {
        return Optional.ofNullable(rfaStyleCCDataList)
                .stream()
                .flatMap(Collection::stream)
                .filter(rfaStylesCcData -> ObjectUtils.allNotNull(rfaStylesCcData.getStyle_nbr(), rfaStylesCcData.getCustomer_choice())
                        && rfaStylesCcData.getStyle_nbr().equalsIgnoreCase(sizeResponseDTO.getStyleNbr())
                        && rfaStylesCcData.getCustomer_choice().contains(sizeResponseDTO.getCcId())
                )
                .findFirst()
                .orElse(null);
    }

    private RFASpaceResponse getRFAData(Long planId, Integer finelineNbr) {
        RFASpaceResponse response = new RFASpaceResponse();
        RFARequest rfaRequest = new RFARequest();
        rfaRequest.setPlanId(planId);
        rfaRequest.setFinelineNbr(finelineNbr);
        try {
            response = assortProductService.getRFASpaceDataOutput(rfaRequest);
        } catch (Exception e) {
            log.error("Exception retrieving RFA data: ", e);
        }
        return response;
    }

    private List<SizeResponseDTO> getSizeResponseDTOS(Long planId, String channel, Integer finelineNbr, Long strategyId) {
        return strategyCcRepository
                .getCcSizeByFineline(planId, strategyId, finelineNbr, ChannelType.getChannelIdFromName(channel));
    }

    private Long getStrategyIdBySeasonCd() {
        return strategyGroupRepository.getStrategyIdBySeasonCd(StratGroupType.SIZE_PROFILE.getStrategyGroupTypeCode(), null, null);
    }

    private List<StrategyGroup> getStrategyIds(List<Integer> strategyGroupType) {
        return strategyGroupRepository.findAllByStrategyGroupTypeIdIn(strategyGroupType.stream().map(Long::valueOf).collect(Collectors.toList())).orElse(Collections.emptyList());
    }

    private List<SizeResponseDTO> getValidationSizeResponseList(Long planId, String channel, Long sizeStrategyId, Long presentationStrategyId) {
        if (channel.equalsIgnoreCase(ChannelType.STORE.getDescription())) {
            return stratCcSPClusRepository.getMerchMethodAndTotalSizePct(planId, sizeStrategyId, presentationStrategyId, ChannelType.getChannelIdFromName(channel));
        } else {
            return stratCcSPClusRepository.getTotalSizePct(planId, sizeStrategyId, ChannelType.getChannelIdFromName(channel));
        }
    }

    private Long getStrategyId(List<StrategyGroup> strategyGroups, Integer strategyGroupTypeCode) {
        return strategyGroups.stream()
                .filter(sg -> sg.getStrategyGroupTypeId().equals(Long.valueOf(strategyGroupTypeCode)))
                .findAny()
                .map(StrategyGroup::getStrategyId)
                .orElse(null);
    }

    public List<CustomerChoiceSP> getCcWithUnAssociatedSizeData(List<CustomerChoiceSP> customerChoiceSPList) {
        log.info("ViewExceptionFlow : Fetching those CCs without any size association from the list : {}", customerChoiceSPList);
        try {
            ListIterator<CustomerChoiceSP> iter = customerChoiceSPList.listIterator();
            while(iter.hasNext()){
                CustomerChoiceSP customerChoiceSP = iter.next();
                if (customerChoiceSP != null && customerChoiceSP.getStrategy() != null) {
                    List<SizeProfileDTO> sizeProfileDTOS = customerChoiceSP.getStrategy().getSizeProfile();
                    boolean assignedSizes = false;
                    for (SizeProfileDTO sizeProfileDTO : sizeProfileDTOS) {
                        //If for any size, if isEligible is 1, then skip that cc
                        //Get only those cc whose status is not equal 1
                        if (Objects.nonNull(sizeProfileDTO.getIsEligible()) && sizeProfileDTO.getIsEligible() == 1) {
                            assignedSizes = true;
                            break;
                        }
                    }

                    if (assignedSizes) {
                        //remove the cc which has size association
                        iter.remove();
                    }
                }
            }
        } catch (Exception jsonProcessingException) {
            log.error("Error parsing cc size: ", jsonProcessingException);
            throw new CustomException("Error Parsing cc size profile");
        }
        return customerChoiceSPList;
    }


    public List<FineLineSPResponse> getFinelineWithUnAssociatedSizeData(List<FineLineSPResponse> fineLineSPResponsesList) {
        log.info("ViewExceptionFlow : Fetching those Fineline's without any size association from the list : {}", fineLineSPResponsesList);
        try {
            ListIterator<FineLineSPResponse> iter = fineLineSPResponsesList.listIterator();
            while(iter.hasNext()){
            	FineLineSPResponse fineLineSPResponse = iter.next();
                if (fineLineSPResponse != null && fineLineSPResponse.getStrategy() != null) {
                    List<SizeProfileDTO> sizeProfileDTOS = fineLineSPResponse.getStrategy().getSizeProfile();
                    boolean assignedSizes = false;
                    for (SizeProfileDTO sizeProfileDTO : sizeProfileDTOS) {
                        //If for any size, if isEligible is 1, then skip that fineline
                        //Get only those fineline whose status is not equal 1
                        if (Objects.nonNull(sizeProfileDTO.getIsEligible()) && sizeProfileDTO.getIsEligible() == 1) {
                            assignedSizes = true;
                            break;
                        }
                    }

                    if (assignedSizes) {
                        //remove the fineline which has size association
                        iter.remove();
                    }
                }
            }
        } catch (Exception jsonProcessingException) {
            log.error("Error parsing fineline size: ", jsonProcessingException);
            throw new CustomException("Error Parsing fineline size profile");
        }
        return fineLineSPResponsesList;
    }

    private static SizeEligMapperDTO getSizeEligMapperDTO(PlanStrategySPResponse response, List<SizeResponseDTO> validationSizeResponseList, Integer finelineNbr, Integer catgFlag, String channel) {
        return SizeEligMapperDTO.builder()
                .validationSizeResponseList(validationSizeResponseList)
                .response(response)
                .finelineNbr(finelineNbr)
                .catgFlag(catgFlag)
                .channel(channel)
                .build();
    }

}
