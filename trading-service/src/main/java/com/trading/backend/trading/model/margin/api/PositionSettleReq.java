package com.google.backend.trading.model.margin.api;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

/**
 * @author jiayi.zhang
 * @date 2021/9/28
 */
@ApiModel(value = "交割请求")
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PositionSettleReq {

    @NotBlank
    @ApiModelProperty(name="position_id", value = "仓位ID", required = true, example = "57e1a41a-584a-4774-bb34-217a385c4ec3")
    private String positionId;

    @NotNull
    @PositiveOrZero
    @ApiModelProperty(name = "quantity", value = "委托数量", required = true, example = "1231.1234")
    @Digits(integer = 32, fraction = 16)
    private BigDecimal quantity;

    @ApiModelProperty(value = "是否完全交割", example = "true")
    private boolean all;

}
