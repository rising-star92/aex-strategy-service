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
public class StrategySubCatgId implements Serializable {

    @Embedded
    private StrategyMerchCatgId strategyMerchCatgId;

    @Column(name = "rpt_lvl_4_nbr", nullable = false)
    private Integer lvl4Nbr;
}
