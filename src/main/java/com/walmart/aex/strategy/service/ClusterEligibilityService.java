package com.walmart.aex.strategy.service;

import com.walmart.aex.strategy.dto.*;
import com.walmart.aex.strategy.dto.request.Fineline;
import com.walmart.aex.strategy.dto.request.Lvl3;
import com.walmart.aex.strategy.dto.request.Lvl4;
import com.walmart.aex.strategy.dto.request.UpdatedFields;
import com.walmart.aex.strategy.entity.PlanStrategyId;
import com.walmart.aex.strategy.entity.StrategyFlClusEligRanking;
import com.walmart.aex.strategy.enums.StratGroupType;
import com.walmart.aex.strategy.exception.CustomException;
import com.walmart.aex.strategy.properties.AppProperties;
import com.walmart.aex.strategy.repository.*;
import com.walmart.aex.strategy.util.CommonUtil;
import com.walmart.aex.strategy.util.SetAllClusterOffshoreUtil;
import io.strati.ccm.utils.client.annotation.ManagedConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Tuple;
import java.util.*;
import java.util.stream.Collectors;

import static com.walmart.aex.strategy.service.ClusterEligibilityFlService.*;

@Service
@Slf4j
public class ClusterEligibilityService {


    private final ClusterEligibilityFlService clusterEligibilityFlService;
    private final StrategyFlClusEligRankingRepository strategyFlClusEligRankingRepository;
    private final StrategyCcRepository strategyCcRepository;
    private final StrategyFinelineRepository strategyFinelineRepository;
    private final PlanStrategyClusterEligRankingMapper planStrategyClusterEligRankingMapper;
    private final StrategyGroupRepository strategyGroupRepository;
    private final StrategySubCatgFixtureRepository strategySubCatgFixtureRepository;
    private final SetAllClusterOffshoreUtil setAllClusterOffshoreUtil;
    @ManagedConfiguration
    private AppProperties appProperties;


    public ClusterEligibilityService(ClusterEligibilityFlService clusterEligibilityFlService,
                                     StrategyFlClusEligRankingRepository strategyFlClusEligRankingRepository,
                                     StrategyCcRepository strategyCcRepository,
                                     StrategyFinelineRepository strategyFinelineRepository,
                                     PlanStrategyClusterEligRankingMapper planStrategyClusterEligRankingMapper,
                                     StrategyGroupRepository strategyGroupRepository,
                                     StrategySubCatgFixtureRepository strategySubCatgFixtureRepository,
                                     SetAllClusterOffshoreUtil setAllClusterOffshoreUtil) {
        this.clusterEligibilityFlService = clusterEligibilityFlService;
        this.strategyFlClusEligRankingRepository = strategyFlClusEligRankingRepository;
        this.strategyCcRepository = strategyCcRepository;
        this.strategyFinelineRepository = strategyFinelineRepository;
        this.planStrategyClusterEligRankingMapper = planStrategyClusterEligRankingMapper;
        this.strategyGroupRepository = strategyGroupRepository;
        this.strategySubCatgFixtureRepository= strategySubCatgFixtureRepository;
        this.setAllClusterOffshoreUtil = setAllClusterOffshoreUtil;

    }

    /**
     * This method get PlanStrategy for a planId & strategyId.
     *
     * @param planId
     * @return PlanStrategyDTO
     */
    public PlanStrategyResponse fetchClusterEligRankingStrategy(Long planId) {
        PlanStrategyResponse response = new PlanStrategyResponse();
        Long strategyId = strategyGroupRepository.getStrategyIdByStrategyGroupTypeAndPlanId(StratGroupType.WEATHER_CLUSTER.getStrategyGroupTypeCode(), planId);
        try {
            Optional.ofNullable(strategyFinelineRepository.getWeatherClusterStrategy(planId, strategyId, null))
                    .stream()
                    .flatMap(Collection::stream)
                    .forEach(weatherClusterStrategy -> planStrategyClusterEligRankingMapper.mapPlanStrategyLvl2(weatherClusterStrategy, response, null));
            log.info("Setting All cluster include offshore list, based on cluster 1 & cluster 7, for planId: {}", planId);
            setAllClusterOffshoreUtil.setAllClusterOffshoreList(response);
        } catch (HibernateException exp) {
            log.error("Failed to fetch the plan Strategy for Weather cluster");
            throw new CustomException("Failed to fetch the plan Strategy for Weather cluster, due to" + exp);
        }
        log.info("Fetch Fineline response: {}", response);
        return response ;
    }

