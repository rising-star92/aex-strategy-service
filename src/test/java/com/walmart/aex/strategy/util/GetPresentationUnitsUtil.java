package com.walmart.aex.strategy.util;
import com.walmart.aex.strategy.dto.FixtureAllocationStrategy;
import com.walmart.aex.strategy.dto.request.*;
import com.walmart.aex.strategy.entity.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;

public class GetPresentationUnitsUtil {
    public static PlanStrategyDTO getStrategyRequestDto(){
        PlanStrategyDTO stratDto =  new PlanStrategyDTO();
        stratDto.setPlanId(465L);

        PresentationUnit pu = new PresentationUnit();
        pu.setType("TABLE");
        List<PresentationUnit> puList = new ArrayList<>();
        puList.add(pu);

        PresentationUnit pu1 = new PresentationUnit();
        pu1.setType("RACK");
        List<PresentationUnit> puList1 = new ArrayList<>();
        puList1.add(pu1);

        Strategy strategy = new Strategy();
        strategy.setPresentationUnits(puList);

        Strategy strategy1 = new Strategy();
        strategy1.setPresentationUnits(puList1);

        List<Fineline> lineList = new ArrayList<>();
        Fineline line = new Fineline();
        line.setFinelineNbr(1135);
        ProductDimensions productDimensions1 = new ProductDimensions();
        productDimensions1.setProductDimId(321);
        productDimensions1.setProductDimDesc("Medium");
        line.setProductDimensions(productDimensions1);
        line.setStrategy(strategy);
        lineList.add(line);

        Fineline line1 = new Fineline();
        line1.setFinelineNbr(1134);
        ProductDimensions productDimensions = new ProductDimensions();
        productDimensions.setProductDimId(123);
        productDimensions.setProductDimDesc("Thin");
        line1.setProductDimensions(productDimensions);
        line1.setStrategy(strategy1);
        lineList.add(line1);

        List<PresentationUnit> catPuList = new ArrayList<>();
        catPuList.add(pu);
        catPuList.add(pu1);

        Strategy catStrategy = new Strategy();
        catStrategy.setPresentationUnits(catPuList);

        Lvl4 lvl4 = new Lvl4();
        lvl4.setLvl4Nbr(7110);
        lvl4.setStrategy(catStrategy);
        lvl4.setFinelines(lineList);

        List<Lvl4> lvl4List =  new ArrayList<>();
        lvl4List.add(lvl4);

        Lvl3 lvl3 = new Lvl3();
        lvl3.setLvl3Nbr(6033);
        lvl3.setStrategy(catStrategy);
        lvl3.setLvl4List(lvl4List);

        List<Fineline> lineList1 = new ArrayList<>();

        Fineline line11 = new Fineline();
        line11.setFinelineNbr(1145);
        ProductDimensions productDimensions2 = new ProductDimensions();
        productDimensions2.setProductDimId(111);
        productDimensions2.setProductDimDesc("Thick");
        line.setProductDimensions(productDimensions1);
        line11.setStrategy(strategy);
        lineList1.add(line11);

        Fineline line12 = new Fineline();
        line12.setFinelineNbr(1144);
        line12.setStrategy(strategy1);
        lineList1.add(line12);

        List<PresentationUnit> catPuList1 = new ArrayList<>();
        catPuList1.add(pu);
        catPuList1.add(pu1);

        Strategy catStrategy1 = new Strategy();
        catStrategy1.setPresentationUnits(catPuList1);

        Lvl4 lvl41 = new Lvl4();
        lvl41.setLvl4Nbr(7112);
        lvl41.setStrategy(catStrategy1);
        lvl41.setFinelines(lineList1);

        List<Lvl4> lvl4List1 =  new ArrayList<>();
        lvl4List1.add(lvl41);

        Lvl3 lvl31 = new Lvl3();
        lvl31.setLvl3Nbr(6034);
        lvl31.setStrategy(catStrategy);
        lvl31.setLvl4List(lvl4List1);

        Lvl2 lvl2 = new Lvl2();
        lvl2.setLvl2Nbr(2999);
        lvl2.setLvl3List(new ArrayList<>());
        lvl2.getLvl3List().add(lvl3);
        lvl2.getLvl3List().add(lvl31);
        List<Lvl2> lvl2List = new ArrayList<>();
        lvl2List.add(lvl2);

        Lvl1 lvl1 = new Lvl1();
        lvl1.setLvl1Nbr(34);
        lvl1.setLvl2List(lvl2List);
        List<Lvl1> lvl1List = new ArrayList<>();
        lvl1List.add(lvl1);

        stratDto.setLvl1List(lvl1List);
        stratDto.setLvl0Nbr(5000);

        return  stratDto;
    }

