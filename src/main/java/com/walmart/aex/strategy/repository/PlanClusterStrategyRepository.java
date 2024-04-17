package com.walmart.aex.strategy.repository;

import com.walmart.aex.strategy.entity.PlanClusterStrategy;
import com.walmart.aex.strategy.entity.PlanClusterStrategyId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlanClusterStrategyRepository extends JpaRepository<PlanClusterStrategy, PlanClusterStrategyId> {
    Optional<PlanClusterStrategy> findByPlanClusterStrategyId(PlanClusterStrategyId planClusterStrategyId);
}
