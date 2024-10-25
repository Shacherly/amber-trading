package com.google.backend.trading.model.internal.aceup;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.backend.trading.framework.web.RequestUnderlineToCamel;
import com.google.backend.trading.model.trade.AssetStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author david.chen
 * @date 2022/3/17 19:24
 */
@Data
@RequestUnderlineToCamel
@ApiModel(value = "aceup-杠杠订单成交记录返回")
public class AceUpMarginTransRes {
    @ApiModelProperty(name = "trans_id", value = "成交ID", example = "57e1a41a-584a-4774-bb34-217a385c4ec3")
    private String transId;

    @ApiModelProperty(name = "order_id", value = "订单ID", example = "57e1a41a-584a-4774-bb34-217a385c4ec3")
    private String orderId;

    @ApiModelProperty(name = "position_id", value = "仓位ID", example = "57e1a41a-584a-4774-bb34-217a385c4ec3")
    private String positionId;

    @ApiModelProperty(name = "uid", value = "uid", example = "6166cbc138f16e6a3fa634a6")
    private String uid;

    @ApiModelProperty(name = "type", value = "成交类型 OPEN_POSITION 开仓 ADD_POSITION 加仓 CLOSE_POSITION 平仓  REDUCE_POSITION 减仓 SETTLE_POSITION 交割 SPOT 现货  SWAP 兑换")
    private String type;

    @ApiModelProperty(name = "order_type", value = "成交对应的订单类型 STOP_MARKET 条件市价单, STOP_LIMIT 条件限价单, MARKET 市价单, LIMIT 限价单")
    private String orderType;

    @ApiModelProperty(name = "symbol", value = "币对", example = "BTC_USDT")
    private String symbol;


    @ApiModelProperty(value = "方向", example = "BUG")
    private String direction;

    @ApiModelProperty(name = "base_quantity", value = "成交量 quote", example = "1.6")
    private BigDecimal baseQuantity;

    @ApiModelProperty(name = "quote_quantity", value = "成交额度 base", example = "1.6")
    private BigDecimal quoteQuantity;

    @ApiModelProperty(value = "开仓价格 可能为null", example = "1.6")
    private BigDecimal openPrice;

    @ApiModelProperty(value = "成交均价", example = "1.6")
    private BigDecimal price;

    @ApiModelProperty(value = "实现盈亏")
    private BigDecimal pnl;

    @ApiModelProperty(value = "手续费", example = "1.6")
    private BigDecimal fee;

    @ApiModelProperty(name = "fee_coin", value = "手续费币种", example = "1.6")
    private String feeCoin;

    @ApiModelProperty(name = "status", value = "成交状态", example = "FAILED（失败）、COMPLETED（成功）")
    private String status;

    @ApiModelProperty(name = "error", value = "(pdt_error)pdt发单失败 (asset_error)资金请求失败", example = "pdt_error")
    private String error;

    @ApiModelProperty(hidden = true)
    @JsonIgnore
    private String assetStatus;

    @ApiModelProperty(hidden = true)
    @JsonIgnore
    private String pdtStatus;

    @ApiModelProperty(value = "订单来源(MARGIN,SPOT,SWAP的全集) PLACED_BY_CLIENT 用户主动发生 LIQUIDATION 清算发生 AUTO_CONVERSION 盈亏转换发生 PLACED_BY_API API用户发生  FIX_NEGATIVE 平负余额发生 OTC_SHOP BOOKING用户发生 LOAN_LIDQUIDATION 借贷发生 REPAY_WITH_COLLATERAL 借贷质押还款 FORCE_CLOSE 强平发生 AUTO_POSITION_SETTLE 自动交割 TAKE_PROFIT_STOP_LOSS 止盈止损")
    private String source;

    @ApiModelProperty(value = "更新时间", example = "1.6")
    private Date mtime;

    @ApiModelProperty(value = "创建时间", example = "1.6")
    private Date ctime;

    public String getError() {
        if ("FAILED".equals(status)) {
            if (!AssetStatus.COMPLETED.name().equals(assetStatus)) {
                return "asset_error";
            } else {
                return "pdt_error";
            }
        }
        return error;
    }
}
