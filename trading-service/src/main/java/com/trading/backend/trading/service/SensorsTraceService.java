package com.google.backend.trading.service;

import com.google.backend.trading.dao.model.TradeMarginOrder;
import com.google.backend.trading.dao.model.TradeSpotOrder;
import com.google.backend.trading.dao.model.TradeSwapOrder;
import com.google.backend.trading.dao.model.TradeTransaction;
import com.google.backend.trading.dao.model.TradeUserAlarmPrice;
import com.google.backend.trading.dao.model.TradeUserTradeSetting;
import com.google.backend.trading.model.margin.api.PositionSettleInfoRes;

import java.math.BigDecimal;

public interface SensorsTraceService {

    void swapSubmit(TradeSwapOrder order, BigDecimal availableAmount);

    void spotSubmit(TradeSpotOrder order, BigDecimal availableAmount);

    void spotStatusChange(TradeSpotOrder order, TradeTransaction transaction, BigDecimal availableAmount);

    void marginSubmission(TradeMarginOrder order);

    void marginOpen(TradeTransaction transaction, TradeMarginOrder order, TradeUserTradeSetting setting);

    void marginClose(TradeTransaction transaction, TradeMarginOrder order, TradeUserTradeSetting setting);

    void settleSuccess(TradeTransaction transaction, PositionSettleInfoRes settleInfo);

    void tradeSettingChange(TradeUserTradeSetting before, TradeUserTradeSetting after);

    void setAlert(TradeUserAlarmPrice tradeUserAlarmPrice, BigDecimal marketPrice);

    void marketStatusChange(TradeSwapOrder order, BigDecimal availableAmount);

}
