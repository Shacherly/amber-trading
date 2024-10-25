package com.google.backend.trading.controller.internal;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.ImmutableMap;
import com.google.backend.trading.component.CoinDomain;
import com.google.backend.trading.component.SymbolDomain;
import com.google.backend.trading.constant.Constants;
import com.google.backend.trading.dao.mapper.DefaultTradeMarginOrderMapper;
import com.google.backend.trading.dao.mapper.DefaultTradePositionMapper;
import com.google.backend.trading.dao.mapper.DefaultTradeSpotOrderMapper;
import com.google.backend.trading.dao.mapper.DefaultTradeTransactionMapper;
import com.google.backend.trading.dao.model.TradeMarginOrder;
import com.google.backend.trading.dao.model.TradeMarginOrderExample;
import com.google.backend.trading.dao.model.TradePosition;
import com.google.backend.trading.dao.model.TradePositionExample;
import com.google.backend.trading.dao.model.TradeSpotOrder;
import com.google.backend.trading.dao.model.TradeSpotOrderExample;
import com.google.backend.trading.dao.model.TradeTransaction;
import com.google.backend.trading.dao.model.TradeTransactionExample;
import com.google.backend.trading.exception.BusinessException;
import com.google.backend.trading.exception.BusinessExceptionEnum;
import com.google.backend.trading.mapstruct.open.api.OpenApiStruct;
import com.google.backend.trading.model.common.PageResultWithPage;
import com.google.backend.trading.model.common.Response;
import com.google.backend.trading.model.margin.PositionStatus;
import com.google.backend.trading.model.margin.api.ActivePositionInfoVo;
import com.google.backend.trading.model.margin.api.MarginOrderInfoRes;
import com.google.backend.trading.model.margin.api.PositionSettleInfoRes;
import com.google.backend.trading.model.margin.dto.MarginInfo;
import com.google.backend.trading.model.margin.dto.MarginOrderCancel;
import com.google.backend.trading.model.margin.dto.MarginOrderPlace;
import com.google.backend.trading.model.margin.dto.PositionSettle;
import com.google.backend.trading.model.open.api.MarginInfoRes;
import com.google.backend.trading.model.open.api.OpenApiTransactionType;
import com.google.backend.trading.model.open.v2.api.ActivePositionVo;
import com.google.backend.trading.model.open.v2.api.MarginPlaceReq;
import com.google.backend.trading.model.open.v2.api.MarginPlaceRes;
import com.google.backend.trading.model.open.v2.api.OrderCancelReq;
import com.google.backend.trading.model.open.v2.api.OrderInfoVo;
import com.google.backend.trading.model.open.v2.api.PositionCloseReq;
import com.google.backend.trading.model.open.v2.api.PositionCloseVo;
import com.google.backend.trading.model.open.v2.api.RfqRes;
import com.google.backend.trading.model.open.v2.api.SettlementsInfoRes;
import com.google.backend.trading.model.open.v2.api.SettlementsReq;
import com.google.backend.trading.model.open.v2.api.SpotPlaceReq;
import com.google.backend.trading.model.open.v2.api.SpotPlaceRes;
import com.google.backend.trading.model.open.v2.api.SymbolConfigRes;
import com.google.backend.trading.model.open.v2.api.TransactionVo;
import com.google.backend.trading.model.spot.api.SpotOrderPlaceRes;
import com.google.backend.trading.model.spot.dto.SpotOrderCancel;
import com.google.backend.trading.model.spot.dto.SpotOrderPlace;
import com.google.backend.trading.model.trade.Direction;
import com.google.backend.trading.model.trade.OpenApiOrderStatus;
import com.google.backend.trading.model.trade.OrderType;
import com.google.backend.trading.model.trade.PdtStatus;
import com.google.backend.trading.model.trade.SourceType;
import com.google.backend.trading.model.trade.TradeStrategy;
import com.google.backend.trading.model.trade.TradeTerminator;
import com.google.backend.trading.model.trade.TradeType;
import com.google.backend.trading.model.trade.TransactionType;
import com.google.backend.trading.model.web.MiddleOrderType;
import com.google.backend.trading.service.MarginService;
import com.google.backend.trading.service.SpotService;
import com.google.backend.trading.util.CommonUtils;
import com.google.backend.trading.util.ListUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * v2版本
 * <p>
 * 提供给open api的接口，用于api用户使用
 *
 * @author trading
 * @date 2021/9/29 15:25
 */
@Api(tags = "提供给open api的v2接口")
@Slf4j
@Validated
@RequestMapping("/internal/v2/open")
@RestController
public class OpenApiV2Controller {


