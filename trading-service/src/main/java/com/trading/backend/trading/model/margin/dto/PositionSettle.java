package com.google.backend.trading.model.margin.dto;

import com.google.backend.trading.model.trade.SourceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author jiayi.zhang
 * @date 2021/9/28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PositionSettle {
    private String uid;

    private String positionId;

    private BigDecimal quantity;

    private SourceType source;

    private boolean all;
}
