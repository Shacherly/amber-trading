package com.google.backend.trading.service;

/**
 * @author trading
 * @date 2021/12/6 17:14
 */
public interface RiskInfoService {
	/**
	 * 检查用户风控状态
	 * @param uid
	 */
	void validateRiskStatus(String uid);
}
