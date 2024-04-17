package com.walmart.aex.strategy.repository;

import com.walmart.aex.strategy.entity.StrategySubCategorySPCluster;
import com.walmart.aex.strategy.entity.StrategySubCatgSPClusId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface StratSubCatgSPClusterRepository extends JpaRepository<StrategySubCategorySPCluster, StrategySubCatgSPClusId> {


    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "update dbo.strat_subcatg_sp_clus set size_profile_obj = :sizeProfileObj where plan_id =:plan_id and strategy_id =:strategy_id and analytics_cluster_id=:analytics_cluster_id\n" +
            "and rpt_lvl_0_nbr=:rpt_lvl_0_nbr and rpt_lvl_1_nbr=:rpt_lvl_1_nbr and rpt_lvl_2_nbr=:rpt_lvl_2_nbr and rpt_lvl_3_nbr=:rpt_lvl_3_nbr and rpt_lvl_4_nbr=:rpt_lvl_4_nbr and channel_id=:channel_id", nativeQuery = true)
    void updateStartSubCatgSPClusterSizedata(@Param("plan_id") Long plan_id,@Param("sizeProfileObj") String sizeProfileObj, @Param("strategy_id") Long strategy_id, @Param("analytics_cluster_id") Integer analytics_cluster_id, @Param("rpt_lvl_0_nbr") Integer rpt_lvl_0_nbr, @Param("rpt_lvl_1_nbr") Integer rpt_lvl_1_nbr
            , @Param("rpt_lvl_2_nbr") Integer rpt_lvl_2_nbr, @Param("rpt_lvl_3_nbr") Integer rpt_lvl_3_nbr, @Param("rpt_lvl_4_nbr") Integer rpt_lvl_4_nbr,@Param("channel_id") Integer channel_id);
}
