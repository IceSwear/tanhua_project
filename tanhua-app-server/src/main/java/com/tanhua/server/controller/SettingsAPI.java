package com.tanhua.server.controller;

import com.tanhua.model.vo.PageResult;
import com.tanhua.model.vo.SettingsVo;
import com.tanhua.server.service.QuestionService;
import com.tanhua.server.service.SettingsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @Description: 通用设置服务 setting service
 * @Author: Spike Wong
 * @Date: 2022/8/10
 */
@RequestMapping("/users")
@RestController
@Slf4j
public class SettingsAPI {

    @Autowired
    SettingsService settingsService;

    @Autowired
    QuestionService questionService;


    /**
     * display setting information回显设置信息
     * 用户通用设置-读取
     *
     * @return
     */
    @GetMapping("/settings")
    public ResponseEntity settings() {
        SettingsVo vo = settingsService.settings();
        return ResponseEntity.ok(vo);
    }


    /**
     * Stranger questions 陌生人问题-保存 ,改造成功
     *
     * @param map
     * @return
     */
    @PostMapping("/questions")
    public ResponseEntity questions(@RequestBody Map map) {
        String content = (String) map.get("content");
        questionService.saveQuestion(content);
        return ResponseEntity.ok(null);
    }


    /**
     * save setting 保存设置
     *
     * @param map
     * @return
     */
    @PostMapping("/notifications/setting")
    public ResponseEntity notifications(@RequestBody Map map) {
        log.info("设置Map:{}", map);
        settingsService.saveSettings(map);
        return ResponseEntity.ok(null);
    }


    /**
     * blacklist 黑名单列表(分页查询)
     *
     * @param page
     * @param pagesize
     * @return
     */
    @GetMapping("/blacklist")
    public ResponseEntity blacklist(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer pagesize) {
        log.info("page:{},pagesize:{}", page, pagesize);
        PageResult pr = settingsService.getBlackList(page, pagesize);
        return ResponseEntity.ok(pr);
    }


    /**
     * remove blacklist 将id删除黑名单，黑名单移除
     *
     * @param uid
     * @return
     */
    @DeleteMapping("/blacklist/{uid}")
    public ResponseEntity removeBlacklist(@PathVariable Long uid) {
        settingsService.removeBlacklist(uid);
        return ResponseEntity.ok(null);
    }

}
