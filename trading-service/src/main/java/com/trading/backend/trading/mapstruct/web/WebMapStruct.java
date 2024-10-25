package com.google.backend.trading.mapstruct.web;

import com.google.backend.trading.dao.model.TradeMarginOrder;
import com.google.backend.trading.dao.model.TradeMarginOrderModification;
import com.google.backend.trading.dao.model.TradeSpotOrder;
import com.google.backend.trading.dao.model.TradeSpotOrderModification;
import com.google.backend.trading.dao.model.TradeTransaction;
import com.google.backend.trading.model.web.OrderHistoryRes;
import com.google.backend.trading.model.web.OrderInfoRes;
import com.google.backend.trading.model.web.OrderInfoResEvent;
import com.google.backend.trading.model.web.OrderModificationVo;
import com.google.backend.trading.model.web.TransactionInfoRes;
import com.google.backend.trading.model.web.TransactionInfoResEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel="spring")
public interface WebMapStruct {

    @Mapping(target = "orderId", source = "uuid")
    @Mapping(target="status",expression =
            "java(com.google.backend.trading.model.trade.OrderStatus.getFeignStatus(" +
                    "tradeMarginOrder.getStatus()).getCode())")
    @Mapping(target="isSpot",expression ="java(false)")
    @Mapping(target="isQuote",expression ="java(false)")
    OrderInfoRes tradeMarginOrder2OrderRes(TradeMarginOrder tradeMarginOrder);

    @Mapping(target = "orderId", source = "uuid")
    @Mapping(target="status",expression =
            "java(com.google.backend.trading.model.trade.OrderStatus.getFeignStatus(" +
                    "tradeMarginOrder.getStatus()).getCode())")
    @Mapping(target="isSpot",expression ="java(false)")
    @Mapping(target="isQuote",expression ="java(false)")
    OrderInfoResEvent tradeMarginOrder2OrderResEvent(TradeMarginOrder tradeMarginOrder);

    @Mapping(target = "orderId", source = "uuid")
    @Mapping(target="status",expression =
            "java(com.google.backend.trading.model.trade.OrderStatus.getFeignStatus(" +
                    "tradeMarginOrder.getStatus()).getCode())")
    @Mapping(target="isSpot",expression ="java(false)")
    @Mapping(target="isQuote",expression ="java(false)")
    OrderHistoryRes tradeMarginOrder2OrderHistoryRes(TradeMarginOrder tradeMarginOrder);


    @Mapping(target = "orderId", source = "uuid")
    @Mapping(target="status",expression =
            "java(com.google.backend.trading.model.trade.OrderStatus.getFeignStatus(" +
                    "tradeSpotOrder.getStatus()).getCode())")
    @Mapping(target="isSpot",expression ="java(true)")
    OrderInfoResEvent tradeSpotOrder2OrderResEvent(TradeSpotOrder tradeSpotOrder);

    @Mapping(target = "orderId", source = "uuid")
    @Mapping(target="status",expression =
            "java(com.google.backend.trading.model.trade.OrderStatus.getFeignStatus(" +
                    "tradeSpotOrder.getStatus()).getCode())")
    @Mapping(target="isSpot",expression ="java(true)")
    OrderInfoRes tradeSpotOrder2OrderRes(TradeSpotOrder tradeSpotOrder);

    @Mapping(target = "orderId", source = "uuid")
    @Mapping(target="status",expression =
            "java(com.google.backend.trading.model.trade.OrderStatus.getFeignStatus(" +
                    "tradeSpotOrder.getStatus()).getCode())")
    @Mapping(target="isSpot",expression ="java(true)")
    OrderHistoryRes tradeSpotOrder2OrderHistoryRes(TradeSpotOrder tradeSpotOrder);

    @Mappings({
            @Mapping(target = "transactionType",expression = "java(com.google.backend.trading.model.trade.TransactionType.getByName(transaction.getType()).getCode())"),
            @Mapping(target = "optType",expression = "java(com.google.backend.trading.model.trade.OptType.getOptType(transaction))"),
            @Mapping(target = "transactionId",source = "id"),
    })
    TransactionInfoRes tradeTransaction2TransactionRes(TradeTransaction transaction);


    @Mappings({
            @Mapping(target = "transactionType",expression = "java(com.google.backend.trading.model.trade.TransactionType.getByName(transaction.getType()).getCode())"),
            @Mapping(target = "optType",expression = "java(com.google.backend.trading.model.trade.OptType.getOptType(transaction))"),
            @Mapping(target = "transactionId",source = "id"),
    })
    TransactionInfoResEvent tradeTransaction2TransactionResEvent(TradeTransaction transaction);

    List<TransactionInfoRes> tradeTransactions2TransactionRes(List<TradeTransaction> transactions);

    /**
     * tradeMarginOrderModification2Vo
     * @param tradeMarginOrderModification
     * @return
     */
    OrderModificationVo tradeMarginOrderModification2Vo(TradeMarginOrderModification tradeMarginOrderModification);

    /**
     * list tradeMarginOrderModification2Vos
     * @param tradeMarginOrderModifications
     * @return
     */
    List<OrderModificationVo> tradeMarginOrderModification2Vos(List<TradeMarginOrderModification> tradeMarginOrderModifications);

    /**
     * tradeSpotOrderModification2Vo
     * @param tradeMarginOrderModification
     * @return
     */
    OrderModificationVo tradeSpotOrderModification2Vo(TradeSpotOrderModification tradeMarginOrderModification);

    /**
     * list tradeSpotOrderModification2Vos
     * @param tradeMarginOrderModifications
     * @return
     */
    List<OrderModificationVo> tradeSpotOrderModification2Vos(List<TradeSpotOrderModification> tradeMarginOrderModifications);

}
