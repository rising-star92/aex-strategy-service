package com.walmart.aex.strategy.dto.programfixture;

import lombok.Data;

import java.util.List;

@Data
public class Fixture {

    private String fixtureType;

    private Integer storeCnt;

    private List<Integer> storeList;

}
