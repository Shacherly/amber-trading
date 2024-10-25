package com.google.backend.trading.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.backend.asset.common.model.asset.res.TradeSpotLockRes;
import com.google.backend.asset.common.model.trade.req.TradeClearLockReq;
import com.google.backend.common.dto.base.MsgHeadDto;
import com.google.backend.common.mq.HeaderUtils;
import com.google.backend.common.web.Response;
import com.google.backend.trading.client.feign.AssetTradeClient;
import com.google.backend.trading.constant.Constants;
import com.google.backend.trading.dao.mapper.DefaultTradePositionMapper;
import com.google.backend.trading.dao.model.TradePosition;
import com.google.backend.trading.dao.model.TradePositionExample;
import com.google.backend.trading.dao.model.TradeSwapOrder;
import com.google.backend.trading.dao.model.TradeTransaction;
import com.google.backend.trading.exception.BusinessExceptionEnum;
import com.google.backend.trading.mapstruct.config.TradeUserTradeSettingMapStruct;
import com.google.backend.trading.mapstruct.margin.TradePositionMapStruct;
import com.google.backend.trading.model.common.model.riskcontrol.ReducePositionReq;
import com.google.backend.trading.model.common.model.riskcontrol.UserSettingRes;
import com.google.backend.trading.model.common.model.riskcontrol.notice.CancelOrderNotice;
import com.google.backend.trading.model.common.model.riskcontrol.notice.LiquidBalanceNotice;
import com.google.backend.trading.model.common.model.riskcontrol.notice.LiquidSpotNotice;
import com.google.backend.trading.model.common.model.riskcontrol.notice.PositionInfo;
import com.google.backend.trading.model.common.model.riskcontrol.notice.ReducePositionNotice;
import com.google.backend.trading.model.trade.TransactionType;
import com.google.backend.trading.service.MarginService;
import com.google.backend.trading.service.RiskControlService;
import com.google.backend.trading.service.SpotService;
import com.google.backend.trading.service.SwapService;
import com.google.backend.trading.util.AlarmLogUtil;
import com.google.backend.trading.util.ListUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Lazy;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 提供给风控的接口实现
 *
 * @author savion.chen
 * @date 2021/10/8 11:11
 */
@Slf4j
@Service
@Data
@ConfigurationProperties(prefix = "risk.topic")
public class RiskControlServiceImpl implements RiskControlService {

	private String reducePosition;
	private String cancelOrder;
	private String liquidSpot;
	private String liquidBalance;
	private String userSetting;
	private String positionOpen;
	private String positionChange;
	private static final ThreadLocal<ObjectMapper> OM = ThreadLocal.withInitial(() -> {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		return objectMapper;
	});

	@Lazy
	@Resource
	private SpotService spotService;
	@Lazy
	@Resource
	private MarginService marginService;
	@Resource
	private KafkaTemplate<String, String> kafkaTemplate;
	@Autowired
	private TradeUserTradeSettingMapStruct tradeSettingMapStruct;
	@Resource
	private DefaultTradePositionMapper defaultTradePositionMapper;
	@Resource
	private TradePositionMapStruct tradePositionMapStruct;
	@Resource
	private PushMsgServiceImpl pushService;
	@Autowired
	private RedissonClient redissonClient;
	@Autowired
	private SwapService swapService;
	@Autowired
	private AssetTradeClient assetTradeClient;

	@Override
	@Async
	public void cancelOrder(String uid) {
		RLock lock = redissonClient.getLock(uid + ":risk-operation:" + Constants.LOCK_CANCEL_ORDER);
		if (lock.tryLock()) {
			try {
				//取消现货订单
				int cancelNum = spotService.cancelAllNotSystemOrders(uid);
				List<TradeSwapOrder> orders = swapService.listAllActiveOrders();
				int i = 10;
				while (!orders.isEmpty() && i-- > 0) {
					Thread.sleep(2000);
					orders = swapService.listAllActiveOrders();
				}
				log.info("swap active orders = {}", orders);
				if (cancelNum == 0) {
					//兜底取消所有现货
					Map<String, BigDecimal> map = doClearSpotLock(uid);
					if (map.size() > 0) {
						AlarmLogUtil.alarm("assure execute asset clear all spot lock, data = {}", map);
					}
				}
				//通知风控
				pushOrderlyMessage(cancelOrder, new CancelOrderNotice(uid));
			} catch (InterruptedException ignored) {
				//ignored
			} finally {
				lock.unlock();
			}
		}
	}

	private Map<String, BigDecimal> doClearSpotLock(String uid) {
		TradeClearLockReq req = new TradeClearLockReq();
		TradeClearLockReq.Params params = new TradeClearLockReq.Params();
		params.setUid(uid);
		req.setReqId(UUID.randomUUID().toString());
		req.setParams(params);
		Response<List<TradeSpotLockRes>> res = assetTradeClient.doClearSpotLock(req);
		if (BusinessExceptionEnum.SUCCESS.getCode() == res.getCode()) {
			if (CollectionUtils.isNotEmpty(res.getData())) {
				return res.getData().stream().filter(v -> v.getClearSpotLocked().compareTo(BigDecimal.ZERO) != 0).collect(Collectors.toMap(TradeSpotLockRes::getCoin,
						TradeSpotLockRes::getClearSpotLocked));
			}
			return Collections.emptyMap();
		}
		return Collections.emptyMap();
	}


