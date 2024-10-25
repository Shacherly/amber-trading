package com.google.backend.trading.model.margin.api;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author trading
 * @date 2021/9/28 21:49
 */
@Data
@ApiModel(value = "仓位记录vo")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PositionRecordVo {

	@ApiModelProperty(value = "操作类型: 杠杆（ OPEN_POSITION 开仓 ADD_POSITION 加仓 CLOSE_POSITION 平仓 REDUCE_POSITION 减仓 SETTLE_POSITION 交割 FORCE_CLOSE 强减发生（包括强平））", example = "OPEN")
	private String type;

	@ApiModelProperty(value = "数量", example = "100")
	private BigDecimal quantity;

	@ApiModelProperty(value = "价格", example = "20000")
	private BigDecimal price;

	@ApiModelProperty(value = "实现盈亏", example = "10.11")
	private BigDecimal pnl;

	@ApiModelProperty(value = "手续费", example = "10.11")
	private BigDecimal fee;

	@ApiModelProperty(value = "时间", example = "1632835745000")
	private Date ctime;
}
