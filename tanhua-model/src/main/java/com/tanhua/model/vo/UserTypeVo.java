package com.tanhua.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: 互相喜欢、喜欢、粉丝、谁看过我 Vo
 * @Author: Spike Wong
 * @Date: 2022/9/5
 */
@Slf4j
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserTypeVo {
    /**
     * user  id
     */
    private Long id;
    /**
     * 昵称
     */
    private String nickname;
    /**
     * 头像
     */
    private String avatar;
    /**
     * 生日
     */
    private String birthday;
    /*
     * 性别
     */
    private String gender;
    /**
     * age,这里的age是string
     */
    private Integer age;
    /**
     * 城市
     */
    private String city;
    /**
     * 学历
     */
    private String education;
    /**
     * 婚姻状态  默认为0
     */
    private Integer marriage = 0;

    /**
     * 匹配度	默认55，最大值: 99
     * 最小值: 30
     */

    private Integer matchRate = 55;
    /**
     * 是否喜欢ta(指当前用户是否喜欢)
     */
    private boolean alreadyLove;

}
