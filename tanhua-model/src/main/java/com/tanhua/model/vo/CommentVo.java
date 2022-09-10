package com.tanhua.model.vo;

import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.mongo.Comment;
import com.tanhua.model.mongo.VideoComment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Description: 评论VO
 * @Author: Spike Wong
 * @Date: 2022/8/26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentVo implements Serializable {

    /**
     * 评论id
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
     * 评论
     */
    private String content;
    /**
     * 评论时间
     */
    private String createDate;
    /**
     * 点赞数
     */
    private Integer likeCount;
    /**
     * 是否点赞（1是，0否）
     */
    private Integer hasLiked=0;


    /**
     * 对于动态的评论
     * @param userInfo
     * @param comment
     * @return
     */
    public static CommentVo init(UserInfo userInfo, Comment comment) {
        CommentVo vo = new CommentVo();
        BeanUtils.copyProperties(userInfo, vo);
        BeanUtils.copyProperties(comment, vo);
        //评论喜欢还没搞定
        vo.setHasLiked(0);
        Date date = new Date(comment.getCreated());
        vo.setCreateDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date));
        vo.setId(comment.getId().toHexString());
        return vo;
    }


    /**
     * 对于短视频的评论
     * @param userInfo
     * @param videoComment
     * @return
     */
    public static CommentVo init(UserInfo userInfo, VideoComment videoComment) {
        CommentVo vo = new CommentVo();
        BeanUtils.copyProperties(userInfo, vo);
        BeanUtils.copyProperties(videoComment, vo);
        vo.setHasLiked(0);
        Date date = new Date(videoComment.getCreated());
        vo.setCreateDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date));
        vo.setId(videoComment.getId().toHexString());
        return vo;
    }
}