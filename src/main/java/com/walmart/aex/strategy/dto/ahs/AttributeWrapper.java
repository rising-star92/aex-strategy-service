package com.walmart.aex.strategy.dto.ahs;

import lombok.Data;

import java.util.List;

@Data
public class AttributeWrapper {

    private List<Attribute> finelineAttribute;
    private List<Attribute> customerChoiceAttribute;

}
