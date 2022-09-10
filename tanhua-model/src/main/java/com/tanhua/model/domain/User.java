package com.tanhua.model.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 用户登录信息 user login information
 * @Author: Spike Wong
 * @Date: 2022/8/9
 */

@Data
@AllArgsConstructor  //满参构造方法
@NoArgsConstructor   //无参构造方法
public class User extends BasePojo {


    /**
     * id
     */
    private Long id;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 密码
     */
    private String password;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 环信用户
     */
    private String hxUser;

    /**
     * 环信密码
     */
    private String hxPassword;

    /**
     * 逻辑删除
     */
    private String isDeleted;
}
