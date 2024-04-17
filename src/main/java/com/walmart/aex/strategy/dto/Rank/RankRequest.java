package com.walmart.aex.strategy.dto.Rank;

import lombok.Data;

import java.util.List;

@Data
public class RankRequest {

    private List<Long> planId;
    private List<Integer> lvl0Nbr;
    private List<Integer> lvl1Nbr;
    private List<Integer> lvl2Nbr;
    private List<Integer> lvl3Nbr;
    private List<Integer> lvl4Nbr;
    private Integer finelineNbr;
    private String styleNbr;
    private String ccNo;
    private Integer channelId;

}
