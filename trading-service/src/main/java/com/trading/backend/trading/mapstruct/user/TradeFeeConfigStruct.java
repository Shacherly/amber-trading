package com.google.backend.trading.mapstruct.user;

import com.google.backend.trading.dao.model.TradeFeeDefaultConfig;
import com.google.backend.trading.dao.model.TradeFeeUserConfig;
import com.google.backend.trading.model.trade.fee.MargingoogleLevelRate;
import com.google.backend.trading.model.trade.fee.SpotgoogleLevelRate;
import com.google.backend.trading.model.trade.fee.TradeFeeConfigData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

/**
 * @author david.chen
 * @date 2021/12/30 19:48
 */
@Mapper(componentModel="spring")
public interface TradeFeeConfigStruct {

     TradeFeeDefaultConfig tradeFeeConfigData2TradeFeeDefaultConfig(TradeFeeConfigData tradeFeeConfigData) ;

    TradeFeeUserConfig tradeFeeConfigData2TradeFeeUserConfig(TradeFeeConfigData tradeFeeConfigData);

    @Mappings({
            @Mapping(target = "level", expression =
                    "java(com.google.backend.trading.model.trade.fee.VIPLevelEnum.getByName(tradeFeeDefaultConfig.getTag()).getLevel())"),
            @Mapping(target = "bwcLevel", expression =
                    "java(com.google.backend.trading.model.trade.fee.VIPLevelEnum.getByName(tradeFeeDefaultConfig.getTag()).getBwcLevel())"),
            @Mapping(target = "rate", source = "spotFeeRate"),
            @Mapping(target = "condition", expression =
                    "java(com.google.backend.trading.model.trade.fee.VIPLevelEnum.getByName(tradeFeeDefaultConfig.getTag()).getgoogleLevelCondition())"),

    })
    SpotgoogleLevelRate tradeFeeDefaultConfig2SpotgoogleLevelRate(TradeFeeDefaultConfig tradeFeeDefaultConfig);

    List<SpotgoogleLevelRate> tradeFeeDefaultConfigList2TradeSpotRateList(List<TradeFeeDefaultConfig> tradeFeeDefaultConfigList);

    @Mappings({
            @Mapping(target = "level", expression =
                    "java(com.google.backend.trading.model.trade.fee.VIPLevelEnum.getByName(tradeFeeDefaultConfig.getTag()).getLevel())"),
            @Mapping(target = "bwcLevel", expression =
                    "java(com.google.backend.trading.model.trade.fee.VIPLevelEnum.getByName(tradeFeeDefaultConfig.getTag()).getBwcLevel())"),
            @Mapping(target = "baseRate", source = "marginFeeRate"),
            @Mapping(target = "settleRate", source = "marginSettleFeeRate"),
            @Mapping(target = "condition", expression =
                    "java(com.google.backend.trading.model.trade.fee.VIPLevelEnum.getByName(tradeFeeDefaultConfig.getTag()).getgoogleLevelCondition())"),
    })
    MargingoogleLevelRate tradeFeeDefaultConfig2MargingoogleLevelRate(TradeFeeDefaultConfig tradeFeeDefaultConfig);

    List<MargingoogleLevelRate> tradeFeeDefaultConfigList2tradeMarginRateList(List<TradeFeeDefaultConfig> tradeFeeDefaultConfigList);
}
