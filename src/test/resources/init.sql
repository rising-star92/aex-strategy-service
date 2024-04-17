CREATE SCHEMA IF NOT EXISTS dbo;

DROP TABLE IF EXISTS dbo.strat_cc_sp_clus;
DROP TABLE IF EXISTS dbo.strat_style_sp_clus ;
DROP TABLE IF EXISTS dbo.strat_fl_sp_clus;
DROP TABLE IF EXISTS dbo.strat_subcatg_sp_clus;
DROP TABLE IF EXISTS dbo.strat_merchcatg_sp_clus;
DROP TABLE IF EXISTS dbo.channel_text;
DROP TABLE IF EXISTS dbo.strat_clus_store;
DROP TABLE IF EXISTS dbo.fiscal_years;
DROP TABLE IF EXISTS dbo.weeks;
DROP TABLE IF EXISTS dbo.strat_cc_fixture;
DROP TABLE IF EXISTS dbo.strat_cc;
DROP TABLE IF EXISTS dbo.strat_style_fixture;
DROP TABLE IF EXISTS dbo.strat_style;
DROP TABLE IF EXISTS dbo.strat_fl_fixture_rank;
DROP TABLE IF EXISTS dbo.strat_fl_fixture;
DROP TABLE IF EXISTS dbo.strat_fl;
DROP TABLE IF EXISTS dbo.strat_subcatg_fixture;
DROP TABLE IF EXISTS dbo.strat_subcatg;
DROP TABLE IF EXISTS dbo.strat_subcatg_lineplan;
DROP TABLE IF EXISTS dbo.strat_merchcatg_fixture;
DROP TABLE IF EXISTS dbo.fixturetype_rollup;
DROP TABLE IF EXISTS dbo.strat_merchcatg;
DROP TABLE IF EXISTS dbo.strat_merchcatg_lineplan;
DROP TABLE IF EXISTS dbo.elig_fl_clus_metrics;
DROP TABLE IF EXISTS dbo.elig_fl_mkt_clus;
DROP TABLE IF EXISTS dbo.elig_cc_mkt_clus;
DROP TABLE IF EXISTS dbo.market_selections;
DROP TABLE IF EXISTS dbo.elig_cc_clus_rank;
DROP TABLE IF EXISTS dbo.clus_style;
DROP TABLE IF EXISTS dbo.elig_cc_clus_prog;
DROP TABLE IF EXISTS dbo.elig_style_clus_prog;
DROP TABLE IF EXISTS dbo.elig_fl_mkt_clus_prog;
DROP TABLE IF EXISTS dbo.elig_fl_clus_prog;
DROP TABLE IF EXISTS dbo.elig_fl_clus_rank;
DROP TABLE IF EXISTS dbo.plan_strat_clus;
DROP TABLE IF EXISTS dbo.plan_strategy;
DROP TABLE IF EXISTS dbo.strat_group;
DROP TABLE IF EXISTS dbo.strat_group_type;
DROP TABLE IF EXISTS dbo.strat_clus;
DROP TABLE IF EXISTS dbo.program_store;
DROP TABLE IF EXISTS dbo.program;
DROP TABLE IF EXISTS dbo.rfa_status_text;
DROP TABLE IF EXISTS dbo.run_status_text;
DROP TABLE IF EXISTS dbo.alloc_run_type_text;
DRop TABLE IF EXISTS dbo.subcatg_minmax;


CREATE TABLE IF NOT EXISTS dbo.strat_clus(
   strategy_id bigint NOT NULL,
   analytics_cluster_id int NOT NULL,
    analytics_cluster_label varchar(100) NULL,
   CONSTRAINT pk1_stratc PRIMARY KEY (strategy_id, analytics_cluster_id)
);

CREATE TABLE dbo.program(
	program_id bigint IDENTITY(1,1) NOT NULL,
	aex_program_name varchar(160) NOT NULL,
	trait_nbr int NOT NULL,
	CONSTRAINT pk1_prog PRIMARY KEY (program_id)
	);
	

CREATE TABLE dbo.program_store(
	program_id bigint NOT NULL,
	store_nbr int NOT NULL,
	country_code char(2) NULL,
    state_province_code char(2) NULL,
	CONSTRAINT pk1_progstr PRIMARY KEY (program_id, store_nbr)
	);

ALTER TABLE dbo.program_store
    ADD CONSTRAINT fk1_progstr FOREIGN KEY (program_id) REFERENCES dbo.program (program_id);

CREATE TABLE IF NOT EXISTS dbo.strat_clus_store(
	strategy_id bigint NOT NULL,
	analytics_cluster_id int NOT NULL,
	store_nbr int NOT NULL,
	country_code char(2) NULL,
	state_province_code char(2) NULL,
	CONSTRAINT pk1_scs PRIMARY KEY (strategy_id, analytics_cluster_id, store_nbr)
);

CREATE TABLE IF NOT EXISTS dbo.fiscal_years(
    fiscal_year_id int NOT NULL,
    wm_year int NOT NULL,
    fiscal_year_desc varchar(15) NOT NULL,
    fiscal_year int NOT NULL
    );

CREATE TABLE IF NOT EXISTS dbo.fixturetype_rollup(
    fixturetype_rollup_id int NOT NULL,
    fixturetype_rollup_name varchar(75) NOT NULL,
    fixturetype_rollup_desc varchar(255) NOT NULL
    );

CREATE TABLE IF NOT EXISTS dbo.weeks(
    wm_yr_wk smallint NOT NULL,
    fiscal_year_id bigint NOT NULL,
    fiscal_week_desc char(15) NOT NULL,
    wm_week tinyint NOT NULL,
    wm_year smallint NOT NULL,
    wm_month tinyint NOT NULL,
    wm_qtr tinyint NOT NULL,
    season_code char(2) NULL,
    wm_week_code char(4) NULL,
    gregorian_start_date date NULL,
    yr_wk_ccyyww int NULL,
    ly_comp_yr_wk int NULL,
    lly_comp_yr_wk int NULL
    );
    
CREATE TABLE IF NOT EXISTS dbo.strat_group_type(
	strategy_group_type_code smallint NOT NULL,
	strategy_group_desc nvarchar(100) NULL,
	detailed_desc nvarchar(255) NULL,
	CONSTRAINT pk1_sgt PRIMARY KEY (strategy_group_type_code)
);
    
CREATE TABLE IF NOT EXISTS dbo.strat_group(
	strategy_id bigint IDENTITY(1,1) NOT NULL,
	strategy_group_type_code smallint NOT NULL,
	analytics_cluster_group_desc nvarchar(100) NULL,
	analytics_season_desc nvarchar(100) NULL,
	detailed_analytics_desc nvarchar(255) NULL,
	season_code char(2) NULL,
	fiscal_year smallint,
	CONSTRAINT pk1_sg PRIMARY KEY (strategy_id)
);
ALTER TABLE dbo.strat_group
    ADD CONSTRAINT fk1_sg FOREIGN KEY (strategy_group_type_code) REFERENCES dbo.strat_group_type (strategy_group_type_code);

CREATE TABLE IF NOT EXISTS dbo.plan_strategy(
    plan_id     int     NOT NULL,
    strategy_id int     NOT NULL,
    CONSTRAINT pk1_ps PRIMARY KEY (plan_id, strategy_id)
);

CREATE TABLE IF NOT EXISTS dbo.plan_strat_clus(
	plan_id bigint NOT NULL,
	strategy_id bigint NOT NULL,
	analytics_cluster_id int NOT NULL,
	analytics_cluster_label varchar(100) NULL,
	detailed_analytics_cluster_desc varchar(255) NULL,
	CONSTRAINT pk1_strclus PRIMARY KEY (plan_id, strategy_id, analytics_cluster_id)
	);
ALTER TABLE dbo.plan_strat_clus
    ADD CONSTRAINT fk1_stgclus FOREIGN KEY (plan_id, strategy_id) REFERENCES dbo.plan_strategy (plan_id, strategy_id);

CREATE TABLE IF NOT EXISTS dbo.strat_merchcatg(
    plan_id             int          NOT NULL,
    strategy_id         int          NOT NULL,
    rpt_lvl_0_nbr       int          NOT NULL,
    rpt_lvl_1_nbr       int          NOT NULL,
    rpt_lvl_2_nbr       int          NOT NULL,
    rpt_lvl_3_nbr       int          NOT NULL,
    channel_id          int          NULL,
    CONSTRAINT pk1_stgmcatg1 PRIMARY KEY (plan_id, strategy_id, rpt_lvl_0_nbr, rpt_lvl_1_nbr, rpt_lvl_2_nbr, rpt_lvl_3_nbr)
);
ALTER TABLE dbo.strat_merchcatg
    ADD CONSTRAINT fk1_stgfmcatg FOREIGN KEY (plan_id, strategy_id) REFERENCES dbo.plan_strategy (plan_id, strategy_id);

CREATE TABLE IF NOT EXISTS dbo.strat_merchcatg_fixture(

    plan_id             int          NOT NULL,
    strategy_id         int          NOT NULL,
    rpt_lvl_0_nbr       int          NOT NULL,
    rpt_lvl_1_nbr       int          NOT NULL,
    rpt_lvl_2_nbr       int          NOT NULL,
    rpt_lvl_3_nbr       int          NOT NULL,
    fixturetype_rollup_id   smallint    NOT NULL,
    min_nbr_per_type    decimal(15, 4)  NULL,
    max_nbr_per_type    decimal(15, 4)  NULL,
    min_present_unit_qty    int      NULL,
    max_present_unit_qty    int      NULL,
    max_cc_per_type     int          NULL,
    merch_method_code   int          NULL,
    CONSTRAINT pk1_stgmcatgfxtr1 PRIMARY KEY (plan_id, strategy_id, rpt_lvl_0_nbr, rpt_lvl_1_nbr, rpt_lvl_2_nbr, rpt_lvl_3_nbr, fixturetype_rollup_id)
);
ALTER TABLE dbo.strat_merchcatg_fixture
    ADD CONSTRAINT fk1_stgfmcatgfxtr FOREIGN KEY (plan_id, strategy_id, rpt_lvl_0_nbr, rpt_lvl_1_nbr, rpt_lvl_2_nbr, rpt_lvl_3_nbr) REFERENCES dbo.strat_merchcatg (plan_id, strategy_id, rpt_lvl_0_nbr, rpt_lvl_1_nbr, rpt_lvl_2_nbr, rpt_lvl_3_nbr);

ALTER TABLE dbo.strat_merchcatg_fixture
    ADD CONSTRAINT fk1_stgfmcatgfxttype FOREIGN KEY (fixturetype_rollup_id) REFERENCES dbo.fixturetype_rollup (fixturetype_rollup_id);

CREATE TABLE IF NOT EXISTS dbo.strat_merchcatg_lineplan(
      plan_id             int          NOT NULL,
      strategy_id         int          NOT NULL,
      rpt_lvl_0_nbr       int          NOT NULL,
      rpt_lvl_1_nbr       int          NOT NULL,
      rpt_lvl_2_nbr       int          NOT NULL,
      rpt_lvl_3_nbr       int          NOT NULL,
      channel_id          int          NOT NULL,
      fl_count            int          NULL,
      cc_count            int          NULL,
      attribute_obj            varchar(50000)          NULL,
    CONSTRAINT pk1_stgmcatglp1 PRIMARY KEY (plan_id, strategy_id, rpt_lvl_0_nbr, rpt_lvl_1_nbr, rpt_lvl_2_nbr, rpt_lvl_3_nbr, channel_id)
);

CREATE TABLE IF NOT EXISTS dbo.strat_subcatg(
    plan_id             int          NOT NULL,
    strategy_id         int          NOT NULL,
    rpt_lvl_0_nbr       int          NOT NULL,
    rpt_lvl_1_nbr       int          NOT NULL,
    rpt_lvl_2_nbr       int          NOT NULL,
    rpt_lvl_3_nbr       int          NOT NULL,
    rpt_lvl_4_nbr       int          NOT NULL,
    channel_id          int NULL,
    CONSTRAINT pk1_stgsubcatg PRIMARY KEY (plan_id, strategy_id, rpt_lvl_0_nbr, rpt_lvl_1_nbr, rpt_lvl_2_nbr,
     rpt_lvl_3_nbr, rpt_lvl_4_nbr)
);
ALTER TABLE dbo.strat_subcatg
    ADD CONSTRAINT fk1_stgsubcat FOREIGN KEY (plan_id, strategy_id, rpt_lvl_0_nbr, rpt_lvl_1_nbr, rpt_lvl_2_nbr,
     rpt_lvl_3_nbr) REFERENCES dbo.strat_merchcatg (plan_id, strategy_id,
                                        rpt_lvl_0_nbr, rpt_lvl_1_nbr, rpt_lvl_2_nbr, rpt_lvl_3_nbr);

CREATE TABLE IF NOT EXISTS dbo.strat_subcatg_fixture(

    plan_id             int          NOT NULL,
    strategy_id         int          NOT NULL,
    rpt_lvl_0_nbr       int          NOT NULL,
    rpt_lvl_1_nbr       int          NOT NULL,
    rpt_lvl_2_nbr       int          NOT NULL,
    rpt_lvl_3_nbr       int          NOT NULL,
    rpt_lvl_4_nbr       int          NOT NULL,
    fixturetype_rollup_id   smallint    NOT NULL,
    min_nbr_per_type    decimal(15, 4)  NULL,
    max_nbr_per_type    decimal(15, 4)  NULL,
    min_present_unit_qty    int      NULL,
    max_present_unit_qty    int      NULL,
    max_cc_per_type     int          NULL,
    merchandising_method_code int    NULL,
    merch_method_code   int          NULL,
    CONSTRAINT pk1_stgscatgfxtr1 PRIMARY KEY (plan_id, strategy_id, rpt_lvl_0_nbr, rpt_lvl_1_nbr, rpt_lvl_2_nbr, rpt_lvl_3_nbr, rpt_lvl_4_nbr, fixturetype_rollup_id)
);
ALTER TABLE dbo.strat_subcatg_fixture
    ADD CONSTRAINT fk1_stgfscatgfxtr FOREIGN KEY (plan_id, strategy_id, rpt_lvl_0_nbr, rpt_lvl_1_nbr, rpt_lvl_2_nbr, rpt_lvl_3_nbr, fixturetype_rollup_id) REFERENCES dbo.strat_merchcatg_fixture (plan_id, strategy_id, rpt_lvl_0_nbr, rpt_lvl_1_nbr, rpt_lvl_2_nbr, rpt_lvl_3_nbr, fixturetype_rollup_id);

