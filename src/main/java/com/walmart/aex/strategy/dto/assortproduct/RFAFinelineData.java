package com.walmart.aex.strategy.dto.assortproduct;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class RFAFinelineData extends RFAData {
    private List<Integer> fineline_nbr;
    private List<String> customer_choice;
}
