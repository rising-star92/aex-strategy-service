package com.walmart.aex.strategy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProgramResponseDTO {
    private List<ProgramMappingDTO> programMappings;
    private String status;
    private String error;
}
