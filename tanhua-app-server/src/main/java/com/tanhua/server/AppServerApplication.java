package com.tanhua.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @Description: app端启动类
 * @Author: Spike Wong
 * @Date: 2022/8/8
 */
@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
//        (exclude = {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
@EnableCaching
@EnableScheduling
public class AppServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(AppServerApplication.class, args);
        System.getProperties().setProperty("mail.mime.splitlongparameters", "false");
        System.getProperties().setProperty("mail.mime.charset", "UTF-8");
    }

}
