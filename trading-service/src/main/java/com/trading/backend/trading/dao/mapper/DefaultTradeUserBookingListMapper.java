package com.google.backend.trading.dao.mapper;

import com.google.backend.trading.dao.model.TradeUserBookingList;
import com.google.backend.trading.dao.model.TradeUserBookingListExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DefaultTradeUserBookingListMapper {
	/**
	 * This method was generated by MyBatis Generator.
	 * This method corresponds to the database table trade_user_booking_list
	 *
	 * @mbg.generated
	 */
	long countByExample(TradeUserBookingListExample example);

	/**
	 * This method was generated by MyBatis Generator.
	 * This method corresponds to the database table trade_user_booking_list
	 *
	 * @mbg.generated
	 */
	int deleteByExample(TradeUserBookingListExample example);

	/**
	 * This method was generated by MyBatis Generator.
	 * This method corresponds to the database table trade_user_booking_list
	 *
	 * @mbg.generated
	 */
	int deleteByPrimaryKey(Long id);

	/**
	 * This method was generated by MyBatis Generator.
	 * This method corresponds to the database table trade_user_booking_list
	 *
	 * @mbg.generated
	 */
	int insert(TradeUserBookingList record);

	/**
	 * This method was generated by MyBatis Generator.
	 * This method corresponds to the database table trade_user_booking_list
	 *
	 * @mbg.generated
	 */
	int insertSelective(TradeUserBookingList record);

	/**
	 * This method was generated by MyBatis Generator.
	 * This method corresponds to the database table trade_user_booking_list
	 *
	 * @mbg.generated
	 */
	List<TradeUserBookingList> selectByExample(TradeUserBookingListExample example);

	/**
	 * This method was generated by MyBatis Generator.
	 * This method corresponds to the database table trade_user_booking_list
	 *
	 * @mbg.generated
	 */
	TradeUserBookingList selectByPrimaryKey(Long id);

	/**
	 * This method was generated by MyBatis Generator.
	 * This method corresponds to the database table trade_user_booking_list
	 *
	 * @mbg.generated
	 */
	int updateByExampleSelective(@Param("record") TradeUserBookingList record, @Param("example") TradeUserBookingListExample example);

	/**
	 * This method was generated by MyBatis Generator.
	 * This method corresponds to the database table trade_user_booking_list
	 *
	 * @mbg.generated
	 */
	int updateByExample(@Param("record") TradeUserBookingList record, @Param("example") TradeUserBookingListExample example);

	/**
	 * This method was generated by MyBatis Generator.
	 * This method corresponds to the database table trade_user_booking_list
	 *
	 * @mbg.generated
	 */
	int updateByPrimaryKeySelective(TradeUserBookingList record);

	/**
	 * This method was generated by MyBatis Generator.
	 * This method corresponds to the database table trade_user_booking_list
	 *
	 * @mbg.generated
	 */
	int updateByPrimaryKey(TradeUserBookingList record);
}