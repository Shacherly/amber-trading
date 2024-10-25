package com.google.backend.trading.controller.internal;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.ImmutableMap;
import com.google.backend.trading.component.CoinDomain;
import com.google.backend.trading.component.SymbolDomain;
import com.google.backend.trading.dao.mapper.DefaultTradeMarginOrderMapper;
import com.google.backend.trading.dao.mapper.DefaultTradePositionMapper;
import com.google.backend.trading.dao.mapper.DefaultTradeSpotMarginMiddleOrderMapper;
import com.google.backend.trading.dao.mapper.DefaultTradeSpotOrderMapper;
import com.google.backend.trading.dao.mapper.DefaultTradeTransactionMapper;
import com.google.backend.trading.dao.model.TradeMarginOrder;
import com.google.backend.trading.dao.model.TradeMarginOrderExample;
import com.google.backend.trading.dao.model.TradePosition;
import com.google.backend.trading.dao.model.TradePositionExample;
import com.google.backend.trading.dao.model.TradeSpotMarginMiddleOrder;
import com.google.backend.trading.dao.model.TradeSpotMarginMiddleOrderExample;
import com.google.backend.trading.dao.model.TradeSpotOrder;
import com.google.backend.trading.dao.model.TradeSpotOrderExample;
import com.google.backend.trading.dao.model.TradeTransaction;
import com.google.backend.trading.dao.model.TradeTransactionExample;
import com.google.backend.trading.exception.BusinessException;
import com.google.backend.trading.exception.BusinessExceptionEnum;
import com.google.backend.trading.model.common.PageResult;
import com.google.backend.trading.model.common.PageResultWithPage;
import com.google.backend.trading.model.common.Response;
import com.google.backend.trading.model.margin.PositionStatus;
import com.google.backend.trading.model.margin.api.ActivePositionInfoVo;
import com.google.backend.trading.model.margin.api.MarginOrderInfoRes;
import com.google.backend.trading.model.margin.api.PositionCloseHistoryReq;
import com.google.backend.trading.model.margin.api.PositionCloseHistoryRes;
import com.google.backend.trading.model.margin.api.PositionSettleHistoryReq;
import com.google.backend.trading.model.margin.api.PositionSettleHistoryRes;
import com.google.backend.trading.model.margin.api.PositionSettleInfoRes;
import com.google.backend.trading.model.margin.dto.MarginInfo;
import com.google.backend.trading.model.margin.dto.MarginOrderCancel;
import com.google.backend.trading.model.margin.dto.MarginOrderPlace;
import com.google.backend.trading.model.margin.dto.PositionSettle;
import com.google.backend.trading.model.open.api.ActivePositionVo;
import com.google.backend.trading.model.open.api.ClosePositionTransactionVo;
import com.google.backend.trading.model.open.api.MarginInfoRes;
import com.google.backend.trading.model.open.api.OpenApiFillStatus;
import com.google.backend.trading.model.open.api.OpenApiOrderType;
import com.google.backend.trading.model.open.api.OpenApiStatus;
import com.google.backend.trading.model.open.api.OpenApiTransactionType;
import com.google.backend.trading.model.open.api.OrderCancelReq;
import com.google.backend.trading.model.open.api.OrderInfoVo;
import com.google.backend.trading.model.open.api.OrderPlaceReq;
import com.google.backend.trading.model.open.api.OrderPlaceRes;
import com.google.backend.trading.model.open.api.OrderTypeAndStrategy;
import com.google.backend.trading.model.open.api.PositionCloseReq;
import com.google.backend.trading.model.open.api.PositionCloseVo;
import com.google.backend.trading.model.open.api.PositionSettleHistoryVo;
import com.google.backend.trading.model.open.api.RfqRes;
import com.google.backend.trading.model.open.api.SettlementsInfoRes;
import com.google.backend.trading.model.open.api.SettlementsReq;
import com.google.backend.trading.model.open.api.SymbolConfigRes;
import com.google.backend.trading.model.open.api.TransactionVo;
import com.google.backend.trading.model.spot.api.SpotOrderPlaceRes;
import com.google.backend.trading.model.spot.dto.SpotOrderCancel;
import com.google.backend.trading.model.spot.dto.SpotOrderPlace;
import com.google.backend.trading.model.trade.Direction;
import com.google.backend.trading.model.trade.OrderStatus;
import com.google.backend.trading.model.trade.OrderType;
import com.google.backend.trading.model.trade.PdtStatus;
import com.google.backend.trading.model.trade.SourceType;
import com.google.backend.trading.model.trade.TradeStrategy;
import com.google.backend.trading.model.trade.TradeTerminator;
import com.google.backend.trading.model.trade.TransactionType;
import com.google.backend.trading.model.web.MiddleOrderType;
import com.google.backend.trading.service.MarginService;
import com.google.backend.trading.service.SpotService;
import com.google.backend.trading.util.CommonUtils;
import com.google.backend.trading.util.ListUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 提供给open api的接口，用于api用户使用
 *
 * @author trading
 * @date 2021/9/29 15:25
 */
