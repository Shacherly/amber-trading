package com.google.backend.trading.service;

import com.google.backend.trading.dao.model.TradeMarginOrder;
import com.google.backend.trading.dao.model.TradePosition;
import com.google.backend.trading.dao.model.TradeSpotOrder;
import com.google.backend.trading.dao.model.TradeTransaction;

import java.math.BigDecimal;
import java.util.List;

/**
 * 封装消息触达的统一接口，方便调用
 *
 * @author savion.chen
 * @date 2021/11/13 10:32
 */
public interface PushMsgService {

    //-------------通用消息推送处理------------

    /**
     *  订单委托成功
     * @param userId 用户Id
     * @param isSpot 是否是现货
     */
    void submitOrderOk(String userId, boolean isSpot);

    /**
     *  订单取消成功
     * @param userId 用户Id
     * @param isSpot 是否是现货
     */
    void cancelOrderOk(String userId, boolean isSpot);

    /**
     *  订单修改成功
     * @param userId 用户Id
     * @param isSpot 是否是现货
     */
    void modifyOrderOk(String userId, boolean isSpot);


    //-------------现货的消息推送处理------------

    /**
     *  现货条件单触发
     * @param order 现货订单
     */
    void spotTriggerOk(TradeSpotOrder order);

    /**
     *  订单成交
     * @param order 现货订单
     */
    void spotOrderTraded(TradeSpotOrder order);


    //-------------杠杆的消息推送处理------------

    /**
     *  杠杆条件单触发
     * @param order 杠杆订单
     */
    void marginTriggerOk(TradeMarginOrder order);

    /**
     *  订单成交
     * @param transaction 杠杆订单
     */
    void marginOrderTraded(TradeTransaction transaction);


    /**
     *  单个仓位的止盈止损
     * @param pos 仓位信息
     * @param isProfit 是否止盈
     * @param pnl 盈亏值
     * @param amount 头寸
     */
    void marginStopSinglePosition(TradePosition pos, boolean isProfit, BigDecimal pnl, BigDecimal amount);

    /**
     *  全仓模式下的止盈止损
     * @param userId 用户
     * @param pnl 盈亏
     */
    void marginStopCrossedPosition(String userId, BigDecimal pnl);


    /**
     *  资金费结算
     * @param userId 用户
     * @param cost 费用
     */
    void marginSettleDone(String userId, BigDecimal cost);

    /**
     *  自动交割
     * @param userId 用户
     * @param isOk 是否成功
     */
    void marginDelivery(String userId, boolean isOk);

    /**
     * 自动交割
     *
     * @param userId 用户
     */
    void marginAutoDelivery(String userId);

    /**
     * 自动交割成功
     *
     * @param userId
     * @param successSymbols
     */
    void positionAutoSettleSuccess(String userId, List<String> successSymbols);

    /**
     * 自动交割失败
     *
     * @param userId
     * @param failSymbols
     */
    void positionAutoSettleFail(String userId, List<String> failSymbols);

    /**
     * 保证金不足
     *
     * @param order 杠杆订单
     */
    void marginNotEnough(TradeMarginOrder order);

    /**
     *  强平通知
     * @param userId 用户
     */
    void marginForceClose(String userId);

    /**
     * 部分强平通知
     *
     * @param userId
     */
    void marginPartForceClose(String userId);

    /**
     * 预警价格通知
     *
     * @param userId
     */
    void alarmPriceToUser(String userId, String coin, BigDecimal price);

}
