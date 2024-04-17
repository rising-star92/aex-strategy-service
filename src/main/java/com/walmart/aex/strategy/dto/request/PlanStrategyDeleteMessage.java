package com.walmart.aex.strategy.dto.request;

import lombok.Data;

@Data
public class PlanStrategyDeleteMessage {
    private StrongKey strongKey;
    private PlanStrategyDTO planStrategyDTO;
}
