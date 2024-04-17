package com.walmart.aex.strategy.service;

import com.walmart.aex.strategy.dto.PlanStrategyRequest;
import com.walmart.aex.strategy.dto.PlanStrategyResponse;
import com.walmart.aex.strategy.dto.WeatherClusterCcStrategyDTO;
import com.walmart.aex.strategy.dto.WeatherClusterStrategyDTO;
import com.walmart.aex.strategy.dto.request.Fineline;
import com.walmart.aex.strategy.dto.request.Lvl3;
import com.walmart.aex.strategy.dto.request.Lvl4;
import com.walmart.aex.strategy.dto.request.UpdatedFields;
import com.walmart.aex.strategy.entity.PlanStrategyId;
import com.walmart.aex.strategy.entity.StrategyFlClusPrgmEligRanking;
import com.walmart.aex.strategy.properties.AppProperties;
import com.walmart.aex.strategy.repository.StrategyCcRepository;
import com.walmart.aex.strategy.repository.StrategyFinelineRepository;
import com.walmart.aex.strategy.repository.StrategyFlClusPrgmEligRankingRepository;
import com.walmart.aex.strategy.repository.StrategyGroupRepository;
import com.walmart.aex.strategy.util.CommonUtil;
import com.walmart.aex.strategy.util.SetAllClusterOffshoreUtil;
import io.strati.ccm.utils.client.annotation.ManagedConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.*;

import static com.walmart.aex.strategy.service.ClusterEligibilityFlService.*;
import static com.walmart.aex.strategy.service.ClusterEligibilityFlService.MARK_DOWN_DATE;

@Service
@Slf4j
public class ProgClusterEligibilityService {


    private final StrategyFlClusPrgmEligRankingRepository strategyFlClusPrgmEligRankingRepository;
    private final StrategyFinelineRepository strategyFinelineRepository;
    private final PlanStrategyClusterEligRankingMapper planStrategyClusterEligRankingMapper;
    private final StrategyCcRepository strategyCcRepository;
    private final StrategyGroupRepository strategyGroupRepository;
    private final SetAllClusterOffshoreUtil setAllClusterOffshoreUtil;
    @ManagedConfiguration
    private AppProperties appProperties;

    @Autowired
    private EntityManager entityManager;

    private final ProgClusterEligibilityFlService progClusterEligibilityFlService;

    public ProgClusterEligibilityService(ProgClusterEligibilityFlService progClusterEligibilityFlService,
                                         StrategyFlClusPrgmEligRankingRepository strategyFlClusPrgmEligRankingRepository,
                                         StrategyFinelineRepository strategyFinelineRepository,
                                         PlanStrategyClusterEligRankingMapper planStrategyClusterEligRankingMapper,
                                         StrategyCcRepository strategyCcRepository,
                                         StrategyGroupRepository strategyGroupRepository,
                                         SetAllClusterOffshoreUtil setAllClusterOffshoreUtil) {
        this.progClusterEligibilityFlService = progClusterEligibilityFlService;
        this.strategyFlClusPrgmEligRankingRepository = strategyFlClusPrgmEligRankingRepository;
        this.strategyFinelineRepository = strategyFinelineRepository;
        this.planStrategyClusterEligRankingMapper = planStrategyClusterEligRankingMapper;
        this.strategyCcRepository = strategyCcRepository;
        this.strategyGroupRepository = strategyGroupRepository;
        this.setAllClusterOffshoreUtil = setAllClusterOffshoreUtil;

    }

    @Transactional
    public void updatePrgClusterEligibility(PlanStrategyRequest request, PlanStrategyId planStrategyId, Long programId, Integer finelineNbr, Set<String> updatedField) {
        List<StrategyFlClusPrgmEligRanking> strategyFlClusPrgmEligRankings = new ArrayList<>();
        final Boolean[] eligibilityChanged = new Boolean[1];
        Integer analyticsClusterId=0;
        for (Lvl3 lvl3 : request.getLvl3List()) {
            for (Lvl4 lvl4 : lvl3.getLvl4List()) {
                for (Fineline fineline : lvl4.getFinelines()) {
                    analyticsClusterId = CommonUtil.getAnalyticsClusterId(CommonUtil.getWeatherClusterFineline(fineline));
                    Optional.ofNullable(fineline.getUpdatedFields())
                            .map(UpdatedFields::getWeatherCluster)
                            .map(CommonUtil::getUpdatedFieldsMap)
                            .ifPresentOrElse(weatherClusterFlUpdatedFields->{
                                eligibilityChanged[0] =weatherClusterFlUpdatedFields.containsKey(IS_ELIGIBLE);
                                if (weatherClusterFlUpdatedFields.containsKey(IN_STORE_DATE)) updatedField.add(IN_STORE_DATE);
                                if (weatherClusterFlUpdatedFields.containsKey(MARK_DOWN_DATE)) updatedField.add(MARK_DOWN_DATE);
                                    }
                            ,()-> eligibilityChanged[0] =false);
                    strategyFlClusPrgmEligRankings.addAll(progClusterEligibilityFlService.updateProgClusterEligibilityMetrics(fineline,
                            planStrategyId, lvl3.getLvl3Nbr(), lvl4.getLvl4Nbr(), programId,updatedField));
                }
            }
        }
        strategyFlClusPrgmEligRankingRepository.saveAll(strategyFlClusPrgmEligRankings);
        entityManager.flush();
        //Check and Update is_eligible flag at fineline, based on overall CCs
        if (Boolean.TRUE.equals(eligibilityChanged[0]) && analyticsClusterId>0 )
            strategyFlClusPrgmEligRankingRepository.updateIsEligForCCAllBasedOnAllCCs(planStrategyId.getPlanId(), planStrategyId.getStrategyId(), finelineNbr, programId);
        strategyFlClusPrgmEligRankingRepository.updateIsEligAndDatesForFlBasedOnAllCCsPartial(planStrategyId.getPlanId(), planStrategyId.getStrategyId(), finelineNbr, programId);
    }

    public PlanStrategyResponse fetchProgFlAndCcChanges(PlanStrategyId planStrategyId, PlanStrategyResponse response, Long programId, Integer finelineNbr) {
        Optional.ofNullable(strategyFinelineRepository.getWeatherClusterTraitStrategy(planStrategyId.getPlanId(), planStrategyId.getStrategyId(), programId))
                .stream()
                .flatMap(Collection::stream)
                .map(WeatherClusterStrategyDTO::getWeatherClusterStrategy)
                .filter(weatherClusterStrategy -> weatherClusterStrategy.getFinelineNbr().equals(finelineNbr))
                .forEach(weatherClusterStrategy -> planStrategyClusterEligRankingMapper.mapPlanStrategyLvl2(weatherClusterStrategy, response, null));
        //calling cc fetch for the fineline
        Optional.ofNullable(strategyCcRepository.getWeatherClusterTraitCcStrategy(planStrategyId.getPlanId(), planStrategyId.getStrategyId(), programId, finelineNbr))
                .stream()
                .flatMap(Collection::stream)
                .map(WeatherClusterCcStrategyDTO::getWeatherClusterCcStrategy)
                .forEach(weatherClusterStrategy -> planStrategyClusterEligRankingMapper.mapPlanStrategyLvl2(weatherClusterStrategy, response, finelineNbr));
        log.info("Setting All cluster include offshore list, based on cluster 1 & cluster 7");
        setAllClusterOffshoreUtil.setAllClusterOffshoreList(response);
        return response;
    }

}