    @Autowired
    private MarginService marginService;
    @Autowired
    private SpotService spotService;
    @Autowired
    private DefaultTradeSpotOrderMapper defaultTradeSpotOrderMapper;
    @Autowired
    private DefaultTradeMarginOrderMapper defaultTradeMarginOrderMapper;
    @Autowired
    private DefaultTradePositionMapper defaultTradePositionMapper;
    @Autowired
    private DefaultTradeTransactionMapper defaultTradeTransactionMapper;
    @Autowired
    private OpenApiStruct openApiStruct;

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
     * rfq 询价
     *
     * @param symbol
     * @param quantity
     * @param direction
     * @return
     */
    @GetMapping("/rfq")
    @ApiOperation(value = "询价")
    public Response<RfqRes> rfq(
            @ApiParam(value = "币对", example = "BTC_USDT") @RequestParam String symbol,
            @ApiParam(value = "数量") @RequestParam BigDecimal quantity,
            @ApiParam(value = "方向", example = "BUY, SELL") @RequestParam String direction) {
        SymbolDomain symbolDomain = SymbolDomain.nullableGet(symbol);
        if (symbolDomain == null) {
            throw new BusinessException(BusinessExceptionEnum.SYMBOL_NOT_SUPPORT_SPOT);
        }
        BigDecimal price = SymbolDomain.nonNullGet(symbol).priceByQuantity(quantity, Direction.getByName(direction));
        RfqRes res = new RfqRes();
        res.setPrice(price);
        return Response.ok(res);
    }

    /**
     * API to fetch all symbols
     * 获取币种配置
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
        res.setConfig(map);
        return Response.ok(res);
    }


    /**
     * API to create margin order
     * 杠杆下单
     *
     * @param req
     * @return
     */
    @PostMapping("/margin/order/place")
    @ApiOperation(value = "杠杆下单")
    public Response<MarginPlaceRes> placeMargin(@Valid @RequestBody MarginPlaceReq req) {
        OrderType orderType = OrderType.getByName(req.getType());
        TradeStrategy tradeStrategy = TradeStrategy.getByName(req.getStrategy());
        String symbol = req.getSymbol();
        SymbolDomain symbolDomain = SymbolDomain.nullableGet(symbol);
        if (symbolDomain == null) {
            throw new BusinessException(BusinessExceptionEnum.OPEN_API_SYMBOL_NOT_EXIST);
        }
        if (!CommonUtils.checkMarginSymbol(symbol)) {
            throw new BusinessException(BusinessExceptionEnum.SYMBOL_NOT_SUPPORT_MARGIN);
        }
        //rewrite precision
        int pricePrecision = CommonUtils.getPrecision(symbol);
        BigDecimal originalPrice = req.getPrice();
        BigDecimal price = null == originalPrice ? null : originalPrice.setScale(pricePrecision, RoundingMode.HALF_UP);
        MarginPlaceRes res = new MarginPlaceRes();
        MarginOrderPlace place = MarginOrderPlace.builder()
                .uid(req.getUid())
                .direction(Direction.getByCode(req.getDirection()))
                .quantity(req.getQuantity())
                .type(orderType)
                .symbol(symbol)
                .source(SourceType.PLACED_BY_API)
                .build();
        if (place.getType().isLimitOrder()) {
            place.setPrice(price);
            place.setStrategy(tradeStrategy);
        }
        MarginOrderInfoRes placeRes = marginService.placeOrder(place);
        res.setOrderId(placeRes.getOrderId());
        res.setOrderType(req.getType());
        res.setStrategy(req.getStrategy());
        res.setSymbol(placeRes.getSymbol());
        res.setDirection(placeRes.getDirection());
        res.setPrice(placeRes.getPrice());
        res.setQuantity(placeRes.getQuantity());
        OpenApiOrderStatus feignStatus = OpenApiOrderStatus.getFeignStatus(placeRes.getStatus());
        res.setStatus(feignStatus.getCode());
        res.setFilledQuantity(placeRes.getQuantityFilled());
        res.setFilledPrice(placeRes.getFilledPrice());
        res.setCtime(placeRes.getCtime().getTime());
        res.setMtime(placeRes.getMtime().getTime());
        return Response.ok(res);
    }

