package com.google.backend.trading.service;

import com.google.backend.trading.model.trade.TradeType;
import com.google.backend.trading.model.user.UserInfo;

import java.util.Locale;

/**
 * @author trading
 * @date 2021/12/6 17:56
 */
public interface UserService {

	/**
	 * 检查用户kyc状态是否通过以及ip是否合规，不通过 throw 状态码
	 *
	 * @param userInfo
	 */
	void checkUserComplianceOrThrowE(UserInfo userInfo);

	/**
	 * 检查用户kyc状态是否通过，不通过 throw 状态码
	 *
	 * @param userInfo
	 * @param tradeType
	 */
	void checkUserKycOnlyOrThrowE(UserInfo userInfo, TradeType tradeType);

	/**
	 * 获取对应用户的 Locale
	 *
	 * @param uid
	 * @return
	 */
	Locale locale(String uid);

	/**
	 * 设置对应用户的 Locale cache
	 * @param uid
	 * @param locale
	 */
	void setLocale(String uid, Locale locale);
}
