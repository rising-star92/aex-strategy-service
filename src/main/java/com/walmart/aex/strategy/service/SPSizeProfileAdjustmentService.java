package com.walmart.aex.strategy.service;

import com.walmart.aex.strategy.dto.request.SizeProfileDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SPSizeProfileAdjustmentService {
    private static final Double TOTAL_PERCENTAGE = 100.00;
    private static final Double DEFAULT_SIZE_PROFILE = 2.0;

    List<SizeProfileDTO> getAdjustedSizeProfileObject(List<SizeProfileDTO> sizeProfileDTOList)
    {
        double sizeProfilePrcntTotal = sizeProfileDTOList.stream().filter(s->s.getIsEligible()>0 && s.getSizeProfilePrcnt()!=null).mapToDouble(SizeProfileDTO::getAdjustedSizeProfile).sum();
        double adjustedSizeProfilePrcntTotal = sizeProfileDTOList.stream().filter(s->s.getIsEligible()>0 && s.getAdjustedSizeProfile()!=null).mapToDouble(SizeProfileDTO::getAdjustedSizeProfile).sum();
        if(Math.round(adjustedSizeProfilePrcntTotal)!=TOTAL_PERCENTAGE){
            sizeProfileDTOList.stream().filter(sizeProfile -> sizeProfile.getIsEligible() > 0).forEach(sizeProfile -> {
                boolean isAddSizeAssociatedByMerchant = (sizeProfile.getAdjustedSizeProfile().equals(DEFAULT_SIZE_PROFILE) && sizeProfile.getSizeProfilePrcnt() == null);
                boolean isAddSizeByMerchButNoSizeAssociated = !isAddSizeAssociatedByMerchant && (sizeProfile.getSizeProfilePrcnt() != null && sizeProfile.getSizeProfilePrcnt() == 0);

                if (!(isAddSizeAssociatedByMerchant || isAddSizeByMerchButNoSizeAssociated)) {
                    double adjustedSizeProfileUpdated = getAdjustedSizeProfileUpdated(sizeProfile.getAdjustedSizeProfile(), sizeProfilePrcntTotal, TOTAL_PERCENTAGE - adjustedSizeProfilePrcntTotal);
                    sizeProfile.setAdjustedSizeProfile(Math.round(adjustedSizeProfileUpdated*100.0)/100.0);
                }
            });
        }
        return sizeProfileDTOList;
    }

    private double getAdjustedSizeProfileUpdated(double currentPercentage, double totalCurrentPercentage , double addedOrRemovedPercentage) {
        return currentPercentage + ((currentPercentage / totalCurrentPercentage) * (addedOrRemovedPercentage));
    }
    // MTAP-5667
    public List<SizeProfileDTO> addDefaultAdjSizeProfilePct(List<SizeProfileDTO> sizeProfileObj) {

        if (!CollectionUtils.isEmpty(sizeProfileObj)) {
            sizeProfileObj.stream().filter(
                            sizeProfile -> sizeProfile.getIsEligible() > 0 && sizeProfile.getAdjustedSizeProfile() == null)
                    .forEach(sizeProfile -> sizeProfile.setAdjustedSizeProfile(DEFAULT_SIZE_PROFILE));
        }
        return sizeProfileObj;
    }

    public List<SizeProfileDTO> processSizeProfileAdjustment(List<SizeProfileDTO> sizeProfileObj, Set<Integer> ahsIds) {
        List<SizeProfileDTO> updateSizeProfileDTOWithAdjustedSizePrcnt = updateSizeProfileDTOWithisEligibleFlag(sizeProfileObj, ahsIds, 1);
        List<SizeProfileDTO> updateSizeProfileDTO = addDefaultAdjSizeProfilePct(updateSizeProfileDTOWithAdjustedSizePrcnt);
        updateSizeProfileDTOWithAdjustedSizePrcnt = getAdjustedSizeProfileObject(updateSizeProfileDTO);
        updateSizeProfileDTOWithAdjustedSizePrcnt = updateSizeProfileDTOWithisEligibleFlag(updateSizeProfileDTOWithAdjustedSizePrcnt, ahsIds, 0);
        return updateSizeProfileDTOWithAdjustedSizePrcnt;
    }

    private List<SizeProfileDTO> updateSizeProfileDTOWithisEligibleFlag(List<SizeProfileDTO> sizeProfileObj, Set<Integer> ahsIds, int isEligible) {
        return sizeProfileObj.stream()
                .map(s -> {
                    if (ahsIds.contains(s.getAhsSizeId())) {
                        s.setIsEligible(isEligible);
                        if (isEligible == 1) {
                            s.setAdjustedSizeProfile(s.getSizeProfilePrcnt());
                        }
                    }
                    return s;
                })
                .collect(Collectors.toList());
    }

    public List<SizeProfileDTO> resetEligibleSizesWithoutRecommendation(List<SizeProfileDTO> sizeProfileObj, Set<Integer> eligibleSizeAHSIds) {
        if (!CollectionUtils.isEmpty(sizeProfileObj)) {
            sizeProfileObj.stream().filter(
                            sizeProfile -> eligibleSizeAHSIds.contains(sizeProfile.getAhsSizeId()))
                    .forEach(sizeProfile -> sizeProfile.setAdjustedSizeProfile(null));
        }
        return sizeProfileObj;
    }
}