@Api(tags = "提供给open api的接口")
@Slf4j
@Validated
@RequestMapping("/internal/v1/open")
@RestController
public class OpenApiController {


	@Autowired
	private MarginService marginService;
	@Autowired
	private SpotService spotService;
	@Autowired
	private DefaultTradeSpotMarginMiddleOrderMapper defaultTradeSpotMarginMiddleOrderMapper;
	@Autowired
	private DefaultTradeSpotOrderMapper defaultTradeSpotOrderMapper;
	@Autowired
	private DefaultTradeMarginOrderMapper defaultTradeMarginOrderMapper;
	@Autowired
	private DefaultTradePositionMapper defaultTradePositionMapper;
	@Autowired
	private DefaultTradeTransactionMapper defaultTradeTransactionMapper;

	@GetMapping("/margin")
	@ApiOperation(value = "杠杆信息")
	public Response<MarginInfoRes> marginInfo(@RequestParam String uid) {
		MarginInfo info = marginService.marginInfo(uid);
		MarginInfoRes res = new MarginInfoRes();
		res.setUsedMargin(info.getUsedMargin());
		res.setUnpnl(info.getUnpnl());
		res.setUsedCredit(info.getUsedCredit());
		res.setUsedMarginWithoutCredit(info.getUsedMarginWithoutCredit());
		return Response.ok(res);
	}


	/**
	 * GET api/v1/rfq
	 *
	 * @param symbol
	 * @param quantity
	 * @param direction
	 * @return
	 */
	@GetMapping("/rfq")
	@ApiOperation(value = "询价")
	public Response<RfqRes> rfq(@RequestParam("contract") String symbol, @RequestParam BigDecimal quantity,
								@RequestParam String direction) {
		symbol = symbol.toUpperCase();
		direction = direction.toUpperCase();
		BigDecimal price = SymbolDomain.nonNullGet(symbol).priceByQuantity(quantity, Direction.getByName(direction));
		RfqRes res = new RfqRes();
		res.setRfqId(UUID.randomUUID().toString());
		res.setValidUntil(System.currentTimeMillis() + 1000);
		res.setPrice(price);
		return Response.ok(res);
	}

	/**
	 * GET api/v1/contracts
	 *
	 * @return
	 */
	@GetMapping("/symbol/config")
	@ApiOperation(value = "币对配置")
	public Response<SymbolConfigRes> symbolConfig() {
		Map<String, SymbolConfigRes.Config> map = SymbolDomain.CACHE.entrySet().stream()
				.collect(Collectors.toMap(e -> e.getKey().toLowerCase(),
						e -> new SymbolConfigRes.Config(e.getValue().getCoinSymbolConfig(),
								CoinDomain.nonNullGet(CommonUtils.coinPair(e.getKey()).getFirst()).getCommonConfig())));
		SymbolConfigRes res = new SymbolConfigRes();
		res.setMap(map);
		return Response.ok(res);
	}

