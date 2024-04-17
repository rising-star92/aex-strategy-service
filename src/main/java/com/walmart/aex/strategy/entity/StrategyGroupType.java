package com.walmart.aex.strategy.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "strat_group_type", schema = "dbo")
public class StrategyGroupType {
    @Id
    @Column(name = "strategy_group_type_code", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer strategyGroupTypeId;

    @Column(name = "strategy_group_desc")
    private String strategyGroupDesc;

    @Column(name = "detailed_desc")
    private String detailedDesc;

    @OneToMany(mappedBy = "strategyGroupType", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<StrategyGroup> strategyGroups;
}
