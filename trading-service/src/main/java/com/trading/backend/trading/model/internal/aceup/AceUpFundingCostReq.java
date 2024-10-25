package com.google.backend.trading.model.internal.aceup;

import com.google.backend.trading.framework.web.RequestUnderlineToCamel;
import com.google.backend.trading.model.common.PageTimeRangeExtReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author david.chen
 * @date 2022/3/17 19:29
 */

@Data
@RequestUnderlineToCamel
@ApiModel(value = "aceup-杠杠资金费率请求")
public class AceUpFundingCostReq extends PageTimeRangeExtReq {
    @ApiModelProperty(value = "流水ID")
    private String uuid;
    @ApiModelProperty(name = "仓位ID", value = "仓位ID")
    private String positionId;
    @ApiModelProperty(name = "订单ID", value = "订单ID")
    private String orderId;
    @ApiModelProperty(value = "用户ID")
    private String uid;
    @ApiModelProperty(value = "方向", example = "BUY")
    private String direction;
    @ApiModelProperty(name = "symbol_list", value = "币对", example = "[\"BTC_USDT\",\"ETH_USDT\"]")
    private List<String> symbolList;
    @ApiModelProperty(value = "状态PENDING COMPLETED")
    private List<String> statusList;
}
