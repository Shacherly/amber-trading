package com.google.backend.trading.task;

import com.google.backend.trading.dao.mapper.DefaultTradeMarginOrderMapper;
import com.google.backend.trading.dao.model.TradeMarginOrder;
import com.google.backend.trading.dao.model.TradeMarginOrderExample;
import com.google.backend.trading.model.trade.OrderStatus;
import com.google.backend.trading.model.trade.OrderType;
import com.google.backend.trading.model.trade.SourceType;
import com.google.backend.trading.model.trade.TradeStrategy;
import com.google.backend.trading.service.MarginService;
import com.google.backend.trading.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.List;

/**
 * 杠杆订单执行LOOP
 *
 * @author jiayi.zhang
 * @date 2021/10/21 17:01
 */
@Slf4j
@Component
public class MarginOrderTriggerLoopThread extends AbstractLoopThread {
	@Resource
	private MarginService marginService;
	@Resource
	private DefaultTradeMarginOrderMapper defaultTradeMarginOrderMapper;

	public MarginOrderTriggerLoopThread() {
		super(Duration.ofSeconds(5).toMillis());
	}

	@Override
	@Trace(operationName = "MarginOrderTriggerLoopThread")
	public void handle() throws InterruptedException {
		TradeMarginOrderExample example = new TradeMarginOrderExample();
		TradeMarginOrderExample.Criteria criteria = example.createCriteria();
		criteria.andStatusEqualTo(OrderStatus.PRE_TRIGGER.getName());
		List<TradeMarginOrder> orders = defaultTradeMarginOrderMapper.selectByExample(example);
		log.info("loop handler handle trigger margin order num = {}", orders.size());
		for (TradeMarginOrder order : orders) {
			if (log.isDebugEnabled()) {
				log.debug("MarginOrderTriggerLoop|l2| order:{}",order);
			}
			if (!CommonUtils.isSyncOrder(OrderType.getByName(order.getType()), TradeStrategy.getByName(order.getStrategy()), SourceType.getByName(order.getSource()))) {
				try {
					marginService.triggerOrder(order);
				} catch (Exception e) {
					log.error("handle err, cause = {}", ExceptionUtils.getRootCauseMessage(e), e);
				}
			}
		}
		Thread.sleep(Duration.ofMillis(100).toMillis());
	};
}
