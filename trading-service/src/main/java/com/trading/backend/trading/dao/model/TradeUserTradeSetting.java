package com.google.backend.trading.dao.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Database Table Remarks:
 *   用户交易设置
 *
 * This class was generated by MyBatis Generator.
 * This class corresponds to the database table trade_user_trade_setting
 */
public class TradeUserTradeSetting implements Serializable {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column trade_user_trade_setting.id
     *
     * @mbg.generated
     */
    private Long id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column trade_user_trade_setting.uid
     *
     * @mbg.generated
     */
    private String uid;

    /**
     * Database Column Remarks:
     *   杠杆倍数
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column trade_user_trade_setting.leverage
     *
     * @mbg.generated
     */
    private BigDecimal leverage;

    /**
     * Database Column Remarks:
     *   全仓止损
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column trade_user_trade_setting.max_loss
     *
     * @mbg.generated
     */
    private BigDecimal maxLoss;

    /**
     * Database Column Remarks:
     *   全仓止盈
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column trade_user_trade_setting.take_profit
     *
     * @mbg.generated
     */
    private BigDecimal takeProfit;

    /**
     * Database Column Remarks:
     *   理财质押
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column trade_user_trade_setting.earn_pledge
     *
     * @mbg.generated
     */
    private Boolean earnPledge;

    /**
     * Database Column Remarks:
     *   默认货币，自动转换后，浮动盈亏和负余额资金费率会按照默认货币转换后结算
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column trade_user_trade_setting.default_coin
     *
     * @mbg.generated
     */
    private String defaultCoin;

    /**
     * Database Column Remarks:
     *   结算时区
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column trade_user_trade_setting.settle_time_zone_id
     *
     * @mbg.generated
     */
    private String settleTimeZoneId;

    /**
     * Database Column Remarks:
     *   自动交割
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column trade_user_trade_setting.auto_settle
     *
     * @mbg.generated
     */
    private Boolean autoSettle;

    /**
     * Database Column Remarks:
     *   自动平负余额
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column trade_user_trade_setting.auto_fix_negative
     *
     * @mbg.generated
     */
    private Boolean autoFixNegative;

    /**
     * Database Column Remarks:
     *   清算理财资产
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column trade_user_trade_setting.liquid_earn
     *
     * @mbg.generated
     */
    private Boolean liquidEarn;

    /**
     * Database Column Remarks:
     *   邮件通知
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column trade_user_trade_setting.email_notification
     *
     * @mbg.generated
     */
    private Boolean emailNotification;

    /**
     * Database Column Remarks:
     *   订单二次确认
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column trade_user_trade_setting.reconfirm_order
     *
     * @mbg.generated
     */
    private Boolean reconfirmOrder;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column trade_user_trade_setting.ctime
     *
     * @mbg.generated
     */
    private Date ctime;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column trade_user_trade_setting.mtime
     *
     * @mbg.generated
     */
    private Date mtime;

    /**
     * Database Column Remarks:
     *   双击下单
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column trade_user_trade_setting.double_click_place_order
     *
     * @mbg.generated
     */
    private Boolean doubleClickPlaceOrder;

    /**
     * Database Column Remarks:
     *   自动转换
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column trade_user_trade_setting.auto_convert
     *
     * @mbg.generated
     */
    private Boolean autoConvert;

    /**
     * Database Column Remarks:
     *   settle_time_zone_id生效的时间点，未到时间时结算使用last_settle_time_zone_id
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column trade_user_trade_setting.settle_time_next_effective_time
     *
     * @mbg.generated
     */
    private Date settleTimeNextEffectiveTime;

