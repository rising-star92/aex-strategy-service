package com.walmart.aex.strategy.dto.programfixture;

import lombok.Data;

import java.util.List;

@Data
public class ProgramDetail {

    private String programId;

    private String programName;

    private Integer storeCnt;

    private List<Fixture> fixtures;

}
