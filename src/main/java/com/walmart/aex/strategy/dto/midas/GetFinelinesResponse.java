package com.walmart.aex.strategy.dto.midas;

import lombok.Data;

import java.util.List;

@Data
public class GetFinelinesResponse {
    private List<StrongKeyFlat> payload;
    private String status;
    private String errorMessage;
}
