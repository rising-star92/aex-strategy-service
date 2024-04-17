package com.walmart.aex.strategy.dto;

import lombok.Getter;

@Getter
public class CSASeasonInfo {
    private Integer csaId;
    private Integer seasonStartWk;
    private Integer seasonEndWk;
    private boolean csaIdPresent;


    public CSASeasonInfo(Integer csaId, Integer seasonStartWk, Integer seasonEndWk, boolean csaIdPresent) {
        this.csaId = csaId;
        this.seasonStartWk = seasonStartWk;
        this.seasonEndWk = seasonEndWk;
        this.csaIdPresent = csaIdPresent;
    }

}
