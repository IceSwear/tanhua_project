package com.tanhua.server.controller;

import com.tanhua.model.enums.FreezingRangeType;
import com.tanhua.model.mongo.Movement;
import com.tanhua.model.vo.ErrorResult;
import com.tanhua.model.vo.MovementsVo;
import com.tanhua.model.vo.PageResult;
import com.tanhua.model.vo.VisitorsVo;
import com.tanhua.server.expcetion.BusinessException;
import com.tanhua.server.interceptor.UserHolder;
import com.tanhua.server.service.MovementService;
import com.tanhua.server.service.UserFreezeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * @Description: 动态API  mvm api
 * @Author: Spike Wong
 * @Date: 2022/8/16
 */
@RestController
@RequestMapping("/movements")
@Slf4j
public class MovementAPI {


    @Autowired
    private MovementService movementService;

    @Autowired
    private UserFreezeService userFreezeService;

    /**
     * 发布动态 publish mvm
     *
     * @return
     */
    @PostMapping
    public ResponseEntity movements(Movement movement, MultipartFile imageContent[]) throws IOException {
        //movement--Movement(id=null, pid=null, created=null, userId=null, textContent=木叶之光, medias=null, longitude=118.164766, latitude=24.723193, locationName=null, state=0, likeCount=0, commentCount=0, loveCount=0), imageContent[]--[org.springframework.web.multipart.support.StandardMultipartHttpServletRequest$StandardMultipartFile@121cfe80]
        log.info("movement--{}, imageContent[]--{}", movement, imageContent);
        userFreezeService.checkUserStatus(FreezingRangeType.BAN_PUBLISH.getType(), UserHolder.getUserId());
        movementService.publishMovement(movement, imageContent);
        return ResponseEntity.ok("null");
    }


    /**
     * 我的动态 my mvm
     *
     * @param userId
     * @param page
     * @param pagesize
     * @return
     */
    @GetMapping("/all")
    public ResponseEntity findByUserId(Long userId, @RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer pagesize) {
        log.info("findByUserId---userId:{},page:{},pagesize:{}", userId, page, pagesize);
        PageResult pr = movementService.findByUserId(userId, page, pagesize);
        return ResponseEntity.ok(pr);
    }


    /**
     * query friends' movements 查询好友动态，get method
     *
     * @param page
     * @param pagesize
     * @return
     */
    @GetMapping
    public ResponseEntity queryMovementsOfFriend(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer pagesize) {
        log.info("查询好友页表queryMovementsOfFriend,page:{},pagesize:{}", page, pagesize);
        PageResult pr = movementService.queryMovementsOfFriends(page, pagesize);
        log.info("好友pageresult:{}", pr);
        return ResponseEntity.ok(pr);
    }

    /**
     * query single movement
     * 查看单条动态
     *
     * @param id 动态id
     * @return
     */
    @GetMapping("/{id}")
    public ResponseEntity findByMovementId(@PathVariable(value = "id") String id) {
        log.info("查询单条动态id:{}", id);
        MovementsVo vo = movementService.findByMovementId(id);
        return ResponseEntity.ok(vo);
    }


    /**
     * query recommend mvm  查询推荐动态
     *
     * @param page
     * @param pagesize
     * @return
     */
    @GetMapping("/recommend")
    public ResponseEntity recommend(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer pagesize) {
        PageResult pageResult = null;
        try {
            pageResult = movementService.findRecommendMovements(page, pagesize);
        } catch (IllegalArgumentException e) {
            throw new BusinessException(ErrorResult.builder().errMessage("老色批，你查的太多快了了！！即使心上人儿也需要慢慢驻足欣赏，请晚点再试试！！").build());
        }
        return ResponseEntity.ok(pageResult);
    }


    /**
     * like mvm 点赞 用redis哈希
     *
     * @param movementId
     * @return
     */
    @GetMapping("/{id}/like")
    public ResponseEntity like(@PathVariable(value = "id") String movementId) {
        log.info("动态点赞:{}", movementId);
        Integer likeCounts = movementService.like(movementId);
        return ResponseEntity.ok(likeCounts);
    }

    /**
     * dislike mvm 取消点赞
     *
     * @param movementId
     * @return
     */
    @GetMapping("/{id}/dislike")
    public ResponseEntity dislike(@PathVariable(value = "id") String movementId) {
        log.info("取消动态点赞:{}", movementId);

        Integer likeCounts = movementService.dislike(movementId);
        return ResponseEntity.ok(likeCounts);
    }

    /**
     * love mvm  喜欢动态
     *
     * @param movementId
     * @return
     */
    @GetMapping("/{id}/love")
    public ResponseEntity love(@PathVariable(value = "id") String movementId) {
        log.info("喜欢动态:{}", movementId);
        Integer loveCounts = movementService.love(movementId);
        return ResponseEntity.ok(loveCounts);
    }

    /**
     * unlove mvm  取消喜欢动态
     *
     * @param movementId
     * @return
     */
    @GetMapping("/{id}/unlove")
    public ResponseEntity unlove(@PathVariable(value = "id") String movementId) {
        log.info("取消喜欢动态:{}", movementId);
        Integer loveCounts = movementService.unlove(movementId);
        return ResponseEntity.ok(loveCounts);
    }


    /**
     * get recent visitors 访客记录
     *
     * @return
     */
    @GetMapping("/visitors")
    public ResponseEntity visitors() {
        List<VisitorsVo> vos = movementService.listVisitorsByUserId();
        return ResponseEntity.ok(vos);
    }
}
