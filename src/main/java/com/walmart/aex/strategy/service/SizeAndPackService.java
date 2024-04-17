package com.walmart.aex.strategy.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.strategy.dto.MerchMethodResponse;
import com.walmart.aex.strategy.dto.SPMerchMethodFixtureResponse;
import com.walmart.aex.strategy.dto.SPMerchMethodResponse;
import com.walmart.aex.strategy.dto.SizeResponseDTO;
import com.walmart.aex.strategy.dto.request.SPMerchMethodFixtureRequest;
import com.walmart.aex.strategy.dto.request.SizeProfileDTO;
import com.walmart.aex.strategy.dto.request.SizeProfileRequest;
import com.walmart.aex.strategy.dto.sizepack.SPMerchMethLvl3;
import com.walmart.aex.strategy.dto.sizeprofile.*;
import com.walmart.aex.strategy.entity.PlanStrategyId;
import com.walmart.aex.strategy.entity.StrategyCcSPClusId;
import com.walmart.aex.strategy.entity.StrategyCcSPCluster;
import com.walmart.aex.strategy.entity.StrategyMerchCatgFixture;
import com.walmart.aex.strategy.enums.ChannelType;
import com.walmart.aex.strategy.enums.StratGroupType;
import com.walmart.aex.strategy.exception.CustomException;
import com.walmart.aex.strategy.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;

@Service
@Slf4j
public class SizeAndPackService {

    public static final String FAILED_STATUS = "Failed";
    public static final String SUCCESS_STATUS = "Success";
    public static final double DEFAULT_SIZE_PROFILE_PCT = -1.00;

    private final FixtureAllocationStrategyRepository fixtureAllocationStrategyRepository;

    private final SPMerchMethFixtureMapper spMerchMethFixtureMapper;

    private final StrategyGroupRepository strategyGroupRepository;

    private final StrategyFinelineRepository strategyFinelineRepository;

    private final FinelineSizeProfileRepo finelineSizeProfileRepo;

    private final SizeProfileMapper sizeProfileMapper;

    private final StyleSizeProfileRepo styleSizeProfileRepo;

    private final CcSizeProfileRepo ccSizeProfileRepo;

    private final ObjectMapper objectMapper;

