package com.walmart.aex.strategy.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.strategy.dto.LinePlanCount;
import com.walmart.aex.strategy.dto.LinePlanStrategyAttributeGrpResponse;
import com.walmart.aex.strategy.dto.LinePlanStrategyResponse;
import com.walmart.aex.strategy.dto.StrategyUpdateResponse;
import com.walmart.aex.strategy.dto.ahs.Attribute;
import com.walmart.aex.strategy.dto.ahs.AttributeObj;
import com.walmart.aex.strategy.dto.request.*;
import com.walmart.aex.strategy.dto.request.update.*;
import com.walmart.aex.strategy.enums.ChannelType;
import com.walmart.aex.strategy.exception.CustomException;
import com.walmart.aex.strategy.repository.CategoryLinePlanRepository;
import com.walmart.aex.strategy.repository.SubcategoryLinePlanRepository;
import com.walmart.aex.strategy.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Service
@Slf4j
public class LinePlanUpdateService {

    private final CategoryLinePlanRepository categoryLinePlanRepository;

    private final SubcategoryLinePlanRepository subcategoryLinePlanRepository;

    private final LinePlanStrategyService linePlanStrategyService;

    final ObjectMapper objectMapper = new ObjectMapper();

    public LinePlanUpdateService(CategoryLinePlanRepository categoryLinePlanRepository, SubcategoryLinePlanRepository subcategoryLinePlanRepository,  LinePlanStrategyService linePlanStrategyService) {
        this.categoryLinePlanRepository = categoryLinePlanRepository;
        this.subcategoryLinePlanRepository = subcategoryLinePlanRepository;
        this.linePlanStrategyService = linePlanStrategyService;
    }

    @Transactional
    public StrategyUpdateResponse updateLinePLanTargetCounts(LinePlanStrategyUpdateRequest request) {
        StrategyUpdateResponse objStrategyUpdateResponse = new StrategyUpdateResponse();
        try {

            StrategyType strategy = request.getStrategyType();
            if (strategy == null) {
                throw new CustomException("Invalid Strategy Type: " + request.getStrategyType());
            }
            Long strategyId = strategy.getStrategyId();
            if (strategyId == null) {
                throw new CustomException("Invalid Strategy ID: " + strategyId);
            }

            Optional.ofNullable(request.getAll()).ifPresent(all -> updateAll(request,all));

            Optional.ofNullable(request.getLvl1List()).ifPresent(lvl1DTO ->
                    lvl1DTO.forEach(lvl1 -> lvl1.getLvl2List().forEach(lvl2 ->
                            Optional.ofNullable(lvl2.getLvl3List()).ifPresent(lvl3List ->
                                    lvl3List.forEach(lvl3 -> {
                                        Optional.ofNullable(lvl3.getUpdatedFields()).ifPresent(updatedField -> updateCategoryLinePlan(request.getPlanId(), lvl3.getLvl3Nbr(), request.getChannel(), updatedField, lvl1.getLvl1Nbr(),lvl2.getLvl2Nbr(),request.getLvl0Nbr(),strategyId));
                                        Optional.ofNullable(lvl3.getLvl4List()).ifPresent(lvl4List ->
                                                lvl4List.forEach(lvl4 ->
                                                        Optional.ofNullable(lvl4.getUpdatedFields()).ifPresent(updatedField -> updateSubcategoryLinePlan(request.getPlanId(), lvl3.getLvl3Nbr(),lvl4.getLvl4Nbr(), request.getChannel(), updatedField,lvl1.getLvl1Nbr(),lvl2.getLvl2Nbr(),request.getLvl0Nbr(),strategyId))));
                                    })))));
            objStrategyUpdateResponse.setStatus("Updated Strategy targets successfully");
        } catch (Exception e) {
            objStrategyUpdateResponse.setStatus("Error occurred while updating targets.");
            log.error("Error occurred while updating targets.", e);
            throw e;
        }
        return objStrategyUpdateResponse;
    }


