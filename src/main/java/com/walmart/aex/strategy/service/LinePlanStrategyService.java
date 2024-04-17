package com.walmart.aex.strategy.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.strategy.dto.*;
import com.walmart.aex.strategy.dto.ahs.*;
import com.walmart.aex.strategy.dto.request.*;
import com.walmart.aex.strategy.entity.TargetCounts;
import com.walmart.aex.strategy.exception.CustomException;
import com.walmart.aex.strategy.repository.CategoryLinePlanRepository;
import com.walmart.aex.strategy.repository.SubcategoryLinePlanRepository;
import com.walmart.aex.strategy.repository.LinePlanStrategyRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class LinePlanStrategyService {

    private final CategoryLinePlanRepository categoryLinePlanRepository;
    private final SubcategoryLinePlanRepository subcategoryLinePlanRepository;
    private final LinePlanStrategyRepository linePlanStrategyRepository;
    final ObjectMapper objectMapper = new ObjectMapper();

    public LinePlanStrategyService(CategoryLinePlanRepository categoryLinePlanRepository,
                                   SubcategoryLinePlanRepository subcategoryLinePlanRepository,
                                   LinePlanStrategyRepository linePlanStrategyRepository) {
        this.categoryLinePlanRepository = categoryLinePlanRepository;
        this.subcategoryLinePlanRepository = subcategoryLinePlanRepository;
        this.linePlanStrategyRepository = linePlanStrategyRepository;
    }

    public StrategyCountResponse fetchCurrentLinePlanStrategyTargetCount(Long planId) {
        try {
            List<TargetCounts> targetStoreCount = categoryLinePlanRepository.findTargetCounts(planId, 2);
            List<TargetCounts> targetOnlineCount = categoryLinePlanRepository.findTargetCounts(planId, 1);

            return populateTargetCountResponse( targetStoreCount, targetOnlineCount);
        } catch (Exception e) {
            log.error("Error occurred while fetching counts", e);
            throw e;
        }
    }

    private StrategyCountResponse populateResponse(Map<String, Long> finelineActualCountMap, Map<String,
            Long> ccActualCountMap, List<TargetCounts> targetStoreCount, List<TargetCounts> targetOnlineCount) {
        StrategyCountResponse response = new StrategyCountResponse();
        response.setFinelineStoreActual(finelineActualCountMap.get("store"));
        response.setFinelineOnlineActual(finelineActualCountMap.get("online"));
        response.setCcStoreActual(ccActualCountMap.get("store"));
        response.setCcOnlineActual(ccActualCountMap.get("online"));

        if (!targetStoreCount.isEmpty() ) {
            response.setFinelineStoreTarget(Optional.ofNullable(targetStoreCount.get(0).getFlCount()).orElse(0L));
            response.setCcStoreTarget(Optional.ofNullable(targetStoreCount.get(0).getCcCount()).orElse(0L));
        }
        if (!targetOnlineCount.isEmpty()) {
            response.setFinelineOnlineTarget(Optional.ofNullable(targetOnlineCount.get(0).getFlCount()).orElse(0L));
            response.setCcOnlineTarget(Optional.ofNullable(targetOnlineCount.get(0).getCcCount()).orElse(0L));
        }
        return response;
    }

    private StrategyCountResponse populateTargetCountResponse(List<TargetCounts> targetStoreCount, List<TargetCounts> targetOnlineCount) {
        StrategyCountResponse response = new StrategyCountResponse();
        if (!targetStoreCount.isEmpty() ) {
            response.setFinelineStoreTarget(Optional.ofNullable(targetStoreCount.get(0).getFlCount()).orElse(0L));
            response.setCcStoreTarget(Optional.ofNullable(targetStoreCount.get(0).getCcCount()).orElse(0L));
        }
        if (!targetOnlineCount.isEmpty()) {
            response.setFinelineOnlineTarget(Optional.ofNullable(targetOnlineCount.get(0).getFlCount()).orElse(0L));
            response.setCcOnlineTarget(Optional.ofNullable(targetOnlineCount.get(0).getCcCount()).orElse(0L));
        }
        return response;
    }

    private int getChannelId(String channel) {
        try {
            if ("Store".equalsIgnoreCase(channel)) {
                return 1;
            } else if ("Online".equalsIgnoreCase(channel)) {
                return 2;
            } else {
                throw new CustomException("Invalid Channel");
            }
        } catch (Exception e){
            throw new CustomException("Invalid Channel");
        }
    }


    public List<LinePlanCount> fetchCurrentLinePlanStrategy(LinePlanStrategyRequest request) {
        Long planId = request.getPlanId();
        Integer channelId = getChannelId(request.getChannel());
        List<LinePlanCount> countList = new ArrayList<>();
        countList.addAll(linePlanStrategyRepository.fetchCurrentLinePlanCount(planId, channelId));
        return countList;
    }

    private Lvl2 populateLvl2Resp(Integer key, List<LinePlanCount> lvl2CountList, List<LinePlanCount> countList,
                                  Integer planYear) {
        Lvl2 lvl2 = new Lvl2();
        lvl2.setLvl2Nbr(key);
        lvl2.setLvl2Name(lvl2CountList.stream()
                .filter(lvl2Count -> StringUtils.isNotEmpty(lvl2Count.getLvl2Name()))
                .findAny().map(lvl2Count -> lvl2Count.getLvl2Name())
                .orElse(null));
        lvl2.setStrategy(getStrategy(lvl2CountList, planYear));

        Map<Integer, List<LinePlanCount>> lvl3CountMap = new HashMap<>();
        for (LinePlanCount linePlanCount : countList) {
            if (linePlanCount.getType() == 2 && linePlanCount.getLvl2Nbr().equals(key)) {
                if (lvl3CountMap.containsKey(linePlanCount.getLvl3Nbr())) {
                    lvl3CountMap.get(linePlanCount.getLvl3Nbr()).add(linePlanCount);
                } else {
                    lvl3CountMap.put(linePlanCount.getLvl3Nbr(), new ArrayList<>(Arrays.asList(linePlanCount)));
                }
            }
        }

        List<Lvl3> lvl3List = new ArrayList<>();
        lvl3List.addAll(lvl3CountMap.entrySet().stream().map(entrySet -> populateLvl3Resp(entrySet.getKey(),
                        entrySet.getValue(), countList, planYear))
                .collect(Collectors.toList()));

        lvl2.setLvl3List(lvl3List);
        return lvl2;

    }

    private Lvl3 populateLvl3Resp(Integer key, List<LinePlanCount> lvl3CountList, List<LinePlanCount> countList,
                                  Integer planYear) {
        Lvl3 lvl3 = new Lvl3();
        lvl3.setLvl3Nbr(key);
        lvl3.setLvl3Name(lvl3CountList.stream()
                .filter(lvl3Count -> StringUtils.isNotEmpty(lvl3Count.getLvl3Name()))
                .findAny().map(lvl3Count -> lvl3Count.getLvl3Name())
                .orElse(null));
        lvl3.setStrategy(getStrategy(lvl3CountList, planYear));

        Map<Integer, List<LinePlanCount>> lvl4CountMap = new HashMap<>();
        for (LinePlanCount linePlanCount : countList) {
            if (linePlanCount.getType() == 3 && linePlanCount.getLvl3Nbr().equals(key)) {
                if (lvl4CountMap.containsKey(linePlanCount.getLvl4Nbr())) {
                    lvl4CountMap.get(linePlanCount.getLvl4Nbr()).add(linePlanCount);
                } else {
                    lvl4CountMap.put(linePlanCount.getLvl4Nbr(), new ArrayList<>(Arrays.asList(linePlanCount)));
                }
            }
        }

        List<Lvl4> lvl4List = new ArrayList<>();
        lvl4List.addAll(lvl4CountMap.entrySet().stream().map(entrySet -> populateLvl4Resp(entrySet.getKey(),
                        entrySet.getValue(), countList, planYear))
                .collect(Collectors.toList()));
        lvl3.setLvl4List(lvl4List);

        return lvl3;
    }

    private Lvl4 populateLvl4Resp(Integer key, List<LinePlanCount> lvl4CountList, List<LinePlanCount> countList,
                                  Integer planYear) {
        Lvl4 lvl4 = new Lvl4();
        lvl4.setLvl4Nbr(key);
        lvl4.setLvl4Name(lvl4CountList.stream()
                .filter(lvl4Count -> StringUtils.isNotEmpty(lvl4Count.getLvl4Name()))
                .findAny().map(lvl3Count -> lvl3Count.getLvl4Name())
                .orElse(null));
        lvl4.setStrategy(getStrategy(lvl4CountList, planYear));
        return lvl4;
    }

    private Strategy getStrategy(List<LinePlanCount> linePlanCountList, Integer planYear) {
        Strategy strategy = new Strategy();
        LinePlanStrategy linePlanStrategy = new LinePlanStrategy();

        for (LinePlanCount lvl2Count : linePlanCountList) {

            if (lvl2Count.getFiscalYear() == null) {
                linePlanStrategy.setCurrent(getLinePlanStrategyAttributes(lvl2Count));
            } else if (lvl2Count.getFiscalYear() == planYear - 1) {
                linePlanStrategy.setLyActuals(getLinePlanStrategyAttributes(lvl2Count));
            } else if (lvl2Count.getFiscalYear() == planYear - 2) {
                linePlanStrategy.setLlyActuals(getLinePlanStrategyAttributes(lvl2Count));
            }

        }

        strategy.setLinePlan(linePlanStrategy);
        return strategy;
    }

    private LinePlanStrategyAttributes getLinePlanStrategyAttributes(LinePlanCount linePlanCount) {
        LinePlanStrategyAttributes linePlanStrategyAttributes = new LinePlanStrategyAttributes();
        linePlanStrategyAttributes.setFinelineCount(linePlanCount.getFinelineCount());
        linePlanStrategyAttributes.setCustomerChoiceCount(linePlanCount.getCustomerChoiceCount());
        linePlanStrategyAttributes.setAttributeGroups(linePlanCount.getAttributeObj());
        return linePlanStrategyAttributes;
    }

    private Strategy getAllStrategy(List<Lvl2> lvl2List) {
        Strategy strategy = new Strategy();
        LinePlanStrategy linePlanStrategy = new LinePlanStrategy();

        List<LinePlanStrategyAttributes> currentLinePlanStrategies = lvl2List.stream()
                .map(Lvl2::getStrategy)
                .map(Strategy::getLinePlan)
                .map(LinePlanStrategy::getCurrent)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        LinePlanStrategyAttributes currentLinePlanStrategy = new LinePlanStrategyAttributes();
        if (!CollectionUtils.isEmpty(currentLinePlanStrategies)) {
            currentLinePlanStrategy.setFinelineCount(currentLinePlanStrategies.stream()
                    .filter(linePlanStrategyAttributes -> linePlanStrategyAttributes.getFinelineCount() != null)
                    .map(LinePlanStrategyAttributes::getFinelineCount)
                    .reduce(0, Integer::sum));
            currentLinePlanStrategy.setCustomerChoiceCount(currentLinePlanStrategies.stream()
                    .filter(linePlanStrategyAttributes -> linePlanStrategyAttributes.getCustomerChoiceCount() != null)
                    .map(LinePlanStrategyAttributes::getCustomerChoiceCount)
                    .reduce(0, Integer::sum));
        }

        List<LinePlanStrategyAttributes> lyActualsPlanStrategies = lvl2List.stream()
                .map(Lvl2::getStrategy)
                .map(Strategy::getLinePlan)
                .map(LinePlanStrategy::getLyActuals)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        LinePlanStrategyAttributes lyActualsLinePlanStrategy = new LinePlanStrategyAttributes();
        if (!CollectionUtils.isEmpty(lyActualsPlanStrategies)) {
            lyActualsLinePlanStrategy.setFinelineCount(lyActualsPlanStrategies.stream()
                    .map(LinePlanStrategyAttributes::getFinelineCount)
                    .reduce(0, Integer::sum));
            lyActualsLinePlanStrategy.setCustomerChoiceCount(lyActualsPlanStrategies.stream()
                    .map(LinePlanStrategyAttributes::getCustomerChoiceCount)
                    .reduce(0, Integer::sum));
        }

        List<LinePlanStrategyAttributes> llyActualsPlanStrategies = lvl2List.stream()
                .map(Lvl2::getStrategy)
                .map(Strategy::getLinePlan)
                .map(LinePlanStrategy::getLlyActuals)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        LinePlanStrategyAttributes llyActualsLinePlanStrategy = new LinePlanStrategyAttributes();
        if (!CollectionUtils.isEmpty(llyActualsPlanStrategies)) {
            llyActualsLinePlanStrategy.setFinelineCount(llyActualsPlanStrategies.stream()
                    .map(LinePlanStrategyAttributes::getFinelineCount)
                    .reduce(0, Integer::sum));
            llyActualsLinePlanStrategy.setCustomerChoiceCount(llyActualsPlanStrategies.stream()
                    .map(LinePlanStrategyAttributes::getCustomerChoiceCount)
                    .reduce(0, Integer::sum));
        }

        linePlanStrategy.setCurrent(currentLinePlanStrategy);
        linePlanStrategy.setLyActuals(lyActualsLinePlanStrategy);
        linePlanStrategy.setLlyActuals(llyActualsLinePlanStrategy);

        strategy.setLinePlan(linePlanStrategy);
        return strategy;

    }

    private void parseAttributeObj(String attributeName, List<LinePlanCount> attributesList, List<LinePlanCount> attributeList, int type) throws JsonProcessingException {
        for(LinePlanCount list : attributesList) {
            LinePlanCount wrapper = new LinePlanCount();
            Collection<AttributeObj> attributes = objectMapper.readValue(list.getAttributeJson(), new TypeReference<Collection<AttributeObj>>(){});
            List<AttributeObj> jsonObject = attributes.stream()
                    .filter( a -> a.getAttributeGroupName().equals(attributeName))
                    .collect(Collectors.toList());
            wrapper.setAttributeObj(jsonObject);
            wrapper.setLvl0Nbr(list.getLvl0Nbr());
            wrapper.setLvl1Nbr(list.getLvl1Nbr());
            wrapper.setLvl2Nbr(list.getLvl2Nbr());
            wrapper.setLvl3Nbr(list.getLvl3Nbr());
            wrapper.setLvl4Nbr(list.getLvl4Nbr());
            wrapper.setType(type);
            wrapper.setFinelineCount(list.getFinelineCount());
            wrapper.setCustomerChoiceCount(list.getCustomerChoiceCount());
            attributeList.add(wrapper);
        }
    }

    public LinePlanStrategyResponse fetchTargetsByAttribute(LinePlanStrategyRequest request) throws JsonProcessingException {
        Long planId = request.getPlanId();
        String planDesc = request.getPlanDesc();
        String attributeName = request.getAttribute();

        int channelId = getChannelId(request.getChannel());
        if (channelId == 0) {
            throw new CustomException("Invalid Channel ID: " + request.getChannel());
        }

        StrategyType strategy = request.getStrategyType();
        if (strategy == null) {
            throw new CustomException("Invalid Strategy Type: " + request.getStrategyType());
        }
        Long strategyId = strategy.getStrategyId();
        if (strategyId == null) {
            throw new CustomException("Invalid Strategy ID: " + strategyId);
        }

        int planYear;
        try {
            planYear = Integer.parseInt(planDesc.substring(planDesc.length() - 4));
        } catch (Exception e) {
            log.error("Exception when fetching fiscal year for plan definition - " + e.getMessage());
            throw new CustomException("Invalid PlanDesc: " + planDesc);
        }

        //Get rows from database
        List<LinePlanCount> categoryAttributesList = new ArrayList<>(categoryLinePlanRepository.getCategoryAttributeStrategy(request.getPlanId(), channelId, strategyId));
        List<LinePlanCount> subcategoryAttributesList = new ArrayList<>(subcategoryLinePlanRepository.getSubcategoryAttributeStrategy(request.getPlanId(), channelId, strategyId));

        //Parse json into object
        List<LinePlanCount> attributeList = new ArrayList<>();

        if (request.getAttribute() == null) {
            throw new CustomException("Invalid Attribute: " + request.getAttribute());
        }

        parseAttributeObj(attributeName, categoryAttributesList, attributeList, 2);
        parseAttributeObj(attributeName, subcategoryAttributesList, attributeList, 3);

        Integer lvl0Nbr = null;
        Integer lvl1Nbr = null;

        if (!CollectionUtils.isEmpty(attributeList)) {
            LinePlanCount wrapper = attributeList.get(0);
            lvl0Nbr = wrapper.getLvl0Nbr();
            lvl1Nbr = wrapper.getLvl1Nbr();
        }

        LinePlanStrategyResponse response = new LinePlanStrategyResponse();
        response.setPlanId(planId);
        response.setPlanDesc(planDesc);
        response.setLvl0Nbr(lvl0Nbr);

        List<Lvl1> lvl1List = new ArrayList<>();
        Lvl1 lvl1 = new Lvl1();
        lvl1.setLvl1Nbr(lvl1Nbr);

        Map<Integer, List<LinePlanCount>> lvl2AttributeMap = new HashMap<>();
        for (LinePlanCount list : attributeList) {
            lvl2AttributeMap.put(list.getLvl2Nbr(), new ArrayList<>(Arrays.asList(list)));
        }

        List<Lvl2> lvl2List = new ArrayList<>();
        lvl2List.addAll(lvl2AttributeMap.entrySet().stream().map(entrySet -> populateLvl2Resp(entrySet.getKey(),
                entrySet.getValue(), attributeList, planYear))
                .collect(Collectors.toList()));

        lvl1.setLvl2List(lvl2List);
        lvl1List.add(lvl1);
        response.setLvl1List(lvl1List);

        return response;
    }

}
