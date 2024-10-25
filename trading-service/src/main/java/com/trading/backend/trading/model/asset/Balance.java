package com.google.backend.trading.model.asset;

import com.google.backend.trading.model.trade.PriorityCoin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 余额
 * @author adam.wang
 * @date 2021/10/10 17:57
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Balance implements Comparable{

    /**
     * 币种
     */
    private String coin;

    /**
     * 余额
     */
    private BigDecimal balance;

    /**
     * 现货锁定
     */
    private BigDecimal spotLocked;

    @Override
    public int compareTo(Object o) {
        Balance balance1 = (Balance) o;
        return PriorityCoin.coinCompare(coin,balance1.getCoin());
    }

}
