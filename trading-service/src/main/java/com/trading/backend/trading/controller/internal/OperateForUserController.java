package com.google.backend.trading.controller.internal;

import com.google.backend.trading.dao.model.TradePosition;
import com.google.backend.trading.model.common.Response;
import com.google.backend.trading.model.user.api.UserDeletionInfo;
import com.google.backend.trading.service.MarginService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

/**
 * @author trading
 * @date 2022/1/4 11:17
 */
@Api(tags = "提供给用户中心服务调用的接口")
@Slf4j
@Validated
@RequestMapping("/internal/v1/user")
@RestController
public class OperateForUserController {

	@Autowired
	private MarginService marginService;


	@GetMapping("/get-user-deletion-info")
	@ApiOperation(value = "用户注销信息", notes = "用户注销信息")
	public Response<UserDeletionInfo> available(@RequestParam String uid) {
		long activeOrder = marginService.countActiveOrder(uid);
		List<TradePosition> positions = marginService.listAllActivePositions(Collections.singletonList(uid));
		return Response.ok(new UserDeletionInfo(activeOrder > 0 || positions.size() > 0));
	}
}
