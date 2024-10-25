package com.google.backend.trading.service.impl;

import com.google.backend.trading.dao.model.TradeMarginOrder;
import com.google.backend.trading.dao.model.TradeSpotOrder;
import com.google.backend.trading.dao.model.TradeSwapOrder;
import com.google.backend.trading.dao.model.TradeTransaction;
import com.google.backend.trading.dao.model.TradeUserAlarmPrice;
import com.google.backend.trading.dao.model.TradeUserTradeSetting;
import com.google.backend.trading.model.margin.api.PositionSettleInfoRes;
import com.google.backend.trading.model.sensors.SensorsDirection;
import com.google.backend.trading.model.sensors.SensorsOrderStatus;
import com.google.backend.trading.model.sensors.SensorsOrderType;
import com.google.backend.trading.model.sensors.SensorsSourceType;
import com.google.backend.trading.model.sensors.SensorsStrategy;
import com.google.backend.trading.model.sensors.SensorsSwapOrderStatus;
import com.google.backend.trading.model.swap.SwapType;
import com.google.backend.trading.model.trade.OrderType;
import com.google.backend.trading.model.trade.SourceType;
import com.google.backend.trading.model.trade.TransactionType;
import com.google.backend.trading.sensors.SensorsEventEnum;
import com.google.backend.trading.sensors.SensorsProfileEnum;
import com.google.backend.trading.sensors.SensorsTrace;
import com.google.backend.trading.service.SensorsTraceService;
import com.google.backend.trading.util.CommonUtils;
import com.google.backend.trading.util.ThreadLocalUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
public class SensorsTraceServiceImpl implements SensorsTraceService {

    @Resource
    private SensorsTrace sensorsTrace;

    @Override
    public void swapSubmit(TradeSwapOrder order, BigDecimal availableAmount) {
        Map<String, Object> properties = new HashMap<>();
        String originChannel = ThreadLocalUtils.ORIGIN_CHANNEL.get();
        if (StringUtils.isNotEmpty(originChannel)) {
            properties.put("origin_channel", originChannel);
        }
        String lorp = ThreadLocalUtils.L_OR_P.get();
        if (StringUtils.isNotEmpty(lorp)) {
            properties.put("l_or_p", lorp);
        }
        properties.put("source_type", SensorsSourceType.getCodeByName(order.getSource()));
        properties.put("order_id", order.getUuid());
        properties.put("order_time", order.getCtime());
        properties.put("update_time", order.getMtime());
        if (Objects.equals(order.getMode(), SwapType.PAYMENT.getName())) {
            properties.put("base_amount", order.getFromQuantity());
            if (order.getToQuantity() != null) {
                properties.put("quote_amount", order.getToQuantity());
            }
            properties.put("direction", SensorsDirection.SELL.getCode());
        } else {
            properties.put("quote_amount", order.getToQuantity());
            if (order.getFromQuantity() != null) {
                properties.put("base_amount", order.getFromQuantity());
            }
            properties.put("direction", SensorsDirection.BUY.getCode());
        }
        properties.put("base", order.getFromCoin());
        properties.put("quote", order.getToCoin());
        properties.put("price", order.getOrderPrice());
        if (order.getDealPrice() != null) {
            properties.put("avg_price", order.getDealPrice());
        }
        properties.put("trade_status", SensorsSwapOrderStatus.getCodeByName(order.getStatus()));
        sensorsTrace.track(order.getUid(), SensorsEventEnum.SWAP_SUBMIT.getCode(), properties);
        if (SourceType.isFromUser(order.getSource())) {
            sensorsTrace.profileSetOnce(order.getUid(), SensorsProfileEnum.FIRST_SWAP.getCode(), true);
        }
    }

