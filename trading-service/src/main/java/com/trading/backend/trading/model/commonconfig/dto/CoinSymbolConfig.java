package com.google.backend.trading.model.commonconfig.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * @Author: linhuayao
 * @Date: 2021/10/4 10:58
 */
@ToString
@Getter
@Setter
@ApiModel(value = "币对配置")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CoinSymbolConfig {
	@ApiModelProperty(value = "币对")
	private String name;
	@ApiModelProperty(value = "排序优先级越小越优先")
	private int priority;
	@ApiModelProperty(value = "价格精度")
	private int precision;
	@ApiModelProperty(value = "base的默认展示level值，逗号间隔，web端")
	private String baseLevels;
	@ApiModelProperty(value = "quote的默认展示level值，逗号间隔，web端")
	private String quoteLevels;
	@ApiModelProperty(value = "web端价格展示的正则")
	private String priceRegular;
	@ApiModelProperty(value = "最小发单量")
	private BigDecimal minOrderAmount;
	@ApiModelProperty(value = "最大发单量")
	private BigDecimal maxOrderAmount;
	@ApiModelProperty(value = "FOK的最大发单量")
	private BigDecimal fokMaxOrderAmount;
	@ApiModelProperty(value = "现货是否开启")
	private boolean spotValid;
	@ApiModelProperty(value = "杠杆是否开启")
	private boolean marginValid;
	@ApiModelProperty(value = "备注信息")
	private String remark;

	public static final CoinSymbolConfig INVALID = new CoinSymbolConfig() {

		@Override
		public boolean isSpotValid() {
			return false;
		}

		@Override
		public boolean isMarginValid() {
			return false;
		}
	};
}
