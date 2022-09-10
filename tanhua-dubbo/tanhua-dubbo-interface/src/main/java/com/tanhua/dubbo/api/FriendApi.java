package com.tanhua.dubbo.api;

import com.tanhua.model.vo.PageResult;

import java.util.List;

/**
 * @Description: friendapi 表：朋友表
 * @Author: Spike Wong
 * @Date: 2022/8/18
 */

public interface FriendApi {
    void saveRelationship(Long currentUserId, Long friendId);


    PageResult findPageReusltById(Long userId, Integer page, Integer pagesize);

    void remove(Long currentUserId, Long friendId);

    int countFriendsById(Long userId);

    List<Long> getFriendsIds(Long userId);
}
