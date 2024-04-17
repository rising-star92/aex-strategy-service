package com.walmart.aex.strategy.repository;

import com.walmart.aex.strategy.dto.PresentationUnitsStrategy;
import com.walmart.aex.strategy.entity.StrategyMerchCatgFixture;
import com.walmart.aex.strategy.entity.StrategyMerchCatgFixtureId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PresentationUnitStrategyRepository extends JpaRepository<StrategyMerchCatgFixture, StrategyMerchCatgFixtureId> {

    @Query(value ="SELECT DISTINCT new com.walmart.aex.strategy.dto.PresentationUnitsStrategy(smf.strategyMerchCatgFixtureId.strategyMerchCatgId.planStrategyId.planId, " +
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
            "            smf.fixtureType.fixtureTypeName, " +
            "            smf.minPresentationUnits, " +
            "            smf.maxPresentationUnits, " +
            "            ssf.minPresentationUnits," +
            "            ssf.maxPresentationUnits, " +
            "            sff.minPresentationUnits, " +
            "            sff.maxPresentationUnits)" +
            "       FROM StrategyMerchCatgFixture smf " +
            "       LEFT JOIN StrategySubCatgFixture ssf " +
            "            ON smf.strategyMerchCatgFixtureId.strategyMerchCatgId.planStrategyId.planId = ssf.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.planStrategyId.planId " +
            "            AND smf.strategyMerchCatgFixtureId.strategyMerchCatgId.planStrategyId.strategyId = ssf.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.planStrategyId.strategyId " +
            "            AND smf.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl0Nbr = ssf.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl0Nbr " +
            "            AND smf.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl1Nbr = ssf.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl1Nbr " +
            "            AND smf.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl2Nbr = ssf.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl2Nbr " +
            "            AND smf.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl3Nbr = ssf.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl3Nbr " +
            "            AND smf.strategyMerchCatgFixtureId.fixtureTypeId = ssf.strategySubCatgFixtureId.strategyMerchCatgFixtureId.fixtureTypeId " +
            "       LEFT JOIN StrategyFinelineFixture sff " +
            "            ON  sff.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.planStrategyId.planId = ssf.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.planStrategyId.planId " +
            "            AND sff.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.planStrategyId.strategyId = ssf.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.planStrategyId.strategyId " +
            "            AND sff.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl0Nbr = ssf.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl0Nbr " +
            "            AND sff.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl1Nbr = ssf.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl1Nbr " +
            "            AND sff.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl2Nbr = ssf.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl2Nbr " +
            "            AND sff.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl3Nbr = ssf.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl3Nbr " +
            "            AND sff.strategyFinelineFixtureId.strategySubCatgFixtureId.lvl4Nbr = ssf.strategySubCatgFixtureId.lvl4Nbr " +
            "            AND sff.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.fixtureTypeId = ssf.strategySubCatgFixtureId.strategyMerchCatgFixtureId.fixtureTypeId " +
            "        LEFT JOIN StrategyFineline sf  " +
            "            ON sf.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.planStrategyId.planId = sff.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.planStrategyId.planId  " +
            "            AND sf.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl0Nbr = sff.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl0Nbr  " +
            "            AND sf.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl1Nbr = sff.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl1Nbr  " +
            "            AND sf.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl2Nbr = sff.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl2Nbr  " +
            "            AND sf.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl3Nbr = sff.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl3Nbr  " +
            "            AND sf.strategyFinelineId.strategySubCatgId.lvl4Nbr = sff.strategyFinelineFixtureId.strategySubCatgFixtureId.lvl4Nbr  " +
            "            AND sf.strategyFinelineId.finelineNbr = sff.strategyFinelineFixtureId.finelineNbr " +
            "            AND sf.traitChoiceCode IS NOT NULL" +
            "        LEFT JOIN FixtureType fr " +
            "            ON fr.fixtureTypeId = sff.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.fixtureTypeId " +
            "        WHERE smf.strategyMerchCatgFixtureId.strategyMerchCatgId.planStrategyId.planId = :planId " +
            "            AND smf.strategyMerchCatgFixtureId.strategyMerchCatgId.planStrategyId.strategyId = :strategyId " +
            "            AND sf.channelId IN (1,3) " +
            "            AND (:lvl3Nbr is null or smf.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl3Nbr = :lvl3Nbr) " +
            "            AND (:lvl4Nbr is null or ssf.strategySubCatgFixtureId.lvl4Nbr = :lvl4Nbr) "+
            "            AND (:finelineNbr is null or sff.strategyFinelineFixtureId.finelineNbr = :finelineNbr) ")
    List<PresentationUnitsStrategy> getPresentationUnitsStrategy(@Param("planId") Long planId, @Param("strategyId") Long strategyId,
                                                                 @Param("lvl3Nbr") Integer lvl3Nbr, @Param("lvl4Nbr") Integer lvl4Nbr, @Param("finelineNbr") Integer finelineNbr);

}
