
create schema if not exists dbo;

create table if not exists dbo.program_csa_space_inbound (
   csa_space_inbound_id bigint not null,
   csa_season_code char(2) not null,
   fiscal_year smallint not null,
   csa_space_season_code int not null,
   csa_space_season_desc char(4) null,
   modular_dept_nbr smallint not null,
   csa_start_yr_wk_ccyyww int not null,
   csa_end_yr_wk_ccyyww int not null,
   csa_space_file_name nvarchar(255) not null,
   create_userid varchar(15) null,
   create_ts timestamp(7) null,
   last_modified_userid varchar(15) null,
   last_modified_ts timestamp(7) null
);



create table if not exists dbo.program (
     program_id bigint not null,
     csa_space_inbound_id bigint not null,
     aex_program_name nvarchar(160) not null,
     trait_nbr int not null,
     merch_dept_nbr smallint not null,
     trait_last_maint_ts timestamp(7) null,
     trait_last_maint_userid char(8) null,
     trait_short_desc nvarchar(60) not null,
     trait_long_desc nvarchar(510) not null,
     pgm_create_userid char(35) null,
     pgm_last_change_userid char(35) null,
     pgm_create_ts timestamp(7) null,
     pgm_last_change_ts timestamp(7) null,
     program_err_msg nvarchar(255) null,
     acct_dept_nbr smallint null,
     rfa_rerun_status_code tinyint null
);

alter table dbo.program_csa_space_inbound add primary key (csa_space_inbound_id);
alter table dbo.program add primary key (program_id, csa_space_inbound_id);
alter table dbo.program add constraint FK_pc1 foreign key (csa_space_inbound_id) references dbo.program_csa_space_inbound (csa_space_inbound_id);


create table dbo.program_mod_dept_catg (
                                               program_id bigint not null,
                                               csa_space_inbound_id bigint not null,
                                               modular_dept_nbr smallint not null,
                                               modular_catg_nbr smallint not null,
                                               modular_catg_desc nvarchar(40) null,
                                               merchant_email varchar(40) null,
                                               first_name nvarchar(40) null,
                                               last_name nvarchar(40) null,
                                               userid varchar(15) null,
                                               merchant_domain varchar(255) null,
                                               create_userid varchar(15) null,
                                               create_ts timestamp(7) null,
                                               last_modified_userid varchar(15) null,
                                               last_modified_ts timestamp(7) null
);
alter table dbo.program_mod_dept_catg add primary key (program_id, csa_space_inbound_id, modular_dept_nbr, modular_catg_nbr);
alter table dbo.program_mod_dept_catg add constraint FK_pmdcc1 foreign key (program_id, csa_space_inbound_id) references dbo.program (program_id, csa_space_inbound_id);


CREATE TABLE dbo.program_mod_dept_catg_fixture(
    program_id bigint NOT NULL,
    csa_space_inbound_id bigint NOT NULL,
    modular_dept_nbr smallint NOT NULL,
    modular_catg_nbr smallint NOT NULL,
    fixturetype_rollup_id smallint NOT NULL,
    create_userid varchar(15) NULL,
    create_ts timestamp(7) NULL,
    last_modified_userid varchar(15) NULL,
    last_modified_ts timestamp(7) NULL
    )
    GO
ALTER TABLE dbo.program_mod_dept_catg_fixture ADD PRIMARY KEY
    (
    program_id ASC,
    csa_space_inbound_id ASC,
    modular_dept_nbr ASC,
    modular_catg_nbr ASC,
    fixturetype_rollup_id ASC
    );
ALTER TABLE dbo.program_mod_dept_catg_fixture   ADD  CONSTRAINT FK_pmdccf1 FOREIGN KEY(program_id, csa_space_inbound_id, modular_dept_nbr, modular_catg_nbr)
    REFERENCES dbo.program_mod_dept_catg (program_id, csa_space_inbound_id, modular_dept_nbr, modular_catg_nbr)




CREATE SEQUENCE dbo.seq_ap_count
    MINVALUE 1
    MAXVALUE 999999999
    INCREMENT BY 1
    START WITH 1
    NOCACHE
    NOCYCLE;