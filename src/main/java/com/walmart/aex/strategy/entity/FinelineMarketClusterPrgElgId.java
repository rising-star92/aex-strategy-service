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
public class FinelineMarketClusterPrgElgId implements Serializable {
    @Embedded
    private StrategyFlClusPrgmEligRankingId strategyFlClusPrgmEligRankingId;

    @Column(name = "market_select_code", nullable = false)
    private Integer marketSelectCode;
}
