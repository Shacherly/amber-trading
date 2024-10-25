package com.google.backend.trading.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.backend.asset.common.model.asset.req.GetReqIdStatusReq;
import com.google.backend.asset.common.model.trade.req.TradeSpotOrderReq;
import com.google.backend.asset.common.model.trade.req.TradeSpotReq;
import com.google.backend.common.web.Response;
import com.google.backend.trading.client.feign.AssetInfoClient;
import com.google.backend.trading.component.CoinDomain;
import com.google.backend.trading.config.i18n.I18nEnum;
import com.google.backend.trading.constant.Constants;
import com.google.backend.trading.dao.mapper.DefaultTradeSwapOrderMapper;
import com.google.backend.trading.dao.mapper.TradeSwapOrderMapper;
import com.google.backend.trading.dao.model.TradeSwapOrder;
import com.google.backend.trading.dao.model.TradeSwapOrderExample;
import com.google.backend.trading.dao.model.TradeTransaction;
import com.google.backend.trading.exception.BusinessException;
import com.google.backend.trading.exception.BusinessExceptionEnum;
import com.google.backend.trading.mapstruct.swap.TradeSwapOrderMapStruct;
import com.google.backend.trading.model.common.PageResult;
import com.google.backend.trading.model.internal.aceup.AceUpSwapReq;
import com.google.backend.trading.model.internal.aceup.AceUpSwapRes;
import com.google.backend.trading.model.internal.amp.AmpSwapReq;
import com.google.backend.trading.model.internal.amp.AmpSwapRes;
import com.google.backend.trading.model.pdt.CreateSwapReq;
import com.google.backend.trading.model.pdt.CreateSwapRes;
import com.google.backend.trading.model.pdt.CrexSwapPriceReq;
import com.google.backend.trading.model.pdt.SwapByIdRes;
import com.google.backend.trading.model.swap.SwapType;
import com.google.backend.trading.model.swap.api.AipSwapOrderRes;
import com.google.backend.trading.model.swap.api.CoinBalanceRes;
import com.google.backend.trading.model.swap.api.QuickSwapInfoRes;
import com.google.backend.trading.model.swap.api.QuickSwapOrderPlaceReq;
import com.google.backend.trading.model.swap.api.QuickSwapPriceReq;
import com.google.backend.trading.model.swap.api.SwapNotice;
import com.google.backend.trading.model.swap.api.SwapOrderHistoryLiteReq;
import com.google.backend.trading.model.swap.api.SwapOrderHistoryReq;
import com.google.backend.trading.model.swap.api.SwapOrderLiteRes;
import com.google.backend.trading.model.swap.api.SwapOrderRes;
import com.google.backend.trading.model.swap.api.SwapPriceReq;
import com.google.backend.trading.model.swap.api.SwapPriceRes;
import com.google.backend.trading.model.swap.dto.AipSwapOrderPlaceReqDTO;
import com.google.backend.trading.model.swap.dto.SwapOrderPlace;
import com.google.backend.trading.model.trade.AssetStatus;
import com.google.backend.trading.model.trade.Direction;
import com.google.backend.trading.model.trade.OrderStatus;
import com.google.backend.trading.model.trade.PdtStatus;
import com.google.backend.trading.model.trade.SourceType;
import com.google.backend.trading.model.trade.fee.UserFeeConfigRate;
import com.google.backend.trading.push.FundingBehaviorEventMessage;
import com.google.backend.trading.push.PushComponent;
import com.google.backend.trading.push.PushEventEnum;
import com.google.backend.trading.push.WsPushMessage;
import com.google.backend.trading.service.AssetRequest;
import com.google.backend.trading.service.CrexApiRequest;
import com.google.backend.trading.service.RiskInfoService;
import com.google.backend.trading.service.SensorsTraceService;
import com.google.backend.trading.service.SwapService;
import com.google.backend.trading.service.TradeAssetService;
import com.google.backend.trading.service.TradeFeeConfigService;
import com.google.backend.trading.service.TradeTransactionService;
import com.google.backend.trading.transaction.SwapTransaction;
import com.google.backend.trading.util.AlarmLogUtil;
import com.google.backend.trading.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 兑换业务的接口实现
 *
 * @author savion.chen
 * @date 2021/10/4 9:40
 */
@Slf4j
@Service
public class SwapServiceImpl implements SwapService {

    @Autowired
    private AssetRequest assetRequest;
    @Autowired
    private CrexApiRequest crexApiRequest;
    @Autowired
    private TradeAssetService tradeAssetService;
    @Autowired
    private TradeFeeConfigService tradeFeeConfigService;
    @Autowired
    private DefaultTradeSwapOrderMapper defaultTradeSwapOrderMapper;
    @Resource
    private TradeSwapOrderMapper tradeSwapOrderMapper;
    @Autowired
    private TradeSwapOrderMapStruct swapMapStruct;
    @Autowired
    private PushComponent pushComponent;
    @Autowired
    private SwapTransaction swapTransaction;
    @Autowired
    private SensorsTraceService sensorsTraceService;
    @Autowired
    private RiskInfoService riskInfoService;
    @Autowired
    private TradeTransactionService tradeTransactionService;
    @Autowired
    private AssetInfoClient assetInfoClient;


    private static final long MAX_TIMEOUT = 20 * 1000;
    /**
     * 用于价差保护，发给pdt的limit price是需要加上这个buffer后的price
     */
    private final BigDecimal PRICE_BUFFER_RATE = new BigDecimal("0.005");


    @Override
    public SwapPriceRes queryPrice(SwapPriceReq req, String userId) {
        String fromCoin = req.getFromCoin();
        String toCoin = req.getToCoin();
        BigDecimal quantity = req.getQuantity();
        if (!CommonUtils.checkCoinConfig(fromCoin)
                || !CommonUtils.checkCoinConfig(toCoin)) {
            throw new BusinessException(BusinessExceptionEnum.SYMBOL_NOT_SUPPORT_SWAP);
        }
        CrexSwapPriceReq sendReq = new CrexSwapPriceReq();
        sendReq.setFrom(fromCoin);
        sendReq.setTo(toCoin);
        boolean isPay = SwapType.isPayment(req.getMode());
        if (isPay) {
            BigDecimal minOrderAmount = CoinDomain.nonNullGet(fromCoin).getSwapConfig().getMinOrderAmount();
            if (quantity.compareTo(minOrderAmount) < 0) {
                quantity = minOrderAmount;
            }
            sendReq.setQuantity(quantity);
        } else {
            BigDecimal minOrderAmount = CoinDomain.nonNullGet(toCoin).getSwapConfig().getMinOrderAmount();
            if (quantity.compareTo(minOrderAmount) < 0) {
                quantity = minOrderAmount;
            }
            sendReq.setToQuantity(quantity);
        }

        BigDecimal swapPrice = crexApiRequest.querySwapPrice(sendReq);
        if (swapPrice != null) {
            SwapPriceRes res = new SwapPriceRes();
            BigDecimal feeRate = getUserFeeRate(userId);
            BigDecimal deductFee = BigDecimal.ONE.subtract(feeRate);
            if (!Boolean.TRUE.equals(req.getFeeFree())) {
                swapPrice = swapPrice.multiply(deductFee);
            }
            res.setPrice(swapPrice.setScale(Constants.SWAP_PRICE_MAX_PRECISION, RoundingMode.DOWN));
            res.setReversePrice(BigDecimal.ONE.divide(swapPrice, Constants.SWAP_PRICE_MAX_PRECISION, RoundingMode.UP));
            res.setSymbol(fromCoin + "_" + toCoin);
            return res;
        } else {
            throw new BusinessException(BusinessExceptionEnum.UNEXPECTED_ERROR);
        }
    }

