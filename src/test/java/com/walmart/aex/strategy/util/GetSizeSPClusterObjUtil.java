package com.walmart.aex.strategy.util;

import com.walmart.aex.strategy.dto.request.SizeProfileDTO;
import com.walmart.aex.strategy.entity.*;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class GetSizeSPClusterObjUtil {

    private static Integer lvl0Nbr = 39107153;
    private static Integer lvl1Nbr = 50400;
    private static Integer lvl2Nbr = 105400;
    private static Integer lvl3Nbr = 34556;
    private static Integer lvl4Nbr = 4567;
    private static Integer fineline1Nbr = 151;
    private static String style1Nbr = "151_23_01_001";
    private static String ccId = "151_23_01_001_001";

    public static Set<PlanClusterStrategy> getPlanClusterStrategies() {
        Set<PlanClusterStrategy> planClusterStrategies = new HashSet<>();
        PlanClusterStrategy planClusterStrategy = getPlanClusterStrategy();
        planClusterStrategies.add(planClusterStrategy);
        return planClusterStrategies;
    }

    @NotNull
    public static PlanClusterStrategy getPlanClusterStrategy() {
        PlanClusterStrategy planClusterStrategy = new PlanClusterStrategy();
        planClusterStrategy.setPlanClusterStrategyId(getPlanClusterStrategyId());
        planClusterStrategy.setAnalyticsClusterLabel(null);
        planClusterStrategy.setDetailedAnalyticsClusterDesc(null);
        planClusterStrategy.setStrategyMerchCategorySPCluster(getStrategyMerchCategorySPClusters());
        return planClusterStrategy;
    }

    private static PlanClusterStrategyId getPlanClusterStrategyId() {
        PlanClusterStrategyId planClusterStrategyId = new PlanClusterStrategyId();
        planClusterStrategyId.setPlanStrategyId(getPlanStrategyIdForSize());
        planClusterStrategyId.setAnalyticsClusterId(0);
        return planClusterStrategyId;
    }

    public static Set<StrategyMerchCategorySPCluster> getStrategyMerchCategorySPClusters() {
        Set<StrategyMerchCategorySPCluster> sp = new HashSet<>();
        StrategyMerchCategorySPCluster strategyMerchCategorySPCluster = getStrategyMerchCategorySPCluster();
        sp.add(strategyMerchCategorySPCluster);
        return sp;
    }

    public static StrategyMerchCategorySPCluster getStrategyMerchCategorySPCluster() {
        StrategyMerchCategorySPCluster strategyMerchCategorySPCluster = new StrategyMerchCategorySPCluster();
        strategyMerchCategorySPCluster.setStrategyMerchCatgSPClusId(getStrategyMerchCatgSPClusId());
        strategyMerchCategorySPCluster.setAnalyticsSPPercent(0);
        strategyMerchCategorySPCluster.setMerchantOverrideSPPercent(0);
        SizeProfileDTO sizeProfile = getSizeProfileDTO();
        strategyMerchCategorySPCluster.setSizeProfileObj(sizeProfile.toString());
        strategyMerchCategorySPCluster.setStrategySubCatgSPClusters(getStrategySubCategorySPClusters());
        return strategyMerchCategorySPCluster;
    }

    public static SizeProfileDTO getSizeProfileDTO() {
        SizeProfileDTO sizeProfile = new SizeProfileDTO();
        sizeProfile.setAdjustedSizeProfile(0.01);
        sizeProfile.setSizeProfilePrcnt(0.5);
        sizeProfile.setIsEligible(0);
        sizeProfile.setAhsSizeId(324);
        sizeProfile.setSizeDesc("S/M");
        return sizeProfile;
    }

    public static Set<StrategySubCategorySPCluster> getStrategySubCategorySPClusters() {

        Set<StrategySubCategorySPCluster> strategySubCategorySPClusters = new HashSet<>();
        strategySubCategorySPClusters.add(getStrategySubCategorySPCluster());
        return strategySubCategorySPClusters;
    }

    public static StrategySubCategorySPCluster getStrategySubCategorySPCluster() {
        StrategySubCategorySPCluster strategySubCategorySPCluster = new StrategySubCategorySPCluster();
        StrategySubCatgSPClusId strategySubCatgSPClusId = getStrategySubCatgSPClusId();
        strategySubCategorySPCluster.setStrategySubCatgSPClusId(strategySubCatgSPClusId);
        strategySubCategorySPCluster.setSizeProfileObj(getSizeProfileDTO().toString());
        strategySubCategorySPCluster.setStrategyFinelinesSPCluster(getStrategyFineLineSPClusters());
        return strategySubCategorySPCluster;
    }

    @NotNull
    private static StrategySubCatgSPClusId getStrategySubCatgSPClusId() {
        StrategySubCatgSPClusId strategySubCatgSPClusId = new StrategySubCatgSPClusId();
        strategySubCatgSPClusId.setStrategyMerchCatgSPClusId(getStrategyMerchCatgSPClusId());
        strategySubCatgSPClusId.setLvl4Nbr(lvl4Nbr);
        return strategySubCatgSPClusId;
    }

    public static Set<StrategyFineLineSPCluster> getStrategyFineLineSPClusters() {
        Set<StrategyFineLineSPCluster> srategyFineLineSPClusters = new HashSet<>();
        srategyFineLineSPClusters.add(getStrategyFineLineSPCluster());
        return srategyFineLineSPClusters;
    }

    public static StrategyFineLineSPCluster getStrategyFineLineSPCluster() {
        StrategyFineLineSPCluster strategyFineLineSPCluster = new StrategyFineLineSPCluster();
        StrategyFineLineSPClusId strategyFineLineSPClusId = getStrategyFineLineSPClusId();
        strategyFineLineSPCluster.setStrategyIFineLineId(strategyFineLineSPClusId);
        strategyFineLineSPCluster.setSizeProfileObj(getSizeProfileDTO().toString());
        strategyFineLineSPCluster.setStrategyStylesSPClusters(getStrategyStyleSPClusters());
        return strategyFineLineSPCluster;
    }

    private static StrategyFineLineSPClusId getStrategyFineLineSPClusId() {
        StrategyFineLineSPClusId strategyFineLineSPClusId = new StrategyFineLineSPClusId();
        strategyFineLineSPClusId.setStrategySubCatgSPClusId(getStrategySubCatgSPClusId());
        strategyFineLineSPClusId.setFinelineNbr(fineline1Nbr);
        return strategyFineLineSPClusId;
    }

    public static Set<StrategyStyleSPCluster> getStrategyStyleSPClusters() {
        Set<StrategyStyleSPCluster> strategyStyleSPClusters = new HashSet<>();
        strategyStyleSPClusters.add(getStrategyStyleSPCluster());
        return strategyStyleSPClusters;
    }

    public static StrategyStyleSPCluster getStrategyStyleSPCluster() {
        StrategyStyleSPCluster StrategyStyleSPCluster = new StrategyStyleSPCluster();
        com.walmart.aex.strategy.entity.StrategyStyleSPClusId StrategyStyleSPClusId = getStrategyStyleSPClusId();
        StrategyStyleSPCluster.setStrategyStyleSPClusId(StrategyStyleSPClusId);
        StrategyStyleSPCluster.setSizeProfileObj(getSizeProfileDTO().toString());
        StrategyStyleSPCluster.setStrategyCcSPClusters(getStrategyCustomerChoiceSPClusters());
        return StrategyStyleSPCluster;
    }

    public static StrategyStyleSPCluster getStrategyStyleSPClusterv2() {
        StrategyStyleSPCluster StrategyStyleSPCluster = new StrategyStyleSPCluster();
        com.walmart.aex.strategy.entity.StrategyStyleSPClusId StrategyStyleSPClusId = getStrategyStyleSPClusId();
        StrategyStyleSPCluster.setStrategyStyleSPClusId(StrategyStyleSPClusId);
        StrategyStyleSPCluster.setSizeProfileObj(getSizeProfileDTO().toString());
        StrategyStyleSPCluster.setStrategyFinelineSPClus(getStrategyFineLineSPCluster());
        return StrategyStyleSPCluster;
    }

    @NotNull
    private static StrategyStyleSPClusId getStrategyStyleSPClusId() {
        StrategyStyleSPClusId StrategyStyleSPClusId = new StrategyStyleSPClusId();
        StrategyStyleSPClusId.setStrategyFinelineSPClusId(getStrategyFineLineSPClusId());
        StrategyStyleSPClusId.setStyleNbr(style1Nbr);
        return StrategyStyleSPClusId;
    }

    public static Set<StrategyCcSPCluster> getStrategyCustomerChoiceSPClusters() {
        Set<StrategyCcSPCluster> strategyCcSPClusters = new HashSet<>();
        strategyCcSPClusters.add(getStrategyCustomerChoiceSPCluster());
        return strategyCcSPClusters;
    }

    public static StrategyCcSPCluster getStrategyCustomerChoiceSPCluster() {
        StrategyCcSPCluster StrategyCcSPCluster = new StrategyCcSPCluster();
        StrategyCcSPClusId StrategyCcSPClusId = getStrategyCustomerChoiceSPClusId();
        StrategyCcSPCluster.setStrategyCcSPClusId(StrategyCcSPClusId);
        StrategyCcSPCluster.setSizeProfileObj(getSizeProfileDTO().toString());
        return StrategyCcSPCluster;
    }

    public static StrategyCcSPCluster getStrategyCustomerChoiceSPClusterv2() {
        StrategyCcSPCluster StrategyCcSPCluster = new StrategyCcSPCluster();
        StrategyCcSPClusId StrategyCcSPClusId = getStrategyCustomerChoiceSPClusId();
        StrategyCcSPCluster.setStrategyCcSPClusId(StrategyCcSPClusId);
        StrategyCcSPCluster.setSizeProfileObj(getSizeProfileDTO().toString());
        StrategyCcSPCluster.setStrategyStyleSPCluster(getStrategyStyleSPClusterv2());
        return StrategyCcSPCluster;
    }

    public static StrategyCcSPClusId getStrategyCustomerChoiceSPClusId() {
        StrategyCcSPClusId StrategyCcSPClusId = new StrategyCcSPClusId();
        StrategyCcSPClusId.setStrategyStyleSPClusId(getStrategyStyleSPClusId());
        StrategyCcSPClusId.setCcId(ccId);
        return StrategyCcSPClusId;
    }

    private static StrategyMerchCatgSPClusId getStrategyMerchCatgSPClusId() {
        StrategyMerchCatgSPClusId strategyMerchCatgSPClusId = new StrategyMerchCatgSPClusId();
        strategyMerchCatgSPClusId.setPlanClusterStrategyId(getPlanClusterStrategyId());
        strategyMerchCatgSPClusId.setLvl0Nbr(lvl0Nbr);
        strategyMerchCatgSPClusId.setLvl1Nbr(lvl1Nbr);
        strategyMerchCatgSPClusId.setLvl2Nbr(lvl2Nbr);
        strategyMerchCatgSPClusId.setLvl3Nbr(lvl3Nbr);
        strategyMerchCatgSPClusId.setChannelId(1);
        return strategyMerchCatgSPClusId;
    }

    public static PlanStrategyId getPlanStrategyIdForSize(){
        PlanStrategyId planStrategyId = new PlanStrategyId();
        planStrategyId.setPlanId(346l);
        planStrategyId.setStrategyId(4l);
        return planStrategyId;
    }

    public static PlanStrategy getPlanStrategyForSize(){
        PlanStrategy planStrategy = new PlanStrategy();
        planStrategy.setPlanStrategyId(getPlanStrategyIdForSize());
        Set<StrategyMerchCatg> strategyMerchCatgs = new HashSet<>();
        StrategyMerchCatg strategyMerchCatg = new StrategyMerchCatg();
        StrategyMerchCatgId strategyMerchCatgId = new StrategyMerchCatgId();
        strategyMerchCatgId.setLvl0Nbr(lvl0Nbr);
        strategyMerchCatgId.setLvl1Nbr(lvl1Nbr);
        strategyMerchCatgId.setLvl2Nbr(lvl2Nbr);
        strategyMerchCatgId.setLvl3Nbr(lvl3Nbr);
        strategyMerchCatgId.setPlanStrategyId(planStrategy.getPlanStrategyId());

        PlanClusterStrategy planClusterStrategy = new PlanClusterStrategy();
        PlanClusterStrategyId planClusterStrategyId = new PlanClusterStrategyId();
        planClusterStrategyId.setAnalyticsClusterId(0);
        planClusterStrategyId.setPlanStrategyId(getPlanStrategyIdForSize());

        Set<StrategyMerchCategorySPCluster> strategyMerchCategorySPClusters = new HashSet<>();
        StrategyMerchCategorySPCluster strategyMerchCategorySPCluster = new StrategyMerchCategorySPCluster();

        StrategyMerchCatgSPClusId strategyMerchCatgSPClusId = new StrategyMerchCatgSPClusId();
        strategyMerchCatgSPClusId.setLvl0Nbr(lvl0Nbr);
        strategyMerchCatgSPClusId.setLvl1Nbr(lvl1Nbr);
        strategyMerchCatgSPClusId.setLvl2Nbr(lvl2Nbr);
        strategyMerchCatgSPClusId.setLvl3Nbr(lvl3Nbr);
        strategyMerchCatgSPClusId.setChannelId(1);
        strategyMerchCatgSPClusId.setPlanClusterStrategyId(planClusterStrategyId);
        strategyMerchCategorySPCluster.setStrategyMerchCatgSPClusId(strategyMerchCatgSPClusId);

        //subcatg
        Set<StrategySubCategorySPCluster> strategySubCategorySPClusters = new HashSet<>();
        StrategySubCategorySPCluster strategySubCategorySPCluster = new StrategySubCategorySPCluster();
        StrategySubCatgSPClusId strategySubCatgSPClusId = new StrategySubCatgSPClusId();
        strategySubCatgSPClusId.setStrategyMerchCatgSPClusId(strategyMerchCatgSPClusId);
        strategySubCatgSPClusId.setLvl4Nbr(lvl4Nbr);
        strategySubCategorySPCluster.setStrategySubCatgSPClusId(strategySubCatgSPClusId);

        //Fineline
        Set<StrategyFineLineSPCluster> strategyFineLineSPClusters = new HashSet<>();


        strategySubCategorySPClusters.add(strategySubCategorySPCluster);
        strategyMerchCategorySPCluster.setStrategySubCatgSPClusters(strategySubCategorySPClusters);
        strategyMerchCategorySPClusters.add(strategyMerchCategorySPCluster);
        planClusterStrategy.setStrategyMerchCategorySPCluster(strategyMerchCategorySPClusters);


        FixtureType fixtureType = new FixtureType();
        fixtureType.setFixtureTypeId(1);

        Set<StrategyMerchCatgFixture> strategyMerchCatgFixtures = new HashSet<>();
        StrategyMerchCatgFixture strategyMerchCatgFixture = new StrategyMerchCatgFixture();
        StrategyMerchCatgFixtureId strategyMerchCatgFixtureId = new StrategyMerchCatgFixtureId();
        strategyMerchCatgFixtureId.setStrategyMerchCatgId(strategyMerchCatgId);
        strategyMerchCatgFixtureId.setFixtureTypeId(1);
        strategyMerchCatgFixture.setStrategyMerchCatgFixtureId(strategyMerchCatgFixtureId);
        strategyMerchCatgFixture.setFixtureType(fixtureType);

        Set<StrategySubCatgFixture> strategySubCatgFixtures = new HashSet<>();
        StrategySubCatgFixture strategySubCatgFixture = new StrategySubCatgFixture();
        StrategySubCatgFixtureId strategySubCatgFixtureId = new StrategySubCatgFixtureId();
        strategySubCatgFixtureId.setStrategyMerchCatgFixtureId(strategyMerchCatgFixtureId);
        strategySubCatgFixtureId.setLvl4Nbr(lvl4Nbr);
        strategySubCatgFixture.setStrategySubCatgFixtureId(strategySubCatgFixtureId);

        Set<StrategyFinelineFixture> strategyFinelineFixtures = new HashSet<>();
        StrategyFinelineFixture strategyFinelineFixture = new StrategyFinelineFixture();
        StrategyFinelineFixtureId strategyFinelineFixtureId = new StrategyFinelineFixtureId();
        strategyFinelineFixtureId.setStrategySubCatgFixtureId(strategySubCatgFixtureId);
        strategyFinelineFixtureId.setFinelineNbr(fineline1Nbr);
        strategyFinelineFixture.setStrategyFinelineFixtureId(strategyFinelineFixtureId);
        strategyFinelineFixtures.add(strategyFinelineFixture);
        strategySubCatgFixture.setStrategyFinelineFixtures(strategyFinelineFixtures);

        strategySubCatgFixtures.add(strategySubCatgFixture);
        strategyMerchCatgFixture.setStrategySubCatgFixtures(strategySubCatgFixtures);

        strategyMerchCatgFixtures.add(strategyMerchCatgFixture);
        strategyMerchCatg.setStrategyMerchCatgFixtures(strategyMerchCatgFixtures);
        strategyMerchCatgs.add(strategyMerchCatg);
        planStrategy.setStrategyMerchCatgs(strategyMerchCatgs);
        return planStrategy;
    }

}

