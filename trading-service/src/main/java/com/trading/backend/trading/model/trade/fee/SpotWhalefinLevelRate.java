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
@ApiModel("现货交易费率")
public class SpotgoogleLevelRate {
    @ApiModelProperty("等级")
    private Integer level;
    @ApiModelProperty("BWC等级 -1级为非bwc")
    private Integer bwcLevel;
    @ApiModelProperty("条件")
    private String condition;
    @ApiModelProperty("费率")
    private BigDecimal rate;
}
