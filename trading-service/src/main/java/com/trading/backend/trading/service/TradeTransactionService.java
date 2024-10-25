package com.google.backend.trading.service;

import com.google.backend.trading.dao.model.TradeTransaction;

import java.util.List;

/**
 * @author david.chen
 * @date 2021/12/17 17:22
 */
public interface TradeTransactionService {
    /**
     * 全量数据根据transId查询TradeTransaction
     * 包括异常状态
     *
     * @param transId
     * @return
     */
    TradeTransaction queryAllByTransId(String transId);

    boolean insert(TradeTransaction transaction);

    boolean updateTransactionById(TradeTransaction transaction);

    /**
     * 根据OrderID查询最近一个资金异常交易记录
     *
     * @param orderId
     * @return
     */
    TradeTransaction queryNearAssetExceptionByOrderId(String orderId);

    /**
     * 全量数据根据orderId查询TradeTransaction
     * 包括异常状态
     *
     * @param orderId
     * @return mtime desc
     */
    List<TradeTransaction> queryAllByOrderId(String orderId);
}
