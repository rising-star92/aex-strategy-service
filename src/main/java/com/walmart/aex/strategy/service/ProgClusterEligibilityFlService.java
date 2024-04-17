package com.walmart.aex.strategy.service;

import com.walmart.aex.strategy.dto.AnalyticsClusterStoreDTO;
import com.walmart.aex.strategy.dto.request.Fineline;
import com.walmart.aex.strategy.dto.request.UpdatedFields;
import com.walmart.aex.strategy.dto.request.WeatherCluster;
import com.walmart.aex.strategy.entity.*;
import com.walmart.aex.strategy.enums.IncludeOffshoreMkt;
import com.walmart.aex.strategy.exception.CustomException;
import com.walmart.aex.strategy.properties.AppProperties;
import com.walmart.aex.strategy.repository.StrategyFlClusEligRankingRepository;
import com.walmart.aex.strategy.repository.StrategyFlClusPrgmEligRankingRepository;
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

@Service
@Slf4j
public class ProgClusterEligibilityFlService {

    private final StrategyFlClusPrgmEligRankingRepository strategyFlClusPrgmEligRankingRepository;
    private final ProgClusterEligibilityCcService progClusterEligibilityCcService;
    private final StrategyFlClusEligRankingRepository strategyFlClusEligRankingRepository;
    private final TraitClusterStoreCountService traitClusterStoreCountService;
    private final PlanStrategyClusterEligMapper planStrategyClusterEligMapper;

    @ManagedConfiguration
    private AppProperties appProperties;

    @Autowired
    private EntityManager entityManager;

    public ProgClusterEligibilityFlService(StrategyFlClusPrgmEligRankingRepository strategyFlClusPrgmEligRankingRepository,
                                           ProgClusterEligibilityCcService progClusterEligibilityCcService,
                                           StrategyFlClusEligRankingRepository strategyFlClusEligRankingRepository,
                                           TraitClusterStoreCountService traitClusterStoreCountService,
                                           PlanStrategyClusterEligMapper planStrategyClusterEligMapper) {
        this.strategyFlClusPrgmEligRankingRepository = strategyFlClusPrgmEligRankingRepository;
        this.progClusterEligibilityCcService = progClusterEligibilityCcService;
        this.strategyFlClusEligRankingRepository = strategyFlClusEligRankingRepository;
        this.traitClusterStoreCountService = traitClusterStoreCountService;
        this.planStrategyClusterEligMapper = planStrategyClusterEligMapper;
    }

    public Set<StrategyFlClusPrgmEligRanking> updateProgClusterEligibilityMetrics(Fineline fineline, PlanStrategyId planStrategyId,
                                                                                  Integer lvl3Nbr, Integer lvl4Nbr, Long programId, Set<String> updatedField) {
        //For new Program, fineline association it will be a new arrayList, else we update the existing one
        Set<StrategyFlClusPrgmEligRanking> strategyFlClusPrgmEligRankings =
                strategyFlClusPrgmEligRankingRepository.findStrategyFlClusPrgmEligRankingByStrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_PlanClusterStrategyId_PlanStrategyIdAndStrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_lvl3NbrAndStrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_lvl4NbrAndStrategyFlClusPrgmEligRankingId_StrategyFlClusEligRankingId_finelineNbrAndStrategyFlClusPrgmEligRankingId_programId(
                                planStrategyId, lvl3Nbr, lvl4Nbr, fineline.getFinelineNbr(), programId)
                        .orElse(new HashSet<>());
        //Get the fineline cluster ranking data to populate program fineline
        Set<StrategyFlClusEligRanking> strategyFlClusEligRankings =
                strategyFlClusEligRankingRepository.findStrategyFlClusEligRankingByStrategyFlClusEligRankingId_PlanClusterStrategyId_PlanStrategyIdAndStrategyFlClusEligRankingId_lvl3NbrAndStrategyFlClusEligRankingId_lvl4NbrAndStrategyFlClusEligRankingId_finelineNbr(
                                planStrategyId, lvl3Nbr, lvl4Nbr, fineline.getFinelineNbr())
                        .orElseThrow(() -> new CustomException(String.format("Unable to find a fineline :%s in planId :%s & StategyId:%s"
                                , fineline.getFinelineNbr(), planStrategyId.getPlanId(), planStrategyId.getStrategyId())));
        //Update triggered at fineline level
        Optional.ofNullable(fineline.getUpdatedFields())
                .map(UpdatedFields::getWeatherCluster)
                .map(CommonUtil::getUpdatedFieldsMap)
                .ifPresent(weatherClusterFlUpdatedFields -> updateProgFlMetrics(fineline, planStrategyId,
                        weatherClusterFlUpdatedFields, strategyFlClusPrgmEligRankings, strategyFlClusEligRankings, programId));
        //Check if we have CC level UpdateFields
        if (fineline.getStyles() != null) {
            progClusterEligibilityCcService.updateProgClusterEligibilityMetricsAtStyleCc(strategyFlClusPrgmEligRankings, strategyFlClusEligRankings,
                    fineline.getStyles(), planStrategyId, fineline.getFinelineNbr(), programId, updatedField);
        }
        return strategyFlClusPrgmEligRankings;
    }

    private void updateProgFlMetrics(Fineline fineline, PlanStrategyId planStrategyId, Map<String, String> programFlUpdatedFields,
                                     Set<StrategyFlClusPrgmEligRanking> strategyFlClusPrgmEligRankings,
                                     Set<StrategyFlClusEligRanking> strategyFlClusEligRankings, Long programId) {
        log.info("Updating the fineline :{} for field & value: {}", fineline.getFinelineNbr(), StringUtils.join(programFlUpdatedFields));
        WeatherCluster weatherClusterFineline = CommonUtil.getWeatherClusterFineline(fineline);
        Integer analyticsClusterId = CommonUtil.getAnalyticsClusterId(weatherClusterFineline);
        //Set Attributes at cluster All level
        if (analyticsClusterId.equals(0)) {
            setFlMetricsForClusterAll(programFlUpdatedFields, strategyFlClusPrgmEligRankings, weatherClusterFineline,
                    strategyFlClusEligRankings, programId);
        } else {
            setProgMetricsForClusterN(programFlUpdatedFields, strategyFlClusEligRankings, strategyFlClusPrgmEligRankings,
                    weatherClusterFineline, analyticsClusterId, planStrategyId, programId);
        }
    }

