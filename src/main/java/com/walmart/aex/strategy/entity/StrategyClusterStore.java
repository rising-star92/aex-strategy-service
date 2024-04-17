package com.walmart.aex.strategy.entity;

import lombok.*;

import javax.persistence.*;

@Getter
@Entity
@Table(name = "strat_clus_store", schema = "dbo")
public class StrategyClusterStore {

    @EmbeddedId
    @EqualsAndHashCode.Include
    private StrategyClusterStoreId strategyClusterStoreId;

    @Column(name = "country_code")
    private String countryCode;

    @Column(name = "state_province_code")
    private String stateProvinceCode;
}