    public static PlanStrategyDTO getSingleCategoryStrategyDto(){
        PlanStrategyDTO stratDto =  new PlanStrategyDTO();
        stratDto.setPlanId(465L);

        PresentationUnit pu = new PresentationUnit();
        pu.setType("Wall");
        List<PresentationUnit> puList = new ArrayList<>();
        puList.add(pu);

        Strategy strategy = new Strategy();
        strategy.setPresentationUnits(puList);

        Fineline line1134 = new Fineline();
        line1134.setFinelineNbr(1134);
        line1134.setStrategy(strategy);

        List<Fineline> lineList7110 = new ArrayList<>();
        lineList7110.add(line1134);

        Lvl4 lvl4 = new Lvl4();
        lvl4.setLvl4Nbr(7110);
        lvl4.setStrategy(strategy);
        lvl4.setFinelines(lineList7110);

        Fineline line2134 = new Fineline();
        line2134.setFinelineNbr(2134);
        line2134.setStrategy(strategy);

        List<Fineline> lineList8110 = new ArrayList<>();
        lineList8110.add(line2134);

        Lvl4 lvl48110 = new Lvl4();
        lvl48110.setLvl4Nbr(8110);
        lvl48110.setStrategy(strategy);
        lvl48110.setFinelines(lineList8110);

        List<Lvl4> lvl4List =  new ArrayList();
        lvl4List.add(lvl4);
        lvl4List.add(lvl48110);

        Lvl3 lvl3 = new Lvl3();
        lvl3.setLvl3Nbr(6033);
        lvl3.setStrategy(strategy);
        List<Lvl3> lvl3List = new ArrayList<>();
        lvl3List.add(lvl3);
        lvl3.setLvl4List(lvl4List);

        Lvl2 lvl2 = new Lvl2();
        lvl2.setLvl2Nbr(2999);
        lvl2.setLvl3List(lvl3List);
        List<Lvl2> lvl2List = new ArrayList<>();
        lvl2List.add(lvl2);

        Lvl1 lvl1 = new Lvl1();
        lvl1.setLvl1Nbr(34);
        lvl1.setLvl2List(lvl2List);
        List<Lvl1> lvl1List = new ArrayList<>();
        lvl1List.add(lvl1);

        stratDto.setLvl1List(lvl1List);
        stratDto.setLvl0Nbr(5000);

        return stratDto;
    }

