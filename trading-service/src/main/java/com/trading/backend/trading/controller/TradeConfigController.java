package com.google.backend.trading.controller;

import com.google.backend.trading.component.TimeZone;
import com.google.backend.trading.model.common.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 系统侧交易配置
 *
 * @author trading
 * @date 2021/9/27 16:39
 */
@Slf4j
@Api(value="系统配置相关接口",tags="系统配置相关接口")
@RestController
@Validated
@RequestMapping("/v1/config")
public class TradeConfigController {

	@GetMapping("/timezone")
	@ApiOperation(value = "时区列表", notes = "时区列表")
	public Response<TimeZone[]> timeZoneList() {
		return Response.ok(TimeZone.values());
	}

}
