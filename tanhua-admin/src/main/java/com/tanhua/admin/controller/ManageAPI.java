package com.tanhua.admin.controller;

import com.tanhua.admin.exception.BusinessException;
import com.tanhua.admin.service.ManageService;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.vo.MovementsVo;
import com.tanhua.model.vo.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @Description: 管理接口
 * @Author: Spike Wong
 * @Date: 2022/8/31
 */

@RestController
@RequestMapping("/manage")
@Slf4j
public class ManageAPI {

    @Autowired
    private ManageService manageService;

    /**
     * 获取用户详情列表
     *
     * @param page
     * @param pagesize
     * @return
     */
    @GetMapping("/users")
    public ResponseEntity users(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer pagesize) {
        log.info("用户列表分页page:{},pagesize:{}", page, pagesize);
        PageResult pr = manageService.getUserList(page, pagesize);
        log.info("用户列表分页pr:{}", pr);
        return ResponseEntity.ok(pr);
    }


    /**
     * 获取用户信息 byuserID
     *
     * @param id
     * @return
     */
    @GetMapping("/users/{userId}")
    public ResponseEntity getUserInfo(@PathVariable(value = "userId") Long id) {
        log.info("获取用详情 用户id:{}", id);
        UserInfo userInfo = manageService.getUserInfo(id);
        return ResponseEntity.ok(userInfo);
    }


    /**
     * 视频翻页分页
     *
     * @param page
     * @param pagesize
     * @param uid
     * @return
     */
    @GetMapping("/videos")
    public ResponseEntity getVideosList(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer pagesize, Long uid) {
        log.info("后台视频管理 视频分页:page:{},pagesize:{},uid:{}", page, pagesize, uid);
        PageResult pr = manageService.getVideosList(uid, page, pagesize);
        log.info("视频分页pr:{}", pr);
        return ResponseEntity.ok(pr);
    }

    /**
     * 查动态  动态分页
     *
     * @param page
     * @param pagesize
     * @param uid
     * @param state
     * @return
     */
    @GetMapping("/messages")
    public ResponseEntity getMovement(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer pagesize, Long uid, Integer state) {
        log.info("动态分页 page:{},pagesize{},uid:{},state{}", page, pagesize, uid, state);
        PageResult pr = null;
        try {
            if (Objects.isNull(state) || "''".equals(state.toString())) {
                state = null;
                //https://blog.csdn.net/Melo_FengZhi/article/details/85011230
            }
            pr = manageService.getMovements(page, pagesize, uid, state);
        } catch (Exception e) {
            throw new BusinessException(("操作太过！请稍后再试！！"));
        }
        return ResponseEntity.ok(pr);
    }

    /**
     * 动态详情
     *
     * @param movementId
     * @return
     */
    @GetMapping("/messages/{id}")
    public ResponseEntity movementDetails(@PathVariable(value = "id") String movementId) {
        log.info("动态详情 movementId:{}", movementId);
        MovementsVo vo = manageService.getMovementsDetails(movementId);
        log.info("/messages/{id}--VO:{}", vo);
        return ResponseEntity.ok(vo);
//      mo  manageService.movementDetails(movementId);
    }


    /**
     * 评论列表 评论列表翻页
     *
     * @param page
     * @param pagesize
     * @param messageID
     * @return
     */
    @GetMapping("/messages/comments")
    public ResponseEntity comments(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer pagesize, String messageID) {
        log.info("messageID:{}", messageID);
        PageResult pr = manageService.commentList(page, pagesize, messageID);
        return ResponseEntity.ok(pr);
    }


    /**
     * 驳回
     *
     * @param ids
     * @return
     */
    @PostMapping("/messages/reject")
    public ResponseEntity reject(@RequestBody List<String> ids) {
        //TODO
        log.info("reject---List<String>:{}", ids);
        Map map = manageService.reject(ids);
        return ResponseEntity.ok(map);
    }

    @PostMapping("/messages/pass")
    public ResponseEntity pass(@RequestBody List<String> ids) {
        //TODO
        log.info("pass---List<String>:{}", ids);
        Map map = manageService.pass(ids);
        return ResponseEntity.ok(map);
    }

    @PostMapping("/users/freeze")
    public ResponseEntity freeze(@RequestBody Map map) {
        log.info("冻结------map:{}", map);
        Map map1 = manageService.freeze(map);
        return ResponseEntity.ok(map1);
    }


    @PostMapping("/users/unfreeze")
    public ResponseEntity unfreeze(@RequestBody Map map) {
        log.info("解冻---------map:{}", map);
        Map map1 = manageService.unfreeze(map);
        return ResponseEntity.ok(map1);
    }
}
