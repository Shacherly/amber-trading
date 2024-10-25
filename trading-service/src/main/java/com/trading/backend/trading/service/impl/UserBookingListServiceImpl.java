package com.google.backend.trading.service.impl;

import com.github.pagehelper.PageHelper;
import com.google.backend.trading.constant.RedisKeyConstants;
import com.google.backend.trading.dao.mapper.DefaultTradeUserBookingListMapper;
import com.google.backend.trading.dao.model.TradeUserBookingList;
import com.google.backend.trading.dao.model.TradeUserBookingListExample;
import com.google.backend.trading.service.UserBookingListService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author trading
 * @date 2022/1/7 16:37
 */
@Slf4j
@Service
public class UserBookingListServiceImpl implements UserBookingListService {

	@Autowired
	private DefaultTradeUserBookingListMapper userBookingListMapper;

	private final RBucket<Set<String>> bucket;

	public UserBookingListServiceImpl(RedissonClient redissonClient) {
		this.bucket = redissonClient.getBucket(RedisKeyConstants.TRADE_BOOKING_USERS);
	}


	@Override
	public void add(TradeUserBookingList list) {
		try {
			userBookingListMapper.insertSelective(list);
		} catch (DuplicateKeyException ignore) {
			log.info("liquid white list exist, uid = {}", list.getUid());
		}
		log.info("booking名单新增, uid = {}", list.getUid());
	}

	@Override
	public void remove(String uid) {
		TradeUserBookingListExample example = new TradeUserBookingListExample();
		example.createCriteria().andUidEqualTo(uid);
		int rows = userBookingListMapper.deleteByExample(example);
		log.info("booking名单删除, uid = {}, effect rows = {}", uid, rows);
	}

	@Override
	public List<TradeUserBookingList> find(String uid, Long startTime, Long endTime, int page, int pageSize) {
		TradeUserBookingListExample example = new TradeUserBookingListExample();
		TradeUserBookingListExample.Criteria criteria = example.createCriteria();
		if (StringUtils.isNotBlank(uid)) {
			criteria.andUidEqualTo(uid);
		}
		if (ObjectUtils.allNotNull(startTime, endTime)) {
			criteria.andCtimeBetween(new Date(startTime), new Date(endTime));
		}
		example.setOrderByClause("ctime desc");
		PageHelper.startPage(page, pageSize, true);
		return userBookingListMapper.selectByExample(example);
	}

	@Override
	public Set<String> allBookingUidSet() {
		return userBookingListMapper.selectByExample(new TradeUserBookingListExample()).stream().map(TradeUserBookingList::getUid).collect(Collectors.toCollection(HashSet::new));
	}
}
