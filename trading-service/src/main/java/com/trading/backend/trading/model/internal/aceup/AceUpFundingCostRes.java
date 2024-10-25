package com.google.backend.trading.model.internal.aceup;

import com.google.backend.trading.framework.web.RequestUnderlineToCamel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author david.chen
 * @date 2022/3/17 19:29
 */
@Data
@RequestUnderlineToCamel
@ApiModel(value = "aceup-杠杠资金费率返回")
public class AceUpFundingCostRes {
    @ApiModelProperty(value = "流水ID")
    private String uuid;
    @ApiModelProperty(value = "用户ID")
    private String uid;
    @ApiModelProperty(value = "状态PENDING COMPLETED")
    private String status;
    @ApiModelProperty(value = "仓位数量")
    private BigDecimal quantity;
    @ApiModelProperty(value = "资金费率的费用")
    private BigDecimal fundingCost;
    @ApiModelProperty(value = "仓位币对")
    private String symbol;
    @ApiModelProperty(value = "方向")
    private String direction;
    @ApiModelProperty(value = "仓位ID")
    private String positionId;
    @ApiModelProperty(value = "仓位币种")
    private String coin;
    @ApiModelProperty(value = "创建时间")
    private Date ctime;
    @ApiModelProperty(value = "更新时间")
    private Date mtime;
    @ApiModelProperty(value = "用户借出利率")
    private BigDecimal lend;
    @ApiModelProperty(value = "用户借入利率")
    private BigDecimal borrow;
    @ApiModelProperty(value = "收取资金费率轮次的时间点（毫秒时间戳）")
    private Long round;
}
