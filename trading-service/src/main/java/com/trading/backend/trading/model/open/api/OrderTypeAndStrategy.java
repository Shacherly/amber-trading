package com.google.backend.trading.model.open.api;

import com.google.backend.trading.model.trade.OrderType;
import com.google.backend.trading.model.trade.TradeStrategy;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderTypeAndStrategy {
    private OrderType orderType;
    private TradeStrategy strategy;
}
