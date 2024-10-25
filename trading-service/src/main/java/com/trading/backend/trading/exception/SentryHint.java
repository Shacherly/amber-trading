package com.google.backend.trading.exception;

/**
 * @author trading
 * @date 2022/1/18 14:08
 */
public enum SentryHint {

	/**
	 * 参数错误
	 */
	INVALID_PARAMETER,
	/**
	 * 内部接口错误
	 */
	INTERNAL_API_ERROR,
	/**
	 * 业务预期外的错误
	 */
	BUSINESS_UNEXPECTED_ERROR,
	/**
	 * 预期外的错误
	 */
	UNEXPECTED_ERROR,
}