    @Override
    public SwapOrderRes placeOrder(SwapOrderPlace req) {
        if (!CommonUtils.checkCoinConfig(req.getFromCoin())
                || !CommonUtils.checkCoinConfig(req.getToCoin())) {
            throw new BusinessException(BusinessExceptionEnum.SYMBOL_NOT_SUPPORT_SWAP);
        }

        if (req.getSource().isFromUser()) {
            riskInfoService.validateRiskStatus(req.getUid());
        }
        TradeSwapOrder order = createSwapOrder(req);

        BigDecimal quantity = req.getQuantity();
        BigDecimal minQty = CommonUtils.getCoinMin(req.getReqCoin());
        BigDecimal maxQty = CommonUtils.getCoinMax(req.getReqCoin());
        if (quantity.compareTo(minQty) < 0) {
            throw new BusinessException(BusinessExceptionEnum.ORDER_AMOUNT_TOO_SMALL);
        }

        if (quantity.compareTo(maxQty) > 0) {
            throw new BusinessException(BusinessExceptionEnum.ORDER_AMOUNT_TOO_LARGE);
        }

        defaultTradeSwapOrderMapper.insertSelective(order);
        boolean isLoan = isLoanType(order.getSource());
        if (!isLoan) {
            TradeSpotOrderReq freezeReq = getSwapFreezeReq(order, order.getUuid());
            BusinessExceptionEnum code = assetRequest.freezeFunds(freezeReq);
            if (code != BusinessExceptionEnum.SUCCESS) {
                log.error("freeze fail. order={} code={}", order, code);
                if (code == BusinessExceptionEnum.UNEXPECTED_ERROR) {
                    //HTTP请求失败，异步查询请求状态
                    order.setStatus(OrderStatus.ASSET_PENDING.getName());
                    updateSwapOrder(order);
                    AlarmLogUtil.alarm("swap placeOrder 资金请求异常 转异步处理 order={} reqid={}", order, freezeReq.getReqId());
                } else {
                    //业务失败
                    setOrderFailMsg(order);
                }
                throw new BusinessException(code);
            }
        }

        updateOrderStatus(order, OrderStatus.EXECUTING);
        CreateSwapReq sendReq = getPlaceRequest(order);
        CreateSwapRes result = crexApiRequest.executeSwapOrder(sendReq);
        if (result != null) {
            this.performUpdate(order, result, isLoan);
        } else {
            // 如果执行异常，则置锁定态，由异步线程查询更新处理
            order.setMemo(I18nEnum.TRADING_SWAP_ORDER_MEMO_TIMEOUT.getKey());
            updateOrderStatus(order, OrderStatus.LOCKED);
        }

        if (OrderStatus.isFinish(order.getStatus())) {
            // 推异步消息给前端，埋点
            noticeMessage(order, req.isLiteMarketSwap());
        }
        return swapMapStruct.tradeSwapOrder2OrderRes(order);
    }

    @Override
    public void checkAndFillOrder(TradeSwapOrder order, boolean isAsync) {
        log.debug("checkAndFillOrder| orderId:{}, order:{}", order.getUuid(), order);

        boolean isLoan = isLoanType(order.getSource());
        if (isExecuteTimeOut(order)) {
            // 先检查超时的情况，进行撤单处理，给出超时提醒
            order.setMemo(I18nEnum.TRADING_SWAP_ORDER_MEMO_TIMEOUT.getKey());
            log.debug("isExecuteTimeOut order:{}", order);
            this.performCancel(order, isLoan);

        } else {
            // 检查异常的订单，主动查询其是否完成
            SwapByIdRes resp = crexApiRequest.querySwapOrder(order.getUuid());
            if (resp != null) {
                CreateSwapRes data = convertTradedData(resp);
                this.performUpdate(order, data, isLoan);
            }
        }

        if (OrderStatus.isFinish(order.getStatus())) {
            // 推异步消息给前端，埋点
            noticeMessage(order, false);
        }
    }

    private void performUpdate(TradeSwapOrder order, CreateSwapRes info, boolean isLoan) {
        if (info.isSuccess()) {
            setTradedResult(order, info);
            performTraded(order, info, isLoan);
            TradeTransaction transaction = generateTradeTransaction(order);
            //set成交额到数据库
            tradeAssetService.setUserAmountUsd2DB(order.getUid(), order.getToCoin(), info.getToFilled(), transaction.getUuid());
            swapTransaction.insertTransactionAndUpdateOrder(transaction, order);
            FundingBehaviorEventMessage msg = FundingBehaviorEventMessage.buildSwapValue(order, transaction);
            if (SourceType.isFromUser(order.getSource()) || SourceType.TAKE_PROFIT_STOP_LOSS.getName().equals(order.getSource())) {
                pushComponent.pushFundingEventMessage(msg);
            }
        } else {
            order.setMemo(I18nEnum.TRADING_SWAP_ORDER_MEMO_PRICE_OFFSET_OVER.getKey());
            performCancel(order, isLoan);
        }
    }

    private void performTraded(TradeSwapOrder order, CreateSwapRes info, boolean isLoan) {
        if (!isLoan) {
            // 在保证请求资金模块id不重复的情况下，能够根据订单id追踪日志信息
            String updateId = "update-" + order.getUuid();
            TradeSpotReq tradeReq = this.getSwapTradeReq(order, info, updateId);
            int retCode = assetRequest.updateTradedResult(tradeReq);
            if (retCode != 0) {
                order.setMemo("");
                log.error("update asset error. order={} code={}", order.toString(), retCode);
            }
        }
        order.setStatus(OrderStatus.COMPLETED.getName());
    }

    private void performTradedForAip(TradeSwapOrder order, TradeTransaction tradeTransaction, CreateSwapRes info, boolean isLoan) {
        if (!isLoan) {
            // 在保证请求资金模块id不重复的情况下，能够根据订单id追踪日志信息(AIP使用)
            String updateId = "update-" + tradeTransaction.getUuid();
            TradeSpotReq tradeReq = this.getSwapTradeReq(order, info, updateId);
            int retCode = assetRequest.updateTradedResult(tradeReq);
            if (retCode != 0) {
                order.setMemo("");
                log.error("update asset error. order={} code={}", order.toString(), retCode);
            }
        }
        order.setStatus(OrderStatus.COMPLETED.getName());
    }

    private void performCancel(TradeSwapOrder order, boolean isLoan) {
        log.debug("performCancel order:{}", order);
        if (!isLoan) {
            String cancelId = "cancel-" + order.getUuid();
            TradeSpotOrderReq freezeReq = getSwapFreezeReq(order, cancelId);
            int retCode = assetRequest.cancelFreeze(freezeReq);
            if (retCode != 0) {
                order.setMemo("");
                log.error("cancel freeze error. order={} code={}", order.toString(), retCode);
            }
        }
        order.setStatus(OrderStatus.CANCELED.getName());
        updateSwapOrder(order);
    }

