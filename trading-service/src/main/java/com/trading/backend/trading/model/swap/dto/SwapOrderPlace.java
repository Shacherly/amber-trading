package com.google.backend.trading.model.swap.dto;
import com.google.backend.trading.model.swap.SwapType;
import com.google.backend.trading.model.trade.SourceType;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SwapOrderPlace {
    private String orderId;

    private String uid;

    private String fromCoin;

    private String toCoin;

    private String reqCoin;

    private SwapType mode;

    private BigDecimal price;

    private BigDecimal quantity;

    private SourceType source;

    /**
     * lite market swap 埋点使用
     */
    private boolean liteMarketSwap;


}
