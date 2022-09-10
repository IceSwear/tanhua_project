package com.tanhua.model.vo;

import com.tanhua.model.domain.UserInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Objects;

/**
 * @Description: 联系人Vo
 * @Author: Spike Wong
 * @Date: 2022/8/26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactVo implements Serializable {


    /**
     * id
     */
    private Long id;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 昵称
     */
    private String nickname;

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
     * 联系人Vo
     *
     * @param userInfo
     * @return
     */
    public static ContactVo init(UserInfo userInfo) {
        ContactVo vo = new ContactVo();
        if (!Objects.isNull(userInfo)) {
            BeanUtils.copyProperties(userInfo, vo);
            vo.setUserId(userInfo.getId().toString());
        }
        return vo;
    }
}