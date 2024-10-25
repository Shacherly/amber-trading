package com.google.backend.trading.controller.internal;

import com.google.backend.trading.model.common.Response;
import com.google.backend.trading.model.margin.api.ActivePositionInfoVo;
import com.google.backend.trading.model.margin.api.MarginAssetInfoRes;
import com.google.backend.trading.service.MarginService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.function.Consumer;

/**
 * 提供给webSokect 调用
 *
 * @author adam.wang
 * @date 2021/9/29 15:25
 */
@Api(tags = "提供给websocket调用")
@Slf4j
@Validated
@RequestMapping("/internal/v1/websocket")
@RestController
public class WebSocketApiController {

	@Autowired
	private MarginService marginService;

	/**
	 * @param uids
	 * @return
	 */
	@PostMapping("/margin")
	@ApiOperation(value = "杠杆信息")
	public Response<Map<String,MarginAssetInfoRes>> marginInfo(@Valid @RequestBody List<String> uids) {
		Map<String,MarginAssetInfoRes> m =  marginService.marginInfos(uids);
		m.forEach((key, res) -> {
			if (CollectionUtils.isEmpty(res.getCurrentPositionVos()) && res.getTotalLiquidMargin().compareTo(BigDecimal.ZERO) < 0) {
				res.setRiskRate(null);
				res.setCurrentLeverage(null);
			}
		});
		return Response.ok(m);
	}
}
