package com.walmart.aex.strategy.dto.request;

import lombok.Data;

@Data
public class UpdateStyleAdjSizeProfileRequest {

	private Long planId;
	private String channel;
	private Integer fineline;
	private String style;
	private Integer clusterId;
	private Integer ahsSizeId;
	private Double adjSizePct;

}