CREATE TABLE IF NOT EXISTS dbo.strat_subcatg_lineplan(
    plan_id             int          NOT NULL,
    strategy_id         int          NOT NULL,
    rpt_lvl_0_nbr       int          NOT NULL,
    rpt_lvl_1_nbr       int          NOT NULL,
    rpt_lvl_2_nbr       int          NOT NULL,
    rpt_lvl_3_nbr       int          NOT NULL,
    rpt_lvl_4_nbr       int          NOT NULL,
    channel_id          int          NOT NULL,
    fl_count            int          NULL,
    cc_count            int          NULL,
    attribute_obj            varchar(50000)          NULL,
    CONSTRAINT pk1_stgsubcatglp PRIMARY KEY (plan_id, strategy_id, rpt_lvl_0_nbr, rpt_lvl_1_nbr, rpt_lvl_2_nbr,
    rpt_lvl_3_nbr, rpt_lvl_4_nbr, channel_id)
);

CREATE TABLE dbo.rfa_status_text(
	rfa_status_code int NOT NULL,
	rfa_status_desc varchar(80) NOT NULL,
	CONSTRAINT pl1_mktsel1 PRIMARY KEY (rfa_status_code)
);

INSERT INTO dbo.rfa_status_text
(rfa_status_code, rfa_status_desc)
VALUES
(1, 'RFA InProgress'),
(2, 'RFA Has Not Run'),
(3, 'RFA Allocated'),
(4, 'RFA Failed'),
(5, 'RFA Not Allocated'),
(6, 'Program Level Lock'),
(11, 'Default(editable)');

CREATE TABLE dbo.run_status_text(
	run_status_code int NOT NULL,
	run_status_desc varchar(80) NOT NULL,
	CONSTRAINT pl1_mktsel2 PRIMARY KEY (run_status_code)
);

INSERT INTO dbo.run_status_text
(run_status_code, run_status_desc)
VALUES
(0, 'Unselected (Run at fineline)'),
(1, 'Selected (Run at cc)'),
(2, 'Hide CC Selection');

CREATE TABLE dbo.alloc_run_type_text(
	alloc_run_type_code int NOT NULL,
	alloc_run_type_desc varchar(80) NOT NULL,
	CONSTRAINT pl1_mktsel3 PRIMARY KEY (alloc_run_type_code)
);

INSERT INTO dbo.alloc_run_type_text
(alloc_run_type_code, alloc_run_type_desc)
VALUES
(1, 'Locked'),
(2, 'Unlocked');

CREATE TABLE IF NOT EXISTS dbo.strat_fl(
    plan_id             int          NOT NULL,
    strategy_id         int          NOT NULL,
    rpt_lvl_0_nbr       int          NOT NULL,
    rpt_lvl_1_nbr       int          NOT NULL,
    rpt_lvl_2_nbr       int          NOT NULL,
    rpt_lvl_3_nbr       int          NOT NULL,
    rpt_lvl_4_nbr       int          NOT NULL,
    fineline_nbr        int          NOT NULL,
    rpt_lvl_0_gen_desc1 varchar(100) NULL,
    rpt_lvl_1_gen_desc1 varchar(100) NULL,
    rpt_lvl_2_gen_desc1 varchar(100) NULL,
    rpt_lvl_3_gen_desc1 varchar(100) NULL,
    rpt_lvl_4_gen_desc1 varchar(100) NULL,
    channel_id          int NULL,
    fineline_desc       varchar(40)  NULL,
    trait_choice_code   int          NULL,
    alt_fineline_desc   varchar(40)  NULL,
    outfitting_fl_list  varchar(40)  NULL,
    run_status_code     int NULL,
    rfa_status_code     int NULL,
    alloc_run_type_code int NULL,
    brand_obj           nvarchar(max) NULL,
    ahs_v_id            int NULL,
    CONSTRAINT pk1_stgfl PRIMARY KEY (plan_id, strategy_id, rpt_lvl_0_nbr, rpt_lvl_1_nbr, rpt_lvl_2_nbr, rpt_lvl_3_nbr,
                                                                                           rpt_lvl_4_nbr, fineline_nbr)
);
ALTER TABLE dbo.strat_fl
    ADD CONSTRAINT fk1_stgfl FOREIGN KEY (plan_id, strategy_id, rpt_lvl_0_nbr, rpt_lvl_1_nbr, rpt_lvl_2_nbr, rpt_lvl_3_nbr,
                                        rpt_lvl_4_nbr) REFERENCES dbo.strat_subcatg (plan_id, strategy_id,
                                        rpt_lvl_0_nbr, rpt_lvl_1_nbr, rpt_lvl_2_nbr, rpt_lvl_3_nbr, rpt_lvl_4_nbr);

CREATE TABLE IF NOT EXISTS dbo.strat_fl_fixture(

    plan_id             int          NOT NULL,
    strategy_id         int          NOT NULL,
    rpt_lvl_0_nbr       int          NOT NULL,
    rpt_lvl_1_nbr       int          NOT NULL,
    rpt_lvl_2_nbr       int          NOT NULL,
    rpt_lvl_3_nbr       int          NOT NULL,
    rpt_lvl_4_nbr       int          NOT NULL,
    fineline_nbr        int          NOT NULL,
    fixturetype_rollup_id   smallint    NOT NULL,
    adj_below_min_pct   decimal(7, 4)  NULL,
    adj_above_max_pct   decimal(7, 4)  NULL,
    min_rollup_type_pct   decimal(7, 4)  NULL,
    max_rollup_type_pct   decimal(7, 4)  NULL,
    min_type_per_cc_pct   decimal(7, 4)  NULL,
    max_type_per_cc_pct   decimal(7, 4)  NULL,
    fixture_group_min_cnt int  NULL,
    fixture_group_max_cnt int  NULL,
    min_present_unit_qty    int      NULL,
    max_present_unit_qty    int      NULL,
    max_cc_per_type     int          NULL,
    adj_max_cc_per_type int    NULL,
    merch_method_code   int          NULL,
    CONSTRAINT pk1_stgflfxtr1 PRIMARY KEY (plan_id, strategy_id, rpt_lvl_0_nbr, rpt_lvl_1_nbr, rpt_lvl_2_nbr, rpt_lvl_3_nbr, rpt_lvl_4_nbr, fineline_nbr, fixturetype_rollup_id)
);
ALTER TABLE dbo.strat_fl_fixture
    ADD CONSTRAINT fk1_stgfflfxtr FOREIGN KEY (plan_id, strategy_id, rpt_lvl_0_nbr, rpt_lvl_1_nbr, rpt_lvl_2_nbr, rpt_lvl_3_nbr, rpt_lvl_4_nbr, fixturetype_rollup_id) REFERENCES dbo.strat_subcatg_fixture (plan_id, strategy_id, rpt_lvl_0_nbr, rpt_lvl_1_nbr, rpt_lvl_2_nbr, rpt_lvl_3_nbr, rpt_lvl_4_nbr, fixturetype_rollup_id);

CREATE TABLE IF NOT EXISTS dbo.strat_fl_fixture_rank(

    plan_id             int          NOT NULL,
    strategy_id         int          NOT NULL,
    rpt_lvl_0_nbr       int          NOT NULL,
    rpt_lvl_1_nbr       int          NOT NULL,
    rpt_lvl_2_nbr       int          NOT NULL,
    rpt_lvl_3_nbr       int          NOT NULL,
    rpt_lvl_4_nbr       int          NOT NULL,
    fineline_nbr        int          NOT NULL,
    fixturetype_rollup_id   smallint    NOT NULL,
    type_fl_rank        int          NOT NULL,
    CONSTRAINT pk1_stgflfxtrrnk1 PRIMARY KEY (plan_id, strategy_id, rpt_lvl_0_nbr, rpt_lvl_1_nbr, rpt_lvl_2_nbr, rpt_lvl_3_nbr, rpt_lvl_4_nbr, fineline_nbr, fixturetype_rollup_id, type_fl_rank)
);
ALTER TABLE dbo.strat_fl_fixture_rank
    ADD CONSTRAINT fk1_stgfflfxtrrnk FOREIGN KEY (plan_id, strategy_id, rpt_lvl_0_nbr, rpt_lvl_1_nbr, rpt_lvl_2_nbr, rpt_lvl_3_nbr, rpt_lvl_4_nbr, fineline_nbr, fixturetype_rollup_id) REFERENCES dbo.strat_fl_fixture (plan_id, strategy_id, rpt_lvl_0_nbr, rpt_lvl_1_nbr, rpt_lvl_2_nbr, rpt_lvl_3_nbr, rpt_lvl_4_nbr, fineline_nbr, fixturetype_rollup_id);

CREATE TABLE IF NOT EXISTS dbo.strat_style_fixture(
	plan_id bigint NOT NULL,
	strategy_id bigint NOT NULL,
	rpt_lvl_0_nbr int NOT NULL,
	rpt_lvl_1_nbr int NOT NULL,
	rpt_lvl_2_nbr int NOT NULL,
	rpt_lvl_3_nbr int NOT NULL,
	rpt_lvl_4_nbr int NOT NULL,
	fineline_nbr smallint NOT NULL,
	style_nbr char(50) NOT NULL,
	fixturetype_rollup_id smallint NOT NULL,
	adj_below_min_pct decimal(7, 4) NULL,
	adj_above_max_pct decimal(7, 4) NULL,
	min_rollup_type_pct decimal(7, 4) NULL,
	max_rollup_type_pct decimal(7, 4) NULL,
	CONSTRAINT pk1_stgstylefxtr1 PRIMARY KEY (plan_id, strategy_id, rpt_lvl_0_nbr, rpt_lvl_1_nbr, rpt_lvl_2_nbr, rpt_lvl_3_nbr, rpt_lvl_4_nbr, fineline_nbr, style_nbr, fixturetype_rollup_id)
);
ALTER TABLE dbo.strat_style_fixture
    ADD CONSTRAINT fk1_stgstylefxtr1 FOREIGN KEY (plan_id, strategy_id, rpt_lvl_0_nbr, rpt_lvl_1_nbr, rpt_lvl_2_nbr, rpt_lvl_3_nbr, rpt_lvl_4_nbr, fineline_nbr, fixturetype_rollup_id) REFERENCES dbo.strat_fl_fixture (plan_id, strategy_id, rpt_lvl_0_nbr, rpt_lvl_1_nbr, rpt_lvl_2_nbr, rpt_lvl_3_nbr, rpt_lvl_4_nbr, fineline_nbr, fixturetype_rollup_id);


CREATE TABLE IF NOT EXISTS dbo.strat_style(
    plan_id             int          NOT NULL,
    strategy_id         int          NOT NULL,
    rpt_lvl_0_nbr       int          NOT NULL,
    rpt_lvl_1_nbr       int          NOT NULL,
    rpt_lvl_2_nbr       int          NOT NULL,
    rpt_lvl_3_nbr       int          NOT NULL,
    rpt_lvl_4_nbr       int          NOT NULL,
    fineline_nbr        int          NOT NULL,
    style_nbr           varchar(50)  NOT NULL,
    channel_id          int NULL,
    alt_style_desc      varchar(50)  NULL,
    CONSTRAINT pk1_stgsty PRIMARY KEY (plan_id, strategy_id, rpt_lvl_0_nbr, rpt_lvl_1_nbr, rpt_lvl_2_nbr, rpt_lvl_3_nbr,
                                                                                           rpt_lvl_4_nbr, fineline_nbr
                                                                                           ,style_nbr)
);

ALTER TABLE dbo.strat_style
    ADD CONSTRAINT fk1_stgsty FOREIGN KEY (plan_id, strategy_id, rpt_lvl_0_nbr, rpt_lvl_1_nbr, rpt_lvl_2_nbr, rpt_lvl_3_nbr,
                                        rpt_lvl_4_nbr, fineline_nbr) REFERENCES dbo.strat_fl (plan_id, strategy_id,
                                         rpt_lvl_0_nbr, rpt_lvl_1_nbr, rpt_lvl_2_nbr, rpt_lvl_3_nbr,
                                                                                  rpt_lvl_4_nbr, fineline_nbr);

CREATE TABLE IF NOT EXISTS dbo.strat_cc(
    plan_id             int          NOT NULL,
    strategy_id         int          NOT NULL,
    rpt_lvl_0_nbr       int          NOT NULL,
    rpt_lvl_1_nbr       int          NOT NULL,
    rpt_lvl_2_nbr       int          NOT NULL,
    rpt_lvl_3_nbr       int          NOT NULL,
    rpt_lvl_4_nbr       int          NOT NULL,
    fineline_nbr        int          NOT NULL,
    style_nbr           varchar(50)  NOT NULL,
    customer_choice     varchar(50)  NOT NULL,
    color_name          varchar(50)  NOT NULL,
    channel_id          int          NULL,
    alt_cc_desc         varchar(50)  NULL,
    color_family_desc   varchar(50)  NULL,
    CONSTRAINT pk1_stgcc PRIMARY KEY (plan_id, strategy_id, rpt_lvl_0_nbr, rpt_lvl_1_nbr, rpt_lvl_2_nbr, rpt_lvl_3_nbr,
                                                                                           rpt_lvl_4_nbr, fineline_nbr
                                                                                           ,style_nbr, customer_choice)
);
ALTER TABLE dbo.strat_cc
    ADD CONSTRAINT fk1_stgcc FOREIGN KEY (plan_id, strategy_id, rpt_lvl_0_nbr, rpt_lvl_1_nbr, rpt_lvl_2_nbr, rpt_lvl_3_nbr,
                                        rpt_lvl_4_nbr, fineline_nbr, style_nbr) REFERENCES dbo.strat_style (plan_id,
                                        strategy_id, rpt_lvl_0_nbr, rpt_lvl_1_nbr, rpt_lvl_2_nbr, rpt_lvl_3_nbr,
                                        rpt_lvl_4_nbr, fineline_nbr, style_nbr);

CREATE TABLE IF NOT EXISTS dbo.strat_cc_fixture(

    plan_id             int          NOT NULL,
    strategy_id         int          NOT NULL,
    rpt_lvl_0_nbr       int          NOT NULL,
    rpt_lvl_1_nbr       int          NOT NULL,
    rpt_lvl_2_nbr       int          NOT NULL,
    rpt_lvl_3_nbr       int          NOT NULL,
    rpt_lvl_4_nbr       int          NOT NULL,
    fineline_nbr        int          NOT NULL,
    style_nbr           varchar(50)  NOT NULL,
    customer_choice     varchar(50)  NOT NULL,
    fixturetype_rollup_id   smallint    NOT NULL,
    adj_max_cc_type_pct decimal(7, 4)  NULL,
    CONSTRAINT pk1_stgccfxtr1 PRIMARY KEY (plan_id, strategy_id, rpt_lvl_0_nbr, rpt_lvl_1_nbr, rpt_lvl_2_nbr, rpt_lvl_3_nbr, rpt_lvl_4_nbr, fineline_nbr, style_nbr, customer_choice, fixturetype_rollup_id)
);
ALTER TABLE dbo.strat_cc_fixture
    ADD CONSTRAINT fk1_stgfccfxtr FOREIGN KEY (plan_id, strategy_id, rpt_lvl_0_nbr, rpt_lvl_1_nbr, rpt_lvl_2_nbr, rpt_lvl_3_nbr, rpt_lvl_4_nbr, fineline_nbr, style_nbr, fixturetype_rollup_id) REFERENCES dbo.strat_style_fixture (plan_id, strategy_id, rpt_lvl_0_nbr, rpt_lvl_1_nbr, rpt_lvl_2_nbr, rpt_lvl_3_nbr, rpt_lvl_4_nbr, fineline_nbr, style_nbr, fixturetype_rollup_id);

