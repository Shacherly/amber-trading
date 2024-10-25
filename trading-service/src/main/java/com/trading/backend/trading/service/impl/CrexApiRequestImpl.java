package com.google.backend.trading.service.impl;

import com.google.backend.trading.alarm.AlarmComponent;
import com.google.backend.trading.alarm.AlarmEnum;
import com.google.backend.trading.client.feign.PdtClient;
import com.google.backend.trading.exception.BusinessExceptionEnum;
import com.google.backend.trading.model.common.Response;
import com.google.backend.trading.model.pdt.CreateSwapReq;
import com.google.backend.trading.model.pdt.CreateSwapRes;
import com.google.backend.trading.model.pdt.CreateTradeReq;
import com.google.backend.trading.model.pdt.CreateTradeRes;
import com.google.backend.trading.model.pdt.CrexSwapPriceReq;
import com.google.backend.trading.model.pdt.CrexSwapPriceRes;
import com.google.backend.trading.model.pdt.SwapByIdReq;
import com.google.backend.trading.model.pdt.SwapByIdRes;
import com.google.backend.trading.model.pdt.TradeByIdReq;
import com.google.backend.trading.model.pdt.TradeByIdRes;
import com.google.backend.trading.model.trade.TradeType;
import com.google.backend.trading.service.CrexApiRequest;
import com.google.backend.trading.util.AlarmLogUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * desc
 *
 * @author savion.chen
 * @date 2021/10/2 15:49
 */
@Slf4j
@Service
public class CrexApiRequestImpl implements CrexApiRequest {

    @Autowired
    private PdtClient pdtClient;

    @Autowired
    private AlarmComponent alarmComponent;

    @Override
    public CreateTradeRes executeOrder(CreateTradeReq req, TradeType tradeType) {
        Response<CreateTradeRes> resp = pdtClient.createTrade(req);
        if (resp.getCode() == BusinessExceptionEnum.SUCCESS.getCode()) {
            return resp.getData();
        }
        //fallback alarm
        CreateTradeRes fail = CreateTradeRes.FAIL;
        if (resp.getCode() == BusinessExceptionEnum.PDT_FALLBACK.getCode()) {
            //http请求失败 状态需要传递
            fail = CreateTradeRes.HTTP_FAIL;
            switch (tradeType) {
                case SPOT:
                    alarmComponent.asyncAlarm(AlarmEnum.SPOT_PLACE_ORDER_ERROR, resp.getMsg());
                    break;
                case MARGIN:
                    alarmComponent.asyncAlarm(AlarmEnum. MARGIN_PLACE_ORDER_ERROR, resp.getMsg());
                    break;
                default:
                    break;

            }
        }
        AlarmLogUtil.alarm("createTrade req = {} resp = {}", req.toString(), resp.getData());
        return fail;
    }

    @Override
    public TradeByIdRes queryOrder(String tradeId) {
        TradeByIdReq sendReq = new TradeByIdReq();
        sendReq.setTradeId(tradeId);
        try {
            Response<TradeByIdRes> resp = pdtClient.tradeById(sendReq);
            if (resp.getCode() == BusinessExceptionEnum.SUCCESS.getCode()) {
                return resp.getData();
            } else {
                log.error("tradeById={} error={}", tradeId, resp.getMsg());
            }
        } catch (Exception e) {
            log.error("tradeById={} error={}", tradeId, e.toString());
        }
        return null;
    }


    @Override
    public BigDecimal querySwapPrice(CrexSwapPriceReq req) {
        Response<CrexSwapPriceRes> resp = pdtClient.swapPrice(req);
        if (resp.getCode() == BusinessExceptionEnum.SUCCESS.getCode()) {
            String getPrice = resp.getData().getPrice();
            return new BigDecimal(getPrice);
        }
        alarmComponent.asyncAlarm(AlarmEnum.SWAP_PRICE_ERROR, resp.getMsg());
        log.error("querySwapPrice req = {} resp = {}", req.toString(), resp.getData());
        return null;
    }

    @Override
    public CreateSwapRes executeSwapOrder(CreateSwapReq req) {
        Response<CreateSwapRes> resp = pdtClient.createSwap(req);
        if (resp.getCode() == BusinessExceptionEnum.SUCCESS.getCode()) {
            return resp.getData();
        }
        //fallback alarm
        CreateSwapRes createSwapRes = CreateSwapRes.FAIL;
        if (resp.getCode() == BusinessExceptionEnum.PDT_FALLBACK.getCode()) {
            createSwapRes = CreateSwapRes.HTTP_FAIL;
            alarmComponent.asyncAlarm(AlarmEnum.SWAP_PLACE_ORDER_ERROR, resp.getMsg());
        }
        AlarmLogUtil.alarm("querySwapPrice req = {} resp = {}", req.toString(), resp.getData());
        return createSwapRes;
    }

    @Override
    public SwapByIdRes querySwapOrder(String tradeId) {
        SwapByIdReq sendReq = new SwapByIdReq();
        sendReq.setTradeId(tradeId);
        try {
            Response<SwapByIdRes> resp = pdtClient.swapById(sendReq);
            if (resp.getCode() == BusinessExceptionEnum.SUCCESS.getCode()) {
                return resp.getData();
            } else {
                log.error("swapById={} error={}", tradeId, resp.getMsg());
            }
        } catch (Exception e) {
            log.error("swapById={} error={}", tradeId, e.toString());
        }
        return null;
    }

}
