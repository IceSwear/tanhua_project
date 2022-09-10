package com.tanhua.dubbo.api;

import com.tanhua.model.enums.CommentType;
import com.tanhua.model.mongo.Comment;
import com.tanhua.model.vo.PageResult;

/**
 * @Description:
 * @Author: Spike Wong
 * @Date: 2022/8/17
 */
public interface CommentApi {
    /*
     publish and save comments then get the comment count
     发布评论并获取评论数量
     */
    Integer save(Comment comment);


    /**
     * get comments 得到评论
     *
     * @param movementId
     * @param commentType
     * @param page
     * @param pagesize
     * @return
     */
    PageResult getComments(String movementId, CommentType commentType, Integer page, Integer pagesize);

    /**
     * check if the comment exits 确认是否存在评论
     *
     * @param movementId
     * @param userId
     * @param like
     * @return
     */
    Boolean hasComment(String movementId, Long userId, CommentType like);

    /**
     * 删除评论
     *
     * @param comment
     * @return
     */
    Integer remove(Comment comment);

    PageResult getCommentList(Integer page, Integer pagesize, String messageID);


    /**
     * 确认评论是否已喜欢
     * @param userId
     * @param commentId
     * @return
     */
    boolean commentHasLike(Long userId, String commentId);
}
