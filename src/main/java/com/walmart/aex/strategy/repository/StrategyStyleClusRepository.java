package com.walmart.aex.strategy.repository;

import com.walmart.aex.strategy.entity.PlanStrategyId;
import com.walmart.aex.strategy.entity.StrategyStyleClus;
import com.walmart.aex.strategy.entity.StrategyStyleClusId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StrategyStyleClusRepository extends JpaRepository<StrategyStyleClus, StrategyStyleClusId> {
    Optional<StrategyStyleClus> findByStrategyStyleClusId(StrategyStyleClusId strategyStyleClusId);

    void deleteStrategyStyleClusByStrategyStyleClusId_StrategyFlClusEligRankingId_PlanClusterStrategyId_planStrategyIdAndStrategyStyleClusId_StrategyFlClusEligRankingId_lvl3NbrAndStrategyStyleClusId_StrategyFlClusEligRankingId_lvl4NbrAndStrategyStyleClusId_StrategyFlClusEligRankingId_finelineNbrAndStrategyStyleClusId_styleNbr(
            PlanStrategyId planStrategyId, Integer lvl3Nbr, Integer lvl4Nbr, Integer finelineNbr, String styleNbr);
}
