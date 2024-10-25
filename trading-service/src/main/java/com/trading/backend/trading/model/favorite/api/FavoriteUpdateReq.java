package com.google.backend.trading.model.favorite.api;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author adam.wang
 * @date 2021/10/4 16:07
 */
@ApiModel(value = "收藏更新请求")
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class FavoriteUpdateReq {

    @NotNull
    @ApiModelProperty(value = "币对",required = true, example = "BTC_USD")
    private String symbol;

    @NotNull
    @ApiModelProperty(value = "true 表示收藏， false 表示取消收藏",required = true, example = "true")
    private Boolean favorite;

}
