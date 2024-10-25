package com.google.backend.trading.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.backend.asset.common.model.asset.req.GetReqIdStatusReq;
import com.google.backend.asset.common.model.trade.req.TradeCurrencyConversion;
import com.google.backend.asset.common.model.trade.req.TradeSpotOrderReq;
import com.google.backend.asset.common.model.trade.req.TradeSpotReq;
import com.google.backend.trading.alarm.AlarmComponent;
import com.google.backend.trading.alarm.AlarmEnum;
import com.google.backend.trading.client.feign.AssetInfoClient;
import com.google.backend.trading.client.feign.KlineInfoClient;
import com.google.backend.trading.component.CoinDomain;
import com.google.backend.trading.component.SymbolDomain;
import com.google.backend.trading.constant.Constants;
import com.google.backend.trading.constant.TradeConstants;
import com.google.backend.trading.dao.mapper.DefaultTradeSpotOrderMapper;
import com.google.backend.trading.dao.mapper.DefaultTradeSpotOrderModificationMapper;
import com.google.backend.trading.dao.mapper.TradeFeeConfigMapper;
import com.google.backend.trading.dao.model.TradeFeeConfig;
import com.google.backend.trading.dao.model.TradeSpotOrder;
import com.google.backend.trading.dao.model.TradeSpotOrderExample;
import com.google.backend.trading.dao.model.TradeSpotOrderModification;
import com.google.backend.trading.dao.model.TradeSpotOrderModificationExample;
import com.google.backend.trading.dao.model.TradeTransaction;
import com.google.backend.trading.dao.model.TradeTransactionExample;
import com.google.backend.trading.exception.BusinessException;
import com.google.backend.trading.exception.BusinessExceptionEnum;
import com.google.backend.trading.mapstruct.spot.TradeSpotOrderMapStruct;
import com.google.backend.trading.mapstruct.web.WebMapStruct;
import com.google.backend.trading.model.asset.Balance;
import com.google.backend.trading.model.buycrypt.BuyCryptRes;
import com.google.backend.trading.model.common.PageResult;
import com.google.backend.trading.model.common.Response;
import com.google.backend.trading.model.common.model.riskcontrol.OrderRes;
import com.google.backend.trading.model.internal.aceup.AceUpSpotReq;
import com.google.backend.trading.model.internal.aceup.AceUpSpotRes;
import com.google.backend.trading.model.internal.aceup.AceUpSpotTransRes;
import com.google.backend.trading.model.internal.amp.AmpSpotReq;
import com.google.backend.trading.model.internal.amp.AmpSpotRes;
import com.google.backend.trading.model.kline.dto.PriceChange;
import com.google.backend.trading.model.kline.dto.PriceChange24h;
import com.google.backend.trading.model.pdt.CreateTradeReq;
import com.google.backend.trading.model.pdt.CreateTradeRes;
import com.google.backend.trading.model.spot.api.SpotDetailRes;
import com.google.backend.trading.model.spot.api.SpotOrderActiveReq;
import com.google.backend.trading.model.spot.api.SpotOrderHistoryReq;
import com.google.backend.trading.model.spot.api.SpotOrderInfoRes;
import com.google.backend.trading.model.spot.api.SpotOrderPlaceRes;
import com.google.backend.trading.model.spot.api.SpotOrderUpdateReq;
import com.google.backend.trading.model.spot.dto.SpotOrderCancel;
import com.google.backend.trading.model.spot.dto.SpotOrderPlace;
import com.google.backend.trading.model.trade.AmpOrderStatus;
import com.google.backend.trading.model.trade.AssetStatus;
import com.google.backend.trading.model.trade.Direction;
import com.google.backend.trading.model.trade.OrderError;
import com.google.backend.trading.model.trade.OrderStatus;
import com.google.backend.trading.model.trade.OrderType;
import com.google.backend.trading.model.trade.PdtStatus;
import com.google.backend.trading.model.trade.PriorityCoinWithUSD;
import com.google.backend.trading.model.trade.SourceType;
import com.google.backend.trading.model.trade.TradeStrategy;
import com.google.backend.trading.model.trade.TradeTerminator;
import com.google.backend.trading.model.trade.TradeType;
import com.google.backend.trading.model.trade.TriggerType;
import com.google.backend.trading.push.FundingBehaviorEventMessage;
import com.google.backend.trading.push.PushComponent;
import com.google.backend.trading.push.PushEventEnum;
import com.google.backend.trading.push.WsPushMessage;
import com.google.backend.trading.service.AssetRequest;
import com.google.backend.trading.service.CrexApiRequest;
import com.google.backend.trading.service.MarketService;
import com.google.backend.trading.service.OrderRequest;
import com.google.backend.trading.service.PushMsgService;
import com.google.backend.trading.service.RiskInfoService;
import com.google.backend.trading.service.SensorsTraceService;
import com.google.backend.trading.service.SpotService;
import com.google.backend.trading.service.TradeAssetService;
import com.google.backend.trading.service.TradeFeeConfigService;
import com.google.backend.trading.service.TradeTransactionService;
import com.google.backend.trading.transaction.SpotTransaction;
import com.google.backend.trading.util.AlarmLogUtil;
import com.google.backend.trading.util.CommonUtils;
import com.google.backend.trading.util.LiquidUtil;
import com.google.backend.trading.util.ListUtil;
import com.google.backend.trading.util.SpotUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 现货处理逻辑的实现
 *
 * @author savion.chen
 * @date 2021/9/30 10:46
 */
@Slf4j
@Service
public class SpotServiceImpl implements SpotService {

    @Autowired
    private AssetRequest assetRequest;
    @Autowired
    private OrderRequest orderRequest;
    @Autowired
    private CrexApiRequest crexApiRequest;
    @Autowired
    private SpotTransaction spotTransaction;
    @Autowired
    private TradeTransactionService tradeTransactionService;

    @Autowired
    private DefaultTradeSpotOrderModificationMapper spotModifyMapper;
    @Autowired
    private DefaultTradeSpotOrderMapper tradeSpotOrderMapper;
    @Autowired
    private TradeFeeConfigMapper tradeFeeConfigMapper;
    @Autowired
    private TradeSpotOrderMapStruct tradeSpotOrderMapStruct;

    @Autowired
    private PushComponent pushComponent;
    @Autowired
    private PushMsgService pushService;

    @Autowired
    private MarketService marketService;
    @Autowired
    private KlineInfoClient klineInfoClient;
    @Resource
    private WebMapStruct webMapStruct;
    @Resource
    private SensorsTraceService sensorsTraceService;
    @Autowired
    private RiskInfoService riskInfoService;
    @Autowired
    private TradeFeeConfigService tradeFeeConfigService;
    @Autowired
    private TradeAssetService tradeAssetService;
    @Autowired
    private AssetInfoClient assetInfoClient;
    @Autowired
    private AlarmComponent alarmComponent;

    @Value("${place.max-num}")
    private Integer maxPlaceNum;

    //-----------------------报撤单的管理接口部分-------------------
    @Override
    public SpotOrderPlaceRes placeOrder(SpotOrderPlace req) {
        String uid = req.getUid();
        String symbol = req.getSymbol();
        orderRequest.checkPlaceIDKOrder(uid, symbol);
        if (!CommonUtils.checkSpotSymbol(symbol)) {
            throw new BusinessException(BusinessExceptionEnum.SYMBOL_NOT_SUPPORT_SPOT);
        }

        if (req.getSource().isFromUser()) {
            // 风控状态检查
            riskInfoService.validateRiskStatus(uid);
            // 超过最大允许挂单笔数的限制
            orderRequest.validateMaxPendingNum(uid);
            // 校验PDT和指数是否异常
            orderRequest.validatePriceStatus(symbol);
        }

        checkPriceAndQuantity(req);
        if (orderRequest.isImmediately(req.getType(), req.getStrategy())) {
            if (!orderRequest.isReachPrice(symbol, req.getDirection(), req.getPrice())) {
                throw new BusinessException(BusinessExceptionEnum.FOK_IOC_LIMIT_PRICE_NOT_REACH);
            }
        }

        TradeSpotOrder order = createSpotOrder(req);
        orderRequest.validateLockQuantity(order);
        spotTransaction.insertOrder(order);

        this.orderNewNotice(order);
        if (!OrderType.isTriggerOrder(order.getType())) {
            performFreeze(order);
        }

        if (req.getSource().isFromUser()) {
            pushService.submitOrderOk(order.getUid(), true);
        }

        if (CommonUtils.isSyncOrder(req.getType(), req.getStrategy(), req.getSource())) {
            checkAndFillOrder(order, false);
        }
        return getOrderResult(order);
    }

