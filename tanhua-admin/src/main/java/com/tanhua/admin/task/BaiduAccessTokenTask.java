package com.tanhua.admin.task;

import com.tanhua.autoconfig.template.BaiduGreenTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BaiduAccessTokenTask {


    @Autowired
    BaiduGreenTemplate baiduGreenTemplate;

    /**
     * 秒 分 时 日 月 周 年
     */
    @Scheduled(cron = "0 0 0 ? * MON")
    public void xxx() {
        log.info("----百度TOKEN定时任务每周一凌晨执行开始-----");
        baiduGreenTemplate.setBaiduAccessToken();
        //集群可能需要存在redis里
        log.info("----百度TOKEN定时任务每周一凌晨执行结束-----");
    }
}
