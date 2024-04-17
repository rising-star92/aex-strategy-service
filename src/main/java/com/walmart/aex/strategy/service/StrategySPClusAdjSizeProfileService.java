package com.walmart.aex.strategy.service;

import com.walmart.aex.strategy.dto.SpreadSizeProfileResponse;
import com.walmart.aex.strategy.dto.request.*;
import com.walmart.aex.strategy.entity.StrategyCcSPClusId;
import com.walmart.aex.strategy.entity.StrategyCcSPCluster;
import com.walmart.aex.strategy.entity.StrategyFineLineSPCluster;
import com.walmart.aex.strategy.entity.StrategyStyleSPCluster;
import com.walmart.aex.strategy.enums.ChannelType;
import com.walmart.aex.strategy.enums.StratGroupType;
import com.walmart.aex.strategy.properties.AppProperties;
import com.walmart.aex.strategy.properties.FeatureConfigProperties;
import com.walmart.aex.strategy.repository.StratCcSPClusRepository;
import com.walmart.aex.strategy.repository.StratFineLineSPClusRepository;
import com.walmart.aex.strategy.repository.StratStyleSPClusRepository;
import com.walmart.aex.strategy.repository.StrategyGroupRepository;
import io.strati.ccm.utils.client.annotation.ManagedConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.walmart.aex.strategy.service.SizeAndPackService.*;
import static com.walmart.aex.strategy.util.Constant.FEATURE_COMING_SOON;

@Service
@Transactional
@Slf4j
public class StrategySPClusAdjSizeProfileService {

    @Autowired
    AdjustedSizeProfileUpdateMapper adjustedSizeProfileUpdateMapper;

    @Autowired
    StratFineLineSPClusRepository stratFineLineSpRep;

    @Autowired
    StratStyleSPClusRepository stratStyleSPClusRepo;

    @Autowired
    StratCcSPClusRepository stratCcSPClusRepository;

    @Autowired
    StrategyGroupRepository strategyGroupRepository;

    @Autowired
    StratCcSPClusRepository stratCcRepo;

    @Autowired
    private SPSizeProfileAdjustmentService spSizeProfileAdjustmentService;

    @ManagedConfiguration
    private AppProperties appProperties;

    @ManagedConfiguration
    private FeatureConfigProperties featureConfigProperties;

    @Autowired
    private SizeAndPackService sizeAndPackService;
    private static final Integer CAL_SPREAD_INDICATOR_ENABLE = 1;
    private static final Integer CAL_SPREAD_INDICATOR_DISABLE = 0;

    public void updateStratFinelineSPClusAdjustedSizeProfile(UpdateFinelineAdjSizeProfileRequest finelineSPClusRequest) {
        Integer ahsSizeId = finelineSPClusRequest.getAhsSizeId();
        Integer clusterId = finelineSPClusRequest.getClusterId();
        Double adjSizePct = finelineSPClusRequest.getAdjSizePct();
        Integer channelId = ChannelType.getChannelIdFromName(finelineSPClusRequest.getChannel());
        Long strategyId = strategyGroupRepository.getStrategyIdBySeasonCd(StratGroupType.SIZE_PROFILE.getStrategyGroupTypeCode(), null, null);

        List<StrategyFineLineSPCluster> finelineSPClusterList;

        if (clusterId == 0) {
            finelineSPClusterList = stratFineLineSpRep.getStrategyFinelineSPAllCluster(
                    finelineSPClusRequest.getPlanId(), finelineSPClusRequest.getFineline(),
                    channelId, strategyId);
        } else {
            finelineSPClusterList = stratFineLineSpRep.getStrategyFinelineSPClusterData(
                    finelineSPClusRequest.getPlanId(), finelineSPClusRequest.getFineline(),
                    finelineSPClusRequest.getClusterId(), channelId, strategyId);
        }

        adjustedSizeProfileUpdateMapper.updateFinelineAdjSizeProfile(finelineSPClusterList, ahsSizeId, clusterId, adjSizePct);
        stratFineLineSpRep.saveAll(finelineSPClusterList);
    }

