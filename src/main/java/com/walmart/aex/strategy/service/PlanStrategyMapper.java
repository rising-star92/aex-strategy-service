package com.walmart.aex.strategy.service;

import com.walmart.aex.strategy.dto.request.*;
import com.walmart.aex.strategy.entity.*;
import com.walmart.aex.strategy.enums.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;


import java.util.*;

@Service
@Slf4j
public class PlanStrategyMapper {

    /**
     * This method creates Merch Catg (lvl3) into Strategy Merch Catg Table based on CLP data events.
     *
     * @param lvl3s - lvl3
     * @param planStrategyId - This is planStrategyId
     * @param request - request
     * @param lvl1 - lvl1
     * @param lvl2 - lvl2
     * @return StrategyMerchCatg - StrategyMerchCatg
     */

    public Set<StrategyMerchCatg> setStrategyMerchCatg(PlanStrategy planStrategy, List<Lvl3> lvl3s, PlanStrategyId planStrategyId, PlanStrategyDTO request,
                                                       Lvl1 lvl1, Lvl2 lvl2) {
        Set<StrategyMerchCatg> strategyMerchCatgs = Optional.ofNullable(planStrategy.getStrategyMerchCatgs())
                .orElse(new HashSet<>());
        for (Lvl3 lvl3 : lvl3s) {
            StrategyMerchCatgId strategyMerchCatgId = new StrategyMerchCatgId(planStrategyId,
                    request.getLvl0Nbr(), lvl1.getLvl1Nbr(), lvl2.getLvl2Nbr(), lvl3.getLvl3Nbr());
            log.info("Check if a strategyMerchCatg Id : {} already exists or not", strategyMerchCatgId.toString());
            StrategyMerchCatg strategyMerchCatg = Optional.of(strategyMerchCatgs)
                    .stream()
                    .flatMap(Collection::stream).filter(strategyMerchCatg1 -> strategyMerchCatg1.getStrategyMerchCatgId().equals(strategyMerchCatgId))
                    .findFirst()
                    .orElse(new StrategyMerchCatg());
            if (strategyMerchCatg.getStrategyMerchCatgId() == null) {
                strategyMerchCatg.setStrategyMerchCatgId(strategyMerchCatgId);
            }
            strategyMerchCatg.setChannelId(ChannelType.getChannelIdFromName(lvl3.getChannel()));
            if (!CollectionUtils.isEmpty(lvl3.getLvl4List())) {
                strategyMerchCatg.setStrategySubCatgs(setStrategySubCatg(strategyMerchCatg, lvl3.getLvl4List(),
                        request.getLvl0Name(), lvl1.getLvl1Name(), lvl2.getLvl2Name(), lvl3.getLvl3Name()));
            }
            strategyMerchCatgs.add(strategyMerchCatg);
        }
        return strategyMerchCatgs;
    }

    /**
     * This method creates Sub Catg into Strategy SubCatg Table based on CLP data events.
     * @param strategyMerchCatg - strategyMerchCatg
     * @param lvl4s - lvl4s
     * @param lvl0Name - lvl0Name
     * @param lvl1Name - lvl1Name
     * @param lvl2Name - lvl2Name
     * @param lvl3Name - lvl3Name
     * @return StrategySubCatg - StrategySubCatg
     */
    private Set<StrategySubCatg> setStrategySubCatg(StrategyMerchCatg strategyMerchCatg, List<Lvl4> lvl4s, String lvl0Name,
                                                    String lvl1Name, String lvl2Name, String lvl3Name) {
        Set<StrategySubCatg> strategySubCatgs = Optional.ofNullable(strategyMerchCatg.getStrategySubCatgs())
                .orElse(new HashSet<>());
        for (Lvl4 lvl4 : lvl4s) {
            StrategySubCatgId strategySubCatgId = new StrategySubCatgId(strategyMerchCatg.getStrategyMerchCatgId(), lvl4.getLvl4Nbr());
            log.info("Check if a strategySubCatg Id : {} already exists or not", strategySubCatgId.toString());
            StrategySubCatg strategySubCatg = Optional.of(strategySubCatgs)
                    .stream()
                    .flatMap(Collection::stream).filter(strategySubCatg1 -> strategySubCatg1.getStrategySubCatgId().equals(strategySubCatgId))
                    .findFirst()
                    .orElse(new StrategySubCatg());
            if (strategySubCatg.getStrategySubCatgId() == null) {
                strategySubCatg.setStrategySubCatgId(strategySubCatgId);
            }
            strategySubCatg.setChannelId(ChannelType.getChannelIdFromName(lvl4.getChannel()));
            if (!CollectionUtils.isEmpty(lvl4.getFinelines())) {
                strategySubCatg.setStrategyFinelines(setStrategyFineline(strategySubCatg, lvl4.getFinelines(), lvl0Name, lvl1Name, lvl2Name, lvl3Name, lvl4.getLvl4Name()));
            }
            strategySubCatgs.add(strategySubCatg);
        }
        return strategySubCatgs;
    }

    /**
     * This method creates Finelines into Strategy Fineline Tabls based on CLP data events.
     * @param strategySubCatg - strategySubCatg
     * @param finelines - finelines
     * @param lvl0Name - lvl0Name
     * @param lvl1Name - lvl1Name
     * @param lvl2Name - lvl2Name
     * @param lvl3Name - lvl3Name
     * @param lvl4Name - lvl4Name
     * @return StrategyFineline - StrategyFineline
     */

