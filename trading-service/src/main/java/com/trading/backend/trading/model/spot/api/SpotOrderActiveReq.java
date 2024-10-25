package com.google.backend.trading.model.spot.api;

import com.google.backend.trading.framework.web.RequestUnderlineToCamel;
import com.google.backend.trading.model.common.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author adam.wang
 * @date 2021/9/27
 */
@ApiModel(value = "现货委托活跃列表请求对象")
@Data
@RequestUnderlineToCamel
public class SpotOrderActiveReq extends PageReq {


    @NotBlank
    @ApiModelProperty(value = "币对", example = "BTC_USD")
    private String symbol;

    @NotNull
    @ApiModelProperty(name="only_current",value = "是否仅展示当前币对", example = "true")
    private Boolean onlyCurrent;

}
