package com.google.backend.trading.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author adam.wang
 * @date 2021/10/1 17:53
 */
public class DateUtil {

    private static final ThreadLocal<DateFormat> SDF_SIMPLE = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS"));

    public static Long getTime(Date date){
        if(null ==date){
            return 0L;
        }
        return date.getTime();
    }

    public static String formatSimple(Date date) {
        return SDF_SIMPLE.get().format(date);
    }
}
