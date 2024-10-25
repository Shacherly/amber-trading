package com.google.backend.trading.model.internal.aceup;

import com.google.backend.trading.framework.web.RequestUnderlineToCamel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author david.chen
 * @date 2022/3/17 19:26
 */

@Data
@RequestUnderlineToCamel
@ApiModel(value = "aceup-杠杠仓位返回")
public class AceUpMarginPositionRes {
    @ApiModelProperty(name = "position_id", value = "仓位ID")
    private String positionId;
    @ApiModelProperty("用户ID")
    private String uid;
    @ApiModelProperty(name = "symbol", value = "交易对")
    private String symbol;
    @ApiModelProperty(name = "direction", value = "方向")
    private String direction;
    @ApiModelProperty(name = "quantity", value = "当前持仓数量")
    private BigDecimal quantity;
    @ApiModelProperty(name = "price", value = "当前持仓均价")
    private BigDecimal price;
    @ApiModelProperty(value = "是否自动交割")
    private Boolean autoSettle;
    @ApiModelProperty(value = "止损价格 可能为null")
    private BigDecimal stopLossPrice;
    @ApiModelProperty(value = "止损占仓比")
    private BigDecimal stopLossPercentage;
    @ApiModelProperty(value = "止盈价格 可能为null")
    private BigDecimal takeProfitPrice;
    @ApiModelProperty(value = "止盈占仓比")
    private BigDecimal takeLossPercentage;
    @ApiModelProperty(value = "最大持仓数量")
    private BigDecimal maxQuantity;
    @ApiModelProperty(value = "实现盈亏")
    private BigDecimal pnl;
    @ApiModelProperty(name = "status", value = "仓位状态 ACTIVE CLOSE")
    private String status;

    @ApiModelProperty(value = "更新时间", example = "1.6")
    private Date mtime;

    @ApiModelProperty(value = "创建时间", example = "1.6")
    private Date ctime;

}
