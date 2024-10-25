package com.google.backend.trading.dao.mapper;

import com.google.backend.trading.dao.model.TradeUserPageSetting;
import com.google.backend.trading.dao.model.TradeUserPageSettingExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface DefaultTradeUserPageSettingMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table trade_user_page_setting
     *
     * @mbg.generated
     */
    long countByExample(TradeUserPageSettingExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table trade_user_page_setting
     *
     * @mbg.generated
     */
    int deleteByExample(TradeUserPageSettingExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table trade_user_page_setting
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table trade_user_page_setting
     *
     * @mbg.generated
     */
    int insert(TradeUserPageSetting record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table trade_user_page_setting
     *
     * @mbg.generated
     */
    int insertSelective(TradeUserPageSetting record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table trade_user_page_setting
     *
     * @mbg.generated
     */
    List<TradeUserPageSetting> selectByExample(TradeUserPageSettingExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table trade_user_page_setting
     *
     * @mbg.generated
     */
    TradeUserPageSetting selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table trade_user_page_setting
     *
     * @mbg.generated
     */
    int updateByExampleSelective(@Param("record") TradeUserPageSetting record, @Param("example") TradeUserPageSettingExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table trade_user_page_setting
     *
     * @mbg.generated
     */
    int updateByExample(@Param("record") TradeUserPageSetting record, @Param("example") TradeUserPageSettingExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table trade_user_page_setting
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(TradeUserPageSetting record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table trade_user_page_setting
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(TradeUserPageSetting record);
}