package com.walmart.aex.strategy.entity;

import java.io.Serializable;

import lombok.*;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
@EqualsAndHashCode
public class StrategyCcId implements Serializable {
    @Embedded
    private StrategyStyleId strategyStyleId;

    @Column(name = "customer_choice", nullable = false)
    @Convert(converter = CharConverter.class)
    private String ccId;
}
