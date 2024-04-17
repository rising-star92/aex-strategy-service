package com.walmart.aex.strategy.repository;

import com.walmart.aex.strategy.dto.RunRFAFetchDTO;
import com.walmart.aex.strategy.entity.AllocationRunTypeText;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RunRFARepository extends Repository<AllocationRunTypeText, Integer> {

  @Query(value = "SELECT DISTINCT sff.plan_id AS planId,\n" +
          "    sff.strategy_id AS strategyId,\n" +
          "    sf.strategy_id AS weatherClusterStrategyId,\n" +
          "    sg.season_code AS seasonCode,\n" +
          "    sg.fiscal_year AS fiscalYear,\n" +
          "    sff.rpt_lvl_0_nbr AS lvl0Nbr,\n" +
          "    sff.rpt_lvl_1_nbr AS lvl1Nbr,\n" +
          "    sff.rpt_lvl_2_nbr AS lvl2Nbr,\n" +
          "    sff.rpt_lvl_3_nbr AS lvl3Nbr,\n" +
          "    sf.rpt_lvl_3_gen_desc1 AS lvl3Name,\n" +
          "    sff.rpt_lvl_4_nbr AS lvl4Nbr,\n" +
          "    sf.rpt_lvl_4_gen_desc1 AS lvl4Name,\n" +
          "    sff.fineline_nbr AS finelineNbr,\n" +
          "    sf.fineline_desc AS finelineName,\n" +
          "    sf.alt_fineline_desc As altFinelineName,\n" +
          "    sf.outfitting_fl_list AS outFitting,\n" +
          "    sf.brand_obj as brands,\n" +
          "    CASE \n" +
          " WHEN sf.trait_choice_code = 1 THEN 'Traited'\n" +
          "    WHEN sf.trait_choice_code = 2 THEN 'Non-Traited'\n" +
          "    ELSE 'Both'\n" +
          "END AS traitChoice,\n" +
          "sf.alloc_run_type_code AS allocRunTypeCode,\n" +
          "    artt.alloc_run_type_desc AS allocRunTypeDesc,\n" +
          "    sf.run_status_code AS runStatusCode,\n" +
          "    rst.run_status_desc AS runStatusDesc,\n" +
          "    sf.rfa_status_code AS rfaStatusCode,\n" +
          "    rfast.rfa_status_desc AS rfaStatusDesc,\n" +
          "    COALESCE(COALESCE(efcpTemp.ProgramClusterRank,efcr.merchant_override_rank),efcm.analytics_cluster_rank) AS finelineRank\n" +
          "FROM dbo.strat_fl_fixture sff\n" +
          "LEFT JOIN dbo.strat_fl sf\n" +
          "    ON sf.plan_id = sff.plan_id\n" +
          "        AND sf.rpt_lvl_0_nbr = sff.rpt_lvl_0_nbr\n" +
          "        AND sf.rpt_lvl_1_nbr = sff.rpt_lvl_1_nbr\n" +
          "        AND sf.rpt_lvl_2_nbr = sff.rpt_lvl_2_nbr\n" +
          "        AND sf.rpt_lvl_3_nbr = sff.rpt_lvl_3_nbr\n" +
          "        AND sf.rpt_lvl_4_nbr = sff.rpt_lvl_4_nbr\n" +
          "        AND sf.fineline_nbr = sff.fineline_nbr\n" +
          "    LEFT JOIN \n" +
          "    (SELECT  efcp.plan_id,\n" +
          "        efcp.strategy_id,\n" +
          "        efcp.rpt_lvl_0_nbr,\n" +
          "        efcp.rpt_lvl_1_nbr,\n" +
          "        efcp.rpt_lvl_2_nbr,\n" +
          "        efcp.rpt_lvl_3_nbr,\n" +
          "        efcp.rpt_lvl_4_nbr,\n" +
          "        efcp.fineline_nbr,\n" +
          "        min(efcp.merchant_override_rank) as ProgramClusterRank\n" +
          "        FROM dbo.elig_fl_clus_prog efcp \n" +
          "        WHERE efcp.analytics_cluster_id = 0\n" +
          "        GROUP BY\n" +
          "        efcp.plan_id,\n" +
          "    efcp.strategy_id,\n" +
          "    efcp.rpt_lvl_0_nbr,\n" +
          "    efcp.rpt_lvl_1_nbr,\n" +
          "    efcp.rpt_lvl_2_nbr,\n" +
          "    efcp.rpt_lvl_3_nbr,\n" +
          "    efcp.rpt_lvl_4_nbr,\n" +
          "    efcp.fineline_nbr) AS efcpTemp\n" +
          "    ON sf.plan_id = efcpTemp.plan_id\n" +
          "        AND sf.strategy_id = efcpTemp.strategy_id\n" +
          "        AND sf.rpt_lvl_0_nbr = efcpTemp.rpt_lvl_0_nbr\n" +
          "        AND sf.rpt_lvl_1_nbr = efcpTemp.rpt_lvl_1_nbr\n" +
          "        AND sf.rpt_lvl_2_nbr = efcpTemp.rpt_lvl_2_nbr\n" +
          "        AND sf.rpt_lvl_3_nbr = efcpTemp.rpt_lvl_3_nbr\n" +
          "        AND sf.rpt_lvl_4_nbr = efcpTemp.rpt_lvl_4_nbr\n" +
          "        AND sf.fineline_nbr = efcpTemp.fineline_nbr\n" +
          "        LEFT JOIN dbo.elig_fl_clus_rank efcr \n" +
          "        ON sf.plan_id = efcr.plan_id\n" +
          "        AND sf.strategy_id = efcr.strategy_id\n" +
          "        AND sf.rpt_lvl_0_nbr = efcr.rpt_lvl_0_nbr\n" +
          "        AND sf.rpt_lvl_1_nbr = efcr.rpt_lvl_1_nbr\n" +
          "        AND sf.rpt_lvl_2_nbr = efcr.rpt_lvl_2_nbr\n" +
          "        AND sf.rpt_lvl_3_nbr = efcr.rpt_lvl_3_nbr\n" +
          "        AND sf.rpt_lvl_4_nbr = efcr.rpt_lvl_4_nbr\n" +
          "        AND sf.fineline_nbr = efcr.fineline_nbr\n" +
          "        AND efcr.analytics_cluster_id = 0\n" +
          "        LEFT JOIN  dbo.elig_fl_clus_metrics efcm\n" +
          "        ON sf.plan_id = efcm.plan_id\n" +
          "        AND sf.strategy_id = efcm.strategy_id\n" +
          "        AND sf.rpt_lvl_0_nbr = efcm.rpt_lvl_0_nbr\n" +
          "        AND sf.rpt_lvl_1_nbr = efcm.rpt_lvl_1_nbr\n" +
          "        AND sf.rpt_lvl_2_nbr = efcm.rpt_lvl_2_nbr\n" +
          "        AND sf.rpt_lvl_3_nbr = efcm.rpt_lvl_3_nbr\n" +
          "        AND sf.rpt_lvl_4_nbr = efcm.rpt_lvl_4_nbr\n" +
          "        AND sf.fineline_nbr = efcm.fineline_nbr\n" +
          "        AND efcm.analytics_cluster_id = 0\n" +
          "        LEFT JOIN dbo.alloc_run_type_text artt\n" +
          "    ON sf.alloc_run_type_code = artt.alloc_run_type_code\n" +
          "    LEFT JOIN dbo.run_status_text rst\n" +
          "    ON sf.run_status_code = rst.run_status_code\n" +
          "    LEFT JOIN dbo.rfa_status_text rfast\n" +
          "    ON sf.rfa_status_code = rfast.rfa_status_code\n" +
          "    LEFT JOIN dbo.strat_group sg\n" +
          "    ON sf.strategy_id = sg.strategy_id\n" +
          "WHERE sff.strategy_id = :strategyId\n" +
          "    AND sff.plan_id = :planId\n" +
          "    AND sf.channel_id in(1,3)\n" +
          "    AND sf.trait_choice_code IS NOT NULL", nativeQuery = true)
  List<RunRFAFetchDTO> getRunRFAStatusForFinelines(@Param("planId") Long planId, @Param("strategyId") Long strategyId);
}
