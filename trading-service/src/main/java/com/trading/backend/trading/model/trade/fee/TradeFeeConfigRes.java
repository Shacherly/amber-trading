package com.google.backend.trading.model.trade.fee;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.PipedReader;
import java.math.BigDecimal;
import java.util.List;

/**
 * 交易手续费说明展示
 *
 * @author david.chen
 * @date 2022/1/4 11:37
 */
@Data
@ApiModel(value = "交易手续费说明展示")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class TradeFeeConfigRes {
    @ApiModelProperty("用户当前手续费率")
    private UserFeeConfigRate userFeeConfigRate;
    @ApiModelProperty("交易等级费率")
    private List<TradeLevelRate> tradeLevelRateList;
    @ApiModelProperty("现货交易费率")
    private List<SpotgoogleLevelRate> tradeSpotRateList;
    @ApiModelProperty("杠杆交易费率")
    private List<MargingoogleLevelRate> tradeMarginRateList;
}
