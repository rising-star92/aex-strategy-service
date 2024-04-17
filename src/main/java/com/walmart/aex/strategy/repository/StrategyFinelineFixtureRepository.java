package com.walmart.aex.strategy.repository;

import com.walmart.aex.strategy.entity.StrategyFinelineFixture;
import com.walmart.aex.strategy.entity.StrategyFinelineFixtureId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StrategyFinelineFixtureRepository extends JpaRepository<StrategyFinelineFixture, StrategyFinelineFixtureId> {
    @Query(value = "select fix from StrategyFinelineFixture fix where fix.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.planStrategyId.planId= :planId AND fix.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.planStrategyId.strategyId= :strategyId AND fix.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl0Nbr= :lvl0Nbr AND fix.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl1Nbr= :lvl1Nbr AND fix.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl2Nbr= :lvl2Nbr AND fix.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl3Nbr= :lvl3Nbr AND fix.strategyFinelineFixtureId.strategySubCatgFixtureId.lvl4Nbr= :lvl4Nbr AND fix.strategyFinelineFixtureId.finelineNbr= :finelineNbr")
    List<StrategyFinelineFixture> findFineLines_ByPlan_Id_And_Strategy_IdAnd_Cat_IdAndSub_Cat_IdAndFineline_nbr(@Param("planId") Long planId, @Param("strategyId") Long strategyId, @Param("lvl0Nbr") Integer lvl0Nbr, @Param("lvl1Nbr") Integer lvl1Nbr, @Param("lvl2Nbr") Integer lvl2Nbr, @Param("lvl3Nbr") Integer lvl3Nbr, @Param("lvl4Nbr") Integer lvl4Nbr, @Param("finelineNbr") Integer finelineNbr);

    @Query(value = "select fix from StrategyFinelineFixture fix where fix.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.planStrategyId.planId= :planId AND fix.strategyFinelineFixtureId.finelineNbr IN (:finelineNbr)")
    List <StrategyFinelineFixture> findByPlan_idAndFineline_nbr(@Param("planId") Long planId, @Param("finelineNbr") List<Integer> finelineNbr);

}