    private void updateAll(LinePlanStrategyUpdateRequest request, AllInput all) {
        long planId = request.getPlanId();
        int channelId = ChannelType.getChannelIdFromName(request.getChannel());
        Integer subCatCount = subcategoryLinePlanRepository.findCount(planId,channelId);
        Map<Integer,Long> lvl3ToLvl4Count =  mapLvl3ToLvl4Map(categoryLinePlanRepository.fetchLvl3ToLvl4Map(request.getPlanId(),channelId));

        all.getUpdatedFields().forEach(field -> {
            int value = Integer.parseInt(field.getValue());
            float rollDownValueSubCat = value / (float) subCatCount;
            List<LinePlanCount> categoryList = categoryLinePlanRepository.getCategoryAttributeStrategy(planId,channelId,request.getStrategyType().getStrategyId());
            if (field.getKey().equals("flCount")) {
                subcategoryLinePlanRepository.updateFlCountAll((int) rollDownValueSubCat, planId, channelId);
                lvl3ToLvl4Count.forEach( (lvl3Nbr, lvl4Count)->{
                    categoryLinePlanRepository.updateFlCount((int) (rollDownValueSubCat*lvl4Count), planId, channelId, lvl3Nbr);
                });
            }
            else if (field.getKey().equals("ccCount")) {
                subcategoryLinePlanRepository.updateCcCountByAll((int) rollDownValueSubCat, planId, channelId);
                lvl3ToLvl4Count.forEach( (lvl3Nbr, lvl4Count)->{
                    categoryLinePlanRepository.updateCcCount((int) (rollDownValueSubCat*lvl4Count), planId, channelId, lvl3Nbr);
                });
            }
            categoryList.forEach(category -> {
                try {
                    updateAttributeGrpCategoryLinePlan(planId,category.getLvl3Nbr(),request.getChannel(),null,request.getStrategyType().getStrategyId(),category.getLvl2Nbr(),category.getLvl1Nbr(),request.getLvl0Nbr(),field.getKey());
                    updateSubcategoryAttributeObj(planId,category.getLvl3Nbr(),channelId,request.getStrategyType().getStrategyId(),category.getLvl2Nbr(),category.getLvl1Nbr(),request.getLvl0Nbr(),field.getKey());
                } catch (JsonProcessingException e) {
                    log.error("Error While Parsing attribute Obj exception {}", e);
                }
            });


        });

    }

    private Map<Integer, Long> mapLvl3ToLvl4Map(List<Object[]> fetchLvl3ToLvl4Map) {
        Map<Integer,Long> lvl3ToLvl4Count = new HashMap<>();
        fetchLvl3ToLvl4Map.forEach(objects -> lvl3ToLvl4Count.put((Integer) objects[0], (Long) objects[1]));
        return lvl3ToLvl4Count;
    }

    private void updateSubcategoryLinePlan(Long planId,Integer lvl3Nbr, Integer lvl4Nbr, String channel, List<Field> updatedFields,Integer lvl1Nbr,Integer lvl2Nbr, Integer lvl0Nbr,Long strategyId) {
        int channelId = ChannelType.getChannelIdFromName(channel);
        updatedFields.forEach(field -> {
            int value = Integer.parseInt(field.getValue());
            if (field.getKey().equals("flCount")) {
                subcategoryLinePlanRepository.updateFlCount(value, planId, channelId, lvl4Nbr);
                Integer summedFlCount =  subcategoryLinePlanRepository.findFlCountByLvl3(planId, lvl3Nbr,channelId);
                categoryLinePlanRepository.updateFlCount(summedFlCount, planId, channelId, lvl3Nbr);
            }
            else if (field.getKey().equals("ccCount")) {
                subcategoryLinePlanRepository.updateCcCount(value, planId, channelId, lvl4Nbr);
                Integer summedCcCount =  subcategoryLinePlanRepository.findCcCountByLvl3(planId, lvl3Nbr,channelId);
                categoryLinePlanRepository.updateCcCount(summedCcCount, planId, channelId, lvl3Nbr);
            }
            try {
                updateAttributeGrpSubCategoryLinePlan(planId,lvl3Nbr,lvl4Nbr,channel,null,strategyId,lvl2Nbr,lvl1Nbr,lvl0Nbr,field.getKey());
                updateAttributeGrpCategoryLinePlan(planId,lvl3Nbr,channel,null,strategyId,lvl2Nbr,lvl1Nbr,lvl0Nbr,field.getKey());
            } catch (JsonProcessingException e) {
                log.error("Error While Parsing attribute Obj exception {}", e);
            }

        });
    }

