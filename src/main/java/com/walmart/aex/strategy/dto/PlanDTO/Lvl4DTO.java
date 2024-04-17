package com.walmart.aex.strategy.dto.PlanDTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)

public class Lvl4DTO {
    private Integer lvl4Nbr;
    private String lvl4GenDesc1;
}
