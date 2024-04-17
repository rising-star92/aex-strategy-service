package com.walmart.aex.strategy.exception;

public class StrategyServiceException extends Exception {
	
	private static final long serialVersionUID = 1L;
	
	public StrategyServiceException(String errorMessage) {
        super(errorMessage);
    }

	public StrategyServiceException(String message, Throwable cause) {
		super(message, cause);
	}

}
