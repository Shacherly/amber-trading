package com.google.backend.trading.controller;

import com.google.backend.trading.framework.web.RequestUnderlineToCamel;
import com.google.backend.trading.model.common.PageResult;
import com.google.backend.trading.model.common.Response;
import com.google.backend.trading.model.swap.SwapType;
import com.google.backend.trading.model.swap.api.CoinBalanceRes;
import com.google.backend.trading.model.swap.api.QuickSwapInfoReq;
import com.google.backend.trading.model.swap.api.QuickSwapInfoRes;
import com.google.backend.trading.model.swap.api.QuickSwapOrderPlaceReq;
import com.google.backend.trading.model.swap.api.QuickSwapPriceReq;
import com.google.backend.trading.model.swap.api.SwapOrderHistoryLiteReq;
import com.google.backend.trading.model.swap.api.SwapOrderHistoryReq;
import com.google.backend.trading.model.swap.api.SwapOrderLiteRes;
import com.google.backend.trading.model.swap.api.SwapOrderPlaceReq;
import com.google.backend.trading.model.swap.api.SwapOrderRes;
import com.google.backend.trading.model.swap.api.SwapPriceReq;
import com.google.backend.trading.model.swap.api.SwapPriceRes;
import com.google.backend.trading.model.swap.api.SwapQueryReq;
import com.google.backend.trading.model.swap.dto.SwapOrderPlace;
import com.google.backend.trading.model.trade.SourceType;
import com.google.backend.trading.model.trade.TradeType;
import com.google.backend.trading.model.user.UserInfo;
import com.google.backend.trading.service.AssetRequest;
import com.google.backend.trading.service.SwapService;
import com.google.backend.trading.service.UserService;
import com.google.backend.trading.util.CommonUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * 兑换交易接口
 * @author trading
 * @date 2021/9/27 14:14
 */
@Api(value = "兑换相关接口", tags = "兑换相关接口")
@RestController
@Validated
@RequestMapping("/v1/swap")
public class SwapController {

    @Autowired
    private SwapService swapService;
    @Autowired
    private AssetRequest assetRequest;
    @Autowired
    private UserService userService;


    @ApiOperation(value = "兑换报价接口")
    @GetMapping("/price")
    public Response<SwapPriceRes> price(@Valid SwapPriceReq req, UserInfo userInfo) {
        String uid = null == userInfo ? null : userInfo.getUid();
        req.setFeeFree(false);
        if (null == req.getMode()) {
            req.setMode(SwapType.OBTAINED.getName());
        }
        if (null == req.getQuantity()) {
            req.setQuantity(BigDecimal.ZERO);
        }
        if (req.getFromCoin().equals(req.getToCoin())) {
            throw new IllegalArgumentException("from coin and to coin can't be the same");
        }
        return Response.ok(swapService.queryPrice(req, uid));
    }

    @ApiOperation(value = "查询币种可用余额")
    @GetMapping("/balance")
    public Response<List<CoinBalanceRes>> balance(@RequestParam(required = false) String coin, UserInfo userInfo) {
        List<CoinBalanceRes> resp = assetRequest.querySwapCoinBalance(userInfo.getUid(), coin, false);
        return Response.ok(resp);
    }

    @ApiOperation(value = "查询lite币种可用余额")
    @GetMapping("/balance/lite")
    public Response<List<CoinBalanceRes>> balanceLite(@RequestParam(required = false) String coin, UserInfo userInfo) {
        List<CoinBalanceRes> resp = assetRequest.querySwapCoinBalance(userInfo.getUid(), coin, true);
        return Response.ok(resp);
    }


    @ApiOperation(value = "上次兑换订单查询")
    @GetMapping("/order/last")
    public Response<SwapOrderRes> orderLast(UserInfo userInfo) {
        SwapOrderRes resp = swapService.queryLast(userInfo.getUid());
        return Response.ok(resp);
    }

    @ApiOperation(value = "指定订单ID查询")
    @GetMapping("/order/query")
    public Response<SwapOrderRes> orderQuery(@Valid SwapQueryReq req, UserInfo userInfo) {
        SwapOrderRes resp = swapService.querySwapOrder(req.getOrderId(), userInfo.getUid());
        return Response.ok(resp);
    }

    @ApiOperation(value = "兑换历史查询")
    @GetMapping("/order/history")
    public Response<PageResult<SwapOrderRes>> orderHistory(@Valid @RequestUnderlineToCamel SwapOrderHistoryReq req, UserInfo userInfo) {
        return Response.ok(swapService.queryHistory(req, userInfo.getUid()));
    }

