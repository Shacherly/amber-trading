package com.google.backend.trading.model.margin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * @author trading
 * @date 2021/10/20 11:59
 */
@Getter
@ToString
@AllArgsConstructor
public class SettleAsset {

	/**
	 * 可用余额
	 */
	private BigDecimal available;

	/**
	 * 可交割数量
	 */
	private BigDecimal settleAvailable;
}
