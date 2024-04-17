package com.walmart.aex.strategy.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
@EqualsAndHashCode
@ToString
public class StrategyCcMktClusEligId implements Serializable {
    @Embedded
    private StrategyCcClusEligRankingId strategyCcClusEligRankingId;

    @Column(name = "market_select_code", nullable = false)
    private Integer marketSelectCode;
}