	@Async
	@Override
	public void reducePosition(ReducePositionReq reducePositionReq) {
		String uid = reducePositionReq.getUid();
		RLock lock = redissonClient.getLock(uid + ":risk-operation:" + Constants.LOCK_REDUCE_POSITION);
		if (lock.tryLock()) {
			try {
				//查询所有杠杆订单并取消 无需取消杠杆订单
				marginService.cancelAllNotSystemOrders(uid);
				//强制减仓
				marginService.forceClosePosition(reducePositionReq);
				//通知风控
				pushOrderlyMessage(reducePosition, new ReducePositionNotice(uid));
			} finally {
				lock.unlock();
			}
		}
	}


	@Async
	@Override
	public void liquidSpot(String uid) {
		RLock lock = redissonClient.getLock(uid + ":risk-operation:" + Constants.LOCK_LIQUID_SPOT_BALANCE);
		if (lock.tryLock()) {
			try {
				spotService.liquidSpot(uid);
				//通知风控
				pushOrderlyMessage(liquidSpot, new LiquidSpotNotice(uid));
			} finally {
				lock.unlock();
			}
		}
	}


	@Async
	@Override
	public void liquidBalance(String uid) {
		RLock lock = redissonClient.getLock(uid + ":risk-operation:" + Constants.LOCK_LIQUID_SPOT_BALANCE);
		if (lock.tryLock()) {
			try {
				//清算资产余额
				spotService.liquidBalance(uid);
				//通知风控
				pushOrderlyMessage(liquidBalance, new LiquidBalanceNotice(uid));
			} finally {
				lock.unlock();
			}
		}
	}

	@Override
	public void userSettingChangeNotice(UserSettingRes settingRes) {
		pushOrderlyMessage(userSetting, settingRes);
	}

	@Override
	public void positionNotice(TradeTransaction transaction) {
		TradePositionExample example = new TradePositionExample();
		example.createCriteria().andUuidEqualTo(transaction.getPositionId());
		List<TradePosition> tradePositions = defaultTradePositionMapper.selectByExample(example);
		if (ListUtil.isNotEmpty(tradePositions)) {
			TradePosition tradePosition = tradePositions.get(0);
			PositionInfo positionInfo = tradePositionMapStruct.tradePosition2PositionInfo(tradePosition);

			//开仓
			if (TransactionType.OPEN_POSITION == TransactionType.getByName(transaction.getType())) {
				log.info("positionOpen positionInfo:{}", positionInfo);
				pushOrderlyMessage(positionOpen, positionInfo);
			} else {//仓位变化
				log.info("positionChange positionInfo:{}", positionInfo);
				pushOrderlyMessage(positionChange, positionInfo);
			}
		}
	}

	@Override
	public void positionNoticeWithVersion(TradeTransaction transaction, MsgHeadDto dto) {
		TradePositionExample example = new TradePositionExample();
		example.createCriteria().andUuidEqualTo(transaction.getPositionId());
		List<TradePosition> tradePositions = defaultTradePositionMapper.selectByExample(example);
		if (ListUtil.isNotEmpty(tradePositions)) {
			TradePosition tradePosition = tradePositions.get(0);
			PositionInfo positionInfo = tradePositionMapStruct.tradePosition2PositionInfo(tradePosition);
			TransactionType transactionType = TransactionType.getByName(transaction.getType());
			log.info("position notice positionInfo = {} transaction = {}", positionInfo, transaction);
			//开仓
			if (TransactionType.OPEN_POSITION == transactionType) {
				pushOrderlyMessage(positionOpen, positionInfo, dto);
			} else if (TransactionType.CLOSE_POSITION == transactionType || TransactionType.REDUCE_POSITION == transactionType || TransactionType.SETTLE_POSITION == transactionType) {
				//减仓/平仓/交割产生的仓位变化需要携带版本进行消息聚合
				pushOrderlyMessage(positionChange, positionInfo, dto);
			} else {//一般仓位变化
				pushOrderlyMessage(positionChange, positionInfo, dto);
			}
		}
	}

	/**
	 * 暂时通过指定分区来保证发送的消息有序
	 *
	 * @param topic
	 * @param o
	 */
	private void pushOrderlyMessage(String topic, Object o) {
		try {
			kafkaTemplate.send(topic, 0, topic + "-key", OM.get().writeValueAsString(o));
		} catch (JsonProcessingException e) {
			log.error("json parse fail, topic = {}, data = {}, cause = {}", topic, o, ExceptionUtils.getRootCauseMessage(e), e);
		}
	}

	private void pushOrderlyMessage(String topic, Object o, MsgHeadDto dto) {
		try {
			Header header = HeaderUtils.toKafkaHeader(dto);
			ProducerRecord<String, String> record = new ProducerRecord<>(topic, 0, topic + "-key",
					OM.get().writeValueAsString(o), Collections.singletonList(header));
			kafkaTemplate.send(record);
		} catch (JsonProcessingException e) {
			log.error("json parse fail, topic = {}, data = {}, cause = {}", topic, o, ExceptionUtils.getRootCauseMessage(e), e);
		}
	}
}
