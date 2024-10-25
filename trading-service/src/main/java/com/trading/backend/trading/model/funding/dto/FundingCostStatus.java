package com.google.backend.trading.model.funding.dto;

/**
 * @author adam.wang
 * @date 2021/10/2 19:47
 */
public enum FundingCostStatus {
    /**
     * 等待状态
     */
    PENDING("PENDING"),
    /**
     * 已完成状态
     */
    COMPLETED("COMPLETED");
    /**
     * 仓位状态
     */
    private final String name;

    FundingCostStatus(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
