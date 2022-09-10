package com.tanhua.server.controller;

import com.tanhua.model.vo.HuanXinUserVo;
import com.tanhua.server.service.HuanxinService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: Huanxin service
 * @Author: Spike Wong
 * @Date: 2022/8/18
 */
@RestController
@RequestMapping("/huanxin")
@Slf4j
public class HuanxinAPI {

    @Autowired
    HuanxinService huanxinService;

    /**
     * get account Huanxin vo  得到环信user的账号密码
     *
     * @return
     */
    @GetMapping("/user")
    public ResponseEntity user() {
        HuanXinUserVo vo = huanxinService.findHuanxinUser();
        log.info("环信vo:{}", vo);
        return ResponseEntity.ok(vo);
    }
}
