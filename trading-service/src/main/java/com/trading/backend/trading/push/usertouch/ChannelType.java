package com.google.backend.trading.push.usertouch;

import java.util.ArrayList;
import java.util.List;

/**
 * @author adam.wang
 * @date 2021/10/27 20:32
 */
public enum ChannelType {
    // 邮件
    EMAIL("0"),
    // 短信
    SMS("1"),
    // 极光
    AURORA("2"),
    // 站内信
    IN_MAIL("3"),
    // toast
    TOAST("4"),
    ;

    public String type;
    ChannelType(String type) {
        this.type = type;
    }

    public static List<String> listTypes(List<ChannelType> types){
        List<String> res = new ArrayList<>();
        for(ChannelType t: types){
            res.add(t.type);
        }
        return res;
    }

}