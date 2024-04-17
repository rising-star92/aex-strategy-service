package com.walmart.aex.strategy.dto.sizeprofile;
import lombok.Data;

import java.util.List;

@Data
public class PlanSizeProfile {
    private Long planId;
    private String planDesc;
    private Integer lvl0Nbr;
    private String lvl0Desc;
    private Integer lvl1Nbr;
    private String lvl1Desc;
    private Integer lvl2Nbr;
    private String lvl2Desc;
    private Integer channelId;
    private String channelDesc;
    private List<Lvl3List> lvl3List ;
}