    public void updateStratStyleSPClusAdjustedSizeProfile(UpdateStyleAdjSizeProfileRequest styleSPClusRequest) {
        Integer channelId = ChannelType.getChannelIdFromName(styleSPClusRequest.getChannel());
        Integer ahsSizeId = styleSPClusRequest.getAhsSizeId();
        Integer clusterId = styleSPClusRequest.getClusterId();
        Double adjSizePct = styleSPClusRequest.getAdjSizePct();
        Long strategyId = strategyGroupRepository.getStrategyIdBySeasonCd(StratGroupType.SIZE_PROFILE.getStrategyGroupTypeCode(), null, null);

        List<StrategyStyleSPCluster> styleSPClusterList;
      
        if (clusterId == 0) {
            styleSPClusterList = stratStyleSPClusRepo.getStrategyStyleSPAllClusterData(styleSPClusRequest.getPlanId(), styleSPClusRequest.getFineline(),
                    styleSPClusRequest.getStyle(), channelId, strategyId);
        } else {
            styleSPClusterList = stratStyleSPClusRepo.getStrategyStyleSPClusterData(styleSPClusRequest.getPlanId(), styleSPClusRequest.getClusterId(), styleSPClusRequest.getFineline(),
                    styleSPClusRequest.getStyle(), channelId, strategyId);
        }

        adjustedSizeProfileUpdateMapper.updateStyleAdjSizeProfile(styleSPClusterList, ahsSizeId, clusterId, adjSizePct);
        stratStyleSPClusRepo.saveAll(styleSPClusterList);
    }

    public void updateCustomerChoicesAdjSizeProfile(UpdateCustomerChoicesAdjSizeProfileRequest customerChoiceRequest) {
        Integer ahsSizeId = customerChoiceRequest.getAhsSizeId();
        Integer channelId = ChannelType.getChannelIdFromName(customerChoiceRequest.getChannel());
        Integer clusterId = customerChoiceRequest.getClusterId();
        Double adjSizePct = customerChoiceRequest.getAdjSizePct();
        Long strategyId = strategyGroupRepository.getStrategyIdBySeasonCd(StratGroupType.SIZE_PROFILE.getStrategyGroupTypeCode(), null, null);

        List<StrategyCcSPCluster> ccSPClusterList;
      
        if (clusterId == 0) {
            ccSPClusterList = stratCcSPClusRepository.getStrategyCcAllSPClusterData(customerChoiceRequest.getPlanId(),
                    customerChoiceRequest.getFinelineNbr(), channelId,
                    customerChoiceRequest.getStyle(), customerChoiceRequest.getCustomerChoice(), strategyId);
        } else {
            ccSPClusterList = stratCcSPClusRepository.getStrategyCcSPClusterData(customerChoiceRequest.getPlanId(),
                    customerChoiceRequest.getFinelineNbr(), customerChoiceRequest.getClusterId(), channelId,
                    customerChoiceRequest.getStyle(), customerChoiceRequest.getCustomerChoice(), strategyId);
        }

        adjustedSizeProfileUpdateMapper.updateCustomerChoiceAdjSizeProfile(ccSPClusterList, ahsSizeId, clusterId, adjSizePct);
        stratCcSPClusRepository.saveAll(ccSPClusterList);
    }

