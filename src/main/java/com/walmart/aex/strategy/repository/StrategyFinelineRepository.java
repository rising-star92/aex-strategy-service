package com.walmart.aex.strategy.repository;

import com.walmart.aex.strategy.dto.*;
import com.walmart.aex.strategy.dto.midas.StrongKeyFlat;
import com.walmart.aex.strategy.entity.PlanStrategyId;
import com.walmart.aex.strategy.entity.StrategyFineline;
import com.walmart.aex.strategy.entity.StrategyFinelineId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StrategyFinelineRepository extends JpaRepository<StrategyFineline, StrategyFinelineId>{

    Optional<StrategyFineline> findByStrategyFinelineId(StrategyFinelineId strategyFinelineId);

    void deleteStrategyFinelineByStrategyFinelineId_StrategySubCatgId_StrategyMerchCatgId_planStrategyIdAndStrategyFinelineId_StrategySubCatgId_StrategyMerchCatgId_lvl3NbrAndStrategyFinelineId_StrategySubCatgId_lvl4NbrAndStrategyFinelineId_finelineNbr(
            PlanStrategyId planStrategyId, Integer lvl3Nbr, Integer lvl4Nbr, Integer finelineNbr);

    @Query(value = "select new com.walmart.aex.strategy.dto.WeatherClusterStrategy(ps.planStrategyId.planId, ps.planStrategyId.strategyId, " +
            " sf.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl0Nbr , " +
            " sf.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl1Nbr , " +
            " sf.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl2Nbr , " +
            " sf.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl3Nbr , " +
            " sf.lvl3GenDesc1 , " +
            " sf.strategyFinelineId.strategySubCatgId.lvl4Nbr , " +
            " sf.lvl4GenDesc1 , " +
            "sf.strategyFinelineId.finelineNbr, " +
            "sf.finelineDesc, " +
            "sf.outFitting, " +
            "sf.altFinelineName, "+
            "psc.planClusterStrategyId.analyticsClusterId, "+
            "psc.analyticsClusterLabel, " +
            "elgFl.isEligible, "+
            "elgFl.isEligibleFlag, "+
            "elgFl.inStoreYrWkDesc, " +
            "elgFl.markDownYrWkDesc, " +
            "elgFl.markDownYrWk - elgFl.inStoreYrWk, "+
            "mkt.marketValue, " +
            "elgMet.salesDollars, " +
            "elgMet.salesUnits, " +
            "elgMet.onHandQty, " +
            "elgMet.salesToStockRatio, " +
            "elgMet.forecastedDollars, " +
            "elgMet.forecastedUnits, " +
            "elgFl.merchantOverrideRank, " +
            "elgMet.analyticsClusterRank, " +
            "elgFl.storeCount, "+
            "sf.brands ) " +
            "FROM PlanStrategy ps " +
            "INNER JOIN PlanClusterStrategy psc " +
                "ON ps.planStrategyId.planId = psc.planClusterStrategyId.planStrategyId.planId " +
                "AND ps.planStrategyId.strategyId = psc.planClusterStrategyId.planStrategyId.strategyId " +
            "INNER JOIN StrategyFineline sf " +
                "ON ps.planStrategyId.planId = sf.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.planStrategyId.planId " +
                "AND ps.planStrategyId.strategyId = sf.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.planStrategyId.strategyId " +
            "INNER JOIN StrategyFlClusEligRanking elgFl " +
                "ON elgFl.strategyFlClusEligRankingId.planClusterStrategyId.planStrategyId.planId = psc.planClusterStrategyId.planStrategyId.planId " +
                "AND elgFl.strategyFlClusEligRankingId.planClusterStrategyId.planStrategyId.strategyId = psc.planClusterStrategyId.planStrategyId.strategyId " +
                "AND elgFl.strategyFlClusEligRankingId.planClusterStrategyId.analyticsClusterId = psc.planClusterStrategyId.analyticsClusterId " +
                "AND elgFl.strategyFlClusEligRankingId.finelineNbr = sf.strategyFinelineId.finelineNbr " +
            "INNER JOIN StrategyFlClusMetrics elgMet " +
                "ON elgFl.strategyFlClusEligRankingId.planClusterStrategyId.planStrategyId.planId = elgMet.strategyFlClusEligRankingId.planClusterStrategyId.planStrategyId.planId " +
                "AND elgFl.strategyFlClusEligRankingId.planClusterStrategyId.planStrategyId.strategyId = elgMet.strategyFlClusEligRankingId.planClusterStrategyId.planStrategyId.strategyId " +
                "AND elgFl.strategyFlClusEligRankingId.planClusterStrategyId.analyticsClusterId = elgMet.strategyFlClusEligRankingId.planClusterStrategyId.analyticsClusterId " +
                "AND elgFl.strategyFlClusEligRankingId.lvl0Nbr = elgMet.strategyFlClusEligRankingId.lvl0Nbr " +
                "AND elgFl.strategyFlClusEligRankingId.lvl1Nbr = elgMet.strategyFlClusEligRankingId.lvl1Nbr " +
                "AND elgFl.strategyFlClusEligRankingId.lvl2Nbr = elgMet.strategyFlClusEligRankingId.lvl2Nbr " +
                "AND elgFl.strategyFlClusEligRankingId.lvl2Nbr = elgMet.strategyFlClusEligRankingId.lvl2Nbr " +
                "AND elgFl.strategyFlClusEligRankingId.lvl3Nbr = elgMet.strategyFlClusEligRankingId.lvl3Nbr " +
                "AND elgFl.strategyFlClusEligRankingId.lvl4Nbr = elgMet.strategyFlClusEligRankingId.lvl4Nbr " +
                "AND elgFl.strategyFlClusEligRankingId.finelineNbr = elgMet.strategyFlClusEligRankingId.finelineNbr " +
            "LEFT JOIN StrategyFlMktClusElig elgMkt " +
                "ON elgMkt.strategyFlMktClusEligId.strategyFlClusEligRankingId.planClusterStrategyId.planStrategyId.planId = elgMet.strategyFlClusEligRankingId.planClusterStrategyId.planStrategyId.planId " +
                "AND elgMkt.strategyFlMktClusEligId.strategyFlClusEligRankingId.planClusterStrategyId.planStrategyId.strategyId = elgMet.strategyFlClusEligRankingId.planClusterStrategyId.planStrategyId.strategyId " +
                "AND elgMkt.strategyFlMktClusEligId.strategyFlClusEligRankingId.planClusterStrategyId.analyticsClusterId = elgMet.strategyFlClusEligRankingId.planClusterStrategyId.analyticsClusterId " +
                "AND elgMkt.strategyFlMktClusEligId.strategyFlClusEligRankingId.lvl0Nbr = elgMet.strategyFlClusEligRankingId.lvl0Nbr " +
                "AND elgMkt.strategyFlMktClusEligId.strategyFlClusEligRankingId.lvl1Nbr = elgMet.strategyFlClusEligRankingId.lvl1Nbr " +
                "AND elgMkt.strategyFlMktClusEligId.strategyFlClusEligRankingId.lvl2Nbr = elgMet.strategyFlClusEligRankingId.lvl2Nbr " +
                "AND elgMkt.strategyFlMktClusEligId.strategyFlClusEligRankingId.lvl3Nbr = elgMet.strategyFlClusEligRankingId.lvl3Nbr " +
                "AND elgMkt.strategyFlMktClusEligId.strategyFlClusEligRankingId.lvl4Nbr = elgMet.strategyFlClusEligRankingId.lvl4Nbr " +
                "AND elgMkt.strategyFlMktClusEligId.strategyFlClusEligRankingId.finelineNbr = elgMet.strategyFlClusEligRankingId.finelineNbr " +
            "LEFT JOIN MarketSelection mkt " +
                "ON mkt.marketSelectCode = elgMkt.strategyFlMktClusEligId.marketSelectCode " +
            "WHERE sf.traitChoiceCode IN (2,3) " +
                "AND sf.channelId IN (1,3)   " +
                "AND ps.planStrategyId.planId = :planId " +
                "AND ps.planStrategyId.strategyId = :strategyId " +
                "AND (:finelineNbr is null or sf.strategyFinelineId.finelineNbr = :finelineNbr)")
    List<WeatherClusterStrategy> getWeatherClusterStrategy(@Param("planId") Long planId,
                                                           @Param("strategyId") Long strategyId,
                                                           @Param("finelineNbr") Integer finelineNbr);

    @Query(value="SELECT DISTINCT new com.walmart.aex.strategy.dto.midas.StrongKeyFlat(1, " +
            "sf.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.planStrategyId.planId, " +
            "sf.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl0Nbr, " +
            "sf.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl1Nbr, "+
            "sf.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl2Nbr, "+
            "sf.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl3Nbr, "+
            "sf.strategyFinelineId.strategySubCatgId.lvl4Nbr, " +
            "sf.strategyFinelineId.finelineNbr) FROM StrategyFineline sf WHERE " +
            "sf.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.planStrategyId.strategyId = :strategyId AND " +
            "(:lvl0Nbr IS NULL OR sf.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl0Nbr = :lvl0Nbr) AND " +
            "(:lvl1Nbr IS NULL OR sf.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl1Nbr = :lvl1Nbr) AND " +
            "sf.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl2Nbr = :lvl2Nbr AND " +
            "(:lvl3Nbr IS NULL OR sf.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl3Nbr = :lvl3Nbr) AND "+
            "(:lvl4Nbr IS NULL OR sf.strategyFinelineId.strategySubCatgId.lvl4Nbr = :lvl4Nbr)")
    List<StrongKeyFlat> getFinelines(@Param("strategyId") Long strategyId,
                                     @Param("lvl0Nbr") Integer lvl0Nbr,
                                     @Param("lvl1Nbr") Integer lvl1Nbr,
                                     @Param("lvl2Nbr") Integer lvl2Nbr,
                                     @Param("lvl3Nbr") Integer lvl3Nbr,
                                     @Param("lvl4Nbr") Integer lvl4Nbr);


    @Query(value = "SELECT " +
            "  ps.plan_id AS planId, ps.strategy_id AS strategyId, sf.rpt_lvl_0_nbr AS lvl0Nbr, sf.rpt_lvl_1_nbr AS lvl1Nbr, " +
            "  sf.rpt_lvl_2_nbr AS lvl2Nbr, sf.rpt_lvl_3_nbr AS lvl3Nbr, sf.rpt_lvl_3_gen_desc1 AS lvl3Name, " +
            "  sf.rpt_lvl_4_nbr AS lvl4Nbr, sf.rpt_lvl_4_gen_desc1 AS lvl4Name, sf.fineline_nbr AS finelineNbr, " +
            "  sf.fineline_desc AS finelineDesc, " +
            "  sf.outfitting_fl_list AS outFitting, " +
            "  sf.alt_fineline_desc As altFinelineName, " +
            "psc.analytics_cluster_id AS analyticsClusterId, psc.analytics_cluster_label AS analyticsClusterDesc, " +
            "  COALESCE(elgFlPrg.is_eligible, 0) AS isEligible, " +
            "  COALESCE(elgFlPrg.select_status_id, 0) AS isEligibleFlag, " +
            "  COALESCE(elgFlPrg.in_store_yrwk_desc, elgFlClus.in_store_yrwk_desc) AS inStoreDate, " +
            "  COALESCE(elgFlPrg.markdown_yrwk_desc, elgFlClus.markdown_yrwk_desc) AS markDownDate, " +
            "  (CASE WHEN (elgFlPrg.markdown_yr_wk IS NULL AND elgFlPrg.in_store_yr_wk IS NULL) " +
            "  THEN (elgFlClus.markdown_yr_wk - elgFlClus.in_store_yr_wk) " +
            "  ELSE (elgFlPrg.markdown_yr_wk - elgFlPrg.in_store_yr_wk) END) AS sellingWeeks, " +
            "  mkt.market_value AS excludeOffshore, " +
            "  elgFlClusMet.sales_dollars AS lySales, " +
            "  elgFlClusMet.sales_units AS lyUnits, " +
            "  elgFlClusMet.on_hand_qty AS onHandQty, " +
            "  elgFlClusMet.sales_to_stock_ratio AS salesToStockRatio, " +
            "  elgFlClusMet.forecasted_dollars AS forecastedSales, " +
            "  elgFlClusMet.forecasted_units AS forecastedUnits, " +
            "  elgFlPrg.merchant_override_rank AS ranking, " +
            "  elgFlClusMet.analytics_cluster_rank AS algoClusterRanking, " +
            "  elgFlPrg.store_cnt as storeCount, " +
            "  sf.brand_obj AS brands "+
            "  FROM " +
            "  [dbo].[plan_strategy] ps " +
            "  INNER JOIN [dbo].[plan_strat_clus] psc " +
            "  ON psc.plan_id = ps.plan_id AND psc.strategy_id = ps.strategy_id " +
            "  INNER JOIN [dbo].[strat_fl] sf " +
            "  ON sf.plan_id = ps.plan_id AND sf.strategy_id = ps.strategy_id " +
            "  INNER JOIN [dbo].[elig_fl_clus_rank] elgFlClus " +
            "  ON elgFlClus.plan_id = ps.plan_id AND elgFlClus.strategy_id = ps.strategy_id " +
            "  AND elgFlClus.analytics_cluster_id = psc.analytics_cluster_id " +
            "  AND elgFlClus.rpt_lvl_0_nbr = sf.rpt_lvl_0_nbr " +
            "  AND elgFlClus.rpt_lvl_1_nbr = sf.rpt_lvl_1_nbr " +
            "  AND elgFlClus.rpt_lvl_2_nbr = sf.rpt_lvl_2_nbr " +
            "  AND elgFlClus.rpt_lvl_3_nbr = sf.rpt_lvl_3_nbr " +
            "  AND elgFlClus.rpt_lvl_4_nbr = sf.rpt_lvl_4_nbr " +
            "  AND elgFlClus.fineline_nbr = sf.fineline_nbr " +
            "  INNER JOIN [dbo].[elig_fl_clus_metrics] elgFlClusMet " +
            "  ON elgFlClusMet.plan_id = ps.plan_id AND elgFlClusMet.strategy_id = ps.strategy_id " +
            "  AND elgFlClusMet.analytics_cluster_id = elgFlClus.analytics_cluster_id " +
            "  AND elgFlClusMet.rpt_lvl_0_nbr = elgFlClus.rpt_lvl_0_nbr " +
            "  AND elgFlClusMet.rpt_lvl_1_nbr = elgFlClus.rpt_lvl_1_nbr " +
            "  AND elgFlClusMet.rpt_lvl_2_nbr = elgFlClus.rpt_lvl_2_nbr " +
            "  AND elgFlClusMet.rpt_lvl_3_nbr = elgFlClus.rpt_lvl_3_nbr " +
            "  AND elgFlClusMet.rpt_lvl_4_nbr = elgFlClus.rpt_lvl_4_nbr " +
            "  AND elgFlClusMet.fineline_nbr = elgFlClus.fineline_nbr " +
            "  LEFT JOIN ( " +
            "      SELECT * FROM [dbo].[elig_fl_clus_prog] " +
            "      WHERE program_id = :programId " +
            "  ) elgFlPrg " +
            "  ON elgFlPrg.plan_id = ps.plan_id AND elgFlPrg.strategy_id = ps.strategy_id " +
            "  AND elgFlPrg.analytics_cluster_id = elgFlClus.analytics_cluster_id " +
            "  AND elgFlPrg.rpt_lvl_0_nbr = elgFlClus.rpt_lvl_0_nbr " +
            "  AND elgFlPrg.rpt_lvl_1_nbr = elgFlClus.rpt_lvl_1_nbr " +
            "  AND elgFlPrg.rpt_lvl_2_nbr = elgFlClus.rpt_lvl_2_nbr " +
            "  AND elgFlPrg.rpt_lvl_3_nbr = elgFlClus.rpt_lvl_3_nbr " +
            "  AND elgFlPrg.rpt_lvl_4_nbr = elgFlClus.rpt_lvl_4_nbr " +
            "  AND elgFlPrg.fineline_nbr = elgFlClus.fineline_nbr " +
            "  LEFT JOIN [dbo].[elig_fl_mkt_clus_prog] elgPrgMkt " +
            "  ON elgPrgMkt.plan_id = ps.plan_id AND elgPrgMkt.strategy_id = ps.strategy_id " +
            "  AND elgPrgMkt.analytics_cluster_id = elgFlClus.analytics_cluster_id " +
            "  AND elgPrgMkt.rpt_lvl_0_nbr = elgFlClus.rpt_lvl_0_nbr " +
            "  AND elgPrgMkt.rpt_lvl_1_nbr = elgFlClus.rpt_lvl_1_nbr " +
            "  AND elgPrgMkt.rpt_lvl_2_nbr = elgFlClus.rpt_lvl_2_nbr " +
            "  AND elgPrgMkt.rpt_lvl_3_nbr = elgFlClus.rpt_lvl_3_nbr " +
            "  AND elgPrgMkt.rpt_lvl_4_nbr = elgFlClus.rpt_lvl_4_nbr " +
            "  AND elgPrgMkt.fineline_nbr = elgFlClus.fineline_nbr " +
            "  AND elgPrgMkt.program_id = elgFlPrg.program_id " +
            "  LEFT JOIN [dbo].[market_selections] mkt " +
            "  ON mkt.market_select_code = elgPrgMkt.market_select_code " +
            "  WHERE ps.plan_id = :planId " +
            "  AND ps.strategy_id = :strategyId " +
            "  AND sf.trait_choice_code IN (1,3) " +
            "  AND sf.channel_id IN (1,3) ", nativeQuery = true)
    List<WeatherClusterStrategyDTO> getWeatherClusterTraitStrategy(@Param("planId") Long planId,
                                                                   @Param("strategyId") Long strategyId,
                                                                   @Param("programId") Long programId);

    @Query(value="select new com.walmart.aex.strategy.dto.SizeResponseDTO(ps.planStrategyId.planId, ps.planStrategyId.strategyId, " +
            " smsc.strategyMerchCatgSPClusId.lvl0Nbr , " +
            " smsc.strategyMerchCatgSPClusId.lvl1Nbr , " +
            " smsc.strategyMerchCatgSPClusId.lvl2Nbr , " +
            " smsc.strategyMerchCatgSPClusId.lvl3Nbr , " +
            " smsc.sizeProfileObj AS catSizeObj, " +
            " sssc.strategySubCatgSPClusId.lvl4Nbr , " +
            " sssc.sizeProfileObj AS subCategorySizeObj, " +
            " sfsc.strategyIFineLineId.finelineNbr, " +
            " sfsc.sizeProfileObj AS fineLineSizeObj, " +
            " sf.lvl0GenDesc1, " +
            " sf.lvl1GenDesc1, " +
            " sf.lvl2GenDesc1, " +
            " sf.lvl3GenDesc1, " +
            " sf.lvl4GenDesc1, " +
            " sf.finelineDesc, "+
            " sf.altFinelineName) "+
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
            "smsc.strategyMerchCatgSPClusId.planClusterStrategyId.analyticsClusterId = 0")
    List<SizeResponseDTO> getCategoriesWithSize(@Param("planId") Long planId,
                                                @Param("strategyId") Long strategyId, @Param("channelId") Integer channelId);


    @Query(value="select new com.walmart.aex.strategy.dto.MerchMethodResponse(ps.planStrategyId.planId, ps.planStrategyId.strategyId, " +
            " smc.strategyMerchCatgId.lvl0Nbr , " +
            " smc.strategyMerchCatgId.lvl1Nbr , " +
            " smc.strategyMerchCatgId.lvl2Nbr , " +
            " smc.strategyMerchCatgId.lvl3Nbr , " +
            " ssc.strategySubCatgFixtureId.lvl4Nbr , " +
            " sff.strategyFinelineFixtureId.finelineNbr, " +
            " smcf.strategyMerchCatgFixtureId.fixtureTypeId , " +
            " sf.channelId, " +
            " sf.lvl0GenDesc1, " +
            " sf.lvl1GenDesc1, " +
            " sf.lvl2GenDesc1, " +
            " sf.lvl3GenDesc1, " +
            " sf.lvl4GenDesc1, " +
            " sf.finelineDesc) "+
            "FROM PlanStrategy ps " +
            "INNER JOIN StrategyMerchCatg smc " +
            "ON ps.planStrategyId.planId = smc.strategyMerchCatgId.planStrategyId.planId " +
            "AND ps.planStrategyId.strategyId = smc.strategyMerchCatgId.planStrategyId.strategyId " +
            "INNER JOIN StrategyMerchCatgFixture smcf " +
            "ON smc.strategyMerchCatgId.planStrategyId.planId = smcf.strategyMerchCatgFixtureId.strategyMerchCatgId.planStrategyId.planId " +
            "AND smc.strategyMerchCatgId.planStrategyId.strategyId = smcf.strategyMerchCatgFixtureId.strategyMerchCatgId.planStrategyId.strategyId " +
            "INNER JOIN StrategySubCatgFixture ssc " +
            "ON smcf.strategyMerchCatgFixtureId.strategyMerchCatgId.planStrategyId.planId = ssc.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.planStrategyId.planId " +
            "AND smcf.strategyMerchCatgFixtureId.strategyMerchCatgId.planStrategyId.strategyId = ssc.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.planStrategyId.strategyId " +
            "AND smcf.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl0Nbr = ssc.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl0Nbr " +
            "AND smcf.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl1Nbr = ssc.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl1Nbr " +
            "AND smcf.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl2Nbr = ssc.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl2Nbr " +
            "AND smcf.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl3Nbr = ssc.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl3Nbr " +
            "INNER JOIN StrategyFinelineFixture sff " +
            "ON ssc.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.planStrategyId.planId = sff.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.planStrategyId.planId " +
            "AND ssc.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.planStrategyId.strategyId = sff.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.planStrategyId.strategyId " +
            "AND ssc.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl0Nbr = sff.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl0Nbr " +
            "AND ssc.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl1Nbr = sff.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl1Nbr " +
            "AND ssc.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl2Nbr = sff.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl2Nbr " +
            "AND ssc.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl3Nbr = sff.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl3Nbr " +
            "AND ssc.strategySubCatgFixtureId.lvl4Nbr = sff.strategyFinelineFixtureId.strategySubCatgFixtureId.lvl4Nbr " +
            "LEFT JOIN StrategyFineline sf "+
            "ON sff.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.planStrategyId.planId = sf.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.planStrategyId.planId " +
            "AND sff.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl0Nbr = sf.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl0Nbr " +
            "AND sff.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl1Nbr = sf.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl1Nbr " +
            "AND sff.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl2Nbr = sf.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl2Nbr " +
            "AND sff.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl3Nbr = sf.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl3Nbr " +
            "AND sff.strategyFinelineFixtureId.strategySubCatgFixtureId.lvl4Nbr = sf.strategyFinelineId.strategySubCatgId.lvl4Nbr " +
            "AND sff.strategyFinelineFixtureId.finelineNbr = sf.strategyFinelineId.finelineNbr " +
            "WHERE ps.planStrategyId.planId = :planId AND ps.planStrategyId.strategyId = :strategyId AND sf.channelId = :channelId")
    List<MerchMethodResponse> getDescriptionsWithFixture(@Param("planId") Long planId,
                                                      @Param("strategyId") Long strategyId, @Param("channelId") Integer channelId );

    @Query(value="select new com.walmart.aex.strategy.dto.MerchMethodResponse(ps.planStrategyId.planId, ps.planStrategyId.strategyId, " +
            " smc.strategyMerchCatgId.lvl0Nbr , " +
            " smc.strategyMerchCatgId.lvl1Nbr , " +
            " smc.strategyMerchCatgId.lvl2Nbr , " +
            " smc.strategyMerchCatgId.lvl3Nbr , " +
            " ssc.strategySubCatgFixtureId.lvl4Nbr , " +
            " sff.strategyFinelineFixtureId.finelineNbr, " +
            " sff.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.fixtureTypeId , " +
            " sf.channelId, " +
            " sf.lvl0GenDesc1, " +
            " sf.lvl1GenDesc1, " +
            " sf.lvl2GenDesc1, " +
            " sf.lvl3GenDesc1, " +
            " sf.lvl4GenDesc1, " +
            " sf.finelineDesc, " +
            "sf.altFinelineName, "+
            " sff.merchMethodCode ) "+
            "FROM PlanStrategy ps " +
            "INNER JOIN StrategyMerchCatg smc " +
            "ON ps.planStrategyId.planId = smc.strategyMerchCatgId.planStrategyId.planId " +
            "AND ps.planStrategyId.strategyId = smc.strategyMerchCatgId.planStrategyId.strategyId " +
            "INNER JOIN StrategyMerchCatgFixture smcf " +
            "ON smc.strategyMerchCatgId.planStrategyId.planId = smcf.strategyMerchCatgFixtureId.strategyMerchCatgId.planStrategyId.planId " +
            "AND smc.strategyMerchCatgId.planStrategyId.strategyId = smcf.strategyMerchCatgFixtureId.strategyMerchCatgId.planStrategyId.strategyId " +
            "AND smc.strategyMerchCatgId.lvl0Nbr = smcf.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl0Nbr " +
            "AND smc.strategyMerchCatgId.lvl1Nbr = smcf.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl1Nbr " +
            "AND smc.strategyMerchCatgId.lvl2Nbr = smcf.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl2Nbr " +
            "AND smc.strategyMerchCatgId.lvl3Nbr = smcf.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl3Nbr " +
            "INNER JOIN StrategySubCatgFixture ssc " +
            "ON smcf.strategyMerchCatgFixtureId.strategyMerchCatgId.planStrategyId.planId = ssc.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.planStrategyId.planId " +
            "AND smcf.strategyMerchCatgFixtureId.strategyMerchCatgId.planStrategyId.strategyId = ssc.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.planStrategyId.strategyId " +
            "AND smcf.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl0Nbr = ssc.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl0Nbr " +
            "AND smcf.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl1Nbr = ssc.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl1Nbr " +
            "AND smcf.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl2Nbr = ssc.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl2Nbr " +
            "AND smcf.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl3Nbr = ssc.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl3Nbr " +
            "AND smcf.strategyMerchCatgFixtureId.fixtureTypeId = ssc.strategySubCatgFixtureId.strategyMerchCatgFixtureId.fixtureTypeId " +
            "INNER JOIN StrategyFinelineFixture sff " +
            "ON ssc.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.planStrategyId.planId = sff.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.planStrategyId.planId " +
            "AND ssc.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.planStrategyId.strategyId = sff.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.planStrategyId.strategyId " +
            "AND ssc.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl0Nbr = sff.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl0Nbr " +
            "AND ssc.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl1Nbr = sff.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl1Nbr " +
            "AND ssc.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl2Nbr = sff.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl2Nbr " +
            "AND ssc.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl3Nbr = sff.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl3Nbr " +
            "AND ssc.strategySubCatgFixtureId.lvl4Nbr = sff.strategyFinelineFixtureId.strategySubCatgFixtureId.lvl4Nbr " +
            "AND ssc.strategySubCatgFixtureId.strategyMerchCatgFixtureId.fixtureTypeId = sff.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.fixtureTypeId " +
            "INNER JOIN StrategyFineline sf "+
            "ON sff.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.planStrategyId.planId = sf.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.planStrategyId.planId " +
            "AND sff.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl0Nbr = sf.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl0Nbr " +
            "AND sff.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl1Nbr = sf.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl1Nbr " +
            "AND sff.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl2Nbr = sf.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl2Nbr " +
            "AND sff.strategyFinelineFixtureId.strategySubCatgFixtureId.strategyMerchCatgFixtureId.strategyMerchCatgId.lvl3Nbr = sf.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl3Nbr " +
            "AND sff.strategyFinelineFixtureId.strategySubCatgFixtureId.lvl4Nbr = sf.strategyFinelineId.strategySubCatgId.lvl4Nbr " +
            "AND sff.strategyFinelineFixtureId.finelineNbr = sf.strategyFinelineId.finelineNbr " +
            "WHERE ps.planStrategyId.planId = :planId AND ps.planStrategyId.strategyId = :strategyId AND sf.channelId in (:channelId, 3) " +
            "AND (:lvl3Nbr = NULL or smc.strategyMerchCatgId.lvl3Nbr = :lvl3Nbr)")
    List<MerchMethodResponse> getMerchMethod(@Param("planId") Long planId,
                                                         @Param("strategyId") Long strategyId, @Param("channelId") Integer channelId, @Param("lvl3Nbr") Integer lvl3Nbr);
    @Query("select fl from StrategyFineline fl where fl.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.planStrategyId.planId= :planId AND fl.strategyFinelineId.finelineNbr IN(:finelineNbr)")
    List <StrategyFineline> findByPlan_idAndFineline_nbr(@Param("planId") Long planId, @Param("finelineNbr") List<Integer> finelineNbr);

    @Query(value = "select fl from StrategyFineline fl where fl.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.planStrategyId.planId= :planId AND fl.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl3Nbr= :lvl3Nbr AND fl.strategyFinelineId.strategySubCatgId.lvl4Nbr= :lvl4Nbr AND fl.strategyFinelineId.finelineNbr IN(:finelineNbr)")
    List<StrategyFineline> findFineLines_ByPlan_Id_AndCat_IdAndSub_Cat_IdAndFineline_nbr(@Param("planId") Long planId, @Param("lvl3Nbr") Integer lvl3Nbr, @Param("lvl4Nbr") Integer lvl4Nbr, @Param("finelineNbr") List<Integer> finelineNbr);

    @Query(value="SELECT DISTINCT new com.walmart.aex.strategy.dto.PlanFinelinesThickness(" +
            "sf.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.planStrategyId.planId, " +
            "sf.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl0Nbr, " +
            "sf.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl1Nbr, "+
            "sf.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl2Nbr, "+
            "sf.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl3Nbr, "+
            "sf.strategyFinelineId.strategySubCatgId.lvl4Nbr, " +
            "sf.strategyFinelineId.finelineNbr, sf.productDimId as thicknessId) FROM StrategyFineline sf WHERE " +
            "sf.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.planStrategyId.planId = :planId AND " +
            "sf.strategyFinelineId.finelineNbr IN (:finelineNbr)")
    List<PlanFinelinesThickness> getFinelinesThickness(@Param("planId") Long planId,
                                                       @Param("finelineNbr") List<Integer> finelineNbr);



}
