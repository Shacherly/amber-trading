package com.google.backend.trading.model.user;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author david.chen
 * @date 2021/12/22 19:28
 */
@Data
@ApiModel(value = "预警价格删除请求")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class AlarmPriceDelReq {
    @ApiModelProperty(value = "币对", required = true, example = "BTC_USD")
    @NotNull
    private String symbol;
    @ApiModelProperty(value = "预警价格ID,不传时删除此symbol所有设置", example = "111")
    private Long alarmId;

    public boolean isDelAll() {
        return alarmId == null;
    }

}
