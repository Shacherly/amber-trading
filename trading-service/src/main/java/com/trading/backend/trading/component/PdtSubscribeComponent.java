package com.google.backend.trading.component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.backend.trading.config.pdt.PdtProperties;
import com.google.backend.trading.util.AlarmLogUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.redisson.Redisson;
import org.redisson.api.RPatternTopic;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.Optional;

/**
 * @author trading
 * @date 2021/12/22 20:45
 */
@Slf4j
@Component
public class PdtSubscribeComponent {

	private static final String REDIS_PROTOCOL_PREFIX = "redis://";
	private static final String REDISS_PROTOCOL_PREFIX = "rediss://";

	public static final String REDIS_CHANNEL_ORDERBOOK_PREFIX = "aps:crex:orderbook";
	public static final String REDIS_CHANNEL_INDEX_DATA = "aps:crex:index-price";
	public static final String REDIS_CHANNEL_FUNDING_RATE = "aps:crex:funding-rate";

	private final ObjectMapper objectMapper;

	private final RedissonClient redissonClient;

	private static final long MAX_RECEIVE_DURATION = Duration.ofSeconds(5).toMillis();


	private static final String ASKS = "asks";
	private static final String BIDS = "bids";
	private static final String SYMBOL = "symbol";
	// 数据原始时间戳
	private static final String RAW_TS = "rawTs";
	// PDT转发时间戳
	private static final String PDT_TS = "pdtTs";
	// crex转发时间戳
	private static final String CREX_TS = "crexTs";
	// 通用时间戳
	private static final String COMMON_TS = "ts";

	private static final String PDT_LEND = "lend";
	private static final String PDT_BORROW = "borrow";

	public PdtSubscribeComponent(ObjectMapper objectMapper, PdtProperties properties) {
		this.objectMapper = objectMapper;
		PdtProperties.RedisConfig redisConfig = properties.getRedis();
		Config config = new Config();
		config.setCodec(StringCodec.INSTANCE);
		SingleServerConfig singleServerConfig = config.useSingleServer();
		String prefix = redisConfig.isSsl()? REDISS_PROTOCOL_PREFIX : REDIS_PROTOCOL_PREFIX;
		singleServerConfig.setAddress(prefix + redisConfig.getHost() + ":" + redisConfig.getPort());
		singleServerConfig.setPassword(redisConfig.getPassword());
		singleServerConfig.setDatabase(redisConfig.getDatabase());
		singleServerConfig.setConnectTimeout(redisConfig.getTimeout());
		singleServerConfig.setSslEnableEndpointIdentification(redisConfig.isSsl());
		this.redissonClient = Redisson.create(config);

	}


	@PostConstruct
	public void init() {
		RPatternTopic orderBookTopic = redissonClient.getPatternTopic(REDIS_CHANNEL_ORDERBOOK_PREFIX + ":*");
		RTopic indexTopic = redissonClient.getTopic(REDIS_CHANNEL_INDEX_DATA);
		RTopic fundingRateTopic = redissonClient.getTopic(REDIS_CHANNEL_FUNDING_RATE);


		orderBookTopic.addListener(String.class, (pattern, channel, msg) -> {
			try {
				Optional<JsonNode> optionalJsonNode = convert(msg);
				if (!optionalJsonNode.isPresent()) {
					return;
				}
				JsonNode node = optionalJsonNode.get();
				JsonNode data = node.get(SYMBOL);
				if (null != data && !data.isNull()) {
					String symbol = data.asText();
					SymbolDomain symbolDomain = SymbolDomain.nullableGet(symbol);
					if (null != symbolDomain) {
						long rawTs = node.get(RAW_TS).asLong();
						long pdtTs = node.get(PDT_TS).asLong();
						long crexTs = node.get(CREX_TS).asLong();
						long receiveTs = System.currentTimeMillis();
						long duration = receiveTs - crexTs;
						if (duration > MAX_RECEIVE_DURATION) {
							AlarmLogUtil.alarm("receive order book price , duration more than {} ms, duration is = {} ms", MAX_RECEIVE_DURATION,
									duration);
						}
						symbolDomain.updateOrderBook(node.get(BIDS), node.get(ASKS), rawTs, pdtTs, crexTs, receiveTs);
					}
				}
			} catch (Exception e) {
				AlarmLogUtil.alarm("fundingRateTopic err, cause = {}", ExceptionUtils.getRootCauseMessage(e), e);
			}
		});

		indexTopic.addListener(String.class, (channel, msg) -> {
			try {
				Optional<JsonNode> optionalJsonNode = convert(msg);
				if (!optionalJsonNode.isPresent()) {
					return;
				}
				JsonNode node = optionalJsonNode.get();
				if (log.isDebugEnabled()) {
					log.debug("index price = {}", node);
				}
				long ts = node.get(COMMON_TS).asLong();
				SymbolDomain.CACHE.forEach((symbol, symbolDomain) -> {
					JsonNode data = node.get(symbol);
					long receiveTs = System.currentTimeMillis();
					if (null != data && !data.isNull()) {
						symbolDomain.updateIndexPrice(data.decimalValue(), ts, receiveTs);
					}
				});
			} catch (Exception e) {
				AlarmLogUtil.alarm("fundingRateTopic err, cause = {}", ExceptionUtils.getRootCauseMessage(e), e);
			}
		});

		fundingRateTopic.addListener(String.class, (channel, msg) -> {
			try {
				Optional<JsonNode> optionalJsonNode = convert(msg);
				if (!optionalJsonNode.isPresent()) {
					return;
				}
				JsonNode node = optionalJsonNode.get();
				if (log.isDebugEnabled()) {
					log.debug("funding rate = {}", node);
				}
				long ts = node.get(COMMON_TS).asLong();
				CoinDomain.CACHE.forEach((coin, coinDomain) -> {
					JsonNode data = node.get(coin);
					if (null != data && !data.isNull()) {
						//channel 数据是以pdt为视角的lend borrow，应用侧使用和存储使用用户视角
						coinDomain.updateFundingRate(data.get(PDT_BORROW).decimalValue(), data.get(PDT_LEND).decimalValue(), ts);
					}
				});
			} catch (Exception e) {
				AlarmLogUtil.alarm("fundingRateTopic err, cause = {}", ExceptionUtils.getRootCauseMessage(e), e);
			}
		});
	}

	public Optional<JsonNode> convert(String msg) {
		JsonNode node = null;
		try {
			node = objectMapper.readTree(msg);
		} catch (JsonProcessingException e) {
			log.error("parse err, json data = {}", msg);
		}
		if (log.isDebugEnabled()) {
			log.debug("order book = {}", msg);
		}
		return Optional.ofNullable(node);
	}
}
