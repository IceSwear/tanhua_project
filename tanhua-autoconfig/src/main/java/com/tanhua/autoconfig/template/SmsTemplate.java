package com.tanhua.autoconfig.template;

import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.dysmsapi20170525.models.SendSmsResponseBody;
import com.aliyun.teaopenapi.models.Config;
import com.tanhua.autoconfig.properties.SmsProperties;

/**
 * @Description: SMS Template for user registry
 * @Author: Spike Wong
 * @Date: 2022/8/8
 */
public class SmsTemplate {


    private SmsProperties properties;

    public SmsTemplate(SmsProperties properties) {
        this.properties = properties;
    }

    public void sendSms(String mobile, String code) {
        try {
            //配置阿里云
            Config config = new Config()
                    // 您的AccessKey ID
                    .setAccessKeyId(properties.getAccessKey())
                    // 您的AccessKey Secret
                    .setAccessKeySecret(properties.getSecret());
            // 访问的域名  domain web address
            config.endpoint = "dysmsapi.aliyuncs.com";
            //build connection
            com.aliyun.dysmsapi20170525.Client client = new com.aliyun.dysmsapi20170525.Client(config);
            SendSmsRequest sendSmsRequest = new SendSmsRequest().setPhoneNumbers(mobile).setSignName(properties.getSignName()).setTemplateCode(properties.getTemplateCode()).setTemplateParam("{\"code\":\"" + code + "\"}");
            // 复制代码运行请自行打印 API 的返回值
            SendSmsResponse response = client.sendSms(sendSmsRequest);
            SendSmsResponseBody body = response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
