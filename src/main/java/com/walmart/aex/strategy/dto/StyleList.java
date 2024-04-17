package com.walmart.aex.strategy.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class StyleList {

    private String styleNbr;
    private StrategySPResponse strategy;
    private List<CustomerChoice> customerChoice ;
}