CREATE TABLE IF NOT EXISTS dbo.elig_fl_clus_rank(
	plan_id bigint NOT NULL,
	strategy_id bigint NOT NULL,
	analytics_cluster_id int NOT NULL,
	rpt_lvl_0_nbr int NOT NULL,
	rpt_lvl_1_nbr int NOT NULL,
	rpt_lvl_2_nbr int NOT NULL,
	rpt_lvl_3_nbr int NOT NULL,
	rpt_lvl_4_nbr int NOT NULL,
	fineline_nbr smallint NOT NULL,
	fineline_desc nvarchar(40) NULL,
	cluster_nbr_value int NULL,
	cluster_char_value char(150) NULL,
	merchant_override_rank int NULL,
	in_store_yr_wk smallint NULL,
	markdown_yr_wk smallint NULL,
	is_eligible bit NULL,
	select_status_id smallint NULL,
	in_store_yrwk_desc char(15) NULL,
	markdown_yrwk_desc char(15) NULL,
	store_cnt INT NULL,
	CONSTRAINT pl1_eligflclusrank PRIMARY KEY (plan_id, strategy_id, analytics_cluster_id, rpt_lvl_0_nbr, rpt_lvl_1_nbr,
	 rpt_lvl_2_nbr, rpt_lvl_3_nbr, rpt_lvl_4_nbr, fineline_nbr)
);
ALTER TABLE dbo.elig_fl_clus_rank
    ADD CONSTRAINT fk1_eligflclusrank FOREIGN KEY (plan_id, strategy_id, analytics_cluster_id) REFERENCES dbo.plan_strat_clus
     (plan_id, strategy_id,analytics_cluster_id );

CREATE TABLE IF NOT EXISTS dbo.elig_fl_clus_metrics(
	plan_id bigint NOT NULL,
	strategy_id bigint NOT NULL,
	analytics_cluster_id int NOT NULL,
	rpt_lvl_0_nbr int NOT NULL,
	rpt_lvl_1_nbr int NOT NULL,
	rpt_lvl_2_nbr int NOT NULL,
	rpt_lvl_3_nbr int NOT NULL,
	rpt_lvl_4_nbr int NOT NULL,
	fineline_nbr smallint NOT NULL,
	sales_dollars decimal(15, 4) NULL,
	sales_units bigint NULL,
	forecasted_units bigint NULL,
	forecasted_dollars decimal(15, 4) NULL,
	on_hand_qty bigint NULL,
	sales_to_stock_ratio decimal(7, 4) NULL,
	analytics_cluster_rank int NULL,
	CONSTRAINT pl1_eligflclusmetrics PRIMARY KEY (plan_id, strategy_id, analytics_cluster_id, rpt_lvl_0_nbr, rpt_lvl_1_nbr,
    	 rpt_lvl_2_nbr, rpt_lvl_3_nbr, rpt_lvl_4_nbr, fineline_nbr)
);
ALTER TABLE dbo.elig_fl_clus_metrics
    ADD CONSTRAINT fk1_eligflclusmetrics FOREIGN KEY (plan_id, strategy_id, analytics_cluster_id, rpt_lvl_0_nbr,
    rpt_lvl_1_nbr, rpt_lvl_2_nbr, rpt_lvl_3_nbr, rpt_lvl_4_nbr, fineline_nbr) REFERENCES dbo.elig_fl_clus_rank
     (plan_id, strategy_id, analytics_cluster_id, rpt_lvl_0_nbr, rpt_lvl_1_nbr, rpt_lvl_2_nbr, rpt_lvl_3_nbr,
      rpt_lvl_4_nbr, fineline_nbr);


CREATE TABLE IF NOT EXISTS dbo.elig_fl_mkt_clus(
	plan_id bigint NOT NULL,
	strategy_id bigint NOT NULL,
	analytics_cluster_id int NOT NULL,
	rpt_lvl_0_nbr int NOT NULL,
	rpt_lvl_1_nbr int NOT NULL,
	rpt_lvl_2_nbr int NOT NULL,
	rpt_lvl_3_nbr int NOT NULL,
	rpt_lvl_4_nbr int NOT NULL,
	fineline_nbr smallint NOT NULL,
	market_select_code smallint NOT NULL,
	CONSTRAINT pl1_eligflmktclus PRIMARY KEY (plan_id, strategy_id, analytics_cluster_id, rpt_lvl_0_nbr, rpt_lvl_1_nbr,
        	 rpt_lvl_2_nbr, rpt_lvl_3_nbr, rpt_lvl_4_nbr, fineline_nbr, market_select_code)
);
ALTER TABLE dbo.elig_fl_mkt_clus
    ADD CONSTRAINT fk1_eligflmktclus FOREIGN KEY (plan_id, strategy_id, analytics_cluster_id, rpt_lvl_0_nbr,
    rpt_lvl_1_nbr, rpt_lvl_2_nbr, rpt_lvl_3_nbr, rpt_lvl_4_nbr, fineline_nbr) REFERENCES dbo.elig_fl_clus_rank
     (plan_id, strategy_id, analytics_cluster_id, rpt_lvl_0_nbr, rpt_lvl_1_nbr, rpt_lvl_2_nbr, rpt_lvl_3_nbr,
      rpt_lvl_4_nbr, fineline_nbr);

CREATE TABLE IF NOT EXISTS dbo.market_selections(
      	market_select_code smallint NOT NULL,
      	market_value nvarchar(255) NULL,
      	market_desc nvarchar(255) NULL,
      	CONSTRAINT pl1_mktsel PRIMARY KEY (market_select_code)
      );

 CREATE TABLE IF NOT EXISTS dbo.clus_style(
 	plan_id bigint NOT NULL,
 	strategy_id bigint NOT NULL,
 	analytics_cluster_id int NOT NULL,
 	rpt_lvl_0_nbr int NOT NULL,
 	rpt_lvl_1_nbr int NOT NULL,
 	rpt_lvl_2_nbr int NOT NULL,
 	rpt_lvl_3_nbr int NOT NULL,
 	rpt_lvl_4_nbr int NOT NULL,
 	fineline_nbr smallint NOT NULL,
 	style_nbr char(50) NOT NULL,
 	CONSTRAINT pl1_clssty PRIMARY KEY (plan_id, strategy_id, analytics_cluster_id, rpt_lvl_0_nbr, rpt_lvl_1_nbr,
                                               	 rpt_lvl_2_nbr, rpt_lvl_3_nbr, rpt_lvl_4_nbr, fineline_nbr,style_nbr)
 );

 ALTER TABLE dbo.clus_style
     ADD CONSTRAINT fk1_clssty FOREIGN KEY (plan_id, strategy_id, analytics_cluster_id, rpt_lvl_0_nbr,
     rpt_lvl_1_nbr, rpt_lvl_2_nbr, rpt_lvl_3_nbr, rpt_lvl_4_nbr, fineline_nbr) REFERENCES dbo.elig_fl_clus_rank
      (plan_id, strategy_id, analytics_cluster_id, rpt_lvl_0_nbr, rpt_lvl_1_nbr, rpt_lvl_2_nbr, rpt_lvl_3_nbr,
       rpt_lvl_4_nbr, fineline_nbr);

 CREATE TABLE dbo.elig_cc_clus_rank(
 	plan_id bigint NOT NULL,
 	strategy_id bigint NOT NULL,
 	analytics_cluster_id int NOT NULL,
 	rpt_lvl_0_nbr int NOT NULL,
 	rpt_lvl_1_nbr int NOT NULL,
 	rpt_lvl_2_nbr int NOT NULL,
 	rpt_lvl_3_nbr int NOT NULL,
 	rpt_lvl_4_nbr int NOT NULL,
 	fineline_nbr smallint NOT NULL,
 	style_nbr char(50) NOT NULL,
 	customer_choice char(50) NOT NULL,
 	in_store_yr_wk smallint NULL,
 	markdown_yr_wk smallint NULL,
 	merchant_override_rank int NULL,
 	ap_ranking int NULL,
 	is_eligible bit NULL,
 	select_status_id smallint NULL,
 	in_store_yrwk_desc char(15) NULL,
 	markdown_yrwk_desc char(15) NULL,
 	CONSTRAINT pl1_eccclusrank PRIMARY KEY (plan_id, strategy_id, analytics_cluster_id, rpt_lvl_0_nbr, rpt_lvl_1_nbr,
                                                rpt_lvl_2_nbr, rpt_lvl_3_nbr, rpt_lvl_4_nbr, fineline_nbr,style_nbr, customer_choice)
 );
  ALTER TABLE dbo.elig_cc_clus_rank
      ADD CONSTRAINT fk1_eccclusrank FOREIGN KEY (plan_id, strategy_id, analytics_cluster_id, rpt_lvl_0_nbr,
      rpt_lvl_1_nbr, rpt_lvl_2_nbr, rpt_lvl_3_nbr, rpt_lvl_4_nbr, fineline_nbr, style_nbr) REFERENCES dbo.clus_style
       (plan_id, strategy_id, analytics_cluster_id, rpt_lvl_0_nbr, rpt_lvl_1_nbr, rpt_lvl_2_nbr, rpt_lvl_3_nbr,
        rpt_lvl_4_nbr, fineline_nbr, style_nbr);

CREATE TABLE IF NOT EXISTS dbo.elig_cc_mkt_clus(
	plan_id bigint NOT NULL,
	strategy_id bigint NOT NULL,
	analytics_cluster_id int NOT NULL,
	rpt_lvl_0_nbr int NOT NULL,
	rpt_lvl_1_nbr int NOT NULL,
	rpt_lvl_2_nbr int NOT NULL,
	rpt_lvl_3_nbr int NOT NULL,
	rpt_lvl_4_nbr int NOT NULL,
	fineline_nbr smallint NOT NULL,
	style_nbr char(50) NOT NULL,
	customer_choice char(50) NOT NULL,
	market_select_code smallint NOT NULL,
	CONSTRAINT pl1_eligccmktclus PRIMARY KEY (plan_id, strategy_id, analytics_cluster_id, rpt_lvl_0_nbr, rpt_lvl_1_nbr,
        	 rpt_lvl_2_nbr, rpt_lvl_3_nbr, rpt_lvl_4_nbr, fineline_nbr, style_nbr, customer_choice, market_select_code)
);
ALTER TABLE dbo.elig_cc_mkt_clus
    ADD CONSTRAINT fk1_eligccmktclus FOREIGN KEY (plan_id, strategy_id, analytics_cluster_id, rpt_lvl_0_nbr,
    rpt_lvl_1_nbr, rpt_lvl_2_nbr, rpt_lvl_3_nbr, rpt_lvl_4_nbr, fineline_nbr, style_nbr, customer_choice) REFERENCES dbo.elig_cc_clus_rank
     (plan_id, strategy_id, analytics_cluster_id, rpt_lvl_0_nbr, rpt_lvl_1_nbr, rpt_lvl_2_nbr, rpt_lvl_3_nbr,
      rpt_lvl_4_nbr, fineline_nbr, style_nbr, customer_choice);

 CREATE TABLE IF NOT EXISTS dbo.elig_fl_clus_prog(
 	plan_id bigint NOT NULL,
 	strategy_id bigint NOT NULL,
 	analytics_cluster_id int NOT NULL,
 	rpt_lvl_0_nbr int NOT NULL,
 	rpt_lvl_1_nbr int NOT NULL,
 	rpt_lvl_2_nbr int NOT NULL,
 	rpt_lvl_3_nbr int NOT NULL,
 	rpt_lvl_4_nbr int NOT NULL,
 	fineline_nbr smallint NOT NULL,
 	program_id bigint NOT NULL,
 	in_store_yr_wk smallint NULL,
 	markdown_yr_wk smallint NULL,
 	merchant_override_rank int NULL,
 	is_eligible bit NULL,
 	select_status_id smallint NULL,
 	in_store_yrwk_desc char(15) NULL,
 	markdown_yrwk_desc char(15) NULL,
 	store_cnt INT NULL,
 	CONSTRAINT pl1_eligflprog PRIMARY KEY (plan_id, strategy_id, analytics_cluster_id, rpt_lvl_0_nbr, rpt_lvl_1_nbr,
                                             rpt_lvl_2_nbr, rpt_lvl_3_nbr, rpt_lvl_4_nbr, fineline_nbr, program_id)
 );
   ALTER TABLE dbo.elig_fl_clus_prog
       ADD CONSTRAINT fk1_eligflprog FOREIGN KEY (plan_id, strategy_id, analytics_cluster_id, rpt_lvl_0_nbr,
       rpt_lvl_1_nbr, rpt_lvl_2_nbr, rpt_lvl_3_nbr, rpt_lvl_4_nbr, fineline_nbr) REFERENCES dbo.elig_fl_clus_rank
        (plan_id, strategy_id, analytics_cluster_id, rpt_lvl_0_nbr, rpt_lvl_1_nbr, rpt_lvl_2_nbr, rpt_lvl_3_nbr,
         rpt_lvl_4_nbr, fineline_nbr);

CREATE TABLE IF NOT EXISTS dbo.elig_style_clus_prog(
	plan_id bigint NOT NULL,
	strategy_id bigint NOT NULL,
	analytics_cluster_id int NOT NULL,
	rpt_lvl_0_nbr int NOT NULL,
	rpt_lvl_1_nbr int NOT NULL,
	rpt_lvl_2_nbr int NOT NULL,
	rpt_lvl_3_nbr int NOT NULL,
	rpt_lvl_4_nbr int NOT NULL,
	fineline_nbr smallint NOT NULL,
	style_nbr char(50) NOT NULL,
	program_id bigint NOT NULL,
	CONSTRAINT pl1_eligstyleprog PRIMARY KEY (plan_id, strategy_id, analytics_cluster_id, rpt_lvl_0_nbr, rpt_lvl_1_nbr,
                                                 rpt_lvl_2_nbr, rpt_lvl_3_nbr, rpt_lvl_4_nbr, fineline_nbr, style_nbr, program_id)
);
 ALTER TABLE dbo.elig_style_clus_prog
       ADD CONSTRAINT fk1_eligstyleprog FOREIGN KEY (plan_id, strategy_id, analytics_cluster_id, rpt_lvl_0_nbr,
       rpt_lvl_1_nbr, rpt_lvl_2_nbr, rpt_lvl_3_nbr, rpt_lvl_4_nbr, fineline_nbr, program_id) REFERENCES dbo.elig_fl_clus_prog
        (plan_id, strategy_id, analytics_cluster_id, rpt_lvl_0_nbr, rpt_lvl_1_nbr, rpt_lvl_2_nbr, rpt_lvl_3_nbr,
         rpt_lvl_4_nbr, fineline_nbr, program_id);


