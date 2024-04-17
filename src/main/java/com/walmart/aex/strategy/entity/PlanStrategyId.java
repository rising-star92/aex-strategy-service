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
public class PlanStrategyId implements Serializable {
    @Column(name = "plan_id", nullable = false)
    private Long planId;

    @Column(name = "strategy_id", nullable = false)
    private Long strategyId;
}