    private TradeSwapOrder createSwapOrder(SwapOrderPlace req) {
        SwapType mode = req.getMode();
        TradeSwapOrder order = new TradeSwapOrder();
        // 借贷质押还款会指定orderId, 保证幂等
        if (req.getOrderId() != null) {
            order.setUuid(req.getOrderId());
        } else {
            order.setUuid(CommonUtils.generateUUID());
        }
        order.setUid(req.getUid());

        order.setFromCoin(req.getFromCoin());
        order.setToCoin(req.getToCoin());
        order.setMode(mode.getName());
        order.setOrderPrice(CommonUtils.convertData(req.getPrice()));
        BigDecimal feeRate = getUserFeeRate(order.getUid());
        order.setFeeRate(feeRate);
        order.setFee(CommonUtils.ZERO_NUM);
        order.setStatus(OrderStatus.PENDING.getName());
        if (req.getSource() != null) {
            order.setSource(req.getSource().getName());
        } else {
            order.setSource(SourceType.PLACED_BY_CLIENT.getName());
        }

        // 根据用户询价的下单价，来计算另一方的量
        BigDecimal placeQty = CommonUtils.convertData(req.getQuantity());
        if (SwapType.PAYMENT == mode) {
            order.setFeeCoin(req.getToCoin());
            order.setFromQuantity(placeQty);
        } else {
            order.setFeeCoin(req.getFromCoin());
            order.setToQuantity(placeQty);
        }

        order.setMemo("");
        Date nowTime = CommonUtils.getNowTime();
        order.setCtime(nowTime);
        order.setMtime(nowTime);
        return order;
    }

    private TradeSwapOrder createSwapOrderForAip(SwapOrderPlace req, TradeSwapOrder dbOrder) {
        SwapType mode = req.getMode();
        TradeSwapOrder order = new TradeSwapOrder();
        // 借贷质押还款会指定orderId, 保证幂等
        if (req.getOrderId() != null) {
            order.setUuid(req.getOrderId());
        } else {
            order.setUuid(CommonUtils.generateUUID());
        }
        order.setUid(req.getUid());
        order.setFromCoin(req.getFromCoin());
        order.setToCoin(req.getToCoin());
        order.setMode(mode.getName());
        order.setOrderPrice(CommonUtils.convertData(req.getPrice()));
        BigDecimal feeRate = getUserFeeRate(order.getUid());
        order.setFeeRate(feeRate);
        order.setFee(CommonUtils.ZERO_NUM);
        order.setStatus(OrderStatus.PENDING.getName());
        if (req.getSource() != null) {
            order.setSource(req.getSource().getName());
        } else {
            order.setSource(SourceType.PLACED_BY_CLIENT.getName());
        }

        // 根据用户询价的下单价，来计算另一方的量
        BigDecimal placeQty = CommonUtils.convertData(req.getQuantity());
        if (SwapType.PAYMENT == mode) {
            order.setFeeCoin(req.getToCoin());
            order.setFromQuantity(placeQty);
        } else {
            order.setFeeCoin(req.getFromCoin());
            order.setToQuantity(placeQty);
        }

        order.setMemo("");
        Date nowTime = CommonUtils.getNowTime();
        order.setCtime(dbOrder != null ? dbOrder.getCtime() : nowTime);
        order.setMtime(nowTime);
        return order;
    }

    @Override
    public SwapOrderRes queryLast(String userId) {
        TradeSwapOrderExample example = new TradeSwapOrderExample();
        example.setOrderByClause("CTIME DESC LIMIT 1");
        example.createCriteria().andUidEqualTo(userId);
        List<TradeSwapOrder> orderList = defaultTradeSwapOrderMapper.selectByExample(example);
        if (!orderList.isEmpty()) {
            TradeSwapOrder item = orderList.get(0);
            return swapMapStruct.tradeSwapOrder2OrderRes(item);
        }
        return null;
    }

    @Override
    public SwapOrderRes querySwapOrder(String orderId, String userId) {
        TradeSwapOrderExample example = new TradeSwapOrderExample();
        TradeSwapOrderExample.Criteria criteria = example.createCriteria();
        criteria.andUuidEqualTo(orderId);
        criteria.andUidEqualTo(userId);
        List<TradeSwapOrder> orderList = defaultTradeSwapOrderMapper.selectByExample(example);
        if (!orderList.isEmpty()) {
            TradeSwapOrder item = orderList.get(0);
            return swapMapStruct.tradeSwapOrder2OrderRes(item);
        }
        return null;
    }

    @Override
    public PageResult<SwapOrderRes> queryHistory(SwapOrderHistoryReq req, String userId) {
        PageHelper.startPage(req.getPage(), req.getPageSize(), true);
        TradeSwapOrderExample example = new TradeSwapOrderExample();
        example.setOrderByClause("CTIME DESC");
        TradeSwapOrderExample.Criteria criteria = example.createCriteria();
        criteria.andUidEqualTo(userId);
        if (StringUtils.isNotEmpty(req.getStatus())) {
            if (Objects.equals(req.getStatus(), OrderStatus.APP_QUERY_STATUS_CANCELED)) {
                criteria.andStatusIn(OrderStatus.APP_CANCELED_STATUS);
            } else if (Objects.equals(req.getStatus(), OrderStatus.APP_QUERY_STATUS_COMPLETED)) {
                criteria.andStatusIn(OrderStatus.APP_COMPLETED_STATUS);
            }
        } else {
            criteria.andStatusIn(OrderStatus.HISTORY_STATUS);
        }
        if (req.getStartTime() != null) {
            Date startTime = CommonUtils.getNowTime(req.getStartTime());
            criteria.andMtimeGreaterThan(startTime);
        }
        if (req.getEndTime() != null) {
            Date endTime = CommonUtils.getNowTime(req.getEndTime());
            criteria.andMtimeLessThan(endTime);
        }
        if (req.getCoin() != null) {
            TradeSwapOrderExample.Criteria orCriteria = example.or();
            orCriteria.getCriteria().addAll(criteria.getCriteria());
            criteria.andToCoinEqualTo(req.getCoin());
            orCriteria.andFromCoinEqualTo(req.getCoin());
        }
        List<TradeSwapOrder> orderList = defaultTradeSwapOrderMapper.selectByExample(example);
        PageInfo<TradeSwapOrder> pageInfo = new PageInfo<>(orderList);
        List<SwapOrderRes> infoRes = swapMapStruct.tradeSwapOrder2OrderRes(pageInfo.getList());
        return PageResult.generate(pageInfo.getTotal(), req.getPage(), req.getPageSize(), infoRes);
    }


    @Override
    public PageResult<SwapOrderLiteRes> queryHistoryLite(SwapOrderHistoryLiteReq req, String userId) {
        PageHelper.startPage(req.getPage(), req.getPageSize(), true);
        List<TradeSwapOrder> orderList = tradeSwapOrderMapper.selectHistoryLite(req, userId);
        PageInfo<TradeSwapOrder> pageInfo = new PageInfo<>(orderList);
        List<SwapOrderLiteRes> infoRes = swapMapStruct.tradeSwapOrder2OrderLiteRes(pageInfo.getList());
        infoRes.stream().forEach(e -> {
            BigDecimal price = e.getPrice();
            String quoteCoin = CommonUtils.getQuoteCoin(req.getSymbol());
            if (!e.getToCoin().equals(quoteCoin) && price != null && price.compareTo(BigDecimal.ZERO) != 0) {
                e.setPrice(CommonUtils.roundDivide(BigDecimal.ONE, price));
            }
        });
        return PageResult.generate(pageInfo.getTotal(), req.getPage(), req.getPageSize(), infoRes);
    }

