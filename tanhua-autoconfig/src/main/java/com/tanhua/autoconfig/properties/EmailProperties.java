package com.tanhua.autoconfig.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Description: 邮箱属性
 * @Author: Spike Wong
 * @Date: 2022/8/9
 */
@Data
@ConfigurationProperties(prefix = "tanhua.email")
public class EmailProperties {
    private String smtpHost;
    private String smtpPort;
    private String username;
    private String senderAlias;
    private String password;
    private String emailSubject;
}
