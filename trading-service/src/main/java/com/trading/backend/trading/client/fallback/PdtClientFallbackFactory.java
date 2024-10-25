package com.google.backend.trading.client.fallback;

import com.google.backend.trading.client.feign.PdtClient;
import com.google.backend.trading.exception.BusinessExceptionEnum;
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
import com.google.backend.trading.util.AlarmLogUtil;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

@Slf4j
@Component
public class PdtClientFallbackFactory implements FallbackFactory<PdtClient> {
    @Override
    public PdtClient create(Throwable throwable) {
        String message = ExceptionUtils.getRootCauseMessage(throwable);
        AlarmLogUtil.alarm("PdtClient fallback, cause = {}", message, throwable);
        return new PdtClient() {
            @Override
            public Response<CreateTradeRes> createTrade(CreateTradeReq req) {
                return Response.fail(BusinessExceptionEnum.PDT_FALLBACK.getCode(), message);
            }

            @Override
            public Response<TradeByIdRes> tradeById(TradeByIdReq req) {
                return Response.fail(BusinessExceptionEnum.PDT_FALLBACK.getCode(), message);
            }

            @Override
            public Response<CrexSwapPriceRes> swapPrice(CrexSwapPriceReq req) {
                return Response.fail(BusinessExceptionEnum.PDT_FALLBACK.getCode(), message);
            }

            @Override
            public Response<CreateSwapRes> createSwap(CreateSwapReq req) {
                return Response.fail(BusinessExceptionEnum.PDT_FALLBACK.getCode(), message);
            }

            @Override
            public Response<SwapByIdRes> swapById(SwapByIdReq req) {
                return Response.fail(BusinessExceptionEnum.PDT_FALLBACK.getCode());
            }

            @Override
            public Response<Map<String, PriceRes>> price(PriceReq req) {
                return Response.fail(BusinessExceptionEnum.PDT_FALLBACK.getCode());
            }

            @Override
            public Response<Map<String, BigDecimal>> indexPrice(PriceReq req) {
                return Response.fail(BusinessExceptionEnum.PDT_FALLBACK.getCode());
            }
        };
    }
}