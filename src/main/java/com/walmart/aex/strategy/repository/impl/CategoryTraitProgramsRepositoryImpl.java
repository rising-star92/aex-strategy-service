package com.walmart.aex.strategy.repository.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.walmart.aex.strategy.dto.CategoryTraitProgram;
import com.walmart.aex.strategy.repository.CategoryTraitProgramsRepository;

@Repository
public class CategoryTraitProgramsRepositoryImpl implements CategoryTraitProgramsRepository{
	
	private static final String CATEGORY_TRAIT_PROGRAMS = "SELECT rpt_lvl_1_nbr as deptId, rpt_lvl_3_nbr as catgId, SUBSTRING(min(in_store_yrwk_desc), 4, 4)+SUBSTRING(min(in_store_yrwk_desc), 10, 2) AS startYrWeek, SUBSTRING(max(markdown_yrwk_desc), 4, 4)+SUBSTRING(max(markdown_yrwk_desc), 10, 2) AS endYrWeek\n" +
			"            FROM dbo.elig_fl_clus_rank\n" +
			"            WHERE plan_id = :planId\n" +
			"            GROUP BY rpt_lvl_1_nbr, rpt_lvl_3_nbr\n" +
			"        UNION\n" +
			"            SELECT rpt_lvl_1_nbr as deptId, rpt_lvl_3_nbr as catgId, SUBSTRING(min(in_store_yrwk_desc), 4, 4)+SUBSTRING(min(in_store_yrwk_desc), 10, 2) AS startYrWeek, SUBSTRING(max(markdown_yrwk_desc), 4, 4)+SUBSTRING(max(markdown_yrwk_desc), 10, 2) AS endYrWeek\n" +
			"            FROM dbo.elig_fl_clus_prog\n" +
			"            WHERE plan_id = :planId\n" +
			"            GROUP BY rpt_lvl_1_nbr, rpt_lvl_3_nbr;";
	
	private final NamedParameterJdbcTemplate aexJdbcTemplate;
	
	public CategoryTraitProgramsRepositoryImpl(@Qualifier("strategyJdbcTemplate") NamedParameterJdbcTemplate aexJdbcTemplate) {
        this.aexJdbcTemplate = aexJdbcTemplate;
    }

	@Override
	public List<CategoryTraitProgram> getCategoryTraitPrograms(Long planId) {
		MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("planId", planId);
        return aexJdbcTemplate.query(CATEGORY_TRAIT_PROGRAMS, params, new BeanPropertyRowMapper<>(CategoryTraitProgram.class));
	}

}
