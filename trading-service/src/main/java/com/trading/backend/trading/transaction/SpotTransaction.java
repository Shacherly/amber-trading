package com.google.backend.trading.transaction;

import com.google.backend.trading.constant.Constants;
import com.google.backend.trading.dao.mapper.DefaultTradeSpotMarginMiddleOrderMapper;
import com.google.backend.trading.dao.mapper.DefaultTradeSpotOrderMapper;
import com.google.backend.trading.dao.mapper.DefaultTradeSpotOrderModificationMapper;
import com.google.backend.trading.dao.model.TradeSpotMarginMiddleOrder;
import com.google.backend.trading.dao.model.TradeSpotMarginMiddleOrderExample;
import com.google.backend.trading.dao.model.TradeSpotOrder;
import com.google.backend.trading.dao.model.TradeSpotOrderExample;
import com.google.backend.trading.dao.model.TradeSpotOrderModification;
import com.google.backend.trading.dao.model.TradeTransaction;
import com.google.backend.trading.exception.BusinessException;
import com.google.backend.trading.exception.BusinessExceptionEnum;
import com.google.backend.trading.mapstruct.web.WebMapStruct;
import com.google.backend.trading.model.spot.dto.SpotOrderCancel;
import com.google.backend.trading.model.trade.OrderStatus;
import com.google.backend.trading.model.trade.SourceType;
import com.google.backend.trading.model.trade.TradeTerminator;
import com.google.backend.trading.model.web.MiddleOrderType;
import com.google.backend.trading.push.PushComponent;
import com.google.backend.trading.push.PushEventEnum;
import com.google.backend.trading.push.WsPushMessage;
import com.google.backend.trading.service.OrderRequest;
import com.google.backend.trading.service.TradeTransactionService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Component
public class SpotTransaction {
    @Resource
    private OrderRequest orderRequest;
    @Resource
    private DefaultTradeSpotMarginMiddleOrderMapper tradeSpotMarginMiddleOrderMapper;
    @Resource
    private DefaultTradeSpotOrderMapper tradeSpotOrderMapper;
    @Resource
    private TradeTransactionService tradeTransactionService;

    @Resource
    private DefaultTradeSpotOrderModificationMapper tradeSpotOrderModificationMapper;
    @Resource
    private WebMapStruct webMapStruct;
    @Resource
    private PushComponent pushComponent;


    @Transactional(rollbackFor = Throwable.class)
    public void insertOrder(TradeSpotOrder order) {
        tradeSpotOrderMapper.insertSelective(order);
        TradeSpotMarginMiddleOrder middleOrder = new TradeSpotMarginMiddleOrder();
        middleOrder.setUid(order.getUid());
        middleOrder.setOrderId(order.getUuid());
        middleOrder.setType(MiddleOrderType.SPOT.name());
        middleOrder.setSymbol(order.getSymbol());
        middleOrder.setDirection(order.getDirection());
        middleOrder.setStatus(order.getStatus());
        tradeSpotMarginMiddleOrderMapper.insertSelective(middleOrder);
    }

    @Transactional(rollbackFor = Throwable.class)
    public boolean cancelOrderToDb(TradeSpotOrder order) {
        String uuid = order.getUuid();
        BigDecimal quantityFilled = order.getQuantityFilled();
        Date mtime = new Date();
        TradeSpotOrder update = new TradeSpotOrder();
        update.setMtime(mtime);
        update.setTerminator(order.getTerminator());
        update.setStatus(order.getStatus());

        TradeSpotOrderExample example = new TradeSpotOrderExample();
        TradeSpotOrderExample.Criteria criteria = example.createCriteria();
        criteria.andUuidEqualTo(uuid).andQuantityFilledEqualTo(quantityFilled).andStatusIn(OrderStatus.CAN_CANCEL_SPOT_STATUS);
        boolean success = 1 == tradeSpotOrderMapper.updateByExampleSelective(update, example);
        if (!success) {
            return false;
        }
        TradeSpotMarginMiddleOrder middleUpdate = new TradeSpotMarginMiddleOrder();
        middleUpdate.setStatus(update.getStatus());
        middleUpdate.setMtime(mtime);
        TradeSpotMarginMiddleOrderExample middleOrderExample = new TradeSpotMarginMiddleOrderExample();
        middleOrderExample.createCriteria().andOrderIdEqualTo(uuid).andTypeEqualTo(Constants.SPOT_TYPE);
        success = 1 == tradeSpotMarginMiddleOrderMapper.updateByExampleSelective(middleUpdate, middleOrderExample);
        if (success) {
            this.orderUpdateNotice(order.getUid(), order.getUuid());
        }
        return success;
    }

