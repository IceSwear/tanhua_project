package com.tanhua.autoconfig.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Description:  短信服务属性
 * @Author: Spike Wong
 * @Date: 2022/8/8
 */
@Data
@ConfigurationProperties(prefix = "tanhua.sms")
public class SmsProperties {
    private String signName;
    private String templateCode;
    private String accessKey;
    private String secret;
}