CREATE TABLE IF NOT EXISTS  dbo.elig_cc_clus_prog(
	plan_id bigint NOT NULL,
	strategy_id bigint NOT NULL,
	analytics_cluster_id int NOT NULL,
	rpt_lvl_0_nbr int NOT NULL,
	rpt_lvl_1_nbr int NOT NULL,
	rpt_lvl_2_nbr int NOT NULL,
	rpt_lvl_3_nbr int NOT NULL,
	rpt_lvl_4_nbr int NOT NULL,
	fineline_nbr smallint NOT NULL,
	style_nbr char(50) NOT NULL,
	customer_choice char(100) NOT NULL,
	program_id bigint NOT NULL,
	in_store_yr_wk smallint NULL,
	markdown_yr_wk smallint NULL,
	merchant_override_rank int NULL,
	is_eligible bit NULL,
	select_status_id smallint NULL,
	in_store_yrwk_desc char(15) NULL,
	markdown_yrwk_desc char(15) NULL,
	CONSTRAINT pl1_eligccprog PRIMARY KEY (plan_id, strategy_id, analytics_cluster_id, rpt_lvl_0_nbr, rpt_lvl_1_nbr,
                                                     rpt_lvl_2_nbr, rpt_lvl_3_nbr, rpt_lvl_4_nbr, fineline_nbr, style_nbr, customer_choice, program_id)
);
ALTER TABLE dbo.elig_cc_clus_prog
       ADD CONSTRAINT fk1_eligccprog FOREIGN KEY (plan_id, strategy_id, analytics_cluster_id, rpt_lvl_0_nbr,
       rpt_lvl_1_nbr, rpt_lvl_2_nbr, rpt_lvl_3_nbr, rpt_lvl_4_nbr, fineline_nbr, style_nbr, program_id) REFERENCES dbo.elig_style_clus_prog
        (plan_id, strategy_id, analytics_cluster_id, rpt_lvl_0_nbr, rpt_lvl_1_nbr, rpt_lvl_2_nbr, rpt_lvl_3_nbr,
         rpt_lvl_4_nbr, fineline_nbr, style_nbr, program_id);


 CREATE TABLE IF NOT EXISTS dbo.elig_fl_mkt_clus_prog(
	plan_id bigint NOT NULL,
	strategy_id bigint NOT NULL,
	analytics_cluster_id int NOT NULL,
	rpt_lvl_0_nbr int NOT NULL,
	rpt_lvl_1_nbr int NOT NULL,
	rpt_lvl_2_nbr int NOT NULL,
	rpt_lvl_3_nbr int NOT NULL,
	rpt_lvl_4_nbr int NOT NULL,
	fineline_nbr smallint NOT NULL,
	program_id bigint NOT NULL,
	market_select_code smallint NOT NULL,
	CONSTRAINT pl1_eligflmktclusprog PRIMARY KEY (plan_id, strategy_id, analytics_cluster_id, rpt_lvl_0_nbr, rpt_lvl_1_nbr,
        	 rpt_lvl_2_nbr, rpt_lvl_3_nbr, rpt_lvl_4_nbr, fineline_nbr, program_id, market_select_code)
);
    ALTER TABLE dbo.elig_fl_mkt_clus_prog
        ADD  CONSTRAINT fk1_eligflmktprog FOREIGN KEY(plan_id, strategy_id, analytics_cluster_id,
        rpt_lvl_0_nbr, rpt_lvl_1_nbr, rpt_lvl_2_nbr, rpt_lvl_3_nbr, rpt_lvl_4_nbr, fineline_nbr,
        program_id) REFERENCES dbo.elig_fl_clus_prog (plan_id, strategy_id, analytics_cluster_id,
        rpt_lvl_0_nbr, rpt_lvl_1_nbr, rpt_lvl_2_nbr, rpt_lvl_3_nbr, rpt_lvl_4_nbr,
        fineline_nbr, program_id);

CREATE TABLE IF NOT EXISTS dbo.subcatg_minmax(
	rpt_lvl_0_nbr int NOT NULL,
    rpt_lvl_1_nbr int NOT NULL,
    rpt_lvl_2_nbr int NOT NULL,
    rpt_lvl_3_nbr int NOT NULL,
    rpt_lvl_4_nbr int NOT NULL,
    fixturetype_rollup_id int NOT NULL,
    ahs_asi_id int NOT NULL,
    ahs_v_id int NOT NULL,
    ahs_asi_attr_name varchar(100) NULL,
    ash_v_value varchar(50) NULL,
    min_qty int NOT NULL,
    max_qty int NOT NULL,
	CONSTRAINT pl1_subcat_1 PRIMARY KEY (rpt_lvl_0_nbr, rpt_lvl_1_nbr, rpt_lvl_2_nbr, rpt_lvl_3_nbr, rpt_lvl_4_nbr, fixturetype_rollup_id, ahs_asi_id, ahs_v_id)
);

INSERT INTO dbo.subcatg_minmax
(rpt_lvl_0_nbr,rpt_lvl_1_nbr,rpt_lvl_2_nbr,rpt_lvl_3_nbr,rpt_lvl_4_nbr,fixturetype_rollup_id,ahs_asi_id,ahs_v_id,ahs_asi_attr_name,ash_v_value,min_qty,max_qty)
VALUES(50000, 23, 3669, 8244, 16893, 1, 2607, 2697,'Fabric Weight', 'Thin', 90, 100),
(50000, 23, 3669, 8244, 16893, 1, 2607, 2698,'Fabric Weight', 'Medium', 70, 80),
(50000, 23, 3669, 8244, 16893, 1, 2607, 2699,'Fabric Weight', 'Thick', 50, 60),
(50000, 23, 3669, 8244, 16893, 2, 2607, 2697,'Fabric Weight', 'Thin', 90, 100),
(50000, 23, 3669, 8244, 16893, 2, 2607, 2698,'Fabric Weight', 'Medium', 70, 80),
(50000, 23, 3669, 8244, 16893, 2, 2607, 2699,'Fabric Weight', 'Thick', 50, 60),
(50000, 23, 3669, 8244, 16893, 3, 2607, 2697,'Fabric Weight', 'Thin', 100, 110),
(50000, 23, 3669, 8244, 16893, 3, 2607, 2698,'Fabric Weight', 'Medium', 80, 90),
(50000, 23, 3669, 8244, 16893, 3, 2607, 2699,'Fabric Weight', 'Thick', 60, 70);


INSERT INTO dbo.market_selections (market_select_code, market_value)
VALUES (1, 'PR'),
(2, 'AK'),
(3, 'HI');

INSERT INTO dbo.strat_group_type
(strategy_group_type_code, strategy_group_desc)
VALUES
(1, 'Finelines ranked within weather clusters'),
(8, 'Presentation Units'),
(9, 'Line Planning'),
(10, 'Fixture Merchandise Method'),
(3, 'Size Only Profiles');

INSERT INTO dbo.strat_group
(strategy_id, strategy_group_type_code, analytics_cluster_group_desc, season_code, fiscal_year )
VALUES
(1, 1, 'Weather Cluster', 'S2', 2024),
(2, 9, 'Line Planning', null, null),
(3, 10, 'Fixture', null, null),
(4, 3, 'Size Only Profiles', 'S2', null),
(5, 8, 'Presentation Units', null, null),
(14, 1, 'Weather Cluster', 'S1', 2025);


 INSERT INTO dbo.plan_strategy (plan_id, strategy_id)
 VALUES
 (1, 1),
 (2, 1),
 (1, 3),
 (2, 3),
 (2, 5),
 (1, 4),
 (2, 4),
 (1, 5),
 (3, 3),
 (3, 14);

 INSERT INTO dbo.strat_merchcatg
 (plan_id, strategy_id, rpt_lvl_0_nbr, rpt_lvl_1_nbr, rpt_lvl_2_nbr, rpt_lvl_3_nbr, channel_id )
 VALUES
 (1, 1, 50000, 34, 1488, 9074, 1),
 (1, 3, 50000, 34, 1488, 9074, 1),
 (2, 1, 50000, 34, 1489, 9073, 1),
 (2, 3, 50000, 34, 1489, 9073, 1),
 (1, 5, 50000, 34, 1488, 9074, 1),
 (3, 3, 50000, 23, 3669, 7506, 1),
 (3, 3, 50000, 23, 3669, 8244, 1),
 (3, 14, 50000, 23, 3669, 7506, 1),
 (3, 14, 50000, 23, 3669, 8244, 1);


 INSERT INTO dbo.strat_subcatg
 (plan_id, strategy_id, rpt_lvl_0_nbr, rpt_lvl_1_nbr, rpt_lvl_2_nbr, rpt_lvl_3_nbr, rpt_lvl_4_nbr, channel_id )
 VALUES
 (1, 1, 50000, 34, 1488, 9074, 7204, 1),
 (1, 3, 50000, 34, 1488, 9074, 7204, 1),
 (2, 1, 50000, 34, 1489, 9073, 7203, 1),
 (2, 3, 50000, 34, 1489, 9073, 7203, 1),
 (3, 3, 50000, 23, 3669, 7506, 16890, 1),
 (3, 3, 50000, 23, 3669, 8244, 16891, 1),
 (3, 3, 50000, 23, 3669, 8244, 16893, 1),
 (3, 14, 50000, 23, 3669, 7506, 16890, 1),
 (3, 14, 50000, 23, 3669, 8244, 16891, 1),
 (3, 14, 50000, 23, 3669, 8244, 16893, 1);


 INSERT INTO dbo.plan_strat_clus
 (plan_id, strategy_id,analytics_cluster_id,analytics_cluster_label)
 VALUES(1,1,0,'All'),
 (1,1,1,'Cluster 1'),
 (1,1,2,'Cluster 2'),
 (1,1,3,'Cluster 3'),
 (1,1,4,'Cluster 4'),
 (1,1,5,'Cluster 5'),
 (1,1,6,'Cluster 6'),
 (1,1,7,'Cluster 7'),
  (2,1,0,'All'),
  (2,1,1,'Cluster 1'),
  (2,1,2,'Cluster 2'),
  (2,1,3,'Cluster 3'),
  (2,1,4,'Cluster 4'),
  (2,1,5,'Cluster 5'),
  (2,1,6,'Cluster 6'),
  (2,1,7,'Cluster 7'),
  (3,14,0,'All'),
  (3,14,1,'Cluster 1'),
  (3,14,2,'Cluster 2'),
  (3,14,3,'Cluster 3'),
  (3,14,4,'Cluster 4'),
  (3,14,5,'Cluster 5'),
  (3,14,6,'Cluster 6'),
  (3,14,7,'Cluster 7');

 INSERT INTO dbo.strat_fl (plan_id , strategy_id , rpt_lvl_0_nbr, rpt_lvl_1_nbr, rpt_lvl_2_nbr, rpt_lvl_3_nbr, rpt_lvl_4_nbr, fineline_nbr, channel_id,
  rpt_lvl_0_gen_desc1, rpt_lvl_1_gen_desc1, rpt_lvl_2_gen_desc1, rpt_lvl_3_gen_desc1, rpt_lvl_4_gen_desc1,  fineline_desc, trait_choice_code, run_status_code, rfa_status_code, alloc_run_type_code, brand_obj, ahs_v_id)
 VALUES
 (1, 1, 50000, 34, 1488, 9074, 7204, 465, 1, 'APPAREL', 'D34 - Womens Apparel', 'Activewear Womens', 'Tops Activewear Womens', 'Jackets Tops Active Womens', 'finelineDesc', 3, 1, 3, 1, null, null),
 (1, 3, 50000, 34, 1488, 9074, 7204, 465, 1, 'APPAREL', 'D34 - Womens Apparel', 'Activewear Womens', 'Tops Activewear Womens', 'Jackets Tops Active Womens', 'finelineDesc', 3, null, null, null, null, null),
 (2, 1, 50000, 34, 1489, 9073, 7203, 123, 1, 'APPAREL', 'D34 - Womens Apparel', 'Activewear Womens', 'Tops Activewear Womens', 'Jackets Tops Active Womens', 'finelineDesc', 3, 1, 3, 1, null, null),
 (1, 1, 50000, 34, 1488, 9074, 7204, 4567, 1, 'APPAREL', 'D34 - Womens Apparel', 'Activewear Womens', 'Tops Activewear Womens', 'Jackets Tops Active Womens', 'finelineDesc', 3, null, null, null, null, null),
 (1, 3, 50000, 34, 1488, 9074, 7204, 4567, 1, 'APPAREL', 'D34 - Womens Apparel', 'Activewear Womens', 'Tops Activewear Womens', 'Jackets Tops Active Womens', 'finelineDesc', 3, null, null, null, null, null),
 (3, 14, 50000, 23, 3669, 7506, 16890, 399, 1, 'APPAREL', 'D34 - Womens Apparel', 'Activewear Womens', 'Tops Activewear Womens', 'Jackets Tops Active Womens', 'finelineDesc', 3, 2, 2, 1, null, null),
 (3, 14, 50000, 23, 3669, 7506, 16890, 473, 1, 'APPAREL', 'D34 - Womens Apparel', 'Activewear Womens', 'Tops Activewear Womens', 'Jackets Tops Active Womens', 'finelineDesc', 3, 2, 2, 1, null, null),
 (3, 14, 50000, 23, 3669, 8244, 16891, 190, 1, 'APPAREL', 'D34 - Womens Apparel', 'Activewear Womens', 'Tops Activewear Womens', 'Jackets Tops Active Womens', 'finelineDesc', 3, 2, 2, 1, null, null),
 (3, 14, 50000, 23, 3669, 8244, 16891, 329, 1, 'APPAREL', 'D34 - Womens Apparel', 'Activewear Womens', 'Tops Activewear Womens', 'Jackets Tops Active Womens', 'finelineDesc', 3, 2, 2, 1, null, null),
 (3, 14, 50000, 23, 3669, 8244, 16893, 151, 1, 'APPAREL', 'D34 - Womens Apparel', 'Activewear Womens', 'Tops Activewear Womens', 'Jackets Tops Active Womens', 'finelineDesc', 3, 2, 2, 1, null, null),
 (3, 14, 50000, 23, 3669, 8244, 16893, 232, 1, 'APPAREL', 'D34 - Womens Apparel', 'Activewear Womens', 'Tops Activewear Womens', 'Jackets Tops Active Womens', 'finelineDesc', 3, 2, 2, 1, null, null);

