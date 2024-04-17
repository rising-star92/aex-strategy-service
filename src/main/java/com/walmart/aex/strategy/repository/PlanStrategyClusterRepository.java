package com.walmart.aex.strategy.repository;

import com.walmart.aex.strategy.entity.PlanStrategy;
import com.walmart.aex.strategy.entity.PlanStrategyId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanStrategyClusterRepository extends JpaRepository<PlanStrategy, PlanStrategyId> {
}
