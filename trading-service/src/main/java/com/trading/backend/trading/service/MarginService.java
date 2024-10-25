package com.google.backend.trading.service;

import com.github.pagehelper.PageInfo;
import com.google.backend.trading.dao.model.TradeMarginOrder;
import com.google.backend.trading.dao.model.TradeMarginOrderModification;
import com.google.backend.trading.dao.model.TradePosition;
import com.google.backend.trading.dao.model.TradeUserTradeSetting;
import com.google.backend.trading.model.booking.api.BookingPlaceReq;
import com.google.backend.trading.model.common.PageResult;
import com.google.backend.trading.model.common.model.riskcontrol.ReducePositionReq;
import com.google.backend.trading.model.internal.aceup.AceUpFundingCostReq;
import com.google.backend.trading.model.internal.aceup.AceUpFundingCostRes;
import com.google.backend.trading.model.internal.aceup.AceUpMarginPositionReq;
import com.google.backend.trading.model.internal.aceup.AceUpMarginPositionRes;
import com.google.backend.trading.model.internal.aceup.AceUpMarginReq;
import com.google.backend.trading.model.internal.aceup.AceUpMarginRes;
import com.google.backend.trading.model.internal.aceup.AceUpMarginTransReq;
import com.google.backend.trading.model.internal.aceup.AceUpMarginTransRes;
import com.google.backend.trading.model.internal.amp.AmpMarginReq;
import com.google.backend.trading.model.internal.amp.AmpMarginRes;
import com.google.backend.trading.model.internal.amp.AmpPositionReq;
import com.google.backend.trading.model.internal.amp.AmpPositionRes;
import com.google.backend.trading.model.internal.amp.AmpTransDetailRes;
import com.google.backend.trading.model.internal.amp.AmpTransReq;
import com.google.backend.trading.model.internal.amp.PositionFlowDetailReq;
import com.google.backend.trading.model.internal.amp.PositionFlowDetailRes;
import com.google.backend.trading.model.internal.amp.PositionInfoReq;
import com.google.backend.trading.model.internal.amp.PositionInfoRes;
import com.google.backend.trading.model.margin.api.ActiveOrderReq;
import com.google.backend.trading.model.margin.api.ActiveOrderRes;
import com.google.backend.trading.model.margin.api.ActivePositionInfoVo;
import com.google.backend.trading.model.margin.api.HistoryPositionDetailRes;
import com.google.backend.trading.model.margin.api.HistoryPositionRes;
import com.google.backend.trading.model.margin.api.MarginAssetInfoRes;
import com.google.backend.trading.model.margin.api.MarginDetailRes;
import com.google.backend.trading.model.margin.api.MarginOrderDetailReq;
import com.google.backend.trading.model.margin.api.MarginOrderDetailRes;
import com.google.backend.trading.model.margin.api.MarginOrderHistoryReq;
import com.google.backend.trading.model.margin.api.MarginOrderInfoRes;
import com.google.backend.trading.model.margin.api.MarginOrderModifyReq;
import com.google.backend.trading.model.margin.api.Position6HFundingCostVo;
import com.google.backend.trading.model.margin.api.PositionCloseHistoryReq;
import com.google.backend.trading.model.margin.api.PositionCloseHistoryRes;
import com.google.backend.trading.model.margin.api.PositionCloseReq;
import com.google.backend.trading.model.margin.api.PositionDetailReq;
import com.google.backend.trading.model.margin.api.PositionFundingCostReq;
import com.google.backend.trading.model.margin.api.PositionFundingCostVo;
import com.google.backend.trading.model.margin.api.PositionHistoryDetailReq;
import com.google.backend.trading.model.margin.api.PositionHistorySearchReq;
import com.google.backend.trading.model.margin.api.PositionRecordVo;
import com.google.backend.trading.model.margin.api.PositionRecordeReq;
import com.google.backend.trading.model.margin.api.PositionSettingReq;
import com.google.backend.trading.model.margin.api.PositionSettleHistoryReq;
import com.google.backend.trading.model.margin.api.PositionSettleHistoryRes;
import com.google.backend.trading.model.margin.api.PositionSettleInfoRes;
import com.google.backend.trading.model.margin.dto.ExecutableInfo;
import com.google.backend.trading.model.margin.dto.MarginInfo;
import com.google.backend.trading.model.margin.dto.MarginOrderCancel;
import com.google.backend.trading.model.margin.dto.MarginOrderPlace;
import com.google.backend.trading.model.margin.dto.PositionSettle;
import com.google.backend.trading.model.trade.Direction;
import com.google.backend.trading.model.trade.SourceType;
import org.springframework.data.util.Pair;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * 杠杆服务
 * @author adam.wang
 * @date 2021/9/29 19:58
 */