    private void updateCategoryLinePlan(Long planId, Integer lvl3Nbr, String channel, List<Field> updatedFields, Integer lvl1Nbr, Integer lvl2Nbr, Integer lvl0Nbr, Long strategyId) {
        int channelId = ChannelType.getChannelIdFromName(channel);
        Integer subCatSize = subcategoryLinePlanRepository.findCountByLvl3(planId,lvl3Nbr,channelId);
        updatedFields.forEach(field -> {
            int value = Integer.parseInt(field.getValue());
            int rollDownValue = value / subCatSize;
            if (field.getKey().equals("flCount")) {
                categoryLinePlanRepository.updateFlCount(value, planId, channelId, lvl3Nbr);
                subcategoryLinePlanRepository.updateFlCountByLvl3(rollDownValue, planId, channelId, lvl3Nbr);
            }
            else if (field.getKey().equals("ccCount")) {
                categoryLinePlanRepository.updateCcCount(value, planId, channelId, lvl3Nbr);
                subcategoryLinePlanRepository.updateCcCountByLvl3(rollDownValue, planId, channelId, lvl3Nbr);
            }
            try {
                updateAttributeGrpCategoryLinePlan(planId,lvl3Nbr,channel,null,strategyId,lvl2Nbr,lvl1Nbr,lvl0Nbr,field.getKey());
                updateSubcategoryAttributeObj(planId,lvl3Nbr,channelId,strategyId,lvl2Nbr,lvl1Nbr,lvl0Nbr,field.getKey());
            } catch (JsonProcessingException e) {
                log.error("Error While Parsing attribute Obj exception {}", e);
            }

        });
    }


    @Transactional
    public LinePlanStrategyAttributeGrpResponse updateAttributeGroupCounts(LinePlanStrategyUpdateAttributeRequest request)throws JsonProcessingException {


        try {
            StrategyType strategy = request.getStrategyType();
            if (strategy == null) {
                throw new CustomException("Invalid Strategy Type: " + request.getStrategyType());
            }
            Long strategyId = strategy.getStrategyId();
            if (strategyId == null) {
                throw new CustomException("Invalid Strategy ID: " + strategyId);
            }

                if(request.getLvl1List()!=null) {
                    List<Lvl1AttributeGrpInput> lvl1DTO = request.getLvl1List();
                    for (Lvl1AttributeGrpInput lvl1 : lvl1DTO) {
                        List<Lvl12AttributeGrpInput> lvl2List = lvl1.getLvl2List();
                        for (Lvl12AttributeGrpInput lvl2 : lvl2List) {
                            List<Lvl13AttributeGrpInput> lvl3List = lvl2.getLvl3List();
                            Integer lvl2Number = lvl2.getLvl2Nbr();
                            if (!CollectionUtils.isEmpty(lvl3List)) {
                                for (Lvl13AttributeGrpInput lvl3 : lvl3List) {

                                    List<AttributeFields> updatedField = lvl3.getUpdatedFields();
                                    if (!CollectionUtils.isEmpty(updatedField)) {
                                        updateAttributeGrpCategoryLinePlan(request.getPlanId(), lvl3.getLvl3Nbr(), request.getChannel(), updatedField, strategyId,lvl2Number,lvl1.getLvl1Nbr(),request.getLvl0Nbr(), null);

                                    }

                                    List<Lvl14AttributeGrpInput> lvl4List = lvl3.getLvl4List();
                                    if (!CollectionUtils.isEmpty(lvl4List)) {
                                        for (Lvl14AttributeGrpInput lvl4 : lvl4List) {
                                            List<AttributeFields> updatedFields = lvl4.getUpdatedFields();
                                            if (!CollectionUtils.isEmpty(updatedFields)) {
                                                updateAttributeGrpSubCategoryLinePlan(request.getPlanId(), lvl3.getLvl3Nbr(), lvl4.getLvl4Nbr(), request.getChannel(), updatedFields, strategyId,lvl2Number,lvl1.getLvl1Nbr(),request.getLvl0Nbr(),null );
                                            }
                                        }

                                    }
                                }
                            }
                        }
                    }
                }
        }

        catch (Exception e) {
            log.error("Error occurred while updating targets.", e);
            throw e;
        }

        return new LinePlanStrategyAttributeGrpResponse().builder().planId(request.getPlanId())
                .planDesc(request.getPlanDesc()).build();

    }

