package com.walmart.aex.strategy.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.strategy.dto.*;
import com.walmart.aex.strategy.dto.mapper.PlanStrategyMapperDTO;
import com.walmart.aex.strategy.dto.request.*;
import com.walmart.aex.strategy.dto.request.PlanStrategyDTO;
import com.walmart.aex.strategy.entity.*;
import com.walmart.aex.strategy.enums.EventType;
import com.walmart.aex.strategy.enums.StratGroupType;
import com.walmart.aex.strategy.exception.CustomException;
import com.walmart.aex.strategy.properties.AppProperties;
import com.walmart.aex.strategy.repository.*;
import com.walmart.aex.strategy.util.CommonUtil;
import io.strati.ccm.utils.client.annotation.ManagedConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Service
@Slf4j
public class PlanStrategyService {

    public static final String FAILED_STATUS = "Failed";
    public static final String SUCCESS_STATUS = "Success";

    private final PlanStrategyRepository planStrategyRepository;

    private final PlanStrategyClusterRepository planStrategyClusterRepository;
    private final PlanStrategyMapper planStrategyMapper;
    private final PlanStrategyFixtureMapper planStrategyFixtureMapper;
    private final PlanStrategyClusterEligMapper planStrategyClusterEligMapper;
    private final ObjectMapper objectMapper;
    private final PlanStrategyUpdateMapper planStrategyUpdateMapper;
    private final StrategySPClusterMapper strategySPClusterMapper;
    private final StrategyFlClustMetricRepository strategyFlClustMetricRepository;
    private final StrategyGroupRepository strategyGroupRepository;
    private final StrategySubCatgFixtureRepository strategySubCatgFixtureRepository;
    private final StrategyMerchCatgFixtureRepository strategyMerchCatgFixtureRepository;

    private final PresentationUnitsService presentationUnitsService;

    @ManagedConfiguration
    private AppProperties appProperties;

    public PlanStrategyService(PlanStrategyRepository planStrategyRepository,
                               PlanStrategyMapper planStrategyMapper, PlanStrategyClusterEligMapper planStrategyClusterEligMapper,
                               ObjectMapper objectMapper, PlanStrategyUpdateMapper planStrategyUpdateMapper,
                               StrategySPClusterMapper strategySPClusterMapper,
                               StrategyFlClustMetricRepository strategyFlClustMetricRepository, StrategyGroupRepository strategyGroupRepository, PlanStrategyClusterRepository planStrategyClusterRepository,
                               PlanStrategyFixtureMapper planStrategyFixtureMapper,
                               StrategySubCatgFixtureRepository strategySubCatgFixtureRepository,
                               StrategyMerchCatgFixtureRepository strategyMerchCatgFixtureRepository,
                               PresentationUnitsService presentationUnitsService) {

        this.planStrategyRepository = planStrategyRepository;
        this.planStrategyClusterRepository = planStrategyClusterRepository;
        this.planStrategyMapper = planStrategyMapper;
        this.planStrategyClusterEligMapper = planStrategyClusterEligMapper;
        this.objectMapper = objectMapper;
        this.planStrategyUpdateMapper = planStrategyUpdateMapper;
        this.strategySPClusterMapper = strategySPClusterMapper;
        this.strategyFlClustMetricRepository = strategyFlClustMetricRepository;
        this.strategyGroupRepository = strategyGroupRepository;
        this.planStrategyFixtureMapper = planStrategyFixtureMapper;
        this.strategySubCatgFixtureRepository = strategySubCatgFixtureRepository;
        this.strategyMerchCatgFixtureRepository = strategyMerchCatgFixtureRepository;
        this.presentationUnitsService = presentationUnitsService;
    }

