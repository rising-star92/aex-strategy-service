package com.walmart.aex.strategy.dto;

import lombok.Data;

@Data
public class RFAMinMaxValidationResponse {
    private Boolean isCCRulesValid;
    private Boolean isFinelineRulesValid;
    private String status;
    private String errorMessage;
}
