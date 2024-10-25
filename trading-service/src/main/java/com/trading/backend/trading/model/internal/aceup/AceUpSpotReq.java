package com.google.backend.trading.model.internal.aceup;

import com.google.backend.trading.framework.web.RequestUnderlineToCamel;
import com.google.backend.trading.model.common.PageTimeRangeExtReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author adam.wang
 * @date 2021/11/18 17:13
 */
@Data
@RequestUnderlineToCamel
@ApiModel(value = "aceup-现货查询对象")
public class AceUpSpotReq extends PageTimeRangeExtReq {

    /**
     * 用户id
     */
    @ApiModelProperty(name = "uid", value = "用户编码", required = true, example = "61628983d4b1a6d195d6f285")
    private String uid;

    @ApiModelProperty(name = "order_id", value = "仓位ID", example = "857eddeb-f650-49ce-a7ed-3f64af8cc644")
    private String orderId;

    @ApiModelProperty(name = "symbol_list", value = "币对", example = "[\"BTC_USDT\",\"ETH_USDT\"]")
    private List<String> symbolList;

    @ApiModelProperty(value = "方向", example = "BUY")
    private String direction;

    @ApiModelProperty(name = "type_list", value = "订单类型:LIMIT,MARKET,STOP_LIMIT,STOP_MARKET", allowableValues = "LIMIT,MARKET,STOP_LIMIT,STOP_MARKET",
            example = "[\"MARKET\",\"STOP_MARKET\"]")
    private List<String> typeList;

    @ApiModelProperty(name = "strategy_list", value = "订单策略:FOK,IOC,GTC", example = "[\"FOK\",\"IOC\"]")
    private List<String> strategyList;

    @ApiModelProperty(name = "status_list", value = "仓位状态 PRE_TRIGGER(待触发) PENDING(等待处理) EXECUTING(挂单中) EXCEPTION(执行异常) COMPLETED(完全成交) CANCELED(完全取消) " +
            "PART_CANCELED(部分成交取消)", example = "[\"PENDING\",\"EXECUTING\"]")
    private List<String> statusList;

    @ApiModelProperty(value = "PLACED_BY_CLIENT（用户下的手工单） PLACED_BY_API（用户通过API下的程序化订单） LIQUIDATION（清算订单） AUTO_CONVERSION（盈亏转换） OTC_SHOP(BOOKING用户来源)\n" +
            "  FORCE_CLOSE（强平或强制减仓）LOAN_LIQUIDATION（借贷强平订单）EARN_LIQUIDATION（理财清算）REPAY_WITH_COLLATERAL（借贷质押还款订单）AUTO_POSITION_SETTLE（自动交割）\n" +
            "  TAKE_PROFIT_STOP_LOSS（止盈止损）", example = "PLACED_BY_CLIENT")
    private String source;

}