    public SizeAndPackService(SPMerchMethFixtureMapper spMerchMethFixtureMapper, StrategyGroupRepository strategyGroupRepository,
                              StrategyFinelineRepository strategyFinelineRepository, FixtureAllocationStrategyRepository fixtureAllocationStrategyRepository,
                              FinelineSizeProfileRepo finelineSizeProfileRepo, SizeProfileMapper sizeProfileMapper, StyleSizeProfileRepo styleSizeProfileRepo,
                              CcSizeProfileRepo ccSizeProfileRepo, ObjectMapper objectMapper) {
        this.spMerchMethFixtureMapper = spMerchMethFixtureMapper;
        this.strategyGroupRepository = strategyGroupRepository;
        this.strategyFinelineRepository = strategyFinelineRepository;
        this.fixtureAllocationStrategyRepository = fixtureAllocationStrategyRepository;
        this.finelineSizeProfileRepo = finelineSizeProfileRepo;
        this.sizeProfileMapper = sizeProfileMapper;
        this.styleSizeProfileRepo = styleSizeProfileRepo;
        this.ccSizeProfileRepo = ccSizeProfileRepo;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public SPMerchMethodFixtureResponse updateMerchMethod(SPMerchMethodFixtureRequest request) {
        SPMerchMethodResponse response = new SPMerchMethodResponse();
        //TODO:
        //Long strategyId = strategyGroupRepository.getStrategyIdBySeasonCd(StratGroupType.SIZE_PROFILE.getStrategyGroupTypeCode(), CommonUtil.getSeasonCode(request.getPlanDesc()));
        Long strategyId = strategyGroupRepository.getStrategyIdBySeasonCd(StratGroupType.PRESENTATION_UNITS.getStrategyGroupTypeCode(), null, null);
        PlanStrategyId planStrategyId = PlanStrategyId.builder()
                .planId(request.getPlanId())
                .strategyId(strategyId)
                .build();
        log.info("Check if a planStrategy Id : {} already exists or not for given set merch method planID", planStrategyId.toString());
        List<StrategyMerchCatgFixture> strategyMerchCatgFixtures = new ArrayList<>();
        try {
            for (SPMerchMethLvl3 lvl3 : request.getLvl3List()) {
                strategyMerchCatgFixtures.addAll(spMerchMethFixtureMapper.updateFixtureMerchMethod(lvl3, planStrategyId));
            }
            fixtureAllocationStrategyRepository.saveAll(strategyMerchCatgFixtures);
            response.setStatus(SUCCESS_STATUS);
        } catch (Exception e) {
            response.setStatus(FAILED_STATUS);
            response.setMessage(e.getMessage());
            log.error("Failed to set merch method fixture due to :", e);
        }
        log.info("Set Merch method response: {}", response);
        return fetchMerchMethodFixture(request.getPlanId(), request.getPlanDesc(), getLvl3Nbr(request));
    }

    public SPMerchMethodFixtureResponse fetchMerchMethodFixture(Long planId, String planDesc, Integer lvl3Nbr) {
        SPMerchMethodFixtureResponse response = new SPMerchMethodFixtureResponse();
        try {
            Long strategyId = strategyGroupRepository.getStrategyIdBySeasonCd(StratGroupType.PRESENTATION_UNITS.getStrategyGroupTypeCode(), null, null);
            List<MerchMethodResponse> merchMethodResponses = strategyFinelineRepository
                    .getMerchMethod(planId, strategyId, ChannelType.getChannelIdFromName(ChannelType.STORE.getDescription()), lvl3Nbr);
            if (!CollectionUtils.isEmpty(merchMethodResponses)) {
                Optional.of(merchMethodResponses)
                        .stream()
                        .flatMap(Collection::stream)
                        .forEach(merchMethodResponse -> spMerchMethFixtureMapper.mapMerchMethodFixture(merchMethodResponse, response));
            }
        } catch (Exception e) {
            log.error("Error occurred :", e);
            throw new CustomException("Failed to fetch merch method fixture data, due to" + e);
        }
        log.info("Fetch merch method fixture response: {}", response);
        return response;
    }

    private Integer getLvl3Nbr(SPMerchMethodFixtureRequest request) {
        return Optional.ofNullable(request.getLvl3List())
                .stream()
                .flatMap(Collection::stream)
                .findFirst()
                .map(SPMerchMethLvl3::getLvl3Nbr)
                .orElse(null);
    }

    public PlanSizeProfile fetchFinelineSizeProfile(SizeProfileRequest sizeProfileRequest) {
        PlanSizeProfile planSizeProfile = new PlanSizeProfile();
        Integer finelineNbr = sizeProfileRequest.getFinelineNbr();
        Long planId = sizeProfileRequest.getPlanId();
        Long strategyId = strategyGroupRepository.getStrategyIdBySeasonCd(StratGroupType.SIZE_PROFILE.getStrategyGroupTypeCode(), null, null);
        try {
            List<SizeResponseDTO> finelineResponseList = finelineSizeProfileRepo
                    .getFinelineSizeProfiles(planId, strategyId, ChannelType.getChannelIdFromName(sizeProfileRequest.getChannel()), finelineNbr);

            String eligibleSizeObj = getEligibleSizeObj(finelineResponseList, SizeResponseDTO::getFineLineSizeObj);

            Optional.of(finelineResponseList)
                    .stream()
                    .flatMap(Collection::stream)
                    .forEach(fineLineResponse -> sizeProfileMapper
                            .mapSizeProfileLvl2Sp(SizeProfileMapperDTO.builder()
                                    .sizeResponseDTO(fineLineResponse)
                                    .response(planSizeProfile)
                                    .eligibleAhsSizeIds(getEligibleSizeIds(eligibleSizeObj))
                                    .build()));
        } catch (Exception e) {
            log.error("Exception While fetching Fineline Size Profiles :", e);
            throw new CustomException("Failed to fetch Fineline Size Profiles, due to" + e);
        }
        log.info("Fetch Size Profile Fineline response: {}", planSizeProfile);
        return planSizeProfile;
    }

    public PlanSizeProfile fetchStyleSizeProfile(SizeProfileRequest sizeProfileRequest, String styleNbr) {
        PlanSizeProfile planSizeProfile = new PlanSizeProfile();
        Integer finelineNbr = sizeProfileRequest.getFinelineNbr();

        Long planId = sizeProfileRequest.getPlanId();
        Long strategyId = strategyGroupRepository.getStrategyIdBySeasonCd(StratGroupType.SIZE_PROFILE.getStrategyGroupTypeCode(), null, null);
        try {
            List<SizeResponseDTO> styleResponseList = styleSizeProfileRepo
                    .getStyleSizeProfiles(planId, strategyId, finelineNbr, ChannelType.getChannelIdFromName(sizeProfileRequest.getChannel()), styleNbr);

            String eligibleSizeObj = getEligibleSizeObj(styleResponseList, SizeResponseDTO::getStyleSizeObj);

            Optional.of(styleResponseList)
                    .stream()
                    .flatMap(Collection::stream)
                    .forEach(styleResponse -> sizeProfileMapper
                            .mapSizeProfileLvl2Sp(SizeProfileMapperDTO.builder()
                                    .sizeResponseDTO(styleResponse)
                                    .response(planSizeProfile)
                                    .styleNbr(styleNbr)
                                    .eligibleAhsSizeIds(getEligibleSizeIds(eligibleSizeObj))
                                    .build()));
        } catch (Exception e) {
            log.error("Exception While fetching Style Size Profiles :", e);
            throw new CustomException("Failed to fetch Style Size Profiles, due to" + e);
        }
        log.info("Fetch Style Size Profile response: {}", planSizeProfile);
        return planSizeProfile;
    }

    public PlanSizeProfile fetchCcSizeProfile(SizeProfileRequest sizeProfileRequest, String ccId) {
        PlanSizeProfile planSizeProfile = new PlanSizeProfile();
        Integer finelineNbr = sizeProfileRequest.getFinelineNbr();

        Long planId = sizeProfileRequest.getPlanId();
        Long strategyId = strategyGroupRepository.getStrategyIdBySeasonCd(StratGroupType.SIZE_PROFILE.getStrategyGroupTypeCode(), null, null);
        try {
            List<SizeResponseDTO> ccResponseList = ccSizeProfileRepo
                    .getCcSizeByFineline(planId, strategyId, finelineNbr, ChannelType.getChannelIdFromName(sizeProfileRequest.getChannel()), sizeProfileRequest.getStyleNbr(), ccId);

            String eligibleSizeObj = getEligibleSizeObj(ccResponseList, SizeResponseDTO::getCcSizeObj);

            Optional.of(ccResponseList)
                    .stream()
                    .flatMap(Collection::stream)
                    .forEach(ccResponse -> sizeProfileMapper
                            .mapSizeProfileLvl2Sp(SizeProfileMapperDTO.builder()
                                    .sizeResponseDTO(ccResponse)
                                    .response(planSizeProfile)
                                    .ccId(ccId)
                                    .eligibleAhsSizeIds(getEligibleSizeIds(eligibleSizeObj))
                                    .build()));

            setAvgSizeProfilePct(sizeProfileRequest, getClusterDtos(planSizeProfile), null);
        } catch (Exception e) {
            log.error("Exception While fetching CC Size Profiles :", e);
            throw new CustomException("Failed to fetch CC Size Profiles, due to" + e);
        }
        log.info("Fetch Buy Qty Fineline response: {}", planSizeProfile);
        return planSizeProfile;
    }

    public PlanSizeProfile fetchAllCcSizeProfile(SizeProfileRequest sizeProfileRequest) {
        PlanSizeProfile planSizeProfile = new PlanSizeProfile();
        Integer finelineNbr = sizeProfileRequest.getFinelineNbr();

        log.info("Fetch All CC size profiles for {}", sizeProfileRequest);

        Long planId = sizeProfileRequest.getPlanId();
        Long strategyId = strategyGroupRepository.getStrategyIdBySeasonCd(StratGroupType.SIZE_PROFILE.getStrategyGroupTypeCode(), null, null);
        Long presentationUnitsStrategyId = strategyGroupRepository.getStrategyIdBySeasonCd(StratGroupType.PRESENTATION_UNITS.getStrategyGroupTypeCode(), null, null);
        try {
            List<SizeResponseDTO> ccResponseList = ccSizeProfileRepo
                    .getAllCcSizeByFineline(planId, strategyId, presentationUnitsStrategyId, finelineNbr, ChannelType.getChannelIdFromName(sizeProfileRequest.getChannel()), null, null);

            Map<String, String> ccSizeEligibleMap = new HashMap<>();
            if (!CollectionUtils.isEmpty(ccResponseList)) {
                Optional.of(ccResponseList)
                        .stream()
                        .flatMap(Collection::stream)
                        .filter(sizeResponseDTO -> sizeResponseDTO.getClusterId().equals(0))
                        .forEach(sizeResponseDTO -> {
                            if (!ccSizeEligibleMap.containsKey(sizeResponseDTO.getCcId())) {
                                ccSizeEligibleMap.put(sizeResponseDTO.getCcId(), sizeResponseDTO.getCcSizeObj());
                            }
                        });
            }

            List<SizeResponseDTO> validationResponseList = new ArrayList<>(ccResponseList);
            if (sizeProfileRequest.getChannel().equalsIgnoreCase(ChannelType.STORE.getDescription())) {
                validationResponseList.removeIf(size -> size.getClusterId() == 0);
            }
            Optional.of(ccResponseList)
                    .stream()
                    .flatMap(Collection::stream)
                    .forEach(ccResponse -> sizeProfileMapper
                            .mapSizeProfileLvl2Sp(SizeProfileMapperDTO.builder()
                                    .sizeResponseDTO(ccResponse)
                                    .response(planSizeProfile)
                                    .ccId(ccResponse.getCcId())
                                    .eligibleAhsSizeIds(getEligibleSizeIds(ccSizeEligibleMap.get(ccResponse.getCcId())))
                                    .validationResponseList(validationResponseList)
                                    .channel(sizeProfileRequest.getChannel())
                                    .build()));

            calculateCcSizeProfileAvgs(sizeProfileRequest, planSizeProfile);
        } catch (Exception e) {
            log.error("Exception While fetching CC Size Profiles :", e);
            throw new CustomException("Failed to fetch CC Size Profiles, due to" + e);
        }
        log.info("Fetch Buy Qty Fineline response: {}", planSizeProfile);
        return planSizeProfile;
    }

    private void calculateCcSizeProfileAvgs(SizeProfileRequest sizeProfileRequest, PlanSizeProfile planSizeProfile) {
        List<StyleDto> styleDtos = getStyles(planSizeProfile);

        if (!CollectionUtils.isEmpty(styleDtos)) {
            styleDtos.forEach(styleDto -> {
                if (!CollectionUtils.isEmpty(styleDto.getCustomerChoices())) {
                    styleDto.getCustomerChoices().forEach(customerChoiceList -> {
                        if (!CollectionUtils.isEmpty(customerChoiceList.getClusters())) {
                            setAvgSizeProfilePct(sizeProfileRequest, customerChoiceList.getClusters(), customerChoiceList);
                        }
                    });
                }
            });
        }
    }

    private String getEligibleSizeObj(List<SizeResponseDTO> finelineResponseList, Function<SizeResponseDTO, String> sizeObjFunc) {
        return Optional.ofNullable(finelineResponseList)
                .stream()
                .flatMap(Collection::stream)
                .filter(sizeResponseDTO -> sizeResponseDTO.getClusterId().equals(0))
                .findFirst()
                .map(sizeObjFunc)
                .orElse(null);
    }

    private void setAvgSizeProfilePct(SizeProfileRequest sizeProfileRequest, List<ClusterDto> clusterDtos, CustomerChoiceList customerChoiceList) {
        if (!CollectionUtils.isEmpty(clusterDtos)) {
            Optional.of(clusterDtos)
                    .stream()
                    .flatMap(Collection::stream)
                    .filter(clusterDto -> clusterDto.getClusterID().equals(0))
                    .findFirst().ifPresent(clusterDtoAll -> {
                        clusterDtoAll.getSizes().forEach(size -> {
                            if (ChannelType.ONLINE.getDescription().equalsIgnoreCase(sizeProfileRequest.getChannel()) || clusterDtos.size() == 1) {
                                size.getMetrics().setAvgSizeProfilePct(size.getMetrics().getSizeProfilePct());
                                size.getMetrics().setAdjAvgSizeProfilePct(size.getMetrics().getAdjSizeProfilePct());
                            } else {
                                size.getMetrics().setAvgSizeProfilePct(getAvgSizeProfilePct(clusterDtos, size, Metrics::getSizeProfilePct));
                                size.getMetrics().setAdjAvgSizeProfilePct(getAvgSizeProfilePct(clusterDtos, size, Metrics::getAdjSizeProfilePct));
                            }
                        });
                        if (customerChoiceList != null) {
                            customerChoiceList.getMetrics().setAdjAvgSizeProfilePct(calculateTotalAvgSizeProfilePct(clusterDtoAll.getSizes(), Metrics::getAdjAvgSizeProfilePct));
                            customerChoiceList.getMetrics().setAvgSizeProfilePct(calculateTotalAvgSizeProfilePct(clusterDtoAll.getSizes(), Metrics::getAvgSizeProfilePct));
                        }
                    });
        }
    }

    private static Double calculateTotalAvgSizeProfilePct(List<SizeProfileDTO> sizes, Function<Metrics, Double> metricsDoubleFunction) {
        final Double ZERO = 0.0;
        try {
            return Optional.ofNullable(sizes)
                    .stream()
                    .flatMap(Collection::stream)
                    .filter(Objects::nonNull)
                    .map(SizeProfileDTO::getMetrics)
                    .mapToDouble(metrics -> {
                        return metricsDoubleFunction != null
                                ? Optional.ofNullable(metricsDoubleFunction.apply(metrics))
                                .orElse(ZERO)
                                : ZERO;
                    })
                    .sum();
        } catch (NullPointerException npe) {
            return ZERO;
        }
    }

    Set<Integer> getEligibleSizeIds(String eligibleSizeObj) {
        Set<Integer> eligibleAhsSizeIds = new HashSet<>();
        if (eligibleSizeObj != null) {
            List<SizeProfileDTO> sizeProfileDTOS = safeReadSizeObject(eligibleSizeObj);
            sizeProfileDTOS.forEach(sizeProfileDTO -> {
                if (Objects.nonNull(sizeProfileDTO.getIsEligible()) && sizeProfileDTO.getIsEligible() > 0 && Objects.nonNull(sizeProfileDTO.getSizeDesc())) {
                    eligibleAhsSizeIds.add(sizeProfileDTO.getAhsSizeId());
                }
            });
        }
        return eligibleAhsSizeIds;
    }

    private List<ClusterDto> getClusterDtos(PlanSizeProfile planSizeProfile) {
        return Optional.ofNullable(planSizeProfile.getLvl3List())
                .stream()
                .flatMap(Collection::stream)
                .findFirst()
                .map(Lvl3List::getLvl4List)
                .stream()
                .flatMap(Collection::stream)
                .findFirst()
                .map(Lvl4List::getFinelines)
                .stream()
                .flatMap(Collection::stream)
                .findFirst()
                .map(FineLine::getStyles)
                .stream()
                .flatMap(Collection::stream)
                .findFirst()
                .map(StyleDto::getCustomerChoices)
                .stream()
                .flatMap(Collection::stream)
                .findFirst()
                .map(CustomerChoiceList::getClusters)
                .orElse(null);
    }

    private List<StyleDto> getStyles(PlanSizeProfile planSizeProfile) {
        return Optional.ofNullable(planSizeProfile.getLvl3List())
                .stream()
                .flatMap(Collection::stream)
                .findFirst()
                .map(Lvl3List::getLvl4List)
                .stream()
                .flatMap(Collection::stream)
                .findFirst()
                .map(Lvl4List::getFinelines)
                .stream()
                .flatMap(Collection::stream)
                .findFirst()
                .map(FineLine::getStyles)
                .orElse(null);
    }

    private double getAvgSizeProfilePct(List<ClusterDto> clusterDtos, SizeProfileDTO size, ToDoubleFunction<Metrics> sizeMetrics) {
        return clusterDtos.stream()
                .filter(clusterDto -> clusterDto.getClusterID() > 0)
                .map(ClusterDto::getSizes)
                .flatMap(Collection::stream)
                .filter(sizeList -> sizeList.getAhsSizeId().equals(size.getAhsSizeId()))
                .map(SizeProfileDTO::getMetrics)
                .mapToDouble(sizeMetrics)
                .average()
                .orElse(0);
    }

    /***
     * This method will calculate the sum of adjusted size profile object for all the eligible sizes for a CC
     * @param sizeProfileStr
     * @param eligibleSizeIds
     * @return 0.00 when adjusted size profile is null for all eligible size, -1 when no sizes are eligible and > 0.00 when all adjusted size profiles are not null for eligible sizes
     */
    public double getTotalSizeProfilePct(String sizeProfileStr, Set<Integer> eligibleSizeIds) {
        if(!CollectionUtils.isEmpty(eligibleSizeIds)){
            List<SizeProfileDTO> profileDTOS = safeReadSizeObject(sizeProfileStr);
            return profileDTOS.stream().filter(size -> eligibleSizeIds.contains(size.getAhsSizeId()) && null != size.getAdjustedSizeProfile()).mapToDouble(SizeProfileDTO::getAdjustedSizeProfile).sum();
        }
        return DEFAULT_SIZE_PROFILE_PCT;
    }

    /***
     * This method helps to find the matching key from the mapCCSPClusIdByEligibleSizeIds map , which is then used to fetch the eligible size ids for a given cc when analytics cluster is non zero
     * @param mapCCSPClusIdByEligibleSizeIds
     * @param strategyCcSPCluster
     * @return
     */
    public StrategyCcSPClusId getStratCCSPIdKey(HashMap<StrategyCcSPClusId, Set<Integer>> mapCCSPClusIdByEligibleSizeIds, StrategyCcSPCluster strategyCcSPCluster) {
        return Optional.of(mapCCSPClusIdByEligibleSizeIds.keySet())
                .stream()
                .flatMap(Collection::stream)
                .filter(id -> id.getCcId().equals(strategyCcSPCluster.getStrategyCcSPClusId().getCcId()) &&
                        id.getStrategyStyleSPClusId().getStyleNbr().equals(strategyCcSPCluster.getStrategyCcSPClusId().getStrategyStyleSPClusId().getStyleNbr()) &&
                        id.getStrategyStyleSPClusId().getStrategyFinelineSPClusId().getFinelineNbr().equals(strategyCcSPCluster.getStrategyCcSPClusId().getStrategyStyleSPClusId().getStrategyFinelineSPClusId().getFinelineNbr()))
                .findFirst()
                .orElse(new StrategyCcSPClusId());
    }

    public List<SizeProfileDTO> safeReadSizeObject(String sizeObj) {
        try {
            return Arrays.asList(objectMapper.readValue(sizeObj, SizeProfileDTO[].class));

        } catch (JsonProcessingException e) {
            log.error("Error deserializing size object: {}", sizeObj);
            throw new CustomException("Error deserializing size object");
        } catch (IllegalArgumentException|NullPointerException e) {
            log.warn("Size object provided was null");
            return new ArrayList<>();
        }
    }

}
