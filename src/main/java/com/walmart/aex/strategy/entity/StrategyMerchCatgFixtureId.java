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
public class StrategyMerchCatgFixtureId implements Serializable {

    @Embedded
    private StrategyMerchCatgId strategyMerchCatgId;

    @Column(name = "fixturetype_rollup_id", nullable = false)
    private Integer fixtureTypeId;
}
