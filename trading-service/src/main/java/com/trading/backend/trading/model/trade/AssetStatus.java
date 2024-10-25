package com.google.backend.trading.model.trade;


/**
 * 请求资产平台状态
 *
 * @author savion.chen
 * @date 2021/9/29 18:29
 */
public enum AssetStatus {
    /**
     * 等待处理
     */
    PENDING,
    /**
     * 执行异常
     */
    EXCEPTION,
    /**
     * 成功
     */
    COMPLETED,
}
