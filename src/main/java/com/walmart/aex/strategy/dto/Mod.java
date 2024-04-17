package com.walmart.aex.strategy.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Mod {
    private String modName;
    private String uniqueKey;
    private Integer modId;
    private Set<Integer> storeList;
    private Integer programId;
    private String programName;
    private String firstName;
    private String lastName;
    private Integer deptNbr;
    private List<String> fixtureType;

    public Mod(String modName, String uniqueKey, Integer modId, Set<Integer> storeList, Integer programId, String programName, String firstName, String lastName, Integer deptNbr) {
        this.modName = modName;
        this.uniqueKey = uniqueKey;
        this.modId = modId;
        this.storeList = storeList;
        this.programId = programId;
        this.programName = programName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.deptNbr = deptNbr;
    }
}
