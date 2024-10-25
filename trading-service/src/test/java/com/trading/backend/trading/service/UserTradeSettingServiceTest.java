package com.google.backend.trading.service;

import com.google.backend.trading.TradingServiceApplicationTest;
import com.google.backend.trading.dao.model.TradeUserTradeSetting;
import org.junit.Test;

import javax.annotation.Resource;

/**
 * @author adam.wang
 * @date 2021/10/4 10:46
 */
public class UserTradeSettingServiceTest extends TradingServiceApplicationTest {

    @Resource
    UserTradeSettingService userTradeSettingService;
    @Test
    public void query(){
        TradeUserTradeSetting tradeUserTradeSetting = userTradeSettingService.queryTradeSettingByUid("11");
        System.out.println("tradeUserTradeSetting = {}"+tradeUserTradeSetting.getLeverage());
    }
}
