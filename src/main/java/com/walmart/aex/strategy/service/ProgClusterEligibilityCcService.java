package com.walmart.aex.strategy.service;

import com.walmart.aex.strategy.dto.request.CustomerChoice;
import com.walmart.aex.strategy.dto.request.Style;
import com.walmart.aex.strategy.dto.request.UpdatedFields;
import com.walmart.aex.strategy.dto.request.WeatherCluster;
import com.walmart.aex.strategy.entity.*;
import com.walmart.aex.strategy.enums.IncludeOffshoreMkt;
import com.walmart.aex.strategy.properties.AppProperties;
import com.walmart.aex.strategy.repository.EligCcClusProgRepository;
import com.walmart.aex.strategy.util.CommonUtil;
import io.strati.ccm.utils.client.annotation.ManagedConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManager;
import java.util.*;
import java.util.stream.Collectors;

import static com.walmart.aex.strategy.service.ClusterEligibilityFlService.IN_STORE_DATE;
import static com.walmart.aex.strategy.service.ClusterEligibilityFlService.MARK_DOWN_DATE;

@Service
@Slf4j
public class ProgClusterEligibilityCcService {

    private final EligCcClusProgRepository eligCcClusProgRepository;

    @Autowired
    private EntityManager entityManager;

    @ManagedConfiguration
    private AppProperties appProperties;

    public ProgClusterEligibilityCcService(EligCcClusProgRepository eligCcClusProgRepository) {
        this.eligCcClusProgRepository = eligCcClusProgRepository;
    }

    public void updateProgClusterEligibilityMetricsAtStyleCc(Set<StrategyFlClusPrgmEligRanking> strategyFlClusPrgmEligRankings,
                                                             Set<StrategyFlClusEligRanking> strategyFlClusEligRankings,
                                                             List<Style> styles, PlanStrategyId planStrategyId, Integer finelineNbr, Long programId, Set<String> updatedField) {
        for (Style style : styles) {
            if (!CollectionUtils.isEmpty(style.getCustomerChoices())) {
                for (CustomerChoice customerChoice : style.getCustomerChoices()) {
                    Map<String, String> commonCcUpdatedFields = Optional.ofNullable(customerChoice.getUpdatedFields())
                            .map(UpdatedFields::getWeatherCluster)
                            .map(CommonUtil::getUpdatedFieldsMap)
                            .orElse(null);
                    if (commonCcUpdatedFields != null) {
                        log.info("Updating the cc :{} level field for: {}", customerChoice.getCcId(), StringUtils.join(commonCcUpdatedFields));
                        if (commonCcUpdatedFields.containsKey(IN_STORE_DATE)) updatedField.add(IN_STORE_DATE);
                        if (commonCcUpdatedFields.containsKey(MARK_DOWN_DATE)) updatedField.add(MARK_DOWN_DATE);
                        Set<EligCcClusProg> eligCcClusProgs =
                                eligCcClusProgRepository.findEligCcClusProgByEligCcClusProgId_EligStyleClusProgId_StrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_PlanClusterStrategyId_PlanStrategyIdAndEligCcClusProgId_EligStyleClusProgId_StrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_finelineNbrAndEligCcClusProgId_EligStyleClusProgId_styleNbrAndEligCcClusProgId_ccIdAndEligCcClusProgId_EligStyleClusProgId_StrategyFlClusPrgmEligRankingId_ProgramId
                                        (planStrategyId, finelineNbr, style.getStyleNbr(), customerChoice.getCcId(), programId)
                                        .orElse(new HashSet<>());
                        //Check if Program cluster level Fineline, Style & Cc Entities Exists if not Populate based on CLuster Eligibility entities
                        if (CollectionUtils.isEmpty(strategyFlClusEligRankings) || CollectionUtils.isEmpty(eligCcClusProgs)) {
                            checkIfFlStyleCcProgExists(strategyFlClusPrgmEligRankings, strategyFlClusEligRankings, programId,
                                    style.getStyleNbr(), customerChoice.getCcId());
                            eligCcClusProgs = Optional.ofNullable(strategyFlClusPrgmEligRankings)
                                    .stream()
                                    .flatMap(Collection::stream)
                                    .map(strategyFlClusPrgmEligRanking -> fetchProgStyleCcs(strategyFlClusPrgmEligRanking.getEligStyleClusProgs(), style.getStyleNbr(), customerChoice.getCcId()))
                                    .collect(Collectors.toSet());
                        }
                        //Update the Metrics based on the request
                        updateProgCcMetrics(strategyFlClusPrgmEligRankings, commonCcUpdatedFields, eligCcClusProgs, customerChoice, planStrategyId);
                    }
                }
            }
        }
    }

