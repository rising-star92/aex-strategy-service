package com.walmart.aex.strategy.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.strategy.dto.PlanDTO.*;
import com.walmart.aex.strategy.dto.PlanStrategyListenerResponse;
import com.walmart.aex.strategy.dto.ahs.Attribute;
import com.walmart.aex.strategy.dto.ahs.AttributeObj;
import com.walmart.aex.strategy.dto.ahs.AttributeWrapper;
import com.walmart.aex.strategy.dto.request.Fineline;
import com.walmart.aex.strategy.dto.request.Lvl3;
import com.walmart.aex.strategy.dto.request.Lvl4;
import com.walmart.aex.strategy.entity.*;
import com.walmart.aex.strategy.enums.ChannelType;
import com.walmart.aex.strategy.enums.IncludeOffshoreMkt;
import com.walmart.aex.strategy.exception.CustomException;
import com.walmart.aex.strategy.properties.AppProperties;
import com.walmart.aex.strategy.repository.*;
import io.strati.ccm.utils.client.annotation.ManagedConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.walmart.aex.strategy.util.CommonUtil.STRATEGY_ID;

@Service
@Slf4j
@Transactional
public class PlanDefinitionService {

    private final CategoryLinePlanRepository categoryLinePlanRepository;
    private final SubcategoryLinePlanRepository subcategoryLinePlanRepository;
    private final StrategyMerchCatgRepository strategyMerchCatgRepository;
    private final StrategySubCatgRepository strategySubCatgRepository;
    private final PlanStrategyRepository planStrategyRepository;
    private final AttributeHelperService attributeHelperService;

    private final ObjectMapper objectMapper;

    @ManagedConfiguration
    private AppProperties appProperties;


    public PlanDefinitionService(CategoryLinePlanRepository categoryLinePlanRepository, SubcategoryLinePlanRepository subcategoryLinePlanRepository, StrategyMerchCatgRepository strategyMerchCatgRepository, StrategySubCatgRepository strategySubCatgRepository, PlanStrategyRepository planStrategyRepository, AttributeHelperService attributeHelperService, ObjectMapper objectMapper) {
        this.categoryLinePlanRepository = categoryLinePlanRepository;
        this.subcategoryLinePlanRepository = subcategoryLinePlanRepository;
        this.strategyMerchCatgRepository = strategyMerchCatgRepository;
        this.strategySubCatgRepository = strategySubCatgRepository;
        this.planStrategyRepository = planStrategyRepository;
        this.attributeHelperService = attributeHelperService;
        this.objectMapper =objectMapper;
    }


