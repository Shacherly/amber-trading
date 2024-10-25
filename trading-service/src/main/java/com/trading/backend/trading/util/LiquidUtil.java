package com.google.backend.trading.util;

import com.google.backend.trading.component.SymbolDomain;
import com.google.backend.trading.constant.Constants;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author adam.wang
 * @date 2021/10/23 17:52
 */
public class LiquidUtil {

    /**
     * 基准值
     */
    private  final static BigDecimal BASE_NUM = new BigDecimal("0.1");


    /**
     * 清算金额计算
     * @param negativeUsd
     * @return
     */
    public static  BigDecimal actLiquid(BigDecimal negativeUsd){
        negativeUsd = negativeUsd.abs().setScale(Constants.USD_LIQUID_PRECISION, RoundingMode.UP);
        if (negativeUsd.compareTo(BASE_NUM) >= 0) {
            return negativeUsd;
        }
        return BASE_NUM;
    }

    /**
     * 清算金额计算
     * @param bigDecimal
     * @return
     */
    public static  BigDecimal actLiquid(BigDecimal bigDecimal,String symbol){
        SymbolDomain symbolDomain = SymbolDomain.CACHE.get(symbol);
        BigDecimal midPrice = symbolDomain.midPrice();
        BigDecimal minNum = BASE_NUM.divide(midPrice, Constants.DEFAULT_PRECISION, RoundingMode.UP);
        bigDecimal = bigDecimal.abs();
        if (bigDecimal.abs().compareTo(minNum)>=0){
            return bigDecimal.abs();
        }
        return minNum;
    }
}
