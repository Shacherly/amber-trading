package com.google.backend.trading.mapstruct.margin;

import com.google.backend.trading.dao.model.TradePositionFundingCost;
import com.google.backend.trading.model.internal.amp.AmpPositionFundRes;
import com.google.backend.trading.model.margin.api.PositionFundingCostVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

/**
 * @author adam.wang
 * @date 2021/10/2 15:18
 */
@Mapper(componentModel="spring")
public interface PositionFundingCostMapStruct {

    /**
     * positionFundingCost2Vo
     * @param positionFundingCost
     * @return
     */
    @Mappings({
            @Mapping(target = "time",source ="mtime"),
            @Mapping(target = "amount",source = "fundingCost"),
    })
    PositionFundingCostVo positionFundingCost2Vo(TradePositionFundingCost positionFundingCost);

    /**
     * list positionsFundingCost2Vos
     * @param positionFundingCosts
     * @return
     */
    List<PositionFundingCostVo> positionsFundingCost2Vos(List<TradePositionFundingCost> positionFundingCosts);


    @Mappings({
            @Mapping(target = "positionSize",source ="quantity"),
            @Mapping(target = "settlePrice",source = "price"),
    })
    AmpPositionFundRes positionFundingCost2AmpPositionFundRes(TradePositionFundingCost positionFundingCost);


    List<AmpPositionFundRes> positionsFundingCost2AmpPositionFundRes(List<TradePositionFundingCost> positionFundingCosts);
}
