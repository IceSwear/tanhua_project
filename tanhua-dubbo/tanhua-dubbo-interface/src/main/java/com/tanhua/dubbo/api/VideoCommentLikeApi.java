package com.tanhua.dubbo.api;

import com.tanhua.model.mongo.VideoCommentLike;

/**
 * @Description:  视频点赞api数据层接口
 * @Author: Spike Wong
 * @Date: 2022/8/26
 */
public interface VideoCommentLikeApi {

    /**
     * 确认是否点赞了视频评论
     * @param currentUserId
     * @param commentId
     * @return
     */
    Boolean commentHasLike(Long currentUserId, String commentId);


    /**
     * 保存视频评论点赞记录并返回
     * @param videoCommentLike
     * @return
     */
    Integer saveAndReturnCounts(VideoCommentLike videoCommentLike);


    /**
     * 删除视频评论点赞并返回
     * @param currentUserId
     * @param commentId
     * @return
     */
    Integer removeAndReturnCounts(Long currentUserId, String commentId);
}