    /**
     * This method creates Entries into Strategy Tables based on CLP data events.
     *
     * @param request
     * @return PlanStrategyResponse
     */
    @Transactional
    public PlanStrategyListenerResponse addPlanStrategy(PlanStrategyDTO request) {
        PlanStrategyListenerResponse responseDTO = new PlanStrategyListenerResponse();
        try {
            log.info("Received the payload from strategy listener for CLP & Analytics: {}", objectMapper.writeValueAsString(request));
        } catch (JsonProcessingException exp) {
            responseDTO.setStatus(FAILED_STATUS);
            log.error("Couldn't parse the payload sent to Strategy Listener. Error: {}", exp.toString());
        }

        if (Boolean.parseBoolean(appProperties.getAPReleaseFlag())) {
            Long strategyId = strategyGroupRepository.getStrategyIdBySeasonCd(StratGroupType.WEATHER_CLUSTER.getStrategyGroupTypeCode(),
                    CommonUtil.getSeasonCode(request.getPlanDesc()), CommonUtil.getFiscalYearFromPlanDesc(request.getPlanDesc()));
            PlanStrategyId planStrategyId = new PlanStrategyId();
            planStrategyId.setPlanId(request.getPlanId());
            planStrategyId.setStrategyId(strategyId);
            PlanStrategy planStrategy = planStrategyRepository.findById(planStrategyId).orElse(new PlanStrategy());
            if (planStrategy.getPlanStrategyId() == null) {
                planStrategy.setPlanStrategyId(planStrategyId);
            }
            for (Lvl1 lvl1 : request.getLvl1List()) {
                for (Lvl2 lvl2 : lvl1.getLvl2List()) {
                    //add or update planStrategy hierarchy & strong keys
                    Optional.ofNullable(planStrategyMapper.setStrategyMerchCatg(planStrategy, lvl2.getLvl3List(),
                            planStrategyId, request, lvl1, lvl2)).ifPresent(planStrategy::setStrategyMerchCatgs);
                    //add or update planStrategyCluster weather cluster at a fineline/style/cc and metrics
                    Optional.ofNullable(planStrategyClusterEligMapper.setPlanClusterStrategy(planStrategy, lvl2.getLvl3List(),
                            planStrategyId, request, lvl1, lvl2)).ifPresent(planStrategy::setPlanClusterStrategies);
                }
            }
            planStrategyRepository.save(planStrategy);
            planStrategyRepository.flush();
            //Add Fixture Strategy from LP request
            addFixtureStrategy(request);
            strategyFlClustMetricRepository.updateAlgoClusterRanking(request.getPlanId(), strategyId);
        }
        //Check if SP is ready for release
        if (Boolean.parseBoolean(appProperties.getSPReleaseFlag())) {
            addSizeStrategy(request);
          
        }
        responseDTO.setStatus(SUCCESS_STATUS);
        log.info("Response for creating Plan Strategy {}", responseDTO);
        return responseDTO;
    }

   

    private void setFixtureStrategy(PlanStrategyDTO request, Long strategyId) {
        PlanStrategyId planStrategyId = new PlanStrategyId();
        planStrategyId.setPlanId(request.getPlanId());
        planStrategyId.setStrategyId(strategyId);
        log.info("Check if a planStrategy Id : {} already exists or not", planStrategyId.toString());
        PlanStrategy planStrategy = planStrategyRepository.findById(planStrategyId).orElse(new PlanStrategy());
        if (planStrategy.getPlanStrategyId() == null) {
            planStrategy.setPlanStrategyId(planStrategyId);
        }
        for (Lvl1 lvl1 : request.getLvl1List()) {
            for (Lvl2 lvl2 : lvl1.getLvl2List()) {
                //add or update planStrategy hierarchy & strong keys
                Optional.ofNullable(planStrategyFixtureMapper.setStrategyMerchCatg(planStrategy, lvl2.getLvl3List(),
                        planStrategyId, request, lvl1, lvl2)).ifPresent(planStrategy::setStrategyMerchCatgs);
            }
        }
        planStrategyRepository.save(planStrategy);
        planStrategyRepository.flush();
        log.info("Fixture Strategy is update and saved");
        strategySubCatgFixtureRepository.deleteOrphanSubCatgFixtures(planStrategyId.getPlanId(),
                planStrategyId.getStrategyId(), CommonUtil.getLvl3NbrFromListenerDTO(request),
                CommonUtil.getLvl4NbrFromListenerDTO(request));
        log.info("Deleted Orphan records if exists at SubCatg Fixture type");
        strategySubCatgFixtureRepository.flush();
        strategyMerchCatgFixtureRepository.deleteOrphanMerchCatgFixtures(planStrategyId.getPlanId(),
                planStrategyId.getStrategyId(), CommonUtil.getLvl3NbrFromListenerDTO(request));
        log.info("Deleted Orphan records if exists at MerchCatg Fixture type");
    }