    public SpreadSizeProfileResponse updateCustomerChoicesAdjSizeProfileActiveSpreadIndicator(SpreadSizeProfileRequest spreadSizeProfileRequest) {
        log.info("Adjust All CC size profiles for {}", spreadSizeProfileRequest);
        SpreadSizeProfileResponse response = new SpreadSizeProfileResponse();
        if (!Boolean.parseBoolean(appProperties.getSPSpreadFeatureFlag())) {
            response.setStatus(FEATURE_COMING_SOON);
            return response;
        }
        Integer channelId = ChannelType.getChannelIdFromName(spreadSizeProfileRequest.getChannel());
        try {
            List<StrategyCcSPCluster> ccSPClusterDTOList = stratCcSPClusRepository.getStrategyCcAllSPClusterDataWithActiveSpreadInd(spreadSizeProfileRequest.getPlanId(),
                    channelId, CAL_SPREAD_INDICATOR_ENABLE);

            //creating a map with eligible StrategyCcSPClusId as key and ahs ids as value
            HashMap<StrategyCcSPClusId, Set<Integer>> eligibleMap = new HashMap();
            List<String> nonEligibleCc = new ArrayList<>();
            List<StrategyCcSPClusId> ccSPClusterIdListWithNonZeroAnalyticsClusId = getCcSPClusterIdListWithZeroAnalyticsClusId(ccSPClusterDTOList);
            for (StrategyCcSPCluster ccSPClusterDTO : ccSPClusterDTOList) {
                if (!CollectionUtils.isEmpty(ccSPClusterIdListWithNonZeroAnalyticsClusId) && ccSPClusterIdListWithNonZeroAnalyticsClusId.contains(ccSPClusterDTO.getStrategyCcSPClusId())) {
                    Set<Integer> eligibleSizeIds = sizeAndPackService.getEligibleSizeIds(ccSPClusterDTO.getSizeProfileObj());
                    if (!eligibleSizeIds.isEmpty()) {
                        eligibleMap.put(ccSPClusterDTO.getStrategyCcSPClusId(), eligibleSizeIds);
                    } else {
                        nonEligibleCc.add(ccSPClusterDTO.getStrategyCcSPClusId().getCcId());
                    }
                }
            }
            // find respective StrategyCcSPCluster list with size profile obj to adjust
            for (StrategyCcSPClusId id : eligibleMap.keySet()) {
                List<StrategyCcSPCluster> strategyCcSPClusterEligibleList = getEligibleList(ccSPClusterDTOList, id);
                //adjustment of cc size profile starts here
                if (!CollectionUtils.isEmpty(strategyCcSPClusterEligibleList)) {
                    ccSPClusterDTOList = getAdjustedStrategyCcSPClusters(ccSPClusterDTOList, eligibleMap, id, strategyCcSPClusterEligibleList);
                }
            }

            ccSPClusterDTOList.forEach(cc -> {
                cc.setCalcSpSpreadInd(CAL_SPREAD_INDICATOR_DISABLE);
                if (nonEligibleCc.contains(cc.getStrategyCcSPClusId().getCcId())) {
                    cc.setTotalSizeProfilePct(DEFAULT_SIZE_PROFILE_PCT);
                }
            });
            stratCcRepo.saveAll(ccSPClusterDTOList);
            response.setStatus(SUCCESS_STATUS);
        } catch (Exception e) {
            log.error("Exception while adjusting cc level size profiles :", e);
            response.setStatus(FAILED_STATUS);
        }
        return response;
    }

    private List<StrategyCcSPClusId> getCcSPClusterIdListWithZeroAnalyticsClusId(List<StrategyCcSPCluster> ccSPClusterDTOList) {
        return ccSPClusterDTOList.stream()
                .map(StrategyCcSPCluster::getStrategyCcSPClusId)
                .filter(strategyCcSPClusId -> strategyCcSPClusId.getStrategyStyleSPClusId().getStrategyFinelineSPClusId().getStrategySubCatgSPClusId().getStrategyMerchCatgSPClusId().getPlanClusterStrategyId().getAnalyticsClusterId().equals(0))
                .collect(Collectors.toList());
    }

    private List<StrategyCcSPCluster> getAdjustedStrategyCcSPClusters(List<StrategyCcSPCluster> ccSPClusterDTOList, HashMap<StrategyCcSPClusId, Set<Integer>> map, StrategyCcSPClusId id, List<StrategyCcSPCluster> ccSPClusterDTOListForProcessing) {
        List<StrategyCcSPCluster> ccSPClusterDTOListAfterProcessing = getProcessedStrategyCcSPClusters(map, id, ccSPClusterDTOListForProcessing);
        //update the original cc DTO list with updated size object
        ccSPClusterDTOList = ccSPClusterDTOList.stream()
                .peek(ccSPClusterDTO -> {
                    for (StrategyCcSPCluster ccSPClusterObj : ccSPClusterDTOListAfterProcessing) {
                        if (ccSPClusterObj.getStrategyCcSPClusId().equals(ccSPClusterDTO.getStrategyCcSPClusId())) {
                            ccSPClusterDTO.setSizeProfileObj(ccSPClusterObj.getSizeProfileObj());
                            //Update total size profile percentage
                            if (featureConfigProperties.getTotalSizePercentFeature()) {
                                StrategyCcSPClusId strategyCcSPClusIdKey = sizeAndPackService.getStratCCSPIdKey(map, ccSPClusterDTO);
                                ccSPClusterDTO.setTotalSizeProfilePct(sizeAndPackService.getTotalSizeProfilePct(ccSPClusterObj.getSizeProfileObj(), map.get(strategyCcSPClusIdKey)));
                            }
                        }
                    }
                }).collect(Collectors.toList());
        return ccSPClusterDTOList;
    }

