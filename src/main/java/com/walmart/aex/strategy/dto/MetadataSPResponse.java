package com.walmart.aex.strategy.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MetadataSPResponse {
    private ValidationSPResponse validationData;
}