    public PlanStrategyListenerResponse addPlanDefinition(PlanDefMessageDTO requestParent) {
        PlanDTO request = requestParent.getPayload();
        PlanStrategyListenerResponse responseDTO = new PlanStrategyListenerResponse();
        PlanHierarchyDTO planHierarchyDTO = Optional.ofNullable(request.getPlanHierarchy())
                .orElseThrow(() -> new CustomException("Plan Hierarchy cannot be null"));


        PlanStrategyId planStrategyId = PlanStrategyId.builder()
                .planId(request.getPlanId())
                .strategyId(STRATEGY_ID)
                .build();
        PlanStrategy planStrategy = planStrategyRepository.findById(planStrategyId).orElseGet(() -> planStrategyRepository.save(PlanStrategy.builder()
                .planStrategyId(planStrategyId)
                .planClusterStrategies(new HashSet<>())
                .strategyMerchCatgs(new HashSet<>())
                .build()));



        Integer lvl0Nbr = planHierarchyDTO.getLvl0Nbr();

        Optional.ofNullable(planHierarchyDTO.getLvl1()).ifPresent(lvl1DTO -> {
            lvl1DTO.forEach(lvl1 -> {
                Integer lvl1Nbr = lvl1.getLvl1Nbr();
                lvl1.getLvl2().forEach(lvl2 -> {
                    Integer lvl2Nbr = lvl2.getLvl2Nbr();
                    lvl2.getLvl3().forEach(lvl3 -> {

                        Integer  lvl3Nbr= lvl3.getLvl3Nbr();
                        String attributeObj = fetchAttributeValues(lvl1Nbr,lvl3Nbr,request.getPlanDesc());

                        StrategyMerchCatg strategyMerchCatg = addStrategyMechCatg(planStrategy, lvl0Nbr, lvl1Nbr, lvl2Nbr, lvl3Nbr);

                        addCategoryLinePlan(strategyMerchCatg, request.getPlanId(), lvl0Nbr, lvl1Nbr, lvl2Nbr, lvl3Nbr, ChannelType.STORE.getId(),attributeObj);
                        addCategoryLinePlan(strategyMerchCatg, request.getPlanId(), lvl0Nbr, lvl1Nbr, lvl2Nbr, lvl3Nbr, ChannelType.ONLINE.getId(),attributeObj);
                        lvl3.getLvl4().forEach(lvl4 -> {
                            Integer lvl4Nbr = lvl4.getLvl4Nbr();
                            addStrategySubCat(lvl4Nbr, strategyMerchCatg);

                            addSubcategoryLinePlan(request.getPlanId(), planStrategyId.getStrategyId(), lvl0Nbr, lvl1Nbr, lvl2Nbr, lvl3Nbr, lvl4Nbr, ChannelType.STORE.getId(),attributeObj);
                            addSubcategoryLinePlan(request.getPlanId(), planStrategyId.getStrategyId(), lvl0Nbr, lvl1Nbr, lvl2Nbr, lvl3Nbr, lvl4Nbr, ChannelType.ONLINE.getId(),attributeObj);

                        });

                    });
                });
            });
        });
        responseDTO.setStatus(PlanStrategyService.SUCCESS_STATUS);
        return responseDTO;
    }

