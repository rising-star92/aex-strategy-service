package com.walmart.aex.strategy.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class ProductDimensions implements Serializable {
    private Integer productDimId;
    private String productDimDesc;
}
