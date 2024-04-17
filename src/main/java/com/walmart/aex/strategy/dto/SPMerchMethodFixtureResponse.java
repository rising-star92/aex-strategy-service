package com.walmart.aex.strategy.dto;

import lombok.Data;

import java.util.List;
@Data
public class SPMerchMethodFixtureResponse {
    private Long planId;
    private String planDesc;
    private Integer lvl0Nbr;
    private String lvl0Desc;
    private Integer lvl1Nbr;
    private String lvl1Desc;
    private Integer lvl2Nbr;
    private String lvl2Desc;
    private List<SPMerchMethLvl3Response> lvl3List;
}
