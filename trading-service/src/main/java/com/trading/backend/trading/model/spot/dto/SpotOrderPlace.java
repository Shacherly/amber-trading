package com.google.backend.trading.model.spot.dto;

import com.google.backend.trading.model.trade.OrderType;
import com.google.backend.trading.model.trade.Direction;
import com.google.backend.trading.model.trade.SourceType;
import com.google.backend.trading.model.trade.TradeStrategy;
import com.google.backend.trading.model.trade.TriggerType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

/**
 * @author adam.wang
 * @date 2021/9/27
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class SpotOrderPlace {

    private String orderId;

    private String uid;

    private String symbol;

    private OrderType type;

    private TradeStrategy strategy;

    private Direction direction;

    private Boolean isQuote;

    @Positive
    @Digits(integer = 32, fraction = 16)
    private BigDecimal quantity;

    @Positive
    private BigDecimal price;

    @Positive
    private BigDecimal triggerPrice;

    private TriggerType triggerCompare;

    private SourceType source;

    private String notes;
}
