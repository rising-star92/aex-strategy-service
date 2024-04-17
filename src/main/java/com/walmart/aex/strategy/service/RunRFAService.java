package com.walmart.aex.strategy.service;

import com.walmart.aex.strategy.dto.*;
import com.walmart.aex.strategy.dto.request.*;
import com.walmart.aex.strategy.entity.StrategyFineline;
import com.walmart.aex.strategy.enums.StratGroupType;
import com.walmart.aex.strategy.exception.CustomException;
import com.walmart.aex.strategy.repository.*;
import com.walmart.aex.strategy.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;


import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RunRFAService {
    private final StrategyGroupRepository strategyGroupRepository;

    private final RunRFARepository runRFARepository;

    private final AllocRunTypeRepository allocRunTypeRepository;

    private final StrategyFinelineRepository strategyFinelineRepository;
    @Autowired
    RunRFAStatusFinelineMapper runRFAStatusFinelineMapper;

    public RunRFAService(StrategyGroupRepository strategyGroupRepository,
                                     RunRFARepository runRFARepository,
                                     AllocRunTypeRepository allocRunTypeRepository,
                                     StrategyFinelineRepository strategyFinelineRepository) {
        this.strategyGroupRepository = strategyGroupRepository;
        this.runRFARepository = runRFARepository;
        this.allocRunTypeRepository = allocRunTypeRepository;
        this.strategyFinelineRepository = strategyFinelineRepository;
    }


    public List<RFAStatusDataDTO> fetchAllocRunTypes(){
        List<RFAStatusDataDTO> allocRunTypes = new ArrayList<>();
        Optional.ofNullable(allocRunTypeRepository.fetAllAllocationRunType())
                .stream()
                .flatMap(Collection::stream)
                .forEach(allocRunType -> runRFAStatusFinelineMapper.mapAllocationRuntype(allocRunType, allocRunTypes));
        return allocRunTypes;
    }

    public PlanStrategyResponse fetchRunRFAStatusByPlan(Long planId, PlanStrategyResponse response) {
        Long strategyId = strategyGroupRepository.getStrategyIdByStrategyGroupTypeAndPlanId(StratGroupType.FIXTURE.getStrategyGroupTypeCode(),planId);
        try{
            List<RunRFAFetchDTO> runRFAStatusDTOList = runRFARepository.getRunRFAStatusForFinelines(planId, strategyId);
            List<FixtureAllocationStrategy> runRFAStatusList = Optional.ofNullable(runRFAStatusDTOList)
                    .stream().flatMap(Collection::stream)
                    .map(RunRFAFetchDTO::getRunRFAFetchData)
                    .collect(Collectors.toList());

            Optional.of(runRFAStatusList)
                    .stream()
                    .flatMap(Collection::stream)
                    .forEach(runRFAStatusFineline -> {
                        if(runRFAStatusFineline!=null)
                            runRFAStatusFinelineMapper
                            .mapRunRFAStatusLvl2(runRFAStatusFineline, response);
                    });
        } catch (Exception e) {
            log.error("Error Occured when fetching Run RFA for planId: {}", e.getMessage());
            throw new CustomException("Exception occurred when fetching Run RFA for finelines: "+e);
        }
        return response;
    }


    @Transactional
    public void updateFinelineRFAStatus(PlanStrategyRequest request) {
        List<StrategyFineline> strategyFinelineList = new ArrayList<>();
        Map<String, String> updatingFields = new HashMap<>();
        UpdatedFields allUpdatedFields = Optional.ofNullable(request.getAll()).
                map(UpdateAll::getUpdatedFields).orElse(null);
        if(allUpdatedFields != null)
            updatingFields = getUpdatingFields(allUpdatedFields);

        updateFineLineStatuses(request, updatingFields,strategyFinelineList);
        if (!CollectionUtils.isEmpty(strategyFinelineList))
            strategyFinelineRepository.saveAll(strategyFinelineList);
    }

    private void updateFineLineStatuses(PlanStrategyRequest request, Map<String, String> updatingFields, List<StrategyFineline> stratFlList) {
        Optional.ofNullable(request.getLvl3List()).stream().flatMap(Collection::stream)
                .forEach(lvl3->fetchSubCategories(request.getPlanId(), lvl3, updatingFields, stratFlList));
    }

    private void fetchSubCategories(Long planId, Lvl3 lvl3, Map<String, String> updatingFields, List<StrategyFineline> stratFlList){
        Optional.ofNullable(lvl3.getLvl4List()).stream().flatMap(Collection::stream)
                .forEach(lvl4->fetchAndUpdateFineLines(planId, lvl3.getLvl3Nbr(), lvl4, updatingFields, stratFlList));
    }

    private void fetchAndUpdateFineLines(Long planId, Integer lvl3Nbr, Lvl4 lvl4, Map<String, String> updatingFields, List<StrategyFineline> stratFlList){
        if (updatingFields.isEmpty()) {
            Optional.ofNullable(lvl4.getFinelines()).stream().flatMap(Collection::stream)
                    .forEach(fineLine -> fetchAndUpdateFinelineList(planId, lvl3Nbr, lvl4.getLvl4Nbr(), Arrays.asList(fineLine.getFinelineNbr()), getUpdatingFields(fineLine.getUpdatedFields()), stratFlList));
        }
        else {
            List<Integer> fineLineNbrs = lvl4.getFinelines().stream().map(Fineline::getFinelineNbr).collect(Collectors.toList());
            fetchAndUpdateFinelineList(planId, lvl3Nbr, lvl4.getLvl4Nbr(),fineLineNbrs, updatingFields, stratFlList);
        }
    }

    private void fetchAndUpdateFinelineList(Long plandId, Integer lvl3Nbr, Integer lvl4Nbr, List<Integer> fineLineNbrs, Map<String, String> updatingFields, List<StrategyFineline> stratFLList){
        List<StrategyFineline> finelineList = strategyFinelineRepository.findFineLines_ByPlan_Id_AndCat_IdAndSub_Cat_IdAndFineline_nbr(plandId, lvl3Nbr, lvl4Nbr, fineLineNbrs);
        if (!CollectionUtils.isEmpty(finelineList)) {
            finelineList.forEach(strategyFineline -> setUpdatedCode(strategyFineline, updatingFields));
            stratFLList.addAll(finelineList);
        }
    }

    private void setUpdatedCode(StrategyFineline strategyFineline, Map<String, String> updatingFields) {
        final String allocTypeCode = "allocTypeCode";
        final String runStatusCode = "runStatusCode";
        final String rfaStatusCode = "rfaStatusCode";

        if (updatingFields.containsKey(allocTypeCode))
            strategyFineline.setAllocTypeCode(Integer.parseInt(updatingFields.get(allocTypeCode)));

        if (updatingFields.containsKey(runStatusCode) && !updatingFields.containsKey(rfaStatusCode))
            strategyFineline.setRunStatusCode(Integer.parseInt(updatingFields.get(runStatusCode)));

        if (updatingFields.containsKey(rfaStatusCode)) {
            strategyFineline.setRfaStatusCode(Integer.parseInt(updatingFields.get(rfaStatusCode)));
            if(updatingFields.containsKey(runStatusCode) && (((Integer.parseInt(updatingFields.get(rfaStatusCode))==4 || Integer.parseInt(updatingFields.get(rfaStatusCode))==5) && (strategyFineline.getRunStatusCode()==null || strategyFineline.getRunStatusCode()!=2))|| (Integer.parseInt(updatingFields.get(rfaStatusCode))!=4 && Integer.parseInt(updatingFields.get(rfaStatusCode))!=5)))
                strategyFineline.setRunStatusCode(Integer.parseInt(updatingFields.get(runStatusCode)));
        }
    }

    private Map<String, String> getUpdatingFields(UpdatedFields updatedFields){
        return Optional.ofNullable(updatedFields)
                .map(UpdatedFields::getRunRfaStatus)
                .map(CommonUtil::getUpdatedFieldsMap).orElse(new HashMap<>());
    }
}
