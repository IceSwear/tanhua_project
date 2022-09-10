package com.tanhua.dubbo.api;

import com.tanhua.model.mongo.PeachBlossom;

/**
 * @Description: PeachBlossomService数据层api
 * @Author: Spike Wong
 * @Date: 2022/9/6
 */
public interface PeachBlossomApi {


    void savePeachBlosoomVoice(Long userId, String voiceUrl);

    PeachBlossom getRandomVoiceExceptSelf(Long userId);
}