    public static PlanStrategy getPlanStrategy(){

        StrategyMerchCatgFixture startMerchCatgFixture;
        StrategySubCatgFixture strategySubCatgFixture;
        StrategyFinelineFixture strategyFinelineFixture;

        PlanStrategy planStrategy = new PlanStrategy();
        PlanStrategyId strategyId = new PlanStrategyId();
        strategyId.setPlanId(465L);
        strategyId.setStrategyId(7L);
        planStrategy.setPlanStrategyId(strategyId);

        planStrategy.setStrategyMerchCatgs(new HashSet<>());

        StrategyMerchCatg strategyMerchCatg6033 = new StrategyMerchCatg();
        StrategyMerchCatgId strategyMerchCatgId =  new StrategyMerchCatgId(planStrategy.getPlanStrategyId(),
                5000, 34, 2999, 6033);
        strategyMerchCatg6033.setStrategyMerchCatgId(strategyMerchCatgId);
        strategyMerchCatg6033.setStrategyMerchCatgFixtures(new HashSet<>());

        startMerchCatgFixture = new StrategyMerchCatgFixture();
        StrategyMerchCatgFixtureId strategyMerchCatgFixtureId = new StrategyMerchCatgFixtureId(strategyMerchCatg6033.getStrategyMerchCatgId(), 1);
        startMerchCatgFixture.setStrategyMerchCatgFixtureId(strategyMerchCatgFixtureId);
        startMerchCatgFixture.setMinPresentationUnits(1);
        startMerchCatgFixture.setMaxPresentationUnits(3);

        startMerchCatgFixture.setStrategySubCatgFixtures(new HashSet<>());

        strategySubCatgFixture = new StrategySubCatgFixture();
        StrategySubCatgFixtureId strategySubCatgFixtureId = new StrategySubCatgFixtureId(startMerchCatgFixture.getStrategyMerchCatgFixtureId(), 7110);
        strategySubCatgFixture.setMinPresentationUnits(1);
        strategySubCatgFixture.setMaxPresentationUnits(3);
        strategySubCatgFixture.setStrategySubCatgFixtureId(strategySubCatgFixtureId);

        strategyFinelineFixture =  new StrategyFinelineFixture();
        strategySubCatgFixture.setStrategyFinelineFixtures(new HashSet<>());
        StrategyFinelineFixtureId strategyFinelineFixtureId = new StrategyFinelineFixtureId(strategySubCatgFixture.getStrategySubCatgFixtureId(), 1134);
        strategyFinelineFixture.setStrategyFinelineFixtureId(strategyFinelineFixtureId);
        strategyFinelineFixture.setMinPresentationUnits(1);
        strategyFinelineFixture.setMaxPresentationUnits(3);
        strategySubCatgFixture.getStrategyFinelineFixtures().add(strategyFinelineFixture);

        strategyFinelineFixture =  new StrategyFinelineFixture();
        StrategyFinelineFixtureId strategyFinelineFixtureId1 = new StrategyFinelineFixtureId(strategySubCatgFixture.getStrategySubCatgFixtureId(), 1135);
        strategyFinelineFixture.setStrategyFinelineFixtureId(strategyFinelineFixtureId1);
        strategyFinelineFixture.setMinPresentationUnits(1);
        strategyFinelineFixture.setMaxPresentationUnits(3);
        strategySubCatgFixture.getStrategyFinelineFixtures().add(strategyFinelineFixture);

        startMerchCatgFixture.getStrategySubCatgFixtures().add(strategySubCatgFixture);

        strategyMerchCatg6033.getStrategyMerchCatgFixtures().add(startMerchCatgFixture);

        planStrategy.getStrategyMerchCatgs().add(strategyMerchCatg6033);

        StrategyMerchCatg strategyMerchCatg6034 = new StrategyMerchCatg();
        StrategyMerchCatgId strategyMerchCatgId6034 =  new StrategyMerchCatgId(planStrategy.getPlanStrategyId(),
                5000, 34, 2999, 6034);
        strategyMerchCatg6034.setStrategyMerchCatgId(strategyMerchCatgId6034);
        strategyMerchCatg6034.setStrategyMerchCatgFixtures(new HashSet<>());

        startMerchCatgFixture = new StrategyMerchCatgFixture();
        StrategyMerchCatgFixtureId strategyMerchCatgFixtureId1 = new StrategyMerchCatgFixtureId(strategyMerchCatg6034.getStrategyMerchCatgId(), 1);
        startMerchCatgFixture.setStrategyMerchCatgFixtureId(strategyMerchCatgFixtureId1);
        startMerchCatgFixture.setStrategySubCatgFixtures(new HashSet<>());

        strategySubCatgFixture = new StrategySubCatgFixture();
        StrategySubCatgFixtureId strategySubCatgFixtureId1 = new StrategySubCatgFixtureId(startMerchCatgFixture.getStrategyMerchCatgFixtureId(), 7112);
        strategySubCatgFixture.setStrategySubCatgFixtureId(strategySubCatgFixtureId1);

        strategyFinelineFixture =  new StrategyFinelineFixture();
        strategySubCatgFixture.setStrategyFinelineFixtures(new HashSet<>());
        StrategyFinelineFixtureId strategyFinelineFixtureId11 = new StrategyFinelineFixtureId(strategySubCatgFixture.getStrategySubCatgFixtureId(), 1144);
        strategyFinelineFixture.setStrategyFinelineFixtureId(strategyFinelineFixtureId11);
        strategySubCatgFixture.getStrategyFinelineFixtures().add(strategyFinelineFixture);

        strategyFinelineFixture =  new StrategyFinelineFixture();
        StrategyFinelineFixtureId strategyFinelineFixtureId12 = new StrategyFinelineFixtureId(strategySubCatgFixture.getStrategySubCatgFixtureId(), 1145);
        strategyFinelineFixture.setStrategyFinelineFixtureId(strategyFinelineFixtureId12);
        strategySubCatgFixture.getStrategyFinelineFixtures().add(strategyFinelineFixture);

        startMerchCatgFixture.getStrategySubCatgFixtures().add(strategySubCatgFixture);

        strategyMerchCatg6034.getStrategyMerchCatgFixtures().add(startMerchCatgFixture);

        planStrategy.getStrategyMerchCatgs().add(strategyMerchCatg6034);

        return planStrategy;
    }

