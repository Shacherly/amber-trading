package com.google.backend.trading.model.user;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * 预警数据返回
 * @author david.chen
 * @date 2021/12/23 11:57
 */

@ApiModel(value = "预警数据请求返回")
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UserAlarmPriceRes {
    @ApiModelProperty(value = "预警ID", example = "21312")
    private Long id;
    @ApiModelProperty(value = "用户id", example = "21312")
    private String uid;
    @ApiModelProperty(value = "币对",example = "BTC_USD")
    private String symbol;
    @ApiModelProperty(value = "预警价格", example = "21312")
    private BigDecimal alarmPrice;
    @ApiModelProperty(value = "创建时间", notes = "13位毫秒时间戳", example = "1632822186544")
    private Date ctime;
}