    public PlanStrategyResponse fetchCcClusterEligRankingStrategy(PlanStrategyRequest planStrategyRequest) {
        log.info("Get Eligibility & Ranking- Cluster of CC for PlanStrategy Request: {}", planStrategyRequest);
        PlanStrategyResponse response = new PlanStrategyResponse();
        Long strategyId = strategyGroupRepository.getStrategyIdByStrategyGroupTypeAndPlanId(StratGroupType.WEATHER_CLUSTER.getStrategyGroupTypeCode(), planStrategyRequest.getPlanId());
        Long planId = planStrategyRequest.getPlanId();
        try {
            Integer finelineNbr = CommonUtil.getFinelineDetails(planStrategyRequest);
            Optional.ofNullable(strategyCcRepository.getCcStrategy(planId, strategyId, finelineNbr))
                    .stream()
                    .flatMap(Collection::stream)
                    .forEach(weatherClusterStrategy -> planStrategyClusterEligRankingMapper.mapPlanStrategyLvl2(weatherClusterStrategy, response, finelineNbr));
            response.setPlanId(planId);
            log.info("Setting All cluster include offshore list, based on cluster 1 & cluster 7, for planId: {}", planId);
            setAllClusterOffshoreUtil.setAllClusterOffshoreList(response);
        } catch (HibernateException exp) {
            log.error("Failed to fetch the plan Strategy for Weather cluster for a planId: {}", planId);
            throw new CustomException("Failed to fetch the plan Strategy for Weather cluster, due to" + exp);
        }
        log.info("Fetch CC response: {}", response);
        return response;
    }

    @Transactional
    public void updateClusterEligibility(PlanStrategyRequest planStrategyRequest, PlanStrategyId planStrategyId, Integer finelineNbr, Set<String> updatedField) {
        List<StrategyFlClusEligRanking> strategyFlClusEligRankings = new ArrayList<>();
        final Boolean[] eligibilityChanged = new Boolean[1];
        Integer analyticsClusterId = 0;
        for (Lvl3 lvl3 : planStrategyRequest.getLvl3List()) {
            for (Lvl4 lvl4 : lvl3.getLvl4List()) {
                for (Fineline fineline : lvl4.getFinelines()) {
                    analyticsClusterId = CommonUtil.getAnalyticsClusterId(CommonUtil.getWeatherClusterFineline(fineline));
                    Optional.ofNullable(fineline.getUpdatedFields())
                            .map(UpdatedFields::getWeatherCluster)
                            .map(CommonUtil::getUpdatedFieldsMap)
                            .ifPresentOrElse(weatherClusterFlUpdatedFields -> {
                                eligibilityChanged[0] = weatherClusterFlUpdatedFields.containsKey(IS_ELIGIBLE);
                                if (weatherClusterFlUpdatedFields.containsKey(IN_STORE_DATE)) updatedField.add(IN_STORE_DATE);
                                if (weatherClusterFlUpdatedFields.containsKey(MARK_DOWN_DATE)) updatedField.add(MARK_DOWN_DATE);
                                    }
                                    , () -> eligibilityChanged[0] = false);
                    strategyFlClusEligRankings.addAll(clusterEligibilityFlService.updateClusterEligibilityMetrics(fineline,
                            planStrategyId, lvl3.getLvl3Nbr(), lvl4.getLvl4Nbr(),updatedField));
                }
            }
        }
        strategyFlClusEligRankingRepository.saveAll(strategyFlClusEligRankings);
        //Check and Update is_eligible flag at fineline, based on overall CCs
        if (Boolean.TRUE.equals(eligibilityChanged[0]) && analyticsClusterId > 0)
            strategyFlClusEligRankingRepository.updateIsEligForCCAllBasedOnCCs(planStrategyId.getPlanId(), planStrategyId.getStrategyId(), finelineNbr);
        strategyFlClusEligRankingRepository.updateIsEligAndDatesForFlBasedOnAllCCsPartial(planStrategyId.getPlanId(), planStrategyId.getStrategyId(), finelineNbr);
    }

