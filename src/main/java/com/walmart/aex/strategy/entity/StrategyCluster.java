package com.walmart.aex.strategy.entity;


import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Entity
@Table(name = "strat_clus", schema = "dbo")
public class StrategyCluster {

    @EmbeddedId
    @EqualsAndHashCode.Include
    private StrategyClusterId strategyClusterId;

    @Column(name = "analytics_cluster_label", nullable = false)
    private String analyticsClusterLabel ;


}
