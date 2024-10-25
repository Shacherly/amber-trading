package com.google.backend.trading.controller.internal;

import com.google.backend.trading.model.common.Response;
import com.google.backend.trading.model.eaas.api.TradeInfoRes;
import com.google.backend.trading.model.trade.TradeLevelEnum;
import com.google.backend.trading.model.trade.fee.UserFeeConfigRate;
import com.google.backend.trading.service.TradeFeeConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author trading
 * @date 2022/1/4 11:17
 */
@Api(tags = "提供给Eaas服务调用的接口")
@Slf4j
@Validated
@RequestMapping("/internal/v1/eaas")
@RestController
public class OperateForEaasController {

	@Autowired
	private TradeFeeConfigService tradeFeeConfigService;


	@GetMapping("/trade-info")
	@ApiOperation(value = "交易信息", notes = "交易等级，交易折扣和交易量")
	public Response<TradeInfoRes> feeInfo(@RequestParam String uid) {
		UserFeeConfigRate config = tradeFeeConfigService.selectUserFeeConfig(uid, true);
		TradeLevelEnum levelEnum = config.getTradeLevelEnum();
		TradeInfoRes res = new TradeInfoRes(levelEnum.getLevel(), config.getTradeAmount30d(), levelEnum.getFeeOff());
		return Response.ok(res);
	}
}
