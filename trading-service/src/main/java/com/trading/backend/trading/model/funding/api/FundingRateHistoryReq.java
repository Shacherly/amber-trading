package com.google.backend.trading.model.funding.api;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.google.backend.trading.model.common.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotNull;

/**
 * 资金历史费用请求对象
 * @author adam.wang
 * @date 2021/9/30 10:31
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "资金历史费用请求对象")
@Data
public class FundingRateHistoryReq extends PageReq {

    @NotNull
    @ApiModelProperty(value = "币种",required = true, example = "BTC")
    private String coin;
}
