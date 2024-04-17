package com.walmart.aex.strategy.repository;

import com.walmart.aex.strategy.entity.StrategyGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StrategyGroupRepository extends JpaRepository<StrategyGroup, Long> {
    @Query(value ="select sg.strategyId from StrategyGroup sg Join StrategyGroupType sgt on sg.strategyGroupTypeId = sgt.strategyGroupTypeId Where " +
            "sgt.strategyGroupTypeId = :strategy_group_type_code AND (:season_code is null or sg.seasonCode = :season_code) " +
            "AND (:fiscal_year is null or sg.fiscalYear = :fiscal_year)")
    Long getStrategyIdBySeasonCd(@Param("strategy_group_type_code") Integer strategyGroupTypeId,
                                 @Param("season_code") String seasonCode, @Param("fiscal_year") Integer fiscalYear);


    @Query(value ="select sg.strategyId from StrategyGroup sg Join StrategyGroupType sgt on sg.strategyGroupTypeId = sgt.strategyGroupTypeId " +
            "Join PlanStrategy ps ON ps.planStrategyId.strategyId = sg.strategyId " +
            "Where sgt.strategyGroupTypeId = :strategy_group_type_code AND ps.planStrategyId.planId = :plan_id")
    Long getStrategyIdByStrategyGroupTypeAndPlanId(@Param("strategy_group_type_code") Integer strategyGroupTypeId,
                                 @Param("plan_id") Long planId);

    @Query("select sg.strategyId from  StrategyGroup sg where sg.analyticsClusterGroupDesc= :flow_plan")
    Long findStratergyId(String flow_plan);

    @Query(value = "select sg.strategyId from StrategyGroup sg where sg.strategyGroupTypeId = :strategy_group_type_code")
    Long getStrategyIdByStrategyGroupType(@Param("strategy_group_type_code")Long strategyGroupTypeId);

    Optional<List<StrategyGroup>> findAllByStrategyGroupTypeIdIn(List<Long> strategyGroupTypeIds);
}