    public EligCcClusProg fetchProgStyleCcs(Set<EligStyleClusProg> eligStyleClusProgs, String styleNbr, String ccId) {
        return Optional.ofNullable(eligStyleClusProgs)
                .stream()
                .flatMap(Collection::stream)
                .filter(eligStyleClusProg -> eligStyleClusProg.getEligStyleClusProgId().getStyleNbr().equals(styleNbr))
                .findFirst()
                .map(eligStyleClusProg -> fetchProgCcs(eligStyleClusProg.getEligCcClusProgs(), ccId))
                .orElse(null);
    }

    public EligCcClusProg fetchProgCcs(Set<EligCcClusProg> eligCcClusProgs, String ccId) {
        return Optional.ofNullable(eligCcClusProgs)
                .stream()
                .flatMap(Collection::stream)
                .filter(eligCcClusProg -> eligCcClusProg.getEligCcClusProgId().getCcId().equals(ccId))
                .findFirst().orElse(null);
    }

    public void checkIfFlStyleCcProgExists(Set<StrategyFlClusPrgmEligRanking> strategyFlClusPrgmEligRankings,
                                           Set<StrategyFlClusEligRanking> strategyFlClusEligRankings, Long programId, String styleNbr, String ccId) {
        Optional.ofNullable(strategyFlClusEligRankings)
                .stream()
                .flatMap(Collection::stream)
                .forEach(strategyFlClusEligRanking -> strategyFlClusPrgmEligRankings.add(createFlStyleProgForEachClus(strategyFlClusPrgmEligRankings,
                        strategyFlClusEligRanking, programId, styleNbr, ccId)));
    }