    private void setFlMetricsForClusterAll(Map<String, String> programFlUpdatedFields, Set<StrategyFlClusPrgmEligRanking> strategyFlClusPrgmEligRankings,
                                           WeatherCluster weatherClusterFineline, Set<StrategyFlClusEligRanking> strategyFlClusEligRankings,
                                           Long programId) {
        //Set isEligibility for all clusterType
        if (programFlUpdatedFields.containsKey("isEligible")) {
            Integer isEligibleFlag = Optional.ofNullable(weatherClusterFineline)
                    .map(WeatherCluster::getIsEligible)
                    .map(flag -> Boolean.TRUE.equals(flag) ? 1 : 0)
                    .orElse(null);
            if (isEligibleFlag != null && isEligibleFlag == 1) {
                Optional.ofNullable(strategyFlClusEligRankings)
                        .stream()
                        .flatMap(Collection::stream)
                        .forEach(strategyFlClusEligRanking -> checkAndSetFlIsEligToTrue(strategyFlClusEligRanking, strategyFlClusPrgmEligRankings, programId, weatherClusterFineline));

            } else {
                Optional.ofNullable(strategyFlClusPrgmEligRankings)
                        .stream()
                        .flatMap(Collection::stream)
                        .forEach(strategyFlClusPrgmEligRanking -> {
                            strategyFlClusPrgmEligRanking.setIsEligibleFlag(0);
                            if(!CollectionUtils.isEmpty(strategyFlClusPrgmEligRanking.getFinelineMarketClusterPrgElgs())){
                                strategyFlClusPrgmEligRanking.getFinelineMarketClusterPrgElgs().clear();
                                entityManager.flush();
                            }
                            //Rolldown to style & Cc
                            setStyleIsEligibilityToFalse(strategyFlClusPrgmEligRanking.getEligStyleClusProgs());
                        });
            }
        }
        //Set inStore for all clusterType
        if (programFlUpdatedFields.containsKey("inStoreDate") && !CollectionUtils.isEmpty(strategyFlClusPrgmEligRankings)) {
            setFlInStoreDateForAll(strategyFlClusPrgmEligRankings, weatherClusterFineline);
        }
        //MarkDown Date for all clusterType
        if (programFlUpdatedFields.containsKey("markDownDate")) {
            setFlMarkDownDateForAll(strategyFlClusPrgmEligRankings, weatherClusterFineline);
        }
        //Rank for all clusterType
        if (programFlUpdatedFields.containsKey("ranking")) {
            setFlRankingForAll(strategyFlClusPrgmEligRankings, weatherClusterFineline);
        }
    }

    private void setFlRankingForAll(Set<StrategyFlClusPrgmEligRanking> strategyFlClusPrgmEligRankings, WeatherCluster weatherClusterFineline) {
        Optional.ofNullable(strategyFlClusPrgmEligRankings)
                .stream()
                .flatMap(Collection::stream)
                .forEach(strategyFlClusPrgmEligRanking -> strategyFlClusPrgmEligRanking.setMerchantOverrideRank(weatherClusterFineline.getRanking()));
    }

    private void setFlMarkDownDateForAll(Set<StrategyFlClusPrgmEligRanking> strategyFlClusPrgmEligRankings, WeatherCluster weatherClusterFineline) {
        Integer markDownYrWk = CommonUtil.getMarkdownYrWk(weatherClusterFineline);
        String markDownDesc = CommonUtil.getMarkdownYrWkDesc(weatherClusterFineline);
        Optional.ofNullable(strategyFlClusPrgmEligRankings)
                .stream()
                .flatMap(Collection::stream)
                .forEach(strategyFlClusPrgmEligRanking -> {
                    strategyFlClusPrgmEligRanking.setMarkDownYrWkDesc(markDownDesc);
                    strategyFlClusPrgmEligRanking.setMarkDownYrWk(markDownYrWk);
                    //Rolldown to style & cc level
                    if (!CollectionUtils.isEmpty(strategyFlClusPrgmEligRanking.getEligStyleClusProgs())) {
                        Set<EligStyleClusProg> eligStyleClusProgs = strategyFlClusPrgmEligRanking.getEligStyleClusProgs();
                        setStyleCcMarkDownDateForAll(eligStyleClusProgs, markDownDesc, markDownYrWk);
                    }
                });
    }

    private void setStyleCcMarkDownDateForAll(Set<EligStyleClusProg> eligStyleClusProgs, String markDownDesc, Integer markDownYrWk) {
        for (EligStyleClusProg eligStyleClusProg : eligStyleClusProgs) {
            if (!CollectionUtils.isEmpty(eligStyleClusProg.getEligCcClusProgs())) {
                Set<EligCcClusProg> eligCcClusProgs = eligStyleClusProg.getEligCcClusProgs();
                for (EligCcClusProg eligCcClusProg : eligCcClusProgs) {
                    eligCcClusProg.setMarkDownYrWkDesc(markDownDesc);
                    eligCcClusProg.setMarkDownYrWk(markDownYrWk);
                }
            }
        }
    }

    private void setFlInStoreDateForAll(Set<StrategyFlClusPrgmEligRanking> strategyFlClusPrgmEligRankings, WeatherCluster weatherClusterFineline) {
        Integer inStoreYrWk = CommonUtil.getInStoreYrWk(weatherClusterFineline);
        String inStoreDesc = CommonUtil.getInStoreYrWkDesc(weatherClusterFineline);
        Optional.ofNullable(strategyFlClusPrgmEligRankings)
                .stream()
                .flatMap(Collection::stream)
                .forEach(strategyFlClusPrgmEligRanking -> {
                    strategyFlClusPrgmEligRanking.setInStoreYrWkDesc(inStoreDesc);
                    strategyFlClusPrgmEligRanking.setInStoreYrWk(inStoreYrWk);
                    //Rolldown to style & cc level
                    if (!CollectionUtils.isEmpty(strategyFlClusPrgmEligRanking.getEligStyleClusProgs())) {
                        Set<EligStyleClusProg> eligStyleClusProgs = strategyFlClusPrgmEligRanking.getEligStyleClusProgs();
                        setStyleCcInStoreDateForAll(eligStyleClusProgs, inStoreDesc, inStoreYrWk);
                    }
                });
    }

    private void setStyleCcInStoreDateForAll(Set<EligStyleClusProg> eligStyleClusProgs, String inStoreDesc, Integer inStoreYrWk) {
        for (EligStyleClusProg eligStyleClusProg : eligStyleClusProgs) {
            if (!CollectionUtils.isEmpty(eligStyleClusProg.getEligCcClusProgs())) {
                Set<EligCcClusProg> eligCcClusProgs = eligStyleClusProg.getEligCcClusProgs();
                for (EligCcClusProg eligCcClusProg : eligCcClusProgs) {
                    eligCcClusProg.setInStoreYrWkDesc(inStoreDesc);
                    eligCcClusProg.setInStoreYrWk(inStoreYrWk);
                }
            }
        }
    }

    private void setStyleIsEligibilityToFalse(Set<EligStyleClusProg> eligStyleClusProgs) {
        Optional.ofNullable(eligStyleClusProgs)
                .stream()
                .flatMap(Collection::stream)
                .forEach(this::setCcIsEligibilityToFalse);
    }

    private void setCcIsEligibilityToFalse(EligStyleClusProg eligStyleClusProg) {
        Optional.ofNullable(eligStyleClusProg.getEligCcClusProgs())
                .stream()
                .flatMap(Collection::stream)
                .forEach(eligCcClusProg -> {
                    eligCcClusProg.setIsEligibleFlag(0);
                    if (!Objects.isNull(eligCcClusProg.getEligCcMktClusProgs())){
                        eligCcClusProg.getEligCcMktClusProgs().clear();
                        entityManager.flush();
                    }

                });
    }