    private boolean lockOrderForUpdate(String orderId, OrderStatus currentStatus) {
        OrderStatus lockStatus = (currentStatus == OrderStatus.PRE_TRIGGER ? OrderStatus.PENDING : OrderStatus.LOCKED);
        TradeSpotOrder update = new TradeSpotOrder();
        update.setStatus(lockStatus.getName());
        TradeSpotOrderExample example = new TradeSpotOrderExample();
        example.createCriteria().andUuidEqualTo(orderId).andStatusEqualTo(currentStatus.getName());
        return tradeSpotOrderMapper.updateByExampleSelective(update, example) == 1;
    }

    private boolean unlockOrderForUpdate(String orderId, OrderStatus originalStatus) {
        OrderStatus lockStatus = (originalStatus == OrderStatus.PRE_TRIGGER ? OrderStatus.PENDING : OrderStatus.LOCKED);
        TradeSpotOrder update = new TradeSpotOrder();
        update.setStatus(originalStatus.getName());
        TradeSpotOrderExample example = new TradeSpotOrderExample();
        example.createCriteria().andUuidEqualTo(orderId).andStatusEqualTo(lockStatus.getName());
        return tradeSpotOrderMapper.updateByExampleSelective(update, example) == 1;
    }

    @Override
    public String updateOrder(SpotOrderUpdateReq req, String userId) {
        TradeSpotOrder order = querySpotOrderById(req.getOrderId(), userId);
        if (order == null) {
            throw new BusinessException(BusinessExceptionEnum.ORDER_CHANGE_OR_NOT_FOUND);
        }

        riskInfoService.validateRiskStatus(order.getUid());
        OrderStatus status = OrderStatus.getByName(order.getStatus());
        //市价单不允许修改
        if (OrderType.MARKET.getName().equals(order.getType())) {
            throw new BusinessException(BusinessExceptionEnum.ORDER_CHANGE_OR_NOT_FOUND);
        }
        // 只有PRE_TRIGGER，EXECUTING状态订单允许修改
        if (!(status == OrderStatus.PRE_TRIGGER || status == OrderStatus.EXECUTING)) {
            throw new BusinessException(BusinessExceptionEnum.ORDER_CHANGE_OR_NOT_FOUND);
        }

        // 状态改变了不允许修改
        if (!(order.getStatus().equals(req.getLastStatus())
                && order.getQuantity().compareTo(req.getLastQuantity()) == 0)) {
            throw new BusinessException(BusinessExceptionEnum.ORDER_CHANGE_OR_NOT_FOUND);
        }

        // 锁定订单，锁定失败表示订单正在被触发和正在发单中，不允许修改
        // PRE_TRIGGER -> PENDING   EXECUTING -> LOCKED
        if (!lockOrderForUpdate(order.getUuid(), status)) {
            throw new BusinessException(BusinessExceptionEnum.ORDER_CHANGE_OR_NOT_FOUND);
        }
        try {
            //锁定后再次获取
            order = tradeSpotOrderMapper.selectByPrimaryKey(order.getId());
            BigDecimal filledQty = order.getQuantityFilled();
            if (req.getQuantity().compareTo(filledQty) < 1) {
                throw new BusinessException(BusinessExceptionEnum.ORDER_CHANGE_OR_NOT_FOUND);
            }

            SpotOrderPlace params = getCheckRequest(req, order);
            checkPriceAndQuantity(params);

            // 先进行参数检查，更新order对象，不写数据库；
            TradeSpotOrderModification origin = null;
            if (isEmptyAddModify(order.getUuid())) {
                // 如果order没有修改记录，则新增一条下单记录到trade_spot_order_modification表, uuid与order的uuid一致
                origin = createSpotModifyOrder(order, CommonUtils.generateUUID(), status, order.getCtime());
            }

            String modifyId = CommonUtils.generateUUID();
            // 如果订单已经进入执行态，则需要检查冻结的资金量是否足够
            if (status == OrderStatus.EXECUTING) {
                BusinessExceptionEnum code = performModify(order, req.getQuantity(), req.getPrice(), modifyId);
                if (code != BusinessExceptionEnum.SUCCESS) {
                    throw new BusinessException(code);
                }
            } else {
                if (!isSufficientFunds(order, req.getQuantity(), req.getPrice())) {
                    throw new BusinessException(BusinessExceptionEnum.INSUFFICIENT_FUNDS);
                }
            }

            // 这里进行order的更新操作
            modifySpotOrder(order, req, status);
            TradeSpotOrderModification after = createSpotModifyOrder(order, modifyId, status, new Date());
            spotTransaction.insertModificationAndUpdateOrder(order, origin, after);
        } finally {
            if (!unlockOrderForUpdate(order.getUuid(), status)) {
                log.error("unlock order fail, order = {}", order);
            }
        }
        return order.getUuid();
    }

    @Override
    public String cancelOrder(SpotOrderCancel req) {
        TradeSpotOrder order = spotTransaction.cancelOrder(req);
        String cancelId = "cancel-" + order.getUuid();
        if (!performCancel(order, cancelId)) {
            throw new BusinessException(BusinessExceptionEnum.CANCEL_ORDER_FAIL);
        }
        if (req.getTerminator().isFromUser()) {
            pushService.cancelOrderOk(order.getUid(), true);
        }
        sensorsTraceService.spotStatusChange(order, null, BigDecimal.ZERO);
        return req.getOrderId();
    }

    @Override
    public boolean checkTriggerOrder(TradeSpotOrder order) {
        log.debug("checkTriggerOrder| orderId:{}, order:{}", order.getUuid(), order);
        OrderStatus status = OrderStatus.getByName(order.getStatus());
        String symbol = order.getSymbol();
        if (OrderType.isTriggerOrder(order.getType()) && status == OrderStatus.PRE_TRIGGER) {
            BigDecimal compPrice = CommonUtils.getMiddlePrice(symbol);
            if (orderRequest.isSatisfyTrigger(order.getTriggerPrice(), TriggerType.getByName(order.getTriggerCompare()),
                    compPrice)) {
                log.info("margin pre trigger -> executing, trigger price = {}, trigger type = {}, com price = {}", order.getTriggerPrice(),
                        order.getTriggerCompare(), compPrice);
                if (updateSpotOrderStatus(order, status, OrderStatus.PENDING)) {
                    performFreeze(order);
                    pushService.spotTriggerOk(order);
                    sensorsTraceService.spotStatusChange(order, null, BigDecimal.ZERO);
                }
            }
        }
        return true;
    }


    @Override
    public void checkAndFillOrder(TradeSpotOrder order, boolean isAsync) {
        log.info("checkAndFillOrder| orderId:{}, order:{}", order.getUuid(), order);
        // 下单前的价格检查机制
        if (OrderType.isLimitOrder(order.getType())) {
            Direction direct = Direction.getByName(order.getDirection());
            if (!orderRequest.isReachPrice(order.getSymbol(), direct, order.getPrice())) {
                return;
            }
        }

        //开始执行下单操作
        OrderStatus status = OrderStatus.getByName(order.getStatus());
        if (orderRequest.isConversionCoin(order.getSource())) {
            log.debug("checkAndFillOrder|isConversionCoin| orderId:{}, order:{}", order.getUuid(), order);
            if (updateSpotOrderStatus(order, status, OrderStatus.LOCKED)) {
                performConversion(order);
            }
        } else {
            log.debug("checkAndFillOrder|normal| orderId:{}, order:{}", order.getUuid(), order);
            if (status != OrderStatus.EXECUTING ||
                    !updateSpotOrderStatus(order, status, OrderStatus.LOCKED)) {
                return;
            }
            // 锁定成功后，开始执行下单及资金更新的操作
            String tradeId = CommonUtils.generateUUID();
            try {
                boolean isOk = performPlaceOrder(order, tradeId);
                if (!isOk && OrderStatus.isFinish(order.getStatus())) {
                    log.debug("checkAndFillOrder isFinish order:{}", order);
                    String cancelId = "finish-" + order.getUuid();
                    performCancel(order, cancelId);
                }
            } catch (Exception err) {
                AlarmLogUtil.alarm("performPlaceOrder order={} error={}", order, err.toString());
                order.setStatus(OrderStatus.EXCEPTION.getName());
                spotTransaction.updateOrder(order);
            }
        }
    }

    private boolean modifySpotOrder(TradeSpotOrder order, SpotOrderUpdateReq req, OrderStatus status) {
        // 修改是基于当前订单类型的，丢弃一些无效的提交字段
        if (CommonUtils.isPositive(req.getQuantity())) {
            order.setQuantity(req.getQuantity());
        }
        if (CommonUtils.isValidString(req.getNotes())) {
            order.setNotes(req.getNotes());
        }

        OrderType type = OrderType.getByName(order.getType());
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
        return true;
    }


