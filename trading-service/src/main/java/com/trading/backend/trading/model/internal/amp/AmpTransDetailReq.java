package com.google.backend.trading.model.internal.amp;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.google.backend.trading.framework.web.RequestUnderlineToCamel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author david.chen
 * @date 2021/11/19 11:13
 */
@Data
@RequestUnderlineToCamel
@ApiModel(value = "APM订单详情查询对象")
public class AmpTransDetailReq {
    @NotBlank(message = "交易ID不能为空")
    @ApiModelProperty(name = "trans_id", value = "交易详情id", required = true, example = "fc5f8b75-5605-4ceb-af6c-e11ce0fddc52")
    private String transId;
    @NotBlank(message = "用户ID不能为空")
    @ApiModelProperty(name = "uid", value = "uid", required = true, example = "616289a2d4b1a6d195d6f286")
    private String uid;
}
