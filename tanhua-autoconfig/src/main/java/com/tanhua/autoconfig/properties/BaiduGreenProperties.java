package com.tanhua.autoconfig.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
/**
 * @Description:  内容审核装配属性
 * @Author: Spike Wong
 * @Date: 2022/8/9
 */
@Data
@ConfigurationProperties(prefix = "tanhua.green")
public class BaiduGreenProperties {
    private String appId;
    private String apiKey;
    private String secretKey;
}