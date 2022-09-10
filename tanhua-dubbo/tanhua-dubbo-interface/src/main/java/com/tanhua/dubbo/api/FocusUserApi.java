package com.tanhua.dubbo.api;

/**
 * @Description:  关注用户数据层api
 * @Author: Spike Wong
 * @Date: 2022/8/26
 */
public interface FocusUserApi {

    /**
     * follow someone 关注用户
     *
     * @param currentUserId
     * @param uid
     */
    void save(Long currentUserId, Long uid);

    /**
     * unfollow someone 取关,
     *
     * @param currentUserId
     * @param uid
     */
    void remove(Long currentUserId, Long uid);
}
