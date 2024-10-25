package com.google.backend.trading.model.margin.dto;

import com.google.backend.trading.model.trade.OrderError;
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
public class ExecutableInfo {
	/**
	 * 错误
	 */
	private ExecutableError error;

	/**
	 * 可执行数额
	 */
	private BigDecimal quantity;

	/**
	 * 预计占用保证金
	 */
	private BigDecimal expectUsedMargin;


	public enum ExecutableError {
		REDUCE_ONLY,
		INSUFFICIENT_MARGIN,
		EXCEED_TOTAL_POSITION_LIMIT,
		EXCEED_SYMBOL_POSITION_LIMIT,
		;


		public OrderError convert() {
			switch (this) {
				case REDUCE_ONLY:
					return OrderError.TRADING_MARGIN_ORDER_EXCEPTION_REDUCE_ONLY;
				case INSUFFICIENT_MARGIN:
					return OrderError.TRADING_MARGIN_ORDER_EXCEPTION_INSUFFICIENT_MARGIN;
				case EXCEED_TOTAL_POSITION_LIMIT:
					return OrderError.TRADING_MARGIN_ORDER_EXCEPTION_EXCEED_TOTAL_POSITION_LIMIT;
				case EXCEED_SYMBOL_POSITION_LIMIT:
					return OrderError.TRADING_MARGIN_ORDER_EXCEPTION_EXCEED_SYMBOL_POSITION_LIMIT;
			}
			return OrderError.TRADING_MARGIN_ORDER_EXCEPTION;
		}
	}
}
