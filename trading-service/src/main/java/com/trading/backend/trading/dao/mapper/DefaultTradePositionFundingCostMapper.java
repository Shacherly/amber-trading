package com.google.backend.trading.dao.mapper;

import com.google.backend.trading.dao.model.TradePositionFundingCost;
import com.google.backend.trading.dao.model.TradePositionFundingCostExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface DefaultTradePositionFundingCostMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table trade_position_funding_cost
     *
     * @mbg.generated
     */
    long countByExample(TradePositionFundingCostExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table trade_position_funding_cost
     *
     * @mbg.generated
     */
    int deleteByExample(TradePositionFundingCostExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table trade_position_funding_cost
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table trade_position_funding_cost
     *
     * @mbg.generated
     */
    int insert(TradePositionFundingCost record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table trade_position_funding_cost
     *
     * @mbg.generated
     */
    int insertSelective(TradePositionFundingCost record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table trade_position_funding_cost
     *
     * @mbg.generated
     */
    List<TradePositionFundingCost> selectByExample(TradePositionFundingCostExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table trade_position_funding_cost
     *
     * @mbg.generated
     */
    TradePositionFundingCost selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table trade_position_funding_cost
     *
     * @mbg.generated
     */
    int updateByExampleSelective(@Param("record") TradePositionFundingCost record, @Param("example") TradePositionFundingCostExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table trade_position_funding_cost
     *
     * @mbg.generated
     */
    int updateByExample(@Param("record") TradePositionFundingCost record, @Param("example") TradePositionFundingCostExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table trade_position_funding_cost
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(TradePositionFundingCost record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table trade_position_funding_cost
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(TradePositionFundingCost record);
}