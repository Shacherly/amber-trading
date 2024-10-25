package com.google.backend.trading.model.trade;


/**
 * 请求发单接口状态
 *
 * @author savion.chen
 * @date 2021/9/29 18:29
 */
public enum PdtStatus {
    /**
     * 等待处理
     */
    PENDING,
    /**
     * 执行异常
     */
    EXCEPTION,
    /**
     * 完成
     */
    COMPLETED,
}
