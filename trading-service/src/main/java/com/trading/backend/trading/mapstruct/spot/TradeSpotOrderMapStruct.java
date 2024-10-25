package com.google.backend.trading.mapstruct.spot;

import com.google.backend.trading.dao.model.TradeSpotOrder;
import com.google.backend.trading.dao.model.TradeTransaction;
import com.google.backend.trading.model.common.model.riskcontrol.OrderRes;
import com.google.backend.trading.model.internal.aceup.AceUpSpotRes;
import com.google.backend.trading.model.internal.aceup.AceUpSpotTransRes;
import com.google.backend.trading.model.internal.amp.AmpSpotRes;
import com.google.backend.trading.model.spot.api.SpotOrderInfoRes;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

/**
 * @author adam.wang
 * @date 2021/10/2 20:01
 */
@Mapper(componentModel="spring")
public interface TradeSpotOrderMapStruct {

    /**
     *  tradeSpotOrder2OrderRes
     * @param tradeSpotOrder
     * @return
     */
    @Mappings({
            @Mapping(target = "orderId", source = "id"),
            @Mapping(target = "size", expression =
                    "java(com.google.backend.trading.mapstruct.spot.ExpressionUtil.subtract(" +
                            "tradeSpotOrder.getQuantity(),tradeSpotOrder.getQuantityFilled()))"),
    })
    OrderRes tradeSpotOrder2OrderRes(TradeSpotOrder tradeSpotOrder);

    /**
     *  list tradeSpotOrders2OrderRes
     * @param tradeSpotOrders
     * @return
     */
    List<OrderRes> tradeSpotOrders2OrderRes(List<TradeSpotOrder> tradeSpotOrders);


    /**
     *  tradeSpotOrder2SpotOrderInfoRes
     * @param tradeSpotOrder
     * @return
     */
    @Mappings({
            @Mapping(target = "orderId", source = "uuid"),
            @Mapping(target="status",expression =
                    "java(com.google.backend.trading.model.trade.OrderStatus.getFeignStatus(" +
                            "tradeSpotOrder.getStatus()).getCode())"),
            @Mapping(target="filledQuantity",source = "quantityFilled")
    })
    SpotOrderInfoRes tradeSpotOrder2SpotOrderInfoRes(TradeSpotOrder tradeSpotOrder);

    /**
     *  list tradeSpotOrders2SpotOrderInfoRes
     * @param tradeSpotOrder
     * @return
     */
    List<SpotOrderInfoRes> tradeSpotOrders2SpotOrderInfoRes(List<TradeSpotOrder> tradeSpotOrder);

    /**
     *
     * @param tradeSpotOrder
     * @return
     */
    @Mappings({
            @Mapping(target = "orderId", source = "uuid"),
            @Mapping(target = "filledQuantity", expression = "java(tradeSpotOrder.getIsQuote()?tradeSpotOrder.getAmountFilled():tradeSpotOrder.getQuantityFilled())"),
            @Mapping(target = "status", expression =
                    "java(com.google.backend.trading.model.trade.AmpOrderStatus.getFeignStatus(" +
                            "tradeSpotOrder.getStatus()).getCode())"),
    })
    AmpSpotRes tradeSpotOrder2AmpSpotRes(TradeSpotOrder tradeSpotOrder);

    /**
     * list tradeSpotOrders2AmpSpotResList
     *
     * @param tradeSpotOrders
     * @return
     */
    List<AmpSpotRes> tradeSpotOrders2AmpSpotResList(List<TradeSpotOrder> tradeSpotOrders);


    /**
     * @param tradeSpotOrder
     * @return
     */
    @Mappings({
            @Mapping(target = "orderId", source = "uuid"),
            @Mapping(target = "status", expression =
                    "java(com.google.backend.trading.model.trade.AmpOrderStatus.getFeignStatus(" +
                            "tradeSpotOrder.getStatus()).getCode())"),
    })
    AceUpSpotRes tradeSpotOrder2AceUpSpotRes(TradeSpotOrder tradeSpotOrder);

    /**
     * @param tradeSpotOrders
     * @return
     */
    List<AceUpSpotRes> tradeSpotOrders2AceUpSpotResList(List<TradeSpotOrder> tradeSpotOrders);

    /**
     * @param transaction
     * @return
     */
    @Mappings({
            @Mapping(target = "transId", source = "uuid"),
            @Mapping(target = "status", expression =
                    "java(java.util.Objects.equals(transaction.getAssetStatus(), \"COMPLETED\") &&" +
                            "java.util.Objects.equals(transaction.getPdtStatus(), \"COMPLETED\")?\"COMPLETED\":\"FAILED\")"),
    })
    AceUpSpotTransRes tradeTransaction2AceUpSpotTransRes(TradeTransaction transaction);

    List<AceUpSpotTransRes> tradeTransactions2AceUpSpotTransResList(List<TradeTransaction> tradeTransactionList);
}
