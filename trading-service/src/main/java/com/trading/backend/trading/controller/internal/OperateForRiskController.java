package com.google.backend.trading.controller.internal;

import com.github.pagehelper.PageInfo;
import com.google.backend.common.mq.HeaderUtils;
import com.google.backend.common.web.Response;
import com.google.backend.trading.config.TradeProperties;
import com.google.backend.trading.constant.Constants;
import com.google.backend.trading.dao.model.TradePosition;
import com.google.backend.trading.httpclient.client.feign.RiskControlApi;
import com.google.backend.trading.mapstruct.margin.TradePositionMapStruct;
import com.google.backend.trading.model.common.PageResult;
import com.google.backend.trading.model.common.model.riskcontrol.PositionRes;
import com.google.backend.trading.model.common.model.riskcontrol.ReducePositionReq;
import com.google.backend.trading.model.common.model.riskcontrol.UserSettingRes;
import com.google.backend.trading.service.MarginService;
import com.google.backend.trading.service.RiskControlService;
import com.google.backend.trading.service.UserTradeSettingService;
import com.google.backend.trading.util.ListUtil;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 提供给风控模块的接口支持
 *
 * @author trading
 * @date 2021/9/27 19:33
 */
@Api(tags = "提供给风控的接口")
@Slf4j
@Validated
@RestController
public class OperateForRiskController implements RiskControlApi {

	@Autowired
	private TradeProperties properties;
	@Resource
	private MarginService marginService;
	@Resource
	private UserTradeSettingService userTradeSettingService;
	@Resource
	private RiskControlService riskControlService;
	@Resource
	private TradePositionMapStruct tradePositionMapStruct;

	@Override
	public Response<List<PositionRes>> listAllActivePositions(@Valid @RequestBody List<String> uids) {
		List<PositionRes> res = new ArrayList<>();
		if (ListUtil.isNotEmpty(uids)) {
			Pair<Long, List<TradePosition>> pair = marginService.listAllActivePositionsWithSeq(uids);
			String txnId = HeaderUtils.toTxnId(Constants.SERVICE_NAME, pair.getFirst());
			List<TradePosition> tradePositions = pair.getSecond();
			res = tradePositionMapStruct.tradePositions2PositionRes(tradePositions, txnId);
		}
		return Response.ok(res);
	}

	@Override
	public Response<List<PositionRes>> listAllActivePositions() {
		Pair<Long, List<TradePosition>> pair = marginService.listAllActivePositionsWithSeq(Collections.emptyList());
		String txnId = HeaderUtils.toTxnId(Constants.SERVICE_NAME, pair.getFirst());
		List<TradePosition> tradePositions = pair.getSecond();
		List<PositionRes> res = tradePositionMapStruct.tradePositions2PositionRes(tradePositions, txnId);
		return Response.ok(res);
	}

	@Override
	public Response<PageResult<PositionRes>> listAllActivePositions(@RequestParam(value = "page") Integer page,
																	@RequestParam(value = "page_size") Integer pageSize) {
		Pair<Long, PageInfo<TradePosition>> pair = marginService.listAllActivePositionsWithSeq(page, pageSize);
		String txnId = HeaderUtils.toTxnId(Constants.SERVICE_NAME, pair.getFirst());
		PageInfo<TradePosition> pageInfo = pair.getSecond();
		PageResult<PositionRes> pageResult = PageResult.generate(pageInfo,
				tradePosition -> tradePositionMapStruct.tradePosition2PositionRes(tradePosition, txnId));
		return Response.ok(pageResult);
	}

	@Override
	public Response<List<UserSettingRes>> queryUserSetting(@NotNull @RequestBody List<String> uids) {
		if (ListUtil.isEmpty(uids)) {
			return Response.fail();
		}
		return Response.ok(userTradeSettingService.queryUserSetting(uids));
	}

	@Override
	public Response<Void> cancelOrder(@RequestParam String uid) {
		if (properties.getRisk().isEnabled()) {
			riskControlService.cancelOrder(uid);
		} else {
			log.info("risk ops is disable");
		}
		return Response.ok();
	}

	@Override
	public Response<Void> reducePosition(@Valid @RequestBody ReducePositionReq reducePositionReq) {
		if (properties.getRisk().isEnabled()) {
			riskControlService.reducePosition(reducePositionReq);
		} else {
			log.info("risk ops is disable");
		}
		return Response.ok();
	}


	/**
	 * 需要和 liquidBalance共用锁
	 * liquidBalance 内部有 liquidBalance 的逻辑
	 * @param uid
	 * @return
	 */
	@Override
	public Response<Void> liquidSpot(@RequestParam String uid) {
		if (properties.getRisk().isEnabled()) {
			riskControlService.liquidSpot(uid);
		} else {
			log.info("risk ops is disable");
		}
		return Response.ok();
	}


	@Override
	public Response<Void> liquidBalance(@RequestParam String uid) {
		if (properties.getRisk().isEnabled()) {
			riskControlService.liquidBalance(uid);
		} else {
			log.info("risk ops is disable");
		}
		return Response.ok();
	}

}