    /**
     * 冻结资金并进行状态转换，返回true，状态会设置为EXECUTING
     *
     * @param order
     * @return
     */
    private void performFreeze(TradeSpotOrder order) {
        BusinessExceptionEnum code = BusinessExceptionEnum.SUCCESS;
        if (orderRequest.notNeedFreezeAsset(order.getSource())
                || orderRequest.isConversionCoin(order.getSource())) {
            order.setStatus(OrderStatus.EXECUTING.getName());

        } else {
            BigDecimal freezeQty = orderRequest.calcFreezeFunds(order, order.getQuantity(), order.getPrice());
            TradeSpotOrderReq sendReq = assetRequest.getSpotFreezeReq(order, freezeQty, order.getUuid());
            code = assetRequest.freezeFunds(sendReq);
            if (code == BusinessExceptionEnum.SUCCESS) {
                order.setLockAmount(freezeQty);
                order.setStatus(OrderStatus.EXECUTING.getName());
            } else {
                order.setStatus(OrderStatus.CANCELED.getName());
                order.setError(OrderError.TRADING_SPOT_ORDER_ERROR_FREEZE_ASSET.getName());
                if (code == BusinessExceptionEnum.UNEXPECTED_ERROR) {
                    //HTTP请求失败，异步查询请求状态
                    order.setLockAmount(freezeQty);
                    order.setStatus(OrderStatus.ASSET_PENDING.getName());
                    AlarmLogUtil.alarm("spot performFreeze 资金请求异常 转异步处理 order={} reqid={}", order, sendReq.getReqId());
                }
            }
        }

        spotTransaction.updateOrder(order);
        if (code != BusinessExceptionEnum.SUCCESS) {
            throw new BusinessException(code);
        }
    }

    /**
     * 根据成交的结果来更新订单信息，并及时通知资金模块更新资金
     *
     * @return 操作成功或失败
     */
    private boolean performUpdate(TradeSpotOrder order, CreateTradeRes resp, TradeTransaction trans) {
        if (orderRequest.notNeedUnFreezeAsset(order.getSource())
                || orderRequest.isConversionCoin(order.getSource())) {
            return true;
        }

        boolean result = false;
        if (CommonUtils.isPositive(resp.getFilled())) {
            TradeSpotReq tradeReq = assetRequest.getUpdateTradeReq(order, trans);
            int retCode = assetRequest.updateTradedResult(tradeReq);
            if (retCode == BusinessExceptionEnum.SUCCESS.getCode()) {
                if (OrderStatus.isFinish(order.getStatus())) {
                    order.setLockAmount(CommonUtils.ZERO_NUM);
                } else {
                    BigDecimal oldLock = order.getLockAmount();
                    BigDecimal traded = orderRequest.getTradedQuantity(order.getDirection(), resp);
                    order.setLockAmount(oldLock.subtract(traded));
                }
                result = true;
            } else {
                trans.setAssetStatus(AssetStatus.EXCEPTION.name());
                order.setError(OrderError.TRADING_SPOT_ORDER_ERROR_FREEZE_ASSET.getName());
            }
        }
        return result;
    }

    /**
     * 执行资金取消操作，并更新订单的状态和锁定资金
     *
     * @return 操作成功或失败
     */
    private boolean performCancel(TradeSpotOrder order, String cancelId) {
        boolean result = true;
        BigDecimal lockQty = order.getLockAmount();
        if (CommonUtils.isPositive(lockQty)) {
            TradeSpotOrderReq sendReq = assetRequest.getSpotFreezeReq(order, lockQty, cancelId);
            int retCode = assetRequest.cancelFreeze(sendReq);
            if (retCode == 0) {
                orderRequest.setSpotFinishStatus(order);
                order.setLockAmount(CommonUtils.ZERO_NUM);
            } else {
                order.setError(OrderError.TRADING_SPOT_ORDER_ERROR_UNFREEZE_ASSET.getName());
                result = false;
            }
        } else {
            BigDecimal quantityFilled = order.getQuantityFilled();
            if (CommonUtils.isPositive(quantityFilled)) {
                //PDT发单进度丢失 BUY单（price * quantityFilled 向上取整）可能LockAmount为0且已成交完进入此分支
                order.setStatus(OrderStatus.PART_CANCELED.getName());
            } else {
                order.setStatus(OrderStatus.CANCELED.getName());
            }
        }
        spotTransaction.updateOrder(order);
        return result;
    }

    /**
     * 处于执行态的订单，由于修改需要重新调整冻结资金量
     *
     * @return 带回状态码
     */
    private BusinessExceptionEnum performModify(TradeSpotOrder order, BigDecimal newQty,
                                                BigDecimal newPrice, String modifyId) {
        BusinessExceptionEnum code = BusinessExceptionEnum.SUCCESS;
        BigDecimal needLock = orderRequest.calcFreezeFunds(order, newQty, newPrice);
        BigDecimal diffQty = needLock.subtract(order.getLockAmount());
        if (diffQty.compareTo(CommonUtils.ZERO_NUM) == 0) {
            return code;
        }
        TradeSpotOrderReq sendReq = assetRequest.getSpotFreezeReq(order, diffQty.abs(), modifyId);
        if (diffQty.compareTo(CommonUtils.ZERO_NUM) > 0) {
            // 然后进行增量资金量的冻结
            code = assetRequest.freezeFunds(sendReq);
            if (code == BusinessExceptionEnum.SUCCESS) {
                order.setLockAmount(needLock);
            } else {
                if (code == BusinessExceptionEnum.UNEXPECTED_ERROR) {
                    alarmComponent.asyncAlarm(AlarmEnum.SPOT_PLACE_ORDER_ERROR, "spot 修改订单 资金请求异常 reqID=" + modifyId);
                }
                order.setError(OrderError.TRADING_SPOT_ORDER_ERROR_FREEZE_ASSET.getName());
            }
        } else {
            // 差异资金的取消冻结
            int retCode = assetRequest.cancelFreeze(sendReq);
            if (retCode == BusinessExceptionEnum.SUCCESS.getCode()) {
                order.setLockAmount(needLock);
            } else {
                order.setError(OrderError.TRADING_SPOT_ORDER_ERROR_UNFREEZE_ASSET.getName());
            }
        }
        return code;
    }

