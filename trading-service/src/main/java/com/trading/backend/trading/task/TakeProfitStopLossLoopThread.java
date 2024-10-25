package com.google.backend.trading.task;

import com.google.backend.trading.component.SymbolDomain;
import com.google.backend.trading.constant.Constants;
import com.google.backend.trading.dao.mapper.DefaultTradePositionMapper;
import com.google.backend.trading.dao.mapper.DefaultTradeTransactionMapper;
import com.google.backend.trading.dao.mapper.DefaultTradeUserTradeSettingMapper;
import com.google.backend.trading.dao.mapper.TradePositionMapper;
import com.google.backend.trading.dao.model.TradePosition;
import com.google.backend.trading.dao.model.TradePositionExample;
import com.google.backend.trading.dao.model.TradeTransaction;
import com.google.backend.trading.dao.model.TradeTransactionExample;
import com.google.backend.trading.dao.model.TradeUserTradeSetting;
import com.google.backend.trading.dao.model.TradeUserTradeSettingExample;
import com.google.backend.trading.model.margin.PositionStatus;
import com.google.backend.trading.model.trade.Direction;
import com.google.backend.trading.model.trade.PdtStatus;
import com.google.backend.trading.model.trade.SourceType;
import com.google.backend.trading.model.trade.TransactionType;
import com.google.backend.trading.service.MarginService;
import com.google.backend.trading.service.PushMsgService;
import com.google.backend.trading.service.TradeAssetService;
import com.google.backend.trading.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
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
public class TakeProfitStopLossLoopThread extends AbstractLoopThread {
	@Resource
	private MarginService marginService;
	@Resource
	private TradeAssetService tradeAssetService;
	@Resource
	private DefaultTradeUserTradeSettingMapper defaultTradeUserTradeSettingMapper;
	@Resource
	private DefaultTradePositionMapper defaultTradePositionMapper;
	@Resource
	private DefaultTradeTransactionMapper transactionMapper;
	@Autowired
	private TradePositionMapper positionMapper;
	@Autowired
	private PushMsgService pushService;
	private static final List<String> CLOSE_POS_TYPE = Arrays.asList(
			TransactionType.CLOSE_POSITION.getName(), TransactionType.REDUCE_POSITION.getName());

	public TakeProfitStopLossLoopThread() {
		super(Duration.ofSeconds(5).toMillis());
	}


	@Override
	@Trace(operationName = "TakeProfitStopLossLoopThread")
	public void handle() throws InterruptedException {
		Map<String, List<TradePosition>> positionMap = this.loadActivePosition();
		Map<String, TradeUserTradeSetting> settingMap = this.loadEffectiveSetting();
		log.info("loop handler handle tp sl position num = {}", positionMap.values().size());
		for (Map.Entry<String, List<TradePosition>> entry : positionMap.entrySet()) {
			TradeUserTradeSetting userSetting = settingMap.get(entry.getKey());
			// 是否进行了全仓止盈止损，如果进行了全仓止盈止损，则不需要进行单个仓位的止盈止损
			boolean didFull = false;
			if (userSetting != null) {
				didFull = doFullPosition(userSetting, entry.getValue());
			}
			if (!didFull) {
				for (TradePosition pos: entry.getValue()) {
					doSinglePosition(pos);
				}
			}
		}
		Thread.sleep(Duration.ofMillis(100).toMillis());
	}

	private Map<String, List<TradePosition>> loadActivePosition() {
		Map<String, List<TradePosition>> positionMap = new HashMap<>();
		TradePositionExample example = new TradePositionExample();
		TradePositionExample.Criteria criteria = example.createCriteria();
		criteria.andStatusEqualTo(PositionStatus.ACTIVE.name()).andQuantityGreaterThan(BigDecimal.ZERO);
		List<TradePosition> positions = defaultTradePositionMapper.selectByExample(example);
		for (TradePosition pos : positions) {
			List<TradePosition> userPositions = positionMap.getOrDefault(pos.getUid(), new ArrayList<>());
			userPositions.add(pos);
			positionMap.put(pos.getUid(), userPositions);
		}
		return positionMap;
	}

