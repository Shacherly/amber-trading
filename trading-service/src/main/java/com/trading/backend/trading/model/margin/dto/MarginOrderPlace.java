package com.google.backend.trading.model.margin.dto;

import com.google.backend.trading.model.trade.OrderType;
import com.google.backend.trading.model.trade.Direction;
import com.google.backend.trading.model.trade.SourceType;
import com.google.backend.trading.model.trade.TradeStrategy;
import com.google.backend.trading.model.trade.TriggerType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author adam.wang
 * @date 2021/9/27
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class MarginOrderPlace {
    private String orderId;

    private String uid;

    private String symbol;

    private OrderType type;

    private TradeStrategy strategy;

    private Direction direction;

    private BigDecimal quantity;

    private BigDecimal price;

    private BigDecimal triggerPrice;

    private TriggerType triggerCompare;

    private SourceType source;

    private String notes;

    private Boolean reduceOnly;

    private boolean all;

    private String positionId;
}
