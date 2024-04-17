package com.walmart.aex.strategy.repository;

import com.walmart.aex.strategy.dto.AnalyticsClusterStoreDTO;
import com.walmart.aex.strategy.entity.StrategyClusterStore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface StrategyClusterStoreRepository extends JpaRepository<StrategyClusterStore, Long> {

    @Query(value = "select new com.walmart.aex.strategy.dto.AnalyticsClusterStoreDTO(strategyClusterStoreId.analyticsClusterId as analyticsClusterId, " +
            "stateProvinceCode, " +
            "count(strategyClusterStoreId.storeNbr) as storeCount) from StrategyClusterStore " +
            "where strategyClusterStoreId.strategyId = :strategyId group by strategyClusterStoreId.analyticsClusterId, stateProvinceCode")
    List<AnalyticsClusterStoreDTO> getAnalyticsStateProvinceStoreCount(@Param("strategyId") Long strategyId);

    @Query(value = "select new com.walmart.aex.strategy.dto.AnalyticsClusterStoreDTO(tps.traitProgramStoreId.programId, " +
            "scs.strategyClusterStoreId.analyticsClusterId, " +
            "scs.stateProvinceCode, " +
            "count(scs.strategyClusterStoreId.storeNbr) as storeCount) " +
            "FROM TraitProgramStore tps " +
            "JOIN StrategyClusterStore scs ON tps.traitProgramStoreId.storeNbr = scs.strategyClusterStoreId.storeNbr " +
            "WHERE scs.strategyClusterStoreId.strategyId = :strategyId GROUP BY " +
            "scs.strategyClusterStoreId.analyticsClusterId, scs.stateProvinceCode, tps.traitProgramStoreId.programId " +
            "ORDER BY tps.traitProgramStoreId.programId, scs.strategyClusterStoreId.analyticsClusterId ")
    List<AnalyticsClusterStoreDTO> getTraitClusterStateProvinceStoreCount(@Param("strategyId") Long strategyId);

}
