package com.walmart.aex.strategy.entity;

import javax.persistence.Column;
import javax.persistence.Embedded;

public class StrategyFLVGCatgId{

    @Embedded
    private PlanStrategyId planStrategyId;

    @Column(name = "rpt_lvl_0_nbr", nullable = false)
    private Integer lvl0Nbr;

    @Column(name = "rpt_lvl_1_nbr", nullable = false)
    private Integer lvl1Nbr;

    @Column(name = "rpt_lvl_2_nbr", nullable = false)
    private Integer lvl2Nbr;

    @Column(name = "rpt_lvl_3_nbr", nullable = false)
    private Integer lvl3Nbr;

}
