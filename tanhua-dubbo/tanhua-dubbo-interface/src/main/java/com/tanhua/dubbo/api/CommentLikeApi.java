package com.tanhua.dubbo.api;

import com.tanhua.model.mongo.CommentLike;

/**
 * @Description: 评论点赞Api
 * @Author: Spike Wong
 * @Date: 2022/9/6
 */

public interface CommentLikeApi {


    /**
     * 确认评论是否已点赞
     * @param currentUserId
     * @param commentId
     * @return
     */
    Boolean commentHasLike(Long currentUserId, String commentId);

    /**
     * 保存点赞喜欢
     * @param commentLike
     * @return
     */
    Integer save(CommentLike commentLike);


    /**
     * 动态评论取消点赞 删除
     * @param currentUserId
     * @param commentId
     * @return
     */
    Integer remove(Long currentUserId, String commentId);
}
