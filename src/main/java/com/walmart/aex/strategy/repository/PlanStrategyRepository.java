package com.walmart.aex.strategy.repository;

import com.walmart.aex.strategy.entity.PlanStrategy;
import com.walmart.aex.strategy.entity.PlanStrategyId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;


@Repository
public interface PlanStrategyRepository extends JpaRepository<PlanStrategy, PlanStrategyId>, QuerydslPredicateExecutor<PlanStrategy> {

}
