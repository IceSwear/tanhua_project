package com.tanhua.model.vo;

import com.tanhua.model.domain.UserInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Description: 评论类型Vo
 * @Author: Spike Wong
 * @Date: 2022/8/23
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CommentTypeListVo implements Serializable {


    /**
     * id
     */
    private String id;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 创建时间
     */
    private String createDate;


    /**
     * 评论vo
     * @param userInfo
     * @return
     */
    public static CommentTypeListVo init(UserInfo userInfo) {
        CommentTypeListVo vo = new CommentTypeListVo();
        if (userInfo != null) {
            vo.setId(userInfo.getId().toString());
            vo.setNickname(userInfo.getNickname());
            vo.setAvatar(userInfo.getAvatar());
            //还差一个时间
        }
        return vo;
    }
}
