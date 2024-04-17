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
public class StrategySubCatgSPClusId implements Serializable {
    @Embedded
    private StrategyMerchCatgSPClusId strategyMerchCatgSPClusId;

    @Column(name = "rpt_lvl_4_nbr", nullable = false)
    private Integer lvl4Nbr;

}