	private Map<String, TradeUserTradeSetting> loadEffectiveSetting() {
		Map<String, TradeUserTradeSetting> settingMap = new HashMap<>();
		TradeUserTradeSettingExample example = new TradeUserTradeSettingExample();
		example.createCriteria().andMaxLossGreaterThan(CommonUtils.ZERO_NUM);
		example.or(example.createCriteria().andTakeProfitGreaterThan(CommonUtils.ZERO_NUM));
		List<TradeUserTradeSetting> userSettings = defaultTradeUserTradeSettingMapper.selectByExample(example);
		for (TradeUserTradeSetting setting : userSettings) {
			settingMap.put(setting.getUid(), setting);
		}
		return settingMap;
	}

	private boolean doFullPosition(TradeUserTradeSetting setting, List<TradePosition> posList) {
		BigDecimal totalPnl = CommonUtils.ZERO_NUM;
		for (TradePosition position: posList) {
			totalPnl = totalPnl.add(tradeAssetService.calculatePositionUnpnlUsd(position).getFirst());
		}
		if (isTriggerFullTakeProfitStopLoss(totalPnl, setting)) {
			log.info("trigger full tp sl, total pnl = {}, positions = {}, setting = {}", totalPnl, posList, setting);
			BigDecimal tradePnl = BigDecimal.ZERO;
			for (TradePosition position: posList) {
				closePosition(position, BigDecimal.ONE);
				Pair<BigDecimal, BigDecimal> trade = getLastClosePnl(position);
				if (trade != null) {
					tradePnl = tradePnl.add(trade.getFirst());
				}
			}
			pushService.marginStopCrossedPosition(setting.getUid(), tradePnl);
			// 全仓模式止盈止损完毕清理设置值
			clearUserSettingValue(setting);
			return true;
		}
		return false;
	}

	private boolean doSinglePosition(TradePosition position) {
		Pair<BigDecimal, Boolean> pair = getPositionTakeProfitStopLossPercentage(position);
		//为空表示没有设置止盈止损
		if (null == pair) {
			return false;
		}
		BigDecimal closePercentage = pair.getFirst();
		if (CommonUtils.isPositive(closePercentage)) {
			//TODO 待优化
			closePosition(position, closePercentage);
			//清除止盈止损标记
			Boolean isProfit = pair.getSecond();
			clearTakeProfitOrStopLoss(position, isProfit);
			Pair<BigDecimal, BigDecimal> last = getLastClosePnl(position);
			if (last != null) {
				pushService.marginStopSinglePosition(position, isProfit, last.getFirst(), last.getSecond());
			}
			return true;
		}
		return false;
	}

	private void clearTakeProfitOrStopLoss(TradePosition position, Boolean takeProfit) {
		boolean clearTakeProfitOrStopLoss = takeProfit;
		positionMapper.clearTakeProfitOrStopLoss(clearTakeProfitOrStopLoss, position.getId());
	}

	private void clearUserSettingValue(TradeUserTradeSetting setting) {
		setting.setTakeProfit(BigDecimal.ZERO);
		setting.setMaxLoss(BigDecimal.ZERO);
		setting.setMtime(CommonUtils.getNowTime());
		defaultTradeUserTradeSettingMapper.updateByPrimaryKey(setting);
	}

	private boolean isTriggerFullTakeProfitStopLoss(BigDecimal unpnl, TradeUserTradeSetting userTradeSetting) {
		if (CommonUtils.isPositive(userTradeSetting.getMaxLoss()) && unpnl.compareTo(userTradeSetting.getMaxLoss().negate()) <= 0) {
			return true;
		}
		if (CommonUtils.isPositive(userTradeSetting.getTakeProfit()) && unpnl.compareTo(userTradeSetting.getTakeProfit()) >= 0) {
			return true;
		}
		return false;
	}

