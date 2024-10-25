package com.google.backend.trading.dao.mapper;

import com.google.backend.trading.dao.model.TradeSwapOrder;
import com.google.backend.trading.model.swap.api.SwapOrderHistoryLiteReq;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author david.chen
 * @date 2022/1/7 15:13
 */
public interface TradeSwapOrderMapper {
    List<TradeSwapOrder> selectHistoryLite(@Param("req") SwapOrderHistoryLiteReq req, @Param("userId") String userId);
}
