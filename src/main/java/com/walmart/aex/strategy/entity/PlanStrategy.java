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
@Table(name = "plan_strategy", schema = "dbo")
public class PlanStrategy {

    @EmbeddedId
    @EqualsAndHashCode.Include
    private PlanStrategyId planStrategyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "strategy_id", referencedColumnName = "strategy_id", nullable = false, insertable = false, updatable = false)
    @JsonIgnore
    private StrategyGroup strategyGroup;

    @OneToMany(mappedBy = "planStrategy", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PlanClusterStrategy> planClusterStrategies;

    @OneToMany(mappedBy = "planStrategy", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<StrategyMerchCatg> strategyMerchCatgs;

}