    private void updateAttributeGrpCategoryLinePlan(Long planId, Integer lvl3Nbr, String channel, List<AttributeFields> updatedFields, Long strategyId,Integer lvl2Nbr, Integer lvl1Nbr, Integer lvl0Nbr, String flOrcc) throws JsonProcessingException {

        int channelId = ChannelType.getChannelIdFromName(channel);
        AtomicReference<Integer> rollDownValue = new AtomicReference<>();
        Integer subCatSize = subcategoryLinePlanRepository.findCountByLvl3(planId,lvl3Nbr,channelId);
        LinePlanCount categoryAttributes = categoryLinePlanRepository.getCategoryAttributeGrpStrategy(planId, channelId, strategyId, lvl3Nbr,lvl2Nbr,lvl1Nbr, lvl0Nbr);
        Collection<AttributeObj> attributes = objectMapper.readValue(categoryAttributes.getAttributeJson(), new TypeReference<Collection<AttributeObj>>(){});
        Integer ccCount = categoryAttributes.getCustomerChoiceCount();
        Integer flCount = categoryAttributes.getFinelineCount();

        if(CollectionUtils.isEmpty(updatedFields))
        {
            if("flCount".equalsIgnoreCase(flOrcc)){
                attributes.forEach(attribute -> {
                        attribute.getGoal().getFinelineAttribute().forEach(values -> {
                                values.setPercentage(CommonUtil.getPercentageValue(flCount,values.getCount()));
                        });
                });
            }
            else if("ccCount".equalsIgnoreCase(flOrcc)){
                attributes.forEach(attribute -> {
                    attribute.getGoal().getCustomerChoiceAttribute().forEach(values -> {
                        values.setPercentage(CommonUtil.getPercentageValue(ccCount,values.getCount()));
                    });
                });
            }

            String updatedJSONtring = objectMapper.writeValueAsString(attributes);
            categoryLinePlanRepository.updateAttributeGroup(planId, channelId, lvl3Nbr, lvl2Nbr, strategyId, lvl1Nbr, lvl0Nbr, updatedJSONtring);
        }

        else {
            for (AttributeFields field : updatedFields) {

                if ("finelineAttribute".equalsIgnoreCase(field.getAttribute())) {
                    attributes.forEach(attribute -> {
                        if (attribute.getAttributeGroupName().equals(field.getAttributeGroupName())) {
                            rollDownValue.set(field.getCount() / subCatSize);
                            attribute.getGoal().getFinelineAttribute().forEach(values -> {
                                if (values.getName().equals(field.getName())) {
                                    values.setCount(field.getCount());
                                    values.setPercentage(CommonUtil.getPercentageValue(flCount, field.getCount()));
                                }
                            });
                        }
                    });
                } else if ("customerChoiceAttribute".equalsIgnoreCase(field.getAttribute())) {
                    attributes.forEach(attribute -> {
                        if (attribute.getAttributeGroupName().equals(field.getAttributeGroupName())) {
                            rollDownValue.set(field.getCount() / subCatSize);
                            attribute.getGoal().getCustomerChoiceAttribute().forEach(values -> {
                                if (values.getName().equals(field.getName())) {
                                    values.setCount(field.getCount());
                                    values.setPercentage(CommonUtil.getPercentageValue(ccCount, field.getCount()));
                                }
                            });
                        }
                    });
                }

                String updatedJSONtring = objectMapper.writeValueAsString(attributes);
                categoryLinePlanRepository.updateAttributeGroup(planId, channelId, lvl3Nbr, lvl2Nbr, strategyId, lvl1Nbr, lvl0Nbr, updatedJSONtring);
                updateAttributeGrpRollUporRollDown(rollDownValue, planId, channelId, lvl3Nbr, field, strategyId, lvl2Nbr, lvl1Nbr, lvl0Nbr);
            }
        }

    }

