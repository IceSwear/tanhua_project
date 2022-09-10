package com.tanhua.autoconfig;

import com.tanhua.autoconfig.properties.*;
import com.tanhua.autoconfig.template.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * @Description: 注册bean
 * @Author: Spike Wong
 * @Date: 2022/8/8
 */

@EnableConfigurationProperties({SmsProperties.class, EmailProperties.class, AliyunOssProperties.class, AipFaceProperties.class, HuanXinProperties.class, BaiduGreenProperties.class})
public class TanhuaAutoConfiguration {

    @Bean
    public SmsTemplate smsTemplate(SmsProperties properties) {
        return new SmsTemplate(properties);
    }

    @Bean
    public EmailTemplate myMailUtils(EmailProperties properties) {
        return new EmailTemplate(properties);
    }

    @Bean
    public AliyunOssTemplate aliyunOssTemplate(AliyunOssProperties properties) {
        return new AliyunOssTemplate(properties);
    }

    @Bean
    public AipFaceTemplate aipFaceTemplate() {
        return new AipFaceTemplate();
    }

    @Bean
    public HuanXinTemplate huanXinTemplate(HuanXinProperties properties) {
        return new HuanXinTemplate(properties);
    }

    @Bean
    public BaiduGreenTemplate baiduGreenTemplate(BaiduGreenProperties properties) {
        return new BaiduGreenTemplate(properties);
    }
}
