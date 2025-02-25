package com.google.backend.trading.dao.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Database Table Remarks:
 *   资金费率表
 *
 * This class was generated by MyBatis Generator.
 * This class corresponds to the database table trade_funding_rate
 */
public class TradeFundingRate implements Serializable {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column trade_funding_rate.id
     *
     * @mbg.generated
     */
    private Long id;

    /**
     * Database Column Remarks:
     *   币种
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column trade_funding_rate.coin
     *
     * @mbg.generated
     */
    private String coin;

    /**
     * Database Column Remarks:
     *   借出费率
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column trade_funding_rate.lend
     *
     * @mbg.generated
     */
    private BigDecimal lend;

    /**
     * Database Column Remarks:
     *   借入费率
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column trade_funding_rate.borrow
     *
     * @mbg.generated
     */
    private BigDecimal borrow;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column trade_funding_rate.time
     *
     * @mbg.generated
     */
    private Date time;

    /**
     * Database Column Remarks:
     *   是否4点存入的？
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column trade_funding_rate.show
     *
     * @mbg.generated
     */
    private Boolean show;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table trade_funding_rate
     *
     * @mbg.generated
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column trade_funding_rate.id
     *
     * @return the value of trade_funding_rate.id
     *
     * @mbg.generated
     */
    public Long getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column trade_funding_rate.id
     *
     * @param id the value for trade_funding_rate.id
     *
     * @mbg.generated
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column trade_funding_rate.coin
     *
     * @return the value of trade_funding_rate.coin
     *
     * @mbg.generated
     */
    public String getCoin() {
        return coin;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column trade_funding_rate.coin
     *
     * @param coin the value for trade_funding_rate.coin
     *
     * @mbg.generated
     */
    public void setCoin(String coin) {
        this.coin = coin;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column trade_funding_rate.lend
     *
     * @return the value of trade_funding_rate.lend
     *
     * @mbg.generated
     */
    public BigDecimal getLend() {
        return lend;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column trade_funding_rate.lend
     *
     * @param lend the value for trade_funding_rate.lend
     *
     * @mbg.generated
     */
    public void setLend(BigDecimal lend) {
        this.lend = lend;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column trade_funding_rate.borrow
     *
     * @return the value of trade_funding_rate.borrow
     *
     * @mbg.generated
     */
    public BigDecimal getBorrow() {
        return borrow;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column trade_funding_rate.borrow
     *
     * @param borrow the value for trade_funding_rate.borrow
     *
     * @mbg.generated
     */
    public void setBorrow(BigDecimal borrow) {
        this.borrow = borrow;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column trade_funding_rate.time
     *
     * @return the value of trade_funding_rate.time
     *
     * @mbg.generated
     */
    public Date getTime() {
        return time;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column trade_funding_rate.time
     *
     * @param time the value for trade_funding_rate.time
     *
     * @mbg.generated
     */
    public void setTime(Date time) {
        this.time = time;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column trade_funding_rate.show
     *
     * @return the value of trade_funding_rate.show
     *
     * @mbg.generated
     */
    public Boolean getShow() {
        return show;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column trade_funding_rate.show
     *
     * @param show the value for trade_funding_rate.show
     *
     * @mbg.generated
     */
    public void setShow(Boolean show) {
        this.show = show;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table trade_funding_rate
     *
     * @mbg.generated
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", coin=").append(coin);
        sb.append(", lend=").append(lend);
        sb.append(", borrow=").append(borrow);
        sb.append(", time=").append(time);
        sb.append(", show=").append(show);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}