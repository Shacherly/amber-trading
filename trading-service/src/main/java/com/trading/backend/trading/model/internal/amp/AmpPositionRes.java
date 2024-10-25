package com.google.backend.trading.model.internal.amp;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.google.backend.trading.constant.Constants;
import com.google.backend.trading.model.common.PageTimeRangeReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author adam.wang
 * @date 2021/11/18 17:13
 */
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@ApiModel(value = "APM仓位返回对象")
public class AmpPositionRes {

    @ApiModelProperty(name = "position_id",value = "仓位ID", example = "57e1a41a-584a-4774-bb34-217a385c4ec3")
    private String positionId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column trade_position.uid
     *
     * @mbg.generated
     */
    @NotBlank
    @ApiModelProperty(name = "uid",value = "用户编码", required = true, example = "57e1a41a-584a-4774-bb34-217a385c4ec3")
    private String uid;

    /**
     * Database Column Remarks:
     *   仓位状态 active close
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column trade_position.status
     *
     * @mbg.generated
     */
    @ApiModelProperty(value = "仓位状态 ACTIVE CLOSE", example = "ACTIVE")
    private String status;

    /**
     * Database Column Remarks:
     *   币对（下划线分割小写）
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column trade_position.symbol
     *
     * @mbg.generated
     */
    @ApiModelProperty(value = "币对", example = "BTC_USD")
    private String symbol;
    /**
     * Database Column Remarks:
     *   方向 long short
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column trade_position.direction
     *
     * @mbg.generated
     */
    @ApiModelProperty(value = "方向", example = "BUY")
    private String direction;

    /**
     * Database Column Remarks:
     *   持仓数量
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column trade_position.quantity
     *
     * @mbg.generated
     */
    @ApiModelProperty(value = "持仓数量", example = "2")
    private BigDecimal quantity;

    /**
     * Database Column Remarks:
     *   仓位成交均价
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column trade_position.price
     *
     * @mbg.generated
     */
    @ApiModelProperty(value = "仓位成交均价", example = "2")
    private BigDecimal price;

    /**
     * Database Column Remarks:
     *   是否自动交割
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column trade_position.auto_settle
     *
     * @mbg.generated
     */
    @ApiModelProperty(name="auto_settle",value = "是否自动交割", example = "true")
    private Boolean autoSettle;

    /**
     * Database Column Remarks:
     *   止损价格
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column trade_position.stop_loss_price
     *
     * @mbg.generated
     */
    @ApiModelProperty(name="stop_loss_price",value = "止损价格 可能为null", example = "2.2")
    private BigDecimal stopLossPrice;

    /**
     * Database Column Remarks:
     *   止损占仓位比，默认0，表示不开启止损
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column trade_position.stop_loss_percentage
     *
     * @mbg.generated
     */
    @ApiModelProperty(name="stop_loss_percentage",value = "止损占仓位比，默认0，表示不开启止损", example = "2.2")
    private BigDecimal stopLossPercentage;

    /**
     * Database Column Remarks:
     *   止盈价格
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column trade_position.take_profit_price
     *
     * @mbg.generated
     */
    @ApiModelProperty(name="take_profit_price",value = "止盈价格 可能为null", example = "2.2")
    private BigDecimal takeProfitPrice;

    /**
     * Database Column Remarks:
     *   止盈占仓位比，默认0，表示不开启止盈
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column trade_position.take_profit_percentage
     *
     * @mbg.generated
     */
    @ApiModelProperty(name="take_profit_percentage",value = "止盈占仓位比，默认0，表示不开启止盈", example = "2.2")
    private BigDecimal takeProfitPercentage;

    /**
     * Database Column Remarks:
     *   最大持仓数量
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column trade_position.max_quantity
     *
     * @mbg.generated
     */
    @ApiModelProperty(name="max_quantity", value = "最大持仓数量", example = "2.2")
    private BigDecimal maxQuantity;

    /**
     * Database Column Remarks:
     *   仓位已实现pnl（包括资金费率和手续费）
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column trade_position.pnl
     *
     * @mbg.generated
     */
    @ApiModelProperty(value = "仓位已实现pnl（包括资金费率和手续费）", example = "2.2")
    private BigDecimal pnl;

    @ApiModelProperty(name="pnl_coin",value = "pnl单位", example = "USD")
    private String pnlCoin = Constants.BASE_COIN;

    @ApiModelProperty(value = "仓位未实现pnl（包括资金费率和手续费）", example = "2.2")
    private BigDecimal unpnl;

    @ApiModelProperty(name="unpnl_coin",value = "unpnl单位", example = "USD")
    private String unpnlCoin = Constants.BASE_COIN;
    /**
     * Database Column Remarks:
     *   创建时间
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column trade_position.ctime
     *
     * @mbg.generated
     */
    @ApiModelProperty(value = "创建时间")
    private Date ctime;

    /**
     * Database Column Remarks:
     *   修改时间
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column trade_position.mtime
     *
     * @mbg.generated
     */
    @ApiModelProperty(value = "修改时间")
    private Date mtime;

}
