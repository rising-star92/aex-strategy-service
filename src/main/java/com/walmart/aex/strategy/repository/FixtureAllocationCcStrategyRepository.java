package com.walmart.aex.strategy.repository;

import com.walmart.aex.strategy.dto.FixtureAllocationStrategy;
import com.walmart.aex.strategy.entity.StrategyCcFixture;
import com.walmart.aex.strategy.entity.StrategyCcFixtureId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FixtureAllocationCcStrategyRepository extends JpaRepository<StrategyCcFixture, StrategyCcFixtureId> {

    void deleteStrategyCcFixtureByStrategyCcFixtureId_StrategyStyleFixtureId_StrategyFinelineFixtureId_StrategySubCatgFixtureId_StrategyMerchCatgFixtureId_StrategyMerchCatgId_PlanStrategyId_planIdAndStrategyCcFixtureId_StrategyStyleFixtureId_StrategyFinelineFixtureId_StrategySubCatgFixtureId_StrategyMerchCatgFixtureId_StrategyMerchCatgId_lvl3NbrAndStrategyCcFixtureId_StrategyStyleFixtureId_StrategyFinelineFixtureId_StrategySubCatgFixtureId_lvl4NbrAndStrategyCcFixtureId_StrategyStyleFixtureId_StrategyFinelineFixtureId_finelineNbrAndStrategyCcFixtureId_StrategyStyleFixtureId_styleNbrAndStrategyCcFixtureId_ccId(
            Long planId, Integer lvl3Nbr, Integer lvl4Nbr, Integer finelineNbr, String styleNbr, String ccId);

    @Query(value = "SELECT DISTINCT new com.walmart.aex.strategy.dto.FixtureAllocationStrategy( " +
            "scf.strategyCcFixtureId.strategyStyleFixtureId.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.planStrategyId.planId,  " +
            "scf.strategyCcFixtureId.strategyStyleFixtureId.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.planStrategyId.strategyId,  " +
            "scf.strategyCcFixtureId.strategyStyleFixtureId.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl0Nbr,  " +
            "scf.strategyCcFixtureId.strategyStyleFixtureId.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl1Nbr,  " +
            "scf.strategyCcFixtureId.strategyStyleFixtureId.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl2Nbr,  " +
            "scf.strategyCcFixtureId.strategyStyleFixtureId.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl3Nbr,  " +
            "sf.lvl3GenDesc1,  " +
            "scf.strategyCcFixtureId.strategyStyleFixtureId.strategyFinelineFixtureId.strategySubCatgFixtureId.lvl4Nbr,  " +
            "sf.lvl4GenDesc1,  " +
            "scf.strategyCcFixtureId.strategyStyleFixtureId.strategyFinelineFixtureId.finelineNbr,  " +
            "sf.finelineDesc, " +
            "sf.outFitting, " +
            "sf.altFinelineName, " +
            "scf.strategyCcFixtureId.strategyStyleFixtureId.styleNbr, " +
            "scf.strategyCcFixtureId.ccId, " +
            "sc.altCcDesc, " +
            "sc.colorName, " +
            "fr.fixtureTypeName,  " +
            "scf.adjMaxFixturesPerCc )  " +
            "FROM StrategyCcFixture scf  " +
            "LEFT JOIN StrategyCc sc " +
            "    ON sc.strategyCcId.strategyStyleId.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.planStrategyId.planId = scf.strategyCcFixtureId.strategyStyleFixtureId.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.planStrategyId.planId  " +
            "    AND sc.strategyCcId.strategyStyleId.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl0Nbr = scf.strategyCcFixtureId.strategyStyleFixtureId.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl0Nbr  " +
            "    AND sc.strategyCcId.strategyStyleId.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl1Nbr = scf.strategyCcFixtureId.strategyStyleFixtureId.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl1Nbr  " +
            "    AND sc.strategyCcId.strategyStyleId.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl2Nbr = scf.strategyCcFixtureId.strategyStyleFixtureId.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl2Nbr  " +
            "    AND sc.strategyCcId.strategyStyleId.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl3Nbr = scf.strategyCcFixtureId.strategyStyleFixtureId.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl3Nbr  " +
            "    AND sc.strategyCcId.strategyStyleId.strategyFinelineId.strategySubCatgId.lvl4Nbr = scf.strategyCcFixtureId.strategyStyleFixtureId.strategyFinelineFixtureId.strategySubCatgFixtureId.lvl4Nbr  " +
            "    AND sc.strategyCcId.strategyStyleId.strategyFinelineId.finelineNbr = scf.strategyCcFixtureId.strategyStyleFixtureId.strategyFinelineFixtureId.finelineNbr " +
            "    AND sc.strategyCcId.strategyStyleId.styleNbr = scf.strategyCcFixtureId.strategyStyleFixtureId.styleNbr " +
            "    AND sc.strategyCcId.ccId = scf.strategyCcFixtureId.ccId " +
            "LEFT JOIN StrategyFineline sf " +
            "    ON sc.strategyCcId.strategyStyleId.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.planStrategyId.planId = sf.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.planStrategyId.planId " +
            "    AND sc.strategyCcId.strategyStyleId.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl0Nbr = sf.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl0Nbr " +
            "    AND sc.strategyCcId.strategyStyleId.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl1Nbr = sf.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl1Nbr " +
            "    AND sc.strategyCcId.strategyStyleId.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl2Nbr = sf.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl2Nbr " +
            "    AND sc.strategyCcId.strategyStyleId.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl3Nbr = sf.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl3Nbr " +
            "    AND sc.strategyCcId.strategyStyleId.strategyFinelineId.strategySubCatgId.lvl4Nbr = sf.strategyFinelineId.strategySubCatgId.lvl4Nbr " +
            "    AND sc.strategyCcId.strategyStyleId.strategyFinelineId.finelineNbr = sf.strategyFinelineId.finelineNbr " +
            "LEFT JOIN FixtureType fr  " +
            "    ON fr.fixtureTypeId = scf.strategyCcFixtureId.strategyStyleFixtureId.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.fixtureTypeId  " +
            "WHERE scf.strategyCcFixtureId.strategyStyleFixtureId.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.planStrategyId.planId = :planId  " +
            "    AND scf.strategyCcFixtureId.strategyStyleFixtureId.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.planStrategyId.strategyId= :strategyId  " +
            "    AND scf.strategyCcFixtureId.strategyStyleFixtureId.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl3Nbr in (:lvl3Nbr)  " +
            "    AND sc.channelId IN (1,3) " +
            "    AND scf.strategyCcFixtureId.strategyStyleFixtureId.strategyFinelineFixtureId.strategySubCatgFixtureId.lvl4Nbr in (:lvl4Nbr) " +
            "    AND scf.strategyCcFixtureId.strategyStyleFixtureId.strategyFinelineFixtureId.finelineNbr in (:finelineNbr)")
    List<FixtureAllocationStrategy> getFixtureCCBasedOnCatgSubCatgAndFl(@Param("planId") Long planId, @Param("strategyId") Long strategyId,
                                                                        @Param("lvl3Nbr") List<Integer> lvl3Nbr, @Param("lvl4Nbr") List<Integer> lvl4Nbr,
                                                                        @Param("finelineNbr") List<Integer> finelineNbr);
}
