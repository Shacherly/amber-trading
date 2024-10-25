package com.google.backend.trading.service;

import com.google.backend.trading.dao.model.TradeSpotOrder;
import com.google.backend.trading.dao.model.TradeSpotOrderModification;
import com.google.backend.trading.model.common.PageResult;
import com.google.backend.trading.model.common.model.riskcontrol.OrderRes;
import com.google.backend.trading.model.internal.aceup.AceUpSpotReq;
import com.google.backend.trading.model.internal.aceup.AceUpSpotRes;
import com.google.backend.trading.model.internal.aceup.AceUpSpotTransRes;
import com.google.backend.trading.model.internal.amp.AmpSpotReq;
import com.google.backend.trading.model.internal.amp.AmpSpotRes;
import com.google.backend.trading.model.spot.api.SpotDetailRes;
import com.google.backend.trading.model.spot.api.SpotOrderActiveReq;
import com.google.backend.trading.model.spot.api.SpotOrderHistoryReq;
import com.google.backend.trading.model.spot.api.SpotOrderInfoRes;
import com.google.backend.trading.model.spot.api.SpotOrderPlaceRes;
import com.google.backend.trading.model.spot.api.SpotOrderUpdateReq;
import com.google.backend.trading.model.spot.dto.SpotOrderCancel;
import com.google.backend.trading.model.spot.dto.SpotOrderPlace;
import com.google.backend.trading.model.trade.Direction;
import com.google.backend.trading.model.trade.SourceType;
import org.springframework.lang.Nullable;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

/**
 * 现货的处理逻辑
 *
 * @author savion.chen
 * @date 2021/9/29 12:08
 */
public interface SpotService {

    /**
     * 现货下单的接口
     *
     * @param spotOrderPlace 请求数据
     * @return 返回信息
     */
    public SpotOrderPlaceRes placeOrder(@Valid SpotOrderPlace spotOrderPlace);

    /**
     * 修改订单操作
     * @param req 修改订单的请求
     * @param userId 用户ID
     * @return 成功返回订单号, 否则为null
     */
    public String updateOrder(SpotOrderUpdateReq req, String userId);

    /**
     * 取消订单的接口
     *
     * @param req 撤单请求
     * @return 成功返回订单号, 否则为null
     */
    public String cancelOrder(SpotOrderCancel req);


    /**
     * 检查条件单是否达到触发条件
     * @param order 查询订单对象
     * @return 成交的结果
     */
    public boolean checkTriggerOrder(TradeSpotOrder order);

    /**
     * 执行现货下单操作
     * @param order 查询订单对象
     * @param isAsync 是否异步执行
     * @return 成交的结果
     */
    public void checkAndFillOrder(TradeSpotOrder order, boolean isAsync);


    /**
     * 查询所有活跃条件订单,给查询循环检查用
     * @param 无
     * @return
     */
    List<TradeSpotOrder> listAllActiveTriggerOrders();

    /**
     * 检查现货单是否为同步单
     * @param order 查询订单对象
     * @return 是或否
     */
    public boolean isSpotSyncOrder(TradeSpotOrder order);

    /**
     * 查询所有异步订单,给查询循环检查用
     * @param 无
     * @return
     */
    List<TradeSpotOrder> fetchAsyncExecuteOrders();

    /**
     * 查询指定用户的活跃订单
     * @param uids 指定的
     * @return
     */
    List<OrderRes> listAllActiveOrders(List<String> uids);

    /**
     * 当前活跃委托
     * @param req
     * @param uid
	 * @return
     */
    PageResult<SpotOrderInfoRes> orderActive(SpotOrderActiveReq req, String uid);

    /**
     * 历史委托
     * @param req
     * @return
     */
    PageResult<SpotOrderInfoRes> orderHistory(SpotOrderHistoryReq req, String uid);


    /**
     * 获取用户活跃非系统单
     * @param uid
     * @return
     */
    List<TradeSpotOrder> getTradeSpotOrders(String uid);

    /**
     * 取消非系统单 for RISK
     * @param uid
     * @return 取消的数量
     */
    int cancelAllNotSystemOrders(String uid);


    /**
     * 清算资产余额
     * @param uid
     */
   void liquidBalance(String uid);

    /**
     * 清算现货锁定余额
     * @param uid
     */
    void liquidSpot(String uid);
    /**
     * 修改记录默认最多返回100条
     * @param orderId
     * @return
     */
    List<TradeSpotOrderModification> orderModifications(String orderId);

    /**
     * 获取现货详情页的聚合数据
     * @param uid
     * @param symbol
     * @return
     */
    SpotDetailRes detail(@Nullable String uid, String symbol);

    /**
     * 下单直至全部成交
     * @param symbol
     * @param quantity
     */
    SpotOrderPlaceRes placeOrderUntilAllComplete(String uid, String symbol, BigDecimal quantity, Direction direction, Boolean isQuote, SourceType source);

    /**
     * TradeSpotOrder转换为SpotOrderPlaceRes
     * @param order TradeSpotOrder
     */
    SpotOrderPlaceRes getOrderResult(TradeSpotOrder order);

    /**
     * 获取订单 by orderId and uid
     * @param orderId order uuid
     * @param uid uid
     */
    TradeSpotOrder querySpotOrderById(String orderId, String uid);

    /**
     * amp现货历史
     *
     * @param req
     * @return
     */
    PageResult<AmpSpotRes> spotHistoryForAmp(AmpSpotReq req);

    /**
     * 获取资金请求失败订单
     *
     * @return
     */
    List<TradeSpotOrder> getAssetExceptionOrder();

    /**
     * 检查资金请求情况，并更新order状态
     *
     * @param order
     */
    void checkAssetAndUpdateStatus(TradeSpotOrder order);

    /**
     * aceup现货历史
     *
     * @param req
     * @return
     */
    PageResult<AceUpSpotRes> spotHistoryForAceUp(AceUpSpotReq req);

    /**
     * aceup订单交易记录
     *
     * @param orderId
     * @return
     */
    List<AceUpSpotTransRes> spotTransactionListForAceUp(String orderId);
}
