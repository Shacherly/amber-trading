package com.google.backend.trading.service;

import com.google.backend.trading.dao.model.TradePositionLimitUser;
import org.springframework.lang.Nullable;

import java.util.List;

/**
 * 用户杠杆持仓限额配置
 *
 * @author trading
 * @date 2022/1/7 16:58
 */
public interface PositionLimitUserService {

	/**
	 * 新增配置
	 *
	 * @param limitUser
	 */
	void add(TradePositionLimitUser limitUser);

	/**
	 * 移除配置
	 *
	 * @param uid
	 */
	void remove(String uid);

	/**
	 * 更新配置
	 *
	 * @param update
	 */
	void update(TradePositionLimitUser update);

	/**
	 * 查询名单列表
	 *
	 * @param uid
	 * @param startTime
	 * @param endTime
	 * @param page
	 * @param pageSize
	 * @return
	 */
	List<TradePositionLimitUser> find(@Nullable String uid, @Nullable Long startTime, @Nullable Long endTime, int page, int pageSize);
}
