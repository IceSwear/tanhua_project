package com.tanhua.model.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 设置 setting
 * @Author: Spike Wong
 * @Date: 2022/8/10
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Settings extends BasePojo {


    /**
     * id
     */
    private Long id;


    /**
     * 用户id
     */
    private Long userId;

    /**
     * 点赞提醒
     */
    private Boolean likeNotification;

    /**
     * 评论提醒
     */
    private Boolean pinglunNotification;

    /**
     * 公告提醒
     */
    private Boolean gonggaoNotification;

    /**
     * 逻辑删除 Y N
     */
    private String isDeleted;
}
