package com.google.backend.trading.service;

import com.google.backend.trading.dao.model.TradeUserAlarmPrice;
import com.google.backend.trading.model.user.AlarmPriceDelReq;
import com.google.backend.trading.model.user.AlarmPriceSetReq;
import com.google.backend.trading.model.user.UserInfo;

import java.util.List;

/**
 * @author david.chen
 * @date 2021/12/22 19:37
 */
public interface UserAlarmPriceService {
    boolean setAlarmPrice(AlarmPriceSetReq alarmPriceSetReq, UserInfo userInfo);

    boolean delAlarmPrice(AlarmPriceDelReq alarmPriceDelReq, UserInfo userInfo);

    List<TradeUserAlarmPrice> queryAll();

    boolean updateById(TradeUserAlarmPrice tradeUserAlarmPrice);

    List<TradeUserAlarmPrice> getAlarmPriceByUserIdAndSymbol(String symbol, String uid);
}
