package com.google.backend.trading.mapstruct.margin;

/**
 * TradeMarginOrderMap 转换工具
 * @author adam.wang
 * @date 2021/10/1 16:23
 */

import com.google.backend.trading.dao.model.TradeMarginOrder;
import com.google.backend.trading.dao.model.TradePosition;
import com.google.backend.trading.dao.model.TradePositionFundingCost;
import com.google.backend.trading.dao.model.TradeTransaction;
import com.google.backend.trading.model.internal.aceup.AceUpFundingCostRes;
import com.google.backend.trading.model.internal.aceup.AceUpMarginPositionRes;
import com.google.backend.trading.model.internal.aceup.AceUpMarginRes;
import com.google.backend.trading.model.internal.aceup.AceUpMarginTransRes;
import com.google.backend.trading.model.internal.amp.AmpMarginRes;
import com.google.backend.trading.model.internal.amp.PositionFlowDetailRes;
import com.google.backend.trading.model.margin.api.ActiveOrderRes;
import com.google.backend.trading.model.margin.api.MarginOrderInfoRes;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel="spring")
public interface OrderMapStruct {
    /**
     * entity转换为MarginOrderInfoRes
     * @param tradeMarginOrder
     * @return
     */
    @Mappings({
            @Mapping(target="orderId",source="uuid"),
            @Mapping(target="status",expression = "java(com.google.backend.trading.model.trade.OrderStatus.getFeignStatus(tradeMarginOrder.getStatus()).getCode())"),
    })
    MarginOrderInfoRes tradeMarginOrder2marginOrderInfoRes(TradeMarginOrder tradeMarginOrder);

    /**
     * entity转换为MarginOrderInfoRes
     * @param tradeMarginOrder
     * @return
     */
    @Mappings({
            @Mapping(target="status",expression = "java(com.google.backend.trading.model.trade.OrderStatus.getFeignStatus(tradeMarginOrder.getStatus()).getCode())"),
            @Mapping(target = "orderId",source = "uuid")
    })
    ActiveOrderRes tradeMarginOrder2activeOrderRes(TradeMarginOrder tradeMarginOrder);

    /**
     * list entity转换为ActiveOrderRes
     * @param tradeMarginOrders
     * @return
     */

    List<ActiveOrderRes> tradeMarginOrders2activeOrderRes(List<TradeMarginOrder> tradeMarginOrders);

    /**
     * List entity转换为MarginOrderInfoRes
     * @param tradeMarginOrders
     * @return
     */
    List<MarginOrderInfoRes> tradeMarginOrders2marginOrderInfoRes(List<TradeMarginOrder> tradeMarginOrders);


    /**
     * tradeMarginOrder2PositionFlowDetailReq
     * @param tradeMarginOrder
     * @return
     */
    @Mappings({
            @Mapping(target="orderId",source="uuid"),
    })
    PositionFlowDetailRes tradeMarginOrder2PositionFlowDetailRes(TradeMarginOrder tradeMarginOrder);

    /**
     * list tradeMarginOrders2PositionFlowDetailRes
     * @param tradeMarginOrders
     * @return
     */
    List<PositionFlowDetailRes> tradeMarginOrders2PositionFlowDetailRes(List<TradeMarginOrder> tradeMarginOrders);


    /**
     * tradeMarginOrder2AmpMarginRes
     * @param tradeMarginOrder
     * @return
     */
    @Mappings({
            @Mapping(target="orderId",source="uuid"),
            @Mapping(target="filledQuantity",source="quantityFilled"),
            @Mapping(target = "status", expression = "java(com.google.backend.trading.model.trade.AmpOrderStatus.getFeignStatus(tradeMarginOrder.getStatus()).getCode())"),
    })
    AmpMarginRes tradeMarginOrder2AmpMarginRes(TradeMarginOrder tradeMarginOrder);

    /**
     * tradeMarginOrders2AmpMarginRes
     *
     * @param tradeMarginOrders
     * @return
     */
    List<AmpMarginRes> tradeMarginOrders2AmpMarginRes(List<TradeMarginOrder> tradeMarginOrders);

    /**
     * tradeMarginOrder2AceUpMarginRes
     *
     * @param tradeMarginOrder
     * @return
     */
    @Mappings({
            @Mapping(target = "orderId", source = "uuid"),
            @Mapping(target = "status", expression = "java(com.google.backend.trading.model.trade.AmpOrderStatus.getFeignStatus(tradeMarginOrder.getStatus()).getCode())"),
    })
    AceUpMarginRes tradeMarginOrder2AceUpMarginRes(TradeMarginOrder tradeMarginOrder);

    List<AceUpMarginRes> tradeMarginOrders2AceUpMarginRes(List<TradeMarginOrder> tradeMarginOrders);

    /**
     * tradeMarginOrder2AceUpMarginTransRes
     *
     * @param transaction
     * @return
     */
    @Mappings({
            @Mapping(target = "transId", source = "uuid"),
            @Mapping(target = "status", expression =
                    "java(java.util.Objects.equals(transaction.getAssetStatus(), \"COMPLETED\") &&" +
                            "java.util.Objects.equals(transaction.getPdtStatus(), \"COMPLETED\")?\"COMPLETED\":\"FAILED\")"),})
    AceUpMarginTransRes tradeMarginOrder2AceUpMarginTransRes(TradeTransaction transaction);

    List<AceUpMarginTransRes> tradeTransactions2AceUpMarginTransRes(List<TradeTransaction> transactionList);

    /**
     * tradePositions2AceUpMarginPositionRes
     *
     * @param tradePosition
     * @return
     */
    @Mappings({
            @Mapping(target = "positionId", source = "uuid")})
    AceUpMarginPositionRes tradePositions2AceUpMarginPositionRes(TradePosition tradePosition);

    List<AceUpMarginPositionRes> tradePositions2AceUpMarginPositionRes(List<TradePosition> list);

    List<AceUpFundingCostRes> tradePositionFundingCosts2AceUpFundingCostRes(List<TradePositionFundingCost> list);
}
