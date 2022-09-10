package com.tanhua.admin.controller;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import com.tanhua.admin.service.AdminService;
import com.tanhua.commons.utils.Constants;
import com.tanhua.model.vo.AdminVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * @Description: 系统登录
 * @Author: Spike Wong
 * @Date: 2022/8/31
 */
@RestController
@RequestMapping("/system/users")
@Slf4j
public class SystemAPI {

    @Autowired
    private AdminService adminService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    /**
     * 验证码
     *
     * @param uuid
     * @param response
     * @throws IOException
     */
    @GetMapping("/verification")
    public void verification(String uuid, HttpServletResponse response) throws IOException {
        log.info("uuid:{}", uuid);

        //set response parameter
        response.setDateHeader("Expires", 0);
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");
        response.setContentType("image/jpeg");
        LineCaptcha captcha = CaptchaUtil.createLineCaptcha(299, 97);
        String code = captcha.getCode();
        //

        stringRedisTemplate.opsForValue().set(Constants.CAP_CODE + uuid, code, 5L, TimeUnit.MINUTES);
        //output stream
        captcha.write(response.getOutputStream());
    }


    /**
     * 生成token
     *
     * @param map
     * @return
     */

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody Map map) {
        log.info("用户登录map:{}", map);
        Map retMap = adminService.login(map);
        return ResponseEntity.ok(retMap);
    }

    /**
     * 登出
     *
     * @return
     */
    @PostMapping("/logout")
    public ResponseEntity logout() {
        //TODO
        log.info("后台登出");
        adminService.logout();
        return ResponseEntity.ok(null);
    }


    /**
     * 登录成功获取用户信息
     *
     * @return
     */
    @PostMapping("/profile")
    public ResponseEntity profile() {
        log.info("登录获取用户id资料");
        AdminVo adminVo = adminService.profile();
        return ResponseEntity.ok(adminVo);
    }
}
