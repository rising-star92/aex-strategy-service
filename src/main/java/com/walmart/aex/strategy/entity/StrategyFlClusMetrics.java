package com.walmart.aex.strategy.entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import javax.persistence.*;
import java.math.BigDecimal;


@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "elig_fl_clus_metrics", schema = "dbo")
public class StrategyFlClusMetrics {

    @EmbeddedId
    @EqualsAndHashCode.Include
    private StrategyFlClusEligRankingId strategyFlClusEligRankingId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "plan_id", referencedColumnName = "plan_id", nullable = false),
            @JoinColumn(name = "strategy_id", referencedColumnName = "strategy_id", nullable = false),
            @JoinColumn(name = "analytics_cluster_id", referencedColumnName = "analytics_cluster_id", nullable = false),
            @JoinColumn(name = "rpt_lvl_0_nbr", referencedColumnName = "rpt_lvl_0_nbr", nullable = false),
            @JoinColumn(name = "rpt_lvl_1_nbr", referencedColumnName = "rpt_lvl_1_nbr", nullable = false),
            @JoinColumn(name = "rpt_lvl_2_nbr", referencedColumnName = "rpt_lvl_2_nbr", nullable = false),
            @JoinColumn(name = "rpt_lvl_3_nbr", referencedColumnName = "rpt_lvl_3_nbr", nullable = false),
            @JoinColumn(name = "rpt_lvl_4_nbr", referencedColumnName = "rpt_lvl_4_nbr", nullable = false),
            @JoinColumn(name = "fineline_nbr", referencedColumnName = "fineline_nbr", nullable = false)
    })
    @JsonIgnore
    private StrategyFlClusEligRanking strategyFlClusEligRanking;

    @Column(name = "sales_dollars")
    private BigDecimal salesDollars;

    @Column(name = "sales_units")
    private Integer salesUnits;

    @Column(name = "forecasted_units")
    private Integer forecastedUnits;

    @Column(name = "forecasted_dollars")
    private BigDecimal forecastedDollars;

    @Column(name = "on_hand_qty")
    private Integer onHandQty;

    @Column(name = "sales_to_stock_ratio")
    private BigDecimal salesToStockRatio;

    @Column(name = "analytics_cluster_rank")
    private Integer analyticsClusterRank ;
}