    /**
     * 主执行逻辑，包括下单/成交结果的检测，状态的更新以及写逐笔的成交记录
     *
     * @return 成功或失败
     */
    private boolean performPlaceOrder(TradeSpotOrder order, String tradeId) {
        boolean result = false;
        CreateTradeReq placeReq = orderRequest.getPlaceSpotOrder(order, tradeId);
        if (placeReq != null) {
            //1. PDT请求之前 先创建transOrder订单
            TradeTransaction transOrder = initTradeTransaction(order, placeReq);
            String oldTradeId = order.getTransId();
            if (StringUtils.isNotEmpty(oldTradeId)) {
                //说明此order 执行下单的请求异常
                tradeId = oldTradeId;
                TradeTransactionExample transactionExample = new TradeTransactionExample();
                transactionExample.createCriteria().andUuidEqualTo(oldTradeId);
                transOrder = tradeTransactionService.queryAllByTransId(oldTradeId);
            } else {
                tradeTransactionService.insert(transOrder);
            }
            //2. PDT下单
            CreateTradeRes resp = crexApiRequest.executeOrder(placeReq, TradeType.SPOT);
            //3. 根据请求情况 更新TradeTransaction、TradeSpotOrder状态
            if (resp.isSuccess()) {
                //TransId 清空
                order.setTransId("");
                //set 交易量 2 DB
                String quoteCoin = CommonUtils.getQuoteCoin(order.getSymbol());
                tradeAssetService.setUserAmountUsd2DB(order.getUid(), quoteCoin, resp.getQuoteFilled(), tradeId);
                result = true;

                BigDecimal tradeQty = resp.getFilled();
                BigDecimal quoteQty = resp.getQuoteFilled();
                log.info("executeOrder placeReq={} filled={}", placeReq, resp);
                if (CommonUtils.isPositive(tradeQty) || CommonUtils.isPositive(quoteQty)) {
                    orderRequest.calcAveragePrice(order, resp);
                    orderRequest.calcOrderStatus(order);
                    resetTradeTransaction(transOrder, order, placeReq, resp);
                    result = performUpdate(order, resp, transOrder);
                    spotTransaction.updateTransactionAndUpdateOrder(transOrder, order);
                    if (!result) {
                        alarmComponent.asyncAlarm(AlarmEnum.SPOT_PLACE_ORDER_ERROR,
                                "dospot 成交后更新资金信息失败 orderid = " + order.getUuid() + " transID = " + transOrder.getUuid());
                    }
                    buyCryptPush(order);
                    //notice | push
                    this.transactionNewNotice(transOrder, order);
                    if (SourceType.isFromUser(order.getSource()) || SourceType.TAKE_PROFIT_STOP_LOSS.getName().equals(order.getSource())) {
                        if (result) {
                            pushService.spotOrderTraded(order);
                            pushComponent.pushFundingEventMessage(FundingBehaviorEventMessage.buildSpotValue(transOrder));
                        }
                    }
                    sensorsTraceService.spotStatusChange(order, null, BigDecimal.ZERO);
                    return result;
                }
            }
            if (!resp.httpSuccess()) {
                //http请求失败 PdtStatus -》 PENDING
                // 记录transid 后续继续幂等使用
                boolean isSync = this.isSpotSyncOrder(order);
                if (isSync) {
                    alarmComponent.asyncAlarm(AlarmEnum.SPOT_PLACE_ORDER_ERROR, "同步单PDT请求异常,transId为：" + tradeId);
                    //同步订单给出最终状态
                    orderRequest.calcOrderStatus(order);
                    order.setTransId(tradeId);
                    order.setError(OrderError.TRADING_SPOT_ORDER_ERROR_CREX.getName());
                    transOrder.setPdtStatus(PdtStatus.PENDING.name());
                } else {
                    order.setTransId(tradeId);
                    order.setStatus(OrderStatus.EXECUTING.getName());
                    transOrder.setPdtStatus(PdtStatus.PENDING.name());
                }
                spotTransaction.updateOrder(order);
                tradeTransactionService.updateTransactionById(transOrder);
                sensorsTraceService.spotStatusChange(order, null, BigDecimal.ZERO);
                log.error("performPlaceOrder req = {}, resp = {}", placeReq, resp);
                return false;
            }

            if (resp.httpSuccess() && !resp.isSuccess() && !resp.isPending()) {
                // 业务失败 PdtStatus -》 EXCEPTION
                // 记录transid 后续继续幂等使用
                boolean isSync = this.isSpotSyncOrder(order);
                if (isSync) {
                    //同步订单给出最终状态
                    orderRequest.calcOrderStatus(order);
                    order.setTransId("");
                    order.setError(OrderError.TRADING_SPOT_ORDER_ERROR_CREX.getName());
                    transOrder.setPdtStatus(PdtStatus.EXCEPTION.name());
                } else {
                    order.setTransId("");
                    order.setStatus(OrderStatus.EXECUTING.getName());
                    transOrder.setPdtStatus(PdtStatus.EXCEPTION.name());
                }
                spotTransaction.updateOrder(order);
                tradeTransactionService.updateTransactionById(transOrder);
                sensorsTraceService.spotStatusChange(order, null, BigDecimal.ZERO);
                log.error("performPlaceOrder req = {}, resp = {}", placeReq, resp);
                return false;
            }

            if (resp.isPending()) {
                // 订单PENDING状态 PdtStatus -》 PENDING
                boolean isSync = this.isSpotSyncOrder(order);
                if (isSync) {
                    alarmComponent.asyncAlarm(AlarmEnum.SPOT_PLACE_ORDER_ERROR, "同步单PDT返回PENDING,transId为：" + tradeId);
                    //同步订单给出最终状态
                    orderRequest.calcOrderStatus(order);
                    order.setTransId(tradeId);
                    order.setError(OrderError.TRADING_SPOT_ORDER_ERROR_CREX.getName());
                    transOrder.setPdtStatus(PdtStatus.PENDING.name());
                } else {
                    order.setTransId(tradeId);
                    order.setStatus(OrderStatus.EXECUTING.getName());
                    transOrder.setPdtStatus(PdtStatus.PENDING.name());
                }
                spotTransaction.updateOrder(order);
                tradeTransactionService.updateTransactionById(transOrder);
                sensorsTraceService.spotStatusChange(order, null, BigDecimal.ZERO);
                log.error("performPlaceOrder req = {}, resp = {}", placeReq, resp);
                return false;
            }
        }
        orderRequest.calcOrderStatus(order);
        spotTransaction.updateOrder(order);
        sensorsTraceService.spotStatusChange(order, null, BigDecimal.ZERO);
        return result;
    }

    private TradeTransaction resetTradeTransaction(TradeTransaction trade, TradeSpotOrder order, CreateTradeReq req, CreateTradeRes resp) {
        trade.setUuid(req.getTradeId());
        trade.setUid(order.getUid());
        trade.setOrderId(order.getUuid());
        trade.setType(Constants.SPOT_TYPE);
        trade.setOrderType(order.getType());
        trade.setSymbol(order.getSymbol());
        trade.setDirection(order.getDirection());
        trade.setBaseQuantity(resp.getFilled());
        trade.setQuoteQuantity(resp.getQuoteFilled());
        trade.setPrice(resp.getFilledPrice());
        trade.setSource(order.getSource());
        trade.setAssetStatus(AssetStatus.COMPLETED.name());
        trade.setPdtStatus(PdtStatus.COMPLETED.name());
        BigDecimal fee = orderRequest.calcMiddleFee(order, resp);
        trade.setFee(CommonUtils.convertData(fee));
        trade.setFeeCoin(order.getFeeCoin());
        Date date = new Date();
        trade.setMtime(date);
        return trade;
    }

    private TradeTransaction initTradeTransaction(TradeSpotOrder order, CreateTradeReq req) {
        TradeTransaction trade = new TradeTransaction();
        trade.setUuid(req.getTradeId());
        trade.setUid(order.getUid());
        trade.setOrderId(order.getUuid());
        trade.setType(Constants.SPOT_TYPE);
        trade.setOrderType(order.getType());
        trade.setSymbol(order.getSymbol());
        trade.setDirection(order.getDirection());
        trade.setBaseQuantity(BigDecimal.ZERO);
        trade.setQuoteQuantity(BigDecimal.ZERO);
        trade.setPrice(BigDecimal.ZERO);
        trade.setSource(order.getSource());
        trade.setAssetStatus(AssetStatus.PENDING.name());
        trade.setPdtStatus(PdtStatus.PENDING.name());
        trade.setFee(BigDecimal.ZERO);
        trade.setFeeCoin(order.getFeeCoin());
        Date date = new Date();
        trade.setCtime(date);
        trade.setMtime(date);
        return trade;
    }

    private void buyCryptPush(TradeSpotOrder order) {
        //BUY_CRYPTOCURRENCY_CONVERSION 来源且完全成交时进行推送结果
        if (SourceType.BUY_CRYPTOCURRENCY_CONVERSION.getName().equals(order.getSource()) && OrderStatus.COMPLETED.getName().equals(order.getStatus())) {
            //BUY_CRYPTOCURRENCY_CONVERSION 始终为 quote 发单 的 买单
            BuyCryptRes res = BuyCryptRes.from(order);
            pushComponent.pushBuyCryptSuccessResult(res);
        }
    }

    /**
     * 支持币种转换的接口操作，同步更新状态
     *
     * @return 成功或失败
     */
    private boolean performConversion(TradeSpotOrder order) {
        boolean result = true;
        String tradeId = CommonUtils.generateUUID();
        if (performPlaceOrder(order, tradeId)) {
            TradeCurrencyConversion sendReq = orderRequest.getConversionReq(order, tradeId);
            int retCode = assetRequest.conversionCoin(sendReq);
            if (retCode != BusinessExceptionEnum.SUCCESS.getCode()) {
                result = false;
                log.error("conversionCoin sendReq={} code={}", sendReq.toString(), retCode);
                order.setError(OrderError.TRADING_SPOT_ORDER_ERROR_CONVERSION_ASSET.getName());
            }
            order.setStatus(OrderStatus.COMPLETED.getName());
        } else {
            orderRequest.setSpotFinishStatus(order);
        }
        spotTransaction.updateOrder(order);
        return result;
    }

    //-----------------------对外的接口查询部分-------------------
    @Override
    public List<TradeSpotOrder> listAllActiveTriggerOrders() {
        TradeSpotOrderExample example = new TradeSpotOrderExample();
        TradeSpotOrderExample.Criteria criteria = example.createCriteria();
        criteria.andTypeIn(OrderType.TRIGGER_TYPE);
        criteria.andStatusEqualTo(OrderStatus.PRE_TRIGGER.getName());
        return tradeSpotOrderMapper.selectByExample(example);
    }

    @Override
    public boolean isSpotSyncOrder(TradeSpotOrder order) {
        OrderType type = OrderType.getByName(order.getType());
        TradeStrategy strategy = TradeStrategy.getByName(order.getStrategy());
        SourceType source = SourceType.getByName(order.getSource());
        return CommonUtils.isSyncOrder(type, strategy, source);
    }

    @Override
    public List<TradeSpotOrder> fetchAsyncExecuteOrders() {
        TradeSpotOrderExample example = new TradeSpotOrderExample();
        TradeSpotOrderExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo(OrderStatus.EXECUTING.getName());
        return tradeSpotOrderMapper.selectByExample(example);
    }

