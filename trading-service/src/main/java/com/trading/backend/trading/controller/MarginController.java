package com.google.backend.trading.controller;

import com.google.backend.trading.component.SymbolDomain;
import com.google.backend.trading.constant.Constants;
import com.google.backend.trading.exception.BusinessException;
import com.google.backend.trading.exception.BusinessExceptionEnum;
import com.google.backend.trading.model.common.PageResult;
import com.google.backend.trading.model.common.Response;
import com.google.backend.trading.model.margin.api.ActiveOrderReq;
import com.google.backend.trading.model.margin.api.ActiveOrderRes;
import com.google.backend.trading.model.margin.api.ActivePositionInfoVo;
import com.google.backend.trading.model.margin.api.HistoryPositionDetailRes;
import com.google.backend.trading.model.margin.api.HistoryPositionRes;
import com.google.backend.trading.model.margin.api.MarginAssetInfoRes;
import com.google.backend.trading.model.margin.api.MarginDetailRes;
import com.google.backend.trading.model.margin.api.MarginOrderCancelReq;
import com.google.backend.trading.model.margin.api.MarginOrderConfirmReq;
import com.google.backend.trading.model.margin.api.MarginOrderConfirmRes;
import com.google.backend.trading.model.margin.api.MarginOrderDetailReq;
import com.google.backend.trading.model.margin.api.MarginOrderDetailRes;
import com.google.backend.trading.model.margin.api.MarginOrderHistoryReq;
import com.google.backend.trading.model.margin.api.MarginOrderInfoRes;
import com.google.backend.trading.model.margin.api.MarginOrderModifyReq;
import com.google.backend.trading.model.margin.api.MarginOrderPlaceReq;
import com.google.backend.trading.model.margin.api.PositionCloseHistoryReq;
import com.google.backend.trading.model.margin.api.PositionCloseHistoryRes;
import com.google.backend.trading.model.margin.api.PositionCloseReq;
import com.google.backend.trading.model.margin.api.PositionDetailReq;
import com.google.backend.trading.model.margin.api.PositionFundingCostReq;
import com.google.backend.trading.model.margin.api.PositionFundingCostVo;
import com.google.backend.trading.model.margin.api.PositionHistoryDetailReq;
import com.google.backend.trading.model.margin.api.PositionHistorySearchReq;
import com.google.backend.trading.model.margin.api.PositionRecordVo;
import com.google.backend.trading.model.margin.api.PositionRecordeReq;
import com.google.backend.trading.model.margin.api.PositionSettingReq;
import com.google.backend.trading.model.margin.api.PositionSettleHistoryReq;
import com.google.backend.trading.model.margin.api.PositionSettleHistoryRes;
import com.google.backend.trading.model.margin.api.PositionSettleInfoRes;
import com.google.backend.trading.model.margin.api.PositionSettleReq;
import com.google.backend.trading.model.margin.api.PriceReq;
import com.google.backend.trading.model.margin.api.PriceRes;
import com.google.backend.trading.model.margin.dto.MarginOrderCancel;
import com.google.backend.trading.model.margin.dto.MarginOrderPlace;
import com.google.backend.trading.model.margin.dto.PositionSettle;
import com.google.backend.trading.model.trade.Direction;
import com.google.backend.trading.model.trade.OrderType;
import com.google.backend.trading.model.trade.SourceType;
import com.google.backend.trading.model.trade.TradeStrategy;
import com.google.backend.trading.model.trade.TradeTerminator;
import com.google.backend.trading.model.trade.TriggerType;
import com.google.backend.trading.model.user.UserInfo;
import com.google.backend.trading.service.MarginService;
import com.google.backend.trading.service.TradeFeeConfigService;
import com.google.backend.trading.service.UserService;
import com.google.backend.trading.util.CommonUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;


/**
 * 杠杆订单和仓位接口
 *
 * @author trading
 * @date 2021/9/27 14:14
 */
@Api(tags = "杠杆订单和仓位接口")
@Slf4j
@Validated
@RequestMapping("/v1/margin")
@RestController
public class MarginController {

    @Resource
    private MarginService marginService;
    @Autowired
    private UserService userService;
    @Autowired
    private TradeFeeConfigService tradeFeeConfigService;

