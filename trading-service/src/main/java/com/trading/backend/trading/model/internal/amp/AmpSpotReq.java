package com.google.backend.trading.model.internal.amp;

import com.google.backend.trading.framework.web.RequestUnderlineToCamel;
import com.google.backend.trading.model.common.PageTimeRangeReq;
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
@ApiModel(value = "APM现货查询对象")
public class AmpSpotReq extends PageTimeRangeReq {

    /**
     * 用户id
     */
    @NotBlank
    @ApiModelProperty(name = "uid", value = "用户编码", required = true, example = "61628983d4b1a6d195d6f285")
    private String uid;

    @ApiModelProperty(name = "order_id", value = "仓位ID", example = "857eddeb-f650-49ce-a7ed-3f64af8cc644")
    private String orderId;

    @ApiModelProperty(value = "订单类型:LIMIT,MARKET,STOP_LIMIT,STOP_MARKET", allowableValues = "LIMIT,MARKET,STOP_LIMIT,STOP_MARKET",
            example = "MARKET")
    private String type;

    @ApiModelProperty(value = "订单策略:FOK,IOC,GTC", allowableValues = "FOK,IOC,GTC", example = "FOK")
    private String strategy;

    @ApiModelProperty(value = "币对", example = "BTC_USD")
    private String symbol;

    @ApiModelProperty(value = "方向", example = "BUY")
    private String direction;

    @ApiModelProperty(value = "仓位状态 PRE_TRIGGER(待触发) PENDING(等待处理) EXECUTING(挂单中) EXCEPTION(执行异常) COMPLETED(完全成交) CANCELED(完全取消) " +
			"PART_CANCELED(部分成交取消)", example = "COMPLETED")
    private String status;


}
