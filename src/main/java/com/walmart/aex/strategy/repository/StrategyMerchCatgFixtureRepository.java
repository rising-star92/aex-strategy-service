package com.walmart.aex.strategy.repository;

import com.walmart.aex.strategy.entity.StrategyMerchCatgFixture;
import com.walmart.aex.strategy.entity.StrategyMerchCatgFixtureId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StrategyMerchCatgFixtureRepository extends JpaRepository<StrategyMerchCatgFixture, StrategyMerchCatgFixtureId> {

    @Query(value = "DELETE FROM dbo.strat_merchcatg_fixture " +
            "WHERE plan_id = :planId AND strategy_id = :strategyId AND rpt_lvl_3_nbr = :lvl3Nbr " +
            "AND fixturetype_rollup_id NOT IN" +
            "   (SELECT DISTINCT fixturetype_rollup_id FROM dbo.strat_subcatg_fixture WHERE " +
            "   plan_id = :planId AND strategy_id = :strategyId AND rpt_lvl_3_nbr = :lvl3Nbr )", nativeQuery = true)
    @Modifying
    void deleteOrphanMerchCatgFixtures(@Param("planId") Long planId, @Param("strategyId") Long strategyId, @Param("lvl3Nbr") Integer lvl3Nbr);

}
