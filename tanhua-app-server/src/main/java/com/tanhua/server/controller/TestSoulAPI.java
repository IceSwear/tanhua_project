package com.tanhua.server.controller;

import com.tanhua.model.vo.testsoul.questionlist.QuestionListVo;
import com.tanhua.model.vo.testsoul.report.TestSoulReportVo;
import com.tanhua.server.service.TestSoulService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @Description: 测灵魂API
 * @Author: Spike Wong
 * @Date: 2022/9/6
 */
@Slf4j
@RestController
@RequestMapping("/testSoul")
public class TestSoulAPI {


    @Autowired
    private TestSoulService testSoulService;

    /**
     * @param map
     * @return
     */
    @PostMapping
    public ResponseEntity comeUpWith(@RequestBody Map map) {
        log.info("map------{}:", map);
//        Object o = map.get("");
        //TODO 这里直接提交一个固定值
        return ResponseEntity.ok("98548321");
    }


    /**
     * 问题列表
     * @return
     */
    @GetMapping
    public ResponseEntity questionsList() {
        log.info("questionsLis接口被调用");
        List<QuestionListVo> vos = testSoulService.getQuestionsList();
        return ResponseEntity.ok(vos);
    }


    /**
     * 获取报告
     * @param id
     * @return
     */
    @GetMapping("/report/{id}")
    public ResponseEntity getResult(@PathVariable(value = "id") String id) {


        log.info("getResult{}:", id);
        TestSoulReportVo vo = testSoulService.getResult(id);
        log.info("Vo{}:",vo);
        return ResponseEntity.ok(vo);
    }


}
