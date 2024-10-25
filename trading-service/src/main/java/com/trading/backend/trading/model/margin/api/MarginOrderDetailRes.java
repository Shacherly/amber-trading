package com.google.backend.trading.model.margin.api;

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
@ApiModel(value = "杠杆详情信息")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Data
public class MarginOrderDetailRes extends MarginOrderInfoRes {
    @ApiModelProperty(value = "历史仓位列表")
    private List<MarginOrderModificationVo> modifications;
}
