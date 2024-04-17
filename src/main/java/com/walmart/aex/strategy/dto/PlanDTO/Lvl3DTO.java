package com.walmart.aex.strategy.dto.PlanDTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Lvl3DTO {
    private Integer lvl3Nbr;
    private String lvl3GenDesc1;
    private List<Lvl4DTO> lvl4;
}
