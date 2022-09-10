package com.tanhua.server.controller;

import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.vo.CountsVo;
import com.tanhua.model.vo.PageResult;
import com.tanhua.model.vo.UserInfoVo;
import com.tanhua.server.interceptor.UserHolder;
import com.tanhua.server.service.TanhuaService;
import com.tanhua.server.service.UserInfoService;
import com.tanhua.server.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @Description:  用户接口
 * @Author: Spike Wong
 * @Date: 2022/8/9
 */
@RestController
@Slf4j
@RequestMapping("/users")
public class UsersAPI {

    @Autowired
    private TanhuaService tanhuaService;
    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private UserService userService;


    /**
     * 用户资料读取，当不传递id时，查询当前用户的资料信息
     *
     * @param userID
     * @return
     */
    @GetMapping
    public ResponseEntity<UserInfoVo> queryUser(Long userID) {
        if (Objects.isNull(userID)) {
            userID = UserHolder.getUserId();
        }
        UserInfoVo userInfoVo = userInfoService.findById(userID);
        return ResponseEntity.ok(userInfoVo);
    }


    /**
     * save information of user 用户资料保存，传入的事一个userinfo
     *
     * @param userInfo
     * @return
     */
    @PutMapping
    public ResponseEntity updateUserInfo(@RequestBody UserInfo userInfo) {
        log.info("更新用户资料{}", userInfo);
//        boolean verifyToken = JwtUtils.verifyToken(token);
//        if (!verifyToken) {
//            return ResponseEntity.status(401).body(null);
//        }
//        Claims claims = JwtUtils.getClaims(token);
//        Integer id = (Integer) claims.get("id");
        Long currentIserId = UserHolder.getUserId();
        userInfo.setId(currentIserId);
        //改造已完成
        userInfoService.updateUserInfoById(userInfo, currentIserId);
        return ResponseEntity.ok(null);
    }

    /**
     * 更新头像
     *
     * @param headPhoto
     * @return
     * @throws IOException
     */
    @PostMapping("/header")
    public ResponseEntity head(MultipartFile headPhoto) throws IOException {
        boolean saveHeadPhoto = userInfoService.updateAvatar(headPhoto, UserHolder.getUserId());
        if (!saveHeadPhoto) {
            return ResponseEntity.status(401).body(null);
        }
        return ResponseEntity.ok(null);
    }


    /**
     * send Verification Code 发送验证码 send verification code to modify phone
     *
     * @return
     */
    @PostMapping("/phone/sendVerificationCode")
    public ResponseEntity sendVerificationCode() {
//        String mobile = (String) map.get("phone"); 这个逻辑不太好
        userService.sendVerificationCode();
        return ResponseEntity.ok(null);
    }

    /**
     * 校验验证码 verify phone
     *
     * @param map
     * @return
     */
    @PostMapping("/phone/checkVerificationCode")
    public ResponseEntity checkVerificationCode(@RequestBody Map map) {
        String code = (String) map.get("verificationCode");
        return ResponseEntity.ok(userService.checkVerificationCode(code));
    }


    /**
     * 这里有个问题——token会不见
     *
     * @param map
     * @return
     */
    @PostMapping("/phone")
    public ResponseEntity phone(@RequestBody Map map) {
        String phone = (String) map.get("phone");
        log.info("得到的手机号:{}", phone);
        userService.updateUphone(phone);
        //TODO,修改后，用户没有显示新的，难道直接退出？
        return ResponseEntity.ok(null);
    }


    /**
     * 互相喜欢，喜欢，粉丝-统计
     *
     * @return
     */
    @GetMapping("/counts")
    public ResponseEntity counts() {

        CountsVo vo = userService.counts(UserHolder.getUserId());
        log.info(" 互相喜欢，喜欢，粉丝-统计:{}", vo);
        return ResponseEntity.ok(vo);
    }


    @GetMapping("/friends/{type}")
    public ResponseEntity actionType(@PathVariable(value = "type") String type, @RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer pagesize, String nickname) {
        log.info("actionType--type:{}---page:{}---pagesize:{}---nickname:{}", type, page, pagesize, nickname);
        PageResult pr = userService.listUserInfoByType(type, page, pagesize);
        return ResponseEntity.ok(pr);
    }


    /**
     * 查看喜欢状态
     * @param uid
     * @return
     */
    @GetMapping("/{uid}/alreadyLove")
    public ResponseEntity alreadyLove(@PathVariable(value = "uid") String uid) {
        log.info("alreadyLove-uid:{}", uid);
        Boolean alreadyLove = userService.alreadyLove(uid);
        return ResponseEntity.ok(alreadyLove);
    }


    /**
     * 喜欢列表：取消
     * @param uid
     * @return
     */
    @DeleteMapping("/like/{uid}")
    public ResponseEntity likeDeleted(@PathVariable("uid") String uid) {
        log.info("取消喜欢likeDeleted-uid:{}", uid);
//        userService.like
        userService.likeDeleted(Long.valueOf(uid));
        return ResponseEntity.ok(null);
    }


    /**
     * 粉丝列表：喜欢
     * @param uid
     * @return
     */
    @PostMapping("/fans/{uid}")
    public ResponseEntity fansLike(@PathVariable(value = "uid") String uid) {
        log.info("粉丝喜欢-uid:{}", uid);
        userService.fansLike(Long.valueOf(uid));
        return ResponseEntity.ok(null);
    }


    /**
     * 缘分值最高
     * @return
     */
    @GetMapping("/friends/luck")
    public ResponseEntity luck() {
        log.info("缘分值高 用户 - 列表");
        List<UserInfo> list = new ArrayList<>();
        UserInfo userInfo = new UserInfo();
        userInfo.setId(1L);
        userInfo.setAvatar("https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/images/tanhua/avatar_10.jpg");
        list.add(userInfo);
        return ResponseEntity.ok(list);
    }
}

