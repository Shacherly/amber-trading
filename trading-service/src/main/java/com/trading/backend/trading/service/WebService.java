package com.google.backend.trading.service;

import com.google.backend.trading.model.common.PageReq;
import com.google.backend.trading.model.common.PageResult;
import com.google.backend.trading.model.web.OrderHistoryReq;
import com.google.backend.trading.model.web.OrderHistoryRes;
import com.google.backend.trading.model.web.OrderInfoRes;
import com.google.backend.trading.model.web.TransactionInfoRes;
import com.google.backend.trading.model.web.TransactionReq;

/**
 * Web接口对应服务
 *
 * @author jiayi.zhang
 * @date 2021/10/12
 */
public interface WebService {

    /**
     * 活跃订单
     * @param req
     * @param uid
     * @return
     */
    PageResult<OrderInfoRes> orderActive(PageReq req, String uid);

    /**
     * 历史订单列表
     * @param req
     * @param uid
     * @return
     */
    PageResult<OrderHistoryRes> orderHistory(OrderHistoryReq req, String uid);
    /**
     * 交易记录
     * @param req
     * @param uid
     * @return
     */
    PageResult<TransactionInfoRes> transaction(TransactionReq req, String uid);
}