    @Transactional(rollbackFor = Throwable.class)
    public boolean updateOrder(TradeSpotOrder order) {
        Date mtime = new Date();
        order.setMtime(mtime);
        TradeSpotOrderExample example = new TradeSpotOrderExample();
        TradeSpotOrderExample.Criteria criteria = example.createCriteria();
        if (null != order.getId()) {
            criteria.andIdEqualTo(order.getId());
        } else {
            criteria.andUuidEqualTo(order.getUuid());
        }
        boolean success = 1 == tradeSpotOrderMapper.updateByExampleSelective(order, example);
        if (!success) {
            return false;
        }
        TradeSpotMarginMiddleOrder middleUpdate = new TradeSpotMarginMiddleOrder();
        middleUpdate.setStatus(order.getStatus());
        middleUpdate.setMtime(mtime);
        TradeSpotMarginMiddleOrderExample middleOrderExample = new TradeSpotMarginMiddleOrderExample();
        middleOrderExample.createCriteria().andOrderIdEqualTo(order.getUuid()).andTypeEqualTo(Constants.SPOT_TYPE);
        success = 1 == tradeSpotMarginMiddleOrderMapper.updateByExampleSelective(middleUpdate, middleOrderExample);
        if (success) {
            this.orderUpdateNotice(order.getUid(), order.getUuid());
        }
        return success;
    }

    @Transactional(rollbackFor = Throwable.class)
    public void insertTransactionAndUpdateOrder(TradeTransaction transaction, TradeSpotOrder order) {
        tradeTransactionService.insert(transaction);
        this.updateOrder(order);
        this.orderUpdateNotice(order.getUid(), order.getUuid());
    }

    @Transactional(rollbackFor = Throwable.class)
    public void updateTransactionAndUpdateOrder(TradeTransaction transaction, TradeSpotOrder order) {
        tradeTransactionService.updateTransactionById(transaction);
        this.updateOrder(order);
        this.orderUpdateNotice(order.getUid(), order.getUuid());
    }



    @Transactional(rollbackFor = Throwable.class)
    public void insertModificationAndUpdateOrder(TradeSpotOrder order,
                                                 TradeSpotOrderModification origin,
                                                 TradeSpotOrderModification after) {
        if (origin != null) {
            tradeSpotOrderModificationMapper.insertSelective(origin);
        }
        tradeSpotOrderModificationMapper.insertSelective(after);
        tradeSpotOrderMapper.updateByPrimaryKeySelective(order);
        this.orderUpdateNotice(order.getUid(), order.getUuid());
    }

    public TradeSpotOrder querySpotOrder(SpotOrderCancel args) {
        TradeSpotOrderExample example = new TradeSpotOrderExample();
        TradeSpotOrderExample.Criteria criteria = example.createCriteria();
        criteria.andUuidEqualTo(args.getOrderId());
        if (args.getTerminator() == TradeTerminator.CLIENT) {
            criteria.andUidEqualTo(args.getUid());
        }
        List<TradeSpotOrder> orderList = tradeSpotOrderMapper.selectByExample(example);
        if (!orderList.isEmpty()) {
            return orderList.get(0);
        }
        return null;
    }

    @Transactional(rollbackFor = Throwable.class)
    public TradeSpotOrder cancelOrder(SpotOrderCancel args) {
        TradeSpotOrder order = this.querySpotOrder(args);
        if (order == null || !SourceType.isFromUser(order.getSource())) {
            throw new BusinessException(BusinessExceptionEnum.ORDER_CHANGE_OR_NOT_FOUND);
        }

        OrderStatus status = OrderStatus.getByName(order.getStatus());
        for (int i = 0; i < 5; i++) {
            if (status == OrderStatus.PENDING || status == OrderStatus.LOCKED) {
                try {
                    Thread.sleep(300);
                    order = this.querySpotOrder(args);
                    status = OrderStatus.getByName(order.getStatus());
                } catch (Exception ignored) {
                    ;
                    ;
                }
            } else {
                break;
            }
        }
        if (!OrderStatus.canCancelSpot(status.getName())) {
            throw new BusinessException(BusinessExceptionEnum.ORDER_CHANGE_OR_NOT_FOUND);
        }

        orderRequest.setSpotFinishStatus(order);
        order.setTerminator(args.getTerminator().getCode());
        boolean success = cancelOrderToDb(order);
        if (!success) {
            throw new BusinessException(BusinessExceptionEnum.ORDER_CHANGE_OR_NOT_FOUND);
        }
        return order;
    }


    /**
     * 修改订单通知
     *
     * @param uid
     * @param orderId
     */
    private void orderUpdateNotice(String uid, String orderId) {
        TradeSpotOrderExample example = new TradeSpotOrderExample();
        example.createCriteria().andUuidEqualTo(orderId);
        List<TradeSpotOrder> orderList = tradeSpotOrderMapper.selectByExample(example);
        if (orderList.isEmpty()) {
            throw new BusinessException(BusinessExceptionEnum.ORDER_CHANGE_OR_NOT_FOUND);
        }
        TradeSpotOrder order = orderList.get(0);
        if (SourceType.isFromUser(order.getSource())) {
            pushComponent.pushWsMessage(WsPushMessage.buildAllConsumersMessage(uid,
                    PushEventEnum.WEB_ORDER_UPDATE, webMapStruct.tradeSpotOrder2OrderResEvent(order)));
        }
    }

}
