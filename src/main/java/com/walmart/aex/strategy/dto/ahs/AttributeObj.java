package com.walmart.aex.strategy.dto.ahs;

import lombok.Data;

@Data
public class AttributeObj {
    private String attributeGroupName;
    private AttributeWrapper goal;
    private AttributeWrapper actual;

}
