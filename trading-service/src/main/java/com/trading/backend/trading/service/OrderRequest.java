package com.google.backend.trading.service;
import com.google.backend.asset.common.model.trade.req.TradeCurrencyConversion;
import com.google.backend.trading.dao.model.TradeMarginOrder;
import com.google.backend.trading.dao.model.TradeSpotOrder;
import com.google.backend.trading.model.pdt.CreateTradeReq;
import com.google.backend.trading.model.pdt.CreateTradeRes;
import com.google.backend.trading.model.trade.Direction;
import com.google.backend.trading.model.trade.OrderStatus;
import com.google.backend.trading.model.trade.OrderType;
import com.google.backend.trading.model.trade.TradeStrategy;
import com.google.backend.trading.model.trade.TriggerType;
import lombok.Data;
import org.springframework.lang.Nullable;

import java.math.BigDecimal;

/**
 * 定义订单相关的资源整合接口
 *
 * @author savion.chen
 * @date 2021/9/30 14:43
 */

public interface OrderRequest {


    void validateLockQuantity(TradeSpotOrder order);

    /**
     * 将订单变动的部分封装使用
     */
    @Data
    class ChangeOrderData {
        private BigDecimal filledPrice;
        private BigDecimal qtyFilled;
        private BigDecimal amountFilled;

        private BigDecimal fee;
        private BigDecimal pnl;

        private OrderStatus status;
        private String error;
    }

    /**
     * 校验下单量是否符合最大最小发单量
     */
    void validateQuantity(BigDecimal quantity, String symbol, Boolean isFok, Boolean isQuote, @Nullable BigDecimal price,
                          boolean ignoreMinOrderNumCheck);

    /**
     * 校验下单量是否符合最小发单量
     * @param symbol 币对
     * @param quantity 下单量
     * @param isFok 是否fok单子
     * @return 是或否
     */
    Boolean validateMinQuantity(String symbol, BigDecimal quantity,  Boolean isFok);

    /**
     * 校验价格精度
     */
    void validatePricePrecision(OrderType orderType, String symbol, BigDecimal price, BigDecimal triggerPrice);

    /**
     * 校验是否超过最大活跃订单数
     */
    void validateMaxPendingNum(String uid);

    /**
     * 校验PDT和指数是否异常
     */
    void validatePriceStatus(String symbol);

    /**
     * 检查是否满足调价单触发
     *
     * @param triggerPrice 触发价
     * @param compare 比较类型
     * @param compPrice
     * @return 是否满足
     */
    boolean isSatisfyTrigger(BigDecimal triggerPrice, TriggerType compare, BigDecimal compPrice);


    /**
     * 检查是否达到了价格
     *
     * @param symbol 币对
     * @param direct 方向
     * @param price 价格
     * @return 是否满足
     */
    boolean isReachPrice(String symbol, Direction direct, BigDecimal price);

    /**
     * 检查下单量是否满足
     *
     * @param qty 下单量
     * @param symbol 币对
     * @return 是否满足
     */
    boolean checkOrderQuantity(BigDecimal qty, String symbol);

    /**
     * 检查是否满足最小下单量
     *
     * @param qty 下单量
     * @param symbol 币对
     * @return 是否满足
     */
    public boolean isSmallQuantity(BigDecimal qty, String symbol);

    /**
     * 获取订单的委托成交量
     *
     * @param order 本地订单
     * @return 数量
     */
    BigDecimal getEntrustAmount(TradeSpotOrder order);

    /**
     * 获取订单的成交量
     *
     * @param order 本地订单
     * @return 数量
     */
    BigDecimal getTradeAmount(TradeSpotOrder order);


    /**
     * 将本地订单转化成执行下单的请求
     *
     * @param order 订单数据
     * @param tradeId 请求Id
     * @return 转化后的数据
     */
    CreateTradeReq getPlaceSpotOrder(TradeSpotOrder order, String tradeId);

    /**
     * 获取币种转换的请求
     *
     * @param order 订单数据
     * @param tradeId 请求Id
     * @return 转化后的数据
     */
    TradeCurrencyConversion getConversionReq(TradeSpotOrder order, String tradeId);


    /**
     * 判断是否需要冻结资金
     * @param source
     * @return
     */
    boolean notNeedFreezeAsset(String source);

    /**
     * 判断是否需要解冻资金
     * @param source
     * @return
     */
    boolean notNeedUnFreezeAsset(String source);
    /**
     * 判断是否豁免冻结资金
     *
     * @param source 订单来源
     * @return 转化后的数据
     */
    @Deprecated
    boolean isImmunityAsset(String source);

    /**
     * 判断是否调用币种转换的接口
     *
     * @param source 订单来源
     * @return 转化后的数据
     */
    boolean isConversionCoin(String source);


    /**
     * 根据成交计算均价信息
     *
     * @param order 订单数据
     * @param info 成交信息
     */
    void calcAveragePrice(TradeSpotOrder order, CreateTradeRes info);

    /**
     * 计算订单的状态
     *
     * @param order 订单数据
     */
    void calcOrderStatus(TradeSpotOrder order);


    /**
     * 设置现货订单为取消状态
     *
     * @param order 订单数据
     */
    void setSpotFinishStatus(TradeSpotOrder order);

    /**
     * 设置杠杆订单为取消状态
     *
     * @param order 订单数据
     */
    void setMarginFinishStatus(TradeMarginOrder order);


    /**
     * 查看订单是否立即成交
     *
     * @param type 订单数据
     * @param life 订单数据
     * @return 返回成功或失败
     */
    boolean isImmediately(OrderType type, TradeStrategy life);


    /**
     * 计算订单逐笔费用
     *
     * @param order 订单信息
     * @param resp 成交信息
     * @return 返回费用
     */
    BigDecimal calcMiddleFee(TradeSpotOrder order, CreateTradeRes resp);


    /**
     * 计算已成交的量
     *
     * @param direct 方向
     * @param resp 成交信息
     * @return 返回费用
     */
    BigDecimal getTradedQuantity(String direct, CreateTradeRes resp);


    /**
     * 计算需要冻结的量
     *
     * @param order 订单信息
     * @param quantity 下单量
     * @param price 下单价格, 市价单可传null
     * @return 返回费用
     */
    BigDecimal calcFreezeFunds(TradeSpotOrder order, BigDecimal quantity, BigDecimal price);

    /**
     * 校验IDK币种下单
     * @param uid
     * @param symbol
     */
    void checkPlaceIDKOrder(String uid, String symbol);
}
