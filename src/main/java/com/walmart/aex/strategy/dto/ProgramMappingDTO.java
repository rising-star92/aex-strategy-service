package com.walmart.aex.strategy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProgramMappingDTO {
    private String programType;
    private Long sourceId;
    private Long programId;
    private String programName;
    private String error;
}
