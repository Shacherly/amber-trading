package com.google.backend.trading.transaction;

import com.google.backend.trading.constant.Constants;
import com.google.backend.trading.dao.mapper.DefaultTradeMarginOrderMapper;
import com.google.backend.trading.dao.mapper.DefaultTradeMarginOrderModificationMapper;
import com.google.backend.trading.dao.mapper.DefaultTradePositionFundingCostMapper;
import com.google.backend.trading.dao.mapper.DefaultTradePositionMapper;
import com.google.backend.trading.dao.mapper.DefaultTradeSpotMarginMiddleOrderMapper;
import com.google.backend.trading.dao.mapper.DefaultTradeTransactionMapper;
import com.google.backend.trading.dao.mapper.TradeFeeConfigMapper;
import com.google.backend.trading.dao.mapper.TradePositionMapper;
import com.google.backend.trading.dao.model.TradeMarginOrder;
import com.google.backend.trading.dao.model.TradeMarginOrderExample;
import com.google.backend.trading.dao.model.TradeMarginOrderModification;
import com.google.backend.trading.dao.model.TradeMarginOrderModificationExample;
import com.google.backend.trading.dao.model.TradePosition;
import com.google.backend.trading.dao.model.TradePositionExample;
import com.google.backend.trading.dao.model.TradePositionFundingCost;
import com.google.backend.trading.dao.model.TradePositionFundingCostExample;
import com.google.backend.trading.dao.model.TradeSpotMarginMiddleOrder;
import com.google.backend.trading.dao.model.TradeSpotMarginMiddleOrderExample;
import com.google.backend.trading.dao.model.TradeTransaction;
import com.google.backend.trading.dao.model.TradeUserTradeSetting;
import com.google.backend.trading.exception.BusinessException;
import com.google.backend.trading.exception.BusinessExceptionEnum;
import com.google.backend.trading.exception.LockException;
import com.google.backend.trading.mapstruct.web.WebMapStruct;
import com.google.backend.trading.model.funding.dto.FundingCostStatus;
import com.google.backend.trading.model.margin.PositionStatus;
import com.google.backend.trading.model.margin.dto.MarginOrderCancel;
import com.google.backend.trading.model.trade.AssetStatus;
import com.google.backend.trading.model.trade.Direction;
import com.google.backend.trading.model.trade.OrderStatus;
import com.google.backend.trading.model.trade.OrderType;
import com.google.backend.trading.model.trade.PdtStatus;
import com.google.backend.trading.model.trade.SourceType;
import com.google.backend.trading.model.trade.TradeStrategy;
import com.google.backend.trading.model.trade.TradeTerminator;
import com.google.backend.trading.model.trade.TransactionType;
import com.google.backend.trading.model.trade.fee.UserFeeConfigRate;
import com.google.backend.trading.model.web.MiddleOrderType;
import com.google.backend.trading.push.PushComponent;
import com.google.backend.trading.push.PushEventEnum;
import com.google.backend.trading.push.WsPushMessage;
import com.google.backend.trading.service.OrderRequest;
import com.google.backend.trading.service.TradeFeeConfigService;
import com.google.backend.trading.service.TradeTransactionService;
import com.google.backend.trading.service.UserService;
import com.google.backend.trading.service.UserTradeSettingService;
import com.google.backend.trading.util.AlarmLogUtil;
import com.google.backend.trading.util.CommonUtils;
import com.google.backend.trading.util.ListUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Slf4j
@Component
public class MarginTransaction {
    @Resource
    private DefaultTradeSpotMarginMiddleOrderMapper defaultTradeSpotMarginMiddleOrderMapper;

    @Resource
    private DefaultTradeMarginOrderMapper defaultTradeMarginOrderMapper;

    @Resource
    private DefaultTradeTransactionMapper defaultTradeTransactionMapper;
    @Resource
    private TradeTransactionService tradeTransactionService;

    @Resource
    private DefaultTradeMarginOrderModificationMapper defaultTradeMarginOrderModificationMapper;

    @Resource
    private DefaultTradePositionMapper defaultTradePositionMapper;

