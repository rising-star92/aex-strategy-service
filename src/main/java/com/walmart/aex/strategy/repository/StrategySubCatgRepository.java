package com.walmart.aex.strategy.repository;

import com.walmart.aex.strategy.entity.StrategySubCatg;
import com.walmart.aex.strategy.entity.StrategySubCatgId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StrategySubCatgRepository extends JpaRepository<StrategySubCatg, StrategySubCatgId> {
    Optional<StrategySubCatg> findByStrategySubCatgId(StrategySubCatgId strategySubCatgId);
}