    public PlanStrategyResponse fetchFinelineAndCcChanges(PlanStrategyId planStrategyId, Integer finelineNbr, PlanStrategyResponse response) {
        Optional.ofNullable(strategyFinelineRepository.getWeatherClusterStrategy(planStrategyId.getPlanId(), planStrategyId.getStrategyId(), null))
                .stream()
                .flatMap(Collection::stream)
                .filter(weatherClusterStrategy -> weatherClusterStrategy.getFinelineNbr().equals(finelineNbr))
                .forEach(weatherClusterStrategy -> planStrategyClusterEligRankingMapper.mapPlanStrategyLvl2(weatherClusterStrategy, response, null));
        //calling cc fetch for the fineline
        Optional.ofNullable(strategyCcRepository.getCcStrategy(planStrategyId.getPlanId(), planStrategyId.getStrategyId(), finelineNbr))
                .stream()
                .flatMap(Collection::stream)
                .forEach(weatherClusterStrategy -> planStrategyClusterEligRankingMapper.mapPlanStrategyLvl2(weatherClusterStrategy, response, finelineNbr));
        log.debug("Setting All cluster include offshore list, based on cluster 1 & cluster 7, for planId: {}", planStrategyId.getPlanId());
        setAllClusterOffshoreUtil.setAllClusterOffshoreList(response);
        return response;
    }

    /**
     * This method fetched PlanStrategy (Traited Finelines - TRAITED or BOTH) for a planId & strategyId.
     *
     * @param planId
     * @return PlanStrategyDTO
     */
    public PlanStrategyResponse fetchTraitEligRankingStrategy(Long planId, Long programId) {
        PlanStrategyResponse response = new PlanStrategyResponse();
        Long strategyId = strategyGroupRepository.getStrategyIdByStrategyGroupTypeAndPlanId(StratGroupType.WEATHER_CLUSTER.getStrategyGroupTypeCode(), planId);
        try {
            List<WeatherClusterStrategyDTO> weatherClusterStrategyDTOList = strategyFinelineRepository
                    .getWeatherClusterTraitStrategy(planId, strategyId, programId);
            List<WeatherClusterStrategy> weatherClusterStrategyList = Optional.ofNullable(weatherClusterStrategyDTOList)
                    .orElseGet(Collections::emptyList).stream()
                    .map(WeatherClusterStrategyDTO::getWeatherClusterStrategy)
                    .collect(Collectors.toList());
            Optional.of(weatherClusterStrategyList)
                    .stream()
                    .flatMap(Collection::stream)
                    .forEach(weatherClusterStrategy -> planStrategyClusterEligRankingMapper
                            .mapPlanStrategyLvl2(weatherClusterStrategy, response, null));
            log.info("Setting All cluster include offshore list, based on cluster 1 & cluster 7, for planId: {}", planId);
            setAllClusterOffshoreUtil.setAllClusterOffshoreList(response);
        } catch (HibernateException exp) {
            log.error("Failed to fetch the plan Strategy for Traited finelines in Weather cluster");
            throw new CustomException("Failed to fetch Trait finelines plan Strategy for Weather cluster, due to" + exp);
        } catch (Exception e) {
            log.error("Error occurred :", e);
        }
        log.info("Trait Fetch Fineline response: {}", response);
        return response;
    }

