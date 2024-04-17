package com.walmart.aex.strategy.repository;

import com.walmart.aex.strategy.entity.StrategySubCatgFixture;
import com.walmart.aex.strategy.entity.StrategySubCatgFixtureId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.Tuple;

public interface StrategySubCatgFixtureRepository extends JpaRepository<StrategySubCatgFixture, StrategySubCatgFixtureId> {

    @Query(value = "DELETE FROM dbo.strat_subcatg_fixture " +
            "WHERE plan_id = :planId AND strategy_id = :strategyId AND rpt_lvl_3_nbr = :lvl3Nbr AND rpt_lvl_4_nbr = :lvl4Nbr  " +
            "AND fixturetype_rollup_id NOT IN " +
            "   (SELECT DISTINCT fix.fixturetype_rollup_id FROM dbo.strat_fl_fixture fix " +
            "     WHERE fix.plan_id = :planId AND fix.strategy_id = :strategyId AND fix.rpt_lvl_3_nbr = :lvl3Nbr AND fix.rpt_lvl_4_nbr = :lvl4Nbr )", nativeQuery = true)
    @Modifying
    void deleteOrphanSubCatgFixtures(@Param("planId") Long planId, @Param("strategyId") Long strategyId, @Param("lvl3Nbr") Integer lvl3Nbr,
                                          @Param("lvl4Nbr") Integer lvl4Nbr);

    @Query(value = "SELECT MIN(CASE WHEN a.max_cc_per_type IS NULL THEN 0 ELSE 1 END) AS isCCRulesValid,\n" +
            "       MIN(CASE WHEN a.max_nbr_per_type IS NULL OR min_nbr_per_type IS NULL THEN 0 ELSE 1 END) AS isFinelineRulesValid\n" +
            "FROM strat_subcatg_fixture A JOIN strat_fl_fixture sff ON sff.plan_id = a.plan_id\n" +
            "    AND sff.rpt_lvl_0_nbr = a.rpt_lvl_0_nbr\n" +
            "    AND sff.rpt_lvl_1_nbr = a.rpt_lvl_1_nbr\n" +
            "    AND sff.rpt_lvl_2_nbr = a.rpt_lvl_2_nbr\n" +
            "    AND sff.rpt_lvl_3_nbr = a.rpt_lvl_3_nbr\n" +
            "    AND sff.rpt_lvl_4_nbr = a.rpt_lvl_4_nbr\n" +
            "    AND sff.fixturetype_rollup_id = a.fixturetype_rollup_id\n" +
            "JOIN strat_fl sf ON sff.plan_id = sf.plan_id\n" +
            "    AND sff.rpt_lvl_0_nbr = sf.rpt_lvl_0_nbr\n" +
            "    AND sff.rpt_lvl_1_nbr = sf.rpt_lvl_1_nbr\n" +
            "    AND sff.rpt_lvl_2_nbr = sf.rpt_lvl_2_nbr\n" +
            "    AND sff.rpt_lvl_3_nbr = sf.rpt_lvl_3_nbr\n" +
            "    AND sff.rpt_lvl_4_nbr = sf.rpt_lvl_4_nbr\n" +
            "    AND sff.fineline_nbr = sf.fineline_nbr\n" +
            "WHERE a.plan_id = :planId\n" +
            "  AND a.strategy_id = :strategyId \n" +
            "  AND sf.channel_id in (1, 3)\n" +
            "  AND ((0 IN (:lvl3List)) OR (a.rpt_lvl_3_nbr IN (:lvl3List)))\n" +
            "  AND ((0 IN (:lvl4List)) OR (a.rpt_lvl_4_nbr IN (:lvl4List)))\n" +
            "  AND sf.trait_choice_code IS NOT NULL", nativeQuery = true)
    Tuple isSubCategoryFixtureMinMaxInvalid(@Param("planId") Long planId, @Param("lvl3List") Integer[] lvl3List, @Param("lvl4List") Integer[] lvl4List, @Param("strategyId") Long strategyId);

}


