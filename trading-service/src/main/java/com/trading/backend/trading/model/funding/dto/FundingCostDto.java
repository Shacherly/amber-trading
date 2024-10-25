package com.google.backend.trading.model.funding.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author trading
 * @date 2021/11/9 21:20
 */
@Data
@AllArgsConstructor
public class FundingCostDto {

	private BigDecimal fundingCost;

	private BigDecimal settlePrice;

	private BigDecimal lend;

	private BigDecimal borrow;
}
