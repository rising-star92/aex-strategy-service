package com.walmart.aex.strategy.dto;

import com.walmart.aex.strategy.dto.ahs.AttributeObj;
import lombok.Data;
import java.util.List;

@Data
public class LinePlanCount {

    private Integer type;

    private Long planId;

    private Integer lvl0Nbr;

    private Integer lvl1Nbr;

    private Integer lvl2Nbr;

    private String lvl2Name;

    private Integer lvl3Nbr;

    private String lvl3Name;

    private Integer lvl4Nbr;

    private String lvl4Name;

    private Integer fiscalYear;

    private Integer finelineCount;

    private Integer customerChoiceCount;

    private String attributeJson;

    private List<AttributeObj> attributeObj;

    public LinePlanCount() {}

    public LinePlanCount(Integer lvl0Nbr, Integer lvl1Nbr, Integer lvl2Nbr, Integer lvl3Nbr, Integer finelineCount, Integer customerChoiceCount, String attributeJson) {
        this.lvl0Nbr = lvl0Nbr;
        this.lvl1Nbr = lvl1Nbr;
        this.lvl2Nbr = lvl2Nbr;
        this.lvl3Nbr = lvl3Nbr;
        this.finelineCount = finelineCount;
        this.customerChoiceCount = customerChoiceCount;
        this.attributeJson = attributeJson;
    }

    public LinePlanCount(Integer lvl0Nbr, Integer lvl1Nbr, Integer lvl2Nbr, Integer lvl3Nbr, Integer lvl4Nbr, Integer finelineCount, Integer customerChoiceCount, String attributeJson) {
        this.lvl0Nbr = lvl0Nbr;
        this.lvl1Nbr = lvl1Nbr;
        this.lvl2Nbr = lvl2Nbr;
        this.lvl3Nbr = lvl3Nbr;
        this.lvl4Nbr = lvl4Nbr;
        this.finelineCount = finelineCount;
        this.customerChoiceCount = customerChoiceCount;
        this.attributeJson = attributeJson;
    }
}
