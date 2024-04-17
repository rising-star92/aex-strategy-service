package com.walmart.aex.strategy.dto.request;

import lombok.Data;

@Data
public class FiscalWeek {
    private Integer wmYearWeek;
    private Integer dwWeekId;
    private String fiscalWeekDesc;
}
