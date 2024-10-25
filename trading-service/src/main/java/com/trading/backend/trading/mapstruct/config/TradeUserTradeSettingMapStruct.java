package com.google.backend.trading.mapstruct.config;

import com.google.backend.trading.dao.model.TradeUserTradeSetting;
import com.google.backend.trading.model.common.model.config.UserTradeSettingVo;
import com.google.backend.trading.model.common.model.riskcontrol.UserSettingRes;
import com.google.backend.trading.model.config.api.UserTradeSettingUpdateReq;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

/**
 * @author adam.wang
 * @date 2021/10/5 17:46
 */
@Mapper(componentModel="spring")
public interface TradeUserTradeSettingMapStruct {

    /**
     * TradeUserTradeSetting2Vo
     * @param tradeUserTradeSetting
     * @return
     */
    UserTradeSettingVo tradeUserTradeSetting2Vo(TradeUserTradeSetting tradeUserTradeSetting);


    /**
     * vo2TradeUserTradeSetting
     * @param userTradeSettingVo
     * @return
     */
    TradeUserTradeSetting vo2TradeUserTradeSetting(UserTradeSettingVo userTradeSettingVo);

    TradeUserTradeSetting req2TradeUserTradeSetting(UserTradeSettingUpdateReq req);

    /**
     * tradeUserTradeSetting2Leverage
     * @param tradeUserTradeSetting
     * @return
     */
    @Mappings({
            @Mapping(target = "fillNegative",source = "autoFixNegative"),
            @Mapping(target = "earnLiquid",source = "liquidEarn"),
    })
    UserSettingRes tradeUserTradeSetting2UserSettingRes(TradeUserTradeSetting tradeUserTradeSetting);
    /**
     * tradeUserTradeSettings2Leverages
     * @param tradeUserTradeSettings
     * @return
     */
    List<UserSettingRes> tradeUserTradeSettings2UserSettingRes(List<TradeUserTradeSetting> tradeUserTradeSettings);
}
