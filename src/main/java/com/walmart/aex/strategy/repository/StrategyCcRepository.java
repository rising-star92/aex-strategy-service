package com.walmart.aex.strategy.repository;

import com.walmart.aex.strategy.dto.Rank.FlAndCcRankData;
import com.walmart.aex.strategy.dto.SizeResponseDTO;
import com.walmart.aex.strategy.dto.WeatherClusterCcStrategyDTO;
import com.walmart.aex.strategy.dto.WeatherClusterStrategy;
import com.walmart.aex.strategy.entity.StrategyCc;
import com.walmart.aex.strategy.entity.StrategyCcId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StrategyCcRepository extends JpaRepository<StrategyCc, StrategyCcId> {
    Optional<StrategyCc> findByStrategyCcId(StrategyCcId strategyCcId);

    void deleteStrategyCcByStrategyCcId_StrategyStyleId_StrategyFinelineId_StrategySubCatgId_StrategyMerchCatgId_PlanStrategyId_planIdAndStrategyCcId_StrategyStyleId_StrategyFinelineId_StrategySubCatgId_StrategyMerchCatgId_lvl3NbrAndStrategyCcId_StrategyStyleId_StrategyFinelineId_StrategySubCatgId_lvl4NbrAndStrategyCcId_StrategyStyleId_StrategyFinelineId_finelineNbrAndStrategyCcId_StrategyStyleId_styleNbrAndStrategyCcId_ccId(
            Long planId, Integer lvl3Nbr, Integer lvl4Nbr, Integer finelineNbr, String styleNbr, String ccId);

    @Query(value = "select new com.walmart.aex.strategy.dto.WeatherClusterStrategy(ps.planStrategyId.planId, ps.planStrategyId.strategyId, " +
            "sc.strategyCcId.strategyStyleId.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl3Nbr , " +
            " sf.lvl3GenDesc1 , " +
            "sc.strategyCcId.strategyStyleId.strategyFinelineId.strategySubCatgId.lvl4Nbr , " +
            " sf.lvl4GenDesc1 , " +
            "sc.strategyCcId.strategyStyleId.strategyFinelineId.finelineNbr," +
            "sf.finelineDesc, " +
            "sf.outFitting, " +
            "sf.altFinelineName, "+
            "sc.strategyCcId.strategyStyleId.styleNbr, " +
            "sc.strategyCcId.ccId, " +
            "sc.altCcDesc, " +
            "sc.colorName, " +
            "psc.planClusterStrategyId.analyticsClusterId, "+
            "psc.analyticsClusterLabel, " +
            "elgCc.isEligible, "+
            "elgCc.isEligibleFlag, "+
            "elgCc.inStoreYrWkDesc, " +
            "elgCc.markDownYrWkDesc, " +
            "elgCc.markDownYrWk - elgCc.inStoreYrWk, "+
            "mkt.marketValue, " +
            "elgCc.merchantOverrideRank) " +
            "FROM PlanStrategy ps " +
            "INNER JOIN PlanClusterStrategy psc " +
            "ON ps.planStrategyId.planId = psc.planClusterStrategyId.planStrategyId.planId " +
            "AND ps.planStrategyId.strategyId = psc.planClusterStrategyId.planStrategyId.strategyId " +
            "INNER JOIN StrategyCc sc " +
            "ON psc.planClusterStrategyId.planStrategyId.planId = sc.strategyCcId.strategyStyleId.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.planStrategyId.planId " +
            "AND psc.planClusterStrategyId.planStrategyId.strategyId = sc.strategyCcId.strategyStyleId.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.planStrategyId.strategyId " +
            "INNER JOIN StrategyCcClusEligRanking elgCc " +
            "ON elgCc.strategyCcClusEligRankingId.strategyStyleClusId.strategyFlClusEligRankingId.planClusterStrategyId.planStrategyId.planId = psc.planClusterStrategyId.planStrategyId.planId " +
            "AND elgCc.strategyCcClusEligRankingId.strategyStyleClusId.strategyFlClusEligRankingId.planClusterStrategyId.planStrategyId.strategyId = psc.planClusterStrategyId.planStrategyId.strategyId " +
            "AND elgCc.strategyCcClusEligRankingId.strategyStyleClusId.strategyFlClusEligRankingId.planClusterStrategyId.analyticsClusterId = psc.planClusterStrategyId.analyticsClusterId " +
            "AND elgCc.strategyCcClusEligRankingId.ccId = sc.strategyCcId.ccId " +
            "LEFT JOIN StrategyFineline sf " +
            "ON sf.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.planStrategyId.planId = sc.strategyCcId.strategyStyleId.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.planStrategyId.planId " +
            "AND sf.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.planStrategyId.strategyId = sc.strategyCcId.strategyStyleId.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.planStrategyId.strategyId " +
            "AND sf.strategyFinelineId.finelineNbr = sc.strategyCcId.strategyStyleId.strategyFinelineId.finelineNbr " +
            "LEFT JOIN StrategyCcMktClusElig elgMkt " +
            "ON elgMkt.strategyCcMktClusEligId.strategyCcClusEligRankingId.strategyStyleClusId.strategyFlClusEligRankingId.planClusterStrategyId.planStrategyId.planId = elgCc.strategyCcClusEligRankingId.strategyStyleClusId.strategyFlClusEligRankingId.planClusterStrategyId.planStrategyId.planId " +
            "AND elgMkt.strategyCcMktClusEligId.strategyCcClusEligRankingId.strategyStyleClusId.strategyFlClusEligRankingId.planClusterStrategyId.planStrategyId.strategyId = elgCc.strategyCcClusEligRankingId.strategyStyleClusId.strategyFlClusEligRankingId.planClusterStrategyId.planStrategyId.strategyId " +
            "AND elgMkt.strategyCcMktClusEligId.strategyCcClusEligRankingId.strategyStyleClusId.strategyFlClusEligRankingId.planClusterStrategyId.analyticsClusterId = elgCc.strategyCcClusEligRankingId.strategyStyleClusId.strategyFlClusEligRankingId.planClusterStrategyId.analyticsClusterId " +
            "AND elgMkt.strategyCcMktClusEligId.strategyCcClusEligRankingId.strategyStyleClusId.strategyFlClusEligRankingId.lvl0Nbr = elgCc.strategyCcClusEligRankingId.strategyStyleClusId.strategyFlClusEligRankingId.lvl0Nbr " +
            "AND elgMkt.strategyCcMktClusEligId.strategyCcClusEligRankingId.strategyStyleClusId.strategyFlClusEligRankingId.lvl1Nbr = elgCc.strategyCcClusEligRankingId.strategyStyleClusId.strategyFlClusEligRankingId.lvl1Nbr " +
            "AND elgMkt.strategyCcMktClusEligId.strategyCcClusEligRankingId.strategyStyleClusId.strategyFlClusEligRankingId.lvl2Nbr = elgCc.strategyCcClusEligRankingId.strategyStyleClusId.strategyFlClusEligRankingId.lvl2Nbr " +
            "AND elgMkt.strategyCcMktClusEligId.strategyCcClusEligRankingId.strategyStyleClusId.strategyFlClusEligRankingId.lvl3Nbr = elgCc.strategyCcClusEligRankingId.strategyStyleClusId.strategyFlClusEligRankingId.lvl3Nbr " +
            "AND elgMkt.strategyCcMktClusEligId.strategyCcClusEligRankingId.strategyStyleClusId.strategyFlClusEligRankingId.lvl4Nbr = elgCc.strategyCcClusEligRankingId.strategyStyleClusId.strategyFlClusEligRankingId.lvl4Nbr " +
            "AND elgMkt.strategyCcMktClusEligId.strategyCcClusEligRankingId.strategyStyleClusId.strategyFlClusEligRankingId.finelineNbr = elgCc.strategyCcClusEligRankingId.strategyStyleClusId.strategyFlClusEligRankingId.finelineNbr " +
            "AND elgMkt.strategyCcMktClusEligId.strategyCcClusEligRankingId.ccId = elgCc.strategyCcClusEligRankingId.ccId " +
            "LEFT JOIN MarketSelection mkt " +
            "ON mkt.marketSelectCode = elgMkt.strategyCcMktClusEligId.marketSelectCode " +
            "WHERE ps.planStrategyId.planId = :planId " +
            "AND ps.planStrategyId.strategyId = :strategyId " +
            "AND sc.strategyCcId.strategyStyleId.strategyFinelineId.finelineNbr = :finelineNbr " +
            "AND sc.channelId IN (1,3)")
    List<WeatherClusterStrategy> getCcStrategy(@Param("planId") Long planId, @Param("strategyId") Long strategyId, @Param("finelineNbr") Integer finelineNbr);

    @Query(value = "SELECT ps.plan_id AS planId, ps.strategy_id AS strategyId, elgCcPrg.program_id AS programId," +
            "sc.rpt_lvl_0_nbr AS lvl0Nbr," +
            "sc.rpt_lvl_1_nbr AS lvl1Nbr," +
            "sc.rpt_lvl_2_nbr AS lvl2Nbr," +
            "sc.rpt_lvl_3_nbr AS lvl3Nbr," +
            "sf.rpt_lvl_3_gen_desc1 AS lvl3Name," +
            "sc.rpt_lvl_4_nbr AS lvl4Nbr, " +
            "sf.rpt_lvl_4_gen_desc1 AS lvl4Name, " +
            "sc.fineline_nbr AS finelineNbr, " +
            "sf.fineline_desc AS finelineDesc, " +
            "sf.outfitting_fl_list AS outFitting, " +
            "sf.alt_fineline_desc AS altFinelineName, " +
            "LTRIM(RTRIM(sc.style_nbr)) AS styleNbr, " +
            "LTRIM(RTRIM(sc.customer_choice)) as customerChoice, " +
            "sc.alt_cc_desc as altCcDesc, " +
            "sc.color_name as colorName, " +
            "psc.analytics_cluster_id AS analyticsClusterId, " +
            "psc.analytics_cluster_label AS analyticsClusterDesc, " +
            "COALESCE(elgCcPrg.is_eligible, 0) AS isEligible, " +
            "COALESCE(elgCcPrg.select_status_id, 0) AS isEligibleFlag, " +
            "COALESCE(elgCcPrg.in_store_yrwk_desc, elgCcClus.in_store_yrwk_desc) AS inStoreDate, " +
            "COALESCE(elgCcPrg.markdown_yrwk_desc, elgCcClus.markdown_yrwk_desc) AS markDownDate, " +
            "(CASE WHEN (elgCcPrg.markdown_yr_wk IS NULL AND elgCcPrg.in_store_yr_wk IS NULL) THEN (elgCcClus.markdown_yr_wk - elgCcClus.in_store_yr_wk) " +
            "ELSE (elgCcPrg.markdown_yr_wk - elgCcPrg.in_store_yr_wk) END) AS sellingWeeks, " +
            "mkt.market_value AS excludeOffshore, " +
            "elgCcPrg.merchant_override_rank AS ranking " +
            "FROM [dbo].[plan_strategy] ps " +
            "INNER JOIN [dbo].[plan_strat_clus] psc " +
            "ON psc.plan_id = ps.plan_id AND " +
            "psc.strategy_id = ps.strategy_id " +
            "INNER JOIN [dbo].[strat_cc] sc " +
            "ON sc.plan_id = ps.plan_id AND sc.strategy_id = ps.strategy_id " +
            "INNER JOIN [dbo].[elig_cc_clus_rank] elgCcClus " +
            "ON elgCcClus.plan_id = ps.plan_id AND elgCcClus.strategy_id = ps.strategy_id AND elgCcClus.analytics_cluster_id = psc.analytics_cluster_id " +
            "AND elgCcClus.rpt_lvl_0_nbr = sc.rpt_lvl_0_nbr AND elgCcClus.rpt_lvl_1_nbr = sc.rpt_lvl_1_nbr AND elgCcClus.rpt_lvl_2_nbr = sc.rpt_lvl_2_nbr " +
            "AND elgCcClus.rpt_lvl_3_nbr = sc.rpt_lvl_3_nbr AND elgCcClus.rpt_lvl_4_nbr = sc.rpt_lvl_4_nbr AND elgCcClus.fineline_nbr = sc.fineline_nbr " +
            "AND elgCcClus.customer_choice = sc.customer_choice " +
            "LEFT JOIN Strat_Fl sf " +
            "ON sf.plan_Id = sc.plan_Id AND sf.strategy_Id = sc.strategy_Id AND sf.fineline_Nbr = sc.fineline_Nbr " +
            "LEFT JOIN ( SELECT * FROM [dbo].[elig_cc_clus_prog] WHERE program_id = :programId AND fineline_nbr = :finelineNbr ) elgCcPrg " +
            "ON elgCcPrg.plan_id = ps.plan_id AND elgCcPrg.strategy_id = ps.strategy_id AND elgCcPrg.analytics_cluster_id = elgCcClus.analytics_cluster_id " +
            "AND elgCcPrg.rpt_lvl_0_nbr = elgCcClus.rpt_lvl_0_nbr AND elgCcPrg.rpt_lvl_1_nbr = elgCcClus.rpt_lvl_1_nbr AND elgCcPrg.rpt_lvl_2_nbr = elgCcClus.rpt_lvl_2_nbr " +
            "AND elgCcPrg.rpt_lvl_3_nbr = elgCcClus.rpt_lvl_3_nbr AND elgCcPrg.rpt_lvl_4_nbr = elgCcClus.rpt_lvl_4_nbr AND elgCcPrg.fineline_nbr = elgCcClus.fineline_nbr " +
            "AND elgCcPrg.customer_choice = elgCcClus.customer_choice " +
            "LEFT JOIN [dbo].[elig_cc_mkt_clus_prog] elgPrgMkt " +
            "ON elgPrgMkt.plan_id = ps.plan_id AND elgPrgMkt.strategy_id = ps.strategy_id AND elgPrgMkt.analytics_cluster_id = elgCcClus.analytics_cluster_id " +
            "AND elgPrgMkt.rpt_lvl_0_nbr = elgCcClus.rpt_lvl_0_nbr AND elgPrgMkt.rpt_lvl_1_nbr = elgCcClus.rpt_lvl_1_nbr AND elgPrgMkt.rpt_lvl_2_nbr = elgCcClus.rpt_lvl_2_nbr " +
            "AND elgPrgMkt.rpt_lvl_3_nbr = elgCcClus.rpt_lvl_3_nbr AND elgPrgMkt.rpt_lvl_4_nbr = elgCcClus.rpt_lvl_4_nbr AND elgPrgMkt.fineline_nbr = elgCcClus.fineline_nbr " +
            "AND elgPrgMkt.customer_choice = elgCcClus.customer_choice AND elgPrgMkt.program_id = elgCcPrg.program_id " +
            "LEFT JOIN [dbo].[market_selections] mkt " +
            "ON mkt.market_select_code = elgPrgMkt.market_select_code " +
            "WHERE  " +
            "ps.plan_id = :planId AND " +
            "sc.fineline_nbr = :finelineNbr AND " +
            "sc.channel_id IN (1,3) AND " +
            "ps.strategy_id = :strategyId", nativeQuery = true)
    List<WeatherClusterCcStrategyDTO> getWeatherClusterTraitCcStrategy(@Param("planId") Long planId,
                                                                       @Param("strategyId") Long strategyId,
                                                                       @Param("programId") Long programId,
                                                                       @Param("finelineNbr") Integer finelineNbr);

    @Query(value="select new com.walmart.aex.strategy.dto.SizeResponseDTO(ps.planStrategyId.planId, ps.planStrategyId.strategyId, " +
            " smsc.strategyMerchCatgSPClusId.lvl0Nbr , " +
            " smsc.strategyMerchCatgSPClusId.lvl1Nbr , " +
            " smsc.strategyMerchCatgSPClusId.lvl2Nbr , " +
            " smsc.strategyMerchCatgSPClusId.lvl3Nbr , " +
            " sssc.strategySubCatgSPClusId.lvl4Nbr , " +
            " sfsc.strategyIFineLineId.finelineNbr , " +
            " ssc.strategyStyleSPClusId.styleNbr , " +
            " stStyle.altStyleDesc ," +
            " scsc.strategyCcSPClusId.ccId ," +
            " sc.altCcDesc ," +
            " sc.colorName ," +
            " sc.colorFamily ," +
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
            "LEFT JOIN StrategyStyle stStyle " +
            "ON ssc.strategyStyleSPClusId.strategyFinelineSPClusId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.planClusterStrategyId.planStrategyId.planId = stStyle.strategyStyleId.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.planStrategyId.planId " +
            "AND ssc.strategyStyleSPClusId.strategyFinelineSPClusId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.lvl0Nbr = stStyle.strategyStyleId.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl0Nbr " +
            "AND ssc.strategyStyleSPClusId.strategyFinelineSPClusId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.lvl1Nbr = stStyle.strategyStyleId.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl1Nbr " +
            "AND ssc.strategyStyleSPClusId.strategyFinelineSPClusId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.lvl2Nbr = stStyle.strategyStyleId.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl2Nbr " +
            "AND ssc.strategyStyleSPClusId.strategyFinelineSPClusId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.lvl3Nbr = stStyle.strategyStyleId.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl3Nbr " +
            "AND ssc.strategyStyleSPClusId.strategyFinelineSPClusId.strategySubCatgSPClusId.lvl4Nbr = stStyle.strategyStyleId.strategyFinelineId.strategySubCatgId.lvl4Nbr " +
            "AND ssc.strategyStyleSPClusId.strategyFinelineSPClusId.finelineNbr = stStyle.strategyStyleId.strategyFinelineId.finelineNbr " +
            "AND ssc.strategyStyleSPClusId.styleNbr = stStyle.strategyStyleId.styleNbr " +
            "LEFT JOIN StrategyCc sc " +
            "ON scsc.strategyCcSPClusId.strategyStyleSPClusId.strategyFinelineSPClusId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.planClusterStrategyId.planStrategyId.planId = sc.strategyCcId.strategyStyleId.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.planStrategyId.planId " +
            "AND scsc.strategyCcSPClusId.strategyStyleSPClusId.strategyFinelineSPClusId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.lvl0Nbr = sc.strategyCcId.strategyStyleId.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl0Nbr " +
            "AND scsc.strategyCcSPClusId.strategyStyleSPClusId.strategyFinelineSPClusId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.lvl1Nbr = sc.strategyCcId.strategyStyleId.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl1Nbr " +
            "AND scsc.strategyCcSPClusId.strategyStyleSPClusId.strategyFinelineSPClusId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.lvl2Nbr = sc.strategyCcId.strategyStyleId.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl2Nbr " +
            "AND scsc.strategyCcSPClusId.strategyStyleSPClusId.strategyFinelineSPClusId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.lvl3Nbr = sc.strategyCcId.strategyStyleId.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl3Nbr " +
            "AND scsc.strategyCcSPClusId.strategyStyleSPClusId.strategyFinelineSPClusId.strategySubCatgSPClusId.lvl4Nbr = sc.strategyCcId.strategyStyleId.strategyFinelineId.strategySubCatgId.lvl4Nbr " +
            "AND scsc.strategyCcSPClusId.strategyStyleSPClusId.strategyFinelineSPClusId.finelineNbr = sc.strategyCcId.strategyStyleId.strategyFinelineId.finelineNbr " +
            "AND scsc.strategyCcSPClusId.strategyStyleSPClusId.styleNbr = sc.strategyCcId.strategyStyleId.styleNbr " +
            "AND scsc.strategyCcSPClusId.ccId = sc.strategyCcId.ccId " +
            "WHERE ps.planStrategyId.planId = :planId AND ps.planStrategyId.strategyId = :strategyId AND scsc.strategyCcSPClusId.strategyStyleSPClusId.strategyFinelineSPClusId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.channelId IN (:channelId) AND sfsc.strategyIFineLineId.finelineNbr = :finelineNbr AND " +
            "smsc.strategyMerchCatgSPClusId.planClusterStrategyId.analyticsClusterId = 0")
    List<SizeResponseDTO> getCcSizeByFineline(@Param("planId") Long planId,
                                                @Param("strategyId") Long strategyId, @Param("finelineNbr") Integer finelineNbr, @Param("channelId") Integer channelId);

    @Query(value="select new com.walmart.aex.strategy.dto.SizeResponseDTO(ps.planStrategyId.planId, ps.planStrategyId.strategyId, " +
            " smsc.strategyMerchCatgSPClusId.lvl0Nbr , " +
            " smsc.strategyMerchCatgSPClusId.lvl1Nbr , " +
            " smsc.strategyMerchCatgSPClusId.lvl2Nbr , " +
            " smsc.strategyMerchCatgSPClusId.lvl3Nbr , " +
            " smsc.sizeProfileObj AS catSizeObj, " +
            " sssc.strategySubCatgSPClusId.lvl4Nbr , " +
            " sssc.sizeProfileObj AS subCategorySizeObj, " +
            " sfsc.strategyIFineLineId.finelineNbr , " +
            " sfsc.sizeProfileObj AS fineLineSizeObj, " +
            " sf.lvl0GenDesc1, " +
            " sf.lvl1GenDesc1, " +
            " sf.lvl2GenDesc1, " +
            " sf.lvl3GenDesc1, " +
            " sf.lvl4GenDesc1, " +
            " sf.finelineDesc, "+
            " sf.altFinelineName, "+
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
            "INNER JOIN StrategyFineline sf "+
            "ON sfsc.strategyIFineLineId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.planClusterStrategyId.planStrategyId.planId = sf.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.planStrategyId.planId " +
            "AND sfsc.strategyIFineLineId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.lvl0Nbr = sf.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl0Nbr " +
            "AND sfsc.strategyIFineLineId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.lvl1Nbr = sf.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl1Nbr " +
            "AND sfsc.strategyIFineLineId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.lvl2Nbr = sf.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl2Nbr " +
            "AND sfsc.strategyIFineLineId.strategySubCatgSPClusId.strategyMerchCatgSPClusId.lvl3Nbr = sf.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl3Nbr " +
            "AND sfsc.strategyIFineLineId.strategySubCatgSPClusId.lvl4Nbr = sf.strategyFinelineId.strategySubCatgId.lvl4Nbr " +
            "AND sfsc.strategyIFineLineId.finelineNbr = sf.strategyFinelineId.finelineNbr " +
            "WHERE ps.planStrategyId.planId = :planId AND ps.planStrategyId.strategyId = :strategyId AND smsc.strategyMerchCatgSPClusId.channelId IN (:channelId) " +
            "AND smsc.strategyMerchCatgSPClusId.lvl3Nbr = :lvl3Nbr AND smsc.strategyMerchCatgSPClusId.planClusterStrategyId.analyticsClusterId = 0 ")
    List<SizeResponseDTO> getSizeByCatg(@Param("planId") Long planId, @Param("strategyId") Long strategyId, @Param("lvl3Nbr") Integer lvl3Nbr, @Param("channelId") Integer channelId);

    @Query(value = "SELECT new com.walmart.aex.strategy.dto.Rank.FlAndCcRankData(ps.planStrategyId.planId , " +
            " sc.strategyCcId.strategyStyleId.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl0Nbr , " +
            " sc.strategyCcId.strategyStyleId.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl1Nbr , " +
            " sc.strategyCcId.strategyStyleId.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl2Nbr , " +
            " sc.strategyCcId.strategyStyleId.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl3Nbr , " +
            " sc.strategyCcId.strategyStyleId.strategyFinelineId.strategySubCatgId.lvl4Nbr , " +
            " elgCc.strategyCcClusEligRankingId.strategyStyleClusId.strategyFlClusEligRankingId.finelineNbr , " +
            " elgCc.strategyCcClusEligRankingId.strategyStyleClusId.styleNbr , " +
            " elgCc.strategyCcClusEligRankingId.ccId as customerChoice , " +
            " elgCc.merchantOverrideRank as ccRank) " +
            "FROM PlanStrategy ps " +
            "INNER JOIN PlanClusterStrategy psc " +
            "ON ps.planStrategyId.planId = psc.planClusterStrategyId.planStrategyId.planId " +
            "AND ps.planStrategyId.strategyId = psc.planClusterStrategyId.planStrategyId.strategyId " +
            "INNER JOIN StrategyCc sc " +
            "ON psc.planClusterStrategyId.planStrategyId.planId = sc.strategyCcId.strategyStyleId.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.planStrategyId.planId " +
            "AND psc.planClusterStrategyId.planStrategyId.strategyId = sc.strategyCcId.strategyStyleId.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.planStrategyId.strategyId " +
            "INNER JOIN StrategyCcClusEligRanking elgCc " +
            "ON elgCc.strategyCcClusEligRankingId.strategyStyleClusId.strategyFlClusEligRankingId.planClusterStrategyId.planStrategyId.planId = psc.planClusterStrategyId.planStrategyId.planId " +
            "AND elgCc.strategyCcClusEligRankingId.strategyStyleClusId.strategyFlClusEligRankingId.planClusterStrategyId.planStrategyId.strategyId = psc.planClusterStrategyId.planStrategyId.strategyId " +
            "AND elgCc.strategyCcClusEligRankingId.strategyStyleClusId.strategyFlClusEligRankingId.planClusterStrategyId.analyticsClusterId = psc.planClusterStrategyId.analyticsClusterId  " +
            "AND elgCc.strategyCcClusEligRankingId.strategyStyleClusId.strategyFlClusEligRankingId.lvl0Nbr = sc.strategyCcId.strategyStyleId.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl0Nbr " +
            "AND elgCc.strategyCcClusEligRankingId.strategyStyleClusId.strategyFlClusEligRankingId.lvl1Nbr = sc.strategyCcId.strategyStyleId.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl1Nbr " +
            "AND elgCc.strategyCcClusEligRankingId.strategyStyleClusId.strategyFlClusEligRankingId.lvl2Nbr = sc.strategyCcId.strategyStyleId.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl2Nbr " +
            "AND elgCc.strategyCcClusEligRankingId.strategyStyleClusId.strategyFlClusEligRankingId.lvl3Nbr = sc.strategyCcId.strategyStyleId.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl3Nbr " +
            "AND elgCc.strategyCcClusEligRankingId.strategyStyleClusId.strategyFlClusEligRankingId.lvl4Nbr = sc.strategyCcId.strategyStyleId.strategyFinelineId.strategySubCatgId.lvl4Nbr " +
            "AND elgCc.strategyCcClusEligRankingId.strategyStyleClusId.strategyFlClusEligRankingId.finelineNbr = sc.strategyCcId.strategyStyleId.strategyFinelineId.finelineNbr " +
            "AND elgCc.strategyCcClusEligRankingId.strategyStyleClusId.styleNbr = sc.strategyCcId.strategyStyleId.styleNbr " +
            "AND elgCc.strategyCcClusEligRankingId.ccId = sc.strategyCcId.ccId " +
            "WHERE (ps.planStrategyId.planId in (:planIds) or :includelPlanIds is null) " +
            "AND (sc.strategyCcId.strategyStyleId.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl0Nbr in (:lvl0List) or :includelvl0NbrToFilter is null) " +
            "AND (sc.strategyCcId.strategyStyleId.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl1Nbr in (:lvl1List) or :includelvl1NbrToFilter is null) " +
            "AND (sc.strategyCcId.strategyStyleId.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl2Nbr in (:lvl2List) or :includelvl2NbrToFilter is null) " +
            "AND (sc.strategyCcId.strategyStyleId.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl3Nbr in (:lvl3List) or :includelvl3NbrToFilter is null) " +
            "AND (sc.strategyCcId.strategyStyleId.strategyFinelineId.strategySubCatgId.lvl4Nbr in (:lvl4List) or :includelvl4NbrToFilter is null) " +
            "AND (sc.strategyCcId.strategyStyleId.strategyFinelineId.finelineNbr =:finelineNbr or :finelineNbr is null) " +
            "AND (sc.strategyCcId.strategyStyleId.styleNbr = :styleNbr or :styleNbr is null) " +
            "AND (sc.strategyCcId.ccId = :ccId or :ccId is null) " +
            "AND (elgCc.strategyCcClusEligRankingId.strategyStyleClusId.strategyFlClusEligRankingId.planClusterStrategyId.analyticsClusterId = :cluster) " +
            "AND (psc.planClusterStrategyId.analyticsClusterId = :cluster)" +
            "AND (sc.channelId IN (:channel,3) or :channel is null)")
    List<FlAndCcRankData> getCcRankData(@Param("includelPlanIds") Boolean includelPlanIds,
                                        @Param("planIds") List<Long> planIds, @Param("includelvl0NbrToFilter") Boolean includelvl0NbrToFilter,
                                        @Param("lvl0List") List<Integer> lvl0List, @Param("includelvl1NbrToFilter") Boolean includelvl1NbrToFilter,
                                        @Param("lvl1List") List<Integer> lvl1List, @Param("includelvl2NbrToFilter") Boolean includelvl2NbrToFilter,
                                        @Param("lvl2List") List<Integer> lvl2List, @Param("includelvl3NbrToFilter") Boolean includelvl3NbrToFilter,
                                        @Param("lvl3List") List<Integer> lvl3List, @Param("includelvl4NbrToFilter") Boolean includelvl4NbrToFilter,
                                        @Param("lvl4List") List<Integer> lvl4List, @Param("finelineNbr") Integer finelineNbr,
                                        @Param("styleNbr") String styleNbr,@Param("ccId") String ccId, @Param("channel") Integer channel, @Param("cluster") Integer cluster);


    @Query(value = "SELECT new com.walmart.aex.strategy.dto.Rank.FlAndCcRankData(ps.planStrategyId.planId , " +
            " sf.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl0Nbr , " +
            " sf.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl1Nbr , " +
            " sf.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl2Nbr , " +
            " sf.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl3Nbr , " +
            " sf.strategyFinelineId.strategySubCatgId.lvl4Nbr , " +
            " elgCc.strategyCcClusEligRankingId.strategyStyleClusId.strategyFlClusEligRankingId.finelineNbr , " +
            " elgCc.strategyCcClusEligRankingId.strategyStyleClusId.styleNbr , " +
            " elgCc.strategyCcClusEligRankingId.ccId as customerChoice , " +
            " elgCcPrg.merchantOverrideRank as ccRank) " +
            "FROM PlanStrategy ps " +
            "INNER JOIN PlanClusterStrategy psc " +
            "ON ps.planStrategyId.planId = psc.planClusterStrategyId.planStrategyId.planId " +
            "AND ps.planStrategyId.strategyId = psc.planClusterStrategyId.planStrategyId.strategyId " +
            "INNER JOIN StrategyFineline sf " +
            "ON ps.planStrategyId.planId = sf.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.planStrategyId.planId " +
            "AND ps.planStrategyId.strategyId = sf.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.planStrategyId.strategyId " +
            "INNER JOIN StrategyStyle ss " +
            "ON sf.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.planStrategyId.planId = ss.strategyStyleId.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.planStrategyId.planId " +
            "AND sf.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.planStrategyId.strategyId = ss.strategyStyleId.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.planStrategyId.strategyId " +
            "AND sf.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl0Nbr = ss.strategyStyleId.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl0Nbr " +
            "AND sf.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl1Nbr = ss.strategyStyleId.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl1Nbr " +
            "AND sf.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl2Nbr = ss.strategyStyleId.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl2Nbr " +
            "AND sf.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl3Nbr = ss.strategyStyleId.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl3Nbr " +
            "AND sf.strategyFinelineId.strategySubCatgId.lvl4Nbr = ss.strategyStyleId.strategyFinelineId.strategySubCatgId.lvl4Nbr " +
            "AND sf.strategyFinelineId.finelineNbr = ss.strategyStyleId.strategyFinelineId.finelineNbr " +
            "INNER JOIN StrategyCc sc " +
            "ON sc.strategyCcId.strategyStyleId.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.planStrategyId.planId = ss.strategyStyleId.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.planStrategyId.planId " +
            "AND sc.strategyCcId.strategyStyleId.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.planStrategyId.strategyId = ss.strategyStyleId.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.planStrategyId.strategyId " +
            "AND sc.strategyCcId.strategyStyleId.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl0Nbr = ss.strategyStyleId.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl0Nbr " +
            "AND sc.strategyCcId.strategyStyleId.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl1Nbr = ss.strategyStyleId.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl1Nbr " +
            "AND sc.strategyCcId.strategyStyleId.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl2Nbr = ss.strategyStyleId.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl2Nbr " +
            "AND sc.strategyCcId.strategyStyleId.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl3Nbr = ss.strategyStyleId.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl3Nbr " +
            "AND sc.strategyCcId.strategyStyleId.strategyFinelineId.strategySubCatgId.lvl4Nbr = ss.strategyStyleId.strategyFinelineId.strategySubCatgId.lvl4Nbr " +
            "AND sc.strategyCcId.strategyStyleId.strategyFinelineId.finelineNbr = ss.strategyStyleId.strategyFinelineId.finelineNbr " +
            "AND sc.strategyCcId.strategyStyleId.styleNbr = ss.strategyStyleId.styleNbr " +
            "INNER JOIN StrategyCcClusEligRanking elgCc " +
            "ON elgCc.strategyCcClusEligRankingId.strategyStyleClusId.strategyFlClusEligRankingId.planClusterStrategyId.planStrategyId.planId = psc.planClusterStrategyId.planStrategyId.planId " +
            "AND elgCc.strategyCcClusEligRankingId.strategyStyleClusId.strategyFlClusEligRankingId.planClusterStrategyId.planStrategyId.strategyId = psc.planClusterStrategyId.planStrategyId.strategyId " +
            "AND elgCc.strategyCcClusEligRankingId.strategyStyleClusId.strategyFlClusEligRankingId.planClusterStrategyId.analyticsClusterId = psc.planClusterStrategyId.analyticsClusterId  " +
            "AND elgCc.strategyCcClusEligRankingId.strategyStyleClusId.strategyFlClusEligRankingId.lvl0Nbr = sc.strategyCcId.strategyStyleId.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl0Nbr " +
            "AND elgCc.strategyCcClusEligRankingId.strategyStyleClusId.strategyFlClusEligRankingId.lvl1Nbr = sc.strategyCcId.strategyStyleId.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl1Nbr " +
            "AND elgCc.strategyCcClusEligRankingId.strategyStyleClusId.strategyFlClusEligRankingId.lvl2Nbr = sc.strategyCcId.strategyStyleId.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl2Nbr " +
            "AND elgCc.strategyCcClusEligRankingId.strategyStyleClusId.strategyFlClusEligRankingId.lvl3Nbr = sc.strategyCcId.strategyStyleId.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl3Nbr " +
            "AND elgCc.strategyCcClusEligRankingId.strategyStyleClusId.strategyFlClusEligRankingId.lvl4Nbr = sc.strategyCcId.strategyStyleId.strategyFinelineId.strategySubCatgId.lvl4Nbr " +
            "AND elgCc.strategyCcClusEligRankingId.strategyStyleClusId.strategyFlClusEligRankingId.finelineNbr = sc.strategyCcId.strategyStyleId.strategyFinelineId.finelineNbr " +
            "AND elgCc.strategyCcClusEligRankingId.strategyStyleClusId.styleNbr = sc.strategyCcId.strategyStyleId.styleNbr " +
            "AND elgCc.strategyCcClusEligRankingId.ccId = sc.strategyCcId.ccId " +
            "LEFT JOIN EligCcClusProg elgCcPrg " +
            "ON elgCcPrg.eligCcClusProgId.eligStyleClusProgId.strategyFlClusPrgmEligRankingId.strategyFlClusEligRankingId.planClusterStrategyId.planStrategyId.planId = elgCc.strategyCcClusEligRankingId.strategyStyleClusId.strategyFlClusEligRankingId.planClusterStrategyId.planStrategyId.planId " +
            "AND elgCcPrg.eligCcClusProgId.eligStyleClusProgId.strategyFlClusPrgmEligRankingId.strategyFlClusEligRankingId.planClusterStrategyId.planStrategyId.strategyId = elgCc.strategyCcClusEligRankingId.strategyStyleClusId.strategyFlClusEligRankingId.planClusterStrategyId.planStrategyId.strategyId " +
            "AND elgCcPrg.eligCcClusProgId.eligStyleClusProgId.strategyFlClusPrgmEligRankingId.strategyFlClusEligRankingId.planClusterStrategyId.analyticsClusterId = elgCc.strategyCcClusEligRankingId.strategyStyleClusId.strategyFlClusEligRankingId.planClusterStrategyId.analyticsClusterId " +
            "AND elgCcPrg.eligCcClusProgId.eligStyleClusProgId.strategyFlClusPrgmEligRankingId.strategyFlClusEligRankingId.lvl0Nbr = elgCc.strategyCcClusEligRankingId.strategyStyleClusId.strategyFlClusEligRankingId.lvl0Nbr " +
            "AND elgCcPrg.eligCcClusProgId.eligStyleClusProgId.strategyFlClusPrgmEligRankingId.strategyFlClusEligRankingId.lvl1Nbr = elgCc.strategyCcClusEligRankingId.strategyStyleClusId.strategyFlClusEligRankingId.lvl1Nbr " +
            "AND elgCcPrg.eligCcClusProgId.eligStyleClusProgId.strategyFlClusPrgmEligRankingId.strategyFlClusEligRankingId.lvl2Nbr = elgCc.strategyCcClusEligRankingId.strategyStyleClusId.strategyFlClusEligRankingId.lvl2Nbr " +
            "AND elgCcPrg.eligCcClusProgId.eligStyleClusProgId.strategyFlClusPrgmEligRankingId.strategyFlClusEligRankingId.lvl3Nbr = elgCc.strategyCcClusEligRankingId.strategyStyleClusId.strategyFlClusEligRankingId.lvl3Nbr " +
            "AND elgCcPrg.eligCcClusProgId.eligStyleClusProgId.strategyFlClusPrgmEligRankingId.strategyFlClusEligRankingId.lvl4Nbr = elgCc.strategyCcClusEligRankingId.strategyStyleClusId.strategyFlClusEligRankingId.lvl4Nbr " +
            "AND elgCcPrg.eligCcClusProgId.eligStyleClusProgId.strategyFlClusPrgmEligRankingId.strategyFlClusEligRankingId.finelineNbr = elgCc.strategyCcClusEligRankingId.strategyStyleClusId.strategyFlClusEligRankingId.finelineNbr " +
            "AND elgCcPrg.eligCcClusProgId.eligStyleClusProgId.styleNbr = elgCc.strategyCcClusEligRankingId.strategyStyleClusId.styleNbr " +
            "AND elgCcPrg.eligCcClusProgId.ccId = elgCc.strategyCcClusEligRankingId.ccId " +
            "WHERE (ps.planStrategyId.planId in (:planIds) or :includelPlanIds is null) " +
            "AND (sf.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl0Nbr in (:lvl0List) or :includelvl0NbrToFilter is null) " +
            "AND (sf.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl1Nbr in (:lvl1List) or :includelvl1NbrToFilter is null) " +
            "AND (sf.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl2Nbr in (:lvl2List) or :includelvl2NbrToFilter is null) " +
            "AND (sf.strategyFinelineId.strategySubCatgId.strategyMerchCatgId.lvl3Nbr in (:lvl3List) or :includelvl3NbrToFilter is null) " +
            "AND (sf.strategyFinelineId.strategySubCatgId.lvl4Nbr in (:lvl4List) or :includelvl4NbrToFilter is null) " +
            "AND (sf.strategyFinelineId.finelineNbr =:finelineNbr or :finelineNbr is null)  " +
            "AND (ss.strategyStyleId.styleNbr = :styleNbr or :styleNbr is null) " +
            "AND (sc.strategyCcId.ccId = :ccId or :ccId is null) " +
            "AND (elgCc.strategyCcClusEligRankingId.strategyStyleClusId.strategyFlClusEligRankingId.planClusterStrategyId.analyticsClusterId = :cluster) " +
            "AND (psc.planClusterStrategyId.analyticsClusterId = :cluster)" +
            "AND (elgCcPrg.merchantOverrideRank != null) " +
            "AND (sf.traitChoiceCode IN (1,3)) " +
            "AND (sc.channelId IN (:channel,3) or :channel is null)")
    List<FlAndCcRankData> getCcRankPrgData(@Param("includelPlanIds") Boolean includelPlanIds,
                                        @Param("planIds") List<Long> planIds, @Param("includelvl0NbrToFilter") Boolean includelvl0NbrToFilter,
                                        @Param("lvl0List") List<Integer> lvl0List, @Param("includelvl1NbrToFilter") Boolean includelvl1NbrToFilter,
                                        @Param("lvl1List") List<Integer> lvl1List, @Param("includelvl2NbrToFilter") Boolean includelvl2NbrToFilter,
                                        @Param("lvl2List") List<Integer> lvl2List, @Param("includelvl3NbrToFilter") Boolean includelvl3NbrToFilter,
                                        @Param("lvl3List") List<Integer> lvl3List, @Param("includelvl4NbrToFilter") Boolean includelvl4NbrToFilter,
                                        @Param("lvl4List") List<Integer> lvl4List, @Param("finelineNbr") Integer finelineNbr,
                                        @Param("styleNbr") String styleNbr,@Param("ccId") String ccId, @Param("channel") Integer channel, @Param("cluster") Integer cluster);



}
