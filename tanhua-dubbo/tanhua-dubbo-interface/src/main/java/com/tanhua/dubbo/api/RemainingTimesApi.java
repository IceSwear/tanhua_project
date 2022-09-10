package com.tanhua.dubbo.api;

import com.tanhua.model.mongo.RemainingTimes;

/**
 * @Description: 剩余次数api
 * @Author: Spike Wong
 * @Date: 2022/9/6
 */

public interface RemainingTimesApi {
    RemainingTimes findByUserId(Long currentUserId);
}
