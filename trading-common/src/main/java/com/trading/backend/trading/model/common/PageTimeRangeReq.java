package com.google.backend.trading.model.common;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 分页查询基类，包含起止时间
 * @author adam.wang
 * @date 2021/9/30 10:13
 */
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@ApiModel(value = "分页查询基类，包含起止时间")
public class PageTimeRangeReq extends PageReq {

    @ApiModelProperty(name = "start_time", value = "开始时间", required = false, example = "1633017600000")
    private Long startTime;

    @ApiModelProperty(name ="end_time",value = "结束时间",required = false, example = "1653425311991")
    private Long endTime;
}
