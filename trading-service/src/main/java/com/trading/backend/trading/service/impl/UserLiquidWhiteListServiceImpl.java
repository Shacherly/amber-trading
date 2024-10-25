package com.google.backend.trading.service.impl;

import com.github.pagehelper.PageHelper;
import com.google.backend.trading.dao.mapper.DefaultUserSystemSettingMapper;
import com.google.backend.trading.dao.model.TradeUserSystemSetting;
import com.google.backend.trading.dao.model.TradeUserSystemSettingExample;
import com.google.backend.trading.service.UserLiquidWhiteListService;
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
 * @date 2022/1/7 15:46
 */
@Slf4j
@Service
public class UserLiquidWhiteListServiceImpl implements UserLiquidWhiteListService {

	@Autowired
	private DefaultUserSystemSettingMapper userSystemSettingMapper;

	@Override
	public void add(TradeUserSystemSetting setting) {
		try {
			userSystemSettingMapper.insertSelective(setting);
		} catch (DuplicateKeyException ignore) {
			log.info("liquid white list exist, uid = {}", setting.getUid());
		}
		log.info("用户杠杆强平开关白名单新增, uid = {}", setting.getUid());
	}

	@Override
	public void remove(String uid) {
		TradeUserSystemSettingExample example = new TradeUserSystemSettingExample();
		example.createCriteria().andUidEqualTo(uid);
		int rows = userSystemSettingMapper.deleteByExample(example);
		log.info("用户杠杆强平开关白名单新增, uid = {}, effect rows = {}", uid, rows);
	}

	@Override
	public List<TradeUserSystemSetting> find(String uid, Long startTime, Long endTime, int page, int pageSize) {
		TradeUserSystemSettingExample example = new TradeUserSystemSettingExample();
		TradeUserSystemSettingExample.Criteria criteria = example.createCriteria();
		if (StringUtils.isNotBlank(uid)) {
			criteria.andUidEqualTo(uid);
		}
		if (ObjectUtils.allNotNull(startTime, endTime)) {
			criteria.andCtimeBetween(new Date(startTime), new Date(endTime));
		}
		example.setOrderByClause("ctime desc");
		PageHelper.startPage(page, pageSize, true);
		return userSystemSettingMapper.selectByExample(example);
	}
}