    /**
     * 1、无仓位的时候 &（负总保证金）
     * 风险率值 = --
     * 风险等级 = Low （最低的一个等级）
     * 持仓杠杆 = --
     * 2、有仓位的时候 &（负总保证金）
     * 风险率值 = 100%
     * 风险等级 = Very high （即最高的一个等级）
     * 持仓杠杆 = (1/维持保证金率)
     * @param userInfo
     * @return
     */
    @GetMapping("/info")
    @ApiOperation(value = "查询用户杠杆信息", notes = "查询用户杠杆信息")
    public Response<MarginAssetInfoRes> marginAssetInfo(UserInfo userInfo) {
        MarginAssetInfoRes res = marginService.marginAssetInfo(userInfo.getUid());
        if (CollectionUtils.isEmpty(res.getCurrentPositionVos()) && res.getTotalLiquidMargin().compareTo(BigDecimal.ZERO) < 0) {
            res.setRiskRate(null);
            res.setCurrentLeverage(null);
        }
        return Response.ok(res);
    }


    @GetMapping("/detail")
    @ApiOperation(value = "杠杆详情，支持游客", notes = "杠杆详情")
    public Response<MarginDetailRes> detail(@RequestParam String symbol, @RequestParam String direction, UserInfo userInfo) {
        String uid = null;
        if (null != userInfo) {
            uid = userInfo.getUid();
        }
        return Response.ok(marginService.detail(uid, symbol, direction));
    }

    @GetMapping("/price")
    @ApiOperation(value = "查询实时价格", notes = "查询实时价格，按照数量和方向提供")
    public Response<PriceRes> price(@Valid PriceReq req) {
        SymbolDomain symbolDomain = SymbolDomain.nonNullGet(req.getSymbol());
        BigDecimal price;
        if (StringUtils.isBlank(req.getDirection())) {
            price = symbolDomain.midPrice();
        } else if (null == req.getQuantity()) {
            price = symbolDomain.price(Direction.getByName(req.getDirection()));
        } else {
            price = symbolDomain.priceByQuantity(req.getQuantity(), Direction.getByName(req.getDirection()));
        }
        PriceRes res = new PriceRes(price);
        return Response.ok(res);
    }

    @PostMapping("/order/place")
    @ApiOperation(value = "杠杆下单接口", notes = "杠杆下单接口")
    public Response<MarginOrderInfoRes> orderPlace(@Valid @RequestBody MarginOrderPlaceReq req, UserInfo userInfo) {
        String uid = userInfo.getUid();
        MarginOrderPlace place = MarginOrderPlace.builder()
                .uid(uid)
                .direction(Direction.getByCode(req.getDirection()))
                .quantity(req.getQuantity())
                .type(OrderType.getByCode(req.getType()))
                .symbol(req.getSymbol())
                .source(SourceType.PLACED_BY_CLIENT)
                .notes(req.getNotes())
                .build();
        //Check USA KYC user
        userService.checkUserComplianceOrThrowE(userInfo);
        if (place.getType().isLimitOrder()) {
            place.setPrice(req.getPrice());
            if (StringUtils.isNotBlank(req.getStrategy())) {
                TradeStrategy strategy = TradeStrategy.getByName(req.getStrategy());
                place.setStrategy(strategy);
            }
        }
        if (place.getType().isTriggerOrder()) {
            place.setTriggerPrice(req.getTriggerPrice());
            place.setTriggerCompare(TriggerType.getByCode(req.getTriggerCompare()));
        }
        place.setReduceOnly(req.isReduceOnly());
        MarginOrderInfoRes res = marginService.placeOrder(place);
        return Response.ok(res);
    }

