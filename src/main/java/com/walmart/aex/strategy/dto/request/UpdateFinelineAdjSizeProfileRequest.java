package com.walmart.aex.strategy.dto.request;

import lombok.Data;

@Data
public class UpdateFinelineAdjSizeProfileRequest {
	
	private Long planId;
	private String channel;
	private Integer fineline;
	private Integer clusterId;
	private Double adjSizePct;
	private Integer ahsSizeId;

}
