package com.tanhua.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Description: setting Vo
 * @Author: Spike Wong
 * @Date: 2022/8/26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SettingsVo implements Serializable {

    /**
     * id
     */
    private Long id;
    /**
     * 陌生人问题
     */
    private String strangerQuestion = "";
    /**
     * 手机号
     */
    private String phone;
    /**
     * 喜欢提示
     */
    private Boolean likeNotification = true;
    /**
     * 评论提示
     */
    private Boolean pinglunNotification = true;

    /**
     * 公告提示
     */
    private Boolean gonggaoNotification = true;

}