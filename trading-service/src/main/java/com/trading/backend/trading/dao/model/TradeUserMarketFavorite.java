package com.google.backend.trading.dao.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Database Table Remarks:
 *   用户市场收藏
 *
 * This class was generated by MyBatis Generator.
 * This class corresponds to the database table trade_user_market_favorite
 */
public class TradeUserMarketFavorite implements Serializable {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column trade_user_market_favorite.id
     *
     * @mbg.generated
     */
    private Long id;

    /**
     * Database Column Remarks:
     *   币对
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column trade_user_market_favorite.symbol_arr
     *
     * @mbg.generated
     */
    private String[] symbolArr;

    /**
     * Database Column Remarks:
     *   用户id
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column trade_user_market_favorite.uid
     *
     * @mbg.generated
     */
    private String uid;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column trade_user_market_favorite.ctime
     *
     * @mbg.generated
     */
    private Date ctime;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column trade_user_market_favorite.mtime
     *
     * @mbg.generated
     */
    private Date mtime;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table trade_user_market_favorite
     *
     * @mbg.generated
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column trade_user_market_favorite.id
     *
     * @return the value of trade_user_market_favorite.id
     *
     * @mbg.generated
     */
    public Long getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column trade_user_market_favorite.id
     *
     * @param id the value for trade_user_market_favorite.id
     *
     * @mbg.generated
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column trade_user_market_favorite.symbol_arr
     *
     * @return the value of trade_user_market_favorite.symbol_arr
     *
     * @mbg.generated
     */
    public String[] getSymbolArr() {
        return symbolArr;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column trade_user_market_favorite.symbol_arr
     *
     * @param symbolArr the value for trade_user_market_favorite.symbol_arr
     *
     * @mbg.generated
     */
    public void setSymbolArr(String[] symbolArr) {
        this.symbolArr = symbolArr;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column trade_user_market_favorite.uid
     *
     * @return the value of trade_user_market_favorite.uid
     *
     * @mbg.generated
     */
    public String getUid() {
        return uid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column trade_user_market_favorite.uid
     *
     * @param uid the value for trade_user_market_favorite.uid
     *
     * @mbg.generated
     */
    public void setUid(String uid) {
        this.uid = uid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column trade_user_market_favorite.ctime
     *
     * @return the value of trade_user_market_favorite.ctime
     *
     * @mbg.generated
     */
    public Date getCtime() {
        return ctime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column trade_user_market_favorite.ctime
     *
     * @param ctime the value for trade_user_market_favorite.ctime
     *
     * @mbg.generated
     */
    public void setCtime(Date ctime) {
        this.ctime = ctime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column trade_user_market_favorite.mtime
     *
     * @return the value of trade_user_market_favorite.mtime
     *
     * @mbg.generated
     */
    public Date getMtime() {
        return mtime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column trade_user_market_favorite.mtime
     *
     * @param mtime the value for trade_user_market_favorite.mtime
     *
     * @mbg.generated
     */
    public void setMtime(Date mtime) {
        this.mtime = mtime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table trade_user_market_favorite
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
        sb.append(", symbolArr=").append(symbolArr);
        sb.append(", uid=").append(uid);
        sb.append(", ctime=").append(ctime);
        sb.append(", mtime=").append(mtime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}