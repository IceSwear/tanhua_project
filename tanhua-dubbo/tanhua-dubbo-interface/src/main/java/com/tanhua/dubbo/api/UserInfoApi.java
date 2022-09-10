package com.tanhua.dubbo.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tanhua.model.domain.UserInfo;

import java.util.List;
import java.util.Map;

public interface UserInfoApi {

    /**
     * find by user Id 根据用户ID查询用户信息
     */
    public UserInfo findById(Long id);

    /**
     * 更新头像
     * @param url
     * @param userId
     */
    public void updateAvatarByUserId(String url, Long userId);


    /**
     * 保存用户信息
     * @param userInfo
     */
    public void saveUserInfo(UserInfo userInfo);

    /**
     * 批量查询，等到map
     * Map<id,UserInfo></>
     */
    Map<Long, UserInfo> findByIds(List<Long> userIds, UserInfo info);

    IPage findAll(Integer page, Integer pagesize);

    void updateUserInfoById(UserInfo userInfo, Long userId);
}