	/**
	 * POST api/v1/orders
	 *
	 * @param req
	 * @return
	 */
	@PostMapping("/order/place")
	@ApiOperation(value = "下单")
	public Response<OrderPlaceRes> placeOrder(@Valid @RequestBody OrderPlaceReq req) {
		OpenApiOrderType type = OpenApiOrderType.getByName(req.getType());
		OrderTypeAndStrategy orderTypeAndStrategy = type.toOrderTypeAndStrategy();
		OrderPlaceRes res = new OrderPlaceRes();
		String symbol = req.getSymbol();
		//rewrite precision
		int pricePrecision = CommonUtils.getPrecision(symbol);
		BigDecimal originalPrice = req.getPrice();
		BigDecimal price = null == originalPrice ? null : originalPrice.setScale(pricePrecision, RoundingMode.HALF_UP);
		log.info("symbol = {}, before price = {}, after price = {}", symbol, originalPrice, price);
		if (req.isSpot()) {
			SpotOrderPlace place = new SpotOrderPlace();
			place.setUid(req.getUid());
			place.setDirection(Direction.getByCode(req.getDirection()));
			place.setType(orderTypeAndStrategy.getOrderType());
			place.setSymbol(symbol);
			if (place.getType().isLimitOrder()) {
				place.setStrategy(orderTypeAndStrategy.getStrategy());
				place.setPrice(price);
			}
			place.setIsQuote(false);
			place.setQuantity(req.getQuantity());
			place.setSource(SourceType.PLACED_BY_API);
			SpotOrderPlaceRes placeRes = spotService.placeOrder(place);
			res.setOrderId(placeRes.getOrderId());
			res.setSymbol(symbol);
			res.setDirection(req.getDirection());
			res.setPrice(price);
			res.setQuantity(req.getQuantity());
			OrderStatus orderStatus = OrderStatus.getByName(placeRes.getStatus());
			res.setStatus(OpenApiStatus.fromOrderStatus(orderStatus).name());
			res.setFillStatus(OpenApiFillStatus.fromOrderStatus(orderStatus).name());
			res.setFilledQuantity(placeRes.getQuantityFilled());
			res.setFilledPrice(placeRes.getFilledPrice());
			res.setCtime(placeRes.getCtime().getTime());
			res.setOrderType(req.getType());
		} else {
			MarginOrderPlace place = MarginOrderPlace.builder()
					.uid(req.getUid())
					.direction(Direction.getByCode(req.getDirection()))
					.quantity(req.getQuantity())
					.type(orderTypeAndStrategy.getOrderType())
					.symbol(symbol)
					.source(SourceType.PLACED_BY_API)
					.build();
			if (place.getType().isLimitOrder()) {
				place.setPrice(price);
				place.setStrategy(orderTypeAndStrategy.getStrategy());
			}
			MarginOrderInfoRes placeRes = marginService.placeOrder(place);
			res.setOrderId(placeRes.getOrderId());
			res.setSymbol(placeRes.getSymbol());
			res.setDirection(placeRes.getDirection());
			res.setPrice(placeRes.getPrice());
			res.setQuantity(placeRes.getQuantity());
			OrderStatus orderStatus = OrderStatus.getByName(placeRes.getStatus());
			res.setStatus(OpenApiStatus.fromOrderStatus(orderStatus).name());
			res.setFillStatus(OpenApiFillStatus.fromOrderStatus(orderStatus).name());
			res.setFilledQuantity(placeRes.getQuantityFilled());
			res.setFilledPrice(placeRes.getFilledPrice());
			res.setCtime(placeRes.getCtime().getTime());
			res.setOrderType(req.getType());
		}
		res.setIsSpot(req.isSpot());
		return Response.ok(res);
	}


	/**
	 * GET api/v1/orders?order_id=xxx
	 *
	 * @param uid
	 * @param orderId
	 * @return
	 */
	@GetMapping("/order/info")
	@ApiOperation(value = "订单信息")
	public Response<OrderInfoVo> orderInfo(@RequestParam("uid") String uid, @RequestParam("order_id") String orderId) {
		TradeSpotMarginMiddleOrderExample example = new TradeSpotMarginMiddleOrderExample();
		TradeSpotMarginMiddleOrderExample.Criteria criteria = example.createCriteria();
		criteria.andUidEqualTo(uid);
		criteria.andOrderIdEqualTo(orderId);
		List<TradeSpotMarginMiddleOrder> middleOrders = defaultTradeSpotMarginMiddleOrderMapper.selectByExample(example);
		if (middleOrders.isEmpty()) {
			throw new BusinessException(BusinessExceptionEnum.ORDER_CHANGE_OR_NOT_FOUND);
		}
		TradeSpotMarginMiddleOrder middleOrder = middleOrders.get(0);
		if (Objects.equals(middleOrders.get(0).getType(), MiddleOrderType.MARGIN.name())) {
			TradeMarginOrderExample marginExample = new TradeMarginOrderExample();
			marginExample.createCriteria().andUuidEqualTo(middleOrder.getOrderId());
			List<TradeMarginOrder> orders = defaultTradeMarginOrderMapper.selectByExample(marginExample);
			if (orders.isEmpty()) {
				throw new BusinessException(BusinessExceptionEnum.ORDER_CHANGE_OR_NOT_FOUND);
			}
			TradeMarginOrder order = orders.get(0);
			return Response.ok(tradeMarginOrder2OrderInfoVo(order));
		} else {
			TradeSpotOrderExample spotExample = new TradeSpotOrderExample();
			spotExample.createCriteria().andUuidEqualTo(middleOrder.getOrderId());
			List<TradeSpotOrder> orders = defaultTradeSpotOrderMapper.selectByExample(spotExample);
			if (orders.isEmpty()) {
				throw new BusinessException(BusinessExceptionEnum.ORDER_CHANGE_OR_NOT_FOUND);
			}
			TradeSpotOrder order = orders.get(0);
			return Response.ok(tradeSpotOrder2OrderInfoVo(order));
		}
	}

