package com.walmart.aex.strategy.dto;

import lombok.Data;

@Data
public class StrategyCountResponse {
    private Long finelineOnlineTarget;
    private Long finelineStoreTarget;
    private Long ccOnlineTarget;
    private Long ccStoreTarget;

    private Long finelineOnlineActual;
    private Long finelineStoreActual;
    private Long ccOnlineActual;
    private Long ccStoreActual;
}
