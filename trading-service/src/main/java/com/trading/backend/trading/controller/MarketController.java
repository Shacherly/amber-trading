package com.google.backend.trading.controller;

import com.google.backend.trading.dao.model.TradeUserAlarmPrice;
import com.google.backend.trading.mapstruct.user.TradeUserAlarmPriceStruct;
import com.google.backend.trading.model.common.Response;
import com.google.backend.trading.model.favorite.api.FavoriteOverrideReq;
import com.google.backend.trading.model.favorite.api.FavoriteUpdateReq;
import com.google.backend.trading.model.favorite.api.LiteMarketSymbolVo;
import com.google.backend.trading.model.favorite.api.MarketSymbolVo;
import com.google.backend.trading.model.user.AlarmPriceDelReq;
import com.google.backend.trading.model.user.AlarmPriceSetReq;
import com.google.backend.trading.model.user.UserAlarmPriceReq;
import com.google.backend.trading.model.user.UserAlarmPriceRes;
import com.google.backend.trading.model.user.UserInfo;
import com.google.backend.trading.service.MarketService;
import com.google.backend.trading.service.UserAlarmPriceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * 用户收藏相关接口
 *
 * @author adam.wang
 * @date 2021/10/4 16:05
 */
@Api(value = "交易市场相关接口", tags = "交易市场相关接口")
@RestController
@Validated
@RequestMapping("/v1/market")
public class MarketController {

    @Resource
    private MarketService marketService;
    @Resource
    private UserAlarmPriceService userAlarmPriceService;
    @Resource
    private TradeUserAlarmPriceStruct tradeUserAlarmPriceStruct;

    @PostMapping("/favorite/override")
    @ApiOperation(value = "覆盖更新收藏")
    public Response<Void> favoriteOverride(@Valid @RequestBody FavoriteOverrideReq req, UserInfo userInfo) {
        marketService.favoriteOverride(userInfo.getUid(), req.getSymbolList().toArray(new String[0]));
        return Response.ok();
    }

    @PostMapping("/favorite/update")
    @ApiOperation(value = "更新收藏")
    public Response<Void> favoriteUpdate(@Valid @RequestBody FavoriteUpdateReq req, UserInfo userInfo) {
        marketService.favoriteUpdate(userInfo.getUid(), req.getSymbol(), req.getFavorite());
        return Response.ok();
    }

    @GetMapping("/list")
    @ApiOperation(value = "市场币对列表", notes = "市场币对列表")
    public Response<List<MarketSymbolVo>> listAllFavorite(@ApiParam(allowableValues = "SPOT, MARGIN") @RequestParam String type, UserInfo userInfo) {
        String uid = null == userInfo ? null : userInfo.getUid();
        return Response.ok(marketService.marketSymbolList(uid, type, false));
    }

    @PostMapping("/alarm-price")
    @ApiOperation(value = "预警价格设置", notes = "预警价格设置")
    public Response alarmPrice(@Valid @RequestBody AlarmPriceSetReq alarmPriceSetReq, UserInfo userInfo) {
        userAlarmPriceService.setAlarmPrice(alarmPriceSetReq, userInfo);
        return Response.ok();
    }

    @PostMapping("/del/alarm-price")
    @ApiOperation(value = "删除预警价格设置", notes = "删除预警价格设置")
    public Response delAlarmPrice(@Valid @RequestBody AlarmPriceDelReq alarmPriceDelReq, UserInfo userInfo) {
        userAlarmPriceService.delAlarmPrice(alarmPriceDelReq, userInfo);
        return Response.ok();
    }

    @GetMapping("/alarm-price")
    @ApiOperation(value = "获取币种预警设置", notes = "获取币种预警设置")
    public Response<List<UserAlarmPriceRes>> getAlarmPrice(@Valid UserAlarmPriceReq userAlarmPriceReq, UserInfo userInfo) {
        List<TradeUserAlarmPrice> tradeUserAlarmPriceList = userAlarmPriceService.getAlarmPriceByUserIdAndSymbol(userAlarmPriceReq.getSymbol(), userInfo.getUid());
        List<UserAlarmPriceRes> userAlarmPriceResList = tradeUserAlarmPriceStruct.tradeUserAlarmPrice2UserAlarmPriceRes(tradeUserAlarmPriceList);
        return Response.ok(userAlarmPriceResList);
    }

    @PostMapping("lite/top/update")
    @ApiOperation(value = "lite版本 设置/取消market置顶", notes = "lite版本 设置/取消market置顶")
    public Response topMarket(@Valid @RequestBody FavoriteUpdateReq req, UserInfo userInfo) {
        marketService.favoriteUpdateLite(userInfo.getUid(), req.getSymbol(), req.getFavorite());
        return Response.ok();
    }

    @GetMapping("lite/top")
    @ApiOperation(value = "lite版本 获取market列表", notes = "lite版本 获取market列表")
    public Response<List<LiteMarketSymbolVo>> getLiteMarket(UserInfo userInfo) {
        List<LiteMarketSymbolVo> liteMarketSymbolVos = marketService.getLiteMarket(userInfo);
        return Response.ok(liteMarketSymbolVos);
    }

}
