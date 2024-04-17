package com.walmart.aex.strategy.dto;

import com.walmart.aex.strategy.dto.ahs.AttributeObj;
import lombok.Data;
import java.util.List;

@Data
public class LinePlanStrategyAttributes {

    private Integer finelineCount;

    private Integer customerChoiceCount;

    private List<AttributeObj> attributeGroups;

}
