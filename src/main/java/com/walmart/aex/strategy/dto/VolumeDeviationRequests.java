package com.walmart.aex.strategy.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class VolumeDeviationRequests {
    @NotNull
    @NotEmpty
    private List<Integer> finelineNbr;
    private Integer planId;
}