    @Override
    public void spotSubmit(TradeSpotOrder order, BigDecimal availableAmount) {
        Map<String, Object> properties = new HashMap<>();
        String[] baseQuote = order.getSymbol().split(CommonUtils.SEPARATOR);
        properties.put("base", baseQuote[0]);
        properties.put("quote", baseQuote[1]);
        properties.put("coin", order.getIsQuote() ? baseQuote[1] : baseQuote[0]);
        properties.put("source_type", SensorsSourceType.getCodeByName(order.getSource()));
        properties.put("order_id", order.getUuid());
        properties.put("order_time", order.getCtime());
        if (OrderType.isLimitOrder(order.getType())) {
            properties.put("price", order.getPrice());
        }
        properties.put("quantity", order.getQuantity());
        properties.put("direction", SensorsDirection.getCodeByName(order.getDirection()));
        properties.put("order_type", SensorsOrderType.getCodeByName(order.getType()));
//        properties.put("sl_tp", null);
        if (null != order.getTriggerPrice()) {
            properties.put("trigger_price", order.getTriggerPrice());
        }
        if (null != availableAmount) {
            properties.put("available_amount", availableAmount);
        }
        properties.put("remain", order.getQuantity().subtract(order.getQuantityFilled()));
        properties.put("avg_price", Optional.ofNullable(order.getFilledPrice()).orElse(BigDecimal.ZERO));
        properties.put("fee_amount", Optional.ofNullable(order.getFee()).orElse(BigDecimal.ZERO));
        if (Objects.equals(order.getType(), OrderType.LIMIT.getCode())) {
            properties.put("order_duration", SensorsStrategy.getCodeByName(order.getStrategy()));
        }
        if (OrderType.isTriggerOrder(order.getType())) {
            properties.put("trigger_price", order.getTriggerPrice());
        }
        properties.put("available_amount", availableAmount);
        properties.put("trade_status", SensorsOrderStatus.getCodeByName(order.getStatus()));
        sensorsTrace.track(order.getUid(), SensorsEventEnum.SPOT_SUBMIT.getCode(), properties);
        if (SourceType.isFromUser(order.getSource())) {
            sensorsTrace.profileSetOnce(order.getUid(), SensorsProfileEnum.FIRST_SPOT.getCode(), true);
        }
    }

    @Override
    public void spotStatusChange(TradeSpotOrder order, TradeTransaction transaction, BigDecimal availableAmount) {
        Map<String, Object> properties = new HashMap<>();
        String[] baseQuote = order.getSymbol().split(CommonUtils.SEPARATOR);
        properties.put("base", baseQuote[0]);
        properties.put("quote", baseQuote[1]);
        properties.put("coin", order.getIsQuote() ? baseQuote[1] : baseQuote[0]);
        properties.put("source_type", SensorsSourceType.getCodeByName(order.getSource()));
        properties.put("order_id", order.getUuid());
        if (transaction != null) {
            properties.put("trade_id", transaction.getUuid());
        }
        if (OrderType.isLimitOrder(order.getType())) {
            properties.put("price", order.getPrice());
        }
        properties.put("quantity", order.getQuantity());
        properties.put("direction", SensorsDirection.getCodeByName(order.getDirection()));
        properties.put("order_type", SensorsOrderType.getCodeByName(order.getType()));
        if (Objects.equals(order.getType(), OrderType.LIMIT.getCode())) {
            properties.put("order_duration", SensorsStrategy.getCodeByName(order.getStrategy()));
        }
        if (OrderType.isTriggerOrder(order.getType())) {
            properties.put("trigger_price", order.getTriggerPrice());
        }
        properties.put("accumulate_amount", order.getAmountFilled());
        properties.put("available_amount", availableAmount);
        properties.put("remain", order.getQuantity().subtract(order.getQuantityFilled()));
        properties.put("avg_price", order.getFilledPrice());
        properties.put("fee_amount", order.getFee());
        properties.put("trade_status", SensorsOrderStatus.getCodeByName(order.getStatus()));
        sensorsTrace.track(order.getUid(), SensorsEventEnum.SPOT_STATUS_CHANGE.getCode(), properties);
    }

    @Override
    public void marginSubmission(TradeMarginOrder order) {
        Map<String, Object> properties = new HashMap<>();
        properties.put("source_type", SensorsSourceType.getCodeByName(order.getSource()));
        properties.put("order_id", order.getUuid());
        properties.put("order_time", order.getCtime());
        properties.put("symbol", order.getSymbol());
        properties.put("direction", SensorsDirection.getCodeByName(order.getDirection()));
        properties.put("order_type", SensorsOrderType.getCodeByName(order.getType()));
        if (Objects.equals(order.getType(), OrderType.LIMIT.getCode())) {
            properties.put("order_duration", SensorsStrategy.getCodeByName(order.getStrategy()));
        }
        properties.put("reduce_only", Optional.ofNullable(order.getReduceOnly()).orElse(false));
        if (OrderType.isTriggerOrder(order.getType())) {
            properties.put("trigger_price", order.getTriggerPrice());
        }
        String[] baseQuote = order.getSymbol().split(CommonUtils.SEPARATOR);
        properties.put("base", baseQuote[0]);
        properties.put("quote", baseQuote[1]);
        properties.put("amount", order.getQuantity());
        if (OrderType.isLimitOrder(order.getType())) {
            properties.put("price", order.getPrice());
        }
        sensorsTrace.track(order.getUid(), SensorsEventEnum.MARGIN_SUBMIT.getCode(), properties);
        if (SourceType.isFromUser(order.getSource())) {
            sensorsTrace.profileSetOnce(order.getUid(), SensorsProfileEnum.FIRST_MARGIN.getCode(), true);
        }
    }

