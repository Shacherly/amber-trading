package com.google.backend.trading.util;

/**
 * @author david.chen
 * @date 2022/1/17 19:46
 */
public class ThreadLocalUtils {

    public static final ThreadLocal<String> L_OR_P = new ThreadLocal<>();
    public static final ThreadLocal<String> ORIGIN_CHANNEL = new ThreadLocal<>();
}
