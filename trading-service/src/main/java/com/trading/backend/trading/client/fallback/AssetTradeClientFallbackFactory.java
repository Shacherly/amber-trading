package com.google.backend.trading.client.fallback;

import com.google.backend.asset.common.model.asset.res.TradeSpotLockRes;
import com.google.backend.asset.common.model.trade.req.TradeClearLockReq;
import com.google.backend.asset.common.model.trade.req.TradeClosePositionReq;
import com.google.backend.asset.common.model.trade.req.TradeCurrencyConversion;
import com.google.backend.asset.common.model.trade.req.TradeFundingCostReq;
import com.google.backend.asset.common.model.trade.req.TradeModifySpotOrderReq;
import com.google.backend.asset.common.model.trade.req.TradeOpenPositionReq;
import com.google.backend.asset.common.model.trade.req.TradeSettlePositionReq;
import com.google.backend.asset.common.model.trade.req.TradeSpotOrderReq;
import com.google.backend.asset.common.model.trade.req.TradeSpotReq;
import com.google.backend.common.web.Response;
import com.google.backend.trading.client.feign.AssetTradeClient;
import com.google.backend.trading.exception.BusinessExceptionEnum;
import com.google.backend.trading.util.AlarmLogUtil;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import java.util.List;

/**
 * @author trading
 */
@Slf4j
@Component
public class AssetTradeClientFallbackFactory implements FallbackFactory<AssetTradeClient> {
    @Override
    public AssetTradeClient create(Throwable throwable) {
        AlarmLogUtil.alarm("AssetTradeClient fallback, cause = {}", ExceptionUtils.getRootCauseMessage(throwable), throwable);
        return new AssetTradeClient() {

            @Override
            public Response<Void> doSpotOrder(TradeSpotOrderReq req) {
                return Response.fail(BusinessExceptionEnum.ASSET_FALLBACK.getCode());
            }

            @Override
            public Response<Void> doCancelSpotOrder(TradeSpotOrderReq req) {
                return Response.fail(BusinessExceptionEnum.ASSET_FALLBACK.getCode());
            }

            @Override
            public Response<Void> doSpot(TradeSpotReq req) {
                return Response.fail(BusinessExceptionEnum.ASSET_FALLBACK.getCode());
            }

            @Override
            public Response<Void> doFundingCost(TradeFundingCostReq req, MultiValueMap<String, String> headers) {
                return Response.fail(BusinessExceptionEnum.ASSET_FALLBACK.getCode());
            }

            @Override
            public Response<Void> doOpenPosition(TradeOpenPositionReq req, MultiValueMap<String, String> headers) {
                return Response.fail(BusinessExceptionEnum.ASSET_FALLBACK.getCode());
            }

            @Override
            public Response<Void> doClosePosition(TradeClosePositionReq req, MultiValueMap<String, String> headers) {
                return Response.fail(BusinessExceptionEnum.ASSET_FALLBACK.getCode());
            }

            @Override
            public Response<Void> doSettlePosition(TradeSettlePositionReq req, MultiValueMap<String, String> headers) {
                return Response.fail(BusinessExceptionEnum.ASSET_FALLBACK.getCode());
            }

            @Override
            public Response<Void> doModifySpotOrder(TradeModifySpotOrderReq req) {
                return Response.fail(BusinessExceptionEnum.ASSET_FALLBACK.getCode());
            }

            @Override
            public Response<Void> doCurrencyConversion(TradeCurrencyConversion req) {
                return Response.fail(BusinessExceptionEnum.ASSET_FALLBACK.getCode());
            }

            @Override
            public Response<List<TradeSpotLockRes>> doClearSpotLock(TradeClearLockReq req) {
                return Response.fail(BusinessExceptionEnum.ASSET_FALLBACK.getCode());
            }
        };
    }
}