package com.walmart.aex.strategy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CsaReservedProgramsResponse implements Serializable {
    private String categoryId;
    private List<Trait> programDetails;
}