    private void checkAndSetFlIsEligToTrue(StrategyFlClusEligRanking strategyFlClusEligRanking,
                                           Set<StrategyFlClusPrgmEligRanking> strategyFlClusPrgmEligRankings, Long programId,
                                           WeatherCluster weatherClusterFineline) {
        //If this is First time for this fineline to program
        StrategyFlClusPrgmEligRankingId strategyFlClusPrgmEligRankingId = new StrategyFlClusPrgmEligRankingId(
                strategyFlClusEligRanking.getStrategyFlClusEligRankingId(), programId);

        Optional.ofNullable(strategyFlClusPrgmEligRankings)
                .stream()
                .flatMap(Collection::stream)
                .filter(strategyFlClusPrgmEligRanking -> strategyFlClusPrgmEligRanking.getStrategyFlClusPrgmEligRankingId()
                        .equals(strategyFlClusPrgmEligRankingId))
                .findFirst()
                .ifPresentOrElse(strategyFlClusPrgmEligRanking -> {
                    addEligibleAndOffshoreAndStoreCount(strategyFlClusPrgmEligRanking, strategyFlClusPrgmEligRankingId, weatherClusterFineline, programId);
                    //RollDown to Cc
                    setCCIsEligAfterInitialSet(strategyFlClusPrgmEligRanking, strategyFlClusEligRanking, weatherClusterFineline, programId);
                }, () -> createAndSetFlIsElig(strategyFlClusEligRanking, strategyFlClusPrgmEligRankings, strategyFlClusPrgmEligRankingId, weatherClusterFineline, programId));
    }

    private void createAndSetFlIsElig(StrategyFlClusEligRanking strategyFlClusEligRanking,
                                      Set<StrategyFlClusPrgmEligRanking> strategyFlClusPrgmEligRankings,
                                      StrategyFlClusPrgmEligRankingId strategyFlClusPrgmEligRankingId, WeatherCluster weatherClusterFineline, Long programId) {

        StrategyFlClusPrgmEligRanking strategyFlClusPrgmEligRanking = new StrategyFlClusPrgmEligRanking();
        addEligibleAndOffshoreAndStoreCount(strategyFlClusPrgmEligRanking, strategyFlClusPrgmEligRankingId, weatherClusterFineline, programId);
        strategyFlClusPrgmEligRanking.setMarkDownYrWk(strategyFlClusEligRanking.getMarkDownYrWk());
        strategyFlClusPrgmEligRanking.setMarkDownYrWkDesc(strategyFlClusEligRanking.getMarkDownYrWkDesc());
        strategyFlClusPrgmEligRanking.setInStoreYrWk(strategyFlClusEligRanking.getInStoreYrWk());
        strategyFlClusPrgmEligRanking.setInStoreYrWkDesc(strategyFlClusEligRanking.getInStoreYrWkDesc());
        strategyFlClusPrgmEligRanking.setStrategyFlClusPrgmEligRankingId(strategyFlClusPrgmEligRankingId);
        //Rolldown to style & cc level
        if (!CollectionUtils.isEmpty(strategyFlClusEligRanking.getStrategyStyleCluses())) {
            strategyFlClusPrgmEligRanking.setEligStyleClusProgs(setStyleCcIsElig(strategyFlClusEligRanking.getStrategyStyleCluses(), strategyFlClusPrgmEligRankingId,weatherClusterFineline));
        }
        strategyFlClusPrgmEligRankings.add(strategyFlClusPrgmEligRanking);
    }

    private void addEligibleAndOffshoreAndStoreCount(StrategyFlClusPrgmEligRanking strategyFlClusPrgmEligRanking, StrategyFlClusPrgmEligRankingId strategyFlClusPrgmEligRankingId, WeatherCluster weatherClusterFineline, Long programId){
        strategyFlClusPrgmEligRanking.setIsEligibleFlag(1);
        if(CollectionUtils.isEmpty(strategyFlClusPrgmEligRanking.getFinelineMarketClusterPrgElgs())){
            strategyFlClusPrgmEligRanking.setFinelineMarketClusterPrgElgs(planStrategyClusterEligMapper.fetchStrategyFlMktCustEligProgram(weatherClusterFineline,
                    strategyFlClusPrgmEligRanking, strategyFlClusPrgmEligRankingId));
        }
        strategyFlClusPrgmEligRanking.setStoreCount(Math.toIntExact(
                Optional.of(getTraitClusterStoreCount(strategyFlClusPrgmEligRankingId, programId))
                        .orElse(null)));
    }

    private Long getTraitClusterStoreCount(StrategyFlClusPrgmEligRankingId strategyFlClusPrgmEligRankingId, Long programId) {
        Integer analyticsClusterId = Optional.ofNullable(strategyFlClusPrgmEligRankingId.getStrategyFlClusEligRankingId())
                .map(StrategyFlClusEligRankingId::getPlanClusterStrategyId)
                .map(PlanClusterStrategyId::getAnalyticsClusterId)
                .orElse(null);
        Long strategyId = Optional.ofNullable(strategyFlClusPrgmEligRankingId.getStrategyFlClusEligRankingId())
                .map(StrategyFlClusEligRankingId::getPlanClusterStrategyId)
                .map(PlanClusterStrategyId::getPlanStrategyId)
                .map(PlanStrategyId::getStrategyId)
                .orElse(null);
        log.info("Get the Trait StoreCount for analyticsClusterId:{}, strategyId:{}", analyticsClusterId, strategyId);
        List<AnalyticsClusterStoreDTO> analyticsClusterStoreCount = traitClusterStoreCountService.getAnalyticsClusterStoreCount(strategyId);
        Long storeCount = null;
        if (analyticsClusterId != null) {
            if (analyticsClusterId == 0) {
                log.info("Getting the store count for all & programId:{}, and excluding the offshore state by default", programId);
                storeCount = Optional.ofNullable(analyticsClusterStoreCount)
                        .stream()
                        .flatMap(Collection::stream)
                        .filter(analyticsClusterStoreDTO -> analyticsClusterStoreDTO.getProgramId().equals(programId))
                        .map(AnalyticsClusterStoreDTO::getStoreCount)
                        .mapToLong(Long::intValue)
                        .sum();
            } else if (analyticsClusterId == 1) {
                log.info("Getting the store count for cluster 1 & programId:{}, and excluding the offshore state for cluster 1 by default", programId);
                storeCount = Optional.ofNullable(analyticsClusterStoreCount)
                        .stream()
                        .flatMap(Collection::stream)
                        .filter(analyticsClusterStoreDTO -> analyticsClusterStoreDTO.getProgramId().equals(programId))
                        .filter(analyticsClusterStoreDTO -> analyticsClusterStoreDTO.getAnalyticsClusterId().equals(analyticsClusterId))
                        .map(AnalyticsClusterStoreDTO::getStoreCount)
                        .mapToLong(Long::intValue)
                        .sum();
            } else if (analyticsClusterId == 2) {
                log.info("Getting the store count for cluster 2 & programId:{}, and excluding the offshore state for cluster 2 by default", programId);
                storeCount = Optional.ofNullable(analyticsClusterStoreCount)
                        .stream()
                        .flatMap(Collection::stream)
                        .filter(analyticsClusterStoreDTO -> analyticsClusterStoreDTO.getProgramId().equals(programId))
                        .filter(analyticsClusterStoreDTO -> analyticsClusterStoreDTO.getAnalyticsClusterId().equals(analyticsClusterId))
                        .map(AnalyticsClusterStoreDTO::getStoreCount)
                        .mapToLong(Long::intValue)
                        .sum();
            } else if (analyticsClusterId == 7) {
                log.info("Getting the store count for cluster 7 & programId:{}, and excluding the offshore state for cluster 7 by default", programId);
                storeCount = Optional.ofNullable(analyticsClusterStoreCount)
                        .stream()
                        .flatMap(Collection::stream)
                        .filter(analyticsClusterStoreDTO -> analyticsClusterStoreDTO.getProgramId().equals(programId))
                        .filter(analyticsClusterStoreDTO -> analyticsClusterStoreDTO.getAnalyticsClusterId().equals(analyticsClusterId))
                        .map(AnalyticsClusterStoreDTO::getStoreCount)
                        .mapToLong(Long::intValue)
                        .sum();
            } else {
                storeCount = Optional.ofNullable(analyticsClusterStoreCount)
                        .stream()
                        .flatMap(Collection::stream)
                        .filter(analyticsClusterStoreDTO -> analyticsClusterStoreDTO.getProgramId().equals(programId))
                        .filter(analyticsClusterStoreDTO -> analyticsClusterStoreDTO.getAnalyticsClusterId().equals(analyticsClusterId))
                        .map(AnalyticsClusterStoreDTO::getStoreCount)
                        .mapToLong(Long::intValue)
                        .sum();
            }
        }
        return storeCount;
    }

