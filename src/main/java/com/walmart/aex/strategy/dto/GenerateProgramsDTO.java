package com.walmart.aex.strategy.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GenerateProgramsDTO {
    private String fileName;
    private int seasonStartWk;
    private int seasonEndWk;
    private int fiscalYear;
    private int csaSeasonId;
    private String seasonCode;
    private int deptNbr;

}
