package com.walmart.aex.strategy.repository;

import com.walmart.aex.strategy.dto.FixtureAllocationStrategy;
import com.walmart.aex.strategy.entity.PlanStrategyId;
import com.walmart.aex.strategy.entity.StrategyMerchCatgFixture;
import com.walmart.aex.strategy.entity.StrategyMerchCatgFixtureId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FixtureAllocationStrategyRepository extends JpaRepository<StrategyMerchCatgFixture, StrategyMerchCatgFixtureId> {

    Optional<List<StrategyMerchCatgFixture>>
    findStrategyMerchCatgFixtureByStrategyMerchCatgFixtureId_StrategyMerchCatgId_PlanStrategyIdAndStrategyMerchCatgFixtureId_StrategyMerchCatgId_lvl3Nbr(PlanStrategyId planStrategyId, Integer lvl3Nbr);

    @Query(value = "SELECT DISTINCT new com.walmart.aex.strategy.dto.FixtureAllocationStrategy(smf.strategyMerchCatgFixtureId.strategyMerchCatgId.planStrategyId.planId, " +
            "smf.strategyMerchCatgFixtureId.strategyMerchCatgId.planStrategyId.strategyId, " +
            "smf.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl0Nbr, " +
            "smf.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl1Nbr, " +
            "smf.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl2Nbr, " +
            "smf.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl3Nbr, " +
            "sf.lvl3GenDesc1, " +
            "ssf.strategySubCatgFixtureId.lvl4Nbr, " +
            "sf.lvl4GenDesc1, " +
            "smf.fixtureType.fixtureTypeName, " +
            "smf.minFixturesPerFineline, " +
            "smf.maxFixturesPerFineline, " +
            "ssf.minFixturesPerFineline, " +
            "ssf.maxFixturesPerFineline, " +
            "smf.maxCcsPerFixture, " +
            "ssf.maxCcsPerFixture) " +
            "FROM StrategyMerchCatgFixture smf " +
            "INNER JOIN StrategySubCatgFixture ssf " +
            "  ON smf.strategyMerchCatgFixtureId.strategyMerchCatgId.planStrategyId.planId = ssf.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.planStrategyId.planId " +
            "  AND smf.strategyMerchCatgFixtureId.strategyMerchCatgId.planStrategyId.strategyId = ssf.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.planStrategyId.strategyId " +
            "  AND smf.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl0Nbr = ssf.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl0Nbr " +
            "  AND smf.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl1Nbr = ssf.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl1Nbr " +
            "  AND smf.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl2Nbr = ssf.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl2Nbr " +
            "  AND smf.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl3Nbr = ssf.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl3Nbr " +
            "  AND smf.strategyMerchCatgFixtureId.fixtureTypeId = ssf.strategySubCatgFixtureId.strategyMerchCatgFixtureId.fixtureTypeId " +
            "LEFT JOIN StrategyFinelineFixture sff " +
            "  ON  sff.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.planStrategyId.planId = ssf.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.planStrategyId.planId " +
            "  AND sff.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.planStrategyId.strategyId = ssf.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.planStrategyId.strategyId " +
            "  AND sff.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl0Nbr = ssf.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl0Nbr " +
            "  AND sff.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl1Nbr = ssf.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl1Nbr " +
            "  AND sff.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl2Nbr = ssf.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl2Nbr " +
            "  AND sff.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl3Nbr = ssf.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl3Nbr " +
            "  AND sff.strategyFinelineFixtureId.strategySubCatgFixtureId.lvl4Nbr = ssf.strategySubCatgFixtureId.lvl4Nbr " +
            "  AND sff.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.fixtureTypeId = ssf.strategySubCatgFixtureId.strategyMerchCatgFixtureId.fixtureTypeId " +
            "LEFT JOIN StrategyFineline sf " +
            "  ON sf.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.planStrategyId.planId = ssf.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.planStrategyId.planId " +
            "  AND sf.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl0Nbr = ssf.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl0Nbr " +
            "  AND sf.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl1Nbr = ssf.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl1Nbr " +
            "  AND sf.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl2Nbr = ssf.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl2Nbr " +
            "  AND sf.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl3Nbr = ssf.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl3Nbr " +
            "  AND sf.strategyFinelineId.strategySubCatgId.lvl4Nbr = ssf.strategySubCatgFixtureId.lvl4Nbr " +
            "  AND sf.strategyFinelineId.finelineNbr = sff.strategyFinelineFixtureId.finelineNbr " +
            "INNER JOIN FixtureType fr " +
            " ON fr.fixtureTypeId = smf.fixtureType.fixtureTypeId " +
            "WHERE (smf.strategyMerchCatgFixtureId.strategyMerchCatgId.planStrategyId.planId =:planId " +
            "AND smf.strategyMerchCatgFixtureId.strategyMerchCatgId.planStrategyId.strategyId=:strategyId " +
            "AND sf.traitChoiceCode IS NOT NULL " +
            "AND sf.channelId IN (1,3) )")
    List<FixtureAllocationStrategy> getFineLineRuleMinMax(@Param("planId") Long planId, @Param("strategyId") Long strategyId);


    @Query(value = "SELECT DISTINCT new com.walmart.aex.strategy.dto.FixtureAllocationStrategy( " +
            "sff.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.planStrategyId.planId, " +
            "sff.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.planStrategyId.strategyId, " +
            "sff.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl0Nbr, " +
            "sff.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl1Nbr, " +
            "sff.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl2Nbr, " +
            "sff.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl3Nbr, " +
            "sf.lvl3GenDesc1, " +
            "sff.strategyFinelineFixtureId.strategySubCatgFixtureId.lvl4Nbr, " +
            "sf.lvl4GenDesc1, " +
            "sff.strategyFinelineFixtureId.finelineNbr, " +
            "sf.finelineDesc, " +
            "sf.outFitting, " +
            "sf.brands," +
            "sf.altFinelineName, " +
            "fr.fixtureTypeName, " +
            "sff.adjBelowMinFixturesPerFineline, " +
            "sff.adjBelowMaxFixturesPerFineline, " +
            "sff.minFixtureGroup, " +
            "sff.maxFixtureGroup, " +
            "sff.adjMinFixturesPerFineline, " +
            "sff.adjMaxFixturesPerFineline, " +
            "sff.maxCcsPerFixture) " +
            "FROM StrategyFinelineFixture sff " +
            "LEFT JOIN StrategyFineline sf " +
            "  ON sf.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.planStrategyId.planId = sff.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.planStrategyId.planId " +
            "  AND sf.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl0Nbr = sff.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl0Nbr " +
            "  AND sf.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl1Nbr = sff.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl1Nbr " +
            "  AND sf.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl2Nbr = sff.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl2Nbr " +
            "  AND sf.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl3Nbr = sff.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl3Nbr " +
            "  AND sf.strategyFinelineId.strategySubCatgId.lvl4Nbr = sff.strategyFinelineFixtureId.strategySubCatgFixtureId.lvl4Nbr " +
            "  AND sf.strategyFinelineId.finelineNbr = sff.strategyFinelineFixtureId.finelineNbr " +
            "LEFT JOIN FixtureType fr " +
            " ON fr.fixtureTypeId = sff.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.fixtureTypeId " +
            "WHERE sff.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.planStrategyId.planId = :planId " +
            "   AND sff.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.planStrategyId.strategyId= :strategyId " +
            "   AND sff.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl3Nbr in (:lvl3Nbr) " +
            "   AND sff.strategyFinelineFixtureId.strategySubCatgFixtureId.lvl4Nbr in (:lvl4Nbr) " +
            "   AND sf.traitChoiceCode IS NOT NULL " +
            "   AND sf.channelId IN (1,3) ")
    List<FixtureAllocationStrategy> getFineLineRuleMinMaxByCatgAndSubCatg(@Param("planId") Long planId, @Param("strategyId") Long strategyId,
                                                                          @Param("lvl3Nbr") List<Integer> lvl3Nbr, @Param("lvl4Nbr") List<Integer> lvl4Nbr);

    @Query(value = "SELECT DISTINCT new com.walmart.aex.strategy.dto.FixtureAllocationStrategy(smf.strategyMerchCatgFixtureId.strategyMerchCatgId.planStrategyId.planId, " +
            "            smf.strategyMerchCatgFixtureId.strategyMerchCatgId.planStrategyId.strategyId, " +
            "            smf.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl0Nbr, " +
            "            smf.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl1Nbr, " +
            "            smf.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl2Nbr, " +
            "            smf.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl3Nbr, " +
            "            sf.lvl3GenDesc1, " +
            "            ssf.strategySubCatgFixtureId.lvl4Nbr, " +
            "            sf.lvl4GenDesc1, " +
            "            sff.strategyFinelineFixtureId.finelineNbr," +
            "            sf.finelineDesc, " +
            "            sf.altFinelineName, " +
            "            scf.strategyCcFixtureId.strategyStyleFixtureId.styleNbr," +
            "            scf.strategyCcFixtureId.ccId, " +
            "            sc.altCcDesc, " +
            "            sc.colorName," +
            "            smf.fixtureType.fixtureTypeName, " +
            "            smf.minFixturesPerFineline, " +
            "            smf.maxFixturesPerFineline, " +
            "            ssf.minFixturesPerFineline, " +
            "            ssf.maxFixturesPerFineline, " +
            "            smf.maxCcsPerFixture, " +
            "            ssf.maxCcsPerFixture," +
            "            sff.adjBelowMinFixturesPerFineline," +
            "            sff.adjBelowMaxFixturesPerFineline," +
            "            sff.minFixtureGroup," +
            "            sff.maxFixtureGroup," +
            "            sff.adjMinFixturesPerFineline," +
            "            sff.adjMaxFixturesPerFineline," +
            "            sff.maxCcsPerFixture," +
            "            scf.adjMaxFixturesPerCc) " +
            "            FROM StrategyMerchCatgFixture smf " +
            "            LEFT JOIN StrategySubCatgFixture ssf " +
            "              ON smf.strategyMerchCatgFixtureId.strategyMerchCatgId.planStrategyId.planId = ssf.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.planStrategyId.planId " +
            "              AND smf.strategyMerchCatgFixtureId.strategyMerchCatgId.planStrategyId.strategyId = ssf.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.planStrategyId.strategyId " +
            "              AND smf.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl0Nbr = ssf.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl0Nbr " +
            "              AND smf.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl1Nbr = ssf.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl1Nbr " +
            "              AND smf.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl2Nbr = ssf.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl2Nbr " +
            "              AND smf.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl3Nbr = ssf.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl3Nbr " +
            "              AND smf.strategyMerchCatgFixtureId.fixtureTypeId = ssf.strategySubCatgFixtureId.strategyMerchCatgFixtureId.fixtureTypeId " +
            "            LEFT JOIN StrategyFinelineFixture sff " +
            "              ON  sff.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.planStrategyId.planId = ssf.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.planStrategyId.planId " +
            "              AND sff.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.planStrategyId.strategyId = ssf.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.planStrategyId.strategyId " +
            "              AND sff.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl0Nbr = ssf.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl0Nbr " +
            "              AND sff.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl1Nbr = ssf.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl1Nbr " +
            "              AND sff.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl2Nbr = ssf.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl2Nbr " +
            "              AND sff.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl3Nbr = ssf.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl3Nbr " +
            "              AND sff.strategyFinelineFixtureId.strategySubCatgFixtureId.lvl4Nbr = ssf.strategySubCatgFixtureId.lvl4Nbr " +
            "              AND sff.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.fixtureTypeId = ssf.strategySubCatgFixtureId.strategyMerchCatgFixtureId.fixtureTypeId " +
            "            LEFT JOIN StrategyStyleFixture sstf " +
            "              ON  sstf.strategyStyleFixtureId.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.planStrategyId.planId = sff.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.planStrategyId.planId" +
            "              AND sstf.strategyStyleFixtureId.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.planStrategyId.strategyId =sff.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.planStrategyId.strategyId " +
            "              AND sstf.strategyStyleFixtureId.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl0Nbr = sff.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl0Nbr " +
            "              AND sstf.strategyStyleFixtureId.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl1Nbr = sff.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl1Nbr " +
            "              AND sstf.strategyStyleFixtureId.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl2Nbr = sff.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl2Nbr " +
            "              AND sstf.strategyStyleFixtureId.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl3Nbr = sff.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl3Nbr " +
            "              AND sstf.strategyStyleFixtureId.strategyFinelineFixtureId.strategySubCatgFixtureId.lvl4Nbr = sff.strategyFinelineFixtureId.strategySubCatgFixtureId.lvl4Nbr " +
            "              AND sstf.strategyStyleFixtureId.strategyFinelineFixtureId.finelineNbr = sff.strategyFinelineFixtureId.finelineNbr " +
            "              AND sstf.strategyStyleFixtureId.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.fixtureTypeId = sff.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.fixtureTypeId " +
            "            LEFT JOIN StrategyCcFixture scf " +
            "              ON  scf.strategyCcFixtureId.strategyStyleFixtureId.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.planStrategyId.planId = sstf.strategyStyleFixtureId.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.planStrategyId.planId" +
            "              AND scf.strategyCcFixtureId.strategyStyleFixtureId.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.planStrategyId.strategyId = sstf.strategyStyleFixtureId.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.planStrategyId.strategyId" +
            "              AND scf.strategyCcFixtureId.strategyStyleFixtureId.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl0Nbr = sstf.strategyStyleFixtureId.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl0Nbr" +
            "              AND scf.strategyCcFixtureId.strategyStyleFixtureId.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl1Nbr = sstf.strategyStyleFixtureId.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl1Nbr" +
            "              AND scf.strategyCcFixtureId.strategyStyleFixtureId.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl2Nbr = sstf.strategyStyleFixtureId.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl2Nbr" +
            "              AND scf.strategyCcFixtureId.strategyStyleFixtureId.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl3Nbr = sstf.strategyStyleFixtureId.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl3Nbr" +
            "              AND scf.strategyCcFixtureId.strategyStyleFixtureId.strategyFinelineFixtureId.strategySubCatgFixtureId.lvl4Nbr = sstf.strategyStyleFixtureId.strategyFinelineFixtureId.strategySubCatgFixtureId.lvl4Nbr" +
            "              AND scf.strategyCcFixtureId.strategyStyleFixtureId.strategyFinelineFixtureId.finelineNbr = sstf.strategyStyleFixtureId.strategyFinelineFixtureId.finelineNbr " +
            "              AND scf.strategyCcFixtureId.strategyStyleFixtureId.styleNbr = sstf.strategyStyleFixtureId.styleNbr " +
            "              AND scf.strategyCcFixtureId.strategyStyleFixtureId.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.fixtureTypeId = sstf.strategyStyleFixtureId.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.fixtureTypeId " +
            "            LEFT JOIN StrategyFineline sf  " +
            "              ON sf.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.planStrategyId.planId = sff.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.planStrategyId.planId  " +
            "              AND sf.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl0Nbr = sff.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl0Nbr  " +
            "              AND sf.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl1Nbr = sff.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl1Nbr  " +
            "              AND sf.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl2Nbr = sff.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl2Nbr  " +
            "              AND sf.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl3Nbr = sff.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl3Nbr  " +
            "              AND sf.strategyFinelineId.strategySubCatgId.lvl4Nbr = sff.strategyFinelineFixtureId.strategySubCatgFixtureId.lvl4Nbr  " +
            "              AND sf.strategyFinelineId.finelineNbr = sff.strategyFinelineFixtureId.finelineNbr" +
            "            LEFT JOIN StrategyCc sc " +
            "                ON sc.strategyCcId.strategyStyleId.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.planStrategyId.planId = scf.strategyCcFixtureId.strategyStyleFixtureId.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.planStrategyId.planId  " +
            "                AND sc.strategyCcId.strategyStyleId.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl0Nbr = scf.strategyCcFixtureId.strategyStyleFixtureId.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl0Nbr  " +
            "                AND sc.strategyCcId.strategyStyleId.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl1Nbr = scf.strategyCcFixtureId.strategyStyleFixtureId.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl1Nbr  " +
            "                AND sc.strategyCcId.strategyStyleId.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl2Nbr = scf.strategyCcFixtureId.strategyStyleFixtureId.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl2Nbr  " +
            "                AND sc.strategyCcId.strategyStyleId.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl3Nbr = scf.strategyCcFixtureId.strategyStyleFixtureId.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl3Nbr  " +
            "                AND sc.strategyCcId.strategyStyleId.strategyFinelineId.strategySubCatgId.lvl4Nbr = scf.strategyCcFixtureId.strategyStyleFixtureId.strategyFinelineFixtureId.strategySubCatgFixtureId.lvl4Nbr  " +
            "                AND sc.strategyCcId.strategyStyleId.strategyFinelineId.finelineNbr = scf.strategyCcFixtureId.strategyStyleFixtureId.strategyFinelineFixtureId.finelineNbr " +
            "                AND sc.strategyCcId.strategyStyleId.styleNbr = scf.strategyCcFixtureId.strategyStyleFixtureId.styleNbr " +
            "                AND sc.strategyCcId.ccId = scf.strategyCcFixtureId.ccId " +
            "            LEFT JOIN FixtureType fr " +
            "             ON fr.fixtureTypeId = sff.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.fixtureTypeId " +
            "            WHERE smf.strategyMerchCatgFixtureId.strategyMerchCatgId.planStrategyId.planId = :planId AND smf.strategyMerchCatgFixtureId.strategyMerchCatgId.planStrategyId.strategyId = :strategyId " +
            "            AND smf.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl3Nbr = :lvl3Nbr " +
            "            AND (:lvl4Nbr is null or ssf.strategySubCatgFixtureId.lvl4Nbr = :lvl4Nbr) " +
            "            AND sf.traitChoiceCode IS NOT NULL " +
            "            AND sf.channelId IN (1,3) " +
            "            AND (:finelineNbr is null or sff.strategyFinelineFixtureId.finelineNbr = :finelineNbr) " +
            "            AND (:ccId is null or scf.strategyCcFixtureId.ccId = :ccId)")
    List<FixtureAllocationStrategy> getFixtureAllocationRulesMetrics(@Param("planId") Long planId, @Param("strategyId") Long strategyId,
                                                                     @Param("lvl3Nbr") Integer lvl3Nbr, @Param("lvl4Nbr") Integer lvl4Nbr,
                                                                     @Param("finelineNbr") Integer finelineNbr, @Param("ccId") String ccId);
}