    @Override
    public List<OrderRes> listAllActiveOrders(List<String> uids) {
        TradeSpotOrderExample example = new TradeSpotOrderExample();
        TradeSpotOrderExample.Criteria criteria = example.createCriteria();

        criteria.andStatusIn(OrderStatus.ACTIVE_STATUS);
        criteria.andTypeIn(Arrays.asList(OrderType.LIMIT.getName(), OrderType.STOP_LIMIT.getName()));
        if (ListUtil.isNotEmpty(uids)) {
            criteria.andUidIn(uids);
        }
        List<TradeSpotOrder> tradeSpotOrders = tradeSpotOrderMapper.selectByExample(example);
        return tradeSpotOrderMapStruct.tradeSpotOrders2OrderRes(tradeSpotOrders);
    }

    @Override
    public PageResult<SpotOrderInfoRes> orderActive(SpotOrderActiveReq req, String uid) {
        PageHelper.startPage(req.getPage(), req.getPageSize(), true);
        TradeSpotOrderExample example = new TradeSpotOrderExample();
        TradeSpotOrderExample.Criteria criteria = example.createCriteria();
        example.setOrderByClause("CASE SYMBOL WHEN '" + req.getSymbol() + "' THEN 1 ELSE 10 END ASC ,CTIME DESC");
        if (req.getOnlyCurrent()) {
            criteria.andSymbolEqualTo(req.getSymbol());
        }
        //过滤掉BUY_CRYPTOCURRENCY_CONVERSION来源
        criteria.andSourceNotEqualTo(SourceType.BUY_CRYPTOCURRENCY_CONVERSION.getName());
        criteria.andUidEqualTo(uid);
        criteria.andStatusIn(OrderStatus.ACTIVE_STATUS);
        return getSpotOrderInfoResPageResult(example);
    }

    @Override
    public PageResult<SpotOrderInfoRes> orderHistory(SpotOrderHistoryReq req, String uid) {
        PageHelper.startPage(req.getPage(), req.getPageSize(), true);
        TradeSpotOrderExample example = new TradeSpotOrderExample();
        TradeSpotOrderExample.Criteria criteria = example.createCriteria();
        criteria.andUidEqualTo(uid);
        //过滤掉BUY_CRYPTOCURRENCY_CONVERSION来源
        criteria.andSourceNotEqualTo(SourceType.BUY_CRYPTOCURRENCY_CONVERSION.getName());
        String sortType;
        String direct = TradeConstants.ORDER_BY_ASC.equals(req.getOrderMode()) ?
                TradeConstants.ORDER_BY_ASC : TradeConstants.ORDER_BY_DESC;
        if (req.getSymbol() == null && TradeConstants.ORDER_ITEM_SYMBOL.equals(req.getOrderItem())) {
            sortType = TradeConstants.ORDER_ITEM_SYMBOL + " " + direct;
        } else {
            if (TradeConstants.ORDER_ITEM_CTIME.equals(req.getOrderItem())) {
                sortType = TradeConstants.ORDER_ITEM_CTIME + " " + direct;
            } else {
                sortType = TradeConstants.ORDER_ITEM_MTIME + " " + direct;
            }
        }
        example.setOrderByClause(sortType);

        if (req.getSymbol() != null) {
            criteria.andSymbolEqualTo(req.getSymbol());
        }
        if (!Objects.isNull(req.getDirection())) {
            criteria.andDirectionEqualTo(req.getDirection());
        }
        if (!Objects.isNull(req.getStatus())) {
            if (Objects.equals(req.getStatus(), OrderStatus.APP_QUERY_STATUS_CANCELED)) {
                criteria.andStatusIn(OrderStatus.APP_CANCELED_STATUS);
            } else if (Objects.equals(req.getStatus(), OrderStatus.APP_QUERY_STATUS_COMPLETED)) {
                criteria.andStatusIn(OrderStatus.APP_COMPLETED_STATUS);
            }
        } else {
            criteria.andStatusIn(OrderStatus.HISTORY_STATUS);
        }

        if (req.getStartTime() != null && req.getEndTime() != null) {
            Date startTime = CommonUtils.getNowTime(req.getStartTime());
            Date endTime = CommonUtils.getNowTime(req.getEndTime());
            if (Objects.equals(req.getOrderItem(), TradeConstants.ORDER_ITEM_CTIME)) {
                criteria.andCtimeBetween(startTime, endTime);
            } else {
                criteria.andMtimeBetween(startTime, endTime);
            }
        }
        return getSpotOrderInfoResPageResult(example);
    }

    private PageResult<SpotOrderInfoRes> getSpotOrderInfoResPageResult(TradeSpotOrderExample example) {
        List<TradeSpotOrder> tradeSpotOrders = tradeSpotOrderMapper.selectByExample(example);
        PageInfo<TradeSpotOrder> pageInfo = new PageInfo<>(tradeSpotOrders);
        List<SpotOrderInfoRes> infoRes = tradeSpotOrderMapStruct.tradeSpotOrders2SpotOrderInfoRes(pageInfo.getList());
        return PageResult.generate(pageInfo.getTotal(), pageInfo.getPageNum(), pageInfo.getPageSize(), infoRes);
    }


    //-----------------------数据库交互的部分-------------------

    private TradeSpotOrder createSpotOrder(SpotOrderPlace req) {
        TradeSpotOrder order = new TradeSpotOrder();
        // 借贷清算会指定orderId, 保证幂等
        if (req.getOrderId() != null) {
            order.setUuid(req.getOrderId());
        } else {
            order.setUuid(CommonUtils.generateUUID());
        }
        order.setUid(req.getUid());

        order.setType(req.getType().getName());
        if (req.getType().isLimitOrder()) {
            order.setStrategy(req.getStrategy().getName());
            order.setPrice(req.getPrice());
        }
        order.setDirection(req.getDirection().getName());
        order.setSymbol(req.getSymbol());
        if (req.getType().isTriggerOrder()) {
            order.setTriggerPrice(req.getTriggerPrice());
            // 检查设置的触发价格是否太接近当前价
//            if (!orderRequest.checkPriceOffset(req.getSymbol(),
//                    req.getTriggerCompare(), req.getTriggerPrice())) {
//                return null;
//            }
            order.setTriggerCompare(req.getTriggerCompare().getName());
        }

        order.setIsQuote(req.getIsQuote());
        //现货精度精度 by WT 20211028
        order.setQuantity(req.getQuantity().setScale(Constants.DEFAULT_PRECISION, RoundingMode.DOWN));
        order.setQuantityFilled(BigDecimal.ZERO);

        boolean isTrigger = OrderType.isTriggerOrder(order.getType());
        String status = OrderStatus.getInitStatus(isTrigger);
        order.setStatus(status);
        order.setLockAmount(BigDecimal.ZERO);
        order.setNotes(req.getNotes());

        if (req.getSource() != null) {
            order.setSource(req.getSource().getName());
        } else {
            order.setSource(SourceType.PLACED_BY_CLIENT.getName());
        }
        if (req.getDirection() == Direction.BUY) {
            order.setFeeCoin(CommonUtils.getBaseCoin(req.getSymbol()));
        } else {
            order.setFeeCoin(CommonUtils.getQuoteCoin(req.getSymbol()));
        }

        // 初始化成交信息部分
        order.setCtime(CommonUtils.getNowTime());
        order.setMtime(CommonUtils.getNowTime());
        order.setFilledPrice(CommonUtils.ZERO_NUM);
        order.setQuantityFilled(CommonUtils.ZERO_NUM);
        order.setAmountFilled(CommonUtils.ZERO_NUM);
        order.setFee(CommonUtils.ZERO_NUM);
        return order;
    }

    private boolean updateSpotOrderStatus(TradeSpotOrder order, OrderStatus prev, OrderStatus next) {
        order.setMtime(CommonUtils.getNowTime());
        order.setStatus(next.getName());
        TradeSpotOrderExample example = new TradeSpotOrderExample();
        TradeSpotOrderExample.Criteria criteria = example.createCriteria();
        criteria.andUuidEqualTo(order.getUuid());
        criteria.andStatusEqualTo(prev.getName());
        if (tradeSpotOrderMapper.updateByExampleSelective(order, example) == 0) {
            log.error("convert spot status fail. order={}", order.toString());
            return false;
        }
        return true;
    }

    private TradeSpotOrderModification createSpotModifyOrder(TradeSpotOrder order, String modifyId, OrderStatus status, Date modifyTime) {
        TradeSpotOrderModification modify = new TradeSpotOrderModification();
        modify.setUuid(modifyId);
        modify.setOrderId(order.getUuid());
        modify.setQuantity(order.getQuantity());
        modify.setLockAmount(order.getLockAmount());
        modify.setPrice(order.getPrice());
        modify.setTriggerPrice(order.getTriggerPrice());
        modify.setTriggerCompare(order.getTriggerCompare());
        modify.setNotes(order.getNotes());
        modify.setCurrentStatus(status.getName());
        modify.setCtime(modifyTime);
        return modify;
    }

