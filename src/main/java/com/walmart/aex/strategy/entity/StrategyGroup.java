package com.walmart.aex.strategy.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "strat_group", schema = "dbo")
public class StrategyGroup {
    @Id
    @Column(name = "strategy_id", nullable = false)
    private Long strategyId;

    @MapsId(value = "strategyGroupTypeId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "strategy_group_type_code", nullable = false)
    private StrategyGroupType strategyGroupType;

    @Column(name = "strategy_group_type_code")
    private Long strategyGroupTypeId;

    @Column(name = "analytics_cluster_group_desc")
    private String analyticsClusterGroupDesc;

    @Column(name = "analytics_season_desc")
    private String analyticsSeasonDesc;

    @Column(name = "detailed_analytics_desc")
    private String detailedAnalyticsDesc;

    @Column(name = "season_code")
    private String seasonCode;

    @Column(name = "fiscal_year")
    private Integer fiscalYear;

    @OneToMany(mappedBy = "strategyGroup", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PlanStrategy> planStrategies;

}
