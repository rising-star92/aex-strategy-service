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
public class StrategyFineLineSPClusId implements Serializable {
    @Embedded
    private StrategySubCatgSPClusId strategySubCatgSPClusId;

    @Column(name = "fineline_nbr", nullable = false)
    private Integer finelineNbr;
}
