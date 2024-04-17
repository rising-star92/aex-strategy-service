package com.walmart.aex.strategy.dto;

import lombok.Data;

import java.util.List;

@Data
public class RFAStatusDataResponse {
    private List<RFAStatusDataDTO> rfaStatusList;
    private String status;
}
