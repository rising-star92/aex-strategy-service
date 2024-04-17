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
@Table(name = "strat_merchcatg", schema = "dbo")
public class StrategyMerchCatg {

    @EmbeddedId
    @EqualsAndHashCode.Include
    private StrategyMerchCatgId strategyMerchCatgId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", referencedColumnName = "plan_id", nullable = false, insertable = false, updatable = false)
    @JoinColumn(name = "strategy_id", referencedColumnName = "strategy_id", nullable = false, insertable = false, updatable = false)
    @JsonIgnore
    private PlanStrategy planStrategy;

    @Column(name = "channel_id")
    private Integer channelId;

    @OneToMany(mappedBy = "strategyMerchCatg", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CategoryLineplan> categoryLineplan;

    @OneToMany(mappedBy = "strategyMerchCatg", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<StrategySubCatg> strategySubCatgs;

    @OneToMany(mappedBy = "strategyMerchCatg", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<StrategyMerchCatgFixture> strategyMerchCatgFixtures;
}
