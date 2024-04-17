package com.walmart.aex.strategy.repository;

import com.walmart.aex.strategy.dto.SizeResponseDTO;
import com.walmart.aex.strategy.entity.StrategyFineLineSPClusId;
import com.walmart.aex.strategy.entity.StrategyFineLineSPCluster;
import com.walmart.aex.strategy.entity.StrategyStyleSPClusId;
import com.walmart.aex.strategy.entity.StrategyStyleSPCluster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StyleSizeProfileRepo extends JpaRepository<StrategyStyleSPCluster, StrategyStyleSPClusId> {


    @Query(value="select new com.walmart.aex.strategy.dto.SizeResponseDTO(ps.planStrategyId.planId, ps.planStrategyId.strategyId, " +
            " sc.planClusterStrategyId.analyticsClusterId , " +
            " smsc.strategyMerchCatgSPClusId.lvl0Nbr , " +
            " smsc.strategyMerchCatgSPClusId.lvl1Nbr , " +
            " smsc.strategyMerchCatgSPClusId.lvl2Nbr , " +
            " smsc.strategyMerchCatgSPClusId.lvl3Nbr , " +
            " sssc.strategySubCatgSPClusId.lvl4Nbr , " +
            " sfsc.strategyIFineLineId.finelineNbr , " +
            " ssc.strategyStyleSPClusId.styleNbr , " +
            " ssc.sizeProfileObj as styleSizeObj) " +
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
            "INNER JOIN StrategyFineLineSPCluster sfsc " +
            "ON sssc.strategySubCatgSPClusId.strategyMerchCatgSPClusId.planClusterStrategyId.planStrategyId.planId = sfsc.strategyIFineLineId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.planClusterStrategyId.planStrategyId.planId " +
            "AND sssc.strategySubCatgSPClusId.strategyMerchCatgSPClusId.planClusterStrategyId.planStrategyId.strategyId = sfsc.strategyIFineLineId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.planClusterStrategyId.planStrategyId.strategyId " +
            "AND sssc.strategySubCatgSPClusId.strategyMerchCatgSPClusId.lvl0Nbr = sfsc.strategyIFineLineId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.lvl0Nbr " +
            "AND sssc.strategySubCatgSPClusId.strategyMerchCatgSPClusId.lvl1Nbr = sfsc.strategyIFineLineId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.lvl1Nbr " +
            "AND sssc.strategySubCatgSPClusId.strategyMerchCatgSPClusId.lvl2Nbr = sfsc.strategyIFineLineId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.lvl2Nbr " +
            "AND sssc.strategySubCatgSPClusId.strategyMerchCatgSPClusId.lvl3Nbr = sfsc.strategyIFineLineId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.lvl3Nbr " +
            "AND sssc.strategySubCatgSPClusId.lvl4Nbr = sfsc.strategyIFineLineId.strategySubCatgSPClusId.lvl4Nbr " +
            "AND sssc.strategySubCatgSPClusId.strategyMerchCatgSPClusId.channelId = sfsc.strategyIFineLineId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.channelId " +
            "AND sssc.strategySubCatgSPClusId.strategyMerchCatgSPClusId.planClusterStrategyId.analyticsClusterId = sfsc.strategyIFineLineId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.planClusterStrategyId.analyticsClusterId  " +
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
            "WHERE ps.planStrategyId.planId = :planId AND ps.planStrategyId.strategyId = :strategyId AND ssc.strategyStyleSPClusId.strategyFinelineSPClusId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.channelId IN (:channelId) AND sfsc.strategyIFineLineId.finelineNbr = :finelineNbr " +
            "AND ssc.strategyStyleSPClusId.styleNbr = :styleNbr ")
    List<SizeResponseDTO> getStyleSizeProfiles(@Param("planId") Long planId,
                                              @Param("strategyId") Long strategyId, @Param("finelineNbr") Integer finelineNbr, @Param("channelId") Integer channelId,
                                              @Param("styleNbr") String styleNbr);
}
