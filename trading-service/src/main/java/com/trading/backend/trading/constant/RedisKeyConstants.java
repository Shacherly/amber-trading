package com.google.backend.trading.constant;

/**
 * redis key
 *
 * @author david.chen
 * @date 2022/1/6 11:44
 */
public class RedisKeyConstants {

    /**
     * zset :
     * key -> uid
     * value -> amount
     * score -> System.currentTimeMillis()
     */
    private static final String TRADE_USER_TRANS = "trading:user-trans:%s";
    private static final String TRADE_USER_30D_TRADE_AMOUNT = "trading:user-trade-amount-30d:%s";
    /**
     * string
     * key -> uid_alarmId
     */
    private static final String TRADE_ALARM_24H = "trading:user-alarm:%s_%d";

    public static final String TRADE_USER_LOCALE = "trading:user-locale";
    public static final String TRADE_BOOKING_USERS = "trading:booking";

    public static String getTradeUserTransKey(String uid) {
        return String.format(TRADE_USER_TRANS, uid);
    }

    public static String getUserAlarm24HKey(String uid, long alarmId) {
        return String.format(TRADE_ALARM_24H, uid, alarmId);
    }

    public static String get30DUserTradeAmountKey(String uid) {
        return String.format(TRADE_USER_30D_TRADE_AMOUNT, uid);
    }

    public static void main(String[] args) {
        System.out.println(RedisKeyConstants.getUserAlarm24HKey("uid", 2));
    }
}
