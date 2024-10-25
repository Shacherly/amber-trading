package com.google.backend.trading.model.user;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

/**
 * @author david.chen
 * @date 2021/12/22 19:28
 */
@Data
@ApiModel(value = "预警价格设置请求")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class AlarmPriceSetReq {
    @NotBlank
    @ApiModelProperty(value = "币对", example = "BTC_USD")
    private String symbol;
    @ApiModelProperty(value = "预警价格", example = "111")
    @Positive
    @Digits(integer = 32, fraction = 16)
    private BigDecimal alarmPrice;
}
