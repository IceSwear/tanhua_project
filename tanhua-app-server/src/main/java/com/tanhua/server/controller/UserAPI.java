package com.tanhua.server.controller;

import com.tanhua.model.domain.UserInfo;
import com.tanhua.server.interceptor.UserHolder;
import com.tanhua.server.service.UserInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @Description: 用户首次登录保存用户信息
 * @Author: Spike Wong
 * @Date: 2022/8/9
 */

@RestController
@RequestMapping("/user")
@Slf4j
public class UserAPI {

    @Autowired
    private UserInfoService userInfoService;


    /**
     * save user info 保存用户信息（首次登录）
     *
     * @param userInfo
     * @return
     */
    @PostMapping("/loginReginfo")
    public ResponseEntity loginReginfo(@RequestBody UserInfo userInfo) {
        log.info("首次登录要保存的userInfo信息:{}", userInfo);
        userInfo.setId(UserHolder.getUserId());
        //service to save
        userInfoService.saveUserInfo(userInfo);
        return ResponseEntity.ok(null);
    }


    /**
     * save avatar 冲刺登录补充头像,  保存头像
     *
     * @param headPhoto
     * @return
     * @throws IOException
     */
    @PostMapping("/loginReginfo/head")
    public ResponseEntity head(MultipartFile headPhoto) throws IOException {
        boolean saveHeadPhoto = userInfoService.saveAvatar(headPhoto, UserHolder.getUserId());
        if (!saveHeadPhoto) {
            return ResponseEntity.status(401).body(null);
        }
        return ResponseEntity.ok(null);
    }


}