    /**
     * This method fetched PlanStrategy CC for a programId & planId & strategyId & finelineNbr.
     *
     * @param programId, planStrategyRequest
     * @return PlanStrategyResponse
     */
    public PlanStrategyResponse fetchCcTraitEligRankingStrategy(Long programId, PlanStrategyRequest planStrategyRequest) {
        PlanStrategyResponse response = new PlanStrategyResponse();
        Long strategyId = strategyGroupRepository.getStrategyIdByStrategyGroupTypeAndPlanId(StratGroupType.WEATHER_CLUSTER.getStrategyGroupTypeCode(), planStrategyRequest.getPlanId());
        Long planId = planStrategyRequest.getPlanId();
        try {
            Integer finelineNbr = getFinelineDetails(planStrategyRequest);
            List<WeatherClusterCcStrategyDTO> weatherClusterCcStrategyDTOList = strategyCcRepository
                    .getWeatherClusterTraitCcStrategy(planId, strategyId, programId, finelineNbr);
            List<WeatherClusterStrategy> weatherClusterStrategyList = Optional.ofNullable(weatherClusterCcStrategyDTOList)
                    .orElseGet(Collections::emptyList).stream()
                    .map(WeatherClusterCcStrategyDTO::getWeatherClusterCcStrategy)
                    .collect(Collectors.toList());
            log.info(String.valueOf(weatherClusterStrategyList));

            Optional.of(weatherClusterStrategyList)
                    .stream()
                    .flatMap(Collection::stream)
                    .forEach(weatherClusterStrategy -> planStrategyClusterEligRankingMapper
                            .mapPlanStrategyLvl2(weatherClusterStrategy, response, finelineNbr));
            response.setPlanId(planId);
            log.info("Setting All cluster include offshore list, based on cluster 1 & cluster 7, for planId: {}", planId);
            setAllClusterOffshoreUtil.setAllClusterOffshoreList(response);
        } catch (HibernateException exp) {
            log.error("Failed to fetch the plan Strategy for Traited finelines CC in Weather cluster");
            throw new CustomException("Failed to fetch Trait finelines CC plan Strategy for Weather cluster, due to" + exp);
        } catch (Exception e) {
            log.error("Error occurred :", e);
        }
        log.info("Trait Fetch CC response: {}", response);
        return response;
    }

    private Integer getFinelineDetails(PlanStrategyRequest planStrategyRequest) {
        log.info("getFineline: {}", planStrategyRequest);
        return Optional.ofNullable(planStrategyRequest)
                .map(PlanStrategyRequest::getLvl3List)
                .stream()
                .flatMap(Collection::stream)
                .findFirst()
                .map(Lvl3::getLvl4List)
                .stream()
                .flatMap(Collection::stream)
                .findFirst()
                .map(Lvl4::getFinelines)
                .stream()
                .flatMap(Collection::stream)
                .findFirst()
                .map(Fineline::getFinelineNbr)
                .orElse(null);
    }
    public Map<String, Boolean> isRfaDefaultMinMaxCapValid(PlanStrategyRequest planStrategyRequest, Long strategyId) {
        Map<String,Boolean> response = new HashMap<>();
        Integer[] lvl3List = null;
        Integer[] lvl4List = null;

        if(planStrategyRequest.getLvl3List() != null) {
            lvl3List = planStrategyRequest.getLvl3List().stream().map(lvl3 -> lvl3.getLvl3Nbr()).toArray(Integer[]::new);
            lvl4List = planStrategyRequest.getLvl3List().stream().flatMap(lvl4 -> lvl4.getLvl4List().stream()
                    .map(l4 -> l4.getLvl4Nbr())).toArray(Integer[]::new);
        }
        else{
            lvl3List = new Integer[]{0};
            lvl4List = new Integer[]{0};
        }
        Tuple queryResult = strategySubCatgFixtureRepository.isSubCategoryFixtureMinMaxInvalid(planStrategyRequest.getPlanId(),lvl3List, lvl4List, strategyId);

        response.put("isCCRulesValid", queryResult.get(0).equals(1));
        response.put("isFinelineRulesValid", queryResult.get(1).equals(1));
        return response;
    }
}
