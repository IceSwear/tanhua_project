package com.tanhua.model.vo;

import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.mongo.Video;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;

/**
 * @Description:  视频Vo
 * @Author: Spike Wong
 * @Date: 2022/8/26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoVo implements Serializable {

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
     * id
     */
    private String id;

    /**
     * 封面
     */
    private String cover;
    /**
     * 视频URL
     */
    private String videoUrl;
    /**
     * 发布视频时，传入的文字内容
     */
    private String signature;


    /**
     * 点赞数量
     */
    private Integer likeCount;
    /**
     * 是否已赞（1是，0否）
     */
    private Integer hasLiked;

    /**
     * 是否关注 （1是，0否）
     */
    private Integer hasFocus;

    /**
     * 评论数量
     */
    private Integer commentCount;


    /**
     * 初始化
     *
     * @param userInfo
     * @param video
     * @return
     */
    public static VideoVo init(UserInfo userInfo, Video video) {
        VideoVo vo = new VideoVo();
        //copy用户属性
        BeanUtils.copyProperties(userInfo, vo);
        //copy视频属性
        BeanUtils.copyProperties(video, vo);
        vo.setCover(video.getPicUrl());
        vo.setId(video.getId().toHexString());
        vo.setSignature(video.getText());
        //下面这些要缓存和数据表再弄出来
        vo.setHasFocus(0);
        vo.setHasLiked(0);
        return vo;
    }
}