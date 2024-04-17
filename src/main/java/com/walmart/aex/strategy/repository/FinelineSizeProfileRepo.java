package com.walmart.aex.strategy.repository;

import com.walmart.aex.strategy.dto.SizeResponseDTO;
import com.walmart.aex.strategy.entity.*;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface FinelineSizeProfileRepo extends JpaRepository<StrategyFineLineSPCluster, StrategyFineLineSPClusId> {


	 List<StrategyFineLineSPCluster> findByStrategyIFineLineIdStrategySubCatgSPClusIdStrategyMerchCatgSPClusIdPlanClusterStrategyIdPlanStrategyIdPlanIdAndStrategyIFineLineIdStrategySubCatgSPClusIdStrategyMerchCatgSPClusIdChannelIdAndStrategyIFineLineIdFinelineNbr(Long planId,Integer channelId,Integer finelineNbr);

	@Query(value="select new com.walmart.aex.strategy.dto.SizeResponseDTO(ps.planStrategyId.planId, ps.planStrategyId.strategyId, " +
			"sc.planClusterStrategyId.analyticsClusterId , " +
			" smsc.strategyMerchCatgSPClusId.lvl0Nbr , " +
			" smsc.strategyMerchCatgSPClusId.lvl1Nbr , " +
			" smsc.strategyMerchCatgSPClusId.lvl2Nbr , " +
			" smsc.strategyMerchCatgSPClusId.lvl3Nbr , " +
			" sssc.strategySubCatgSPClusId.lvl4Nbr , " +
			" sfsc.strategyIFineLineId.finelineNbr, " +
			" sfsc.sizeProfileObj AS fineLineSizeObj " +
			" ) "+
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
			"INNER JOIN StrategyFineline sf "+
			"ON sfsc.strategyIFineLineId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.planClusterStrategyId.planStrategyId.planId = sf.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.planStrategyId.planId " +
			"AND sfsc.strategyIFineLineId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.lvl0Nbr = sf.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl0Nbr " +
			"AND sfsc.strategyIFineLineId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.lvl1Nbr = sf.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl1Nbr " +
			"AND sfsc.strategyIFineLineId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.lvl2Nbr = sf.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl2Nbr " +
			"AND sfsc.strategyIFineLineId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.lvl3Nbr = sf.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl3Nbr " +
			"AND sfsc.strategyIFineLineId.strategySubCatgSPClusId.lvl4Nbr = sf.strategyFinelineId.strategySubCatgId.lvl4Nbr " +
			"AND sfsc.strategyIFineLineId.finelineNbr = sf.strategyFinelineId.finelineNbr " +
			"WHERE ps.planStrategyId.planId = :planId AND ps.planStrategyId.strategyId = :strategyId AND sfsc.strategyIFineLineId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.channelId IN (:channelId) AND " +
			"sfsc.strategyIFineLineId.finelineNbr = :finelineNbr ")
	List<SizeResponseDTO> getFinelineSizeProfiles(@Param("planId") Long planId,
												@Param("strategyId") Long strategyId, @Param("channelId") Integer channelId,
												@Param("finelineNbr") Integer finelineNbr);
}
