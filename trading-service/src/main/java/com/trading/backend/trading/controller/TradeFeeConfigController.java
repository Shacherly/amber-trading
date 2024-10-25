package com.google.backend.trading.controller;

import com.google.backend.trading.dao.model.TradeFeeDefaultConfig;
import com.google.backend.trading.mapstruct.user.TradeFeeConfigStruct;
import com.google.backend.trading.model.common.Response;
import com.google.backend.trading.model.trade.TradeLevelEnum;
import com.google.backend.trading.model.trade.fee.MargingoogleLevelRate;
import com.google.backend.trading.model.trade.fee.SpotgoogleLevelRate;
import com.google.backend.trading.model.trade.fee.TradeFeeConfigRes;
import com.google.backend.trading.model.trade.fee.TradeLevelRate;
import com.google.backend.trading.model.trade.fee.UserFeeConfigRate;
import com.google.backend.trading.model.user.UserInfo;
import com.google.backend.trading.service.TradeFeeConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.List;

/**
 * @author david.chen
 * @date 2022/1/4 11:10
 */
@Slf4j
@Api(value = "手续费展示", tags = "手续费相关接口")
@RestController
@Validated
@RequestMapping("/v1/config/fee")
public class TradeFeeConfigController {

    @Autowired
    private TradeFeeConfigService tradeFeeConfigService;
    @Resource
    private TradeFeeConfigStruct tradeFeeConfigStruct;

    @GetMapping
    @ApiOperation(value = "平台交易等级费率", notes = "平台交易等级费率")
    public Response<TradeFeeConfigRes> getFeeConfig(UserInfo userInfo) {
        String uid = null;
        if (userInfo != null) {
            uid = userInfo.getUid();
        }
        TradeFeeConfigRes tradeFeeConfigRes = new TradeFeeConfigRes();
        List<TradeFeeDefaultConfig> tradeFeeDefaultConfigList = tradeFeeConfigService.selectAllDefaultConfig();
        //用户手续费
        UserFeeConfigRate userFeeConfigRate = tradeFeeConfigService.selectUserFeeConfig(uid);
        tradeFeeConfigRes.setUserFeeConfigRate(userFeeConfigRate);
        //现货Level展示
        List<SpotgoogleLevelRate> tradeSpotRateList = tradeFeeConfigStruct.tradeFeeDefaultConfigList2TradeSpotRateList(tradeFeeDefaultConfigList);
        tradeSpotRateList.sort(Comparator.comparingInt(SpotgoogleLevelRate::getLevel));
        tradeFeeConfigRes.setTradeSpotRateList(tradeSpotRateList);
        //杠杠Level展示
        List<MargingoogleLevelRate> tradeMarginRateList = tradeFeeConfigStruct.tradeFeeDefaultConfigList2tradeMarginRateList(tradeFeeDefaultConfigList);
        tradeMarginRateList.sort(Comparator.comparingInt(MargingoogleLevelRate::getLevel));
        tradeFeeConfigRes.setTradeMarginRateList(tradeMarginRateList);
        //交易Level展示
        List<TradeLevelRate> tradeLevelRateList = TradeLevelEnum.getTradeLevelRateList();
        tradeLevelRateList.sort(Comparator.comparingInt(TradeLevelRate::getLevel));
        tradeFeeConfigRes.setTradeLevelRateList(tradeLevelRateList);
        return Response.ok(tradeFeeConfigRes);
    }

}