	/**
	 * GET api/v1/orders
	 *
	 * @param uid
	 * @param page
	 * @param pageSize
	 * @return
	 */
	@GetMapping("/order/list")
	@ApiOperation(value = "用户所有订单信息")
	public Response<PageResultWithPage<OrderInfoVo>> orders(@RequestParam("uid") String uid,
															@Min(value = 1) @RequestParam(value = "page", defaultValue = "1") Integer page,
															@Max(value = 500) @RequestParam(value = "page_size", defaultValue = "6") Integer pageSize) {
		TradeSpotMarginMiddleOrderExample example = new TradeSpotMarginMiddleOrderExample();
		TradeSpotMarginMiddleOrderExample.Criteria criteria = example.createCriteria();
		criteria.andUidEqualTo(uid);
		example.setOrderByClause("ctime desc");
		return Response.ok(getOrderInfoVoRes(example, page, pageSize));
	}

	@PostMapping("/position/close")
	@ApiOperation(value = "平仓")
	public Response<PositionCloseVo> positionClose(@Valid @RequestBody PositionCloseReq req) {
		TradePosition position = getActivePosition(req.getUid(), req.getSymbol());
		MarginOrderPlace place = MarginOrderPlace.builder()
				.uid(req.getUid())
				.direction(Direction.rivalDirection(position.getDirection()))
				.quantity(position.getQuantity())
				.type(OrderType.MARKET)
				.symbol(position.getSymbol())
				.source(SourceType.PLACED_BY_API)
				.build();
		MarginOrderInfoRes order = marginService.placeOrder(place);
		OrderStatus orderStatus = OrderStatus.getByName(order.getStatus());
		PositionCloseVo vo = new PositionCloseVo();
		vo.setOrderId(order.getOrderId());
		vo.setSymbol(order.getSymbol());
		vo.setDirection(order.getDirection());
		vo.setQuantity(order.getQuantity());
		vo.setFilledQuantity(order.getQuantityFilled());
		vo.setFilledPrice(order.getFilledPrice());
		vo.setCtime(order.getCtime().getTime());
		vo.setMtime(order.getMtime().getTime());
		vo.setStatus(OpenApiStatus.fromOrderStatus(orderStatus).name());
		vo.setFillStatus(OpenApiFillStatus.fromOrderStatus(orderStatus).name());
		return Response.ok(vo);
	}

