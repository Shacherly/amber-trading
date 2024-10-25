package com.google.backend.trading.task;

import com.google.backend.trading.dao.mapper.DefaultTradeMarginOrderMapper;
import com.google.backend.trading.dao.model.TradeMarginOrder;
import com.google.backend.trading.dao.model.TradeMarginOrderExample;
import com.google.backend.trading.dao.model.TradeUserTradeSetting;
import com.google.backend.trading.exception.LockException;
import com.google.backend.trading.model.trade.Direction;
import com.google.backend.trading.model.trade.OrderStatus;
import com.google.backend.trading.model.trade.OrderType;
import com.google.backend.trading.model.trade.SourceType;
import com.google.backend.trading.model.trade.TradeStrategy;
import com.google.backend.trading.service.MarginService;
import com.google.backend.trading.service.OrderRequest;
import com.google.backend.trading.service.UserTradeSettingService;
import com.google.backend.trading.util.AlarmLogUtil;
import com.google.backend.trading.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 杠杆订单执行LOOP
 *
 * @author jiayi.zhang
 * @date 2021/10/21 17:01
 */
@Slf4j
@Component
public class MarginOrderExecuteLoopThread extends AbstractLoopThread {
	@Resource
	private MarginService marginService;
	@Resource
	private UserTradeSettingService userTradeSettingService;
	@Resource
	private OrderRequest orderRequest;
	@Resource
	private DefaultTradeMarginOrderMapper defaultTradeMarginOrderMapper;

	private final ExecutorService pool = Executors.newFixedThreadPool(4);

	public MarginOrderExecuteLoopThread() {
		super(Duration.ofSeconds(5).toMillis());
	}

	@Override
	@Trace(operationName = "MarginOrderExecuteLoopThread")
	public void handle() throws InterruptedException, ExecutionException {
		TradeMarginOrderExample example = new TradeMarginOrderExample();
		TradeMarginOrderExample.Criteria criteria = example.createCriteria();
		criteria.andStatusEqualTo(OrderStatus.EXECUTING.getName());
		List<TradeMarginOrder> orders = defaultTradeMarginOrderMapper.selectByExample(example);
		Map<String, Map<String, List<TradeMarginOrder>>> orderMap = new HashMap<>();
		log.info("loop handler handle execute margin order num = {}", orders.size());
		// todo: order排序
		for (TradeMarginOrder order : orders) {
			if (!CommonUtils.isSyncOrder(OrderType.getByName(order.getType()), TradeStrategy.getByName(order.getStrategy()), SourceType.getByName(order.getSource()))) {
				if (log.isDebugEnabled()) {
					log.debug("MarginOrderExecuteLoop|l1| orderId:{}, order:{}",order.getUuid(),order);
				}
				if (!OrderType.isLimitOrder(order.getType()) || orderRequest.isReachPrice(order.getSymbol(), Direction.getByName(order.getDirection()), order.getPrice())) {
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
		}
		Collection<Callable<Void>> tasks = new ArrayList<>();
		for (Map.Entry<String, Map<String, List<TradeMarginOrder>>> entry : orderMap.entrySet()) {
			if (log.isDebugEnabled()) {
				log.debug("MarginOrderExecuteLoop|l2| entry:{}", entry);
			}
			try {
				String uid = entry.getKey();
				Map<String, List<TradeMarginOrder>> userOrders = entry.getValue();
				TradeUserTradeSetting userTradeSetting = userTradeSettingService.queryTradeSettingByUid(entry.getKey());
				for (Map.Entry<String, List<TradeMarginOrder>> userEntry : userOrders.entrySet()) {
					String symbol = userEntry.getKey();
					List<TradeMarginOrder> userSymbolOrders = userEntry.getValue();
					tasks.add(decorate(() -> {
						try {
							marginService.checkAndExecuteOrder(uid, symbol, userSymbolOrders, userTradeSetting);
						} catch (Exception e) {
							AlarmLogUtil.alarm("Execute Order Exception: {}, orders: {}", e, userSymbolOrders);
						}
						return null;
					}));
				}
			}
			catch (Exception e) {
				log.error("handle err, cause = {}", ExceptionUtils.getRootCauseMessage(e), e);
			}
		}
		log.info("handle task num = {}", tasks.size());
		pool.invokeAll(tasks);
		Thread.sleep(Duration.ofMillis(100).toMillis());
	}


	public static Callable decorate(Callable callable) {
		Map<String, String> contextMap = MDC.getCopyOfContextMap();
		return () -> {
			if (contextMap != null) {
				MDC.setContextMap(contextMap);
			}
			try {
				return callable.call();
			} finally {
				MDC.clear();
			}
		};
	}

}