    @Override
    public AipSwapOrderRes aipPerformOrder(@NonNull TradeSwapOrder tradeSwapOrder) {
        if (!OrderStatus.AIP_LOCKED.getName().equals(tradeSwapOrder.getStatus())) {
            AlarmLogUtil.alarm("aipPerformOrder 出现重复执行 : order: {}", tradeSwapOrder);
            return swapMapStruct.tradeSwapOrder2AipSwapOrderRes(tradeSwapOrder);
        }
        log.info("aipPerformOrder : order={}", tradeSwapOrder);
        updateOrderStatus(tradeSwapOrder, OrderStatus.EXECUTING);

        //1. 是否存在transID 上次PDT请求异常（PDT异常不会解锁资金），幂等查询+SWAP订单处理
        String transId = tradeSwapOrder.getTransId();
        if (StringUtils.isNotEmpty(transId)) {
            SwapByIdRes resp = crexApiRequest.querySwapOrder(transId);
            CreateSwapRes createSwapRes;
            if (resp == null) {
                createSwapRes = CreateSwapRes.FAIL;
            } else {
                createSwapRes = convertTradedData(resp);
            }
            //PDT请求完成后　统一处理
            TradeTransaction tradeTransaction = tradeTransactionService.queryAllByTransId(transId);
            return performUpdateOrderByPDTResForAip(createSwapRes, tradeSwapOrder, tradeTransaction);
        }

        //2. 询价
        try {
            SwapPriceReq swapPriceReq = new SwapPriceReq();
            swapPriceReq.setMode(tradeSwapOrder.getMode());
            swapPriceReq.setToCoin(tradeSwapOrder.getToCoin());
            swapPriceReq.setFromCoin(tradeSwapOrder.getFromCoin());
            boolean isBuy = SwapType.isPayment(tradeSwapOrder.getMode());
            if (isBuy) {
                BigDecimal fromQuantity = tradeSwapOrder.getFromQuantity();
                swapPriceReq.setQuantity(fromQuantity);
            } else {
                BigDecimal toQuantity = tradeSwapOrder.getToQuantity();
                swapPriceReq.setQuantity(toQuantity);
            }
            swapPriceReq.setFeeFree(false);
            SwapPriceRes swapPriceRes = this.queryPrice(swapPriceReq, tradeSwapOrder.getUid());
            //设置下单价格
            tradeSwapOrder.setOrderPrice(CommonUtils.convertData(swapPriceRes.getPrice()));
        } catch (Exception e) {
            //询价失败
            updateOrderStatus(tradeSwapOrder, OrderStatus.AIP_LOCKED);
            return swapMapStruct.tradeSwapOrder2AipSwapOrderRes(tradeSwapOrder);
        }

        //3. 询价成功  create trade_transaction and reset order
        TradeTransaction tradeTransaction = generateTradeTransactionForAip(tradeSwapOrder);
        tradeTransaction.setAssetStatus(AssetStatus.PENDING.name());
        tradeSwapOrder.setTransId(tradeTransaction.getUuid());
        //4. insert trade_transaction and update order
        swapTransaction.insertTransactionAndUpdateOrder(tradeTransaction, tradeSwapOrder);
        //5. 冻结资金
        TradeSpotOrderReq freezeReq = getSwapFreezeReq(tradeSwapOrder, tradeTransaction.getUuid());
        BusinessExceptionEnum code = assetRequest.freezeFunds(freezeReq);
        if (code != BusinessExceptionEnum.SUCCESS) {
            tradeSwapOrder.setTransId("");
            log.error("freeze fail. order={} code={}", tradeSwapOrder, code);
            if (code == BusinessExceptionEnum.UNEXPECTED_ERROR) {
                //HTTP请求失败，异步查询请求状态
                tradeSwapOrder.setStatus(OrderStatus.ASSET_PENDING.getName());
                AlarmLogUtil.alarm("swap aipPerformOrder 资金请求异常 转异步处理 order={} reqid={}", tradeSwapOrder, freezeReq.getReqId());
            } else {
                tradeSwapOrder.setStatus(OrderStatus.CANCELED.getName());
            }
            tradeSwapOrder.setMemo(I18nEnum.TRADING_SWAP_ORDER_MEMO_NO_BALANCE.getKey());
            tradeTransaction.setAssetStatus(AssetStatus.EXCEPTION.name());
            swapTransaction.updateTransactionAndUpdateOrder(tradeTransaction, tradeSwapOrder);
            AipSwapOrderRes swapOrderRes = swapMapStruct.tradeSwapOrder2AipSwapOrderRes(tradeSwapOrder);
            //资金不足 终态推送
            pushComponent.pushAipSwapOrderResult(swapOrderRes);
            return swapOrderRes;
        }
        //6. PDT下单
        tradeTransaction.setAssetStatus(AssetStatus.COMPLETED.name());
        CreateSwapReq sendReq = getPlaceRequestForAip(tradeSwapOrder, tradeTransaction.getUuid());
        CreateSwapRes result = crexApiRequest.executeSwapOrder(sendReq);
        return performUpdateOrderByPDTResForAip(result, tradeSwapOrder, tradeTransaction);
    }

    /**
     * 根据 PDT请求结果
     * 1. 更新订单以及交易记录状态
     * 2. 判断是否解冻资金
     * 3. 终态推送AIP
     *
     * @param result
     * @param order
     * @param tradeTransaction
     * @return
     */
    private AipSwapOrderRes performUpdateOrderByPDTResForAip(CreateSwapRes result, TradeSwapOrder order, TradeTransaction tradeTransaction) {
        if (result.isSuccess()) {
            order.setTransId("");
            //业务成功
            setTradedResult(order, result);
            performTradedForAip(order, tradeTransaction, result, false);
            updateDoneStatusTradeTransactionForAip(tradeTransaction, order);
            //set成交额到数据库
            tradeAssetService.setUserAmountUsd2DB(order.getUid(), order.getToCoin(), result.getToFilled(), tradeTransaction.getUuid());
            swapTransaction.updateTransactionAndUpdateOrder(tradeTransaction, order);
            FundingBehaviorEventMessage msg = FundingBehaviorEventMessage.buildSwapValue(order, tradeTransaction);
            pushComponent.pushFundingEventMessage(msg);
            AipSwapOrderRes swapOrderRes = swapMapStruct.tradeSwapOrder2AipSwapOrderRes(order);
            pushComponent.pushAipSwapOrderResult(swapOrderRes);
            return swapOrderRes;
        } else if (!result.isSuccess() && result.httpSuccess()) {
            order.setTransId("");
            //业务失败
            // 1. 解冻资金（AIP冻结资金使用的是 transId）
            cancelFreezeFunds(order, tradeTransaction.getUuid());
            // 2. 更新PDT状态
            tradeTransaction.setPdtStatus(PdtStatus.EXCEPTION.name());
            // 3. 判断是否超过10min
            AipSwapOrderRes swapOrderRes = updateOrderStatusAndTransactionForException(order, tradeTransaction);
            return swapOrderRes;
        } else if (!result.httpSuccess()) {
            //http请求失败
            log.error("AIP定投 executeSwapOrder PDT发单HTTP失败，result = {} , order = {}", result, order);
            order.setTransId(tradeTransaction.getUuid());
            order.setStatus(OrderStatus.AIP_LOCKED.getName());
            tradeTransaction.setPdtStatus(PdtStatus.PENDING.name());
            swapTransaction.updateTransactionAndUpdateOrder(tradeTransaction, order);
            return swapMapStruct.tradeSwapOrder2AipSwapOrderRes(order);
        } else {
            AlarmLogUtil.alarm("CreateSwapRes result 未知状态 {}", result);
            order.setStatus(OrderStatus.AIP_LOCKED.getName());
            tradeTransaction.setPdtStatus(PdtStatus.PENDING.name());
            swapTransaction.updateTransactionAndUpdateOrder(tradeTransaction, order);
            return swapMapStruct.tradeSwapOrder2AipSwapOrderRes(order);
        }
    }