    @Resource
    private TradeFeeConfigMapper tradeFeeConfigMapper;

    @Autowired
    private TradeFeeConfigService tradeFeeConfigService;

    @Resource
    private DefaultTradePositionFundingCostMapper defaultPositionFundingCostMapper;

    @Resource
    private TradePositionMapper positionMapper;
    @Resource
    private OrderRequest orderRequest;

    @Autowired
    private UserTradeSettingService userTradeSettingService;

    @Autowired
    private UserService userService;

    @Resource
    private WebMapStruct webMapStruct;
    @Resource
    private PushComponent pushComponent;
    @Transactional(rollbackFor = Throwable.class)


    public void insertOrder(TradeMarginOrder order) {
        defaultTradeMarginOrderMapper.insertSelective(order);
        TradeSpotMarginMiddleOrder middleOrder = new TradeSpotMarginMiddleOrder();
        middleOrder.setUid(order.getUid());
        middleOrder.setOrderId(order.getUuid());
        middleOrder.setType(MiddleOrderType.MARGIN.name());
        middleOrder.setSymbol(order.getSymbol());
        middleOrder.setDirection(order.getDirection());
        middleOrder.setStatus(order.getStatus());
        defaultTradeSpotMarginMiddleOrderMapper.insertSelective(middleOrder);
    }

    public TradeMarginOrder getMarginOrder(TradeMarginOrder order) {
        TradeMarginOrderExample example = new TradeMarginOrderExample();
        TradeMarginOrderExample.Criteria criteria = example.createCriteria();
        criteria.andUuidEqualTo(order.getUuid());
        criteria.andUidEqualTo(order.getUid());
        List<TradeMarginOrder> allList = defaultTradeMarginOrderMapper.selectByExample(example);
        return allList.get(0);
    }

