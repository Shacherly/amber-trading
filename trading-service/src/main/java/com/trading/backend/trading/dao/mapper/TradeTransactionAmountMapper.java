package com.google.backend.trading.dao.mapper;

import com.google.backend.trading.dao.model.TradeTransactionAmount;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface TradeTransactionAmountMapper {

    BigDecimal sumAmountByUserIdAndCtime(@Param("uid") String uid,
                                         @Param("start") LocalDateTime start,
                                         @Param("end") LocalDateTime end);

    int batchInsert(@Param("list") List<TradeTransactionAmount> list);

    List<String> getUserIdListByTime(@Param("start") LocalDateTime start,
                                     @Param("end") LocalDateTime end);
}