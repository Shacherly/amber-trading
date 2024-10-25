package com.google.backend.trading.model.funding.api;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.google.backend.trading.model.common.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 实时费率请求实体
 * @author adam.wang
 * @date 2021/9/28 17:29
 */
@ApiModel(value = "实时费率请求实体")
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class FundingRateReq extends PageReq {

    @ApiModelProperty(value = "币种", example = "BTC")
    private String coin;
}
