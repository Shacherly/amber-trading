package com.google.backend.trading.model.margin.api;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @author adam.wang
 * @date 2021/10/11 16:35
 */
@Data
@ApiModel(value = "仓位减仓返回")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PositionReduceRes {
}
