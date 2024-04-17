package com.walmart.aex.strategy.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@ToString
@AllArgsConstructor
@Setter
@Getter
public class Finelines {
    private Integer finelineId ;
    private String finelineName ;
    private Integer lvl0Nbr ;
    private Integer lvl1Nbr ;
    private Integer lvl2Nbr ;
    private Integer lvl3Nbr ;
    private Integer lvl4Nbr ;
    private Integer planId ;
    private String volumeDeviationLevel;
}