    private void setCCIsEligAfterInitialSet(StrategyFlClusPrgmEligRanking strategyFlClusPrgmEligRanking,
                                            StrategyFlClusEligRanking strategyFlClusEligRanking, WeatherCluster weatherClusterFineline, Long programId) {
        if (!CollectionUtils.isEmpty(strategyFlClusPrgmEligRanking.getEligStyleClusProgs())) {
            Optional.ofNullable(strategyFlClusPrgmEligRanking.getEligStyleClusProgs())
                    .stream()
                    .flatMap(Collection::stream)
                    .forEach(eligStyleClusProg -> callCcLevel(eligStyleClusProg, strategyFlClusEligRanking, weatherClusterFineline, programId));
        } else {
            strategyFlClusPrgmEligRanking.getEligStyleClusProgs().addAll(setStyleCcIsElig(strategyFlClusEligRanking.getStrategyStyleCluses(), strategyFlClusPrgmEligRanking.getStrategyFlClusPrgmEligRankingId(),weatherClusterFineline));
        }
    }

    private void callCcLevel(EligStyleClusProg eligStyleClusProg, StrategyFlClusEligRanking strategyFlClusEligRanking,
                             WeatherCluster weatherClusterCc, Long programId) {
        StrategyStyleClusId strategyStyleClusId = new StrategyStyleClusId(strategyFlClusEligRanking.getStrategyFlClusEligRankingId(),
                eligStyleClusProg.getEligStyleClusProgId().getStyleNbr());
        StrategyStyleClus strategyStyleClus = strategyFlClusEligRanking.getStrategyStyleCluses()
                .stream()
                .filter(strategyStyleClus1 -> strategyStyleClus1.getStrategyStyleClusId().equals(strategyStyleClusId))
                .findFirst()
                .orElse(new StrategyStyleClus());
        StrategyFlClusPrgmEligRankingId strategyFlClusPrgmEligRankingId = new StrategyFlClusPrgmEligRankingId(strategyFlClusEligRanking.getStrategyFlClusEligRankingId(), programId);
        EligStyleClusProgId eligStyleClusProgId = new EligStyleClusProgId(strategyFlClusPrgmEligRankingId, eligStyleClusProg.getEligStyleClusProgId().getStyleNbr());
        if (!CollectionUtils.isEmpty(eligStyleClusProg.getEligCcClusProgs())) {
            eligStyleClusProg.getEligCcClusProgs().forEach(eligCcClusProg -> {
                eligCcClusProg.setIsEligibleFlag(1);
                EligCcClusProgId eligCcClusProgId = new EligCcClusProgId(
                        eligStyleClusProgId, eligCcClusProg.getEligCcClusProgId().getCcId());
                if(CollectionUtils.isEmpty(eligCcClusProg.getEligCcMktClusProgs())){
                    eligCcClusProg.setEligCcMktClusProgs(planStrategyClusterEligMapper.fetchStrategyCcMktCustEligProgram(
                            weatherClusterCc, eligCcClusProg, eligCcClusProgId));
                }
            });
        } else {
            eligStyleClusProg.getEligCcClusProgs().addAll(setCcIsElig(strategyStyleClus.getStrategyCcClusEligRankings(), eligStyleClusProg.getEligStyleClusProgId(),weatherClusterCc));
        }

    }

    private Set<EligStyleClusProg> setStyleCcIsElig(Set<StrategyStyleClus> strategyStyleCluses,
                                                    StrategyFlClusPrgmEligRankingId strategyFlClusPrgmEligRankingId, WeatherCluster weatherClusterCc) {
        return Optional.ofNullable(strategyStyleCluses)
                .stream()
                .flatMap(Collection::stream)
                .map(strategyStyleClus -> setStyleIsElig(strategyStyleClus, strategyFlClusPrgmEligRankingId, weatherClusterCc))
                .collect(Collectors.toSet());
    }

    private EligStyleClusProg setStyleIsElig(StrategyStyleClus strategyStyleClus, StrategyFlClusPrgmEligRankingId strategyFlClusPrgmEligRankingId, WeatherCluster weatherClusterCc) {
        EligStyleClusProg eligStyleClusProg = new EligStyleClusProg();
        EligStyleClusProgId eligStyleClusProgId = new EligStyleClusProgId(strategyFlClusPrgmEligRankingId, strategyStyleClus.getStrategyStyleClusId().getStyleNbr());
        eligStyleClusProg.setEligStyleClusProgId(eligStyleClusProgId);
        //RollDown to CC
        if (!CollectionUtils.isEmpty(strategyStyleClus.getStrategyCcClusEligRankings())) {
            eligStyleClusProg.setEligCcClusProgs(setCcIsElig(strategyStyleClus.getStrategyCcClusEligRankings(), eligStyleClusProgId, weatherClusterCc));
        }
        return eligStyleClusProg;
    }

