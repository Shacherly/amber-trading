package com.google.backend.trading.dao.mapper;

import com.google.backend.trading.dao.model.TradePositionFundingCost;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TradePositionFundingCostMapper {

    int batchInsert(@Param("list") List<TradePositionFundingCost> list);
}