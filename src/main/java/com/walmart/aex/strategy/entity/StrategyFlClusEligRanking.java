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
@Table(name = "elig_fl_clus_rank", schema = "dbo")
public class StrategyFlClusEligRanking {

    @EmbeddedId
    @EqualsAndHashCode.Include
    private StrategyFlClusEligRankingId strategyFlClusEligRankingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", referencedColumnName = "plan_id", nullable = false, insertable = false, updatable = false)
    @JoinColumn(name = "strategy_id", referencedColumnName = "strategy_id", nullable = false, insertable = false, updatable = false)
    @JoinColumn(name = "analytics_cluster_id", referencedColumnName = "analytics_cluster_id", nullable = false, insertable = false, updatable = false)
    @JsonIgnore
    private PlanClusterStrategy planClusterStrategy;

    @Column(name = "fineline_desc")
    private String finelineDesc;

    @Column(name = "in_store_yr_wk")
    private Integer inStoreYrWk;

    @Column(name = "markdown_yr_wk")
    private Integer markDownYrWk;

    @Column(name = "in_store_yrwk_desc")
    private String inStoreYrWkDesc;

    @Column(name = "markdown_yrwk_desc")
    private String markDownYrWkDesc;

    @Column(name = "is_eligible", nullable = false)
    private Integer isEligible = 1;

    @Column(name = "select_status_id ", nullable = false)
    private Integer isEligibleFlag = 1;

    @Column(name = "merchant_override_rank")
    private Integer merchantOverrideRank;

    @Column(name = "store_cnt")
    private Integer storeCount;

    @OneToMany(mappedBy = "strategyFlClusEligRanking", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<StrategyStyleClus> strategyStyleCluses;

    @OneToMany(mappedBy = "strategyFlClusEligRanking", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<StrategyFlClusPrgmEligRanking> strategyFlClusPrgmEligRankings;

    @OneToOne(mappedBy = "strategyFlClusEligRanking", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private StrategyFlClusMetrics strategyFlClusMetrics;

    @OneToMany(mappedBy = "strategyFlClusEligRanking", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<StrategyFlMktClusElig> strategyFlMktClusEligs;
}
