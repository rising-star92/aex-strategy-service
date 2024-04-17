package com.walmart.aex.strategy.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "market_selections", schema = "dbo")
public class MarketSelection {

    @Id
    @Column(name = "market_select_code", nullable = false)
    private Integer marketSelectCode;

    @Column(name = "market_value")
    private String marketValue;

    @Column(name = "market_desc", nullable = false)
    private String marketDesc;

}
