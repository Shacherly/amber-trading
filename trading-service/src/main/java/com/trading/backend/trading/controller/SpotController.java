package com.google.backend.trading.controller;

import com.google.backend.trading.component.SymbolDomain;
import com.google.backend.trading.model.common.PageResult;
import com.google.backend.trading.model.common.Response;
import com.google.backend.trading.model.spot.api.SpotAvailableReq;
import com.google.backend.trading.model.spot.api.SpotAvailableRes;
import com.google.backend.trading.model.spot.api.SpotDetailRes;
import com.google.backend.trading.model.spot.api.SpotOrderActiveReq;
import com.google.backend.trading.model.spot.api.SpotOrderCancelReq;
import com.google.backend.trading.model.spot.api.SpotOrderHistoryReq;
import com.google.backend.trading.model.spot.api.SpotOrderInfoRes;
import com.google.backend.trading.model.spot.api.SpotOrderPlaceReq;
import com.google.backend.trading.model.spot.api.SpotOrderPlaceRes;
import com.google.backend.trading.model.spot.api.SpotOrderUpdateReq;
import com.google.backend.trading.model.spot.dto.SpotOrderCancel;
import com.google.backend.trading.model.spot.dto.SpotOrderPlace;
import com.google.backend.trading.model.trade.Direction;
import com.google.backend.trading.model.trade.OrderType;
import com.google.backend.trading.model.trade.SourceType;
import com.google.backend.trading.model.trade.TradeStrategy;
import com.google.backend.trading.model.trade.TradeTerminator;
import com.google.backend.trading.model.trade.TradeType;
import com.google.backend.trading.model.trade.TriggerType;
import com.google.backend.trading.model.user.UserInfo;
import com.google.backend.trading.service.SpotService;
import com.google.backend.trading.service.TradeAssetService;
import com.google.backend.trading.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.math.BigDecimal;


/**
 * 现货交易接口
 * @author trading
 * @date 2021/9/27 14:13
 */
@Api(value="现货相关接口",tags="现货相关接口")
@RestController
@Validated
@RequestMapping("/v1/spot")
public class SpotController {

    @Autowired
    private SpotService spotService;

    @Autowired
    private TradeAssetService tradeAssetService;

    @Autowired
    private UserService userService;

    @ApiOperation(value = "现货详情")
    @GetMapping("/detail")
    public Response<SpotDetailRes> detail(@RequestParam String symbol, UserInfo userInfo) {
        String uid = null;
        if (null != userInfo) {
            uid = userInfo.getUid();
        }
        return Response.ok(spotService.detail(uid, symbol));
    }

    @ApiOperation(value = "现货可用余额查询")
    @GetMapping("/available")
    public Response<SpotAvailableRes> available(@Valid SpotAvailableReq req, UserInfo userInfo){
        BigDecimal quote = tradeAssetService.spotAvailable(userInfo.getUid(), req.getSymbol(), Direction.BUY);
        BigDecimal base = tradeAssetService.spotAvailable(userInfo.getUid(), req.getSymbol(), Direction.SELL);
        SpotAvailableRes res = new SpotAvailableRes();
        res.setBaseAvailable(base);
        res.setQuoteAvailable(quote);
        return Response.ok(res);
    }

    @ApiOperation(value = "现货下单接口")
    @PostMapping("/order/place")
    public Response<SpotOrderPlaceRes> orderPlace(@Valid @RequestBody SpotOrderPlaceReq req, UserInfo userInfo){
        userService.checkUserKycOnlyOrThrowE(userInfo, TradeType.SPOT);
        SpotOrderPlace place = new SpotOrderPlace();
        place.setUid(userInfo.getUid());
        place.setSymbol(req.getSymbol());
        place.setType(OrderType.getByCode(req.getType()));
        if (place.getType().isLimitOrder()) {
            place.setStrategy(TradeStrategy.getByCode(req.getStrategy()));
            place.setPrice(req.getPrice());
        }
        place.setDirection(Direction.getByCode(req.getDirection()));
        place.setIsQuote(req.getIsQuote());
        place.setQuantity(req.getQuantity());

        if (place.getType().isTriggerOrder()) {
            place.setTriggerPrice(req.getTriggerPrice());
            place.setTriggerCompare(TriggerType.getByCode(req.getTriggerCompare()));
        }
        place.setSource(SourceType.PLACED_BY_CLIENT);
        place.setNotes(req.getNotes());
        return Response.ok(spotService.placeOrder(place));
    }

    @ApiOperation(value = "现货委托活跃列表")
    @GetMapping("/order/active")
    public Response<PageResult<SpotOrderInfoRes>> orderActive(@Valid SpotOrderActiveReq req, UserInfo userInfo) {
        if (StringUtils.isNotBlank(req.getSymbol())) {
            SymbolDomain.checkoutSymbol(req.getSymbol());
        }
        PageResult<SpotOrderInfoRes> res = spotService.orderActive(req, userInfo.getUid());
        return Response.ok(res);
    }

    @ApiOperation(value = "现货委托历史列表")
    @GetMapping("/order/history")
    public Response<PageResult<SpotOrderInfoRes>> orderHistory(@Valid SpotOrderHistoryReq req, UserInfo userInfo){
        return Response.ok(spotService.orderHistory(req, userInfo.getUid()));
    }

    @ApiOperation(value = "订单修改接口")
    @PostMapping("/order/update")
    public Response<String> orderUpdate(@Valid @RequestBody SpotOrderUpdateReq orderUpdateReq, UserInfo userInfo){
        String orderId = spotService.updateOrder(orderUpdateReq, userInfo.getUid());
        return Response.ok(orderId);
    }

    @ApiOperation(value = "现货撤单接口")
    @PostMapping("/order/cancel")
    public Response<String> orderCancel(@Valid @RequestBody SpotOrderCancelReq req, UserInfo userInfo) {
        SpotOrderCancel cancel = new SpotOrderCancel();
        cancel.setOrderId(req.getOrderId());
        cancel.setUid(userInfo.getUid());
        cancel.setTerminator(TradeTerminator.CLIENT);
        String cancelId = spotService.cancelOrder(cancel);
        return Response.ok(cancelId);
    }

}