    public StrategyFlClusPrgmEligRanking createFlStyleProgForEachClus(Set<StrategyFlClusPrgmEligRanking> strategyFlClusPrgmEligRankings,
                                                                      StrategyFlClusEligRanking strategyFlClusEligRanking, Long programId,
                                                                      String styleNbr, String ccId) {
        StrategyStyleClusId strategyStyleClusId = new StrategyStyleClusId(strategyFlClusEligRanking.getStrategyFlClusEligRankingId(), styleNbr);
        StrategyCcClusEligRankingId strategyCcClusEligRankingId = new StrategyCcClusEligRankingId(strategyStyleClusId, ccId);

        StrategyCcClusEligRanking strategyCcClusEligRanking = Optional.ofNullable(strategyFlClusEligRanking.getStrategyStyleCluses())
                .stream()
                .flatMap(Collection::stream)
                .filter(strategyStyleClus -> checkStyleClusterExists(strategyStyleClus, strategyStyleClusId))
                .findFirst()
                .map(StrategyStyleClus::getStrategyCcClusEligRankings)
                .stream()
                .flatMap(Collection::stream)
                .filter(strategyCcClusEligRanking1 -> checkCcClusterExists(strategyStyleClusId, strategyCcClusEligRanking1, strategyCcClusEligRankingId))
                .findFirst()
                .orElse(null);

        StrategyFlClusPrgmEligRankingId strategyFlClusPrgmEligRankingId = new StrategyFlClusPrgmEligRankingId(
                strategyFlClusEligRanking.getStrategyFlClusEligRankingId(), programId);

        StrategyFlClusPrgmEligRanking strategyFlClusPrgmEligRanking = Optional.ofNullable(strategyFlClusPrgmEligRankings)
                .stream()
                .flatMap(Collection::stream)
                .filter(strategyFlClusPrgmEligRanking1 -> strategyFlClusPrgmEligRanking1.getStrategyFlClusPrgmEligRankingId().equals(strategyFlClusPrgmEligRankingId))
                .findFirst()
                .orElse(new StrategyFlClusPrgmEligRanking());

        //Initial setup
        if (strategyFlClusPrgmEligRanking.getStrategyFlClusPrgmEligRankingId() == null) {
            strategyFlClusPrgmEligRanking.setStrategyFlClusPrgmEligRankingId(strategyFlClusPrgmEligRankingId);
            //default value to false
            strategyFlClusPrgmEligRanking.setIsEligibleFlag(0);
            strategyFlClusPrgmEligRanking.setMarkDownYrWk(strategyFlClusEligRanking.getMarkDownYrWk());
            strategyFlClusPrgmEligRanking.setMarkDownYrWkDesc(strategyFlClusEligRanking.getMarkDownYrWkDesc());
            strategyFlClusPrgmEligRanking.setInStoreYrWk(strategyFlClusEligRanking.getInStoreYrWk());
            strategyFlClusPrgmEligRanking.setInStoreYrWkDesc(strategyFlClusEligRanking.getInStoreYrWkDesc());
        }
        EligStyleClusProgId eligStyleClusProgId = new EligStyleClusProgId(strategyFlClusPrgmEligRankingId, styleNbr);
        EligStyleClusProg eligStyleClusProg =
                Optional.ofNullable(strategyFlClusPrgmEligRanking.getEligStyleClusProgs())
                        .stream()
                        .flatMap(Collection::stream)
                        .filter(eligStyleClusProg1 -> checkStyleClusterProgExists(eligStyleClusProg1, eligStyleClusProgId))
                        .findFirst()
                        .orElse(new EligStyleClusProg());
        if (eligStyleClusProg.getEligStyleClusProgId() == null) {
            eligStyleClusProg.setEligStyleClusProgId(eligStyleClusProgId);
        }
        EligCcClusProgId eligCcClusProgId = new EligCcClusProgId(eligStyleClusProgId, ccId);
        EligCcClusProg eligCcClusProg = Optional.ofNullable(eligStyleClusProg.getEligCcClusProgs())
                .stream()
                .flatMap(Collection::stream)
                .filter(eligCcClusProg1 -> checkCcClusterProgExists(eligStyleClusProg, eligCcClusProg1, eligCcClusProgId))
                .findFirst()
                .orElse(new EligCcClusProg());
        if (eligCcClusProg.getEligCcClusProgId() == null) {
            eligCcClusProg.setEligCcClusProgId(eligCcClusProgId);
            eligCcClusProg.setIsEligibleFlag(0);
            eligCcClusProg.setInStoreYrWk(Optional.ofNullable(strategyCcClusEligRanking)
                    .map(StrategyCcClusEligRanking::getInStoreYrWk)
                    .orElse(null));
            eligCcClusProg.setInStoreYrWkDesc(Optional.ofNullable(strategyCcClusEligRanking)
                    .map(StrategyCcClusEligRanking::getInStoreYrWkDesc)
                    .orElse(null));
            eligCcClusProg.setMarkDownYrWk(Optional.ofNullable(strategyCcClusEligRanking)
                    .map(StrategyCcClusEligRanking::getMarkDownYrWk)
                    .orElse(null));
            eligCcClusProg.setMarkDownYrWkDesc(Optional.ofNullable(strategyCcClusEligRanking)
                    .map(StrategyCcClusEligRanking::getMarkDownYrWkDesc)
                    .orElse(null));
        }
        if (eligStyleClusProg.getEligCcClusProgs() == null) {
            eligStyleClusProg.setEligCcClusProgs(Collections.singleton(eligCcClusProg));
        } else {
            eligStyleClusProg.getEligCcClusProgs().add(eligCcClusProg);
        }
        if (strategyFlClusPrgmEligRanking.getEligStyleClusProgs() == null) {
            strategyFlClusPrgmEligRanking.setEligStyleClusProgs(Collections.singleton(eligStyleClusProg));
        } else {
            strategyFlClusPrgmEligRanking.getEligStyleClusProgs().add(eligStyleClusProg);
        }
        return strategyFlClusPrgmEligRanking;
    }

    private boolean checkStyleClusterExists(StrategyStyleClus strategyStyleClus, StrategyStyleClusId strategyStyleClusId) {
        StrategyStyleClusId strategyStyleClusId1 = new StrategyStyleClusId(strategyStyleClus.getStrategyStyleClusId().getStrategyFlClusEligRankingId(),
                strategyStyleClus.getStrategyStyleClusId().getStyleNbr());
        return strategyStyleClusId1.equals(strategyStyleClusId);
    }

