package com.walmart.aex.strategy.repository;

import com.walmart.aex.strategy.dto.SizeResponseDTO;
import com.walmart.aex.strategy.entity.StrategyCcSPClusId;
import com.walmart.aex.strategy.entity.StrategyCcSPCluster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface CcSizeProfileRepo extends JpaRepository<StrategyCcSPCluster, StrategyCcSPClusId> {

    @Query(value="select new com.walmart.aex.strategy.dto.SizeResponseDTO(ps.planStrategyId.planId, ps.planStrategyId.strategyId, " +
            "sc.planClusterStrategyId.analyticsClusterId , " +
            " smsc.strategyMerchCatgSPClusId.lvl0Nbr , " +
            " smsc.strategyMerchCatgSPClusId.lvl1Nbr , " +
            " smsc.strategyMerchCatgSPClusId.lvl2Nbr , " +
            " smsc.strategyMerchCatgSPClusId.lvl3Nbr , " +
            " sssc.strategySubCatgSPClusId.lvl4Nbr , " +
            " sfsc.strategyIFineLineId.finelineNbr , " +
            " ssc.strategyStyleSPClusId.styleNbr , " +
            " scsc.strategyCcSPClusId.ccId ," +
            " scsc.sizeProfileObj as ccSizeObj) " +
            "FROM PlanStrategy ps " +
            "INNER JOIN PlanClusterStrategy sc " +
            "ON ps.planStrategyId.planId = sc.planClusterStrategyId.planStrategyId.planId " +
            "AND ps.planStrategyId.strategyId = sc.planClusterStrategyId.planStrategyId.strategyId " +
            "INNER JOIN StrategyMerchCategorySPCluster smsc " +
            "ON sc.planClusterStrategyId.planStrategyId.planId = smsc.strategyMerchCatgSPClusId.planClusterStrategyId.planStrategyId.planId " +
            "AND sc.planClusterStrategyId.planStrategyId.strategyId = smsc.strategyMerchCatgSPClusId.planClusterStrategyId.planStrategyId.strategyId " +
            "AND sc.planClusterStrategyId.analyticsClusterId = smsc.strategyMerchCatgSPClusId.planClusterStrategyId.analyticsClusterId " +
            "INNER JOIN StrategySubCategorySPCluster sssc " +
            "ON smsc.strategyMerchCatgSPClusId.planClusterStrategyId.planStrategyId.planId = sssc.strategySubCatgSPClusId.strategyMerchCatgSPClusId.planClusterStrategyId.planStrategyId.planId " +
            "AND smsc.strategyMerchCatgSPClusId.planClusterStrategyId.planStrategyId.strategyId = sssc.strategySubCatgSPClusId.strategyMerchCatgSPClusId.planClusterStrategyId.planStrategyId.strategyId " +
            "AND smsc.strategyMerchCatgSPClusId.planClusterStrategyId.analyticsClusterId = sssc.strategySubCatgSPClusId.strategyMerchCatgSPClusId.planClusterStrategyId.analyticsClusterId " +
            "AND smsc.strategyMerchCatgSPClusId.lvl0Nbr = sssc.strategySubCatgSPClusId.strategyMerchCatgSPClusId.lvl0Nbr " +
            "AND smsc.strategyMerchCatgSPClusId.lvl1Nbr = sssc.strategySubCatgSPClusId.strategyMerchCatgSPClusId.lvl1Nbr " +
            "AND smsc.strategyMerchCatgSPClusId.lvl2Nbr = sssc.strategySubCatgSPClusId.strategyMerchCatgSPClusId.lvl2Nbr " +
            "AND smsc.strategyMerchCatgSPClusId.lvl3Nbr = sssc.strategySubCatgSPClusId.strategyMerchCatgSPClusId.lvl3Nbr " +
            "AND smsc.strategyMerchCatgSPClusId.channelId = sssc.strategySubCatgSPClusId.strategyMerchCatgSPClusId.channelId " +
            "AND smsc.strategyMerchCatgSPClusId.planClusterStrategyId.analyticsClusterId = sssc.strategySubCatgSPClusId.strategyMerchCatgSPClusId.planClusterStrategyId.analyticsClusterId " +
            "INNER JOIN StrategyFineLineSPCluster sfsc " +
            "ON sssc.strategySubCatgSPClusId.strategyMerchCatgSPClusId.planClusterStrategyId.planStrategyId.planId = sfsc.strategyIFineLineId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.planClusterStrategyId.planStrategyId.planId " +
            "AND sssc.strategySubCatgSPClusId.strategyMerchCatgSPClusId.planClusterStrategyId.planStrategyId.strategyId = sfsc.strategyIFineLineId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.planClusterStrategyId.planStrategyId.strategyId " +
            "AND sssc.strategySubCatgSPClusId.strategyMerchCatgSPClusId.lvl0Nbr = sfsc.strategyIFineLineId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.lvl0Nbr " +
            "AND sssc.strategySubCatgSPClusId.strategyMerchCatgSPClusId.lvl1Nbr = sfsc.strategyIFineLineId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.lvl1Nbr " +
            "AND sssc.strategySubCatgSPClusId.strategyMerchCatgSPClusId.lvl2Nbr = sfsc.strategyIFineLineId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.lvl2Nbr " +
            "AND sssc.strategySubCatgSPClusId.strategyMerchCatgSPClusId.lvl3Nbr = sfsc.strategyIFineLineId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.lvl3Nbr " +
            "AND sssc.strategySubCatgSPClusId.lvl4Nbr = sfsc.strategyIFineLineId.strategySubCatgSPClusId.lvl4Nbr " +
            "AND sssc.strategySubCatgSPClusId.strategyMerchCatgSPClusId.channelId = sfsc.strategyIFineLineId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.channelId " +
            "AND sssc.strategySubCatgSPClusId.strategyMerchCatgSPClusId.planClusterStrategyId.analyticsClusterId = sfsc.strategyIFineLineId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.planClusterStrategyId.analyticsClusterId " +
            "INNER JOIN StrategyStyleSPCluster ssc "+
            "ON sfsc.strategyIFineLineId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.planClusterStrategyId.planStrategyId.planId = ssc.strategyStyleSPClusId.strategyFinelineSPClusId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.planClusterStrategyId.planStrategyId.planId " +
            "AND sfsc.strategyIFineLineId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.planClusterStrategyId.planStrategyId.strategyId = ssc.strategyStyleSPClusId.strategyFinelineSPClusId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.planClusterStrategyId.planStrategyId.strategyId " +
            "AND sfsc.strategyIFineLineId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.lvl0Nbr = ssc.strategyStyleSPClusId.strategyFinelineSPClusId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.lvl0Nbr " +
            "AND sfsc.strategyIFineLineId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.lvl1Nbr = ssc.strategyStyleSPClusId.strategyFinelineSPClusId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.lvl1Nbr " +
            "AND sfsc.strategyIFineLineId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.lvl2Nbr = ssc.strategyStyleSPClusId.strategyFinelineSPClusId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.lvl2Nbr " +
            "AND sfsc.strategyIFineLineId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.lvl3Nbr = ssc.strategyStyleSPClusId.strategyFinelineSPClusId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.lvl3Nbr " +
            "AND sfsc.strategyIFineLineId.strategySubCatgSPClusId.lvl4Nbr = ssc.strategyStyleSPClusId.strategyFinelineSPClusId.strategySubCatgSPClusId.lvl4Nbr " +
            "AND sfsc.strategyIFineLineId.finelineNbr = ssc.strategyStyleSPClusId.strategyFinelineSPClusId.finelineNbr " +
            "AND sfsc.strategyIFineLineId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.channelId = ssc.strategyStyleSPClusId.strategyFinelineSPClusId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.channelId " +
            "AND sfsc.strategyIFineLineId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.planClusterStrategyId.analyticsClusterId = ssc.strategyStyleSPClusId.strategyFinelineSPClusId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.planClusterStrategyId.analyticsClusterId " +
            "INNER JOIN StrategyCcSPCluster scsc " +
            "ON ssc.strategyStyleSPClusId.strategyFinelineSPClusId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.planClusterStrategyId.planStrategyId.planId = scsc.strategyCcSPClusId.strategyStyleSPClusId.strategyFinelineSPClusId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.planClusterStrategyId.planStrategyId.planId " +
            "AND ssc.strategyStyleSPClusId.strategyFinelineSPClusId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.planClusterStrategyId.planStrategyId.strategyId = scsc.strategyCcSPClusId.strategyStyleSPClusId.strategyFinelineSPClusId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.planClusterStrategyId.planStrategyId.strategyId " +
            "AND ssc.strategyStyleSPClusId.strategyFinelineSPClusId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.lvl0Nbr = scsc.strategyCcSPClusId.strategyStyleSPClusId.strategyFinelineSPClusId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.lvl0Nbr " +
            "AND ssc.strategyStyleSPClusId.strategyFinelineSPClusId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.lvl1Nbr = scsc.strategyCcSPClusId.strategyStyleSPClusId.strategyFinelineSPClusId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.lvl1Nbr " +
            "AND ssc.strategyStyleSPClusId.strategyFinelineSPClusId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.lvl2Nbr = scsc.strategyCcSPClusId.strategyStyleSPClusId.strategyFinelineSPClusId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.lvl2Nbr " +
            "AND ssc.strategyStyleSPClusId.strategyFinelineSPClusId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.lvl3Nbr = scsc.strategyCcSPClusId.strategyStyleSPClusId.strategyFinelineSPClusId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.lvl3Nbr " +
            "AND ssc.strategyStyleSPClusId.strategyFinelineSPClusId.strategySubCatgSPClusId.lvl4Nbr = scsc.strategyCcSPClusId.strategyStyleSPClusId.strategyFinelineSPClusId.strategySubCatgSPClusId.lvl4Nbr " +
            "AND ssc.strategyStyleSPClusId.strategyFinelineSPClusId.finelineNbr = scsc.strategyCcSPClusId.strategyStyleSPClusId.strategyFinelineSPClusId.finelineNbr " +
            "AND ssc.strategyStyleSPClusId.styleNbr = scsc.strategyCcSPClusId.strategyStyleSPClusId.styleNbr " +
            "AND ssc.strategyStyleSPClusId.strategyFinelineSPClusId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.channelId = scsc.strategyCcSPClusId.strategyStyleSPClusId.strategyFinelineSPClusId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.channelId " +
            "AND ssc.strategyStyleSPClusId.strategyFinelineSPClusId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.planClusterStrategyId.analyticsClusterId = scsc.strategyCcSPClusId.strategyStyleSPClusId.strategyFinelineSPClusId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.planClusterStrategyId.analyticsClusterId "+
            "WHERE ps.planStrategyId.planId = :planId AND ps.planStrategyId.strategyId = :strategyId AND scsc.strategyCcSPClusId.strategyStyleSPClusId.strategyFinelineSPClusId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.channelId IN (:channelId) AND sfsc.strategyIFineLineId.finelineNbr = :finelineNbr " +
            "AND (ssc.strategyStyleSPClusId.styleNbr = :styleNbr OR :styleNbr = NULL) AND (scsc.strategyCcSPClusId.ccId = :ccId OR :ccId = NULL) ")
    List<SizeResponseDTO> getCcSizeByFineline(@Param("planId") Long planId,
                                              @Param("strategyId") Long strategyId, @Param("finelineNbr") Integer finelineNbr, @Param("channelId") Integer channelId,
                                              @Param("styleNbr") String styleNbr,
                                              @Param("ccId") String ccId);

    @Query(value="select distinct new com.walmart.aex.strategy.dto.SizeResponseDTO(ps.planStrategyId.planId, ps.planStrategyId.strategyId, " +
            "sc.planClusterStrategyId.analyticsClusterId , " +
            " smsc.strategyMerchCatgSPClusId.lvl0Nbr , " +
            " smsc.strategyMerchCatgSPClusId.lvl1Nbr , " +
            " smsc.strategyMerchCatgSPClusId.lvl2Nbr , " +
            " smsc.strategyMerchCatgSPClusId.lvl3Nbr , " +
            " sssc.strategySubCatgSPClusId.lvl4Nbr , " +
            " sfsc.strategyIFineLineId.finelineNbr , " +
            " sff.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.fixtureTypeId , " +
            " sff.merchMethodCode , " +
            " ssc.strategyStyleSPClusId.styleNbr , " +
            " scsc.strategyCcSPClusId.ccId ," +
            " scsc.sizeProfileObj as ccSizeObj, " +
            " scc.colorFamily as colorFamily, " +
            " scsc.totalSizeProfilePct )" +
            "FROM PlanStrategy ps " +
            "INNER JOIN PlanClusterStrategy sc " +
            "ON ps.planStrategyId.planId = sc.planClusterStrategyId.planStrategyId.planId " +
            "AND ps.planStrategyId.strategyId = sc.planClusterStrategyId.planStrategyId.strategyId " +
            "INNER JOIN StrategyMerchCategorySPCluster smsc " +
            "ON sc.planClusterStrategyId.planStrategyId.planId = smsc.strategyMerchCatgSPClusId.planClusterStrategyId.planStrategyId.planId " +
            "AND sc.planClusterStrategyId.planStrategyId.strategyId = smsc.strategyMerchCatgSPClusId.planClusterStrategyId.planStrategyId.strategyId " +
            "AND sc.planClusterStrategyId.analyticsClusterId = smsc.strategyMerchCatgSPClusId.planClusterStrategyId.analyticsClusterId " +
            "INNER JOIN StrategySubCategorySPCluster sssc " +
            "ON smsc.strategyMerchCatgSPClusId.planClusterStrategyId.planStrategyId.planId = sssc.strategySubCatgSPClusId.strategyMerchCatgSPClusId.planClusterStrategyId.planStrategyId.planId " +
            "AND smsc.strategyMerchCatgSPClusId.planClusterStrategyId.planStrategyId.strategyId = sssc.strategySubCatgSPClusId.strategyMerchCatgSPClusId.planClusterStrategyId.planStrategyId.strategyId " +
            "AND smsc.strategyMerchCatgSPClusId.planClusterStrategyId.analyticsClusterId = sssc.strategySubCatgSPClusId.strategyMerchCatgSPClusId.planClusterStrategyId.analyticsClusterId " +
            "AND smsc.strategyMerchCatgSPClusId.lvl0Nbr = sssc.strategySubCatgSPClusId.strategyMerchCatgSPClusId.lvl0Nbr " +
            "AND smsc.strategyMerchCatgSPClusId.lvl1Nbr = sssc.strategySubCatgSPClusId.strategyMerchCatgSPClusId.lvl1Nbr " +
            "AND smsc.strategyMerchCatgSPClusId.lvl2Nbr = sssc.strategySubCatgSPClusId.strategyMerchCatgSPClusId.lvl2Nbr " +
            "AND smsc.strategyMerchCatgSPClusId.lvl3Nbr = sssc.strategySubCatgSPClusId.strategyMerchCatgSPClusId.lvl3Nbr " +
            "AND smsc.strategyMerchCatgSPClusId.channelId = sssc.strategySubCatgSPClusId.strategyMerchCatgSPClusId.channelId " +
            "AND smsc.strategyMerchCatgSPClusId.planClusterStrategyId.analyticsClusterId = sssc.strategySubCatgSPClusId.strategyMerchCatgSPClusId.planClusterStrategyId.analyticsClusterId " +
            "INNER JOIN StrategyFineLineSPCluster sfsc " +
            "ON sssc.strategySubCatgSPClusId.strategyMerchCatgSPClusId.planClusterStrategyId.planStrategyId.planId = sfsc.strategyIFineLineId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.planClusterStrategyId.planStrategyId.planId " +
            "AND sssc.strategySubCatgSPClusId.strategyMerchCatgSPClusId.planClusterStrategyId.planStrategyId.strategyId = sfsc.strategyIFineLineId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.planClusterStrategyId.planStrategyId.strategyId " +
            "AND sssc.strategySubCatgSPClusId.strategyMerchCatgSPClusId.lvl0Nbr = sfsc.strategyIFineLineId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.lvl0Nbr " +
            "AND sssc.strategySubCatgSPClusId.strategyMerchCatgSPClusId.lvl1Nbr = sfsc.strategyIFineLineId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.lvl1Nbr " +
            "AND sssc.strategySubCatgSPClusId.strategyMerchCatgSPClusId.lvl2Nbr = sfsc.strategyIFineLineId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.lvl2Nbr " +
            "AND sssc.strategySubCatgSPClusId.strategyMerchCatgSPClusId.lvl3Nbr = sfsc.strategyIFineLineId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.lvl3Nbr " +
            "AND sssc.strategySubCatgSPClusId.lvl4Nbr = sfsc.strategyIFineLineId.strategySubCatgSPClusId.lvl4Nbr " +
            "AND sssc.strategySubCatgSPClusId.strategyMerchCatgSPClusId.channelId = sfsc.strategyIFineLineId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.channelId " +
            "AND sssc.strategySubCatgSPClusId.strategyMerchCatgSPClusId.planClusterStrategyId.analyticsClusterId = sfsc.strategyIFineLineId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.planClusterStrategyId.analyticsClusterId " +
            "INNER JOIN StrategyStyleSPCluster ssc "+
            "ON sfsc.strategyIFineLineId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.planClusterStrategyId.planStrategyId.planId = ssc.strategyStyleSPClusId.strategyFinelineSPClusId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.planClusterStrategyId.planStrategyId.planId " +
            "AND sfsc.strategyIFineLineId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.planClusterStrategyId.planStrategyId.strategyId = ssc.strategyStyleSPClusId.strategyFinelineSPClusId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.planClusterStrategyId.planStrategyId.strategyId " +
            "AND sfsc.strategyIFineLineId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.lvl0Nbr = ssc.strategyStyleSPClusId.strategyFinelineSPClusId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.lvl0Nbr " +
            "AND sfsc.strategyIFineLineId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.lvl1Nbr = ssc.strategyStyleSPClusId.strategyFinelineSPClusId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.lvl1Nbr " +
            "AND sfsc.strategyIFineLineId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.lvl2Nbr = ssc.strategyStyleSPClusId.strategyFinelineSPClusId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.lvl2Nbr " +
            "AND sfsc.strategyIFineLineId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.lvl3Nbr = ssc.strategyStyleSPClusId.strategyFinelineSPClusId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.lvl3Nbr " +
            "AND sfsc.strategyIFineLineId.strategySubCatgSPClusId.lvl4Nbr = ssc.strategyStyleSPClusId.strategyFinelineSPClusId.strategySubCatgSPClusId.lvl4Nbr " +
            "AND sfsc.strategyIFineLineId.finelineNbr = ssc.strategyStyleSPClusId.strategyFinelineSPClusId.finelineNbr " +
            "AND sfsc.strategyIFineLineId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.channelId = ssc.strategyStyleSPClusId.strategyFinelineSPClusId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.channelId " +
            "AND sfsc.strategyIFineLineId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.planClusterStrategyId.analyticsClusterId = ssc.strategyStyleSPClusId.strategyFinelineSPClusId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.planClusterStrategyId.analyticsClusterId " +
            "INNER JOIN StrategyCcSPCluster scsc " +
            "ON ssc.strategyStyleSPClusId.strategyFinelineSPClusId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.planClusterStrategyId.planStrategyId.planId = scsc.strategyCcSPClusId.strategyStyleSPClusId.strategyFinelineSPClusId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.planClusterStrategyId.planStrategyId.planId " +
            "AND ssc.strategyStyleSPClusId.strategyFinelineSPClusId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.planClusterStrategyId.planStrategyId.strategyId = scsc.strategyCcSPClusId.strategyStyleSPClusId.strategyFinelineSPClusId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.planClusterStrategyId.planStrategyId.strategyId " +
            "AND ssc.strategyStyleSPClusId.strategyFinelineSPClusId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.lvl0Nbr = scsc.strategyCcSPClusId.strategyStyleSPClusId.strategyFinelineSPClusId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.lvl0Nbr " +
            "AND ssc.strategyStyleSPClusId.strategyFinelineSPClusId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.lvl1Nbr = scsc.strategyCcSPClusId.strategyStyleSPClusId.strategyFinelineSPClusId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.lvl1Nbr " +
            "AND ssc.strategyStyleSPClusId.strategyFinelineSPClusId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.lvl2Nbr = scsc.strategyCcSPClusId.strategyStyleSPClusId.strategyFinelineSPClusId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.lvl2Nbr " +
            "AND ssc.strategyStyleSPClusId.strategyFinelineSPClusId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.lvl3Nbr = scsc.strategyCcSPClusId.strategyStyleSPClusId.strategyFinelineSPClusId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.lvl3Nbr " +
            "AND ssc.strategyStyleSPClusId.strategyFinelineSPClusId.strategySubCatgSPClusId.lvl4Nbr = scsc.strategyCcSPClusId.strategyStyleSPClusId.strategyFinelineSPClusId.strategySubCatgSPClusId.lvl4Nbr " +
            "AND ssc.strategyStyleSPClusId.strategyFinelineSPClusId.finelineNbr = scsc.strategyCcSPClusId.strategyStyleSPClusId.strategyFinelineSPClusId.finelineNbr " +
            "AND ssc.strategyStyleSPClusId.styleNbr = scsc.strategyCcSPClusId.strategyStyleSPClusId.styleNbr " +
            "AND ssc.strategyStyleSPClusId.strategyFinelineSPClusId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.channelId = scsc.strategyCcSPClusId.strategyStyleSPClusId.strategyFinelineSPClusId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.channelId " +
            "AND ssc.strategyStyleSPClusId.strategyFinelineSPClusId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.planClusterStrategyId.analyticsClusterId = scsc.strategyCcSPClusId.strategyStyleSPClusId.strategyFinelineSPClusId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.planClusterStrategyId.analyticsClusterId " +
            "LEFT JOIN StrategyCc scc " +
            "ON scsc.strategyCcSPClusId.strategyStyleSPClusId.strategyFinelineSPClusId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.planClusterStrategyId.planStrategyId.planId = scc.strategyCcId.strategyStyleId.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.planStrategyId.planId " +
            "AND scsc.strategyCcSPClusId.strategyStyleSPClusId.strategyFinelineSPClusId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.lvl0Nbr = scc.strategyCcId.strategyStyleId.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl0Nbr " +
            "AND scsc.strategyCcSPClusId.strategyStyleSPClusId.strategyFinelineSPClusId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.lvl1Nbr = scc.strategyCcId.strategyStyleId.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl1Nbr " +
            "AND scsc.strategyCcSPClusId.strategyStyleSPClusId.strategyFinelineSPClusId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.lvl2Nbr = scc.strategyCcId.strategyStyleId.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl2Nbr " +
            "AND scsc.strategyCcSPClusId.strategyStyleSPClusId.strategyFinelineSPClusId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.lvl3Nbr = scc.strategyCcId.strategyStyleId.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl3Nbr " +
            "AND scsc.strategyCcSPClusId.strategyStyleSPClusId.strategyFinelineSPClusId.strategySubCatgSPClusId.lvl4Nbr = scc.strategyCcId.strategyStyleId.strategyFinelineId.strategySubCatgId.lvl4Nbr " +
            "AND scsc.strategyCcSPClusId.strategyStyleSPClusId.strategyFinelineSPClusId.finelineNbr = scc.strategyCcId.strategyStyleId.strategyFinelineId.finelineNbr " +
            "AND scsc.strategyCcSPClusId.strategyStyleSPClusId.styleNbr = scc.strategyCcId.strategyStyleId.styleNbr " +
            "AND scsc.strategyCcSPClusId.ccId = scc.strategyCcId.ccId " +
            "LEFT JOIN StrategyFinelineFixture sff " +
            "ON sfsc.strategyIFineLineId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.planClusterStrategyId.planStrategyId.planId = sff.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.planStrategyId.planId " +
            "AND sfsc.strategyIFineLineId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.lvl0Nbr = sff.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl0Nbr " +
            "AND sfsc.strategyIFineLineId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.lvl1Nbr = sff.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl1Nbr " +
            "AND sfsc.strategyIFineLineId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.lvl2Nbr = sff.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl2Nbr " +
            "AND sfsc.strategyIFineLineId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.lvl3Nbr = sff.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl3Nbr " +
            "AND sfsc.strategyIFineLineId.strategySubCatgSPClusId.lvl4Nbr = sff.strategyFinelineFixtureId.strategySubCatgFixtureId.lvl4Nbr " +
            "AND sfsc.strategyIFineLineId.finelineNbr = sff.strategyFinelineFixtureId.finelineNbr " +
            "WHERE ps.planStrategyId.planId = :planId AND ps.planStrategyId.strategyId = :strategyId AND (sff.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.planStrategyId.strategyId = :presentationUnitsStrategyId OR :channelId = 2) AND scsc.strategyCcSPClusId.strategyStyleSPClusId.strategyFinelineSPClusId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.channelId IN (:channelId) AND sfsc.strategyIFineLineId.finelineNbr = :finelineNbr " +
            "AND (ssc.strategyStyleSPClusId.styleNbr = :styleNbr OR :styleNbr = NULL) AND (scsc.strategyCcSPClusId.ccId = :ccId OR :ccId = NULL) " +
            "ORDER BY sc.planClusterStrategyId.analyticsClusterId")
    List<SizeResponseDTO> getAllCcSizeByFineline(@Param("planId") Long planId,
                                              @Param("strategyId") Long strategyId, @Param("presentationUnitsStrategyId") Long presentationUnitsStrategyId, @Param("finelineNbr") Integer finelineNbr, @Param("channelId") Integer channelId,
                                              @Param("styleNbr") String styleNbr,
                                              @Param("ccId") String ccId);
}
