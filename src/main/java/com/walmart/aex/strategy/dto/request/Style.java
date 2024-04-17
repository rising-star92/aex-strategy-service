package com.walmart.aex.strategy.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class Style {
    private String styleNbr;
    private String altStyleDesc;
    private String channel;
    private Strategy strategy;
    private List<CustomerChoice> customerChoices;

}
