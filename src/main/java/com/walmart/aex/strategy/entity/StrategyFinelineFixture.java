package com.walmart.aex.strategy.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Set;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "strat_fl_fixture", schema = "dbo")
public class StrategyFinelineFixture {

    @EmbeddedId
    @EqualsAndHashCode.Include
    private StrategyFinelineFixtureId strategyFinelineFixtureId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", referencedColumnName = "plan_id", nullable = false, insertable = false, updatable = false)
    @JoinColumn(name = "strategy_id", referencedColumnName = "strategy_id", nullable = false, insertable = false, updatable = false)
    @JoinColumn(name = "rpt_lvl_0_nbr", referencedColumnName = "rpt_lvl_0_nbr", nullable = false, insertable = false, updatable = false)
    @JoinColumn(name = "rpt_lvl_1_nbr", referencedColumnName = "rpt_lvl_1_nbr", nullable = false, insertable = false, updatable = false)
    @JoinColumn(name = "rpt_lvl_2_nbr", referencedColumnName = "rpt_lvl_2_nbr", nullable = false, insertable = false, updatable = false)
    @JoinColumn(name = "rpt_lvl_3_nbr", referencedColumnName = "rpt_lvl_3_nbr", nullable = false, insertable = false, updatable = false)
    @JoinColumn(name = "rpt_lvl_4_nbr", referencedColumnName = "rpt_lvl_4_nbr", nullable = false, insertable = false, updatable = false)
    @JoinColumn(name = "fixturetype_rollup_id", referencedColumnName = "fixturetype_rollup_id", nullable = false, insertable = false, updatable = false)
    @JsonIgnore
    private StrategySubCatgFixture strategySubCatgFixture;

    @OneToMany(mappedBy = "strategyFinelineFixture", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<StrategyFinelineFixtureRank> strategyFinelineFixtureRanks;

    @Column(name = "adj_below_min_pct")
    private BigDecimal adjBelowMinFixturesPerFineline;

    @Column(name = "adj_above_max_pct")
    private BigDecimal adjBelowMaxFixturesPerFineline;

    @Column(name = "min_rollup_type_pct")
    private BigDecimal adjMinFixturesPerFineline;

    @Column(name = "max_rollup_type_pct")
    private BigDecimal adjMaxFixturesPerFineline;

    //Not Needed
    @Column(name = "min_type_per_cc_pct")
    private BigDecimal minFixturesPerCc;

    //Not Needed
    @Column(name = "max_type_per_cc_pct")
    private BigDecimal maxFixturesPerCc;

    @Column(name = "fixture_group_min_cnt")
    private Integer minFixtureGroup;

    @Column(name = "fixture_group_max_cnt")
    private Integer maxFixtureGroup;

    @Column(name = "min_present_unit_qty")
    private Integer minPresentationUnits;

    @Column(name = "max_present_unit_qty")
    private Integer maxPresentationUnits;

    @Column(name = "max_cc_per_type")
    private Integer maxCcsPerFixture;
    //Not Needed
    @Column(name = "adj_max_cc_per_type")
    private Integer adjMaxCcsPerFixture;

    @Column(name = "merch_method_code")
    private Integer merchMethodCode;

    @OneToMany(mappedBy = "strategyFinelineFixture", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<StrategyStyleFixture> strategyStyleFixtures;
}
