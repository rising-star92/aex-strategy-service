package com.walmart.aex.strategy.dto.sizeprofile;
import lombok.Data;

import java.util.List;

@Data
public class Lvl4List {
    private Integer lvl4Nbr;
    private String lvl4Desc;
    private List<FineLine> finelines;
}
