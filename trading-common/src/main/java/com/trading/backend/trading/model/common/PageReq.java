package com.google.backend.trading.model.common;


import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 分页查询基类（不含起止时间）
 * @author adam.wang
 * @date 2021/9/28 11:45
 */

@Data
@ApiModel(value = "分页查询基类")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PageReq {
    /**
     * 页
     */
    @NotNull(message ="page must not be null")
    @Min(1)
    @Max(2147483647)
    @ApiModelProperty(value = "页",required = true,example = "1")
    private Integer page = 1;

    /**
     * 页大小
     */
    @NotNull(message ="page_size must not be null")
    @Min(1)
    @Max(1000)
    @ApiModelProperty(name = "page_size", value = "页大小", required = true, example = "10")
    private Integer pageSize = 10;

}