INSERT INTO dbo.elig_fl_clus_rank
(plan_id, strategy_id, analytics_cluster_id, rpt_lvl_0_nbr, rpt_lvl_1_nbr, rpt_lvl_2_nbr, rpt_lvl_3_nbr, rpt_lvl_4_nbr,
fineline_nbr, fineline_desc,merchant_override_rank, in_store_yr_wk, markdown_yr_wk, is_eligible,select_status_id, in_store_yrwk_desc, markdown_yrwk_desc, store_cnt)
VALUES(1,1,0, 50000,34,1488,9074,7204,465, 'Womens Active Sleevless',1,12101,12112,1,1, 'FYE2021WK01', 'FYE2021WK12', 2800),
(1,1,1, 50000,34,1488,9074,7204,465, 'Womens Active Sleevless',1,12101,12112,1,1, 'FYE2021WK01', 'FYE2021WK12', 100),
(1,1,2, 50000,34,1488,9074,7204,465, 'Womens Active Sleevless',1,12101,12112,1,1, 'FYE2021WK01', 'FYE2021WK12', 200),
(1,1,3, 50000,34,1488,9074,7204,465, 'Womens Active Sleevless',1,12101,12112,1,1, 'FYE2021WK01', 'FYE2021WK12', 300),
(1,1,4, 50000,34,1488,9074,7204,465, 'Womens Active Sleevless',1,12101,12112,1,1, 'FYE2021WK01', 'FYE2021WK12', 400),
(1,1,5, 50000,34,1488,9074,7204,465, 'Womens Active Sleevless',1,12101,12112,1,1, 'FYE2021WK01', 'FYE2021WK12', 500),
(1,1,6, 50000,34,1488,9074,7204,465, 'Womens Active Sleevless',1,12101,12112,1,1, 'FYE2021WK01', 'FYE2021WK12', 600),
(1,1,7, 50000,34,1488,9074,7204,465, 'Womens Active Sleevless',1,12101,12112,1,1, 'FYE2021WK01', 'FYE2021WK12', 700),
(2,1,0, 50000,34,1489,9073,7203,123, 'Womens Active Sleevless',1,12101,12112,1,1, 'FYE2021WK01', 'FYE2021WK12', 2800),
(2,1,1, 50000,34,1489,9073,7203,123, 'Womens Active Sleevless',1,12101,12112,1,1, 'FYE2021WK01', 'FYE2021WK12', 100),
(2,1,2, 50000,34,1489,9073,7203,123, 'Womens Active Sleevless',1,12101,12112,1,1, 'FYE2021WK01', 'FYE2021WK12', 200),
(2,1,3, 50000,34,1489,9073,7203,123, 'Womens Active Sleevless',1,12101,12112,1,1, 'FYE2021WK01', 'FYE2021WK12', 300),
(2,1,4, 50000,34,1489,9073,7203,123, 'Womens Active Sleevless',1,12101,12112,1,1, 'FYE2021WK01', 'FYE2021WK12', 400),
(2,1,5, 50000,34,1489,9073,7203,123, 'Womens Active Sleevless',1,12101,12112,1,1, 'FYE2021WK01', 'FYE2021WK12', 500),
(2,1,6, 50000,34,1489,9073,7203,123, 'Womens Active Sleevless',1,12101,12112,1,1, 'FYE2021WK01', 'FYE2021WK12', 600),
(2,1,7, 50000,34,1489,9073,7203,123, 'Womens Active Sleevless',1,12101,12112,1,1, 'FYE2021WK01', 'FYE2021WK12', 700),
(1,1,0, 50000,34,1488,9074,7204,4567, 'Womens Active Sleevless',1,12101,12112,1,1, 'FYE2021WK01', 'FYE2021WK12', 2800),
(1,1,1, 50000,34,1488,9074,7204,4567, 'Womens Active Sleevless',1,12101,12112,1,1, 'FYE2021WK01', 'FYE2021WK12', 100),
(1,1,2, 50000,34,1488,9074,7204,4567, 'Womens Active Sleevless',1,12101,12112,1,1, 'FYE2021WK01', 'FYE2021WK12', 200),
(1,1,3, 50000,34,1488,9074,7204,4567, 'Womens Active Sleevless',1,12101,12112,1,1, 'FYE2021WK01', 'FYE2021WK12', 300),
(1,1,4, 50000,34,1488,9074,7204,4567, 'Womens Active Sleevless',1,12101,12112,1,1, 'FYE2021WK01', 'FYE2021WK12', 400),
(1,1,5, 50000,34,1488,9074,7204,4567, 'Womens Active Sleevless',1,12101,12112,1,1, 'FYE2021WK01', 'FYE2021WK12', 500),
(1,1,6, 50000,34,1488,9074,7204,4567, 'Womens Active Sleevless',1,12101,12112,1,1, 'FYE2021WK01', 'FYE2021WK12', 600),
(1,1,7, 50000,34,1488,9074,7204,4567, 'Womens Active Sleevless',1,12101,12112,1,1, 'FYE2021WK01', 'FYE2021WK12', 700),
(3,14,0,50000,23,3669,7506,16890,399, '399-USPA CREWNECK FLEECE SWTSHIRT',1,12401,12415,1,1,'FYE2025WK01','FYE2025WK15',3939),
(3,14,0,50000,23,3669,7506,16890,473, '473-USPA FLEECE CREWNECK SWTSHIRT1',1,12401,12415,1,1,'FYE2025WK01','FYE2025WK15',3939),
(3,14,0,50000,23,3669,8244,16891,190, '190-WR STRAIGHT JEAN',1,12401,12415,1,1,'FYE2025WK01','FYE2025WK15',3939),
(3,14,0,50000,23,3669,8244,16891,329, '329-WR FLEECE LINED JEAN',1,12401,12415,1,1,'FYE2025WK01','FYE2025WK15',3939),
(3,14,0,50000,23,3669,8244,16893,151, '151-WR PERF OUTDOOR EWAIST',1,12401,12415,1,1,'FYE2025WK01','FYE2025WK15',3939),
(3,14,0,50000,23,3669,8244,16893,232, '232-GE STRIPE WALK SHORT',1,12401,12415,1,1,'FYE2025WK01','FYE2025WK15',3939),
(3,14,1,50000,23,3669,7506,16890,399, '399-USPA CREWNECK FLEECE SWTSHIRT',1,12401,12415,1,1,'FYE2025WK01','FYE2025WK15',569),
(3,14,1,50000,23,3669,7506,16890,473, '473-USPA FLEECE CREWNECK SWTSHIRT1',1,12401,12415,1,1,'FYE2025WK01','FYE2025WK15',569),
(3,14,1,50000,23,3669,8244,16891,190, '190-WR STRAIGHT JEAN',1,12401,12415,1,1,'FYE2025WK01','FYE2025WK15',569),
(3,14,1,50000,23,3669,8244,16891,329, '329-WR FLEECE LINED JEAN',1,12401,12415,1,1,'FYE2025WK01','FYE2025WK15',569),
(3,14,1,50000,23,3669,8244,16893,151, '151-WR PERF OUTDOOR EWAIST',1,12401,12415,1,1,'FYE2025WK01','FYE2025WK15',569),
(3,14,1,50000,23,3669,8244,16893,232, '232-GE STRIPE WALK SHORT',1,12401,12415,1,1,'FYE2025WK01','FYE2025WK15',569),
(3,14,2,50000,23,3669,7506,16890,399, '399-USPA CREWNECK FLEECE SWTSHIRT',1,12401,12415,1,1,'FYE2025WK01','FYE2025WK15',286),
(3,14,2,50000,23,3669,7506,16890,473, '473-USPA FLEECE CREWNECK SWTSHIRT1',1,12401,12415,1,1,'FYE2025WK01','FYE2025WK15',286),
(3,14,2,50000,23,3669,8244,16891,190, '190-WR STRAIGHT JEAN',1,12401,12415,1,1,'FYE2025WK01','FYE2025WK15',286),
(3,14,2,50000,23,3669,8244,16891,329, '329-WR FLEECE LINED JEAN',1,12401,12415,1,1,'FYE2025WK01','FYE2025WK15',286),
(3,14,2,50000,23,3669,8244,16893,151, '151-WR PERF OUTDOOR EWAIST',1,12401,12415,1,1,'FYE2025WK01','FYE2025WK15',286),
(3,14,2,50000,23,3669,8244,16893,232, '232-GE STRIPE WALK SHORT',1,12401,12415,1,1,'FYE2025WK01','FYE2025WK15',286),
(3,14,3,50000,23,3669,7506,16890,399, '399-USPA CREWNECK FLEECE SWTSHIRT',1,12401,12415,1,1,'FYE2025WK01','FYE2025WK15',705),
(3,14,3,50000,23,3669,7506,16890,473, '473-USPA FLEECE CREWNECK SWTSHIRT1',1,12401,12415,1,1,'FYE2025WK01','FYE2025WK15',705),
(3,14,3,50000,23,3669,8244,16891,190, '190-WR STRAIGHT JEAN',1,12401,12415,1,1,'FYE2025WK01','FYE2025WK15',705),
(3,14,3,50000,23,3669,8244,16891,329, '329-WR FLEECE LINED JEAN',1,12401,12415,1,1,'FYE2025WK01','FYE2025WK15',705),
(3,14,3,50000,23,3669,8244,16893,151, '151-WR PERF OUTDOOR EWAIST',1,12401,12415,1,1,'FYE2025WK01','FYE2025WK15',705),
(3,14,3,50000,23,3669,8244,16893,232, '232-GE STRIPE WALK SHORT',1,12401,12415,1,1,'FYE2025WK01','FYE2025WK15',705),
(3,14,4,50000,23,3669,7506,16890,399, '399-USPA CREWNECK FLEECE SWTSHIRT',1,12401,12415,1,1,'FYE2025WK01','FYE2025WK15',447),
(3,14,4,50000,23,3669,7506,16890,473, '473-USPA FLEECE CREWNECK SWTSHIRT1',1,12401,12415,1,1,'FYE2025WK01','FYE2025WK15',447),
(3,14,4,50000,23,3669,8244,16891,190, '190-WR STRAIGHT JEAN',1,12401,12415,1,1,'FYE2025WK01','FYE2025WK15',447),
(3,14,4,50000,23,3669,8244,16891,329, '329-WR FLEECE LINED JEAN',1,12401,12415,1,1,'FYE2025WK01','FYE2025WK15',447),
(3,14,4,50000,23,3669,8244,16893,151, '151-WR PERF OUTDOOR EWAIST',1,12401,12415,1,1,'FYE2025WK01','FYE2025WK15',447),
(3,14,4,50000,23,3669,8244,16893,232, '232-GE STRIPE WALK SHORT',1,12401,12415,1,1,'FYE2025WK01','FYE2025WK15',447),
(3,14,5,50000,23,3669,7506,16890,399, '399-USPA CREWNECK FLEECE SWTSHIRT',1,12401,12415,1,1,'FYE2025WK01','FYE2025WK15',705),
(3,14,5,50000,23,3669,7506,16890,473, '473-USPA FLEECE CREWNECK SWTSHIRT1',1,12401,12415,1,1,'FYE2025WK01','FYE2025WK15',705),
(3,14,5,50000,23,3669,8244,16891,190, '190-WR STRAIGHT JEAN',1,12401,12415,1,1,'FYE2025WK01','FYE2025WK15',705),
(3,14,5,50000,23,3669,8244,16891,329, '329-WR FLEECE LINED JEAN',1,12401,12415,1,1,'FYE2025WK01','FYE2025WK15',705),
(3,14,5,50000,23,3669,8244,16893,151, '151-WR PERF OUTDOOR EWAIST',1,12401,12415,1,1,'FYE2025WK01','FYE2025WK15',705),
(3,14,5,50000,23,3669,8244,16893,232, '232-GE STRIPE WALK SHORT',1,12401,12415,1,1,'FYE2025WK01','FYE2025WK15',705),
(3,14,6,50000,23,3669,7506,16890,399, '399-USPA CREWNECK FLEECE SWTSHIRT',1,12401,12415,1,1,'FYE2025WK01','FYE2025WK15',733),
(3,14,6,50000,23,3669,7506,16890,473, '473-USPA FLEECE CREWNECK SWTSHIRT1',1,12401,12415,1,1,'FYE2025WK01','FYE2025WK15',733),
(3,14,6,50000,23,3669,8244,16891,190, '190-WR STRAIGHT JEAN',1,12401,12415,1,1,'FYE2025WK01','FYE2025WK15',733),
(3,14,6,50000,23,3669,8244,16891,329, '329-WR FLEECE LINED JEAN',1,12401,12415,1,1,'FYE2025WK01','FYE2025WK15',733),
(3,14,6,50000,23,3669,8244,16893,151, '151-WR PERF OUTDOOR EWAIST',1,12401,12415,1,1,'FYE2025WK01','FYE2025WK15',733),
(3,14,6,50000,23,3669,8244,16893,232, '232-GE STRIPE WALK SHORT',1,12401,12415,1,1,'FYE2025WK01','FYE2025WK15',733),
(3,14,7,50000,23,3669,7506,16890,399, '399-USPA CREWNECK FLEECE SWTSHIRT',1,12401,12415,1,1,'FYE2025WK01','FYE2025WK15',494),
(3,14,7,50000,23,3669,7506,16890,473, '473-USPA FLEECE CREWNECK SWTSHIRT1',1,12401,12415,1,1,'FYE2025WK01','FYE2025WK15',494),
(3,14,7,50000,23,3669,8244,16891,190, '190-WR STRAIGHT JEAN',1,12401,12415,1,1,'FYE2025WK01','FYE2025WK15',494),
(3,14,7,50000,23,3669,8244,16891,329, '329-WR FLEECE LINED JEAN',1,12401,12415,1,1,'FYE2025WK01','FYE2025WK15',494),
(3,14,7,50000,23,3669,8244,16893,151, '151-WR PERF OUTDOOR EWAIST',1,12401,12415,1,1,'FYE2025WK011','FYE2025WK15',494),
(3,14,7,50000,23,3669,8244,16893,232, '232-GE STRIPE WALK SHORT',1,12401,12415,1,1,'FYE2025WK011','FYE2025WK15',494);



