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
public class StrategyFinelineFixtureRankId implements Serializable {

    @Embedded
    private StrategyFinelineFixtureId strategyFinelineFixtureId;

    @Column(name = "type_fl_rank", nullable = false)
    private Integer finelineFixtureRank;
}
