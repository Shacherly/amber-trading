package com.google.backend.trading.model.favorite.api;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.google.backend.trading.model.user.UserInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author adam.wang
 * @date 2021/10/4 16:07
 */
@ApiModel(value = "覆盖更新收藏请求")
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class FavoriteOverrideReq {

    @NotNull
    @ApiModelProperty(value = "币对",required = true)
    private List<String> symbolList;

}
