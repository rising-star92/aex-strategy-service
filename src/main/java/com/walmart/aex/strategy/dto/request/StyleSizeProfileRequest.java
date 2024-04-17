package com.walmart.aex.strategy.dto.request;

import lombok.Data;

@Data
public class StyleSizeProfileRequest {
    private Long planId;
    private String channel;
    private String planDesc;
    private Integer finelineNbr;

}
