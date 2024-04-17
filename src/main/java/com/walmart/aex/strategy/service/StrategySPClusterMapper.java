package com.walmart.aex.strategy.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.strategy.dto.SizeCluster;
import com.walmart.aex.strategy.dto.mapper.ClusterMetadata;
import com.walmart.aex.strategy.dto.mapper.PlanStrategyMapperDTO;
import com.walmart.aex.strategy.dto.request.*;
import com.walmart.aex.strategy.entity.*;
import com.walmart.aex.strategy.enums.ChannelType;
import com.walmart.aex.strategy.exception.CustomException;
import com.walmart.aex.strategy.properties.FeatureConfigProperties;
import com.walmart.aex.strategy.util.CommonUtil;
import io.strati.ccm.utils.client.annotation.ManagedConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class StrategySPClusterMapper {
    private final ObjectMapper objectMapper;
    private final SizeAndPackService sizeAndPackService;
    @ManagedConfiguration
    private FeatureConfigProperties featureConfigProperties;


    public StrategySPClusterMapper(ObjectMapper objectMapper, SizeAndPackService sizeAndPackService) {
        this.objectMapper = objectMapper;
        this.sizeAndPackService = sizeAndPackService;
    }

    public Set<PlanClusterStrategy> setPlanStrategyCluster(PlanStrategyMapperDTO planStrategyMapperDTO) {
        Set<PlanClusterStrategy> planClusterStrategies = Optional.ofNullable(planStrategyMapperDTO.getPlanStrategy().getPlanClusterStrategies()).orElse(new HashSet<>());
        planStrategyMapperDTO.setPlanClusterStrategies(planClusterStrategies);
        for (Lvl3 lvl3 : planStrategyMapperDTO.getLvl2().getLvl3List()) {
            Integer channelId = ChannelType.getChannelIdFromName(CommonUtil.getRequestedFlChannel(lvl3));
            List<Integer> channelList = getChannelList(channelId);
            if (!CollectionUtils.isEmpty(channelList)) {
                channelList.forEach(channel -> {
                    planStrategyMapperDTO.setChannel(channel);
                    planStrategyMapperDTO.setLvl3(lvl3);
                    addClusterByChannel(planStrategyMapperDTO);

                });
            }
        }
        return planClusterStrategies;
    }

    private void addClusterByChannel(PlanStrategyMapperDTO planStrategyMapperDTO) {
        HashMap<StrategyCcSPClusId, Set<Integer>> mapCCSPClusIdByEligibleSizeIds = new HashMap<>();
        if (planStrategyMapperDTO.getChannel().equals(ChannelType.STORE.getId())) {
            List<SizeCluster> sizeClusters = getSizeClusters(planStrategyMapperDTO.getLvl3());
            if (sizeClusters != null) {
                // Sort the sizeClusters list by the AnalyticsClusterId field of the Type object in ascending order
                sizeClusters.sort(Comparator.comparing(c -> c.getType().getAnalyticsClusterId()));
                sizeClusters.forEach(sizeCluster -> {
                    ClusterMetadata clusterMetadata = new ClusterMetadata(sizeCluster, mapCCSPClusIdByEligibleSizeIds);
                    planStrategyMapperDTO.setClusterMetadata(clusterMetadata);
                    setAnalyticsCluster(planStrategyMapperDTO);
                });
            }
        } else {
            ClusterMetadata clusterMetadata = ClusterMetadata.builder().mapCCSPClusIdByEligibleSizeIds(mapCCSPClusIdByEligibleSizeIds).build();
            planStrategyMapperDTO.setClusterMetadata(clusterMetadata);
            setOnlineSpClusters(planStrategyMapperDTO);
        }
    }

    private void setOnlineSpClusters(PlanStrategyMapperDTO planStrategyMapperDTO) {
        Set<ClusterType> clusterTypes = new HashSet<>();
        List<Style> styles = getStyles(planStrategyMapperDTO.getLvl3());
        if (!CollectionUtils.isEmpty(styles)) {
            styles.forEach(style -> {
                List<SizeCluster> onlineSizeClusters = Optional.ofNullable(style.getStrategy())
                        .map(Strategy::getOnlineSizeClusters)
                        .orElse(null);
                if (!CollectionUtils.isEmpty(onlineSizeClusters)) {
                    onlineSizeClusters.forEach(sizeCluster -> clusterTypes.add(sizeCluster.getType()));
                }
            });
            clusterTypes.forEach(clusterType -> {
                ClusterMetadata clusterMetadata = new ClusterMetadata(SizeCluster.builder().type(clusterType).build(),planStrategyMapperDTO.getClusterMetadata().getMapCCSPClusIdByEligibleSizeIds());
                planStrategyMapperDTO.setClusterMetadata(clusterMetadata);
                setAnalyticsCluster(planStrategyMapperDTO);
            });
        }
    }

    private List<SizeCluster> getSizeClusters(Lvl3 lvl3) {
        return Optional.ofNullable(lvl3.getLvl4List())
                .stream()
                .flatMap(Collection::stream)
                .findFirst()
                .map(Lvl4::getFinelines)
                .stream()
                .flatMap(Collection::stream)
                .findFirst()
                .map(Fineline::getStrategy)
                .map(Strategy::getStoreSizeClusters)
                .orElse(null);
    }

    private List<Style> getStyles(Lvl3 lvl3) {
        return Optional.ofNullable(lvl3.getLvl4List())
                .stream()
                .flatMap(Collection::stream)
                .findFirst()
                .map(Lvl4::getFinelines)
                .stream()
                .flatMap(Collection::stream)
                .findFirst()
                .map(Fineline::getStyles)
                .orElse(null);
    }

    private void setAnalyticsCluster(PlanStrategyMapperDTO planStrategyMapperDTO) {
        PlanClusterStrategyId planClusterStrategyId = new PlanClusterStrategyId(planStrategyMapperDTO.getPlanStrategy().getPlanStrategyId(), planStrategyMapperDTO.getClusterMetadata().getSizeCluster().getType().getAnalyticsClusterId());
        log.debug("Check if a PlanClusterStrategy Id : {} already exists or not", planClusterStrategyId.toString());
        PlanClusterStrategy planClusterStrategy = Optional.ofNullable(planStrategyMapperDTO.getPlanClusterStrategies()).orElse(new HashSet<>())
              .stream().filter(planClusterStrat1 -> planClusterStrat1.getPlanClusterStrategyId().equals(planClusterStrategyId))
              .findFirst()
              .orElse(PlanClusterStrategy.builder()
                    .planClusterStrategyId(planClusterStrategyId)
                    .planStrategy(planStrategyMapperDTO.getPlanStrategy())
                    .analyticsClusterLabel(planStrategyMapperDTO.getClusterMetadata().getSizeCluster().getType().getAnalyticsClusterDesc()).build());

        planClusterStrategy.setStrategyMerchCategorySPCluster(setStrategyMerchCategorySPCluster(planClusterStrategy,planStrategyMapperDTO));
        planStrategyMapperDTO.getPlanClusterStrategies().add(planClusterStrategy);
    }

    private Set<StrategyMerchCategorySPCluster> setStrategyMerchCategorySPCluster(PlanClusterStrategy planStratClus,PlanStrategyMapperDTO planStrategyMapperDTO ) {
        Set<StrategyMerchCategorySPCluster> strategyMerchCategorySPClusters = Optional.ofNullable(planStratClus.getStrategyMerchCategorySPCluster()).orElse(new HashSet<>());

        StrategyMerchCatgSPClusId strategyMerchCatgSPClusId = StrategyMerchCatgSPClusId.builder()
              .planClusterStrategyId(planStratClus.getPlanClusterStrategyId())
              .lvl0Nbr(planStrategyMapperDTO.getRequest().getLvl0Nbr()).lvl1Nbr(planStrategyMapperDTO.getLvl1().getLvl1Nbr())
              .lvl2Nbr(planStrategyMapperDTO.getLvl2().getLvl2Nbr()).lvl3Nbr(planStrategyMapperDTO.getLvl3().getLvl3Nbr())
              .channelId(planStrategyMapperDTO.getChannel()).build();
        log.debug("Check if a StrategyMerchCategory SP Clus Id : {} already exists or not", strategyMerchCatgSPClusId.toString());

        StrategyMerchCategorySPCluster strategyMerchCategorySPCluster = strategyMerchCategorySPClusters.stream()
              .filter(stratMerchCatgSPClus -> stratMerchCatgSPClus.getStrategyMerchCatgSPClusId().equals(strategyMerchCatgSPClusId))
              .findFirst()
              .orElse(StrategyMerchCategorySPCluster.builder()
                    .strategyMerchCatgSPClusId(strategyMerchCatgSPClusId)
                    .planClusterStrategy(planStratClus).build());

        String sizeStr = null;
        sizeStr = setCatgSizeProfile(planStrategyMapperDTO.getLvl3(), planStrategyMapperDTO.getChannel(), planStrategyMapperDTO.getClusterMetadata().getSizeCluster().getType(), strategyMerchCategorySPCluster, sizeStr);
        if (!CollectionUtils.isEmpty(planStrategyMapperDTO.getLvl3().getLvl4List())) {
            planStrategyMapperDTO.setSizeStr(sizeStr);
            strategyMerchCategorySPCluster.setStrategySubCatgSPClusters(setStrategySubCatgSPClusters(strategyMerchCategorySPCluster,planStrategyMapperDTO));
        }
        strategyMerchCategorySPClusters.add(strategyMerchCategorySPCluster);
        return strategyMerchCategorySPClusters;
    }

    private String setCatgSizeProfile(Lvl3 lvl3, Integer channel, ClusterType clusterType, StrategyMerchCategorySPCluster strategyMerchCategorySPCluster, String sizeStr) {
        String sizeStrToUpdate = null;
        if (channel.equals(ChannelType.STORE.getId())) {
            if (clusterType.getAnalyticsClusterId() == 0) {
                sizeStr = getStoreSizeStr(lvl3.getStrategy(), 0);
                String existingSizeStrFromDB = strategyMerchCategorySPCluster.getSizeProfileObj();
                sizeStrToUpdate = getUpdatedSizeProfileData(sizeStr, existingSizeStrFromDB);
                strategyMerchCategorySPCluster.setSizeProfileObj(sizeStrToUpdate);
            }
        } else {
            if (clusterType.getAnalyticsClusterId() == 0) {
                sizeStr = getOnlineSizeStr(lvl3.getStrategy(), 0);
                String existingSizeStrFromDB = strategyMerchCategorySPCluster.getSizeProfileObj();
                sizeStrToUpdate = getUpdatedSizeProfileData(sizeStr, existingSizeStrFromDB);
                strategyMerchCategorySPCluster.setSizeProfileObj(sizeStrToUpdate);
            }
        }
        if (sizeStrToUpdate != null && !sizeStrToUpdate.isEmpty()) {
            sizeStr = sizeStrToUpdate;
        }
        return sizeStr;
    }

    private String getOnlineSizeStr(Strategy strategy, int i) {
        return Optional.ofNullable(strategy)
                .map(Strategy::getOnlineSizeClusters)
                .stream()
                .flatMap(Collection::stream)
                .filter(sizeCluster -> sizeCluster.getType().getAnalyticsClusterId().equals(i))
                .findFirst()
                .map(SizeCluster::getSizeProfiles)
                .map(Object::toString)
                .orElse(null);
    }

    private String getStoreSizeStr(Strategy strategy, int i) {
        return Optional.ofNullable(strategy)
                .map(Strategy::getStoreSizeClusters)
                .stream()
                .flatMap(Collection::stream)
                .filter(sizeCluster -> sizeCluster.getType().getAnalyticsClusterId().equals(i))
                .findFirst()
                .map(SizeCluster::getSizeProfiles)
                .map(Object::toString)
                .orElse(null);
    }

    private Set<StrategySubCategorySPCluster> setStrategySubCatgSPClusters(StrategyMerchCategorySPCluster strategyMerchCategorySPCluster, PlanStrategyMapperDTO planStrategyMapperDTO) {
        Set<StrategySubCategorySPCluster> strategySubCategorySPClusters = Optional.ofNullable(strategyMerchCategorySPCluster.getStrategySubCatgSPClusters()).orElse(new HashSet<>());

        for (Lvl4 lvl4 : planStrategyMapperDTO.getLvl3().getLvl4List()) {
            StrategySubCatgSPClusId strategySubCatgSPClusId = new StrategySubCatgSPClusId(strategyMerchCategorySPCluster.getStrategyMerchCatgSPClusId(), lvl4.getLvl4Nbr());

            log.debug("Check if a strategySubCatg SP Clus Id : {} already exists or not", strategySubCatgSPClusId.toString());
            StrategySubCategorySPCluster strategySubCategorySPCluster = strategySubCategorySPClusters.stream()
                  .filter(stratSubcatSPClus -> stratSubcatSPClus.getStrategySubCatgSPClusId().equals(strategySubCatgSPClusId))
                  .findFirst()
                  .orElse(StrategySubCategorySPCluster.builder()
                        .strategySubCatgSPClusId(strategySubCatgSPClusId)
                        .strategyMerchCatgSPClusters(strategyMerchCategorySPCluster).build());

            strategySubCategorySPCluster.setSizeProfileObj(planStrategyMapperDTO.getSizeStr());
            planStrategyMapperDTO.setFinelines(lvl4.getFinelines());
            strategySubCategorySPCluster.setStrategyFinelinesSPCluster(setStrategyFinelinesSPClusters(strategySubCategorySPCluster, planStrategyMapperDTO));
            strategySubCategorySPClusters.add(strategySubCategorySPCluster);
        }
        return strategySubCategorySPClusters;
    }

    private Set<StrategyFineLineSPCluster> setStrategyFinelinesSPClusters(StrategySubCategorySPCluster strategySubCategorySPCluster, PlanStrategyMapperDTO planStrategyMapperDTO) {
        Set<StrategyFineLineSPCluster> strategyFineLineSPClusters = Optional.ofNullable(strategySubCategorySPCluster.getStrategyFinelinesSPCluster()).orElse(new HashSet<>());

        for (Fineline fineline : planStrategyMapperDTO.getFinelines()) {
            StrategyFineLineSPClusId strategyFineLineSPClusId = new StrategyFineLineSPClusId(strategySubCategorySPCluster.getStrategySubCatgSPClusId(), fineline.getFinelineNbr());
            log.debug("Check if a strategyFineLineSPCluster Id : {} already exists or not", strategyFineLineSPClusId.toString());

            StrategyFineLineSPCluster strategyFineLineSPCluster = strategyFineLineSPClusters
                  .stream().filter(stratFinelineSPClus -> stratFinelineSPClus.getStrategyIFineLineId().equals(strategyFineLineSPClusId))
                  .findAny()
                  .orElse(StrategyFineLineSPCluster.builder()
                        .strategyIFineLineId(strategyFineLineSPClusId)
                        .strategySubCatgSpClus(strategySubCategorySPCluster).build());

            if (planStrategyMapperDTO.getChannel().equals(ChannelType.STORE.getId())) {
                String existingSizeStrFromDB = strategyFineLineSPCluster.getSizeProfileObj();
                String sizeStrFromRequest = getStoreSizeStr(fineline.getStrategy(), planStrategyMapperDTO.getClusterMetadata().getSizeCluster().getType().getAnalyticsClusterId());
                String sizeStrToBeUpdated = getUpdatedSizeProfileData(sizeStrFromRequest, existingSizeStrFromDB);
                strategyFineLineSPCluster.setSizeProfileObj(sizeStrToBeUpdated);
            } else {
                if (planStrategyMapperDTO.getClusterMetadata().getSizeCluster().getType().getAnalyticsClusterId() == 0) {
                    String existingSizeStrFromDB = strategyFineLineSPCluster.getSizeProfileObj();
                    String sizeStrFromRequest = getOnlineSizeStr(fineline.getStrategy(), 0);
                    String sizeStrToBeUpdated = getUpdatedSizeProfileData(sizeStrFromRequest, existingSizeStrFromDB);
                    strategyFineLineSPCluster.setSizeProfileObj(sizeStrToBeUpdated);
                }
            }
            if (!CollectionUtils.isEmpty(fineline.getStyles())) {
                planStrategyMapperDTO.setStyles(fineline.getStyles());
                strategyFineLineSPCluster.setStrategyStylesSPClusters(setStrategyStyleSPClusters(strategyFineLineSPCluster,planStrategyMapperDTO));
            }
            strategyFineLineSPClusters.add(strategyFineLineSPCluster);
        }
        return strategyFineLineSPClusters;
    }

    private Set<StrategyStyleSPCluster> setStrategyStyleSPClusters(StrategyFineLineSPCluster strategyFineLineSPCluster, PlanStrategyMapperDTO planStrategyMapperDTO) {
        Set<StrategyStyleSPCluster> strategyStyleSPClusters = Optional.ofNullable(strategyFineLineSPCluster.getStrategyStylesSPClusters()).orElse(new HashSet<>());
        for (Style style : planStrategyMapperDTO.getStyles()) {
            if (getMerchCatgChannelId(strategyFineLineSPCluster).equals(ChannelType.getChannelIdFromName(style.getChannel()))
                    || ChannelType.getChannelIdFromName(style.getChannel()).equals(3)) {
                StrategyStyleSPClusId strategyStyleSPClusId = new StrategyStyleSPClusId(strategyFineLineSPCluster.getStrategyIFineLineId(), style.getStyleNbr());
                log.debug("Check if a strategyStyleSPClus Id : {} already exists or not", strategyStyleSPClusId.toString());
                StrategyStyleSPCluster strategyStyleSPCluster = strategyStyleSPClusters.stream()
                      .filter(stratStyleSPClus -> stratStyleSPClus.getStrategyStyleSPClusId().equals(strategyStyleSPClusId))
                      .findAny()
                      .orElse(StrategyStyleSPCluster.builder()
                            .strategyStyleSPClusId(strategyStyleSPClusId)
                            .strategyFinelineSPClus(strategyFineLineSPCluster).build());

                setStyleSizeProfile(planStrategyMapperDTO.getChannel(), planStrategyMapperDTO.getClusterMetadata().getSizeCluster().getType(), style, strategyStyleSPCluster);
                if (!CollectionUtils.isEmpty(style.getCustomerChoices())) {
                    planStrategyMapperDTO.setCustomerChoices(style.getCustomerChoices());
                    strategyStyleSPCluster.setStrategyCcSPClusters(setStrategyCustomerChoiceSPClusters(strategyStyleSPCluster, planStrategyMapperDTO));
                }
                strategyStyleSPClusters.add(strategyStyleSPCluster);
            }
        }
        return strategyStyleSPClusters;
    }

    private void setStyleSizeProfile(Integer channel, ClusterType clusterType, Style style, StrategyStyleSPCluster strategyStyleSPCluster) {
        if (channel.equals(ChannelType.STORE.getId())) {
            if (clusterType.getAnalyticsClusterId() == 0) {

                String existingSizeStrFromDB = strategyStyleSPCluster.getSizeProfileObj();
                log.debug("existingSizeStrFromDB is : " + existingSizeStrFromDB);
                String sizeStrFromRequest = getStoreSizeStr(style.getStrategy(), 0);
                log.debug("existingSizeStr is : " + sizeStrFromRequest);
                String sizeStrToBeUpdated = getUpdatedSizeProfileData(sizeStrFromRequest, existingSizeStrFromDB);
                log.debug("existingSizeStrUpdated is : " + sizeStrToBeUpdated);
                strategyStyleSPCluster.setSizeProfileObj(sizeStrToBeUpdated);
            }
        } else {
            String existingSizeStrFromDB = strategyStyleSPCluster.getSizeProfileObj();
            log.debug("existingSizeStrFromDB is : " + existingSizeStrFromDB);
            String sizeStrFromRequest = getOnlineSizeStr(style.getStrategy(), clusterType.getAnalyticsClusterId());
            log.debug("existingSizeStr is : " + sizeStrFromRequest);
            String sizeStrToBeUpdated = getUpdatedSizeProfileData(sizeStrFromRequest, existingSizeStrFromDB);
            log.debug("existingSizeStrUpdated is : " + sizeStrToBeUpdated);
            strategyStyleSPCluster.setSizeProfileObj(sizeStrToBeUpdated);
        }
    }

    private Set<StrategyCcSPCluster> setStrategyCustomerChoiceSPClusters(StrategyStyleSPCluster strategyStyleSPCluster, PlanStrategyMapperDTO planStrategyMapperDTO){
        Set<StrategyCcSPCluster> strategyCcSPClusters = Optional.ofNullable(strategyStyleSPCluster.getStrategyCcSPClusters()).orElse(new HashSet<>());

        for (CustomerChoice cc : planStrategyMapperDTO.getCustomerChoices()) {

            if ((getMerchCatgChannelId(strategyStyleSPCluster).equals(ChannelType.getChannelIdFromName(cc.getChannel()))
                    || ChannelType.getChannelIdFromName(cc.getChannel()).equals(3))) {
                setCcSizeProfileAndTotalSizeProfilePrcnt(strategyStyleSPCluster,strategyCcSPClusters,planStrategyMapperDTO,cc);
            }
        }
        return strategyCcSPClusters;
    }

    /***
     * This method sets the total size profile percent for each CCs under a style for a cluster
     * @param strategyCcSPCluster
     * @param mapCCSPClusIdByEligibleSizeIds
     */
    protected void  setCCTotalSizeProfilePrct(StrategyCcSPCluster strategyCcSPCluster, HashMap<StrategyCcSPClusId, Set<Integer>> mapCCSPClusIdByEligibleSizeIds) {
        // Create a Map with key as strategyCcSPCluster with analytics cluster id ZERO and value as list of all eligible AHS Ids
        boolean isZeroAnalyticsCluster = strategyCcSPCluster.getStrategyCcSPClusId().getStrategyStyleSPClusId().getStrategyFinelineSPClusId().getStrategySubCatgSPClusId().getStrategyMerchCatgSPClusId().getPlanClusterStrategyId().getAnalyticsClusterId().equals(0);
        if (isZeroAnalyticsCluster) {
            Set<Integer> eligibleSizeIds = sizeAndPackService.getEligibleSizeIds(strategyCcSPCluster.getSizeProfileObj());
            mapCCSPClusIdByEligibleSizeIds.put(strategyCcSPCluster.getStrategyCcSPClusId(), eligibleSizeIds);
        }
        StrategyCcSPClusId strategyCcSPClusIdKey = sizeAndPackService.getStratCCSPIdKey(mapCCSPClusIdByEligibleSizeIds,strategyCcSPCluster);
        Set<Integer> eligibleAHSSizeIds = mapCCSPClusIdByEligibleSizeIds.get(strategyCcSPClusIdKey);
        strategyCcSPCluster.setTotalSizeProfilePct(sizeAndPackService.getTotalSizeProfilePct(strategyCcSPCluster.getSizeProfileObj(), eligibleAHSSizeIds));
    }

    /***
     * This method sets/updates the size profile object and also the total size profile percent field for strategyCcSPClusters and adds it to the strategyCcSPClusters
     * @param strategyStyleSPCluster
     * @param strategyCcSPClusters
     * @param planStrategyMapperDTO
     */
    private void setCcSizeProfileAndTotalSizeProfilePrcnt(StrategyStyleSPCluster strategyStyleSPCluster, Set<StrategyCcSPCluster> strategyCcSPClusters,PlanStrategyMapperDTO planStrategyMapperDTO,CustomerChoice cc) {
        ClusterType flag = getCcClusterFlag(planStrategyMapperDTO.getChannel(), planStrategyMapperDTO.getClusterMetadata().getSizeCluster().getType(), cc);
        if ((flag != null) || (planStrategyMapperDTO.getChannel().equals(ChannelType.ONLINE.getId()))) {
            StrategyCcSPClusId strategyCcSPClusId = new StrategyCcSPClusId(strategyStyleSPCluster.getStrategyStyleSPClusId(), cc.getCcId());
            log.debug("Check if a strategySubCatgSPClus Id : {} already exists or not", strategyCcSPClusId.toString());
            StrategyCcSPCluster strategyCcSPCluster = Optional.of(strategyCcSPClusters)
                  .stream()
                  .flatMap(Collection::stream).filter(stratCCSPClus -> stratCCSPClus.getStrategyCcSPClusId().equals(strategyCcSPClusId))
                  .findFirst()
                  .orElse(StrategyCcSPCluster.builder()
                        .strategyCcSPClusId(strategyCcSPClusId)
                        .strategyStyleSPCluster(strategyStyleSPCluster).build());

            String existingSizeStrFromDB = strategyCcSPCluster.getSizeProfileObj();
            String sizeStrFromRequest = planStrategyMapperDTO.getChannel().equals(ChannelType.STORE.getId())
                  ? getStoreSizeStr(cc.getStrategy(), planStrategyMapperDTO.getClusterMetadata().getSizeCluster().getType().getAnalyticsClusterId())
                  : getOnlineSizeStr(cc.getStrategy(),  planStrategyMapperDTO.getClusterMetadata().getSizeCluster().getType().getAnalyticsClusterId());
            String sizeStrToBeUpdated = getUpdatedSizeProfileData(sizeStrFromRequest, existingSizeStrFromDB);
            strategyCcSPCluster.setSizeProfileObj(sizeStrToBeUpdated);

            // calling the below method to set the total size profile percent field before adding to strategyCcSPClusters list
            if (featureConfigProperties.getTotalSizePercentFeature()) {
                setCCTotalSizeProfilePrct(strategyCcSPCluster, planStrategyMapperDTO.getClusterMetadata().getMapCCSPClusIdByEligibleSizeIds());
            }
            strategyCcSPClusters.add(strategyCcSPCluster);
        }
    }

    private ClusterType getCcClusterFlag(Integer channel, ClusterType clusterType, CustomerChoice cc) {

        if (channel.equals(ChannelType.STORE.getId())) {
            return Optional.ofNullable(cc.getStrategy())
                    .map(Strategy::getStoreSizeClusters)
                    .stream()
                    .flatMap(Collection::stream)
                    .map(SizeCluster::getType)
                    .filter(clusterType1 -> clusterType1.getAnalyticsClusterId().equals(clusterType.getAnalyticsClusterId()))
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    public void updatePlanStrategyCluster(PlanStrategyMapperDTO planStrategyMapperDTO) {
        Set<StrategyMerchCategorySPCluster> strategyMerchCategorySPClusters =
                planStrategyMapperDTO.getPlanClusterStrategies().stream().map(PlanClusterStrategy::getStrategyMerchCategorySPCluster)
                        .flatMap(Collection::stream)
                        .filter(strategyMerchCategorySPCluster -> strategyMerchCategorySPCluster.getStrategyMerchCatgSPClusId().getLvl3Nbr().equals(planStrategyMapperDTO.getLvl3().getLvl3Nbr()))
                        .collect(Collectors.toSet());

        Integer finelineChannel = ChannelType.getChannelIdFromName(CommonUtil.getRequestedFlChannel(planStrategyMapperDTO.getLvl3()));
        List<Integer> channelList = getChannelList(finelineChannel);
        channelList.forEach(channelId -> {
            if (!CollectionUtils.isEmpty(strategyMerchCategorySPClusters)) {
                deleteCatgSp(strategyMerchCategorySPClusters, planStrategyMapperDTO.getLvl3(), channelId, planStrategyMapperDTO.getPlanClusterStrategies());
            }
        });
        PlanStrategyMapperDTO planStrategyMapperDTO1 = PlanStrategyMapperDTO.builder()
                .planStrategy(planStrategyMapperDTO.getPlanStrategy())
                .lvl1(planStrategyMapperDTO.getLvl1())
                .lvl2(planStrategyMapperDTO.getLvl2())
                .request(planStrategyMapperDTO.getRequest())
                .build();
        setPlanStrategyCluster(planStrategyMapperDTO1);
    }

    private List<Integer> getChannelList(Integer finelineChannel) {
        List<Integer> channelList = new ArrayList<>();
        if (finelineChannel.equals(ChannelType.OMNI.getId())) {
            channelList.add(ChannelType.STORE.getId());
            channelList.add(ChannelType.ONLINE.getId());
        } else channelList.add(finelineChannel);
        return channelList;
    }

    private void deleteCatgSp(Set<StrategyMerchCategorySPCluster> strategyMerchCategorySPClusters, Lvl3 lvl3, Integer channelId, Set<PlanClusterStrategy> planClusterStrategies) {
        deleteSubCatgSp(strategyMerchCategorySPClusters, lvl3, channelId);
        if (!channelId.equals(3)) {
            strategyMerchCategorySPClusters.removeIf(strategyMerchCategorySPCluster -> CollectionUtils.isEmpty(strategyMerchCategorySPCluster.getStrategySubCatgSPClusters()) && strategyMerchCategorySPCluster.getStrategyMerchCatgSPClusId().getLvl3Nbr().equals(lvl3.getLvl3Nbr()) &&
                    !strategyMerchCategorySPCluster.getStrategyMerchCatgSPClusId().getChannelId().equals(channelId));
        }
        /** Delete the stale Category Clusters if its analytics_clusterId is not existing with their associated subcategory clusters **/
        planClusterStrategies.forEach(planClusterStrategy -> planClusterStrategy.getStrategyMerchCategorySPCluster().removeIf(strategyMerchCategorySPCluster ->
                CollectionUtils.isEmpty(strategyMerchCategorySPCluster.getStrategySubCatgSPClusters()) && strategyMerchCategorySPCluster.getStrategyMerchCatgSPClusId().getLvl3Nbr().equals(lvl3.getLvl3Nbr())));
    }

    private void deleteSubCatgSp(Set<StrategyMerchCategorySPCluster> strategyMerchCategorySPClusters, Lvl3 lvl3, Integer channelId) {
        lvl3.getLvl4List().forEach(lvl4 -> {
            deleteFinelineSp(strategyMerchCategorySPClusters, lvl3, lvl4, channelId);
            Set<StrategyMerchCategorySPCluster> strategyMerchCategorySPClusters1 = fetchStrategyCatgSpClus(strategyMerchCategorySPClusters, lvl3.getLvl3Nbr());
            if (!CollectionUtils.isEmpty(strategyMerchCategorySPClusters1)) {
                if (!channelId.equals(3)) {
                    strategyMerchCategorySPClusters1.forEach(strategyMerchCategorySPCluster -> strategyMerchCategorySPCluster.getStrategySubCatgSPClusters().removeIf(strategySubCategorySPCluster -> CollectionUtils.isEmpty(strategySubCategorySPCluster.getStrategyFinelinesSPCluster()) && strategySubCategorySPCluster.getStrategySubCatgSPClusId().getLvl4Nbr().equals(lvl4.getLvl4Nbr()) &&
                            !strategySubCategorySPCluster.getStrategySubCatgSPClusId().getStrategyMerchCatgSPClusId().getChannelId().equals(channelId)));

                }
                /** Delete the stale Subcategory Clusters if its analytics_clusterId is not existing with their associated fineLine clusters **/
                strategyMerchCategorySPClusters1.forEach(strategyMerchCategorySPCluster ->
                        strategyMerchCategorySPCluster.getStrategySubCatgSPClusters().removeIf(strategySubCategorySPCluster -> CollectionUtils.isEmpty(strategySubCategorySPCluster.getStrategyFinelinesSPCluster()) && strategySubCategorySPCluster.getStrategySubCatgSPClusId().getLvl4Nbr().equals(lvl4.getLvl4Nbr())));
            }
        });
    }

    private void deleteFinelineSp(Set<StrategyMerchCategorySPCluster> strategyMerchCategorySPClusters, Lvl3 lvl3, Lvl4 lvl4, Integer channelId) {
        lvl4.getFinelines().forEach(fineline -> {
            deleteStyleSp(strategyMerchCategorySPClusters, lvl3, lvl4, fineline, channelId);
            Set<StrategySubCategorySPCluster> strategySubCategorySPClusters = fetchStrategySubCatgSpClus(strategyMerchCategorySPClusters, lvl3.getLvl3Nbr(), lvl4.getLvl4Nbr());
            if (!CollectionUtils.isEmpty(strategySubCategorySPClusters)) {
                if (!ChannelType.getChannelIdFromName(fineline.getChannel()).equals(3)) {
                    strategySubCategorySPClusters.forEach(strategySubCategorySPCluster -> {
                        strategySubCategorySPCluster.getStrategyFinelinesSPCluster().removeIf(strategyFineLineSPCluster -> CollectionUtils.isEmpty(strategyFineLineSPCluster.getStrategyStylesSPClusters()) && strategyFineLineSPCluster.getStrategyIFineLineId().getFinelineNbr().equals(fineline.getFinelineNbr()) &&
                                !strategyFineLineSPCluster.getStrategyIFineLineId()
                                        .getStrategySubCatgSPClusId().getStrategyMerchCatgSPClusId().getChannelId().equals(ChannelType.getChannelIdFromName(fineline.getChannel())));
                    });
                }
                /** Delete the stale FineLine Clusters if its analytics_clusterId is not existing with their associated Style clusters **/
                strategySubCategorySPClusters.forEach(strategySubCategorySPCluster ->
                        strategySubCategorySPCluster.getStrategyFinelinesSPCluster().removeIf(strategyFineLineSPCluster -> CollectionUtils.isEmpty(strategyFineLineSPCluster.getStrategyStylesSPClusters()) && strategyFineLineSPCluster.getStrategyIFineLineId().getFinelineNbr().equals(fineline.getFinelineNbr())));
            }
        });
    }

    private void deleteStyleSp(Set<StrategyMerchCategorySPCluster> strategyMerchCategorySPClusters, Lvl3 lvl3, Lvl4 lvl4, Fineline fineline, Integer channelId) {
        fineline.getStyles().forEach(style -> {
            deleteCcSp(strategyMerchCategorySPClusters, lvl3, lvl4, fineline, style, channelId);
            Set<StrategyFineLineSPCluster> strategyFineLineSPClusters = fetchStrategyFinelineSpClus(strategyMerchCategorySPClusters, lvl3.getLvl3Nbr(), lvl4.getLvl4Nbr(), fineline.getFinelineNbr());
            if (!CollectionUtils.isEmpty(strategyFineLineSPClusters)) {
                if (!ChannelType.getChannelIdFromName(style.getChannel()).equals(3)) {
                    strategyFineLineSPClusters.forEach(strategyFineLineSPCluster -> strategyFineLineSPCluster.getStrategyStylesSPClusters().removeIf(strategyStyleSPCluster -> CollectionUtils.isEmpty(strategyStyleSPCluster.getStrategyCcSPClusters()) && strategyStyleSPCluster.getStrategyStyleSPClusId().getStyleNbr().equalsIgnoreCase(style.getStyleNbr()) &&
                            !strategyStyleSPCluster.getStrategyStyleSPClusId().getStrategyFinelineSPClusId()
                                    .getStrategySubCatgSPClusId().getStrategyMerchCatgSPClusId().getChannelId().equals(ChannelType.getChannelIdFromName(style.getChannel()))));
                }
                /** Delete the stale Style Clusters if its analytics_clusterId is not existing with their associated CC clusters **/
                strategyFineLineSPClusters.forEach(strategyFineLineSPCluster ->
                        strategyFineLineSPCluster.getStrategyStylesSPClusters().removeIf(strategyStyleSPCluster -> CollectionUtils.isEmpty(strategyStyleSPCluster.getStrategyCcSPClusters()) && strategyStyleSPCluster.getStrategyStyleSPClusId().getStyleNbr().equalsIgnoreCase(style.getStyleNbr())));
            }
        });
    }

    private void deleteCcSp(Set<StrategyMerchCategorySPCluster> strategyMerchCategorySPClusters, Lvl3 lvl3, Lvl4 lvl4, Fineline fineline, Style style, Integer channelId) {
        style.getCustomerChoices().forEach(customerChoice -> {
            if (!ChannelType.getChannelIdFromName(customerChoice.getChannel()).equals(3)) {
                Set<StrategyStyleSPCluster> strategyStyleSPClusters1 = fetchStrategyStyleSpClus(strategyMerchCategorySPClusters, lvl3.getLvl3Nbr(), lvl4.getLvl4Nbr(), fineline.getFinelineNbr(), style.getStyleNbr());
                if (!CollectionUtils.isEmpty(strategyStyleSPClusters1)) {
                    strategyStyleSPClusters1.forEach(strategyStyleSPCluster -> strategyStyleSPCluster.getStrategyCcSPClusters().removeIf(strategyCcSPCluster -> strategyCcSPCluster.getStrategyCcSPClusId().getCcId().equalsIgnoreCase(customerChoice.getCcId()) &&
                            !strategyCcSPCluster.getStrategyCcSPClusId().getStrategyStyleSPClusId().getStrategyFinelineSPClusId()
                                    .getStrategySubCatgSPClusId().getStrategyMerchCatgSPClusId().getChannelId().equals(ChannelType.getChannelIdFromName(customerChoice.getChannel()))));
                }
            }
            /** Delete the stale Customer_choiceSPCluster if its analytics_clusterId is not matching with request payload **/
            deleteStaleCcSp(strategyMerchCategorySPClusters, lvl3, lvl4, fineline, style, customerChoice, channelId);
        });
    }

    private Set<StrategyMerchCategorySPCluster> fetchStrategyCatgSpClus(Set<StrategyMerchCategorySPCluster> strategyMerchCategorySPClusters, Integer lvl3Nbr) {
        return Optional.ofNullable(strategyMerchCategorySPClusters)
                .stream()
                .flatMap(Collection::stream)
                .filter(strategyMerchCategorySPCluster -> strategyMerchCategorySPCluster.getStrategyMerchCatgSPClusId().getLvl3Nbr().equals(lvl3Nbr))
                .collect(Collectors.toSet());
    }

    private Set<StrategySubCategorySPCluster> fetchStrategySubCatgSpClus(Set<StrategyMerchCategorySPCluster> strategyMerchCategorySPClusters, Integer lvl3Nbr, Integer lvl4Nbr) {
        return Optional.ofNullable(strategyMerchCategorySPClusters)
                .stream()
                .flatMap(Collection::stream)
                .filter(strategyMerchCategorySPCluster -> strategyMerchCategorySPCluster.getStrategyMerchCatgSPClusId().getLvl3Nbr().equals(lvl3Nbr))
                .map(StrategyMerchCategorySPCluster::getStrategySubCatgSPClusters)
                .flatMap(Collection::stream)
                .filter(strategySubCategorySPCluster -> strategySubCategorySPCluster.getStrategySubCatgSPClusId().getLvl4Nbr().equals(lvl4Nbr))
                .collect(Collectors.toSet());
    }

    private Set<StrategyFineLineSPCluster> fetchStrategyFinelineSpClus(Set<StrategyMerchCategorySPCluster> strategyMerchCategorySPClusters, Integer lvl3Nbr, Integer lvl4Nbr, Integer finelineNbr) {
        return Optional.ofNullable(strategyMerchCategorySPClusters)
                .stream()
                .flatMap(Collection::stream)
                .filter(strategyMerchCategorySPCluster -> strategyMerchCategorySPCluster.getStrategyMerchCatgSPClusId().getLvl3Nbr().equals(lvl3Nbr))
                .map(StrategyMerchCategorySPCluster::getStrategySubCatgSPClusters)
                .flatMap(Collection::stream)
                .filter(strategySubCategorySPCluster -> strategySubCategorySPCluster.getStrategySubCatgSPClusId().getLvl4Nbr().equals(lvl4Nbr))
                .map(StrategySubCategorySPCluster::getStrategyFinelinesSPCluster)
                .flatMap(Collection::stream)
                .filter(strategyFineLineSPCluster -> strategyFineLineSPCluster.getStrategyIFineLineId().getFinelineNbr().equals(finelineNbr))
                .collect(Collectors.toSet());
    }

    private Set<StrategyStyleSPCluster> fetchStrategyStyleSpClus(Set<StrategyMerchCategorySPCluster> strategyMerchCategorySPClusters, Integer lvl3Nbr, Integer lvl4Nbr, Integer finelineNbr, String styleNum) {
        return Optional.ofNullable(strategyMerchCategorySPClusters)
                .stream()
                .flatMap(Collection::stream)
                .filter(strategyMerchCategorySPCluster -> strategyMerchCategorySPCluster.getStrategyMerchCatgSPClusId().getLvl3Nbr().equals(lvl3Nbr))
                .map(StrategyMerchCategorySPCluster::getStrategySubCatgSPClusters)
                .flatMap(Collection::stream)
                .filter(strategySubCategorySPCluster -> strategySubCategorySPCluster.getStrategySubCatgSPClusId().getLvl4Nbr().equals(lvl4Nbr))
                .map(StrategySubCategorySPCluster::getStrategyFinelinesSPCluster)
                .flatMap(Collection::stream)
                .filter(strategyFineLineSPCluster -> strategyFineLineSPCluster.getStrategyIFineLineId().getFinelineNbr().equals(finelineNbr))
                .map(StrategyFineLineSPCluster::getStrategyStylesSPClusters)
                .flatMap(Collection::stream)
                .filter(strategyStyleSPCluster -> strategyStyleSPCluster.getStrategyStyleSPClusId().getStyleNbr().equals(styleNum))
                .collect(Collectors.toSet());
    }

    public SizeProfileDTO[] safeReadSizeObject(String sizeObj) {
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

    /*
    Update the existing size association with the request
     */
    public String getUpdatedSizeProfileData(String sizeStrFromRequest, String existingSizeStrFromDB) {
        String updatedSizeStr = null;
        try {
            //For update, before overriding with new values, check the existing size associations
            if (existingSizeStrFromDB != null && !existingSizeStrFromDB.isEmpty()) {
                SizeProfileDTO[] newSizeProfileDTOS = safeReadSizeObject(sizeStrFromRequest);
                List<SizeProfileDTO> existingSizeProfileDTOSList = Arrays.asList(safeReadSizeObject(existingSizeStrFromDB));
                List<SizeProfileDTO> existingEligibleSizeProfileList = existingSizeProfileDTOSList.stream()
                      .filter(sizeProfile -> (sizeProfile.getIsEligible() > 0 || (sizeProfile.getAdjustedSizeProfile() != null && !sizeProfile.getAdjustedSizeProfile().equals(sizeProfile.getSizeProfilePrcnt()))))
                        .collect(Collectors.toList());

                if (!CollectionUtils.isEmpty(existingEligibleSizeProfileList) && newSizeProfileDTOS.length > 0) {
                    existingEligibleSizeProfileList.forEach(existingSizeProfile -> persistExistingSizeUserSelections(newSizeProfileDTOS, existingSizeProfile));
                    try {
                        updatedSizeStr = objectMapper.writeValueAsString(newSizeProfileDTOS);
                    } catch (JsonProcessingException e) {
                        log.error("Error replacing the existing associated sizes to new ones {}, {}", existingSizeStrFromDB, sizeStrFromRequest);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error parsing the size lists {}, {}", existingSizeStrFromDB, sizeStrFromRequest, e);
        }
        if (updatedSizeStr == null || updatedSizeStr.isEmpty()) {
            updatedSizeStr = sizeStrFromRequest;
        }
        return updatedSizeStr;
    }

    private void persistExistingSizeUserSelections(SizeProfileDTO[] newSizeProfileDTOS, SizeProfileDTO existingSizeProfile) {
        for (SizeProfileDTO newSizeProfileDTO : newSizeProfileDTOS) {
            if (newSizeProfileDTO.getAhsSizeId() != null && newSizeProfileDTO.getAhsSizeId().equals(existingSizeProfile.getAhsSizeId())) {
                newSizeProfileDTO.setIsEligible(existingSizeProfile.getIsEligible());
                if (existingSizeProfile.getAdjustedSizeProfile() != null && !existingSizeProfile.getAdjustedSizeProfile().equals(existingSizeProfile.getSizeProfilePrcnt())) {
                    newSizeProfileDTO.setAdjustedSizeProfile(existingSizeProfile.getAdjustedSizeProfile());
                }
            }
        }
    }

    //This method is created to use it in the test class
    public String getUpdatedSizeStrInDB(String sizeStrFromRequest, String existingSizeStrFromDB) {
          return getUpdatedSizeProfileData(sizeStrFromRequest, existingSizeStrFromDB);
    }

    private void deleteStaleCcSp(Set<StrategyMerchCategorySPCluster> strategyMerchCategorySPClusters, Lvl3 lvl3, Lvl4 lvl4, Fineline fineline, Style style, CustomerChoice customerChoice, Integer channelId) {
        Set<StrategyStyleSPCluster> strategyStyleSPClusters = fetchStrategyStyleSpClus(strategyMerchCategorySPClusters, lvl3.getLvl3Nbr(), lvl4.getLvl4Nbr(), fineline.getFinelineNbr(), style.getStyleNbr());
        Set<Integer> analyticsClusterIdsFromRequest = getAnalyticsClusterIdsFromRequest(customerChoice, channelId);
        /** Compare request analytic_clusterId with db cc_analytic_clusterId data if there is no match then remove the cc */
        if (!CollectionUtils.isEmpty(strategyStyleSPClusters) && !CollectionUtils.isEmpty(analyticsClusterIdsFromRequest)) {
            strategyStyleSPClusters.forEach(strategyStyleSPCluster ->
                    strategyStyleSPCluster.getStrategyCcSPClusters().removeAll(getUnreferencedCCSPClusters(strategyStyleSPCluster, customerChoice, channelId, analyticsClusterIdsFromRequest)));
        }
    }


    private Set<StrategyCcSPCluster> getUnreferencedCCSPClusters(StrategyStyleSPCluster strategyStyleSPCluster, CustomerChoice requestCC, Integer channelId, Set<Integer> analyticsClusterIdsFromRequest) {
        Set<StrategyCcSPCluster> strategyCcSPClusters = strategyStyleSPCluster.getStrategyCcSPClusters();
        Set<StrategyCcSPCluster> removeList = new HashSet<>();
        if (CollectionUtils.isEmpty(strategyCcSPClusters)) {
            return removeList;
        }
        for (StrategyCcSPCluster strategyCcSPCluster : strategyCcSPClusters) {
            String dbCCId = strategyCcSPCluster.getStrategyCcSPClusId().getCcId();
            Integer dbChannelId = strategyCcSPCluster.getStrategyCcSPClusId().getStrategyStyleSPClusId().getStrategyFinelineSPClusId()
                    .getStrategySubCatgSPClusId().getStrategyMerchCatgSPClusId().getChannelId();
            Integer dbAnalyticsClusterId = getCCClusterAnalyticsIdFromDB(strategyCcSPCluster);
            /** Compare DB data (customer_choice, analytics_cluster_id, Channel_Id) with request payload (customerChoices, analyticsClusterId, channel and collect the stale record) **/
            if (dbCCId.equals(requestCC.getCcId()) && dbChannelId.equals(channelId) && !dbAnalyticsClusterId.equals(0) && !analyticsClusterIdsFromRequest.contains(dbAnalyticsClusterId)) {
                removeList.add(strategyCcSPCluster);
            }
        }
        return removeList;
    }

    private Set<Integer> getAnalyticsClusterIdsFromRequest(CustomerChoice customerChoice, Integer channelId) {
        Set<Integer> analyticsClusterIds = new HashSet<>();
        Strategy strategy = customerChoice.getStrategy();
        if (strategy != null) {
            List<SizeCluster> sizeClusters = channelId.equals(ChannelType.STORE.getId()) ? strategy.getStoreSizeClusters() : strategy.getOnlineSizeClusters();
            analyticsClusterIds = getAnalyticsSizeClusterIdsFromRequest(sizeClusters);
        }
        return analyticsClusterIds;
    }

    private Set<Integer> getAnalyticsSizeClusterIdsFromRequest(List<SizeCluster> sizeClusters) {
        return Optional.ofNullable(sizeClusters)
                .stream()
                .flatMap(Collection::stream)
                .map(SizeCluster::getType)
                .filter(Objects::nonNull)
                .map(ClusterType::getAnalyticsClusterId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    private Integer getCCClusterAnalyticsIdFromDB(StrategyCcSPCluster strategyCcSPCluster) {
        return Optional.ofNullable(strategyCcSPCluster.getStrategyStyleSPCluster())
                .stream()
                .map(StrategyStyleSPCluster::getStrategyFinelineSPClus)
                .filter(Objects::nonNull)
                .map(StrategyFineLineSPCluster::getStrategySubCatgSpClus)
                .filter(Objects::nonNull)
                .map(StrategySubCategorySPCluster::getStrategyMerchCatgSPClusters)
                .filter(Objects::nonNull)
                .map(StrategyMerchCategorySPCluster::getPlanClusterStrategy)
                .filter(Objects::nonNull)
                .map(PlanClusterStrategy::getPlanClusterStrategyId)
                .filter(Objects::nonNull)
                .map(PlanClusterStrategyId::getAnalyticsClusterId).findFirst().orElse(0);
    }

    private Integer getMerchCatgChannelId(StrategyFineLineSPCluster strategyFineLineSPCluster) {
        return strategyFineLineSPCluster.getStrategyIFineLineId().getStrategySubCatgSPClusId().getStrategyMerchCatgSPClusId().getChannelId();
    }
    private Integer getMerchCatgChannelId(StrategyStyleSPCluster strategyStyleSPCluster) {
        return getMerchCatgChannelId(strategyStyleSPCluster.getStrategyFinelineSPClus());
    }
 }