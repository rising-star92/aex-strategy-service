package com.walmart.aex.strategy.util;

import com.walmart.aex.strategy.dto.PlanStrategyRequest;
import com.walmart.aex.strategy.dto.PlanStrategyResponse;
import com.walmart.aex.strategy.dto.kafka.ChangeScope;
import com.walmart.aex.strategy.dto.kafka.Headers;
import com.walmart.aex.strategy.dto.request.*;
import com.walmart.aex.strategy.entity.StrategyFinelineFixture;
import com.walmart.aex.strategy.enums.EventType;
import com.walmart.aex.strategy.producer.StrategyProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class KafkaUtil {

    @Async
    public static void postMessage(PlanStrategyRequest request, PlanStrategyResponse planStrategyResponse, StrategyProducer strategyProducer,
                                   EventType eventType, Set<String> updatedFieldRequest) {
        log.info("Posting update event to kafka for planId:{} & updatedField: {}", planStrategyResponse.getPlanId(),
                updatedFieldRequest.stream().filter(StringUtils::isNotBlank).collect(Collectors.joining(", ")));
        Headers headers = new Headers();
        ChangeScope changeScope = new ChangeScope();
        StrongKey strongKey = getStrongKeyFromResponse(planStrategyResponse, request);
        changeScope.setStrongKeys(strongKey);
        changeScope.setUpdatedAttributes(updatedFieldRequest);
        headers.setPlanId(Optional.ofNullable(planStrategyResponse.getPlanId()).orElse(null));
        headers.setChangeScope(changeScope);
        strategyProducer.postStrategyChangesToKafka(planStrategyResponse, headers, eventType, planStrategyResponse.getPlanId());
    }

    private static StrongKey getStrongKeyFromResponse(PlanStrategyResponse planStrategyResponse, PlanStrategyRequest request) {
        log.info("set the strong key for kafka headers for planId:{}", planStrategyResponse.getPlanId());
        StrongKey strongKey = new StrongKey();
        strongKey.setPlanId(Optional.ofNullable(planStrategyResponse.getPlanId()).orElse(null));
        strongKey.setLvl0Nbr(Optional.ofNullable(planStrategyResponse.getLvl0Nbr()).orElse(null));
        strongKey.setLvl1Nbr(Optional.ofNullable(planStrategyResponse.getLvl1Nbr()).orElse(null));
        strongKey.setLvl2Nbr(Optional.ofNullable(planStrategyResponse.getLvl2Nbr()).orElse(null));
        if (!CollectionUtils.isEmpty(planStrategyResponse.getLvl3List())) {
            setLvl3Onwards(strongKey, request);
        }
        return strongKey;

    }

    private static void setLvl3Onwards(StrongKey strongKey, PlanStrategyRequest request) {
        for (Lvl3 lvl3 : request.getLvl3List()) {
            strongKey.setLvl3Nbr(lvl3.getLvl3Nbr());
            if (!CollectionUtils.isEmpty(lvl3.getLvl4List())) {
                for (Lvl4 lvl4 : lvl3.getLvl4List()) {
                    strongKey.setLvl4Nbr(lvl4.getLvl4Nbr());
                    if (!CollectionUtils.isEmpty(lvl4.getFinelines())) {
                        for (Fineline fineline : lvl4.getFinelines()) {
                            Fineline skFineline = new Fineline();
                            skFineline.setFinelineNbr(fineline.getFinelineNbr());
                            if (!CollectionUtils.isEmpty(fineline.getStyles())) {
                                for (Style style: fineline.getStyles()){
                                    Style skStyle = new Style();
                                    skStyle.setStyleNbr(style.getStyleNbr());
                                    if (!CollectionUtils.isEmpty(style.getCustomerChoices())){
                                        for (CustomerChoice cc: style.getCustomerChoices()){
                                            CustomerChoice skCc = new CustomerChoice();
                                            skCc.setCcId(cc.getCcId());
                                            skStyle.setCustomerChoices(Collections.singletonList(skCc));
                                        }
                                    }
                                    skFineline.setStyles(Collections.singletonList(skStyle));
                                }
                            }
                            strongKey.setFineline(skFineline);
                        }
                    }
                }
            }
        }
    }

    public static void generateKafkaMessage(PlanStrategyResponse response, List<StrategyFinelineFixture> finelineFixtureList,
                                            StrategyProducer strategyProducer, Integer fineline) {
        StrongKey strongKey = generateStrongKey(finelineFixtureList, fineline);
        Headers headers = new Headers();
        ChangeScope changeScope = new ChangeScope();
        Set<String> updatedFieldRequest = new HashSet<>();
        updatedFieldRequest.add(Constant.MIN);
        updatedFieldRequest.add(Constant.MAX);
        changeScope.setStrongKeys(strongKey);
        changeScope.setUpdatedAttributes(updatedFieldRequest);
        headers.setPlanId(Optional.ofNullable(strongKey.getPlanId()).orElse(null));
        headers.setChangeScope(changeScope);
        strategyProducer.postStrategyChangesToKafka(response, headers, EventType.UPDATE, strongKey.getPlanId());
    }

    private static StrongKey generateStrongKey(List<StrategyFinelineFixture> finelineFixtureList, Integer fl) {
        StrongKey strongKey = new StrongKey();
        Long planId = finelineFixtureList
                .stream()
                .map(strategyFinelineFixture -> strategyFinelineFixture.getStrategyFinelineFixtureId().getStrategySubCatgFixtureId().getStrategyMerchCatgFixtureId().getStrategyMerchCatgId().getPlanStrategyId().getPlanId())
                .findFirst()
                .orElse(null);
        Integer lvl0Nbr = finelineFixtureList
                .stream()
                .map(strategyFinelineFixture -> strategyFinelineFixture.getStrategyFinelineFixtureId().getStrategySubCatgFixtureId().getStrategyMerchCatgFixtureId().getStrategyMerchCatgId().getLvl0Nbr())
                .findFirst()
                .orElse(null);
        Integer lvl1Nbr = finelineFixtureList
                .stream()
                .map(strategyFinelineFixture -> strategyFinelineFixture.getStrategyFinelineFixtureId().getStrategySubCatgFixtureId().getStrategyMerchCatgFixtureId().getStrategyMerchCatgId().getLvl1Nbr())
                .findFirst()
                .orElse(null);
        Integer lvl2Nbr = finelineFixtureList
                .stream()
                .map(strategyFinelineFixture -> strategyFinelineFixture.getStrategyFinelineFixtureId().getStrategySubCatgFixtureId().getStrategyMerchCatgFixtureId().getStrategyMerchCatgId().getLvl2Nbr())
                .findFirst()
                .orElse(null);
        Integer lvl3Nbr = finelineFixtureList
                .stream()
                .map(strategyFinelineFixture -> strategyFinelineFixture.getStrategyFinelineFixtureId().getStrategySubCatgFixtureId().getStrategyMerchCatgFixtureId().getStrategyMerchCatgId().getLvl3Nbr())
                .findFirst()
                .orElse(null);
        Integer lvl4Nbr = finelineFixtureList
                .stream()
                .map(strategyFinelineFixture -> strategyFinelineFixture.getStrategyFinelineFixtureId().getStrategySubCatgFixtureId().getLvl4Nbr())
                .findFirst()
                .orElse(null);
        strongKey.setPlanId(planId);
        strongKey.setLvl0Nbr(lvl0Nbr);
        strongKey.setLvl1Nbr(lvl1Nbr);
        strongKey.setLvl2Nbr(lvl2Nbr);
        strongKey.setLvl2Nbr(lvl2Nbr);
        strongKey.setLvl3Nbr(lvl3Nbr);
        strongKey.setLvl4Nbr(lvl4Nbr);
        Fineline fineline = new Fineline();
        fineline.setFinelineNbr(fl);
        strongKey.setFineline(fineline);
        return strongKey;
    }

}
