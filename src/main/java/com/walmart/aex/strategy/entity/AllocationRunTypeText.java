package com.walmart.aex.strategy.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name="alloc_run_type_text", schema = "dbo")
public class AllocationRunTypeText {
    @Id
    @Column(name = "alloc_run_type_code", nullable = false)
    private Integer allocRunTypeCode;

    @Column(name = "alloc_run_type_desc", nullable = false)
    private String allocRunTypeDesc;
}
