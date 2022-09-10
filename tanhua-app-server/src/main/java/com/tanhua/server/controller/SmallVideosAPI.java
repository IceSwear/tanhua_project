package com.tanhua.server.controller;

import com.tanhua.model.vo.ErrorResult;
import com.tanhua.model.vo.PageResult;
import com.tanhua.server.expcetion.BusinessException;
import com.tanhua.server.service.SmallVideosService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

/**
 * @Description: 小视频API
 * @Author: Spike Wong
 * @Date: 2022/8/26
 */
@Slf4j
@RestController
@RequestMapping("/smallVideos")
public class SmallVideosAPI {

    @Autowired
    private SmallVideosService smallVideosService;


    /**
     * upload videos 视频上传 视频发布
     *
     * @param videoThumbnail
     * @param videoFile
     * @return
     * @throws IOException
     */
    @PostMapping
    public ResponseEntity saveVideos(MultipartFile videoThumbnail, MultipartFile videoFile) throws IOException {
        if (videoFile.isEmpty() || videoThumbnail.isEmpty()) {
            throw new BusinessException(ErrorResult.builder().errMessage("上传视频失败").build());
        }
        log.info("上传视频中图片名:{},----视频名:{}", videoThumbnail.getOriginalFilename(), videoFile.getOriginalFilename());
        smallVideosService.saveVideos(videoThumbnail, videoFile);
        return ResponseEntity.ok(null);
    }


    /**
     * video list 视频列表 (分页查询)
     *
     * @param page
     * @param pagesize
     * @return
     */
    @GetMapping
    public ResponseEntity videoList(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer pagesize) {
        log.info("视频列表 (分页查询)  page:{} pagesize:{}", page, pagesize);
        PageResult pr = smallVideosService.getVideoList(page, pagesize);
        return ResponseEntity.ok(pr);
    }


    /**
     * 关注 follow 关注用户
     *
     * @param uid
     * @return
     */
    @PostMapping("/{uid}/userFocus")
    public ResponseEntity followUser(@PathVariable(value = "uid") Long uid) {
        log.info("关注用户:{}", uid);
        smallVideosService.followUser(uid);
        return ResponseEntity.ok(null);
    }

    /**
     * 取消关注 unfollow
     *
     * @param uid
     * @return
     */
    @PostMapping("/{uid}/userUnFocus")
    public ResponseEntity unfollowUser(@PathVariable(value = "uid") Long uid) {
        log.info("取消关注用户:{}", uid);
        smallVideosService.unfollowUser(uid);
        return ResponseEntity.ok(null);
    }

    /**
     * 点赞
     *
     * @param videoId
     * @return
     */
    @PostMapping("/{id}/like")
    public ResponseEntity like(@PathVariable(value = "id") String videoId) {
        log.info("视频点赞---videoId:{}", videoId);
        Integer likecounts = smallVideosService.likeVideo(videoId);
        return ResponseEntity.ok(likecounts);
    }

    /**
     * 取消点赞
     *
     * @param videoId
     * @return
     */
    @PostMapping("/{id}/dislike")
    public ResponseEntity dislike(@PathVariable(value = "id") String videoId) {
        log.info("视频取消点赞---videoId:{}", videoId);
        Integer likecounts = smallVideosService.dislikeVideo(videoId);
        return ResponseEntity.ok(likecounts);
    }


    /**
     * 对视频发发评论
     *
     * @param videoId
     * @param map
     * @return
     */
    @PostMapping("/{id}/comments")
    public ResponseEntity videoComments(@PathVariable(value = "id") String videoId, @RequestBody Map map) {
        log.info("videoComments视频评论:{}",map);
        String comment = (String) map.get("comment");
        smallVideosService.saveVideoComment(videoId, comment);
        return ResponseEntity.ok(null);
    }


    /**
     *  视频评论点赞 like comment of video
     *
     * @param commentId
     * @return
     */
    @PostMapping("/comments/{id}/like")
    public ResponseEntity likeCommentOfVideo(@PathVariable(value = "id") String commentId) {
        log.info("likeCommentOfVideo视频评论点赞 评论id:{}", commentId);
        Integer likeCommentsCounts = smallVideosService.likeCommentOfVideo(commentId);
        return ResponseEntity.ok(likeCommentsCounts);
    }

    /**
     * 评论取消点赞-dislike
     *
     * @param commentId
     * @return
     */
    @PostMapping("/comments/{id}/dislike")
    public ResponseEntity dislikeCommentOfVideo(@PathVariable(value = "id") String commentId) {
        log.info("dislikeCommentOfVideo视频评论取消点赞 评论id:{}", commentId);
        Integer likeCommentsCounts = smallVideosService.dislikeCommentOfVideo(commentId);
        return ResponseEntity.ok(likeCommentsCounts);
    }


    /**
     * get video comment list 视频评论列表
     *
     * @param videoId
     * @param page
     * @param pagesize
     * @return
     */
    @GetMapping("/{id}/comments")
    public ResponseEntity commentsList(@PathVariable(value = "id") String videoId, @RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer pagesize) {
        log.info("commentsList-入参视频id:{}", videoId);
        PageResult pr = smallVideosService.getVideoCommentsList(videoId, page, pagesize);
        return ResponseEntity.ok(pr);
    }
}
