package com.google.backend.trading.service;

import com.google.backend.trading.dao.model.TradeSwapOrder;
import com.google.backend.trading.model.common.PageResult;
import com.google.backend.trading.model.internal.aceup.AceUpSwapReq;
import com.google.backend.trading.model.internal.aceup.AceUpSwapRes;
import com.google.backend.trading.model.internal.amp.AmpSwapReq;
import com.google.backend.trading.model.internal.amp.AmpSwapRes;
import com.google.backend.trading.model.swap.api.AipSwapOrderRes;
import com.google.backend.trading.model.swap.api.QuickSwapInfoRes;
import com.google.backend.trading.model.swap.api.QuickSwapOrderPlaceReq;
import com.google.backend.trading.model.swap.api.QuickSwapPriceReq;
import com.google.backend.trading.model.swap.api.SwapOrderHistoryLiteReq;
import com.google.backend.trading.model.swap.api.SwapOrderHistoryReq;
import com.google.backend.trading.model.swap.api.SwapOrderLiteRes;
import com.google.backend.trading.model.swap.api.SwapOrderRes;
import com.google.backend.trading.model.swap.api.SwapPriceReq;
import com.google.backend.trading.model.swap.api.SwapPriceRes;
import com.google.backend.trading.model.swap.dto.AipSwapOrderPlaceReqDTO;
import com.google.backend.trading.model.swap.dto.SwapOrderPlace;

import java.math.BigDecimal;
import java.util.List;

/**
 * 兑换业务的接口
 *
 * @author savion.chen
 * @date 2021/10/4 9:40
 */
public interface SwapService {

    /**
     * 现货查币种的余额
     *
     * @param req    请求数据
     * @param userId 用户Id
     * @return 返回信息
     */
    SwapPriceRes queryPrice(SwapPriceReq req, String userId);


    /**
     * 兑换的执行的接口
     *
     * @param req 请求数据
     * @return 返回状态码
     */
    SwapOrderRes placeOrder(SwapOrderPlace req);


    /**
     * 检查并执行swap兑换
     *
     * @param order   本地订单
     * @param isAsync 是否异步调用
     * @return 返回状态码
     */
    void checkAndFillOrder(TradeSwapOrder order, boolean isAsync);


    /**
     * 查询上次的兑换订单
     *
     * @param userId 用户id
     * @return 返回订单信息
     */
    SwapOrderRes queryLast(String userId);

    /**
     * 查询上次的兑换订单
     *
     * @param swapId 订单ID
     * @param userId 用户ID
     * @return 返回订单信息
     */
    SwapOrderRes querySwapOrder(String swapId, String userId);


    /**
     * 查询历史的兑换记录
     *
     * @param req    请求
     * @param userId 用户ID
     * @return 返回订单的结果集
     */
    PageResult<SwapOrderRes> queryHistory(SwapOrderHistoryReq req, String userId);

    /**
     * 查询所有活跃的订单,给查询循环检查用
     *
     * @param 无
     * @return
     */
    List<TradeSwapOrder> listAllActiveOrders();


    /**
     * 快捷询价
     *
     * @param req 请求
     * @param uid
     * @return 返回结果
     */
    BigDecimal quickSwapPrice(QuickSwapPriceReq req, String uid);


    /**
     * 查询快捷兑换信息
     *
     * @param symbol 币对
     * @param direct 方向
     * @param userId 用户ID
     * @return 返回结果
     */
    QuickSwapInfoRes quickSwapInfo(String symbol, String direct, String userId);


    /**
     * 快捷兑换下单
     *
     * @param req    请求
     * @param userId 用户ID
     * @return 返回结果
     */
    SwapOrderRes quickSwapPlace(QuickSwapOrderPlaceReq req, String userId);


    /**
     * amp 查询兑换记录
     *
     * @param ampSwapReq
     * @return
     */
    PageResult<AmpSwapRes> swapHistoryForAmp(AmpSwapReq ampSwapReq);


    /**
     * lite 版本查询SWAP历史记录
     *
     * @param req
     * @param uid
     * @return
     */
    PageResult<SwapOrderLiteRes> queryHistoryLite(SwapOrderHistoryLiteReq req, String uid);


    /**
     * 定投SWAP下单——异步执行{@link com.google.backend.trading.task.AipSwapOrderExecuteLoopThread}
     *
     * @param tradeSwapOrder
     * @return order只有三种状态:EXECUTING 执行中 COMPLETED 已完成 CANCELED 已取消
     * 其中【COMPLETED（完成）CANCELED（取消）——memo记录原因】Kafka终态返回AIP系统
     */
    AipSwapOrderRes aipPerformOrder(TradeSwapOrder tradeSwapOrder);


    /**
     * kafka 定投存入订单供 {@link SwapService#aipPerformOrder(TradeSwapOrder)} 异步执行
     *
     * @param req
     */
    void saveForAip(AipSwapOrderPlaceReqDTO req);


    /**
     * orderID不存在插入 否则更新
     *
     * @param tradeSwapOrder
     * @return
     */
    boolean insertOrUpdate(TradeSwapOrder tradeSwapOrder);


    /**
     * 获取所有定投AIP待执行订单
     *
     * @return
     */
    List<TradeSwapOrder> listAllAipActiveOrders();


    /**
     * 根据orderID查询订单
     *
     * @param orderId
     * @return
     */
    TradeSwapOrder queryByOrderId(String orderId);

    /**
     * 获取资金异常订单
     *
     * @return
     */
    List<TradeSwapOrder> getAssetExceptionOrder();

    /**
     * 检查资金请求情况，并更新order状态
     *
     * @param order
     */
    void checkAssetAndUpdateStatus(TradeSwapOrder order);

    /**
     * aceup order展示
     *
     * @param req
     * @return
     */
    PageResult<AceUpSwapRes> swapHistoryForAceUp(AceUpSwapReq req);
}
