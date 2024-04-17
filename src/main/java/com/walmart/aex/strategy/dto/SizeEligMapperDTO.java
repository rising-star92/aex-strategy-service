package com.walmart.aex.strategy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SizeEligMapperDTO {
    private SizeResponseDTO sizeResponseDTO;
    private PlanStrategySPResponse response;
    private List<SizeResponseDTO> validationSizeResponseList;
    private Integer finelineNbr;
    private Integer catgFlag;
    private String channel;
}
