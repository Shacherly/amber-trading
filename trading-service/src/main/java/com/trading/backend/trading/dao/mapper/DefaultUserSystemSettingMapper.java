package com.google.backend.trading.dao.mapper;

import com.google.backend.trading.dao.model.TradeUserSystemSetting;
import com.google.backend.trading.dao.model.TradeUserSystemSettingExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface DefaultUserSystemSettingMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table trade_user_system_setting
     *
     * @mbg.generated
     */
    long countByExample(TradeUserSystemSettingExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table trade_user_system_setting
     *
     * @mbg.generated
     */
    int deleteByExample(TradeUserSystemSettingExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table trade_user_system_setting
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table trade_user_system_setting
     *
     * @mbg.generated
     */
    int insert(TradeUserSystemSetting record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table trade_user_system_setting
     *
     * @mbg.generated
     */
    int insertSelective(TradeUserSystemSetting record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table trade_user_system_setting
     *
     * @mbg.generated
     */
    List<TradeUserSystemSetting> selectByExample(TradeUserSystemSettingExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table trade_user_system_setting
     *
     * @mbg.generated
     */
    TradeUserSystemSetting selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table trade_user_system_setting
     *
     * @mbg.generated
     */
    int updateByExampleSelective(@Param("record") TradeUserSystemSetting record, @Param("example") TradeUserSystemSettingExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table trade_user_system_setting
     *
     * @mbg.generated
     */
    int updateByExample(@Param("record") TradeUserSystemSetting record, @Param("example") TradeUserSystemSettingExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table trade_user_system_setting
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(TradeUserSystemSetting record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table trade_user_system_setting
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(TradeUserSystemSetting record);
}