package com.google.backend.trading.model.margin.api;

import com.google.backend.trading.framework.web.RequestUnderlineToCamel;
import com.google.backend.trading.model.common.PageTimeRangeReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author trading
 * @date 2021/9/28 21:05
 */
@Data
@ApiModel(value = "历史仓位查询请求")
@RequestUnderlineToCamel
public class PositionHistorySearchReq extends PageTimeRangeReq {

	@ApiModelProperty(value = "币对", example = "BTC_USDT")
	private String symbol;
	@ApiModelProperty(value = "方向", example = "BUY")
	private String direction;

}