    public static PlanStrategy getNewPlanStrategy(){
        PlanStrategy planStrategy = new PlanStrategy();
        PlanStrategyId strategyId = new PlanStrategyId();
        strategyId.setPlanId(465L);
        planStrategy.setPlanStrategyId(strategyId);
        return planStrategy;
    }

    public static List<StrategyFinelineFixture> getFineLineFixture(FixtureAllocationStrategy allocationStrategy) {
        List<StrategyFinelineFixture> stratFlFixtures = new ArrayList<>();
        getPlanStrategy().getStrategyMerchCatgs().stream().forEach(strategyMerchCatg -> {
            if (allocationStrategy.getLvl3Nbr().equals(strategyMerchCatg.getStrategyMerchCatgId().getLvl3Nbr())) {
                strategyMerchCatg.getStrategyMerchCatgFixtures().stream().forEach(strategyMerchCatgFixture -> {
                    strategyMerchCatgFixture.getStrategySubCatgFixtures().stream().forEach(subCatgFixture -> {
                        if (subCatgFixture.getStrategySubCatgFixtureId().getLvl4Nbr().equals(allocationStrategy.getLvl4Nbr())) {
                            subCatgFixture.getStrategyFinelineFixtures().stream().forEach(stratFlFixture -> {
                                stratFlFixtures.add(stratFlFixture);
                            });
                        }
                    });
                });
            }
        });
        return stratFlFixtures;
    }

    public static List<StrategyPUMinMax> getPUMinMaxMetrics(FixtureAllocationStrategy allocationStrategy){
        List<StrategyPUMinMax> strategyPUMinMaxes = new ArrayList<>();
        Stream fixtureTypes = Stream.of(1,2,3,4);

        fixtureTypes.forEach(fixtureType->{
            StrategyPUMinMax minMax = new StrategyPUMinMax();
            StrategyPUMinMaxId minMaxId = new StrategyPUMinMaxId();
            minMaxId.setLvl0Nbr(allocationStrategy.getLvl0Nbr());
            minMaxId.setLvl1Nbr(allocationStrategy.getLvl1Nbr());
            minMaxId.setLvl2Nbr(allocationStrategy.getLvl2Nbr());
            minMaxId.setLvl3Nbr(allocationStrategy.getLvl3Nbr());
            minMaxId.setLvl4Nbr(allocationStrategy.getLvl4Nbr());
            Integer fixture = Integer.parseInt(fixtureType.toString());
            minMaxId.setFixtureTypeId(fixture);
            minMaxId.setAhsVId(2967);
            minMax.setStrategyPUMinMaxId(minMaxId);
            minMax.setMinQty(fixture*50);
            minMax.setMaxQty(fixture*100);
            strategyPUMinMaxes.add(minMax);
        });
        return  strategyPUMinMaxes;
    }
}
