package com.walmart.aex.strategy.dto;

import com.walmart.aex.strategy.dto.request.StyleSP;
import com.walmart.aex.strategy.dto.request.UpdatedSizesSP;
import lombok.Data;

import java.util.List;

@Data
public class FineLineSPResponse {
    private Integer finelineNbr;
    private String finelineName;
    private String altFinelineName;
    private String channel;
    private String traitChoice;
    private UpdatedSizesSP updatedSizes;
    private StrategySPResponse strategy;
    private List<StyleSP> styles;
    private MetadataSPResponse metadata;
}
