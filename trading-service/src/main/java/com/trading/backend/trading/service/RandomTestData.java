package com.google.backend.trading.service;

import java.math.BigDecimal;
import java.util.Random;

/**
 * 用来构造一些随机数测试
 *
 * @author savion.chen
 * @date 2021/10/1 15:43
 */
public class RandomTestData {

    public static int getInt(int min, int max) {
        return randomObj.nextInt(max - min + 1) + min;
    }

    public static String getStrInt(int min, int max) {
        return "" + getInt(min, max);
    }

    public static Boolean getBool() {
        return (getInt(1, 10) > 5);
    }

    public static BigDecimal getDouble(int min, int max) {
        double number = Math.random() * (max - min) + min;
        BigDecimal result = new BigDecimal("" + number);
        return result.setScale(3, BigDecimal.ROUND_HALF_UP);
    }

    public static String getString(int len) {
        StringBuffer temp = new StringBuffer();
        String origin ="zxcvbnmlkjhgfdsaqwertyuiopQWERTYUIOPASDFGHJKLZXCVBNM";
        for (int i = 0; i < len; i++) {
            int index = getInt(0, origin.length()-1);
            temp.append(origin.charAt(index));
        }
        return temp.toString();
    }

    private static Random randomObj = new Random();

}
