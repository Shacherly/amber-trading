package com.google.backend.trading.dao.mapper;

import com.google.backend.trading.dao.model.TradeNegativeBalanceFundingCost;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TradeNegativeBalanceFundingCostMapper {

    int batchInsert(@Param("list") List<TradeNegativeBalanceFundingCost> list);
}