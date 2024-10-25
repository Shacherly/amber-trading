package com.google.backend.trading.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.backend.asset.common.model.asset.req.BatchUserPoolReq;
import com.google.backend.asset.common.model.base.BatchPoolEntityForRisk;
import com.google.backend.asset.common.model.base.PoolEntityForRisk;
import com.google.backend.common.dto.base.MsgHeadDto;
import com.google.backend.common.mq.HeaderUtils;
import com.google.backend.common.web.Response;
import com.google.backend.trading.alarm.AlarmComponent;
import com.google.backend.trading.alarm.AlarmEnum;
import com.google.backend.trading.client.feign.AssetRiskInfoClient;
import com.google.backend.trading.component.CoinDomain;
import com.google.backend.trading.component.LockComponent;
import com.google.backend.trading.component.SymbolDomain;
import com.google.backend.trading.constant.Constants;
import com.google.backend.trading.constant.TradeConstants;
import com.google.backend.trading.dao.mapper.DefaultTradeMarginOrderMapper;
import com.google.backend.trading.dao.mapper.DefaultTradeMarginOrderModificationMapper;
import com.google.backend.trading.dao.mapper.DefaultTradeNegativeBalanceFundingCostMapper;
import com.google.backend.trading.dao.mapper.DefaultTradePositionFundingCostMapper;
import com.google.backend.trading.dao.mapper.DefaultTradePositionMapper;
import com.google.backend.trading.dao.mapper.DefaultTradeTransactionMapper;
import com.google.backend.trading.dao.mapper.TradePositionMapper;
import com.google.backend.trading.dao.mapper.TradeTransactionMapper;
import com.google.backend.trading.dao.model.TradeMarginOrder;
import com.google.backend.trading.dao.model.TradeMarginOrderExample;
import com.google.backend.trading.dao.model.TradeMarginOrderModification;
import com.google.backend.trading.dao.model.TradeMarginOrderModificationExample;
import com.google.backend.trading.dao.model.TradeNegativeBalanceFundingCost;
import com.google.backend.trading.dao.model.TradeNegativeBalanceFundingCostExample;
import com.google.backend.trading.dao.model.TradePosition;
import com.google.backend.trading.dao.model.TradePositionExample;
import com.google.backend.trading.dao.model.TradePositionFundingCost;
import com.google.backend.trading.dao.model.TradePositionFundingCostExample;
import com.google.backend.trading.dao.model.TradeTransaction;
import com.google.backend.trading.dao.model.TradeTransactionExample;
import com.google.backend.trading.dao.model.TradeUserTradeSetting;
import com.google.backend.trading.exception.BusinessException;
import com.google.backend.trading.exception.BusinessExceptionEnum;
import com.google.backend.trading.exception.LockException;
import com.google.backend.trading.mapstruct.margin.MarginAssetMapStruct;
import com.google.backend.trading.mapstruct.margin.OrderMapStruct;
import com.google.backend.trading.mapstruct.margin.OrderModificationMapStruct;
import com.google.backend.trading.mapstruct.margin.PositionFundingCostMapStruct;
import com.google.backend.trading.mapstruct.margin.TradePositionMapStruct;
import com.google.backend.trading.mapstruct.margin.TransactionMapStruct;
import com.google.backend.trading.mapstruct.web.WebMapStruct;
import com.google.backend.trading.migrate.AppMigrateHandle;
import com.google.backend.trading.model.booking.api.BookingPlaceReq;
import com.google.backend.trading.model.common.PageResult;
import com.google.backend.trading.model.common.model.riskcontrol.ReducePositionReq;
import com.google.backend.trading.model.crex.CrexTypeEnum;
import com.google.backend.trading.model.funding.dto.FundingCostStatus;
import com.google.backend.trading.model.internal.aceup.AceUpFundingCostReq;
import com.google.backend.trading.model.internal.aceup.AceUpFundingCostRes;
import com.google.backend.trading.model.internal.aceup.AceUpMarginPositionReq;
import com.google.backend.trading.model.internal.aceup.AceUpMarginPositionRes;
import com.google.backend.trading.model.internal.aceup.AceUpMarginReq;
import com.google.backend.trading.model.internal.aceup.AceUpMarginRes;
import com.google.backend.trading.model.internal.aceup.AceUpMarginTransReq;
import com.google.backend.trading.model.internal.aceup.AceUpMarginTransRes;
import com.google.backend.trading.model.internal.amp.AmpMarginReq;
import com.google.backend.trading.model.internal.amp.AmpMarginRes;
import com.google.backend.trading.model.internal.amp.AmpPositionReq;
import com.google.backend.trading.model.internal.amp.AmpPositionRes;
import com.google.backend.trading.model.internal.amp.AmpTransDetailRes;
import com.google.backend.trading.model.internal.amp.AmpTransReq;
import com.google.backend.trading.model.internal.amp.PositionFlowDetailReq;
import com.google.backend.trading.model.internal.amp.PositionFlowDetailRes;
import com.google.backend.trading.model.internal.amp.PositionInfoReq;
import com.google.backend.trading.model.internal.amp.PositionInfoRes;
import com.google.backend.trading.model.margin.PositionStatus;
import com.google.backend.trading.model.margin.api.ActiveOrderReq;
import com.google.backend.trading.model.margin.api.ActiveOrderRes;
import com.google.backend.trading.model.margin.api.ActivePositionInfoVo;
import com.google.backend.trading.model.margin.api.HistoryPositionDetailRes;
import com.google.backend.trading.model.margin.api.HistoryPositionInfoVo;
import com.google.backend.trading.model.margin.api.HistoryPositionRes;
import com.google.backend.trading.model.margin.api.MarginAssetInfoRes;
import com.google.backend.trading.model.margin.api.MarginDetailRes;
import com.google.backend.trading.model.margin.api.MarginOrderDetailReq;
import com.google.backend.trading.model.margin.api.MarginOrderDetailRes;
import com.google.backend.trading.model.margin.api.MarginOrderHistoryReq;
import com.google.backend.trading.model.margin.api.MarginOrderInfoRes;
import com.google.backend.trading.model.margin.api.MarginOrderModificationVo;
import com.google.backend.trading.model.margin.api.MarginOrderModifyReq;
import com.google.backend.trading.model.margin.api.Position6HFundingCostVo;
import com.google.backend.trading.model.margin.api.PositionCloseHistoryReq;
import com.google.backend.trading.model.margin.api.PositionCloseHistoryRes;
import com.google.backend.trading.model.margin.api.PositionCloseReq;
import com.google.backend.trading.model.margin.api.PositionDetailReq;
import com.google.backend.trading.model.margin.api.PositionFundingCostReq;
import com.google.backend.trading.model.margin.api.PositionFundingCostVo;
import com.google.backend.trading.model.margin.api.PositionHistoryDetailReq;
import com.google.backend.trading.model.margin.api.PositionHistorySearchReq;
import com.google.backend.trading.model.margin.api.PositionRecordVo;
import com.google.backend.trading.model.margin.api.PositionRecordeReq;
import com.google.backend.trading.model.margin.api.PositionSettingReq;
import com.google.backend.trading.model.margin.api.PositionSettleHistoryReq;
import com.google.backend.trading.model.margin.api.PositionSettleHistoryRes;
import com.google.backend.trading.model.margin.api.PositionSettleInfoRes;
import com.google.backend.trading.model.margin.dto.ExecutableInfo;
import com.google.backend.trading.model.margin.dto.MarginInfo;
import com.google.backend.trading.model.margin.dto.MarginOrderCancel;
import com.google.backend.trading.model.margin.dto.MarginOrderPlace;
import com.google.backend.trading.model.margin.dto.PositionSettle;
import com.google.backend.trading.model.margin.dto.SettleAsset;
import com.google.backend.trading.model.pdt.CreateTradeReq;
import com.google.backend.trading.model.pdt.CreateTradeRes;
import com.google.backend.trading.model.spot.api.SpotOrderPlaceRes;
import com.google.backend.trading.model.trade.AmpOrderStatus;
import com.google.backend.trading.model.trade.AssetStatus;
import com.google.backend.trading.model.trade.Direction;
import com.google.backend.trading.model.trade.MarginPositionLimitEnum;
import com.google.backend.trading.model.trade.OrderError;
import com.google.backend.trading.model.trade.OrderStatus;
import com.google.backend.trading.model.trade.OrderType;
import com.google.backend.trading.model.trade.PdtStatus;
import com.google.backend.trading.model.trade.SourceType;
import com.google.backend.trading.model.trade.TradeStrategy;
import com.google.backend.trading.model.trade.TradeTerminator;
import com.google.backend.trading.model.trade.TradeType;
import com.google.backend.trading.model.trade.TransactionType;
import com.google.backend.trading.model.trade.TriggerType;
import com.google.backend.trading.model.trade.fee.UserFeeConfigRate;
import com.google.backend.trading.push.FundingBehaviorEventMessage;
import com.google.backend.trading.push.PushComponent;
import com.google.backend.trading.push.PushEventEnum;
import com.google.backend.trading.push.WsPushMessage;
import com.google.backend.trading.service.AssetRequest;
import com.google.backend.trading.service.CrexApiRequest;
import com.google.backend.trading.service.MarginService;
import com.google.backend.trading.service.MarketService;
import com.google.backend.trading.service.OrderRequest;
import com.google.backend.trading.service.PushMsgService;
import com.google.backend.trading.service.RiskControlService;
import com.google.backend.trading.service.RiskInfoService;
import com.google.backend.trading.service.SensorsTraceService;
import com.google.backend.trading.service.SpotService;
import com.google.backend.trading.service.TradeAssetService;
import com.google.backend.trading.service.TradeFeeConfigService;
import com.google.backend.trading.service.TradeTransactionService;
import com.google.backend.trading.service.UserTradeSettingService;
import com.google.backend.trading.task.CacheRefreshTask;
import com.google.backend.trading.transaction.MarginTransaction;
import com.google.backend.trading.util.AlarmLogUtil;
import com.google.backend.trading.util.CommonUtils;
import com.google.backend.trading.util.ListUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.map.DefaultedMap;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.util.Pair;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * @author adam.wang
 * @date 2021/9/29 19:59
 */
@Slf4j
@Service
public class MarginServiceImpl implements MarginService {

    @Resource
    RiskControlService riskControlService;
    @Resource
    private PushComponent pushComponent;
    @Resource
    private PushMsgService pushService;
    @Resource
    private OrderModificationMapStruct orderModificationMapStruct;
    @Resource
    private DefaultTradeMarginOrderMapper defaultTradeMarginOrderMapper;
    @Resource
    private OrderMapStruct orderMapStruct;
    @Resource
    private DefaultTradeMarginOrderModificationMapper defaultMarginModificationMapper;
    @Resource
    private DefaultTradePositionMapper defaultTradePositionMapper;
    @Resource
    private TradePositionMapStruct tradePositionMapStruct;
    @Resource
    private DefaultTradeTransactionMapper defaultTradeTransactionMapper;
    @Resource
    private TradeTransactionService tradeTransactionService;
    @Resource
    private TransactionMapStruct transactionMapStruct;
    @Resource
    private DefaultTradePositionFundingCostMapper defaultTradePositionFundingCost;
    @Resource
    private PositionFundingCostMapStruct positionFundingCostMapStruct;
    @Resource
    private DefaultTradeNegativeBalanceFundingCostMapper defaultTradeNegativeBalanceFundingCostMapper;
    @Resource
    private TradePositionMapper tradePositionMapper;
    @Autowired
    private TradeFeeConfigService tradeFeeConfigService;

    @Resource
    private AssetRiskInfoClient assetRiskInfoClient;
    @Lazy
    @Resource
    private OrderRequest orderRequest;
    @Resource
    private MarginTransaction marginTransaction;
    @Resource
    private AssetRequest assetRequest;
    @Resource
    private CrexApiRequest crexApiRequest;
    @Resource
    private TradeAssetService tradeAssetService;
    @Resource
    private UserTradeSettingService userTradeSettingService;
    @Resource
    private SpotService spotService;
    @Resource
    private MarginAssetMapStruct marginAssetMapStruct;
    @Autowired
    private MarketService marketService;
    @Resource
    private WebMapStruct webMapStruct;
    @Resource
    private RedissonClient redissonClient;
    @Resource
    private SensorsTraceService sensorsTraceService;
    @Autowired
    private RiskInfoService riskInfoService;
    @Autowired
    private TradeTransactionMapper transactionMapper;
    @Resource
    private DefaultTradePositionFundingCostMapper defaultTradePositionFundingCostMapper;

    @Autowired
    private LockComponent lockComponent;

    @Value("${place.max-num}")
    private Integer maxPlaceNum;
    @Autowired
    private AlarmComponent alarmComponent;

    @Override
    public MarginOrderInfoRes placeOrder(MarginOrderPlace req) {
        String uid = req.getUid();
        String symbol = req.getSymbol();
        orderRequest.checkPlaceIDKOrder(uid, symbol);
        if (!CommonUtils.checkMarginSymbol(symbol)) {
            throw new BusinessException(BusinessExceptionEnum.SYMBOL_NOT_SUPPORT_MARGIN);
        }
        if (req.getSource().isFromUser()) {
            riskInfoService.validateRiskStatus(uid);
            orderRequest.validatePriceStatus(symbol);
            orderRequest.validateMaxPendingNum(uid);
            boolean isFok = (req.getStrategy() == TradeStrategy.FOK);
            //完全平仓时不校验最小数量，且查询仓位当前持仓并设置数量
            boolean ignoreMinOrderNumCheck = false;
            if (req.isAll()) {
                TradePosition position = getActivePosition(req.getPositionId(), uid);
                ignoreMinOrderNumCheck = req.isAll() && req.getType() == OrderType.MARKET;
                if (null == position) {
                    throw new BusinessException(BusinessExceptionEnum.POSITION_CHANGE_OR_NOT_FOUND);
                }
                req.setQuantity(position.getQuantity());
            }
            orderRequest.validateQuantity(req.getQuantity(), symbol, isFok, false, req.getPrice(), ignoreMinOrderNumCheck);
        }

        orderRequest.validatePricePrecision(req.getType(), symbol, req.getPrice(), req.getTriggerPrice());
        if (orderRequest.isImmediately(req.getType(), req.getStrategy())) {
            if (!orderRequest.isReachPrice(symbol, req.getDirection(), req.getPrice())) {
                throw new BusinessException(BusinessExceptionEnum.FOK_IOC_LIMIT_PRICE_NOT_REACH);
            }
        }

        TradeMarginOrder order = generateTradeMarginOrder(req);
        marginTransaction.insertOrder(order);
        this.orderNewNotice(order);
        if (SourceType.isFromUser(order.getSource())) {
            pushService.submitOrderOk(order.getUid(), false);

        }
        log.info("placeOrder: {}", order);

        if (CommonUtils.isSyncOrder(req.getType(), req.getStrategy(), req.getSource())) {
            TradeUserTradeSetting userTradeSetting = userTradeSettingService.queryTradeSettingByUid(uid);
            this.checkAndExecuteOrder(uid, symbol, Collections.singletonList(order), userTradeSetting);
            order = defaultTradeMarginOrderMapper.selectByPrimaryKey(order.getId());
        }
        return orderMapStruct.tradeMarginOrder2marginOrderInfoRes(order);
    }

