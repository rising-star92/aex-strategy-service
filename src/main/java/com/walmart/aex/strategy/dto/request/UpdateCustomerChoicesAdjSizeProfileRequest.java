package com.walmart.aex.strategy.dto.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Data
public class UpdateCustomerChoicesAdjSizeProfileRequest {
	
	private Long planId ;
	private String  channel;
	private String  style;
	private Integer clusterId;
    private Integer finelineNbr ;
    private Double  adjSizePct;
    private Integer ahsSizeId;
    private String customerChoice;  
}

