package com.google.backend.trading.model.spot.dto;

import com.google.backend.trading.model.trade.TradeTerminator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 现货撤单接口
 *
 * @author savion.chen
 * @date 2021/10/14 10:51
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class SpotOrderCancel {
    private String orderId;

    private String uid;

    private TradeTerminator terminator;
}