INSERT INTO dbo.elig_fl_clus_metrics
(plan_id,strategy_id,analytics_cluster_id,rpt_lvl_0_nbr,rpt_lvl_1_nbr,rpt_lvl_2_nbr,rpt_lvl_3_nbr,rpt_lvl_4_nbr,fineline_nbr,sales_dollars,
sales_units,forecasted_units,forecasted_dollars,on_hand_qty,sales_to_stock_ratio,analytics_cluster_rank)
Values(1,1,0, 50000,34,1488,9074,7204,465,100000,50000,60000,120000,100,12.25,2),
(1,1,1, 50000,34,1488,9074,7204,465,100000,50000,60000,120000,100,12.25,2),
(1,1,2, 50000,34,1488,9074,7204,465,100000,50000,60000,120000,100,12.25,2),
(1,1,3, 50000,34,1488,9074,7204,465,100000,50000,60000,120000,100,12.25,2),
(1,1,4, 50000,34,1488,9074,7204,465,100000,50000,60000,120000,100,12.25,2),
(1,1,5, 50000,34,1488,9074,7204,465,100000,50000,60000,120000,100,12.25,2),
(1,1,6, 50000,34,1488,9074,7204,465,100000,50000,60000,120000,100,12.25,2),
(1,1,7, 50000,34,1488,9074,7204,465,100000,50000,60000,120000,100,12.25,2),
(2,1,0, 50000,34,1489,9073,7203,123,100000,50000,60000,120000,100,12.25,2),
(2,1,1, 50000,34,1489,9073,7203,123,100000,50000,60000,120000,100,12.25,2),
(2,1,2, 50000,34,1489,9073,7203,123,100000,50000,60000,120000,100,12.25,2),
(2,1,3, 50000,34,1489,9073,7203,123,100000,50000,60000,120000,100,12.25,2),
(2,1,4, 50000,34,1489,9073,7203,123,100000,50000,60000,120000,100,12.25,2),
(2,1,5, 50000,34,1489,9073,7203,123,100000,50000,60000,120000,100,12.25,2),
(2,1,6, 50000,34,1489,9073,7203,123,100000,50000,60000,120000,100,12.25,2),
(2,1,7, 50000,34,1489,9073,7203,123,100000,50000,60000,120000,100,12.25,2),
(1,1,0, 50000,34,1488,9074,7204,4567,100000,50000,60000,120000,100,12.25,2),
(1,1,1, 50000,34,1488,9074,7204,4567,100000,50000,60000,120000,100,12.25,2),
(1,1,2, 50000,34,1488,9074,7204,4567,100000,50000,60000,120000,100,12.25,2),
(1,1,3, 50000,34,1488,9074,7204,4567,100000,50000,60000,120000,100,12.25,2),
(1,1,4, 50000,34,1488,9074,7204,4567,100000,50000,60000,120000,100,12.25,2),
(1,1,5, 50000,34,1488,9074,7204,4567,100000,50000,60000,120000,100,12.25,2),
(1,1,6, 50000,34,1488,9074,7204,4567,100000,50000,60000,120000,100,12.25,2),
(1,1,7, 50000,34,1488,9074,7204,4567,100000,50000,60000,120000,100,12.25,2),
(3,14,0,50000,23,3669,7506,16890,399,NULL,NULL,NULL,NULL,NULL,NULL,5),
(3,14,0,50000,23,3669,7506,16890,473,NULL,NULL,NULL,NULL,NULL,NULL,5),
(3,14,0,50000,23,3669,8244,16891,190,5609456.0000,267434,264516,5145044.0000,511427,0.0820,1),
(3,14,0,50000,23,3669,8244,16891,329,3870106.0000,185817,138663,2524969.0000,242248,0.1986,1),
(3,14,0,50000,23,3669,8244,16893,151,2458053.0000,140760,165246,2868222.0000,479750,0.0817,2),
(3,14,0,50000,23,3669,8244,16893,232,1280856.0000,97454,104593,1320033.0000,336515,0.0832,3),
(3,14,1,50000,23,3669,7506,16890,399,NULL,NULL,NULL,NULL,NULL,NULL,5),
(3,14,1,50000,23,3669,7506,16890,473,NULL,NULL,NULL,NULL,NULL,NULL,5),
(3,14,1,50000,23,3669,8244,16891,190,1070402.0000,50703,52311,1017490.0000,90724,0.0901,1),
(3,14,1,50000,23,3669,8244,16891,329,55700.0000,2679,3170,57723.0000,5042,0.3043,1),
(3,14,1,50000,23,3669,8244,16893,151,495754.0000,29048,20047,347961.0000,40408,0.1095,2),
(3,14,1,50000,23,3669,8244,16893,232,298384.0000,22311,22273,281100.0000,58957,0.0841,3),
(3,14,2,50000,23,3669,7506,16890,399,NULL,NULL,NULL,NULL,NULL,NULL,5),
(3,14,2,50000,23,3669,7506,16890,473,NULL,NULL,NULL,NULL,NULL,NULL,5),
(3,14,2,50000,23,3669,8244,16891,190,670365.0000,32002,30545,594124.0000,47849,0.1148,1),
(3,14,2,50000,23,3669,8244,16891,329,30432.0000,1462,2223,40479.0000,3150,0.3531,1),
(3,14,2,50000,23,3669,8244,16893,151,288594.0000,16871,9320,161770.0000,19635,0.1247,2),
(3,14,2,50000,23,3669,8244,16893,232,106266.0000,9202,6359,80255.0000,16619,0.1185,3),
(3,14,3,50000,23,3669,7506,16890,399,NULL,NULL,NULL,NULL,NULL,NULL,5),
(3,14,3,50000,23,3669,7506,16890,473,NULL,NULL,NULL,NULL,NULL,NULL,5),
(3,14,3,50000,23,3669,8244,16891,190,904412.0000,43181,43646,848949.0000,86234,0.0759,1),
(3,14,3,50000,23,3669,8244,16891,329,508707.0000,23720,15506,282355.0000,28977,0.1633,1),
(3,14,3,50000,23,3669,8244,16893,151,416411.0000,23877,34111,592075.0000,85883,0.0717,2),
(3,14,3,50000,23,3669,8244,16893,232,223786.0000,16827,16883,213075.0000,54558,0.39,3),
(3,14,4,50000,23,3669,7506,16890,399,NULL,NULL,NULL,NULL,NULL,NULL,5),
(3,14,4,50000,23,3669,7506,16890,473,NULL,NULL,NULL,NULL,NULL,NULL,5),
(3,14,4,50000,23,3669,8244,16891,190,592685.0000,28297,28210,548707.0000,55533,0.0793,1),
(3,14,4,50000,23,3669,8244,16891,329,429528.0000,19642,12805,233171.0000,23888,0.1483,1),
(3,14,4,50000,23,3669,8244,16893,151,246686.0000,13983,22424,389220.0000,58652,0.0709,2),
(3,14,4,50000,23,3669,8244,16893,232,148473.0000,10909,12969,163677.0000,43830,0.0729,3),
(3,14,5,50000,23,3669,7506,16890,399,NULL,NULL,NULL,NULL,NULL,NULL,5),
(3,14,5,50000,23,3669,7506,16890,473,NULL,NULL,NULL,NULL,NULL,NULL,5),
(3,14,5,50000,23,3669,8244,16891,190,906803.0000,43315,42088,818645.0000,86798,0.0742,1),
(3,14,5,50000,23,3669,8244,16891,329,890583.0000,41697,31226,568607.0000,56066,0.1363,1),
(3,14,5,50000,23,3669,8244,16893,151,356938.0000,20140,30601,531150.0000,97473,0.0670,2),
(3,14,5,50000,23,3669,8244,16893,232,197920.0000,14814,17054,215233.0000,59150,0.0693,3),
(3,14,6,50000,23,3669,7506,16890,399,NULL,NULL,NULL,NULL,NULL,NULL,5),
(3,14,6,50000,23,3669,7506,16890,473,NULL,NULL,NULL,NULL,NULL,NULL,5),
(3,14,6,50000,23,3669,8244,16891,190,912203.0000,43545,41204,801450.0000,87050,0.0710,1),
(3,14,6,50000,23,3669,8244,16891,329,1099788.0000,53956,38161,694889.0000,66620,0.1428,1),
(3,14,6,50000,23,3669,8244,16893,151,386892.0000,21792,30262,525266.0000,107282,0.0639,2),
(3,14,6,50000,23,3669,8244,16893,232,195152.0000,14767,18434,232649.0000,64676,0.0694,3),
(3,14,7,50000,23,3669,7506,16890,399,NULL,NULL,NULL,NULL,NULL,NULL,5),
(3,14,7,50000,23,3669,7506,16890,473,NULL,NULL,NULL,NULL,NULL,NULL,5),
(3,14,7,50000,23,3669,8244,16891,190,552586.0000,26391,26512,515679.0000,57239,0.0690,1),
(3,14,7,50000,23,3669,8244,16891,329,855368.0000,42661,35572,647745.0000,58505,0.1424,1),
(3,14,7,50000,23,3669,8244,16893,151,266778.0000,15049,18481,320780.0000,70417,0.0639,2),
(3,14,7,50000,23,3669,8244,16893,232,110875.0000,8624,10621,134044.0000,38725,0.0653,3);

INSERT INTO dbo.elig_fl_mkt_clus
(plan_id,strategy_id,analytics_cluster_id,rpt_lvl_0_nbr,rpt_lvl_1_nbr,rpt_lvl_2_nbr,rpt_lvl_3_nbr,rpt_lvl_4_nbr,fineline_nbr,market_select_code)
values(1,1,7, 50000,34,1488,9074,7204,465,2),
(1,1,1, 50000,34,1488,9074,7204,465,3),
(2,1,7, 50000,34,1489,9073,7203,123,2),
(2,1,1, 50000,34,1489,9073,7203,123,3),
(1,1,7, 50000,34,1488,9074,7204,4567,2),
(1,1,1, 50000,34,1488,9074,7204,4567,3);

INSERT INTO dbo.strat_style (plan_id , strategy_id , rpt_lvl_0_nbr, rpt_lvl_1_nbr, rpt_lvl_2_nbr, rpt_lvl_3_nbr, rpt_lvl_4_nbr, fineline_nbr, style_nbr, channel_id)
 VALUES
 (1, 1, 50000, 34, 1488, 9074, 7204, 465, 'test_style', 1),
 (1, 3, 50000, 34, 1488, 9074, 7204, 465, 'test_style', 1),
 (2, 1, 50000, 34, 1489, 9073, 7203, 123, 'test_style1', 1),
 (1, 1, 50000, 34, 1488, 9074, 7204, 4567, 'test_style_4567', 1),
 (1, 1, 50000, 34, 1488, 9074, 7204, 4567, 'test_style_4567_1', 1);

INSERT INTO dbo.clus_style (plan_id , strategy_id , analytics_cluster_id, rpt_lvl_0_nbr, rpt_lvl_1_nbr, rpt_lvl_2_nbr, rpt_lvl_3_nbr, rpt_lvl_4_nbr, fineline_nbr, style_nbr)
VALUES(1, 1, 0, 50000, 34, 1488, 9074, 7204, 465, 'test_style'),
(1, 1, 1, 50000, 34, 1488, 9074, 7204, 465, 'test_style'),
(1, 1, 2, 50000, 34, 1488, 9074, 7204, 465, 'test_style'),
(1, 1, 3, 50000, 34, 1488, 9074, 7204, 465, 'test_style'),
(1, 1, 4, 50000, 34, 1488, 9074, 7204, 465, 'test_style'),
(1, 1, 5, 50000, 34, 1488, 9074, 7204, 465, 'test_style'),
(1, 1, 6, 50000, 34, 1488, 9074, 7204, 465, 'test_style'),
(1, 1, 7, 50000, 34, 1488, 9074, 7204, 465, 'test_style'),
(2, 1, 0, 50000, 34, 1489, 9073, 7203, 123, 'test_style1'),
(2, 1, 1, 50000, 34, 1489, 9073, 7203, 123, 'test_style1'),
(2, 1, 2, 50000, 34, 1489, 9073, 7203, 123, 'test_style1'),
(2, 1, 3, 50000, 34, 1489, 9073, 7203, 123, 'test_style1'),
(2, 1, 4, 50000, 34, 1489, 9073, 7203, 123, 'test_style1'),
(2, 1, 5, 50000, 34, 1489, 9073, 7203, 123, 'test_style1'),
(2, 1, 6, 50000, 34, 1489, 9073, 7203, 123, 'test_style1'),
(2, 1, 7, 50000, 34, 1489, 9073, 7203, 123, 'test_style1'),
(1, 1, 0, 50000, 34, 1488, 9074, 7204, 4567, 'test_style_4567'),
(1, 1, 1, 50000, 34, 1488, 9074, 7204, 4567, 'test_style_4567'),
(1, 1, 2, 50000, 34, 1488, 9074, 7204, 4567, 'test_style_4567'),
(1, 1, 3, 50000, 34, 1488, 9074, 7204, 4567, 'test_style_4567'),
(1, 1, 4, 50000, 34, 1488, 9074, 7204, 4567, 'test_style_4567'),
(1, 1, 5, 50000, 34, 1488, 9074, 7204, 4567, 'test_style_4567'),
(1, 1, 6, 50000, 34, 1488, 9074, 7204, 4567, 'test_style_4567'),
(1, 1, 7, 50000, 34, 1488, 9074, 7204, 4567, 'test_style_4567'),
(1, 1, 0, 50000, 34, 1488, 9074, 7204, 4567, 'test_style_4567_1'),
(1, 1, 1, 50000, 34, 1488, 9074, 7204, 4567, 'test_style_4567_1'),
(1, 1, 2, 50000, 34, 1488, 9074, 7204, 4567, 'test_style_4567_1'),
(1, 1, 3, 50000, 34, 1488, 9074, 7204, 4567, 'test_style_4567_1'),
(1, 1, 4, 50000, 34, 1488, 9074, 7204, 4567, 'test_style_4567_1'),
(1, 1, 5, 50000, 34, 1488, 9074, 7204, 4567, 'test_style_4567_1'),
(1, 1, 6, 50000, 34, 1488, 9074, 7204, 4567, 'test_style_4567_1'),
(1, 1, 7, 50000, 34, 1488, 9074, 7204, 4567, 'test_style_4567_1');


INSERT INTO dbo.strat_cc (plan_id , strategy_id , rpt_lvl_0_nbr, rpt_lvl_1_nbr, rpt_lvl_2_nbr, rpt_lvl_3_nbr, rpt_lvl_4_nbr, fineline_nbr, style_nbr, customer_choice, color_name, channel_id, color_family_desc)
 VALUES
 (1, 1, 50000, 34, 1488, 9074, 7204, 465, 'test_style', 'test_cc', 'test_color', 1, 'test_color_family'),
 (1, 3, 50000, 34, 1488, 9074, 7204, 465, 'test_style', 'test_cc', 'test_color', 1, 'test_color_family'),
 (2, 1, 50000, 34, 1489, 9073, 7203, 123, 'test_style1', 'test_cc1', 'test_color1', 1, 'test_color_family'),
 (1, 1, 50000, 34, 1488, 9074, 7204, 4567, 'test_style_4567', 'test_cc_4567', 'test_color', 1, 'test_color_family'),
 (1, 1, 50000, 34, 1488, 9074, 7204, 4567, 'test_style_4567', 'test_cc_4567_1', 'test_color', 1, 'test_color_family'),
 (1, 1, 50000, 34, 1488, 9074, 7204, 4567, 'test_style_4567_1', 'test_cc_4567_1_1', 'test_color', 1, 'test_color_family');

