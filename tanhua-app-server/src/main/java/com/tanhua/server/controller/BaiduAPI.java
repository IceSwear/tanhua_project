package com.tanhua.server.controller;

import com.tanhua.server.service.BaiduService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
/**
 * @Description: 百度地理位置获取
 * @Author: Spike Wong
 * @Date: 2022/9/8
 */
@RestController
@RequestMapping("/baidu")
@Slf4j
public class BaiduAPI {

    @Autowired
    private BaiduService baiduService;


    /**
     * update location 更新位置
     *
     * @param param
     * @return
     */
    @PostMapping("/location")
    public ResponseEntity updateLocation(@RequestBody Map param) {
        log.info("baidu location--{}", param);
        Double longitude = Double.valueOf(param.get("longitude").toString());
        Double latitude = Double.valueOf(param.get("latitude").toString());
        String address = param.get("addrStr").toString();
        baiduService.updateLocation(longitude, latitude, address);
        return ResponseEntity.ok(null);
    }
}