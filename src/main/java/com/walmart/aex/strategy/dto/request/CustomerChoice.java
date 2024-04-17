package com.walmart.aex.strategy.dto.request;

import lombok.Data;

@Data
public class CustomerChoice {
    private String ccId;
    private String altCcDesc;
    private String colorName;
    private String channel;
    private UpdatedFields updatedFields;
    private Strategy strategy;
    private String colorFamily;

}
