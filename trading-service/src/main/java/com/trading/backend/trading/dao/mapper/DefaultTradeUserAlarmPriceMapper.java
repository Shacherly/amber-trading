package com.google.backend.trading.dao.mapper;

import com.google.backend.trading.dao.model.TradeUserAlarmPrice;
import com.google.backend.trading.dao.model.TradeUserAlarmPriceExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface DefaultTradeUserAlarmPriceMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table trade_user_alarm_price
     *
     * @mbg.generated
     */
    long countByExample(TradeUserAlarmPriceExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table trade_user_alarm_price
     *
     * @mbg.generated
     */
    int deleteByExample(TradeUserAlarmPriceExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table trade_user_alarm_price
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table trade_user_alarm_price
     *
     * @mbg.generated
     */
    int insert(TradeUserAlarmPrice record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table trade_user_alarm_price
     *
     * @mbg.generated
     */
    int insertSelective(TradeUserAlarmPrice record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table trade_user_alarm_price
     *
     * @mbg.generated
     */
    List<TradeUserAlarmPrice> selectByExample(TradeUserAlarmPriceExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table trade_user_alarm_price
     *
     * @mbg.generated
     */
    TradeUserAlarmPrice selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table trade_user_alarm_price
     *
     * @mbg.generated
     */
    int updateByExampleSelective(@Param("record") TradeUserAlarmPrice record, @Param("example") TradeUserAlarmPriceExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table trade_user_alarm_price
     *
     * @mbg.generated
     */
    int updateByExample(@Param("record") TradeUserAlarmPrice record, @Param("example") TradeUserAlarmPriceExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table trade_user_alarm_price
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(TradeUserAlarmPrice record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table trade_user_alarm_price
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(TradeUserAlarmPrice record);
}