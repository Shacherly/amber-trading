package com.google.backend.trading.model.trade.fee;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author david.chen
 * @date 2021/12/30 17:09
 */
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@AllArgsConstructor
@NoArgsConstructor
public class TradeFeeConfigData{
    private String tag;
    private String uid;
    private BigDecimal spotFeeRate;
    private BigDecimal swapFeeRate;
    private BigDecimal marginFeeRate;
    private BigDecimal algorithmicFeeRate;
    private BigDecimal marginSettleFeeRate;
    private Boolean fundingCostEnable = true;

    public TradeFeeConfigData(String uid, Boolean fundingCostEnable) {
        this.uid = uid;
        this.fundingCostEnable = fundingCostEnable;
    }
}
