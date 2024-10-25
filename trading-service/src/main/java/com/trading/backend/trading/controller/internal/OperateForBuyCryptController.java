package com.google.backend.trading.controller.internal;

import com.google.backend.trading.constant.Constants;
import com.google.backend.trading.dao.model.TradeSpotOrder;
import com.google.backend.trading.exception.BusinessExceptionEnum;
import com.google.backend.trading.model.buycrypt.BuyCryptRes;
import com.google.backend.trading.model.common.Response;
import com.google.backend.trading.model.internal.crypt.CryptSpotOrderPlaceReq;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author trading
 * @date 2021/12/27 10:57
 */
@Api(tags = "提供给买币服务的接口")
@Slf4j
@Validated
@RequestMapping("/internal/v1/crypt")
@RestController
public class OperateForBuyCryptController {

	@Autowired
	private SpotService spotService;

	@ApiOperation(value = "币种转换", notes = "现货下单接口，使用order_id来保证幂等")
	@PostMapping("/order/place")
	public Response<Void> spotOrderPlace(@Valid @RequestBody CryptSpotOrderPlaceReq req) {
		TradeSpotOrder order = spotService.querySpotOrderById(req.getOrderId(), req.getUid());
		if (order != null) {
			return Response.fail(BusinessExceptionEnum.DUPLICATE_IDEMPOTENT_REQUEST.getCode(),
					BusinessExceptionEnum.DUPLICATE_IDEMPOTENT_REQUEST.getMsg());
		}
		String fromCoin = req.getFromCoin();
		String toCoin = req.getToCoin();
		if (!Constants.BASE_COIN.equals(fromCoin)) {
			throw new IllegalArgumentException();
		}
		String symbol = toCoin + "_" + fromCoin;
		SpotOrderPlace spotOrderPlace = SpotOrderPlace.builder()
				.orderId(req.getOrderId())
				.uid(req.getUid())
				.symbol(symbol)
				.direction(Direction.BUY)
				.isQuote(true)
				.quantity(req.getQuantity())
				.type(OrderType.MARKET)
				.source(SourceType.BUY_CRYPTOCURRENCY_CONVERSION)
				.build();
		spotService.placeOrder(spotOrderPlace);
		return Response.ok();
	}

	@ApiOperation(value = "币种转换订单查询", notes = "币种转换订单查询")
	@GetMapping("order")
	public Response<BuyCryptRes> spotOrderPlace(@RequestParam("uid")String uid, @RequestParam("order_id") String orderId) {
		TradeSpotOrder order = spotService.querySpotOrderById(orderId, uid);
		BuyCryptRes res = BuyCryptRes.from(order);
		return Response.ok(res);
	}
}
