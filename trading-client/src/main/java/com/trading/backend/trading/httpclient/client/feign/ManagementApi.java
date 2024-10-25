package com.google.backend.trading.httpclient.client.feign;

import com.google.backend.trading.model.common.PageResult;
import com.google.backend.trading.model.common.Response;
import com.google.backend.trading.model.common.model.aceup.api.BookingListAddReq;
import com.google.backend.trading.model.common.model.aceup.api.BookingListRes;
import com.google.backend.trading.model.common.model.aceup.api.LiquidListAddReq;
import com.google.backend.trading.model.common.model.aceup.api.LiquidListRes;
import com.google.backend.trading.model.common.model.aceup.api.PositionLimitAddReq;
import com.google.backend.trading.model.common.model.aceup.api.PositionLimitRes;
import com.google.backend.trading.model.common.model.aceup.api.PositionLimitUpdateReq;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;

/**
 * @author trading
 * @date 2022/1/12 11:09
 */
@RequestMapping("/internal/v1/aceup")
public interface ManagementApi {

	@PostMapping("/booking")
	@ApiOperation(value = "booking名单添加")
	Response<Void> bookingListAdd(@Valid @RequestBody BookingListAddReq req);

	@DeleteMapping("/booking")
	@ApiOperation(value = "booking名单删除")
	Response<Void> bookingListDelete(@RequestParam String uid);

	@GetMapping("/booking")
	@ApiOperation(value = "booking名单查询")
	Response<PageResult<BookingListRes>> bookingFind(
			@RequestParam String uid,
			@RequestParam(value = "start_time", required = false) Long startTime,
			@RequestParam(value = "end_time", required = false) Long endTime,
			@RequestParam Integer page,
			@RequestParam(value = "page_size") Integer pageSize
	);

	@PostMapping("/liquid")
	@ApiOperation(value = "杠杆强平名单添加")
	Response<Void> liquidListAdd(@Valid @RequestBody LiquidListAddReq req);

	@DeleteMapping("/liquid")
	@ApiOperation(value = "杠杆强平名单删除")
	Response<Void> liquidListDelete(@RequestParam String uid);

	@GetMapping("/liquid")
	@ApiOperation(value = "杠杆强平名单查询")
	Response<PageResult<LiquidListRes>> liquidFind(
			@RequestParam String uid,
			@RequestParam(value = "start_time", required = false) Long startTime,
			@RequestParam(value = "end_time", required = false) Long endTime,
			@RequestParam Integer page,
			@RequestParam(value = "page_size") Integer pageSize
	);

	@PostMapping("/position-limit")
	@ApiOperation(value = "持仓限额配置添加")
	Response<Void> positionLimitAdd(@Valid @RequestBody PositionLimitAddReq req);

	@PutMapping("/position-limit")
	@ApiOperation(value = "持仓限额配置修改")
	Response<Void> positionLimitUpdate(@Valid @RequestBody PositionLimitUpdateReq req);

	@DeleteMapping("/position-limit")
	@ApiOperation(value = "持仓限额配置删除")
	Response<Void> positionLimitDelete(@RequestParam String uid);

	@GetMapping("/position-limit")
	@ApiOperation(value = "持仓限额配置查询")
	Response<PageResult<PositionLimitRes>> positionLimitFind(
			@RequestParam String uid,
			@RequestParam(value = "start_time", required = false) Long startTime,
			@RequestParam(value = "end_time", required = false) Long endTime,
			@RequestParam Integer page,
			@RequestParam(value = "page_size") Integer pageSize
	);
}