    @GetMapping("/order/confirm-info")
    @ApiOperation(value = "杠杆下单确认信息接口", notes = "杠杆下单确认信息接口")
    public Response<MarginOrderConfirmRes> orderConfirmInfo(@Valid MarginOrderConfirmReq req, UserInfo userInfo) {
        BigDecimal basePrice;
        BigDecimal baseUsdPrice;
        BigDecimal quoteUsdPrice;
        String direction = req.getDirection();
        BigDecimal quantity = req.getQuantity();
        String symbol = req.getSymbol();

        Pair<String, String> coinPair = CommonUtils.coinPair(symbol);
        String baseCoin = coinPair.getFirst();
        String quoteCoin = coinPair.getSecond();
        if (null == req.getPrice()) {
            basePrice = SymbolDomain.nonNullGet(symbol).price(direction);
        } else {
            basePrice = req.getPrice();
        }
        if (!Constants.BASE_COIN.equals(quoteCoin)) {
            quoteUsdPrice = SymbolDomain.nonNullGet(quoteCoin + Constants.BASE_QUOTE).price(direction);
            baseUsdPrice = basePrice.multiply(quoteUsdPrice);
        } else {
            quoteUsdPrice = BigDecimal.ONE;
            baseUsdPrice = basePrice;
        }
        BigDecimal feeRate = tradeFeeConfigService.selectUserFeeConfig(userInfo.getUid()).getMarginFeeRate();
        BigDecimal fee = quantity.multiply(baseUsdPrice).multiply(feeRate).setScale(Constants.USD_PRECISION, RoundingMode.UP);
        MarginOrderConfirmRes res = new MarginOrderConfirmRes();
        res.setFeeRate(feeRate);
        res.setFee(fee);
        res.setBaseUsdPrice(baseUsdPrice);
        res.setQuoteUsdPrice(quoteUsdPrice);
        return Response.ok(res);
    }


    @PostMapping("/order/update")
    @ApiOperation(value = "杠杆订单修改接口", notes = "杠杆订单修改接口")
    public Response<String> orderModify(@Valid @RequestBody MarginOrderModifyReq req, UserInfo userInfo) {
        marginService.modifyOrder(req, userInfo.getUid());
        return Response.ok();
    }

    @ApiOperation(value = "撤单")
    @PostMapping("/order/cancel")
    public Response<String> orderCancel(@Valid @RequestBody MarginOrderCancelReq req, UserInfo userInfo) {
        MarginOrderCancel cancel = MarginOrderCancel.builder()
                .orderId(req.getOrderId())
                .uid(userInfo.getUid())
                .terminator(TradeTerminator.CLIENT)
                .build();
        marginService.cancelOrder(cancel);
        return Response.ok();
    }

    @GetMapping("/order/active")
    @ApiOperation(value = "当前委托", notes = "当前委托")
    public Response<PageResult<ActiveOrderRes>> activeOrder(@Valid ActiveOrderReq req, UserInfo userInfo) {
        SymbolDomain.checkoutSymbol(req.getSymbol());
        PageResult<ActiveOrderRes> res = marginService.activeOrder(req, userInfo.getUid());
        return Response.ok(res);
    }

    @ApiOperation(value = "历史订单列表")
    @GetMapping("/order/history")
    public Response<PageResult<MarginOrderInfoRes>> orderHistory(@Valid MarginOrderHistoryReq req, UserInfo userInfo) {
        PageResult<MarginOrderInfoRes> res = marginService.orderHistory(req, userInfo.getUid());
        return Response.ok(res);
    }

    @ApiOperation(value = "订单详情")
    @GetMapping("/order/detail")
    public Response<MarginOrderDetailRes> orderDetail(@Valid MarginOrderDetailReq req, UserInfo userInfo) {
        MarginOrderDetailRes res = marginService.orderDetail(req, userInfo.getUid());
        return Response.ok(res);
    }

    @GetMapping("/position/active")
    @ApiOperation(value = "当前仓位", notes = "当前仓位")
    public Response<List<ActivePositionInfoVo>> activePosition(UserInfo userInfo) {
        List<ActivePositionInfoVo> res = marginService.positionActive(userInfo.getUid());
        return Response.ok(res);
    }

    @PostMapping("/position/close")
    @ApiOperation(value = "平仓", notes = "web平仓请求")
    public Response<MarginOrderInfoRes> positionClose(@Valid @RequestBody PositionCloseReq req, UserInfo userInfo) {
        req.setStrategy(TradeStrategy.GTC.getCode());
        return Response.ok(marginService.closePosition(req, userInfo.getUid()));
    }

