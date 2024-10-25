package com.google.backend.trading.model.internal.amp;

import com.google.backend.trading.framework.web.RequestUnderlineToCamel;
import com.google.backend.trading.model.common.PageTimeRangeExtReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author adam.wang
 * @date 2021/11/18 17:13
 */
@Data
@RequestUnderlineToCamel
@ApiModel(value = "APM仓位查询对象")
public class AmpPositionReq extends PageTimeRangeExtReq {

    /**
     * 用户id
     */
    @NotBlank
    @ApiModelProperty(name = "uid", value = "用户编码", required = true, example = "6191f3061c8ea878a165276b")
    private String uid;

    @ApiModelProperty(name = "position_id", value = "仓位ID", example = "b0a7cb9e-42c5-43eb-89e5-7bd5d2dd0b5e")
    private String positionId;

    @ApiModelProperty(value = "币对", example = "BTC_USD")
    private String symbol;

    @ApiModelProperty(value = "方向", example = "BUY")
    private String direction;

    @ApiModelProperty(value = "仓位状态 ACTIVE CLOSE", example = "ACTIVE")
    private String status;


}
