package com.google.backend.trading.model.pdt;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * 查询Swap交易请求
 * @author adam.wang
 * @date 2021/10/5 11:36
 */
@Data
@ApiModel(value = "查询指定订单请求")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class SwapByIdReq {
    private String tradeId;
}