    private void updateAttributeGrpSubCategoryLinePlan(Long planId, Integer lvl3Nbr,Integer lvl4Nbr, String channel, List<AttributeFields> updatedFields,Long strategyId, Integer lvl2Nbr, Integer lvl1Nbr, Integer lvl0Nbr,String flOrcc) throws JsonProcessingException {

        int channelId = ChannelType.getChannelIdFromName(channel);
        LinePlanCount categoryAttributes = subcategoryLinePlanRepository.getSubcategoryAttributeGrpStrategy(planId, channelId, strategyId, lvl3Nbr, lvl4Nbr, lvl2Nbr, lvl1Nbr,lvl0Nbr);
        Integer flCount = categoryAttributes.getFinelineCount();
        Integer ccCount = categoryAttributes.getCustomerChoiceCount();
        Collection<AttributeObj> attributes = objectMapper.readValue(categoryAttributes.getAttributeJson(), new TypeReference<Collection<AttributeObj>>(){});

        if(CollectionUtils.isEmpty(updatedFields))
        {
            if("flCount".equalsIgnoreCase(flOrcc)){
                attributes.forEach(attribute -> {
                    attribute.getGoal().getFinelineAttribute().forEach(values -> {
                        values.setPercentage(CommonUtil.getPercentageValue(flCount,values.getCount()));
                    });
                });
            }
            else if("ccCount".equalsIgnoreCase(flOrcc)){
                attributes.forEach(attribute -> {
                    attribute.getGoal().getCustomerChoiceAttribute().forEach(values -> {
                        values.setPercentage(CommonUtil.getPercentageValue(ccCount,values.getCount()));
                    });
                });
            }

            String updatedJSONtring = getProductAttributeString(attributes);
            Optional.ofNullable(updatedJSONtring)
                    .ifPresent(JsonString -> subcategoryLinePlanRepository.updateSubCategoryAttributeGroup(planId, channelId, lvl3Nbr, lvl4Nbr, strategyId, lvl2Nbr, lvl1Nbr, lvl0Nbr, JsonString));

        }
        else {
            for (AttributeFields field : updatedFields) {

                if ("finelineAttribute".equalsIgnoreCase(field.getAttribute())) {

                    attributes.stream().forEach(attribute -> {
                        if (attribute.getAttributeGroupName().equals(field.getAttributeGroupName())) {
                            attribute.getGoal().getFinelineAttribute().stream().forEach(values -> {
                                if (values.getName().equals(field.getName())) {
                                    values.setCount(field.getCount());
                                    values.setPercentage(CommonUtil.getPercentageValue(flCount, field.getCount()));

                                }


                            });
                        }
                    });
                } else if ("customerChoiceAttribute".equalsIgnoreCase(field.getAttribute())) {
                    attributes.stream().forEach(attribute -> {
                        if (attribute.getAttributeGroupName().equals(field.getAttributeGroupName())) {
                            attribute.getGoal().getCustomerChoiceAttribute().stream().forEach(values -> {
                                if (values.getName().equals(field.getName())) {
                                    values.setCount(field.getCount());
                                    values.setPercentage(CommonUtil.getPercentageValue(ccCount, field.getCount()));
                                }

                            });
                        }
                    });
                }

                String updatedJSONtring = objectMapper.writeValueAsString(attributes);
                subcategoryLinePlanRepository.updateSubCategoryAttributeGroup(planId, channelId, lvl3Nbr, lvl4Nbr, strategyId, lvl2Nbr, lvl1Nbr, lvl0Nbr, updatedJSONtring);
                updateAttributeGrpRollUporRollDown(null, planId, channelId, lvl3Nbr, field, strategyId, lvl2Nbr, lvl1Nbr, lvl0Nbr);
            }
        }


    }

