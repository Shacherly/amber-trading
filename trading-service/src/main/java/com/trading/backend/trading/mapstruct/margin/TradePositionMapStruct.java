package com.google.backend.trading.mapstruct.margin;

import com.google.backend.trading.dao.model.TradePosition;
import com.google.backend.trading.model.common.model.riskcontrol.PositionRes;
import com.google.backend.trading.model.common.model.riskcontrol.notice.PositionInfo;
import com.google.backend.trading.model.internal.amp.AmpPositionRes;
import com.google.backend.trading.model.internal.amp.PositionInfoRes;
import com.google.backend.trading.model.margin.api.ActivePositionInfoVo;
import com.google.backend.trading.model.margin.api.HistoryPositionDetailRes;
import com.google.backend.trading.model.margin.api.HistoryPositionInfoVo;
import org.mapstruct.Context;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

import java.util.List;

/**
 * @author adam.wang
 * @date 2021/10/2 11:24
 */
@Mapper(componentModel="spring")
public interface TradePositionMapStruct {

    /**
     * tradePosition2ActivePositionInfoVo
     * @param tradePosition
     * @return
     */

    @Mappings({
            @Mapping(target = "positionId", source = "uuid"),
            @Mapping(target = "openPrice", source = "price"),
            @Mapping(target = "mtime", expression = "java(com.google.backend.trading.util.DateUtil.getTime(tradePosition.getMtime()))"),
            @Mapping(target = "ctime", expression = "java(com.google.backend.trading.util.DateUtil.getTime(tradePosition.getCtime()))"),
    })
    ActivePositionInfoVo tradePosition2ActivePositionInfoVo(TradePosition tradePosition);


    /**
     * tradePositions2ActivePositionInfoVos
     * @param tradePositions
     * @return
     */
    List<ActivePositionInfoVo> tradePositions2ActivePositionInfoVos(List<TradePosition> tradePositions);


    /**
     * tradePosition2PositionRes
     * @param tradePosition
     * @return
	 */
	@Mappings({
			@Mapping(target = "positionId", source = "id"),
			@Mapping(target = "size", source = "quantity"),
			@Mapping(target = "tp", source = "takeProfitPercentage"),
			@Mapping(target = "sl", source = "stopLossPrice"),
	})
	@Named(value = "tradePosition2PositionRes")
	PositionRes tradePosition2PositionRes(TradePosition tradePosition);

	/**
	 * tradePosition2PositionRes
	 *
	 * @param tradePosition
	 * @return
	 */
	@Mappings({
			@Mapping(target = "positionId", source = "id"),
			@Mapping(target = "size", source = "quantity"),
			@Mapping(target = "tp", source = "takeProfitPercentage"),
			@Mapping(target = "sl", source = "stopLossPrice"),
			@Mapping(target = "txnId", expression = "java(transactionLastValue)"),
	})
	@Named(value = "tradePosition2PositionResWithTxnId")
	PositionRes tradePosition2PositionRes(TradePosition tradePosition, @Context String transactionLastValue);


	/**
	 * list tradePositions2PositionRes
	 *
	 * @param tradePositions
	 * @return
	 */
	@IterableMapping(qualifiedByName = "tradePosition2PositionRes")
	List<PositionRes> tradePositions2PositionRes(List<TradePosition> tradePositions);

	/**
	 * list tradePositions2PositionRes
	 *
	 * @param tradePositions
	 * @return
	 */
	@IterableMapping(qualifiedByName = "tradePosition2PositionResWithTxnId")
	List<PositionRes> tradePositions2PositionRes(List<TradePosition> tradePositions, @Context String transactionLastValue);


	/**
	 * TradePosition2HistoryVo
	 *
	 * @param tradePosition
	 * @return
	 */
	@Mapping(target = "positionId", source = "uuid")
	HistoryPositionInfoVo TradePosition2HistoryVo(TradePosition tradePosition);

    /**
     * TradePosition2HistoryVo
     * @param tradePositions
     * @return
     */
    List<HistoryPositionInfoVo> tradePositions2HistoryVos(List<TradePosition> tradePositions);


    /**
     * TradePosition2HistoryDetailVo
     * @param tradePosition
     * @return
     */
    HistoryPositionDetailRes tradePosition2HistoryDetailVo(TradePosition tradePosition);

    /**
     * tradePosition2PositionInfoRes
     * @param tradePosition
     * @return
     */
    @Mappings({
            @Mapping(target = "positionId",source = "uuid"),
            @Mapping(target = "avgPrice",source = "price"),
    })
    PositionInfoRes tradePosition2PositionInfoRes(TradePosition tradePosition);

    /**
     * list tradePositions2PositionInfoRes
     * @param tradePositions
     * @return
     */
    List<PositionInfoRes> tradePositions2PositionInfoRes(List<TradePosition> tradePositions);

    /**
     * tradePosition2PositionInfo
     * @param tradePosition
     * @return
     */
    @Mappings({
            @Mapping(target ="positionId",source="id"),
            @Mapping(target ="size",source="quantity"),
            @Mapping(target ="status",expression="java(com.google.backend.trading.model.margin.PositionStatus.isActive(tradePosition.getStatus())?0:1)"),
    })
    PositionInfo tradePosition2PositionInfo(TradePosition tradePosition);



    @Mappings({
            @Mapping(target = "positionId",source = "uuid"),
    })
    AmpPositionRes tradePosition2AmpPositionRes(TradePosition tradePosition);

    List<AmpPositionRes> tradePositions2AmpPositionRes(List<TradePosition> tradePositions);

}
