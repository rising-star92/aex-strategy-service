package com.walmart.aex.strategy.entity;


import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import java.io.Serializable;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
@EqualsAndHashCode
@ToString
public class PlanClusterStrategyId implements Serializable {

    @Embedded
    private PlanStrategyId planStrategyId;

    @Column(name = "analytics_cluster_id", nullable = false)
    private Integer analyticsClusterId;
}
