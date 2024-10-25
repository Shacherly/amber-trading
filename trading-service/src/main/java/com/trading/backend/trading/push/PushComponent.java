package com.google.backend.trading.push;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.backend.trading.config.web.DateLongDeserializer;
import com.google.backend.trading.config.web.DateLongSerializer;
import com.google.backend.trading.model.buycrypt.BuyCryptRes;
import com.google.backend.trading.model.common.jackson.BigDecimalStringSerializer;
import com.google.backend.trading.model.swap.api.AipSwapOrderRes;
import com.google.backend.trading.push.usertouch.UserTouchMessage;
import com.google.backend.trading.util.AlarmLogUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author trading
 * @date 2021/10/23 15:09
 */
@Slf4j
@Component
public class PushComponent {

	@Value("${kafka.topic.ws-push}")
	private String wsPushTopic;
	@Value("${kafka.topic.usertouch-push}")
	private String userTouchTopic;
	@Value("${kafka.topic.funding-push}")
	private String fundingPushTopic;
	@Value("${kafka.topic.buy-crypt-result}")
	private String buyCryptResult;
	@Value("${kafka.topic.aip-swap-place-order-push}")
	private String aipSwapPlaceOrderPushTopic;
	@Autowired
	private Environment env;


	private static final ThreadLocal<ObjectMapper> OM = ThreadLocal.withInitial(() -> {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		SimpleModule bigDecimalModule = new SimpleModule();
		//序列化将BigDecimal转String类型
		bigDecimalModule.addSerializer(BigDecimal.class, BigDecimalStringSerializer.instance);
		bigDecimalModule.addKeySerializer(BigDecimal.class, BigDecimalStringSerializer.instance);
		//序列化将Date转Long类型
		SimpleModule dateModule = new SimpleModule();
		dateModule.addSerializer(Date.class, DateLongSerializer.instance);
		dateModule.addKeySerializer(Date.class,DateLongSerializer.instance);
		dateModule.addDeserializer(Date.class, DateLongDeserializer.instance);
		// 注册转换器
		objectMapper.registerModules(dateModule,bigDecimalModule);
		return objectMapper;
	});

	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;


	/**
	 * 推送ws消息
	 * 暂时通过指定分区来保证发送的消息有序
	 * @param message
	 */
	public void pushWsMessage(WsPushMessage<?> message) {
		String jsonMessage;
		try {
			jsonMessage = OM.get().writeValueAsString(message);
			kafkaTemplate.send(wsPushTopic, 0, wsPushTopic + "-key", jsonMessage);
		} catch (JsonProcessingException e) {
			log.error("push message json serialize fail, cause = {}", ExceptionUtils.getRootCauseMessage(e), e);
		} catch (Exception e) {
			AlarmLogUtil.alarm("push ws message fail, cause = {}", ExceptionUtils.getRootCauseMessage(e), e);
		}
	}

	/**
	 * 推送客户触达消息
	 * @param message
	 */
	@Async
	public void pushClientMessage(UserTouchMessage message) {
//		String[] activeProfiles = env.getActiveProfiles();
//		for (String activeProfile : activeProfiles) {
//			if ("prod".equals(activeProfile)) {
//				log.info("pushClientMessage disable");
//				return;
//			}
//		}
		String jsonMessage;
		try {
			jsonMessage = OM.get().writeValueAsString(message);
			kafkaTemplate.send(userTouchTopic, jsonMessage);
		} catch (JsonProcessingException e) {
			log.error("pushClientMessage json serialize fail, cause = {}", ExceptionUtils.getRootCauseMessage(e), e);
		} catch (Exception e) {
			AlarmLogUtil.alarm("push client message fail, cause = {}", ExceptionUtils.getRootCauseMessage(e), e);
		}
	}

	/**
	 * 推送资金行为 {@link FundingBehaviorEvent.Type}
	 * @param message
	 */
	@Async
	public void pushFundingEventMessage(FundingBehaviorEventMessage message) {
		String jsonMessage;
		try {
			jsonMessage = OM.get().writeValueAsString(message);
			kafkaTemplate.send(fundingPushTopic, jsonMessage);
		} catch (JsonProcessingException e) {
			log.error("pushClientMessage json serialize fail, cause = {}", ExceptionUtils.getRootCauseMessage(e), e);
			return;
		} catch (Exception e) {
			AlarmLogUtil.alarm("push funding event message fail, cause = {}", ExceptionUtils.getRootCauseMessage(e), e);
		}
	}

	public void pushBuyCryptSuccessResult(BuyCryptRes res) {
		String jsonMessage;
		try {
			jsonMessage = OM.get().writeValueAsString(res);
			kafkaTemplate.send(buyCryptResult, jsonMessage);
		} catch (JsonProcessingException e) {
			log.error("pushBuyCryptSuccessResult json serialize fail, cause = {}", ExceptionUtils.getRootCauseMessage(e), e);
			return;
		} catch (Exception e) {
			AlarmLogUtil.alarm("pushBuyCryptSuccessResult fail, cause = {}", ExceptionUtils.getRootCauseMessage(e), e);
		}
	}


	/**
	 * 推送aip终态推送
	 * 只有两个状态 COMPLETED（完成）、CANCELED（取消）——memo记录原因
	 *
	 * @param swapOrderRes
	 */
	public void pushAipSwapOrderResult(AipSwapOrderRes swapOrderRes) {
		String jsonMessage;
		try {
			jsonMessage = OM.get().writeValueAsString(swapOrderRes);
			kafkaTemplate.send(aipSwapPlaceOrderPushTopic, jsonMessage);
		} catch (JsonProcessingException e) {
			log.error("pushAipSwapOrderResult json serialize fail, cause = {}", ExceptionUtils.getRootCauseMessage(e), e);
			return;
		} catch (Exception e) {
			AlarmLogUtil.alarm("pushAipSwapOrderResult fail, cause = {}", ExceptionUtils.getRootCauseMessage(e), e);
		}
	}
}
