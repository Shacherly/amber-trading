package com.google.backend.trading.service.impl;

import com.google.backend.trading.component.SymbolDomain;
import com.google.backend.trading.dao.mapper.DefaultTradeUserAlarmPriceMapper;
import com.google.backend.trading.dao.model.TradeUserAlarmPrice;
import com.google.backend.trading.dao.model.TradeUserAlarmPriceExample;
import com.google.backend.trading.exception.BusinessException;
import com.google.backend.trading.exception.BusinessExceptionEnum;
import com.google.backend.trading.mapstruct.user.TradeUserAlarmPriceStruct;
import com.google.backend.trading.model.user.AlarmPriceDelReq;
import com.google.backend.trading.model.user.AlarmPriceSetReq;
import com.google.backend.trading.model.user.AlarmTriggerType;
import com.google.backend.trading.model.user.UserInfo;
import com.google.backend.trading.service.SensorsTraceService;
import com.google.backend.trading.service.UserAlarmPriceService;
import com.google.backend.trading.util.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author david.chen
 * @date 2021/12/22 19:37
 */
@Service
public class UserAlarmPriceServiceImpl implements UserAlarmPriceService {

    @Autowired
    private DefaultTradeUserAlarmPriceMapper defaultTradeUserAlarmPriceMapper;
    @Autowired
    private TradeUserAlarmPriceStruct tradeUserAlarmPriceStruct;
    @Autowired
    private SensorsTraceService sensorsTraceService;

    private Integer userSetAlarmNum = 5;

    @Override
    public boolean setAlarmPrice(AlarmPriceSetReq alarmPriceSetReq, UserInfo userInfo) {
        //1.查询是否超过5个？
        List<TradeUserAlarmPrice> userAlarmPriceList = this.getAlarmPriceByUserIdAndSymbol(alarmPriceSetReq.getSymbol(), userInfo.getUid());
        if (!CollectionUtils.isEmpty(userAlarmPriceList) && userAlarmPriceList.size() > userSetAlarmNum) {
            throw new BusinessException(BusinessExceptionEnum.ALARM_PRICE_SET_MAXIMUM_NUM);
        }
        //初始化insert数据
        TradeUserAlarmPrice insertAlarmPriceData = tradeUserAlarmPriceStruct.alarmPriceReq2TradeUserAlarmPrice(alarmPriceSetReq);
        insertAlarmPriceData.setUid(userInfo.getUid());
        //2. 判断当前此币种价格，
        String symbol = alarmPriceSetReq.getSymbol();
        BigDecimal alarmPrice = alarmPriceSetReq.getAlarmPrice();
        SymbolDomain symbolDomain = SymbolDomain.nonNullGet(symbol);
        BigDecimal midPrice = symbolDomain.midPrice();

        if (alarmPrice.compareTo(midPrice) > 0) {
            // 当alarmPrice>midPrice 下次 midPrice>alarmPrice预警
            insertAlarmPriceData.setAlarmCompare(AlarmTriggerType.GREATER.getCode());
        } else {
            // 当alarmPrice<midPrice 下次 midPrice<alarmPrice预警
            insertAlarmPriceData.setAlarmCompare(AlarmTriggerType.LESS.getCode());
        }
        //3.插入预警to DB
        Date date = new Date();
        insertAlarmPriceData.setCtime(date);
        insertAlarmPriceData.setMtime(date);
        sensorsTraceService.setAlert(insertAlarmPriceData, midPrice);
        return defaultTradeUserAlarmPriceMapper.insertSelective(insertAlarmPriceData) > 0;
    }

    @Override
    public boolean delAlarmPrice(AlarmPriceDelReq alarmPriceDelReq, UserInfo userInfo) {

        TradeUserAlarmPriceExample example = new TradeUserAlarmPriceExample();
        TradeUserAlarmPriceExample.Criteria criteria = example.createCriteria();
        criteria.andSymbolEqualTo(alarmPriceDelReq.getSymbol())
                .andUidEqualTo(userInfo.getUid());

        //1. check del all?
        if (!alarmPriceDelReq.isDelAll()) {
            criteria.andIdEqualTo(alarmPriceDelReq.getAlarmId());
        }
        //2. del
        return defaultTradeUserAlarmPriceMapper.deleteByExample(example) > 0;
    }

    @Override
    public List<TradeUserAlarmPrice> queryAll() {
        return defaultTradeUserAlarmPriceMapper.selectByExample(new TradeUserAlarmPriceExample());
    }

    @Override
    public boolean updateById(TradeUserAlarmPrice tradeUserAlarmPrice) {
        tradeUserAlarmPrice.setMtime(CommonUtils.getNowTime());
        return defaultTradeUserAlarmPriceMapper.updateByPrimaryKeySelective(tradeUserAlarmPrice) > 0;
    }

    @Override
    public List<TradeUserAlarmPrice> getAlarmPriceByUserIdAndSymbol(String symbol, String uid) {
        TradeUserAlarmPriceExample example = new TradeUserAlarmPriceExample();
        TradeUserAlarmPriceExample.Criteria criteria = example.createCriteria();
        criteria.andUidEqualTo(uid);
        criteria.andSymbolEqualTo(symbol);
        return defaultTradeUserAlarmPriceMapper.selectByExample(example);
    }


}
