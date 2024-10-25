package com.google.backend.trading.model.margin.api;

import com.google.backend.trading.framework.web.RequestUnderlineToCamel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author adam.wang
 * @date 2021/10/2 14:10
 */
@Data
@ApiModel(value = "仓位详情请求")
@RequestUnderlineToCamel
public class PositionDetailReq {

    @NotBlank
    @ApiModelProperty(name = "position_id",value = "仓位ID", required = true, example = "57e1a41a-584a-4774-bb34-217a385c4ec3")
    private String positionId;
}
