package com.google.backend.trading.dao.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Database Table Remarks:
 *   现货杠杆中间表（为了保持兼容web展示用于分页）
 *
 * This class was generated by MyBatis Generator.
 * This class corresponds to the database table trade_spot_margin_middle_order
 */
public class TradeSpotMarginMiddleOrder implements Serializable {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column trade_spot_margin_middle_order.id
     *
     * @mbg.generated
     */
    private Long id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column trade_spot_margin_middle_order.uid
     *
     * @mbg.generated
     */
    private String uid;

    /**
     * Database Column Remarks:
     *   订单id
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column trade_spot_margin_middle_order.order_id
     *
     * @mbg.generated
     */
    private String orderId;

    /**
     * Database Column Remarks:
     *   订单类型   spot   margin
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column trade_spot_margin_middle_order.type
     *
     * @mbg.generated
     */
    private String type;

    /**
     * Database Column Remarks:
     *   币对 BTC_USD
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column trade_spot_margin_middle_order.symbol
     *
     * @mbg.generated
     */
    private String symbol;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column trade_spot_margin_middle_order.direction
     *
     * @mbg.generated
     */
    private String direction;

    /**
     * Database Column Remarks:
     *   订单状态 pre_trigger  pending  executing  locked completed cancelled error_canceled system_cancelling system_canceled
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column trade_spot_margin_middle_order.status
     *
     * @mbg.generated
     */
    private String status;

    /**
     * Database Column Remarks:
     *   订单创建时间
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column trade_spot_margin_middle_order.ctime
     *
     * @mbg.generated
     */
    private Date ctime;

    /**
     * Database Column Remarks:
     *   订单修改时间
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column trade_spot_margin_middle_order.mtime
     *
     * @mbg.generated
     */
    private Date mtime;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table trade_spot_margin_middle_order
     *
     * @mbg.generated
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column trade_spot_margin_middle_order.id
     *
     * @return the value of trade_spot_margin_middle_order.id
     *
     * @mbg.generated
     */
    public Long getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column trade_spot_margin_middle_order.id
     *
     * @param id the value for trade_spot_margin_middle_order.id
     *
     * @mbg.generated
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column trade_spot_margin_middle_order.uid
     *
     * @return the value of trade_spot_margin_middle_order.uid
     *
     * @mbg.generated
     */
    public String getUid() {
        return uid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column trade_spot_margin_middle_order.uid
     *
     * @param uid the value for trade_spot_margin_middle_order.uid
     *
     * @mbg.generated
     */
    public void setUid(String uid) {
        this.uid = uid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column trade_spot_margin_middle_order.order_id
     *
     * @return the value of trade_spot_margin_middle_order.order_id
     *
     * @mbg.generated
     */
    public String getOrderId() {
        return orderId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column trade_spot_margin_middle_order.order_id
     *
     * @param orderId the value for trade_spot_margin_middle_order.order_id
     *
     * @mbg.generated
     */
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column trade_spot_margin_middle_order.type
     *
     * @return the value of trade_spot_margin_middle_order.type
     *
     * @mbg.generated
     */
    public String getType() {
        return type;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column trade_spot_margin_middle_order.type
     *
     * @param type the value for trade_spot_margin_middle_order.type
     *
     * @mbg.generated
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column trade_spot_margin_middle_order.symbol
     *
     * @return the value of trade_spot_margin_middle_order.symbol
     *
     * @mbg.generated
     */
    public String getSymbol() {
        return symbol;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column trade_spot_margin_middle_order.symbol
     *
     * @param symbol the value for trade_spot_margin_middle_order.symbol
     *
     * @mbg.generated
     */
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column trade_spot_margin_middle_order.direction
     *
     * @return the value of trade_spot_margin_middle_order.direction
     *
     * @mbg.generated
     */
    public String getDirection() {
        return direction;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column trade_spot_margin_middle_order.direction
     *
     * @param direction the value for trade_spot_margin_middle_order.direction
     *
     * @mbg.generated
     */
    public void setDirection(String direction) {
        this.direction = direction;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column trade_spot_margin_middle_order.status
     *
     * @return the value of trade_spot_margin_middle_order.status
     *
     * @mbg.generated
     */
    public String getStatus() {
        return status;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column trade_spot_margin_middle_order.status
     *
     * @param status the value for trade_spot_margin_middle_order.status
     *
     * @mbg.generated
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column trade_spot_margin_middle_order.ctime
     *
     * @return the value of trade_spot_margin_middle_order.ctime
     *
     * @mbg.generated
     */
    public Date getCtime() {
        return ctime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column trade_spot_margin_middle_order.ctime
     *
     * @param ctime the value for trade_spot_margin_middle_order.ctime
     *
     * @mbg.generated
     */
    public void setCtime(Date ctime) {
        this.ctime = ctime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column trade_spot_margin_middle_order.mtime
     *
     * @return the value of trade_spot_margin_middle_order.mtime
     *
     * @mbg.generated
     */
    public Date getMtime() {
        return mtime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column trade_spot_margin_middle_order.mtime
     *
     * @param mtime the value for trade_spot_margin_middle_order.mtime
     *
     * @mbg.generated
     */
    public void setMtime(Date mtime) {
        this.mtime = mtime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table trade_spot_margin_middle_order
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
        sb.append(", orderId=").append(orderId);
        sb.append(", type=").append(type);
        sb.append(", symbol=").append(symbol);
        sb.append(", direction=").append(direction);
        sb.append(", status=").append(status);
        sb.append(", ctime=").append(ctime);
        sb.append(", mtime=").append(mtime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}