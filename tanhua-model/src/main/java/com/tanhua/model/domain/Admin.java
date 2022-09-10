package com.tanhua.model.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 管理员实体类 admin
 * @Author: Spike Wong
 * @Date: 2022/8/30
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Admin extends BasePojo {

    /**
     * id
     */
    private Long id;


    /**
     * 用户名
     */
    private String username;


    /**
     * 用户密码
     */
    private String password;


    /**
     * 头像
     */
    private String avatar;

    /**
     * 逻辑删除 N Y
     */
    private String isDeleted;
}
