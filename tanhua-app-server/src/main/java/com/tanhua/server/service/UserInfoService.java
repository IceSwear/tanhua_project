package com.tanhua.server.service;

import com.tanhua.autoconfig.template.AipFaceTemplate;
import com.tanhua.autoconfig.template.AliyunOssTemplate;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.vo.ErrorResult;
import com.tanhua.model.vo.UserInfoVo;
import com.tanhua.server.expcetion.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;

/**
 * @Description: userinfo服务
 * @Author: Spike Wong
 * @Date: 2022/8/9
 */
@Service
@Slf4j
public class UserInfoService {
    @DubboReference
    UserInfoApi userInfoApi;

    @Autowired
    AliyunOssTemplate aliyunOssTemplate;

    @Autowired
    AipFaceTemplate aipFaceTemplate;


    /**
     * 保存用户信息 save userInfo
     *
     * @param userInfo
     */
    public void saveUserInfo(UserInfo userInfo) {
        userInfoApi.saveUserInfo(userInfo);
    }


    /**
     * save avactor保存头像
     *
     * @param headPhoto
     * @param id
     * @return
     * @throws IOException
     */
    public boolean saveAvatar(MultipartFile headPhoto, Long id) throws IOException {
        log.info("saveAvatar:headPhoto:{},id:{}", headPhoto, id);
        String url = aliyunOssTemplate.uploadFile(headPhoto);
        boolean detect = aipFaceTemplate.detect(url);
        if (!detect) {
            throw new BusinessException(ErrorResult.faceError());
//            throw new RuntimeException("图片不合法！");
        }
        //DONE--Mapper 改造已完成
        UserInfo info = userInfoApi.findById(Long.valueOf(id));
        if (!Objects.isNull(info)) {
            userInfoApi.updateAvatarByUserId(url, Long.valueOf(id));
            return true;
        } else {
            return false;
        }
    }


    /**
     * update avatar 更新头像
     *
     * @param avatar
     * @param id
     * @return
     * @throws IOException
     */
    public boolean updateAvatar(MultipartFile avatar, Long id) throws IOException {
        String url = aliyunOssTemplate.uploadFile(avatar);
        boolean detect = aipFaceTemplate.detect(url);
        if (!detect) {
            throw new BusinessException(ErrorResult.faceError());
//            throw new RuntimeException("图片不合法！");
        }
        UserInfo info = userInfoApi.findById(Long.valueOf(id));
//        info.setAvatar(url);
        userInfoApi.updateAvatarByUserId(url, Long.valueOf(id));
        return true;
    }


    /**
     * vo 传输
     *
     * @param id
     * @return
     */
    public UserInfoVo findById(Long id) {
        UserInfo userInfo = userInfoApi.findById(Long.valueOf(id));
        UserInfoVo vo = new UserInfoVo();
        //copy同名，同类型的属性
        BeanUtils.copyProperties(userInfo, vo);
        Integer age = userInfo.getAge();
        if (!Objects.isNull(age)) {
            vo.setAge(age.toString());
        }
        return vo;
    }


    /**
     * save information of user  By id 根据Id用户资料保存
     *
     * @param userInfo
     * @param userId
     */
    public void updateUserInfoById(UserInfo userInfo, Long userId) {
        userInfoApi.updateUserInfoById(userInfo, userId);
    }
}