    @ApiOperation(value = "lite版本 兑换历史查询")
    @GetMapping("/order/history/lite")
    public Response<PageResult<SwapOrderLiteRes>> liteOrderHistory(@Valid @RequestUnderlineToCamel SwapOrderHistoryLiteReq req, UserInfo userInfo) {
        return Response.ok(swapService.queryHistoryLite(req, userInfo.getUid()));
    }

    @ApiOperation(value = "兑换接口")
    @PostMapping("/order/place")
    public Response<SwapOrderRes> orderPlace(@Valid @RequestBody SwapOrderPlaceReq req, UserInfo userInfo) {
        if (req.getFromCoin().equals(req.getToCoin())) {
            throw new IllegalArgumentException("from coin and to coin can't be the same");
        }
        userService.checkUserKycOnlyOrThrowE(userInfo, TradeType.SWAP);
        SwapOrderPlace place = new SwapOrderPlace();
        place.setFromCoin(req.getFromCoin());
        place.setToCoin(req.getToCoin());
        place.setMode(SwapType.getByName(req.getMode()));
        place.setPrice(req.getPrice());
        place.setQuantity(req.getQuantity());
        place.setUid(userInfo.getUid());
        place.setSource(SourceType.PLACED_BY_CLIENT);
        if (SwapType.isPayment(req.getMode())) {
            place.setReqCoin(req.getFromCoin());
        } else {
            place.setReqCoin(req.getToCoin());
        }
        SwapOrderRes res = swapService.placeOrder(place);
        return Response.ok(res);
    }


    @ApiOperation(value = "lite版本 行情兑换接口")
    @PostMapping("/lite/order/place")
    public Response<SwapOrderRes> liteOrderPlace(@Valid @RequestBody SwapOrderPlaceReq req, UserInfo userInfo) {
        if (req.getFromCoin().equals(req.getToCoin())) {
            throw new IllegalArgumentException("from coin and to coin can't be the same");
        }
        userService.checkUserKycOnlyOrThrowE(userInfo, TradeType.SWAP);
        SwapOrderPlace place = new SwapOrderPlace();
        place.setFromCoin(req.getFromCoin());
        place.setToCoin(req.getToCoin());
        place.setMode(SwapType.getByName(req.getMode()));
        place.setPrice(req.getPrice());
        place.setQuantity(req.getQuantity());
        place.setUid(userInfo.getUid());
        place.setSource(SourceType.PLACED_BY_CLIENT);
        place.setLiteMarketSwap(true);
        if (SwapType.isPayment(req.getMode())) {
            place.setReqCoin(req.getFromCoin());
        } else {
            place.setReqCoin(req.getToCoin());
        }
        SwapOrderRes res = swapService.placeOrder(place);
        return Response.ok(res);
    }

    /**
     * 首页的快捷买币
     *
     * @param req
     * @param info
     * @return
     */
    @ApiOperation(value = "快捷兑换接口")
    @PostMapping("/quick/order/place")
    public Response<SwapOrderRes> quickOrderPlace(@Valid @RequestBody QuickSwapOrderPlaceReq req, UserInfo info) {
        Pair<String, String> coinPair = CommonUtils.coinPair(req.getSymbol());
        if (coinPair.getFirst().equals(coinPair.getSecond())) {
            throw new IllegalArgumentException("from coin and to coin can't be the same");
        }
        userService.checkUserKycOnlyOrThrowE(info, TradeType.SWAP);
        SwapOrderRes res = swapService.quickSwapPlace(req, info.getUid());
        return Response.ok(res);
    }

    /**
     * 首页的快捷买币
     * @param req
     * @param info
     * @return
     */
    @ApiOperation(value = "快捷兑换信息")
    @GetMapping("/quick/info/")
    public Response<QuickSwapInfoRes> quickInfo(@Valid QuickSwapInfoReq req, UserInfo info) {
        QuickSwapInfoRes res = swapService.quickSwapInfo(req.getSymbol(), req.getDirection(), info.getUid());
        return Response.ok(res);
    }

    /**
     * 首页的快捷买币
     *
     * @param req
     * @return
     */
    @ApiOperation(value = "快捷兑换报价接口")
    @GetMapping("/quick/price")
    public Response<BigDecimal> quickPrice(@Valid QuickSwapPriceReq req, UserInfo info) {
        Pair<String, String> coinPair = CommonUtils.coinPair(req.getSymbol());
        if (coinPair.getFirst().equals(coinPair.getSecond())) {
            throw new IllegalArgumentException("from coin and to coin can't be the same");
        }
        return Response.ok(swapService.quickSwapPrice(req, Optional.ofNullable(info).map(UserInfo::getUid).orElse(null)));
    }

}
