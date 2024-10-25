package com.google.backend.trading.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.backend.trading.constant.Constants;
import com.google.backend.trading.dao.mapper.DefaultTradeUserPageSettingMapper;
import com.google.backend.trading.dao.model.TradeUserPageSetting;
import com.google.backend.trading.dao.model.TradeUserPageSettingExample;
import com.google.backend.trading.model.config.api.UserPageSettingVo;
import com.google.backend.trading.service.UserPageSettingService;
import com.google.backend.trading.util.AlarmLogUtil;
import com.google.backend.trading.util.ListUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.postgresql.util.PGobject;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 用户交易设置
 *
 * @author jiayi.zhang
 * @date 2021/10/9
 */
@Slf4j
@Service
public class UserPageSettingServiceImpl implements UserPageSettingService {

    @Resource
    private DefaultTradeUserPageSettingMapper defaultTradeUserPageSettingMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RedissonClient redissonClient;


    private TradeUserPageSetting queryUserPageSettingByUid(String uid) {
        TradeUserPageSettingExample example = new TradeUserPageSettingExample();
        TradeUserPageSettingExample.Criteria criteria = example.createCriteria();
        criteria.andUidEqualTo(uid);
        List<TradeUserPageSetting> tradeUserPageSettings = defaultTradeUserPageSettingMapper.selectByExample(example);
        return ListUtil.isEmpty(tradeUserPageSettings) ? null : tradeUserPageSettings.get(0);
    }

    @Override
    public UserPageSettingVo queryUserPageSetting(String uid) {
        String priceCard = Constants.DEFAULT_PAGE_SETTING_PRICE_CARD;
        if (uid != null) {
            TradeUserPageSetting tradeUserPageSetting = this.queryUserPageSettingByUid(uid);
            if (tradeUserPageSetting != null) {
                priceCard = tradeUserPageSetting.getPriceCard().getValue();
            }
        }
        UserPageSettingVo vo = new UserPageSettingVo();
        try {
            vo.setPriceCard(objectMapper.readTree(priceCard));
        } catch (JsonProcessingException e) {
            log.error("err", e);
        }
        return vo;
    }

    @Override
    public UserPageSettingVo updateOrInsertUserPageSetting(UserPageSettingVo req, String uid) {
        RLock lock = redissonClient.getLock(uid + ":user-page-setting");
        try {
            if (lock.tryLock(5, TimeUnit.SECONDS)) {
                TradeUserPageSetting tradeUserPageSetting = this.queryUserPageSettingByUid(uid);
                PGobject priceCard = new PGobject();
                priceCard.setType("json");
                priceCard.setValue(req.getPriceCard().toString());
                if (tradeUserPageSetting == null) {
                    tradeUserPageSetting = new TradeUserPageSetting();
                    tradeUserPageSetting.setUid(uid);
                    tradeUserPageSetting.setPriceCard(priceCard);
                    defaultTradeUserPageSettingMapper.insertSelective(tradeUserPageSetting);
                } else {
                    tradeUserPageSetting.setPriceCard(priceCard);
                    defaultTradeUserPageSettingMapper.updateByPrimaryKeySelective(tradeUserPageSetting);
                }
            }
        } catch (InterruptedException ignore) {
        } catch (SQLException e) {
            AlarmLogUtil.alarm("Invalid format, value = {}， cause = {}", req.getPriceCard(), ExceptionUtils.getRootCauseMessage(e));
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        return req;
    }
}