    /**
     * 只更新状态 不解锁资金
     * 【外部调用注意 解锁资金情况】
     *
     * @param order
     * @param tradeTransaction
     * @return
     */
    private AipSwapOrderRes updateOrderStatusAndTransactionForException(TradeSwapOrder order, TradeTransaction tradeTransaction) {
        AipSwapOrderRes swapOrderRes = null;
        Date ctime = order.getCtime();
        if (System.currentTimeMillis() - ctime.getTime() > Duration.ofMinutes(10).toMillis()) {
            order.setMemo(I18nEnum.TRADING_SWAP_ORDER_MEMO_TIMEOUT.getKey());
            order.setStatus(OrderStatus.CANCELED.name());
            swapTransaction.updateTransactionAndUpdateOrder(tradeTransaction, order);
            swapOrderRes = swapMapStruct.tradeSwapOrder2AipSwapOrderRes(order);
            pushComponent.pushAipSwapOrderResult(swapOrderRes);
            AlarmLogUtil.alarm("aip swap OrderPlace 执行失败 超过10分钟 : order: {}", order);
        } else {
            order.setStatus(OrderStatus.AIP_LOCKED.name());
            swapTransaction.updateTransactionAndUpdateOrder(tradeTransaction, order);
            swapOrderRes = swapMapStruct.tradeSwapOrder2AipSwapOrderRes(order);
        }
        return swapOrderRes;
    }

    @Override
    public void saveForAip(AipSwapOrderPlaceReqDTO req) {
        log.info("saveForAip req:{}", req);
        TradeSwapOrder dbOrder = queryByOrderId(req.getAipSwapId());
        if (dbOrder != null) {
            AlarmLogUtil.alarm("aip swap OrderPlace 出现重复下单 : req :{} order: {}", req, dbOrder);
            return;
        }
        // 构建swap order
        SwapOrderPlace swapOrderPlace = swapMapStruct.aipSwapOrderPlaceReq2SwapOrderPlace(req);
        swapOrderPlace.setPrice(BigDecimal.ZERO);
        swapOrderPlace.setSource(SourceType.AUTOMATIC_INVESTMENT_PLAN);
        TradeSwapOrder order = createSwapOrderForAip(swapOrderPlace, dbOrder);
        order.setStatus(OrderStatus.AIP_LOCKED.getName());
        defaultTradeSwapOrderMapper.insertSelective(order);
    }

    private void cancelFreezeFunds(TradeSwapOrder order, @NonNull String reqId) {
        String cancelId = "cancel-" + reqId;
        TradeSpotOrderReq freezeReq = getSwapFreezeReq(order, cancelId);
        int retCode = assetRequest.cancelFreeze(freezeReq);
        if (retCode != 0) {
            order.setMemo("");
            log.error("cancel freeze error. order={} code={}", order.toString(), retCode);
        }
    }

    @Override
    public boolean insertOrUpdate(TradeSwapOrder tradeSwapOrder) {
        String uuid = tradeSwapOrder.getUuid();
        TradeSwapOrderExample example = new TradeSwapOrderExample();
        example.createCriteria().andUuidEqualTo(uuid);
        long count = defaultTradeSwapOrderMapper.countByExample(example);
        if (count > 0) {
            Date ctime = tradeSwapOrder.getCtime();
            tradeSwapOrder.setCtime(null);
            boolean b = defaultTradeSwapOrderMapper.updateByExampleSelective(tradeSwapOrder, example) > 0;
            tradeSwapOrder.setCtime(ctime);
            return b;
        } else {
            return defaultTradeSwapOrderMapper.insertSelective(tradeSwapOrder) > 0;
        }
    }

    @Override
    public List<TradeSwapOrder> listAllAipActiveOrders() {
        TradeSwapOrderExample example = new TradeSwapOrderExample();
        TradeSwapOrderExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo(OrderStatus.AIP_LOCKED.getName());
        return defaultTradeSwapOrderMapper.selectByExample(example);
    }