    /**
     * API to create spot order
     * 现货下单
     *
     * @param req
     * @return
     */
    @PostMapping("/spot/order/place")
    @ApiOperation(value = "现货下单")
    public Response<SpotPlaceRes> placeSpot(@Valid @RequestBody SpotPlaceReq req) {
        OrderType orderType = OrderType.getByName(req.getType());
        TradeStrategy tradeStrategy = TradeStrategy.getByName(req.getStrategy());
        String symbol = req.getSymbol();
        SymbolDomain symbolDomain = SymbolDomain.nullableGet(symbol);
        if (symbolDomain == null) {
            throw new BusinessException(BusinessExceptionEnum.OPEN_API_SYMBOL_NOT_EXIST);
        }
        if (!CommonUtils.checkSpotSymbol(symbol)) {
            throw new BusinessException(BusinessExceptionEnum.SYMBOL_NOT_SUPPORT_SPOT);
        }
        SpotPlaceRes res = new SpotPlaceRes();
        //rewrite precision
        int pricePrecision = CommonUtils.getPrecision(symbol);
        BigDecimal originalPrice = req.getPrice();
        BigDecimal price = null == originalPrice ? null : originalPrice.setScale(pricePrecision, RoundingMode.HALF_UP);
        log.info("symbol = {}, before price = {}, after price = {}", symbol, originalPrice, price);
        SpotOrderPlace place = new SpotOrderPlace();
        place.setUid(req.getUid());
        place.setDirection(Direction.getByCode(req.getDirection()));
        place.setType(orderType);
        place.setSymbol(symbol);
        if (place.getType().isLimitOrder()) {
            place.setStrategy(tradeStrategy);
            place.setPrice(price);
        }
        place.setIsQuote(false);
        place.setQuantity(req.getQuantity());
        place.setSource(SourceType.PLACED_BY_API);
        SpotOrderPlaceRes placeRes = spotService.placeOrder(place);
        res.setOrderId(placeRes.getOrderId());
        res.setOrderType(req.getType());
        res.setStrategy(req.getStrategy());
        res.setSymbol(symbol);
        res.setIsQuote(place.getIsQuote());
        res.setDirection(req.getDirection());
        res.setPrice(price);
        res.setQuantity(req.getQuantity());
        OpenApiOrderStatus feignStatus = OpenApiOrderStatus.getFeignStatus(placeRes.getStatus());
        res.setStatus(feignStatus.getCode());
        res.setFilledQuantity(placeRes.getQuantityFilled());
        res.setFilledPrice(placeRes.getFilledPrice());
        res.setCtime(placeRes.getCtime().getTime());
        res.setMtime(placeRes.getCtime().getTime());
        return Response.ok(res);
    }