    public PlanStrategyListenerResponse updatePlanStrategy(PlanDefMessageDTO requestParent){
        PlanDTO request = requestParent.getPayload();
        PlanStrategyListenerResponse responseDTO = new PlanStrategyListenerResponse();
        log.info("hierarchy from request::" + request.getPlanHierarchy());
        PlanHierarchyDTO planHierarchyDTO = Optional.ofNullable(request.getPlanHierarchy())
                .orElseThrow(() -> new CustomException("Plan Hierarchy cannot be null"));

        PlanStrategyId planStrategyId = PlanStrategyId.builder()
                .planId(request.getPlanId())
                .strategyId(STRATEGY_ID)
                .build();
        PlanStrategy planStrategy = planStrategyRepository.findById(planStrategyId).orElse(null);

        if(planStrategy != null){

            Integer lvl0Nbr = planHierarchyDTO.getLvl0Nbr();

            Optional.ofNullable(planHierarchyDTO.getLvl1()).ifPresent(lvl1DTO -> {
                lvl1DTO.forEach(lvl1 -> {
                    Integer lvl1Nbr = lvl1.getLvl1Nbr();
                    lvl1.getLvl2().forEach(lvl2 -> {
                        Integer lvl2Nbr = lvl2.getLvl2Nbr();
                        List<Integer> dbCategoryList = categoryLinePlanRepository.getCategories(request.getPlanId(),STRATEGY_ID,lvl2Nbr,lvl1Nbr,lvl0Nbr);

                        List<Integer> requestCategoryList = lvl2.getLvl3()
                                .stream()
                                .map(Lvl3DTO::getLvl3Nbr)
                                .collect(Collectors.toList());

                        List<Integer> catsToDel = new ArrayList<>(CollectionUtils.subtract(dbCategoryList,requestCategoryList));

                        lvl2.getLvl3().forEach(lvl3 -> {

                            Integer  lvl3Nbr= lvl3.getLvl3Nbr();

                            if(dbCategoryList.contains(lvl3Nbr)){

                                List<Integer> uiCatSubCatgs = lvl3.getLvl4()
                                        .stream()
                                        .map(Lvl4DTO::getLvl4Nbr)
                                        .collect(Collectors.toList());

                                List<Integer> dbSubCatgs = subcategoryLinePlanRepository.getSubCategoryNbr(request.getPlanId(),STRATEGY_ID,lvl3Nbr,lvl2Nbr,lvl1Nbr,lvl0Nbr);

                                // check sub-catgs from ui payload and database, if subcatgs are different
                                if(!CollectionUtils.isEqualCollection(dbSubCatgs,uiCatSubCatgs)){
                                        // check sub-catgs to add
                                    List<Integer> subCatgsToAdd = new ArrayList<>(CollectionUtils.subtract(uiCatSubCatgs,dbSubCatgs));
                                    List<Integer> subCatgsToDel = new ArrayList<>(CollectionUtils.subtract(dbSubCatgs,uiCatSubCatgs));



                                    if(!subCatgsToAdd.isEmpty()){
                                        String attributeObj = fetchAttributeValues(lvl1Nbr,lvl3Nbr,request.getPlanDesc());
                                        StrategyMerchCatg strategyMerchCatg = addStrategyMechCatg(planStrategy, lvl0Nbr, lvl1Nbr, lvl2Nbr, lvl3Nbr);

                                        //uiCatSubCatgs will have catgs to be added in DB
                                        subCatgsToAdd.forEach(lvl4Nbr -> {
                                            addStrategySubCat(lvl4Nbr, strategyMerchCatg);
                                            addSubcategoryLinePlan(request.getPlanId(), planStrategyId.getStrategyId(), lvl0Nbr, lvl1Nbr, lvl2Nbr, lvl3Nbr, lvl4Nbr, ChannelType.STORE.getId(),attributeObj);
                                            addSubcategoryLinePlan(request.getPlanId(), planStrategyId.getStrategyId(), lvl0Nbr, lvl1Nbr, lvl2Nbr, lvl3Nbr, lvl4Nbr, ChannelType.ONLINE.getId(),attributeObj);
                                        });


                                    }
                                    if(!subCatgsToDel.isEmpty()){

                                        subCatgsToDel.forEach(lvl4Nbr ->{

                                            Stream.of(ChannelType.values()).forEach(channelType -> {
                                                if(channelType.getId() != 3) {
                                                    SubCategoryLineplanId subCategoryLineplanId = SubCategoryLineplanId.builder()
                                                            .lvl0Nbr(lvl0Nbr)
                                                            .lvl1Nbr(lvl1Nbr)
                                                            .lvl2Nbr(lvl2Nbr)
                                                            .lvl3Nbr(lvl3Nbr)
                                                            .lvl4Nbr(lvl4Nbr)
                                                            .planId(request.getPlanId())
                                                            .strategyId(STRATEGY_ID)
                                                            .channelId(channelType.getId())
                                                            .build();
                                                    subcategoryLinePlanRepository.deleteById(subCategoryLineplanId);
                                                }
                                                    });

                                        });
                                    }
                                }
                            }else{
                                String attributeObj = fetchAttributeValues(lvl1Nbr,lvl3Nbr,request.getPlanDesc());

                                StrategyMerchCatg strategyMerchCatg = addStrategyMechCatg(planStrategy, lvl0Nbr, lvl1Nbr, lvl2Nbr, lvl3Nbr);

                                addCategoryLinePlan(strategyMerchCatg, request.getPlanId(), lvl0Nbr, lvl1Nbr, lvl2Nbr, lvl3Nbr, ChannelType.STORE.getId(),attributeObj);
                                addCategoryLinePlan(strategyMerchCatg, request.getPlanId(), lvl0Nbr, lvl1Nbr, lvl2Nbr, lvl3Nbr, ChannelType.ONLINE.getId(),attributeObj);
                                lvl3.getLvl4().forEach(lvl4 -> {
                                    Integer lvl4Nbr = lvl4.getLvl4Nbr();
                                    addStrategySubCat(lvl4Nbr, strategyMerchCatg);

                                    addSubcategoryLinePlan(request.getPlanId(), planStrategyId.getStrategyId(), lvl0Nbr, lvl1Nbr, lvl2Nbr, lvl3Nbr, lvl4Nbr, ChannelType.STORE.getId(),attributeObj);
                                    addSubcategoryLinePlan(request.getPlanId(), planStrategyId.getStrategyId(), lvl0Nbr, lvl1Nbr, lvl2Nbr, lvl3Nbr, lvl4Nbr, ChannelType.ONLINE.getId(),attributeObj);

                                });

                            }


                        });
                        if(!catsToDel.isEmpty()){
                            catsToDel.forEach(lvl3Nbr -> {
                                Stream.of(ChannelType.values()).forEach(channelType -> {

                                    if(channelType.getId() != 3){
                                        CategoryLineplanId categoryLineplanId = CategoryLineplanId.builder()
                                                .lvl0Nbr(lvl0Nbr)
                                                .lvl1Nbr(lvl1Nbr)
                                                .lvl2Nbr(lvl2Nbr)
                                                .lvl3Nbr(lvl3Nbr)
                                                .planId(request.getPlanId())
                                                .strategyId(STRATEGY_ID)
                                                .channelId(channelType.getId())
                                                .build();
                                        categoryLinePlanRepository.deleteById(categoryLineplanId);
                                        List<Integer> subCatToDel = subcategoryLinePlanRepository.getSubCategoryNbr(request.getPlanId(),STRATEGY_ID,lvl3Nbr,lvl2Nbr,lvl1Nbr,lvl0Nbr);
                                        if(!CollectionUtils.isEmpty(subCatToDel)){
                                            subCatToDel.forEach(lvl4Nbr -> {
                                                SubCategoryLineplanId subCategoryLineplanId = SubCategoryLineplanId.builder()
                                                        .lvl0Nbr(lvl0Nbr)
                                                        .lvl1Nbr(lvl1Nbr)
                                                        .lvl2Nbr(lvl2Nbr)
                                                        .lvl3Nbr(lvl3Nbr)
                                                        .lvl4Nbr(lvl4Nbr)
                                                        .planId(request.getPlanId())
                                                        .strategyId(STRATEGY_ID)
                                                        .channelId(channelType.getId())
                                                        .build();
                                                subcategoryLinePlanRepository.deleteById(subCategoryLineplanId);
                                            });
                                        }
                                    }
                                });
                            });
                        }
                    });
                });
            });
        }

        responseDTO.setStatus(PlanStrategyService.SUCCESS_STATUS);
        return responseDTO;
    }

