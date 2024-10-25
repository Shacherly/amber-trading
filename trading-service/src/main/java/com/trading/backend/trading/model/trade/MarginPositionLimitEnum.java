package com.google.backend.trading.model.trade;

import com.google.backend.trading.component.CoinDomain;
import com.google.backend.trading.util.CoinUtil;
import com.google.backend.trading.util.CommonUtils;
import org.springframework.data.util.Pair;

import java.math.BigDecimal;

/**
 * 总持仓限额
 *
 * @author trading
 * @date 2021/10/19 16:17
 */
public enum MarginPositionLimitEnum {

    /**
     * 杠杆倍数对应的限额
     */
    LEVERAGE_1X(1, BigDecimal.ONE),

    LEVERAGE_2X(2, new BigDecimal("0.9")),

    LEVERAGE_3X(3, new BigDecimal("0.8")),

    LEVERAGE_4X(4, new BigDecimal("0.7")),

    LEVERAGE_5X(5, new BigDecimal("0.6")),

    ;
    MarginPositionLimitEnum(Integer leverage, BigDecimal factor) {
        this.leverage = leverage;
        this.factor = factor;
    }

    /**
     * 平台默认基础持仓限额
     */
    public static final BigDecimal BASE_POSITION_LIMITS = new BigDecimal("80000000");

    /**
     * 杠杆设置倍数
     */
    private final Integer leverage;
    /**
     * 杠杆设置乘数（因子）
     */
    private final BigDecimal factor;


    public Integer getLeverage() {
        return leverage;
    }

    public BigDecimal getFactor() {
        return factor;
    }

    /**
     * 所有交易对的持仓限额
     * @param leverage
     * @return
     */
    public static BigDecimal totalPositionLimit(BigDecimal leverage) {
        Integer intLeverage = leverage.intValue();
        intLeverage = Math.max(intLeverage, 1);
        for (MarginPositionLimitEnum value : MarginPositionLimitEnum.values()) {
            if (value.getLeverage().compareTo(intLeverage) == 0) {
                return value.getFactor().multiply(BASE_POSITION_LIMITS);
            }
        }
        return BigDecimal.ZERO;
    }

    /**
     * 单个交易对的持仓限额
     * @param leverage
     * @param symbol
     * @return
     */
    public static BigDecimal positionLimitBySymbol(BigDecimal leverage, String symbol) {
        Pair<String, String> coinPair = CommonUtils.coinPair(symbol);
        String baseCoin = coinPair.getFirst();
        String quoteCoin = coinPair.getSecond();
        BigDecimal totalPositionLimit = totalPositionLimit(leverage);
        BigDecimal baseLiquidityIndex = CoinDomain.nonNullGet(baseCoin).getCommonConfig().getBaseLiquidityIndex();
        BigDecimal quoteLiquidityIndex = CoinDomain.nonNullGet(quoteCoin).getCommonConfig().getQuoteLiquidityIndex();
        return totalPositionLimit.multiply(baseLiquidityIndex).multiply(quoteLiquidityIndex);
    }
}
