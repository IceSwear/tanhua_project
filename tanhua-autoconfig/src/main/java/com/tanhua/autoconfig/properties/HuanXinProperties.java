package com.tanhua.autoconfig.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


/**
 * @Description: 环信属性
 * @Author: Spike Wong
 * @Date: 2022/8/9
 */
@Configuration
@ConfigurationProperties(prefix = "tanhua.huanxin")
@Data
public class HuanXinProperties {

    private String appkey;
    private String clientId;
    private String clientSecret;
    
}