package com.walmart.aex.strategy.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "strat_style_sp_clus", schema = "dbo")
public class StrategyStyleSPCluster implements Serializable {

    @EmbeddedId
    @EqualsAndHashCode.Include
    private StrategyStyleSPClusId strategyStyleSPClusId;

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
    @JoinColumn(name = "channel_id", referencedColumnName = "channel_id", nullable = false, insertable = false, updatable = false)

    @JsonIgnore
    private StrategyFineLineSPCluster strategyFinelineSPClus;

    @Column(name = "analytics_sp_pct")
    private String analyticsSPPercent;

    @Column(name = "merchant_override_sp_pct")
    private Integer merchantOverrideSPPercent;

    @Column(name = "size_profile_obj")
    private String sizeProfileObj;
    
    @Column(name = "calc_sp_spread_ind")
    private Integer calculateSpreadStatus;
    

    @OneToMany(mappedBy = "strategyStyleSPCluster", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<StrategyCcSPCluster> strategyCcSPClusters;
}
