package com.google.backend.trading.model.asset.api;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author trading
 * @date 2021/11/3 20:08
 */
@Data
@ApiModel(value = "资金unpnl响应数据")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class GetUnpnlRes {

	private List<UnpnlData> list;

	@Data
	@AllArgsConstructor
	public static class UnpnlData {

		private String uid;

		private BigDecimal unpnl;
	}

}