    /**
     * Database Column Remarks:
     *   上一次待生效的时区，配合settle_time_next_effective_time使用
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column trade_user_trade_setting.last_settle_time_zone_id
     *
     * @mbg.generated
     */
    private String lastSettleTimeZoneId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table trade_user_trade_setting
     *
     * @mbg.generated
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column trade_user_trade_setting.id
     *
     * @return the value of trade_user_trade_setting.id
     *
     * @mbg.generated
     */
    public Long getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column trade_user_trade_setting.id
     *
     * @param id the value for trade_user_trade_setting.id
     *
     * @mbg.generated
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column trade_user_trade_setting.uid
     *
     * @return the value of trade_user_trade_setting.uid
     *
     * @mbg.generated
     */
    public String getUid() {
        return uid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column trade_user_trade_setting.uid
     *
     * @param uid the value for trade_user_trade_setting.uid
     *
     * @mbg.generated
     */
    public void setUid(String uid) {
        this.uid = uid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column trade_user_trade_setting.leverage
     *
     * @return the value of trade_user_trade_setting.leverage
     *
     * @mbg.generated
     */
    public BigDecimal getLeverage() {
        return leverage;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column trade_user_trade_setting.leverage
     *
     * @param leverage the value for trade_user_trade_setting.leverage
     *
     * @mbg.generated
     */
    public void setLeverage(BigDecimal leverage) {
        this.leverage = leverage;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column trade_user_trade_setting.max_loss
     *
     * @return the value of trade_user_trade_setting.max_loss
     *
     * @mbg.generated
     */
    public BigDecimal getMaxLoss() {
        return maxLoss;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column trade_user_trade_setting.max_loss
     *
     * @param maxLoss the value for trade_user_trade_setting.max_loss
     *
     * @mbg.generated
     */
    public void setMaxLoss(BigDecimal maxLoss) {
        this.maxLoss = maxLoss;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column trade_user_trade_setting.take_profit
     *
     * @return the value of trade_user_trade_setting.take_profit
     *
     * @mbg.generated
     */
    public BigDecimal getTakeProfit() {
        return takeProfit;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column trade_user_trade_setting.take_profit
     *
     * @param takeProfit the value for trade_user_trade_setting.take_profit
     *
     * @mbg.generated
     */
    public void setTakeProfit(BigDecimal takeProfit) {
        this.takeProfit = takeProfit;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column trade_user_trade_setting.earn_pledge
     *
     * @return the value of trade_user_trade_setting.earn_pledge
     *
     * @mbg.generated
     */
    public Boolean getEarnPledge() {
        return earnPledge;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column trade_user_trade_setting.earn_pledge
     *
     * @param earnPledge the value for trade_user_trade_setting.earn_pledge
     *
     * @mbg.generated
     */
    public void setEarnPledge(Boolean earnPledge) {
        this.earnPledge = earnPledge;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column trade_user_trade_setting.default_coin
     *
     * @return the value of trade_user_trade_setting.default_coin
     *
     * @mbg.generated
     */
    public String getDefaultCoin() {
        return defaultCoin;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column trade_user_trade_setting.default_coin
     *
     * @param defaultCoin the value for trade_user_trade_setting.default_coin
     *
     * @mbg.generated
     */
    public void setDefaultCoin(String defaultCoin) {
        this.defaultCoin = defaultCoin;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column trade_user_trade_setting.settle_time_zone_id
     *
     * @return the value of trade_user_trade_setting.settle_time_zone_id
     *
     * @mbg.generated
     */
    public String getSettleTimeZoneId() {
        return settleTimeZoneId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column trade_user_trade_setting.settle_time_zone_id
     *
     * @param settleTimeZoneId the value for trade_user_trade_setting.settle_time_zone_id
     *
     * @mbg.generated
     */
    public void setSettleTimeZoneId(String settleTimeZoneId) {
        this.settleTimeZoneId = settleTimeZoneId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column trade_user_trade_setting.auto_settle
     *
     * @return the value of trade_user_trade_setting.auto_settle
     *
     * @mbg.generated
     */
    public Boolean getAutoSettle() {
        return autoSettle;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column trade_user_trade_setting.auto_settle
     *
     * @param autoSettle the value for trade_user_trade_setting.auto_settle
     *
     * @mbg.generated
     */
    public void setAutoSettle(Boolean autoSettle) {
        this.autoSettle = autoSettle;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column trade_user_trade_setting.auto_fix_negative
     *
     * @return the value of trade_user_trade_setting.auto_fix_negative
     *
     * @mbg.generated
     */
    public Boolean getAutoFixNegative() {
        return autoFixNegative;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column trade_user_trade_setting.auto_fix_negative
     *
     * @param autoFixNegative the value for trade_user_trade_setting.auto_fix_negative
     *
     * @mbg.generated
     */
    public void setAutoFixNegative(Boolean autoFixNegative) {
        this.autoFixNegative = autoFixNegative;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column trade_user_trade_setting.liquid_earn
     *
     * @return the value of trade_user_trade_setting.liquid_earn
     *
     * @mbg.generated
     */
    public Boolean getLiquidEarn() {
        return liquidEarn;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column trade_user_trade_setting.liquid_earn
     *
     * @param liquidEarn the value for trade_user_trade_setting.liquid_earn
     *
     * @mbg.generated
     */
    public void setLiquidEarn(Boolean liquidEarn) {
        this.liquidEarn = liquidEarn;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column trade_user_trade_setting.email_notification
     *
     * @return the value of trade_user_trade_setting.email_notification
     *
     * @mbg.generated
     */
    public Boolean getEmailNotification() {
        return emailNotification;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column trade_user_trade_setting.email_notification
     *
     * @param emailNotification the value for trade_user_trade_setting.email_notification
     *
     * @mbg.generated
     */
    public void setEmailNotification(Boolean emailNotification) {
        this.emailNotification = emailNotification;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column trade_user_trade_setting.reconfirm_order
     *
     * @return the value of trade_user_trade_setting.reconfirm_order
     *
     * @mbg.generated
     */
    public Boolean getReconfirmOrder() {
        return reconfirmOrder;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column trade_user_trade_setting.reconfirm_order
     *
     * @param reconfirmOrder the value for trade_user_trade_setting.reconfirm_order
     *
     * @mbg.generated
     */
    public void setReconfirmOrder(Boolean reconfirmOrder) {
        this.reconfirmOrder = reconfirmOrder;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column trade_user_trade_setting.ctime
     *
     * @return the value of trade_user_trade_setting.ctime
     *
     * @mbg.generated
     */
    public Date getCtime() {
        return ctime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column trade_user_trade_setting.ctime
     *
     * @param ctime the value for trade_user_trade_setting.ctime
     *
     * @mbg.generated
     */
    public void setCtime(Date ctime) {
        this.ctime = ctime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column trade_user_trade_setting.mtime
     *
     * @return the value of trade_user_trade_setting.mtime
     *
     * @mbg.generated
     */
    public Date getMtime() {
        return mtime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column trade_user_trade_setting.mtime
     *
     * @param mtime the value for trade_user_trade_setting.mtime
     *
     * @mbg.generated
     */
    public void setMtime(Date mtime) {
        this.mtime = mtime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column trade_user_trade_setting.double_click_place_order
     *
     * @return the value of trade_user_trade_setting.double_click_place_order
     *
     * @mbg.generated
     */
    public Boolean getDoubleClickPlaceOrder() {
        return doubleClickPlaceOrder;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column trade_user_trade_setting.double_click_place_order
     *
     * @param doubleClickPlaceOrder the value for trade_user_trade_setting.double_click_place_order
     *
     * @mbg.generated
     */
    public void setDoubleClickPlaceOrder(Boolean doubleClickPlaceOrder) {
        this.doubleClickPlaceOrder = doubleClickPlaceOrder;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column trade_user_trade_setting.auto_convert
     *
     * @return the value of trade_user_trade_setting.auto_convert
     *
     * @mbg.generated
     */
    public Boolean getAutoConvert() {
        return autoConvert;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column trade_user_trade_setting.auto_convert
     *
     * @param autoConvert the value for trade_user_trade_setting.auto_convert
     *
     * @mbg.generated
     */
    public void setAutoConvert(Boolean autoConvert) {
        this.autoConvert = autoConvert;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column trade_user_trade_setting.settle_time_next_effective_time
     *
     * @return the value of trade_user_trade_setting.settle_time_next_effective_time
     *
     * @mbg.generated
     */
    public Date getSettleTimeNextEffectiveTime() {
        return settleTimeNextEffectiveTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column trade_user_trade_setting.settle_time_next_effective_time
     *
     * @param settleTimeNextEffectiveTime the value for trade_user_trade_setting.settle_time_next_effective_time
     *
     * @mbg.generated
     */
    public void setSettleTimeNextEffectiveTime(Date settleTimeNextEffectiveTime) {
        this.settleTimeNextEffectiveTime = settleTimeNextEffectiveTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column trade_user_trade_setting.last_settle_time_zone_id
     *
     * @return the value of trade_user_trade_setting.last_settle_time_zone_id
     *
     * @mbg.generated
     */
    public String getLastSettleTimeZoneId() {
        return lastSettleTimeZoneId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column trade_user_trade_setting.last_settle_time_zone_id
     *
     * @param lastSettleTimeZoneId the value for trade_user_trade_setting.last_settle_time_zone_id
     *
     * @mbg.generated
     */
    public void setLastSettleTimeZoneId(String lastSettleTimeZoneId) {
        this.lastSettleTimeZoneId = lastSettleTimeZoneId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table trade_user_trade_setting
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
        sb.append(", leverage=").append(leverage);
        sb.append(", maxLoss=").append(maxLoss);
        sb.append(", takeProfit=").append(takeProfit);
        sb.append(", earnPledge=").append(earnPledge);
        sb.append(", defaultCoin=").append(defaultCoin);
        sb.append(", settleTimeZoneId=").append(settleTimeZoneId);
        sb.append(", autoSettle=").append(autoSettle);
        sb.append(", autoFixNegative=").append(autoFixNegative);
        sb.append(", liquidEarn=").append(liquidEarn);
        sb.append(", emailNotification=").append(emailNotification);
        sb.append(", reconfirmOrder=").append(reconfirmOrder);
        sb.append(", ctime=").append(ctime);
        sb.append(", mtime=").append(mtime);
        sb.append(", doubleClickPlaceOrder=").append(doubleClickPlaceOrder);
        sb.append(", autoConvert=").append(autoConvert);
        sb.append(", settleTimeNextEffectiveTime=").append(settleTimeNextEffectiveTime);
        sb.append(", lastSettleTimeZoneId=").append(lastSettleTimeZoneId);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}