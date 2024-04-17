package com.walmart.aex.strategy.dto.kafka;

import com.walmart.aex.strategy.dto.request.StrongKey;
import lombok.Data;

import java.util.Set;

@Data
public class ChangeScope {
    private Set<String> updatedAttributes;
    private StrongKey strongKeys;
}
