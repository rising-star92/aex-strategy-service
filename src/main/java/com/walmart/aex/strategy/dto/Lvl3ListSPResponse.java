package com.walmart.aex.strategy.dto;

import com.walmart.aex.strategy.dto.request.UpdatedSizesSP;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Lvl3ListSPResponse {
    private Integer lvl3Nbr;
    private String lvl3Name;
    private String channel;
    private UpdatedSizesSP updatedSizes;
    private StrategySPResponse strategy;
    private List<Lvl4ListSPResponse> lvl4List = new ArrayList<Lvl4ListSPResponse>();
}
