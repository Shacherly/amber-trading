package com.google.backend.trading.mapstruct.spot;

import java.math.BigDecimal;

/**
 * @author adam.wang
 * @date 2021/10/2 20:11
 */
public class ExpressionUtil {

    /**
     * BigDecimal b1-b2
     * @param b1
     * @param b2
     * @return
     */
    public static BigDecimal subtract(BigDecimal b1,BigDecimal b2){
        return b1.subtract(b2);
    }
}