    @Override
    public void marginOpen(TradeTransaction transaction, TradeMarginOrder order, TradeUserTradeSetting setting) {
        Map<String, Object> properties = new HashMap<>();
        properties.put("order_id", transaction.getOrderId());
        properties.put("position_id", transaction.getPositionId());
        properties.put("price", transaction.getPrice());
        properties.put("quantity", transaction.getBaseQuantity());
        properties.put("amount", transaction.getQuoteQuantity());
        properties.put("direction", SensorsDirection.getCodeByName(order.getDirection()));
        properties.put("order_type", SensorsOrderType.getCodeByName(order.getType()));
        if (Objects.equals(order.getType(), OrderType.LIMIT.getCode())) {
            properties.put("order_duration", SensorsStrategy.getCodeByName(order.getStrategy()));
        }
        if (OrderType.isTriggerOrder(order.getType())) {
            properties.put("trigger_price", order.getTriggerPrice());
        }
        String[] baseQuote = order.getSymbol().split(CommonUtils.SEPARATOR);
        properties.put("base", baseQuote[0]);
        properties.put("quote", baseQuote[1]);
        properties.put("margin_status", SensorsOrderStatus.getCodeByName(order.getStatus()));
        properties.put("leverage", setting.getLeverage());
        properties.put("accumulate_amount", order.getQuantityFilled());
        properties.put("remain", order.getQuantity().subtract(order.getQuantityFilled()));
        properties.put("avg_price", transaction.getPnl());
        properties.put("fee_amount", transaction.getFee());
//        properties.put("margin", marginInfo.getTotalOpenMargin());
//        properties.put("margin_utilized", marginInfo.getUsedMargin());
//        properties.put("capital_utilization_rate", marginInfo.getFundUtilization());
        properties.put("reduce_only", Optional.ofNullable(order.getReduceOnly()).orElse(false));
        properties.put("amount", order.getQuantity());
        properties.put("price", order.getPrice());
        sensorsTrace.track(transaction.getUid(), SensorsEventEnum.MARGIN_OPEN.getCode(), properties);
    }

    @Override
    public void marginClose(TradeTransaction transaction, TradeMarginOrder order, TradeUserTradeSetting setting) {
        Map<String, Object> properties = new HashMap<>();
        properties.put("source_type", SensorsSourceType.getCodeByName(transaction.getSource()));
        properties.put("order_id", transaction.getOrderId());
        properties.put("position_id", transaction.getPositionId());
        properties.put("price", transaction.getPrice());
        properties.put("quantity", order.getQuantity());
        properties.put("close_quantity", transaction.getBaseQuantity());
        properties.put("amount", transaction.getQuoteQuantity());
        properties.put("direction", SensorsDirection.getCodeByName(order.getDirection()));
        properties.put("order_type", SensorsOrderType.getCodeByName(order.getType()));
        if (Objects.equals(order.getType(), OrderType.LIMIT.getCode())) {
            properties.put("order_duration", SensorsStrategy.getCodeByName(order.getStrategy()));
        }
        if (OrderType.isTriggerOrder(order.getType())) {
            properties.put("trigger_price", order.getTriggerPrice());
        }
        String[] baseQuote = transaction.getSymbol().split(CommonUtils.SEPARATOR);
        properties.put("base", baseQuote[0]);
        properties.put("quote", baseQuote[1]);
        properties.put("leverage", setting.getLeverage());
        properties.put("is_all", Objects.equals(transaction.getType(), TransactionType.CLOSE_POSITION.getName()));
        properties.put("realized_pnl", transaction.getPnl());
        properties.put("reduce_only", Optional.ofNullable(order.getReduceOnly()).orElse(false));
        sensorsTrace.track(transaction.getUid(), SensorsEventEnum.MARGIN_CLOSE.getCode(), properties);
    }