	/**
	 * GET api/v1/open_positions
	 *
	 * @param uid
	 * @param page
	 * @param pageSize
	 * @return
	 */
	@GetMapping("/active/positions")
	@ApiOperation(value = "用户在持仓位")
	public Response<PageResultWithPage<ActivePositionVo>> activePositions(@RequestParam String uid,
																		  @Min(value = 1) @RequestParam(value = "page",
																				  defaultValue = "1") Integer page,
																		  @Max(value = 500) @RequestParam(value = "page_size",
																				  defaultValue = "6") Integer pageSize) {
		List<ActivePositionInfoVo> positions = marginService.positionActive(uid);
		List<ActivePositionVo> items = new ArrayList<>();
		for (ActivePositionInfoVo position : positions) {
			ActivePositionVo vo = new ActivePositionVo();
			vo.setDirection(position.getDirection());
			vo.setQuantity(position.getQuantity());
			vo.setPrice(position.getOpenPrice());
			vo.setPnlCoin(position.getUnpnlCoin());
			vo.setPnl(position.getUnpnl());
			vo.setUsedMargin(position.getUsedMargin());
			vo.setSymbol(position.getSymbol());
			vo.setMtime(position.getMtime());

			String[] coins = position.getSymbol().split(CommonUtils.SEPARATOR);
			CoinDomain base = CoinDomain.nonNullGet(coins[0]);
			CoinDomain quote = CoinDomain.nonNullGet(coins[1]);
			ActivePositionVo.FundingRate baseFundingRate = new ActivePositionVo.FundingRate(base.getLend(), base.getBorrow());
			ActivePositionVo.FundingRate quoteFundingRate = new ActivePositionVo.FundingRate(quote.getLend(), quote.getBorrow());

			vo.setSymbolFundingRate(new ActivePositionVo.SymbolFundingRate(base.getName(), baseFundingRate, quote.getName(),
					quoteFundingRate, System.currentTimeMillis()));
			vo.setExpectedFundingCost(position.getExpectedFundingCost());
			vo.setPosition(position.getPosition());
			vo.setSize(position.getQuantity());
			items.add(vo);
		}
		// sorted by mtime and do paging
		items = items.stream().sorted(Comparator.comparing(ActivePositionVo::getMtime).reversed()).collect(Collectors.toList());
		items = items.subList(Math.min((page - 1) * pageSize, items.size()), Math.min(page * pageSize, items.size()));
		return Response.ok(PageResultWithPage.generate(positions.size(), page, pageSize, items));
	}


	/**
	 * GET api/v1/settlements
	 *
	 * @param uid
	 * @param symbol
	 * @return
	 */
	@GetMapping("/settlements/info")
	@ApiOperation(value = "仓位交割信息")
	public Response<SettlementsInfoRes> settlementsInfo(@RequestParam String uid, @RequestParam(value = "contract") String symbol) {
		symbol = symbol.toUpperCase();
		TradePosition position = getActivePosition(uid, symbol);
		PositionSettleInfoRes info = marginService.getSettlePositionInfo(position.getUuid(), position.getUid());
		SettlementsInfoRes res = new SettlementsInfoRes();
		res.setPositionId(info.getPositionId());
		res.setSymbol(info.getSymbol());
		String costCoin = Objects.equals(info.getDirection(), Direction.BUY.getName()) ? CommonUtils.getQuoteCoin(info.getSymbol()) :
				CommonUtils.getBaseCoin(info.getSymbol());
		res.setAvailableBalance(ImmutableMap.of(costCoin.toLowerCase(), info.getAvailableBalance()));
		res.setDirection(info.getDirection());
		res.setQuantity(info.getPositionQuantity());
		res.setOpenPrice(info.getOpenPrice());
		res.setMaxSettle(info.getMaxSettleQuantity());
		return Response.ok(res);
	}

	@PostMapping("/settlements")
	@ApiOperation(value = "仓位交割")
	public Response<Void> settlements(@Valid @RequestBody SettlementsReq req) {
		PositionSettle settleReq = new PositionSettle();
		settleReq.setQuantity(req.getSettleSize());
		settleReq.setUid(req.getUid());
		settleReq.setPositionId(req.getPositionId());
		settleReq.setSource(SourceType.PLACED_BY_API);
		marginService.settlePosition(settleReq);
		return Response.ok();
	}

	/**
	 * GET api/v1/closed_positions
	 *
	 * @param uid
	 * @param page
	 * @param pageSize
	 * @return
	 */
	@GetMapping("/close/transactions")
	@ApiOperation(value = "平仓交易记录")
	public Response<PageResultWithPage<ClosePositionTransactionVo>> closePositionTransactions(@RequestParam String uid,
																							  @Min(value = 1) @RequestParam(value = "page"
																									  , defaultValue = "1") Integer page,
																							  @Max(value = 500) @RequestParam(value =
																									  "page_size", defaultValue = "6") Integer pageSize) {
		PositionCloseHistoryReq req = new PositionCloseHistoryReq();
		req.setPage(page);
		req.setPageSize(pageSize);
		PageResult<PositionCloseHistoryRes> serviceRes = marginService.positionCloseHistory(req, uid);
		List<ClosePositionTransactionVo> items = new ArrayList<>();
		for (PositionCloseHistoryRes item : serviceRes.getItems()) {
			ClosePositionTransactionVo vo = new ClosePositionTransactionVo();
			vo.setSymbol(item.getSymbol());
			vo.setDirection(item.getDirection());
			vo.setSize(item.getQuantity());
			vo.setOpenPrice(item.getOpenPrice());
			vo.setClosePrice(item.getPrice());
			vo.setPnl(item.getPnl());
			vo.setPnlCoin(item.getPnlCoin());
			vo.setCtime(item.getCtime().getTime());
			items.add(vo);
		}
		return Response.ok(PageResultWithPage.generate(serviceRes.getCount(), page, pageSize, items));
	}

