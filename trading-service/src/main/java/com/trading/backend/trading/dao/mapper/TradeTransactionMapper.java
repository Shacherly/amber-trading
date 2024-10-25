package com.google.backend.trading.dao.mapper;

/**
 * @author trading
 * @date 2022/2/7 14:44
 */
public interface TradeTransactionMapper {

	/**
	 * 查询交易操作的 sequence
	 *
	 * @return
	 */
	Long selectTransactionSeqLastValue();
}
