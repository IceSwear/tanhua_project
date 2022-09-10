package com.tanhua.server.controller;

import com.tanhua.model.vo.ErrorResult;
import com.tanhua.server.expcetion.BusinessException;
import com.tanhua.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @Description: user login & check verification  用户登录和确认
 * @Author: Spike Wong
 * @Date: 2022/8/8
 */
@RestController
@RequestMapping("/user")
public class LoginAPI {

    @Autowired
    private UserService userService;

    /**
     * user login  用户登录,发送验证码
     *
     * @param map get value from key=phone ,then send the phone number
     * @return
     */
    @PostMapping("/login")
    public ResponseEntity sendToPhone(@RequestBody Map map) {
        //get key="phone" from map 从map得到phone的值
        String phone = (String) map.get("phone");
        //send phone ,发送验证码
        userService.sentMsgToPhone(phone);
        //return ok with nothing 返回ok
        return ResponseEntity.ok(null);//状态码200
        //return ResponseEntity.status(500).body("出错啦");//状态码500
    }


    /**
     * send email, but we don't use, this is what I make ,function is sames as phone's
     *
     * @param map
     * @return
     */
    @PostMapping("/loginWithEmail")
    public ResponseEntity sendToEmail(@RequestBody Map map) {
        String email = (String) map.get("email");
        userService.sentMsgToEmail(email);
        return ResponseEntity.ok(null);
    }


    /**
     * user will come up with a map with key "phone" and "verificationCode" from user interface,we get the key to verify if the code matched the condition we build
     * 用户提交手机号和验证码，这时候后端需要校验
     *
     * @param map map存有phone  verificationCode 2个值
     * @return 返回的是一个map包含token和用户Id
     */
    @PostMapping("/loginVerification")
    public ResponseEntity loginVerification(@RequestBody Map map) {
        //get value from the map ,be sure that key value is corrective
        try {
            String phone = (String) map.get("phone");
            String code = (String) map.get("verificationCode");
            Map retMap = userService.loginVerification(phone, code);
            return ResponseEntity.ok(retMap);
        } catch (BusinessException be) {
            ErrorResult errorResult = be.getErrorResult();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResult);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorResult.error());
        }
    }
}
