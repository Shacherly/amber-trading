package com.google.backend.trading.service;

import com.google.backend.asset.common.model.base.PoolEntity;
import com.google.backend.asset.common.model.base.PoolEntityForRisk;
import com.google.backend.asset.common.model.trade.req.TradeCurrencyConversion;
import com.google.backend.asset.common.model.trade.req.TradeSpotOrderReq;
import com.google.backend.asset.common.model.trade.req.TradeSpotReq;
import com.google.backend.trading.dao.model.TradeSpotOrder;
import com.google.backend.trading.dao.model.TradeTransaction;
import com.google.backend.trading.exception.BusinessExceptionEnum;
import com.google.backend.trading.model.asset.Balance;
import com.google.backend.trading.model.spot.api.SpotAvailableReq;
import com.google.backend.trading.model.spot.api.SpotAvailableRes;
import com.google.backend.trading.model.swap.api.CoinBalanceRes;
import com.google.backend.trading.model.trade.AssetStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 定义和资金模块的交互接口接口
 *
 * @author savion.chen
 * @date 2021/9/30 14:43
 */
public interface AssetRequest {

    /**
     * rollback request
     *
     * @param reqId 请求Id
     */
    void doRollback(String reqId);

    /**
     * 按币种查询余额
     *
     * @param uid 用户Id
     * @param coin 币种
     * @return 返回信息
     */
    BigDecimal queryAvailableByCoin(String uid, String coin);


    /**
     * 现货查币种的余额
     *
     * @param req 请求数据
     * @param uid 用户Id
	 * @return 返回信息
     */
    SpotAvailableRes queryAvailable(SpotAvailableReq req, String uid);

    /**
     * 现货查币种的余额
     *
     * @param userId   用户ID
     * @param coin
     * @param onlyLite
     * @return 返回可用币种的列表信息
     */
    List<CoinBalanceRes> querySwapCoinBalance(String userId, String coin, boolean onlyLite);

    /**
     * 待废弃
     *
     * @param userId
     * @return
     */
    List<CoinBalanceRes> queryCoinBalance(String userId);

    /**
     * 开仓交易推送资产平台
     *
     * @param transaction 逐笔成交信息
     */
    AssetStatus doOpenPosition(TradeTransaction transaction);

    /**
     * 开仓交易推送资产平台
     * @param transaction 逐笔成交信息
     */
    AssetStatus doClosePosition(TradeTransaction transaction);

    /**
     * 请求交割
     * @param transaction 逐笔成交信息
     */
    void doSettlePosition(TradeTransaction transaction);

    /**
     * 将本地存储order转化成下单请求对象
     *
     * @param order 生成的本地订单
     * @param lockQty 冻结的资金量
     * @param reqId 请求Id
     * @return 成功或失败
     */
    public TradeSpotOrderReq getSpotFreezeReq(TradeSpotOrder order, BigDecimal lockQty, String reqId);


    /**
     * 冻结币种资金
     *
     * @param req 资金模块冻结请求
     * @return 冻结操作的状态码
     */
    public BusinessExceptionEnum freezeFunds(TradeSpotOrderReq req);

    /**
     * 取消冻结币种资金
     *
     * @param req 资金模块冻结请求
     * @return 响应状态码
     */
    public int cancelFreeze(TradeSpotOrderReq req);

    /**
     * 币种盈亏转换
     *
     * @param req 资金模块冻结请求
     * @return 响应状态码
     */
    public int conversionCoin(TradeCurrencyConversion req);


    /**
     * 将本地order转化成更新请求
     *
     * @param order 生成的本地订单
     * @param trans 成交
     * @return 成功或失败
     */
    public TradeSpotReq getUpdateTradeReq(TradeSpotOrder order, TradeTransaction trans);


    /**
     * 成交后更新资金信息
     *
     * @param req 更新的数据
     * @return 响应状态码
     */
    public int updateTradedResult(TradeSpotReq req);


    /**
     * 获取用户所有资产
     * @param uid 用户Id
     * @return 资金映射表
     */
    Map<String, PoolEntity> getBalanceMapByUid(String uid);


    /**
     * 获取用户所有资产
     * @param uid 用户Id
     * @return 资金列表
     */
    List<Balance> getBalanceByUid(String uid);

    /**
     * 获取用户非USD负余额资产
     * @param uid 用户Id
     * @return 资金列表
     */
    List<Balance> negativeBalanceExcludingUSD(String uid);

    /**
     * 获取用户非USD正余额资产
     * @param uid 用户Id
     * @return 资金列表
     */
    List<Balance> positiveBalanceExcludingUSD(String uid);


    /**
     * 获取用户锁定的资产
     * @param uid 用户Id
     * @return 资金列表
     */
    List<Balance> getLockedAssert(String uid);


    /**
     * 用户资产（携带部分风控数据）
     * @param uid 用户Id
     * @return 资金映射表
     */
    Map<String, PoolEntityForRisk> assetPoolForRisk(String uid);

    /**
     * 用户现货可用余额
     * @param uid 用户Id
     * @param availableCoin 可用余额的币种
     * @param comCoin 对手币种
     * @return 可用余额
     */
    BigDecimal assetSpotAvailable(String uid, String availableCoin, String comCoin);

    /**
     * 用户资产
     * @param uid 用户Id
     * @return 资金映射表
     */
    Map<String, PoolEntity> assetPool(String uid);

}