INSERT INTO dbo.elig_cc_clus_rank (plan_id , strategy_id , analytics_cluster_id, rpt_lvl_0_nbr, rpt_lvl_1_nbr, rpt_lvl_2_nbr, rpt_lvl_3_nbr, rpt_lvl_4_nbr, fineline_nbr, style_nbr, customer_choice,in_store_yr_wk,markdown_yr_wk,merchant_override_rank,ap_ranking,is_eligible,select_status_id,in_store_yrwk_desc,markdown_yrwk_desc)
VALUES(1, 1, 0, 50000, 34, 1488, 9074, 7204, 465, 'test_style', 'test_cc', 12101, 12112, 1, null, 1,1, 'FYE2021WK01', 'FYE2021WK12'),
(1, 1, 1, 50000, 34, 1488, 9074, 7204, 465, 'test_style', 'test_cc', 12101, 12112, 1, null, 1,1, 'FYE2021WK01', 'FYE2021WK12'),
(1, 1, 2, 50000, 34, 1488, 9074, 7204, 465, 'test_style', 'test_cc', 12101, 12112, 1, null, 1,1, 'FYE2021WK01', 'FYE2021WK12'),
(1, 1, 3, 50000, 34, 1488, 9074, 7204, 465, 'test_style', 'test_cc', 12101, 12112, 1, null, 1,1, 'FYE2021WK01', 'FYE2021WK12'),
(1, 1, 4, 50000, 34, 1488, 9074, 7204, 465, 'test_style', 'test_cc', 12101, 12112, 1, null, 1,1, 'FYE2021WK01', 'FYE2021WK12'),
(1, 1, 5, 50000, 34, 1488, 9074, 7204, 465, 'test_style', 'test_cc', 12101, 12112, 1, null, 1,1, 'FYE2021WK01', 'FYE2021WK12'),
(1, 1, 6, 50000, 34, 1488, 9074, 7204, 465, 'test_style', 'test_cc', 12101, 12112, 1, null, 1,1, 'FYE2021WK01', 'FYE2021WK12'),
(1, 1, 7, 50000, 34, 1488, 9074, 7204, 465, 'test_style', 'test_cc', 12101, 12112, 1, null, 1,1, 'FYE2021WK01', 'FYE2021WK12'),
(2, 1, 0, 50000, 34, 1489, 9073, 7203, 123, 'test_style1', 'test_cc1', 12101, 12112, 1, null, 1,1, 'FYE2021WK01', 'FYE2021WK12'),
(2, 1, 1, 50000, 34, 1489, 9073, 7203, 123, 'test_style1', 'test_cc1', 12101, 12112, 1, null, 1,1, 'FYE2021WK01', 'FYE2021WK12'),
(2, 1, 2, 50000, 34, 1489, 9073, 7203, 123, 'test_style1', 'test_cc1', 12101, 12112, 1, null, 1,1, 'FYE2021WK01', 'FYE2021WK12'),
(2, 1, 3, 50000, 34, 1489, 9073, 7203, 123, 'test_style1', 'test_cc1', 12101, 12112, 1, null, 1,1, 'FYE2021WK01', 'FYE2021WK12'),
(2, 1, 4, 50000, 34, 1489, 9073, 7203, 123, 'test_style1', 'test_cc1', 12101, 12112, 1, null, 1,1, 'FYE2021WK01', 'FYE2021WK12'),
(2, 1, 5, 50000, 34, 1489, 9073, 7203, 123, 'test_style1', 'test_cc1', 12101, 12112, 1, null, 1,1, 'FYE2021WK01', 'FYE2021WK12'),
(2, 1, 6, 50000, 34, 1489, 9073, 7203, 123, 'test_style1', 'test_cc1', 12101, 12112, 1, null, 1,1, 'FYE2021WK01', 'FYE2021WK12'),
(2, 1, 7, 50000, 34, 1489, 9073, 7203, 123, 'test_style1', 'test_cc1', 12101, 12112, 1, null, 1,1, 'FYE2021WK01', 'FYE2021WK12'),
(1, 1, 0, 50000, 34, 1488, 9074, 7204, 4567, 'test_style_4567', 'test_cc_4567', 12101, 12112, 1, null, 1,1, 'FYE2021WK01', 'FYE2021WK12'),
(1, 1, 1, 50000, 34, 1488, 9074, 7204, 4567, 'test_style_4567', 'test_cc_4567', 12101, 12112, 1, null, 1,1, 'FYE2021WK01', 'FYE2021WK12'),
(1, 1, 2, 50000, 34, 1488, 9074, 7204, 4567, 'test_style_4567', 'test_cc_4567', 12101, 12112, 1, null, 1,1, 'FYE2021WK01', 'FYE2021WK12'),
(1, 1, 3, 50000, 34, 1488, 9074, 7204, 4567, 'test_style_4567', 'test_cc_4567', 12101, 12112, 1, null, 1,1, 'FYE2021WK01', 'FYE2021WK12'),
(1, 1, 4, 50000, 34, 1488, 9074, 7204, 4567, 'test_style_4567', 'test_cc_4567', 12101, 12112, 1, null, 1,1, 'FYE2021WK01', 'FYE2021WK12'),
(1, 1, 5, 50000, 34, 1488, 9074, 7204, 4567, 'test_style_4567', 'test_cc_4567', 12101, 12112, 1, null, 1,1, 'FYE2021WK01', 'FYE2021WK12'),
(1, 1, 6, 50000, 34, 1488, 9074, 7204, 4567, 'test_style_4567', 'test_cc_4567', 12101, 12112, 1, null, 1,1, 'FYE2021WK01', 'FYE2021WK12'),
(1, 1, 7, 50000, 34, 1488, 9074, 7204, 4567, 'test_style_4567', 'test_cc_4567', 12101, 12112, 1, null, 1,1, 'FYE2021WK01', 'FYE2021WK12'),
(1, 1, 0, 50000, 34, 1488, 9074, 7204, 4567, 'test_style_4567_1', 'test_cc_4567_1_1', 12101, 12112, 1, null, 1,1, 'FYE2021WK01', 'FYE2021WK12'),
(1, 1, 1, 50000, 34, 1488, 9074, 7204, 4567, 'test_style_4567_1', 'test_cc_4567_1_1', 12101, 12112, 1, null, 1, 1,'FYE2021WK01', 'FYE2021WK12'),
(1, 1, 2, 50000, 34, 1488, 9074, 7204, 4567, 'test_style_4567_1', 'test_cc_4567_1_1', 12101, 12112, 1, null, 1,1, 'FYE2021WK01', 'FYE2021WK12'),
(1, 1, 3, 50000, 34, 1488, 9074, 7204, 4567, 'test_style_4567_1', 'test_cc_4567_1_1', 12101, 12112, 1, null, 1,1, 'FYE2021WK01', 'FYE2021WK12'),
(1, 1, 4, 50000, 34, 1488, 9074, 7204, 4567, 'test_style_4567_1', 'test_cc_4567_1_1', 12101, 12112, 1, null, 1,1, 'FYE2021WK01', 'FYE2021WK12'),
(1, 1, 5, 50000, 34, 1488, 9074, 7204, 4567, 'test_style_4567_1', 'test_cc_4567_1_1', 12101, 12112, 1, null, 1,1, 'FYE2021WK01', 'FYE2021WK12'),
(1, 1, 6, 50000, 34, 1488, 9074, 7204, 4567, 'test_style_4567_1', 'test_cc_4567_1_1', 12101, 12112, 1, null, 1,1, 'FYE2021WK01', 'FYE2021WK12'),
(1, 1, 7, 50000, 34, 1488, 9074, 7204, 4567, 'test_style_4567_1', 'test_cc_4567_1_1', 12101, 12112, 1, null, 1,1, 'FYE2021WK01', 'FYE2021WK12');

INSERT INTO dbo.elig_cc_mkt_clus
(plan_id,strategy_id,analytics_cluster_id,rpt_lvl_0_nbr,rpt_lvl_1_nbr,rpt_lvl_2_nbr,rpt_lvl_3_nbr,rpt_lvl_4_nbr,fineline_nbr,style_nbr, customer_choice,market_select_code)
values(1,1,7, 50000,34,1488,9074,7204,465,'test_style','test_cc',2),
(1,1,1, 50000,34,1488,9074,7204,465,'test_style','test_cc',3),
(2,1,7, 50000,34, 1489, 9073, 7203, 123, 'test_style1', 'test_cc1',2),
(2,1,1, 50000,34, 1489, 9073, 7203, 123, 'test_style1', 'test_cc1',3),
(1,1,7, 50000,34,1488,9074,7204,4567, 'test_style_4567', 'test_cc_4567',2),
(1,1,1, 50000,34,1488,9074,7204,4567, 'test_style_4567', 'test_cc_4567',3),
(1,1,1, 50000,34,1488,9074,7204,4567, 'test_style_4567_1', 'test_cc_4567_1_1',3);

INSERT INTO dbo.strat_clus_store
(strategy_id, analytics_cluster_id, store_nbr, country_code)
VALUES
(1, 1,1, 'US'),
(1, 1,2, 'US'),
(1, 1,3, 'US'),
(1, 1,4, 'US'),
(1, 2,5, 'US'),
(1, 2,7, 'US'),
(1, 2,8, 'US'),
(1, 2,9, 'US'),
(1, 2,10, 'US'),
(1, 3,11, 'US'),
(1, 3,12, 'US'),
(1, 3,13, 'US'),
(1, 3,14, 'US'),
(1, 3,15, 'US'),
(1, 3,16, 'US'),
(1, 3,17, 'US'),
(1, 4,18, 'US'),
(1, 4,19, 'US'),
(1, 4,20, 'US'),
(1, 5,21, 'US'),
(1, 5,22, 'US'),
(1, 6,23, 'US'),
(1, 6,24, 'US'),
(1, 6,25, 'US'),
(1, 6,26, 'US'),
(1, 6,27, 'US'),
(1, 6,28, 'US'),
(1, 6,29, 'US'),
(1, 7,30, 'US');

INSERT INTO dbo.fixturetype_rollup
(fixturetype_rollup_id, fixturetype_rollup_name, fixturetype_rollup_desc)
VALUES
(1,	'walls',	'Gondola and T SYSTEM'),
(2,	'endcaps',	'ENDCAP is an offical fixture type'),
(3,	'racks',	'CIRCLE RACK, H RACK, 4 WAY, Apparel Rack fixtures'),
(4, 'tables',	'TABLE is an official fixture type');

INSERT INTO dbo.strat_merchcatg_fixture
(plan_id,strategy_id,rpt_lvl_0_nbr,rpt_lvl_1_nbr,rpt_lvl_2_nbr,rpt_lvl_3_nbr,fixturetype_rollup_id,min_nbr_per_type,max_nbr_per_type,min_present_unit_qty,max_present_unit_qty)
VALUES
(1, 3, 50000, 34, 1488, 9074, 1, 0.5, 1, null, null),
(1, 3, 50000, 34, 1488, 9074, 2, 0.5, 1, null, null),
(1, 3, 50000, 34, 1488, 9074, 3, 0.5, 1, null, null),
(2, 3, 50000, 34, 1489, 9073, 3, 0.5, 1, null, null),
(1, 5, 50000, 34, 1488, 9074, 1, 0.5, 1, 100, 120),
(1, 5, 50000, 34, 1488, 9074, 2, 0.5, 1, 100, 120),
(1, 5, 50000, 34, 1488, 9074, 3, 0.5, 1, 100, 120),
(3, 3, 50000, 23, 3669, 7506, 1, 0.5, 1, 100, 120),
(3, 3, 50000, 23, 3669, 8244, 1, 0.5, 1, 100, 120);


INSERT INTO dbo.strat_subcatg_fixture
(plan_id,strategy_id,rpt_lvl_0_nbr,rpt_lvl_1_nbr,rpt_lvl_2_nbr,rpt_lvl_3_nbr,rpt_lvl_4_nbr,fixturetype_rollup_id,min_nbr_per_type,max_nbr_per_type,min_present_unit_qty,max_present_unit_qty)
VALUES
(1, 3, 50000, 34, 1488, 9074, 7204, 1, 0.5, 1, null, null),
(1, 3, 50000, 34, 1488, 9074, 7204, 2, 0.5, 1, null, null),
(1, 3, 50000, 34, 1488, 9074, 7204, 3, 0.5, 1, null, null),
(2, 3, 50000, 34, 1489, 9073, 7203, 3, 0.5, 1, null, null),
(1, 5, 50000, 34, 1488, 9074, 7204, 1, 0.5, 1, 100, 120),
(1, 5, 50000, 34, 1488, 9074, 7204, 2, 0.5, 1, 100, 120),
(1, 5, 50000, 34, 1488, 9074, 7204, 3, 0.5, 1, 100, 120),
(3, 3, 50000, 23, 3669, 7506, 16890, 1, 0.5, 1, 100, 120),
(3, 3, 50000, 23, 3669, 8244, 16891, 1, 0.5, 1, 100, 120),
(3, 3, 50000, 23, 3669, 8244, 16893, 1, 0.5, 1, 100, 120);

INSERT INTO dbo.strat_fl_fixture
(plan_id,strategy_id,rpt_lvl_0_nbr,rpt_lvl_1_nbr,rpt_lvl_2_nbr,rpt_lvl_3_nbr,rpt_lvl_4_nbr,fineline_nbr,
 fixturetype_rollup_id,adj_below_min_pct,adj_above_max_pct,min_rollup_type_pct,max_rollup_type_pct,fixture_group_min_cnt,
 fixture_group_max_cnt,max_cc_per_type,min_present_unit_qty,max_present_unit_qty)