    @Override
    public void settleSuccess(TradeTransaction transaction, PositionSettleInfoRes settleInfo) {
        Map<String, Object> properties = new HashMap<>();
        properties.put("source_type", SensorsSourceType.getCodeByName(transaction.getSource()));
        properties.put("order_id", transaction.getUuid());
        properties.put("position_id", transaction.getPositionId());
        properties.put("is_auto_settle", Objects.equals(transaction.getSource(), SourceType.AUTO_POSITION_SETTLE.getName()));
        String[] baseQuote = transaction.getSymbol().split(CommonUtils.SEPARATOR);
        properties.put("base", baseQuote[0]);
        properties.put("quote", baseQuote[1]);
        properties.put("direction", transaction.getDirection());
        properties.put("price", transaction.getPrice());
        properties.put("available_amount", settleInfo.getAvailableBalance());
        properties.put("settle_size", transaction.getBaseQuantity());
        properties.put("settle_available", settleInfo.getPositionQuantity());
        sensorsTrace.track(transaction.getUid(), SensorsEventEnum.SETTLE_SUCCESS.getCode(), properties);
    }

    @Override
    public void tradeSettingChange(TradeUserTradeSetting before, TradeUserTradeSetting after) {
        Map<String, Object> properties = new HashMap<>();
        properties.put("change_before", before.getLeverage());
        properties.put("order_id", after.getLeverage());
        properties.put("is_earn_pledge_on", after.getEarnPledge());
        properties.put("financial_assets", after.getLiquidEarn());
        properties.put("automatic_balance", after.getAutoFixNegative());
        properties.put("is_auto_settle_on", after.getAutoSettle());
        properties.put("full_position_stop_loss", after.getMaxLoss() != null && CommonUtils.isPositive(after.getMaxLoss()));
        properties.put("full_position_take_profit", after.getTakeProfit() != null && CommonUtils.isPositive(after.getMaxLoss()));
        sensorsTrace.track(before.getUid(), SensorsEventEnum.TRADE_SETTING_CHANGE.getCode(), properties);
    }

    @Override
    public void setAlert(TradeUserAlarmPrice tradeUserAlarmPrice, BigDecimal marketPrice) {
        Map<String, Object> properties = new HashMap<>();

        String originChannel = ThreadLocalUtils.ORIGIN_CHANNEL.get();
        if (StringUtils.isNotEmpty(originChannel)) {
            properties.put("origin_channel", originChannel);
        }
        String lorp = ThreadLocalUtils.L_OR_P.get();
        if (StringUtils.isNotEmpty(lorp)) {
            properties.put("l_or_p", lorp);
        }

        properties.put("submitted_time", tradeUserAlarmPrice.getCtime());
        properties.put("set_time", tradeUserAlarmPrice.getCtime());
        properties.put("coin", CommonUtils.getBaseCoin(tradeUserAlarmPrice.getSymbol()));
        properties.put("alert_price", tradeUserAlarmPrice.getAlarmPrice());
        properties.put("market_price", marketPrice);
        sensorsTrace.track(tradeUserAlarmPrice.getUid(), SensorsEventEnum.SET_ALERT.getCode(), properties);
    }

    @Override
    public void marketStatusChange(TradeSwapOrder order, BigDecimal availableAmount) {
        Map<String, Object> properties = new HashMap<>();
        String originChannel = ThreadLocalUtils.ORIGIN_CHANNEL.get();
        if (StringUtils.isNotEmpty(originChannel)) {
            properties.put("origin_channel", originChannel);
        }
        String lorp = ThreadLocalUtils.L_OR_P.get();
        if (StringUtils.isNotEmpty(lorp)) {
            properties.put("l_or_p", lorp);
        }
        properties.put("source_type", SensorsSourceType.getCodeByName(order.getSource()));
        properties.put("order_id", order.getUuid());
        properties.put("order_time", order.getCtime());
        properties.put("update_time", order.getMtime());
        if (Objects.equals(order.getMode(), SwapType.PAYMENT.getName())) {
            properties.put("base_amount", order.getFromQuantity());
            if (order.getToQuantity() != null) {
                properties.put("quote_amount", order.getToQuantity());
            }
            properties.put("direction", SensorsDirection.SELL.getCode());
        } else {
            properties.put("quote_amount", order.getToQuantity());
            if (order.getFromQuantity() != null) {
                properties.put("base_amount", order.getFromQuantity());
            }
            properties.put("direction", SensorsDirection.BUY.getCode());
        }
        properties.put("base", order.getFromCoin());
        properties.put("quote", order.getToCoin());
        properties.put("price", order.getOrderPrice());
        if (order.getDealPrice() != null) {
            properties.put("avg_price", order.getDealPrice());
        }
        properties.put("trade_status", SensorsSwapOrderStatus.getCodeByName(order.getStatus()));
        sensorsTrace.track(order.getUid(), SensorsEventEnum.MARKET_STATUS_CHANGE.getCode(), properties);
    }


}
