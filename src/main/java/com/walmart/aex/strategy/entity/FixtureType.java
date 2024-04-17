package com.walmart.aex.strategy.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "fixturetype_rollup", schema = "dbo")
public class FixtureType {
    @Id
    @Column(name = "fixturetype_rollup_id", nullable = false)
    private Integer fixtureTypeId;

    @Column(name = "fixturetype_rollup_name")
    private String fixtureTypeName;

    @Column(name = "fixturetype_rollup_desc", nullable = false)
    private String fixtureTypeDesc;

}
