package com.google.backend.trading.component;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.backend.trading.constant.Constants;
import com.google.backend.trading.model.commonconfig.dto.CoinSymbolConfig;
import com.google.backend.trading.model.trade.Direction;
import com.google.backend.trading.util.AlarmLogUtil;
import com.google.backend.trading.util.DateUtil;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 币对业务对象
 *
 * @author trading
 * @date 2021/9/29 14:12
 */
@Slf4j
@ToString(exclude = {"bids", "bids"})
public class SymbolDomain {

	public static final long PRICE_DELAY_MS_THRESHOLD = 60 * 1000;

	public static final BigDecimal PRICE_DIFF_BUFFER = new BigDecimal("0.05");

	/**
	 * key = symbol (eg: BTC_USD), value = SymbolDomain
	 */
	public static ConcurrentHashMap<String, SymbolDomain> CACHE = new ConcurrentHashMap<>();

	private static final BigDecimal TWO = new BigDecimal("2");

	/**
	 * symbol name: BTC_USD
	 */
	private final String symbol;

	/**
	 * 指数价格
	 */
	private volatile BigDecimal indexPrice;

	/**
	 * 指数价格时间戳
	 */
	private volatile long indexPriceTs;


	/**
	 * 分档价格原始数据 bids:[[sell_price,...]], asks:[[buy_price,...]]
	 */
	private volatile JsonNode bids;
	private volatile JsonNode asks;
	/**
	 * 分档价格时间戳
	 */
	private volatile long orderBookTs;
	/**
	 * 原始时间戳，暂做保留
	 */
	private volatile long rawTs;
	private volatile long pdtTs;

	private volatile long orderBookReceiveTs = 0;

	private volatile long indexReceiveTs = 0;

	/**
	 * 买一价
	 */
	private volatile BigDecimal buyPrice;
	/**
	 * 卖一价
	 */
	private volatile BigDecimal sellPrice;
	/**
	 * 币对配置
	 */
	private volatile CoinSymbolConfig coinSymbolConfig;

	private volatile BigDecimal midPrice;

	@NonNull
	public CoinSymbolConfig getCoinSymbolConfig() {
		return coinSymbolConfig;
	}


	public static void initCache(String symbol) {
		CACHE.computeIfAbsent(symbol, SymbolDomain::new);
	}

	public static SymbolDomain removeCacheBySymbol(String symbol) {
		return CACHE.remove(symbol);
	}


	private SymbolDomain(String symbol) {
		this.symbol = symbol;
	}

	public void init(BigDecimal ask, BigDecimal bid, BigDecimal indexPrice, CoinSymbolConfig coinSymbolConfig) {
		this.buyPrice = ask;
		this.sellPrice = bid;
		this.midPrice = (buyPrice.add(sellPrice)).divide(TWO, Constants.PRICE_PRECISION, RoundingMode.DOWN);
		if (symbol.contains(Constants.IDK_COIN) && (null == indexPrice || indexPrice.compareTo(BigDecimal.ZERO) == 0)) {
			indexPrice = this.midPrice;
		}
		this.indexPrice = indexPrice;
		this.indexPriceTs = System.currentTimeMillis();
		if (null == coinSymbolConfig) {
			this.coinSymbolConfig = CoinSymbolConfig.INVALID;
		} else {
			this.coinSymbolConfig = coinSymbolConfig;
		}
	}


	public void updateOrderBook(JsonNode bids, JsonNode asks, long rawTs, long pdtTs, long crexTs, long receiveTs) {
		this.bids = bids;
		this.asks = asks;

		this.buyPrice = asks.get(0).get(0).decimalValue();
		this.sellPrice = bids.get(0).get(0).decimalValue();
		this.midPrice = (buyPrice.add(sellPrice)).divide(TWO, Constants.PRICE_PRECISION, RoundingMode.DOWN);
		this.rawTs = rawTs;
		this.pdtTs = pdtTs;
		this.orderBookTs = crexTs;
		this.orderBookReceiveTs = receiveTs;
	}

	public void updateIndexPrice(BigDecimal indexPrice, long indexPriceTs, long receiveTs) {
		this.indexPrice = indexPrice;
		this.indexPriceTs = indexPriceTs;
		this.indexReceiveTs = receiveTs;
	}

	public void updateCoinSymbolConfig(CoinSymbolConfig coinSymbolConfig) {
		this.coinSymbolConfig = coinSymbolConfig;
	}


	/**
	 * 按照数量询价
	 *
	 * @param needQuantity
	 * @param direction
	 * @return
	 */
	@NonNull
	public BigDecimal priceByQuantity(BigDecimal needQuantity, Direction direction) {
		BigDecimal gradePrice = gradePrice(needQuantity, direction);
		//分档价格不存在时获取指数价格
		if (null == gradePrice) {
			log.info("gradePrice is null, symbol = {}", symbol);
			if (null == indexPrice) {
				log.error("indexPrice is null, symbol = {}", symbol);
				return BigDecimal.ZERO;
			}
			return indexPrice;
		}
		return gradePrice;
	}

