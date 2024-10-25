package com.google.backend.trading.task;

import com.google.backend.trading.dao.mapper.DefaultTradeTransactionMapper;
import com.google.backend.trading.dao.model.TradeTransaction;
import com.google.backend.trading.dao.model.TradeTransactionExample;
import com.google.backend.trading.model.trade.AssetStatus;
import com.google.backend.trading.model.trade.PdtStatus;
import com.google.backend.trading.model.trade.TransactionType;
import com.google.backend.trading.service.AssetRequest;
import com.google.backend.trading.util.AlarmLogUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 【杠杆订单】资金请求失败情况 LOOP
 *
 * @author jiayi.zhang
 * @date 2021/10/21 17:01
 */
@Slf4j
@Component
public class MarginAssetRequestErrorHandleLoopThread extends AbstractLoopThread {
    @Resource
    private AssetRequest assetRequest;
    @Resource
    private DefaultTradeTransactionMapper defaultTradeTransactionMapper;

    public MarginAssetRequestErrorHandleLoopThread() {
        super(Duration.ofSeconds(20).toMillis());
    }

    @Override
    @Trace(operationName = "MarginAssetRequestErrorHandleLoopThread")
	public void handle() throws InterruptedException {
		TradeTransactionExample example = new TradeTransactionExample();
		TradeTransactionExample.Criteria criteria = example.createCriteria();
		criteria.andAssetStatusIn(Arrays.asList(AssetStatus.PENDING.name(), AssetStatus.EXCEPTION.name()));
		//查询有效订单
		criteria.andPdtStatusEqualTo(PdtStatus.COMPLETED.name());
		// 筛选已创建10s，asset_status仍为PENDING的交易
		Date date = new Date(System.currentTimeMillis() - Duration.ofSeconds(10).toMillis());
		criteria.andCtimeLessThan(date);
		List<TradeTransaction> transactions = defaultTradeTransactionMapper.selectByExample(example);
		log.info("loop handler handle trade transaction num = {}", transactions.size());
		for (TradeTransaction transaction : transactions) {
			log.info("AssetRequestErrorHandleLoop|l1| transaction:{}", transaction);
			TransactionType type = TransactionType.valueOf(transaction.getType());
			AssetStatus assetStatus = null;
			if (TransactionType.isOpenPos(type)) {
				assetStatus = assetRequest.doOpenPosition(transaction);
			}
			else if (TransactionType.isClosePos(type)) {
				assetStatus = assetRequest.doClosePosition(transaction);
			}
			// todo: other transaction type
			if (assetStatus == AssetStatus.COMPLETED || assetStatus == AssetStatus.EXCEPTION) {
				boolean baseZero = null != transaction.getBaseQuantity() && transaction.getBaseQuantity().compareTo(BigDecimal.ZERO) == 0;
				boolean quoteZero =
						null != transaction.getQuoteQuantity() && transaction.getQuoteQuantity().compareTo(BigDecimal.ZERO) == 0;
				if (baseZero || quoteZero) {
					AlarmLogUtil.alarm("存在异常数据 quantity 为 0，transaction id = {}", transaction.getId());
					assetStatus = AssetStatus.COMPLETED;
				}
				transaction.setAssetStatus(assetStatus.name());
				transaction.setMtime(new Date());
				defaultTradeTransactionMapper.updateByPrimaryKeySelective(transaction);
			}
		}
		Thread.sleep(Duration.ofSeconds(10).toMillis());
	}
}
