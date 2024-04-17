package com.walmart.aex.strategy.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

@EqualsAndHashCode(callSuper = true)
@Slf4j
@Data
public class ClpApException extends RuntimeException{

    public ClpApException(String message) {

        super(message);
    }

    public ClpApException(String message, Throwable cause) {

        super(message, cause);
    }
}
