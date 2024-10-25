package com.google.backend.trading.model.trade.fee;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.google.backend.trading.model.trade.TradeLevelEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.lang.NonNull;

import java.math.BigDecimal;

/**
 * @author david.chen
 * @date 2022/1/4 14:27
 */
@Data
@ApiModel(value = "用户手续费")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UserFeeConfigRate {

    @ApiModelProperty("vip等级 （-1级为从未入金新用户，0级为普通0级用户）")
    private Integer vipLevel;

    @ApiModelProperty("bwc等级(1、2、3级，-1级为非bwc用户)")
    private Integer bwcLevel;

    @ApiModelProperty("google值 ")
    private Long googleValue;

    @ApiModelProperty("交易等级")
    private Integer tradeLevel;
    @ApiModelProperty("30天交易额")
    private BigDecimal tradeAmount30d;

    @ApiModelProperty("现货交易费率")
    private BigDecimal spotFeeRate;
    @ApiModelProperty("是否自定义 现货交易费率")
    private Boolean isCustomSpotFee = false;
    @ApiModelProperty("交换交易费率")
    private BigDecimal swapFeeRate;
    @ApiModelProperty("是否自定义 交换交易费率")
    private Boolean isCustomSwapFee = false;
    @ApiModelProperty("杠杆交易费率")
    private BigDecimal marginFeeRate;
    @ApiModelProperty("是否自定义 杠杆交易费率")
    private Boolean isCustomMarginFee = false;
    @ApiModelProperty("杠杆交割费率")
    private BigDecimal marginSettleFeeRate;
    @ApiModelProperty("是否自定义 杠杆交割费率")
    private Boolean isCustomMarginSettleFee = false;

    @ApiModelProperty("算法交易费率")
    private BigDecimal algorithmicFeeRate = new BigDecimal("0.0015");
    @ApiModelProperty("算法三角交易费率")
    private BigDecimal algoTriangleFeeRate = new BigDecimal("0.003");
    @ApiModelProperty("资金费率开关 true开启-> 原有fundingCost ; false关闭 -> fee为零 ")
    private Boolean fundingCostEnable = Boolean.TRUE;
    @JsonIgnore
    private TradeLevelEnum tradeLevelEnum = TradeLevelEnum.LEVEL_1;

    public void setUserTradeAmount30d(@NonNull BigDecimal tradeAmount30d) {
        this.tradeAmount30d = tradeAmount30d;
        this.tradeLevelEnum = TradeLevelEnum.getByTradeAmount(tradeAmount30d);
        this.tradeLevel = this.tradeLevelEnum.getLevel();
    }

    public BigDecimal getFundingCostRate() {
        if (fundingCostEnable == null) return BigDecimal.ONE;
        return fundingCostEnable ? BigDecimal.ONE : BigDecimal.ZERO;
    }

    public BigDecimal getSpotFeeRate() {
        return isCustomSpotFee ? spotFeeRate : tradeLevelEnum.getFee(spotFeeRate);
    }

    public BigDecimal getSwapFeeRate() {
        return isCustomSwapFee ? swapFeeRate : tradeLevelEnum.getFee(swapFeeRate);
    }

    public BigDecimal getMarginFeeRate() {
        return isCustomMarginFee ? marginFeeRate : tradeLevelEnum.getFee(marginFeeRate);
    }

    public BigDecimal getMarginSettleFeeRate() {
        return isCustomMarginSettleFee ? marginSettleFeeRate : tradeLevelEnum.getFee(marginSettleFeeRate);
    }

    public BigDecimal getAlgorithmicFeeRate() {
        return algorithmicFeeRate;
    }

    public BigDecimal getAlgoTriangleFeeRate() {
        return algoTriangleFeeRate;
    }
}
