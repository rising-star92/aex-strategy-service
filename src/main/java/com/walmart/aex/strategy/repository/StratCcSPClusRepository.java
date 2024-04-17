package com.walmart.aex.strategy.repository;

import com.walmart.aex.strategy.dto.SizeResponseDTO;
import com.walmart.aex.strategy.entity.StrategyCcSPClusId;
import com.walmart.aex.strategy.entity.StrategyCcSPCluster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface StratCcSPClusRepository extends JpaRepository<StrategyCcSPCluster, StrategyCcSPClusId> {
    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "update dbo.strat_cc_sp_clus set size_profile_obj = :sizeProfileObj where plan_id = :plan_id and strategy_id = :strategy_id and analytics_cluster_id=:analytics_cluster_id\n" +
            "and rpt_lvl_0_nbr=:rpt_lvl_0_nbr and rpt_lvl_1_nbr=:rpt_lvl_1_nbr and rpt_lvl_2_nbr=:rpt_lvl_2_nbr and rpt_lvl_3_nbr=:rpt_lvl_3_nbr and rpt_lvl_4_nbr=:rpt_lvl_4_nbr and fineline_nbr=:fineline_nbr and style_nbr=:style_nbr and customer_choice=:customer_choice and channel_id=:channel_id", nativeQuery = true)
    void updateStratCcSPClusSizedata(@Param("plan_id") Long plan_id,@Param("sizeProfileObj") String sizeProfileObj, @Param("strategy_id") Long strategy_id, @Param("analytics_cluster_id") Integer analytics_cluster_id, @Param("rpt_lvl_0_nbr") Integer rpt_lvl_0_nbr, @Param("rpt_lvl_1_nbr") Integer rpt_lvl_1_nbr
            , @Param("rpt_lvl_2_nbr") Integer rpt_lvl_2_nbr, @Param("rpt_lvl_3_nbr") Integer rpt_lvl_3_nbr,@Param("rpt_lvl_4_nbr") Integer rpt_lvl_4_nbr,@Param("fineline_nbr") Integer fineline_nbr,@Param("style_nbr") String style_nbr,@Param("customer_choice") String customer_choice,@Param("channel_id") Integer channel_id);

    @Query(value = "select * from dbo.strat_cc_sp_clus where plan_id = :plan_id and fineline_nbr=:fineline_nbr and analytics_cluster_id in (0, :analytics_cluster_id)\n"+
    "and channel_id=:channel_id and style_nbr=:style_nbr and customer_choice=:customer_choice and strategy_id= :strategy_id",nativeQuery = true)
    List<StrategyCcSPCluster> getStrategyCcSPClusterData(@Param ("plan_id") Long plan_id ,@Param("fineline_nbr") Integer fineline_nbr,@Param("analytics_cluster_id") Integer analytics_cluster_id, @Param("channel_id") Integer channel_id,@Param("style_nbr") String style_nbr,@Param("customer_choice") String customer_choice, @Param("strategy_id") Long strategy_id);

    @Query(value = "select * from dbo.strat_cc_sp_clus where plan_id = :plan_id and fineline_nbr=:fineline_nbr and channel_id=:channel_id and style_nbr=:style_nbr\n"+
    "and customer_choice=:customer_choice and strategy_id= :strategy_id",nativeQuery = true)
    List<StrategyCcSPCluster> getStrategyCcAllSPClusterData(@Param ("plan_id") Long plan_id ,@Param("fineline_nbr") Integer fineline_nbr, @Param("channel_id") Integer channel_id,@Param("style_nbr") String style_nbr,@Param("customer_choice") String customer_choice, @Param("strategy_id") Long strategy_id);

    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "select * from dbo.strat_cc_sp_clus where plan_id = :plan_id and channel_id = :channel_id and customer_choice in (\n" +
            "  select distinct customer_choice from dbo.strat_cc_sp_clus where plan_id = :plan_id " +
            "  and channel_id = :channel_id and analytics_cluster_id = 0 and calc_sp_spread_ind = :calc_sp_spread_ind \n" +
            " ) and plan_id = :plan_id and channel_id = :channel_id "
            ,nativeQuery = true)
    List<StrategyCcSPCluster> getStrategyCcAllSPClusterDataWithActiveSpreadInd(@Param ("plan_id") Long plan_id ,
                                                                               @Param("channel_id") Integer channel_id,
                                                                               @Param("calc_sp_spread_ind") Integer calc_sp_spread_ind
    );

    @Query(value = "SELECT new com.walmart.aex.strategy.dto.SizeResponseDTO(" +
            " scsc.strategyCcSPClusId.strategyStyleSPClusId.strategyFinelineSPClusId.finelineNbr, " +
            " scsc.strategyCcSPClusId.strategyStyleSPClusId.styleNbr, " +
            " scsc.strategyCcSPClusId.ccId, " +
            " scsc.strategyCcSPClusId.strategyStyleSPClusId.strategyFinelineSPClusId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.planClusterStrategyId.analyticsClusterId, " +
            " sff.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.fixtureTypeId, " +
            " sff.merchMethodCode, " +
            " scsc.totalSizeProfilePct) " +
            "FROM StrategyCcSPCluster scsc " +
            "LEFT JOIN StrategyFinelineFixture sff " +
            " ON scsc.strategyCcSPClusId.strategyStyleSPClusId.strategyFinelineSPClusId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.planClusterStrategyId.planStrategyId.planId = sff.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.planStrategyId.planId " +
            " AND scsc.strategyCcSPClusId.strategyStyleSPClusId.strategyFinelineSPClusId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.lvl0Nbr = sff.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl0Nbr " +
            " AND scsc.strategyCcSPClusId.strategyStyleSPClusId.strategyFinelineSPClusId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.lvl1Nbr = sff.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl1Nbr " +
            " AND scsc.strategyCcSPClusId.strategyStyleSPClusId.strategyFinelineSPClusId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.lvl2Nbr = sff.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl2Nbr " +
            " AND scsc.strategyCcSPClusId.strategyStyleSPClusId.strategyFinelineSPClusId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.lvl3Nbr = sff.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl3Nbr " +
            " AND scsc.strategyCcSPClusId.strategyStyleSPClusId.strategyFinelineSPClusId.strategySubCatgSPClusId.lvl4Nbr = sff.strategyFinelineFixtureId.strategySubCatgFixtureId.lvl4Nbr " +
            " AND scsc.strategyCcSPClusId.strategyStyleSPClusId.strategyFinelineSPClusId.finelineNbr = sff.strategyFinelineFixtureId.finelineNbr " +
            "WHERE " +
            " scsc.strategyCcSPClusId.strategyStyleSPClusId.strategyFinelineSPClusId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.planClusterStrategyId.planStrategyId.planId = :planId " +
            " AND scsc.strategyCcSPClusId.strategyStyleSPClusId.strategyFinelineSPClusId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.planClusterStrategyId.planStrategyId.strategyId = :sizeProfileStrategyId " +
            " AND scsc.strategyCcSPClusId.strategyStyleSPClusId.strategyFinelineSPClusId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.channelId IN (:channelId, 3) " +
            " AND sff.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.planStrategyId.strategyId = :presentationStrategyId " +
            " AND scsc.strategyCcSPClusId.strategyStyleSPClusId.strategyFinelineSPClusId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.planClusterStrategyId.analyticsClusterId != 0 " +
            " AND (sff.merchMethodCode IS NULL OR scsc.totalSizeProfilePct != 100) "
    )
    List<SizeResponseDTO> getMerchMethodAndTotalSizePct(@Param ("planId") Long planId ,
                                                        @Param("sizeProfileStrategyId") Long sizeProfileStrategyId,
                                                        @Param("presentationStrategyId") Long presentationStrategyId,
                                                        @Param("channelId") Integer channelId);

    @Query("SELECT new com.walmart.aex.strategy.dto.SizeResponseDTO(" +
            " scsc.strategyCcSPClusId.strategyStyleSPClusId.strategyFinelineSPClusId.finelineNbr, " +
            " scsc.strategyCcSPClusId.strategyStyleSPClusId.styleNbr, " +
            " scsc.strategyCcSPClusId.ccId, " +
            " scsc.strategyCcSPClusId.strategyStyleSPClusId.strategyFinelineSPClusId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.planClusterStrategyId.analyticsClusterId, " +
            " scsc.totalSizeProfilePct) " +
            "FROM StrategyCcSPCluster scsc " +
            "WHERE " +
            " scsc.strategyCcSPClusId.strategyStyleSPClusId.strategyFinelineSPClusId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.planClusterStrategyId.planStrategyId.planId = :planId " +
            " AND scsc.strategyCcSPClusId.strategyStyleSPClusId.strategyFinelineSPClusId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.planClusterStrategyId.planStrategyId.strategyId = :sizeProfileStrategyId " +
            " AND scsc.strategyCcSPClusId.strategyStyleSPClusId.strategyFinelineSPClusId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.channelId IN (:channelId, 3) " +
            " AND scsc.totalSizeProfilePct != 100"
    )
    List<SizeResponseDTO> getTotalSizePct(@Param ("planId") Long planId ,
                                                        @Param("sizeProfileStrategyId") Long sizeProfileStrategyId,
                                                        @Param("channelId") Integer channelId);
}
