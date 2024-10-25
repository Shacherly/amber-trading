package com.google.backend.trading.service.impl;

import com.github.pagehelper.PageHelper;
import com.google.backend.trading.dao.mapper.DefaultTradePositionLimitUserMapper;
import com.google.backend.trading.dao.model.TradePositionLimitUser;
import com.google.backend.trading.dao.model.TradePositionLimitUserExample;
import com.google.backend.trading.service.PositionLimitUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author trading
 * @date 2022/1/7 16:59
 */
@Slf4j
@Service
public class PositionLimitUserServiceImpl implements PositionLimitUserService {

	@Autowired
	private DefaultTradePositionLimitUserMapper positionLimitUserMapper;

	@Override
	public void add(TradePositionLimitUser limitUser) {
		try {
			positionLimitUserMapper.insertSelective(limitUser);
		} catch (DuplicateKeyException ignore) {
			log.info("liquid white list exist, uid = {}", limitUser.getUid());
		}
		log.info("用户杠杆持仓限额配置新增, uid = {}", limitUser.getUid());
	}

	@Override
	public void remove(String uid) {
		TradePositionLimitUserExample example = new TradePositionLimitUserExample();
		example.createCriteria().andUidEqualTo(uid);
		int rows = positionLimitUserMapper.deleteByExample(example);
		log.info("用户杠杆持仓限额配置删除, uid = {}, rows = {}", uid, rows);
	}

	@Override
	public void update(TradePositionLimitUser update) {
		update.setMtime(new Date());
		TradePositionLimitUserExample example = new TradePositionLimitUserExample();
		example.createCriteria().andUidEqualTo(update.getUid());
		int rows = positionLimitUserMapper.updateByExampleSelective(update, example);
		log.info("用户杠杆持仓限额配置修改, uid = {}, rows = {}", update.getUid(), rows);
	}

	@Override
	public List<TradePositionLimitUser> find(String uid, Long startTime, Long endTime, int page, int pageSize) {
		TradePositionLimitUserExample example = new TradePositionLimitUserExample();
		TradePositionLimitUserExample.Criteria criteria = example.createCriteria();
		if (StringUtils.isNotBlank(uid)) {
			criteria.andUidEqualTo(uid);
		}
		if (ObjectUtils.allNotNull(startTime, endTime)) {
			criteria.andCtimeBetween(new Date(startTime), new Date(endTime));
		}
		example.setOrderByClause("ctime desc");
		PageHelper.startPage(page, pageSize, true);
		return positionLimitUserMapper.selectByExample(example);
	}
}
