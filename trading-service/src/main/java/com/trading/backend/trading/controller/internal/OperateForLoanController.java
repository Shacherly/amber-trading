package com.google.backend.trading.controller.internal;

import com.google.backend.trading.dao.model.TradeSpotOrder;
import com.google.backend.trading.dao.model.TradeSwapOrder;
import com.google.backend.trading.exception.BusinessException;
import com.google.backend.trading.exception.BusinessExceptionEnum;
import com.google.backend.trading.mapstruct.swap.TradeSwapOrderMapStruct;
import com.google.backend.trading.model.common.Response;
import com.google.backend.trading.model.internal.loan.LoanSpotOrderPlaceReq;
import com.google.backend.trading.model.internal.loan.LoanSwapOrderPlaceReq;
import com.google.backend.trading.model.swap.SwapType;
import com.google.backend.trading.model.trade.OrderStatus;
import com.google.backend.trading.model.trade.OrderType;
import com.google.backend.trading.model.spot.api.SpotOrderPlaceRes;
import com.google.backend.trading.model.spot.dto.SpotOrderPlace;
import com.google.backend.trading.model.swap.api.SwapOrderRes;
import com.google.backend.trading.model.swap.api.SwapPriceReq;
import com.google.backend.trading.model.swap.api.SwapPriceRes;
import com.google.backend.trading.model.swap.dto.SwapOrderPlace;
import com.google.backend.trading.model.trade.Direction;
import com.google.backend.trading.model.trade.SourceType;
import com.google.backend.trading.service.SpotService;
import com.google.backend.trading.service.SwapService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 提供给借贷模块的接口支持
 *
 * @author trading
 * @date 2021/9/27 19:34
 */
@Api(tags = "提供给借贷的接口")
@Slf4j
@Validated
@RequestMapping("/internal/v1/loan")
@RestController
public class OperateForLoanController {
    @Autowired
    private SpotService spotService;

    @Autowired
    private SwapService swapService;

    @ApiOperation(value = "现货下单接口")
    @PostMapping("spot/order/place")
    public Response<SpotOrderPlaceRes> spotOrderPlace(@Valid @RequestBody LoanSpotOrderPlaceReq req) throws InterruptedException {
        TradeSpotOrder order = spotService.querySpotOrderById(req.getOrderId(), req.getUid());
        if (order != null) {
            SpotOrderPlaceRes res = spotService.getOrderResult(order);
            res.setStatus(res.getOriginalStatus());
            return Response.ok(res, BusinessExceptionEnum.DUPLICATE_IDEMPOTENT_REQUEST.getMsg());
        }
        SpotOrderPlace spotOrderPlace = SpotOrderPlace.builder()
                .orderId(req.getOrderId())
                .uid(req.getUid())
                .symbol(req.getSymbol())
                .direction(Direction.getByCode(req.getDirection()))
                .isQuote(req.getIsQuote())
                .quantity(req.getQuantity())
                .type(OrderType.MARKET)
                .source(SourceType.getByCode(req.getSource()))
                .build();
        SpotOrderPlaceRes res = spotService.placeOrder(spotOrderPlace);
        res.setStatus(res.getOriginalStatus());
        return Response.ok(res);
    }

    @ApiOperation(value = "兑换下单接口")
    @PostMapping("swap/order/place")
    public Response<SwapOrderRes> swapOrderPlace(@Valid @RequestBody LoanSwapOrderPlaceReq req) {
        SwapOrderRes res = swapService.querySwapOrder(req.getOrderId(), req.getUid());
        if (res != null) {
            if (OrderStatus.isFinish(res.getStatus())) {
                return Response.ok(res, BusinessExceptionEnum.DUPLICATE_IDEMPOTENT_REQUEST.getMsg());
            }
            else {
                throw new BusinessException(BusinessExceptionEnum.TIME_OUT_FAIL);
            }
        }
        SwapOrderPlace placeReq = new SwapOrderPlace();
        placeReq.setOrderId(req.getOrderId());
        placeReq.setUid(req.getUid());
        placeReq.setFromCoin(req.getFromCoin());
        placeReq.setToCoin(req.getToCoin());
        placeReq.setQuantity(req.getQuantity());
        placeReq.setMode(SwapType.getByName(req.getMode()));
        placeReq.setSource(SourceType.getByName(req.getSource()));
        BigDecimal price = req.getPrice();
        if (price == null) {
            SwapPriceReq priceReq = new SwapPriceReq();
            priceReq.setFromCoin(req.getFromCoin());
            priceReq.setToCoin(req.getToCoin());
            priceReq.setQuantity(req.getQuantity());
            priceReq.setMode(req.getMode());
            priceReq.setFeeFree(true);
            SwapPriceRes priceRes = swapService.queryPrice(priceReq, req.getUid());
            price = priceRes.getPrice();
        }
        placeReq.setPrice(price);
        if (SwapType.isPayment(req.getMode())) {
            placeReq.setReqCoin(req.getFromCoin());
        } else {
            placeReq.setReqCoin(req.getToCoin());
        }
        res = swapService.placeOrder(placeReq);
        // pdt超时，需要重新请求订单状态
        if (!OrderStatus.isFinish(res.getStatus())) {
            throw new BusinessException(BusinessExceptionEnum.TIME_OUT_FAIL);
        }
        return Response.ok(res);
    }
}
