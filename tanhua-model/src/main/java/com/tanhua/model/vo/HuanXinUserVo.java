package com.tanhua.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 环信Vo
 * @Author: Spike Wong
 * @Date: 2022/8/26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HuanXinUserVo {
    /**
     * 用户名称
     */
    private String username;

    /**
     * 用户密码
     */
    private String password;
}