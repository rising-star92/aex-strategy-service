package com.walmart.aex.strategy.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SizeProfileRequest {
    private Long planId;
    private String channel;
    private String planDesc;
    private Integer lvl3Nbr;
    private Integer lvl4Nbr;
    private Integer finelineNbr;
    private String styleNbr;
    private String ccId;
}
