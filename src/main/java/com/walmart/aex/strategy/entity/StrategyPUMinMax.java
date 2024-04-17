package com.walmart.aex.strategy.entity;

import javax.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "subcatg_minmax", schema = "dbo")
public class StrategyPUMinMax {

    @EmbeddedId
    private  StrategyPUMinMaxId strategyPUMinMaxId;

    @Column(name = "ahs_asi_attr_name", nullable = true)
    private String ahsAttributeName;

    @Column(name = "ash_v_value", nullable = true)
    private String ahsValue;

    @Column(name = "min_qty", nullable = true)
    private Integer minQty;

    @Column(name = "max_qty", nullable = true)
    private Integer maxQty;

}