    private Set<EligCcClusProg> setCcIsElig(Set<StrategyCcClusEligRanking> strategyCcClusEligRankings,
                                            EligStyleClusProgId eligStyleClusProgId, WeatherCluster weatherClusterCc) {
        return Optional.ofNullable(strategyCcClusEligRankings)
                .stream()
                .flatMap(Collection::stream)
                .map(strategyCcClusEligRanking -> setCcIsEligTrue(strategyCcClusEligRanking, eligStyleClusProgId, weatherClusterCc))
                .collect(Collectors.toSet());
    }

    private EligCcClusProg setCcIsEligTrue(StrategyCcClusEligRanking strategyCcClusEligRanking,
                                           EligStyleClusProgId eligStyleClusProgId, WeatherCluster weatherClusterCc) {
        EligCcClusProg eligCcClusProg = new EligCcClusProg();
        EligCcClusProgId eligCcClusProgId = new EligCcClusProgId(eligStyleClusProgId, strategyCcClusEligRanking.getStrategyCcClusEligRankingId().getCcId());
        eligCcClusProg.setEligCcClusProgId(eligCcClusProgId);
        eligCcClusProg.setIsEligibleFlag(1);

        eligCcClusProg.setEligCcMktClusProgs(planStrategyClusterEligMapper.fetchStrategyCcMktCustEligProgram(
                                    weatherClusterCc, eligCcClusProg, eligCcClusProgId));

        eligCcClusProg.setInStoreYrWk(strategyCcClusEligRanking.getInStoreYrWk());
        eligCcClusProg.setInStoreYrWkDesc(strategyCcClusEligRanking.getInStoreYrWkDesc());
        eligCcClusProg.setMarkDownYrWk(strategyCcClusEligRanking.getMarkDownYrWk());
        eligCcClusProg.setMarkDownYrWkDesc(strategyCcClusEligRanking.getMarkDownYrWkDesc());
        return eligCcClusProg;
    }

    private void setProgMetricsForClusterN(Map<String, String> programFlUpdatedFields, Set<StrategyFlClusEligRanking> strategyFlClusEligRankings,
                                           Set<StrategyFlClusPrgmEligRanking> strategyFlClusPrgmEligRankings,
                                           WeatherCluster weatherClusterFineline, Integer analyticsClusterId, PlanStrategyId planStrategyId,
                                           Long programId) {
        PlanClusterStrategyId planClusterStrategyId = new PlanClusterStrategyId(planStrategyId, analyticsClusterId);
        //Set isEligibility for N clusterType
        if (programFlUpdatedFields.containsKey("isEligible")) {
            Integer isEligibleFlag = Optional.ofNullable(weatherClusterFineline)
                    .map(WeatherCluster::getIsEligible)
                    .map(flag -> Boolean.TRUE.equals(flag) ? 1 : 0)
                    .orElse(null);
            if (isEligibleFlag != null && isEligibleFlag == 1) {
                Optional.ofNullable(strategyFlClusEligRankings)
                        .stream()
                        .flatMap(Collection::stream)
                        .filter(strategyFlClusEligRanking -> strategyFlClusEligRanking.getStrategyFlClusEligRankingId().getPlanClusterStrategyId()
                                .equals(planClusterStrategyId))
                        .findFirst()
                        .ifPresent(strategyFlClusEligRanking -> checkAndSetFlIsEligToTrue(strategyFlClusEligRanking, strategyFlClusPrgmEligRankings, programId, weatherClusterFineline));
                Integer overAllCluster0Count = getOverAllStoreCount(strategyFlClusPrgmEligRankings);
                //Need this planClusterStrategyId for calculating update storecount for cluster 0
                PlanClusterStrategyId planClusterStrategyIdFor0 = new PlanClusterStrategyId(planStrategyId, 0);
                //Update the cluster 0 count
                Optional.ofNullable(strategyFlClusPrgmEligRankings)
                        .stream()
                        .flatMap(Collection::stream)
                        .filter(strategyFlClusPrgmEligRanking -> strategyFlClusPrgmEligRanking.getStrategyFlClusPrgmEligRankingId()
                                .getStrategyFlClusEligRankingId().getPlanClusterStrategyId().equals(planClusterStrategyIdFor0))
                        .findFirst()
                        .ifPresent(strategyFlClusPrgmEligRanking -> strategyFlClusPrgmEligRanking.setStoreCount(overAllCluster0Count));
            } else {
                Optional.ofNullable(strategyFlClusPrgmEligRankings)
                        .stream()
                        .flatMap(Collection::stream)
                        .filter(strategyFlClusPrgmEligRanking -> strategyFlClusPrgmEligRanking.getStrategyFlClusPrgmEligRankingId()
                                .getStrategyFlClusEligRankingId().getPlanClusterStrategyId().equals(planClusterStrategyId))
                        .findFirst()
                        .ifPresent(strategyFlClusPrgmEligRanking -> {
                            strategyFlClusPrgmEligRanking.setIsEligibleFlag(0);
                            if(!CollectionUtils.isEmpty(strategyFlClusPrgmEligRanking.getFinelineMarketClusterPrgElgs())) {
                                strategyFlClusPrgmEligRanking.getFinelineMarketClusterPrgElgs().clear();
                                entityManager.flush();
                            }
                            //Rolldown to style & Cc
                            setStyleIsEligibilityToFalse(strategyFlClusPrgmEligRanking.getEligStyleClusProgs());
                        });
            }
            //will check the status of all clusters eligible flag and based on all is updated
            validateFlIsEligFlag(strategyFlClusPrgmEligRankings, planStrategyId);
        }
        //inStore Date at Cluster N
        if (programFlUpdatedFields.containsKey("inStoreDate")) {
            setFlInStoreDateForClusterN(strategyFlClusPrgmEligRankings, analyticsClusterId, planStrategyId, weatherClusterFineline);
        }
        //MarkDown Date at Cluster N
        if (programFlUpdatedFields.containsKey("markDownDate")) {
            setFlMarkDownDateForClusterN(strategyFlClusPrgmEligRankings, analyticsClusterId, planStrategyId, weatherClusterFineline);
        }
        //exclude offshore Selection at Cluster N
        if (programFlUpdatedFields.containsKey("includeOffshore")) {
            setFlIncludeOffshoreForClusterN(strategyFlClusPrgmEligRankings, analyticsClusterId, planStrategyId, weatherClusterFineline, programId);
        }
        //Rank at Cluster N
        if (programFlUpdatedFields.containsKey("ranking")) {
            setFlRankingForClusterN(strategyFlClusPrgmEligRankings, analyticsClusterId, planStrategyId, weatherClusterFineline);
        }
    }

