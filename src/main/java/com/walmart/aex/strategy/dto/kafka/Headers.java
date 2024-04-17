package com.walmart.aex.strategy.dto.kafka;

import com.walmart.aex.strategy.enums.EventType;
import lombok.Data;

@Data
public class Headers {
    private Long planId;
    private EventType type;
    private String source;
    private long timeStamp;
    private ChangeScope changeScope;

}
