package com.google.backend.trading.model.swap.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.google.common.collect.Lists;
import com.google.backend.trading.model.common.PageTimeRangeReq;
import com.google.backend.trading.model.trade.OrderStatus;
import com.google.backend.trading.util.CommonUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Pattern;
import java.util.List;

/**
 * @author adam.wang
 * @date 2021/9/28 15:48
 */
@ApiModel(value = "lite版本兑换历史列表请求对象")
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class SwapOrderHistoryLiteReq extends PageTimeRangeReq {

    @ApiModelProperty(value = "币对", required = true, example = "USD")
    private String symbol;

    @ApiModelProperty(value = "状态", example = "COMPLETED")
    @Pattern(regexp = "COMPLETED|CANCELED", message = "状态错误")
    private String status;


    @JsonIgnore
    @ApiModelProperty(hidden = true)
    private List<String> statusList;
    @JsonIgnore
    @ApiModelProperty(hidden = true)
    private String baseCoin;
    @JsonIgnore
    @ApiModelProperty(hidden = true)
    private String quoteCoin;

    public List<String> getStatusList() {
        if (status == null) {
            return OrderStatus.HISTORY_STATUS;
        } else {
            return Lists.newArrayList(status);
        }
    }

    public String getBaseCoin() {
        if (symbol != null) {
            return CommonUtils.getBaseCoin(symbol);
        }
        return baseCoin;
    }

    public String getQuoteCoin() {
        if (symbol != null) {
            return CommonUtils.getQuoteCoin(symbol);
        }
        return quoteCoin;
    }
}
