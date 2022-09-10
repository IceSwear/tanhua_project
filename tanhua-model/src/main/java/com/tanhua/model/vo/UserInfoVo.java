package com.tanhua.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Description: UserInfo 的VO对象
 * @Author: Spike Wong
 * @Date: 2022/8/9
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoVo implements Serializable {

    /**
     * user  id
     */
    private Long id;
    /**
     * nick name
     */
    private String nickname;
    /**
     * head
     */
    private String avatar;
    /**
     * birthday
     */
    private String birthday;
    /**
     * gender 性别
     */
    private String gender;
    /**
     * age,这里的age是string
     */
    private String age;
    /**
     * 城市
     */
    private String city;
    /**
     * 收入
     */
    private String income;
    /**
     * 教育
     */
    private String education;
    /**
     * 专业
     */
    private String profession;
    /**
     * 结婚状态
     */
    private Integer marriage;
}
