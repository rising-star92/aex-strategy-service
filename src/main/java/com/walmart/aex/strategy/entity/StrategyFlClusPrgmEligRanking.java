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
@Table(name = "elig_fl_clus_prog", schema = "dbo")
public class StrategyFlClusPrgmEligRanking {

    @EmbeddedId
    @EqualsAndHashCode.Include
    private StrategyFlClusPrgmEligRankingId strategyFlClusPrgmEligRankingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", referencedColumnName = "plan_id", nullable = false, insertable = false, updatable = false)
    @JoinColumn(name = "strategy_id", referencedColumnName = "strategy_id", nullable = false, insertable = false, updatable = false)
    @JoinColumn(name = "analytics_cluster_id", referencedColumnName = "analytics_cluster_id", nullable = false, insertable = false, updatable = false)
    @JoinColumn(name = "rpt_lvl_0_nbr", referencedColumnName = "rpt_lvl_0_nbr", nullable = false, insertable = false, updatable = false)
    @JoinColumn(name = "rpt_lvl_1_nbr", referencedColumnName = "rpt_lvl_1_nbr", nullable = false, insertable = false, updatable = false)
    @JoinColumn(name = "rpt_lvl_2_nbr", referencedColumnName = "rpt_lvl_2_nbr", nullable = false, insertable = false, updatable = false)
    @JoinColumn(name = "rpt_lvl_3_nbr", referencedColumnName = "rpt_lvl_3_nbr", nullable = false, insertable = false, updatable = false)
    @JoinColumn(name = "rpt_lvl_4_nbr", referencedColumnName = "rpt_lvl_4_nbr", nullable = false, insertable = false, updatable = false)
    @JoinColumn(name = "fineline_nbr", referencedColumnName = "fineline_nbr", nullable = false, insertable = false, updatable = false)
    @JsonIgnore
    private StrategyFlClusEligRanking strategyFlClusEligRanking;

    @Column(name = "in_store_yr_wk")
    private Integer inStoreYrWk;

    @Column(name = "markdown_yr_wk")
    private Integer markDownYrWk;

    @Column(name = "in_store_yrwk_desc")
    private String inStoreYrWkDesc;

    @Column(name = "markdown_yrwk_desc")
    private String markDownYrWkDesc;

    @Column(name = "is_eligible", nullable = false)
    private Integer isEligible = 0;

    @Column(name = "select_status_id", nullable = false)
    private Integer isEligibleFlag = 0;

    @Column(name = "merchant_override_rank")
    private Integer merchantOverrideRank ;

    @Column(name = "store_cnt")
    private Integer storeCount ;

    @OneToMany(mappedBy = "strategyFlClusPrgmEligRanking", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<EligStyleClusProg> eligStyleClusProgs;

    @OneToMany(mappedBy = "strategyFlClusPrgmEligRanking", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<FinelineMarketClusterPrgElg> finelineMarketClusterPrgElgs;
}
