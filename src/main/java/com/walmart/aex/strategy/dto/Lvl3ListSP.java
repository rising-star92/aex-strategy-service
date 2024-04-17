package com.walmart.aex.strategy.dto;

import com.walmart.aex.strategy.dto.request.StrategySP;
import com.walmart.aex.strategy.dto.request.UpdatedSizesSP;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Lvl3ListSP {
	private Integer lvl3Nbr;
    private String lvl3Name;
    private UpdatedSizesSP updatedSizes;
    private StrategySP strategy;
    private List<Lvl4ListSP> lvl4List ;
}