    public String fetchAttributeValues(Integer lvl1Nbr, Integer lvl3Nbr, String season) {
        try {
            List<String> attributeTypes = appProperties.getAHSAttributeTypes();
            Map<String, Set<String>> attributeMap = new HashMap<>();
            attributeTypes.forEach(attributeType -> attributeMap.put(attributeType, attributeHelperService.retrieveValuesByHierarchy(attributeType, lvl1Nbr, lvl3Nbr, season)));
            return formatAttributeObj(attributeMap);
        } catch (Exception e){
            log.error("Error occured while fetching Attribute Values",e);
            return "";
        }
    }

    public String formatAttributeObj(Map<String, Set<String>> attributeMap) {
        try {
            List<AttributeObj> attributeObjList = new ArrayList<>();
            attributeMap.forEach((attributeType, values) -> {
                AttributeObj attributeObj = new AttributeObj();
                attributeObj.setAttributeGroupName(attributeType);
                AttributeWrapper attributeWrapper = new AttributeWrapper();
                List<Attribute> attributesValues = new ArrayList<>();
                values.forEach(value -> {
                    Attribute attribute = new Attribute();
                    attribute.setName(value);
                    attribute.setCount(0);
                    attribute.setPercentage(0);
                    attributesValues.add(attribute);
                });
                attributeWrapper.setFinelineAttribute(attributesValues);
                attributeWrapper.setCustomerChoiceAttribute(attributesValues);
                attributeObj.setGoal(attributeWrapper);
                attributeObj.setActual(attributeWrapper);
                attributeObjList.add(attributeObj);
            });
            return objectMapper.writeValueAsString(attributeObjList);
        }
        catch (JsonProcessingException e){
            log.error("Error Occurred while processing response from AHS", e);
        }
        return "";
    }