public interface MarginService {

    /**
     * 杠杆下单
     *
     * @param req 请求数据
     * @return 返回订单信息
     */
    MarginOrderInfoRes placeOrder(MarginOrderPlace req);

    /**
     * 请求OTC_SHOP的订单
     *
     * @param req 请求数据
     * @param userId 用户
     * @return 返回订单信息
     */
    MarginOrderInfoRes placeBookingOrder(BookingPlaceReq req, String userId);

    /**
     * 取消订单
     *
     * @param req 撤单请求
     */
    void cancelOrder(MarginOrderCancel req);


    /**
     * 修改订单
     *
     * @param req 撤单请求
     */
    void modifyOrder(MarginOrderModifyReq req, String uid);

    /**
     * 触发条件订单
     *
     * @param order
     * @return order
     */
    TradeMarginOrder triggerOrder(TradeMarginOrder order);

    /**
     * 执行订单前，检查保证金，最大持仓限额，只减仓
     *
     * @param orders: 订单列表
     * @return order
     */
    void checkAndExecuteOrder(String uid, String symbol, List<TradeMarginOrder> orders, TradeUserTradeSetting userTradeSetting);


    HashMap<Long, ExecutableInfo> checkExceptionOrderExecutable(String uid, String symbol, List<TradeMarginOrder> orders,
                                                                TradeUserTradeSetting userTradeSetting);

    /**
     * 执行订单前，检查保证金，最大持仓限额，只减仓，获取订单可执行数量
     *
     * @param orders: 订单列表
     * @return order
     */
    HashMap<Long, ExecutableInfo> checkOrderExecutable(String uid, String symbol, List<TradeMarginOrder> orders, TradeUserTradeSetting userTradeSetting);

    /**
     * 通过uid获取用户杠杆信息
     * @param uid
     * @return
     */
    MarginAssetInfoRes marginAssetInfo(String uid);


    /**
     * 当前委托
     * @param req
     * @param uid
     * @return
     */
    PageResult<ActiveOrderRes> activeOrder(ActiveOrderReq req, String uid);

    /**
     * 当前委托订单的数量
     * @param uid
     * @return
     */
    long countActiveOrder(String uid);

    /**
     * 历史订单查询
     * @param req
     * @param uid
     * @return
     */
    PageResult<MarginOrderInfoRes> orderHistory(MarginOrderHistoryReq req, String uid);

    /**
     * 用户订单详情查询
     * @param req
     * @param uid
     * @return
     */
    MarginOrderDetailRes orderDetail(MarginOrderDetailReq req, String uid);

    /**
     * 当前持仓
     * @param uid
     * @return
     */
    List<ActivePositionInfoVo> positionActive( String uid);
    /**
     * 当前持仓
     * @param uid
     * @return
     */
    Future<List<ActivePositionInfoVo>> activePosition(String uid);

    /**
     * 持仓详情
     * @param req
     * @param uid
     * @return
     */
    ActivePositionInfoVo positionDetail(PositionDetailReq req, String uid);
    /**
     * 资金费用
     * @param req
     * @param uid
     * @return
	 */
	PageResult<PositionFundingCostVo> positionFundingCost(PositionFundingCostReq req, String uid);

	/**
	 * 所有活跃仓位
	 *
	 * @param uids
	 * @return
	 */
	List<TradePosition> listAllActivePositions(List<String> uids);

	Pair<Long, List<TradePosition>> listAllActivePositionsWithSeq(List<String> uids);

	Pair<Long, PageInfo<TradePosition>> listAllActivePositionsWithSeq(Integer page, Integer pageSize);

	/**
	 * 交割
	 *
	 * @return 是否完全交割且成功
	 */
	boolean settlePosition(PositionSettle req);

	/**
	 * 自动交割
     */
    void autoSettlePosition(String uid);

    /**
     * 平仓
     * @param req
     */
    MarginOrderInfoRes closePosition(PositionCloseReq req, String uid);

    /**
     * 交割
     */
    PositionSettleInfoRes getSettlePositionInfo(String positionId, String uid);

