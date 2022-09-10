package com.tanhua.server.task;

import com.tanhua.dubbo.api.RecommendUserApi;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;

/**
 * @Description: 定时清理任务
 * @Author: Spike Wong
 * @Date: 2022/9/5
 */
@Slf4j
@Component
public class CleanTask {


    @DubboReference
    private RecommendUserApi recommendUserApi;

    /**
     * 定时清理mongo的推荐用户数据
     */
//    @Scheduled(cron = "0 5/35 * * * ?")
//    public void cleanRecommendUser() {
//        recommendUserApi.removeAll();
//    }
}
