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
public class StrategyFinelineId implements Serializable {

    @Embedded
    private StrategySubCatgId strategySubCatgId;

    @Column(name = "fineline_nbr", nullable = false)
    private Integer finelineNbr;
}
