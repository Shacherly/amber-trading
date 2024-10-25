package com.google.backend.trading.model.user;

import com.google.backend.trading.framework.web.RequestUnderlineToCamel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author david.chen
 * @date 2021/12/23 10:27
 */
@ApiModel(value = "预警 用户币种预警查询请求")
@Data
@RequestUnderlineToCamel
public class UserAlarmPriceReq {

    @NotNull
    @ApiModelProperty(value = "币对", required = true, example = "BTC_USD")
    private String symbol;
}
