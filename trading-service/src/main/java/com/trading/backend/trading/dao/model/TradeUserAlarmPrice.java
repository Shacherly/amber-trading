package com.google.backend.trading.dao.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Database Table Remarks:
 *   用户币种预警表
 *
 * This class was generated by MyBatis Generator.
 * This class corresponds to the database table trade_user_alarm_price
 */
public class TradeUserAlarmPrice implements Serializable {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column trade_user_alarm_price.id
     *
     * @mbg.generated
     */
    private Long id;

    /**
     * Database Column Remarks:
     *   用户id
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column trade_user_alarm_price.uid
     *
     * @mbg.generated
     */
    private String uid;

    /**
     * Database Column Remarks:
     *   币对
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column trade_user_alarm_price.symbol
     *
     * @mbg.generated
     */
    private String symbol;

    /**
     * Database Column Remarks:
     *   预警价格
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column trade_user_alarm_price.alarm_price
     *
     * @mbg.generated
     */
    private BigDecimal alarmPrice;

    /**
     * Database Column Remarks:
     *   预警方向 (>)大于价格报警 (<)小于价格报警
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column trade_user_alarm_price.alarm_compare
     *
     * @mbg.generated
     */
    private String alarmCompare;

    /**
     * Database Column Remarks:
     *   创建时间
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column trade_user_alarm_price.ctime
     *
     * @mbg.generated
     */
    private Date ctime;

    /**
     * Database Column Remarks:
     *   更新时间
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column trade_user_alarm_price.mtime
     *
     * @mbg.generated
     */
    private Date mtime;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table trade_user_alarm_price
     *
     * @mbg.generated
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column trade_user_alarm_price.id
     *
     * @return the value of trade_user_alarm_price.id
     *
     * @mbg.generated
     */
    public Long getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column trade_user_alarm_price.id
     *
     * @param id the value for trade_user_alarm_price.id
     *
     * @mbg.generated
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column trade_user_alarm_price.uid
     *
     * @return the value of trade_user_alarm_price.uid
     *
     * @mbg.generated
     */
    public String getUid() {
        return uid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column trade_user_alarm_price.uid
     *
     * @param uid the value for trade_user_alarm_price.uid
     *
     * @mbg.generated
     */
    public void setUid(String uid) {
        this.uid = uid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column trade_user_alarm_price.symbol
     *
     * @return the value of trade_user_alarm_price.symbol
     *
     * @mbg.generated
     */
    public String getSymbol() {
        return symbol;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column trade_user_alarm_price.symbol
     *
     * @param symbol the value for trade_user_alarm_price.symbol
     *
     * @mbg.generated
     */
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column trade_user_alarm_price.alarm_price
     *
     * @return the value of trade_user_alarm_price.alarm_price
     *
     * @mbg.generated
     */
    public BigDecimal getAlarmPrice() {
        return alarmPrice;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column trade_user_alarm_price.alarm_price
     *
     * @param alarmPrice the value for trade_user_alarm_price.alarm_price
     *
     * @mbg.generated
     */
    public void setAlarmPrice(BigDecimal alarmPrice) {
        this.alarmPrice = alarmPrice;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column trade_user_alarm_price.alarm_compare
     *
     * @return the value of trade_user_alarm_price.alarm_compare
     *
     * @mbg.generated
     */
    public String getAlarmCompare() {
        return alarmCompare;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column trade_user_alarm_price.alarm_compare
     *
     * @param alarmCompare the value for trade_user_alarm_price.alarm_compare
     *
     * @mbg.generated
     */
    public void setAlarmCompare(String alarmCompare) {
        this.alarmCompare = alarmCompare;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column trade_user_alarm_price.ctime
     *
     * @return the value of trade_user_alarm_price.ctime
     *
     * @mbg.generated
     */
    public Date getCtime() {
        return ctime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column trade_user_alarm_price.ctime
     *
     * @param ctime the value for trade_user_alarm_price.ctime
     *
     * @mbg.generated
     */
    public void setCtime(Date ctime) {
        this.ctime = ctime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column trade_user_alarm_price.mtime
     *
     * @return the value of trade_user_alarm_price.mtime
     *
     * @mbg.generated
     */
    public Date getMtime() {
        return mtime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column trade_user_alarm_price.mtime
     *
     * @param mtime the value for trade_user_alarm_price.mtime
     *
     * @mbg.generated
     */
    public void setMtime(Date mtime) {
        this.mtime = mtime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table trade_user_alarm_price
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
        sb.append(", uid=").append(uid);
        sb.append(", symbol=").append(symbol);
        sb.append(", alarmPrice=").append(alarmPrice);
        sb.append(", alarmCompare=").append(alarmCompare);
        sb.append(", ctime=").append(ctime);
        sb.append(", mtime=").append(mtime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}