    /**
     * API to get order list (filter by type(spot, margin), symbol, status, date, with pagination)
     * 订单列表（区分现货和杠杆）
     *
     * @param uid
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/orders")
    @ApiOperation(value = "用户订单列表")
    public Response<PageResultWithPage<OrderInfoVo>> orders(
            @RequestParam String uid,
            @ApiParam(value = "类目", example = "SPOT", allowableValues = "SPOT,MARGIN") @RequestParam String category,
            @ApiParam(value = "类型", example = "MARKET", allowableValues = "MARKET,LIMIT") @RequestParam(required = false) String type,
            @ApiParam(value = "策略 for LIMIT type", example = "GTC", allowableValues = "GTC,IOC,FOK") @RequestParam(required = false) String strategy,
            @ApiParam(value = "方向", example = "BUY", allowableValues = "BUY,SELL") @RequestParam(required = false) String direction,
            @ApiParam(value = "币对", example = "BTC_USDT") @RequestParam(required = false) String symbol,
            @ApiParam(value = "订单状态 PRE_TRIGGER(待触发) EXECUTING(挂单中) EXCEPTION(执行异常) COMPLETED(完全成交) CANCELED(完全取消) PART_CANCELED(部分成交取消)",
                    example = "EXECUTING")
            @RequestParam(required = false) String status,
            @ApiParam(value = "开始时间（订单创建时间）", example = "1638288000000") @RequestParam(value = "start_time") Long startTime,
            @ApiParam(value = "结束时间（订单创建时间）", example = "1648742400000") @RequestParam(value = "end_time") Long endTime,
            @Min(value = 1) @RequestParam(value = "page", defaultValue = "1") Integer page,
            @Max(value = 500) @RequestParam(value = "page_size", defaultValue = "6") Integer pageSize) {
        PageHelper.startPage(page, pageSize, true);

        TradeSpotOrderExample tradeSpotOrderExample = new TradeSpotOrderExample();
        tradeSpotOrderExample.setOrderByClause("ctime desc");
        TradeSpotOrderExample.Criteria spotCriteria = tradeSpotOrderExample.createCriteria();
        //排除BUY_CRYPTOCURRENCY_CONVERSION来源
        spotCriteria.andSourceNotEqualTo(SourceType.BUY_CRYPTOCURRENCY_CONVERSION.getName());
        TradeMarginOrderExample tradeMarginOrderExample = new TradeMarginOrderExample();
        tradeMarginOrderExample.setOrderByClause("ctime desc");
        TradeMarginOrderExample.Criteria marginCriteria = tradeMarginOrderExample.createCriteria();

        spotCriteria.andUidEqualTo(uid);
        marginCriteria.andUidEqualTo(uid);
        if (StringUtils.isNotBlank(type)) {
            List<String> listByNameLike = OrderType.getListByNameLike(type);
            if (!listByNameLike.isEmpty()) {
                spotCriteria.andTypeIn(listByNameLike);
                marginCriteria.andTypeIn(listByNameLike);
            }
        }
        if (StringUtils.isNotBlank(strategy) && Objects.equals(OrderType.LIMIT.getName(), type)) {
            spotCriteria.andSourceEqualTo(strategy);
            marginCriteria.andSourceEqualTo(strategy);
        }
        if (StringUtils.isNotBlank(direction)) {
            spotCriteria.andDirectionEqualTo(direction);
            marginCriteria.andDirectionEqualTo(direction);
        }
        if (StringUtils.isNotBlank(symbol)) {
            spotCriteria.andSymbolEqualTo(symbol);
            marginCriteria.andSymbolEqualTo(symbol);
        }
        if (StringUtils.isNotBlank(status)) {
            List<String> collect = OpenApiOrderStatus.getByCode(status).stream().map(OpenApiOrderStatus::getName).collect(Collectors.toList());
            if (!collect.isEmpty()) {
                spotCriteria.andStatusIn(collect);
                marginCriteria.andStatusIn(collect);
            }
        }
        if (startTime != null && endTime != null) {
            spotCriteria.andCtimeBetween(new Date(startTime), new Date(endTime));
            marginCriteria.andCtimeBetween(new Date(startTime), new Date(endTime));
        }

        if (TradeType.SPOT.name().equals(category)) {
            List<TradeSpotOrder> tradeSpotOrderList = defaultTradeSpotOrderMapper.selectByExample(tradeSpotOrderExample);
            PageInfo<TradeSpotOrder> pageInfo = new PageInfo<>(tradeSpotOrderList);
            List<OrderInfoVo> orderInfoVoList = openApiStruct.tradeSpotOrderList2OrderInfoVoList(pageInfo.getList());
            return Response.ok(PageResultWithPage.generate(pageInfo.getTotal(), pageInfo.getPageNum(), pageInfo.getPageSize(), orderInfoVoList));
        } else {
            List<TradeMarginOrder> tradeMarginOrderList = defaultTradeMarginOrderMapper.selectByExample(tradeMarginOrderExample);
            PageInfo<TradeMarginOrder> pageInfo = new PageInfo<>(tradeMarginOrderList);
            List<OrderInfoVo> orderInfoVoList = openApiStruct.tradeMarginOrderList2OrderInfoVoList(pageInfo.getList());
            return Response.ok(PageResultWithPage.generate(pageInfo.getTotal(), pageInfo.getPageNum(), pageInfo.getPageSize(), orderInfoVoList));
        }
    }


    /**
     * 根据订单 id 查询订单信息
     *
     * @param uid
     * @param orderId
     * @param category
     * @return
     */
    @GetMapping("/order")
    @ApiOperation(value = "用户订单")
    public Response<OrderInfoVo> order(
            @RequestParam String uid,
            @ApiParam(value = "订单id", example = "df7956ae-00e4-4eba-aa81-cfa6f1610cd6")
            @RequestParam("order_id") String orderId,
            @ApiParam(value = "类目", example = "SPOT", allowableValues = "SPOT,MARGIN")
            @RequestParam String category) {
        OrderInfoVo orderInfoVo = getOrderInfoVoByOrderId(uid, orderId, category);
        return Response.ok(orderInfoVo);
    }

