package com.tanhua.model.enums;

/**
 * @Description: 评论类型：1-点赞，2-评论，3-喜欢
 * @Author: Spike Wong
 * @Date: 2022/9/1
 */
public enum CommentType {

    LIKE(1), COMMENT(2), LOVE(3);

    int type;

    CommentType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}