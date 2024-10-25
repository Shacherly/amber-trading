package com.google.backend.trading.controller.internal;

import com.google.backend.trading.config.TradeProperties;
import com.google.backend.trading.constant.Constants;
import com.google.backend.trading.dao.model.TradeUserTradeSetting;
import com.google.backend.trading.httpclient.client.feign.BffApi;
import com.google.backend.trading.mapstruct.config.TradeUserTradeSettingMapStruct;
import com.google.backend.trading.model.common.Response;
import com.google.backend.trading.model.common.model.bff.MarginInfoVo;
import com.google.backend.trading.model.common.model.config.UserTradeSettingVo;
import com.google.backend.trading.model.margin.api.MarginAssetInfoRes;
import com.google.backend.trading.service.MarginService;
import com.google.backend.trading.service.MarketService;
import com.google.backend.trading.service.UserTradeSettingService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author trading
 * @date 2021/10/10 17:56
 */
@Api(tags = "提供给bff层服务调用的接口")
@Slf4j
@Validated
@RestController
public class SearchForBffController implements BffApi {

	@Resource
	private UserTradeSettingService userTradeSettingService;

	@Autowired
	private MarketService marketService;

	@Autowired
	private MarginService marginService;

	@Autowired
	private TradeProperties properties;

	@Autowired
	private TradeUserTradeSettingMapStruct tradeUserTradeSettingMapStruct;

	/**
	 * 提供给bff的配置接口去掉 对资金的调用
	 * @param uid
	 * @return
	 */
	@Override
	public Response<UserTradeSettingVo> config(String uid) {
		TradeUserTradeSetting tradeSetting = userTradeSettingService.queryTradeSettingByUid(uid);
		UserTradeSettingVo vo = tradeUserTradeSettingMapStruct.tradeUserTradeSetting2Vo(tradeSetting);
		if (properties.getTraders().getUidArr().contains(uid)) {
			vo.setEnableOrderNotes(true);
		}
		return Response.ok(vo);
	}

	@Override
	public Response<List<String>> symbolRecommendList(@RequestParam(required = false) String uid) {
		List<String> symbolList = new ArrayList<>(Arrays.asList(Constants.DEFAULT_MARKET_FAVORITE_LIST));
		if (null != uid) {
			symbolList = marketService.listAllFavorite(uid);
		}
		if (symbolList.size() < 2) {
			symbolList.addAll(Arrays.asList(Constants.DEFAULT_MIN_RECOMMEND_MARKET_LIST));
		}
		return Response.ok(symbolList.stream().distinct().collect(Collectors.toList()));
	}

	@Override
	public Response<MarginInfoVo> marginAssetInfo(@RequestParam String uid) {
		MarginAssetInfoRes info = marginService.marginAssetInfo(uid);
		MarginInfoVo vo = new MarginInfoVo();
		vo.setUnpnl(info.getUnpnl());
		vo.setRiskRate(info.getRiskRate());
		return Response.ok(vo);
	}
}
