package com.tanhua.server.controller;

import com.tanhua.model.enums.FreezingRangeType;
import com.tanhua.model.vo.PageResult;
import com.tanhua.server.interceptor.UserHolder;
import com.tanhua.server.service.CommentService;
import com.tanhua.server.service.UserFreezeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @Description: 评论
 * @Author: Spike Wong
 * @Date: 2022/8/17
 */
@RestController
@RequestMapping("/comments")
@Slf4j
public class CommentAPI {

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserFreezeService userFreezeService;

    /**
     * publish comments 发布评论
     * get variables from map then use commentService to save Comments
     *
     * @return
     */
    @PostMapping
    public ResponseEntity saveComments(@RequestBody Map map) {
        log.info("评论:Map", map);
        //判断用户状态
        userFreezeService.checkUserStatus(FreezingRangeType.BAN_COMMENT.getType(), UserHolder.getUserId());
        String movementId = (String) map.get("movementId");
        String comment = (String) map.get("comment");
        commentService.saveComments(movementId, comment);
        return ResponseEntity.ok(null);
    }

    /**
     * get comments list 获取评论列表
     *
     * @param movementId
     * @param page
     * @param pagesize
     * @return
     */
    @GetMapping
    public ResponseEntity getComents(@RequestParam(value = "movementId") String movementId, @RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer pagesize) {
        log.info("getComents获取评论列表:movementId:{},page:{}pagesize{}", movementId, page, pagesize);
        //return a pr object
        //返回一个pr对象
        PageResult pr = commentService.getComments(movementId, page, pagesize);
        return ResponseEntity.ok(pr);
    }


    /**
     * 动态评论点赞
     *
     * @param commentId
     * @return
     */
    @GetMapping("/{id}/like")
    public ResponseEntity likeComment(@PathVariable(value = "id") String commentId) {
        log.info("评论点赞,评论id-{}", commentId);
        Integer likeCounts = commentService.likeComment(commentId);
        return ResponseEntity.ok(likeCounts);
    }


    /**
     * 动态评论取消点赞
     *
     * @param commentId
     * @return
     */
    @GetMapping("/{id}/dislike")
    public ResponseEntity dislikeComment(@PathVariable(value = "id") String commentId) {
        log.info("评论点赞,评论id-{}", commentId);
        Integer likeCounts = commentService.dislikeComment(commentId);
        return ResponseEntity.ok(likeCounts);
    }

}
