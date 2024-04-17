package com.walmart.aex.strategy.dto;

import com.walmart.aex.strategy.dto.request.SizeProfileDTO;
import lombok.Data;

import java.util.List;

@Data
public class StrategySPResponse {
    private List<SizeProfileDTO> sizeProfile;
}
