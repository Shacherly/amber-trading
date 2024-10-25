package com.google.backend.trading.push;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.google.backend.trading.dao.model.TradeNegativeBalanceFundingCost;
import com.google.backend.trading.dao.model.TradePositionFundingCost;
import com.google.backend.trading.dao.model.TradeSwapOrder;
import com.google.backend.trading.dao.model.TradeTransaction;
import com.google.backend.trading.model.trade.Direction;
import com.google.backend.trading.util.CommonUtils;
import lombok.Data;
import org.springframework.data.util.Pair;

import java.math.BigDecimal;

/**
 * @author trading
 * @date 2021/10/29 11:11
 */
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class FundingBehaviorEventMessage {

	private FundingBehaviorEvent.Type type;

	private FundingBehaviorEvent.BaseEvent value;

	public static FundingBehaviorEventMessage buildSpotValue(TradeTransaction transaction) {
		String symbol = transaction.getSymbol();
		Pair<String, String> coinPair = CommonUtils.coinPair(symbol);
		String buyCoin;
		String sellCoin;
		BigDecimal buySize;
		BigDecimal sellSize;
		if (Direction.isBuy(transaction.getDirection())) {
			buyCoin = coinPair.getFirst();
			sellCoin = coinPair.getSecond();
			buySize = transaction.getBaseQuantity();
			sellSize = transaction.getQuoteQuantity();
		} else {
			buyCoin = coinPair.getSecond();
			sellCoin = coinPair.getFirst();
			buySize = transaction.getQuoteQuantity();
			sellSize = transaction.getBaseQuantity();
		}

		FundingBehaviorEvent.Spot spot = new FundingBehaviorEvent.Spot();
		spot.setUid(transaction.getUid());
		spot.setTradeId(transaction.getUuid());
		spot.setSymbol(symbol);
		spot.setBuyCoin(buyCoin);
		spot.setBuySize(buySize);
		spot.setSellCoin(sellCoin);
		spot.setSellSize(sellSize);
		spot.setFeeCoin(buyCoin);
		spot.setFeeSize(transaction.getFee());
		spot.setTime(transaction.getCtime().getTime());

		FundingBehaviorEventMessage message = new FundingBehaviorEventMessage();
		message.setType(FundingBehaviorEvent.Type.SPOT);
		message.setValue(spot);
		return message;
	}


	public static FundingBehaviorEventMessage buildSwapValue(TradeSwapOrder order, TradeTransaction trade) {
		FundingBehaviorEvent.Swap swap = new FundingBehaviorEvent.Swap();
		swap.setUid(order.getUid());
		swap.setTradeId(trade.getUuid());
		swap.setSymbol(trade.getSymbol());
		swap.setBuyCoin(order.getToCoin());
		swap.setSellCoin(order.getFromCoin());
		swap.setBuySize(order.getToQuantity());
		swap.setSellSize(order.getFromQuantity());
		swap.setFeeCoin(order.getFeeCoin());
		swap.setFeeSize(order.getFee());
		swap.setTime(trade.getCtime().getTime());

		FundingBehaviorEventMessage message = new FundingBehaviorEventMessage();
		message.setType(FundingBehaviorEvent.Type.SWAP);
		message.setValue(swap);
		return message;
	}

	public static FundingBehaviorEventMessage buildMarginValue(TradeTransaction trade) {
		FundingBehaviorEvent.Margin margin = new FundingBehaviorEvent.Margin();
		margin.setUid(trade.getUid());
		margin.setTradeId(trade.getUuid());
		margin.setSymbol(trade.getSymbol());
		margin.setDirection(trade.getDirection());
		margin.setSize(trade.getBaseQuantity());
		margin.setFeeCoin(trade.getFeeCoin());
		margin.setFeeSize(trade.getFee());
		margin.setTime(trade.getCtime().getTime());

		FundingBehaviorEventMessage message = new FundingBehaviorEventMessage();
		message.setType(FundingBehaviorEvent.Type.MARGIN);
		message.setValue(margin);
		return message;
	}

	public static FundingBehaviorEventMessage buildMarginCommission(TradePositionFundingCost funding) {
		FundingBehaviorEvent.MarginCommission commission = new FundingBehaviorEvent.MarginCommission();
		commission.setUid(funding.getUid());
		commission.setUuid(funding.getUuid());
		commission.setCoin(funding.getCoin());
		commission.setAmount(funding.getFundingCost());
		commission.setRound(funding.getCtime().getTime());
		commission.setTime(funding.getMtime().getTime());

		FundingBehaviorEventMessage message = new FundingBehaviorEventMessage();
		message.setType(FundingBehaviorEvent.Type.MARGIN_COMMISSION);
		message.setValue(commission);
		return message;
	}

	public static FundingBehaviorEventMessage buildNegativeBalance(TradeNegativeBalanceFundingCost funding) {
		FundingBehaviorEvent.NegativeBalanceCommission commission = new FundingBehaviorEvent.NegativeBalanceCommission();
		commission.setUid(funding.getUid());
		commission.setUuid(funding.getUuid());
		commission.setCoin(funding.getCoin());
		commission.setAmount(funding.getFundingCost());
		Long nowTime = CommonUtils.getNowTime().getTime();
		commission.setRound(nowTime);
		commission.setTime(nowTime);

		FundingBehaviorEventMessage message = new FundingBehaviorEventMessage();
		message.setType(FundingBehaviorEvent.Type.NEGATIVE_BALANCE_COMMISSION);
		message.setValue(commission);
		return message;
	}


}
