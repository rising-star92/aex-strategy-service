package com.walmart.aex.strategy.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.strategy.dto.Lvl3ListSP;
import com.walmart.aex.strategy.dto.Lvl4ListSP;
import com.walmart.aex.strategy.dto.request.CustomerChoiceSP;
import com.walmart.aex.strategy.dto.request.FineLineSP;
import com.walmart.aex.strategy.dto.request.SizeProfileDTO;
import com.walmart.aex.strategy.dto.request.StyleSP;
import com.walmart.aex.strategy.dto.request.UpdatedSizesSP;
import com.walmart.aex.strategy.entity.PlanClusterStrategyId;
import com.walmart.aex.strategy.entity.StrategyCcSPCluster;
import com.walmart.aex.strategy.entity.StrategyFineLineSPCluster;
import com.walmart.aex.strategy.entity.StrategyMerchCategorySPCluster;
import com.walmart.aex.strategy.entity.StrategyStyleSPCluster;
import com.walmart.aex.strategy.entity.StrategySubCategorySPCluster;
import com.walmart.aex.strategy.enums.EligibilityState;
import com.walmart.aex.strategy.exception.CustomException;
import com.walmart.aex.strategy.repository.SizeEligibilityRepository;
import com.walmart.aex.strategy.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class SizeEligibilityMapper {
    private final SizeEligibilityRepository sizeEligibilityRepository;
    private final ObjectMapper objectMapper;

    public SizeEligibilityMapper (SizeEligibilityRepository sizeEligibilityRepository, ObjectMapper objectMapper) {
        this.sizeEligibilityRepository = sizeEligibilityRepository;
        this.objectMapper = objectMapper;
    }

    public List<StrategyMerchCategorySPCluster> updateCategorySizes(Integer channelId, PlanClusterStrategyId planClusterStrategyId, Lvl3ListSP lvl3) {
        List<StrategyMerchCategorySPCluster> strategyMerchCategorySPClusters = sizeEligibilityRepository.findStrategyMerchCategorySPClusterByStrategyMerchCatgSPClusId_PlanClusterStrategyIdAndStrategyMerchCatgSPClusId_lvl3NbrAndStrategyMerchCatgSPClusId_channelId(planClusterStrategyId, lvl3.getLvl3Nbr(), channelId)
                .orElseThrow(() -> new CustomException(String.format("Size Cluster doesn't exists for the PlanId :%s, StrategyId: %s  & lvl3Nbr : %s provided",
                        planClusterStrategyId.getPlanStrategyId().getPlanId(), planClusterStrategyId.getPlanStrategyId().getStrategyId(), lvl3.getLvl3Nbr())));
        Optional.ofNullable(lvl3.getUpdatedSizes()).map(UpdatedSizesSP::getSizes).map(CommonUtil::getUpdatedFieldsMap).ifPresent(
                clusterSpUpdatedFields -> {
                    try {
                        StrategyMerchCategorySPCluster strategyMerchCategorySPCluster = fetchStrategyCatgSpClus(strategyMerchCategorySPClusters, lvl3.getLvl3Nbr());
                        updateCatFields(clusterSpUpdatedFields, strategyMerchCategorySPCluster);
                    } catch (JsonProcessingException e) {
                        throw new CustomException("Failed while updating category size profile");
                    }
                }
        );
        if (!CollectionUtils.isEmpty(lvl3.getLvl4List())) {
            updateSubCategorySizes(lvl3, strategyMerchCategorySPClusters);
        }
        return strategyMerchCategorySPClusters;
    }

    private void updateSubCategorySizes(Lvl3ListSP lvl3, List<StrategyMerchCategorySPCluster> strategyMerchCategorySPClusters) {
        for (Lvl4ListSP lvl4 : lvl3.getLvl4List()) {
            Optional.ofNullable(lvl4.getUpdatedSizes()).map(UpdatedSizesSP::getSizes).map(CommonUtil::getUpdatedFieldsMap).ifPresent(
                    clusterSpUpdatedFields -> {
                        try {
                            StrategySubCategorySPCluster strategySubCategorySPCluster = fetchStrategySubCatgSpClus(strategyMerchCategorySPClusters, lvl3.getLvl3Nbr(), lvl4.getLvl4Nbr());
                            updateSubCatFields(clusterSpUpdatedFields, strategySubCategorySPCluster);
                            rollupCatgFields(strategyMerchCategorySPClusters,clusterSpUpdatedFields,lvl3.getLvl3Nbr());
                        } catch (JsonProcessingException e) {
                            throw new CustomException("Failed while updating Sub category size profile");
                        }
                    }
            );
            if (!CollectionUtils.isEmpty(lvl4.getFinelines())) {
                updateFinelineSizes(lvl3, strategyMerchCategorySPClusters, lvl4);
            }
        }
    }

    private void updateFinelineSizes(Lvl3ListSP lvl3, List<StrategyMerchCategorySPCluster> strategyMerchCategorySPClusters, Lvl4ListSP lvl4) {
        for (FineLineSP fl : lvl4.getFinelines()) {
            Optional.ofNullable(fl.getUpdatedSizes()).map(UpdatedSizesSP::getSizes).map(CommonUtil::getUpdatedFieldsMap).ifPresent(
                    clusterSpUpdatedFields -> {
                        try {
                            StrategyFineLineSPCluster strategyFineLineSPCluster = fetchStrategyFinelineSpClus(strategyMerchCategorySPClusters, lvl3.getLvl3Nbr(), lvl4.getLvl4Nbr(), fl.getFinelineNbr());
                            updateFlFields(clusterSpUpdatedFields, strategyFineLineSPCluster);
                            rollupSubCatgFields(strategyMerchCategorySPClusters,clusterSpUpdatedFields,lvl3.getLvl3Nbr(),lvl4.getLvl4Nbr());
                        } catch (JsonProcessingException e) {
                            throw new CustomException("Failed while updating Fineline size profile");
                        }
                    }
            );
            if (!CollectionUtils.isEmpty(fl.getStyles())) {
                updateStyleSizes(lvl3, strategyMerchCategorySPClusters, lvl4, fl);
            }
        }
    }

    private void updateStyleSizes(Lvl3ListSP lvl3, List<StrategyMerchCategorySPCluster> strategyMerchCategorySPClusters, Lvl4ListSP lvl4, FineLineSP fl) {
        for (StyleSP st : fl.getStyles()) {
            Optional.ofNullable(st.getUpdatedSizes()).map(UpdatedSizesSP::getSizes).map(CommonUtil::getUpdatedFieldsMap).ifPresent(
                    clusterSpUpdatedFields -> {
                        try {
                            StrategyStyleSPCluster strategyStyleSPCluster = fetchStrategyStyleSpClus(strategyMerchCategorySPClusters, lvl3.getLvl3Nbr(), lvl4.getLvl4Nbr(), fl.getFinelineNbr(), st.getStyleNbr());
                            updateStFields(clusterSpUpdatedFields, strategyStyleSPCluster);
                            rollupFinelineFields(strategyMerchCategorySPClusters,clusterSpUpdatedFields, lvl3.getLvl3Nbr(), lvl4.getLvl4Nbr(),fl.getFinelineNbr());
                        } catch (JsonProcessingException e) {
                            throw new CustomException("Failed while updating Style size profile");
                        }
                    }
            );
            if (!CollectionUtils.isEmpty(st.getCustomerChoices())) {
                updateCcSizes(lvl3, strategyMerchCategorySPClusters, lvl4, fl, st);
            }
        }
    }

    private void updateCcSizes(Lvl3ListSP lvl3, List<StrategyMerchCategorySPCluster> strategyMerchCategorySPClusters, Lvl4ListSP lvl4, FineLineSP fl, StyleSP st) {
        for (CustomerChoiceSP cc : st.getCustomerChoices()) {
            Optional.ofNullable(cc.getUpdatedSizes()).map(UpdatedSizesSP::getSizes).map(CommonUtil::getUpdatedFieldsMap).ifPresent(
                    clusterSpUpdatedFields -> {
                        try {
                            StrategyCcSPCluster strategyCcSPCluster = fetchStrategyCcSpClus(strategyMerchCategorySPClusters, lvl3.getLvl3Nbr(), lvl4.getLvl4Nbr(), fl.getFinelineNbr(), st.getStyleNbr(), cc.getCcId());
                            updateCcFields(clusterSpUpdatedFields, strategyCcSPCluster);
                            rollupStyleFields(strategyMerchCategorySPClusters,clusterSpUpdatedFields,lvl3.getLvl3Nbr(), lvl4.getLvl4Nbr(), fl.getFinelineNbr(), st.getStyleNbr());
                        } catch (JsonProcessingException e) {
                            throw new CustomException("Failed while updating Customer Choice size profile");
                        }
                    }
            );
        }
    }

    private void updateCatFields(Map<String, String> mapFields, StrategyMerchCategorySPCluster strategyMerchCategorySPCluster) throws JsonProcessingException {
        if (strategyMerchCategorySPCluster != null) {
            SizeProfileDTO[] sizeProfileDTOS = safeReadSizeObject(strategyMerchCategorySPCluster.getSizeProfileObj());
            mapFields.keySet().forEach(key -> Stream.of(sizeProfileDTOS).filter(size -> size.getAhsSizeId().equals(Integer.valueOf(key))).findFirst().ifPresent(sizeProfileDTO -> sizeProfileDTO.setIsEligible(Integer.valueOf(mapFields.get(key)))));
            String updatedString = objectMapper.writeValueAsString(sizeProfileDTOS);
            strategyMerchCategorySPCluster.setSizeProfileObj(updatedString);
            if (!CollectionUtils.isEmpty(strategyMerchCategorySPCluster.getStrategySubCatgSPClusters())) {
                strategyMerchCategorySPCluster.getStrategySubCatgSPClusters().forEach(strategySubCategorySPCluster -> {
                    try {
                        updateSubCatFields(mapFields,strategySubCategorySPCluster);
                    } catch (JsonProcessingException jsonProcessingException) {
                        throw new CustomException("Failed while updating Sub Category size profile");
                    }
                });
            }
        }
    }

    private void updateSubCatFields(Map<String, String> mapFields, StrategySubCategorySPCluster strategySubCategorySPCluster) throws JsonProcessingException {
        if (strategySubCategorySPCluster != null) {
            SizeProfileDTO[] sizeProfileDTOS = safeReadSizeObject(strategySubCategorySPCluster.getSizeProfileObj());
            mapFields.keySet().forEach(key -> Stream.of(sizeProfileDTOS).filter(size -> size.getAhsSizeId().equals(Integer.valueOf(key))).findFirst().ifPresent(sizeProfileDTO -> sizeProfileDTO.setIsEligible(Integer.valueOf(mapFields.get(key)))));
            String updatedString = objectMapper.writeValueAsString(sizeProfileDTOS);
            strategySubCategorySPCluster.setSizeProfileObj(updatedString);
            if (!CollectionUtils.isEmpty(strategySubCategorySPCluster.getStrategyFinelinesSPCluster())) {
                strategySubCategorySPCluster.getStrategyFinelinesSPCluster().forEach(strategyFineLineSPCluster -> {
                    try {
                        updateFlFields(mapFields,strategyFineLineSPCluster);
                    } catch (JsonProcessingException jsonProcessingException) {
                        throw new CustomException("Failed while updating Fineline size profile");
                    }
                });
            }
        }
    }

    private void updateFlFields(Map<String, String> mapFields, StrategyFineLineSPCluster strategyFineLineSPCluster) throws JsonProcessingException {
        if (strategyFineLineSPCluster != null) {
            SizeProfileDTO[] sizeProfileDTOS = safeReadSizeObject(strategyFineLineSPCluster.getSizeProfileObj());
            mapFields.keySet().forEach(key -> Stream.of(sizeProfileDTOS).filter(size -> size.getAhsSizeId().equals(Integer.valueOf(key))).findFirst().ifPresent(sizeProfileDTO -> sizeProfileDTO.setIsEligible(Integer.valueOf(mapFields.get(key)))));
            String updatedString = objectMapper.writeValueAsString(sizeProfileDTOS);
            strategyFineLineSPCluster.setSizeProfileObj(updatedString);
            if (!CollectionUtils.isEmpty(strategyFineLineSPCluster.getStrategyStylesSPClusters())) {
                strategyFineLineSPCluster.getStrategyStylesSPClusters().forEach(strategyStyleSPCluster -> {
                    try {
                        updateStFields(mapFields,strategyStyleSPCluster);
                    } catch (JsonProcessingException jsonProcessingException) {
                        throw new CustomException("Failed while updating Style size profile");
                    }
                });
            }
        }
    }

    private void updateStFields(Map<String, String> mapFields, StrategyStyleSPCluster strategyStyleSPCluster) throws JsonProcessingException {
        if (strategyStyleSPCluster != null) {
            SizeProfileDTO[] sizeProfileDTOS = safeReadSizeObject(strategyStyleSPCluster.getSizeProfileObj());
            mapFields.keySet().forEach(key -> Stream.of(sizeProfileDTOS).filter(size -> size.getAhsSizeId().equals(Integer.valueOf(key))).findFirst().ifPresent(sizeProfileDTO -> sizeProfileDTO.setIsEligible(Integer.valueOf(mapFields.get(key)))));
            String updatedString = objectMapper.writeValueAsString(sizeProfileDTOS);
            strategyStyleSPCluster.setSizeProfileObj(updatedString);
            if (!CollectionUtils.isEmpty(strategyStyleSPCluster.getStrategyCcSPClusters())) {
                strategyStyleSPCluster.getStrategyCcSPClusters().forEach(strategyCcSPCluster -> {
                    try {
                        updateCcFields(mapFields,strategyCcSPCluster);
                    } catch (JsonProcessingException jsonProcessingException) {
                        throw new CustomException("Failed while updating CC size profile");
                    }
                });
            }
        }
    }

    private void updateCcFields(Map<String, String> mapFields, StrategyCcSPCluster strategyCcSPCluster) throws JsonProcessingException {
        if (strategyCcSPCluster != null) {
            SizeProfileDTO[] sizeProfileDTOS = safeReadSizeObject(strategyCcSPCluster.getSizeProfileObj());
            setCalcSpreadStatusCc(strategyCcSPCluster, mapFields, sizeProfileDTOS);
            mapFields.keySet().forEach(key -> Stream.of(sizeProfileDTOS).filter(size -> size.getAhsSizeId().equals(Integer.valueOf(key))).findFirst().ifPresent(sizeProfileDTO -> sizeProfileDTO.setIsEligible(Integer.valueOf(mapFields.get(key)))));
            String updatedString = objectMapper.writeValueAsString(sizeProfileDTOS);
            strategyCcSPCluster.setSizeProfileObj(updatedString);
        }
    }

    private void rollupCatgFields(List<StrategyMerchCategorySPCluster> strategyMerchCategorySPClusters, Map<String, String> clusterSpUpdatedFields, Integer lvl3Nbr) throws JsonProcessingException {
        StrategyMerchCategorySPCluster strategyMerchCategorySPCluster = fetchStrategyCatgSpClus(strategyMerchCategorySPClusters, lvl3Nbr);
        if (strategyMerchCategorySPCluster != null && !CollectionUtils.isEmpty(strategyMerchCategorySPCluster.getStrategySubCatgSPClusters())) {
            List<Integer> isEligList = new ArrayList<>();
            strategyMerchCategorySPCluster.getStrategySubCatgSPClusters().forEach(strategySubCategorySPCluster -> {
                SizeProfileDTO[] sizeProfileDTOS = safeReadSizeObject(strategySubCategorySPCluster.getSizeProfileObj());
                clusterSpUpdatedFields.keySet().forEach(key -> Stream.of(sizeProfileDTOS).filter(size -> size.getAhsSizeId().equals(Integer.valueOf(key)))
                      .findFirst().filter(size -> !size.getIsEligible().equals(EligibilityState.NOT_ELIGIBLE.getId())).ifPresent(sizeProfileDTO -> isEligList.add(sizeProfileDTO.getIsEligible())));
            });
            if (!isEligList.isEmpty() && !isEligList.contains(EligibilityState.UNDEFINED.getId()) && isEligList.size() == strategyMerchCategorySPCluster.getStrategySubCatgSPClusters().size()) {
                SizeProfileDTO[] sizeProfileDTOS = safeReadSizeObject(strategyMerchCategorySPCluster.getSizeProfileObj());
                strategyMerchCategorySPCluster.setSizeProfileObj(catgEligibility(sizeProfileDTOS, clusterSpUpdatedFields, EligibilityState.ELIGIBLE.getId()));
            }
            else if (!isEligList.isEmpty() && (isEligList.contains(EligibilityState.UNDEFINED.getId()) || isEligList.size() != strategyMerchCategorySPCluster.getStrategySubCatgSPClusters().size())) {
                SizeProfileDTO[] sizeProfileDTOS = safeReadSizeObject(strategyMerchCategorySPCluster.getSizeProfileObj());
                strategyMerchCategorySPCluster.setSizeProfileObj(catgEligibility(sizeProfileDTOS, clusterSpUpdatedFields, EligibilityState.UNDEFINED.getId()));
            }
            else {
                SizeProfileDTO[] sizeProfileDTOS = safeReadSizeObject(strategyMerchCategorySPCluster.getSizeProfileObj());
                strategyMerchCategorySPCluster.setSizeProfileObj(catgEligibility(sizeProfileDTOS, clusterSpUpdatedFields, EligibilityState.NOT_ELIGIBLE.getId()));
            }
        }
    }

    private void rollupSubCatgFields(List<StrategyMerchCategorySPCluster> strategyMerchCategorySPClusters, Map<String, String> clusterSpUpdatedFields, Integer lvl3Nbr, Integer lvl4Nbr) throws JsonProcessingException {
        StrategySubCategorySPCluster strategySubCategorySPCluster = fetchStrategySubCatgSpClus(strategyMerchCategorySPClusters, lvl3Nbr,lvl4Nbr);
        if (strategySubCategorySPCluster != null && !CollectionUtils.isEmpty(strategySubCategorySPCluster.getStrategyFinelinesSPCluster())) {
            List<Integer> isEligList = new ArrayList<>();
            strategySubCategorySPCluster.getStrategyFinelinesSPCluster().forEach(strategyFineLineSPCluster -> {
                SizeProfileDTO[] sizeProfileDTOS = safeReadSizeObject(strategyFineLineSPCluster.getSizeProfileObj());
                clusterSpUpdatedFields.keySet().forEach(key -> Stream.of(sizeProfileDTOS).filter(size -> size.getAhsSizeId().equals(Integer.valueOf(key)))
                      .findFirst().filter(size -> !size.getIsEligible().equals(EligibilityState.NOT_ELIGIBLE.getId())).ifPresent(sizeProfileDTO -> isEligList.add(sizeProfileDTO.getIsEligible())));
            });
            if (!isEligList.isEmpty() && !isEligList.contains(EligibilityState.UNDEFINED.getId()) && isEligList.size() == strategySubCategorySPCluster.getStrategyFinelinesSPCluster().size()) {
                SizeProfileDTO[] sizeProfileDTOS = safeReadSizeObject(strategySubCategorySPCluster.getSizeProfileObj());
                strategySubCategorySPCluster.setSizeProfileObj(catgEligibility(sizeProfileDTOS, clusterSpUpdatedFields, EligibilityState.ELIGIBLE.getId()));
            }
            else if (!isEligList.isEmpty() && (isEligList.contains(EligibilityState.UNDEFINED.getId()) || isEligList.size() != strategySubCategorySPCluster.getStrategyFinelinesSPCluster().size())) {
                SizeProfileDTO[] sizeProfileDTOS = safeReadSizeObject(strategySubCategorySPCluster.getSizeProfileObj());
                strategySubCategorySPCluster.setSizeProfileObj(catgEligibility(sizeProfileDTOS, clusterSpUpdatedFields, EligibilityState.UNDEFINED.getId()));
            }
            else {
                SizeProfileDTO[] sizeProfileDTOS = safeReadSizeObject(strategySubCategorySPCluster.getSizeProfileObj());
                strategySubCategorySPCluster.setSizeProfileObj(catgEligibility(sizeProfileDTOS, clusterSpUpdatedFields, EligibilityState.NOT_ELIGIBLE.getId()));
            }
        }
        rollupCatgFields(strategyMerchCategorySPClusters,clusterSpUpdatedFields,lvl3Nbr);
    }

    private void rollupFinelineFields(List<StrategyMerchCategorySPCluster> strategyMerchCategorySPClusters, Map<String, String> clusterSpUpdatedFields, Integer lvl3Nbr, Integer lvl4Nbr, Integer finelineNbr) throws JsonProcessingException {
        StrategyFineLineSPCluster strategyFineLineSPCluster = fetchStrategyFinelineSpClus(strategyMerchCategorySPClusters, lvl3Nbr,lvl4Nbr,finelineNbr);
        if (strategyFineLineSPCluster != null && !CollectionUtils.isEmpty(strategyFineLineSPCluster.getStrategyStylesSPClusters())) {
            List<Integer> isEligList = new ArrayList<>();
            strategyFineLineSPCluster.getStrategyStylesSPClusters().forEach(strategyStyleSPCluster -> {
                SizeProfileDTO[] sizeProfileDTOS = safeReadSizeObject(strategyStyleSPCluster.getSizeProfileObj());
               clusterSpUpdatedFields.keySet().forEach(key -> Stream.of(sizeProfileDTOS).filter(size -> size.getAhsSizeId().equals(Integer.valueOf(key)))
                      .findFirst().filter(size -> !size.getIsEligible().equals(EligibilityState.NOT_ELIGIBLE.getId())).ifPresent(sizeProfileDTO -> isEligList.add(sizeProfileDTO.getIsEligible())));
            });
            
           if (!isEligList.isEmpty() && !isEligList.contains(EligibilityState.UNDEFINED.getId()) && isEligList.size() == strategyFineLineSPCluster.getStrategyStylesSPClusters().size()) {
                SizeProfileDTO[] sizeProfileDTOS = safeReadSizeObject(strategyFineLineSPCluster.getSizeProfileObj());
                strategyFineLineSPCluster.setSizeProfileObj(catgEligibility(sizeProfileDTOS, clusterSpUpdatedFields, EligibilityState.ELIGIBLE.getId()));
            }
            else if (!isEligList.isEmpty() && (isEligList.contains(EligibilityState.UNDEFINED.getId()) || isEligList.size() != strategyFineLineSPCluster.getStrategyStylesSPClusters().size())) {
                SizeProfileDTO[] sizeProfileDTOS = safeReadSizeObject(strategyFineLineSPCluster.getSizeProfileObj());
                strategyFineLineSPCluster.setSizeProfileObj(catgEligibility(sizeProfileDTOS, clusterSpUpdatedFields, EligibilityState.UNDEFINED.getId()));
            }
            else {
                SizeProfileDTO[] sizeProfileDTOS = safeReadSizeObject(strategyFineLineSPCluster.getSizeProfileObj());
                strategyFineLineSPCluster.setSizeProfileObj(catgEligibility(sizeProfileDTOS, clusterSpUpdatedFields, EligibilityState.NOT_ELIGIBLE.getId()));
            }
        }
        rollupSubCatgFields(strategyMerchCategorySPClusters,clusterSpUpdatedFields,lvl3Nbr,lvl4Nbr);
    }

   

	private void rollupStyleFields(List<StrategyMerchCategorySPCluster> strategyMerchCategorySPClusters, Map<String, String> clusterSpUpdatedFields, Integer lvl3Nbr, Integer lvl4Nbr, Integer finelineNbr, String styleNbr) throws JsonProcessingException {
        StrategyStyleSPCluster strategyStyleSPCluster = fetchStrategyStyleSpClus(strategyMerchCategorySPClusters, lvl3Nbr,lvl4Nbr,finelineNbr, styleNbr);
        if (strategyStyleSPCluster != null && !CollectionUtils.isEmpty(strategyStyleSPCluster.getStrategyCcSPClusters())) {
            List<Integer> isEligList = new ArrayList<>();
            strategyStyleSPCluster.getStrategyCcSPClusters().forEach(strategyCcSPCluster -> {
                SizeProfileDTO[] sizeProfileDTOS = safeReadSizeObject(strategyCcSPCluster.getSizeProfileObj());
                clusterSpUpdatedFields.keySet().forEach(key -> Stream.of(sizeProfileDTOS).filter(size -> size.getAhsSizeId().equals(Integer.valueOf(key)))
                      .findFirst().filter(size -> !size.getIsEligible().equals(EligibilityState.NOT_ELIGIBLE.getId())).ifPresent(sizeProfileDTO -> isEligList.add(sizeProfileDTO.getIsEligible())));
            });
            if (!isEligList.isEmpty() && !isEligList.contains(EligibilityState.UNDEFINED.getId()) && isEligList.size() == strategyStyleSPCluster.getStrategyCcSPClusters().size()) {
                SizeProfileDTO[] sizeProfileDTOS = safeReadSizeObject(strategyStyleSPCluster.getSizeProfileObj());
                strategyStyleSPCluster.setSizeProfileObj(catgEligibility(sizeProfileDTOS, clusterSpUpdatedFields, EligibilityState.ELIGIBLE.getId()));
            }
            else if (!isEligList.isEmpty() && (isEligList.contains(EligibilityState.UNDEFINED.getId()) || isEligList.size() != strategyStyleSPCluster.getStrategyCcSPClusters().size())) {
                SizeProfileDTO[] sizeProfileDTOS = safeReadSizeObject(strategyStyleSPCluster.getSizeProfileObj());
                strategyStyleSPCluster.setSizeProfileObj(catgEligibility(sizeProfileDTOS, clusterSpUpdatedFields, EligibilityState.UNDEFINED.getId()));
            }
            else {
                SizeProfileDTO[] sizeProfileDTOS = safeReadSizeObject(strategyStyleSPCluster.getSizeProfileObj());
                strategyStyleSPCluster.setSizeProfileObj(catgEligibility(sizeProfileDTOS, clusterSpUpdatedFields, EligibilityState.NOT_ELIGIBLE.getId()));
            }
        }
        rollupFinelineFields(strategyMerchCategorySPClusters,clusterSpUpdatedFields,lvl3Nbr,lvl4Nbr,finelineNbr);
    }

    private String catgEligibility(SizeProfileDTO[] sizeProfileDTOS, Map<String, String> clusterSpUpdatedFields, int i) throws JsonProcessingException {
        clusterSpUpdatedFields.keySet().forEach(key -> Stream.of(sizeProfileDTOS).filter(size -> size.getAhsSizeId().equals(Integer.valueOf(key))).findFirst().ifPresent(sizeProfileDTO -> sizeProfileDTO.setIsEligible(i)));
        return objectMapper.writeValueAsString(sizeProfileDTOS);
    }

    private StrategyMerchCategorySPCluster fetchStrategyCatgSpClus(List<StrategyMerchCategorySPCluster> strategyMerchCategorySPClusters, Integer lvl3Nbr) {
        return Optional.ofNullable(strategyMerchCategorySPClusters)
                .stream()
                .flatMap(Collection::stream)
                .filter(strategyMerchCategorySPCluster -> strategyMerchCategorySPCluster.getStrategyMerchCatgSPClusId().getLvl3Nbr().equals(lvl3Nbr))
                .findFirst()
                .orElse(null);
    }

    private StrategySubCategorySPCluster fetchStrategySubCatgSpClus(List<StrategyMerchCategorySPCluster> strategyMerchCategorySPClusters, Integer lvl3Nbr, Integer lvl4Nbr) {
        return Optional.ofNullable(strategyMerchCategorySPClusters)
                .stream()
                .flatMap(Collection::stream)
                .filter(strategyMerchCategorySPCluster -> strategyMerchCategorySPCluster.getStrategyMerchCatgSPClusId().getLvl3Nbr().equals(lvl3Nbr))
                .findFirst()
                .map(StrategyMerchCategorySPCluster::getStrategySubCatgSPClusters)
                .stream()
                .flatMap(Collection::stream)
                .filter(strategySubCategorySPCluster -> strategySubCategorySPCluster.getStrategySubCatgSPClusId().getLvl4Nbr().equals(lvl4Nbr))
                .findFirst()
                .orElse(null);
    }

    private StrategyFineLineSPCluster fetchStrategyFinelineSpClus(List<StrategyMerchCategorySPCluster> strategyMerchCategorySPClusters, Integer lvl3Nbr, Integer lvl4Nbr, Integer finelineNbr) {
        return Optional.ofNullable(strategyMerchCategorySPClusters)
                .stream()
                .flatMap(Collection::stream)
                .filter(strategyMerchCategorySPCluster -> strategyMerchCategorySPCluster.getStrategyMerchCatgSPClusId().getLvl3Nbr().equals(lvl3Nbr))
                .findFirst()
                .map(StrategyMerchCategorySPCluster::getStrategySubCatgSPClusters)
                .stream()
                .flatMap(Collection::stream)
                .filter(strategySubCategorySPCluster -> strategySubCategorySPCluster.getStrategySubCatgSPClusId().getLvl4Nbr().equals(lvl4Nbr))
                .findFirst()
                .map(StrategySubCategorySPCluster::getStrategyFinelinesSPCluster)
                .stream()
                .flatMap(Collection::stream)
                .filter(strategyFineLineSPCluster -> strategyFineLineSPCluster.getStrategyIFineLineId().getFinelineNbr().equals(finelineNbr))
                .findFirst()
                .orElse(null);
    }

    private StrategyStyleSPCluster fetchStrategyStyleSpClus(List<StrategyMerchCategorySPCluster> strategyMerchCategorySPClusters, Integer lvl3Nbr, Integer lvl4Nbr, Integer finelineNbr, String styleNum) {
        return Optional.ofNullable(strategyMerchCategorySPClusters)
                .stream()
                .flatMap(Collection::stream)
                .filter(strategyMerchCategorySPCluster -> strategyMerchCategorySPCluster.getStrategyMerchCatgSPClusId().getLvl3Nbr().equals(lvl3Nbr))
                .findFirst()
                .map(StrategyMerchCategorySPCluster::getStrategySubCatgSPClusters)
                .stream()
                .flatMap(Collection::stream)
                .filter(strategySubCategorySPCluster -> strategySubCategorySPCluster.getStrategySubCatgSPClusId().getLvl4Nbr().equals(lvl4Nbr))
                .findFirst()
                .map(StrategySubCategorySPCluster::getStrategyFinelinesSPCluster)
                .stream()
                .flatMap(Collection::stream)
                .filter(strategyFineLineSPCluster -> strategyFineLineSPCluster.getStrategyIFineLineId().getFinelineNbr().equals(finelineNbr))
                .findFirst()
                .map(StrategyFineLineSPCluster::getStrategyStylesSPClusters)
                .stream()
                .flatMap(Collection::stream)
                .filter(strategyStyleSPCluster -> strategyStyleSPCluster.getStrategyStyleSPClusId().getStyleNbr().equals(styleNum))
                .findFirst()
                .orElse(null);
    }

    private StrategyCcSPCluster fetchStrategyCcSpClus(List<StrategyMerchCategorySPCluster> strategyMerchCategorySPClusters, Integer lvl3Nbr, Integer lvl4Nbr, Integer finelineNbr, String styleNum, String ccId) {
        return Optional.ofNullable(strategyMerchCategorySPClusters)
                .stream()
                .flatMap(Collection::stream)
                .filter(strategyMerchCategorySPCluster -> strategyMerchCategorySPCluster.getStrategyMerchCatgSPClusId().getLvl3Nbr().equals(lvl3Nbr))
                .findFirst()
                .map(StrategyMerchCategorySPCluster::getStrategySubCatgSPClusters)
                .stream()
                .flatMap(Collection::stream)
                .filter(strategySubCategorySPCluster -> strategySubCategorySPCluster.getStrategySubCatgSPClusId().getLvl4Nbr().equals(lvl4Nbr))
                .findFirst()
                .map(StrategySubCategorySPCluster::getStrategyFinelinesSPCluster)
                .stream()
                .flatMap(Collection::stream)
                .filter(strategyFineLineSPCluster -> strategyFineLineSPCluster.getStrategyIFineLineId().getFinelineNbr().equals(finelineNbr))
                .findFirst()
                .map(StrategyFineLineSPCluster::getStrategyStylesSPClusters)
                .stream()
                .flatMap(Collection::stream)
                .filter(strategyStyleSPCluster -> strategyStyleSPCluster.getStrategyStyleSPClusId().getStyleNbr().equals(styleNum))
                .findFirst()
                .map(StrategyStyleSPCluster::getStrategyCcSPClusters)
                .stream()
                .flatMap(Collection::stream)
                .filter(strategyCcSPCluster -> strategyCcSPCluster.getStrategyCcSPClusId().getCcId().equals(ccId))
                .findFirst()
                .orElse(null);
    }

    private SizeProfileDTO[] safeReadSizeObject(String sizeObj) {
        try {
            return objectMapper.readValue(sizeObj, SizeProfileDTO[].class);
        } catch (JsonProcessingException e) {
            log.error("Error deserializing size object: {}", sizeObj);
            throw new CustomException("Error deserializing size object");
        } catch (IllegalArgumentException|NullPointerException e) {
            log.warn("Size object provided was null");
            return new SizeProfileDTO[0];
        }
    }
    
    public void setCalcSpreadStatusCc(StrategyCcSPCluster strategyCcSPCluster,Map<String, String> clusterSpUpdatedFields, SizeProfileDTO[] sizeProfileDTOS) {
    	
    	List<SizeProfileDTO> filteredSizeList = getFilteredSizelist(clusterSpUpdatedFields,sizeProfileDTOS);

    	if(filteredSizeList != null) {
    		
    		int currentSpreadInd = strategyCcSPCluster.getCalcSpSpreadInd() == null ? 0 : strategyCcSPCluster.getCalcSpSpreadInd();
    		
    		int calculateSpreadStatus = compareIsEligibleValues(clusterSpUpdatedFields,filteredSizeList,currentSpreadInd);
    		
    		strategyCcSPCluster.setCalcSpSpreadInd(calculateSpreadStatus);
    	}

    }
    
    private List<SizeProfileDTO> getFilteredSizelist(Map<String, String> updatedSizesIds, SizeProfileDTO[] sizeProfileDTOS) {
    	
    	List<SizeProfileDTO> sizeProfileList = Arrays.asList(sizeProfileDTOS);
    	
    	List<SizeProfileDTO> filteredList= sizeProfileList.stream().filter(sizeProfile -> updatedSizesIds.keySet().contains(String.valueOf(sizeProfile.getAhsSizeId()))).collect(Collectors.toList());

    	return filteredList;
    }
    
    private int compareIsEligibleValues(Map<String, String> updatedSizesIds,  List<SizeProfileDTO> filteredSizeList,int currentSpreadInd)
    {
    	int status = currentSpreadInd;
    	if(filteredSizeList != null)
    	{
    		for(SizeProfileDTO sizeProfile: filteredSizeList)
    		{
    			for(String key: updatedSizesIds.keySet())
    			{
    				if(sizeProfile.getAhsSizeId().equals(Integer.parseInt(key)))
    				{
    					status = compareValues(sizeProfile.getIsEligible(), Integer.parseInt(updatedSizesIds.get(key)),currentSpreadInd);
    					break;                            
    				}
    			}
    		}

    	}
    	return status;

    }
    
    private int compareValues(int prevValue, int updatedValue,int currentSpreadInd)
    {
    	int result = currentSpreadInd;

    	if((prevValue == 0 && updatedValue == 1) ||  (prevValue == 0 && updatedValue == 2) || (prevValue == 1 && updatedValue == 0) || (prevValue == 2 && updatedValue == 0))        
    		result = 1;

    	return result;

    }
}
