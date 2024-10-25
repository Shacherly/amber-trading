package com.google.backend.trading.model.margin.api;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author jiayi.zhang
 * @date 2021/9/28
 */
@ApiModel(value = "杠杆订单修改记录信息")
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class MarginOrderModificationVo {
    @ApiModelProperty(value = "委托数量", example = "100")
    private BigDecimal quantity;
    @ApiModelProperty(value = "委托价格", notes = "LIMIT单", example = "50000")
    private BigDecimal price;
    @ApiModelProperty(value = "触发价格", notes = "STOP单", example = "<40000")
    private BigDecimal triggerPrice;
    @ApiModelProperty(value = "大于/小于", example = "<")
    private String triggerCompare;
    @ApiModelProperty(value = "订单状态", example = "true")
    private String currentStatus;
    @ApiModelProperty(value = "时间", notes = "13位毫秒时间戳", example = "1632822186544")
    private Date ctime;
    @ApiModelProperty(value = "提示信息", example = "1632835745000")
    private String notes;
}
