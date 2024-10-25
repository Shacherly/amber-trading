package com.google.backend.trading.model.margin;

/**
 * @author adam.wang
 * @date 2021/10/2 19:47
 */
public enum PositionStatus {
    /**
     * 活跃仓位
     */
    ACTIVE("ACTIVE"),
    /**
     * 关闭仓位
     */
    CLOSE("CLOSE");
    /**
     * 仓位状态
     */
    private final String positionStatus;

    PositionStatus(String positionStatus) {
        this.positionStatus = positionStatus;
    }

    public static Boolean isActive(String status){
        return ACTIVE.name().equals(status);
    }
}
