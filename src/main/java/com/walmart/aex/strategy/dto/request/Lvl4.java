package com.walmart.aex.strategy.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class Lvl4 {

    private Integer lvl4Nbr;

    private String lvl4Name;

    private String channel;

    private UpdatedFields updatedFields;

    private Strategy strategy;

    private List<Fineline> finelines;
}
