package com.google.backend.trading.model.buycrypt;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.google.backend.trading.dao.model.TradeSpotOrder;
import com.google.backend.trading.util.CommonUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.util.Pair;

import java.math.BigDecimal;

/**
 * @author trading
 * @date 2021/12/27 17:44
 */
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class BuyCryptRes {

	@ApiModelProperty(value = "用户id")
	private String uid;

	@ApiModelProperty(value = "订单id", notes = "唯一，用于幂等")
	private String orderId;

	@ApiModelProperty(value = "成交数量")
	private BigDecimal filled;

	@ApiModelProperty(value = "支付币种")
	private String fromCoin;

	@ApiModelProperty(value = "获得币种")
	private String toCoin;

	@ApiModelProperty(value = "成交均价")
	private BigDecimal filledPrice;

	public static BuyCryptRes from(TradeSpotOrder order) {
		BuyCryptRes res = new BuyCryptRes();
		res.setUid(order.getUid());
		res.setOrderId(order.getUuid());
		res.setFilled(order.getAmountFilled());
		Pair<String, String> coinPair = CommonUtils.coinPair(order.getSymbol());
		String baseCoin = coinPair.getFirst();
		String quoteCoin = coinPair.getSecond();
		res.setFromCoin(quoteCoin);
		res.setToCoin(baseCoin);
		res.setFilledPrice(order.getFilledPrice());
		return res;
	}

}
