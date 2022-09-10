package com.tanhua.model.enums;

/**
 * @Description: action type of users 用户行为枚举(自己设的)
 * @Author: Spike Wong
 * @Date: 2022/9/1
 */
public enum ActionType {

    LOGIN("0101"),

    REGISTER("0102"),

    PUBLISH_MVM("0201"),

    BROWSE_MVM("0202"),

    LIKE_MVM("0203"),

    LOVE_MVM("0204"),

    COMMENT_MVM("0205"),

    DISLIKE_MVM("0206"),

    UNLOVE_MVM("0207"),

    PUBLISH_VIDEO("0301"),

    LIKE_VIDEO("0302"),

    DISLIKE_VIDEO("0303"),

    COMMENT_VIDEO("0304");


    private String type;

    ActionType(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }
}
