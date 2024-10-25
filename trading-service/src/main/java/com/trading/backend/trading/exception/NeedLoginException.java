package com.google.backend.trading.exception;

/**
 * @author trading
 * @date 2022/1/4 16:12
 */
public class NeedLoginException extends RuntimeException {

	public NeedLoginException(String message) {
		super(message);
	}
}