    @Transactional
    public PlanStrategyListenerResponse updatePlanStrategy(PlanStrategyDTO request, EventType eventType) {
        PlanStrategyListenerResponse response = new PlanStrategyListenerResponse();
        try {
            log.info("Update Request received from strategy listener for CLP & Analytics: {}", objectMapper.writeValueAsString(request));
        } catch (JsonProcessingException exception) {
            response.setStatus(FAILED_STATUS);
            log.error("Couldn't log the payload sent to Strategy Listener. Error: {}", exception.getMessage());
        }
        //Do not update strategy for delete flow
        if (Boolean.parseBoolean(appProperties.getSPReleaseFlag()) && eventType.name().equalsIgnoreCase(EventType.UPDATE.name())) {
            updateSizeStrategy(request);
        }
        Long strategyId = strategyGroupRepository.getStrategyIdBySeasonCd(StratGroupType.WEATHER_CLUSTER.getStrategyGroupTypeCode(),
                CommonUtil.getSeasonCode(request.getPlanDesc()), CommonUtil.getFiscalYearFromPlanDesc(request.getPlanDesc()));
        PlanStrategyId planStrategyId = PlanStrategyId.builder()
                .planId(request.getPlanId())
                .strategyId(strategyId)
                .build();
        log.info("Check if a planStrategy Id : {} exists or not", planStrategyId.toString());
        PlanStrategy planStrategy = planStrategyRepository.findById(planStrategyId)
                .orElseThrow(() -> new CustomException("Invalid Plan Strategy Id"));
        FixtureAllocationStrategy allocationStrategy = new FixtureAllocationStrategy();
        for (Lvl1 lvl1 : request.getLvl1List()) {
            for (Lvl2 lvl2 : lvl1.getLvl2List()) {
                for (Lvl3 lvl3 : lvl2.getLvl3List()) {
                    Set<StrategyMerchCatg> strategyMerchCatgs = planStrategy.getStrategyMerchCatgs();
                    Set<PlanClusterStrategy> planClusterStrategies = planStrategy.getPlanClusterStrategies();
                    planStrategyUpdateMapper.updateMerchCatgMetrics(strategyMerchCatgs, lvl3, planStrategyId,
                            lvl2.getLvl2Nbr(), lvl1.getLvl1Nbr(), request.getLvl0Nbr(), planClusterStrategies, allocationStrategy);
                }
            }
        }
        planStrategyRepository.save(planStrategy);
        planStrategyRepository.flush();
        //Update Fixture Strategy
        addFixtureStrategy(request);

        //updating PU min max if needed
        if(allocationStrategy.isHasProdDimChanged()){
            allocationStrategy.setPlanId(request.getPlanId());
            List<StrategyFinelineFixture> fineLineFixtures = presentationUnitsService.updatePresentationMinMax(allocationStrategy);
            if(!CollectionUtils.isEmpty(fineLineFixtures)){
                PlanStrategyId puPlanStrategyId = PlanStrategyId.builder()
                        .planId(allocationStrategy.getPlanId())
                        .strategyId(allocationStrategy.getStrategyId())
                        .build();
                presentationUnitsService.sendKafkaMessageForEachFineline(allocationStrategy.getFinelineNbr(), fineLineFixtures, puPlanStrategyId);
            }
        }
        response.setStatus(SUCCESS_STATUS);
        log.info("Response for updating Plan Strategy {}", response);
        return response;
    }

