package com.walmart.aex.strategy.repository;

import com.walmart.aex.strategy.entity.StrategyCluster;
import com.walmart.aex.strategy.entity.StrategyClusterId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface StrategyClusterRepository extends JpaRepository<StrategyCluster, StrategyClusterId> {
    @Query(value = "select Distinct sc.strategyClusterId.analyticsClusterId from StrategyCluster sc where sc.strategyClusterId.strategyId = :strategyId")
    List<Integer> findAllWeatherClustersForStrategy(@Param("strategyId") Long strategyId);
}
