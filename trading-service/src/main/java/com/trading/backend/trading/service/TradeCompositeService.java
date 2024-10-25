package com.google.backend.trading.service;

import com.google.backend.trading.dao.model.TradeUserTradeSetting;
import com.google.backend.trading.model.common.model.config.UserTradeSettingVo;

/**
 * @author trading
 * @date 2021/10/29 16:20
 */
public interface TradeCompositeService {

	/**
	 * 检查当前用户风险并修改交易设置
	 * @param userTradeSetting
	 * @param uid
	 * @return
	 */
	UserTradeSettingVo checkAndUpdateUserTradeSetting(TradeUserTradeSetting userTradeSetting, String uid);


	/**
	 * 判断当前用户是不是交易新手用户
	 * 逻辑：是否有成交记录
	 * @param uid
	 * @return
	 */
	boolean isTradeNewUser(String uid);
}
