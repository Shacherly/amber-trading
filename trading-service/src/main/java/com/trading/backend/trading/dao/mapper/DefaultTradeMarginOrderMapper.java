package com.google.backend.trading.dao.mapper;

import com.google.backend.trading.dao.model.TradeMarginOrder;
import com.google.backend.trading.dao.model.TradeMarginOrderExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface DefaultTradeMarginOrderMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table trade_margin_order
     *
     * @mbg.generated
     */
    long countByExample(TradeMarginOrderExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table trade_margin_order
     *
     * @mbg.generated
     */
    int deleteByExample(TradeMarginOrderExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table trade_margin_order
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table trade_margin_order
     *
     * @mbg.generated
     */
    int insert(TradeMarginOrder record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table trade_margin_order
     *
     * @mbg.generated
     */
    int insertSelective(TradeMarginOrder record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table trade_margin_order
     *
     * @mbg.generated
     */
    List<TradeMarginOrder> selectByExample(TradeMarginOrderExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table trade_margin_order
     *
     * @mbg.generated
     */
    TradeMarginOrder selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table trade_margin_order
     *
     * @mbg.generated
     */
    int updateByExampleSelective(@Param("record") TradeMarginOrder record, @Param("example") TradeMarginOrderExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table trade_margin_order
     *
     * @mbg.generated
     */
    int updateByExample(@Param("record") TradeMarginOrder record, @Param("example") TradeMarginOrderExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table trade_margin_order
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(TradeMarginOrder record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table trade_margin_order
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(TradeMarginOrder record);
}