package com.google.backend.trading.model.internal.amp;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.google.backend.trading.model.common.PageTimeRangeReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * 获取客户交易流水请求对象
 * @author adam.wang
 * @date 2021/9/28 19:49
 */
@ApiModel(value = "获取客户交易流水请求对象")
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PositionFlowDetailReq extends PageTimeRangeReq {
    /**
     * 客户uid列表
     */
    @NotNull(message = "uid_list must not be null")
    @ApiModelProperty(name="uid_list",  required = true)
    @Size(min=1,message = "uid_list must not be null")
    private List<String> uidList;

    /**
     * 标的物列表
     */
    @ApiModelProperty(name="symbol_list", value = "币对列表")
    private List<String> symbolList;

    /**
     * 订单类型
     */
    @ApiModelProperty(name="type_list", value = "订单类型，LIMIT、MARKET、STOP-LIMIT、STOP-MARKET", required = true,
            notes="1: 限价单 2:市价单 3:限价条件单 4:市价条件单")
    private List<String> typeList;

    /**
     * 持仓方向
     */
    @ApiModelProperty(value = "持仓方向", notes="方向 1: LONG, 2: SHORT", example = "BUY")
    private String direction;

    /**
     * 合约类型
     */
    @ApiModelProperty(name="order_type_list",value = "合约类型列表：MARGIN（预留未起作用）")
    private List<String> orderTypeList;

    /**
     * 照顾需变更策略
     */
    @ApiModelProperty(name= "strategy_list", value = "执行策略, GTC, IOC, FOK",
            notes="1:GTC 2:IOC 3:FOK")
    private List<String> strategyList;
    
    /**
     * 订单状态列表
     */
    @ApiModelProperty(name = "status_list", value = "订单状态列表: PRE_TRIGGER 待触发 PENDING 准备中 EXECUTING 执行中 LOCKED 锁定 COMPLETED  完成 CANCELED 取消")
    private List<String> statusList;


}