VALUES
(1, 3, 50000, 34, 1488, 9074, 7204, 465, 1, 0.125, 0.25, 0.125, 0.25, 5, 10, 3, null, null),
(1, 3, 50000, 34, 1488, 9074, 7204, 465, 2, 0.125, 0.25, 0.125, 0.25, 5, 10, 3, null, null),
(1, 3, 50000, 34, 1488, 9074, 7204, 465, 3, 0.125, 0.25, 0.125, 0.25, 5, 10, 3, null, null),
(1, 3, 50000, 34, 1488, 9074, 7204, 123, 1, 0.125, 0.25, 0.125, 0.25, 5, 10, 3, null, null),
(1, 3, 50000, 34, 1488, 9074, 7204, 123, 2, 0.125, 0.25, 0.125, 0.25, 5, 10, 3, null, null),
(1, 3, 50000, 34, 1488, 9074, 7204, 123, 3, 0.125, 0.25, 0.125, 0.25, 5, 10, 3, null, null),
(1, 3, 50000, 34, 1488, 9074, 7204, 4567, 1, 0.125, 0.25, 0.125, 0.25, 5, 10, 3, null, null),
(1, 3, 50000, 34, 1488, 9074, 7204, 4567, 2, 0.125, 0.25, 0.125, 0.25, 5, 10, 3, null, null),
(1, 3, 50000, 34, 1488, 9074, 7204, 4567, 3, 0.125, 0.25, 0.125, 0.25, 5, 10, 3, null, null),
(2, 3, 50000, 34, 1489, 9073, 7203, 123, 3, 0.125, 0.25, 0.125, 0.25, 5, 10, 3, null, null),
(1, 5, 50000, 34, 1488, 9074, 7204, 465, 1, 0.125, 0.25, 0.125, 0.25, 5, 10, 3, 100, 120),
(1, 5, 50000, 34, 1488, 9074, 7204, 465, 2, 0.125, 0.25, 0.125, 0.25, 5, 10, 3, 100, 120),
(1, 5, 50000, 34, 1488, 9074, 7204, 465, 3, 0.125, 0.25, 0.125, 0.25, 5, 10, 3, 100, 120),
(3, 3, 50000, 23, 3669, 7506, 16890, 399, 1, 0.125, 0.25, 0.125, 0.25, 5, 10, 3, 100, 120),
(3, 3, 50000, 23, 3669, 7506, 16890, 473, 1, 0.125, 0.25, 0.125, 0.25, 5, 10, 3, 100, 120),
(3, 3, 50000, 23, 3669, 8244, 16891, 190, 1, 0.125, 0.25, 0.125, 0.25, 5, 10, 3, 100, 120),
(3, 3, 50000, 23, 3669, 8244, 16891, 329, 1, 0.125, 0.25, 0.125, 0.25, 5, 10, 3, 100, 120),
(3, 3, 50000, 23, 3669, 8244, 16893, 151, 1, 0.125, 0.25, 0.125, 0.25, 5, 10, 3, 100, 120),
(3, 3, 50000, 23, 3669, 8244, 16893, 232, 1, 0.125, 0.25, 0.125, 0.25, 5, 10, 3, 100, 120);


INSERT INTO dbo.strat_style_fixture
(plan_id,strategy_id,rpt_lvl_0_nbr,rpt_lvl_1_nbr,rpt_lvl_2_nbr,rpt_lvl_3_nbr,rpt_lvl_4_nbr,fineline_nbr,style_nbr,fixturetype_rollup_id)
VALUES
(1, 3, 50000, 34, 1488, 9074, 7204, 465, 'test_style', 1),
(1, 3, 50000, 34, 1488, 9074, 7204, 465, 'test_style', 2),
(1, 3, 50000, 34, 1488, 9074, 7204, 465, 'test_style', 3),
(1, 3, 50000, 34, 1488, 9074, 7204, 4567, 'test_style_4567', 1),
(1, 3, 50000, 34, 1488, 9074, 7204, 4567, 'test_style_4567', 2),
(1, 3, 50000, 34, 1488, 9074, 7204, 4567, 'test_style_4567', 3),
(1, 3, 50000, 34, 1488, 9074, 7204, 4567, 'test_style_4567_1', 2),
(1, 3, 50000, 34, 1488, 9074, 7204, 4567, 'test_style_4567_1', 3);

INSERT INTO dbo.strat_cc_fixture
(plan_id,strategy_id,rpt_lvl_0_nbr,rpt_lvl_1_nbr,rpt_lvl_2_nbr,rpt_lvl_3_nbr,rpt_lvl_4_nbr,fineline_nbr,style_nbr,customer_choice,fixturetype_rollup_id,adj_max_cc_type_pct)
VALUES
(1, 3, 50000, 34, 1488, 9074, 7204, 465, 'test_style', 'test_cc', 1, 33.33),
(1, 3, 50000, 34, 1488, 9074, 7204, 465, 'test_style', 'test_cc', 2, 33.33),
(1, 3, 50000, 34, 1488, 9074, 7204, 465, 'test_style', 'test_cc', 3, 33.33),
(1, 3, 50000, 34, 1488, 9074, 7204, 4567, 'test_style_4567', 'test_cc_4567', 1, 33.33),
(1, 3, 50000, 34, 1488, 9074, 7204, 4567, 'test_style_4567', 'test_cc_4567', 2, 33.33),
(1, 3, 50000, 34, 1488, 9074, 7204, 4567, 'test_style_4567', 'test_cc_4567', 3, 33.33),
(1, 3, 50000, 34, 1488, 9074, 7204, 4567, 'test_style_4567', 'test_cc_4567_1', 1, 33.33),
(1, 3, 50000, 34, 1488, 9074, 7204, 4567, 'test_style_4567', 'test_cc_4567_1', 2, 33.33),
(1, 3, 50000, 34, 1488, 9074, 7204, 4567, 'test_style_4567', 'test_cc_4567_1', 3, 33.33),
(1, 3, 50000, 34, 1488, 9074, 7204, 4567, 'test_style_4567_1', 'test_cc_4567_1_1', 2, 33.33),
(1, 3, 50000, 34, 1488, 9074, 7204, 4567, 'test_style_4567_1', 'test_cc_4567_1_1', 3, 33.33);

INSERT INTO dbo.strat_clus
(strategy_id, analytics_cluster_id, analytics_cluster_label)
VALUES
(1, 0, 'all'),
(1, 1, 'cluster 1'),
(1, 2, 'cluster 2'),
(1, 3, 'cluster 3'),
(1, 4, 'cluster 4');

CREATE TABLE IF NOT EXISTS dbo.channel_text(
	channel_id tinyint NOT NULL,
	channel_desc varchar(15) NOT NULL,
CONSTRAINT pl1_chtxt PRIMARY KEY
	(channel_id) );

INSERT INTO dbo.channel_text
(channel_id, channel_desc)
VALUES
(1, 'store'),
(2, 'online'),
(3, 'omni');

CREATE TABLE IF NOT EXISTS dbo.strat_merchcatg_sp_clus(
	plan_id bigint NOT NULL,
	strategy_id bigint NOT NULL,
	analytics_cluster_id int NOT NULL,
	rpt_lvl_0_nbr int NOT NULL,
	rpt_lvl_1_nbr int NOT NULL,
	rpt_lvl_2_nbr int NOT NULL,
	rpt_lvl_3_nbr int NOT NULL,
	channel_id tinyint NOT NULL,
	analytics_sp_pct decimal (7, 3) NULL,
	merchant_override_sp_pct decimal (7, 3) NULL,
	size_profile_obj nvarchar (max) NULL,
	CONSTRAINT st1_merchcatgspclus PRIMARY KEY ( plan_id,strategy_id ,analytics_cluster_id ,rpt_lvl_0_nbr ,rpt_lvl_1_nbr,rpt_lvl_2_nbr ,rpt_lvl_3_nbr ,channel_id )
);
ALTER TABLE dbo.strat_merchcatg_sp_clus  ADD  CONSTRAINT FK_smscl1 FOREIGN KEY(plan_id, strategy_id, analytics_cluster_id)
REFERENCES dbo.plan_strat_clus (plan_id, strategy_id, analytics_cluster_id);

ALTER TABLE dbo.strat_merchcatg_sp_clus  ADD  CONSTRAINT FK_smscl2 FOREIGN KEY(channel_id)
REFERENCES dbo.channel_text (channel_id);


CREATE TABLE IF NOT EXISTS dbo.strat_subcatg_sp_clus(
	plan_id bigint NOT NULL,
	strategy_id bigint NOT NULL,
	analytics_cluster_id int NOT NULL,
	rpt_lvl_0_nbr int NOT NULL,
	rpt_lvl_1_nbr int NOT NULL,
	rpt_lvl_2_nbr int NOT NULL,
	rpt_lvl_3_nbr int NOT NULL,
	rpt_lvl_4_nbr int NOT NULL,
	channel_id tinyint NOT NULL,
	analytics_sp_pct decimal(7, 3) NULL,
	merchant_override_sp_pct decimal (7, 3) NULL,
	size_profile_obj nvarchar (max) NULL,
    CONSTRAINT st1_subcatgspclus PRIMARY KEY ( plan_id ,strategy_id ,analytics_cluster_id ,rpt_lvl_0_nbr ,rpt_lvl_1_nbr ,rpt_lvl_2_nbr ,rpt_lvl_3_nbr ,rpt_lvl_4_nbr ,channel_id )
);
ALTER TABLE dbo.strat_subcatg_sp_clus  ADD  CONSTRAINT FK_ssscl1 FOREIGN KEY(plan_id, strategy_id, analytics_cluster_id, rpt_lvl_0_nbr, rpt_lvl_1_nbr, rpt_lvl_2_nbr, rpt_lvl_3_nbr, channel_id)
REFERENCES dbo.strat_merchcatg_sp_clus (plan_id, strategy_id, analytics_cluster_id, rpt_lvl_0_nbr, rpt_lvl_1_nbr, rpt_lvl_2_nbr, rpt_lvl_3_nbr, channel_id);


CREATE TABLE IF NOT EXISTS dbo.strat_fl_sp_clus(
	plan_id bigint NOT NULL,
	strategy_id bigint NOT NULL,
	analytics_cluster_id int NOT NULL,
	rpt_lvl_0_nbr int NOT NULL,
	rpt_lvl_1_nbr int NOT NULL,
	rpt_lvl_2_nbr int NOT NULL,
	rpt_lvl_3_nbr int NOT NULL,
	rpt_lvl_4_nbr int NOT NULL,
	fineline_nbr smallint NOT NULL,
	channel_id tinyint NOT NULL,
	analytics_sp_pct decimal(7, 3) NULL,
	merchant_override_sp_pct decimal(7, 3) NULL,
	size_profile_obj nvarchar(max) NULL,
	CONSTRAINT st1_flspclus PRIMARY KEY (
	plan_id ASC,
	strategy_id ASC,
	analytics_cluster_id ASC,
	rpt_lvl_0_nbr ASC,
	rpt_lvl_1_nbr ASC,
	rpt_lvl_2_nbr ASC,
	rpt_lvl_3_nbr ASC,
	rpt_lvl_4_nbr ASC,
	fineline_nbr ASC,
	channel_id ASC)
);
ALTER TABLE dbo.strat_fl_sp_clus ADD  CONSTRAINT FK_sfsc1 FOREIGN KEY(plan_id, strategy_id, analytics_cluster_id, rpt_lvl_0_nbr, rpt_lvl_1_nbr, rpt_lvl_2_nbr, rpt_lvl_3_nbr, rpt_lvl_4_nbr, channel_id)
REFERENCES dbo.strat_subcatg_sp_clus (plan_id, strategy_id, analytics_cluster_id, rpt_lvl_0_nbr, rpt_lvl_1_nbr, rpt_lvl_2_nbr, rpt_lvl_3_nbr, rpt_lvl_4_nbr, channel_id);

CREATE TABLE IF NOT EXISTS dbo.strat_style_sp_clus(
	plan_id bigint NOT NULL,
	strategy_id bigint NOT NULL,
	analytics_cluster_id int NOT NULL,
	rpt_lvl_0_nbr int NOT NULL,
	rpt_lvl_1_nbr int NOT NULL,
	rpt_lvl_2_nbr int NOT NULL,
	rpt_lvl_3_nbr int NOT NULL,
	rpt_lvl_4_nbr int NOT NULL,
	fineline_nbr smallint NOT NULL,
	style_nbr char(50) NOT NULL,
	channel_id tinyint NOT NULL,
	analytics_sp_pct decimal(7, 3) NULL,
	merchant_override_sp_pct decimal(7, 3) NULL,
	size_profile_obj nvarchar(max) NULL,
CONSTRAINT st1_stylespclus PRIMARY KEY (plan_id ,strategy_id ,analytics_cluster_id ,rpt_lvl_0_nbr ,rpt_lvl_1_nbr ,rpt_lvl_2_nbr ,rpt_lvl_3_nbr ,rpt_lvl_4_nbr ,fineline_nbr ,style_nbr ,channel_id )
);
ALTER TABLE dbo.strat_style_sp_clus ADD  CONSTRAINT FK_sssc11 FOREIGN KEY(plan_id, strategy_id, analytics_cluster_id, rpt_lvl_0_nbr, rpt_lvl_1_nbr, rpt_lvl_2_nbr, rpt_lvl_3_nbr, rpt_lvl_4_nbr, fineline_nbr, channel_id)
REFERENCES dbo.strat_fl_sp_clus (plan_id, strategy_id, analytics_cluster_id, rpt_lvl_0_nbr, rpt_lvl_1_nbr, rpt_lvl_2_nbr, rpt_lvl_3_nbr, rpt_lvl_4_nbr, fineline_nbr, channel_id);

CREATE TABLE IF NOT EXISTS dbo.strat_cc_sp_clus(
	plan_id bigint NOT NULL,
	strategy_id bigint NOT NULL,
	analytics_cluster_id int NOT NULL,
	rpt_lvl_0_nbr int NOT NULL,
	rpt_lvl_1_nbr int NOT NULL,
	rpt_lvl_2_nbr int NOT NULL,
	rpt_lvl_3_nbr int NOT NULL,
	rpt_lvl_4_nbr int NOT NULL,
	fineline_nbr smallint NOT NULL,
	style_nbr char(50) NOT NULL,
	customer_choice char(100) NOT NULL,
	channel_id tinyint NOT NULL,
	analytics_sp_pct decimal(7, 3) NULL,
	merchant_override_sp_pct decimal(7, 3) NULL,
	size_profile_obj nvarchar(max) NULL,
CONSTRAINT st1_ccspclus PRIMARY KEY(plan_id ,strategy_id ,analytics_cluster_id ,rpt_lvl_0_nbr ,rpt_lvl_1_nbr ,rpt_lvl_2_nbr ,rpt_lvl_3_nbr ,rpt_lvl_4_nbr ,fineline_nbr ,style_nbr ,customer_choice ,channel_id )
);
ALTER TABLE dbo.strat_cc_sp_clus ADD CONSTRAINT FK_scsc1 FOREIGN KEY(plan_id, strategy_id, analytics_cluster_id, rpt_lvl_0_nbr, rpt_lvl_1_nbr, rpt_lvl_2_nbr, rpt_lvl_3_nbr, rpt_lvl_4_nbr, fineline_nbr, style_nbr, channel_id)
REFERENCES dbo.strat_style_sp_clus (plan_id, strategy_id, analytics_cluster_id, rpt_lvl_0_nbr, rpt_lvl_1_nbr, rpt_lvl_2_nbr, rpt_lvl_3_nbr, rpt_lvl_4_nbr, fineline_nbr, style_nbr, channel_id);

