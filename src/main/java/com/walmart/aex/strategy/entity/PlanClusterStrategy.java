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
@Table(name = "plan_strat_clus", schema = "dbo")
public class PlanClusterStrategy {

    @EmbeddedId
    @EqualsAndHashCode.Include
    private PlanClusterStrategyId planClusterStrategyId;

    @Column(name = "analytics_cluster_label")
    private String analyticsClusterLabel ;

    @Column(name = "detailed_analytics_cluster_desc")
    private String detailedAnalyticsClusterDesc ;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", referencedColumnName = "plan_id", nullable = false, insertable = false, updatable = false)
    @JoinColumn(name = "strategy_id", referencedColumnName = "strategy_id", nullable = false, insertable = false, updatable = false)
    @JsonIgnore
    private PlanStrategy planStrategy;

    @OneToMany(mappedBy = "planClusterStrategy", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<StrategyFlClusEligRanking> strategyFlClusEligRankings;

    @OneToMany(mappedBy = "planClusterStrategy", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<StrategyMerchCategorySPCluster> strategyMerchCategorySPCluster;

}
