package com.google.backend.trading.model.common;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 分页查询基类，包含创建、修改的起止时间
 * @author adam.wang
 * @date 2021/9/30 10:13
 */
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@ApiModel(value = "分页查询基类，包含起止时间")
public class PageTimeRangeExtReq extends PageReq {

    @ApiModelProperty(name = "start_ctime", value = "创建时间开始时间", required = false, example = "1634784180000")
    private Long startCtime;

    @ApiModelProperty(name = "end_ctime", value = "创建时间结束时间", required = false, example = "1666320180000")
    private Long endCtime;

    @ApiModelProperty(name = "start_mtime", value = "修改时间开始时间", required = false, example = "1634784180000")
    private Long startMtime;

    @ApiModelProperty(name = "end_mtime", value = "修改时间结束时间", required = false, example = "1666320180000")
    private Long endMtime;
}
