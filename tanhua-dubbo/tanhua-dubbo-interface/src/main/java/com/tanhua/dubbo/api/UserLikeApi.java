package com.tanhua.dubbo.api;

import com.tanhua.model.vo.PageResult;

/**
 * @Description:
 * @Author: Spike Wong
 * @Date: 2022/8/18
 */
public interface UserLikeApi {
    Boolean saveOrUpdate(Long currentUserId, Long likeUserId, boolean isLike);


    int loveAloneCount(Long userId);

    /**
     * be followed count 喜欢我，但我不喜欢就是粉丝
     * @param userId
     * @return
     */
    int fanCount(Long userId);

    Boolean alreadyLove(Long userId, String uid);

    int loveEachOtherCount(Long userId);

    PageResult loveEachOtherList(Long currentUserId, Integer page, Integer pagesize);

    PageResult loveAloneList(Long currentUserId, Integer page, Integer pagesize);

    PageResult fanList(Long currentUserId, Integer page, Integer pagesize);

    void removeLike(Long currentUserId, Long userId);
}
