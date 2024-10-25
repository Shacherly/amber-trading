package com.google.backend.trading.dao.mapper;

import com.google.backend.trading.dao.model.TradeMarginOrder;
import com.google.backend.trading.dao.model.TradePosition;
import com.google.backend.trading.dao.model.TradePositionExample;
import com.google.backend.trading.model.internal.amp.AmpMarginReq;
import com.google.backend.trading.model.margin.api.PositionHistorySearchReq;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author adam.wang
 * @date 2021/10/3 11:08
 */
public interface TradePositionMapper {

    /**
     * 累计盈亏
     * @param req
     * @return
     */
    BigDecimal sumPnl(@Param("req") PositionHistorySearchReq req, @Param("uid")String uid);

    /**
     * 更新pnl
     * @param positionId
     * @param pnl
     * @return
     */
    int updatePnl(@Param("positionId") String positionId, @Param("pnl") BigDecimal pnl);


    void clearTakeProfitOrStopLoss(@Param("clearTakeProfit") boolean clearTakeProfit, @Param("positionId") Long positionId);

    int updateByExampleSelective(@Param("record") TradePosition record, @Param("example") TradePositionExample example);

    List<TradeMarginOrder> selectTradeMarginOrderByAmpMarginReq(@Param("req")AmpMarginReq req);
}
