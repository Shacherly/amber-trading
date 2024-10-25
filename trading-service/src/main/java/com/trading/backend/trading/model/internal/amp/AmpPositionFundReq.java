package com.google.backend.trading.model.internal.amp;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
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
@ApiModel(value = "APM仓位资金查询对象")
public class AmpPositionFundReq extends PageTimeRangeExtReq {


    @ApiModelProperty(name = "activities_id",value = "流水ID", example = "ca7294a3-c7cf-485b-b093-16ea6abfb978")
    private String activitiesId;
    /**
     * 用户id
     */
    @NotBlank(message = "用户ID不能为空")
    @ApiModelProperty(name = "uid",value = "用户id", required = true, example = "615309c065a76ea30fd8a156")
    private String uid;

    @ApiModelProperty(name = "position_id",value = "仓位ID", example = "57e1a41a-584a-4774-bb34-217a385c4ec3")
    private String positionId;

    @ApiModelProperty(value = "币对，ALL不传参数", example = "BTC_USD")
    private String symbol;

    @ApiModelProperty(value = "方向，ALL不传参数", example = "BUY")
    private String direction;

    @ApiModelProperty(value = "仓位状态 PENDING（等待状态） COMPLETED（已完成状态），ALL不传参数", example = "PENDING")
    private String status;


}
