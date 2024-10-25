package com.google.backend.trading.model.internal.aceup;

import com.google.backend.trading.framework.web.RequestUnderlineToCamel;
import com.google.backend.trading.model.common.PageTimeRangeExtReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author david.chen
 * @date 2022/3/17 19:26
 */
@Data
@RequestUnderlineToCamel
@ApiModel(value = "aceup-杠杠仓位请求")
public class AceUpMarginPositionReq extends PageTimeRangeExtReq {
    @ApiModelProperty(name = "position_id", value = "仓位ID")
    private String positionId;
    @ApiModelProperty(name = "order_id", value = "订单id")
    private String orderId;
    @ApiModelProperty("用户ID")
    private String uid;
    @ApiModelProperty(name = "symbol_list", value = "交易对", example = "[\"BTC_USDT\",\"ETH_USDT\"]")
    private List<String> symbolList;
    @ApiModelProperty(name = "direction", value = "方向")
    private String direction;
    @ApiModelProperty(name = "status", value = "仓位状态 ACTIVE CLOSE")
    private String status;
}
