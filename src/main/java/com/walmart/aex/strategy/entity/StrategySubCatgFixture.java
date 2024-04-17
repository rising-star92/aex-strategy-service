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
@Table(name = "strat_subcatg_fixture", schema = "dbo")
public class StrategySubCatgFixture {

    @EmbeddedId
    @EqualsAndHashCode.Include
    private StrategySubCatgFixtureId strategySubCatgFixtureId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", referencedColumnName = "plan_id", nullable = false, insertable = false, updatable = false)
    @JoinColumn(name = "strategy_id", referencedColumnName = "strategy_id", nullable = false, insertable = false, updatable = false)
    @JoinColumn(name = "rpt_lvl_0_nbr", referencedColumnName = "rpt_lvl_0_nbr", nullable = false, insertable = false, updatable = false)
    @JoinColumn(name = "rpt_lvl_1_nbr", referencedColumnName = "rpt_lvl_1_nbr", nullable = false, insertable = false, updatable = false)
    @JoinColumn(name = "rpt_lvl_2_nbr", referencedColumnName = "rpt_lvl_2_nbr", nullable = false, insertable = false, updatable = false)
    @JoinColumn(name = "rpt_lvl_3_nbr", referencedColumnName = "rpt_lvl_3_nbr", nullable = false, insertable = false, updatable = false)
    @JoinColumn(name = "fixturetype_rollup_id", referencedColumnName = "fixturetype_rollup_id", nullable = false, insertable = false, updatable = false)
    @JsonIgnore
    private StrategyMerchCatgFixture strategyMerchCatgFixture;

    @Column(name = "min_nbr_per_type")
    private BigDecimal minFixturesPerFineline;

    @Column(name = "max_nbr_per_type")
    private BigDecimal maxFixturesPerFineline;

    @Column(name = "min_present_unit_qty")
    private Integer minPresentationUnits;

    @Column(name = "max_present_unit_qty")
    private Integer maxPresentationUnits;

    @Column(name = "max_cc_per_type")
    private Integer maxCcsPerFixture;

    @Column(name = "merch_method_code")
    private Integer merchTypeCode;

    @OneToMany(mappedBy = "strategySubCatgFixture", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<StrategyFinelineFixture> strategyFinelineFixtures;
}
