package com.walmart.aex.strategy.repository;

import com.walmart.aex.strategy.entity.StrategyFlClusEligRankingId;
import com.walmart.aex.strategy.entity.StrategyFlClusMetrics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface StrategyFlClustMetricRepository extends JpaRepository<StrategyFlClusMetrics, StrategyFlClusEligRankingId> {
    Optional<StrategyFlClusMetrics> findByStrategyFlClusEligRankingId(StrategyFlClusEligRankingId strategyFlClusEligRankingId);

    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "update dbo.elig_fl_clus_metrics set analytics_cluster_rank = r.rn from dbo.elig_fl_clus_metrics s \n" +
            "inner join(select plan_id, strategy_id, fineline_nbr, dense_rank() over(PARTITION BY analytics_cluster_id ORDER BY forecasted_dollars desc) rn \n" +
            "from dbo.elig_fl_clus_metrics where plan_id = :plan_id and strategy_id = :strategy_id) r on r.plan_id = s.plan_id and r.strategy_id = s.strategy_id \n" +
            "and r.fineline_nbr = s.fineline_nbr", nativeQuery = true)
    void updateAlgoClusterRanking(@Param("plan_id") Long planId, @Param("strategy_id") Long strategyId);
}
