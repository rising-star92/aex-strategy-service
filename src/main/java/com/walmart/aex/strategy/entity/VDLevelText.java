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
@Table(name = "vd_level_text", schema = "dbo")
public class VDLevelText{

    @Id
    @Column(name = "vd_level_code", nullable = false)
    private Integer vdLevelCode;

    @Column(name = "vd_level_desc", nullable = false)
    private String vdLevelDesc;
}
