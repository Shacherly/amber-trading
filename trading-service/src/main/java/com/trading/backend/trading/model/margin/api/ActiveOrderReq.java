package com.google.backend.trading.model.margin.api;

import com.google.backend.trading.framework.web.RequestUnderlineToCamel;
import com.google.backend.trading.model.common.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * @author trading
 * @date 2021/9/28 21:03
 */
@Data
@ApiModel(value = "杠杆当前委托列表请求")
@RequestUnderlineToCamel
public class ActiveOrderReq extends PageReq {

	@NotBlank
	@ApiModelProperty(value = "币对", example = "BTC_USD")
	private String symbol;

	@NotNull
	@ApiModelProperty(name="only_current",value = "是否仅展示当前币对", example = "true")
	private Boolean onlyCurrent;
}