	/**
	 * GET api/v1/settled_positions
	 *
	 * @param uid
	 * @param page
	 * @param pageSize
	 * @return
	 */
	@GetMapping("/settle/transactions")
	@ApiOperation(value = "交割交易记录")
	public Response<PageResultWithPage<PositionSettleHistoryVo>> settleTransaction(@RequestParam String uid,
																				   @Min(value = 1) @RequestParam(value = "page",
																						   defaultValue = "1") Integer page,
																				   @Max(value = 500) @RequestParam(value = "page_size",
																						   defaultValue = "6") Integer pageSize) {
		PositionSettleHistoryReq req = new PositionSettleHistoryReq();
		req.setPage(page);
		req.setPageSize(pageSize);
		PageResult<PositionSettleHistoryRes> serviceRes = marginService.positionSettleHistory(req, uid);
		List<PositionSettleHistoryVo> items = new ArrayList<>();
		for (PositionSettleHistoryRes item : serviceRes.getItems()) {
			PositionSettleHistoryVo vo = new PositionSettleHistoryVo();
			vo.setTransactionId(item.getTransactionId());
			vo.setSymbol(item.getSymbol());
			String[] coins = item.getSymbol().split(CommonUtils.SEPARATOR);
			vo.setBase(coins[0]);
			vo.setBaseQuantity(item.getQuantity());
			vo.setQuote(coins[1]);
			vo.setQuoteQuantity(item.getQuantity().multiply(item.getPrice()));
			vo.setCtime(item.getCtime().getTime());
			vo.setMtime(item.getCtime().getTime());
			vo.setDirection(item.getDirection());
			items.add(vo);
		}
		return Response.ok(PageResultWithPage.generate(serviceRes.getCount(), page, pageSize, items));
	}

	/**
	 * GET api/v1/active_orders
	 *
	 * @param uid
	 * @param page
	 * @param pageSize
	 * @return
	 */
	@GetMapping("/active/orders")
	@ApiOperation(value = "委托中的订单")
	public Response<PageResultWithPage<OrderInfoVo>> activeOrders(@RequestParam String uid,
																  @Min(value = 1) @RequestParam(value = "page", defaultValue = "1") Integer page,
																  @Max(value = 500) @RequestParam(value = "page_size", defaultValue = "6") Integer pageSize) {
		TradeSpotMarginMiddleOrderExample example = new TradeSpotMarginMiddleOrderExample();
		TradeSpotMarginMiddleOrderExample.Criteria criteria = example.createCriteria();
		criteria.andUidEqualTo(uid);
		criteria.andStatusIn(OrderStatus.ACTIVE_STATUS);
		example.setOrderByClause("ctime desc");
		return Response.ok(getOrderInfoVoRes(example, page, pageSize));
	}

	/**
	 * PUT api/v1/active_orders/cancel
	 */
	@PutMapping("/order/cancel")
	@ApiOperation(value = "取消订单")
	public Response<Void> cancelOrder(@Valid @RequestBody OrderCancelReq req) {
		TradeSpotMarginMiddleOrderExample example = new TradeSpotMarginMiddleOrderExample();
		example.createCriteria().andOrderIdEqualTo(req.getOrderId()).andUidEqualTo(req.getUid());
		List<TradeSpotMarginMiddleOrder> orders = defaultTradeSpotMarginMiddleOrderMapper.selectByExample(example);
		if (ListUtil.isEmpty(orders)) {
			throw new BusinessException(BusinessExceptionEnum.ORDER_CHANGE_OR_NOT_FOUND);
		}
		TradeSpotMarginMiddleOrder order = orders.get(0);
		if (Objects.equals(order.getType(), MiddleOrderType.MARGIN.name())) {
			MarginOrderCancel cancelReq = MarginOrderCancel.builder()
					.orderId(req.getOrderId())
					.uid(req.getUid())
					.terminator(TradeTerminator.CLIENT)
					.build();
			marginService.cancelOrder(cancelReq);
		} else {
			SpotOrderCancel cancelReq = SpotOrderCancel.builder()
					.orderId(req.getOrderId())
					.uid(req.getUid())
					.terminator(TradeTerminator.CLIENT)
					.build();
			spotService.cancelOrder(cancelReq);
		}
		return Response.ok();
	}

