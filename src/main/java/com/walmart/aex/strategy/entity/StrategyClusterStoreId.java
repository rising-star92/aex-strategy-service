package com.walmart.aex.strategy.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
@EqualsAndHashCode
public class StrategyClusterStoreId implements Serializable {

    @Column(name = "strategy_id", nullable = false)
    private Long strategyId;

    @Column(name = "analytics_cluster_id", nullable = false)
    private Integer analyticsClusterId;

    @Column(name = "store_nbr", nullable = false)
    private Integer storeNbr;
}
