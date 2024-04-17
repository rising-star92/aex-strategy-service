package com.walmart.aex.strategy.dto.midas;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
public class StrongKeyFlat {
    private Integer fiscalYear;
    private String seasonCode;
    private Integer stratGroupTypeId;
    private Long planId;
    private Integer lvl0Nbr;
    private Integer lvl1Nbr;
    private Integer lvl2Nbr;
    private Integer lvl3Nbr;
    private Integer lvl4Nbr;
    private Integer finelineNbr;

    public StrongKeyFlat(Integer stratGroupTypeId,
                         Long planId,
                         Integer lvl0Nbr,
                         Integer lvl1Nbr,
                         Integer lvl2Nbr,
                         Integer lvl3Nbr,
                         Integer lvl4Nbr,
                         Integer finelineNbr){
        this.stratGroupTypeId=stratGroupTypeId;
        this.planId=planId;
        this.lvl0Nbr=lvl0Nbr;
        this.lvl1Nbr=lvl1Nbr;
        this.lvl2Nbr=lvl2Nbr;
        this.lvl3Nbr=lvl3Nbr;
        this.lvl4Nbr=lvl4Nbr;
        this.finelineNbr=finelineNbr;
    }
}