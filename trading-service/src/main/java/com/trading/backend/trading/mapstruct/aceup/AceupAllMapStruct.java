package com.google.backend.trading.mapstruct.aceup;

import com.google.backend.trading.dao.model.TradePositionLimitUser;
import com.google.backend.trading.dao.model.TradeUserBookingList;
import com.google.backend.trading.dao.model.TradeUserSystemSetting;
import com.google.backend.trading.model.common.model.aceup.api.BookingListAddReq;
import com.google.backend.trading.model.common.model.aceup.api.BookingListRes;
import com.google.backend.trading.model.common.model.aceup.api.LiquidListAddReq;
import com.google.backend.trading.model.common.model.aceup.api.LiquidListRes;
import com.google.backend.trading.model.common.model.aceup.api.PositionLimitAddReq;
import com.google.backend.trading.model.common.model.aceup.api.PositionLimitRes;
import com.google.backend.trading.model.common.model.aceup.api.PositionLimitUpdateReq;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * @author trading
 */
@Mapper(componentModel = "spring")
public interface AceupAllMapStruct {


	/**
	 * BookingListAddReq to TradeUserBookingList
	 *
	 * @param req
	 * @return
	 */
	TradeUserBookingList bookingListAddReq2TradeUserBookingList(BookingListAddReq req);

	BookingListRes tradeUserBookingList2BookingListRes(TradeUserBookingList list);

	List<BookingListRes> tradeUserBookingList2BookingListRes(List<TradeUserBookingList> lists);


	TradeUserSystemSetting liquidListAddReq2TradeTradeUserSystemSetting(LiquidListAddReq req);

	LiquidListRes tradeUserSystemSetting2LiquidListRes(TradeUserSystemSetting setting);

	List<LiquidListRes> tradeUserSystemSetting2LiquidListRes(List<TradeUserSystemSetting> settings);


	TradePositionLimitUser positionLimitAddReq2TradePositionLimitUser(PositionLimitAddReq req);

	TradePositionLimitUser positionLimitUpdateReq2TradePositionLimitUser(PositionLimitUpdateReq req);

	PositionLimitRes tradePositionLimitUser2PositionLimitRes(TradePositionLimitUser limitUser);

	List<PositionLimitRes> tradePositionLimitUser2PositionLimitRes(List<TradePositionLimitUser> limitUsers);
}