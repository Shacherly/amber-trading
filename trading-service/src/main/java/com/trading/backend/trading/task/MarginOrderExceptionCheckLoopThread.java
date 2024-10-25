package com.google.backend.trading.task;

import com.google.backend.trading.dao.mapper.DefaultTradeMarginOrderMapper;
import com.google.backend.trading.dao.model.TradeMarginOrder;
import com.google.backend.trading.dao.model.TradeMarginOrderExample;
import com.google.backend.trading.dao.model.TradeUserTradeSetting;
import com.google.backend.trading.model.margin.dto.ExecutableInfo;
import com.google.backend.trading.model.trade.OrderStatus;
import com.google.backend.trading.model.trade.OrderType;
import com.google.backend.trading.model.trade.SourceType;
import com.google.backend.trading.model.trade.TradeStrategy;
import com.google.backend.trading.service.MarginService;
import com.google.backend.trading.service.UserTradeSettingService;
import com.google.backend.trading.transaction.MarginTransaction;
import com.google.backend.trading.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 杠杆订单执行LOOP
 *
 * @author jiayi.zhang
 * @date 2021/10/21 17:01
 */
@Slf4j
@Component
public class MarginOrderExceptionCheckLoopThread extends AbstractLoopThread {
	@Resource
	private MarginService marginService;
	@Resource
	private UserTradeSettingService userTradeSettingService;
	@Resource
	private MarginTransaction marginTransaction;
	@Resource
	private DefaultTradeMarginOrderMapper defaultTradeMarginOrderMapper;

	public MarginOrderExceptionCheckLoopThread() {
		super(Duration.ofMinutes(1).plus(Duration.ofSeconds(10)).toMillis());
	}

	@Override
	@Trace(operationName = "MarginOrderExceptionCheckLoopThread")
	public void handle() throws InterruptedException {
		TradeMarginOrderExample example = new TradeMarginOrderExample();
		TradeMarginOrderExample.Criteria criteria = example.createCriteria();
		criteria.andStatusEqualTo(OrderStatus.EXCEPTION.getName());
		List<TradeMarginOrder> orders = defaultTradeMarginOrderMapper.selectByExample(example);
		Map<String, Map<String, List<TradeMarginOrder>>> orderMap = new HashMap<>();
		log.info("loop handler handle exception margin order num = {}", orders.size());
		// todo: order排序
		for (TradeMarginOrder order : orders) {
			if (!CommonUtils.isSyncOrder(OrderType.getByName(order.getType()), TradeStrategy.getByName(order.getStrategy()), SourceType.getByName(order.getSource()))) {
				log.debug("MarginOrderExceptionCheckLoop|l1| orderId:{}, order:{}", order.getUuid(),order);
				if (!orderMap.containsKey(order.getUid())) {
					orderMap.put(order.getUid(), new HashMap<>());
				}
				Map<String, List<TradeMarginOrder>> userOrders = orderMap.get(order.getUid());
				if (!userOrders.containsKey(order.getSymbol())) {
					userOrders.put(order.getSymbol(), new ArrayList<>());
				}
				List<TradeMarginOrder> userSymbolOrders = userOrders.get(order.getSymbol());
				userSymbolOrders.add(order);
			}
		}
		for (Map.Entry<String, Map<String, List<TradeMarginOrder>>> entry : orderMap.entrySet()) {
			log.debug("MarginOrderExceptionCheckLoop|l2| entry:{}", entry);
			try {
				String uid = entry.getKey();
				Map<String, List<TradeMarginOrder>> userOrders = entry.getValue();
				TradeUserTradeSetting userTradeSetting = userTradeSettingService.queryTradeSettingByUid(entry.getKey());
				for (Map.Entry<String, List<TradeMarginOrder>> userEntry : userOrders.entrySet()) {
					String symbol = entry.getKey();
					List<TradeMarginOrder> userSymbolOrders = userEntry.getValue();
					HashMap<Long, ExecutableInfo> checkMarginResult = marginService.checkExceptionOrderExecutable(uid, symbol, userSymbolOrders, userTradeSetting);
					for (TradeMarginOrder order: userSymbolOrders) {
						log.debug("MarginOrderExceptionCheckLoop|l3| order:{}",order);
						ExecutableInfo result = checkMarginResult.get(order.getId());
						if (result.getError() == null) {
							order.setStatus(OrderStatus.EXECUTING.getName());
							order.setError("");
							marginTransaction.updateOrderTransactional(order);
						}
					}
				}
			} catch (Exception e) {
				log.error("handle err, cause = {}", ExceptionUtils.getRootCauseMessage(e), e);
			}
		}
		//异常订单每分钟询检
		Thread.sleep(Duration.ofMinutes(1).toMillis());
	}
}
