package com.google.backend.trading.push;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author trading
 * @date 2021/7/27 20:12
 */
@Data
public class FundingBehaviorEvent {

	public enum Type {
		/**
		 * SWAP 兑换（完全成交产生）,SPOT（完全或部分成交产生） ,MARGIN（开仓，加仓，减仓产生），
		 * MARGIN_COMMISSION（仓位持有期间发生资金费率时产生）NEGATIVE_BALANCE_COMMISSION(负资产产生)
		 */
		SWAP("SWAP"),
		SPOT("SPOT"),
		MARGIN("MARGIN"),
		MARGIN_COMMISSION("MARGIN_COMMISSION"),
        NEGATIVE_BALANCE_COMMISSION("NEGATIVE_BALANCE_COMMISSION"),
		;
		String value;

		Type(String value) {
			this.value = value;
		}

		@JsonValue
		public String getValue() {
			return value;
		}
	}

	public abstract static class BaseEvent {

	}

	@Data
	@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
	public static class Swap extends BaseEvent {
		
		private String uid;
		
		private String symbol;
		
		private String sellCoin;
		
		private String buyCoin;
		
		private String feeCoin;
		
		private Long time;
		
		private String tradeId;

		private BigDecimal buySize;
		
		private BigDecimal feeSize;
		
		private BigDecimal sellSize;

	}

	@Data
	@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
	public static class Spot extends BaseEvent {

		private String uid;
		
		private String symbol;
		
		private String tradeId;
		
		private String sellCoin;
		
		private String buyCoin;
		
		private String feeCoin;
		
		private Long time;
		
		private BigDecimal buySize;
		
		private BigDecimal feeSize;
		
		private BigDecimal sellSize;
	}

	@Data
	@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
	public static class Margin extends BaseEvent {

		private String uid;

		private String symbol;

		private String tradeId;

		private BigDecimal size;

		private String feeCoin;

		private Long time;

		private BigDecimal feeSize;

		private String direction;
	}

	@Data
	@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
	public static class MarginCommission extends BaseEvent {

        private String uuid;

		private String uid;

        private Long round;

        private BigDecimal amount;
		
		private Long time;

		private String coin;
	}

    @Data
	@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class NegativeBalanceCommission extends BaseEvent {

        private String uuid;

        private String uid;

        private Long round;

        private BigDecimal amount;

        private Long time;

        private String coin;
    }
}
