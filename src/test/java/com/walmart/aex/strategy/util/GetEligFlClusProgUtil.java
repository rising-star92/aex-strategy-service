package com.walmart.aex.strategy.util;

import com.walmart.aex.strategy.entity.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
public class GetEligFlClusProgUtil {

    private static Integer lvl0Nbr = 39107153;
    private static Integer lvl1Nbr = 50400;
    private static Integer lvl2Nbr = 105400;
    private static Integer lvl3Nbr = 34556;
    private static Integer lvl4Nbr = 4567;
    private static Integer fineline1Nbr = 151;
    private static Integer programId = 23456;
    private static String fineline1Desc = "Mens Top Hoodie";
    private static String style1Nbr = "151_23_01_001";
    private static String ccId = "151_23_01_001_001";
    private static String lvl0Desc ="General Merchandise";
    private static String lvl1Desc ="Apparel";
    private static String lvl2Desc ="Mens Apparel";
    private static String lvl3Desc ="Mens Tops";
    private static String lvl4Desc ="Mens Tops Active wear";

    public static Optional<Set<StrategyFlClusPrgmEligRanking>> getStrategyFlClusPrgmEligRankings(){
        return Optional.of(IntStream.rangeClosed(0, 7)
                .mapToObj(i -> getStrategyFlClusPrgmEligRankingObj(new StrategyFlClusPrgmEligRanking(), i))
                .collect(Collectors.toSet()));
    }

    public static Optional<Set<StrategyFlClusPrgmEligRanking>> getStrategyFlClusPrgmEligRankingsPartial(){
        return Optional.of(IntStream.rangeClosed(0, 7)
                .mapToObj(i -> getStrategyFlClusPrgmEligRankingObjPartial(new StrategyFlClusPrgmEligRanking(), i))
                .collect(Collectors.toSet()));
    }

    private static StrategyFlClusPrgmEligRanking getStrategyFlClusPrgmEligRankingObj(StrategyFlClusPrgmEligRanking strategyFlClusPrgmEligRanking, int i) {
        StrategyFlClusPrgmEligRankingId strategyFlClusPrgmEligRankingId = getStrategyFlClusPrgmEligRankingId(i);
        strategyFlClusPrgmEligRanking.setStrategyFlClusPrgmEligRankingId(strategyFlClusPrgmEligRankingId);
        strategyFlClusPrgmEligRanking.setIsEligibleFlag(1);
        strategyFlClusPrgmEligRanking.setStoreCount(10);
        strategyFlClusPrgmEligRanking.setInStoreYrWkDesc("FYE2023WK20");
        strategyFlClusPrgmEligRanking.setMarkDownYrWkDesc("FYE2023WK30");
        strategyFlClusPrgmEligRanking.setMerchantOverrideRank(2);
        strategyFlClusPrgmEligRanking.setEligStyleClusProgs(Collections.singleton(getEligStyleClusProg(strategyFlClusPrgmEligRankingId)));
        return strategyFlClusPrgmEligRanking;
    }

    private static StrategyFlClusPrgmEligRanking getStrategyFlClusPrgmEligRankingObjPartial(StrategyFlClusPrgmEligRanking strategyFlClusPrgmEligRanking, int i) {
        StrategyFlClusPrgmEligRankingId strategyFlClusPrgmEligRankingId = getStrategyFlClusPrgmEligRankingId(i);
        strategyFlClusPrgmEligRanking.setStrategyFlClusPrgmEligRankingId(strategyFlClusPrgmEligRankingId);
        strategyFlClusPrgmEligRanking.setIsEligibleFlag(1);
        strategyFlClusPrgmEligRanking.setStoreCount(10);
        strategyFlClusPrgmEligRanking.setInStoreYrWkDesc("FYE2023WK20");
        strategyFlClusPrgmEligRanking.setMarkDownYrWkDesc("FYE2023WK30");
        strategyFlClusPrgmEligRanking.setMerchantOverrideRank(2);
        strategyFlClusPrgmEligRanking.setEligStyleClusProgs(Collections.singleton(getEligStyleClusProgPartial(strategyFlClusPrgmEligRankingId)));
        return strategyFlClusPrgmEligRanking;
    }