    @Transactional(rollbackFor = Throwable.class)
    public List<TradeTransaction> updateTransaction(TradeMarginOrder order, BigDecimal filledQuantity,
                                                    BigDecimal filledPrice, TradeTransaction tradeTransaction) {
        BigDecimal fee = CommonUtils.ZERO_NUM;
        SourceType source = SourceType.getByName(order.getSource());
        if (source != SourceType.FORCE_CLOSE && source != SourceType.OTC_SHOP) {
            UserFeeConfigRate userFeeConfigRate = tradeFeeConfigService.selectUserFeeConfig(order.getUid());
            if (CommonUtils.isPositive(userFeeConfigRate.getMarginFeeRate())) {
                fee = filledQuantity.multiply(filledPrice).multiply(userFeeConfigRate.getMarginFeeRate());
                String quoteCoin = CommonUtils.getQuoteCoin(order.getSymbol());
                if (!Objects.equals(quoteCoin, Constants.BASE_COIN)) {
                    fee = fee.multiply(CommonUtils.getMiddlePrice(quoteCoin + Constants.BASE_QUOTE));
                }
            }
        }
        List<TradeTransaction> transactions = new ArrayList<>();
        TradePosition position = getActivePositionForUpdate(order.getUid(), order.getSymbol());
        // 当前无仓位，则生成一条开仓记录
        if (position == null) {
            TradePosition tradePosition = openPosition(filledQuantity, filledPrice, fee, order);
            TradeTransaction transaction = updateTransactionAndDonePDTStatus(tradeTransaction, filledQuantity, filledPrice, fee,
                    CommonUtils.ZERO_NUM, TransactionType.OPEN_POSITION, order,tradePosition.getUuid());
            transactions.add(transaction);
        }
        else {
            // 当前仓位方向和订单方向一致，则生成一条加仓记录
            if (Objects.equals(position.getDirection(), order.getDirection())) {
                TradePosition tradePosition = addPosition(filledQuantity, filledPrice, fee, position);
                TradeTransaction transaction = updateTransactionAndDonePDTStatus(tradeTransaction, filledQuantity, filledPrice, fee,
                        CommonUtils.ZERO_NUM, TransactionType.ADD_POSITION, order,tradePosition.getUuid());
                transactions.add(transaction);
            }
            else {
                // 当前仓位方向和订单方向相反，生成一条平仓/减仓记录，如果成交数量大于持仓数量，再生成一条反向开仓记录
                BigDecimal closeQuantity = filledQuantity.min(position.getQuantity());
                BigDecimal openQuantity = filledQuantity.subtract(position.getQuantity()).max(CommonUtils.ZERO_NUM);
                BigDecimal pnl;
                if (Objects.equals(position.getDirection(), Direction.BUY.getName())) {
                    pnl = filledPrice.subtract(position.getPrice()).multiply(closeQuantity).setScale(Constants.DEFAULT_PRECISION, RoundingMode.HALF_UP);
                }
                else {
                    pnl = position.getPrice().subtract(filledPrice).multiply(closeQuantity).setScale(Constants.DEFAULT_PRECISION, RoundingMode.HALF_UP);
                }
                String quoteCoin = CommonUtils.getQuoteCoin(position.getSymbol());
                BigDecimal pnlUsd = pnl;
                if (!Objects.equals(quoteCoin, Constants.BASE_COIN)) {
                    pnlUsd = pnlUsd.multiply(CommonUtils.getMiddlePrice(quoteCoin + Constants.BASE_QUOTE));
                }
                BigDecimal closeFee = fee;
                if (CommonUtils.isPositive(position.getQuantity().subtract(closeQuantity))) {
                    TradePosition tradePosition = reducePosition(closeQuantity, closeFee, pnlUsd, position);
                    TradeTransaction transaction = updateTransactionAndDonePDTStatus(tradeTransaction, closeQuantity, filledPrice, closeFee,
                            pnl, TransactionType.REDUCE_POSITION, order,tradePosition.getUuid());
                    transactions.add(transaction);
                }
                else {
                    closeFee = CommonUtils.roundDivide(closeQuantity, filledQuantity).multiply(fee);
                    TradePosition tradePosition = closePosition(closeQuantity, closeFee, pnlUsd, position);
                    TradeTransaction transaction = updateTransactionAndDonePDTStatus(tradeTransaction, closeQuantity, filledPrice, closeFee,
                            pnl, TransactionType.CLOSE_POSITION, order,tradePosition.getUuid());
                    transactions.add(transaction);
                }
                if (CommonUtils.isPositive(openQuantity)) {
                    BigDecimal openFee = CommonUtils.roundDivide(openQuantity, filledQuantity).multiply(fee);
                    TradePosition tradePosition = openPosition(openQuantity, filledPrice, openFee, order);

                    TradeTransaction transaction = insertTransaction(CommonUtils.generateUUID(), openQuantity, filledPrice, openFee,
                            CommonUtils.ZERO_NUM, TransactionType.OPEN_POSITION, order,tradePosition.getUuid());
                    transaction.setPdtStatus(PdtStatus.COMPLETED.name());
                    tradeTransactionService.updateTransactionById(transaction);
                    transactions.add(transaction);
                }
            }
        }
        BigDecimal[] orderQuantityPrice = calcTotalQuantityPrice(order.getQuantityFilled(), order.getFilledPrice(),
                filledQuantity, filledPrice);
        order.setQuantityFilled(orderQuantityPrice[0]);
        order.setFilledPrice(orderQuantityPrice[1]);
        order.setFee(order.getFee().add(fee));

        if (source != SourceType.OTC_SHOP) {
            if (order.getQuantity().compareTo(order.getQuantityFilled()) < 1) {
                order.setStatus(OrderStatus.COMPLETED.getName());
            } else if (CommonUtils.isSyncOrder(OrderType.getByName(order.getType()), TradeStrategy.getByName(order.getStrategy()), SourceType.getByName(order.getSource()))) {
                orderRequest.setMarginFinishStatus(order);
            } else {
                order.setStatus(OrderStatus.EXECUTING.getName());
            }
        }
        updateOrder(order);
        return transactions;
    }