    private TradeTransaction createTradeTransaction(TradeSpotOrder order, CreateTradeReq req, CreateTradeRes resp) {
        TradeTransaction trade = new TradeTransaction();
        trade.setUuid(req.getTradeId());
        trade.setUid(order.getUid());
        trade.setOrderId(order.getUuid());
        trade.setType(Constants.SPOT_TYPE);
        trade.setOrderType(order.getType());
        trade.setSymbol(order.getSymbol());
        trade.setDirection(order.getDirection());
        trade.setBaseQuantity(resp.getFilled());
        trade.setQuoteQuantity(resp.getQuoteFilled());
        trade.setPrice(resp.getFilledPrice());
        trade.setSource(order.getSource());
        trade.setAssetStatus(AssetStatus.COMPLETED.name());
        BigDecimal fee = orderRequest.calcMiddleFee(order, resp);
        trade.setFee(CommonUtils.convertData(fee));
        trade.setFeeCoin(order.getFeeCoin());
        Date date = new Date();
        trade.setCtime(date);
        trade.setMtime(date);
        return trade;
    }

    @Override
    public TradeSpotOrder querySpotOrderById(String orderId, String userId) {
        TradeSpotOrderExample example = new TradeSpotOrderExample();
        TradeSpotOrderExample.Criteria criteria = example.createCriteria();
        criteria.andUuidEqualTo(orderId);
        criteria.andUidEqualTo(userId);
        List<TradeSpotOrder> orderList = tradeSpotOrderMapper.selectByExample(example);
        if (!orderList.isEmpty()) {
            return orderList.get(0);
        }
        return null;
    }

    @Override
    public PageResult<AmpSpotRes> spotHistoryForAmp(AmpSpotReq req) {
        PageHelper.startPage(req.getPage(), req.getPageSize(), true);
        TradeSpotOrderExample example = new TradeSpotOrderExample();
        //TODO排序
        example.setOrderByClause("CASE STATUS " +
                "WHEN 'PRE_TRIGGER' THEN 1 " +
                "WHEN 'PENDING' THEN 1 " +
                " WHEN 'EXECUTING' THEN 3  " +
                " WHEN 'LOCKED' THEN 3 " +
                " WHEN 'COMPLETED' THEN 4  " +
                " WHEN 'CANCELING' THEN 5  " +
                " WHEN 'PART_CANCELED' THEN 6  " +
                " WHEN 'CANCELED' THEN 7  " +
                " WHEN 'EXCEPTION' THEN 8  " +
                "ELSE 10 END ASC ,MTIME DESC,CTIME DESC");
        TradeSpotOrderExample.Criteria criteria = example.createCriteria();
        criteria.andUidEqualTo(req.getUid());
        if (StringUtils.isNotBlank(req.getOrderId())) {
            criteria.andUuidEqualTo(req.getOrderId());
        }
        if (StringUtils.isNotBlank(req.getType())) {
            criteria.andTypeEqualTo(req.getType());
        }
        if (StringUtils.isNotBlank(req.getStrategy())) {
            criteria.andStrategyEqualTo(req.getStrategy());
        }
        if (StringUtils.isNotBlank(req.getSymbol())) {
            criteria.andSymbolEqualTo(req.getSymbol());
        }
        if (StringUtils.isNotBlank(req.getDirection())) {
            criteria.andDirectionEqualTo(req.getDirection());
        }
        if (StringUtils.isNotBlank(req.getStatus())) {
            List<String> statusList = AmpOrderStatus.getByCode(req.getStatus()).stream().map(AmpOrderStatus::getName).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(statusList)) {
                criteria.andStatusIn(AmpOrderStatus.getByCode(req.getStatus()).stream().map(AmpOrderStatus::getName).collect(Collectors.toList()));
            }
        }
        if (req.getStartTime() != null) {
            Date startTime = CommonUtils.getNowTime(req.getStartTime());
            criteria.andMtimeGreaterThan(startTime);
        }
        if (req.getEndTime() != null) {
            Date endTime = CommonUtils.getNowTime(req.getEndTime());
            criteria.andMtimeLessThan(endTime);
        }