    private void validateFlIsEligFlag(Set<StrategyFlClusPrgmEligRanking> strategyFlClusPrgmEligRankings,
                                      PlanStrategyId planStrategyId) {
        PlanClusterStrategyId allPlanClusterStrategyId = new PlanClusterStrategyId(planStrategyId, 0);
        Integer selectStatusIds;
        Set<Integer> flagsPar = getFlIsEligibleFlagsPartialStatusExcludeAll(strategyFlClusPrgmEligRankings, allPlanClusterStrategyId);
        if (flagsPar.size() == 1 && flagsPar.contains(1)) {
            selectStatusIds = 1;
        } else if ((flagsPar.size() > 1)) {
            selectStatusIds = 2;
        } else {
            selectStatusIds = 0;
        }
        Optional.ofNullable(strategyFlClusPrgmEligRankings)
                .stream()
                .flatMap(Collection::stream)
                .filter(strategyFlClusPrgmEligRanking -> strategyFlClusPrgmEligRanking.getStrategyFlClusPrgmEligRankingId()
                        .getStrategyFlClusEligRankingId().getPlanClusterStrategyId().equals(allPlanClusterStrategyId))
                .findFirst()
                .ifPresent(strategyFlClusPrgmEligRanking -> {
                    strategyFlClusPrgmEligRanking.setIsEligibleFlag(selectStatusIds);
                    //Rolldown to style & cc level
                    if (!CollectionUtils.isEmpty(strategyFlClusPrgmEligRanking.getEligStyleClusProgs())) {
                        Set<EligStyleClusProg> eligStyleClusProgs = strategyFlClusPrgmEligRanking.getEligStyleClusProgs();
                        setStyleCcIsEligForAll(eligStyleClusProgs, selectStatusIds);
                    }
                });
    }

    private void setStyleCcIsEligForAll(Set<EligStyleClusProg> eligStyleClusProgs, Integer isEligibleFlag) {
        for (EligStyleClusProg eligStyleClusProg : eligStyleClusProgs) {
            if (!CollectionUtils.isEmpty(eligStyleClusProg.getEligCcClusProgs())) {
                Set<EligCcClusProg> eligCcClusProgs = eligStyleClusProg.getEligCcClusProgs();
                for (EligCcClusProg eligCcClusProg : eligCcClusProgs) {
                    eligCcClusProg.setIsEligibleFlag(isEligibleFlag);
                }
            }
        }
    }

    private Set<Integer> getFlIsEligibleFlagsPartialStatusExcludeAll(Set<StrategyFlClusPrgmEligRanking> strategyFlClusPrgmEligRankings,
                                                                     PlanClusterStrategyId allPlanClusterStrategyId) {
        return Optional.ofNullable(strategyFlClusPrgmEligRankings)
                .stream()
                .flatMap(Collection::stream)
                .filter(strategyFlClusPrgmEligRanking -> !strategyFlClusPrgmEligRanking.getStrategyFlClusPrgmEligRankingId()
                        .getStrategyFlClusEligRankingId().getPlanClusterStrategyId().equals(allPlanClusterStrategyId))
                .map(StrategyFlClusPrgmEligRanking::getIsEligibleFlag).collect(Collectors.toSet());
    }

    private void setFlRankingForClusterN(Set<StrategyFlClusPrgmEligRanking> strategyFlClusPrgmEligRankings,
                                         Integer analyticsClusterId, PlanStrategyId planStrategyId, WeatherCluster weatherClusterFineline) {
        PlanClusterStrategyId planClusterStrategyId = new PlanClusterStrategyId(planStrategyId, analyticsClusterId);
        Optional.ofNullable(strategyFlClusPrgmEligRankings)
                .stream()
                .flatMap(Collection::stream)
                .filter(strategyFlClusPrgmEligRanking -> strategyFlClusPrgmEligRanking.getStrategyFlClusPrgmEligRankingId()
                        .getStrategyFlClusEligRankingId().getPlanClusterStrategyId().equals(planClusterStrategyId))
                .findFirst()
                .ifPresent(strategyFlClusPrgmEligRanking -> strategyFlClusPrgmEligRanking.setMerchantOverrideRank(weatherClusterFineline.getRanking()));
    }

    private void setFlIncludeOffshoreForClusterN(Set<StrategyFlClusPrgmEligRanking> strategyFlClusPrgmEligRankings,
                                                 Integer analyticsClusterId, PlanStrategyId planStrategyId,
                                                 WeatherCluster weatherClusterFineline, Long programId) {
        PlanClusterStrategyId planClusterStrategyId = new PlanClusterStrategyId(planStrategyId, analyticsClusterId);
        //Need this planClusterStrategyId for calculating update storeCount for cluster 0
        PlanClusterStrategyId planClusterStrategyIdFor0 = new PlanClusterStrategyId(planStrategyId, 0);
        //Get analytics StoreCount to set the count based on offshore changes
        List<AnalyticsClusterStoreDTO> analyticsClusterStoreDTOS = Optional.ofNullable(traitClusterStoreCountService.getAnalyticsClusterStoreCount(planStrategyId.getStrategyId()))
                .stream()
                .flatMap(Collection::stream)
                .filter(analyticsClusterStoreDTO -> analyticsClusterStoreDTO.getAnalyticsClusterId().equals(analyticsClusterId))
                .filter(analyticsClusterStoreDTO -> analyticsClusterStoreDTO.getProgramId().equals(programId))
                .collect(Collectors.toList());

        //Requested offshore list
        List<String> offshoreExcludeStateCode = Optional.ofNullable(weatherClusterFineline)
                .map(WeatherCluster::getIncludeOffshore)
                .stream()
                .flatMap(Collection::stream)
                .map(IncludeOffshoreMkt::getMarketValue)
                .collect(Collectors.toList());
        log.info("updating includeOffshore for analyticsClusterId:{}, with list of :{}", analyticsClusterId,
                String.join(",", offshoreExcludeStateCode));
        List<String> difference = new ArrayList<>();
        if (analyticsClusterId == 1) {
            log.info("Exclude the difference from the request list with the actual cluster offshore list for cluster 1");
            difference = appProperties.getCluster1OffshoreList()
                    .stream()
                    .filter(element -> !offshoreExcludeStateCode.contains(element))
                    .collect(Collectors.toList());

        } else if (analyticsClusterId == 2) {
            log.info("Exclude the difference from the request list with the actual cluster offshore list for cluster 2");
            difference = appProperties.getCluster2OffshoreList()
                    .stream()
                    .filter(element -> !offshoreExcludeStateCode.contains(element))
                    .collect(Collectors.toList());

        } else if (analyticsClusterId == 7) {
            log.info("Exclude the difference from the request list with the actual cluster offshore list for cluster 7");
            difference = appProperties.getCluster7OffshoreList()
                    .stream()
                    .filter(element -> !offshoreExcludeStateCode.contains(element))
                    .collect(Collectors.toList());
        }
        //get the count based on the list of selection
        List<String> finalDifference = difference;
        Integer storeCount = Optional.of(analyticsClusterStoreDTOS)
                .stream()
                .flatMap(Collection::stream)
                .filter(analyticsClusterStoreDTO -> !finalDifference.contains(analyticsClusterStoreDTO.getStateProvinceCode()))
                .map(AnalyticsClusterStoreDTO::getStoreCount)
                .mapToInt(Long::intValue)
                .sum();
        //Set the store count for
        Optional.ofNullable(strategyFlClusPrgmEligRankings)
                .stream()
                .flatMap(Collection::stream)
                .filter(strategyFlClusPrgmEligRanking -> strategyFlClusPrgmEligRanking.getStrategyFlClusPrgmEligRankingId()
                        .getStrategyFlClusEligRankingId().getPlanClusterStrategyId().equals(planClusterStrategyId))
                .findFirst()
                .ifPresent(strategyFlClusPrgmEligRanking -> strategyFlClusPrgmEligRanking.setStoreCount(storeCount));
        Integer overAllCluster0Count = getOverAllStoreCount(strategyFlClusPrgmEligRankings);
        //Update the cluster 0 count
        Optional.ofNullable(strategyFlClusPrgmEligRankings)
                .stream()
                .flatMap(Collection::stream)
                .filter(strategyFlClusPrgmEligRanking -> strategyFlClusPrgmEligRanking.getStrategyFlClusPrgmEligRankingId()
                        .getStrategyFlClusEligRankingId().getPlanClusterStrategyId().equals(planClusterStrategyIdFor0))
                .findFirst()
                .ifPresent(strategyFlClusPrgmEligRanking -> strategyFlClusPrgmEligRanking.setStoreCount(overAllCluster0Count));
        //Set the finelineMarketClusterPrgElgs
        Optional.ofNullable(strategyFlClusPrgmEligRankings)
                .stream()
                .flatMap(Collection::stream)
                .filter(strategyFlClusPrgmEligRanking -> strategyFlClusPrgmEligRanking.getStrategyFlClusPrgmEligRankingId()
                        .getStrategyFlClusEligRankingId().getPlanClusterStrategyId().equals(planClusterStrategyId))
                .findFirst()
                .ifPresent(strategyFlClusPrgmEligRanking -> setIncludeOffshoreMkt(strategyFlClusPrgmEligRanking, offshoreExcludeStateCode));
    }

