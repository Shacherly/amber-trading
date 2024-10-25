package com.google.backend.trading.model.trade.fee;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author david.chen
 * @date 2022/1/5 16:52
 */
@Getter
public enum VIPLevelEnum {

    DEFAULT("DEFAULT", 0, -1),
    DEFAULT_VIP1("DEFAULT_VIP1", 1, -1, "1-20"),
    DEFAULT_VIP2("DEFAULT_VIP2", 2, -1, "21-200"),
    DEFAULT_VIP3("DEFAULT_VIP3", 3, -1, "201-1,500"),
    DEFAULT_VIP4("DEFAULT_VIP4", 4, -1, "1,501-5,000"),
    DEFAULT_VIP5("DEFAULT_VIP5", 5, -1, "5,001-10,000"),
    DEFAULT_VIP6("DEFAULT_VIP6", 6, -1, "10,001-50,000"),
    DEFAULT_VIP7("DEFAULT_VIP7", 7, -1, "50,001-100,000"),
    DEFAULT_VIP8("DEFAULT_VIP8", 8, 1, "100,001-500,000"),
    DEFAULT_VIP9("DEFAULT_VIP9", 9, 2, "500,001-1,000,000"),
    DEFAULT_VIP10("DEFAULT_VIP10", 10, 3, "1,000,000-∞"),

    ;


    VIPLevelEnum(String tag, Integer level, Integer bwcLevel) {
        this.tag = tag;
        this.level = level;
        this.bwcLevel = bwcLevel;
    }


    VIPLevelEnum(String tag, Integer level, Integer bwcLevel, String googleLevelCondition) {
        this.tag = tag;
        this.level = level;
        this.bwcLevel = bwcLevel;
        this.googleLevelCondition = googleLevelCondition;
    }

    /**
     * 名称
     */
    private String tag;
    /**
     * 等级
     */
    private Integer level;
    /**
     * BWC等级
     * bwc等级(1、2、3级，-1级为非bwc用户)
     * BWC1 对应原VIP8 rate值
     * BWC2 对应原VIP9 rate值
     * BWC3 对应原VIP10 rate值
     */
    private Integer bwcLevel;

    private String googleLevelCondition;

    public static VIPLevelEnum getByName(String name) {
        return Arrays.stream(values()).filter(e -> StringUtils.equals(name, e.getTag()))
                .findFirst().orElse(DEFAULT);
    }

    public static VIPLevelEnum getByLevel(Integer level) {
        return Arrays.stream(values()).filter(e -> Objects.equals(level, e.getLevel()))
                .findFirst().orElse(DEFAULT);
    }

    public static VIPLevelEnum getByBwcLevel(Integer bwcLevel) {
        return Arrays.stream(values()).filter(e -> Objects.equals(bwcLevel, e.getBwcLevel()))
                .findFirst().orElse(DEFAULT);
    }
}