    private boolean checkCcClusterExists(StrategyStyleClusId strategyStyleClusId, StrategyCcClusEligRanking strategyCcClusEligRanking, StrategyCcClusEligRankingId strategyCcClusEligRankingId) {
        StrategyCcClusEligRankingId strategyCcClusEligRankingId1 = new StrategyCcClusEligRankingId(strategyStyleClusId,
                strategyCcClusEligRanking.getStrategyCcClusEligRankingId().getCcId());
        return strategyCcClusEligRankingId1.equals(strategyCcClusEligRankingId);
    }

    private boolean checkStyleClusterProgExists(EligStyleClusProg eligStyleClusProg, EligStyleClusProgId eligStyleClusProgId) {
        EligStyleClusProgId eligStyleClusProgId1 = new EligStyleClusProgId(eligStyleClusProg.getEligStyleClusProgId().getStrategyFlClusPrgmEligRankingId(),
                eligStyleClusProg.getEligStyleClusProgId().getStyleNbr());
        return eligStyleClusProgId1.equals(eligStyleClusProgId);
    }

    private boolean checkCcClusterProgExists(EligStyleClusProg eligStyleClusProg, EligCcClusProg eligCcClusProg, EligCcClusProgId eligCcClusProgId) {
        EligStyleClusProgId eligStyleClusProgId1 = new EligStyleClusProgId(eligStyleClusProg.getEligStyleClusProgId().getStrategyFlClusPrgmEligRankingId(),
                eligStyleClusProg.getEligStyleClusProgId().getStyleNbr());
        EligCcClusProgId eligCcClusProgId1 = new EligCcClusProgId(eligStyleClusProgId1, eligCcClusProg.getEligCcClusProgId().getCcId());
        return eligCcClusProgId1.equals(eligCcClusProgId);
    }

    public void updateProgCcMetrics(Set<StrategyFlClusPrgmEligRanking> strategyFlClusPrgmEligRankings, Map<String, String> commonCcUpdatedFields,
                                    Set<EligCcClusProg> eligCcClusProgs, CustomerChoice customerChoice, PlanStrategyId planStrategyId) {
        WeatherCluster weatherClusterCc = CommonUtil.getWeatherClusterCc(customerChoice);
        Integer analyticsClusterId = CommonUtil.getAnalyticsClusterId(weatherClusterCc);
        //Set Attributes at cluster All level
        if (analyticsClusterId.equals(0)) {
            setStyleCcMetricsForAll(commonCcUpdatedFields, eligCcClusProgs, weatherClusterCc);
        } else {
            setStyleCcMetricsForClusterN(commonCcUpdatedFields, eligCcClusProgs, weatherClusterCc,
                    analyticsClusterId, planStrategyId);
        }
    }

    public void setStyleCcMetricsForClusterN(Map<String, String> commonCcUpdatedFields, Set<EligCcClusProg> eligCcClusProgs,
                                             WeatherCluster weatherClusterCc, Integer analyticsClusterId, PlanStrategyId planStrategyId) {
        //Set isEligibility for N clusterType
        if (commonCcUpdatedFields.containsKey("isEligible")) {
            Integer isEligibleFlag = Optional.ofNullable(weatherClusterCc)
                    .map(WeatherCluster::getIsEligible)
                    .map(flag -> Boolean.TRUE.equals(flag) ? 1 : 0)
                    .orElse(null);
            setCcIsEligibleForClusterN(eligCcClusProgs, planStrategyId, isEligibleFlag, analyticsClusterId);
        }
        //inStore Date at Cluster N
        if (commonCcUpdatedFields.containsKey("inStoreDate")) {
            setCcInStoreDateForClusterN(eligCcClusProgs, analyticsClusterId, planStrategyId, weatherClusterCc);
        }
        //MarkDown Date at Cluster N
        if (commonCcUpdatedFields.containsKey("markDownDate")) {
            setCcMarkDownDateForClusterN(eligCcClusProgs, analyticsClusterId, planStrategyId, weatherClusterCc);
        }
        //exclude offshore Selection at Cluster N
        if (commonCcUpdatedFields.containsKey("includeOffshore")) {
            setCcExcludeOffshoreForClusterN(eligCcClusProgs, analyticsClusterId, planStrategyId, weatherClusterCc);
        }
        //Rank at Cluster N
        if (commonCcUpdatedFields.containsKey("ranking")) {
            setCcRankingForClusterN(eligCcClusProgs, analyticsClusterId, planStrategyId, weatherClusterCc);
        }
    }