    private static EligStyleClusProg getEligStyleClusProg(StrategyFlClusPrgmEligRankingId strategyFlClusPrgmEligRankingId) {
        EligStyleClusProg eligStyleClusProg = new EligStyleClusProg();
        EligStyleClusProgId eligStyleClusProgId = getEligStyleClusProgId(strategyFlClusPrgmEligRankingId);
        eligStyleClusProg.setEligStyleClusProgId(eligStyleClusProgId);
        eligStyleClusProg.setEligCcClusProgs(Collections.singleton(getEligCcClusProg(eligStyleClusProgId)));
        return eligStyleClusProg;
    }

    private static EligStyleClusProg getEligStyleClusProgPartial(StrategyFlClusPrgmEligRankingId strategyFlClusPrgmEligRankingId) {
        EligStyleClusProg eligStyleClusProg = new EligStyleClusProg();
        EligStyleClusProgId eligStyleClusProgId = getEligStyleClusProgId(strategyFlClusPrgmEligRankingId);
        eligStyleClusProg.setEligStyleClusProgId(eligStyleClusProgId);
        eligStyleClusProg.setEligCcClusProgs(Collections.singleton(getEligCcClusProgPartial(eligStyleClusProgId)));
        return eligStyleClusProg;
    }

    private static EligCcClusProg getEligCcClusProg(EligStyleClusProgId eligStyleClusProgId) {
        EligCcClusProg eligCcClusProg = new EligCcClusProg();
        EligCcClusProgId eligCcClusProgId = geteligCcClusProgId(eligStyleClusProgId);
        eligCcClusProg.setIsEligibleFlag(1);
        eligCcClusProg.setEligCcClusProgId(eligCcClusProgId);
        eligCcClusProg.setInStoreYrWkDesc("FYE2023WK20");
        eligCcClusProg.setMarkDownYrWkDesc("FYE2023WK30");
        eligCcClusProg.setMerchantOverrideRank(1);
        return eligCcClusProg;

    }

    private static EligCcClusProg getEligCcClusProgPartial(EligStyleClusProgId eligStyleClusProgId) {
        EligCcClusProg eligCcClusProg = new EligCcClusProg();
        EligCcClusProgId eligCcClusProgId = geteligCcClusProgId(eligStyleClusProgId);
        eligCcClusProg.setIsEligibleFlag(1);
        eligCcClusProg.setEligCcClusProgId(eligCcClusProgId);
        eligCcClusProg.setInStoreYrWkDesc("FYE2023WK20");
        eligCcClusProg.setMarkDownYrWkDesc("FYE2023WK30");
        eligCcClusProg.setMerchantOverrideRank(1);
        return eligCcClusProg;

    }

    private static EligCcClusProgId geteligCcClusProgId(EligStyleClusProgId eligStyleClusProgId) {
        EligCcClusProgId eligCcClusProgId = new EligCcClusProgId();
        eligCcClusProgId.setEligStyleClusProgId(eligStyleClusProgId);
        eligCcClusProgId.setCcId("51_2_23_001_001");
        return eligCcClusProgId;
    }

    private static EligStyleClusProgId getEligStyleClusProgId(StrategyFlClusPrgmEligRankingId strategyFlClusPrgmEligRankingId) {
        EligStyleClusProgId eligStyleClusProgId = new EligStyleClusProgId();
        eligStyleClusProgId.setStrategyFlClusPrgmEligRankingId(strategyFlClusPrgmEligRankingId);
        eligStyleClusProgId.setStyleNbr("51_2_23_001");
        return eligStyleClusProgId;
    }

