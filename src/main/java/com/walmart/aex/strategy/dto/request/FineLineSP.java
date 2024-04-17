package com.walmart.aex.strategy.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class FineLineSP {

    private Integer finelineNbr;
    private String finelineName;
    private String channel;
    private String traitChoice;
    private UpdatedSizesSP updatedSizes;
    private StrategySP strategy;
    private List<StyleSP> styles;
}