    private void addCategoryLinePlan(StrategyMerchCatg strategyMerchCatg, Long planId, Integer lvl0Nbr, Integer lvl1Nbr, Integer lvl2Nbr, Integer lvl3Nbr, Integer id,String attributeObj ) {
        categoryLinePlanRepository.save(CategoryLineplan.builder()
                .categoryLineplanId(CategoryLineplanId.builder()
                        .planId(planId)
                        .lvl0Nbr(lvl0Nbr)
                        .lvl1Nbr(lvl1Nbr)
                        .lvl2Nbr(lvl2Nbr)
                        .lvl3Nbr(lvl3Nbr)
                        .strategyId(strategyMerchCatg.getPlanStrategy().getPlanStrategyId().getStrategyId())
                        .channelId(id)
                        .build())
                .strategyMerchCatg(strategyMerchCatg)
                        .attributeObj(attributeObj)
                .build());
    }

    private StrategyMerchCatg addStrategyMechCatg(PlanStrategy planStrategy, Integer lvl0Nbr, Integer lvl1Nbr, Integer lvl2Nbr, Integer lvl3Nbr ) {
        StrategyMerchCatgId strategyMerchCatgId = StrategyMerchCatgId.builder()
                .lvl0Nbr(lvl0Nbr)
                .lvl1Nbr(lvl1Nbr)
                .lvl2Nbr(lvl2Nbr)
                .lvl3Nbr(lvl3Nbr)
                .planStrategyId(planStrategy.getPlanStrategyId())
                .build();
        return strategyMerchCatgRepository.findById(strategyMerchCatgId).orElseGet(() -> {
            StrategyMerchCatg strategyMerchCatg1 = StrategyMerchCatg.builder()
                    .strategyMerchCatgId(strategyMerchCatgId)
                    .planStrategy(planStrategy)
                    .strategySubCatgs(new HashSet<>())
                    .categoryLineplan(new HashSet<>())
                    .build();
            strategyMerchCatgRepository.save(strategyMerchCatg1);
            return strategyMerchCatg1;
        });

    }

    private void addSubcategoryLinePlan(Long planId, Long strategyId, Integer lvl0Nbr, Integer lvl1Nbr, Integer lvl2Nbr, Integer lvl3Nbr, Integer lvl4Nbr, Integer channelId,String attributeObj) {
        subcategoryLinePlanRepository.save(SubCategoryLineplan.builder()
                .subCategoryLineplanId(SubCategoryLineplanId.builder()
                        .planId(planId)
                        .lvl0Nbr(lvl0Nbr)
                        .lvl1Nbr(lvl1Nbr)
                        .lvl2Nbr(lvl2Nbr)
                        .lvl3Nbr(lvl3Nbr)
                        .lvl4Nbr(lvl4Nbr)
                        .strategyId(strategyId)
                        .channelId(channelId)
                        .build())
                .attributeObj(attributeObj)
                .build());
    }

    private void addStrategySubCat(Integer lvl4Nbr, StrategyMerchCatg strategyMerchCatg) {
        StrategySubCatgId strategySubCatgId = StrategySubCatgId.builder()
                .lvl4Nbr(lvl4Nbr)
                .strategyMerchCatgId(strategyMerchCatg.getStrategyMerchCatgId())
                .build();

        strategySubCatgRepository. findById(strategySubCatgId).orElseGet(()-> strategySubCatgRepository.save(StrategySubCatg.builder()
                .strategyMerchCatg(strategyMerchCatg)
                .strategySubCatgId(strategySubCatgId)
                .subCategoryLineplan(new HashSet<>())
                .strategyFinelines(new HashSet<>())
                .build()));
    }
}
