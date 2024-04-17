package com.walmart.aex.strategy.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import java.io.Serializable;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
@EqualsAndHashCode
public class StrategyMerchCategoryClusId implements Serializable {
    @Embedded
    private PlanStrategyId planStrategyId;

    @Column(name = "analytics_cluster_id", nullable = false)
    private Integer analyticsClusterId;

    @Column(name = "rpt_lvl_0_nbr", nullable = false)
    private Integer lvl0Nbr;

    @Column(name = "rpt_lvl_1_nbr", nullable = false)
    private Integer lvl1Nbr;

    @Column(name = "rpt_lvl_2_nbr", nullable = false)
    private Integer lvl2Nbr;

    @Column(name = "rpt_lvl_3_nbr", nullable = false)
    private Integer lvl3Nbr;
}