    private Integer getOverAllStoreCount(Set<StrategyFlClusPrgmEligRanking> strategyFlClusPrgmEligRankings) {
        log.info("Calculate the update storecount for cluster 0 at Trait level");
        return Optional.ofNullable(strategyFlClusPrgmEligRankings)
                .stream()
                .flatMap(Collection::stream)
                .filter(strategyFlClusPrgmEligRanking -> !strategyFlClusPrgmEligRanking.getStrategyFlClusPrgmEligRankingId().getStrategyFlClusEligRankingId()
                        .getPlanClusterStrategyId().getAnalyticsClusterId().equals(0))
                .mapToInt(s -> s.getStoreCount() != null ? s.getStoreCount() : 0)
                .sum();
    }

    private void setIncludeOffshoreMkt(StrategyFlClusPrgmEligRanking strategyFlClusPrgmEligRanking, List<String> offshoreInclude) {
        if (!Objects.isNull(strategyFlClusPrgmEligRanking)) {
            Optional.ofNullable(strategyFlClusPrgmEligRanking)
                    .map(StrategyFlClusPrgmEligRanking::getFinelineMarketClusterPrgElgs)
                    .ifPresentOrElse(finelineMarketClusterPrgElgs -> setIncludeOffshoreMktIfExists(strategyFlClusPrgmEligRanking, finelineMarketClusterPrgElgs, offshoreInclude),
                            () -> setIncludeOffshoreMktIfNotExists(strategyFlClusPrgmEligRanking, offshoreInclude));


            if (!CollectionUtils.isEmpty(strategyFlClusPrgmEligRanking.getEligStyleClusProgs())) {
                for (EligStyleClusProg styleClus : strategyFlClusPrgmEligRanking.getEligStyleClusProgs()) {
                    if (!CollectionUtils.isEmpty(styleClus.getEligCcClusProgs())) {
                        for (EligCcClusProg ccClus : styleClus.getEligCcClusProgs()) {
                            Optional.ofNullable(ccClus)
                                    .map(EligCcClusProg::getEligCcMktClusProgs)
                                    .ifPresentOrElse(eligCcMktClusProgs -> setIncludeOffshoreMktIfExistsCc(ccClus, eligCcMktClusProgs, offshoreInclude),
                                            () -> setIncludeOffshoreMktIfNotExistsCc(ccClus, offshoreInclude));
                        }
                    }
                }
            }
        }
    }

    private void setIncludeOffshoreMktIfNotExists(StrategyFlClusPrgmEligRanking strategyFlClusPrgmEligRanking, List<String> offshoreInclude) {
        Set<FinelineMarketClusterPrgElg> finelineMarketClusterPrgElgs = new HashSet<>();
        if (!CollectionUtils.isEmpty(offshoreInclude)) {
            for (String offshoreMkt : offshoreInclude) {
                FinelineMarketClusterPrgElg finelineMarketClusterPrgElg = new FinelineMarketClusterPrgElg();
                FinelineMarketClusterPrgElgId finelineMarketClusterPrgElgId = new FinelineMarketClusterPrgElgId(
                        strategyFlClusPrgmEligRanking.getStrategyFlClusPrgmEligRankingId(), IncludeOffshoreMkt.getChannelIdFromName(offshoreMkt));
                finelineMarketClusterPrgElg.setFinelineMarketClusterPrgElgId(finelineMarketClusterPrgElgId);
                finelineMarketClusterPrgElg.setStrategyFlClusPrgmEligRanking(strategyFlClusPrgmEligRanking);
                finelineMarketClusterPrgElgs.add(finelineMarketClusterPrgElg);
            }
            strategyFlClusPrgmEligRanking.setFinelineMarketClusterPrgElgs(finelineMarketClusterPrgElgs);

        }
    }

    private void setIncludeOffshoreMktIfExists(StrategyFlClusPrgmEligRanking strategyFlClusPrgmEligRanking,
                                               Set<FinelineMarketClusterPrgElg> finelineMarketClusterPrgElgs, List<String> offshoreInclude) {
        finelineMarketClusterPrgElgs.clear();
        entityManager.flush();
        if (!CollectionUtils.isEmpty(offshoreInclude)) {
            for (String offshoreMkt : offshoreInclude) {
                FinelineMarketClusterPrgElg finelineMarketClusterPrgElg = new FinelineMarketClusterPrgElg();
                FinelineMarketClusterPrgElgId finelineMarketClusterPrgElgId = new FinelineMarketClusterPrgElgId(
                        strategyFlClusPrgmEligRanking.getStrategyFlClusPrgmEligRankingId(), IncludeOffshoreMkt.getChannelIdFromName(offshoreMkt));
                finelineMarketClusterPrgElg.setFinelineMarketClusterPrgElgId(finelineMarketClusterPrgElgId);
                finelineMarketClusterPrgElg.setStrategyFlClusPrgmEligRanking(strategyFlClusPrgmEligRanking);
                strategyFlClusPrgmEligRanking.getFinelineMarketClusterPrgElgs().add(finelineMarketClusterPrgElg);
            }

        }
    }

