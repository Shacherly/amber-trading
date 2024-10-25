package com.google.backend.trading.service;

import com.google.backend.trading.dao.model.TradeUserSystemSetting;
import org.springframework.lang.Nullable;

import java.util.List;

/**
 * 用户杠杆强平开关白名单
 *
 * @author trading
 * @date 2022/1/7 15:46
 */
public interface UserLiquidWhiteListService {

	/**
	 * 添加白名单
	 *
	 * @param setting
	 */
	void add(TradeUserSystemSetting setting);

	/**
	 * 移除白名单
	 *
	 * @param uid
	 */
	void remove(String uid);

	/**
	 * 查询白名单列表
	 *
	 * @param uid
	 * @param startTime
	 * @param endTime
	 * @param page
	 * @param pageSize
	 * @return
	 */
	List<TradeUserSystemSetting> find(@Nullable String uid, @Nullable Long startTime, @Nullable Long endTime, int page, int pageSize);
}
