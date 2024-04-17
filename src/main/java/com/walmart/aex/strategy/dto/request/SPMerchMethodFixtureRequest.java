package com.walmart.aex.strategy.dto.request;

import com.walmart.aex.strategy.dto.sizepack.SPMerchMethLvl3;
import lombok.Data;

import java.util.List;
@Data
public class SPMerchMethodFixtureRequest {
    private Long planId;
    private String planDesc;
    private Integer lvl0Nbr;
    private String lvl0Desc;
    private Integer lvl1Nbr;
    private String lvl1Desc;
    private Integer lvl2Nbr;
    private String lvl2Desc;
    private List<SPMerchMethLvl3> lvl3List;
}