    @Override
    public MarginOrderInfoRes placeBookingOrder(BookingPlaceReq req, String userId) {
        TradeMarginOrder order = generateBookingOrder(req, userId);
        marginTransaction.insertOrder(order);
        this.orderNewNotice(order);
        TradeUserTradeSetting setting = userTradeSettingService.queryTradeSettingByUid(userId);
        String transId = CommonUtils.generateUUID();
        TradeTransaction tradeTransaction = generateTradeTransaction(order, transId);
        tradeTransactionService.insert(tradeTransaction);
        safeUpdateFilledResult(order, req.getFilledQuantity(), req.getFilledPrice(), setting, tradeTransaction);

        TradeMarginOrder result = marginTransaction.getMarginOrder(order);
        return orderMapStruct.tradeMarginOrder2marginOrderInfoRes(result);
    }

    @Override
    public void cancelOrder(MarginOrderCancel req) {
        // 如果订单被锁定，则每300毫秒重试，重试上限5次
        for (int i = 0; i < 5; i++) {
            try {
                TradeMarginOrder order = marginTransaction.cancelOrder(req);
                if (order != null && req.getTerminator().isFromUser()) {
                    pushService.cancelOrderOk(order.getUid(), false);
                }
                return;
            } catch (LockException ignored) {
                log.warn("cancel order failed, req={}", req);
            }
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                return;
            }
        }
        AlarmLogUtil.alarm("cancel order failed 5 times, req={}", req);
        throw new BusinessException(BusinessExceptionEnum.CANCEL_ORDER_FAIL);
    }

    @Override
    public void modifyOrder(MarginOrderModifyReq req, String uid) {
        TradeMarginOrderExample example = new TradeMarginOrderExample();
        TradeMarginOrderExample.Criteria criteria = example.createCriteria();
        criteria.andUidEqualTo(uid);
        criteria.andUuidEqualTo(req.getOrderId());
        List<TradeMarginOrder> orders = defaultTradeMarginOrderMapper.selectByExample(example);
        if (ListUtil.isEmpty(orders)) {
            throw new BusinessException(BusinessExceptionEnum.ORDER_CHANGE_OR_NOT_FOUND);
        }
        riskInfoService.validateRiskStatus(uid);
        TradeMarginOrder order = orders.get(0);
        OrderStatus status = OrderStatus.getByName(order.getStatus());
        // 非用户自己下的单不允许修改
        if (!SourceType.isFromUser(order.getSource())) {
            throw new BusinessException(BusinessExceptionEnum.ORDER_CHANGE_OR_NOT_FOUND);
        }
        // 校验quantity
        if (null != req.getQuantity()) {
            boolean isFok = (TradeStrategy.FOK.getName().equals(order.getStrategy()));
            orderRequest.validateQuantity(req.getQuantity(), order.getSymbol(), isFok, false, req.getPrice(), false);
        }
        // 市价单不允许修改
        if (!OrderType.isLimitOrder(order.getType()) && status != OrderStatus.PRE_TRIGGER) {
            throw new BusinessException(BusinessExceptionEnum.ORDER_CHANGE_OR_NOT_FOUND);
        }
        // 只有PRE_TRIGGER，EXECUTING，EXCEPTION状态订单允许修改
        if (status != OrderStatus.PRE_TRIGGER && status != OrderStatus.EXECUTING && status != OrderStatus.EXCEPTION) {
            throw new BusinessException(BusinessExceptionEnum.ORDER_CHANGE_OR_NOT_FOUND);
        }
        // 状态改变了不允许修改
        if (!Objects.equals(req.getLastStatus(), status.getCode())) {
            throw new BusinessException(BusinessExceptionEnum.ORDER_CHANGE_OR_NOT_FOUND);
        }
        // 锁定订单，锁定失败表示订单正在被触发和正在发单中，不允许修改
        // PRE_TRIGGER -> PENDING
        // EXECUTING / EXCEPTION -> LOCKED
        if ((status == OrderStatus.PRE_TRIGGER && !updateOrderStatus(order, OrderStatus.PRE_TRIGGER, OrderStatus.PENDING))
                || (status == OrderStatus.EXECUTING && !updateOrderStatus(order, OrderStatus.EXECUTING, OrderStatus.LOCKED))
                || (status == OrderStatus.EXCEPTION && !updateOrderStatus(order, OrderStatus.EXCEPTION, OrderStatus.LOCKED))) {
            throw new BusinessException(BusinessExceptionEnum.ORDER_CHANGE_OR_NOT_FOUND);
        }
        //锁定后的订单最新数据
        order = defaultTradeMarginOrderMapper.selectByPrimaryKey(order.getId());
        if (!verifyModification(req, order, status)) {
            order.setStatus(status.getName());
            defaultTradeMarginOrderMapper.updateByPrimaryKeySelective(order);
            throw new BusinessException(BusinessExceptionEnum.ORDER_CHANGE_OR_NOT_FOUND);
        }

        TradeMarginOrder modifyData = getModifyMarginOrder(req, order, status);
        marginTransaction.insertModificationAndUpdateOrder(order, modifyData);
        pushService.modifyOrderOk(order.getUid(), false);
    }


    private TradeMarginOrder getModifyMarginOrder(MarginOrderModifyReq req, TradeMarginOrder origin, OrderStatus status) {
        TradeMarginOrder order = new TradeMarginOrder();
        if (CommonUtils.isPositive(req.getQuantity())) {
            order.setQuantity(req.getQuantity());
        }
        if (CommonUtils.isValidString(req.getNotes())) {
            order.setNotes(req.getNotes());
        }

        OrderType type = OrderType.getByName(origin.getType());
        if (type.isLimitOrder()) {
            if (CommonUtils.isPositive(req.getPrice())) {
                order.setPrice(req.getPrice());
            }
        }

        if (type.isTriggerOrder() && status == OrderStatus.PRE_TRIGGER) {
            if (CommonUtils.isPositive(req.getTriggerPrice())) {
                order.setTriggerPrice(req.getTriggerPrice());
            }
            if (CommonUtils.isValidString(req.getTriggerCompare())) {
                order.setTriggerCompare(req.getTriggerCompare());
            }
        }
        order.setMtime(CommonUtils.getNowTime());
        return order;
    }


    private TradeMarginOrder generateTradeMarginOrder(MarginOrderPlace req) {
        OrderType orderType = req.getType();
        TradeMarginOrder order = new TradeMarginOrder();
        if (req.getOrderId() == null) {
            order.setUuid(CommonUtils.generateUUID());
        } else {
            order.setUuid(req.getOrderId());
        }
        order.setUid(req.getUid());
        order.setType(orderType.getName());
        order.setSymbol(req.getSymbol());
        order.setDirection(req.getDirection().getName());
        order.setQuantity(req.getQuantity());
        order.setQuantityFilled(BigDecimal.ZERO);
        order.setReduceOnly(req.getReduceOnly());
        if (orderType.isLimitOrder()) {
            order.setStrategy(req.getStrategy().getName());
        }
        if (orderType.isLimitOrder()) {
            order.setPrice(req.getPrice());
        }
        if (orderType.isTriggerOrder()) {
            order.setTriggerCompare(req.getTriggerCompare().getName());
            order.setTriggerPrice(req.getTriggerPrice());
            order.setStatus(OrderStatus.PRE_TRIGGER.getName());
        } else {
            order.setStatus(OrderStatus.EXECUTING.getName());
        }
        order.setNotes(req.getNotes());
        order.setSource(req.getSource().getName());
        order.setCtime(new Date());
        order.setMtime(new Date());
        return order;
    }

    private TradeMarginOrder generateBookingOrder(BookingPlaceReq req, String userId) {
        TradeMarginOrder order = new TradeMarginOrder();
        order.setUuid(req.getUniqueId());
        order.setUid(userId);
        order.setSymbol(req.getSymbol());
        order.setDirection(req.getDirection());
        order.setStatus(OrderStatus.COMPLETED.getName());
        order.setSource(SourceType.OTC_SHOP.getName());

        if (CommonUtils.isValidString(req.getType())) {
            order.setType(req.getType());
        } else {
            order.setType(OrderType.LIMIT.getName());
        }
        if (CommonUtils.isValidString(req.getStrategy())) {
            order.setStrategy(req.getStrategy());
        } else {
            order.setStrategy(TradeStrategy.FOK.getName());
        }
        if (CommonUtils.isValidString(req.getNotes())) {
            order.setNotes(req.getNotes());
        }
        order.setReduceOnly(false);
        order.setPrice(req.getFilledPrice());
        order.setQuantity(req.getFilledQuantity());
        order.setFilledPrice(BigDecimal.ZERO);
        order.setQuantityFilled(BigDecimal.ZERO);
        order.setFee(BigDecimal.ZERO);
        order.setCtime(new Date());
        return order;
    }

    /**
     * @param req
     * @param order  锁定后获取的最新订单数据
     * @param status 订单变更前的status
     * @return
     */
    private boolean verifyModification(MarginOrderModifyReq req, TradeMarginOrder order, OrderStatus status) {
        OrderType orderType = OrderType.getByName(order.getType());
        if (req.getLastQuantity().compareTo(order.getQuantity()) != 0) {
            return false;
        }
        if (orderType.isLimitOrder()) {
            if (null == req.getLastPrice() || req.getLastPrice().compareTo(order.getPrice()) != 0) {
                return false;
            }
        }
        if (status == OrderStatus.PRE_TRIGGER) {
            if (null == req.getLastTriggerPrice() || req.getLastTriggerPrice().compareTo(order.getTriggerPrice()) != 0) {
                return false;
            }
            if (!Objects.equals(req.getLastTriggerCompare(), order.getTriggerCompare())) {
                return false;
            }
        } else {
            if (req.getLastFilledQuantity().compareTo(order.getQuantityFilled()) != 0) {
                return false;
            }
        }
        return true;
    }

    @Override
    public TradeMarginOrder triggerOrder(TradeMarginOrder order) {
        OrderType type = OrderType.getByName(order.getType());
        OrderStatus status = OrderStatus.getByName(order.getStatus());
        if (type.isTriggerOrder() && status == OrderStatus.PRE_TRIGGER) {
            TriggerType triggerType = TriggerType.getByName(order.getTriggerCompare());
            String symbol = order.getSymbol();
            BigDecimal triggerPrice = order.getTriggerPrice();
            SymbolDomain symbolDomain = SymbolDomain.nonNullGet(symbol);
            BigDecimal midPrice = symbolDomain.midPrice();
            log.info("trigger price = {}, mid price = {}", triggerPrice, midPrice);
            if (orderRequest.isSatisfyTrigger(triggerPrice, triggerType, midPrice)) {
                order.setStatus(OrderStatus.EXECUTING.getName());
                log.info("margin pre trigger -> executing, trigger price = {}, trigger type = {}, price info = {}, order info = {}",
                        triggerPrice, order.getTriggerCompare(), symbolDomain.toPriceInfo(), order);
                marginTransaction.updateOrderTransactional(order);
                pushService.marginTriggerOk(order);
            }
        }
        return order;
    }

    // 检查并执行订单
    @Override
    public void checkAndExecuteOrder(String uid, String symbol, List<TradeMarginOrder> orders,
                                     TradeUserTradeSetting userTradeSetting) {
        HashMap<Long, ExecutableInfo> checkMarginResult = this.checkOrderExecutable(uid, symbol, orders, userTradeSetting);
        orders.stream().filter(order -> null != checkMarginResult.get(order.getId()))
                .forEach(order -> executeOrder(order, checkMarginResult.get(order.getId()), userTradeSetting));
    }

    /**
     * 增加预计的保证金并获取更新后的预计保证金
     * <p>
     * 这里会舍去 BigDecimal 的小数部分，对于USD保证金可忽略影响
     *
     * @param uid
     * @param expectUsedMargin
     * @return
     */
    public BigDecimal addAndGetExpectUsedMargin(String uid, BigDecimal expectUsedMargin) {
        RAtomicLong totalExpectUsedMargin = redissonClient.getAtomicLong(uid + ":expect-used-margin");
        return new BigDecimal(totalExpectUsedMargin.addAndGet(expectUsedMargin.longValue()));
    }

    public BigDecimal getExpectUsedMargin(String uid) {
        RAtomicLong totalExpectUsedMargin = redissonClient.getAtomicLong(uid + ":expect-used-margin");
        return new BigDecimal(totalExpectUsedMargin.get());
    }

    /**
     * 释放预计的保证金并获取更新后的预计保证金
     *
     * @param uid
     * @param releaseMargin
     * @return
     */
    public BigDecimal releaseExpectUsedMargin(String uid, BigDecimal releaseMargin) {
        RAtomicLong totalExpectUsedMargin = redissonClient.getAtomicLong(uid + ":expect-used-margin");
        long remain = totalExpectUsedMargin.addAndGet(releaseMargin.negate().longValue());
        if (remain < 0) {
            AlarmLogUtil.alarm("release expect used margin, remain < 0, reset to 0, remain = {}", remain);
            totalExpectUsedMargin.compareAndSet(remain, 0);
        }
        return new BigDecimal(remain);
    }

    @Override
    public HashMap<Long, ExecutableInfo> checkExceptionOrderExecutable(String uid, String symbol, List<TradeMarginOrder> orders,
                                                                       TradeUserTradeSetting userTradeSetting) {
        HashMap<Long, ExecutableInfo> map = checkOrderExecutable(uid, symbol, orders, userTradeSetting);
        map.values().forEach(executableInfo -> releaseExpectUsedMargin(uid, executableInfo.getExpectUsedMargin()));
        return map;
    }

    @Override
    public HashMap<Long, ExecutableInfo> checkOrderExecutable(String uid, String symbol, List<TradeMarginOrder> orders,
                                                              TradeUserTradeSetting userTradeSetting) {
        MarginInfo marginInfo = this.marginInfo(uid);
        BigDecimal totalMargin = marginInfo.getTotalOpenMargin();
        BigDecimal availableMargin = marginInfo.getAvailableMargin();
        //带符号的仓位quantity
        Map<String, BigDecimal> positionMap = new HashMap<>();
        BigDecimal currentTotalPositionValue = BigDecimal.ZERO;

        // get current position
        for (TradePosition position : marginInfo.getCurrentPositions()) {
            positionMap.put(position.getSymbol(), Objects.equals(position.getDirection(),
                    Direction.SELL.getName()) ? position.getQuantity().negate() : position.getQuantity());
        }

        for (Map.Entry<String, BigDecimal> entry : positionMap.entrySet()) {
            currentTotalPositionValue = currentTotalPositionValue.add(entry.getValue().abs().multiply(
                    CommonUtils.getMiddlePrice(CommonUtils.getBaseCoin(entry.getKey()) + "_USD")));
        }

        BigDecimal specialTotalPositionLimit = CacheRefreshTask.POSITION_LIMIT_USER_MAP.get(uid);
        boolean isSpecialUser = null != specialTotalPositionLimit;
        BigDecimal totalPositionLimit = isSpecialUser ? specialTotalPositionLimit :
                MarginPositionLimitEnum.totalPositionLimit(userTradeSetting.getLeverage());
        HashMap<Long, ExecutableInfo> resultMap = new HashMap<>();

        for (TradeMarginOrder order : orders) {
            String uuid = order.getUuid();
            Direction direction = Direction.getByName(order.getDirection());
            BigDecimal executeQuantity = order.getQuantity().subtract(order.getQuantityFilled() != null
                    ? order.getQuantityFilled() : BigDecimal.ZERO);
            if (!CommonUtils.isPositive(executeQuantity)) {
                log.error("executeOrder={} quantity={} filled={}", uuid, order.getQuantity(),
                        order.getQuantityFilled());
                continue;
            }
            BigDecimal currentPosition = positionMap.getOrDefault(order.getSymbol(), BigDecimal.ZERO);
            // 只减仓订单，计算可执行金额
            if (order.getReduceOnly() != null && order.getReduceOnly()) {
                if (currentPosition.compareTo(BigDecimal.ZERO) != 0
                        && (direction == Direction.BUY ^ CommonUtils.isPositive(currentPosition))) {
                    // 订单方向与仓位方向相反
                    executeQuantity = executeQuantity.min(currentPosition.abs());
                    resultMap.put(order.getId(), new ExecutableInfo(null, executeQuantity, BigDecimal.ZERO));
                    BigDecimal expectPosition = currentPosition.add(direction == Direction.SELL ? executeQuantity.negate() : executeQuantity);
                    positionMap.put(order.getSymbol(), expectPosition);
                } else {
                    // 仓位为0或订单方向与仓位方向相同
                    resultMap.put(order.getId(), new ExecutableInfo(ExecutableInfo.ExecutableError.REDUCE_ONLY, BigDecimal.ZERO, BigDecimal.ZERO));
                }
            }
            // 非只减仓订单，检查保证金和持仓限额
            else {
                BigDecimal expectPosition = currentPosition.add(direction == Direction.SELL ? executeQuantity.negate() : executeQuantity);
                BigDecimal needPositionValue = expectPosition.abs().subtract(currentPosition.abs())
                        .multiply(CommonUtils.getMiddlePrice(CommonUtils.getBaseCoin(order.getSymbol()) + "_USD"));
                //反向单，暂时记为 0
                BigDecimal expectNeedMargin = BigDecimal.ZERO;
                if (CommonUtils.isPositive(needPositionValue)) {
                    //预计该订单需要的保证金
                    expectNeedMargin = needPositionValue.divide(userTradeSetting.getLeverage(), RoundingMode.CEILING);
                    //预计参与计算的订单需要的保证金
                    BigDecimal totalExpectMargin = addAndGetExpectUsedMargin(uid, expectNeedMargin);
                    //预计头寸
                    BigDecimal expectPositionValue = currentTotalPositionValue.add(totalExpectMargin.multiply(userTradeSetting.getLeverage()));
                    BigDecimal currentMargin = currentTotalPositionValue.divide(userTradeSetting.getLeverage(), RoundingMode.CEILING);
                    //预计总保证金 + 当前占用的保证金
                    BigDecimal expectMargin = currentMargin.add(totalExpectMargin);
                    log.info("uid = {}, order id = {}, expect total used margin = {}, current order margin = {}, availableMargin = {}",
                            uid, uuid, totalExpectMargin, expectNeedMargin, availableMargin);
                    if (totalExpectMargin.compareTo(availableMargin) > 0) {
                        log.info("uid = {}, order id = {}, insufficient margin, expect margin = {}, current total margin = {}",
                                uid, uuid, expectMargin, totalMargin);
                        // 推送保证金不足的消息
                        if (SourceType.isFromUser(order.getSource())) {
                            pushService.marginNotEnough(order);
                        }
                        resultMap.put(order.getId(), new ExecutableInfo(
                                ExecutableInfo.ExecutableError.INSUFFICIENT_MARGIN, CommonUtils.ZERO_NUM, BigDecimal.ZERO));
                        releaseExpectUsedMargin(uid, expectNeedMargin);
                        continue;
                    }
                    if (expectPositionValue.compareTo(totalPositionLimit) > 0) {
                        log.info("uid = {}, order id = {}, insufficient margin, expect margin = {}, total margin = {}, expectPositionValue = {}, " +
                                        "totalPositionLimit = {}",
                                uid, uuid, expectMargin, totalMargin, expectPositionValue, totalPositionLimit);
                        resultMap.put(order.getId(), new ExecutableInfo(
                                ExecutableInfo.ExecutableError.EXCEED_TOTAL_POSITION_LIMIT, CommonUtils.ZERO_NUM, BigDecimal.ZERO));
                        releaseExpectUsedMargin(uid, expectNeedMargin);
                        continue;
                    }
                    //特殊用户不检查
                    if (!isSpecialUser) {
                        BigDecimal symbolPositionLimit = MarginPositionLimitEnum.positionLimitBySymbol(userTradeSetting.getLeverage(), order.getSymbol());
                        BigDecimal expectSymbolPositionValue =
                                expectPosition.multiply(CommonUtils.getMiddlePrice(CommonUtils.getBaseCoin(order.getSymbol()) + "_USD"));
                        if (expectSymbolPositionValue.abs().compareTo(symbolPositionLimit) > 0) {
                            log.info("uid = {}, order id = {}, exceed symbol position limit, expectSymbolPositionValue = {}, symbolPositionLimit = " +
                                            "{}",
                                    uid, uuid, expectSymbolPositionValue, symbolPositionLimit);
                            resultMap.put(order.getId(), new ExecutableInfo(ExecutableInfo.ExecutableError.EXCEED_SYMBOL_POSITION_LIMIT,
                                    CommonUtils.ZERO_NUM, BigDecimal.ZERO));
                            releaseExpectUsedMargin(uid, expectNeedMargin);
                            continue;
                        }
                    }
                }
                currentTotalPositionValue = currentTotalPositionValue.add(needPositionValue);
                resultMap.put(order.getId(), new ExecutableInfo(null, executeQuantity, expectNeedMargin));
            }
        }
        return resultMap;
    }

    private TradeMarginOrder executeOrder(TradeMarginOrder order, ExecutableInfo executableInfo, TradeUserTradeSetting userTradeSetting) {
        String uid = order.getUid();
        log.info("executeOrder: order={}, executableInfo={}", order, executableInfo);
        OrderStatus status = OrderStatus.getByName(order.getStatus());
        if (status == OrderStatus.EXECUTING && updateOrderStatus(order, OrderStatus.EXECUTING, OrderStatus.LOCKED)) {
            OrderType orderType = OrderType.getByName(order.getType());
            TradeStrategy strategy = TradeStrategy.getByName(order.getStrategy());
            boolean isSync = CommonUtils.isSyncOrder(orderType, strategy, SourceType.getByName(order.getSource()));
            if (!CommonUtils.isPositive(executableInfo.getQuantity())) {
                if (executableInfo.getError() != null) {
                    order.setError(executableInfo.getError().convert().getName());
                }
                if (isSync || executableInfo.getError() == ExecutableInfo.ExecutableError.REDUCE_ONLY) {
                    orderRequest.setMarginFinishStatus(order);
                    order.setTerminator(TradeTerminator.SYSTEM.name());
                } else {
                    order.setStatus(OrderStatus.EXCEPTION.getName());
                }
                marginTransaction.updateOrderTransactional(order);
            } else {
                order = defaultTradeMarginOrderMapper.selectByPrimaryKey(order.getId());
                //1. crex下单前
                String transId = order.getTransId();
                TradeTransaction tradeTransaction = null;
                if (StringUtils.isEmpty(transId)) {
                    //创建trade_transaction订单
                    transId = CommonUtils.generateUUID();
                    tradeTransaction = generateTradeTransaction(order, transId);
                    tradeTransactionService.insert(tradeTransaction);
                    marginTransaction.transactionNewNotice(tradeTransaction);
                } else {
                    //有transId说明上次HTTP执行失败
                    tradeTransaction = tradeTransactionService.queryAllByTransId(transId);
                }
                CreateTradeReq placeReq = generateCreateTradeReq(transId, order, executableInfo);
                //2. crex下单
                CreateTradeRes resp = crexApiRequest.executeOrder(placeReq, TradeType.MARGIN);
                //3. 判断下单状态 更新trade_transaction状态
                if (resp.isSuccess()) {
                    //set 交易量 2DB
                    String quoteCoin = CommonUtils.getQuoteCoin(order.getSymbol());
                    tradeAssetService.setUserAmountUsd2DB(uid, quoteCoin, resp.getQuoteFilled(), transId);

                    //业务成功
                    order.setTransId("");
                    // 内部 done PdtStatus -> COMPLETED
                    safeUpdateFilledResult(order, resp.getFilled(), resp.getFilledPrice(), userTradeSetting, tradeTransaction);
                }
                TradeTransaction updateTrans = new TradeTransaction();
                updateTrans.setUuid(transId);
                if (!resp.httpSuccess()) {
                    //http请求失败
                    // PdtStatus -> PENDING
                    if (isSync) {
                        alarmComponent.asyncAlarm(AlarmEnum.MARGIN_PLACE_ORDER_ERROR, "同步单PDT请求失败,transId为：" + transId);
                        //同步订单给出最终状态
                        order.setTransId(transId);
                        order.setStatus(OrderStatus.CANCELED.getName());
                        order.setTerminator(TradeTerminator.SYSTEM.name());
                        order.setError(OrderError.TRADING_SPOT_ORDER_ERROR_CREX.getName());
                        marginTransaction.updateOrderTransactional(order);
                        updateTrans.setPdtStatus(PdtStatus.PENDING.name());
                    } else {
                        order.setTransId(transId);
                        order.setStatus(OrderStatus.EXECUTING.getName());
                        updateTrans.setPdtStatus(PdtStatus.PENDING.name());
                        defaultTradeMarginOrderMapper.updateByPrimaryKeySelective(order);
                    }
                    tradeTransactionService.updateTransactionById(updateTrans);
                }

                if (resp.httpSuccess() && !resp.isSuccess() && !resp.isPending()) {
                    // 业务失败
                    // PdtStatus -> EXCEPTION
                    if (isSync) {
                        //同步订单给出最终状态
                        order.setTransId("");
                        order.setStatus(OrderStatus.CANCELED.getName());
                        order.setTerminator(TradeTerminator.SYSTEM.name());
                        order.setError(OrderError.TRADING_SPOT_ORDER_ERROR_CREX.getName());
                        marginTransaction.updateOrderTransactional(order);
                        updateTrans.setPdtStatus(PdtStatus.EXCEPTION.name());
                    } else {
                        order.setTransId("");
                        order.setStatus(OrderStatus.EXECUTING.getName());
                        updateTrans.setPdtStatus(PdtStatus.EXCEPTION.name());
                        defaultTradeMarginOrderMapper.updateByPrimaryKeySelective(order);
                    }
                    tradeTransactionService.updateTransactionById(updateTrans);
                }

                if (resp.isPending()) {
                    //发单PENDING状态 下次继续
                    if (isSync) {
                        alarmComponent.asyncAlarm(AlarmEnum.MARGIN_PLACE_ORDER_ERROR, "同步单PDT返回PENDING,transId为：" + transId);
                        //同步订单给出最终状态
                        order.setTransId(transId);
                        order.setStatus(OrderStatus.CANCELED.getName());
                        order.setTerminator(TradeTerminator.SYSTEM.name());
                        order.setError(OrderError.TRADING_SPOT_ORDER_ERROR_CREX.getName());
                        marginTransaction.updateOrderTransactional(order);
                        updateTrans.setPdtStatus(PdtStatus.PENDING.name());
                    } else {
                        order.setTransId(transId);
                        order.setStatus(OrderStatus.EXECUTING.getName());
                        updateTrans.setPdtStatus(PdtStatus.PENDING.name());
                        defaultTradeMarginOrderMapper.updateByPrimaryKeySelective(order);
                    }
                    tradeTransactionService.updateTransactionById(updateTrans);
                }
            }
        }
        BigDecimal currentExpectUsedMargin = releaseExpectUsedMargin(uid, executableInfo.getExpectUsedMargin());
        log.info("uid = {}, currentExpectUsedMargin = {}", uid, currentExpectUsedMargin);
        return order;
    }

    private TradeTransaction generateTradeTransaction(TradeMarginOrder order, String tradeId) {
        TradeTransaction transaction = new TradeTransaction();
        transaction.setUuid(tradeId);
        transaction.setUid(order.getUid());
        transaction.setOrderId(order.getUuid());
        transaction.setOrderType(order.getType());
        transaction.setType(TransactionType.OPEN_POSITION.getName());
        transaction.setSymbol(order.getSymbol());
        transaction.setDirection(order.getDirection());
        transaction.setBaseQuantity(BigDecimal.ZERO);
        transaction.setQuoteQuantity(BigDecimal.ZERO);
        transaction.setPrice(BigDecimal.ZERO);
        transaction.setFee(BigDecimal.ZERO);
        transaction.setFeeCoin(Constants.BASE_COIN);
        transaction.setPnl(BigDecimal.ZERO);
        transaction.setSource(order.getSource());
        transaction.setAssetStatus(AssetStatus.PENDING.name());
        transaction.setPdtStatus(PdtStatus.PENDING.name());
        Date date = new Date();
        transaction.setMtime(date);
        transaction.setCtime(date);
        return transaction;
    }

    private void safeUpdateFilledResult(TradeMarginOrder order, BigDecimal filledQty, BigDecimal filledPrice, TradeUserTradeSetting userTradeSetting, TradeTransaction tradeTransaction) {
        String tradeId = tradeTransaction.getUuid();
        String uid = order.getUid();
        String symbol = order.getSymbol();
        String uuid = order.getUuid();
        log.info("fillOrder order={}, filledQty={}, filledPrice={}, tradeId={}", order, filledQty, filledPrice, tradeId);
        //加锁仓位锁
        log.info("uid = {}, symbol = {}, order id = {}, start get position lock", uid, uuid, symbol);
        if (!lockComponent.lockPosition(uid, symbol)) {
            return;
        }
        log.info("uid = {}, symbol = {}, order id = {}, start get position lock success", uid, uuid, symbol);
        List<TradeTransaction> transactions;
        try {
            transactions = marginTransaction.updateTransaction(order,
                    filledQty, filledPrice, tradeTransaction);
        } finally {
            //释放仓位锁
            log.info("uid = {}, symbol = {}, order id = {}, start get position unlock", uid, uuid, symbol);
            lockComponent.unlockPosition(uid, symbol);
        }
        if (CollectionUtils.isEmpty(transactions)) {
            return;
        }
        for (TradeTransaction transaction : transactions) {
            MsgHeadDto dto = getMsgHeadDto(transaction);
            //交易自身处理功能马上通知风控，此类动作不存在回滚，自身处理成功视为完成
            riskControlService.positionNoticeWithVersion(transaction, dto);
            TransactionType type = TransactionType.valueOf(transaction.getType());
            if (TransactionType.isOpenPos(type)) {
                AssetStatus assetStatus = assetRequest.doOpenPosition(transaction);
                transaction.setAssetStatus(assetStatus.name());
                defaultTradeTransactionMapper.updateByPrimaryKeySelective(transaction);
                sensorsTraceService.marginOpen(transaction, order, userTradeSetting);
            } else {
                AssetStatus assetStatus = assetRequest.doClosePosition(transaction);
                transaction.setAssetStatus(assetStatus.name());
                defaultTradeTransactionMapper.updateByPrimaryKeySelective(transaction);
                if (userTradeSetting.getAutoConvert()) {
                    convertPnl(transaction);
                }
                sensorsTraceService.marginClose(transaction, order, userTradeSetting);
            }
            FundingBehaviorEventMessage msg = FundingBehaviorEventMessage.buildMarginValue(transaction);
            if (SourceType.isFromUser(order.getSource()) || SourceType.TAKE_PROFIT_STOP_LOSS.getName().equals(order.getSource())) {
                pushService.marginOrderTraded(transaction);
                pushComponent.pushFundingEventMessage(msg);
            }
        }
    }

    private MsgHeadDto getMsgHeadDto(TradeTransaction transaction) {
        MsgHeadDto dto = new MsgHeadDto();
        dto.setUserId(transaction.getUid());
        dto.setGlobalId(transaction.getUuid());
        dto.setCurTopic(Constants.SERVICE_NAME);
        String txnId = HeaderUtils.toTxnId(Constants.SERVICE_NAME, transaction.getId());
        dto.setCurTxnId(txnId);
        dto.setNextTopic(Collections.singletonList("asset"));
        return dto;
    }


    private void convertPnl(TradeTransaction transaction) {
        log.info("handle convert pnl, original transaction = {}", transaction);
        String quoteCoin = CommonUtils.getQuoteCoin(transaction.getSymbol());
        if (Objects.equals(quoteCoin, Constants.BASE_COIN)) {
            return;
        }
        if (transaction.getPnl().compareTo(CommonUtils.ZERO_NUM) == 0) {
            return;
        }
        String convertSymbol = quoteCoin + Constants.BASE_QUOTE;
        Direction direction = transaction.getPnl().compareTo(CommonUtils.ZERO_NUM) > 0 ? Direction.SELL : Direction.BUY;
        log.info("start place convertPnl spot order ");
        SpotOrderPlaceRes res = spotService.placeOrderUntilAllComplete(transaction.getUid(), convertSymbol, transaction.getPnl().abs(), direction, false, SourceType.AUTO_CONVERSION);
        log.info("finish place convertPnl spot order ");
        transaction.setPnlConversion(direction == Direction.BUY ? res.getAmountFilled().negate() : res.getAmountFilled());
        defaultTradeTransactionMapper.updateByPrimaryKeySelective(transaction);
    }

    private CreateTradeReq generateCreateTradeReq(String tradeId, TradeMarginOrder order, ExecutableInfo info) {
        CreateTradeReq placeReq = new CreateTradeReq();
        placeReq.setUserId(order.getUid());
        placeReq.setTradeId(tradeId);
        placeReq.setOrderId(order.getUuid());
        placeReq.setSymbol(order.getSymbol());
        placeReq.setDirection(order.getDirection());
        placeReq.setQuantity(info.getQuantity());

        OrderType orderType = OrderType.getByName(order.getType());
        TradeStrategy strategy = TradeStrategy.getByName(order.getStrategy());

        if (orderType.isLimitOrder()) {
            placeReq.setPrice(order.getPrice());
            if (orderRequest.isSmallQuantity(info.getQuantity(), order.getSymbol())) {
                placeReq.setType(CrexTypeEnum.LIMIT_FILL_REST);
            } else {
                if (strategy == TradeStrategy.FOK) {
                    placeReq.setType(CrexTypeEnum.LIMIT_FOK);
                } else {
                    placeReq.setType(CrexTypeEnum.LIMIT_FAK);
                }
            }
        } else {
            placeReq.setType(CrexTypeEnum.MARKET);
        }
        return placeReq;
    }

    private boolean updateOrderStatus(TradeMarginOrder order, OrderStatus prev, OrderStatus next) {
        order.setMtime(CommonUtils.getNowTime());
        order.setStatus(next.getName());
        TradeMarginOrderExample example = new TradeMarginOrderExample();
        TradeMarginOrderExample.Criteria criteria = example.createCriteria();
        criteria.andUuidEqualTo(order.getUuid());
        criteria.andStatusEqualTo(prev.getName());
        if (defaultTradeMarginOrderMapper.updateByExampleSelective(order, example) == 0) {
            log.error("convert status fail. order={}", order);
            return false;
        }
        return true;
    }

    @Override
    public MarginOrderInfoRes closePosition(PositionCloseReq req, String uid) {
        //风控检查
        riskInfoService.validateRiskStatus(uid);
        TradePosition position = getActivePosition(req.getPositionId(), uid);
        if (position == null) {
            throw new BusinessException(BusinessExceptionEnum.POSITION_CHANGE_OR_NOT_FOUND);
        }
        if (!req.isAll() && position.getQuantity().compareTo(req.getQuantity()) < 0) {
            throw new BusinessException(BusinessExceptionEnum.POSITION_CHANGE_OR_NOT_FOUND);
        }
        MarginOrderPlace place = MarginOrderPlace.builder()
                .uid(uid)
                .strategy(TradeStrategy.getByCode(req.getStrategy()))
                .symbol(position.getSymbol())
                .direction(Direction.rivalDirection(position.getDirection()))
                .type(OrderType.getByName(req.getType()))
                .quantity(req.getQuantity())
                .price(req.getPrice())
                .triggerPrice(req.getTriggerPrice())
                .triggerCompare(TriggerType.getByName(req.getTriggerCompare()))
                .reduceOnly(true)
                .source(SourceType.PLACED_BY_CLIENT)
                .notes(req.getNotes())
                .all(req.isAll())
                .positionId(req.getPositionId())
                .build();
        return placeOrder(place);
    }

    @Override
    public PositionSettleInfoRes getSettlePositionInfo(String positionId, String uid) {
        TradePosition position = getActivePosition(positionId, uid);
        if (position == null) {
            throw new BusinessException(BusinessExceptionEnum.ORDER_CHANGE_OR_NOT_FOUND);
        }
        SettleAsset settleAsset = tradeAssetService.settleAvailable(position);
        PositionSettleInfoRes info = new PositionSettleInfoRes();
        info.setPositionId(positionId);
        info.setSymbol(position.getSymbol());
        info.setDirection(position.getDirection());
        info.setPositionQuantity(position.getQuantity());
        info.setOpenPrice(position.getPrice());
        info.setMaxSettleQuantity(settleAsset.getSettleAvailable());
        info.setAvailableBalance(settleAsset.getAvailable());
        UserFeeConfigRate userFeeConfigRate = tradeFeeConfigService.selectUserFeeConfig(uid);
        info.setMarginSettleFeeRate(userFeeConfigRate.getMarginSettleFeeRate());
        return info;
    }

    @Override
    public boolean settlePosition(PositionSettle req) {
        String uid = req.getUid();
        PositionSettleInfoRes info = getSettlePositionInfo(req.getPositionId(), uid);
        String symbol = info.getSymbol();
        //全部交割时获取到仓位的持仓重置 quantity
        if (req.isAll()) {
            req.setQuantity(info.getPositionQuantity());
        }
        log.info("settlePosition req={} ,positionSettleInfo res= {}", req, info);
        if (req.getQuantity().compareTo(info.getPositionQuantity()) > 0) {
            throw new BusinessException(BusinessExceptionEnum.POSITION_CHANGE_OR_NOT_FOUND);
        }
        if (req.getQuantity().compareTo(info.getMaxSettleQuantity()) > 0) {
            throw new BusinessException(BusinessExceptionEnum.INSUFFICIENT_FUNDS);
        }
        if (!lockComponent.lockPosition(uid, symbol)) {
            throw new BusinessException(BusinessExceptionEnum.POSITION_CHANGE_OR_NOT_FOUND);
        }
        try {
            TradePosition activePosition = getActivePosition(req.getPositionId(), uid);
            if (null == activePosition) {
                throw new BusinessException(BusinessExceptionEnum.POSITION_CHANGE_OR_NOT_FOUND);
            }
            TradeTransaction transaction = generateSettleTransaction(req, uid, info, symbol);
            defaultTradeTransactionMapper.insertSelective(transaction);
            checkSettle(activePosition, transaction);
            assetRequest.doSettlePosition(transaction);
            try {
                marginTransaction.settlePosition(info.getPositionId(), transaction);
                this.transactionNewNotice(transaction);
                pushService.marginDelivery(uid, true);
                MsgHeadDto dto = getMsgHeadDto(transaction);
                riskControlService.positionNoticeWithVersion(transaction, dto);
                sensorsTraceService.settleSuccess(transaction, info);
                //set 交易量 2DB
                tradeAssetService.setUserAmountUsd2DB(uid, CommonUtils.getQuoteCoin(transaction.getSymbol()),
                        transaction.getQuoteQuantity(), transaction.getUuid());
                //完全交割
                return info.getPositionQuantity().compareTo(req.getQuantity()) == 0L;
            } catch (Exception e) {
                assetRequest.doRollback(transaction.getUuid());
                pushService.marginDelivery(uid, false);
                throw new BusinessException(BusinessExceptionEnum.POSITION_CHANGE_OR_NOT_FOUND);
            }
        } finally {
            lockComponent.unlockPosition(uid, symbol);
        }
    }

    /**
     * 构建 交割交易记录
     *
     * @param req
     * @param uid
     * @param info
     * @param symbol
     * @return
     */
    private TradeTransaction generateSettleTransaction(PositionSettle req, String uid, PositionSettleInfoRes info, String symbol) {
        TradeTransaction transaction = new TradeTransaction();
        transaction.setUuid(CommonUtils.generateUUID());
        transaction.setUid(uid);
        transaction.setPositionId(info.getPositionId());
        transaction.setType(TransactionType.SETTLE_POSITION.name());
        transaction.setSymbol(symbol);
        String direction = info.getDirection();
        transaction.setDirection(direction);
        transaction.setBaseQuantity(req.getQuantity());
        if (Direction.isBuy(direction) && info.getMaxSettleQuantity().compareTo(req.getQuantity()) < 0) {
            transaction.setQuoteQuantity(info.getAvailableBalance());
        } else {
            transaction.setQuoteQuantity(req.getQuantity().multiply(info.getOpenPrice()));
        }
        transaction.setPrice(info.getOpenPrice());
        transaction.setAssetStatus(AssetStatus.PENDING.name());
        transaction.setPdtStatus(PdtStatus.COMPLETED.name());
        transaction.setSource(req.getSource().name());
        BigDecimal fee = CommonUtils.ZERO_NUM;
        String feeCoin = Objects.equals(transaction.getDirection(), Direction.BUY.getName()) ? CommonUtils.getBaseCoin(transaction.getSymbol()) : CommonUtils.getQuoteCoin(transaction.getSymbol());
        UserFeeConfigRate userFeeConfigRate = tradeFeeConfigService.selectUserFeeConfig(transaction.getUid());
        if (CommonUtils.isPositive(userFeeConfigRate.getMarginSettleFeeRate())) {
            if (Objects.equals(transaction.getDirection(), Direction.BUY.getName())) {
                fee = transaction.getBaseQuantity().multiply(userFeeConfigRate.getMarginSettleFeeRate());
            } else {
                fee = transaction.getQuoteQuantity().multiply(userFeeConfigRate.getMarginSettleFeeRate());
            }
        }
        transaction.setFee(fee);
        transaction.setFeeCoin(feeCoin);
        Date date = new Date();
        transaction.setCtime(date);
        transaction.setMtime(date);
        return transaction;
    }

    /**
     * 资金前检查是否能交割
     *
     * @param activePosition
     * @param transaction
     */
    private void checkSettle(TradePosition activePosition, TradeTransaction transaction) {
        if (activePosition.getPrice().compareTo(transaction.getPrice()) != 0) {
            throw new RuntimeException("price wrong");
        }
        BigDecimal subtract = activePosition.getQuantity().subtract(transaction.getBaseQuantity());
        if (subtract.compareTo(CommonUtils.ZERO_NUM) < 0) {
            throw new RuntimeException("quantity exceed");
        }
    }

    @Override
    public void autoSettlePosition(String uid) {
        TradePositionExample example = new TradePositionExample();
        TradePositionExample.Criteria criteria = example.createCriteria();
        criteria.andUidEqualTo(uid);
        criteria.andStatusEqualTo(PositionStatus.ACTIVE.name());
        criteria.andAutoSettleEqualTo(true);
        List<TradePosition> positions = defaultTradePositionMapper.selectByExample(example);
        sortAutoSettlePositions(positions);
        int settleCount = 0;
        boolean canSettle = true;
        List<TradePosition> successPositions = new ArrayList<>();
        while (!positions.isEmpty() && canSettle && settleCount < 10) {
            settleCount += 1;
            // 是否有仓位可以做交割，如果没有任何仓位可以做交割，则结束自动交割任务。
            canSettle = false;
            for (TradePosition position : positions) {
                SettleAsset settleAsset = tradeAssetService.settleAvailable(position);
                if (CommonUtils.isPositive(settleAsset.getSettleAvailable())) {
                    canSettle = true;
                    PositionSettle req = new PositionSettle();
                    req.setPositionId(position.getUuid());
                    req.setUid(position.getUid());
                    req.setQuantity(settleAsset.getSettleAvailable());
                    req.setSource(SourceType.AUTO_POSITION_SETTLE);
                    try {
                        boolean allSettle = settlePosition(req);
                        if (allSettle) {
                            successPositions.add(position);
                        }
                    } catch (Exception e) {
                        log.warn("auto settle position {}, exception {}", position, e);
                    }
                }
            }
            positions = defaultTradePositionMapper.selectByExample(example);
            sortAutoSettlePositions(positions);
        }
        Collection<TradePosition> failPositions = CollectionUtils.subtract(positions, successPositions);
        if (!positions.isEmpty()) {
            pushService.marginAutoDelivery(uid);
        }
        if (!successPositions.isEmpty()) {
            List<String> successSymbols =
                    successPositions.stream().map(TradePosition::getSymbol).distinct().sorted().collect(Collectors.toList());
            pushService.positionAutoSettleSuccess(uid, successSymbols);
        }
        if (!failPositions.isEmpty()) {
            List<String> failSymbols =
                    failPositions.stream().map(TradePosition::getSymbol).distinct().sorted().collect(Collectors.toList());
            pushService.positionAutoSettleFail(uid, failSymbols);
        }
        if (log.isInfoEnabled()) {
            log.info("settle positions, success = {}, fail = {}", successPositions, failPositions);
        }
    }

    private void sortAutoSettlePositions(List<TradePosition> positions) {
        positions.sort((o1, o2) -> {
            String o1BaseCoin = CommonUtils.getBaseCoin(o1.getSymbol());
            String o2BaseCoin = CommonUtils.getBaseCoin(o2.getSymbol());
            if (o1BaseCoin.equals(o2BaseCoin)) {
                return -o1.getQuantity().compareTo(o2.getQuantity());
            }
            Integer o1BaseCoinSeq = Constants.BASE_LIQUIDATION_SEQUENCE_MAP.get(o1BaseCoin);
            Integer o2BaseCoinSeq = Constants.BASE_LIQUIDATION_SEQUENCE_MAP.get(o2BaseCoin);
            if (o1BaseCoinSeq != null && o2BaseCoinSeq != null) {
                return o1BaseCoinSeq.compareTo(o2BaseCoinSeq);
            }
            if (o1BaseCoinSeq != null) {
                return -1;
            }
            if (o2BaseCoinSeq != null) {
                return 1;
            }
            return o1BaseCoin.compareTo(o2BaseCoin);
        });
    }

    @Nullable
    private TradePosition getActivePosition(String positionId, String uid) {
        TradePositionExample positionExample = new TradePositionExample();
        TradePositionExample.Criteria positionCriteria = positionExample.createCriteria();
        positionCriteria.andUidEqualTo(uid);
        positionCriteria.andUuidEqualTo(positionId);
        positionCriteria.andStatusEqualTo(PositionStatus.ACTIVE.name());
        List<TradePosition> positions = defaultTradePositionMapper.selectByExample(positionExample);
        if (ListUtil.isEmpty(positions)) {
            return null;
        }
        return positions.get(0);
    }

    @Override
    public MarginAssetInfoRes marginAssetInfo(String uid) {
        MarginInfo marginInfo = this.marginInfo(uid);
        MarginAssetInfoRes marginAssetInfoRes = marginAssetMapStruct.marginInfo2MarginAssetInfoRes(marginInfo);
        return marginAssetInfoRes;
    }

    @Override
    public PageResult<ActiveOrderRes> activeOrder(ActiveOrderReq req, String uid) {
        PageHelper.startPage(req.getPage(), req.getPageSize(), true);
        //查询当前委托订单
        TradeMarginOrderExample example = new TradeMarginOrderExample();
        example.setOrderByClause("CASE SYMBOL WHEN '" + req.getSymbol() + "' THEN 1 ELSE 10 END ASC ,CTIME DESC");
        TradeMarginOrderExample.Criteria criteria = example.createCriteria();
        if (req.getOnlyCurrent()) {
            criteria.andSymbolEqualTo(req.getSymbol());
        }
        criteria.andUidEqualTo(uid);
        criteria.andStatusIn(OrderStatus.ACTIVE_STATUS);
        List<TradeMarginOrder> tradeMarginOrders = defaultTradeMarginOrderMapper.selectByExample(example);
        PageInfo<TradeMarginOrder> pageInfo = new PageInfo<>(tradeMarginOrders);
        return PageResult.generate(pageInfo.getTotal(), pageInfo.getPageNum(), pageInfo.getPageSize(),
                orderMapStruct.tradeMarginOrders2activeOrderRes(pageInfo.getList()));
    }

    @Override
    public long countActiveOrder(String uid) {
        //查询当前委托订单
        TradeMarginOrderExample example = new TradeMarginOrderExample();
        TradeMarginOrderExample.Criteria criteria = example.createCriteria();
        criteria.andUidEqualTo(uid);
        criteria.andStatusIn(OrderStatus.ACTIVE_STATUS);
        return defaultTradeMarginOrderMapper.countByExample(example);
    }


    @Override
    public PageResult<MarginOrderInfoRes> orderHistory(MarginOrderHistoryReq req, String uid) {
        PageHelper.startPage(req.getPage(), req.getPageSize(), true);
        TradeMarginOrderExample example = new TradeMarginOrderExample();
        TradeMarginOrderExample.Criteria criteria = example.createCriteria();
        criteria.andUidEqualTo(uid);
        if (!Objects.isNull(req.getStartTime()) && !Objects.isNull(req.getEndTime())) {
            if (Objects.equals(req.getOrderItem(), TradeConstants.ORDER_ITEM_CTIME)) {
                criteria.andCtimeBetween(new Date(req.getStartTime()), new Date(req.getEndTime()));
            } else {
                criteria.andMtimeBetween(new Date(req.getStartTime()), new Date(req.getEndTime()));
            }
        }
        if (!Objects.isNull(req.getSymbol())) {
            criteria.andSymbolEqualTo(req.getSymbol());
        }
        if (!Objects.isNull(req.getDirection())) {
            criteria.andDirectionEqualTo(req.getDirection());
        }
        if (!Objects.isNull(req.getStatus())) {
            if (Objects.equals(req.getStatus(), OrderStatus.APP_QUERY_STATUS_CANCELED)) {
                criteria.andStatusIn(OrderStatus.APP_CANCELED_STATUS);
            }
            if (Objects.equals(req.getStatus(), OrderStatus.APP_QUERY_STATUS_COMPLETED)) {
                criteria.andStatusIn(OrderStatus.APP_COMPLETED_STATUS);
            }
        } else {
            criteria.andStatusIn(OrderStatus.HISTORY_STATUS);
        }
        if (!Objects.isNull(req.getOrderItem())) {
            example.setOrderByClause(req.getOrderItem() + " DESC");
        }
        List<TradeMarginOrder> tradeMarginOrders = defaultTradeMarginOrderMapper.selectByExample(example);
        PageInfo<TradeMarginOrder> pageInfo = new PageInfo<>(tradeMarginOrders);
        return PageResult.generate(pageInfo.getTotal(), pageInfo.getPageNum(), pageInfo.getPageSize(),
                orderMapStruct.tradeMarginOrders2marginOrderInfoRes(pageInfo.getList()));
    }

    @Override
    public MarginOrderDetailRes orderDetail(MarginOrderDetailReq req, String uid) {
        //查询订单
        TradeMarginOrderExample example = new TradeMarginOrderExample();
        TradeMarginOrderExample.Criteria criteria = example.createCriteria();
        criteria.andUidEqualTo(uid);
        criteria.andUuidEqualTo(req.getOrderId());
        List<TradeMarginOrder> tradeMarginOrders = defaultTradeMarginOrderMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(tradeMarginOrders)) {
            return null;
        }
        MarginOrderDetailRes res = new MarginOrderDetailRes();
        log.info("orderDetail tradeMarginOrders is not empty and size is = {}", tradeMarginOrders.size());
        TradeMarginOrder tradeMarginOrder = tradeMarginOrders.get(0);
        MarginOrderInfoRes marginOrderInfoRes
                = orderMapStruct.tradeMarginOrder2marginOrderInfoRes(tradeMarginOrder);
        BeanUtils.copyProperties(marginOrderInfoRes, res);
        //查询交易记录
        List<TradeMarginOrderModification> modifications = this.orderModifications(tradeMarginOrder.getUuid());
        List<MarginOrderModificationVo> marginOrderModificationVos =
                orderModificationMapStruct.orderModifications2OrderModificationVos(modifications);
        res.setModifications(marginOrderModificationVos);
        return res;
    }

    @Override
    public List<ActivePositionInfoVo> positionActive(String uid) {
        return this.marginInfo(uid).getCurrentPositionVos();
    }

    @Async
    @Override
    public Future<List<ActivePositionInfoVo>> activePosition(String uid) {
        List<ActivePositionInfoVo> activePositionInfoVos = this.positionActive(uid);
        return new AsyncResult<>(activePositionInfoVos);
    }


    @Override
    public ActivePositionInfoVo positionDetail(PositionDetailReq req, String uid) {
        List<ActivePositionInfoVo> currentPositionVos = this.marginInfo(uid).getCurrentPositionVos();
        if (ListUtil.isNotEmpty(currentPositionVos)) {
            for (ActivePositionInfoVo item : currentPositionVos) {
                if (item.getPositionId().equals(req.getPositionId())) {
                    return item;
                }
            }
        }
        return null;

    }

    @Override
    public PageResult<PositionFundingCostVo> positionFundingCost(PositionFundingCostReq req, String uid) {
        PageHelper.startPage(req.getPage(), req.getPageSize(), true);
        TradePositionFundingCostExample example = new TradePositionFundingCostExample();
        TradePositionFundingCostExample.Criteria criteria = example.createCriteria();
        example.setOrderByClause("MTIME DESC");
        criteria.andUidEqualTo(uid);
        criteria.andPositionIdEqualTo(req.getPositionId());
        criteria.andStatusEqualTo(FundingCostStatus.COMPLETED.getName());
        List<TradePositionFundingCost> tradePositionFundingCosts
                = defaultTradePositionFundingCost.selectByExample(example);
        log.debug("tradePositionFundingCosts is not null = " + ListUtil.isNotEmpty(tradePositionFundingCosts));
        historyDataCompatibleHandle(tradePositionFundingCosts);
        PageInfo<TradePositionFundingCost> pageInfo = new PageInfo<>(tradePositionFundingCosts);
        return PageResult.generate(pageInfo.getTotal(), pageInfo.getPageNum(), pageInfo.getPageSize(),
                positionFundingCostMapStruct.positionsFundingCost2Vos(pageInfo.getList()));
    }

    /**
     * 历史APP数据的兼容处理
     *
     * @param tradePositionFundingCosts
     */
    public void historyDataCompatibleHandle(List<TradePositionFundingCost> tradePositionFundingCosts) {
        tradePositionFundingCosts.forEach(cost -> {
            //需要兼容的数据，置空 结算仓位数量 和 结算价格
            if (cost.getPositionId().startsWith(AppMigrateHandle.APP_ID_PREFIX)) {
                cost.setQuantity(null);
                cost.setPrice(null);
            }
        });
    }


    @Override
    public List<TradePosition> listAllActivePositions(List<String> uids) {
        TradePositionExample example = new TradePositionExample();
        TradePositionExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo(PositionStatus.ACTIVE.name());
        if (ListUtil.isNotEmpty(uids)) {
            criteria.andUidIn(uids);
        }
        return defaultTradePositionMapper.selectByExample(example);
    }

    @Override
    public Pair<Long, List<TradePosition>> listAllActivePositionsWithSeq(List<String> uids) {
        TradePositionExample example = new TradePositionExample();
        TradePositionExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo(PositionStatus.ACTIVE.name());
        if (ListUtil.isNotEmpty(uids)) {
            criteria.andUidIn(uids);
        }
        Long lastValue = transactionMapper.selectTransactionSeqLastValue();
        return Pair.of(lastValue, defaultTradePositionMapper.selectByExample(example));
    }

    @Override
    public Pair<Long, PageInfo<TradePosition>> listAllActivePositionsWithSeq(Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize, true);
        TradePositionExample example = new TradePositionExample();
        TradePositionExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo(PositionStatus.ACTIVE.name());
        example.setOrderByClause("ctime asc");
        List<TradePosition> positions = defaultTradePositionMapper.selectByExample(example);
        Long lastValue = transactionMapper.selectTransactionSeqLastValue();
        return Pair.of(lastValue, new PageInfo<>(positions));
    }

    @Override
    public HistoryPositionRes positionHistory(PositionHistorySearchReq req, String uid) {
        HistoryPositionRes res = new HistoryPositionRes();
        //日期差
        res.setDays(new Long((req.getEndTime() - req.getStartTime()) / (1000 * 24 * 60 * 60)).intValue());

        //查询当前条件下的累计收益
        BigDecimal bigDecimal = tradePositionMapper.sumPnl(req, uid);
        res.setTotalPnl(bigDecimal);

        //分页查询历史持仓
        PageHelper.startPage(req.getPage(), req.getPageSize(), true);
        List<TradePosition> tradePositions = this.getTradePositions(req, uid);
        //查询结果为空
        if (ListUtil.isNotEmpty(tradePositions)) {
            PageInfo<TradePosition> pageInfo = new PageInfo(tradePositions);
            List<HistoryPositionInfoVo> historyPositionInfoVos
                    = tradePositionMapStruct.tradePositions2HistoryVos(pageInfo.getList());
            res.setData(PageResult.generate(pageInfo.getTotal(), pageInfo.getPageNum(),
                    pageInfo.getPageSize(), historyPositionInfoVos));
        }

        return res;
    }

    /**
     * 查询持仓列表
     *
     * @param req
     * @param uid
     * @return
     */
    private List<TradePosition> getTradePositions(PositionHistorySearchReq req, String uid) {
        TradePositionExample example = new TradePositionExample();
        example.setOrderByClause("CTIME DESC");
        TradePositionExample.Criteria criteria = example.createCriteria();
        criteria.andUidEqualTo(uid);
        if (!Objects.isNull(req.getSymbol())) {
            criteria.andSymbolEqualTo(req.getSymbol());
        }
        if (!Objects.isNull(req.getDirection())) {
            criteria.andDirectionEqualTo(req.getDirection());
        }
        if (!Objects.isNull(req.getStartTime())) {
            criteria.andCtimeGreaterThan(new Date(req.getStartTime()));
        }
        if (!Objects.isNull(req.getEndTime())) {
            criteria.andCtimeLessThanOrEqualTo(new Date(req.getEndTime()));
        }
        //close仓位
        criteria.andStatusEqualTo(PositionStatus.CLOSE.name());
        List<TradePosition> tradePositions = defaultTradePositionMapper.selectByExample(example);
        return tradePositions;
    }

    @Override
    public HistoryPositionDetailRes positionHistoryDetail(PositionHistoryDetailReq req, String uid) {
        HistoryPositionDetailRes res = new HistoryPositionDetailRes();
        TradePositionExample example = new TradePositionExample();
        TradePositionExample.Criteria criteria = example.createCriteria();
        criteria.andUidEqualTo(uid);
        criteria.andUuidEqualTo(req.getPositionId());
        List<TradePosition> tradePositions = defaultTradePositionMapper.selectByExample(example);
        if (ListUtil.isNotEmpty(tradePositions)) {
            //获取第一条并
            TradePosition tradePosition = tradePositions.get(0);
            res = tradePositionMapStruct.tradePosition2HistoryDetailVo(tradePosition);
        }
        return res;
    }

    @Override
    public PageResult<PositionRecordVo> positionRecord(PositionRecordeReq req, String uid) {
        PageHelper.startPage(req.getPage(), req.getPageSize(), true);
        //查询对应的交易记录
        //查询对应的交易记录
        TradeTransaction tradeTransaction = new TradeTransaction();
        tradeTransaction.setUid(uid);
        tradeTransaction.setPositionId(req.getPositionId());
        PageInfo<TradeTransaction> pageInfo = getTradeTransactionPageInfo(tradeTransaction);
        List<PositionRecordVo> positionRecordVos
                = transactionMapStruct.transactions2PositionRecordVos(pageInfo.getList());
        return PageResult.generate(pageInfo.getTotal(), pageInfo.getPageNum(),
                pageInfo.getPageSize(), positionRecordVos);
    }

    @Override
    public PageResult<PositionCloseHistoryRes> positionCloseHistory(PositionCloseHistoryReq req, String uid) {

        PageHelper.startPage(req.getPage(), req.getPageSize(), true);
        //查询对应的交易记录
        TradeTransaction tradeTransaction = new TradeTransaction();
        tradeTransaction.setUid(uid);
        tradeTransaction.setSymbol(req.getSymbol());
        if (StringUtils.isNotBlank(req.getDirection())) {
            tradeTransaction.setDirection(Direction.rivalDirection(req.getDirection()).getName());
        }
        PageInfo<TradeTransaction> pageInfo = getTradeTransactionPageInfo(tradeTransaction,
                req.getStartTime(), req.getEndTime(),
                Arrays.asList(TransactionType.CLOSE_POSITION.name(), TransactionType.REDUCE_POSITION.name()));
        List<PositionCloseHistoryRes> list = transactionMapStruct.transactions2PositionCloseHistoryRes(pageInfo.getList());
        return PageResult.generate(pageInfo.getTotal(), pageInfo.getPageNum(), pageInfo.getPageSize(), list);
    }

    @Override
    public PageResult<PositionSettleHistoryRes> positionSettleHistory(PositionSettleHistoryReq req, String uid) {
        PageHelper.startPage(req.getPage(), req.getPageSize(), true);
        TradeTransaction tradeTransaction = new TradeTransaction();
        tradeTransaction.setUid(uid);
        tradeTransaction.setSymbol(req.getSymbol());
        tradeTransaction.setType(TransactionType.SETTLE_POSITION.name());
        tradeTransaction.setDirection(req.getDirection());
        PageInfo<TradeTransaction> pageInfo
                = getTradeTransactionPageInfo(tradeTransaction, req.getStartTime(), req.getEndTime(), null);
        List<PositionSettleHistoryRes> list
                = transactionMapStruct.transactions2PositionSettleHistoryRes(pageInfo.getList());
        return PageResult.generate(pageInfo.getTotal(), pageInfo.getPageNum(), pageInfo.getPageSize(), list);
    }

    @Override
    public int setUpPosition(PositionSettingReq req, String uid) {
        TradePosition tradePosition = new TradePosition();
        tradePosition.setUid(uid);
        tradePosition.setUuid(req.getPositionId());
        tradePosition.setAutoSettle(req.getAutoSettle());
        tradePosition.setStopLossPrice(req.getSlPrice());
        tradePosition.setStopLossPercentage(req.getSlPercentage());
        tradePosition.setTakeProfitPrice(req.getTpPrice());
        tradePosition.setTakeProfitPercentage(req.getTpPercentage());

        TradePositionExample example = new TradePositionExample();
        TradePositionExample.Criteria criteria = example.createCriteria();
        criteria.andUidEqualTo(uid);
        criteria.andUuidEqualTo(req.getPositionId());
        criteria.andStatusEqualTo(PositionStatus.ACTIVE.name());
        //设置仓位
        return tradePositionMapper.updateByExampleSelective(tradePosition, example);

    }

    @Override
    public PageResult<PositionInfoRes> queryPositionInfoForAmp(PositionInfoReq req) {
        PageHelper.startPage(req.getPage(), req.getPageSize(), true);
        PositionInfoRes res = new PositionInfoRes();
        //1查询持仓列表
        TradePositionExample example = new TradePositionExample();
        example.setOrderByClause("UID,SYMBOL,CTIME DESC");
        TradePositionExample.Criteria criteria = example.createCriteria();
        criteria.andUidIn(req.getUidList());
        if (ListUtil.isNotEmpty(req.getSymbolList())) {
            criteria.andSymbolIn(req.getSymbolList());
        }
        criteria.andStatusEqualTo(PositionStatus.ACTIVE.name());
        List<TradePosition> tradePositions = defaultTradePositionMapper.selectByExample(example);
        //计算浮动盈亏
        tradePositions.forEach(tradePosition -> tradePosition.setPnl(tradeAssetService.calculatePositionUnpnlUsd(tradePosition).getFirst()));
        //浮动盈亏 相对quote ,对手价格
        PageInfo<TradePosition> pageInfo = new PageInfo<>(tradePositions);
        List<PositionInfoRes> positionInfoRes = tradePositionMapStruct.tradePositions2PositionInfoRes(pageInfo.getList());
        return PageResult.generate(pageInfo.getTotal(), pageInfo.getPageNum(), pageInfo.getPageSize(), positionInfoRes);
    }

    @Override
    public PageResult<PositionFlowDetailRes> flowDetail(PositionFlowDetailReq req) {
        PageHelper.startPage(req.getPage(), req.getPageSize(), true);
        TradeMarginOrderExample example = new TradeMarginOrderExample();
        example.setOrderByClause("UID,CTIME DESC");
        TradeMarginOrderExample.Criteria criteria = example.createCriteria();
        criteria.andUidIn(req.getUidList());
        if (!Objects.isNull(req.getStartTime())) {
            criteria.andCtimeGreaterThan(new Date(req.getStartTime()));
        }
        if (!Objects.isNull(req.getEndTime())) {
            criteria.andCtimeLessThanOrEqualTo(new Date(req.getEndTime()));
        }
        if (ListUtil.isNotEmpty(req.getTypeList())) {
            criteria.andTypeIn(req.getTypeList());
        }
        if (ListUtil.isNotEmpty(req.getSymbolList())) {
            criteria.andSymbolIn(req.getSymbolList());
        }
        if (StringUtils.isNotEmpty(req.getDirection())) {
            criteria.andDirectionEqualTo(req.getDirection());
        }
        if (ListUtil.isNotEmpty(req.getStrategyList())) {
            criteria.andStrategyIn(req.getStrategyList());
        }
        if (ListUtil.isNotEmpty(req.getStatusList())) {
            criteria.andStatusIn(req.getStatusList());
        }
        List<TradeMarginOrder> tradeMarginOrders = defaultTradeMarginOrderMapper.selectByExample(example);
        PageInfo<TradeMarginOrder> pageInfo = new PageInfo<>(tradeMarginOrders);
        List<PositionFlowDetailRes> positionFlowDetailRes =
                orderMapStruct.tradeMarginOrders2PositionFlowDetailRes(pageInfo.getList());

        return PageResult.generate(pageInfo.getTotal(), pageInfo.getPageNum(),
                pageInfo.getPageSize(), positionFlowDetailRes);
    }

    /**
     * 查询交易信息
     *
     * @param tradeTransaction
     * @param start
     * @param end
     * @param list
     * @return
     */
    private PageInfo<TradeTransaction> getTradeTransactionPageInfo(TradeTransaction tradeTransaction,
                                                                   Long start, Long end, List<String> list) {
        //查询对应的交易记录
        TradeTransactionExample example1 = new TradeTransactionExample();
        example1.setOrderByClause("CTIME DESC");
        TradeTransactionExample.Criteria criteria = example1.createCriteria();
        //查询有效记录
        criteria.andPdtStatusEqualTo(PdtStatus.COMPLETED.name());
        if (!StringUtils.isEmpty(tradeTransaction.getSymbol())) {
            criteria.andSymbolEqualTo(tradeTransaction.getSymbol());
        }
        if (!StringUtils.isEmpty(tradeTransaction.getPositionId())) {
            criteria.andPositionIdEqualTo(tradeTransaction.getPositionId());
        }
        if (!StringUtils.isEmpty(tradeTransaction.getType())) {
            criteria.andTypeEqualTo(tradeTransaction.getType());
        }
        if (StringUtils.isNotBlank(tradeTransaction.getDirection())) {
            criteria.andDirectionEqualTo(tradeTransaction.getDirection());
        }
        if (ListUtil.isNotEmpty(list)) {
            criteria.andTypeIn(list);
        }
        if (start != null) {
            criteria.andCtimeGreaterThanOrEqualTo(new Date(start));
        }
        if (end != null) {
            criteria.andCtimeLessThan(new Date(end));
        }
        criteria.andUidEqualTo(tradeTransaction.getUid());
        List<TradeTransaction> tradeTransactions = defaultTradeTransactionMapper.selectByExample(example1);
        return (PageInfo<TradeTransaction>) new PageInfo(tradeTransactions);
    }

    /**
     * 查询交易信息
     *
     * @param tradeTransaction
     * @return
     */
    private PageInfo<TradeTransaction> getTradeTransactionPageInfo(TradeTransaction tradeTransaction) {
        return this.getTradeTransactionPageInfo(tradeTransaction, null, null, null);
    }

    /**
     * 查询所有杠杆订单
     *
     * @param uid
     * @return
     */
    @Override
    public List<TradeMarginOrder> getTradeMarginOrders(String uid) {
        TradeMarginOrderExample example1 = new TradeMarginOrderExample();
        TradeMarginOrderExample.Criteria criteria1 = example1.createCriteria();
        criteria1.andUidEqualTo(uid);
        criteria1.andSourceIn(SourceType.CLIENT_ORDER);
        criteria1.andStatusIn(OrderStatus.ACTIVE_STATUS);
        List<TradeMarginOrder> tradeMarginOrders = defaultTradeMarginOrderMapper.selectByExample(example1);
        return tradeMarginOrders;
    }

    @Override
    public void cancelAllNotSystemOrders(String uid) {
        List<TradeMarginOrder> tradeMarginOrders = this.getTradeMarginOrders(uid);
        if (ListUtil.isNotEmpty(tradeMarginOrders)) {
            tradeMarginOrders.stream().forEach(l -> {
                MarginOrderCancel req = new MarginOrderCancel();
                req.setUid(l.getUid());
                req.setOrderId(l.getUuid());
                req.setTerminator(TradeTerminator.RISK);
                this.cancelOrder(req);
            });
        }
    }


    @Override
    public void forceClosePosition(ReducePositionReq reducePositionReq) {
        String uid = reducePositionReq.getUid();
        //获取减仓比例
        BigDecimal ratio = reducePositionReq.getRatio();
        //查询所有杠杆仓位
        List<TradePosition> items = this.listAllActivePositions(Collections.singletonList(uid));
        //比对数据一致
        if (null != reducePositionReq.getPositions()) {
            comparePositionConsistentAndLog(reducePositionReq.getPositions(), items);
        }

        items.forEach(item -> {
            log.debug("forceClosePosition| ActivePositionInfoVo:{}", item);
            //对手方向
            Direction direction = Direction.rivalDirection(item.getDirection());
            BigDecimal quantity = item.getQuantity().multiply(ratio).setScale(Constants.DEFAULT_PRECISION, RoundingMode.UP);
            //减仓创建杠杆的反向订单
            placeUntilAllComplete(uid, item.getSymbol(), direction, quantity, SourceType.FORCE_CLOSE);
        });
        if (items.size() == 0) {
            AlarmLogUtil.alarm("no active positions force close, {}", uid);
        }
        // 强平完成，推送强平消息
        if (items.size() > 0) {
            if (ratio.compareTo(BigDecimal.ONE) == 0) {
                pushService.marginForceClose(uid);
            } else if (ratio.compareTo(BigDecimal.ZERO) > 0 && ratio.compareTo(BigDecimal.ONE) < 0) {
                pushService.marginPartForceClose(uid);
            }
        }
    }

    public void comparePositionConsistentAndLog(@NonNull List<ReducePositionReq.PositionMajorInfo> riskData,
                                                @NonNull List<TradePosition> tradeData) {
        Map<Long, ReducePositionReq.PositionMajorInfo> positionInfoMap =
                riskData.stream().collect(Collectors.toMap(ReducePositionReq.PositionMajorInfo::getPositionId, v -> v, (t
                        , t2) -> {
                    AlarmLogUtil.alarm("position id duplicate");
                    return t;
                }));
        if (tradeData.size() != positionInfoMap.size()) {
            log.error("position data inconsistent, risk data = {}, trade data = {}", riskData, tradeData);
        } else {
            tradeData.forEach(info -> {
                Long id = info.getId();
                ReducePositionReq.PositionMajorInfo majorInfo = positionInfoMap.get(id);
                if (info.getQuantity().compareTo(majorInfo.getSize()) != 0
                        || info.getPrice().compareTo(majorInfo.getPrice()) != 0) {
                    log.error("position data inconsistent, position id = {}, risk data = {}, trade data = {}"
                            , id, majorInfo, info);
                }
            });
        }
    }

    /**
     * 下单平仓直到全部成交
     *
     * @param uid
     * @param symbol
     * @param direction
     * @param quantity
     */
    @Override
    public void placeUntilAllComplete(String uid, String symbol, Direction direction,
                                      BigDecimal quantity, SourceType source) {
        BigDecimal remainQty = quantity.setScale(Constants.DEFAULT_PRECISION, RoundingMode.DOWN);
        BigDecimal maxQty = CommonUtils.getMaxOrder(symbol);
        // 兜底防止下单出错导致死循环
        for (int i = 0; i < maxPlaceNum; i++) {
            BigDecimal placeQty = remainQty.min(maxQty);
            log.info("placeUntilAllComplete| uid:{}, symbol:{}, total:{}, maxQty:{}, remain:{}, placeQty:{}",
                    uid, symbol, quantity, maxQty, remainQty, placeQty);

            MarginOrderPlace req = MarginOrderPlace.builder()
                    .uid(uid)
                    .reduceOnly(true)
                    .direction(direction)
                    .quantity(placeQty)
                    .type(OrderType.MARKET)
                    .symbol(symbol)
                    .source(source)
                    .build();

            MarginOrderInfoRes res = this.placeOrder(req);
            BigDecimal filled = res.getQuantityFilled();
            remainQty = remainQty.subtract(filled);
            if (remainQty.compareTo(BigDecimal.ZERO) <= 0) {
                break;
            }
        }
    }


    @Override
    public List<TradeMarginOrderModification> orderModifications(String uuid) {
        PageHelper.startPage(1, 100);
        TradeMarginOrderModificationExample example = new TradeMarginOrderModificationExample();
        example.setOrderByClause("CTIME ASC");
        TradeMarginOrderModificationExample.Criteria criteria = example.createCriteria();
        criteria.andOrderIdEqualTo(uuid);
        List<TradeMarginOrderModification> tradeMarginOrderModifications
                = defaultMarginModificationMapper.selectByExample(example);
        PageInfo<TradeMarginOrderModification> pageInfo = new PageInfo<>(tradeMarginOrderModifications);
        return pageInfo.getList();
    }

    @Override
    public MarginInfo marginInfo(String uid) {
        Map<String, PoolEntityForRisk> poolEntityForRiskMap = assetRequest.assetPoolForRisk(uid);
        TradeUserTradeSetting tradeSetting = userTradeSettingService.queryTradeSettingByUid(uid);
        List<TradePosition> tradePositions = listAllActivePositions(Collections.singletonList(uid));
        return tradeAssetService.marginInfo(uid, poolEntityForRiskMap, tradePositions, tradeSetting.getLeverage(),
                tradeSetting.getEarnPledge(), true);
    }

    @Override
    public Map<String, BigDecimal> unpnl(List<String> uidList) {
        List<TradePosition> tradePositions = listAllActivePositions(uidList);
        Map<String, List<TradePosition>> map = tradePositions.stream().collect(Collectors.groupingBy(TradePosition::getUid));
        return map.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,
                e -> tradeAssetService.unpnl(e.getValue())));
    }


    /**
     * 交易创建更新
     *
     * @param transaction
     */
    private void transactionNewNotice(TradeTransaction transaction) {
        pushComponent.pushWsMessage(WsPushMessage.buildAllConsumersMessage(transaction.getUid(),
                PushEventEnum.WEB_TRANSACTION_NEW,
                webMapStruct.tradeTransaction2TransactionRes(transaction)));
    }


    /**
     * 订单创建推送
     *
     * @param order
     */
    private void orderNewNotice(TradeMarginOrder order) {
        if (SourceType.isFromUser(order.getSource())) {
            pushComponent.pushWsMessage(WsPushMessage.buildAllConsumersMessage(order.getUid(),
                    PushEventEnum.WEB_ORDER_NEW, webMapStruct.tradeMarginOrder2OrderResEvent(order)));
        }
        sensorsTraceService.marginSubmission(order);
    }

    @Override
    public MarginDetailRes detail(String uid, String symbol, String direction) {
        MarginDetailRes res = new MarginDetailRes();
        if (null != uid) {
            List<String> favoriteSymbolList = marketService.listAllFavorite(uid);
            MarginInfo info = marginInfo(uid);
            BigDecimal totalPosition = info.getPosition();
            BigDecimal totalPositionLimit = MarginPositionLimitEnum.totalPositionLimit(info.getLeverage());
            BigDecimal symbolPositionLimit = MarginPositionLimitEnum.positionLimitBySymbol(info.getLeverage(), symbol);
            Optional<TradePosition> existPositionOpt = info.getCurrentPositions().stream().filter(v -> v.getSymbol().equals(symbol)).findFirst();
            BigDecimal existQuantity = BigDecimal.ZERO;
            if (existPositionOpt.isPresent()) {
                TradePosition position = existPositionOpt.get();
                BigDecimal positionValue = tradeAssetService.calculatePositionValue(position);
                existQuantity = position.getDirection().equals(direction) ? positionValue : positionValue.negate();
            }
            //totalPosition.subtract(existQuantity.abs()) 排除完当前币种持仓的剩余持仓
            totalPositionLimit = totalPositionLimit.subtract(existQuantity).subtract(totalPosition.subtract(existQuantity.abs()));
            symbolPositionLimit = symbolPositionLimit.subtract(existQuantity);
            BigDecimal canOpenUsd = info.getCanOpenUsd().subtract(existQuantity.min(BigDecimal.ZERO));
            BigDecimal baseMaxOpen;
            String base = CommonUtils.coinPair(symbol).getFirst();
            BigDecimal maxOpenUsd = canOpenUsd.min(totalPositionLimit).min(symbolPositionLimit).max(BigDecimal.ZERO);
            if (Constants.BASE_COIN.equals(base)) {
                baseMaxOpen = maxOpenUsd;
            } else {
                BigDecimal price = SymbolDomain.nonNullGet(base + Constants.BASE_QUOTE).price(Direction.getByName(direction));
                baseMaxOpen = maxOpenUsd.divide(price,
                        Constants.PRICE_PRECISION, RoundingMode.DOWN);
            }
            res.setMaxOpen(baseMaxOpen);
            //反向
            res.setFundingRate(CoinDomain.fundingCostRate(symbol, direction).negate());
            res.setFavorite(favoriteSymbolList.contains(symbol));
            res.setFeeRate(tradeFeeConfigService.selectUserFeeConfig(uid).getMarginFeeRate());
        } else {
            res.setMaxOpen(null);
            res.setFavorite(false);
            //反向
            res.setFundingRate(CoinDomain.fundingCostRate(symbol, direction).negate());
        }
        return res;
    }

    @Override
    public Map<String, MarginAssetInfoRes> marginInfos(List<String> uids) {
        Map<String, MarginAssetInfoRes> resMap = new HashMap<>();
        BatchUserPoolReq req = new BatchUserPoolReq();
        req.setUids(uids);
        Response<List<BatchPoolEntityForRisk>> res = assetRiskInfoClient.getPoolBatch(req);
        Map<String, Map<String, PoolEntityForRisk>> listMap = new DefaultedMap<>(Collections.emptyMap());
        if (BusinessExceptionEnum.SUCCESS.getCode() == res.getCode()) {
            List<BatchPoolEntityForRisk> data = res.getData();
            if (ListUtil.isNotEmpty(data)) {
                listMap = data.stream().collect(Collectors.toMap(BatchPoolEntityForRisk::getUid, BatchPoolEntityForRisk::getPools));
            }
        } else {
            AlarmLogUtil.alarm("risk asset pool err, margin set up to 0, res = {}", res);
        }
        List<TradePosition> tradePositions = listAllActivePositions(uids);
        Map<String, List<TradePosition>> collect = new HashMap<>();
        if (ListUtil.isNotEmpty(tradePositions)) {
            collect = tradePositions.stream().collect(Collectors.groupingBy(TradePosition::getUid));
        }

        for (String uid : uids) {
            TradeUserTradeSetting tradeSetting = userTradeSettingService.queryTradeSettingByUid(uid);
            MarginInfo marginInfo = tradeAssetService.marginInfo(uid, listMap.get(uid), collect.get(uid), tradeSetting.getLeverage(),
                    tradeSetting.getEarnPledge(), tradeSetting.getAutoConvert());
            List<ActivePositionInfoVo> currentPositionVos = marginInfo.getCurrentPositionVos();
            MarginAssetInfoRes marginAssetInfoRes = marginAssetMapStruct.marginInfo2MarginAssetInfoRes(marginInfo);
            marginAssetInfoRes.setCurrentPositionVos(currentPositionVos);
            resMap.put(uid, marginAssetInfoRes);
        }

        return resMap;
    }

    @Override
    public Position6HFundingCostVo getFundingCostBefore6H(Long searchTime) {
        Calendar time = Calendar.getInstance();
        time.setTime(new Date(searchTime));
        time.set(Calendar.MINUTE, 0);
        time.set(Calendar.SECOND, 0);
        time.set(Calendar.MILLISECOND, 0);
        List<BigDecimal> hisFundingCost = new ArrayList<>();
        TradePositionFundingCostExample tradePositionFundingCostExample = new TradePositionFundingCostExample();
        TradeNegativeBalanceFundingCostExample balanceFundingCostExample = new TradeNegativeBalanceFundingCostExample();
        for (int i = 1; i < 7; i++) {
            Date right = time.getTime();
            time.add(Calendar.HOUR_OF_DAY, -1);
            Date left = time.getTime();
            tradePositionFundingCostExample.createCriteria()
                    .andRoundGreaterThan(left.getTime())
                    .andRoundLessThanOrEqualTo(right.getTime());
            List<TradePositionFundingCost> tradePositionFundingCosts = defaultTradePositionFundingCost.selectByExample(tradePositionFundingCostExample);
            Optional<BigDecimal> tradePositionFundingCost = tradePositionFundingCosts.stream().map(TradePositionFundingCost::getFundingCost).reduce(BigDecimal::add);
            balanceFundingCostExample.createCriteria()
                    .andRoundGreaterThan(left.getTime())
                    .andRoundLessThanOrEqualTo(right.getTime());
            List<TradeNegativeBalanceFundingCost> tradeNegativeBalanceFundingCosts = defaultTradeNegativeBalanceFundingCostMapper.selectByExample(balanceFundingCostExample);
            Optional<BigDecimal> balanceFundingCost = tradeNegativeBalanceFundingCosts.stream().map(e -> {
                String coin = e.getCoin();
                if (coin.equals(Constants.BASE_COIN)) {
                    return e.getFundingCost();
                }
                BigDecimal price = SymbolDomain.nonNullGet(e.getCoin() + Constants.BASE_QUOTE).midPrice();
                BigDecimal fundingCost = e.getFundingCost();
                return price.multiply(fundingCost);
            }).reduce(BigDecimal::add);
            BigDecimal fundingCost = tradePositionFundingCost.orElse(BigDecimal.ZERO)
                    .add(balanceFundingCost.orElse(BigDecimal.ZERO));
            hisFundingCost.add(fundingCost);
            tradePositionFundingCostExample.clear();
            balanceFundingCostExample.clear();
        }
        Position6HFundingCostVo position6HFundingCostVo = new Position6HFundingCostVo();
        position6HFundingCostVo.setFundingCostCount(hisFundingCost);
        return position6HFundingCostVo;
    }

    @Override
    public PageResult<String> getTransactionListForAmp(AmpTransReq ampTransReq) {
        PageHelper.startPage(ampTransReq.getPage(), ampTransReq.getPageSize(), true);
        TradeTransactionExample tradeTransactionExample = new TradeTransactionExample();
        tradeTransactionExample.createCriteria()
                //查询有效订单
                .andPdtStatusEqualTo(PdtStatus.COMPLETED.name())
                .andUidEqualTo(ampTransReq.getUid())
                .andOrderIdEqualTo(ampTransReq.getOrderId());
        tradeTransactionExample.setOrderByClause("CTIME DESC");
        List<TradeTransaction> tradeTransactions = defaultTradeTransactionMapper.selectByExample(tradeTransactionExample);
        PageInfo<TradeTransaction> pageInfo = new PageInfo<>(tradeTransactions);
        List<String> list = tradeTransactions.stream().map(TradeTransaction::getUuid).collect(Collectors.toList());
        return PageResult.generate(pageInfo.getTotal(), pageInfo.getPageNum(), pageInfo.getPageSize(), list);
    }

    @Override
    public AmpTransDetailRes getTransDetailForAmp(String uid, String transId) {
        TradeTransactionExample tradeTransactionExample = new TradeTransactionExample();
        tradeTransactionExample.createCriteria()
                //查询有效记录
                .andPdtStatusEqualTo(PdtStatus.COMPLETED.name())
                .andUuidEqualTo(transId)
                .andUidEqualTo(uid);
        List<TradeTransaction> tradeTransactions = defaultTradeTransactionMapper.selectByExample(tradeTransactionExample);
        if (CollectionUtils.isEmpty(tradeTransactions)) {
//            throw new BusinessException();
            return null;
        }
        TradeTransaction tradeTransaction = tradeTransactions.get(0);
        AmpTransDetailRes ampTransDetailRes = transactionMapStruct.transaction2AmpTransDetailRes(tradeTransaction);
        return ampTransDetailRes;
    }


    @Override
    public PageResult<AmpPositionRes> positionHistoryForAmp(AmpPositionReq req) {
        PageHelper.startPage(req.getPage(), req.getPageSize(), true);
        TradePositionExample example = new TradePositionExample();
        example.setOrderByClause("MTIME DESC");
        TradePositionExample.Criteria criteria = example.createCriteria();
        criteria.andUidEqualTo(req.getUid());
        if (StringUtils.isNotBlank(req.getSymbol())) {
            criteria.andSymbolEqualTo(req.getSymbol());
        }
        if (StringUtils.isNotBlank(req.getDirection())) {
            criteria.andDirectionEqualTo(req.getDirection());
        }
        if (StringUtils.isNotBlank(req.getStatus())) {
            criteria.andStatusEqualTo(req.getStatus());
        }
        if (StringUtils.isNotBlank(req.getPositionId())) {
            criteria.andUuidEqualTo(req.getPositionId());
        }
        if (req.getStartMtime() != null) {
            Date startTime = CommonUtils.getNowTime(req.getStartMtime());
            criteria.andMtimeGreaterThan(startTime);
        }
        if (req.getEndMtime() != null) {
            Date endTime = CommonUtils.getNowTime(req.getEndMtime());
            criteria.andMtimeLessThan(endTime);
        }

        if (req.getStartCtime() != null) {
            Date startTime = CommonUtils.getNowTime(req.getStartCtime());
            criteria.andCtimeGreaterThan(startTime);
        }
        if (req.getEndCtime() != null) {
            Date endTime = CommonUtils.getNowTime(req.getEndCtime());
            criteria.andCtimeLessThan(endTime);
        }
        List<TradePosition> tradePositions = defaultTradePositionMapper.selectByExample(example);
        PageInfo<TradePosition> pageInfo = new PageInfo<>(tradePositions);
        List<AmpPositionRes> ampPositionResList = tradePositionMapStruct.tradePositions2AmpPositionRes(pageInfo.getList());
        for (int i = 0; i < ampPositionResList.size(); i++) {
            TradePosition tradePosition = tradePositions.get(i);
            Pair<BigDecimal, BigDecimal> pair = tradeAssetService.calculatePositionUnpnlUsd(tradePosition);
            BigDecimal first = pair.getFirst();
            AmpPositionRes ampPositionRes = ampPositionResList.get(i);
            ampPositionRes.setUnpnl(first);
        }
        return PageResult.generate(pageInfo.getTotal(), pageInfo.getPageNum(), pageInfo.getPageSize(), ampPositionResList);
    }

    @Override
    public PageResult<AmpMarginRes> marginHistoryForAmp(AmpMarginReq req) {
        PageHelper.startPage(req.getPage(), req.getPageSize(), true);
        List<TradeMarginOrder> tradeMarginOrders = tradePositionMapper.selectTradeMarginOrderByAmpMarginReq(req);
        PageInfo<TradeMarginOrder> pageInfo = new PageInfo<>(tradeMarginOrders);
        List<AmpMarginRes> ampMarginRes = orderMapStruct.tradeMarginOrders2AmpMarginRes(pageInfo.getList());
        return PageResult.generate(pageInfo.getTotal(), pageInfo.getPageNum(), pageInfo.getPageSize(), ampMarginRes);
    }

    @Override
    public PageResult<AceUpMarginRes> marginHistoryForAceUp(AceUpMarginReq req) {
        TradeMarginOrderExample example = new TradeMarginOrderExample();
        example.setOrderByClause("CTIME DESC");
        TradeMarginOrderExample.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(req.getOrderId())) {
            criteria.andUuidEqualTo(req.getOrderId());
        }
        if (StringUtils.isNotBlank(req.getUid())) {
            criteria.andUidEqualTo(req.getUid());
        }
        if (CollectionUtils.isNotEmpty(req.getSymbolList())) {
            criteria.andSymbolIn(req.getSymbolList());
        }
        if (StringUtils.isNotBlank(req.getDirection())) {
            criteria.andDirectionEqualTo(req.getDirection());
        }
        if (CollectionUtils.isNotEmpty(req.getTypeList())) {
            criteria.andTypeIn(req.getTypeList());
        }
        if (CollectionUtils.isNotEmpty(req.getStrategyList())) {
            criteria.andStrategyIn(req.getStrategyList());
        }
        if (CollectionUtils.isNotEmpty(req.getStatusList())) {
            ArrayList<String> qStatus = new ArrayList<>();
            for (String status : req.getStatusList()) {
                List<String> collect = AmpOrderStatus.getByCode(status).stream().map(AmpOrderStatus::getName).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(collect)) {
                    qStatus.addAll(collect);
                }
            }
            criteria.andStatusIn(qStatus);
        }
        if (StringUtils.isNotBlank(req.getSource())) {
            criteria.andSourceEqualTo(req.getSource());
        }
        if (req.getStartCtime() != null && req.getEndCtime() != null) {
            Date startTime = CommonUtils.getNowTime(req.getStartCtime());
            Date endTime = CommonUtils.getNowTime(req.getEndCtime());
            criteria.andCtimeBetween(startTime, endTime);
        }
        if (req.getStartMtime() != null && req.getEndMtime() != null) {
            Date startTime = CommonUtils.getNowTime(req.getStartMtime());
            Date endTime = CommonUtils.getNowTime(req.getEndMtime());
            criteria.andMtimeBetween(startTime, endTime);
        }
        PageHelper.startPage(req.getPage(), req.getPageSize(), true);
        List<TradeMarginOrder> tradeMarginOrders = defaultTradeMarginOrderMapper.selectByExample(example);
        PageInfo<TradeMarginOrder> pageInfo = new PageInfo<>(tradeMarginOrders);
        List<AceUpMarginRes> aceUpMarginResList = orderMapStruct.tradeMarginOrders2AceUpMarginRes(pageInfo.getList());
        return PageResult.generate(pageInfo.getTotal(), pageInfo.getPageNum(), pageInfo.getPageSize(), aceUpMarginResList);
    }

    @Override
    public PageResult<AceUpMarginTransRes> marginTransactionListForAceUp(AceUpMarginTransReq req) {
        TradeTransactionExample example = new TradeTransactionExample();
        example.setOrderByClause("CTIME DESC");
        TradeTransactionExample.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(req.getTransId())) {
            criteria.andUuidEqualTo(req.getTransId());
        }
        if (StringUtils.isNotBlank(req.getOrderId())) {
            criteria.andOrderIdEqualTo(req.getOrderId());
        }
        if (StringUtils.isNotBlank(req.getPositionId())) {
            criteria.andPositionIdEqualTo(req.getPositionId());
        }
        if (StringUtils.isNotBlank(req.getUid())) {
            criteria.andUidEqualTo(req.getUid());
        }
        if (StringUtils.isNotBlank(req.getType())) {
            criteria.andTypeEqualTo(req.getType());
        }
        if (CollectionUtils.isNotEmpty(req.getOrderTypeList())) {
            criteria.andOrderTypeIn(req.getOrderTypeList());
        }
        if (CollectionUtils.isNotEmpty(req.getSymbolList())) {
            criteria.andSymbolIn(req.getSymbolList());
        }
        if (StringUtils.isNotBlank(req.getDirection())) {
            criteria.andDirectionEqualTo(req.getDirection());
        }
        if (StringUtils.isNotBlank(req.getSource())) {
            criteria.andSourceEqualTo(req.getSource());
        }
        if (req.getStartCtime() != null && req.getEndCtime() != null) {
            Date startTime = CommonUtils.getNowTime(req.getStartCtime());
            Date endTime = CommonUtils.getNowTime(req.getEndCtime());
            criteria.andCtimeBetween(startTime, endTime);
        }
        if (req.getStartMtime() != null && req.getEndMtime() != null) {
            Date startTime = CommonUtils.getNowTime(req.getStartMtime());
            Date endTime = CommonUtils.getNowTime(req.getEndMtime());
            criteria.andMtimeBetween(startTime, endTime);
        }
        PageHelper.startPage(req.getPage(), req.getPageSize(), true);
        List<TradeTransaction> tradeTransactions = defaultTradeTransactionMapper.selectByExample(example);
        PageInfo<TradeTransaction> pageInfo = new PageInfo<>(tradeTransactions);
        List<AceUpMarginTransRes> aceUpMarginTransResList = orderMapStruct.tradeTransactions2AceUpMarginTransRes(pageInfo.getList());
        TradePositionExample tradePositionExample = new TradePositionExample();
        aceUpMarginTransResList.forEach(aceUpMarginTransRes -> {
            String positionId = aceUpMarginTransRes.getPositionId();
            if (StringUtils.isNotBlank(positionId)) {
                tradePositionExample.clear();
                tradePositionExample.setOrderByClause("id limit 1");
                tradePositionExample.createCriteria().andUuidEqualTo(positionId);
                List<TradePosition> positionList = defaultTradePositionMapper.selectByExample(tradePositionExample);
                if (CollectionUtils.isNotEmpty(positionList)) {
                    TradePosition tradePosition = positionList.get(0);
                    aceUpMarginTransRes.setOpenPrice(tradePosition.getPrice());
                } else {
                    aceUpMarginTransRes.setOpenPrice(BigDecimal.ZERO);
                }
            } else {
                aceUpMarginTransRes.setOpenPrice(BigDecimal.ZERO);
            }
        });
        return PageResult.generate(pageInfo.getTotal(), pageInfo.getPageNum(), pageInfo.getPageSize(), aceUpMarginTransResList);
    }

    @Override
    public PageResult<AceUpMarginPositionRes> marginPositionListForAceUp(AceUpMarginPositionReq req) {
        TradePositionExample example = new TradePositionExample();
        example.setOrderByClause("CTIME DESC");
        TradePositionExample.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(req.getPositionId())) {
            criteria.andUuidEqualTo(req.getPositionId());
        }
        if (StringUtils.isNotBlank(req.getOrderId())) {
            List<TradePosition> positionByOrderId = getPositionByOrderId(req.getOrderId());
            List<String> collect = positionByOrderId.stream().map(TradePosition::getUuid).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(collect)) {
                criteria.andUuidIn(collect);
            } else {
                return PageResult.generate(0, req.getPage(), req.getPageSize(), new ArrayList<>());
            }
        }
        if (StringUtils.isNotBlank(req.getUid())) {
            criteria.andUidEqualTo(req.getUid());
        }
        if (CollectionUtils.isNotEmpty(req.getSymbolList())) {
            criteria.andSymbolIn(req.getSymbolList());
        }
        if (StringUtils.isNotBlank(req.getDirection())) {
            criteria.andDirectionEqualTo(req.getDirection());
        }
        if (StringUtils.isNotBlank(req.getStatus())) {
            criteria.andStatusEqualTo(req.getStatus());
        }
        if (req.getStartCtime() != null && req.getEndCtime() != null) {
            Date startTime = CommonUtils.getNowTime(req.getStartCtime());
            Date endTime = CommonUtils.getNowTime(req.getEndCtime());
            criteria.andCtimeBetween(startTime, endTime);
        }
        if (req.getStartMtime() != null && req.getEndMtime() != null) {
            Date startTime = CommonUtils.getNowTime(req.getStartMtime());
            Date endTime = CommonUtils.getNowTime(req.getEndMtime());
            criteria.andMtimeBetween(startTime, endTime);
        }
        PageHelper.startPage(req.getPage(), req.getPageSize(), true);
        List<TradePosition> tradePositions = defaultTradePositionMapper.selectByExample(example);
        PageInfo<TradePosition> pageInfo = new PageInfo<>(tradePositions);
        List<AceUpMarginPositionRes> aceUpMarginPositionResList = orderMapStruct.tradePositions2AceUpMarginPositionRes(pageInfo.getList());
        return PageResult.generate(pageInfo.getTotal(), pageInfo.getPageNum(), pageInfo.getPageSize(), aceUpMarginPositionResList);
    }

    @NonNull
    private List<TradePosition> getPositionByOrderId(String orderId) {
        TradeTransactionExample example = new TradeTransactionExample();
        TradeTransactionExample.Criteria criteria = example.createCriteria();
        criteria.andOrderIdEqualTo(orderId);
        criteria.andPositionIdIsNotNull();
        List<TradeTransaction> tradeTransactions = defaultTradeTransactionMapper.selectByExample(example);
        if (CollectionUtils.isNotEmpty(tradeTransactions)) {
            TradeTransaction tradeTransaction = tradeTransactions.get(0);
            TradePositionExample tradePositionExample = new TradePositionExample();
            tradePositionExample.createCriteria().andUuidEqualTo(tradeTransaction.getPositionId());
            List<TradePosition> tradePositions = defaultTradePositionMapper.selectByExample(tradePositionExample);
            return tradePositions;
        }
        return new ArrayList<>();
    }

    @Override
    public PageResult<AceUpFundingCostRes> positionFundingCostListForAceUp(AceUpFundingCostReq req) {
        TradePositionFundingCostExample example = new TradePositionFundingCostExample();
        example.setOrderByClause("CTIME DESC");
        TradePositionFundingCostExample.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(req.getUuid())) {
            criteria.andUuidEqualTo(req.getUuid());
        }
        if (StringUtils.isNotBlank(req.getPositionId())) {
            criteria.andPositionIdEqualTo(req.getPositionId());
        }
        if (StringUtils.isNotBlank(req.getOrderId())) {
            List<TradePosition> positionByOrderId = getPositionByOrderId(req.getOrderId());
            List<String> collect = positionByOrderId.stream().map(TradePosition::getUuid).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(collect)) {
                criteria.andPositionIdIn(collect);
            } else {
                return PageResult.generate(0, req.getPage(), req.getPageSize(), new ArrayList<>());
            }
        }
        if (StringUtils.isNotBlank(req.getUid())) {
            criteria.andUidEqualTo(req.getUid());
        }
        if (StringUtils.isNotBlank(req.getDirection())) {
            criteria.andDirectionEqualTo(req.getDirection());
        }
        if (CollectionUtils.isNotEmpty(req.getSymbolList())) {
            criteria.andSymbolIn(req.getSymbolList());
        }
        if (CollectionUtils.isNotEmpty(req.getStatusList())) {
            criteria.andStatusIn(req.getStatusList());
        }
        if (req.getStartCtime() != null && req.getEndCtime() != null) {
            Date startTime = CommonUtils.getNowTime(req.getStartCtime());
            Date endTime = CommonUtils.getNowTime(req.getEndCtime());
            criteria.andCtimeBetween(startTime, endTime);
        }
        if (req.getStartMtime() != null && req.getEndMtime() != null) {
            Date startTime = CommonUtils.getNowTime(req.getStartMtime());
            Date endTime = CommonUtils.getNowTime(req.getEndMtime());
            criteria.andMtimeBetween(startTime, endTime);
        }
        PageHelper.startPage(req.getPage(), req.getPageSize(), true);
        List<TradePositionFundingCost> tradePositionFundingCosts = defaultTradePositionFundingCostMapper.selectByExample(example);
        PageInfo<TradePositionFundingCost> pageInfo = new PageInfo<>(tradePositionFundingCosts);
        List<AceUpFundingCostRes> aceUpFundingCostResList = orderMapStruct.tradePositionFundingCosts2AceUpFundingCostRes(pageInfo.getList());
        return PageResult.generate(pageInfo.getTotal(), pageInfo.getPageNum(), pageInfo.getPageSize(), aceUpFundingCostResList);
    }

}
