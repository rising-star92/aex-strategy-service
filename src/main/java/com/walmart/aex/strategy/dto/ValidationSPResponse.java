package com.walmart.aex.strategy.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class ValidationSPResponse {
    private Set<Integer> merchMethodCodeList;
    private Set<Long> sizeProfilePctList;
}
