package com.google.backend.trading.controller.internal;

import com.github.pagehelper.PageInfo;
import com.google.backend.trading.dao.model.TradePositionLimitUser;
import com.google.backend.trading.dao.model.TradeUserBookingList;
import com.google.backend.trading.dao.model.TradeUserSystemSetting;
import com.google.backend.trading.mapstruct.aceup.AceupAllMapStruct;
import com.google.backend.trading.model.aceup.api.BookingListFindReq;
import com.google.backend.trading.model.aceup.api.LiquidListFindReq;
import com.google.backend.trading.model.aceup.api.PositionLimitFindReq;
import com.google.backend.trading.model.common.PageResult;
import com.google.backend.trading.model.common.Response;
import com.google.backend.trading.model.common.model.aceup.api.BookingListAddReq;
import com.google.backend.trading.model.common.model.aceup.api.BookingListRes;
import com.google.backend.trading.model.common.model.aceup.api.LiquidListAddReq;
import com.google.backend.trading.model.common.model.aceup.api.LiquidListRes;
import com.google.backend.trading.model.common.model.aceup.api.PositionLimitAddReq;
import com.google.backend.trading.model.common.model.aceup.api.PositionLimitRes;
import com.google.backend.trading.model.common.model.aceup.api.PositionLimitUpdateReq;
import com.google.backend.trading.model.internal.aceup.AceUpFundingCostReq;
import com.google.backend.trading.model.internal.aceup.AceUpFundingCostRes;
import com.google.backend.trading.model.internal.aceup.AceUpMarginPositionReq;
import com.google.backend.trading.model.internal.aceup.AceUpMarginPositionRes;
import com.google.backend.trading.model.internal.aceup.AceUpMarginReq;
import com.google.backend.trading.model.internal.aceup.AceUpMarginRes;
import com.google.backend.trading.model.internal.aceup.AceUpMarginTransReq;
import com.google.backend.trading.model.internal.aceup.AceUpMarginTransRes;
import com.google.backend.trading.model.internal.aceup.AceUpSpotReq;
import com.google.backend.trading.model.internal.aceup.AceUpSpotRes;
import com.google.backend.trading.model.internal.aceup.AceUpSpotTransRes;
import com.google.backend.trading.model.internal.aceup.AceUpSwapReq;
import com.google.backend.trading.model.internal.aceup.AceUpSwapRes;
import com.google.backend.trading.service.MarginService;
import com.google.backend.trading.service.PositionLimitUserService;
import com.google.backend.trading.service.SpotService;
import com.google.backend.trading.service.SwapService;
import com.google.backend.trading.service.UserBookingListService;
import com.google.backend.trading.service.UserLiquidWhiteListService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * @author trading
 * @date 2022/1/7 14:20
 */
@Api(tags = "提供给aceup的接口")
@Slf4j
@Validated
@RequestMapping("/internal/v1/aceup")
@RestController
public class OperateForAceupController {

	@Autowired
	private UserBookingListService userBookingListService;

	@Autowired
	private UserLiquidWhiteListService userLiquidWhiteListService;

	@Autowired
	private PositionLimitUserService positionLimitUserService;

	@Autowired
	private AceupAllMapStruct aceupAllMapStruct;

	@Autowired
	private SwapService swapService;

	@Autowired
	private SpotService spotService;

	@Autowired
	private MarginService marginService;

	@PostMapping("/booking")
	@ApiOperation(value = "booking名单添加")
	public Response<Void> bookingListAdd(@Valid @RequestBody BookingListAddReq req) {
		TradeUserBookingList bookingList = aceupAllMapStruct.bookingListAddReq2TradeUserBookingList(req);
		userBookingListService.add(bookingList);
		return Response.ok();
	}

	@DeleteMapping("/booking")
	@ApiOperation(value = "booking名单删除")
	public Response<Void> bookingListDelete(@RequestParam String uid) {
		userBookingListService.remove(uid);
		return Response.ok();
	}

	@GetMapping("/booking")
	@ApiOperation(value = "booking名单查询")
	public Response<PageResult<BookingListRes>> bookingFind(@Valid BookingListFindReq req) {
		List<TradeUserBookingList> lists = userBookingListService.find(req.getUid(), req.getStartTime(), req.getEndTime(),
				req.getPage(), req.getPageSize());
		PageInfo<TradeUserBookingList> pageInfo = new PageInfo<>(lists);
		List<BookingListRes> resList = aceupAllMapStruct.tradeUserBookingList2BookingListRes(lists);
		PageResult<BookingListRes> page = PageResult.generate(pageInfo.getTotal(), pageInfo.getPageNum(), pageInfo.getPageSize()
				, resList);
		return Response.ok(page);
	}

	@PostMapping("/liquid")
	@ApiOperation(value = "杠杆强平名单添加")
	public Response<Void> liquidListAdd(@Valid @RequestBody LiquidListAddReq req) {
		TradeUserSystemSetting setting = aceupAllMapStruct.liquidListAddReq2TradeTradeUserSystemSetting(req);
		userLiquidWhiteListService.add(setting);
		return Response.ok();
	}

