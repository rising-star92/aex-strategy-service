package com.walmart.aex.strategy.dto;

import com.walmart.aex.strategy.dto.request.UpdatedSizesSP;
import lombok.Data;

import java.util.List;

@Data
public class Lvl4ListSPResponse {
    private Integer lvl4Nbr;
    private String lvl4Name;
    private String channel;
    private UpdatedSizesSP updatedSizes;
    private StrategySPResponse strategy;
    private List<FineLineSPResponse> finelines;
}
