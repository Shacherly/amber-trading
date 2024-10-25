package com.google.backend.trading.model.pdt;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @author adam.wang
 * @date 2021/10/5 11:32
 */
@Data
@ApiModel(value = "swap询价的返回")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CrexSwapPriceRes {
    private String price;
}
