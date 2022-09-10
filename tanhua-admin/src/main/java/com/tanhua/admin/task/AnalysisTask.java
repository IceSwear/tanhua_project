package com.tanhua.admin.task;

import com.tanhua.admin.service.AnalysisService;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @Description:
 * @Author: Spike Wong
 * @Date: 2022/8/31
 */
@Component
@Slf4j
public class AnalysisTask {

    @Autowired
    private AnalysisService analysisService;

    //秒 分 时 日 月 周 年
    @Scheduled(cron = "0 0/30 * * * ?")
    public void analysis() {
        log.info("{}", new DateTime().toString("yyyy-MM-dd HH:mm:ss"));
        try {
            analysisService.analysis();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
