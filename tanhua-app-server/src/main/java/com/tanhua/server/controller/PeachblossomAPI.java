package com.tanhua.server.controller;

import com.tanhua.model.vo.PeachBlossomVo;
import com.tanhua.server.service.PeachBlossomService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @Description: 桃花传音API
 * @Author: Spike Wong
 * @Date: 2022/9/6
 */
@Slf4j
@RequestMapping("/peachblossom")
@RestController
public class PeachblossomAPI {


    @Autowired
    private PeachBlossomService peachBlossomService;


    /**
     * 发送桃花传音
     * @param soundFile
     * @return
     * @throws IOException
     */
    @PostMapping
    public ResponseEntity sendVoice(@RequestBody MultipartFile soundFile) throws IOException {
        log.info("上传桃花传音---soundFile:{}", soundFile);
        peachBlossomService.sendVoice(soundFile);
        return ResponseEntity.ok(null);
    }


    /**
     * 接收桃花传音信息
     * @return
     */
    @GetMapping
    public ResponseEntity receiveVoice() {
        PeachBlossomVo vo = peachBlossomService.receivePeachBlossomMessage();
        //user info+ peachblossom 表 VO
        //这里需要设计表
        return ResponseEntity.ok(vo);
    }

}
