package com.google.backend.trading.mapstruct.open.api;

import com.google.backend.trading.dao.model.TradeMarginOrder;
import com.google.backend.trading.dao.model.TradeSpotOrder;
import com.google.backend.trading.model.open.v2.api.OrderInfoVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

/**
 * Open API 业务
 *
 * @author david.chen
 * @date 2022/2/15 11:19
 */
@Mapper(componentModel = "spring")
public interface OpenApiStruct {


    @Mappings({
            @Mapping(target = "orderId", source = "uuid"),
            @Mapping(target = "strategy", expression = "java(order.getStrategy() == null ? com.google.backend.trading.model.trade.TradeStrategy.GTC.getName() : order.getStrategy())"),
            @Mapping(target = "filledQuantity", source = "quantityFilled"),
            @Mapping(target = "mtime", expression = "java(order.getMtime().getTime())"),
            @Mapping(target = "ctime", expression = "java(order.getCtime().getTime())"),
            @Mapping(target = "orderType", expression = "java(com.google.backend.trading.model.trade.OrderType.getSimpleByName(com.google.backend.trading.model.trade.OrderType.getByName(order.getType())))"),
            @Mapping(target = "status", expression = "java(com.google.backend.trading.model.trade.OpenApiOrderStatus.getFeignStatus(order.getStatus()).getCode())"),
    })
    OrderInfoVo tradeSpotOrder2OrderInfoVo(TradeSpotOrder order);

    @Mappings({
            @Mapping(target = "orderId", source = "uuid"),
            @Mapping(target = "strategy", expression = "java(order.getStrategy() == null ? com.google.backend.trading.model.trade.TradeStrategy.GTC.getName() : order.getStrategy())"),
            @Mapping(target = "filledQuantity", source = "quantityFilled"),
            @Mapping(target = "mtime", expression = "java(order.getMtime().getTime())"),
            @Mapping(target = "ctime", expression = "java(order.getCtime().getTime())"),
            @Mapping(target = "orderType", expression = "java(com.google.backend.trading.model.trade.OrderType.getSimpleByName(com.google.backend.trading.model.trade.OrderType.getByName(order.getType())))"),
            @Mapping(target = "status", expression = "java(com.google.backend.trading.model.trade.OpenApiOrderStatus.getFeignStatus(order.getStatus()).getCode())"),
    })
    OrderInfoVo tradeMarginOrder2OrderInfoVo(TradeMarginOrder order);

    List<OrderInfoVo> tradeSpotOrderList2OrderInfoVoList(List<TradeSpotOrder> tradeSpotOrders);

    List<OrderInfoVo> tradeMarginOrderList2OrderInfoVoList(List<TradeMarginOrder> tradeMarginOrders);
}
