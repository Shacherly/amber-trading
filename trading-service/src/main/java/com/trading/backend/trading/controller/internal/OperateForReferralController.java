package com.google.backend.trading.controller.internal;

import com.google.backend.trading.model.common.Response;
import com.google.backend.trading.service.TradeCompositeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author trading
 * @date 2021/11/4 17:53
 */
@Api(tags = "提供给referral的接口")
@Slf4j
@Validated
@RequestMapping("/internal/v1/referral")
@RestController
public class OperateForReferralController {

	@Autowired
	private TradeCompositeService tradeCompositeService;


	@GetMapping("/is-new-user")
	@ApiOperation(value = "是否是新手用户", notes = "是否是新手用户")
	public Response<Boolean> isNewUser(@RequestParam String uid) {
		return Response.ok(tradeCompositeService.isTradeNewUser(uid));
	}


}