    private Set<StrategyFineline> setStrategyFineline(StrategySubCatg strategySubCatg, List<Fineline> finelines,
                                                      String lvl0Name, String lvl1Name, String lvl2Name, String lvl3Name, String lvl4Name) {

        Set<StrategyFineline> strategyFinelines = Optional.ofNullable(strategySubCatg.getStrategyFinelines())
                .orElse(new HashSet<>());
        for (Fineline fineline : finelines) {
            StrategyFinelineId strategyFinelineId = new StrategyFinelineId(strategySubCatg.getStrategySubCatgId(),
                    fineline.getFinelineNbr());
            log.info("Check if a strategyFineline Id : {} already exists or not", strategyFinelineId.toString());
            StrategyFineline strategyFineline = Optional.of(strategyFinelines)
                    .stream()
                    .flatMap(Collection::stream).filter(strategyFineline1 -> strategyFineline1.getStrategyFinelineId().equals(strategyFinelineId))
                    .findFirst()
                    .orElse(new StrategyFineline());
            if (strategyFineline.getStrategyFinelineId() == null) {
                strategyFineline.setStrategyFinelineId(strategyFinelineId);
                strategyFineline.setAllocTypeCode(AllocRunType.LOCKED.getId());
                strategyFineline.setRunStatusCode(RunStatusType.HIDE.getId());
                strategyFineline.setRfaStatusCode(RfaStatusType.RFA_NOT_RUN.getId());
            }
            strategyFineline.setChannelId(ChannelType.getChannelIdFromName(fineline.getChannel()));
            strategyFineline.setFinelineDesc(Optional.ofNullable(fineline.getFinelineName()).orElse(null));
            strategyFineline.setAltFinelineName(Optional.ofNullable(fineline.getAltFinelineName()).orElse(null));
            strategyFineline.setLvl0GenDesc1(lvl0Name);
            strategyFineline.setOutFitting(Optional.ofNullable(fineline.getOutFitting()).orElse(null));
            strategyFineline.setLvl1GenDesc1(lvl1Name);
            strategyFineline.setLvl2GenDesc1(lvl2Name);
            strategyFineline.setLvl3GenDesc1(lvl3Name);
            strategyFineline.setLvl4GenDesc1(lvl4Name);
            strategyFineline.setTraitChoiceCode(TraitChoiceType.getTraitChoiceCodeFromName(
                    Optional.ofNullable(fineline.getTraitChoice()).orElse(null)));
            if (!CollectionUtils.isEmpty(fineline.getStyles())) {
                strategyFineline.setStrategyStyles(setStyles(strategyFineline, fineline.getStyles()));
            }
            strategyFinelines.add(strategyFineline);
        }
        return strategyFinelines;
    }

    /**
     * This method creates Styles into Strategy Style Tables based on CLP data events.
     * @param strategyFineline - strategyFineline
     * @param styles - styles
     * @return StrategyStyle - StrategyStyle
     */

    private Set<StrategyStyle> setStyles(StrategyFineline strategyFineline, List<Style> styles) {
        Set<StrategyStyle> strategyStyles = Optional.ofNullable(strategyFineline.getStrategyStyles())
                .orElse(new HashSet<>());
        for (Style style : styles) {
            StrategyStyleId strategyStyleId = new StrategyStyleId(strategyFineline.getStrategyFinelineId(), style.getStyleNbr());
            log.info("Check if a strategyStyle Id : {} already exists or not", strategyStyleId.toString());
            StrategyStyle strategyStyle = Optional.of(strategyStyles)
                    .stream()
                    .flatMap(Collection::stream).filter(strategyStyle1 -> strategyStyle1.getStrategyStyleId().equals(strategyStyleId))
                    .findFirst()
                    .orElse(new StrategyStyle());
            if (strategyStyle.getStrategyStyleId() == null) {
                strategyStyle.setStrategyStyleId(strategyStyleId);
            }
            strategyStyle.setChannelId(ChannelType.getChannelIdFromName(style.getChannel()));
            strategyStyle.setAltStyleDesc(Optional.ofNullable(style.getAltStyleDesc()).orElse(null));
            if (!CollectionUtils.isEmpty(style.getCustomerChoices())) {
                strategyStyle.setStrategyCcs(setCcs(strategyStyle, style.getCustomerChoices()));
            }
            strategyStyles.add(strategyStyle);
        }
        return strategyStyles;
    }

    /**
     * This method creates CCs into Strategy CC Tables based on CLP data events.
     * @param strategyStyle - strategyStyle
     * @param customerChoices - customerChoices
     * @return StrategyCc - StrategyCc
     */
    private Set<StrategyCc> setCcs(StrategyStyle strategyStyle, List<CustomerChoice> customerChoices) {
        Set<StrategyCc> strategyCcs = Optional.ofNullable(strategyStyle.getStrategyCcs()).orElse(new HashSet<>());
        for (CustomerChoice cc : customerChoices) {
            StrategyCcId strategyCcId = new StrategyCcId(strategyStyle.getStrategyStyleId(), cc.getCcId());
            log.info("Check if a strategyCc Id : {} already exists or not", strategyCcId.toString());
            StrategyCc strategyCc = Optional.of(strategyCcs)
                    .stream()
                    .flatMap(Collection::stream).filter(strategyCc1 -> strategyCc1.getStrategyCcId().equals(strategyCcId))
                    .findFirst()
                    .orElse(new StrategyCc());
            if (strategyCc.getStrategyCcId() == null) {
                strategyCc.setStrategyCcId(strategyCcId);
            }
            strategyCc.setAltCcDesc(Optional.ofNullable(cc.getAltCcDesc()).orElse(null));
            strategyCc.setColorName(cc.getColorName());
            strategyCc.setChannelId(ChannelType.getChannelIdFromName(cc.getChannel()));
            strategyCc.setColorFamily(cc.getColorFamily());
            strategyCcs.add(strategyCc);
        }
        return strategyCcs;
    }

}
