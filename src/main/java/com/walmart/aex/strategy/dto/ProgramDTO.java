package com.walmart.aex.strategy.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
public class ProgramDTO {
    private Integer programId;
    private Integer csaSpaceInboundId;
    private String programName;
    private Set<Integer> storeList;
    private List<Mod> mods;

    public ProgramDTO(Integer programId, String programName, Set<Integer> storeList, List<Mod> mods) {
        this.programId = programId;
        this.programName = programName;
        this.storeList = storeList;
        this.mods = mods;
    }

    public ProgramDTO(Integer programId, Integer csaSpaceInboundId, String programName, Set<Integer> storeList, List<Mod> mods) {
        this.programId = programId;
        this.csaSpaceInboundId = csaSpaceInboundId;
        this.programName = programName;
        this.storeList = storeList;
        this.mods = mods;
    }
}
