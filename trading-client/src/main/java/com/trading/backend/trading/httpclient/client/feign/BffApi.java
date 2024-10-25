package com.google.backend.trading.httpclient.client.feign;

import com.google.backend.trading.model.common.Response;
import com.google.backend.trading.model.common.model.bff.MarginInfoVo;
import com.google.backend.trading.model.common.model.config.UserTradeSettingVo;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author trading
 * @date 2021/10/11 19:39
 */
@RequestMapping("/internal/v1/bff")
public interface BffApi {


	@GetMapping("/config")
	@ApiOperation(value = "用户配置查询", notes = "查询接口特别说明")
	Response<UserTradeSettingVo> config(@RequestParam String uid);


	@GetMapping("/watch-list")
	@ApiOperation(value = "币对关注列表", notes = "币对关注列表")
	@ApiResponses(@ApiResponse(code = 200, message = "响应体结构 {\"code" +
			"\":0," +
			"\"data\":[\"BTC_USD\"," +
			"\"ETH_USD\",\"DOT_USD\",\"BNB_USD\",\"LINK_USD\",\"UNI_USD\"],\"msg\":\"ok\"}"))
	Response<List<String>> symbolRecommendList(@RequestParam(required = false) String uid);


	@GetMapping("/margin/info")
	@ApiOperation(value = "查询用户杠杆信息", notes = "查询用户杠杆信息")
	Response<MarginInfoVo> marginAssetInfo(@RequestParam String uid);
}
