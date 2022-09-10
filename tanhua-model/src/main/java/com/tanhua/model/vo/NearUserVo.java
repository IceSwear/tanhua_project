package com.tanhua.model.vo;

import com.tanhua.model.domain.UserInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 环信附近的人vo对象
 * @Author: Spike Wong
 * @Date: 2022/8/26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NearUserVo {

    /**
     * 用户id
     */
    private Long userId;
    /**
     * 头像
     */
    private String avatar;
    /**
     * 昵称
     */
    private String nickname;


    /**
     * near userVo初始化
     * @param userInfo
     * @return
     */
    public static NearUserVo init(UserInfo userInfo) {
        NearUserVo vo = new NearUserVo();
        vo.setUserId(userInfo.getId());
        vo.setAvatar(userInfo.getAvatar());
        vo.setNickname(userInfo.getNickname());
        return vo;
    }
}