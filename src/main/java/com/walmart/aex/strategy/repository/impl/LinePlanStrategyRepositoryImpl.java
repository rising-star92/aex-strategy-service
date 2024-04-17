package com.walmart.aex.strategy.repository.impl;

import com.walmart.aex.strategy.dto.LinePlanCount;
import com.walmart.aex.strategy.repository.LinePlanStrategyRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class LinePlanStrategyRepositoryImpl implements LinePlanStrategyRepository {

    private static final String TY_LINE_PLAN_COUNT = "SELECT 1 type,\n" +
            "         plan_id AS planId,\n" +
            "         rpt_lvl_0_nbr AS lvl0Nbr,\n" +
            "         rpt_lvl_1_nbr AS lvl1Nbr,\n" +
            "         rpt_lvl_2_nbr AS lvl2Nbr,\n" +
            "         '' lvl3Nbr,\n" +
            "            '' lvl4Nbr,\n" +
            "               sum(fl_count) AS finelineCount,\n" +
            "               sum(cc_count) AS customerChoiceCount\n" +
            "FROM strat_merchcatg_lineplan\n" +
            "WHERE plan_id = :planId\n" +
            "  AND channel_id = :channelId\n" +
            "GROUP BY plan_id,\n" +
            "         rpt_lvl_0_nbr,\n" +
            "         rpt_lvl_1_nbr,\n" +
            "         rpt_lvl_2_nbr\n" +
            "UNION ALL\n" +
            "SELECT 2 type,\n" +
            "         plan_id AS planId,\n" +
            "         rpt_lvl_0_nbr AS lvl0Nbr,\n" +
            "         rpt_lvl_1_nbr AS lvl1Nbr,\n" +
            "         rpt_lvl_2_nbr AS lvl2Nbr,\n" +
            "         rpt_lvl_3_nbr AS lvl3Nbr,\n" +
            "         '' lvl4Nbr,\n" +
            "            sum(fl_count) AS finelineCount,\n" +
            "            sum(cc_count) AS customerChoiceCount\n" +
            "FROM strat_merchcatg_lineplan\n" +
            "WHERE plan_id = :planId\n" +
            "  AND channel_id = :channelId\n" +
            "GROUP BY plan_id,\n" +
            "         rpt_lvl_0_nbr,\n" +
            "         rpt_lvl_1_nbr,\n" +
            "         rpt_lvl_2_nbr,\n" +
            "         rpt_lvl_3_nbr\n" +
            "UNION ALL\n" +
            "SELECT 3 type,\n" +
            "         plan_id AS planId,\n" +
            "         rpt_lvl_0_nbr AS lvl0Nbr,\n" +
            "         rpt_lvl_1_nbr AS lvl1Nbr,\n" +
            "         rpt_lvl_2_nbr AS lvl2Nbr,\n" +
            "         rpt_lvl_3_nbr AS lvl3Nbr,\n" +
            "         rpt_lvl_4_nbr AS lvl4Nbr,\n" +
            "         sum(fl_count) AS finelineCount,\n" +
            "         sum(cc_count) AS customerChoiceCount\n" +
            "FROM strat_subcatg_lineplan\n" +
            "WHERE plan_id = :planId\n" +
            "  AND channel_id = :channelId\n" +
            "GROUP BY plan_id,\n" +
            "         rpt_lvl_0_nbr,\n" +
            "         rpt_lvl_1_nbr,\n" +
            "         rpt_lvl_2_nbr,\n" +
            "         rpt_lvl_3_nbr,\n" +
            "         rpt_lvl_4_nbr";

    private final NamedParameterJdbcTemplate strategyJdbcTemplate;

    public LinePlanStrategyRepositoryImpl(@Qualifier("strategyJdbcTemplate") NamedParameterJdbcTemplate strategyJdbcTemplate) {
        this.strategyJdbcTemplate = strategyJdbcTemplate;
    }

    @Override
    public List<LinePlanCount> fetchCurrentLinePlanCount(Long planId, Integer channelId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("planId", planId);
        params.addValue("channelId", channelId);

        return strategyJdbcTemplate.query(TY_LINE_PLAN_COUNT, params, new BeanPropertyRowMapper<>(LinePlanCount.class));
    }

}
