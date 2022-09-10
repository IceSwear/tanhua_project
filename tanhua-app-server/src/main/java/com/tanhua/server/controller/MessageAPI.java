package com.tanhua.server.controller;

import com.tanhua.model.vo.PageResult;
import com.tanhua.model.vo.UserInfoVo;
import com.tanhua.server.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @Description: 消息服务API接口
 * @Author: Spike Wong
 * @Date: 2022/8/18
 */
@RequestMapping("/messages")
@RestController
@Slf4j
public class MessageAPI {

    @Autowired
    private MessageService messageService;

    /**
     * query userInfo by Huanxin info 根据环信id查询用户详细
     *
     * @param huanxinId
     * @return
     */
    @GetMapping("/userinfo")
    public ResponseEntity userinfo(String huanxinId) {
        log.info("环信id:{}", huanxinId);
        UserInfoVo vo = messageService.findUserInfoByHuanxin(huanxinId);
        return ResponseEntity.ok(vo);
    }


    /**
     * add friends 添加好友
     *
     * @param map
     * @return
     */
    @PostMapping("/contacts")
    public ResponseEntity contacts(@RequestBody Map map) {
        log.info("添加好友:{}", map);
        Long friends = Long.valueOf(map.get("userId").toString());
        messageService.contacts(friends);
        return ResponseEntity.ok(null);
    }


    /**
     * get contacts list  得到联系人列表
     *
     * @param keyword
     * @param page
     * @param pagesize
     * @return
     */
    @GetMapping("/contacts")
    public ResponseEntity getContactsList(String keyword, @RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer pagesize) {
        log.info("keyword:{},page:{},pagesize:{}", keyword, page, pagesize);
        PageResult pr = messageService.getContacts(keyword, page, pagesize);
        return ResponseEntity.ok(pr);
    }


    /**
     * query love list 查询谁对当前用户喜欢列表
     *
     * @param page
     * @param pagesize
     * @return
     */
    @GetMapping("/loves")
    public ResponseEntity getUserLoveList(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer pagesize) {
        log.info("喜欢列表 page:{},pagesize{}", page, pagesize);
        PageResult pr = messageService.loveList(page, pagesize);
        log.info("消息通知-喜欢pr:{}", pr);
        return ResponseEntity.ok(pr);
    }


    /**
     * query like list 查询谁点赞当前用户列表列表
     *
     * @param page
     * @param pagesize
     * @return
     */
    @GetMapping("/likes")
    public ResponseEntity getUserLikeList(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer pagesize) {
        PageResult pr = messageService.likeList(page, pagesize);
        log.info("点赞列表消息通知-点赞pr:{}", pr);
        return ResponseEntity.ok(pr);
    }


    /**
     * query comment list 查询谁评论当前用户列表列表
     *
     * @param page
     * @param pagesize
     * @return
     */
    @GetMapping("/comments")
    public ResponseEntity getUserCommentList(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer pagesize) {
        PageResult pr = messageService.commentList(page, pagesize);
        log.info("评论列表消息通知-评论pr:{}", pr);
        return ResponseEntity.ok(pr);
    }


    /**
     * 公告列表
     *
     * @param page
     * @param pagesize
     * @return
     */
    @GetMapping("/announcements")
    public ResponseEntity announcements(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer pagesize) {
        PageResult pr = messageService.getAnnouncements(page, pagesize);
        log.info("公告pr:{}", pr);
        return ResponseEntity.ok(pr);
    }
}
