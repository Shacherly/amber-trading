package com.google.backend.trading.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author adam.wang
 * @date 2021/10/1 18:19
 */
public class ListUtil {

    /**
     * 判断list为空
     * @param l
     * @return
     */
    public static boolean isEmpty(List l){
        return l==null||l.isEmpty();
    }

    /**
     * 判断list不为空
     * @param l
     * @return
     */
    public static boolean isNotEmpty(List l){
        return !(l==null||l.isEmpty());
    }

    public static void main(String[] args) {
        System.out.println(ListUtil.isEmpty(new ArrayList()));
    }
}
