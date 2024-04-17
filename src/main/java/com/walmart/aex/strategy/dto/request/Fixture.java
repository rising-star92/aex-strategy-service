package com.walmart.aex.strategy.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Fixture {
    private BigDecimal defaultMinCap;
    private BigDecimal defaultMaxCap;
    private Integer orderPref;
    private String type;
    private BigDecimal belowMin;
    private BigDecimal belowMax;
    private Integer fgStart;
    private Integer fgEnd;
    private BigDecimal fgMin;
    private BigDecimal fgMax;
    private Integer maxCcs;
    private BigDecimal adjMaxCc;
}