	@DeleteMapping("/liquid")
	@ApiOperation(value = "杠杆强平名单删除")
	public Response<Void> liquidListDelete(@RequestParam String uid) {
		userLiquidWhiteListService.remove(uid);
		return Response.ok();
	}

	@GetMapping("/liquid")
	@ApiOperation(value = "杠杆强平名单查询")
	public Response<PageResult<LiquidListRes>> liquidFind(@Valid LiquidListFindReq req) {
		List<TradeUserSystemSetting> lists = userLiquidWhiteListService.find(req.getUid(), req.getStartTime(), req.getEndTime(),
				req.getPage(), req.getPageSize());
		PageInfo<TradeUserSystemSetting> pageInfo = new PageInfo<>(lists);
		List<LiquidListRes> resList = aceupAllMapStruct.tradeUserSystemSetting2LiquidListRes(lists);
		PageResult<LiquidListRes> page = PageResult.generate(pageInfo.getTotal(), pageInfo.getPageNum(), pageInfo.getPageSize()
				, resList);
		return Response.ok(page);
	}

	@PostMapping("/position-limit")
	@ApiOperation(value = "持仓限额配置添加")
	public Response<Void> positionLimitAdd(@Valid @RequestBody PositionLimitAddReq req) {
		TradePositionLimitUser limitUser = aceupAllMapStruct.positionLimitAddReq2TradePositionLimitUser(req);
		positionLimitUserService.add(limitUser);
		return Response.ok();
	}

	@PutMapping("/position-limit")
	@ApiOperation(value = "持仓限额配置修改")
	public Response<Void> positionLimitUpdate(@Valid @RequestBody PositionLimitUpdateReq req) {
		TradePositionLimitUser update = aceupAllMapStruct.positionLimitUpdateReq2TradePositionLimitUser(req);
		positionLimitUserService.update(update);
		return Response.ok();
	}

	@DeleteMapping("/position-limit")
	@ApiOperation(value = "持仓限额配置删除")
	public Response<Void> positionLimitDelete(@RequestParam String uid) {
		positionLimitUserService.remove(uid);
		return Response.ok();
	}

	@GetMapping("/position-limit")
	@ApiOperation(value = "持仓限额配置查询")
	public Response<PageResult<PositionLimitRes>> positionLimitFind(@Valid PositionLimitFindReq req) {
		List<TradePositionLimitUser> lists = positionLimitUserService.find(req.getUid(), req.getStartTime(), req.getEndTime(),
				req.getPage(), req.getPageSize());
		PageInfo<TradePositionLimitUser> pageInfo = new PageInfo<>(lists);
		List<PositionLimitRes> resList = aceupAllMapStruct.tradePositionLimitUser2PositionLimitRes(lists);
		PageResult<PositionLimitRes> page = PageResult.generate(pageInfo.getTotal(), pageInfo.getPageNum(), pageInfo.getPageSize()
				, resList);
		return Response.ok(page);
	}

	@PostMapping("/order-history/swap")
	@ApiOperation(value = "查询兑换订单")
	public Response<PageResult<AceUpSwapRes>> swapHistory(@RequestBody @Valid AceUpSwapReq req) {
		return Response.ok(swapService.swapHistoryForAceUp(req));
	}

	@PostMapping("/order-history/spot")
	@ApiOperation(value = "查询现货订单")
	public Response<PageResult<AceUpSpotRes>> spotHistory(@RequestBody @Valid AceUpSpotReq req) {
		return Response.ok(spotService.spotHistoryForAceUp(req));
	}

	@GetMapping("/order/spot/trans")
	@ApiOperation(value = "查询现货订单详情")
	public Response<List<AceUpSpotTransRes>> spotTransactionList(@RequestParam(name = "order_id") String orderId) {
		return Response.ok(spotService.spotTransactionListForAceUp(orderId));
	}


	@PostMapping("/order-history/margin")
	@ApiOperation(value = "查询杠杠订单")
	public Response<PageResult<AceUpMarginRes>> marginHistory(@RequestBody @Valid AceUpMarginReq req) {
		return Response.ok(marginService.marginHistoryForAceUp(req));
	}

	@PostMapping("/order-history/margin/trans")
	@ApiOperation(value = "查询杠杠订单交易记录")
	public Response<PageResult<AceUpMarginTransRes>> marginTransactionList(@RequestBody @Valid AceUpMarginTransReq req) {
		return Response.ok(marginService.marginTransactionListForAceUp(req));
	}

	@PostMapping("/order-history/margin/position")
	@ApiOperation(value = "查询杠杠订单仓位记录")
	public Response<PageResult<AceUpMarginPositionRes>> marginPositionList(@RequestBody @Valid AceUpMarginPositionReq req) {
		return Response.ok(marginService.marginPositionListForAceUp(req));
	}


	@PostMapping("/order-history/margin/funding-cost")
	@ApiOperation(value = "查询杠杠订单资金费率记录")
	public Response<PageResult<AceUpFundingCostRes>> marginPositionFundingCostList(@RequestBody @Valid AceUpFundingCostReq req) {
		return Response.ok(marginService.positionFundingCostListForAceUp(req));
	}
}
