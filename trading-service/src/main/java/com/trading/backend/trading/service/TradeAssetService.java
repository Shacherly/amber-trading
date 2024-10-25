package com.google.backend.trading.service;

import com.google.backend.asset.common.model.base.PoolEntityForRisk;
import com.google.backend.trading.dao.model.TradePosition;
import com.google.backend.trading.model.margin.api.ActivePositionInfoVo;
import com.google.backend.trading.model.margin.dto.MarginInfo;
import com.google.backend.trading.model.margin.dto.SettleAsset;
import com.google.backend.trading.model.trade.Direction;
import org.springframework.data.util.Pair;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author trading
 * @date 2021/10/15 14:41
 */
public interface TradeAssetService {

	BigDecimal unpnl(List<TradePosition> tradePositions);

	/**
	 * 杠杆信息（保证金，持仓杠杆，风险率，unpnl等杠杆相关数据）
	 * 内部不依赖任何外部调用，纯计算，数据由外部传递
	 * @param uid
	 * @param poolEntityForRiskMap
	 * @param tradePositions
	 * @param leverage
	 * @param earnPledge
	 * @param pnlConversion
	 * @return
	 */
	MarginInfo marginInfo(String uid, Map<String, PoolEntityForRisk> poolEntityForRiskMap, List<TradePosition> tradePositions,
						  BigDecimal leverage, boolean earnPledge, boolean pnlConversion);

	/**
	 * 获取总保证金
	 *
	 * @param poolEntityMap
	 * @param earnPledge
	 * @param openUnpnl
	 * @param liquidUnpnl
	 * @return first 是建仓总保证金 second 是清算总保证金
	 */
	Pair<BigDecimal, BigDecimal> calculateTotalMargin(Map<String, PoolEntityForRisk> poolEntityMap, boolean earnPledge,
													  BigDecimal openUnpnl,
													  BigDecimal liquidUnpnl);

	/**
	 * pnl转换成usd后的数额
	 * @param position
	 * @return
	 */
	Pair<BigDecimal, BigDecimal> calculatePositionUnpnlUsd(TradePosition position);

	/**
	 * 原始仓位pnl
	 *
	 * @param position     仓位
	 * @return pnl base计价
	 */
	BigDecimal getOriginalPnl(TradePosition position);

	/**
	 * 计算仓位头寸，中间价计算
	 * @param position
	 * @return
	 */
	BigDecimal calculatePositionValue(TradePosition position);

	/**
	 * 查询现货/兑换可下单数量
	 * @return
	 * @param uid
	 * @param symbol
	 * @param direction
	 */
	BigDecimal spotAvailable(String uid, String symbol, Direction direction);

	/**
	 * 可交割资金
	 *
	 * 买 可交割数量 = min (Quote可用资产/交割价格，持仓数量)
	 * 卖 可交割数量 = min (Base可用资产，持仓数量)
	 * @return
	 * @param position
	 */
	SettleAsset settleAvailable(TradePosition position);

	/**
	 * 仓位转换
	 *
	 * @param totalMargin
	 * @param dto
	 */
	void calculateExtPositionInfo(BigDecimal totalMargin, ActivePositionInfoVo dto, boolean pnlConversion);

	void setUserAmountUsd2DB(String uid, String coin, BigDecimal amount, String transId);

	/**
	 * 计算30天交易额
	 *
	 * @param uid
	 * @return
	 */
	BigDecimal get30TradeAmount(String uid);
}
