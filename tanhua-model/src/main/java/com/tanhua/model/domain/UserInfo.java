package com.tanhua.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: user  information 用户详情
 * @Author: Spike Wong
 * @Date: 2022/8/9
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo extends BasePojo {

    /**
     * 由于userinfo表和user表之间是一对一关系
     * userInfo的id来源于user表的id
     */
    @TableId(type = IdType.INPUT)
    private Long id;
    /**
     * 昵称
     */
    private String nickname;
    /**
     * 用户头像
     */
    private String avatar;
    /**
     * 生日
     */
    private String birthday;
    /**
     * 性别
     */
    private String gender;
    /**
     * 年龄
     */
    private Integer age;
    /**
     * 城市
     */
    private String city;
    /**
     * 收入
     */
    private String income;
    /**
     * 学历
     */
    private String education;
    /**
     * 行业
     */
    private String profession;
    /**
     * 婚姻状态
     */
    private Integer marriage;
    /**
     * 标签
     */
    private String tags;
    /**
     * 封面图片
     */
    private String coverPic;
    /**
     * 逻辑删除
     */
    private String isDeleted;
    //用户状态,1为正常，2为冻结
    @TableField(exist = false)
    private String userStatus = "1";
}
