package com.walmart.aex.strategy.repository;

import com.walmart.aex.strategy.entity.StrategyFlMktClusElig;
import com.walmart.aex.strategy.entity.StrategyFlMktClusEligId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StrategyFlMktClusEligRepository extends JpaRepository<StrategyFlMktClusElig, StrategyFlMktClusEligId> {
    Optional<StrategyFlMktClusElig> findByStrategyFlMktClusEligId(StrategyFlMktClusEligId strategyFlMktClusEligId);
}