    @PostMapping("/position/setting")
    @ApiOperation(value = "在持仓位设置", notes = "在持仓位设置")
    public Response<Void> setUpPosition(@Valid @RequestBody PositionSettingReq req, UserInfo userInfo) {
        BigDecimal slPrice = req.getSlPrice();
        BigDecimal tpPrice = req.getTpPrice();
        if (null != slPrice && slPrice.compareTo(BigDecimal.ZERO) < 0 && slPrice.compareTo(BigDecimal.ONE.negate()) != 0 ) {
            throw new IllegalArgumentException();
        }
        if (null != tpPrice && tpPrice.compareTo(BigDecimal.ZERO) < 0 && tpPrice.compareTo(BigDecimal.ONE.negate()) != 0 ) {
            throw new IllegalArgumentException();
        }
        int i = marginService.setUpPosition(req, userInfo.getUid());
        return i > 0 ? Response.ok() : Response.fail();
    }

    @ApiOperation(value = "仓位可交割信息")
    @GetMapping("/position/settle")
    public Response<PositionSettleInfoRes> positionSettleInfo(@NotBlank @RequestParam("position_id") String positionId, UserInfo userInfo) {
        return Response.ok(marginService.getSettlePositionInfo(positionId, userInfo.getUid()));
    }

    @ApiOperation(value = "交割")
    @PostMapping("/position/settle")
    public Response<String> positionSettle(@Valid @RequestBody PositionSettleReq req, UserInfo userInfo) {
        PositionSettle settle = PositionSettle.builder()
                .uid(userInfo.getUid())
                .positionId(req.getPositionId())
                .quantity(req.getQuantity())
                .source(SourceType.PLACED_BY_CLIENT)
                .all(req.isAll())
                .build();
        marginService.settlePosition(settle);
        return Response.ok();
    }

    @GetMapping("/position/detail")
    @ApiOperation(value = "持仓详情", notes = "持仓详情")
    public Response<ActivePositionInfoVo> positionDetail(@Valid PositionDetailReq req, UserInfo userInfo) {
        ActivePositionInfoVo res = marginService.positionDetail(req, userInfo.getUid());
        return Response.ok(res);
    }

    @GetMapping("/position/history")
    @ApiOperation(value = "历史持仓", notes = "历史持仓，时间跨度不能超过1年")
    public Response<HistoryPositionRes> positionHistory(@Valid PositionHistorySearchReq req, UserInfo userInfo) {
        long a = (req.getEndTime() - req.getStartTime()) / (1000 * 24 * 60 * 60);
        if (a > 366) {
            throw new BusinessException(BusinessExceptionEnum.SEARCH_OVER_RANGE);
        }
        HistoryPositionRes res = marginService.positionHistory(req, userInfo.getUid());
        return Response.ok(res);
    }

    @GetMapping("/position/history/detail")
    @ApiOperation(value = "历史持仓详情", notes = "历史持仓详情")
    public Response<HistoryPositionDetailRes> positionHistoryDetail(@Valid PositionHistoryDetailReq req, UserInfo userInfo) {

        HistoryPositionDetailRes res = marginService.positionHistoryDetail(req, userInfo.getUid());
        return Response.ok(res);
    }

    @GetMapping("/position/recorde")
    @ApiOperation(value = "仓位记录", notes = "仓位记录")
    public Response<PageResult<PositionRecordVo>> positionRecorde(@Valid PositionRecordeReq req, UserInfo userInfo) {
        PageResult<PositionRecordVo> res = marginService.positionRecord(req, userInfo.getUid());
        return Response.ok(res);
    }

    @GetMapping("/position/funding-cost")
    @ApiOperation(value = "仓位资金费率", notes = "仓位资金费率")
    public Response<PageResult<PositionFundingCostVo>> positionFundingCost(@Valid PositionFundingCostReq req, UserInfo userInfo) {
        PageResult<PositionFundingCostVo> res = marginService.positionFundingCost(req, userInfo.getUid());
        return Response.ok(res);
    }

    @GetMapping("/position/close/history")
    @ApiOperation(value = "web平仓记录列表", notes = "web平仓记录列表")
    public Response<PageResult<PositionCloseHistoryRes>> positionCloseHistory(@Valid PositionCloseHistoryReq req, UserInfo userInfo) {
        return Response.ok(marginService.positionCloseHistory(req, userInfo.getUid()));
    }

    @GetMapping("/position/settle/history")
    @ApiOperation(value = "web交割记录列表", notes = "web交割记录列表")
    public Response<PageResult<PositionSettleHistoryRes>> positionSettleHistory(@Valid PositionSettleHistoryReq req, UserInfo userInfo) {
        return Response.ok(marginService.positionSettleHistory(req, userInfo.getUid()));
    }

}
