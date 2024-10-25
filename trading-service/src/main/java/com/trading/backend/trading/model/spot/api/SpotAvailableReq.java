package com.google.backend.trading.model.spot.api;

import com.google.backend.trading.framework.web.RequestUnderlineToCamel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 现货查询可用余额
 *
 * @author savion.chen
 * @date 2021/10/3 14:14
 */
@ApiModel(value = "现货可用余额查询请求")
@Data
@RequestUnderlineToCamel
public class SpotAvailableReq {

    @NotNull
    @ApiModelProperty(value = "币对", required = true, example = "BTC_USD")
    private String symbol;

}
