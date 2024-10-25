package com.google.backend.trading.model.trade.fee;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * redis 30d amount key
 *
 * @author david.chen
 * @date 2022/1/12 12:31
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RedisTradeDTO {
    private BigDecimal amount;
    private Long time;
    @JsonIgnore
    private String uid;
    @JsonIgnore
    private String transId;

    public RedisTradeDTO(BigDecimal amount, Long time) {
        this.amount = amount;
        this.time = time;
    }
}