    private void setIncludeOffshoreMktIfNotExistsCc(EligCcClusProg eligCcClusProg, List<String> offshoreInclude) {
        Set<EligCcMktClusProg> eligCcMktClusProgs = new HashSet<>();
        if (!CollectionUtils.isEmpty(offshoreInclude)) {
            for (String offshoreMkt : offshoreInclude) {
                EligCcMktClusProg eligCcMktClusProg = new EligCcMktClusProg();
                EligCcMktClusProgId eligCcMktClusProgId = new EligCcMktClusProgId(
                        eligCcClusProg.getEligCcClusProgId(), IncludeOffshoreMkt.getChannelIdFromName(offshoreMkt));
                eligCcMktClusProg.setEligCcMktClusProgId(eligCcMktClusProgId);
                eligCcMktClusProg.setEligCcClusProg(eligCcClusProg);
                eligCcMktClusProgs.add(eligCcMktClusProg);
            }
            eligCcClusProg.setEligCcMktClusProgs(eligCcMktClusProgs);

        }
    }

    private void setIncludeOffshoreMktIfExistsCc(EligCcClusProg eligCcClusProg,
                                                 Set<EligCcMktClusProg> eligCcMktClusProgs, List<String> offshoreInclude) {
        eligCcMktClusProgs.clear();
        entityManager.flush();
        if (!CollectionUtils.isEmpty(offshoreInclude)) {
            for (String offshoreMkt : offshoreInclude) {
                EligCcMktClusProg eligCcMktClusProg = new EligCcMktClusProg();
                EligCcMktClusProgId eligCcMktClusProgId = new EligCcMktClusProgId(
                        eligCcClusProg.getEligCcClusProgId(), IncludeOffshoreMkt.getChannelIdFromName(offshoreMkt));
                eligCcMktClusProg.setEligCcMktClusProgId(eligCcMktClusProgId);
                eligCcMktClusProg.setEligCcClusProg(eligCcClusProg);
                eligCcMktClusProgs.add(eligCcMktClusProg);
            }

        }
    }

    private void setFlMarkDownDateForClusterN(Set<StrategyFlClusPrgmEligRanking> strategyFlClusPrgmEligRankings,
                                              Integer analyticsClusterId, PlanStrategyId planStrategyId,
                                              WeatherCluster weatherClusterFineline) {
        PlanClusterStrategyId planClusterStrategyId = new PlanClusterStrategyId(planStrategyId, analyticsClusterId);
        Integer markDownWk = CommonUtil.getMarkdownYrWk(weatherClusterFineline);
        String markDownDesc = CommonUtil.getMarkdownYrWkDesc(weatherClusterFineline);

        Optional.ofNullable(strategyFlClusPrgmEligRankings)
                .stream()
                .flatMap(Collection::stream)
                .filter(strategyFlClusPrgmEligRanking -> strategyFlClusPrgmEligRanking.getStrategyFlClusPrgmEligRankingId()
                        .getStrategyFlClusEligRankingId().getPlanClusterStrategyId().equals(planClusterStrategyId))
                .findFirst()
                .ifPresent(strategyFlClusPrgmEligRanking -> {
                    strategyFlClusPrgmEligRanking.setMarkDownYrWk(markDownWk);
                    strategyFlClusPrgmEligRanking.setMarkDownYrWkDesc(markDownDesc);
                    //Rolldown to style & cc level
                    if (!CollectionUtils.isEmpty(strategyFlClusPrgmEligRanking.getEligStyleClusProgs())) {
                        Set<EligStyleClusProg> eligStyleClusProgs = strategyFlClusPrgmEligRanking.getEligStyleClusProgs();
                        setStyleCcMarkDownForClusterN(eligStyleClusProgs, markDownWk, markDownDesc);
                    }
                });
    }

    private void setStyleCcMarkDownForClusterN(Set<EligStyleClusProg> eligStyleClusProgs, Integer markDownWk, String markDownDesc) {
        for (EligStyleClusProg eligStyleClusProg : eligStyleClusProgs) {
            if (!CollectionUtils.isEmpty(eligStyleClusProg.getEligCcClusProgs())) {
                Set<EligCcClusProg> eligCcClusProgs = eligStyleClusProg.getEligCcClusProgs();
                for (EligCcClusProg eligCcClusProg : eligCcClusProgs) {
                    eligCcClusProg.setMarkDownYrWk(markDownWk);
                    eligCcClusProg.setMarkDownYrWkDesc(markDownDesc);
                }
            }
        }
    }

    private void setFlInStoreDateForClusterN(Set<StrategyFlClusPrgmEligRanking> strategyFlClusPrgmEligRankings,
                                             Integer analyticsClusterId, PlanStrategyId planStrategyId,
                                             WeatherCluster weatherClusterFineline) {
        PlanClusterStrategyId planClusterStrategyId = new PlanClusterStrategyId(planStrategyId, analyticsClusterId);
        Integer inStoreYrWk = CommonUtil.getInStoreYrWk(weatherClusterFineline);
        String inStoreDesc = CommonUtil.getInStoreYrWkDesc(weatherClusterFineline);
        Optional.ofNullable(strategyFlClusPrgmEligRankings)
                .stream()
                .flatMap(Collection::stream)
                .filter(strategyFlClusPrgmEligRanking -> strategyFlClusPrgmEligRanking.getStrategyFlClusPrgmEligRankingId()
                        .getStrategyFlClusEligRankingId().getPlanClusterStrategyId().equals(planClusterStrategyId))
                .findFirst()
                .ifPresent(strategyFlClusPrgmEligRanking -> {
                    strategyFlClusPrgmEligRanking.setInStoreYrWk(inStoreYrWk);
                    strategyFlClusPrgmEligRanking.setInStoreYrWkDesc(inStoreDesc);
                    //Rolldown to style & cc level
                    if (!CollectionUtils.isEmpty(strategyFlClusPrgmEligRanking.getEligStyleClusProgs())) {
                        Set<EligStyleClusProg> eligStyleClusProgs = strategyFlClusPrgmEligRanking.getEligStyleClusProgs();
                        setStyleCcInStoreForClusterN(eligStyleClusProgs, inStoreYrWk, inStoreDesc);
                    }
                });
    }

    private void setStyleCcInStoreForClusterN(Set<EligStyleClusProg> eligStyleClusProgs, Integer inStoreYrWk, String inStoreDesc) {
        for (EligStyleClusProg eligStyleClusProg : eligStyleClusProgs) {
            if (!CollectionUtils.isEmpty(eligStyleClusProg.getEligCcClusProgs())) {
                Set<EligCcClusProg> eligCcClusProgs = eligStyleClusProg.getEligCcClusProgs();
                for (EligCcClusProg eligCcClusProg : eligCcClusProgs) {
                    eligCcClusProg.setInStoreYrWk(inStoreYrWk);
                    eligCcClusProg.setInStoreYrWkDesc(inStoreDesc);
                }
            }
        }
    }


}