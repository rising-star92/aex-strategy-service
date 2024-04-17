package com.walmart.aex.strategy.util;

import com.walmart.aex.strategy.dto.PlanStrategyRequest;
import com.walmart.aex.strategy.dto.PlanStrategyResponse;
import com.walmart.aex.strategy.dto.request.*;
import com.walmart.aex.strategy.entity.*;

import java.util.*;

public class FinelineRFALockingTestUtil {
    public static List<AllocationRunTypeText> getAllocationRunTypeTextEntity(){
        List<AllocationRunTypeText> allocationRunTypeText = new ArrayList<>();
        AllocationRunTypeText runTypeText;
        for(int i=0; i<2;i++){
            runTypeText = new AllocationRunTypeText();
            runTypeText.setAllocRunTypeCode(i);
            runTypeText.setAllocRunTypeDesc("Desc"+i);

            allocationRunTypeText.add(runTypeText);
        }
        return allocationRunTypeText;
    }

    public static List<StrategyFineline> getFinelines(){
        List<StrategyFineline> finelines = new ArrayList<>();

        //Creating category 1
        finelines.add(getFineline(10000, 1000, 100));
        finelines.add(getFineline(10000, 1000, 101));
        finelines.add(getFineline(10000, 1001, 200));
        //Creating category 2
        finelines.add(getFineline(10001, 2000, 300));
        finelines.add(getFineline(10001, 2001, 400));
        finelines.add(getFineline(10001, 2001, 401));

        return finelines;
    }

    private static StrategyFineline getFineline(Integer cat, Integer subCat, Integer fineLineNbr){
        StrategyFineline fineline=  new StrategyFineline();
        fineline.setStrategyFinelineId(StrategyFinelineId.builder().finelineNbr(fineLineNbr).
                strategySubCatgId(StrategySubCatgId.builder().lvl4Nbr(subCat).
                        strategyMerchCatgId(StrategyMerchCatgId.builder().lvl3Nbr(cat).
                                lvl2Nbr(3669).lvl1Nbr(23).lvl0Nbr(50000).
                                planStrategyId(PlanStrategyId.builder().planId(Long.getLong("1")).strategyId(Long.getLong("6")).build()).
                                build()).build()).build());
        fineline.setRfaStatusCode(null);
        fineline.setAllocTypeCode(null);
        fineline.setRunStatusCode(null);
        return fineline;
    }

    public static PlanStrategyRequest createRequest(Long planId, List<String> selectedFinelines, HashMap<String, Integer> fields, boolean isall){
        PlanStrategyRequest request = new PlanStrategyRequest();
        request.setPlanId(planId);
        List<Lvl3> lvl3List = Optional.ofNullable(request.getLvl3List()).orElse(new ArrayList<>());
        List<Field> runRfaStatus = new ArrayList<>();
        UpdatedFields updatedFields = new UpdatedFields();
        fields.forEach((key, value)->{
            Field rfaStatus = new Field();
            rfaStatus.setKey(key);
            rfaStatus.setValue(value.toString());
            runRfaStatus.add(rfaStatus);
        });
        updatedFields.setRunRfaStatus(runRfaStatus);

        if(isall) {
            UpdateAll all = new UpdateAll();
            all.setUpdatedFields(updatedFields);
            request.setAll(all);
        }

        selectedFinelines.forEach(value->{
            String[] keyIds = value.split(",", 3);
            Integer lvl3Nbr = Integer.parseInt(keyIds[0]);
            Integer lvl4Nbr = Integer.parseInt(keyIds[1]);
            Integer flNbr = Integer.parseInt(keyIds[2]);
            lvl3List.stream().filter(lvl3 -> lvl3.getLvl3Nbr().equals(lvl3Nbr))
                    .findFirst().
                    ifPresentOrElse((lvl3) -> lvl3.getLvl4List().stream().filter(lvl4 -> lvl4.getLvl4Nbr().equals(lvl4Nbr))
                                    .findFirst().
                                    ifPresentOrElse(lvl4 -> lvl4.getFinelines().add(setfineline(flNbr, !isall?updatedFields:null)), ()->setLevel4(lvl4Nbr, flNbr, !isall?updatedFields:null, Optional.ofNullable(lvl3.getLvl4List()).orElse(new ArrayList<>()))),
                            ()->setLevel3(lvl3Nbr, lvl4Nbr, flNbr, !isall?updatedFields:null, lvl3List));
                    });
        request.setLvl3List(lvl3List);
        return  request;
    }

    private static void setLevel3(Integer lvl3Nbr, Integer lvl4Nbr, Integer flNbr, UpdatedFields fields, List<Lvl3> lvl3List){
        Lvl3 lv3 = new Lvl3();
        lv3.setLvl3Nbr(lvl3Nbr);
        List<Lvl4> lvl4List = new ArrayList<>();
        lv3.setLvl4List(setLevel4(lvl4Nbr, flNbr, fields, lvl4List));
        lvl3List.add(lv3);
    }

    private static List<Lvl4> setLevel4(Integer lvl4Nbr,Integer flNbr, UpdatedFields fields, List<Lvl4> lvl4List){
        Lvl4 lv4 = new Lvl4();
        lv4.setLvl4Nbr(lvl4Nbr);
        List<Fineline> finelines = new ArrayList<>();
        finelines.add(setfineline(flNbr, fields));
        lv4.setFinelines(finelines);
        lvl4List.add(lv4);
        return lvl4List;
    }

    private static Fineline setfineline(Integer flNbr, UpdatedFields fields){
        Fineline fl = new Fineline();
        fl.setFinelineNbr(flNbr);
        fl.setUpdatedFields(fields);
        return fl;
    }

    public static Fineline getFineline(PlanStrategyResponse response, Integer cat, Integer subCat, Integer flNbr){
        return Optional.ofNullable(response.getLvl3List())
                .stream()
                .flatMap(Collection::stream)
                .filter(lvl3 -> lvl3.getLvl3Nbr().equals(cat))
                .findAny()
                .map(Lvl3 ::getLvl4List)
                .stream()
                .flatMap(Collection::stream)
                .filter(lvl4 -> lvl4.getLvl4Nbr().equals(subCat))
                .findAny()
                .map(Lvl4::getFinelines)
                .stream()
                .flatMap(Collection::stream)
                .filter(fineLine -> fineLine.getFinelineNbr().equals(flNbr))
                .findFirst()
                .get();
    }
}
