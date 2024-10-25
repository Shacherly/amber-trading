package com.google.backend.trading.controller.internal;

import com.google.backend.asset.common.model.base.PoolEntityForRisk;
import com.google.backend.trading.constant.Constants;
import com.google.backend.trading.exception.BusinessException;
import com.google.backend.trading.exception.BusinessExceptionEnum;
import com.google.backend.trading.exception.InternalApiException;
import com.google.backend.trading.model.booking.api.BookingAvailableRes;
import com.google.backend.trading.model.booking.api.BookingPlaceReq;
import com.google.backend.trading.model.common.PageResult;
import com.google.backend.trading.model.common.Response;
import com.google.backend.trading.model.margin.api.ActivePositionInfoVo;
import com.google.backend.trading.model.margin.api.MarginOrderDetailReq;
import com.google.backend.trading.model.margin.api.MarginOrderDetailRes;
import com.google.backend.trading.model.margin.api.MarginOrderHistoryReq;
import com.google.backend.trading.model.margin.api.MarginOrderInfoRes;
import com.google.backend.trading.model.margin.api.Position6HFundingCostVo;
import com.google.backend.trading.model.margin.dto.MarginInfo;
import com.google.backend.trading.service.MarginService;
import com.google.backend.trading.service.UserBookingListService;
import com.google.backend.trading.util.AlarmLogUtil;
import com.google.backend.trading.util.CommonUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author trading
 * @date 2021/9/27 21:14
 */
@Api(tags = "提供给pdt的接口")
@Slf4j
@Validated
@RequestMapping("/internal/v1/pdt")
@RestController
public class OperateForPdtController {

	@Resource
	private MarginService marginService;

	@Autowired
	private RedissonClient redissonClient;

	@Autowired
	private UserBookingListService userBookingListService;

	@PostMapping("/booking/place")
	@ApiOperation(value = "Booking下单", notes = "Booking下单")
	public Response<MarginOrderInfoRes> bookingPlaceOrder(@Valid @RequestBody BookingPlaceReq req) {
		String uid = req.getUid();
		String uniqueId = req.getUniqueId();
		Set<String> uidSet = userBookingListService.allBookingUidSet();
		if (!uidSet.contains(uid)) {
			AlarmLogUtil.alarm("not otc booking user, uid = {}", uid);
			throw new InternalApiException("not otc booking user");
		}
		String symbol = req.getSymbol();
		if (!CommonUtils.checkMarginSymbol(symbol)) {
			AlarmLogUtil.alarm("booking not supported symbol, symbol = {}", symbol);
			throw new InternalApiException("not supported symbol");
		}
		MarginOrderDetailReq detailReq = new MarginOrderDetailReq();
		detailReq.setOrderId(uniqueId);
		String lockKey = uid + ":" + "booking-order" + ":" + uniqueId;
		RLock lock = redissonClient.getLock(lockKey);
		boolean getLock = false;
		try {
			getLock = lock.tryLock(5, TimeUnit.SECONDS);
		} catch (InterruptedException ignore) {
		}
		if (!getLock) {
			throw new BusinessException(BusinessExceptionEnum.REQUEST_TOO_MANY);
		}
		try {
			MarginOrderDetailRes detailRes = marginService.orderDetail(detailReq, uid);
			if (null != detailRes) {
				detailRes.setModifications(null);
				return Response.ok(detailRes, BusinessExceptionEnum.DUPLICATE_IDEMPOTENT_REQUEST.getMsg());
			}
			MarginOrderInfoRes res = marginService.placeBookingOrder(req, uid);
			return Response.ok(res);
		} finally {
			lock.unlock();
		}
	}

	@GetMapping("/booking/query")
	@ApiOperation(value = "Booking查询", notes = "Booking查询")
	public Response<PageResult<MarginOrderInfoRes>> queryBookingOrder(@Valid MarginOrderHistoryReq req, @NotNull String uid) {
		Set<String> uidSet = userBookingListService.allBookingUidSet();
		if (!uidSet.contains(uid)) {
			AlarmLogUtil.alarm("not otc booking user, uid = {}", uid);
			throw new InternalApiException("not otc booking user");
		}
		PageResult<MarginOrderInfoRes> res = marginService.orderHistory(req, uid);
		return Response.ok(res);
	}

	@GetMapping("/available")
	@ApiOperation(value = "用户余额查询", notes = "用户余额查询")
	public Response<BookingAvailableRes> available(@RequestParam String uid) {
		Set<String> uidSet = userBookingListService.allBookingUidSet();
		if (!uidSet.contains(uid)) {
			AlarmLogUtil.alarm("not otc booking user, uid = {}", uid);
			throw new InternalApiException("not otc booking user");
		}
		MarginInfo info = marginService.marginInfo(uid);
		Map<String, PoolEntityForRisk> poolEntityForRiskMap = info.getPoolEntityForRiskMap();
		PoolEntityForRisk poolEntityForRisk = poolEntityForRiskMap.get(Constants.BASE_COIN);
		BigDecimal credit = null == poolEntityForRisk ? BigDecimal.ZERO : poolEntityForRisk.getCredit();
		BookingAvailableRes res = new BookingAvailableRes();
		res.setCredit(credit);
		res.setUsedCredit(info.getUsedCredit());
		Map<String, BigDecimal> balanceMap = poolEntityForRiskMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,
				e -> e.getValue().getBalance()));
		res.setBalance(balanceMap);
		res.setAvailableMargin(info.getAvailableMargin());
		res.setTotalMargin(info.getTotalOpenMargin());
		return Response.ok(res);
	}

	@GetMapping("/position")
	@ApiOperation(value = "用户仓位", notes = "用户仓位")
	public Response<List<ActivePositionInfoVo>> position(@RequestParam String uid) {
		Set<String> uidSet = userBookingListService.allBookingUidSet();
		if (!uidSet.contains(uid)) {
			AlarmLogUtil.alarm("not otc booking user, uid = {}", uid);
			throw new RuntimeException("not otc booking user");
		}
		List<ActivePositionInfoVo> res = marginService.positionActive(uid);
		return Response.ok(res);
	}

	@GetMapping("/uid/all")
	@ApiOperation(value = "获取booking用户ui的列表", notes = "获取booking用户ui的列表")
	public Response<List<String>> uidList() {
		Set<String> uidSet = userBookingListService.allBookingUidSet();
		return Response.ok(new ArrayList<>(uidSet));
	}

	@GetMapping("/funding_cost/6h")
	@ApiOperation(value = "前6h资金费用", notes = "前6h资金费用")
	public Response<Position6HFundingCostVo> allFundingCost() {
		Position6HFundingCostVo position6HFundingCostVo = marginService.getFundingCostBefore6H(System.currentTimeMillis());
		return Response.ok(position6HFundingCostVo);
	}


}
