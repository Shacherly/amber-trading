package com.google.backend.trading.service;

import com.google.backend.trading.dao.model.TradeUserBookingList;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Set;

/**
 * booking用户名单
 *
 * @author trading
 * @date 2022/1/7 16:37
 */
public interface UserBookingListService {

	/**
	 * 添加名单
	 *
	 * @param list
	 */
	void add(TradeUserBookingList list);

	/**
	 * 移除名单
	 *
	 * @param uid
	 */
	void remove(String uid);

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
	List<TradeUserBookingList> find(@Nullable String uid, @Nullable Long startTime, @Nullable Long endTime, int page, int pageSize);

	/**
	 * 所有booking名单
	 *
	 * @return
	 */
	Set<String> allBookingUidSet();
}
