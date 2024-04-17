package com.walmart.aex.strategy.repository;

import com.walmart.aex.strategy.entity.StrategyFineLineSPClusId;
import com.walmart.aex.strategy.entity.StrategyFineLineSPCluster;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface StratFineLineSPClusRepository extends JpaRepository<StrategyFineLineSPCluster,StrategyFineLineSPClusId> {


    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "update dbo.strat_fl_sp_clus set size_profile_obj = :sizeProfileObj where plan_id = :plan_id and strategy_id = :strategy_id and analytics_cluster_id=:analytics_cluster_id\n" +
            "and rpt_lvl_0_nbr=:rpt_lvl_0_nbr and rpt_lvl_1_nbr=:rpt_lvl_1_nbr and rpt_lvl_2_nbr=:rpt_lvl_2_nbr and rpt_lvl_3_nbr=:rpt_lvl_3_nbr and rpt_lvl_4_nbr=:rpt_lvl_4_nbr and fineline_nbr=:fineline_nbr and channel_id=:channel_id", nativeQuery = true)
    void updateStartFineLineSPClusSizedata(@Param("plan_id") Long plan_id,@Param("sizeProfileObj") String sizeProfileObj, @Param("strategy_id") Long strategy_id, @Param("analytics_cluster_id") Integer analytics_cluster_id, @Param("rpt_lvl_0_nbr") Integer rpt_lvl_0_nbr, @Param("rpt_lvl_1_nbr") Integer rpt_lvl_1_nbr
            , @Param("rpt_lvl_2_nbr") Integer rpt_lvl_2_nbr, @Param("rpt_lvl_3_nbr") Integer rpt_lvl_3_nbr,@Param("rpt_lvl_4_nbr") Integer rpt_lvl_4_nbr,@Param("fineline_nbr") Integer fineline_nbr,@Param("channel_id") Integer channel_id);

    @Query(value = "select * from dbo.strat_fl_sp_clus where plan_id = :plan_id and fineline_nbr=:fineline_nbr and analytics_cluster_id in (0, :analytics_cluster_id)\n"+
             "and channel_id=:channel_id and strategy_id= :strategy_id",nativeQuery = true)
    List<StrategyFineLineSPCluster> getStrategyFinelineSPClusterData(@Param ("plan_id") Long plan_id ,@Param("fineline_nbr") Integer fineline_nbr,@Param("analytics_cluster_id") Integer analytics_cluster_id, @Param("channel_id") Integer channel_id, @Param("strategy_id") Long strategy_id);

    @Query(value = "select * from dbo.strat_fl_sp_clus where plan_id = :plan_id and fineline_nbr=:fineline_nbr\n"+
             "and channel_id=:channel_id and strategy_id= :strategy_id",nativeQuery = true)
    List<StrategyFineLineSPCluster> getStrategyFinelineSPAllCluster(@Param ("plan_id") Long plan_id ,@Param("fineline_nbr") Integer fineline_nbr, @Param("channel_id") Integer channel_id, @Param("strategy_id") Long strategy_id);
}

