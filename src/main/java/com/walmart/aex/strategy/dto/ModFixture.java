package com.walmart.aex.strategy.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
public class ModFixture {
    private String modName;
    private String uniqueKey;
    private Integer modId;
    private Set<Integer> storeList;
    private Integer programId;
    private String programName;
    private String firstName;
    private String lastName;
    private Integer deptNbr;
    private String fixtureType;
}
