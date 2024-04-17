package com.walmart.aex.strategy.dto.assortproduct;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
@EqualsAndHashCode(callSuper = true)
@Data
public class RFAStylesCcData extends RFAData {
	private Integer fineline_nbr;
    private String style_nbr;
    private List<String> customer_choice;
}
