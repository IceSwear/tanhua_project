package com.tanhua.autoconfig.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Description: Aliyun
 * @Author: Spike Wong
 * @Date: 2022/8/9
 */
@Data
@ConfigurationProperties(prefix = "tanhua.aliyun.oss")
public class AliyunOssProperties {
    private String accessKeyId;
    private String accesskeySecret;
    private String bucketName;
    private String endpoint;
    private String url;

}
