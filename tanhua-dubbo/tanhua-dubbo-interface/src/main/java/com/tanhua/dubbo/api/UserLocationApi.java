package com.tanhua.dubbo.api;

import java.util.List;

/**
 * @Description: UserLocationApi
 * @Author: Spike Wong
 * @Date: 2022/8/18
 */
public interface UserLocationApi {
    List<Long> queryNearUser(Long currentUserId, String distance);

    Boolean updateLocation(Long userId, Double longitude, Double latitude, String address);
}
