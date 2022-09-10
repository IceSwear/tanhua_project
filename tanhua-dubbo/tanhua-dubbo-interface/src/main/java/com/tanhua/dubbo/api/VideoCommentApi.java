package com.tanhua.dubbo.api;

import com.tanhua.model.enums.CommentType;
import com.tanhua.model.mongo.VideoComment;
import com.tanhua.model.vo.PageResult;

/**
 * @Description: 视频评论Api层
 * @Author: Spike Wong
 * @Date: 2022/8/26
 */
public interface VideoCommentApi {

    /**
     * 评论类型是否存在
     * @param videoId
     * @param currentUserId
     * @param commentType
     * @return
     */
    Boolean hasComment(String videoId, Long currentUserId, CommentType commentType);


    /**
     * 保存视频评论类型并返回最新的数量
     * @param videoComment
     * @return
     */
    Integer saveAndReturnCounts(VideoComment videoComment);


    /**
     * 删除视频评论类型行为信息返回最新的数量
     * @param comment
     * @return
     */
    Integer remove(VideoComment comment);


    /**
     *  根据视频id对评论进行分页
     * @param videoId
     * @param page
     * @param pagesize
     * @return
     */
    PageResult findByVideoId(String videoId, Integer page, Integer pagesize);
}