    private OrderInfoVo getOrderInfoVoByOrderId(String uid, String orderId, String category) {
        TradeSpotOrderExample tradeSpotOrderExample = new TradeSpotOrderExample();
        tradeSpotOrderExample.createCriteria().andUidEqualTo(uid).andUuidEqualTo(orderId);

        TradeMarginOrderExample tradeMarginOrderExample = new TradeMarginOrderExample();
        tradeMarginOrderExample.createCriteria().andUidEqualTo(uid).andUuidEqualTo(orderId);

        OrderInfoVo orderInfoVo = new OrderInfoVo();
        if (TradeType.SPOT.name().equals(category)) {
            List<TradeSpotOrder> tradeSpotOrderList = defaultTradeSpotOrderMapper.selectByExample(tradeSpotOrderExample);
            if (CollectionUtils.isNotEmpty(tradeSpotOrderList)) {
                TradeSpotOrder tradeSpotOrder = tradeSpotOrderList.get(0);
                orderInfoVo = openApiStruct.tradeSpotOrder2OrderInfoVo(tradeSpotOrder);
            }
        } else {
            List<TradeMarginOrder> tradeMarginOrderList = defaultTradeMarginOrderMapper.selectByExample(tradeMarginOrderExample);
            if (CollectionUtils.isNotEmpty(tradeMarginOrderList)) {
                TradeMarginOrder tradeMarginOrder = tradeMarginOrderList.get(0);
                orderInfoVo = openApiStruct.tradeMarginOrder2OrderInfoVo(tradeMarginOrder);
            }
        }
        return orderInfoVo;
    }

    /**
     * API to close position
     * 平仓
     *
     * @param req
     * @return
     */
    @PostMapping("/position/close")
    @ApiOperation(value = "平仓 返回与【/margin/order/place】一致")
    public Response<PositionCloseVo> positionClose(@Valid @RequestBody PositionCloseReq req) {
        TradePosition position = getActivePosition(req.getUid(), req.getSymbol());
        MarginOrderPlace place = MarginOrderPlace.builder()
                .uid(req.getUid())
                .direction(Direction.rivalDirection(position.getDirection()))
                .quantity(position.getQuantity())
                .type(OrderType.getByName(req.getOrderType()))
                .strategy(TradeStrategy.getByName(req.getStrategy()))
                .price(req.getPrice())
                .symbol(position.getSymbol())
                .source(SourceType.PLACED_BY_API)
                .build();
        MarginOrderInfoRes placeRes = marginService.placeOrder(place);
        PositionCloseVo res = new PositionCloseVo();
        res.setOrderId(placeRes.getOrderId());
        res.setOrderType(req.getOrderType());
        res.setStrategy(req.getStrategy());
        res.setSymbol(placeRes.getSymbol());
        res.setDirection(placeRes.getDirection());
        res.setPrice(placeRes.getPrice());
        res.setQuantity(placeRes.getQuantity());
        OpenApiOrderStatus feignStatus = OpenApiOrderStatus.getFeignStatus(placeRes.getStatus());
        res.setStatus(feignStatus.getCode());
        res.setFilledQuantity(placeRes.getQuantityFilled());
        res.setFilledPrice(placeRes.getFilledPrice());
        res.setCtime(placeRes.getCtime().getTime());
        res.setMtime(placeRes.getMtime().getTime());
        return Response.ok(res);
    }