	/**
	 * GET api/v1/trades
	 *
	 * @param uid
	 * @param page
	 * @param pageSize
	 * @return
	 */
	@GetMapping("/transactions/list")
	@ApiOperation(value = "交易记录")
	public Response<PageResultWithPage<TransactionVo>> transactionRecords(@RequestParam String uid,
																		  @Min(value = 1) @RequestParam(value = "page",
																				  defaultValue = "1") Integer page,
																		  @Max(value = 500) @RequestParam(value = "page_size",
																				  defaultValue = "6") Integer pageSize) {
		PageHelper.startPage(page, pageSize, true);
		TradeTransactionExample example = new TradeTransactionExample();
		TradeTransactionExample.Criteria criteria = example.createCriteria();
		//排除无效订单
		criteria.andPdtStatusEqualTo(PdtStatus.COMPLETED.name());
		example.setOrderByClause("CTIME DESC");
		criteria.andUidEqualTo(uid);
		// 只展示开仓，加仓，平仓，减仓，现货
		criteria.andTypeIn(Arrays.asList(TransactionType.OPEN_POSITION.getName(), TransactionType.ADD_POSITION.getName(),
				TransactionType.CLOSE_POSITION.getName(), TransactionType.REDUCE_POSITION.getName(),
				TransactionType.SPOT.getName()));
		List<TradeTransaction> transactions = defaultTradeTransactionMapper.selectByExample(example);
		PageInfo<TradeTransaction> pageInfo = new PageInfo<>(transactions);
		List<TransactionVo> items = new ArrayList<>();
		for (TradeTransaction item : transactions) {
			TransactionVo vo = new TransactionVo();
			vo.setTransactionId(item.getUuid());
			vo.setDirection(item.getDirection());
			vo.setSymbol(item.getSymbol());
			vo.setQuantity(item.getBaseQuantity());
			vo.setPrice(item.getPrice());
			vo.setCtime(item.getCtime().getTime());
			vo.setType(OpenApiTransactionType.fromTransactionType(TransactionType.getByName(item.getType())).name());
			// TODO: get order strategy
			OrderTypeAndStrategy ts = new OrderTypeAndStrategy(OrderType.getByName(item.getOrderType()), TradeStrategy.GTC);
			vo.setOrderType(OpenApiOrderType.fromOrderTypeAndStrategy(ts).name());
			vo.setOrderId(item.getOrderId());
			items.add(vo);
		}
		return Response.ok(PageResultWithPage.generate(pageInfo.getTotal(), pageInfo.getPageNum(), pageInfo.getPageSize(), items));
	}

	private TradePosition getActivePosition(String uid, String symbol) {
		TradePositionExample example = new TradePositionExample();
		example.createCriteria().andStatusEqualTo(PositionStatus.ACTIVE.name()).andUidEqualTo(uid).andSymbolEqualTo(symbol);
		List<TradePosition> positions = defaultTradePositionMapper.selectByExample(example);
		if (ListUtil.isEmpty(positions)) {
			throw new BusinessException(BusinessExceptionEnum.ORDER_CHANGE_OR_NOT_FOUND);
		}
		return positions.get(0);
	}