    public void addSizeStrategy(PlanStrategyDTO request) {
        log.info("Adding Size Strategy request: {}", request);
        Long strategyId = strategyGroupRepository.getStrategyIdBySeasonCd(StratGroupType.SIZE_PROFILE.getStrategyGroupTypeCode(), null, null);
        PlanStrategyId planStrategyId = new PlanStrategyId();
        planStrategyId.setPlanId(request.getPlanId());
        planStrategyId.setStrategyId(strategyId);
        log.info("Check if a planStrategy Id : {} already exists or not", planStrategyId.toString());
        PlanStrategy planStrategy = planStrategyRepository.findById(planStrategyId).orElse(new PlanStrategy());
        if (planStrategy.getPlanStrategyId() == null) {
            planStrategy.setPlanStrategyId(planStrategyId);
        }
        for (Lvl1 lvl1 : request.getLvl1List()) {
            for (Lvl2 lvl2 : lvl1.getLvl2List()) {
                PlanStrategyMapperDTO planStrategyMapperDTO = PlanStrategyMapperDTO.builder()
                        .planStrategy(planStrategy)
                        .lvl1(lvl1)
                        .lvl2(lvl2)
                        .request(request)
                        .build();
//                add or update planStrategyCluster size cluster at a merchCatg/subCatg/fineline/style/cc
                planStrategy.setPlanClusterStrategies(strategySPClusterMapper.setPlanStrategyCluster(planStrategyMapperDTO));
            }
        }
        planStrategyClusterRepository.save(planStrategy);
    }

    private void addFixtureStrategy(PlanStrategyDTO request) {
        log.info("Adding Fixture Strategy request: {}", request);
        Long strategyId = strategyGroupRepository.getStrategyIdBySeasonCd(StratGroupType.FIXTURE.getStrategyGroupTypeCode(), null, null);
        setFixtureStrategy(request, strategyId);
    }

    /***
     * This method will update the Plan Strategy for Size Object whenever an UPDATE event will be sent by strategy listener
     * @param request
     */
    public void updateSizeStrategy(PlanStrategyDTO request) {
        log.info("Updating Size Strategy request: {}", request);
        Long strategyId = strategyGroupRepository.getStrategyIdBySeasonCd(StratGroupType.SIZE_PROFILE.getStrategyGroupTypeCode(), null, null);
        PlanStrategyId planStrategyId = PlanStrategyId.builder()
                .planId(request.getPlanId())
                .strategyId(strategyId)
                .build();
        log.info("Check if a planStrategy Id : {} exists or not", planStrategyId.toString());
        PlanStrategy planStrategy = planStrategyRepository.findById(planStrategyId)
                .orElseThrow(() -> new CustomException("Invalid Plan Strategy Id"));
        for (Lvl1 lvl1 : request.getLvl1List()) {
            for (Lvl2 lvl2 : lvl1.getLvl2List()) {
                for (Lvl3 lvl3 : lvl2.getLvl3List()) {
                	if((!CollectionUtils.isEmpty(lvl3.getStrategy().getStoreSizeClusters())) ||(!CollectionUtils.isEmpty(lvl3.getStrategy().getOnlineSizeClusters()))) {
                	    Set<PlanClusterStrategy> planClusterStrategies = planStrategy.getPlanClusterStrategies();
                        PlanStrategyMapperDTO planStrategyMapperDTO = PlanStrategyMapperDTO.builder()
                                .planStrategy(planStrategy)
                                .planClusterStrategies(planClusterStrategies)
                                .lvl1(lvl1)
                                .lvl2(lvl2)
                                .lvl3(lvl3)
                                .request(request)
                                .build();
                        strategySPClusterMapper.updatePlanStrategyCluster(planStrategyMapperDTO);
                	}
                }
            }
        }
        planStrategyRepository.save(planStrategy);

    }
   
 }


