package com.walmart.aex.strategy.dto.PlanDTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlanDefMessageDTO {
    private PlanDTO payload;
}
