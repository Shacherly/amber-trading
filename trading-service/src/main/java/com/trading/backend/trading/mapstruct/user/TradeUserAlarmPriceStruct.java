package com.google.backend.trading.mapstruct.user;

import com.google.backend.trading.dao.model.TradeUserAlarmPrice;
import com.google.backend.trading.model.user.AlarmPriceSetReq;
import com.google.backend.trading.model.user.UserAlarmPriceReq;
import com.google.backend.trading.model.user.UserAlarmPriceRes;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * 预警数据类型转换
 * @author david.chen
 * @date 2021/12/23 11:55
 */
@Mapper(componentModel="spring")
public interface TradeUserAlarmPriceStruct {

    UserAlarmPriceRes tradeUserAlarmPrice2UserAlarmPriceRes(TradeUserAlarmPrice tradeUserAlarmPrice);
    List<UserAlarmPriceRes> tradeUserAlarmPrice2UserAlarmPriceRes(List<TradeUserAlarmPrice> alarmPriceList);
    TradeUserAlarmPrice alarmPriceReq2TradeUserAlarmPrice(AlarmPriceSetReq alarmPriceSetReq);

}
