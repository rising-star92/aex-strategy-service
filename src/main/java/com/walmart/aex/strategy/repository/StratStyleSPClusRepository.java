package com.walmart.aex.strategy.repository;

import com.walmart.aex.strategy.entity.StrategyStyleSPClusId;
import com.walmart.aex.strategy.entity.StrategyStyleSPCluster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface StratStyleSPClusRepository extends JpaRepository<StrategyStyleSPCluster, StrategyStyleSPClusId> {


    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "update dbo.strat_style_sp_clus set size_profile_obj = :sizeProfileObj where plan_id = :plan_id and strategy_id = :strategy_id and analytics_cluster_id=:analytics_cluster_id\n" +
            "and rpt_lvl_0_nbr=:rpt_lvl_0_nbr and rpt_lvl_1_nbr=:rpt_lvl_1_nbr and rpt_lvl_2_nbr=:rpt_lvl_2_nbr and rpt_lvl_3_nbr=:rpt_lvl_3_nbr and rpt_lvl_4_nbr=:rpt_lvl_4_nbr and fineline_nbr=:fineline_nbr and style_nbr=:style_nbr and channel_id=:channel_id", nativeQuery = true)
    void updateStratStyleSPClusSizedata(@Param("plan_id") Long plan_id,@Param("sizeProfileObj") String sizeProfileObj, @Param("strategy_id") Long strategy_id, @Param("analytics_cluster_id") Integer analytics_cluster_id, @Param("rpt_lvl_0_nbr") Integer rpt_lvl_0_nbr, @Param("rpt_lvl_1_nbr") Integer rpt_lvl_1_nbr
            , @Param("rpt_lvl_2_nbr") Integer rpt_lvl_2_nbr, @Param("rpt_lvl_3_nbr") Integer rpt_lvl_3_nbr,@Param("rpt_lvl_4_nbr") Integer rpt_lvl_4_nbr,@Param("fineline_nbr") Integer fineline_nbr,@Param("style_nbr") String style_nbr,@Param("channel_id") Integer channel_id);

    @Query(value = "select * from dbo.strat_style_sp_clus where plan_id = :plan_id and analytics_cluster_id in (0, :analytics_cluster_id)\n" +
    		"and fineline_nbr=:fineline_nbr and style_nbr=:style_nbr and channel_id=:channel_id and strategy_id= :strategy_id",nativeQuery = true)
    List<StrategyStyleSPCluster> getStrategyStyleSPClusterData(@Param ("plan_id") Long planId ,@Param ("analytics_cluster_id") Integer clusterId, 
    		@Param("fineline_nbr") Integer fineline_nbr, @Param ("style_nbr") String style, @Param("channel_id") Integer channel, @Param("strategy_id") Long strategy_id);

    @Query(value = "select * from dbo.strat_style_sp_clus where plan_id = :plan_id \n" + 
    		"and fineline_nbr=:fineline_nbr and style_nbr=:style_nbr and channel_id=:channel_id and strategy_id= :strategy_id",nativeQuery = true)
    List<StrategyStyleSPCluster> getStrategyStyleSPAllClusterData(@Param ("plan_id") Long planId ,
    		@Param("fineline_nbr") Integer fineline_nbr, @Param ("style_nbr") String style, @Param("channel_id") Integer channel, @Param("strategy_id") Long strategy_id);
}
