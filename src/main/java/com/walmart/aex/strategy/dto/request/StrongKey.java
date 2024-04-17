
package com.walmart.aex.strategy.dto.request;

import lombok.Data;

@Data
public class StrongKey {
    private Long planId;
    private String planDesc;
    private Integer lvl0Nbr;
    private Integer lvl1Nbr;
    private Integer lvl2Nbr;
    private Integer lvl3Nbr;
    private Integer lvl4Nbr;
    private Fineline fineline;
}