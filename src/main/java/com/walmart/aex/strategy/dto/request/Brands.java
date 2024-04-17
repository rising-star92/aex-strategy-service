package com.walmart.aex.strategy.dto.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class Brands implements Serializable {
    private Integer brandId;
    private String brandLabelCode;
    private String brandName;
    private String brandType;
}
