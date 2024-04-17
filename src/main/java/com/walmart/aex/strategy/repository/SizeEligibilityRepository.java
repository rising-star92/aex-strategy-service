package com.walmart.aex.strategy.repository;

import com.walmart.aex.strategy.entity.PlanClusterStrategyId;
import com.walmart.aex.strategy.entity.StrategyMerchCategorySPCluster;
import com.walmart.aex.strategy.entity.StrategyMerchCatgSPClusId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SizeEligibilityRepository extends JpaRepository<StrategyMerchCategorySPCluster, StrategyMerchCatgSPClusId> {
    Optional<List<StrategyMerchCategorySPCluster>>
    findStrategyMerchCategorySPClusterByStrategyMerchCatgSPClusId_PlanClusterStrategyIdAndStrategyMerchCatgSPClusId_lvl3NbrAndStrategyMerchCatgSPClusId_channelId(PlanClusterStrategyId planClusterStrategyId, Integer lvl3Nbr, Integer channelId);
}
