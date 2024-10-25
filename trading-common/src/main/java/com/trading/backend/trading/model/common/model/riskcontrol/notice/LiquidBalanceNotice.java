package com.google.backend.trading.model.common.model.riskcontrol.notice;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author adam.wang
 * @date 2021/10/22 14:52
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("清算现货通知")
public class LiquidBalanceNotice {
    @ApiModelProperty("用户uid")
    private String uid;
}