	private PageResultWithPage<OrderInfoVo> getOrderInfoVoRes(TradeSpotMarginMiddleOrderExample example, int page, int pageSize) {
		PageHelper.startPage(page, pageSize, true);
		List<TradeSpotMarginMiddleOrder> middleOrders = defaultTradeSpotMarginMiddleOrderMapper.selectByExample(example);
		PageInfo<TradeSpotMarginMiddleOrder> pageInfo = new PageInfo<>(middleOrders);

		List<String> marginOrderIds = new ArrayList<>();
		List<String> spotOrderIds = new ArrayList<>();
		for (TradeSpotMarginMiddleOrder middleOrder : middleOrders) {
			if (Objects.equals(middleOrder.getType(), MiddleOrderType.MARGIN.name())) {
				marginOrderIds.add(middleOrder.getOrderId());
			} else {
				spotOrderIds.add(middleOrder.getOrderId());
			}
		}
		HashMap<String, TradeMarginOrder> marginOrderMap = new HashMap<>();
		if (!ListUtil.isEmpty(marginOrderIds)) {
			TradeMarginOrderExample marginOrderExample = new TradeMarginOrderExample();
			TradeMarginOrderExample.Criteria marginOrderCriteria = marginOrderExample.createCriteria();
			marginOrderCriteria.andUuidIn(marginOrderIds);
			List<TradeMarginOrder> marginOrders = defaultTradeMarginOrderMapper.selectByExample(marginOrderExample);
			for (TradeMarginOrder order : marginOrders) {
				marginOrderMap.put(order.getUuid(), order);
			}
		}
		HashMap<String, TradeSpotOrder> spotOrderMap = new HashMap<>();
		if (!ListUtil.isEmpty(spotOrderIds)) {
			TradeSpotOrderExample spotOrderExample = new TradeSpotOrderExample();
			TradeSpotOrderExample.Criteria spotOrderCriteria = spotOrderExample.createCriteria();
			spotOrderCriteria.andUuidIn(spotOrderIds);
			List<TradeSpotOrder> spotOrders = defaultTradeSpotOrderMapper.selectByExample(spotOrderExample);
			for (TradeSpotOrder order : spotOrders) {
				spotOrderMap.put(order.getUuid(), order);
			}
		}
		List<OrderInfoVo> items = new ArrayList<>();
		for (TradeSpotMarginMiddleOrder middleOrder : middleOrders) {
			if (Objects.equals(middleOrder.getType(), MiddleOrderType.MARGIN.name())) {
				TradeMarginOrder order = marginOrderMap.get(middleOrder.getOrderId());
				items.add(tradeMarginOrder2OrderInfoVo(order));
			} else {
				TradeSpotOrder order = spotOrderMap.get(middleOrder.getOrderId());
				items.add(tradeSpotOrder2OrderInfoVo(order));
			}
		}
		return PageResultWithPage.generate(pageInfo.getTotal(), pageInfo.getPageNum(), pageInfo.getPageSize(), items);
	}

	private OrderInfoVo tradeMarginOrder2OrderInfoVo(TradeMarginOrder order) {
		OrderInfoVo vo = new OrderInfoVo();
		vo.setIsSpot(false);
		vo.setReduceOnly(order.getReduceOnly());
		vo.setOrderId(order.getUuid());
		vo.setDirection(order.getDirection());
		vo.setSymbol(order.getSymbol());
		OrderTypeAndStrategy orderTypeAndStrategy = new OrderTypeAndStrategy(OrderType.getByName(order.getType()),
				TradeStrategy.getByName(order.getStrategy()));
		vo.setOrderType(OpenApiOrderType.fromOrderTypeAndStrategy(orderTypeAndStrategy).getName());
		vo.setQuantity(order.getQuantity());
		vo.setFilledQuantity(order.getQuantityFilled());
		vo.setPrice(order.getPrice());
		vo.setFilledPrice(order.getFilledPrice());
		OrderStatus orderStatus = OrderStatus.getByName(order.getStatus());
		vo.setStatus(OpenApiStatus.fromOrderStatus(orderStatus).name());
		vo.setFillStatus(OpenApiFillStatus.fromOrderStatus(orderStatus).name());
		vo.setCtime(order.getCtime().getTime());
		return vo;
	}

	private OrderInfoVo tradeSpotOrder2OrderInfoVo(TradeSpotOrder order) {
		OrderInfoVo vo = new OrderInfoVo();
		vo.setIsSpot(true);
		vo.setOrderId(order.getUuid());
		vo.setDirection(order.getDirection());
		vo.setSymbol(order.getSymbol());
		OrderTypeAndStrategy orderTypeAndStrategy = new OrderTypeAndStrategy(OrderType.getByName(order.getType()),
				TradeStrategy.getByName(order.getStrategy()));
		vo.setOrderType(OpenApiOrderType.fromOrderTypeAndStrategy(orderTypeAndStrategy).getName());
		vo.setQuantity(order.getQuantity());
		vo.setFilledQuantity(order.getQuantityFilled());
		vo.setPrice(order.getPrice());
		vo.setFilledPrice(order.getFilledPrice());
		OrderStatus orderStatus = OrderStatus.getByName(order.getStatus());
		vo.setStatus(OpenApiStatus.fromOrderStatus(orderStatus).name());
		vo.setFillStatus(OpenApiFillStatus.fromOrderStatus(orderStatus).name());
		vo.setCtime(order.getCtime().getTime());
		return vo;
	}

}
