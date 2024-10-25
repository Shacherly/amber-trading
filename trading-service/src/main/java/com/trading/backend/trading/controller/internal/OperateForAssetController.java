package com.google.backend.trading.controller.internal;

import com.google.backend.trading.model.asset.api.GetMarginInfoRes;
import com.google.backend.trading.model.asset.api.GetUnpnlReq;
import com.google.backend.trading.model.asset.api.GetUnpnlRes;
import com.google.backend.trading.model.common.Response;
import com.google.backend.trading.service.MarginService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 调用方：referral，asset
 * @author trading
 * @date 2021/11/3 20:02
 */
@Api(tags = "提供给资金的接口")
@Slf4j
@Validated
@RequestMapping("/internal/v1/asset")
@RestController
public class OperateForAssetController {

	@Autowired
	private MarginService marginService;

	@PostMapping("/unpnl")
	@ApiOperation(value = "unpnl查询", notes = "unpnl查询")
	public Response<GetUnpnlRes> usersUnpnl(@Valid @RequestBody GetUnpnlReq req) {
		Map<String, BigDecimal> unpnlMap = marginService.unpnl(req.getUidList());
		List<GetUnpnlRes.UnpnlData> list = unpnlMap.entrySet().stream().map(entry -> new GetUnpnlRes.UnpnlData(entry.getKey(),
				entry.getValue())).collect(Collectors.toList());
		GetUnpnlRes res = new GetUnpnlRes();
		res.setList(list);
		return Response.ok(res);
	}

	@GetMapping("/margin")
	@ApiOperation(value = "杠杆信息")
	public Response<GetMarginInfoRes> marginInfo(String uid) {
		BigDecimal usedMargin = marginService.marginInfo(uid).getUsedMargin();
		GetMarginInfoRes res = new GetMarginInfoRes(usedMargin);
		return Response.ok(res);
	}

}