	private Pair<BigDecimal, Boolean> getPositionTakeProfitStopLossPercentage(TradePosition position) {
		if (null == position.getTakeProfitPrice() && null == position.getStopLossPrice()) {
			return null;
		}
		BigDecimal currentPrice = SymbolDomain.nonNullGet(position.getSymbol()).rivalPrice(position.getDirection());
		Direction direction = Direction.getByName(position.getDirection());
		if (direction == Direction.BUY) {
			if (CommonUtils.isPositive(position.getTakeProfitPercentage()) && CommonUtils.isPositive(position.getTakeProfitPrice()) && currentPrice.compareTo(position.getTakeProfitPrice()) >= 0) {
				log.info("single position tp or sl, position = {}, current price = {}", position, currentPrice);
				return Pair.of(position.getTakeProfitPercentage(), true);
			}
			if (CommonUtils.isPositive(position.getStopLossPercentage()) && CommonUtils.isPositive(position.getStopLossPrice()) && currentPrice.compareTo(position.getStopLossPrice()) <= 0) {
				log.info("single position tp or sl, position = {}, current price = {}", position, currentPrice);
				return Pair.of(position.getStopLossPercentage(), false);
			}
		} else {
			if (CommonUtils.isPositive(position.getTakeProfitPercentage()) && CommonUtils.isPositive(position.getTakeProfitPrice()) && currentPrice.compareTo(position.getTakeProfitPrice()) <= 0) {
				log.info("single position tp or sl, position = {}, current price = {}", position, currentPrice);
				return Pair.of(position.getTakeProfitPercentage(), true);
			}
			if (CommonUtils.isPositive(position.getStopLossPercentage()) && CommonUtils.isPositive(position.getStopLossPrice()) && currentPrice.compareTo(position.getStopLossPrice()) >= 0) {
				log.info("single position tp or sl, position = {}, current price = {}", position, currentPrice);
				return Pair.of(position.getStopLossPercentage(), false);
			}
		}
		return null;
	}

	private void closePosition(TradePosition position, BigDecimal closePercentage) {
		if (CommonUtils.isPositive(position.getQuantity())) {
			marginService.placeUntilAllComplete(
					position.getUid(),
					position.getSymbol(),
					Direction.rivalDirection(position.getDirection()),
					position.getQuantity().multiply(closePercentage),
					SourceType.TAKE_PROFIT_STOP_LOSS
			);
		} else {
			log.error("position error: {}", position);
		}
	}

	private Pair<BigDecimal, BigDecimal> getLastClosePnl(TradePosition pos) {
		TradeTransactionExample example = new TradeTransactionExample();
		example.setOrderByClause("CTIME DESC LIMIT 1");
		TradeTransactionExample.Criteria criteria = example.createCriteria();
		//查询有效订单
		criteria.andPdtStatusEqualTo(PdtStatus.COMPLETED.name());
		criteria.andUidEqualTo(pos.getUid());
		criteria.andPositionIdEqualTo(pos.getUuid());
		criteria.andTypeIn(CLOSE_POS_TYPE);
		criteria.andSourceEqualTo(SourceType.TAKE_PROFIT_STOP_LOSS.getName());

		List<TradeTransaction> tradeList = transactionMapper.selectByExample(example);
		if (!tradeList.isEmpty()) {
			TradeTransaction trade = tradeList.get(0);
			BigDecimal middle = this.getMiddlePrice(pos.getSymbol());
			BigDecimal lastPnl = trade.getPnl().multiply(middle).subtract(trade.getFee());
			BigDecimal amount = trade.getQuoteQuantity();
			return Pair.of(lastPnl, amount);
		}

		log.error("find close position error:{}", pos);
        return null;
	}

	private BigDecimal getMiddlePrice(String symbol) {
		String[] coins = symbol.split("_");
		if (coins[1].equals(Constants.BASE_COIN)) {
			return BigDecimal.ONE;
		} else {
			String convSymbol = coins[1] + Constants.BASE_QUOTE;
			return CommonUtils.getMiddlePrice(convSymbol);
		}
	}
}
