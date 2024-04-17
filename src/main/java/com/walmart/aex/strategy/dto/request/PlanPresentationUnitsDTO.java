package com.walmart.aex.strategy.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class PlanPresentationUnitsDTO {
    private PlanStrategyDTO planStrategyDTO;
    private List<Integer> finelineNbrs;
}
