package com.walmart.aex.strategy.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Convert;
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
public class StrategyStyleFixtureId implements Serializable {

    @Embedded
    private StrategyFinelineFixtureId strategyFinelineFixtureId;

    @Column(name = "style_nbr", nullable = false)
    @Convert(converter = CharConverter.class)
    private String styleNbr;
}
