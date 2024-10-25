package com.google.backend.trading.mapstruct.swap;

import com.google.backend.trading.dao.model.TradeSwapOrder;
import com.google.backend.trading.model.internal.aceup.AceUpSwapRes;
import com.google.backend.trading.model.internal.amp.AmpSwapRes;
import com.google.backend.trading.model.swap.api.AipSwapOrderRes;
import com.google.backend.trading.model.swap.api.SwapOrderLiteRes;
import com.google.backend.trading.model.swap.api.SwapOrderRes;
import com.google.backend.trading.model.swap.api.SwapPriceReq;
import com.google.backend.trading.model.swap.dto.AipSwapOrderPlaceReqDTO;
import com.google.backend.trading.model.swap.dto.SwapOrderPlace;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

/**
 * swap订单数据类型的转化
 *
 * @author savion.chen
 * @date 2021/10/5 15:37
 */
@Mapper(componentModel="spring")
public interface TradeSwapOrderMapStruct {

    /**
     *  list tradeSpotOrders2OrderRes
     * @param order
     * @return
     */
    @Mappings({
            @Mapping(target = "orderId",source = "uuid"),
            @Mapping(target = "price",source = "dealPrice"),
            @Mapping(target="status",expression =
                    "java(com.google.backend.trading.model.trade.OrderStatus.getFeignStatus(order.getStatus()).getCode())"),
            @Mapping(target = "ctime",expression =
                    "java(com.google.backend.trading.util.DateUtil.getTime(order.getCtime()))"),
    })
    SwapOrderRes tradeSwapOrder2OrderRes(TradeSwapOrder order);


    /**
     * list tradeSwapOrder2OrderRes
     *
     * @param orderList
     * @return
     */
    List<SwapOrderRes> tradeSwapOrder2OrderRes(List<TradeSwapOrder> orderList);

    List<AmpSwapRes> tradeSwapOrder2AmpSwapRes(List<TradeSwapOrder> tradeSwapOrders);

    @Mappings({
            @Mapping(target = "orderId", source = "uuid"),
            @Mapping(target = "price", source = "dealPrice"),
            @Mapping(target = "status", expression =
                    "java(com.google.backend.trading.model.trade.OrderStatus.getFeignStatus(order.getStatus()).getCode())"),
            @Mapping(target = "ctime", expression =
                    "java(com.google.backend.trading.util.DateUtil.getTime(order.getCtime()))"),
    })
    SwapOrderLiteRes tradeSwapOrder2OrderLiteRes(TradeSwapOrder order);

    List<SwapOrderLiteRes> tradeSwapOrder2OrderLiteRes(List<TradeSwapOrder> orderList);

    SwapPriceReq aipSwapPriceReq2SwapPriceReq(AipSwapOrderPlaceReqDTO aipSwapOrderPlaceReqDTO);

    @Mappings({
            @Mapping(target = "orderId", source = "aipSwapId"),
            @Mapping(target = "mode", expression = "java(com.google.backend.trading.model.swap.SwapType.getByName(aipSwapOrderPlaceReqDTO.getMode()))"),
    })
    SwapOrderPlace aipSwapOrderPlaceReq2SwapOrderPlace(AipSwapOrderPlaceReqDTO aipSwapOrderPlaceReqDTO);

    @Mappings({
            @Mapping(target = "orderId", source = "uuid"),
            @Mapping(target = "price", source = "dealPrice"),
            @Mapping(target = "status", expression =
                    "java(com.google.backend.trading.model.trade.OrderStatus.getFeignStatus(order.getStatus()).getCode())"),
            @Mapping(target = "ctime", expression =
                    "java(com.google.backend.trading.util.DateUtil.getTime(order.getCtime()))"),
    })
    AipSwapOrderRes tradeSwapOrder2AipSwapOrderRes(TradeSwapOrder order);

    List<AceUpSwapRes> tradeSwapOrder2AceUpSwapRes(List<TradeSwapOrder> tradeSwapOrders);

    @Mappings({
            @Mapping(target = "orderId", source = "uuid"),
            @Mapping(target = "status", expression =
                    "java(com.google.backend.trading.model.trade.OrderStatus.getFeignStatus(tradeSwapOrder.getStatus()).getCode())"),
    })
    AceUpSwapRes tradeSwapOrder2AceUpSwapRes(TradeSwapOrder tradeSwapOrder);

}
