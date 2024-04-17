package com.walmart.aex.strategy.entity;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
@EqualsAndHashCode
public class StrategyStyleClusId implements Serializable {
    @Embedded
    private StrategyFlClusEligRankingId strategyFlClusEligRankingId;

    @Column(name = "style_nbr", nullable = false)
    @Convert(converter = CharConverter.class)
    private String styleNbr;
}
