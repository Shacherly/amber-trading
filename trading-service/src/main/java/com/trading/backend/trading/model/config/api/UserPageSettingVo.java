package com.google.backend.trading.model.config.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author jiayi.zhang
 * @date 2021/9/30
 */
@Data
@ApiModel(value = "用户页面行情卡片配置")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UserPageSettingVo {
    @NotNull(message = "price_card must not be null")
    @ApiModelProperty(value = "结算时区", required = true, example = "json,前端把data和layout放在一起传过来")
    private JsonNode priceCard;
}
