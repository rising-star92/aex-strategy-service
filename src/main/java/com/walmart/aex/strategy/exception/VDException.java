package com.walmart.aex.strategy.exception;

public class VDException extends Exception {

	private static final long serialVersionUID = 1L;

	public VDException(String errorMessage) {
        super(errorMessage);
    }

	public VDException(String message, Throwable cause) {
		super(message, cause);
	}

}
