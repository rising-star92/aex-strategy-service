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
public class StrategyFlClusPrgmEligRankingId implements Serializable{
    @Embedded
    private StrategyFlClusEligRankingId strategyFlClusEligRankingId;

    @Column(name = "program_id", nullable = false)
    private Long programId;
}
