package com.google.backend.trading.client.feign;


import com.google.backend.trading.client.fallback.PdtClientFallbackFactory;
import com.google.backend.trading.config.feign.FeignConfig;
import com.google.backend.trading.model.common.Response;
import com.google.backend.trading.model.pdt.CreateSwapReq;
import com.google.backend.trading.model.pdt.CreateSwapRes;
import com.google.backend.trading.model.pdt.CreateTradeReq;
import com.google.backend.trading.model.pdt.CreateTradeRes;
import com.google.backend.trading.model.pdt.CrexSwapPriceReq;
import com.google.backend.trading.model.pdt.CrexSwapPriceRes;
import com.google.backend.trading.model.pdt.PriceReq;
import com.google.backend.trading.model.pdt.PriceRes;
import com.google.backend.trading.model.pdt.SwapByIdReq;
import com.google.backend.trading.model.pdt.SwapByIdRes;
import com.google.backend.trading.model.pdt.TradeByIdReq;
import com.google.backend.trading.model.pdt.TradeByIdRes;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.Map;

/**
 *
 * @author adam.wang
 * @date 2021/10/2 20:11
 */
@FeignClient(name = "pdtClient", url = "${pdt.server.host}", configuration = FeignConfig.class, fallbackFactory = PdtClientFallbackFactory.class)
@RequestMapping("/crex/internal")
public interface PdtClient {

    /**
     * 创建交易, 超时>10s IDK币种发单超时25S
     *
     * @param req
     * @return
     */
    @PostMapping(value="/createTrade")
    Response<CreateTradeRes> createTrade(@RequestBody CreateTradeReq req);

    /**
     * 查询交易
     * @param req
     * @return
     */
    @PostMapping(value="/tradeById")
    Response<TradeByIdRes> tradeById(@RequestBody TradeByIdReq req);


    /**
     * swap询价
     * @param req
     * @return
     */
    @PostMapping(value="/swapPrice")
    Response<CrexSwapPriceRes> swapPrice(@RequestBody CrexSwapPriceReq req);

    /**
     * 创建兑换交易, 超时>10s
     * @param req
     * @return
     */
    @PostMapping(value="/createSwap")
    Response<CreateSwapRes> createSwap(@RequestBody CreateSwapReq req);


    /**
     * 查询Swap交易
     * @param req
     * @return
     */
    @PostMapping(value="/swapById")
    Response<SwapByIdRes> swapById(@RequestBody SwapByIdReq req);

    /**
     * 获取价格, 非高频场景可用
     * @param req
     * @return
     */
    @PostMapping(value="/price")
    Response<Map<String, PriceRes>> price(@RequestBody PriceReq req);


    /**
     * 获取指数价格, 非高频场景可用
     * @param req
     * @return
     */
    @PostMapping(value="/indexPrice")
    Response<Map<String, BigDecimal>> indexPrice(@RequestBody PriceReq req);

}