	/**
	 * 获取分档价格，如果分档价格未获取到则返回 null
	 *
	 * @return
	 */
	@Nullable
	private BigDecimal gradePrice(BigDecimal needQuantity, Direction direction) {
		if (null == asks || null == bids) {
			return null;
		}
		JsonNode node;
		if (direction == Direction.BUY) {
			node = asks;
		} else {
			node = bids;
		}

		BigDecimal gradePrice = null;
		BigDecimal tempPrice;
		BigDecimal tempQuantity;
		BigDecimal amount = BigDecimal.ZERO;
		BigDecimal calQuantity = BigDecimal.ZERO;
		for (int i = 0, len = node.size(); i < len; i++) {
			JsonNode book = node.get(i);
			tempPrice = book.get(0).decimalValue();
			tempQuantity = book.get(1).decimalValue();
			calQuantity = calQuantity.add(tempQuantity);
			amount = amount.add(tempQuantity.multiply(tempPrice));
			if (calQuantity.compareTo(needQuantity) >= 0) {
				gradePrice = amount.divide(calQuantity, Constants.PRICE_PRECISION, RoundingMode.HALF_DOWN);
				break;
			}
		}
		return gradePrice;
	}

	/**
	 * 最优价格
	 *
	 * @param direction
	 * @return
	 */
	public BigDecimal price(Direction direction) {
		if (direction == Direction.BUY) {
			return buyPrice;
		} else {
			return sellPrice;
		}
	}

	/**
	 * 最优价格
	 *
	 * @param direction
	 * @return
	 */
	public BigDecimal price(String direction) {
		return price(Direction.getByName(direction));
	}

	/**
	 * 对手最优价格
	 *
	 * @param direction
	 * @return
	 */
	public BigDecimal rivalPrice(String direction) {
		return rivalPrice(Direction.getByName(direction));
	}

	/**
	 * 对手最优价格
	 *
	 * @param direction
	 * @return
	 */
	public BigDecimal rivalPrice(Direction direction) {
		if (direction == Direction.BUY) {
			return sellPrice;
		} else {
			return buyPrice;
		}
	}

	public BigDecimal midPrice() {
		try {
			return Objects.requireNonNull(midPrice);
		} catch (NullPointerException e) {
			log.error("price err, symbol = {}", symbol);
			throw e;
		}
	}

	/**
	 * 获取价格异常时返回零（谨慎使用）
	 *
	 * @return
	 */
	public BigDecimal midPriceOrZero() {
		try {
			return midPrice();
		} catch (NullPointerException e) {
			log.error("price err, symbol = {}", symbol);
			return BigDecimal.ZERO;
		}
	}

	public BigDecimal getIndexPrice() {
		return indexPrice;
	}

	public BigDecimal getBuyPrice() {
		return buyPrice;
	}

	public BigDecimal getSellPrice() {
		return sellPrice;
	}

	public String getSymbol() {
		return symbol;
	}

	/**
	 * 在用户下单时进行校验，false 需要阻断用户的下单，提示 ”该交易市场正在维护中“
	 *
	 * @return 是否可进行下单
	 */
	public boolean checkPlaceOrderPriceStatus() {
		if (symbol.contains(Constants.IDK_COIN)) {
			return true;
		}
		long now = System.currentTimeMillis();
		BigDecimal midPrice = midPrice();
		boolean pdtTimeout = now - pdtTs > PRICE_DELAY_MS_THRESHOLD;
		boolean indexTimeout = now - indexPriceTs > PRICE_DELAY_MS_THRESHOLD;
		boolean pdtPriceOverBuffer =
				midPrice.subtract(indexPrice).divide(indexPrice, Constants.DEFAULT_PRECISION, RoundingMode.UP).abs().compareTo(PRICE_DIFF_BUFFER) > 0;
		boolean canPlaceOrder = true;
		if (pdtTimeout) {
			AlarmLogUtil.alarm("{} PDT Price not updated for more than 1 minute, pdtTime = {}, receiveTime = {}", symbol,
					DateUtil.formatSimple(new Date(pdtTs)), DateUtil.formatSimple(new Date(orderBookReceiveTs)));
			canPlaceOrder = false;
		}
		if (indexTimeout) {
			AlarmLogUtil.alarm("{} index Price not updated for more than 1 minute, indexPriceTime = {}, receiveTime = {}", symbol,
					DateUtil.formatSimple(new Date(indexPriceTs)), DateUtil.formatSimple(new Date(indexReceiveTs)));
		}
		if (pdtPriceOverBuffer) {
			AlarmLogUtil.alarm("{} PDT Price not match the Index Price, mid price = {}, index price = {}", symbol, midPrice, indexPrice);
		}
		return canPlaceOrder;
	}

	@NonNull
	public static SymbolDomain nonNullGet(String symbol) {
		SymbolDomain symbolDomain = SymbolDomain.CACHE.get(symbol);
		return Objects.requireNonNull(symbolDomain, String.format("SymbolDomain not found in cache, symbol = %s", symbol));
	}

	@Nullable
	public static SymbolDomain nullableGet(String symbol) {
		return SymbolDomain.CACHE.get(symbol);
	}

	/**
	 * 校验币种合法
	 *
	 * @param symbol
	 */
	public static void checkoutSymbol(String symbol) {
		SymbolDomain domain = SymbolDomain.CACHE.get(symbol);
		if (null == domain) {
			throw new RuntimeException(String.format("not support symbol, symbol = %s", symbol));
		}
	}



	public String toPriceInfo() {
		return "price info {" +
				"symbol='" + symbol + '\'' +
				", indexPrice=" + indexPrice +
				", indexPriceTs=" + indexPriceTs +
				", buyPrice=" + buyPrice +
				", sellPrice=" + sellPrice +
				", midPrice=" + midPrice +
				", rawTs=" + rawTs +
				", pdtTs=" + pdtTs +
				'}';
	}
}
