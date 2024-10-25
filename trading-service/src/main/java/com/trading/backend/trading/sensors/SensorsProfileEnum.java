package com.google.backend.trading.sensors;

import lombok.Getter;

/**
 * @author david.chen
 * @date 2022/3/10 15:20
 */
@Getter
public enum SensorsProfileEnum {
    /**
     * 是否已完成兑换
     */
    FIRST_SWAP("isfinished_firstswap"),
    /**
     * 是否已完成首笔现货交易
     */
    FIRST_SPOT("isfinished_firstspot"),
    /**
     * 是否已完成首笔杠杆交易
     */
    FIRST_MARGIN("isfinished_firstmargin"),
    ;

    SensorsProfileEnum(String code) {
        this.code = code;
    }

    private String code;

}
