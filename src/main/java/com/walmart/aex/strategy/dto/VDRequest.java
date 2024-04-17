package com.walmart.aex.strategy.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class VDRequest {
    @NotNull
    @NotEmpty
    private List<VolumeDeviationRequests> volumeDeviationRequestsList;


}