        List<TradeSpotOrder> orderList = tradeSpotOrderMapper.selectByExample(example);
        PageInfo<TradeSpotOrder> pageInfo = new PageInfo<>(orderList);
        List<AmpSpotRes> ampSpotRes = tradeSpotOrderMapStruct.tradeSpotOrders2AmpSpotResList(pageInfo.getList());
        return PageResult.generate(pageInfo.getTotal(), pageInfo.getPageNum(), pageInfo.getPageSize(), ampSpotRes);
    }

    @Override
    public List<TradeSpotOrder> getAssetExceptionOrder() {
        TradeSpotOrderExample example = new TradeSpotOrderExample();
        TradeSpotOrderExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo(OrderStatus.ASSET_PENDING.getName());
        return tradeSpotOrderMapper.selectByExample(example);
    }

    @Override
    public void checkAssetAndUpdateStatus(TradeSpotOrder order) {
        GetReqIdStatusReq req = new GetReqIdStatusReq();
        req.setReqId(order.getUuid());
        log.info("checkAssetAndUpdateStatus reqId:{} order={}", req.getReqId(), order);
        com.google.backend.common.web.Response<Integer> reqIdStatus = assetInfoClient.getReqIdStatus(req);
        if (reqIdStatus.getCode() != BusinessExceptionEnum.SUCCESS.getCode()) {
            long time = order.getCtime().getTime();
            if (System.currentTimeMillis() - time > Duration.ofSeconds(60).toMillis()) {
                //大于60S终态更新，否则不处理下次继续
                order.setLockAmount(CommonUtils.ZERO_NUM);
                boolean b = updateSpotOrderStatus(order, OrderStatus.ASSET_PENDING, OrderStatus.CANCELED);
                if (!b) {
                    AlarmLogUtil.alarm("checkAssetAndUpdateStatus 更新订单状态失败，before= {} after= {} ,order={}",
                            OrderStatus.ASSET_PENDING, OrderStatus.CANCELED, order);
                }
            }
        } else {
            if (reqIdStatus.getData() == 0) {
                //资金请求成功,需要取消订单
                String cancelId = "cancel-" + order.getUuid();
                performCancel(order, cancelId);
            } else {
                boolean b = updateSpotOrderStatus(order, OrderStatus.ASSET_PENDING, OrderStatus.CANCELED);
                if (!b) {
                    AlarmLogUtil.alarm("checkAssetAndUpdateStatus 更新订单状态失败，before= {} after= {} ,order={}",
                            OrderStatus.ASSET_PENDING, OrderStatus.CANCELED, order);
                }
            }
        }
    }

    @Override
    public PageResult<AceUpSpotRes> spotHistoryForAceUp(AceUpSpotReq req) {
        TradeSpotOrderExample example = new TradeSpotOrderExample();
        //TODO排序
        example.setOrderByClause("CTIME DESC");
        TradeSpotOrderExample.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(req.getUid())) {
            criteria.andUidEqualTo(req.getUid());
        }
        if (StringUtils.isNotBlank(req.getOrderId())) {
            criteria.andUuidEqualTo(req.getOrderId());
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
        List<TradeSpotOrder> orderList = tradeSpotOrderMapper.selectByExample(example);
        PageInfo<TradeSpotOrder> pageInfo = new PageInfo<>(orderList);
        List<AceUpSpotRes> aceUpSpotRes = tradeSpotOrderMapStruct.tradeSpotOrders2AceUpSpotResList(pageInfo.getList());
        return PageResult.generate(pageInfo.getTotal(), pageInfo.getPageNum(), pageInfo.getPageSize(), aceUpSpotRes);
    }

    @Override
    public List<AceUpSpotTransRes> spotTransactionListForAceUp(String orderId) {
        List<TradeTransaction> tradeTransactionList = tradeTransactionService.queryAllByOrderId(orderId);
        return tradeSpotOrderMapStruct.tradeTransactions2AceUpSpotTransResList(tradeTransactionList);
    }


    private BigDecimal getUserFeeRate(String userId) {
        TradeFeeConfig cfg = tradeFeeConfigMapper.selectFeeConfigByUid(userId);
        return cfg.getSpotFeeRate();
    }

    private boolean isEmptyAddModify(String orderId) {
        TradeSpotOrderModificationExample example = new TradeSpotOrderModificationExample();
        example.createCriteria().andOrderIdEqualTo(orderId);
        List<TradeSpotOrderModification> orderList = spotModifyMapper.selectByExample(example);
        return orderList.isEmpty();
    }

    //-----------------------业务逻辑的计算部分-------------------
    private String setResponseFail(TradeSpotOrder order, BusinessExceptionEnum error) {
        String info = order != null ? order.toString() : "null";
        log.warn("update order={} fail. error={}", info, error.getMsg());
        return error.getMsg();
    }

    private SpotOrderPlace getCheckRequest(SpotOrderUpdateReq req, TradeSpotOrder order) {
        SpotOrderPlace place = new SpotOrderPlace();
        place.setSymbol(order.getSymbol());
        place.setType(OrderType.getByName(order.getType()));
        place.setStrategy(TradeStrategy.getByName(order.getStrategy()));
        place.setSource(SourceType.getByName(order.getSource()));
        place.setIsQuote(order.getIsQuote());
        place.setTriggerPrice(req.getTriggerPrice());
        place.setPrice(req.getPrice());
        place.setQuantity(req.getQuantity());
        place.setDirection(Direction.getByName(order.getDirection()));
        return place;
    }

    private boolean checkPriceAndQuantity(SpotOrderPlace req) {
        // 下单量上的检查流程
        if (req.getSource().isFromUser()) {
            boolean isFok = (req.getStrategy() == TradeStrategy.FOK);
            orderRequest.validateQuantity(req.getQuantity(), req.getSymbol(),
                    isFok, req.getIsQuote(), req.getPrice(), false);
        }

        // 价格上的检查流程
        orderRequest.validatePricePrecision(req.getType(),
                req.getSymbol(), req.getPrice(), req.getTriggerPrice());
        return true;
    }

    // 检查条件单的方向是否合理
    private boolean isSideConflict(TradeSpotOrder order, TriggerType value) {
        Direction side = Direction.getByName(order.getDirection());
        boolean isGreat = TriggerType.isGreater(value);
        return (side == Direction.BUY && isGreat)
                || (side == Direction.SELL && !isGreat);
    }

    private boolean isSufficientFunds(TradeSpotOrder order, BigDecimal newQty, BigDecimal newPrice) {
        BigDecimal needQty = orderRequest.calcFreezeFunds(order, newQty, newPrice);
        String payCoin = CommonUtils.getPaymentCoin(order.getSymbol(), order.getDirection());
        BigDecimal available = assetRequest.queryAvailableByCoin(order.getUid(), payCoin);
        return available.compareTo(needQty) > 0;
    }

    @Override
    public SpotOrderPlaceRes getOrderResult(TradeSpotOrder order) {
        SpotOrderPlaceRes res = new SpotOrderPlaceRes();
        res.setOrderId(order.getUuid());
        res.setFilledPrice(order.getFilledPrice());
        String status = OrderStatus.getFeignStatus(order.getStatus()).getCode();
        res.setOriginalStatus(order.getStatus());
        res.setStatus(status);
        BigDecimal filledQty = order.getQuantityFilled();
        BigDecimal baseQty = order.getAmountFilled();
        if (order.getIsQuote()) {
            res.setBaseFilled(baseQty);
            res.setQuoteFilled(filledQty);
        } else {
            res.setBaseFilled(filledQty);
            res.setQuoteFilled(baseQty);
        }
        res.setQuote(order.getIsQuote());
        res.setQuantityFilled(order.getQuantityFilled());
        res.setAmountFilled(order.getAmountFilled());
        res.setCtime(order.getCtime());
        return res;
    }


    /**
     * 获取现货活跃非系统订单
     *
     * @param uid
     * @return
     */
    @Override
    public List<TradeSpotOrder> getTradeSpotOrders(String uid) {
        TradeSpotOrderExample example = new TradeSpotOrderExample();
        TradeSpotOrderExample.Criteria criteria = example.createCriteria();
        criteria.andUidEqualTo(uid);
        criteria.andStatusIn(OrderStatus.ACTIVE_STATUS);
        //非系统单
        criteria.andSourceIn(SourceType.CLIENT_ORDER);
        return tradeSpotOrderMapper.selectByExample(example);
    }

    /**
     * 查询所有现货订单并取消
     *
     * @param uid
     */
    @Override
    public int cancelAllNotSystemOrders(String uid) {
        //查询所有现货订单并取消
        List<TradeSpotOrder> list = this.getTradeSpotOrders(uid);
        if (ListUtil.isNotEmpty(list)) {
            for (TradeSpotOrder order : list) {
                SpotOrderCancel cancelReq = new SpotOrderCancel();
                cancelReq.setOrderId(order.getUuid());
                cancelReq.setTerminator(TradeTerminator.RISK);
                this.cancelOrder(cancelReq);
            }
        }
        return list.size();
    }

    @Override
    public void liquidBalance(String uid) {

        //非USD负余额资产
        List<Balance> balances = assetRequest.negativeBalanceExcludingUSD(uid);
        if (ListUtil.isNotEmpty(balances)) {
            balances.forEach(balance -> {
                String baseCoin = balance.getCoin().toUpperCase();
                String symbol = baseCoin + Constants.BASE_QUOTE;
                int baseIssueQuantity = CoinDomain.nonNullGet(baseCoin).getCommonConfig().getBaseIssueQuantity();
                BigDecimal quantity = balance.getBalance().abs().setScale(baseIssueQuantity, RoundingMode.UP);
                //BigDecimal liquid = LiquidUtil.actLiquid(quantity,symbol); 无需预留buffer
                log.info("liquidBalance| symbol:{}, quantity:{}, uid:{}", symbol, quantity, uid);
                this.placeOrderUntilAllComplete(uid, symbol, quantity, Direction.BUY, false, SourceType.LIQUIDATION);
            });
        }
        //平负USD
        this.flatUsdAsset(uid);
    }


    /**
     * 下单直至全部成交
     *
     * @param symbol
     * @param quantity
     * @return SpotOrderPlaceRes, 只有QuantityFilled和AmountFilled有值
     */
    @Override
    public SpotOrderPlaceRes placeOrderUntilAllComplete(String uid, String symbol, BigDecimal quantity, Direction direction, Boolean isQuote, SourceType source) {
        log.info("placeUntilAllComplete| uid:{}, symbol:{}, direction:{}, quantity:{}, source:{}", uid, symbol, direction, quantity, source);
        quantity = quantity.setScale(Constants.DEFAULT_PRECISION, RoundingMode.DOWN);
        int count = 0;
        BigDecimal quantityFilled = CommonUtils.ZERO_NUM;
        BigDecimal amountFilled = CommonUtils.ZERO_NUM;
        while (count < maxPlaceNum && CommonUtils.isPositive(quantity)) {
            count += 1;
            //下现货订单填平负资产, pnl转换
            SpotOrderPlace req = SpotOrderPlace.builder()
                    .symbol(symbol)
                    .direction(direction)
                    .isQuote(isQuote)
                    .quantity(quantity)
                    .source(source)
                    .type(OrderType.MARKET)
                    .uid(uid)
                    .build();
            SpotOrderPlaceRes spotOrderPlaceRes = this.placeOrder(req);
            if (!OrderStatus.COMPLETED.getName().equals(spotOrderPlaceRes.getStatus())) {
                log.warn("placeOrderUntilAllComplete is not completed spotOrderPlaceRes:{}", spotOrderPlaceRes);
            }
            quantity = quantity.subtract(spotOrderPlaceRes.getQuantityFilled());
            quantityFilled = quantityFilled.add(spotOrderPlaceRes.getQuantityFilled());
            amountFilled = amountFilled.add(spotOrderPlaceRes.getAmountFilled());
        }
        SpotOrderPlaceRes res = new SpotOrderPlaceRes();
        res.setQuantityFilled(quantityFilled);
        res.setAmountFilled(amountFilled);
        return res;
    }


    /**
     * 清算现货
     *
     * @param uid
     */
    @Override
    public void liquidSpot(String uid) {
        //若ABS（USD负余额） >  ∑ CCY币种现货锁定金额之和，则释放所有现货锁定，并执行「（1）清算资产余额」流程；结束后进入「（3）清算理财锁定」流程
        //若ABS（USD负余额） <=  ∑ CCY币种现货锁定金额之和，则按照（USDT、BTC、ETH…..其余按照币种名字字母排序）的顺序对释放现货锁定，直至临界值的CCYi 现货锁定按照订单进行撤单释放；
        //如临界值CCYi锁定资产对应多笔现货订单，按照锁定金额优先撤单，锁定金额小的订单先撤单；直至完成；
        //现货锁定与usd比较
        BigDecimal b = assetRequest.queryAvailableByCoin(uid, Constants.BASE_COIN);
        BigDecimal base = b.abs();
        if (BigDecimal.ZERO.compareTo(b) <= 0) {
            return;
        }
        //获取所有订单
        List<TradeSpotOrder> tradeSpotOrders = this.getLockedOrdersNotBlocked(uid);

        //按lockCoin排序,lockedAmount从小到大
        List<TradeSpotOrder> list = tradeSpotOrders.stream()
                .sorted(Comparator.comparing(SpotUtil::lockCoin, (l1, l2) -> PriorityCoinWithUSD.coinCompare(l1, l2)).thenComparing(TradeSpotOrder::getLockAmount)).collect(Collectors.toList());
        if (ListUtil.isNotEmpty(list)) {
            BigDecimal sum = BigDecimal.ZERO;
            for (TradeSpotOrder item : list) {
                SpotOrderCancel cancelReq = new SpotOrderCancel();
                cancelReq.setOrderId(item.getUuid());
                cancelReq.setTerminator(TradeTerminator.RISK);
                this.cancelOrder(cancelReq);
                sum = sum.add(item.getLockAmount().multiply(SymbolDomain.nonNullGet(SpotUtil.lockCoin(item) + Constants.BASE_QUOTE).midPrice()));
                if (sum.compareTo(base) > 0) {
                    break;
                }
            }
        }
        //释放现货锁定到账户余额后会按照「（1）清算资产余额」对USD进行平负余额
        this.liquidBalance(uid);
    }

    /**
     * 查询所有非LOCKED有锁定资产的订单客户
     *
     * @param uid
     * @return
     */
    private List<TradeSpotOrder> getLockedOrdersNotBlocked(String uid) {
        TradeSpotOrderExample example = new TradeSpotOrderExample();
        example.createCriteria().andUidEqualTo(uid)
                .andStatusIn(OrderStatus.EXECUTE_STATUS)
                .andSourceIn(SourceType.CLIENT_ORDER);
        List<TradeSpotOrder> orderList = tradeSpotOrderMapper.selectByExample(example);
        return orderList;

    }


    @Override
    public List<TradeSpotOrderModification> orderModifications(String orderId) {
        PageHelper.startPage(1, 100);
        TradeSpotOrderModificationExample example = new TradeSpotOrderModificationExample();
        example.setOrderByClause("CTIME ASC");
        TradeSpotOrderModificationExample.Criteria criteria = example.createCriteria();
        criteria.andOrderIdEqualTo(orderId);
        List<TradeSpotOrderModification> list = spotModifyMapper.selectByExample(example);
        PageInfo<TradeSpotOrderModification> pageInfo = new PageInfo<>(list);
        return pageInfo.getList();
    }


    /**
     * if USD<0 ,平USD负资产
     *
     * @param uid
     */
    private void flatUsdAsset(String uid) {

        List<Balance> balances = assetRequest.positiveBalanceExcludingUSD(uid);
        if (ListUtil.isEmpty(balances)) {
            return;
        }
        for (Balance balance : balances) {
            String symbol = balance.getCoin().toUpperCase() + Constants.BASE_QUOTE;
            BigDecimal quantity = balance.getBalance();

            BigDecimal usdBalance = assetRequest.queryAvailableByCoin(uid, Constants.BASE_COIN);
            //下现货订卖单
            if (usdBalance.compareTo(BigDecimal.ZERO) >= 0) {
                return;
            }
            BigDecimal liquidUSD = LiquidUtil.actLiquid(usdBalance);
            log.info("flat usd asset, usd balance = {}, need liquid usd = {}, balances = {}", usdBalance, liquidUSD, balances);

            BigDecimal price = SymbolDomain.nonNullGet(symbol).midPrice();
            //比较欠的usd和资产的价格
            if (liquidUSD.compareTo(quantity.multiply(price).multiply(Constants.LIQUID_RATE)) >= 0) {
                this.placeOrderUntilAllComplete(uid, symbol, quantity, Direction.SELL, false, SourceType.LIQUIDATION);
            } else {
                this.placeOrderUntilAllComplete(uid, symbol, liquidUSD, Direction.SELL, true, SourceType.LIQUIDATION);
            }

        }
    }

    @Override
    public SpotDetailRes detail(@Nullable String uid, String symbol) {
        Response<List<PriceChange24h>> priceChange24hRes =
                klineInfoClient.priceChange24h(org.springframework.util.StringUtils.collectionToDelimitedString(SymbolDomain.CACHE.keySet(), ","));
        //symbol - 24h price change rate
        //symbol - 24h price change rate
        Map<String, BigDecimal> priceChange24hMap = new HashMap<>();
        if (BusinessExceptionEnum.SUCCESS.getCode() == priceChange24hRes.getCode() && null != priceChange24hRes.getData()) {
            List<PriceChange24h> priceChange24hList = priceChange24hRes.getData();
            priceChange24hMap = priceChange24hList.stream().collect(Collectors.toMap(PriceChange24h::getSymbol, priceChange24h -> {
                BigDecimal price = priceChange24h.getPrice();
                BigDecimal priceOld = priceChange24h.getPriceOld();
                return price.subtract(priceOld).divide(priceOld, Constants.PRICE_CHANGE_RATE_PRECISION, RoundingMode.DOWN);
            }));
        }

        Response<List<PriceChange>> priceChangeRes = klineInfoClient.priceChange(symbol,
                org.springframework.util.StringUtils.arrayToDelimitedString(Constants.PRICE_CHANGE_TIME_ARR,
                        ","));
        //time - time price change rate
        Map<Integer, BigDecimal> priceChangeMap = Collections.emptyMap();
        if (BusinessExceptionEnum.SUCCESS.getCode() == priceChangeRes.getCode()) {
            List<PriceChange> priceChanges = priceChangeRes.getData();
            priceChangeMap = priceChanges.stream().collect(Collectors.toMap(PriceChange::getDays, priceChange -> {
                try {
                    BigDecimal price = priceChange.getPrice();
                    BigDecimal priceOld = priceChange.getPriceOld();
                    return price.subtract(priceOld).divide(priceOld, Constants.PRICE_CHANGE_RATE_PRECISION, RoundingMode.DOWN);
                } catch (Exception e) {
                    log.error("kline data err, cause = {}", ExceptionUtils.getRootCauseMessage(e), e);
                    return BigDecimal.ZERO;
                }
            }));
        }
        SpotDetailRes res = new SpotDetailRes();
        res.setChange24h(priceChange24hMap.getOrDefault(symbol, BigDecimal.ZERO));
        res.setChange30d(priceChangeMap.getOrDefault(Constants.PRICE_CHANGE_30D, BigDecimal.ZERO));
        res.setChange1y(priceChangeMap.getOrDefault(Constants.PRICE_CHANGE_1Y, BigDecimal.ZERO));
        SymbolDomain symbolDomain = SymbolDomain.nonNullGet(symbol);
        res.setPrice(symbolDomain.midPrice());
        res.setBuy(symbolDomain.getBuyPrice());
        res.setSell(symbolDomain.getSellPrice());
        if (null != uid) {
            List<String> allFavorite = marketService.listAllFavorite(uid);
            res.setFavorite(allFavorite.contains(symbol));
        }
        Pair<String, String> coinPair = CommonUtils.coinPair(symbol);
        String base = coinPair.getFirst();
        List<String> symbolList = SymbolDomain.CACHE.keySet().stream()
                .filter(s -> s.startsWith(base) && !s.contains(Constants.IDK_COIN))
                .sorted(Comparator.comparing(o -> Constants.MARKET_QUOTE_INDEX_MAP.getOrDefault(CommonUtils.getQuoteCoin(o),
                        Integer.MAX_VALUE))).collect(Collectors.toList());
        res.setSymbols(symbolList);
        res.setFeeRate(tradeFeeConfigService.selectUserFeeConfig(uid).getSpotFeeRate());
        return res;
    }


    /**
     * 交易创建更新
     *
     * @param transaction
     * @param order
     */
    private void transactionNewNotice(TradeTransaction transaction, TradeSpotOrder order) {
        if (SourceType.isFromUser(order.getSource())) {
            pushComponent.pushWsMessage(WsPushMessage.buildAllConsumersMessage(order.getUid(), PushEventEnum.WEB_TRANSACTION_NEW,
                    webMapStruct.tradeTransaction2TransactionResEvent(transaction)));
        }
    }

    /**
     * 订单创建推送
     *
     * @param order
     */
    private void orderNewNotice(TradeSpotOrder order) {
        if (SourceType.isFromUser(order.getSource())) {
            pushComponent.pushWsMessage(WsPushMessage.buildAllConsumersMessage(order.getUid(),
                    PushEventEnum.WEB_ORDER_NEW, webMapStruct.tradeSpotOrder2OrderResEvent(order)));
        }
        // todo: available amount
        sensorsTraceService.spotSubmit(order, BigDecimal.ZERO);
    }

}
