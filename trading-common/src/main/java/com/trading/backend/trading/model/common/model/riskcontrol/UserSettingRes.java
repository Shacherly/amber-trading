package com.google.backend.trading.model.common.model.riskcontrol;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author adam.wang
 * @date 2021/10/25 14:06
 */
@ApiModel(value = "用户配置信息")
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UserSettingRes {
    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id", example = "21312")
    private String uid;
    /**
     * 用户id
     */
    @ApiModelProperty(value = "杠杆", example = "2")
    private BigDecimal leverage;
    /**
     * 全仓止损
     */
    @ApiModelProperty(value = "全仓止损", example = "2")
    private BigDecimal maxLoss;

    /**
     * 全仓止盈
     */
    @ApiModelProperty(value = "全仓止盈", example = "2")
    private BigDecimal takeProfit;


    /**
     * 是否抵押理财做保证金
     * true 开启抵押
     */
    @ApiModelProperty(value = "是否抵押理财做保证金", example = "true")
    private boolean earnPledge;

    /**
     * 是否开启理财清算
     * 默认true
     */
    @ApiModelProperty(value = "是否开启理财清算", example = "true")
    private boolean earnLiquid;

    /**
     * 是否开启平负余额开关
     * true 开启
     */
    @ApiModelProperty(value = "是否开启平负余额开关", example = "true")
    private boolean fillNegative;

    /**
     * 清算开关，默认是开
     */
    @ApiModelProperty(value = "清算开关，默认是开", example = "true")
    private boolean liquidation = true;
}