    private TradePosition getActivePositionForUpdate(String uid, String symbol) {
        TradePositionExample positionExample = new TradePositionExample();
        TradePositionExample.Criteria positionCriteria = positionExample.createCriteria();
        positionCriteria.andUidEqualTo(uid);
        positionCriteria.andSymbolEqualTo(symbol);
        positionCriteria.andStatusEqualTo(PositionStatus.ACTIVE.name());
        positionExample.setOrderByClause("id for update");
        List<TradePosition> positions = defaultTradePositionMapper.selectByExample(positionExample);
        if (!ListUtil.isEmpty(positions)) {
            return positions.get(0);
        }
        return null;
    }

    @Transactional(rollbackFor = Throwable.class)
    public void settlePosition(String positionId, TradeTransaction transaction) {
        TradePositionExample positionExample = new TradePositionExample();
        TradePositionExample.Criteria positionCriteria = positionExample.createCriteria();
        positionCriteria.andUuidEqualTo(positionId);
        positionCriteria.andStatusEqualTo(PositionStatus.ACTIVE.name());
        positionExample.setOrderByClause("id for update");
        List<TradePosition> positions = defaultTradePositionMapper.selectByExample(positionExample);
        if (ListUtil.isEmpty(positions)) {
            throw new BusinessException(BusinessExceptionEnum.POSITION_CHANGE_OR_NOT_FOUND);
        }
        TradePosition position = positions.get(0);
        if (position.getPrice().compareTo(transaction.getPrice()) != 0) {
            throw new RuntimeException("price wrong");
        }
        position.setQuantity(position.getQuantity().subtract(transaction.getBaseQuantity()));
        if (position.getQuantity().compareTo(CommonUtils.ZERO_NUM) < 0) {
            throw new RuntimeException("quantity exceed");
        } else if (position.getQuantity().compareTo(CommonUtils.ZERO_NUM) == 0) {
            position.setStatus(PositionStatus.CLOSE.name());
        }
        TradeTransaction update = new TradeTransaction();
        update.setId(transaction.getId());
        update.setAssetStatus(AssetStatus.COMPLETED.name());
        defaultTradeTransactionMapper.updateByPrimaryKeySelective(update);
        defaultTradePositionMapper.updateByPrimaryKeySelective(position);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void insertModificationAndUpdateOrder(TradeMarginOrder originOrder, TradeMarginOrder modifyData) {
        TradeMarginOrderModificationExample example = new TradeMarginOrderModificationExample();
        TradeMarginOrderModificationExample.Criteria criteria = example.createCriteria();
        criteria.andOrderIdEqualTo(originOrder.getUuid());
        List<TradeMarginOrderModification> modifications = defaultTradeMarginOrderModificationMapper.selectByExample(example);
        // 如果没有修改记录，需要插入一条order的创建的记录到modification表
        if (ListUtil.isEmpty(modifications)) {
            TradeMarginOrderModification beforeModification = new TradeMarginOrderModification();
            beforeModification.setOrderId(originOrder.getUuid());
            beforeModification.setQuantity(originOrder.getQuantity());
            beforeModification.setPrice(originOrder.getPrice());
            beforeModification.setTriggerPrice(originOrder.getTriggerPrice());
            beforeModification.setTriggerCompare(originOrder.getTriggerCompare());
            beforeModification.setCurrentStatus(OrderType.isTriggerOrder(originOrder.getType()) ? OrderStatus.PRE_TRIGGER.name() : OrderStatus.EXECUTING.name());
            beforeModification.setNotes(originOrder.getNotes());
            beforeModification.setUuid(CommonUtils.generateUUID());
            beforeModification.setCtime(originOrder.getCtime());
            defaultTradeMarginOrderModificationMapper.insertSelective(beforeModification);
        }
        OrderStatus status = OrderStatus.getByName(originOrder.getStatus());
        status = status == OrderStatus.LOCKED ? OrderStatus.EXECUTING : OrderStatus.PRE_TRIGGER;
        TradeMarginOrderModification modification = new TradeMarginOrderModification();
        modification.setOrderId(originOrder.getUuid());
        modification.setQuantity(modifyData.getQuantity());
        modification.setPrice(modifyData.getPrice());
        modification.setTriggerPrice(modifyData.getTriggerPrice());
        modification.setTriggerCompare(modifyData.getTriggerCompare());
        modification.setCurrentQuantityFilled(originOrder.getQuantityFilled());
        modification.setCurrentStatus(status.getName());
        modification.setNotes(modifyData.getNotes());
        modification.setUuid(CommonUtils.generateUUID());
        defaultTradeMarginOrderModificationMapper.insertSelective(modification);
        modifyData.setId(originOrder.getId());
        modifyData.setStatus(status.getName());
        defaultTradeMarginOrderMapper.updateByPrimaryKeySelective(modifyData);
        this.orderUpdateNotice(originOrder.getUid(), originOrder.getUuid());
    }

    @Transactional(rollbackFor = Throwable.class)
    public TradeMarginOrder cancelOrder(MarginOrderCancel args) throws LockException {
        TradeMarginOrderExample example = new TradeMarginOrderExample();
        TradeMarginOrderExample.Criteria criteria = example.createCriteria();
        criteria.andUuidEqualTo(args.getOrderId());
        if (args.getTerminator() == TradeTerminator.CLIENT) {
            criteria.andUidEqualTo(args.getUid());
        }
        example.setOrderByClause("id for share");
        List<TradeMarginOrder> orderList = defaultTradeMarginOrderMapper.selectByExample(example);
        if (orderList.isEmpty()) {
            throw new BusinessException(BusinessExceptionEnum.ORDER_CHANGE_OR_NOT_FOUND);
        }
        TradeMarginOrder order = orderList.get(0);
        if (!SourceType.isFromUser(order.getSource())) {
            throw new BusinessException(BusinessExceptionEnum.UNEXPECTED_ERROR);
        }
        OrderStatus status = OrderStatus.getByName(order.getStatus());
        if (status == OrderStatus.CANCELED || status == OrderStatus.PART_CANCELED) {
            return null;
        }
        if (status == OrderStatus.COMPLETED) {
            throw new BusinessException(BusinessExceptionEnum.ORDER_CHANGE_OR_NOT_FOUND);
        }
        if (status == OrderStatus.PENDING || status == OrderStatus.LOCKED) {
            throw new LockException();
        }
        orderRequest.setMarginFinishStatus(order);
        order.setTerminator(args.getTerminator().getCode());
        updateOrder(order);
        return order;
    }

    @Transactional(rollbackFor = Throwable.class)
    public TradeMarginOrder updateOrderTransactional(TradeMarginOrder order) {
        return updateOrder(order);
    }

    private TradeMarginOrder updateOrder(TradeMarginOrder order) {
        order.setMtime(CommonUtils.getNowTime());
        boolean success = 1 == defaultTradeMarginOrderMapper.updateByPrimaryKeySelective(order);
        if (!success) {
            AlarmLogUtil.alarm("margin order update err, data = {}", order);
            throw new BusinessException(BusinessExceptionEnum.ORDER_CHANGE_OR_NOT_FOUND);
        }
        TradeSpotMarginMiddleOrderExample example = new TradeSpotMarginMiddleOrderExample();
        TradeSpotMarginMiddleOrderExample.Criteria criteria = example.createCriteria();
        criteria.andTypeEqualTo(MiddleOrderType.MARGIN.name());
        criteria.andOrderIdEqualTo(order.getUuid());
        TradeSpotMarginMiddleOrder middleOrder = new TradeSpotMarginMiddleOrder();
        middleOrder.setStatus(order.getStatus());
        middleOrder.setMtime(order.getMtime());
        success = 1 == defaultTradeSpotMarginMiddleOrderMapper.updateByExampleSelective(middleOrder, example);
        if (!success) {
            AlarmLogUtil.alarm("middle margin order update err, data = {}", order);
            throw new BusinessException(BusinessExceptionEnum.ORDER_CHANGE_OR_NOT_FOUND);
        }
        this.orderUpdateNotice(order.getUid(), order.getUuid());
        return order;
    }

    private TradePosition openPosition(BigDecimal quantity, BigDecimal price, BigDecimal fee, TradeMarginOrder order) {
        TradePosition position = new TradePosition();
        //是否开启 全局自动交割
        TradeUserTradeSetting tradeUserTradeSetting = userTradeSettingService.queryTradeSettingByUid(order.getUid());
        position.setAutoSettle(tradeUserTradeSetting.getAutoSettle());
        position.setUuid(CommonUtils.generateUUID());
        position.setUid(order.getUid());
        position.setSymbol(order.getSymbol());
        position.setDirection(order.getDirection());
        position.setStatus(PositionStatus.ACTIVE.name());
        position.setQuantity(quantity);
        position.setPrice(price);
        position.setPnl(BigDecimal.ZERO.subtract(fee));
        position.setMaxQuantity(order.getQuantity());
        defaultTradePositionMapper.insertSelective(position);
        return position;
    }

    public TradePosition addPosition(BigDecimal quantity, BigDecimal price, BigDecimal fee, TradePosition position) {
        BigDecimal[] positionQuantityPrice = calcTotalQuantityPrice(position.getQuantity(), position.getPrice(), quantity, price);
        position.setQuantity(positionQuantityPrice[0]);
        position.setMaxQuantity(position.getMaxQuantity().max(position.getQuantity()));
        position.setPrice(positionQuantityPrice[1]);
        position.setPnl(position.getPnl().subtract(fee));
        position.setMtime(CommonUtils.getNowTime());
        defaultTradePositionMapper.updateByPrimaryKeySelective(position);
        return position;
    }

    public TradePosition closePosition(BigDecimal quantity, BigDecimal fee, BigDecimal pnlUsd, TradePosition position) {
        position.setQuantity(position.getQuantity().subtract(quantity));
        position.setPnl(position.getPnl().add(pnlUsd).subtract(fee));
        position.setStatus(PositionStatus.CLOSE.name());
        position.setMtime(CommonUtils.getNowTime());
        defaultTradePositionMapper.updateByPrimaryKeySelective(position);
        return position;
    }

    public TradePosition reducePosition(BigDecimal quantity, BigDecimal fee, BigDecimal pnlUsd, TradePosition position) {
        position.setQuantity(position.getQuantity().subtract(quantity));
        position.setPnl(position.getPnl().add(pnlUsd).subtract(fee));
        position.setMtime(CommonUtils.getNowTime());
        defaultTradePositionMapper.updateByPrimaryKeySelective(position);
        return position;
    }

    private TradeTransaction insertTransaction(String tradeId, BigDecimal quantity, BigDecimal price,
                                               BigDecimal fee, BigDecimal pnl, TransactionType type,
                                               TradeMarginOrder order,String positionId) {
        TradeTransaction transaction = new TradeTransaction();
        transaction.setUuid(tradeId);
        transaction.setUid(order.getUid());
        transaction.setOrderId(order.getUuid());
        //fixed 仓位id取值错误
        transaction.setPositionId(positionId);
        transaction.setOrderType(order.getType());
        transaction.setType(type.name());
        transaction.setSymbol(order.getSymbol());
        transaction.setDirection(order.getDirection());
        transaction.setBaseQuantity(quantity);
        transaction.setQuoteQuantity(quantity.multiply(price).setScale(Constants.DEFAULT_PRECISION, RoundingMode.HALF_UP));
        transaction.setPrice(price);
        transaction.setFee(fee);
        transaction.setFeeCoin(Constants.BASE_COIN);
        transaction.setPnl(pnl);
        transaction.setSource(order.getSource());
        transaction.setAssetStatus(AssetStatus.PENDING.name());
        Date date = new Date();
        transaction.setMtime(date);
        transaction.setCtime(date);
        defaultTradeTransactionMapper.insertSelective(transaction);
        this.transactionNewNotice(transaction);
        return transaction;
    }
    private TradeTransaction updateTransactionAndDonePDTStatus(TradeTransaction transaction, BigDecimal quantity, BigDecimal price,
                                                               BigDecimal fee, BigDecimal pnl, TransactionType type,
                                                               TradeMarginOrder order, String positionId) {
        transaction.setUid(order.getUid());
        transaction.setOrderId(order.getUuid());
        //fixed 仓位id取值错误
        transaction.setPositionId(positionId);
        transaction.setOrderType(order.getType());
        transaction.setType(type.name());
        transaction.setSymbol(order.getSymbol());
        transaction.setDirection(order.getDirection());
        transaction.setBaseQuantity(quantity);
        transaction.setQuoteQuantity(quantity.multiply(price).setScale(Constants.DEFAULT_PRECISION, RoundingMode.HALF_UP));
        transaction.setPrice(price);
        transaction.setFee(fee);
        transaction.setFeeCoin(Constants.BASE_COIN);
        transaction.setPnl(pnl);
        transaction.setSource(order.getSource());
        transaction.setAssetStatus(AssetStatus.PENDING.name());
        transaction.setPdtStatus(PdtStatus.COMPLETED.name());
        Date date = new Date();
        transaction.setMtime(date);
        tradeTransactionService.updateTransactionById(transaction);
//        this.transactionNewNotice(transaction); todo update 是否需要 push？
        return transaction;
    }

    private BigDecimal[] calcTotalQuantityPrice(BigDecimal orgQuantity, BigDecimal orgPrice,
                                                BigDecimal filledQuantity, BigDecimal filledPrice) {
        BigDecimal totalQuantity = orgQuantity.add(filledQuantity);
        BigDecimal totalPrice;
        if (null != orgPrice) {
            if (CommonUtils.isPositive(totalQuantity)) {
                totalPrice = orgQuantity.multiply(orgPrice).add(filledQuantity.multiply(filledPrice)).divide(totalQuantity, RoundingMode.HALF_UP);
            } else {
                totalPrice = orgPrice;
            }
        } else {
            totalPrice = filledPrice;
        }
        BigDecimal[] result = new BigDecimal[2];
        result[0] = totalQuantity;
        result[1] = totalPrice;
        return result;
    }



    @Transactional(rollbackFor = Throwable.class)
    public boolean updatePositionFundingCost(String positionId, Long fundingCostId, BigDecimal pnl) {
        TradePositionFundingCost update = new TradePositionFundingCost();
        update.setStatus(FundingCostStatus.COMPLETED.getName());
        update.setMtime(new Date());
        TradePositionFundingCostExample example = new TradePositionFundingCostExample();
        example.createCriteria().andIdEqualTo(fundingCostId).andStatusEqualTo(FundingCostStatus.PENDING.getName());
        boolean success = 1 == defaultPositionFundingCostMapper.updateByExampleSelective(update, example);
        if (success) {
            success = 1 == positionMapper.updatePnl(positionId, pnl);
        }
        return success;
    }

    /**
     * 交易创建更新
     *
     * @param transaction
     */
    public void transactionNewNotice(TradeTransaction transaction) {
        pushComponent.pushWsMessage(WsPushMessage.buildAllConsumersMessage(transaction.getUid(),
                PushEventEnum.WEB_TRANSACTION_NEW,
                webMapStruct.tradeTransaction2TransactionResEvent(transaction)));
    }

    /**
     * 修改订单通知
     * @param uid
     * @param orderId
     */
    private void orderUpdateNotice(String uid,String orderId) {
        try {
            TradeMarginOrderExample example = new TradeMarginOrderExample();
            example.createCriteria().andUuidEqualTo(orderId);
            List<TradeMarginOrder> orderList = defaultTradeMarginOrderMapper.selectByExample(example);
            if (orderList.isEmpty()) {
                throw new BusinessException(BusinessExceptionEnum.ORDER_CHANGE_OR_NOT_FOUND);
            }
            TradeMarginOrder order = orderList.get(0);
            if(SourceType.isFromUser(order.getSource())) {
                Locale locale = userService.locale(uid);
                LocaleContextHolder.setLocale(locale);
                pushComponent.pushWsMessage(WsPushMessage.buildAllConsumersMessage(uid,
                        PushEventEnum.WEB_ORDER_UPDATE, webMapStruct.tradeMarginOrder2OrderResEvent(order)));
            }
        } catch (Exception e) {
            AlarmLogUtil.alarm("orderUpdateNotice err, cause = {}", ExceptionUtils.getRootCauseMessage(e), e);
        }
    }

}