    private void setCcExcludeOffshoreForClusterN(Set<EligCcClusProg> eligCcClusProgs, Integer analyticsClusterId, PlanStrategyId planStrategyId,
                                                 WeatherCluster weatherClusterCc) {
        PlanClusterStrategyId planClusterStrategyId = new PlanClusterStrategyId(planStrategyId, analyticsClusterId);
        List<Integer> offshoreExclude = Optional.ofNullable(weatherClusterCc)
                .map(WeatherCluster::getIncludeOffshore)
                .stream()
                .flatMap(Collection::stream)
                .map(IncludeOffshoreMkt::getMarketSelectCode)
                .collect(Collectors.toList());
        Optional.ofNullable(eligCcClusProgs)
                .stream()
                .flatMap(Collection::stream)
                .filter(eligCcClusProg -> eligCcClusProg.getEligCcClusProgId().getEligStyleClusProgId().getStrategyFlClusPrgmEligRankingId()
                        .getStrategyFlClusEligRankingId().getPlanClusterStrategyId()
                        .equals(planClusterStrategyId))
                .findFirst()
                .ifPresent(eligCcClusProg -> setCcIncludeOffshoreMkt(eligCcClusProg, offshoreExclude));
    }

    private void setCcIncludeOffshoreMkt(EligCcClusProg eligCcClusProg, List<Integer> offshoreExclude) {
        Optional.ofNullable(eligCcClusProg)
                .map(EligCcClusProg::getEligCcMktClusProgs)
                .ifPresent(eligCcMktClusProgs -> setCcIncludeOffshoreMktIfExists(eligCcClusProg, eligCcMktClusProgs, offshoreExclude));
    }

    private void setCcIncludeOffshoreMktIfExists(EligCcClusProg eligCcClusProg, Set<EligCcMktClusProg> eligCcMktClusProgs, List<Integer> offshoreExclude) {
        eligCcMktClusProgs.clear();
        entityManager.flush();
        if (!CollectionUtils.isEmpty(offshoreExclude)) {
            for (Integer offshoreMkt : offshoreExclude) {
                EligCcMktClusProg eligCcMktClusProg = new EligCcMktClusProg();
                EligCcMktClusProgId eligCcMktClusProgId = new EligCcMktClusProgId(eligCcClusProg.getEligCcClusProgId(), offshoreMkt);
                eligCcMktClusProg.setEligCcMktClusProgId(eligCcMktClusProgId);
                eligCcMktClusProg.setEligCcClusProg(eligCcClusProg);
                eligCcClusProg.getEligCcMktClusProgs().add(eligCcMktClusProg);
            }

        }
    }

    public void setCcRankingForClusterN(Set<EligCcClusProg> eligCcClusProgs, Integer analyticsClusterId, PlanStrategyId planStrategyId,
                                        WeatherCluster weatherClusterCc) {
        PlanClusterStrategyId planClusterStrategyId = new PlanClusterStrategyId(planStrategyId, analyticsClusterId);
        Optional.ofNullable(eligCcClusProgs)
                .stream()
                .flatMap(Collection::stream)
                .filter(eligCcClusProg -> eligCcClusProg.getEligCcClusProgId().getEligStyleClusProgId().getStrategyFlClusPrgmEligRankingId()
                        .getStrategyFlClusEligRankingId().getPlanClusterStrategyId()
                        .equals(planClusterStrategyId))
                .findFirst()
                .ifPresent(eligCcClusProg -> eligCcClusProg.setMerchantOverrideRank(weatherClusterCc.getRanking()));
    }

