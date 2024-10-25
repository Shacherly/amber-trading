package com.google.backend.trading.service;

import com.google.backend.trading.dao.model.TradeUserTradeSetting;
import com.google.backend.trading.model.common.model.config.UserTradeSettingVo;
import com.google.backend.trading.model.common.model.riskcontrol.UserSettingRes;
import com.google.backend.trading.model.margin.dto.MarginInfo;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author adam.wang
 * @date 2021/10/4 10:41
 */
public interface UserTradeSettingService {


    /**
     * 通过uid查询用户交易配置信息
     * @param uid
     * @return
     */
    TradeUserTradeSetting queryTradeSettingByUid(String uid);

    /**
     * 通过uid查询用户交易配置信息
     * @param uid
     * @return
     */
    UserTradeSettingVo queryTradeSetting(String uid);

    /**
     * 查询用户的配置信息
     * @param uids
     * @return
     */
    List<UserSettingRes> queryUserSetting(List<String> uids);

    UserTradeSettingVo tradeUserTradeSetting2Vo(TradeUserTradeSetting tradeUserTradeSetting, MarginInfo marginInfo);

    BigDecimal calMinStopLoss(BigDecimal unpnl);

    @Deprecated
    BigDecimal calMaxStopLoss(BigDecimal totalMargin);

    BigDecimal calMinTakeProfit(BigDecimal unpnl);
}