    private static StrategyFlClusPrgmEligRankingId getStrategyFlClusPrgmEligRankingId(int i) {
        StrategyFlClusPrgmEligRankingId strategyFlClusPrgmEligRankingId = new StrategyFlClusPrgmEligRankingId();
        strategyFlClusPrgmEligRankingId.setStrategyFlClusEligRankingId(getStrategyFlClusEligRankingId(i));
        return  strategyFlClusPrgmEligRankingId;
    }
    private static StrategyFlClusEligRankingId getStrategyFlClusEligRankingId(int i) {
        StrategyFlClusEligRankingId strategyFlClusEligRankingId = new StrategyFlClusEligRankingId();
        strategyFlClusEligRankingId.setPlanClusterStrategyId(getPlanClusterStrategyId(i));
        strategyFlClusEligRankingId.setFinelineNbr(fineline1Nbr);
        strategyFlClusEligRankingId.setLvl0Nbr(lvl0Nbr);
        strategyFlClusEligRankingId.setLvl1Nbr(lvl1Nbr);
        strategyFlClusEligRankingId.setLvl2Nbr(lvl2Nbr);
        strategyFlClusEligRankingId.setLvl3Nbr(lvl3Nbr);
        strategyFlClusEligRankingId.setLvl4Nbr(lvl4Nbr);
        return  strategyFlClusEligRankingId;
    }

    private static PlanClusterStrategyId getPlanClusterStrategyId(int i) {
        PlanClusterStrategyId planClusterStrategyId = new PlanClusterStrategyId();
        planClusterStrategyId.setPlanStrategyId(getPlanStrategyId());
        planClusterStrategyId.setAnalyticsClusterId(i);
        return planClusterStrategyId;
    }

    private static PlanStrategyId getPlanStrategyId() {
        PlanStrategyId planStrategyId = new PlanStrategyId();
        planStrategyId.setPlanId(1l);
        planStrategyId.setStrategyId(1l);
        return planStrategyId;
    }

    public static Optional<Set<EligCcClusProg>> getEligCcClusProg(){
        return Optional.of(IntStream.rangeClosed(0, 7)
                .mapToObj(i -> getEligCcClusProgObj(new EligCcClusProg(), i)).collect(Collectors.toSet()));
    }

    public static Optional<Set<EligCcClusProg>> getEligCcClusProgPartial(){
        return Optional.of(IntStream.rangeClosed(0, 7)
                .mapToObj(i -> getEligCcClusProgObjPartial(new EligCcClusProg(), i)).collect(Collectors.toSet()));
    }

    private static EligCcClusProg getEligCcClusProgObj(EligCcClusProg eligCcClusProg, int i) {
        EligStyleClusProgId eligStyleClusProgId = new EligStyleClusProgId(getStrategyFlClusPrgmEligRankingId(i), "51_2_23_001");
        EligCcClusProgId eligCcClusProgId = new EligCcClusProgId(eligStyleClusProgId,"51_2_23_001_001");
        eligCcClusProg.setEligCcClusProgId(eligCcClusProgId);
        eligCcClusProg.setIsEligibleFlag(1);
        eligCcClusProg.setInStoreYrWkDesc("FYE2023WK20");
        eligCcClusProg.setMarkDownYrWkDesc("FYE2023WK30");
        eligCcClusProg.setMerchantOverrideRank(1);
        return eligCcClusProg;
    }

    private static EligCcClusProg getEligCcClusProgObjPartial(EligCcClusProg eligCcClusProg, int i) {
        EligStyleClusProgId eligStyleClusProgId = new EligStyleClusProgId(getStrategyFlClusPrgmEligRankingId(i), "51_2_23_001");
        EligCcClusProgId eligCcClusProgId = new EligCcClusProgId(eligStyleClusProgId,"51_2_23_001_001");
        eligCcClusProg.setEligCcClusProgId(eligCcClusProgId);
        eligCcClusProg.setIsEligibleFlag(1);
        eligCcClusProg.setInStoreYrWkDesc("FYE2023WK20");
        eligCcClusProg.setMarkDownYrWkDesc("FYE2023WK30");
        eligCcClusProg.setMerchantOverrideRank(1);
        return eligCcClusProg;
    }

}
