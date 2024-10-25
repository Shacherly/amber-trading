package com.google.backend.trading.model.open.api;

import com.google.backend.trading.model.trade.OrderType;
import com.google.backend.trading.model.trade.TradeStrategy;

public enum OpenApiOrderType {

    /**
     * 限价单
     */
    LIMIT_GTC("LIMIT-GTC"),
    /**
     * 市价单
     */
    LIMIT_FAK("LIMIT-FAK"),
    /**
     * 条件限价单
     */
    LIMIT_FOK("LIMIT-FOK"),
    /**
     * 条件市价单
     */
    MARKET("MARKET"),
    ;

    private final String name;

    OpenApiOrderType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static OpenApiOrderType getByName(String name) {
        for (OpenApiOrderType value : OpenApiOrderType.values()) {
            if (value.getName().equals(name)) {
                return value;
            }
        }
        throw new RuntimeException(String.format("OpenApiOrderType not found by name, name = %s", name));
    }

    public static OpenApiOrderType fromOrderTypeAndStrategy(OrderTypeAndStrategy args) {
        if (args.getOrderType() == OrderType.MARKET || args.getOrderType() == OrderType.STOP_MARKET) {
            return MARKET;
        }
        TradeStrategy strategy = TradeStrategy.GTC;
        if (null != args.getStrategy()) {
            strategy = args.getStrategy();
        }
        switch (strategy) {
            case FOK:
                return LIMIT_FOK;
            case IOC:
                return LIMIT_FAK;
            default:
                return LIMIT_GTC;
        }
    }

    public OrderTypeAndStrategy toOrderTypeAndStrategy() {
        switch (this) {
            case MARKET:
                return new OrderTypeAndStrategy(OrderType.MARKET, TradeStrategy.GTC);
            case LIMIT_FAK:
                return new OrderTypeAndStrategy(OrderType.LIMIT, TradeStrategy.IOC);
            case LIMIT_FOK:
                return new OrderTypeAndStrategy(OrderType.LIMIT, TradeStrategy.FOK);
            case LIMIT_GTC:
                return new OrderTypeAndStrategy(OrderType.LIMIT, TradeStrategy.GTC);
            default:
                throw new RuntimeException("invalid OpenApiOrderType");
        }
    }
}
