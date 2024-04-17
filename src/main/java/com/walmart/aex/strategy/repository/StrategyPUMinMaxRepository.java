package com.walmart.aex.strategy.repository;

import com.walmart.aex.strategy.entity.StrategyPUMinMax;
import com.walmart.aex.strategy.entity.StrategyPUMinMaxId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StrategyPUMinMaxRepository extends JpaRepository<StrategyPUMinMax, StrategyPUMinMaxId> {

}
