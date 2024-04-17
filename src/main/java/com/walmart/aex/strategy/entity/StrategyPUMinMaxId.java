package com.walmart.aex.strategy.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Embeddable
@EqualsAndHashCode
public class StrategyPUMinMaxId implements Serializable {

    @Column(name = "rpt_lvl_0_nbr", nullable = false)
    private Integer lvl0Nbr;

    @Column(name = "rpt_lvl_1_nbr", nullable = false)
    private Integer lvl1Nbr;

    @Column(name = "rpt_lvl_2_nbr", nullable = false)
    private Integer lvl2Nbr;

    @Column(name = "rpt_lvl_3_nbr", nullable = false)
    private Integer lvl3Nbr;

    @Column(name = "rpt_lvl_4_nbr", nullable = false)
    private Integer lvl4Nbr;

    @Column(name = "fixturetype_rollup_id", nullable = false)
    private Integer fixtureTypeId;

    @Column(name = "ahs_asi_id", nullable = false)
    private Integer ahsAsiId;

    @Column(name = "ahs_v_id", nullable = false)
    private Integer ahsVId;
}
