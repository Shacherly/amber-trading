package com.google.backend.trading.controller.internal;

import com.google.backend.trading.constant.Constants;
import com.google.backend.trading.dao.model.TradeSpotOrder;
import com.google.backend.trading.exception.BusinessExceptionEnum;
import com.google.backend.trading.model.common.Response;
import com.google.backend.trading.model.internal.earn.EarnSpotOrderPlaceReq;
import com.google.backend.trading.model.spot.api.SpotOrderPlaceRes;
import com.google.backend.trading.model.spot.dto.SpotOrderPlace;
import com.google.backend.trading.model.trade.Direction;
import com.google.backend.trading.model.trade.OrderType;
import com.google.backend.trading.model.trade.SourceType;
import com.google.backend.trading.service.SpotService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 给理财的内部接口
 *
 * @author savion.chen
 * @date 2021/11/16 18:54
 */

@Api(tags = "给理财的内部接口")
@Slf4j
@Validated
@RequestMapping("/internal/v1/earn")
@RestController
public class OperateForEarnController {

    @Autowired
    private SpotService spotService;

    @ApiOperation(value = "理财清算下单接口")
    @PostMapping("spot/order/place")
    public Response<SpotOrderPlaceRes> spotOrderPlace(@Valid @RequestBody EarnSpotOrderPlaceReq req) {
        //USD不处理
        if (Constants.BASE_COIN.equals(req.getCoin())) {
            return Response.ok();
        }
        TradeSpotOrder order = spotService.querySpotOrderById(req.getOrderId(), req.getUid());
        if (order != null) {
            return Response.ok(spotService.getOrderResult(order), BusinessExceptionEnum.DUPLICATE_IDEMPOTENT_REQUEST.getMsg());
        }
        SpotOrderPlace spotOrderPlace = SpotOrderPlace.builder()
                .orderId(req.getOrderId())
                .uid(req.getUid())
                .symbol(req.getCoin() + Constants.BASE_QUOTE)
                .direction(Direction.SELL)
                .isQuote(false)
                .quantity(req.getQuantity())
                .type(OrderType.MARKET)
                .source(SourceType.EARN_LIQUIDATION)
                .build();
        SpotOrderPlaceRes res = spotService.placeOrder(spotOrderPlace);
        return Response.ok(res);
    }

}
