package com.google.backend.trading.model.trade.fee;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author david.chen
 * @date 2022/1/4 14:18
 */
@Data
@ApiModel("杠杆交易费率")
public class MargingoogleLevelRate {
    @ApiModelProperty("等级")
    private Integer level;
    @ApiModelProperty("BWC等级 -1级为非bwc")
    private Integer bwcLevel;
    @ApiModelProperty("条件")
    private String condition;
    @ApiModelProperty("费率")
    private BigDecimal baseRate;
    @ApiModelProperty("交割手续费率")
    private BigDecimal settleRate;
}
