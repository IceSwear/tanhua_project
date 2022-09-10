package com.tanhua.server.controller;

import com.tanhua.model.dto.RecommendUserDto;
import com.tanhua.model.vo.NearUserVo;
import com.tanhua.model.vo.PageResult;
import com.tanhua.model.vo.TodayBest;
import com.tanhua.server.service.TanhuaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @Description: app第一页探花模块api
 * @Author: Spike Wong
 * @Date: 2022/8/10
 */
@RestController
@RequestMapping("/tanhua")
@Slf4j
public class TanhuaAPI {

    @Autowired
    private TanhuaService tanhuaService;


    /**
     * today best lover  今日佳人
     *
     * @return
     */
    @GetMapping("/todayBest")
    public ResponseEntity todayBest() {
        log.info("今日佳人开始推荐~~~~~");
        TodayBest tb = tanhuaService.todayBest();
        return ResponseEntity.ok(tb);
    }


    /**
     * recommend list  推荐列表
     *
     * @param dto
     * @return
     */
    @GetMapping("/recommendation")
    public ResponseEntity recommendation(RecommendUserDto dto) {
        log.info("RecommendUserDto--{}", dto);
        PageResult pr = tanhuaService.recommendation(dto);
        return ResponseEntity.ok(pr);
    }


    /**
     * query todaybest info 查看佳人信息,要做访客来自
     *
     * @param userId
     * @return
     */
    @GetMapping("/{id}/personalInfo")
    public ResponseEntity personalInfo(@PathVariable(value = "id") Long userId) {
        log.info("查看佳人信息，佳人id:{}", userId);
        TodayBest best = tanhuaService.personalInfo(userId);
        return ResponseEntity.ok(best);
    }


    /**
     * query questions of Stranger 查询陌生人问题
     *
     * @param userId
     * @return
     */

    @GetMapping("/strangerQuestions")
    public ResponseEntity strangerQuestions(Long userId) {
        log.info("入参的userId:{}", userId);
        String questions = tanhuaService.strangerQuestions(userId);
        return ResponseEntity.ok(questions);
    }


    /**
     * reply questions of stranger 回复陌生人问题
     *
     * @param map
     * @return
     */
    @PostMapping("/strangerQuestions")
    public ResponseEntity replyStrangerQuestions(@RequestBody Map map) {
        String id = map.get("userId").toString();
        Long userId = Long.valueOf(id);
        String reply = (String) map.get("reply");
        log.info("userId:{},reply:{}", userId, reply);
        tanhuaService.replyStrangerQuestions(userId, reply);
        return ResponseEntity.ok(null);
    }


    /**
     * recommend list 推荐列表
     *
     * @return
     */
    @GetMapping("/cards")
    public ResponseEntity queryCardsList() {
        List<TodayBest> list = tanhuaService.queryCardsList();
        log.info("探花卡片list{}", list);
        return ResponseEntity.ok(list);
    }


    /**
     * like user 喜欢用户
     *
     * @param likeUserId
     * @return
     */
    @GetMapping("/{id}/love")
    public ResponseEntity likeUser(@PathVariable(value = "id") Long likeUserId) {
        log.info("喜欢用户---likeUserId{}", likeUserId);
        tanhuaService.likeUser(likeUserId);
        return ResponseEntity.ok(null);
    }


    /**
     * 喜欢 - 取消
     * @param unlikeUserId
     * @return
     */
    @GetMapping("/{id}/unlove")
    public ResponseEntity unlikeUser(@PathVariable(value = "id") Long unlikeUserId) {
        log.info("不喜欢用户---likeUserId{}", unlikeUserId);
        tanhuaService.unlikeUser(unlikeUserId);
        return ResponseEntity.ok(null);
    }


    /**
     * search nearby 搜附近
     *
     * @param gender
     * @param distance
     * @return
     */
    @GetMapping("/search")
    public ResponseEntity<List<NearUserVo>> queryNearUser(String gender, @RequestParam(defaultValue = "2000") String distance) {
        log.info("搜附近功能入参性别gender:{},距离distance:{}", gender, distance);
        List<NearUserVo> list = tanhuaService.queryNearUser(gender, distance);
        return ResponseEntity.ok(list);
    }
}
