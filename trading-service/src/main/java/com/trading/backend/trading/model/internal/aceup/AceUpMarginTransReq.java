package com.google.backend.trading.model.internal.aceup;

import com.google.backend.trading.framework.web.RequestUnderlineToCamel;
import com.google.backend.trading.model.common.PageTimeRangeExtReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author david.chen
 * @date 2022/3/17 19:24
 */
@Data
@RequestUnderlineToCamel
@ApiModel(value = "aceup-杠杠订单成交记录请求")
public class AceUpMarginTransReq extends PageTimeRangeExtReq {

    @ApiModelProperty(name = "trans_id", value = "交易ID")
    private String transId;

    @ApiModelProperty(name = "order_id", value = "订单ID")
    private String orderId;

    @ApiModelProperty(name = "position_id", value = "仓位ID")
    private String positionId;

    @ApiModelProperty(name = "uid", value = "用户ID")
    private String uid;

    @ApiModelProperty(name = "type", value = "成交类型 OPEN_POSITION 开仓 ADD_POSITION 加仓 CLOSE_POSITION 平仓  REDUCE_POSITION 减仓 SETTLE_POSITION 交割 SPOT 现货  SWAP 兑换")
    private String type;

    @ApiModelProperty(name = "order_type_list", example = "[\"STOP_MARKET\",\"STOP_LIMIT\"]", value = "成交对应的订单类型 STOP_MARKET 条件市价单, STOP_LIMIT 条件限价单, MARKET 市价单, LIMIT 限价单")
    private List<String> orderTypeList;

    @ApiModelProperty(name = "symbol_list", value = "币对", example = "[\"BTC_USD\", \"BTC_USDT\"]")
    private List<String> symbolList;

    @ApiModelProperty(value = "方向", example = "BUY")
    private String direction;

    @ApiModelProperty(value = "PLACED_BY_CLIENT（用户下的手工单） PLACED_BY_API（用户通过API下的程序化订单） LIQUIDATION（清算订单） AUTO_CONVERSION（盈亏转换） OTC_SHOP(BOOKING用户来源)\n" +
            "  FORCE_CLOSE（强平或强制减仓）LOAN_LIQUIDATION（借贷强平订单）EARN_LIQUIDATION（理财清算）REPAY_WITH_COLLATERAL（借贷质押还款订单）AUTO_POSITION_SETTLE（自动交割）\n" +
            "  TAKE_PROFIT_STOP_LOSS（止盈止损）", example = "PLACED_BY_CLIENT")
    private String source;
}