    public void setCcMarkDownDateForClusterN(Set<EligCcClusProg> eligCcClusProgs, Integer analyticsClusterId, PlanStrategyId planStrategyId,
                                             WeatherCluster weatherClusterCc) {
        PlanClusterStrategyId planClusterStrategyId = new PlanClusterStrategyId(planStrategyId, analyticsClusterId);
        Integer markDownWk = CommonUtil.getMarkdownYrWk(weatherClusterCc);
        String markDownDesc = CommonUtil.getMarkdownYrWkDesc(weatherClusterCc);
        Optional.ofNullable(eligCcClusProgs)
                .stream()
                .flatMap(Collection::stream)
                .filter(eligCcClusProg -> eligCcClusProg.getEligCcClusProgId().getEligStyleClusProgId().getStrategyFlClusPrgmEligRankingId()
                        .getStrategyFlClusEligRankingId().getPlanClusterStrategyId()
                        .equals(planClusterStrategyId))
                .findFirst()
                .ifPresent(eligCcClusProg -> {
                    eligCcClusProg.setMarkDownYrWk(markDownWk);
                    eligCcClusProg.setMarkDownYrWkDesc(markDownDesc);
                });
    }

    public void setCcInStoreDateForClusterN(Set<EligCcClusProg> eligCcClusProgs, Integer analyticsClusterId, PlanStrategyId planStrategyId,
                                            WeatherCluster weatherClusterCc) {
        PlanClusterStrategyId planClusterStrategyId = new PlanClusterStrategyId(planStrategyId, analyticsClusterId);
        Integer inStoreYrWk = CommonUtil.getInStoreYrWk(weatherClusterCc);
        String inStoreDesc = CommonUtil.getInStoreYrWkDesc(weatherClusterCc);

        Optional.ofNullable(eligCcClusProgs)
                .stream()
                .flatMap(Collection::stream)
                .filter(eligCcClusProg -> eligCcClusProg.getEligCcClusProgId().getEligStyleClusProgId().getStrategyFlClusPrgmEligRankingId()
                        .getStrategyFlClusEligRankingId().getPlanClusterStrategyId()
                        .equals(planClusterStrategyId))
                .findFirst()
                .ifPresent(eligCcClusProg -> {
                    eligCcClusProg.setInStoreYrWk(inStoreYrWk);
                    eligCcClusProg.setInStoreYrWkDesc(inStoreDesc);
                });
    }

    public void setCcIsEligibleForClusterN(Set<EligCcClusProg> eligCcClusProgs, PlanStrategyId planStrategyId, Integer isEligibleFlag,
                                           Integer analyticsClusterId) {
        PlanClusterStrategyId planClusterStrategyId = new PlanClusterStrategyId(planStrategyId, analyticsClusterId);
        Optional.ofNullable(eligCcClusProgs)
                .stream()
                .flatMap(Collection::stream)
                .filter(eligCcClusProg -> eligCcClusProg.getEligCcClusProgId().getEligStyleClusProgId().getStrategyFlClusPrgmEligRankingId()
                        .getStrategyFlClusEligRankingId().getPlanClusterStrategyId().equals(planClusterStrategyId))
                .findFirst()
                .ifPresent(eligCcClusProg -> eligCcClusProg.setIsEligibleFlag(isEligibleFlag));
        //Based on Cc flag status check if All level at CC and fineline need to be updated
        validateAndUpdateAllFlagAtProgCc(eligCcClusProgs, planStrategyId);
    }

    private void validateAndUpdateAllFlagAtProgCc(Set<EligCcClusProg> eligCcClusProgs, PlanStrategyId planStrategyId) {
        PlanClusterStrategyId allPlanClusterStrategyId = new PlanClusterStrategyId(planStrategyId, 0);
        Integer selectStatusId;
        Set<Integer> flagsPar = getIsEligibleFlagsPartialAtProgCcClusters(eligCcClusProgs, allPlanClusterStrategyId);
        if (flagsPar.size() == 1 && flagsPar.contains(1)) {
            selectStatusId=1;
        } else if (flagsPar.size() > 1) {
            selectStatusId=2;
        } else {
            selectStatusId=0;
        }
        Optional.ofNullable(eligCcClusProgs)
                .stream()
                .flatMap(Collection::stream)
                .filter(eligCcClusProg -> eligCcClusProg.getEligCcClusProgId().getEligStyleClusProgId().getStrategyFlClusPrgmEligRankingId()
                        .getStrategyFlClusEligRankingId().getPlanClusterStrategyId()
                        .equals(allPlanClusterStrategyId))
                .findFirst()
                .ifPresent(eligCcClusProg -> eligCcClusProg.setIsEligibleFlag(selectStatusId));

    }