    private void updateAttributeGrpRollUporRollDown(AtomicReference<Integer> rollDownValue, Long planId, int channelId, Integer lvl3Nbr, AttributeFields field, Long strategyId, Integer lvl2Nbr, Integer lvl1Nbr, Integer lvl0Nbr)throws JsonProcessingException
    {
       List<LinePlanCount>  categoryAttributes = subcategoryLinePlanRepository.getSubcategoryAttributeGrpStrategy(planId, channelId, strategyId,lvl3Nbr, lvl2Nbr,lvl1Nbr, lvl0Nbr);
       AtomicReference<Integer> rollUpValue = new AtomicReference<>();
        for(LinePlanCount linePlanCount:categoryAttributes) {

            Integer lvl4Nbr = linePlanCount.getLvl4Nbr();
            Integer ccCount = linePlanCount.getCustomerChoiceCount();
            Integer flCount = linePlanCount.getFinelineCount();
            String attributeJson = linePlanCount.getAttributeJson();
            Collection<AttributeObj> attributes = objectMapper.readValue(attributeJson, new TypeReference<Collection<AttributeObj>>() {
                });
            attributes.forEach(attribute -> {

                if (attribute.getAttributeGroupName().equals(field.getAttributeGroupName())) {
                    if ("finelineAttribute".equalsIgnoreCase(field.getAttribute())) {
                        attribute.getGoal().getFinelineAttribute().forEach(values -> {
                            updateCountValue(values,field,rollDownValue,rollUpValue, flCount);
                        });
                    } else if ("customerChoiceAttribute".equalsIgnoreCase(field.getAttribute())) {
                        attribute.getGoal().getCustomerChoiceAttribute().forEach(values -> {
                            updateCountValue(values,field,rollDownValue,rollUpValue, ccCount);
                        });
                    }

                }
            });
            if (rollDownValue != null) {
                String updatedJSONtring = objectMapper.writeValueAsString(attributes);
                subcategoryLinePlanRepository.updateSubCategoryAttributeGroup(planId, channelId, lvl3Nbr, lvl4Nbr,strategyId,lvl2Nbr,lvl1Nbr,lvl0Nbr, updatedJSONtring);
            }


        }
        if(rollUpValue.get() != null){
            LinePlanCount category =  categoryLinePlanRepository.getCategoryAttributeGrpStrategy(planId, channelId, strategyId,lvl3Nbr,lvl2Nbr, lvl1Nbr,lvl0Nbr);
            String categoryAttributeJson = category.getAttributeJson();

            Integer flCount = category.getFinelineCount();
            Integer ccCount = category.getCustomerChoiceCount();
            Collection<AttributeObj>  attributes = objectMapper.readValue(categoryAttributeJson, new TypeReference<Collection<AttributeObj>>(){});
            attributes.forEach(attribute -> {
                if(attribute.getAttributeGroupName().equals(field.getAttributeGroupName())){
                    if("finelineAttribute".equalsIgnoreCase(field.getAttribute())){
                        attribute.getGoal().getFinelineAttribute().forEach(values -> {
                            if (values.getName().equals(field.getName())) {
                                values.setCount(rollUpValue.get());
                                values.setPercentage(CommonUtil.getPercentageValue(flCount,rollUpValue.get()));
                                log.info("values category percentage:" + values.getPercentage());
                            }

                        });
                    }
                    else if("customerChoiceAttribute".equalsIgnoreCase(field.getAttribute())){
                        attribute.getGoal().getCustomerChoiceAttribute().forEach(values -> {
                            if (values.getName().equals(field.getName())) {
                                values.setCount(rollUpValue.get());
                               values.setPercentage(CommonUtil.getPercentageValue(ccCount,rollUpValue.get()));
                            }
                        });
                    }
                }
            });
            String updatedJSONtring = objectMapper.writeValueAsString(attributes);
            categoryLinePlanRepository.updateAttributeGroup(planId, channelId,lvl3Nbr,lvl2Nbr,strategyId,lvl1Nbr,lvl0Nbr,updatedJSONtring);

        }
    }

