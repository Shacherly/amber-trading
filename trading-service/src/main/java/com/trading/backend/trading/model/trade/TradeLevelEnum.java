package com.google.backend.trading.model.trade;

import com.google.backend.trading.model.trade.fee.TradeLevelRate;
import lombok.Getter;
import org.springframework.lang.Nullable;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * trade 等级枚举
 *
 * @author david.chen
 * @date 2022/1/6 20:24
 */
@Getter
public enum TradeLevelEnum {

    LEVEL_1(1, 0, 100_000, "<$100,000", BigDecimal.ZERO),
    LEVEL_2(2, 100_000, 500_000, "≥$100,000", new BigDecimal("0.05")),
    LEVEL_3(3, 500_000, 2_500_000, "≥$500,000", new BigDecimal("0.10")),
    LEVEL_4(4, 2_500_000, 12_500_000, "≥$2,500,000", new BigDecimal("0.15")),
    LEVEL_5(5, 12_500_000, 75_000_000, "≥$12,500,000", new BigDecimal("0.20")),
    LEVEL_6(6, 75_000_000, 500_000_000, "≥$75,000,000", new BigDecimal("0.25")),
    LEVEL_7(7, 500_000_000, 3_000_000_000L, "≥$500,000,000", new BigDecimal("0.30")),
    LEVEL_8(8, 3_000_000_000L, Long.MAX_VALUE, "≥$3,000,000,000", new BigDecimal("0.40")),
    ;

    private int level;
    private long min;
    private long max;
    private String condition;
    private BigDecimal feeOff;

    TradeLevelEnum(int level, long min, long max, String condition, BigDecimal feeOff) {
        this.level = level;
        this.min = min;
        this.max = max;
        this.condition = condition;
        this.feeOff = feeOff;
    }


    public static TradeLevelEnum getByTradeAmount(BigDecimal tradeAmount) {
        long longValue = tradeAmount.longValue();
        if (longValue >= LEVEL_1.min && longValue < LEVEL_1.max) {
            return LEVEL_1;
        } else if (longValue >= LEVEL_2.min && longValue < LEVEL_2.max) {
            return LEVEL_2;
        } else if (longValue >= LEVEL_3.min && longValue < LEVEL_3.max) {
            return LEVEL_3;
        } else if (longValue >= LEVEL_4.min && longValue < LEVEL_4.max) {
            return LEVEL_4;
        } else if (longValue >= LEVEL_5.min && longValue < LEVEL_5.max) {
            return LEVEL_5;
        } else if (longValue >= LEVEL_6.min && longValue < LEVEL_6.max) {
            return LEVEL_6;
        } else if (longValue >= LEVEL_7.min && longValue < LEVEL_7.max) {
            return LEVEL_7;
        } else if (longValue >= LEVEL_8.min && longValue < LEVEL_8.max) {
            return LEVEL_8;
        } else {
            return LEVEL_1;
        }
    }

    public static List<TradeLevelRate> getTradeLevelRateList() {
        return Arrays.stream(TradeLevelEnum.values()).map(e -> {
                    TradeLevelRate tradeLevelRate = new TradeLevelRate();
                    tradeLevelRate.setLevel(e.getLevel());
                    tradeLevelRate.setRate(e.getFeeOff());
                    tradeLevelRate.setCondition(e.getCondition());
                    return tradeLevelRate;
                }).sorted(Comparator.comparingInt(TradeLevelRate::getLevel))
                .collect(Collectors.toList());
    }


    @Nullable
    public BigDecimal getFee(BigDecimal feeRate) {
        if (feeRate == null) return null;
        return feeRate.multiply(BigDecimal.ONE.subtract(this.feeOff));
    }
}