    private Set<Integer> getIsEligibleFlagsPartialAtProgCcClusters(Set<EligCcClusProg> eligCcClusProgs, PlanClusterStrategyId allPlanClusterStrategyId) {
        return Optional.ofNullable(eligCcClusProgs)
                .stream()
                .flatMap(Collection::stream)
                .filter(eligCcClusProg -> !eligCcClusProg.getEligCcClusProgId().getEligStyleClusProgId().getStrategyFlClusPrgmEligRankingId()
                        .getStrategyFlClusEligRankingId().getPlanClusterStrategyId().equals(allPlanClusterStrategyId))
                .map(EligCcClusProg::getIsEligibleFlag).collect(Collectors.toSet());
    }

    public void setStyleCcMetricsForAll(Map<String, String> commonCcUpdatedFields, Set<EligCcClusProg> eligCcClusProgs,
                                        WeatherCluster weatherClusterCc) {
        //Set isEligibility for all clusterType
        if (commonCcUpdatedFields.containsKey("isEligible")) {
            Integer isEligibleFlag = Optional.ofNullable(weatherClusterCc)
                    .map(WeatherCluster::getIsEligible)
                    .map(flag -> Boolean.TRUE.equals(flag) ? 1 : 0)
                    .orElse(null);
            if (isEligibleFlag != null && isEligibleFlag == 1) {
                Optional.ofNullable(eligCcClusProgs)
                        .stream()
                        .flatMap(Collection::stream)
                        .forEach(eligCcClusProg -> eligCcClusProg.setIsEligibleFlag(1));
            } else {
                Optional.ofNullable(eligCcClusProgs)
                        .stream()
                        .flatMap(Collection::stream)
                        .forEach(eligCcClusProg -> eligCcClusProg.setIsEligibleFlag(0));
            }
        }
        //inStore Date at all clusterType
        if (commonCcUpdatedFields.containsKey("inStoreDate")) {
            setCcInStoreDateForAll(eligCcClusProgs, weatherClusterCc);
        }
        //MarkDown Date all clusterType
        if (commonCcUpdatedFields.containsKey("markDownDate")) {
            setCcMarkDownDateForAll(eligCcClusProgs, weatherClusterCc);
        }
        //Rank at all Cluster type
        if (commonCcUpdatedFields.containsKey("ranking")) {
            setCcRankingForAll(eligCcClusProgs, weatherClusterCc);
        }
    }

    public void setCcRankingForAll(Set<EligCcClusProg> eligCcClusProgs, WeatherCluster weatherClusterCc) {
        Optional.ofNullable(eligCcClusProgs)
                .stream()
                .flatMap(Collection::stream)
                .forEach(eligCcClusProg -> eligCcClusProg.setMerchantOverrideRank(weatherClusterCc.getRanking()));
    }

    public void setCcMarkDownDateForAll(Set<EligCcClusProg> eligCcClusProgs, WeatherCluster weatherClusterCc) {
        Integer markDownWk = CommonUtil.getMarkdownYrWk(weatherClusterCc);
        String markDownDesc = CommonUtil.getMarkdownYrWkDesc(weatherClusterCc);
        Optional.ofNullable(eligCcClusProgs)
                .stream()
                .flatMap(Collection::stream)
                .forEach(eligCcClusProg -> {
                    eligCcClusProg.setMarkDownYrWkDesc(markDownDesc);
                    eligCcClusProg.setMarkDownYrWk(markDownWk);
                });
    }

    public void setCcInStoreDateForAll(Set<EligCcClusProg> eligCcClusProgs, WeatherCluster weatherClusterCc) {
        Integer inStoreWk = CommonUtil.getInStoreYrWk(weatherClusterCc);
        String inStoreDesc = CommonUtil.getInStoreYrWkDesc(weatherClusterCc);
        Optional.ofNullable(eligCcClusProgs)
                .stream()
                .flatMap(Collection::stream)
                .forEach(eligCcClusProg -> {
                    eligCcClusProg.setInStoreYrWkDesc(inStoreDesc);
                    eligCcClusProg.setInStoreYrWk(inStoreWk);
                });
    }

}