    /**
     * 历史持仓
     * @param req
     * @param uid
     * @return
     */
    HistoryPositionRes positionHistory(PositionHistorySearchReq req, String uid);

    /**
     * 历史持仓详情
     * @param req
     * @param uid
     * @return
     */
    HistoryPositionDetailRes positionHistoryDetail(PositionHistoryDetailReq req, String uid);

    /**
     * 仓位记录
     * @param req
     * @param uid
     * @return
     */
    PageResult<PositionRecordVo> positionRecord(PositionRecordeReq req, String uid);

    /**
     * 平仓记录列表
     * @param req
     * @param uid
     * @return
     */
    PageResult<PositionCloseHistoryRes> positionCloseHistory(PositionCloseHistoryReq req, String uid);

    /**
     * 交割历史表
     * @param req
     * @param uid
     * @return
     */
    PageResult<PositionSettleHistoryRes> positionSettleHistory(PositionSettleHistoryReq req, String uid);

    /**
     * 设置仓位
     * @param req
     * @param uid
     */
    int setUpPosition(PositionSettingReq req, String uid);

    /**
     * 获取用户仓位信息
     * @param req
     * @return
     */
    PageResult<PositionInfoRes> queryPositionInfoForAmp(PositionInfoReq req);

    /**
     * 获取客户交易流水
     * @param req
     * @return
     */
    PageResult<PositionFlowDetailRes> flowDetail(PositionFlowDetailReq req);

    /**
     * 查询all活跃订单
     * @param uid
     * @return
     */
    List<TradeMarginOrder> getTradeMarginOrders(String uid);

    /**
     * 取消非系统单 for RISK
     * @param uid
     * @return
     */
    void cancelAllNotSystemOrders(String uid);

    /**
     * 强制减仓或平仓
     * @param reducePositionReq
     */
    void forceClosePosition(ReducePositionReq reducePositionReq);

    /**
     * 下单平仓直到全部成交
     * @param uid
     * @param symbol
     * @param direction
     * @param quantity
     * @param source
     */
    void placeUntilAllComplete(String uid, String symbol, Direction direction,
                               BigDecimal quantity, SourceType source);

    /**
     * 修改记录默认最多返回100条
     * @param uuid
     * @return
     */
    List<TradeMarginOrderModification> orderModifications(String uuid);

    /**
     * 杠杆信息
     * @param uid
     * @return
     */
    MarginInfo marginInfo(String uid);

    /**
     * 查看用户的unpnl
     * @param uidList
     * @return
     */
    Map<String, BigDecimal> unpnl(List<String> uidList);

    /**
     * 杠杆详情
     * @param uid
	 * @param symbol
	 * @param direction
     * @return
     */
    MarginDetailRes detail(String uid, String symbol, String direction);

    /**
     * 杠杆信息获取
     * @param uids
     * @return
     */
    Map<String, MarginAssetInfoRes> marginInfos(List<String> uids);

    Position6HFundingCostVo getFundingCostBefore6H(Long searchTime);

    PageResult<String> getTransactionListForAmp(AmpTransReq ampTransReq);

    AmpTransDetailRes getTransDetailForAmp(String uid, String transId);

    /**
     * 仓位列表foramp
     * @param req
     * @return
     */
    PageResult<AmpPositionRes> positionHistoryForAmp(AmpPositionReq req);

    /**
     * 杠杆订单列表
     *
     * @param req
     * @return
     */
    PageResult<AmpMarginRes> marginHistoryForAmp(AmpMarginReq req);

    /**
     * aceup杠杆订单列表
     *
     * @param req
     * @return
     */
    PageResult<AceUpMarginRes> marginHistoryForAceUp(AceUpMarginReq req);

    /**
     * aceup杠杆订单交易记录
     *
     * @param req
     * @return
     */
    PageResult<AceUpMarginTransRes> marginTransactionListForAceUp(AceUpMarginTransReq req);

    /**
     * aceup杠杆仓位记录
     *
     * @param req
     * @return
     */
    PageResult<AceUpMarginPositionRes> marginPositionListForAceUp(AceUpMarginPositionReq req);

    /**
     * aceup杠杆资金费率记录
     *
     * @param req
     * @return
     */
    PageResult<AceUpFundingCostRes> positionFundingCostListForAceUp(AceUpFundingCostReq req);
}