    private  void updateCountValue(Attribute values, AttributeFields field,AtomicReference<Integer> rollDownValue, AtomicReference<Integer> rollUpValue,Integer totalCount)
    {
        if (values.getName().equals(field.getName())) {

            if (rollDownValue != null) {
                values.setCount(rollDownValue.get());
                //values.setPercentage((rollDownValue.get()/totalCount)*100);
                values.setPercentage(CommonUtil.getPercentageValue(totalCount,rollDownValue.get()));
            }
            else
            {
                if (rollUpValue.get() == null)
                    rollUpValue.set(values.getCount());
                else
                    rollUpValue.set(rollUpValue.get() + values.getCount());

            }

        }
    }

    private void updateSubcategoryAttributeObj(Long planId, Integer lvl3Nbr, int channelId,Long strategyId, Integer lvl2Nbr, Integer lvl1Nbr, Integer lvl0Nbr, String flOrcc){
        log.info("updateing subcategory::" );

        List<LinePlanCount>  categoryAttributes = subcategoryLinePlanRepository.getSubcategoryAttributeGrpStrategy(planId, channelId, strategyId,lvl3Nbr, lvl2Nbr,lvl1Nbr, lvl0Nbr);
        for(LinePlanCount linePlanCount:categoryAttributes) {

            Integer lvl4Nbr = linePlanCount.getLvl4Nbr();
            Integer ccCount = linePlanCount.getCustomerChoiceCount();
            Integer flCount = linePlanCount.getFinelineCount();
            String attributeJson = linePlanCount.getAttributeJson();
            Collection<AttributeObj> attributeObjs = getAttributeObj(attributeJson);
            Optional.ofNullable(attributeObjs).ifPresent(attributeObj -> attributeObj.forEach(attributes ->{
                if("flCount".equalsIgnoreCase(flOrcc)){
                    attributes.getGoal().getFinelineAttribute().forEach(values -> {
                        values.setPercentage(CommonUtil.getPercentageValue(flCount,values.getCount()));
                    });
                }
                else if("ccCount".equalsIgnoreCase(flOrcc)){
                    attributes.getGoal().getCustomerChoiceAttribute().forEach(values -> {
                        values.setPercentage(CommonUtil.getPercentageValue(ccCount,values.getCount()));
                    });
                }
            }));

            String updatedJSONtring = getProductAttributeString(attributeObjs);
            Optional.ofNullable(updatedJSONtring)
                    .ifPresent(JsonString ->subcategoryLinePlanRepository.updateSubCategoryAttributeGroup(planId, channelId, lvl3Nbr, lvl4Nbr,strategyId,lvl2Nbr,lvl1Nbr,lvl0Nbr, JsonString));

           // subcategoryLinePlanRepository.updateSubCategoryAttributeGroup(planId, channelId, lvl3Nbr, lvl4Nbr,strategyId,lvl2Nbr,lvl1Nbr,lvl0Nbr, updatedJSONtring);

        }
    }

    private Collection<AttributeObj> getAttributeObj(String attributeObj) {
        Collection<AttributeObj> attributeObjects = null;
        try {
            if (StringUtils.isNotEmpty(attributeObj)) {
                attributeObjects = Optional.ofNullable(objectMapper.readValue(attributeObj,
                        new TypeReference<Collection<AttributeObj>>() {})).orElse(null);
            }
        } catch (JsonProcessingException e) {
            log.error("Error While Parsing Product Obj {} exception {}", e);
        }
        return attributeObjects;
    }

    private String getProductAttributeString(Collection<AttributeObj> attributeObjects) {
        String attributeObj = null;
        try {
            if (null != attributeObjects) {
                attributeObj = objectMapper.writeValueAsString(attributeObjects);
            }
        } catch (JsonProcessingException e) {
            log.error("Error while converting Attribute Obj to String - exception {}", e);
        }
        return attributeObj;
    }

}
