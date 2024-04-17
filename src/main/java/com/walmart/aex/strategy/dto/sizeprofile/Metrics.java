package com.walmart.aex.strategy.dto.sizeprofile;

import lombok.Data;

@Data
public class Metrics {
	
	private Double sizeProfilePct;
	private Double adjSizeProfilePct;
	private Double avgSizeProfilePct;
	private Double adjAvgSizeProfilePct;
	private Integer buyQty;
	private Integer finalBuyQty;
	private Integer finalInitialSetQty;
	private Integer finalReplenishmentQty;


}
