package com.walmart.aex.strategy.dto.request;

import lombok.Data;

@Data
public class FPVolumeDeviationUserSelectionRequest{

    private Long planId;
    private Integer lvl0Nbr;
    private Integer lvl2Nbr;
    private Integer lvl1Nbr;
    private Integer lvl3Nbr;
    private Integer lvl4Nbr;
    private Integer finelineNbr;
    private String volumeDeviationLevel;

}
