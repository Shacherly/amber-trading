package com.google.backend.trading.mapstruct.margin;

import com.google.backend.trading.dao.model.TradeMarginOrderModification;
import com.google.backend.trading.model.margin.api.MarginOrderModificationVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * @author adam.wang
 * @date 2021/10/1 18:47
 */
@Mapper(componentModel="spring")
public interface OrderModificationMapStruct {

    /**
     * tradeMarginOrderModification2MarginOrderModificationVo
     * @param tradeMarginOrderModification
     * @return
     */
    MarginOrderModificationVo orderModification2OrderModificationVo(TradeMarginOrderModification tradeMarginOrderModification);

    /**
     * list tradeMarginOrderModification2MarginOrderModificationVo
     * @param tradeMarginOrderModifications
     * @return
     */
    List<MarginOrderModificationVo> orderModifications2OrderModificationVos(List<TradeMarginOrderModification> tradeMarginOrderModifications);

}
