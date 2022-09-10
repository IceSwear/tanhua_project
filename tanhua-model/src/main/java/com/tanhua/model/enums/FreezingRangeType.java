package com.tanhua.model.enums;

/**
 * @Description: 冻结范围类型, 冻结范围，1为冻结登录，2为冻结发言，3为冻结发布动态
 * @Author: Spike Wong
 * @Date: 2022/9/9
 */
public enum FreezingRangeType {

    BAN_LOGOIN("1"), BAN_PUBLISH("2"), BAN_COMMENT("3");

    private String type;

    FreezingRangeType(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }
}
