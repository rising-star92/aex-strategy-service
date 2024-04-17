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
public class StrategyCcSPClusId implements Serializable {
    @Embedded
    private StrategyStyleSPClusId strategyStyleSPClusId;

    @Column(name = "customer_choice", nullable = false)
    @Convert(converter = CharConverter.class)
    private String ccId;
}
