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
@Table(name = "strat_merchcatg_clus", schema = "dbo")
public class StrategyMerchCatgCluster implements Serializable {

    @EmbeddedId
    @EqualsAndHashCode.Include
    private StrategyMerchCategoryClusId strategyMerchCategoryClusId;

    @OneToMany(mappedBy = "strategyMerchCatgClus", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<StrategySubCatgCluster> strategySubCatgClusters;

    @OneToMany(mappedBy = "strategyMerchCatgClus", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<StrategyMerchCategorySPCluster> strategyMerchCategorySPClusters;

}
