package com.walmart.aex.strategy.dto;

import com.walmart.aex.strategy.dto.request.FineLineSP;
import com.walmart.aex.strategy.dto.request.StrategySP;
import com.walmart.aex.strategy.dto.request.UpdatedSizesSP;
import lombok.Data;

import java.util.List;

@Data
public class Lvl4ListSP {
	
	private Integer lvl4Nbr;
    private String lvl4Name;
    private UpdatedSizesSP updatedSizes;
    private StrategySP strategy;
    private List<FineLineSP> finelines;

}
