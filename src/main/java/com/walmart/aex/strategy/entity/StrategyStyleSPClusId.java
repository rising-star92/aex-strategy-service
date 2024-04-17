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
public class StrategyStyleSPClusId implements Serializable {
    @Embedded
    private StrategyFineLineSPClusId strategyFinelineSPClusId;

    @Column(name = "style_nbr", nullable = false)
    @Convert(converter = CharConverter.class)
    private String styleNbr;
}
