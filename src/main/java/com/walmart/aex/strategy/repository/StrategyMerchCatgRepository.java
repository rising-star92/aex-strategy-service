package com.walmart.aex.strategy.repository;

import com.walmart.aex.strategy.entity.StrategyMerchCatg;
import com.walmart.aex.strategy.entity.StrategyMerchCatgId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StrategyMerchCatgRepository extends JpaRepository<StrategyMerchCatg, StrategyMerchCatgId> {
    Optional<StrategyMerchCatg> findByStrategyMerchCatgId(StrategyMerchCatgId strategyMerchCatgId);
}
