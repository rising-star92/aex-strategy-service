package com.walmart.aex.strategy.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
@EqualsAndHashCode
@Builder
@ToString
public class FpFinelineVDCategoryId implements Serializable{

    @Embedded
    private VdLevelCodeId vdLevelCodeId;

    @Column(name = "plan_id")
     private Long planId;

    @Column(name = "fineline_nbr")
    private Integer finelineNbr;

    @Column(name = "strategy_id")
    private Integer strategyId;

    @Column(name = "rpt_lvl_0_nbr")
    private Integer rptLvl0Nbr;

    @Column(name = "rpt_lvl_1_nbr")
    private Integer rptLvl1Nbr;

    @Column(name = "rpt_lvl_2_nbr")
    private Integer rptLvl2Nbr;

    @Column(name = "rpt_lvl_3_nbr")
    private Integer rptLvl3Nbr;

    @Column(name = "rpt_lvl_4_nbr")
    private Integer rptLvl4Nbr;

}
