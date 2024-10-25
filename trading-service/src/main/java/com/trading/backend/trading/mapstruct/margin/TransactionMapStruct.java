package com.google.backend.trading.mapstruct.margin;

import com.google.backend.trading.dao.model.TradeTransaction;
import com.google.backend.trading.model.internal.amp.AmpTransDetailRes;
import com.google.backend.trading.model.margin.api.PositionCloseHistoryRes;
import com.google.backend.trading.model.margin.api.PositionRecordVo;
import com.google.backend.trading.model.margin.api.PositionSettleHistoryRes;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

/**
 * @author adam.wang
 * @date 2021/10/1 18:36
 */
@Mapper(componentModel="spring")
public interface TransactionMapStruct {

    /**
     * transaction2PositionRecordVo
     * @param tradeTransaction
     * @return
     */
    @Mappings({
            @Mapping(target = "quantity",source = "baseQuantity"),
            @Mapping(target = "pnl", expression = "java((null == tradeTransaction.getPnl()? java.math.BigDecimal.ZERO : tradeTransaction.getPnl())" +
                    ".subtract(" +
                    "(null == tradeTransaction.getFee() ? java.math.BigDecimal.ZERO : tradeTransaction.getFee())))"),
            @Mapping(target = "type",expression = "java(com.google.backend.trading.model.trade.OptType.getOptType(tradeTransaction))"),
    })
    PositionRecordVo transaction2PositionRecordVo(TradeTransaction tradeTransaction);

    /**
     * transactions2PositionRecordVos
     * @param tradeTransactions
     * @return
     */
    List<PositionRecordVo> transactions2PositionRecordVos(List<TradeTransaction> tradeTransactions);


    /**
     * transaction2PositionCloseHistoryRes
     * @param tradeTransaction
     * @return
     */
    @Mappings({
            @Mapping(target = "transactionId",source = "uuid"),
            @Mapping(target = "quantity",source = "baseQuantity"),
            @Mapping(target = "openPrice",expression = "java(TransactionUtil.getOpenPrice(tradeTransaction))"),
            @Mapping(target = "pnl",expression = "java(null==tradeTransaction.getPnlConversion()?" +
                    "tradeTransaction.getPnl():tradeTransaction.getPnlConversion())"),
            @Mapping(target = "pnlCoin",expression = "java(null==tradeTransaction.getPnlConversion()?" +
                    "tradeTransaction.getSymbol().split(\"_\")[1]:\"USD\")"),
            @Mapping(target = "direction",expression = "java(com.google.backend.trading.model.trade.Direction.rivalDirection(tradeTransaction.getDirection()).getName())"),
    })
    PositionCloseHistoryRes transaction2PositionCloseHistoryRes(TradeTransaction tradeTransaction);

    /**
     * list transactions2PositionCloseHistoryRes
     * @param tradeTransactions
     * @return
     */
    List<PositionCloseHistoryRes> transactions2PositionCloseHistoryRes(List<TradeTransaction> tradeTransactions);
    /**
     * transaction2PositionSettleHistoryRes
     * @param tradeTransaction
     * @return
     */
    @Mappings({
            @Mapping(target = "transactionId",source = "uuid"),
            @Mapping(target = "quantity",source = "baseQuantity"),
    })
    PositionSettleHistoryRes transaction2PositionSettleHistoryRes(TradeTransaction tradeTransaction);

    /**
     * list  transactions2PositionSettleHistoryRes
     * @param tradeTransactions
     * @return
     */
    List<PositionSettleHistoryRes> transactions2PositionSettleHistoryRes(List<TradeTransaction> tradeTransactions);

    /**
     *
     * @param tradeTransaction
     * @return
     */
    @Mappings({
            @Mapping(target = "transId",source = "uuid"),
            @Mapping(target = "tradedQuantity",source = "baseQuantity"),
            @Mapping(target = "tradedPrice",source = "price"),
            @Mapping(target = "tradedAmount",source = "quoteQuantity"),

    })
    AmpTransDetailRes transaction2AmpTransDetailRes(TradeTransaction tradeTransaction);

}
