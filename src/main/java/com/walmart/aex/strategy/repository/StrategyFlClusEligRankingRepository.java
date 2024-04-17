package com.walmart.aex.strategy.repository;

import com.walmart.aex.strategy.dto.Rank.FlAndCcRankData;
import com.walmart.aex.strategy.entity.PlanStrategyId;
import com.walmart.aex.strategy.entity.StrategyFlClusEligRanking;
import com.walmart.aex.strategy.entity.StrategyFlClusEligRankingId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface StrategyFlClusEligRankingRepository extends JpaRepository<StrategyFlClusEligRanking, StrategyFlClusEligRankingId> {
    Optional<StrategyFlClusEligRanking> findByStrategyFlClusEligRankingId(StrategyFlClusEligRankingId strategyFlClusEligRankingId);

    Optional<Set<StrategyFlClusEligRanking>>
    findStrategyFlClusEligRankingByStrategyFlClusEligRankingId_PlanClusterStrategyId_PlanStrategyIdAndStrategyFlClusEligRankingId_lvl3NbrAndStrategyFlClusEligRankingId_lvl4NbrAndStrategyFlClusEligRankingId_finelineNbr
            (PlanStrategyId planStrategyId, Integer lvl3Nbr, Integer lvl4Nbr, Integer finelineNbr);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "UPDATE fl SET fl.is_eligible = CASE WHEN cc1.is_eligible = 1 AND is_eligible_sum = 0 THEN 0 ELSE 1 END FROM elig_fl_clus_rank fl JOIN(SELECT cc.plan_id, cc.fineline_nbr, count(DISTINCT cc.is_eligible) AS is_eligible, sum(cc.is_eligible * 1) AS is_eligible_sum, cc.analytics_cluster_id FROM elig_cc_clus_rank cc WHERE cc.plan_id = :plan_id AND cc.strategy_id = :strategy_id AND cc.fineline_nbr = :fineline_nbr GROUP BY cc.plan_id, cc.fineline_nbr, cc.analytics_cluster_id) cc1 ON fl.plan_id = cc1.plan_id AND fl.fineline_nbr = cc1.fineline_nbr and cc1.analytics_cluster_id = fl.analytics_cluster_id", nativeQuery = true)
    void updateIsEligForFlBasedOnAllCCs(@Param("plan_id") Long planId, @Param("strategy_id") Long strategyId, @Param("fineline_nbr") Integer finelineNbr);

    void deleteStrategyFlClusEligRankingByStrategyFlClusEligRankingId_PlanClusterStrategyId_PlanStrategyIdAndStrategyFlClusEligRankingId_lvl3NbrAndStrategyFlClusEligRankingId_lvl4NbrAndStrategyFlClusEligRankingId_finelineNbr
            (PlanStrategyId planStrategyId, Integer lvl3Nbr, Integer lvl4Nbr, Integer finelineNbr);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "UPDATE fl SET fl.select_status_id = CASE WHEN cc1.is_eligible = 1 AND is_eligible_sum = 0 THEN 0 WHEN cc1.is_eligible = 1 AND is_eligible_sum = cc_count THEN 1 ELSE 2 END, fl.markdown_yr_wk = cc2.max_markdown, fl.markdown_yrwk_desc = cc2.max_markdown_desc, fl.in_store_yr_wk = cc2.min_in_store, fl.in_store_yrwk_desc = cc2.min_in_store_desc FROM elig_fl_clus_rank fl JOIN (SELECT cc.plan_id, cc.fineline_nbr, count(DISTINCT cc.select_status_id) AS is_eligible, count(cc.select_status_id) AS cc_count, sum(cc.select_status_id * 1) AS is_eligible_sum, cc.analytics_cluster_id FROM elig_cc_clus_rank cc WHERE cc.plan_id = :plan_id AND cc.strategy_id = :strategy_id AND cc.fineline_nbr = :fineline_nbr GROUP BY cc.plan_id, cc.fineline_nbr, cc.analytics_cluster_id) cc1 ON fl.plan_id = cc1.plan_id AND fl.fineline_nbr = cc1.fineline_nbr and cc1.analytics_cluster_id = fl.analytics_cluster_id JOIN (SELECT cc.plan_id, cc.fineline_nbr, min(cc.in_store_yr_wk) as min_in_store, min(cc.in_store_yrwk_desc) as min_in_store_desc, max(cc.markdown_yr_wk) as max_markdown, max(cc.markdown_yrwk_desc) as max_markdown_desc, cc.analytics_cluster_id FROM elig_cc_clus_rank cc WHERE cc.select_status_id > 0 and cc.plan_id = :plan_id AND cc.strategy_id = :strategy_id AND cc.fineline_nbr = :fineline_nbr GROUP BY cc.plan_id, cc.fineline_nbr, cc.analytics_cluster_id) cc2 ON fl.plan_id = cc2.plan_id AND fl.fineline_nbr = cc2.fineline_nbr and cc2.analytics_cluster_id = fl.analytics_cluster_id;", nativeQuery = true)
    void updateIsEligAndDatesForFlBasedOnAllCCsPartial(@Param("plan_id") Long planId, @Param("strategy_id") Long strategyId, @Param("fineline_nbr") Integer finelineNbr);  

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "UPDATE cc0 SET cc0.select_status_id = CASE WHEN cc1.is_eligible = 1 AND is_eligible_sum = 0 THEN 0 WHEN cc1.is_eligible = 1 AND is_eligible_sum > 0 THEN 1 ELSE 2 END FROM elig_cc_clus_rank cc0 JOIN (SELECT cc.plan_id, cc.strategy_id, cc.fineline_nbr, count(DISTINCT cc.select_status_id) AS is_eligible, sum(cc.select_status_id * 1) AS is_eligible_sum, cc.style_nbr, cc.customer_choice FROM elig_cc_clus_rank cc WHERE cc.plan_id = :plan_id  AND cc.strategy_id = :strategy_id AND cc.fineline_nbr = :fineline_nbr AND cc.analytics_cluster_id >0 GROUP BY cc.plan_id, cc.strategy_id, cc.fineline_nbr, cc.style_nbr, cc.customer_choice) cc1 ON cc0.plan_id = cc1.plan_id AND cc0.strategy_id = cc1.strategy_id AND cc0.fineline_nbr = cc1.fineline_nbr AND cc0.style_nbr=cc1.style_nbr AND cc0.customer_choice=cc1.customer_choice WHERE cc0.analytics_cluster_id=0;", nativeQuery = true)
    void updateIsEligForCCAllBasedOnCCs(@Param("plan_id") Long planId, @Param("strategy_id") Long strategyId, @Param("fineline_nbr") Integer finelineNbr);

    @Query(value = "SELECT new com.walmart.aex.strategy.dto.Rank.FlAndCcRankData(ps.planStrategyId.planId , " +
            " sf.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl0Nbr , " +
            " sf.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl1Nbr , " +
            " sf.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl2Nbr , " +
            " sf.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl3Nbr , " +
            " sf.strategyFinelineId.strategySubCatgId.lvl4Nbr , " +
            "elgFl.strategyFlClusEligRankingId.finelineNbr , " +
            "(CASE WHEN (elgFl.merchantOverrideRank IS NULL) THEN (elgMet.analyticsClusterRank) " +
            "ELSE (elgFl.merchantOverrideRank) END) AS flRank ) " +
            "FROM PlanStrategy ps " +
            "INNER JOIN PlanClusterStrategy psc " +
            "ON ps.planStrategyId.planId = psc.planClusterStrategyId.planStrategyId.planId " +
            "AND ps.planStrategyId.strategyId = psc.planClusterStrategyId.planStrategyId.strategyId " +
            "INNER JOIN StrategyFineline sf " +
            "ON ps.planStrategyId.planId = sf.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.planStrategyId.planId " +
            "AND ps.planStrategyId.strategyId = sf.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.planStrategyId.strategyId " +
            "INNER JOIN StrategyFlClusEligRanking elgFl " +
            "ON elgFl.strategyFlClusEligRankingId.planClusterStrategyId.planStrategyId.planId = psc.planClusterStrategyId.planStrategyId.planId " +
            "AND elgFl.strategyFlClusEligRankingId.planClusterStrategyId.planStrategyId.strategyId = psc.planClusterStrategyId.planStrategyId.strategyId " +
            "AND elgFl.strategyFlClusEligRankingId.planClusterStrategyId.analyticsClusterId = psc.planClusterStrategyId.analyticsClusterId " +
            "AND elgFl.strategyFlClusEligRankingId.lvl0Nbr = sf.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl0Nbr " +
            "AND elgFl.strategyFlClusEligRankingId.lvl1Nbr = sf.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl1Nbr " +
            "AND elgFl.strategyFlClusEligRankingId.lvl2Nbr = sf.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl2Nbr " +
            "AND elgFl.strategyFlClusEligRankingId.lvl3Nbr = sf.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl3Nbr " +
            "AND elgFl.strategyFlClusEligRankingId.lvl4Nbr = sf.strategyFinelineId.strategySubCatgId.lvl4Nbr " +
            "AND elgFl.strategyFlClusEligRankingId.finelineNbr = sf.strategyFinelineId.finelineNbr " +
            "INNER JOIN StrategyFlClusMetrics elgMet " +
            "ON elgFl.strategyFlClusEligRankingId.planClusterStrategyId.planStrategyId.planId = elgMet.strategyFlClusEligRankingId.planClusterStrategyId.planStrategyId.planId " +
            "AND elgFl.strategyFlClusEligRankingId.planClusterStrategyId.analyticsClusterId = elgMet.strategyFlClusEligRankingId.planClusterStrategyId.analyticsClusterId " +
            "AND elgFl.strategyFlClusEligRankingId.lvl0Nbr = elgMet.strategyFlClusEligRankingId.lvl0Nbr " +
            "AND elgFl.strategyFlClusEligRankingId.lvl1Nbr = elgMet.strategyFlClusEligRankingId.lvl1Nbr " +
            "AND elgFl.strategyFlClusEligRankingId.lvl2Nbr = elgMet.strategyFlClusEligRankingId.lvl2Nbr " +
            "AND elgFl.strategyFlClusEligRankingId.lvl2Nbr = elgMet.strategyFlClusEligRankingId.lvl2Nbr " +
            "AND elgFl.strategyFlClusEligRankingId.lvl3Nbr = elgMet.strategyFlClusEligRankingId.lvl3Nbr " +
            "AND elgFl.strategyFlClusEligRankingId.lvl4Nbr = elgMet.strategyFlClusEligRankingId.lvl4Nbr " +
            "AND elgFl.strategyFlClusEligRankingId.finelineNbr = elgMet.strategyFlClusEligRankingId.finelineNbr " +
            "WHERE (ps.planStrategyId.planId in (:planIds) or :includelPlanIds is null) " +
            "AND (sf.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl0Nbr in (:lvl0List) or :includelvl0NbrToFilter is null) " +
            "AND (sf.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl1Nbr in (:lvl1List) or :includelvl1NbrToFilter is null) " +
            "AND (sf.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl2Nbr in (:lvl2List) or :includelvl2NbrToFilter is null) " +
            "AND (sf.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl3Nbr in (:lvl3List) or :includelvl3NbrToFilter is null) " +
            "AND (sf.strategyFinelineId.strategySubCatgId.lvl4Nbr in (:lvl4List) or :includelvl4NbrToFilter is null) " +
            "AND (sf.strategyFinelineId.finelineNbr =:finelineNbr or :finelineNbr is null)  " +
            "AND (elgFl.strategyFlClusEligRankingId.planClusterStrategyId.analyticsClusterId = :cluster) " +
            "AND (psc.planClusterStrategyId.analyticsClusterId = :cluster)" +
            "AND (sf.channelId IN (:channel,3) or :channel is null)")
    List<FlAndCcRankData> getFinelineRankData(@Param("includelPlanIds") Boolean includelPlanIds,
                                                             @Param("planIds") List<Long> planIds, @Param("includelvl0NbrToFilter") Boolean includelvl0NbrToFilter,
                                                             @Param("lvl0List") List<Integer> lvl0List, @Param("includelvl1NbrToFilter") Boolean includelvl1NbrToFilter,
                                                             @Param("lvl1List") List<Integer> lvl1List, @Param("includelvl2NbrToFilter") Boolean includelvl2NbrToFilter,
                                                             @Param("lvl2List") List<Integer> lvl2List, @Param("includelvl3NbrToFilter") Boolean includelvl3NbrToFilter,
                                                             @Param("lvl3List") List<Integer> lvl3List, @Param("includelvl4NbrToFilter") Boolean includelvl4NbrToFilter,
                                                             @Param("lvl4List") List<Integer> lvl4List, @Param("finelineNbr") Integer finelineNbr ,
                                                             @Param("channel") Integer channel,@Param("cluster")Integer cluster);



}
