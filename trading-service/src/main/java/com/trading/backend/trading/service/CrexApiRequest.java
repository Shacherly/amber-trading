package com.google.backend.trading.service;

import com.google.backend.trading.model.pdt.CreateSwapReq;
import com.google.backend.trading.model.pdt.CreateSwapRes;
import com.google.backend.trading.model.pdt.CreateTradeReq;
import com.google.backend.trading.model.pdt.CreateTradeRes;
import com.google.backend.trading.model.pdt.CrexSwapPriceReq;
import com.google.backend.trading.model.pdt.SwapByIdRes;
import com.google.backend.trading.model.pdt.TradeByIdRes;
import com.google.backend.trading.model.trade.TradeType;

import java.math.BigDecimal;

/**
 * 和PDT接口进行交互
 *
 * @author savion.chen
 * @date 2021/10/2 15:20
 */
public interface CrexApiRequest {


    /**
     * 执行下单的操作
     *
     * @param req 下单请求
     * @param tradeType
	 * @return 成交回报
     */
    CreateTradeRes executeOrder(CreateTradeReq req, TradeType tradeType);

    /**
     * 查询指定订单的结果
     *
     * @param tradeId 下单ID
     * @return 成交回报
     */
    TradeByIdRes queryOrder(String tradeId);


    /**
     * 执行swap的询价请求
     *
     * @param req 询价请求
     * @return 价格
     */
    BigDecimal querySwapPrice(CrexSwapPriceReq req);

    /**
     * 执行swap的下单操作
     *
     * @param req swap下单请求
     * @return 成交信息
     */
    CreateSwapRes executeSwapOrder(CreateSwapReq req);

    /**
     * 执行swap的下单操作
     *
     * @param tradeId 下单ID
     * @return 成交信息
     */
    SwapByIdRes querySwapOrder(String tradeId) ;


}