    @Override
    public TradeSwapOrder queryByOrderId(String orderId) {
        TradeSwapOrderExample example = new TradeSwapOrderExample();
        example.createCriteria().andUuidEqualTo(orderId);
        List<TradeSwapOrder> list = defaultTradeSwapOrderMapper.selectByExample(example);
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    @Override
    public List<TradeSwapOrder> getAssetExceptionOrder() {
        TradeSwapOrderExample example = new TradeSwapOrderExample();
        example.createCriteria().andStatusEqualTo(OrderStatus.ASSET_PENDING.getName());
        return defaultTradeSwapOrderMapper.selectByExample(example);
    }

    @Override
    public void checkAssetAndUpdateStatus(TradeSwapOrder order) {
        //查询reqID
        String reqId = order.getUuid();
        if (Objects.equals(order.getSource(), SourceType.AUTOMATIC_INVESTMENT_PLAN.getName())) {
            // （AIP）source = AUTOMATIC_INVESTMENT_PLAN使用 transId作为的reqId
            TradeTransaction transaction = tradeTransactionService.queryNearAssetExceptionByOrderId(order.getUuid());
            if (transaction != null) {
                reqId = transaction.getUuid();
            } else {
                AlarmLogUtil.alarm("checkAssetAndUpdateStatus 定投单没有找到transaction -> reqid. order={}", order.toString());
                return;
            }
        }
        GetReqIdStatusReq req = new GetReqIdStatusReq();
        req.setReqId(reqId);
        log.info("checkAssetAndUpdateStatus reqId:{} order={}", req.getReqId(), order);
        Response<Integer> reqIdStatus = assetInfoClient.getReqIdStatus(req);
        if (reqIdStatus.getCode() != BusinessExceptionEnum.SUCCESS.getCode()) {
            long time = order.getCtime().getTime();
            if (System.currentTimeMillis() - time > Duration.ofSeconds(60).toMillis()) {
                //大于60S终态更新，否则不处理下次继续
                updateOrderStatus(order, OrderStatus.CANCELED);
            }
        } else {
            if (reqIdStatus.getData() == 0) {
                //资金请求成功,需要取消订单
                cancelFreezeFunds(order, reqId);
            }
            //业务成功，设置取消状态
            updateOrderStatus(order, OrderStatus.CANCELED);
        }
    }

    @Override
    public List<TradeSwapOrder> listAllActiveOrders() {
        TradeSwapOrderExample example = new TradeSwapOrderExample();
        TradeSwapOrderExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo(OrderStatus.LOCKED.getName());
        return defaultTradeSwapOrderMapper.selectByExample(example);
    }

    @Override
    public BigDecimal quickSwapPrice(QuickSwapPriceReq req, String uid) {
        CrexSwapPriceReq priceReq = new CrexSwapPriceReq();
        String[] coins = req.getSymbol().split("_");
        priceReq.setFrom(coins[0]);
        priceReq.setTo(coins[1]);
        priceReq.setQuantity(req.getQuantity());
        BigDecimal lastPrice = crexApiRequest.querySwapPrice(priceReq);
        if (!CommonUtils.isPositive(lastPrice)) {
            return BigDecimal.ZERO;
        }

        BigDecimal swapPrice;
        BigDecimal feeRate = getUserFeeRate(uid);
        BigDecimal deductFee = BigDecimal.ONE.subtract(feeRate);
        if (Direction.isBuy(req.getDirection())) {
            swapPrice = CommonUtils.roundDivide(lastPrice, deductFee);
        } else {
            swapPrice = lastPrice.multiply(deductFee);
        }
        return swapPrice.setScale(Constants.SWAP_PRICE_MAX_PRECISION, RoundingMode.DOWN);
    }

    @Override
    public QuickSwapInfoRes quickSwapInfo(String symbol, String direct, String userId) {
        List<CoinBalanceRes> respList = assetRequest.queryCoinBalance(userId);
        if (respList == null) {
            return null;
        }

        QuickSwapInfoRes swapInfo = new QuickSwapInfoRes();
        Map<String, BigDecimal> coinMap = new HashMap<String, BigDecimal>();
        for (CoinBalanceRes node : respList) {
            coinMap.put(node.getCoin(), CommonUtils.convertShow(node.getBalance()));
        }
        swapInfo.setAvailableMap(coinMap);

        String fromCoin = CommonUtils.getPaymentCoin(symbol, direct);
        BigDecimal placeQty = coinMap.get(fromCoin);
        if (!CommonUtils.isPositive(placeQty)) {
            swapInfo.setMinAmount(BigDecimal.ZERO);
            swapInfo.setMaxAmount(BigDecimal.ZERO);
            return swapInfo;
        }

        BigDecimal lastPrice = CommonUtils.getMiddlePrice(symbol);
        BigDecimal getQty = Direction.isBuy(direct) ? CommonUtils.roundDivide(placeQty, lastPrice) : placeQty;
        getQty = CommonUtils.convertShow(getQty);

        String baseCoin = CommonUtils.getBaseCoin(symbol);
        BigDecimal minQty = CommonUtils.getCoinMin(baseCoin);
        BigDecimal maxQty = CommonUtils.getCoinMax(baseCoin);
        if (getQty.compareTo(minQty) > -1) {
            swapInfo.setMinAmount(minQty);
            if (getQty.compareTo(maxQty) > -1) {
                swapInfo.setMaxAmount(maxQty);
            } else {
                swapInfo.setMaxAmount(getQty);
            }
        } else {
            swapInfo.setMinAmount(BigDecimal.ZERO);
            swapInfo.setMaxAmount(BigDecimal.ZERO);
        }
        return swapInfo;
    }

    @Override
    public SwapOrderRes quickSwapPlace(QuickSwapOrderPlaceReq req, String userId) {
        SwapOrderPlace place = new SwapOrderPlace();
        String fromCoin = CommonUtils.getPaymentCoin(req.getSymbol(), req.getDirection());
        String toCoin = CommonUtils.getObtainedCoin(req.getSymbol(), req.getDirection());
        place.setReqCoin(CommonUtils.getBaseCoin(req.getSymbol()));
        place.setFromCoin(fromCoin);
        place.setToCoin(toCoin);
        place.setUid(userId);

        if (Direction.isBuy(req.getDirection())) {
            place.setMode(SwapType.OBTAINED);
            BigDecimal reversePrice = CommonUtils.roundDivide(BigDecimal.ONE, req.getPrice());
            place.setPrice(CommonUtils.convertData(reversePrice));
        } else {
            place.setMode(SwapType.PAYMENT);
            place.setPrice(CommonUtils.convertData(req.getPrice()));
        }

        place.setQuantity(CommonUtils.convertData(req.getQuantity()));
        place.setSource(SourceType.PLACED_BY_CLIENT);
        return this.placeOrder(place);
    }

    @Override
    public PageResult<AmpSwapRes> swapHistoryForAmp(AmpSwapReq ampSwapReq) {

        TradeSwapOrderExample tradeSwapOrderExample = new TradeSwapOrderExample();
        tradeSwapOrderExample.setOrderByClause("MTIME DESC,CTIME DESC");

        TradeSwapOrderExample.Criteria criteria = tradeSwapOrderExample.createCriteria().andUidEqualTo(ampSwapReq.getUid());

        if (StringUtils.isNotEmpty(ampSwapReq.getUuid())) {
            criteria.andUuidEqualTo(ampSwapReq.getUuid());
        }
        if (StringUtils.isNotEmpty(ampSwapReq.getMode())) {
            criteria.andModeEqualTo(ampSwapReq.getMode());
        }
        if (StringUtils.isNotEmpty(ampSwapReq.getFromCoin())) {
            criteria.andFromCoinEqualTo(ampSwapReq.getFromCoin());
        }
        if (StringUtils.isNotEmpty(ampSwapReq.getToCoin())) {
            criteria.andToCoinEqualTo(ampSwapReq.getToCoin());
        }
        if (StringUtils.isNotEmpty(ampSwapReq.getStatus())) {
            criteria.andStatusEqualTo(ampSwapReq.getStatus());
        }
        if (ampSwapReq.getStartCtime() != null && ampSwapReq.getEndCtime() != null) {
            criteria.andCtimeBetween(new Date(ampSwapReq.getStartCtime()), new Date(ampSwapReq.getEndCtime()));
        }
        if (ampSwapReq.getStartMtime() != null && ampSwapReq.getEndMtime() != null) {
            criteria.andMtimeBetween(new Date(ampSwapReq.getStartMtime()), new Date(ampSwapReq.getEndMtime()));
        }

        PageHelper.startPage(ampSwapReq.getPage(), ampSwapReq.getPageSize(), true);
        List<TradeSwapOrder> tradeSwapOrders = defaultTradeSwapOrderMapper.selectByExample(tradeSwapOrderExample);
        PageInfo<TradeSwapOrder> pageInfo = new PageInfo<>(tradeSwapOrders);
        List<AmpSwapRes> list = swapMapStruct.tradeSwapOrder2AmpSwapRes(tradeSwapOrders);
        return PageResult.generate(pageInfo.getTotal(), pageInfo.getPageNum(), pageInfo.getPageSize(), list);
    }


    @Override
    public PageResult<AceUpSwapRes> swapHistoryForAceUp(AceUpSwapReq aceUpSwapReq) {
        TradeSwapOrderExample tradeSwapOrderExample = new TradeSwapOrderExample();
        tradeSwapOrderExample.setOrderByClause("CTIME DESC");

        TradeSwapOrderExample.Criteria criteria = tradeSwapOrderExample.createCriteria();

        if (StringUtils.isNotEmpty(aceUpSwapReq.getOrderId())) {
            criteria.andUuidEqualTo(aceUpSwapReq.getOrderId());
        }
        if (StringUtils.isNotEmpty(aceUpSwapReq.getUid())) {
            criteria.andUidEqualTo(aceUpSwapReq.getUid());
        }
        if (StringUtils.isNotEmpty(aceUpSwapReq.getMode())) {
            criteria.andModeEqualTo(aceUpSwapReq.getMode());
        }
        if (CollectionUtils.isNotEmpty(aceUpSwapReq.getFromCoinList())) {
            criteria.andFromCoinIn(aceUpSwapReq.getFromCoinList());
        }
        if (CollectionUtils.isNotEmpty(aceUpSwapReq.getToCoinList())) {
            criteria.andToCoinIn(aceUpSwapReq.getToCoinList());
        }
        if (CollectionUtils.isNotEmpty(aceUpSwapReq.getStatusList())) {
            ArrayList<String> qstatus = new ArrayList<>();
            for (String status : aceUpSwapReq.getStatusList()) {
                if (OrderStatus.AIP_LOCKED.getCode().equals(status)) {
                    qstatus.add(OrderStatus.AIP_LOCKED.getName());
                } else {
                    qstatus.add(status);
                }
            }
            criteria.andStatusIn(qstatus);
        }
        if (StringUtils.isNotEmpty(aceUpSwapReq.getSource())) {
            criteria.andSourceEqualTo(aceUpSwapReq.getSource());
        }
        if (aceUpSwapReq.getStartCtime() != null && aceUpSwapReq.getEndCtime() != null) {
            criteria.andCtimeBetween(new Date(aceUpSwapReq.getStartCtime()), new Date(aceUpSwapReq.getEndCtime()));
        }
        if (aceUpSwapReq.getStartMtime() != null && aceUpSwapReq.getEndMtime() != null) {
            criteria.andMtimeBetween(new Date(aceUpSwapReq.getStartMtime()), new Date(aceUpSwapReq.getEndMtime()));
        }
        PageHelper.startPage(aceUpSwapReq.getPage(), aceUpSwapReq.getPageSize(), true);
        List<TradeSwapOrder> tradeSwapOrders = defaultTradeSwapOrderMapper.selectByExample(tradeSwapOrderExample);
        PageInfo<TradeSwapOrder> pageInfo = new PageInfo<>(tradeSwapOrders);
        List<AceUpSwapRes> list = swapMapStruct.tradeSwapOrder2AceUpSwapRes(tradeSwapOrders);
        return PageResult.generate(pageInfo.getTotal(), pageInfo.getPageNum(), pageInfo.getPageSize(), list);
    }


    private BigDecimal getOriginPrice(TradeSwapOrder order) {
        BigDecimal userPrice = order.getOrderPrice();
        BigDecimal deductFee = BigDecimal.ONE.subtract(order.getFeeRate());
        return CommonUtils.roundDivide(userPrice, deductFee);
    }

    private BigDecimal getLockAmount(TradeSwapOrder order) {
        if (SwapType.isPayment(order.getMode())) {
            return order.getFromQuantity();
        } else {
            BigDecimal userPrice = order.getOrderPrice();
            BigDecimal getQty = order.getToQuantity();
            BigDecimal userPriceWithBuffer = userPrice.multiply(BigDecimal.ONE.subtract(PRICE_BUFFER_RATE));
            //锁定尽量往多了锁
            return getQty.divide(userPriceWithBuffer, Constants.DEFAULT_PRECISION, RoundingMode.UP);
        }
    }

    private CreateSwapReq getPlaceRequest(TradeSwapOrder order) {
        CreateSwapReq sendReq = new CreateSwapReq();
        sendReq.setTradeId(order.getUuid());
        sendReq.setFrom(order.getFromCoin());
        sendReq.setTo(order.getToCoin());
        BigDecimal originPrice = this.getOriginPrice(order);
        if (SwapType.isPayment(order.getMode())) {
            sendReq.setQuantity(order.getFromQuantity());
            originPrice = originPrice.multiply(BigDecimal.ONE.subtract(PRICE_BUFFER_RATE));
        } else {
            sendReq.setToQuantity(order.getToQuantity());
            originPrice = originPrice.multiply(BigDecimal.ONE.subtract(PRICE_BUFFER_RATE));
        }
        sendReq.setPrice(originPrice);
        return sendReq;
    }

    private CreateSwapReq getPlaceRequestForAip(TradeSwapOrder order, String tradeId) {
        CreateSwapReq sendReq = new CreateSwapReq();
        sendReq.setTradeId(tradeId);
        sendReq.setFrom(order.getFromCoin());
        sendReq.setTo(order.getToCoin());
        BigDecimal originPrice = this.getOriginPrice(order);
        if (SwapType.isPayment(order.getMode())) {
            sendReq.setQuantity(order.getFromQuantity());
            originPrice = originPrice.multiply(BigDecimal.ONE.subtract(PRICE_BUFFER_RATE));
        } else {
            sendReq.setToQuantity(order.getToQuantity());
            originPrice = originPrice.multiply(BigDecimal.ONE.subtract(PRICE_BUFFER_RATE));
        }
        sendReq.setPrice(originPrice);
        return sendReq;
    }


    private TradeSpotReq getSwapTradeReq(TradeSwapOrder order, CreateSwapRes info, String reqId) {
        String toCoin = order.getToCoin();
        TradeSpotReq tradeReq = new TradeSpotReq();
        tradeReq.setReqId(reqId);

        TradeSpotReq.Params params = new TradeSpotReq.Params();
        params.setUid(order.getUid());
        params.setTradeType("swap");
        params.setDirection(Direction.SELL.getName().toLowerCase());
        BigDecimal lockRemain = BigDecimal.ZERO;
        BigDecimal consumer = order.getFromQuantity();
        BigDecimal obtain = order.getToQuantity();
        if (!SwapType.isPayment(order.getMode())) {
            BigDecimal lockAmount = this.getLockAmount(order);
            lockRemain = lockAmount.subtract(consumer);
            if (!CommonUtils.isPositive(lockRemain)) {
                log.error("lockRemain={} calc error: {}", lockRemain, order);
                lockRemain = BigDecimal.ZERO;
            }
        } else {
            obtain = order.getToQuantity().add(order.getFee());
        }
        params.setBaseCoin(order.getFromCoin());
        params.setBaseAmount(consumer);
        params.setQuoteCoin(toCoin);
        params.setQuoteAmount(obtain);
        params.setUnlockAmount(lockRemain);
        params.setFee(order.getFee());
        params.setFeeCoin(order.getFeeCoin());

        //设置成交均价，用于计算持币均价，在新增的币种上覆盖成交均价即可
        BigDecimal avgPrice = BigDecimal.ONE;
        if (!Constants.BASE_COIN.equals(toCoin)) {
            avgPrice = CommonUtils.getMiddlePrice(toCoin + Constants.BASE_QUOTE).setScale(Constants.USD_PRICE_PRECISION,
                    RoundingMode.HALF_UP);
        }
        params.setQuoteAvgPrice(avgPrice);

        tradeReq.setParams(params);
        return tradeReq;
    }

    private TradeSpotOrderReq getSwapFreezeReq(TradeSwapOrder order, String reqId) {
        TradeSpotOrderReq sendReq = new TradeSpotOrderReq();
        sendReq.setReqId(reqId);

        TradeSpotOrderReq.Params param = new TradeSpotOrderReq.Params();
        param.setUid(order.getUid());
        param.setCoin(order.getFromCoin());
        param.setCompetitorCoin(order.getToCoin());
        BigDecimal lockAmount = this.getLockAmount(order);
        param.setAmount(lockAmount);
        sendReq.setParams(param);
        return sendReq;
    }

    private void setOrderFailMsg(TradeSwapOrder order) {
        order.setStatus(OrderStatus.CANCELED.getName());
        order.setMemo(I18nEnum.TRADING_SWAP_ORDER_MEMO_NO_BALANCE.getKey());
        updateSwapOrder(order);
    }

    private boolean isExecuteTimeOut(TradeSwapOrder order) {
        long begin = order.getCtime().getTime();
        long nowTime = CommonUtils.getNowTime().getTime();
        return nowTime - begin > MAX_TIMEOUT;
    }

    private void setTradedResult(TradeSwapOrder order, CreateSwapRes info) {
        BigDecimal payFee;
        if (SwapType.isPayment(order.getMode())) {
            BigDecimal quoteQty = info.getToFilled();
            payFee = quoteQty.multiply(order.getFeeRate()).setScale(Constants.DEFAULT_PRECISION, RoundingMode.UP);
            order.setToQuantity(quoteQty.subtract(payFee));
        } else {
            BigDecimal baseQty = info.getFilled();
            payFee = baseQty.multiply(order.getFeeRate()).setScale(Constants.DEFAULT_PRECISION, RoundingMode.UP);
            order.setFromQuantity(baseQty.add(payFee));
        }

        order.setMemo("");
        order.setFee(CommonUtils.convertData(payFee));
        order.setDealPrice(info.getFilledPrice().multiply(BigDecimal.ONE.subtract(order.getFeeRate())));
        order.setMtime(CommonUtils.getNowTime());
    }

    private void updateSwapOrder(TradeSwapOrder order) {
        order.setMtime(CommonUtils.getNowTime());
        if (order.getId() != null) {
            defaultTradeSwapOrderMapper.updateByPrimaryKeySelective(order);
        } else {
            TradeSwapOrderExample example = new TradeSwapOrderExample();
            example.createCriteria().andUuidEqualTo(order.getUuid());
            defaultTradeSwapOrderMapper.updateByExampleSelective(order, example);
        }
    }

    private void updateOrderStatus(TradeSwapOrder order, OrderStatus status) {
        order.setStatus(status.getName());
        updateSwapOrder(order);
    }

    private CreateSwapRes convertTradedData(SwapByIdRes resp) {
        CreateSwapRes data = new CreateSwapRes();
        data.setStatus(resp.getStatus());
        data.setFilled(resp.getFilled());
        data.setToFilled(resp.getToFilled());
        data.setFilledPrice(resp.getFilledPrice());
        return data;
    }

    private TradeTransaction generateTradeTransaction(TradeSwapOrder order) {
        TradeTransaction trade = new TradeTransaction();
        trade.setUid(order.getUid());
        trade.setUuid(order.getUuid());
        trade.setOrderId(order.getUuid());
        trade.setType(Constants.SWAP_TYPE);
        trade.setOrderType(order.getMode());

        String symbol = order.getFromCoin() + "_" + order.getToCoin();
        trade.setSymbol(symbol);
        trade.setDirection(Direction.SELL.getName());
        trade.setBaseQuantity(order.getFromQuantity());
        trade.setQuoteQuantity(order.getToQuantity());
        trade.setPrice(order.getDealPrice());
        trade.setSource(order.getSource());
        trade.setFee(order.getFee());
        trade.setFeeCoin(order.getToCoin());
        trade.setAssetStatus(AssetStatus.COMPLETED.name());
        trade.setPdtStatus(PdtStatus.COMPLETED.name());
        Date date = new Date();
        trade.setCtime(date);
        trade.setMtime(date);
        return trade;
    }

    /**
     * 根据order更新TradeTransaction，
     * 并COMPLETED-》AssetStatus、PdtStatus
     *
     * @param tradeTransaction
     * @param order
     * @return
     */
    private TradeTransaction updateDoneStatusTradeTransactionForAip(TradeTransaction tradeTransaction, TradeSwapOrder order) {
        tradeTransaction.setUid(order.getUid());
        tradeTransaction.setUuid(tradeTransaction.getUuid());
        tradeTransaction.setOrderId(order.getUuid());
        tradeTransaction.setType(Constants.SWAP_TYPE);
        tradeTransaction.setOrderType(order.getMode());

        String symbol = order.getFromCoin() + "_" + order.getToCoin();
        tradeTransaction.setSymbol(symbol);
        tradeTransaction.setDirection(Direction.SELL.getName());
        tradeTransaction.setBaseQuantity(order.getFromQuantity());
        tradeTransaction.setQuoteQuantity(order.getToQuantity());
        tradeTransaction.setPrice(order.getDealPrice());
        tradeTransaction.setSource(order.getSource());
        tradeTransaction.setFee(order.getFee());
        tradeTransaction.setFeeCoin(order.getToCoin());
        tradeTransaction.setAssetStatus(AssetStatus.COMPLETED.name());
        tradeTransaction.setPdtStatus(PdtStatus.COMPLETED.name());
        Date date = new Date();
        tradeTransaction.setMtime(date);
        return tradeTransaction;
    }

    /**
     * 定投业务swap的Transaction
     *
     * @param order
     * @return
     */
    private TradeTransaction generateTradeTransactionForAip(TradeSwapOrder order) {
        TradeTransaction trade = new TradeTransaction();
        trade.setUid(order.getUid());
        trade.setUuid(CommonUtils.generateUUID());
        trade.setOrderId(order.getUuid());
        trade.setType(Constants.SWAP_TYPE);
        trade.setOrderType(order.getMode());

        String symbol = order.getFromCoin() + "_" + order.getToCoin();
        trade.setSymbol(symbol);
        trade.setDirection(Direction.SELL.getName());
        trade.setBaseQuantity(order.getFromQuantity());
        trade.setQuoteQuantity(order.getToQuantity());
        trade.setPrice(CommonUtils.ZERO_NUM);
        trade.setSource(order.getSource());
        trade.setFee(order.getFee());
        trade.setFeeCoin(order.getToCoin());
        trade.setAssetStatus(AssetStatus.PENDING.name());
        trade.setPdtStatus(PdtStatus.PENDING.name());
        Date date = new Date();
        trade.setCtime(date);
        trade.setMtime(date);
        return trade;
    }

    private BigDecimal getUserFeeRate(@Nullable String userId) {
        UserFeeConfigRate userFeeConfigRate = tradeFeeConfigService.selectUserFeeConfig(userId);
        return userFeeConfigRate.getSwapFeeRate();
    }

    private boolean isLoanType(String source) {
        SourceType srcType = SourceType.getByName(source);
        return SourceType.isLoan(srcType);
    }

    private void noticeMessage(TradeSwapOrder order, boolean isLiteMarketSwap) {
        SwapNotice msg = new SwapNotice();
        msg.setOrder(order.getUuid());
        msg.setStatus(order.getStatus());
        msg.setInfo(order.getMemo());

        WsPushMessage<SwapNotice> sendData = WsPushMessage.buildAllConsumersMessage(
                order.getUid(), PushEventEnum.SWAP_ORDER_UPDATE, msg);
        pushComponent.pushWsMessage(sendData);
        // TODO: available amount
        if (isLiteMarketSwap) {
            sensorsTraceService.marketStatusChange(order, BigDecimal.ZERO);
        } else {
            sensorsTraceService.swapSubmit(order, BigDecimal.ZERO);
        }
    }

}