    /**
     * API to get position list (filter by status, date, symbol, with pagination)
     * 仓位列表
     *
     * @param uid
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/positions")
    @ApiOperation(value = "用户仓位列表 /active/positions -> /positions")
    public Response<PageResultWithPage<ActivePositionVo>> positions(
            @RequestParam String uid,
            @ApiParam(value = "状态", example = "ACTIVE", allowableValues = "ACTIVE,CLOSE") @RequestParam String status,
            @ApiParam(value = "币对", example = "BTC_USD") @RequestParam(required = false) String symbol,
            @ApiParam(value = "开始时间（订单创建时间）", example = "1638288000000") @RequestParam(value = "start_time") Long startTime,
            @ApiParam(value = "结束时间（订单创建时间）", example = "1648742400000") @RequestParam(value = "end_time") Long endTime,
            @Min(value = 1) @RequestParam(value = "page", defaultValue = "1") Integer page,
            @Max(value = 500) @RequestParam(value = "page_size", defaultValue = "6") Integer pageSize) {
        if (StringUtils.isNotBlank(status) && Objects.equals("ACTIVE", status)) {
            List<ActivePositionInfoVo> positions = marginService.positionActive(uid);
            positions = positions.stream()
                    .filter(p -> StringUtils.isBlank(symbol) || symbol.equals(p.getSymbol()))
                    .filter(p -> (startTime == null || endTime == null) || (p.getCtime() >= startTime && p.getCtime() <= endTime))
                    .collect(Collectors.toList());
            List<ActivePositionVo> items = new ArrayList<>();
            for (ActivePositionInfoVo position : positions) {
                ActivePositionVo vo = new ActivePositionVo();
                vo.setPositionId(position.getPositionId());
                vo.setDirection(position.getDirection());
                vo.setQuantity(position.getQuantity());
                vo.setPrice(position.getOpenPrice());
                vo.setPnlCoin(position.getUnpnlCoin());
                vo.setPnl(position.getPnl());
                vo.setUnpnl(position.getUnpnl());
                vo.setUnpnlCoin(Constants.BASE_COIN);
                vo.setUsedMargin(position.getUsedMargin());
                vo.setSymbol(position.getSymbol());
                vo.setMtime(position.getMtime());
                vo.setCtime(position.getCtime());

                String[] coins = position.getSymbol().split(CommonUtils.SEPARATOR);
                CoinDomain base = CoinDomain.nonNullGet(coins[0]);
                CoinDomain quote = CoinDomain.nonNullGet(coins[1]);
                ActivePositionVo.FundingRate baseFundingRate = new ActivePositionVo.FundingRate(base.getLend(), base.getBorrow());
                ActivePositionVo.FundingRate quoteFundingRate = new ActivePositionVo.FundingRate(quote.getLend(), quote.getBorrow());

                vo.setSymbolFundingRate(new ActivePositionVo.SymbolFundingRate(base.getName(), baseFundingRate, quote.getName(),
                        quoteFundingRate, System.currentTimeMillis()));
                vo.setExpectedFundingCost(position.getExpectedFundingCost());
                vo.setPosition(position.getPosition());
                items.add(vo);
            }
            // sorted by mtime and do paging
            items = items.stream().sorted(Comparator.comparing(ActivePositionVo::getMtime).reversed()).collect(Collectors.toList());
            items = items.subList(Math.min((page - 1) * pageSize, items.size()), Math.min(page * pageSize, items.size()));
            return Response.ok(PageResultWithPage.generate(positions.size(), page, pageSize, items));
        } else {
            PageHelper.startPage(page, pageSize);
            TradePositionExample example = new TradePositionExample();
            example.setOrderByClause("ctime desc");
            TradePositionExample.Criteria criteria = example.createCriteria().andUidEqualTo(uid);
            if (StringUtils.isNotBlank(status)) {
                criteria.andStatusEqualTo(status);
            }
            if (StringUtils.isNotBlank(symbol)) {
                criteria.andSymbolEqualTo(symbol);
            }
            if (startTime != null && endTime != null) {
                criteria.andCtimeBetween(new Date(startTime), new Date(endTime));
            }
            List<TradePosition> positionList = defaultTradePositionMapper.selectByExample(example);
            List<ActivePositionVo> activePositionVos = positionList.stream().map(p -> {
                ActivePositionVo vo = new ActivePositionVo();
                vo.setPositionId(p.getUuid());
                vo.setQuantity(p.getQuantity());
                vo.setPrice(p.getPrice());
                vo.setSymbol(p.getSymbol());
                vo.setDirection(p.getDirection());
                vo.setPnl(p.getPnl());
                vo.setPnlCoin(Constants.BASE_COIN);
                vo.setUnpnl(BigDecimal.ZERO);
                vo.setUnpnlCoin(Constants.BASE_COIN);
                vo.setUsedMargin(BigDecimal.ZERO);
                vo.setPosition(BigDecimal.ZERO);
                vo.setExpectedFundingCost(BigDecimal.ZERO);
                vo.setCtime(p.getCtime().getTime());
                vo.setMtime(p.getMtime().getTime());
                return vo;
            }).collect(Collectors.toList());
            PageInfo<ActivePositionVo> pageInfo = new PageInfo<>(activePositionVos);
            return Response.ok(PageResultWithPage.generate(pageInfo.getTotal(), pageInfo.getPageNum(), pageInfo.getPageSize(), pageInfo.getList()));
        }
    }


    /**
     * API to check settlement info
     * 仓位交割信息
     *
     * @param uid
     * @param symbol
     * @return
     */
    @GetMapping("/settlements/info")
    @ApiOperation(value = "仓位交割信息")
    public Response<SettlementsInfoRes> settlementsInfo(@RequestParam String uid, @RequestParam String symbol) {
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

    /**
     * API to settle position
     * 仓位交割
     *
     * @param req
     * @return
     */
    @PostMapping("/settlements")
    @ApiOperation(value = "仓位交割")
    public Response<Void> settlements(@Valid @RequestBody SettlementsReq req) {
        PositionSettle settleReq = new PositionSettle();
        settleReq.setQuantity(req.getQuantity());
        settleReq.setUid(req.getUid());
        settleReq.setPositionId(req.getPositionId());
        settleReq.setSource(SourceType.PLACED_BY_API);

        TradePositionExample tradePositionExample = new TradePositionExample();
        tradePositionExample.createCriteria().andUuidEqualTo(req.getPositionId());
        long count = defaultTradePositionMapper.countByExample(tradePositionExample);
        if (count == 0) {
            throw new BusinessException(BusinessExceptionEnum.OPEN_API_POSITION_NOT_EXIST);
        }
        PositionSettleInfoRes info = marginService.getSettlePositionInfo(req.getPositionId(), req.getUid());
        if (req.getQuantity().compareTo(info.getPositionQuantity()) > 0) {
            throw new BusinessException(BusinessExceptionEnum.OPEN_API_OVER_MAX_POSITION_QUANTITY);
        }
        if (req.getQuantity().compareTo(info.getMaxSettleQuantity()) > 0) {
            throw new BusinessException(BusinessExceptionEnum.INSUFFICIENT_FUNDS);
        }
        marginService.settlePosition(settleReq);
        return Response.ok();
    }

    /**
     * API to cancel open order
     * 取消订单
     */
    @PutMapping("/order/cancel")
    @ApiOperation(value = "取消订单")
    public Response<OrderInfoVo> cancelOrder(@Valid @RequestBody OrderCancelReq req) {
        TradeMarginOrderExample tradeMarginOrderExample = new TradeMarginOrderExample();
        tradeMarginOrderExample.createCriteria().andUidEqualTo(req.getUid()).andUuidEqualTo(req.getOrderId());

        TradeSpotOrderExample tradeSpotOrderExample = new TradeSpotOrderExample();
        tradeSpotOrderExample.createCriteria().andUidEqualTo(req.getUid()).andUuidEqualTo(req.getOrderId());

        if (Objects.equals(req.getCategory(), MiddleOrderType.MARGIN.name())) {

            List<TradeMarginOrder> tradeMarginOrderList = defaultTradeMarginOrderMapper.selectByExample(tradeMarginOrderExample);
            if (ListUtil.isEmpty(tradeMarginOrderList)) {
                throw new BusinessException(BusinessExceptionEnum.OPEN_API_ORDER_NOT_EXIST);
            }

            MarginOrderCancel cancelReq = MarginOrderCancel.builder()
                    .orderId(req.getOrderId())
                    .uid(req.getUid())
                    .terminator(TradeTerminator.CLIENT)
                    .build();
            marginService.cancelOrder(cancelReq);
        } else {
            List<TradeSpotOrder> tradeSpotOrderList = defaultTradeSpotOrderMapper.selectByExample(tradeSpotOrderExample);
            if (ListUtil.isEmpty(tradeSpotOrderList)) {
                throw new BusinessException(BusinessExceptionEnum.OPEN_API_ORDER_NOT_EXIST);
            }

            SpotOrderCancel cancelReq = SpotOrderCancel.builder()
                    .orderId(req.getOrderId())
                    .uid(req.getUid())
                    .terminator(TradeTerminator.CLIENT)
                    .build();
            spotService.cancelOrder(cancelReq);
        }
        OrderInfoVo orderInfoVo = getOrderInfoVoByOrderId(req.getUid(), req.getOrderId(), req.getCategory());
        return Response.ok(orderInfoVo);
    }

    /**
     * API to get transaction records(type: close, settle, ...)
     * 交易记录列表
     *
     * @param uid
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/transactions/list")
    @ApiOperation(value = "交易记录 新增pnl、fee、pnl_coin字段")
    public Response<PageResultWithPage<TransactionVo>> transactionRecords(
            @RequestParam String uid,
            @ApiParam(value = "订单id", example = "df7956ae-00e4-4eba-aa81-cfa6f1610cd6")
            @RequestParam(value = "order_id", required = false) String orderId,
            @ApiParam(value = "仓位ID", example = "df7956ae-00e4-4eba-aa81-cfa6f1610cd6")
            @RequestParam(value = "position_id", required = false) String positionId,
            @ApiParam(value = "交易记录类型", required = true, example = "SPOT", allowableValues = "MARGIN,SPOT,SETTLE") @RequestParam String type,
            @ApiParam(value = "币对", example = "BTC_USDT") @RequestParam(required = false) String symbol,
            @ApiParam(value = "方向", example = "BUY", allowableValues = "BUY,SELL") @RequestParam(required = false) String direction,
            @ApiParam(value = "订单类型", example = "MARKET", allowableValues = "MARKET,LIMIT")
            @RequestParam(value = "order_type", required = false) String orderType,
            @ApiParam(value = "开始时间（订单创建时间）", example = "1638288000000") @RequestParam(value = "start_time") Long startTime,
            @ApiParam(value = "结束时间（订单创建时间）", example = "1648742400000") @RequestParam(value = "end_time") Long endTime,
            @Min(value = 1) @RequestParam(value = "page", defaultValue = "1") Integer page,
            @Max(value = 500) @RequestParam(value = "page_size", defaultValue = "6") Integer pageSize) {
        PageHelper.startPage(page, pageSize, true);
        TradeTransactionExample example = new TradeTransactionExample();
        TradeTransactionExample.Criteria criteria = example.createCriteria();
        example.setOrderByClause("CTIME DESC");
        criteria.andUidEqualTo(uid);
        //排除BUY_CRYPTOCURRENCY_CONVERSION来源
        criteria.andSourceNotEqualTo(SourceType.BUY_CRYPTOCURRENCY_CONVERSION.getName());
        //查询有效记录
        criteria.andPdtStatusEqualTo(PdtStatus.COMPLETED.name());
        if (StringUtils.isNotBlank(orderId)) {
            criteria.andOrderIdEqualTo(orderId);
        }
        if (StringUtils.isNotBlank(type)) {
            if (Objects.equals(type, "MARGIN")) {
                criteria.andTypeIn(Arrays.asList(TransactionType.OPEN_POSITION.getName(), TransactionType.ADD_POSITION.getName(),
                        TransactionType.CLOSE_POSITION.getName(), TransactionType.REDUCE_POSITION.getName()));
            } else if (Objects.equals(type, "SPOT")) {
                criteria.andTypeEqualTo(TransactionType.SPOT.getName());
            } else if (Objects.equals(type, "SETTLE")) {
                criteria.andTypeEqualTo(TransactionType.SETTLE_POSITION.getName());
            }
        } else {
            // 只展示开仓，加仓，平仓，减仓，现货，交割
            criteria.andTypeIn(Arrays.asList(TransactionType.OPEN_POSITION.getName(), TransactionType.ADD_POSITION.getName(),
                    TransactionType.CLOSE_POSITION.getName(), TransactionType.REDUCE_POSITION.getName(),
                    TransactionType.SPOT.getName(), TransactionType.SETTLE_POSITION.getName()));
        }
        if (StringUtils.isNotBlank(symbol)) {
            criteria.andSymbolEqualTo(symbol);
        }
        if (StringUtils.isNotBlank(positionId)) {
            criteria.andPositionIdEqualTo(positionId);
        }
        if (StringUtils.isNotBlank(direction)) {
            criteria.andDirectionEqualTo(direction);
        }
        if (StringUtils.isNotBlank(orderType)) {
            List<String> listByNameLike = OrderType.getListByNameLike(orderType);
            if (!listByNameLike.isEmpty()) {
                criteria.andOrderTypeIn(listByNameLike);
            }
        }
        if (startTime != null && endTime != null) {
            criteria.andCtimeBetween(new Date(startTime), new Date(endTime));
        }
        List<TradeTransaction> transactions = defaultTradeTransactionMapper.selectByExample(example);
        PageInfo<TradeTransaction> pageInfo = new PageInfo<>(transactions);
        List<TransactionVo> items = new ArrayList<>();
        for (TradeTransaction item : transactions) {
            TransactionVo vo = new TransactionVo();
            vo.setTransactionId(item.getUuid());
            vo.setOrderType(item.getOrderType() == null ? OrderType.MARKET.getName() : OrderType.getSimpleByName(OrderType.getByName(item.getOrderType())));
            vo.setDirection(item.getDirection());
            vo.setPositionId(item.getPositionId());
            vo.setSymbol(item.getSymbol());
            vo.setQuantity(item.getBaseQuantity());
            vo.setPrice(item.getPrice());
            vo.setCtime(item.getCtime().getTime());
            vo.setType(OpenApiTransactionType.fromTransactionType(TransactionType.getByName(item.getType())).name());
            vo.setOrderId(item.getOrderId());
            vo.setFee(item.getFee());
            if (Objects.equals(type, TradeType.MARGIN.name())) {
                BigDecimal pnlConversion = item.getPnlConversion();
                if (pnlConversion != null) {
                    vo.setPnl(pnlConversion);
                    vo.setPnlCoin(Constants.BASE_COIN);
                } else {
                    BigDecimal pnl = (null == item.getPnl() ? BigDecimal.ZERO : item.getPnl()).subtract((null == item.getFee() ? BigDecimal.ZERO : item.getFee()));
                    vo.setPnl(pnl);
                    vo.setPnlCoin(CommonUtils.getQuoteCoin(item.getSymbol()));
                }
            }
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

}
