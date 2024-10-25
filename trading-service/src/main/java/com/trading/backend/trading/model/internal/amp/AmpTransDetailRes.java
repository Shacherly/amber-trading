package com.google.backend.trading.model.internal.amp;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.google.backend.trading.constant.Constants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author david.chen
 * @date 2021/11/19 11:13
 */
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@ApiModel(value = "APM订单详情数据")
public class AmpTransDetailRes {
    @ApiModelProperty(value = "交易ID",example = "1032b321")
    private String transId;
    @ApiModelProperty(value = "订单id 可能为null",example = "1222")
    private String orderId;
    @ApiModelProperty(value = "用户id",example = "103321")
    private String uid;
    @ApiModelProperty(value = "交易方向 SELL、BUY", notes="方向 ELL、BUY", example = "BUY")
    private String direction;
    @ApiModelProperty(value = "交易对", example = "BTC_USDT")
    private String symbol;
    @ApiModelProperty(value = "成交量", example = "0.00481394")
    private BigDecimal tradedQuantity;
    @ApiModelProperty(value = "成交额度", example = "20.400573180674")
    private BigDecimal tradedAmount;
    @ApiModelProperty(value = "成交价格", example = "4237.8121")
    private BigDecimal tradedPrice;
    @ApiModelProperty(value = "手续费", example = "0.0101982156333490")
    private BigDecimal fee;
    @ApiModelProperty(value = "收费币种", example = "USD")
    private String feeCoin;
    /**
     * 创建时间
     */
    @ApiModelProperty(name="ctime", value = "创建时间", example = "1632883532072")
    private Date ctime;
    /**
     * 更新时间
     */
    @ApiModelProperty(name="mtime", value = "更新时间", example = "1632883532072")
    private Date mtime;
    @ApiModelProperty(value = "成交对应的仓位ID 可能为null", example = "a078bd5f-0ec8-4e31-8b44-24355e8c2ef3")
    private String positionId;
    @ApiModelProperty(value = "平仓盈亏（单位需要客户端，根据交易对截取quote 例如 BTC_USDT-》 quote是USDT） 可能为null", example = "-6.5675964275760618")
    private BigDecimal pnl;
    @ApiModelProperty(value = "请求资产平台状态 （PENDING）等待处理 （EXCEPTION）执行异常 （COMPLETED）成功", example = "COMPLETED")
    private String assetStatus;
    @ApiModelProperty(value = "成交类型 OPEN_POSITION 开仓 ADD_POSITION 加仓 CLOSE_POSITION 平仓  REDUCE_POSITION 减仓 SETTLE_POSITION 交割 SPOT 现货  SWAP 兑换",
    example = "OPEN_POSITION")
    private String type;

}
