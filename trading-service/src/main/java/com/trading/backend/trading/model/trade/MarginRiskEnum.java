package com.google.backend.trading.model.trade;

import java.math.BigDecimal;

/**
 * 头寸档位，映射爆仓线
 * @author borkes.mao
 * @created 2021.09.10 - 17:49
 **/
public enum MarginRiskEnum {

    LEVEL_0(0, new BigDecimal("0.02"), BigDecimal.ZERO),

    LEVEL_1(1, new BigDecimal("0.025"), new BigDecimal(100000)),

    LEVEL_2(2, new BigDecimal("0.03"), new BigDecimal(1000000)),

    LEVEL_3(3, new BigDecimal("0.04"), new BigDecimal(2500000)),

    LEVEL_4(4, new BigDecimal("0.05"), new BigDecimal(5000000)),

    ;
    MarginRiskEnum(Integer level, BigDecimal rate, BigDecimal total) {
        this.level = level;
        this.rate = rate;
        this.total = total;
    }

    /**
     * 仓位头寸等级
     */
    private final Integer level;
    /**
     * 维持保证金率（爆仓线）
     */
    private final BigDecimal rate;
    /**
     * 头寸上限
     */
    private final BigDecimal total;

    public Integer getLevel() {
        return this.level;
    }
    public BigDecimal getRate() {
        return this.rate;
    }
    public BigDecimal getTotal() {
        return this.total;
    }

    /**
     * 根据持仓头寸获取 MarginRiskEnum
     * @param positionValue 持仓头寸
     * @return
     */
    public static MarginRiskEnum getByPositionValue(BigDecimal positionValue) {
        if (positionValue.compareTo(MarginRiskEnum.LEVEL_4.getTotal()) > 0) {
            return MarginRiskEnum.LEVEL_4;
        } else if (positionValue.compareTo(MarginRiskEnum.LEVEL_3.getTotal()) > 0) {
            return MarginRiskEnum.LEVEL_3;
        } else if (positionValue.compareTo(MarginRiskEnum.LEVEL_2.getTotal()) > 0) {
            return MarginRiskEnum.LEVEL_2;
        } else if (positionValue.compareTo(MarginRiskEnum.LEVEL_1.getTotal()) > 0) {
            return MarginRiskEnum.LEVEL_1;
        } else {
            return MarginRiskEnum.LEVEL_0;
        }
    }
}
