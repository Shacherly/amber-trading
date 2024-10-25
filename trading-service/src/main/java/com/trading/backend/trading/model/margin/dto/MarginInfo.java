package com.google.backend.trading.model.margin.dto;

import com.google.backend.asset.common.model.base.PoolEntityForRisk;
import com.google.backend.trading.dao.model.TradePosition;
import com.google.backend.trading.model.margin.api.ActivePositionInfoVo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 所有仓位杠杆信息
 *
 * @author trading
 * @date 2021/10/18 15:55
 */
@Getter
@ToString
@AllArgsConstructor
public class MarginInfo {

	/**
	 * 开仓总保证金使用建仓抵扣率（以下以抵扣率称呼）
	 * 开仓总保证金 = 各个币求和 { (可用余额+理财锁定) * 抵扣率 * 兑换usd价格 + 现货锁定 * min (base抵扣率, quote抵扣率) * 兑换usd价格 + 仓位unPnl * 抵扣率 + 信用额度 * 兑换usd价格 }
	 */
	private BigDecimal totalOpenMargin;

	/**
	 * 清算总保证金使用清算抵扣率（以下以抵扣率称呼）
	 * 清算总保证金 = 各个币求和 { (可用余额+理财锁定) * 抵扣率 * 兑换usd价格 + 现货锁定 * min (base抵扣率, quote抵扣率) * 兑换usd价格 + 仓位unPnl * 抵扣率 + 信用额度 * 兑换usd价格 }
	 */
	private BigDecimal totalLiquidMargin;


	/**
	 * 持仓头寸 = 所有仓位求和 { 中间价 * 持仓大小 * 兑换USD价格 }
	 */
	private BigDecimal position;

	/**
	 * 已用保证金 = min { 总保证金， 持仓头寸/杠杠倍数 }
	 * 已用保证金，按照 信用额度 -> 理财质押 -> 余额 + 现货锁定 + unpnl 的顺序来占用
	 */
	private BigDecimal usedMargin;

	/**
	 * 可用保证金 = 开仓总保证金 - 已用保证金
	 */
	private BigDecimal availableMargin;

	/**
	 * unpnl浮动盈亏
	 * 做空： (开仓均价 - 对手价格) * 持仓数量
	 * 做多： (对手价格 - 开仓均价) * 持仓数量
	 */
	private BigDecimal unpnl;

	/**
	 * 持仓杠杆 持仓头寸/总保证金(清算抵扣率)
	 */
	private BigDecimal currentLeverage;

	/**
	 * 风险率= 1 - (清算总保证金 - 持仓头寸 * 维持保证金率)/ max{总保证金 - 浮动盈亏， 总保证金}
	 */
	private BigDecimal riskRate;

	/**
	 * 可开仓数量 = 可用保证金 * 杠杆倍数
	 */
	private BigDecimal canOpenUsd;

	/**
	 * 信用额度占用
	 */
	private BigDecimal usedCredit;

	/**
	 * 排除信用额度外的保证金占用
	 */
	private BigDecimal usedMarginWithoutCredit;

	/**
	 * 资金使用率
	 */
	private BigDecimal fundUtilization;

	/**
	 * 用户设置杠杆
	 */
	private BigDecimal leverage;

	/**
	 * 当前活跃仓位
	 */
	private List<TradePosition> currentPositions;

	/**
	 * 当前活跃仓位,按头寸排序
	 */
	private List<ActivePositionInfoVo> currentPositionVos;

	/**
	 * 携带的资金池
	 */
	private Map<String, PoolEntityForRisk> poolEntityForRiskMap;

}
