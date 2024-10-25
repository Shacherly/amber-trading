package com.google.backend.trading.model.web;

/**
 * @author adam.wang
 * @date 2021/10/16 19:25
 */

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author jiayi.zhang
 * @date 2021/9/28
 */
@ApiModel(value = "web历史订单信息")
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class OrderHistoryRes extends OrderInfoRes{

    @ApiModelProperty(value = "修改记录")
    List<OrderModificationVo> list;

}
