package com.walmart.aex.strategy.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class UpdatedFields {
    private List<Field> weatherCluster;
    private List<Field> fixture;
    private List<Field> presentationUnits;
    private List<Field> runRfaStatus;
}