    private List<StrategyCcSPCluster> getProcessedStrategyCcSPClusters(Map<StrategyCcSPClusId, Set<Integer>> map, StrategyCcSPClusId id, List<StrategyCcSPCluster> ccSPClusterDTOListForProcessing) {
        return ccSPClusterDTOListForProcessing.stream()
                .peek(ccSPClusterDTO -> {
                    List<SizeProfileDTO> ccSPClusterSizeProfileDTOForProcessing = sizeAndPackService.safeReadSizeObject(ccSPClusterDTO.getSizeProfileObj());
                    boolean isAllEligSizeHaveMissingRecommendation = findIfAllEligibleSizeIdsHaveNoRecommendations(ccSPClusterSizeProfileDTOForProcessing,map.get(id));
                    List<SizeProfileDTO> ccSPClusterSizeProfileDTOProcessed;
                    if (!isAllEligSizeHaveMissingRecommendation) {
                        ccSPClusterSizeProfileDTOProcessed = spSizeProfileAdjustmentService.processSizeProfileAdjustment(ccSPClusterSizeProfileDTOForProcessing, map.get(id));
                    } else {
                        //in case user removes the size and none of the sizes dont have recommendation but have default value i.e. 2% , we need to reset it back to null
                        ccSPClusterSizeProfileDTOProcessed = spSizeProfileAdjustmentService.resetEligibleSizesWithoutRecommendation(ccSPClusterSizeProfileDTOForProcessing, map.get(id));
                    }
                    ccSPClusterDTO.setSizeProfileObj(ccSPClusterSizeProfileDTOProcessed.toString());

                }).collect(Collectors.toList());
    }

    private List<StrategyCcSPCluster> getEligibleList(List<StrategyCcSPCluster> ccSPClusterDTOList, StrategyCcSPClusId map) {
        return ccSPClusterDTOList.stream()
                .filter(os -> os.getStrategyCcSPClusId().getCcId().equals(map.getCcId())
                        && os.getStrategyCcSPClusId().getStrategyStyleSPClusId().getStyleNbr().equals(map.getStrategyStyleSPClusId().getStyleNbr())
                        && os.getStrategyCcSPClusId().getStrategyStyleSPClusId().getStrategyFinelineSPClusId().getFinelineNbr().equals(map.getStrategyStyleSPClusId().getStrategyFinelineSPClusId().getFinelineNbr())
                        && os.getStrategyCcSPClusId().getStrategyStyleSPClusId().getStrategyFinelineSPClusId().getStrategySubCatgSPClusId().getLvl4Nbr().equals(map.getStrategyStyleSPClusId().getStrategyFinelineSPClusId().getStrategySubCatgSPClusId().getLvl4Nbr())
                        && os.getStrategyCcSPClusId().getStrategyStyleSPClusId().getStrategyFinelineSPClusId().getStrategySubCatgSPClusId().getStrategyMerchCatgSPClusId().getPlanClusterStrategyId().getAnalyticsClusterId() != 0
                )
                .collect(Collectors.toList());
    }

    /**
     * Checks if all selected (eligible) sizes have no recommended size profile
     * @param sizeProfileDTOS
     * @param eligibleAhsSizeIds
     * @return
     */
    boolean findIfAllEligibleSizeIdsHaveNoRecommendations(List<SizeProfileDTO> sizeProfileDTOS, Set<Integer> eligibleAhsSizeIds) {
        return sizeProfileDTOS.stream()
                .filter(sizeProfileDTO -> eligibleAhsSizeIds.contains(sizeProfileDTO.getAhsSizeId()))
                .allMatch(sizeProfileDTO -> Objects.isNull(sizeProfileDTO.getSizeProfilePrcnt()));
    }

}
