package com.tanhua.dubbo.api;

import com.tanhua.model.vo.PageResult;

public interface BlackListApi {


    /**
     * 取消黑名单 remove blacklist
     * @param userId
     * @param blackUserId
     */
    void removeBlacklist(Long userId, Long blackUserId);

    /**
     * 根据用户id分页查询
     * @param userId
     * @param page
     * @param pagesize
     * @return
     */
    PageResult findBlackListByUserId(Long userId,Integer page,Integer pagesize);
}