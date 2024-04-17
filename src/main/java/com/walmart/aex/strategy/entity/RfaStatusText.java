package com.walmart.aex.strategy.entity;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Entity
@Table(name="run_status_text", schema = "dbo")
public class RfaStatusText {
    @Id
    @Column(name = "rfa_status_code", nullable = false)
    private Integer rfaStatusCode;

    @Column(name = "rfa_status_desc", nullable = false)
    private String rfaStatusDesc;
}
