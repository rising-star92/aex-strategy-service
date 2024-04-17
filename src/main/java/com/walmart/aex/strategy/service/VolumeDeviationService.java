package com.walmart.aex.strategy.service;


import com.walmart.aex.strategy.dto.Finelines;
import com.walmart.aex.strategy.dto.VDRequest;
import com.walmart.aex.strategy.dto.VDResponseDTO;
import com.walmart.aex.strategy.dto.VolumeDeviationRequests;
import com.walmart.aex.strategy.entity.*;
import com.walmart.aex.strategy.entity.converter.VdLevelCodeConverter;
import com.walmart.aex.strategy.properties.FeatureConfigProperties;
import com.walmart.aex.strategy.repository.FpFinelineVDLevelRepository;
import com.walmart.aex.strategy.repository.StrategyFinelineRepository;
import io.strati.ccm.utils.client.annotation.ManagedConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class VolumeDeviationService {

    private static final Integer FL_STRATEGY_VOLUME_DEVIATION = 1;

   @Autowired
   FpFinelineVDLevelRepository finelineVDLevelRepository;

   @Autowired
   StrategyFinelineRepository strategyFinelineRepository;

  @ManagedConfiguration
  FeatureConfigProperties featureConfigProperties;

    public VDResponseDTO getVolumeDeviationCategory(VDRequest requestData, String authorization) {
        VDResponseDTO vdResponseDTO = new VDResponseDTO();
        List<Finelines> finelinesList = new ArrayList<>();
        if (requestData.getVolumeDeviationRequestsList() != null && requestData.getVolumeDeviationRequestsList().size() > 0) {
            List<VolumeDeviationRequests> volumeDeviationRequestsList = requestData.getVolumeDeviationRequestsList();
            for (VolumeDeviationRequests volumeDeviationRequest : volumeDeviationRequestsList) {
                List<FpFinelineVDCategory> fpFinelineVDCategories = finelineVDLevelRepository.findByPlan_idAndFineline_nbr(volumeDeviationRequest.getPlanId().longValue(), volumeDeviationRequest.getFinelineNbr());
                for (FpFinelineVDCategory fpFinelineVDCategory : fpFinelineVDCategories) {
                    VdLevelCodeConverter vdLevelCodeConverter = new VdLevelCodeConverter();
                    Finelines finelines = Finelines.builder().
                            finelineId(fpFinelineVDCategory.getFpFinelineVDCategoryId().getFinelineNbr())
                            .planId(fpFinelineVDCategory.getFpFinelineVDCategoryId().getPlanId().intValue())
                            .lvl0Nbr(fpFinelineVDCategory.getFpFinelineVDCategoryId().getRptLvl0Nbr())
                            .lvl1Nbr(fpFinelineVDCategory.getFpFinelineVDCategoryId().getRptLvl1Nbr())
                            .lvl2Nbr(fpFinelineVDCategory.getFpFinelineVDCategoryId().getRptLvl2Nbr())
                            .lvl3Nbr(fpFinelineVDCategory.getFpFinelineVDCategoryId().getRptLvl3Nbr())
                            .lvl4Nbr(fpFinelineVDCategory.getFpFinelineVDCategoryId().getRptLvl4Nbr())
                            .volumeDeviationLevel(vdLevelCodeConverter.convertToEntityAttribute(fpFinelineVDCategory.getFpFinelineVDCategoryId().getVdLevelCodeId().getVdLevelCode()).toString())
                            .build();

                    finelinesList.add(finelines);
                }
                if(!featureConfigProperties.getSaveDefaultVolumeDeviation()) {
                    /** If there is no Volume Deviation then assign fineline as default volume deviation  **/
                    List<Integer> finelinesWithoutVD = getFinelineWithoutVolumeDeviation(fpFinelineVDCategories, volumeDeviationRequest.getFinelineNbr());
                    if (!CollectionUtils.isEmpty(finelinesWithoutVD)) {
                        List<StrategyFineline> strategyFinelines = strategyFinelineRepository.findByPlan_idAndFineline_nbr(volumeDeviationRequest.getPlanId().longValue(), finelinesWithoutVD);
                        for (StrategyFineline strategyFineline : strategyFinelines) {
                            VdLevelCodeConverter vdLevelCodeConverter = new VdLevelCodeConverter();
                            StrategyFinelineId strategyFinelineId = strategyFineline.getStrategyFinelineId();
                            StrategyMerchCatgId strategyMerchCatgId = strategyFinelineId.getStrategySubCatgId().getStrategyMerchCatgId();
                            Finelines finelines = Finelines.builder().
                                    finelineId(strategyFinelineId.getFinelineNbr())
                                    .planId(strategyMerchCatgId.getPlanStrategyId().getPlanId().intValue())
                                    .lvl0Nbr(strategyMerchCatgId.getLvl0Nbr())
                                    .lvl1Nbr(strategyMerchCatgId.getLvl1Nbr())
                                    .lvl2Nbr(strategyMerchCatgId.getLvl2Nbr())
                                    .lvl3Nbr(strategyMerchCatgId.getLvl3Nbr())
                                    .lvl4Nbr(strategyFinelineId.getStrategySubCatgId().getLvl4Nbr())
                                    .volumeDeviationLevel(vdLevelCodeConverter.convertToEntityAttribute(FL_STRATEGY_VOLUME_DEVIATION).toString())
                                    .build();
                            finelinesList.add(finelines);
                        }
                    }
                }
            }
            vdResponseDTO.setFinelines(finelinesList);
        }
        return vdResponseDTO;
    }

    private List<Integer> getFinelineWithoutVolumeDeviation(List<FpFinelineVDCategory> fpFinelineVDCategories, List<Integer> finelineNbrFromRequest) {
        if (CollectionUtils.isEmpty(fpFinelineVDCategories)) {
            return finelineNbrFromRequest;
        }
        List<Integer> finelineFromDB = Optional.ofNullable(fpFinelineVDCategories)
                .stream()
                .flatMap(Collection::stream)
                .map(fpFinelineVDCategory -> fpFinelineVDCategory.getFpFinelineVDCategoryId().getFinelineNbr())
                .collect(Collectors.toList());
        return finelineNbrFromRequest.stream()
                .filter(finelineNbr -> !finelineFromDB.contains(finelineNbr))
                .collect(Collectors.toList());
    }


}
