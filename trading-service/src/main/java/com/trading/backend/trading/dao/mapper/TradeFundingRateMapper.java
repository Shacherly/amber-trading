package com.google.backend.trading.dao.mapper;

import com.google.backend.trading.dao.model.TradeFundingRate;
import com.google.backend.trading.dao.model.TradeFundingRateExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author adam.wang
 */
public interface TradeFundingRateMapper {

    int batchInsertIgnoreConflict(@Param("list") List<TradeFundingRate> list);

}